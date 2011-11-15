package net.glowstone.net.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import net.glowstone.GlowServer;

import org.getspout.spoutapi.packet.*;

import net.glowstone.msg.SpoutMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class SpoutCodec extends MessageCodec<SpoutMessage> {

    public SpoutCodec() {
        super(SpoutMessage.class, 0xC3);
    }

    @Override
    public SpoutMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readShort();
        int version = buffer.readShort();
        int size = buffer.readInt();
        byte[] data = new byte[size];
        buffer.readBytes(data);
        
        Class<? extends SpoutPacket> packetType = PacketType.getPacketFromId(id).getPacketClass();
        SpoutPacket packet = null;
        try {
            Constructor<? extends SpoutPacket> constructor = packetType.getConstructor();
            packet = constructor.newInstance();
        } catch (Exception ex) {
            GlowServer.logger.log(Level.SEVERE, "Error parsing Spoutcraft packet: {0}", ex.getMessage());
            ex.printStackTrace();
        }
        
        if (packet == null) {
            GlowServer.logger.log(Level.WARNING, "Unknown Spoutcraft packet received with ID " + id);
            return null;
        } else if (packet.getVersion() != version) {
            GlowServer.logger.log(Level.WARNING, "Packet version mismatch! I have " + packet.getVersion() + " but they have " + version);
        }
        ByteArrayInputStream bytes = new ByteArrayInputStream(data);
        packet.readData(new DataInputStream(bytes));
        if (packet instanceof CompressablePacket) {
            if (((CompressablePacket) packet).isCompressed()) {
                ((CompressablePacket) packet).decompress();
            }
        }
        return new SpoutMessage(packet);
    }

    @Override
    public ChannelBuffer encode(SpoutMessage message) throws IOException {
        SpoutPacket packet = message.getPacket();
        if (packet instanceof CompressablePacket) {
            if (!((CompressablePacket) packet).isCompressed()) {
                ((CompressablePacket) packet).compress();
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        packet.writeData(new DataOutputStream(bytes));
        
        ChannelBuffer buffer = ChannelBuffers.buffer(8 + packet.getNumBytes());
        buffer.writeShort(packet.getPacketType().getId());
        buffer.writeShort(packet.getVersion());
        buffer.writeInt(packet.getNumBytes());
        buffer.writeBytes(bytes.toByteArray());
        
        return buffer;
    }

}
