package com.ly.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketImpl;

public class ProxyClient {
	
	public static void main(String[] args) throws IOException {
		String host = "127.0.0.1";
		int port = 2333;
		String proxyHost = "127.0.0.1";
		Proxy proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxyHost,1081));
		Socket socket = new Socket(proxy);
	//	test(socket);
		socket.connect(new InetSocketAddress(host,port));
		InputStream inputStream = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String readLine = reader.readLine();
		System.out.println("readLine:"+readLine);
		socket.close();
	}
	
	
	public static void test(Socket socket){
		Class clazzSocks  = socket.getClass();
		Method setSockVersion  = null;
		Field sockImplField = null; 
		SocketImpl socksimpl = null; 
		try {
		  sockImplField = clazzSocks.getDeclaredField("impl");
		  sockImplField.setAccessible(true);
		  socksimpl  = (SocketImpl) sockImplField.get(socket);
		  Class clazzSocksImpl  =  socksimpl.getClass();
		  setSockVersion  = clazzSocksImpl.getDeclaredMethod("setV4");
		  setSockVersion.setAccessible(true);
		  if(null != setSockVersion){
		      setSockVersion.invoke(socksimpl);
		  }
		  sockImplField.set(socket, socksimpl);
		} catch (Exception e) {

		} 
	}
}
