package com.ly.charset;

public class UnicodeTest {
	
	public static void main(String[] args) {
		System.out.println("RUNNING     :"+addZero(Integer.toBinaryString(-1<<29)));
		System.out.println("SHUTDOWN    :"+addZero(Integer.toBinaryString(0<<29)));
		System.out.println("STOP        :"+addZero(Integer.toBinaryString(1<<29)));
		System.out.println("TIDYING     :"+addZero(Integer.toBinaryString(2<<29)));
		System.out.println("TERMINATED  :"+addZero(Integer.toBinaryString(3<<29)));
		System.out.println("COUNT_BITS  :"+addZero(Integer.toBinaryString((Integer.SIZE - 3)<<29)));
		System.out.println("CAPACITY    :"+addZero(Integer.toBinaryString(( (1 << (Integer.SIZE - 3)) - 1))));
		System.out.println("~CAPACITY   :"+addZero(Integer.toBinaryString(~(( (1 << (Integer.SIZE - 3)) - 1)))));
		
		
	}
	
	public static String addZero(String i	){
		if(i.length()<32){
			String rtnStr ="";
			int zeroLength = 32-i.length();
			for(int j = 0;j<zeroLength;j++){
				rtnStr ="0"+rtnStr;
			}
			return rtnStr+i;
		}
		
		return i;
	}
}
