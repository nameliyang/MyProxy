package com.ly.sock5.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ly.sock5.method.SelectMethodMessage;
import com.ly.sock5.session.Session;

public class NoAuthSocketHandler implements SocksHandler ,Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NoAuthSocketHandler.class);
	
	private Session session;
	private static final byte SUPPORT_METHOD = 0x00;
	
	public NoAuthSocketHandler(Session session){
		this.session = session;
	}

	@Override
	public void run() {
		SelectMethodMessage selectMsg = new SelectMethodMessage();
		try {
			session.doSelectMethod(selectMsg);
			byte[] methods = selectMsg.getMethods();
			boolean findSupportMethod = findSupportMethod(methods);
			int rtnMethod = findSupportMethod ?0x00:0xFF;
			session.writeAndFlush(new byte[]{0x05,(byte)rtnMethod});
			if(rtnMethod == 0xFF ){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	
	public boolean findSupportMethod(byte[] methods){
		boolean findSupport = false;
		for(byte method:methods){
			if(method == SUPPORT_METHOD){
				findSupport = true;
				break;
			}
		}
		return findSupport;
	}
	
}
