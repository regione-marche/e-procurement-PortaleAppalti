package test.it.maggioli.eldasoft.security;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.security.EncryptionConstants;
import it.maggioli.eldasoft.security.PGPEncryptionUtils;
import it.maggioli.eldasoft.security.PGPKeyPairGenerator;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Security;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * Test unit modulo di cifratura e decifratura dati.
 * 
 * @author Stefano.Sabbadin
 */
public class EncryptionUtilsTest extends TestCase {

	static {
		// si imposta il provider sicurezza da utilizzare
		BouncyCastleProvider provider = new BouncyCastleProvider();
		String name = provider.getName();
		synchronized (Security.class) {
			Security.removeProvider(name); // remove old instance
			Security.addProvider(provider);
		}
	}

	public static int NUMERO_UTENTI = 20;
	public static int NUMERO_CHIAVI = 50;

	/** Dati di test da utilizzare per la cifratura. */
	private static final String[] DATI_TESTUALI = {
			"",
			"1",
			"15782",
			"1233453.32",
			"50.0350",
			"-0.025549",
			"Stringa lunga a piacere",
			"Stringa con caratteri strani #ùè+ì?=)&%%£\"$ |",
			"Stringa con caratteri speciali \nflkjdskl\tdjkdfdsfine stringa",
			"Stringa molto lunga. Poiché le applicazioni Alice fanno "
					+ "uso di un unico contenitore di dati (stesso user per Oracle, "
					+ "stesso database per SQL Server ed Access), e poiché potrebbe "
					+ "rendersi necessario realizzare più applicazioni Web che vadano "
					+ "all'interno dello stesso contenitore di dati per fornire diverse "
					+ "funzionalità e logiche di business, si rende necessario slegare "
					+ "l'applicazione Web dalla base dati contenente i veri e propri "
					+ "dati di business." };

	/** Dati di test da utilizzare per la cifratura di file. */
	private static final File[] STREAM = {
			new File("./buildTasks.xml"),
			new File("./WebContent/resources/static/img/01/banner_logo.png"),
			new File("./WebContent/WEB-INF/lib/aspectjrt-1.6.2.jar"),
			new File("./WebContent/WEB-INF/lib/xwork-2.1.2.jar"),
			new File(
					"./WebContent/WEB-INF/plugins/ppgare/aps/jasper/OffertaEconomica.jasper") };


	String[] username = new String[NUMERO_UTENTI];
	Key[] chiaveSimmetrica = new Key[NUMERO_CHIAVI];
	PGPKeyPairGenerator[] chiaviPGP = new PGPKeyPairGenerator[NUMERO_CHIAVI];
	String[] passPhrase = new String[NUMERO_CHIAVI];

