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
	
	public void start() throws IOException{
		if(selector ==null){
			selector = Selector.open();
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(config.getLocalPort()));
			serverChannel.configureBlocking(false);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		
		while(!Thread.interrupted()){
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			while(iterator.hasNext()){
				SelectionKey key = iterator.next();
				iterator.remove();
				if(!key.isValid()){
					System.out.println("key is not valid !!!!!!!!!!!!!!!!");
					continue;
				}
				handler(key);
			}
		}
	}
	
	private void handler(SelectionKey key) throws IOException {
		if(key.isAcceptable()){
			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = serverChannel.accept();
			ClientHandler clientHandler = new ClientHandler(socketChannel,config);
			clientHandler.initEvents(selector);
		} else if(key.isReadable()){
			SocketChannel socketChannel = (SocketChannel) key.channel();
			ClientHandler clientHandler = (ClientHandler) key.attachment();
			clientHandler.handler(socketChannel);
		}else if(key.isConnectable()){
			System.out.println(" isConnectable ....");
		}else if(key.isWritable()){
			SocketChannel socketChannel = (SocketChannel) key.channel();
			ClientHandler clientHandler = (ClientHandler) key.attachment();
			clientHandler.handler(socketChannel);
		}
	}

	
	public static void main(String[] args) throws IOException {
		System.setProperty("BUFFER_SIZE", "4");
		String localhost = "127.0.0.1";
		int localPort = 88	;
		String remoteHost = "127.0.0.1";
		int remotePort = 2333;
		Server server  = new Server(new Config(localhost, localPort,remoteHost,remotePort));
		server.start();
	}
}


