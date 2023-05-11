package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.io.InputStream;

/**
 * Interfaccia per le action di download di file generati dall'applicativo. Si
 * necessita l'implementazione di alcuni metodi per poter garantire, nel caso di
 * errori pre generazione e scrittura della response, di inviare correttamente
 * gli errori nel frame corrispondente, e nel caso di generazione con successo,
 * degli elementi per poter procedere con il download.
 *
 * @author Stefano.Sabbadin
 * @since 1.8.3
 */
public interface IDownloadAction {

	/**
	 * Nome dell'attributo da porre in sessione per memorizzare l'errore avvenuto
	 * in fase di download.
	 */
	public static final String ERROR_DOWNLOAD = "actionErrorDownload";

	/**
	 * Ritorna lo stream con il contenuto del file da far scaricare.
	 *
	 * @return the inputStream
	 */
	InputStream getInputStream();

	/**
	 * Ritorna il nome del file da far scaricare.
	 *
	 * @return the filename
	 */
	String getFilename();

	/**
	 * Indirizzo della pagina .wp da cui si e' partiti e si intende tornare in
	 * caso di download.
	 *
	 * @param urlPage the urlPage to set
	 */
	void setUrlPage(String urlPage);

	/**
	 * Frame associato alla verticalizzazione.
	 *
	 * @param currentFrame the currentFrame to set
	 */
	void setCurrentFrame(String currentFrame);

	/**
	 * Ritorna la url completa per aprire la pagina da cui parte il download e
	 * nella quale si intende tornare visualizzando il messaggio di errore.
	 *
	 * @return the urlErrori
	 */
	public String getUrlErrori();
}
