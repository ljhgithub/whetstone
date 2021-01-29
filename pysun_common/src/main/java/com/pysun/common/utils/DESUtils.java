package com.pysun.common.utils;

import android.util.Base64;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtils {

    public static String encryptBasedDes(String data, String encryptKey) {
        String encryptedData = null;
        try {
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(encryptKey.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(1, key, sr);
            encryptedData = Base64.encodeToString(cipher.doFinal(data.getBytes()), 0);
            encryptedData = URLEncoder.encode(encryptedData, "UTF-8");
            return encryptedData;
        } catch (Exception var8) {
            throw new RuntimeException("加密错误，错误信息：", var8);
        }
    }

    public static byte[] decrypt(byte[] src, String password) {
        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = null;

        try {
            desKey = new DESKeySpec(password.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(2, securekey, random);
            return cipher.doFinal(src);
        } catch (InvalidKeyException var7) {
            var7.printStackTrace();
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
        } catch (NoSuchPaddingException var9) {
            var9.printStackTrace();
        } catch (BadPaddingException var10) {
            var10.printStackTrace();
        } catch (InvalidKeySpecException var11) {
            var11.printStackTrace();
        } catch (IllegalBlockSizeException var12) {
            var12.printStackTrace();
        }

        return null;
    }
}
