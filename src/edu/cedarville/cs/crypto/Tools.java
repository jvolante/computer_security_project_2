package edu.cedarville.cs.crypto;

import java.lang.System;
import java.nio.charset.StandardCharsets;
import java.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Tools {
	
	public static Integer[] convertFromBytesToInts(byte[] bs) {
		// If the lenght cannot make full 64 bit blocks pad with space characters
		if(bs.length / 8 != 0){
			byte[] tmp = bs;
			bs = new byte[tmp.length + tmp.length % 8];
			System.arraycopy(tmp, 0, bs, 0, tmp.length);

			for(int i = tmp.length; i < bs.length; i++){
				bs[i] = " ".getBytes(StandardCharsets.US_ASCII)[0];
			}
		}

		IntBuffer ints = ByteBuffer.wrap(bs).asIntBuffer();
		int[] result = new int[ints.remaining()];
		ints.get(result);
		return result;
	}
	
	public static Integer[] convertFromHexStringToInts(String s) {
		// If the lenght cannot make full 64 bit blocks pad with 0's
		if(s.length() / 16 != 0){
			for(int i = 0; i < s.length() % 16; i++){
				s += "0";
			}
		}
		return convertFromBytesToInts(DatatypConverter.parseHexBinary(s));
	}
	
	public static byte[] convertFromIntsToBytes(Integer[] ints) {
		ByteBuffer bytebuff = ByteBuffer.allocate(ints.length * Integer.SIZE / 8);

		IntBuffer intbuff = ByteBuffer.asIntBuffer();
		intbuff.put(ints);

		byte[] result = bytebuff.array();

		return result;
	}
	
	public static String convertFromIntsToHexString(Integer[] ints) {
		return DatatypeConverter.printHexBinary(convertFromIntsToBytes(ints));
	}
	
}
