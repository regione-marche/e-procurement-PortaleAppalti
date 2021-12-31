package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import it.maggioli.eldasoft.security.EncryptionConstants;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Classe di utilit&agrave; di gestione chiave di cifratura di sessione per il
 * portale.
 * 
 * @author Stefano.Sabbadin
 */
public class EncryptionUtils {

	/**
	 * Installa il provider da utilizzare.
	 */
	static {
		BouncyCastleProvider provider = new BouncyCastleProvider();
		String name = provider.getName();
		synchronized (Security.class) {
			Security.removeProvider(name); // remove old instance
			Security.addProvider(provider);
		}
	}

	/**
	 * Estrae la chiave di sessione a partire dalla stringa memorizzata in un
	 * campo della comunicazione, altrimenti ne genera una ex-novo.
	 * 
	 * @param sessionKey
	 *            chiave di sessione cifrata
	 * @param username
	 *            login utente
	 * @return chiave di sessione in chiaro
	 */
	public static byte[] decodeSessionKey(String sessionKey, String username)
			throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] chiave = null;
		if (sessionKey == null) {
			// se non esiste la comunicazione allora si genera una nuova chiave
			// di sessione AES
			KeyGenerator keyGenerator = KeyGenerator.getInstance(
					EncryptionConstants.SESSION_KEY_GEN_ALGORITHM,
					EncryptionConstants.SECURITY_PROVIDER);
			keyGenerator.init(128);
			Key aesKey = keyGenerator.generateKey();
			chiave = aesKey.getEncoded();
		} else {
			// la chiave simmetrica di sessione viene criptata con una chiave
			// simmetrica AES dipendente dalla login impresa, e codificata in
			// Base64
			byte[] chiaveSessioneCifrataStringa = Base64
					.decodeBase64(sessionKey);
			byte[] chiaveProvvisoriaDecifratura = EncryptionUtils
					.getTemporary128bitKey(username);
			IvParameterSpec iv = new IvParameterSpec(
					chiaveProvvisoriaDecifratura);
			SecretKeySpec skeySpec = new SecretKeySpec(
					chiaveProvvisoriaDecifratura,
					EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
			Cipher cipher = Cipher
					.getInstance(EncryptionConstants.SESSION_KEY_ENCRYPTION_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			chiave = cipher.doFinal(chiaveSessioneCifrataStringa);
		}
		return chiave;
	}

	/**
	 * Cifra la chiave di sessione e la converte in formato Base64 per gestirne
	 * la memorizzazione provvisoria in DB.
	 * 
	 * @param sessionKey
	 *            chiave di sessione
	 * @param username
	 *            login utente
	 * @return chiave di sessione cifrata e convertita in base64
	 */
	public static String encodeSessionKey(byte[] sessionKey, String username)
			throws GeneralSecurityException, UnsupportedEncodingException {
		String chiave = null;

		byte[] chiaveProvvisoriaDecifratura = EncryptionUtils
				.getTemporary128bitKey(username);
		IvParameterSpec iv = new IvParameterSpec(chiaveProvvisoriaDecifratura);
		SecretKeySpec skeySpec = new SecretKeySpec(
				chiaveProvvisoriaDecifratura,
				EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
		Cipher cipher = Cipher
				.getInstance(EncryptionConstants.SESSION_KEY_ENCRYPTION_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		chiave = Base64.encodeBase64String(cipher.doFinal(sessionKey));
		return chiave;
	}

	/**
	 * Ritorna la chiave di cifratura provvisoria per permettere la rilettura
	 * delle bozze prima dell'invio ufficiale.
	 * 
	 * @param username
	 *            login utente
	 * @return stringa da 128 bit da utilizzare come chiave o come vettore di
	 *         inizializzazione per l'algoritmo di cifratura
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getTemporary128bitKey(String username)
			throws UnsupportedEncodingException {
		return StringUtils.substring(StringUtils.rightPad(username, 16, '#'),
				0, 16).getBytes("UTF-8");
	}
}
