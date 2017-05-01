package com.ly.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.ly.buffer.ProxyBuffer;
import com.ly.buffer.ProxyBuffer.BufferState;

public class ClientHandler{
	
	SocketChannel clientChannel;
	Config config;
	SocketChannel serverChannel;
	ProxyBuffer clientBuffer = new ProxyBuffer();
	ProxyBuffer serverBuffer = new ProxyBuffer();
	Selector selector ;
	
	public ClientHandler(SocketChannel socketChannel, Config config){
		this.clientChannel = socketChannel;
		this.config = config;
	}

	public void initEvents(Selector selector) throws IOException{
		this.selector = selector;
		clientChannel.configureBlocking(false);
		
		serverChannel = SocketChannel.open();
		serverChannel.connect(new InetSocketAddress(config.getRemoteHost(),config.getRemotePort()));
		serverChannel.configureBlocking(false);
		clientChannel.register(selector,SelectionKey.OP_READ,this);
		serverChannel.register(selector, SelectionKey.OP_READ,this);
//		register();
	}
	
	public void registerClientChannel() throws ClosedChannelException {
		int interstOps =0;
		if(serverBuffer.state == BufferState.READY_TO_READ){
			interstOps|=SelectionKey.OP_WRITE;
		}else if(clientBuffer.state == BufferState.READY_TO_WRITE){
			interstOps|=SelectionKey.OP_READ;
		}
		clientChannel.register(selector, interstOps,this);
	}
	
	public void handler(SocketChannel socketChannel) throws IOException {
		SelectionKey selectKey = socketChannel.keyFor(selector);
		if(selectKey.isReadable()){
			int num  = readFromChannel(socketChannel);
			if(num == -1){
				closeChannel();
				return;
			}
		} else if(selectKey.isWritable()){
			writeToChannel(socketChannel);
		}
		register();
	}
	
	private void register() throws ClosedChannelException  {
		int interstOps =0;
		if(serverBuffer.state == BufferState.READY_TO_READ){
			interstOps|=SelectionKey.OP_WRITE;
		}else if(clientBuffer.state == BufferState.READY_TO_WRITE){
			interstOps|=SelectionKey.OP_READ;
		}
		
		clientChannel.register(selector, interstOps,this);
		 interstOps =0;
		if(serverChannel.isConnected()){
			interstOps = 0;
			if(clientBuffer.state==BufferState.READY_TO_READ){
				interstOps|=SelectionKey.OP_WRITE;
			}else if(serverBuffer.state==BufferState.READY_TO_WRITE){
				interstOps|=SelectionKey.OP_READ;
			}
			//if(interstOps!=0){
				serverChannel.register(selector, interstOps,this);
		//	}
		}
		
	}
	private void closeChannel() throws IOException {
		   clientChannel.keyFor(selector).cancel();
		   serverChannel.keyFor(selector).cancel();
		   clientChannel.close();
		   serverChannel.close();
	}

	private void writeToChannel(SocketChannel socketChannel) throws IOException {
		if(socketChannel == serverChannel){
			clientBuffer.writeToChannel(socketChannel);
		}else if(socketChannel == clientChannel){
			serverBuffer.writeToChannel(socketChannel);
		}
	}

	private int readFromChannel(SocketChannel socketChannel) throws IOException {
		if(socketChannel == serverChannel){
			return serverBuffer.readFromChannel(socketChannel);
		}else if(socketChannel == clientChannel){
			return clientBuffer.readFromChannel(socketChannel);
		}
		throw new RuntimeException("");
	}

}