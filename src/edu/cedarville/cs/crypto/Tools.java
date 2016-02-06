package edu.cedarville.cs.crypto;

import java.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Tools {
	
	public static Integer[] convertFromBytesToInts(byte[] bs) {
		IntBuffer ints = ByteBuffer.wrap(bs).asIntBuffer();
		int[] result = new int[ints.remaining()];
		ints.get(result);
		return result;
	}
	
	public static Integer[] convertFromHexStringToInts(String s) {
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
