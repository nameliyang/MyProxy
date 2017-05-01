package com.ly.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyBuffer {
	
	private static Logger logger = Logger.getAnonymousLogger();
	
	private ByteBuffer byteBuffer;
	
	public BufferState state = BufferState.READY_TO_WRITE;
	
	private static final int DEFAULT_BUFF_SIZE = 20;
	
	public ProxyBuffer(){
		String buffer_size = System.getProperty("BUFFER_SIZE");
		byteBuffer = ByteBuffer.allocate(buffer_size==null?DEFAULT_BUFF_SIZE:Integer.parseInt(buffer_size));
	}
	
	public int  readFromChannel(SocketChannel socketChannel) throws IOException{
		int readNum = socketChannel.read(byteBuffer);
		if(readNum==-1){
			logger.log(Level.INFO, "socketChannel read -1");
			return -1;
		}
		if(readNum > 0){
			byteBuffer.flip();
			state = BufferState.READY_TO_READ;
		}
		
		logger.log(Level.INFO,"socketChannel read:"+logBuffer(byteBuffer));
		
		return readNum;
	}
	private   String  logBuffer(ByteBuffer byteBuffer){
		int position = byteBuffer.position();
		StringBuilder sb = new StringBuilder();
		while(byteBuffer.hasRemaining()){
			sb.append((char)byteBuffer.get());
		}
		byteBuffer.position(position);
		return sb.toString();
	}
	
	public void writeToChannel(SocketChannel socketChannel) throws IOException{
		socketChannel.write(byteBuffer);
		if(byteBuffer.remaining()==0){
			byteBuffer.clear();
			state = BufferState.READY_TO_WRITE;
		}
	}
	
	public static enum BufferState {
		READY_TO_WRITE, READY_TO_READ
	}
}
