package kr.co.cont.common.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SHA256Util {

	/**
	 * 
	 * SHA256 으로 암호화
	 * 
	 * @param plainText 암호화할 문자열
	 * @param salt 
	 * @return
	 */
	String encrypt(String plainText , String salt) {
		
		String ciperText = "";
		
		try {
			// 1. SHA256 알고리즘 객체 생성
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			// 2. pwd + salt 합친 문자열에 SHA 265 적용
			if (log.isDebugEnabled()) log.debug("[암호화 전] pwd = {}, salt = {}" , plainText, salt);
			String input = plainText + salt;
			md.update(input.getBytes());
			byte[] enc = md.digest();
			
			// 3. byte To String (10진수의 문자열로 변경)
			StringBuilder sb = new StringBuilder();
			for (byte b : enc) {
				sb.append(String.format("%02x", b));
			}
			
			ciperText = sb.toString();
			if (log.isDebugEnabled()) log.debug("[암호화 후] result = {}" , ciperText);
		} catch (NoSuchAlgorithmException e) {
			log.error("암복호화 오류 :  {} , {}", e.getClass().getName(), e);
		}
		
		return ciperText;
	}
}
