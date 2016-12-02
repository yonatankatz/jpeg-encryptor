package jonateam.encryptor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * This class is a utility for stuffing and unstuffing bytes. in jpeg format,
 * If you want to specify 0xFF byte as a value and not as end of marker, you to "stuff" it -
 * meaning to add 0x00 afterwards.
 * Since the shuffling changes the order of the byte, we need to carefully handle the stuffing bytes.
 * 
 * @author yonatan katz
 *
 */
public class BytesStuffer {
	
	/**
	 * test
	 */
	public static void main(String[] args) {
		byte[] b1 = {};
		byte[] b2 = {(byte)0x11, (byte)0x1A, (byte)0xFF, (byte)0x12};
		byte[] b3 = {(byte)0x11, (byte)0x1A, (byte)0xFF};
		byte[] b4 = {(byte)0x11, (byte)0x1A, (byte)0xFF, (byte)0xFF};
		byte[] ab1 = stuffBytes(b1);
		byte[] ab2 = stuffBytes(b2);
		byte[] ab3 = stuffBytes(b3);
		byte[] ab4 = stuffBytes(b4);
		System.out.println(Arrays.toString(b1));
		System.out.println(Arrays.toString(b2));
		System.out.println(Arrays.toString(b3));
		System.out.println(Arrays.toString(b4));
		System.out.println(Arrays.toString(ab1));
		System.out.println(Arrays.toString(ab2));
		System.out.println(Arrays.toString(ab3));
		System.out.println(Arrays.toString(ab4));
		System.out.println(Arrays.toString(unstuffBytes(ab1)));
		System.out.println(Arrays.toString(unstuffBytes(ab2)));
		System.out.println(Arrays.toString(unstuffBytes(ab3)));
		System.out.println(Arrays.toString(unstuffBytes(ab4)));

	}

	static byte[] unstuffBytes(byte[] bytes)
	{		
		if (bytes.length <= 1)
		{
			return bytes;
		}
		List<byte[]> byteArrays = new LinkedList<byte[]>();
		int totalLength = 0;
		int startIndex = 0;
		for (int i =0; i < bytes.length - 1; ++i)
		{
			if (((bytes[i] & 0xFF) == 0xFF && (bytes[i + 1] & 0xFF) == 0x00))
			{
				byte[] part = Arrays.copyOfRange(bytes,startIndex, i + 1);
				byteArrays.add(part);
				totalLength += part.length;
				++i;
				startIndex = i + 1;				
			}
		}
		if (startIndex < bytes.length)
		{
			byte[] part = Arrays.copyOfRange(bytes,startIndex, bytes.length);
			totalLength += part.length;
			byteArrays.add(part);			
		}
		ByteBuffer bb = ByteBuffer.allocate(totalLength);
		for (byte[] byteArray : byteArrays)
		{
			bb.put(byteArray);
		}
		return bb.array();
	}
	
	static byte[] stuffBytes(byte[] bytes)
	{		
		List<byte[]> byteArrays = new LinkedList<byte[]>();
		final byte[] STUFFED = new byte[2];
		STUFFED[0] = (byte)0xFF;
		STUFFED[1] = 0x00;
		int totalLength = 0;
		int startIndex = 0;
		for (int i =0; i < bytes.length; ++i)
		{
			if ((bytes[i] & 0xFF) == 0xFF)
			{
				byte[] part = Arrays.copyOfRange(bytes,startIndex, i);
				byteArrays.add(part);
				byteArrays.add(STUFFED);
				totalLength += part.length + STUFFED.length; 
				startIndex = i + 1;
			}
		}
		if (startIndex < bytes.length)
		{
			byte[] part = Arrays.copyOfRange(bytes,startIndex, bytes.length);
			totalLength += part.length;
			byteArrays.add(part);			
		}
		ByteBuffer bb = ByteBuffer.allocate(totalLength);
		for (byte[] byteArray : byteArrays)
		{
			bb.put(byteArray);
		}
		return bb.array();
	}

}
