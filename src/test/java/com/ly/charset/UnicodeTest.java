package com.ly.charset;

public class UnicodeTest {
	
	public static void main(String[] args) {
		String s = "\uD801\uDC28";
		System.out.println(s);
		String str = "𐐨";
		char t = str.charAt(2);
		System.out.println(t);
	}
}
