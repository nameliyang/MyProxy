package com.ly.sock5.session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ly.sock5.phase.ConnnectionMessage;
import com.ly.sock5.phase.SelectMethodMessage;
import com.ly.sock5.pipeline.PipeLineSocket;

public class Session {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);
	
	Long sessionId;
	
	Socket clientSocket;
	
	InputStream inputStream;
	OutputStream outputStream;
	
	Socket proxySocket;
	
	public Session(Long sessionId,Socket socket) throws IOException{
		this.sessionId = sessionId;
		this.clientSocket = socket;
		inputStream = new BufferedInputStream(socket.getInputStream());
		outputStream = new BufferedOutputStream(socket.getOutputStream());
	}

	public void doSelectMethod(SelectMethodMessage selectMsg) throws Exception{
		selectMsg.doSelectMethod(inputStream);
	}
	
	
	public void close()  {
		if(clientSocket!=null&&!clientSocket.isClosed()){
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(proxySocket!=null&&!proxySocket.isClosed()){
			try {
				proxySocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void writeAndFlush(byte[] bs) throws IOException {
		outputStream.write(bs);
		outputStream.flush();
	}
	
	public Socket doConnect(ConnnectionMessage connMsg) throws IOException {
		return connMsg.doConnect(inputStream);
	}

	public void transfer(Socket proxySocket) throws IOException, InterruptedException {
		this.proxySocket = proxySocket;
		PipeLineSocket socket = new PipeLineSocket(clientSocket, proxySocket);
		socket.start();
	}
	
}
