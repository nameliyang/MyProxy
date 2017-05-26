package com.ly.sock5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.ly.sock5.handler.NoAuthSocketHandler;
import com.ly.sock5.handler.SocksHandler;
import com.ly.sock5.session.BasicSessionManager;
import com.ly.sock5.session.Session;
import com.ly.sock5.session.SessionManager;
import com.ly.sock5.session.SocketWrapper;

public class BasicProxyServer implements ProxyServer,Runnable{
	
	ServerSocket serverSocket  = null;
	
	private ExecutorService service ;
	private int port ;
	
	SessionManager sessionManager = new BasicSessionManager();
	
	public  BasicProxyServer(ExecutorService service,int port) throws IOException {
		this.service = service;
		this.port = port;
		serverSocket = new ServerSocket(port, 50);
	}
	
	public BasicProxyServer(ExecutorService service) throws IOException {
		this(service,DEFAULT_PORT);
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
			try {
				Socket socket  = new SocketWrapper(serverSocket.accept());
				
				Session session = sessionManager.newSession(socket);
				NoAuthSocketHandler handler = new NoAuthSocketHandler(session);
				service.execute(handler);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
