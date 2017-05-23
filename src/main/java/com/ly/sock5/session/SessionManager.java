package com.ly.sock5.session;

import java.net.Socket;

public interface SessionManager {
	
	
	public Session newSession(Socket socket);
	
	
	
	
}
