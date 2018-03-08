package com.gome.meidian.account.shiro;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

/**
 * 密码加密，基于shiro API
 * 
 * @author yaoliang
 * @time 2016-05-23
 */
public class EncryptUtils {
	
	public static void main(String[] args) {
		String source = "123.gome";
		String ba64 = Base64.encodeBase64String(source.getBytes());
		String de64 = new String(Base64.decodeBase64(ba64));
		System.out.println("de64:"+de64);
		String salt = randomSalt();
		String enryPwd = encode(ba64,salt);
		System.out.println("source: "+source+" \nbase64加密: "+ba64+" \nsalt: "+salt+" \n加盐后的密码enryPwd: "+enryPwd);
	}

	/**
	 * 基于传入的盐值加密密码
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	public static String encode(String password, String salt) {
		// Now hash the plain-text password with the random salt and multiple
		// iterations and then Base64-encode the value (requires less space than
		// Hex):
		String hashedPassword = new Sha256Hash(password, salt, 1024).toHex();

		return hashedPassword;
	}
	
	/**
	 * 产生随机盐值
	 * @return
	 */
	public static String randomSalt() {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		ByteSource salt = rng.nextBytes();
		return salt.toBase64();
	}
	
	/**
    * MD5 32位 source加密
    * @param plainText 明文 
    * @return 32位密文
    */
   public static String MD5(String plainText) {
       String re_md5 = new String();
       try {
           MessageDigest md = MessageDigest.getInstance("MD5");
           md.update(plainText.getBytes());
           byte b[] = md.digest();

           int i;

           StringBuffer buf = new StringBuffer("");
           for (int offset = 0; offset < b.length; offset++) {
               i = b[offset];
               if (i < 0)
                   i += 256;
               if (i < 16)
                   buf.append("0");
               buf.append(Integer.toHexString(i));
           }

           re_md5 = buf.toString();

       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       }
       return re_md5;
   }

}
