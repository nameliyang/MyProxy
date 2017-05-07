package com.ly.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
	public static void main(String[] args) throws UnknownHostException {
		
		byte[] tmp = new byte[]{(byte) (-1&0xff), -1, -1, -1};
		String hostAddress = InetAddress.getByAddress(tmp).getHostAddress();
		System.out.println(hostAddress);
	}
}
