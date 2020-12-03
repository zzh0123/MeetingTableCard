package com.zkml.meetingtablecard.utils.httputils.utils;

import android.util.Base64;

import com.zkml.meetingtablecard.BuildConfig;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.application.MyApplication;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类
 * 密钥存储 参考 http://www.apkbus.com/thread-307917-1-1.html
 * Created by Administrator on 2019/6/18.
 */

public class AESUtils {

    /*
      * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
      */
    private String sKey = "";//key，加密的key
    private String ivParameter = "wlw980323zkml652";//偏移量,4*4矩阵
    private static AESUtils instance = null;

    private AESUtils() {

    }

    public static AESUtils getInstance() {
        if (instance == null)
            instance = new AESUtils();
        return instance;
    }

    /**
     * 加密
     * @param encodeData 要加密的内容
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String encodeData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = encodeData.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);//数组复制
            sKey = getBK1() + getBK2()+getBK3() + getBK4();
            SecretKeySpec keySpec = new SecretKeySpec(sKey.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext);
//            return new Base64Encoder().encode(encrypted);
            String AES_encode=new String(new Base64Encoder().encode(encrypted));
            return AES_encode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getBK1(){
        return BuildConfig.appKeyPre;
    }
    private String getBK2(){
        return "34";
    }
    private String getBK3(){
        return new String(Base64.decode(MyApplication.getContext().getResources().getString(R.string.app_key_middle).getBytes(), Base64.DEFAULT));
    }
    private String getBK4(){
        return MyApplication.getContext().getResources().getString(R.string.app_key_end);
    }
}
