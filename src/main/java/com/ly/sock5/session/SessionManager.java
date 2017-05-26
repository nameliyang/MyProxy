package com.ly.sock5.session;

import java.io.IOException;
import java.net.Socket;

public interface SessionManager {
	
	
	public Session newSession(Socket socket) throws IOException;
	
	
	
	
}
