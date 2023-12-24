import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    static String applySHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                // 0xff = 255(b=10)
                String hex = Integer.toHexString(0xff & b);
                // ensure each hex value is represented by two chars, even < 16
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
