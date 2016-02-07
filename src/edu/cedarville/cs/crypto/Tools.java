package edu.cedarville.cs.crypto;

import java.lang.System;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Tools {
	
	public static Integer[] convertFromBytesToInts(byte[] bs) {
		// If the length cannot make full 64 bit blocks pad with space characters
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

		return convertIntArrayToIntegerArray(result);
	}

	private static Integer[] convertIntArrayToIntegerArray(int[] array){
		Integer[] result = new Integer[array.length];
		for(int i = 0; i < array.length; i++){
			result[i] = array[i];
		}
		return result;
	}

	private static int[] convertIntegerArrayToIntArray(Integer[] array){
		int[] result = new int[array.length];
		for(int i = 0; i < array.length; i++){
			result[i] = array[i];
		}
		return result;
	}
	
	public static Integer[] convertFromHexStringToInts(String s) {
		// If the lenght cannot make full 64 bit blocks pad with 0's
		if(s.length() / 16 != 0){
			for(int i = 0; i < s.length() % 16; i++){
				s += "0";
			}
		}
		return convertFromBytesToInts(DatatypeConverter.parseHexBinary(s));
	}
	
	public static byte[] convertFromIntsToBytes(Integer[] ints) {
		int[] data = convertIntegerArrayToIntArray(ints);
		ByteBuffer bytebuff = ByteBuffer.allocate(data.length * Integer.SIZE / 8);

		IntBuffer intbuff = bytebuff.asIntBuffer();
		intbuff.put(data);

		byte[] result = bytebuff.array();

		return result;
	}
	
	public static String convertFromIntsToHexString(Integer[] ints) {
		return DatatypeConverter.printHexBinary(convertFromIntsToBytes(ints));
	}
	
}
