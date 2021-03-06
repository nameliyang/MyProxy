package com.ly.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
	
	Selector selector;
	
	Config config;
	
	public Server(Config config) throws ClosedChannelException, IOException{
		this.config = config;
		initServer();
	}
	
	public void start() throws IOException{
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

	private void initServer() throws IOException, ClosedChannelException {
		if(selector ==null){
			selector = Selector.open();
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.bind(new InetSocketAddress(config.getLocalPort()));
			serverChannel.configureBlocking(false);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
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
			clientHandler.readHandler(socketChannel);
		}else if(key.isConnectable()){
			System.out.println(" isConnectable ....");
		}else if(key.isWritable()){
			SocketChannel socketChannel = (SocketChannel) key.channel();
			ClientHandler clientHandler = (ClientHandler) key.attachment();
			clientHandler.writeHandler(socketChannel);
		}
	}

	
	public static void main(String[] args) throws IOException {
		System.setProperty("BUFFER_SIZE", "4");
		
		int localPort = 88	;
		String remoteHost = "192.168.199.15";
		int remotePort = 22;
		Server server  = new Server(new Config(localPort,remoteHost,remotePort));
		server.start();
	}
}


