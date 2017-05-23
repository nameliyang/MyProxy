package com.ly.sock5.session;

import java.net.Socket;

public class Session {
	
	private Long sessionId;
	
	private Socket socket;
	
	public Session(Long sessionId,Socket socket){
		this.sessionId = sessionId;
		this.socket = socket;
	}
	
	
}
