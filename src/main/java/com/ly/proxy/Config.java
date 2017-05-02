package com.ly.proxy;

public class Config {
	private String remoteHost;
	private int localPort;
	private static final String localhost = "127.0.0.1";
	private int remotePort;
	
	public Config(int localPort,String remoteHost,int remotePort){
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.localPort = localPort;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getLocalhost() {
		return localhost;
	}


	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	
	
}
