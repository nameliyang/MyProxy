package com.ly.sock5.method;

import java.io.IOException;
import java.io.InputStream;

public class SelectMethodMessage {
	  byte protocolVersion;

	  int methodNum;
      byte[] methods;

	  
	
	public void doSelectMethod(InputStream inputStream) throws IOException {
		methods = new byte[methodNum];
		inputStream.read(methods);
	}

	public void readMethods() throws IOException{
		
	}

	public byte getProtocolVersion() {
		return protocolVersion;
	}

	public byte[] getMethods() {
		return methods;
	}

	
}
