package com.ly.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProxyServer {
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleProxyServer.class);
	
	private static final byte[] supportMethods = new byte[]{
		0x00
	};
	static final BlockingQueue<Runnable> queues = new LinkedBlockingQueue<Runnable>(100);
	
	static final ExecutorService service = 
			new ThreadPoolExecutor(10, 100,
            0L, TimeUnit.MILLISECONDS,
            queues);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ServerSocket serverSocket = new ServerSocket(1081);
		serverSocket.setReuseAddress(true);
		serverSocket.setSoTimeout(0);
		while(true){
			Socket socket;
			socket = serverSocket.accept();
			logger.debug("accept a new socket "+queues.size());
			service.submit(new Runnable() {
				@Override
				public void run() {
					try {
						Socket proxySocket = handler(socket);
						SocketPipe socketPipe = new SocketPipe(socket, proxySocket);
					//	socketPipe.start();
						transf(socket,proxySocket);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
	private static  void close(Socket socket, final InputStream inputStream,
			final OutputStream outputStream) {
		try{
			inputStream.close();
			outputStream.close();
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	};
	
	public static final AtomicInteger  id = new AtomicInteger(0);
	private static final int BUFF_SIZE = 1024*1024*10;
	private static void transf(Socket socket, Socket proxySocket) throws IOException, InterruptedException {
		proxySocket.setSoTimeout(2000);
		final InputStream inputStream = socket.getInputStream();
		final OutputStream outputStream = socket.getOutputStream();
		final InputStream proxyInputStream = proxySocket.getInputStream();
		final OutputStream proxyOutputStream = proxySocket.getOutputStream();
		final CountDownLatch latch  = new CountDownLatch(2);
		final int id = SimpleProxyServer.id.incrementAndGet();
		Thread serverThread = new Thread(){
			public void run() {
				long startTime = System.currentTimeMillis();
				System.out.println("--------------clientThread start id = "+id+"-------------");
				try {
					byte[] buffer = new byte[BUFF_SIZE];
					while(true){
						int  read =  inputStream.read(buffer);
						if(read == -1){
							logger.info("客户端关闭连接,将主动关闭代理proxysocket");
							proxySocket.close();
							break;
						}
						writeToRemoteServer(proxyOutputStream,buffer, read);
					}
					
				} catch (IOException e) {
					logger.error(e.toString());
				}finally{
					latch.countDown();
					close(socket, inputStream, outputStream);
					long endTime = System.currentTimeMillis();
					System.out.println("--------------clientThread end id = "+id+",time="+(endTime-startTime)/1000);
				}
			}

			private void writeToRemoteServer(
					final OutputStream proxyOutputStream,byte[] buffer, int read)
					throws IOException {
				try{
					proxyOutputStream.write(buffer,0,read);
					proxyOutputStream.flush();
					
				}catch(IOException e){
					close(proxySocket, proxyInputStream, proxyOutputStream);
					throw e;
				}
			}

		};
		Thread   clientThread = new Thread(){
			public void run() {
				ByteBuffer tmpBuffer = ByteBuffer.allocate(1024);
				long startTime = System.currentTimeMillis();
				System.out.println("--------------proxyThread start id = "+id+"-------------");
				try {
					byte[] buffer = new byte[BUFF_SIZE];
					while(true){
						int read =  proxyInputStream.read(buffer);
						if(read==-1){
							logger.info("远程主机关闭连接。。。将关闭客户端socket");
							socket.close();
							break;
						}else{
							if(tmpBuffer.hasRemaining()){
								tmpBuffer.put((byte) (read&0xFF));
							}else{
								
							}
							writeToClient(outputStream,buffer, read);
						}
					}
				} catch (IOException e) {
					logger.error(e.toString());
				}finally{
					latch.countDown();
					close(proxySocket, proxyInputStream, proxyOutputStream);
					long endTime = System.currentTimeMillis();
					System.out.println("--------------proxyThread end id = "+id+",time="+(endTime-startTime)/1000);
				}
			}

			private void writeToClient(final OutputStream outputStream,byte[] buf, int read)
					throws IOException {
				try{
					outputStream.write(buf,0,read);
					outputStream.flush();
				}catch(IOException e){
					close(socket, inputStream, outputStream);
					throw e;
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
			    	logger.error("{}:{} 不可达 ",host,port);
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
