package net.glowstone.net.codec;

import java.io.IOException;

import net.glowstone.msg.BedMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class BedCodec extends MessageCodec<BedMessage> {

    public BedCodec() {
        super(BedMessage.class, 0x11);
    }

    @Override
    public ChannelBuffer encode(BedMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(15);
        buffer.writeInt(message.getEntityId());
        buffer.writeByte(message.getInBed() ? 1 : 0);
        buffer.writeInt(message.getX());
        buffer.writeByte(message.getY());
        buffer.writeInt(message.getZ());
        return buffer;
    }

    @Override
    public BedMessage decode(ChannelBuffer buffer) throws IOException {
        int entityId = buffer.readInt();
        boolean inBed = buffer.readByte() == 1;
        int x = buffer.readInt();
        int y = buffer.readByte();
        int z = buffer.readInt();
        return new BedMessage(entityId, inBed, x, y, z);
    }

}
