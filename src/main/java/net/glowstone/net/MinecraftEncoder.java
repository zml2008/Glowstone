package net.glowstone.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import gnu.trove.set.hash.TIntHashSet;
import net.glowstone.msg.Message;
import net.glowstone.net.codec.MessageCodec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * A {@link OneToOneEncoder} which encodes Minecraft {@link Message}s into
 * {@link ChannelBuffer}s.
 */
public class MinecraftEncoder extends OneToOneEncoder {

    private static final TIntHashSet ignoredPrint = new TIntHashSet(new int[] {0x04});

    @SuppressWarnings("unchecked")
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel c, Object msg) throws Exception {
        if (msg instanceof Message) {
            Message message = (Message) msg;

            Class<? extends Message> clazz = message.getClass();
            MessageCodec<Message> codec = (MessageCodec<Message>) CodecLookupService.find(clazz);
            if (codec == null) {
                throw new IOException("Unknown message type: " + clazz + ".");
            }
            //if (!ignoredPrint.contains(codec.getOpcode()))
            //System.out.println("Message S->C: "+ message);

            ChannelBuffer opcodeBuf = ChannelBuffers.buffer(1);
            opcodeBuf.writeByte(codec.getOpcode());

            return ChannelBuffers.wrappedBuffer(opcodeBuf, codec.encode(message));
        }
        return msg;
    }

}
