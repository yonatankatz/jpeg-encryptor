package jonateam.encryptor;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * This class encrypts a byte-array with a password, by shuffling the bytes.
 * The password is not taken "as is" while coming to encrypt, so there is not way to 
 * expose it.
 *   
 * @author yonatan katz
 */
public class ShuffleBytes {
	
	public static void main(String[] args) {
		String password = "ynantheking";
		byte[] arr = "yonatantheking".getBytes();
		System.out.println(Arrays.toString(arr));
		shuffle(arr, password);
		System.out.println(Arrays.toString(arr));
		deShuffle(arr, password);
		System.out.println(Arrays.toString(arr));
	}
	
	private static int getEncryptedKey(String password) {
		if (password.length() <= 3) {
			password = "jonabase";
		} 
		else if (password.length() < 8) {
			password = "!@#$JONa" + password.substring(1, 3); // prevent finding the password
		}
		else {
			password = password.substring(6, 7) + "!@#$JONa" + password.substring(1, 2); // prevent finding the password
		}
		return new BigInteger(password.getBytes()).intValue();
	}

	
	public static int[] getShuffleExchanges(int size, int key)
    {
        int[] exchanges = new int[size - 1];
        Random rand = new Random(key);
        for (int i = size - 1; i > 0; i--)
        {
            int n = rand.nextInt(i + 1);
            exchanges[size - 1 - i] = n;
        }
        return exchanges;
    }

    public static void shuffle(byte[] toShuffle, String password)
    {
    	int key = getEncryptedKey(password);
        int size = toShuffle.length;        
        int[] exchanges = getShuffleExchanges(size, key);
        for (int i = size - 1; i > 0; i--)
        {
            int n = exchanges[size - 1 - i];
            byte tmp = toShuffle[i];
            toShuffle[i] = toShuffle[n];
            toShuffle[n] = tmp;
        }
    }

    public static void deShuffle(byte[] shuffled, String password)
    {
		int key = getEncryptedKey(password);
        int size = shuffled.length;
        int[] exchanges = getShuffleExchanges(size, key);
        for (int i = 1; i < size; i++)
        {
            int n = exchanges[size - i - 1];
            byte tmp = shuffled[i];
            shuffled[i] = shuffled[n];
            shuffled[n] = tmp;
        }
    }
}
