package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Utilities per la gestione di file.
 * 
 * @author marco.perazzetta
 */
public class FileUploadUtilities {

	public static final int MAX_LUNGHEZZA_NOME_FILE = 100;
	public static final String VALID_FILE_NAME_CHARS   = "A-Za-z0-9-_\\s\\.";
	public static final String INVALID_FILE_NAME_REGEX = "[^" + VALID_FILE_NAME_CHARS + "]";

	/**
	 * restituisce la regular expression da utilizzare nelle jsp
	 * di upload di un allegato, per la verifica dei caratteri validi di un file
	 */
	public static String getValidFilenameChars() {
		return VALID_FILE_NAME_CHARS.replace("\\", "\\\\");
	}
	
	/**
	 * Estrae la configurazione che indica il limite massimo in megabyte della
	 * dimensione di un file uploadabile.
	 * 
	 * @param appParamManager
	 * @param paramName
	 * @return dimensione massima in KB di un file in upload
	 */
	public static int getLimiteUploadFile(IAppParamManager appParamManager, String paramName) {
		Integer dim = (Integer) appParamManager.getConfigurationValue(paramName);
		int limiteUploadFile = (dim == null ? 0 : dim * 1024);
		return limiteUploadFile;
	}

	/**
	 * Estrae la configurazione che indica il limite massimo in megabyte della
	 * dimensione di un file uploadabile.
	 * 
	 * @param appParamManager
	 * @return dimensione massima in KB di un file in upload
	 */
	public static int getLimiteUploadFile(IAppParamManager appParamManager) {
		return getLimiteUploadFile(appParamManager, AppParamManager.LIMITE_UPLOAD_FILE);
	}

	/**
	 * Estrae la configurazione che indica il limite massimo in megabyte della
	 * dimensione di un gruppo di file uploadabili per una comunicazione.
	 * 
	 * @param appParamManager
	 * @param paramName
	 * @return dimensione massima in KB di un set di file in upload per una comunicazione
	 */
	public static int getLimiteTotaleUploadFile(IAppParamManager appParamManager, String paramName) {
		Integer dim = (Integer) appParamManager.getConfigurationValue(paramName);
		int limiteTotaleUploadDocProdotto = (dim == null ? 0 : dim * 1024);
		return limiteTotaleUploadDocProdotto;
	}
	
	/**
	 * Estrae la configurazione che indica il limite massimo in megabyte della
	 * dimensione di un gruppo di file uploadabili per una comunicazione.
	 * 
	 * @param appParamManager
	 * @return dimensione massima in KB di un set di file in upload per una comunicazione
	 */
	public static int getLimiteTotaleUploadFile(IAppParamManager appParamManager) {
		return getLimiteTotaleUploadFile(appParamManager, AppParamManager.LIMITE_TOTALE_UPLOAD_FILE);
	}

	/**
	 * Genera una stringa caratterizzata da una sequenza di cifre che identifica
	 * un timestamp pi&ugrave; un progressivo casuale di 4 cifre.
	 *
	 * @return stringa numerica di 15 cifre
	 */
	public static String generateFileName() {

		DateFormat format = new SimpleDateFormat("yyMMddHHmmss");
		String formatDate = format.format(new Date());
		int random = new Random().nextInt(10000);
		return formatDate + random;
	}

	public static int getFileSize(File document) {
		if (document != null) {
			return (int) Math.ceil(document.length() / 1024.0);
		} else {
			return 0;
		}
	}
}
