package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers;

import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati per i documenti di un prodotto
 *
 * @author Marco.Perazzetta
 */
public class WizardDocumentiProdottoHelper implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5154001008149080656L;

	public static final int MAX_LUNGHEZZA_NOME_FILE = 100;

	// informazioni per la gestione delle certificazioni richieste
	private List<File> certificazioniRichieste = null;
	private List<String> certificazioniRichiesteContentType = null;
	private List<String> certificazioniRichiesteFileName = null;
	private List<Integer> certificazioniRichiesteSize = null;

	// informazioni per la gestione delle schede tecniche
	private List<File> schedeTecniche = null;
	private List<String> schedeTecnicheContentType = null;
	private List<String> schedeTecnicheFileName = null;
	private List<Integer> schedeTecnicheSize = null;

	// informazioni per la gestione dell'immagine prodotto
	private File immagine = null;
	private String immagineContentType = null;
	private String immagineFileName = null;
	private Integer immagineSize = null;

	public List<File> getCertificazioniRichieste() {
		return certificazioniRichieste;
	}

	public void setCertificazioniRichieste(List<File> certificazioniRichieste) {
		this.certificazioniRichieste = certificazioniRichieste;
	}

	public List<String> getCertificazioniRichiesteContentType() {
		return certificazioniRichiesteContentType;
	}

	public void setCertificazioniRichiesteContentType(List<String> certificazioniRichiesteContentType) {
		this.certificazioniRichiesteContentType = certificazioniRichiesteContentType;
	}

	public List<String> getCertificazioniRichiesteFileName() {
		return certificazioniRichiesteFileName;
	}

	public void setCertificazioniRichiesteFileName(List<String> certificazioniRichiesteFileName) {
		this.certificazioniRichiesteFileName = certificazioniRichiesteFileName;
	}

	public List<Integer> getCertificazioniRichiesteSize() {
		return certificazioniRichiesteSize;
	}

	public void setCertificazioniRichiesteSize(List<Integer> certificazioniRichiesteSize) {
		this.certificazioniRichiesteSize = certificazioniRichiesteSize;
	}

	public List<File> getSchedeTecniche() {
		return schedeTecniche;
	}

	public void setSchedeTecniche(List<File> schedeTecniche) {
		this.schedeTecniche = schedeTecniche;
	}

	public List<String> getSchedeTecnicheContentType() {
		return schedeTecnicheContentType;
	}

	public void setSchedeTecnicheContentType(List<String> schedeTecnicheContentType) {
		this.schedeTecnicheContentType = schedeTecnicheContentType;
	}

	public List<String> getSchedeTecnicheFileName() {
		return schedeTecnicheFileName;
	}

	public void setSchedeTecnicheFileName(List<String> schedeTecnicheFileName) {
		this.schedeTecnicheFileName = schedeTecnicheFileName;
	}

	public List<Integer> getSchedeTecnicheSize() {
		return schedeTecnicheSize;
	}

	public void setSchedeTecnicheSize(List<Integer> schedeTecnicheSize) {
		this.schedeTecnicheSize = schedeTecnicheSize;
	}

	public File getImmagine() {
		return immagine;
	}

	public void setImmagine(File immagine) {
		this.immagine = immagine;
	}

	public String getImmagineContentType() {
		return immagineContentType;
	}

	public void setImmagineContentType(String immagineContentType) {
		this.immagineContentType = immagineContentType;
	}

	public String getImmagineFileName() {
		return immagineFileName;
	}

	public void setImmagineFileName(String immagineFileName) {
		this.immagineFileName = immagineFileName;
	}

	public Integer getImmagineSize() {
		return immagineSize;
	}

	public void setImmagineSize(Integer immagineSize) {
		this.immagineSize = immagineSize;
	}

	/**
	 * costruttore 
	 */
	public WizardDocumentiProdottoHelper() {
		certificazioniRichieste = new ArrayList<File>();
		certificazioniRichiesteContentType = new ArrayList<String>();
		certificazioniRichiesteFileName = new ArrayList<String>();
		certificazioniRichiesteSize = new ArrayList<Integer>();
		schedeTecniche = new ArrayList<File>();
		schedeTecnicheContentType = new ArrayList<String>();
		schedeTecnicheFileName = new ArrayList<String>();
		schedeTecnicheSize = new ArrayList<Integer>();
	}

	/**
	 * ... 
	 */
	public static void addDocumenti(
			WizardDocumentiProdottoHelper documenti, 
			it.eldasoft.sil.portgare.datatypes.ProdottoType prodotto) throws IOException 
	{
		if (documenti != null) {
			if (documenti.getImmagine() != null) {
				DocumentoType immagine = prodotto.addNewImmagine();
				immagine.setContentType(documenti.getImmagineContentType());
				immagine.setFile(FileUtils.readFileToByteArray(documenti.getImmagine()));
				immagine.setNomeFile(documenti.getImmagineFileName());
			}
			if (!documenti.getCertificazioniRichieste().isEmpty()) {
				ListaDocumentiType certificazioniRichieste = prodotto.addNewCertificazioniRichieste();
				for (int i = 0; i < documenti.getCertificazioniRichieste().size(); i++) {
					DocumentoType certificazioneRichiesta = certificazioniRichieste.addNewDocumento();
					certificazioneRichiesta.setContentType(documenti.getCertificazioniRichiesteContentType().get(i));
					certificazioneRichiesta.setNomeFile(documenti.getCertificazioniRichiesteFileName().get(i));
					certificazioneRichiesta.setFile(FileUtils.readFileToByteArray(documenti.getCertificazioniRichieste().get(i)));
				}
			}
			if (!documenti.getSchedeTecniche().isEmpty()) {
				ListaDocumentiType schedeTecniche = prodotto.addNewSchedeTecniche();
				for (int i = 0; i < documenti.getSchedeTecniche().size(); i++) {
					DocumentoType schedeTecnica = schedeTecniche.addNewDocumento();
					schedeTecnica.setContentType(documenti.getSchedeTecnicheContentType().get(i));
					schedeTecnica.setNomeFile(documenti.getSchedeTecnicheFileName().get(i));
					schedeTecnica.setFile(FileUtils.readFileToByteArray(documenti.getSchedeTecniche().get(i)));
				}
			}
		}
	}

	/**
	 * ... 
	 */
	@Override
	protected WizardDocumentiProdottoHelper clone() throws CloneNotSupportedException {
		WizardDocumentiProdottoHelper clone = new WizardDocumentiProdottoHelper();
		List<File> listaCertificazioni = new ArrayList<File>();
		List<Integer> listaCertificazioniSize = new ArrayList<Integer>();
		List<String> listaCertificazioniContentType = new ArrayList<String>();
		List<String> listaCertificazioniFileName = new ArrayList<String>();
		
		if (this.getCertificazioniRichieste() != null && !this.getCertificazioniRichieste().isEmpty()) {
			for (int i = 0; i < this.getCertificazioniRichieste().size(); i++) {
				File file = this.getCertificazioniRichieste().get(i);
				File f = new File(file.getParent() + File.separatorChar + FileUploadUtilities.generateFileName());
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(f);
					fos.write(FileUtils.readFileToByteArray(file));
				} catch (FileNotFoundException ex) {
					ApsSystemUtils.logThrowable(ex, this, "clone");
				} catch (IOException ex) {
					ApsSystemUtils.logThrowable(ex, this, "clone");
				} catch (Throwable ex) {
					ApsSystemUtils.logThrowable(ex, this, "clone");
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException ex) {
						}
					}
				}
				listaCertificazioni.add(f);
				listaCertificazioniContentType.add(this.getCertificazioniRichiesteContentType().get(i));
				listaCertificazioniSize.add(this.getCertificazioniRichiesteSize().get(i));
				listaCertificazioniFileName.add(this.getCertificazioniRichiesteFileName().get(i));
			}
			clone.setCertificazioniRichieste(listaCertificazioni);
			clone.setCertificazioniRichiesteContentType(listaCertificazioniContentType);
			clone.setCertificazioniRichiesteFileName(listaCertificazioniFileName);
			clone.setCertificazioniRichiesteSize(listaCertificazioniSize);
		}
		
		if (this.getSchedeTecniche() != null && !this.getSchedeTecniche().isEmpty()) {
			List<File> listaSchedeTecniche = new ArrayList<File>();
			List<Integer> listaSchedeTecnicheSize = new ArrayList<Integer>();
			List<String> listaSchedeTecnicheContentType = new ArrayList<String>();
			List<String> listaSchedeTecnicheFileName = new ArrayList<String>();
			for (int i = 0; i < this.getSchedeTecniche().size(); i++) {
				File file = this.getSchedeTecniche().get(i);
				File f = new File(file.getParent() + File.separatorChar + FileUploadUtilities.generateFileName());
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(f);
					fos.write(FileUtils.readFileToByteArray(file));
				} catch (FileNotFoundException ex) {
					ApsSystemUtils.logThrowable(ex, this, "clone");
				} catch (IOException ex) {
					ApsSystemUtils.logThrowable(ex, this, "clone");
				} catch (Throwable ex) {
					ApsSystemUtils.logThrowable(ex, this, "clone");
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException ex) {
						}
					}
				}
				listaSchedeTecniche.add(f);
				listaSchedeTecnicheContentType.add(this.getSchedeTecnicheContentType().get(i));
				listaSchedeTecnicheSize.add(this.getSchedeTecnicheSize().get(i));
				listaSchedeTecnicheFileName.add(this.getSchedeTecnicheFileName().get(i));
			}
			clone.setSchedeTecniche(listaSchedeTecniche);
			clone.setSchedeTecnicheContentType(listaSchedeTecnicheContentType);
			clone.setSchedeTecnicheFileName(listaSchedeTecnicheFileName);
			clone.setSchedeTecnicheSize(listaSchedeTecnicheSize);
		}
		
		if (this.immagine != null) {
			File file = this.getImmagine();
			File f = new File(file.getParent() + File.separatorChar + FileUploadUtilities.generateFileName());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
				fos.write(FileUtils.readFileToByteArray(file));
			} catch (FileNotFoundException ex) {
				ApsSystemUtils.logThrowable(ex, this, "clone");
			} catch (IOException ex) {
				ApsSystemUtils.logThrowable(ex, this, "clone");
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "clone");
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ex) {
					}
				}
			}
			clone.setImmagine(f);
			clone.setImmagineContentType(this.getImmagineContentType());
			clone.setImmagineFileName(this.getImmagineFileName());
			clone.setImmagineSize(this.getImmagineSize());
		}
		
		return clone;
	}
	
}
