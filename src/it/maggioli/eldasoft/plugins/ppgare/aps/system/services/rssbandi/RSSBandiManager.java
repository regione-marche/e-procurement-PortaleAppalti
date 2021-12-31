package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.rssbandi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;

/**
 * Servizio gestore dell'upload dei feed rss per bandi esiti ed avvisi nel
 * rispetto del D.P.C.M. del 26/04/2011.
 * 
 * @version 1.11.4
 * @author Eleonora.Favaro
 */

public class RSSBandiManager extends AbstractService implements
IRSSBandiManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 4450359293992829964L;
	private static final String NOME_DIR_DESTINAZIONE = "rss";

	ConfigInterface configService;



	/**
	 * @param configService
	 *            the configService to set
	 */
	public void setConfigService(ConfigInterface configService) {
		this.configService = configService;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public EsitoOutType uploadRssBandi(byte[] datasetCompresso) {

		EsitoOutType esito = new EsitoOutType();
		esito.setEsitoOk(false);

		String root = configService.getParam(SystemConstants.PAR_RESOURCES_DISK_ROOT)
				+ File.separator
				+ NOME_DIR_DESTINAZIONE ;

		if (StringUtils.isBlank(esito.getCodiceErrore())) {
			if (datasetCompresso == null) {
				esito.setCodiceErrore("ZIP-NOT-VALID");
			}
		}

		if (StringUtils.isBlank(esito.getCodiceErrore())) {
			synchronized (this) {
				File dirDestinazione = new File(root);
				File dirBackup = null;
				boolean esisteDirDestinazione = dirDestinazione.isDirectory();

				if (esisteDirDestinazione) {
					// backup del contenuto rinominando il nome della cartella
					dirBackup = new File(root + "backup"+File.separator);
					if (!dirDestinazione.renameTo(dirBackup)) {
						esito.setCodiceErrore("NO-BACKUP");
					}
					// ricreo la referenza alla cartella destinazione, che ora
					// non esistera' piu'
					dirDestinazione = new File(root);
				}

				if (StringUtils.isBlank(esito.getCodiceErrore())) {
					// si crea la cartella
					if (!dirDestinazione.mkdirs()) {
						esito.setCodiceErrore("NO-MKDIR");
					}
				}

				if (StringUtils.isBlank(esito.getCodiceErrore())) {
					// si decomprimono i file xml nella cartella di destinazione
					ZipInputStream zis = null;
					File rootStazioneAppaltante = null;
					FileOutputStream fos = null;
					int len;

					try {
						zis = new ZipInputStream(new ByteArrayInputStream(datasetCompresso));
						ZipEntry ze = zis.getNextEntry();
						//Parto dalla prima folder e/o dal primo file
						ze = zis.getNextEntry();
						byte[] buffer = new byte[1024];
						while (ze != null) {
							if(ze.isDirectory()){
								rootStazioneAppaltante = new File(dirDestinazione.getAbsolutePath()	+ File.separator + ze.getName());
								rootStazioneAppaltante.mkdir();
							}else{

								fos = new FileOutputStream(dirDestinazione.getAbsolutePath()+ File.separator + ze.getName());
								while ((len = zis.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}
								fos.flush();
								fos.close();
							}
							ze = zis.getNextEntry();
						}
					} catch (IOException e) {
						ApsSystemUtils.logThrowable(e, this,
								"uploadRSSBandi");
						esito.setCodiceErrore("ZIP-READ-ERROR");
					} catch (Throwable e) {
						ApsSystemUtils.logThrowable(e, this,
								"uploadRSSBandi");
						esito.setCodiceErrore("ZIP-READ-ERROR");
					} finally {
						if (zis != null) {
							try {
								zis.closeEntry();
							} catch (IOException e) {
							}
							try {
								zis.close();
							} catch (IOException e) {
							}
						}

						if (StringUtils.isBlank(esito.getCodiceErrore())) {
							if (esisteDirDestinazione) {
								// si rimuove la cartella di backup perche'
								// l'operazione
								// ha concluso senza errori
								try {
									FileUtils.cleanDirectory(dirBackup);
									dirBackup.delete();
									esito.setEsitoOk(true);
								} catch (IOException e) {
									ApsSystemUtils.logThrowable(e, this,
											"uploadRSSBandi");
									esito.setCodiceErrore("WARN-REMOVE-BACKUP");
								}
							} else {
								esito.setEsitoOk(true);
							}
						} else {
							// se si sono verificati errori in creazione
							// cartella e
							// copia dei file, si ripristina la situazione
							// iniziale
							String codErrore = esito.getCodiceErrore();
							// guardando i codici di errore si procede a ritroso
							// in modo
							// da ripristinare la situazione iniziale
							if ("ZIP-READ-ERROR".equals(codErrore)) {
								try {
									FileUtils.cleanDirectory(dirDestinazione);
									dirDestinazione.delete();
								} catch (IOException e) {
									ApsSystemUtils
									.logThrowable(
											e,
											this,
											"uploadRSSBandi",
											"Errore durante l'eliminazione dei file creati in "
													+ dirDestinazione
													.getPath());
								}
							}
							if (dirBackup != null) {
								// ripristino la cartella originaria
								dirBackup.renameTo(dirDestinazione);
							}
						}
					}
				}
			}
		}
		return esito;
	}

}
