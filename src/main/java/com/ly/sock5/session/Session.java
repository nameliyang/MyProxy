package com.ly.sock5.session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ly.sock5.method.SelectMethodMessage;

public class Session {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
	
	Long sessionId;
	
	Socket socket;
	
	InputStream inputStream;
	OutputStream outputStream;
	
	public Session(Long sessionId,Socket socket) throws IOException{
		this.sessionId = sessionId;
		this.socket = socket;
		inputStream = new BufferedInputStream(socket.getInputStream());
		outputStream = new BufferedOutputStream(socket.getOutputStream());
	}

	public void doSelectMethod(SelectMethodMessage selectMsg) throws Exception{
		selectMsg.doSelectMethod(inputStream);
		
	}
	
	
	public void close()  {
		if(socket!=null&&!socket.isClosed()){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeAndFlush(byte[] bs) throws IOException {
		outputStream.write(bs);
		outputStream.flush();
	}
	
}
