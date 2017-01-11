package master.sudoku.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class AppUtil {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ASCII = Charset.forName("US-ASCII");

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode(Class<?> klass) {
        if (AppUtil.hasGingerbread()) {
            final StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            final StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            if (AppUtil.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                if (klass != null) {
                    vmPolicyBuilder.setClassInstanceLimit(klass, 1);
                }
            }

            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static final boolean hasEclair() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
    }

    public static final boolean hasCupcake() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE;
    }

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Convert a String object to UTF-8 bytes array.
     *
     * @param s The String to be converted.
     * @return UTF-8 encoded bytes array.
     */
    public static byte[] toUtf8(String s) {
        return encode(UTF_8, s);
    }

    /**
     * Build a String from UTF-8 bytes array.
     *
     * @param b The bytes array to be decoded.
     * @return The byte relative String object.
     */
    public static String fromUtf8(byte[] b) {
        return decode(UTF_8, b);
    }

    /**
     * Convert a String object to ASCII bytes array.
     *
     * @param s The String to be converted.
     * @return ASCII encoded bytes array.
     */
    public static byte[] toAscii(String s) {
        return encode(ASCII, s);
    }

    /**
     * Build a String from ASCII bytes array.
     *
     * @param b The bytes array to be decoded.
     * @return The byte relative String object.
     */
    public static String fromAscii(byte[] b) {
        return decode(ASCII, b);
    }

    private static byte[] encode(Charset charset, String s) {
        if (s == null) {
            return null;
        }
        final ByteBuffer buffer = charset.encode(CharBuffer.wrap(s));
        final byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    private static String decode(Charset charset, byte[] b) {
        if (b == null) {
            return null;
        }
        final CharBuffer buffer = charset.decode(ByteBuffer.wrap(b));
        return new String(buffer.array(), 0, buffer.length());
    }

    public static String getSmallHash(final String value) {
        final MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        sha.update(AppUtil.toUtf8(value));
        final int hash = getSmallHashFromSha1(sha.digest());
        return Integer.toString(hash);
    }

    private static int getSmallHashFromSha1(byte[] sha1) {
        final int offset = sha1[19] & 0xf;
        return ((sha1[offset] & 0x7f) << 24)
                | ((sha1[offset + 1] & 0xff) << 16)
                | ((sha1[offset + 2] & 0xff) << 8) | ((sha1[offset + 3] & 0xff));
    }

    public static String getMd5(String value) {
        final MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return value;
        }
        md5.update(AppUtil.toUtf8(value));
        final StringBuilder sb = new StringBuilder();
        for (byte b : md5.digest()) {
            if (b < 0) {
                b += 256;
            }
            if (b < 16) {
                b = 0;
            }
            sb.append(Integer.toHexString(b));
        }

        return sb.toString();
    }

    public static String encrypt(String source, String key) {
        try {
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance("DES");
            SecureRandom random = new SecureRandom();
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);
            final byte[] bytes = Base64.encode(cipher.doFinal(source.getBytes()), 0);
            return new String(bytes);
        } catch (Exception e) {
            return "";
        }
    }
}
