package com.RAHA.doorlock;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    public static byte[] run(String message, String password) {
        try {
            SecretKey secret = generateKey(password);
            String messageC = message16(message);
            Log.d("Security", ">>>>>>>>>>>> " + password);
            Log.d("Security", ">>>>>>>>>>>>> "+messageC);

            return encryptMsg(messageC, secret);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKey generateKey(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
//        String password2 = "1234567800000000";
        String password2 = pass16(password);
        SecretKeySpec secret;
        return secret = new SecretKeySpec(password2.getBytes(), "AES");
    }

    public static byte[] encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        /* Encrypt the message. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        byte[] ans = new byte[message.length()];
        System.arraycopy(cipherText, 0, ans, 0, message.length());
        return ans;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

    public static byte[] mergeByteString(String str, byte[] data) {
        byte[] strd = str.getBytes();
        byte[] ans = new byte[strd.length + data.length];
        System.arraycopy(strd, 0, ans, 0, strd.length);
        System.arraycopy(data, 0, ans, strd.length, data.length);
        return ans;
    }

    public static byte[] AddNewline(byte[] data) {
        byte[] strd = "\r\r\r\r\r\r\r\r\r\r\r\r\r\r\r\r".getBytes();
        byte[] ans = new byte[strd.length + data.length];
        Log.d("Security", ">>>>>>>>>>>>> len data");
        Log.d("sec",    String.valueOf(data.length));
        Log.d("sec", String.valueOf(strd.length));
        System.arraycopy(data, 0, ans, 0, data.length);
        System.arraycopy(strd, 0, ans, data.length, strd.length);
        return ans;
    }

    private static String pass16(String pass) {
        if(pass.length() == 16)
            return pass;
        else if(pass.length() > 16)
            return pass.substring(0, 16);
        else
            while(pass.length()<16)
                pass = pass + "0";
            return pass;
    }
    private static String message16(String message) {
        String message2 = message + "#";
        if(message2.length() % 16 == 0)
            return message2;
        else
            while(message2.length() % 16 != 0)
                message2 = message2 + "0";
            return message2;
    }
}
