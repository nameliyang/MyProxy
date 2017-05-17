package com.ly.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		String host = "127.0.0.1";
		int port = 8080;
		Socket socket = new Socket(host,port);
		OutputStream outputStream = socket.getOutputStream();
		PrintWriter out = new PrintWriter(outputStream);
		
		out.println("GET / HTTP/1.1");
		out.println("Host: localhost:8080");
		out.println("Connection: keep-alive");
		out.println();
		out.println();
		out.flush();
		
		Thread thread = new Thread(){
			public void run() {
				InputStream inputStream;
				try {
					inputStream = socket.getInputStream();
					int read = 0;
					while((read = inputStream.read())!=-1){
						System.out.print((char)read);
					}
						
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			};
		};
		thread.start();
		thread.join();
		
		socket.close();
		System.out.println("..................");
		System.out.println("localAddress："+socket.getLocalAddress());
		System.out.println("localAddress:"+socket.getLocalSocketAddress());
		System.out.println("localport:"+socket.getLocalPort());
	}
}
