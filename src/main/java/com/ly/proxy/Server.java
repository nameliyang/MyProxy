package com.ly.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
	
	Selector selector;
	Config config;
	
	public Server(Config config){
		this.config = config;
	}
	

	public void bindServerSocket(int port) throws IOException{
		if(selector ==null){
			selector = Selector.open();
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(port));
			serverChannel.configureBlocking(false);
		}
	}
	
	public void start() throws IOException{
		while(!Thread.interrupted()){
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				handler(key);
				iterator.remove();
			}
		}
	}
	
	
	private void handler(SelectionKey key) throws IOException {
		if(key.isValid()&&key.isAcceptable()){
			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = serverChannel.accept();
			new ClientHandler(socketChannel,config).handler();
		}
	}

	
	public static void main(String[] args) {
		
	}
}


