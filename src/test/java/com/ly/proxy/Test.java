package com.ly.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {
	public static void main(String[] args) throws IOException {
		String host = "127.0.0.1";
		int port = 8080;
		Socket socket = new Socket(host,port);
		OutputStream outputStream = socket.getOutputStream();
		PrintWriter out = new PrintWriter(outputStream);
		
		out.println("GET /hello HTTP/1.1");
		out.println("Host: localhost:8080");
		out.println("Connection: Close");
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
		
		
		socket.close();
		System.out.println("..................");
	}
}
