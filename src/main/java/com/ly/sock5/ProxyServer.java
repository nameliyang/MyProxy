package com.ly.sock5;

import java.io.IOException;

import com.ly.sock5.handler.SocksHandler;

public interface ProxyServer {
	
	int DEFAULT_PORT = 1080;
	
	public void start() throws IOException;
	
	public void stop();
	
	public SocksHandler createHandler();
	
	
}
