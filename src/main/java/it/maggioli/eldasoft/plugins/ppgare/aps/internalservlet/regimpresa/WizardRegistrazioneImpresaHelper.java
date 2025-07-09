package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.i18n.II18nManager;
import it.eldasoft.sil.portgare.datatypes.AccountType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaAggiornabiliType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaType;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaType;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc.ImpresaImportExport;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc.ImpresaImportExport.WrongCRCException;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import java.io.File;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Contenitore dei dati del wizard registrazione impresa al portale
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class WizardRegistrazioneImpresaHelper extends WizardDatiImpresaHelper {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -2340091857169744572L;

	// credenziali di accesso
    private String username;
    private String usernameConfirm;

    private String tipologiaRegistrazione;
    // consenso alla privacy
    private Integer privacy;
    
    // consenso extra customizzabile
    private Integer consensoExtra;
    
    // consenso all'utilizzo piattaforma
    private Integer utilizzoPiattaforma;
    
    // soggetto richiedente
    private String soggettoRichiedente;
    
    // True indica che e' attiva la registrazione manuale delle imprese 
    private boolean attesaValidazione;
    
    public String getUsername() {
    	return username;
    }

    public void setUsername(String username) {
    	this.username = username;
    }

    public String getUsernameConfirm() {
    	return usernameConfirm;
    }

    public void setUsernameConfirm(String usernameConfirm) {
    	this.usernameConfirm = usernameConfirm;
    }

    public Integer getPrivacy() {
    	return privacy;
    }

    public void setPrivacy(Integer privacy) {
    	this.privacy = privacy;
    }

	public Integer getConsensoExtra() {
		return consensoExtra;
	}

	public void setConsensoExtra(Integer consensoExtra) {
		this.consensoExtra = consensoExtra;
	}

	public Integer getUtilizzoPiattaforma() {
		return utilizzoPiattaforma;
	}

	public void setUtilizzoPiattaforma(Integer utilizzoPiattaforma) {
		this.utilizzoPiattaforma = utilizzoPiattaforma;
	}

	public String getSoggettoRichiedente() {
		return soggettoRichiedente;
	}

	public void setSoggettoRichiedente(String soggettoRichiedente) {
		this.soggettoRichiedente = soggettoRichiedente;
	}
	
    public boolean isAttesaValidazione() {
		return attesaValidazione;
	}

	public void setAttesaValidazione(boolean attesaValidazione) {
		this.attesaValidazione = attesaValidazione;
	}

	public String getTipologiaRegistrazione() {
		return tipologiaRegistrazione;
	}

	public void setTipologiaRegistrazione(String tipologiaRegistrazione) {
		this.tipologiaRegistrazione = tipologiaRegistrazione;
	}

	/**
	 * costruttori  
	 */
    public WizardRegistrazioneImpresaHelper() {
		super();
		this.username = null;
		this.usernameConfirm = null;
		this.privacy = null;
		this.consensoExtra = null;
		this.utilizzoPiattaforma = null;
		this.soggettoRichiedente = null;
		this.attesaValidazione = false;
    }
    
	public WizardRegistrazioneImpresaHelper(DatiImpresaType datiImpresa) {
		super(datiImpresa);
	}

	public WizardRegistrazioneImpresaHelper(DatiImpresaAggiornabiliType datiImpresa) {
		super(datiImpresa);
	}	

   /**
     * Crea l'oggetto documento per la generazione della stringa XML per
     * l'inoltro della richiesta di registrazione al backoffice
     * 
     * @return oggetto documento contenente i dati della registrazione
     *         dell'impresa
 * @throws ApsSystemException 
     */
    public XmlObject getXmlDocumentRegistrazione(II18nManager ii18nManager, Locale locale) {
		RegistrazioneImpresaDocument document = RegistrazioneImpresaDocument.Factory.newInstance();
		document.documentProperties().setEncoding("UTF-8");
		RegistrazioneImpresaType regImpresa = document.addNewRegistrazioneImpresa();
		// set dati impresa
		DatiImpresaType datiImpresa = regImpresa.addNewDatiImpresa();
		this.addImpresa(datiImpresa);
		// set dati account
		AccountType account = regImpresa.addNewAccount();
		account.setUsername(this.username);
		// set dati consenso al trattamento dati personali
		regImpresa.setConsensoPrivacy(this.privacy.intValue() == 1);
		// set dati consenso all'utilizzo piattaforma
		regImpresa.setConsensoUtilizzoPiattaforma(this.utilizzoPiattaforma.intValue() == 1);
		// set dati consenso extra
		if(this.consensoExtra != null){
			regImpresa.setConsensoExtra(this.consensoExtra.intValue() == 1);
		}
		String elencoConsensi = "";
		try {
			if(this.privacy != null && this.privacy.intValue() == 1){
				elencoConsensi+= ii18nManager.getLabel("LABEL_PRIVACY", locale.getLanguage());
				elencoConsensi+="\n";
			}
			if(this.utilizzoPiattaforma!= null && this.utilizzoPiattaforma.intValue() == 1){
				elencoConsensi+=ii18nManager.getLabel("LABEL_REGISTRA_OE_REGOLE_UTILIZZO_PIATTAFORMA", locale.getLanguage());
				elencoConsensi+="\n";
			}
			if(this.consensoExtra!= null && this.consensoExtra.intValue() == 1){
				elencoConsensi+=ii18nManager.getLabel("LABEL_CONSENSO_EXTRA", locale.getLanguage());
				elencoConsensi+="\n";
			}
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "save");
		}
		
		if(!"".equals(elencoConsensi)){
			regImpresa.setElencoConsensi(elencoConsensi);
		}
		// soggetto richiedente
		regImpresa.setSoggettoRichiedente(this.soggettoRichiedente);
		return document;
    }
    
    /**
     * recupera i dati impresa dall'xml della comunicazione di registrazione impresa
     * @throws XmlException 
     */
    public static XmlObject getXmlDocumentRegistrazione(ComunicazioneType comunicazione) throws XmlException {
    	RegistrazioneImpresaDocument document = null;
		AllegatoComunicazioneType allegatoXml = null;
		int i = 0;
		while (comunicazione.getAllegato() != null && i < comunicazione.getAllegato().length) {
			// si cerca l'allegato con l'xml
			if (PortGareSystemConstants.NOME_FILE_REGISTRAZIONE.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoXml = comunicazione.getAllegato()[i];
				break;
			}
			i++;
		}
		if (allegatoXml == null) {
			// non dovrebbe succedere mai...si inserisce questo controllo 
			// per blindare il codice da eventuali comportamenti anomali !!!
			ApsSystemUtils.getLogger().error("getXmlDocumentRegistrazione(): " + 
					"documento xml per la registrazione impresa non trovato nella comunicazione (" + comunicazione.getDettaglioComunicazione().getId() +  ")");
		} else {
			document = RegistrazioneImpresaDocument.Factory.parse(new String(allegatoXml.getFile()));
		}
		return document;
    }

	/**
	 * esporta i dati impresa su file xml 
	 * @throws ImportExportException 
	 * @throws Exception 
	 */
	public void exportToXmlPortale(OutputStream os, String filename) throws Exception {
		try {
			importExport = new ImpresaImportExport();
			importExport.exportToXml(this, os, filename);
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * importa i dati impresa da file xml
	 * 
	 * @param evento 
	 * @throws ImportExportException 
	 * @throws WrongCRCException 
	 * @throws Exception 
	 */
	public void importFromXmlPortale(File file, String filename, boolean isSpidBusiness) throws WrongCRCException, Exception {
		try {
			importExport = new ImpresaImportExport();
			importExport.importFromXml(file, filename, this, isSpidBusiness);
		} catch(WrongCRCException ex) {
			throw ex;
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * importa i dati impresa da modello DGUE
	 * @throws ImportExportException 
	 * @throws Exception 
	 */
	public void importFromXmlDGUE(File file, String filename) throws Exception {
		try {
			importExport = new ImpresaImportExport();
			importExport.importFromDGUE(file, filename, this);
		} catch(Exception ex) {
			throw ex;
		}
	}
	
	/**
	 * importa i dati impresa da Michelangelo (SACE)
	 * @throws ImportExportException 
	 * @throws Exception 
	 */
	public void importFromMichelangelo(String codiceFiscale) throws Exception {
		try {
			importExport = new ImpresaImportExport();
			importExport.importFromMichelangelo(codiceFiscale, this);
		} catch(Exception ex) {
			throw ex;
		}
	}

}
