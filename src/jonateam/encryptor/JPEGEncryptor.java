package jonateam.encryptor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class encrypts a valid jpeg image, while the output encrypted file is still a valid jpeg format. 
 * 
 * @author yonatan katz
 */
public class JPEGEncryptor {	
	
	/**
	 * test.<br>
	 * args[0] - password
	 * args[1] - input file
	 * args[2] - output file
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 3)
		{
			System.out.println("Expected program arguments:\n password input-image output-image");
			System.exit(1);
		}
		System.out.println("Start...");
		JPEGEncryptor jpegReader = new JPEGEncryptor(args[0]);
		jpegReader.encryptFile(args[1],  args[2]);		
		jpegReader.decryptFile(args[2],  args[1]);		
		System.out.println("end.");
	}
	
	private final String password;
	
	public JPEGEncryptor(String password)
	{
		this.password = password;
	}
	
	/**
	 * The function takes a valid jpeg and encrypts it so that the output file will be a valid jpeg image and
	 * the file size will remain the same.
	 * 
	 * The encryption is made by the password supplied in the constructor
	 * 
	 * @param inputImage full path to the input image
	 * @param outputImage full path to the output encrypted image
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void encryptFile(String inputImage, String outputImage) throws FileNotFoundException, IOException {
		processFile(inputImage, outputImage, true);
	}

	/**
	 * The function takes an encrypted imaged and decrypts with the password provided in the constructor.
	 * 
	 * @param inputImage full path to the input image
	 * @param outputImage full path to the output encrypted image
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void decryptFile(String inputImage, String outputImage) throws FileNotFoundException, IOException {
		processFile(inputImage, outputImage, false);
	}

	private void processFile(String inputImage, String outputImage, boolean encrypt) throws FileNotFoundException, IOException {
		byte[] image = Files.readAllBytes(Paths.get(inputImage));
		OutputStream output = new FileOutputStream(outputImage);
		int sectionStartIndex = 0;
		int sosStartIndex = getNextSosIndex(image, 0);
		while (sosStartIndex > -1)
		{
			int sosHeaderLength = getSosHeaderLength(image, sosStartIndex);
			output.write(image, sectionStartIndex, sosStartIndex - sectionStartIndex + sosHeaderLength);
			int endOfSosIndex = getEndOfSosIndex(image, sosStartIndex + sosHeaderLength);
			encryptImage(output, image, sosStartIndex + sosHeaderLength, endOfSosIndex, encrypt);
			sectionStartIndex = endOfSosIndex + 1; 
			sosStartIndex = getNextSosIndex(image, sectionStartIndex);
		}
		output.write(image, sectionStartIndex, image.length - sectionStartIndex);
		output.close();
	}

	private void encryptImage(OutputStream output, byte[] image, int startSosEntrothyData, int endOfSosIndex, boolean encrypt) throws IOException {
		byte[] sosData = Arrays.copyOfRange(image, startSosEntrothyData, endOfSosIndex + 1);		
		if (encrypt)
		{
			sosData = BytesStuffer.unstuffBytes(sosData);
			ShuffleBytes.shuffle(sosData, password);			
			sosData = BytesStuffer.stuffBytes(sosData);
		}
		else
		{
			sosData = BytesStuffer.unstuffBytes(sosData);
			ShuffleBytes.deShuffle(sosData, password);			
			sosData = BytesStuffer.stuffBytes(sosData);
		}
		output.write(sosData);
	}

	private int getEndOfSosIndex(byte[] image, int startIndex) {
		for (int i = startIndex; i < image.length - 1; ++i)
		{
			if ((image[i] & 0xFF) == 0xFF && (image[i + 1] & 0xFF) != 0x00)
			{
				return i - 1;
			}
		}
		throw new IllegalStateException("SOS part was not closed with 0xFF");
	}

	private int getSosHeaderLength(byte[] image, int sosStartIndex) {
		sosStartIndex = sosStartIndex + 2; // skip the SOS marker header
		int length = (image[sosStartIndex] << 8) | image[sosStartIndex + 1];
		return length;
	}

	private int getNextSosIndex(byte[] image, int startIndex) {
		for (int i = startIndex; i < image.length - 1; ++i)
		{
			if ((image[i] & 0xFF) == 0xFF && (image[i + 1] & 0xFF) == 0xDA)
			{
				return i;
			}
		}
		return -1;
	}

}

