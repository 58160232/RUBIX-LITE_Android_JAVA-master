package rubix.mobile.rubix_mobile;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by niwat on 15/1/2561.
 */

public class StringEncryption extends AppCompatActivity {
    public static String result;
    public StringEncryption(String st) throws UnsupportedEncodingException, NoSuchAlgorithmException {
            SHA1(st);
    }
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    public static void SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        result = Base64.encodeToString(sha1hash, Base64.DEFAULT);
        result = result.substring(0, result.length()-1);
       // return convertToHex(sha1hash);
    }
    public String getString(){
        return result;
    }
}
