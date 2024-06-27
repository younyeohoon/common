package kr.co.cont.common.crypto;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Component;

import kr.co.cont.common.biz.base.model.BaseMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RSAUtil {

	private KeyPairGenerator generator;
	private KeyFactory keyFactory;
	private KeyPair keypair;

	// 1024비트 RSA 키쌍을 생성
	public RSAUtil() {
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (Exception e) {
			log.error("RsaUtil Create Failed", e);
		}
	}

	public BaseMap createRSA() {
		BaseMap rsa = new BaseMap();
		try {
			keypair = generator.generateKeyPair();
			PublicKey publicKey = keypair.getPublic();
			PrivateKey privateKey = keypair.getPrivate();

			RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			String modulus = publicSpec.getModulus().toString(16);
			String exponent = publicSpec.getPublicExponent().toString(16);
			rsa.put("privateKey", privateKey);
			rsa.put("modulus", modulus);
			rsa.put("exponent", exponent);

		} catch (Exception e) {
			log.error("RsaUtil.createRSA()", e);
		}

		return rsa;
	}

	// Key로 RSA 복호화를 수행
	@SuppressWarnings("static-access")
	public String decrypt(PrivateKey privateKey, String ecryptText) {
		String decryptText = ecryptText;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedBytes = cipher.doFinal(hexToByteArray(ecryptText));
			
			decryptText = new String(decryptedBytes, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			log.error("[복호화 오류] :", e);
		} catch (NoSuchPaddingException e) {
			log.error("[복호화 오류] :", e);
		} catch (InvalidKeyException e) {
			log.error("[복호화 오류] :", e);
		} catch (IllegalBlockSizeException e) {
			log.error("[복호화 오류] :", e);
		} catch (BadPaddingException e) {
			log.error("[복호화 오류] :", e);
		} catch (UnsupportedEncodingException e) {
			log.error("[복호화 오류] :", e);
		}
		
		return decryptText;
	}

	// Key로 RSA 암호화를 수행
	public String encrypt(PrivateKey publicKey, String ecryptText) {
		String encryptText = ecryptText;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedBytes = cipher.doFinal(ecryptText.getBytes());
			
			encryptText = new String(encryptedBytes, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			log.error("[암호화 오류] :", e);
		} catch (NoSuchPaddingException e) {
			log.error("[암호화 오류] :", e);
		} catch (InvalidKeyException e) {
			log.error("[암호화 오류] :", e);
		} catch (IllegalBlockSizeException e) {
			log.error("[암호화 오류] :", e);
		} catch (BadPaddingException e) {
			log.error("[암호화 오류] :", e);
		} catch (UnsupportedEncodingException e) {
			log.error("[암호화 오류] :", e);
		}
		return encryptText;
	}

	private byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}

		return bytes;
	}
}
