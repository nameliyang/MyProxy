package com.ly.sock5.handler;

import com.ly.sock5.session.Session;

public class NoAuthSocketHandler implements SocksHandler ,Runnable{
	
	private Session session;
	
	public NoAuthSocketHandler(Session session){
		this.session = session;
	}

	@Override
	public void run() {
		
		
	}
	
}
