package com.ly.proxy;

public class Config {
	private String remoteHost;
	private int localPort;
	private String localhost;
	private int remotePort;
	
	public Config(String localHost,int localPort,String remoteHost,int remotePort){
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.localhost = localHost;
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

	public void setLocalhost(String localhost) {
		this.localhost = localhost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	
	
}
