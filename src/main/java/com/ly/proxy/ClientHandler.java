package com.ly.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientHandler{
	
	SocketChannel socketChannel;
	Config config;
	SocketChannel serverChannel;
	public ClientHandler(SocketChannel socketChannel, Config config){
		this.socketChannel = socketChannel;
		this.config = config;
	}

	public void handler() throws IOException{
		socketChannel.configureBlocking(false);
		SocketChannel serverChannel = SocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.connect(new InetSocketAddress(config.getHost(), config.getPort()));
		register();
	}

	private void register() {
		
	}
}