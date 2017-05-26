package com.ly.sock5.session;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class BasicSessionManager implements SessionManager{
	
	private AtomicLong sessionId = new AtomicLong();
	
	@Override
	public Session newSession(Socket socket) throws IOException {
		Session session = new Session(sessionId.incrementAndGet(),socket);
		return session;
	}

}