	public EncryptionUtilsTest(final String name) {
		super(name);
		// si generano username piu o meno lunghi (da 8 a 40 caratteri)
		for (int i = 0; i < username.length; i++) {
			username[i] = generateRandomUsername();
		}

		// si generano le chiavi simmetriche
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(
					EncryptionConstants.SESSION_KEY_GEN_ALGORITHM,
					EncryptionConstants.SECURITY_PROVIDER);
			keyGenerator.init(128);
			for (int i = 0; i < chiaveSimmetrica.length; i++) {
				chiaveSimmetrica[i] = keyGenerator.generateKey();
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			fail("fallita generazione chiavi simmetriche");
		}

		// si generano le coppie chiave pubblica/privata
		try {
			for (int i = 0; i < chiaviPGP.length; i++) {
				passPhrase[i] = "PASS" + i;
				chiaviPGP[i] = new PGPKeyPairGenerator(UUID.randomUUID()
						.toString(), passPhrase[i]);
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			fail("fallita generazione chiavi asimmetriche");
		} catch (PGPException e) {
			e.printStackTrace();
			fail("fallita generazione chiavi asimmetriche");
		}
	}

	public void testMassivoCifraturaChiaveSimmetricaConChiavePubblicaPrivata() {
		System.out
				.println("Avvio test di cifratura decifratura a chiave pubblica/privata per con "
						+ chiaviPGP.length
						+ " coppie su "
						+ DATI_TESTUALI.length + " chiavi di sessione");
		try {
			for (int i = 0; i < chiaviPGP.length; i++) {
				System.out.print((i + 1) + "/" + chiaviPGP.length + " => ");
				PGPKeyPairGenerator chiavePGP = chiaviPGP[i];
				String pin = passPhrase[i];
				ByteArrayOutputStream streamPublicKey = new ByteArrayOutputStream();
				chiavePGP.getAsciiArmoredPublicKey(streamPublicKey);
				ByteArrayOutputStream streamPrivateKey = new ByteArrayOutputStream();
				chiavePGP.getAsciiArmoredPrivateKey(streamPrivateKey);

				for (int j = 0; j < DATI_TESTUALI.length; j++) {
					String test = DATI_TESTUALI[j];
					byte[] msgCifrato = PGPEncryptionUtils.encrypt(
							new ByteArrayInputStream(streamPublicKey
									.toByteArray()), test.getBytes());
					assertFalse(StringUtils
							.equals(new String(msgCifrato), test));
					byte[] msgDecifrato = PGPEncryptionUtils.decrypt(
							new ByteArrayInputStream(streamPrivateKey
									.toByteArray()), pin, msgCifrato);
					assertEquals(test, new String(msgDecifrato));
					System.out.print(".");
				}
				System.out.println("OK!");			
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("fallito il test di cifratura/decifratura con chiave pubblica/privata - "
					+ e.getMessage());
		}
	}

	public void testMassivoCifraturaDecifraturaChiaveSimmetrica() {
		System.out
				.println("Avvio test di cifratura decifratura della chiave simmetrica per "
						+ username.length
						+ " utenti con "
						+ chiaveSimmetrica.length + " chiavi");
		try {
			for (int i = 0; i < username.length; i++) {
				String login = username[i];
				System.out.print((i + 1) + "/" + username.length + " => ");
				for (Key chiave : chiaveSimmetrica) {
					String encodedSessionKey = EncryptionUtils.encodeSessionKey(
							chiave.getEncoded(), login);
					assertFalse(StringUtils.equals(
							new String(encodedSessionKey), login));
					byte[] decodedSessionKey = EncryptionUtils.decodeSessionKey(
							encodedSessionKey, login);
					assertTrue(ByteUtils.equals(chiave.getEncoded(),
							decodedSessionKey));
					System.out.print(".");
				}
				System.out.println("OK!");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("fallito encoding dello username - " + e.getMessage());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			fail("fallito il test di cifratura/decifratura encoding dello username - "
					+ e.getMessage());
		}
	}

	public void testMassivoCifraturaDatiTestualiConChiaveSimmetrica() {
		System.out
				.println("Avvio test di cifratura decifratura a chiave simmetrica per "
						+ username.length
						+ " utenti con "
						+ chiaveSimmetrica.length
						+ " chiavi su "
						+ DATI_TESTUALI.length + " casi di testo");
		try {
			for (int i = 0; i < username.length; i++) {
				String login = username[i];
				System.out.print((i + 1) + "/" + username.length + " => ");
				Cipher encoder = null;
				Cipher decoder = null;
				for (int j = 0; j < chiaveSimmetrica.length; j++) {
					Key chiave = chiaveSimmetrica[j];
					try {
						if (j == 0) {
							encoder = SymmetricEncryptionUtils.getEncoder(chiave.getEncoded(), login);
						} else {
							reinitEncoder(encoder,chiave.getEncoded(), login);
						}
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
						fail("fallita istanziazione encoder - "
								+ e.getMessage());
					}
					try {
						if (j == 0) {
							decoder = SymmetricEncryptionUtils.getDecoder(chiave.getEncoded(), login);
						} else {
							reinitDecoder(decoder,chiave.getEncoded(), login);
						}
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
						fail("fallita istanziazione decoder - "
								+ e.getMessage());
					}
					for (String test : DATI_TESTUALI) {
						byte[] msgCifrato = SymmetricEncryptionUtils.translate(encoder,
								test.getBytes());
						assertFalse(StringUtils.equals(new String(msgCifrato),
								test));
						byte[] msgDecifrato = SymmetricEncryptionUtils.translate(decoder,
								msgCifrato);
						assertEquals(test, new String(msgDecifrato));
					}
					System.out.print(".");
				}
				System.out.println("OK!");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("fallito encoding dello username - " + e.getMessage());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			fail("fallito il test di cifratura/decifratura encoding dello username - "
					+ e.getMessage());
		}
	}

	public void testMassivoCifraturaStreamConChiaveSimmetrica() {
		System.out
				.println("Avvio test di cifratura decifratura a chiave simmetrica per "
						+ username.length
						+ " utenti con "
						+ chiaveSimmetrica.length
						+ " chiavi su "
						+ STREAM.length + " stream di prova");
		try {
			for (int i = 0; i < username.length; i++) {
				String login = username[i];
				System.out.print((i + 1) + "/" + username.length + " => ");
				Cipher encoder = null;
				Cipher decoder = null;
				for (int j = 0; j < chiaveSimmetrica.length; j++) {
					Key chiave = chiaveSimmetrica[j];
					try {
						if (j == 0) {
							encoder = SymmetricEncryptionUtils.getEncoder(chiave.getEncoded(), login);
						} else {
							reinitEncoder(encoder,chiave.getEncoded(), login);
						}
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
						fail("fallita istanziazione encoder - "
								+ e.getMessage());
					}
					try {
						if (j == 0) {
							decoder = SymmetricEncryptionUtils.getDecoder(chiave.getEncoded(), login);
						} else {
							reinitDecoder(decoder,chiave.getEncoded(), login);
						}
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
						fail("fallita istanziazione decoder - "
								+ e.getMessage());
					}
					for (File input : STREAM) {
						File encodedFile = new File("file.enc");
						File decodedFile = new File("file.dec");
						FileInputStream fis = new FileInputStream(input);
						FileOutputStream fos = new FileOutputStream(encodedFile);
						SymmetricEncryptionUtils.translate(encoder, fis, fos);
						assertFalse(FileUtils.contentEquals(input, encodedFile));
						FileInputStream fis2 = new FileInputStream(encodedFile);
						FileOutputStream fos2 = new FileOutputStream(
								decodedFile);
						SymmetricEncryptionUtils.translate(decoder, fis2, fos2);
						assertTrue(FileUtils.contentEquals(input, decodedFile));
						encodedFile.delete();
						decodedFile.delete();
					}
					System.out.print(".");
				}
				System.out.println("OK!");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail("fallito encoding dello username - " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail("fallito il test di cifratura/decifratura encoding dello username - "
					+ e.getMessage());
		}
	}

	/**
	 * Genera uno username secondo le convenzioni di memorizzazione nel db
	 * 
	 * @return username lungo da 8 a 40 caratteri
	 */
	private String generateRandomUsername() {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789."
				.toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		int dimensione = random.nextInt(41);
		if (dimensione < 8) {
			dimensione = 8;
		}
		for (int i = 0; i < dimensione; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Varia la configurazione di un encoder in modo da impostarlo per una nuova
	 * chiave di cifratura.
	 * 
	 * @param cipher
	 *            encoder da riconfigurare
	 * @param chiaveSimmetrica
	 *            chiave di sessione simmetrica
	 * @param username
	 *            login utente, utilizzato per inizializzare la cifratura
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static void reinitEncoder(Cipher cipher, byte[] chiaveSimmetrica,
			String username) throws UnsupportedEncodingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		SecretKeySpec skeySpec = new SecretKeySpec(chiaveSimmetrica,
				EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
		IvParameterSpec iv = new IvParameterSpec(
				SymmetricEncryptionUtils.fill128bitKey(username));
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	}
	
	/**
	 * Varia la configurazione di un decoder in modo da impostarlo per una nuova
	 * chiave di decifratura.
	 * 
	 * @param cipher
	 *            decoder da riconfigurare
	 * @param chiaveSimmetrica
	 *            chiave di sessione simmetrica
	 * @param username
	 *            login utente, utilizzato per inizializzare la decifratura
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	public static void reinitDecoder(Cipher cipher, byte[] chiaveSimmetrica,
			String username) throws UnsupportedEncodingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		SecretKeySpec skeySpec = new SecretKeySpec(chiaveSimmetrica,
				EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
		IvParameterSpec iv = new IvParameterSpec(
				SymmetricEncryptionUtils.fill128bitKey(username));
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	}

}
