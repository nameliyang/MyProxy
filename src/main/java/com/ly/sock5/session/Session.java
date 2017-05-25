package com.ly.sock5.session;

import java.net.Socket;

import com.ly.sock5.method.SelectMethodMessage;

public class Session {
	
	private Long sessionId;
	
	private Socket socket;
	
	public Session(Long sessionId,Socket socket){
		this.sessionId = sessionId;
		this.socket = socket;
	}

	public void read(SelectMethodMessage selectMsg) {
		selectMsg.read(this);
	}
	
	
}
