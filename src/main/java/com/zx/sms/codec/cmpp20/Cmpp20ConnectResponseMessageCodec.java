package com.zx.sms.codec.cmpp20;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import com.zx.sms.codec.cmpp.msg.CmppConnectResponseMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.CmppConnectResponse;
import com.zx.sms.codec.cmpp.packet.PacketType;
import com.zx.sms.codec.cmpp20.packet.Cmpp20ConnectResponse;
import com.zx.sms.codec.cmpp20.packet.Cmpp20PacketType;

/**
 *
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class Cmpp20ConnectResponseMessageCodec extends MessageToMessageCodec<Message, CmppConnectResponseMessage> {
	private PacketType packetType;

	/**
     * 
     */
	public Cmpp20ConnectResponseMessageCodec() {
		this(Cmpp20PacketType.CMPPCONNECTRESPONSE);
	}

	public Cmpp20ConnectResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (commandId != packetType.getCommandId()) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}
		CmppConnectResponseMessage responseMessage = new CmppConnectResponseMessage(msg.getHeader());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());

		responseMessage.setStatus(bodyBuffer.readUnsignedByte());
		responseMessage.setAuthenticatorISMG(bodyBuffer.readBytes(Cmpp20ConnectResponse.AUTHENTICATORISMG.getLength()).array());
		responseMessage.setVersion(bodyBuffer.readUnsignedByte());
		
		ReferenceCountUtil.release(bodyBuffer);
		out.add(responseMessage);

	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppConnectResponseMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer = Unpooled.buffer(Cmpp20ConnectResponse.AUTHENTICATORISMG.getBodyLength());

		bodyBuffer.writeByte((int) msg.getStatus());
		bodyBuffer.writeBytes(msg.getAuthenticatorISMG());
		bodyBuffer.writeByte(msg.getVersion());

		msg.setBodyBuffer(bodyBuffer.array());
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);

	}

}
