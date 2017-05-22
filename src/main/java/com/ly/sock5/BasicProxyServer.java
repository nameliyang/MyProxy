package com.ly.sock5;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

import com.ly.sock5.handler.SocksHandler;

public class BasicProxyServer implements ProxyServer,Runnable{
	
	ServerSocket serverSocket  = null;
	
	private ExecutorService service = null;
	private int port ;
	
	public  BasicProxyServer(ExecutorService service) {
		this.service = service;
	}
	
	@Override
	public void start() throws IOException {
		
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void stop() {
		Thread.currentThread().interrupt();
	}

	@Override
	public SocksHandler createHandler() {
		return null;
	}

	@Override
	public void run() {
		while(!Thread.interrupted()){
		}
	}

}
