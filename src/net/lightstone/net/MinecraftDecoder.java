package net.lightstone.net;

import java.io.IOException;

import net.lightstone.net.codec.MessageCodec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

public class MinecraftDecoder extends ReplayingDecoder<VoidEnum> {

	private int previousOpcode = -1;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf, VoidEnum state) throws Exception {
		int opcode = buf.readUnsignedByte();

		MessageCodec<?> codec = CodecLookupService.find(opcode);
		if (codec == null) {
			throw new IOException("Unknown operation code: " + opcode + " (previous opcode: " + previousOpcode + ").");
		}

		previousOpcode = opcode;

		return codec.decode(buf);
	}

}
