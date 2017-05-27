package com.ly.sock5.phase;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnnectionMessage {
	
	private byte requestType;
	private byte addressType;
	
	private static Logger LOGGER = LoggerFactory.getLogger(ConnnectionMessage.class);
	
	public Socket doConnect(InputStream inputStream) throws IOException {
		byte[] msgHeader = new byte[2]; // 协议版本号 状态码
		inputStream.read(msgHeader);
		requestType = (byte) msgHeader[1];
		inputStream.read(); // 保留字段
		addressType = (byte) inputStream.read();
		Socket proxySocket ;
		short port ;
		byte[] portBuffer = new byte[2];
		switch (addressType) {
		case 0x01:
			// IPV4
			byte[] ipv4 = new byte[4];
			inputStream.read(ipv4);
			String hostAddress = Inet4Address.getByAddress(ipv4).getHostAddress();
			inputStream.read(portBuffer);
			port = ByteBuffer.wrap(portBuffer).asShortBuffer().get();
			proxySocket = new Socket(hostAddress,port);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug(String.format("connect {}:{}", hostAddress,port));
			}
 			break;
		case 0x02:
			byte domainLength = (byte) inputStream.read();
			byte[] domainBuf = new byte[domainLength];
			inputStream.read(domainBuf);
			String domain = new String(domainBuf,Charset.forName("UTF-8"));
			portBuffer = new byte[2];
			port = ByteBuffer.wrap(portBuffer).asShortBuffer().get();
			proxySocket = new Socket(domain, port);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug(String.format("connect {}:{}", domain,port));
			}
			break;
		case 0x04:
			throw new RuntimeException("not support UDP");
		default:
			throw new RuntimeException();
		}
		return null;
	}
	public static void main(String[] args) throws UnknownHostException {
		byte[] buffer = new byte[]{-1,-1,-1,-1};
		
		System.out.println(Inet4Address.getByAddress(buffer).getHostAddress());
	}
}
