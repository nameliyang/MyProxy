package com.ly.sock5.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public class PipeLineStream implements Runnable {
	
	private int state;
	
	private static final int STARTING = 1;
	private static final int RUNNING = 2;
	private static final int STOPPING = 3;
	private static final int STOPPED = 4;
	InputStream inputStream;
	
	OutputStream outputStream;
	
	
	public PipeLineStream(InputStream inputStream,OutputStream outputStream){
		this.inputStream = inputStream ;
		this.outputStream = outputStream;
		
	}

	@Override
	public void run() {
		state = PipeLineStream.RUNNING;
		byte[] buffer = new byte[1024*1024];
		int length = 0;
		try {
			while((length =inputStream.read(buffer))!=-1){
				outputStream.write(buffer, 0, length);
			}
		} catch (InterruptedIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			state = PipeLineStream.STOPPED;
		}
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public boolean isRunning() {
		return state <= PipeLineStream.RUNNING;
	}
	
	public boolean isStopping(){
		return state >= PipeLineStream.STOPPED;
	}
	
	public void stop(){
		state = STOPPED;
		Thread.currentThread().interrupt();
	}
	
	
}
