package com.ly.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServer {
	
	final static ExecutorService service = Executors.newCachedThreadPool();
	
	public static void main(String[] args) throws IOException {
			ServerSocket serverSocket = new ServerSocket(2333);
			System.out.println("start server.....");
			while(true){
				Socket socket = serverSocket.accept();
				System.out.println("accept a new socket.....");
				service.submit(new EchoServer(socket));
			}
			
	}
	
	static class EchoServer implements Runnable{
		
		private Socket socket;

		public EchoServer(Socket socket){
			this.socket = socket;
		}
		
		@Override
		public void run() {
			try {
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer.println(new Date().toLocaleString());writer.flush();
				String line = "";
				while((line = bufferedReader.readLine())!=null&&!"".equals(line)){
					writer.println(line.toUpperCase());
					writer.flush();
					System.out.println("read line = "+line);
				}
				bufferedReader.close();
				writer.close();
				System.out.println("end server........");
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(socket!=null){
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
}
