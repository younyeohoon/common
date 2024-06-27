package kr.co.cont.common.crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.cont.common.cache.BaseCache;
import lombok.extern.slf4j.Slf4j;

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 */
@Slf4j
@Component
public class AES256Util {
	
	private String iv;
	private Key keySpec;
	
	@Autowired
	private BaseCache baseCache;

	AES256Util() throws UnsupportedEncodingException {

	}
	
	/**
	 * 
	 * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
	 */
	@PostConstruct
	private void init() throws UnsupportedEncodingException {
		// 16자리의 키값을 입력하여 객체를 생성
//		String key = "asdfqwpoifas;dkQW!@#$!asdfasf(+_)(@#$"; // 비밀키입력하는곳
		String key = baseCache.propertyValue("crypto.aes256.secret.key");
		
		this.iv = key.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

		this.keySpec = keySpec;
	}

	/**
	 * AES256 으로 암호화
	 * 
	 * @param ecryptText 암호화할 문자열
	 * @return
	 */
	String encrypt(String ecryptText) {
		String encryptText = ecryptText;
		try {
			
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
			byte[] encrypted = c.doFinal(ecryptText.getBytes("UTF-8"));
			encryptText = new String(Base64.encodeBase64(encrypted));
		} catch (NoSuchAlgorithmException e) {
			log.error("[암호화 오류] {}", e);
		} catch (GeneralSecurityException e) {
			log.error("[암호화 오류] {}", e);
		} catch (UnsupportedEncodingException e) {
			log.error("[암호화 오류] {}", e);
		}
		return encryptText;
	}

	/**
	 * AES256으로 암호화된 txt를 복호화
	 * 
	 * @param encStr 복호화할 문자열
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	String decrypt(String ecryptText) {
		String decryptText = ecryptText;
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
			byte[] byteStr = Base64.decodeBase64(ecryptText.getBytes());
			decryptText = new String(c.doFinal(byteStr), "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			log.error("[암호화 오류] {}", e);
		} catch (GeneralSecurityException e) {
			log.error("[암호화 오류] {}", e);
		} catch (UnsupportedEncodingException e) {
			log.error("[암호화 오류] {}", e);
		}
		
		return decryptText;

	}
}
