package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.dsappalti;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;

/**
 * Servizio gestore dell'upload dei file xml per il dataset appalti.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class DatasetAppaltiManager extends AbstractService implements
IDatasetAppaltiManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 665752823588592279L;

	private static final String NOME_DIR_DESTINAZIONE = "appaltiavcp";

	ConfigInterface configService;

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	/**
	 * @param configService
	 *            the configService to set
	 */
	public void setConfigService(ConfigInterface configService) {
		this.configService = configService;
	}

	@Override
	public EsitoOutType uploadDatasetAppalti(int anno,
			String codFiscaleStazAppaltante, byte[] datasetCompresso) {
		EsitoOutType esito = new EsitoOutType();
		esito.setEsitoOk(false);

		String root = configService
				.getParam(SystemConstants.PAR_RESOURCES_DISK_ROOT)
				+ "/"
				+ NOME_DIR_DESTINAZIONE + "/";

		if (anno < 2010 || anno > Calendar.getInstance().get(Calendar.YEAR)) {
			esito.setCodiceErrore("YEAR-NOT-VALID");
		}

		if (StringUtils.isBlank(esito.getCodiceErrore())) {
			codFiscaleStazAppaltante = StringUtils
					.stripToNull(codFiscaleStazAppaltante);
			if (codFiscaleStazAppaltante != null) {
				if (UtilityFiscali.isValidPartitaIVA(codFiscaleStazAppaltante)) {
					// se specificata la stazione appaltante, si crea una
					// sottocartella per stazione appaltante
					root += codFiscaleStazAppaltante + "/";
				} else {
					esito.setCodiceErrore("CFSA-NOT-VALID");
				}
			}
		}

		if (StringUtils.isBlank(esito.getCodiceErrore())) {
			if (datasetCompresso == null) {
				esito.setCodiceErrore("ZIP-NOT-VALID");
			}
		}

		if (StringUtils.isBlank(esito.getCodiceErrore())) {
			synchronized (this) {
				File dirDestinazione = new File(root + anno);
				File dirBackup = null;
				boolean esisteDirDestinazione = dirDestinazione.isDirectory();

				if (esisteDirDestinazione) {
					// backup del contenuto rinominando il nome della cartella
					dirBackup = new File(root + "backup" + anno);
					if (!dirDestinazione.renameTo(dirBackup)) {
						esito.setCodiceErrore("NO-BACKUP");
					}
					// ricreo la referenza alla cartella destinazione, che ora
					// non esistera' piu'
					dirDestinazione = new File(root + anno);
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
					try {
						zis = new ZipInputStream(new ByteArrayInputStream(
								datasetCompresso));
						ZipEntry ze = zis.getNextEntry();

						byte[] buffer = new byte[1024];
						while (ze != null) {
							FileOutputStream fos = new FileOutputStream(
									dirDestinazione.getAbsolutePath()
									+ File.separator + ze.getName());
							int len;
							while ((len = zis.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
							fos.flush();
							fos.close();
							ze = zis.getNextEntry();
						}
					} catch (IOException e) {
						ApsSystemUtils.logThrowable(e, this,
								"uploadDatasetAppalti");
						esito.setCodiceErrore("ZIP-READ-ERROR");
					} catch (Throwable e) {
						ApsSystemUtils.logThrowable(e, this,
								"uploadDatasetAppalti");
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
											"uploadDatasetAppalti");
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
											"uploadDatasetAppalti",
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
