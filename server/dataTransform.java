import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Created by lifeng on 10/26/16.
 * encryption, check sum generation and buffer copy
 * some contents come from here: http://siberean.livejournal.com/14788.html
 */
public class dataTransform {
    private static final int IV_LENGTH=16;

    // encrypt data from InputStream to OutputStream
    public static int encrypt(InputStream in, OutputStream out, String password) throws Exception{

        SecureRandom r = new SecureRandom();
        byte[] iv = new byte[IV_LENGTH];
        r.nextBytes(iv);
        out.write(iv); //write IV as a prefix
        out.flush();
        //System.out.println(">>>>>>>>written"+Arrays.toString(iv));

        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding"); //"DES/ECB/PKCS5Padding";"AES/CBC/PKCS5Padding"
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        out = new CipherOutputStream(out, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        int totalRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
            totalRead += numRead;
        }
        out.close();
        return  totalRead;
    }


    // decrypt data from InputStream to OutputStream
    public static int decrypt(InputStream in, OutputStream out, String password) throws Exception{

        byte[] iv = new byte[IV_LENGTH];
        in.read(iv);
        //System.out.println(">>>>>>>>red"+Arrays.toString(iv));

        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding"); //"DES/ECB/PKCS5Padding";"AES/CBC/PKCS5Padding"
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        in = new CipherInputStream(in, cipher);
        byte[] buf = new byte[1024];
        int numRead = 0;
        int totalRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
            totalRead += numRead;
        }
        out.close();
        return totalRead;
    }


    // generate the checksum string from the file
    public static String createChecksum(String filename) throws
            Exception
    {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        byte[] bytes =  complete.digest();
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
