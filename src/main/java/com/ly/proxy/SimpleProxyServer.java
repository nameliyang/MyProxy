package com.ly.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SimpleProxyServer {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final byte[] supportMethods = new byte[]{
		0x00
	};
	
	static final ExecutorService service = Executors.newFixedThreadPool(10);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ServerSocket serverSocket = new ServerSocket(1081);
		while(true){
			Socket socket;
			socket = serverSocket.accept();
			try {
				Socket proxySocket = handler(socket);
				transf(socket,proxySocket);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			service.submit(new Runnable() {
//				@Override
//				public void run() {
//					
//					
//				}
//			});
		}
	}
	
	  
	private static void transf(Socket socket, Socket proxySocket) throws IOException, InterruptedException {
		final InputStream inputStream = socket.getInputStream();
		final OutputStream outputSteam = socket.getOutputStream();
		final InputStream proxyInputStream = proxySocket.getInputStream();
		final OutputStream proxyOutputStream = proxySocket.getOutputStream();
		final CountDownLatch latch  = new CountDownLatch(2);
		
		Thread serverThread = new Thread(){
			public void run() {
				try {
					while(true){
						int  read =  inputStream.read();
						if(read==-1){
							proxySocket.close();
							break;
						}else{
							proxyOutputStream.write(read);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					latch.countDown();
				}
			};
		};
		
		Thread   clientThread = new Thread(){
			public void run() {
				try {
					while(true){
						int read =  proxyInputStream.read();
						if(read==-1){
							socket.close();
							break;
						}else{
							outputSteam.write(read);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					latch.countDown();
				}
			};
		};
		serverThread.start();
		clientThread.start();
		latch.await();
	}
    
	
	private static Socket handler(Socket socket) throws IOException {
		InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = socket.getOutputStream();
		int read = inputStream.read();
		byte[] methods;
		//socketProxy 5
		if(read==5){
			int nmethods = inputStream.read(); 
			methods = new byte[nmethods];
			for(int i =0;i<nmethods;i++){
				methods[i] = (byte) inputStream.read();
			}
			byte selectMethods = selectMethods(methods);
			if(selectMethods != -1){
				outputStream.write(new byte[]{0x5,selectMethods});
				outputStream.flush();
				logger.info("write client msg 0x5 "+Integer.toHexString(selectMethods));
				byte[] headers = new byte[4];
				inputStream.read(headers);
				logger.info("headers = "+ Arrays.toString(headers));
				byte command = headers[1];
				String host = getHost(headers[3],inputStream);
				byte[] p = new byte[2];
				inputStream.read(p);
				int port = ByteBuffer.wrap(p).asShortBuffer().get();
			    logger.info("connect "+host+", "+port);
			    ByteBuffer byteBuffer = ByteBuffer.allocate(20);
			    byteBuffer.put((byte) 0x5);
			    if(command == 0x1){
			    	Socket proxySocket = new Socket(host, port);
			    	byteBuffer.put((byte) 0x00);
			    	byteBuffer.put((byte) 0x00);
			    	byteBuffer.put((byte) 0x01);
			    	
			    	byteBuffer.put(socket.getLocalAddress().getAddress());
		            Short localPort = (short) ((socket.getLocalPort()) & 0xFFFF);
		            byteBuffer.putShort(localPort);
		            outputStream.write(byteBuffer.array(),0,byteBuffer.position());
		            outputStream.flush();
		            return proxySocket;
			    }else{
			    	throw new RuntimeException();
			    }
			}  
			
		}
		return null;
	}

	private static String getHost(byte b, InputStream inputStream) throws IOException {
		String host = null;
		switch (b) {
		case 0x01:
			byte[] hostip = new byte[4];
			inputStream.read(hostip);
			host = InetAddress.getByAddress(hostip).getHostAddress();
			break;
		case 0x03:
			int l = inputStream.read();
			byte[] hbyte = new byte[l];
			inputStream.read(hbyte);
			host = new String(hbyte);
			break;
		default:
			break;
		}
		return host;
	}

	private static byte selectMethods(byte[] methods) {
		for(byte supprotMethod :supportMethods){
			for(int i =0;i<methods.length;i++){
				if(supprotMethod==methods[i]){
					return supprotMethod;
				}
			}
		}
		return -1;
	}
}
