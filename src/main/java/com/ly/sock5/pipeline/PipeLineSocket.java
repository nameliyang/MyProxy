package com.ly.sock5.pipeline;

import java.io.IOException;
import java.net.Socket;

public class PipeLineSocket{
	
	PipeLineStream clientStream;
	PipeLineStream proxyStream;
	
//	private static final ExecutorService service = new ThreadPoolExecutor(0, 100,
//            60L, TimeUnit.SECONDS,
//            new SynchronousQueue<Runnable>());
	
	public PipeLineSocket(Socket clientSocket,Socket proxySocket) throws IOException{
		clientStream = new PipeLineStream(clientSocket.getInputStream(), proxySocket.getOutputStream());
		proxyStream  = new PipeLineStream(proxySocket.getInputStream(), clientSocket.getOutputStream());
	}

	public void start() throws InterruptedException {
		clientStream.start();
		proxyStream.start();
		while(clientStream.isRunning()&&proxyStream.isRunning()){
			Thread.sleep(1000);
		}
		clientStream.stop();
		proxyStream.stop();
	}
}
