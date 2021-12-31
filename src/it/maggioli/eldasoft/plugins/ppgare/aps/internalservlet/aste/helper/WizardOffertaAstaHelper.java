package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractWizardHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.DocumentiComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.InitOffertaEconomicaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.InitPartecipazioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Contenitore dei dati del wizard dell'offerta di un'asta
 *
 * @author 
 */
public class WizardOffertaAstaHelper extends AbstractWizardHelper 
	implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * definizione degli step del wizard   
	 */
	public static final String STEP_DATI_OPERATORE 	= "datioperatore";
	public static final String STEP_DATI_OFFERTA 	= "datiofferta";
	public static final String STEP_FIRMATARI		= "firmatari";
//	public static final String STEP_GENERA_PDF      = "generapdf";
	public static final String STEP_UPLOAD_PDF 		= "uploadpdf";
	public static final String STEP_RIEPILOGO 		= "riepilogo";
	
	public String getSTEP_DATI_OPERATORE() { return STEP_DATI_OPERATORE; }
	public String getSTEP_DATI_OFFERTA() { return STEP_DATI_OFFERTA; }
	public String getSTEP_FIRMATARI() { return STEP_FIRMATARI; }
//	public String getSTEP_GENERA_PDF() { return STEP_GENERA_PDF; }
	public String getSTEP_UPLOAD_PDF() { return STEP_UPLOAD_PDF; }
	public String getSTEP_RIEPILOGO() { return STEP_RIEPILOGO; }
		
	/**
	 * Elenco dei file generati durante la sessione del wizard (barcode, pdf, ...)
	 */
	private List<File> tempFiles;

	/**
	 * Flag di protezione dall'invio ripetuto dei dati se l'utente dopo aver
	 * effettuato un invio preme sul pulsante indietro del browser e poi
	 * riseleziona l'invio
	 */
	private boolean datiInviati;
		
	/**
	 * Dettaglio dell'asta
	 */
	private DettaglioAstaType asta;

	/**
	 * Elenco della classifica dell'asta
	 */
	private List<DettaglioClassificaType> classifica;

	/**
	 * Elenco dei rilanci dell'asta
	 */
	private List<DettaglioRilancioType> rilanci;

	/**
	 * importo dell'offerta d'asta
	 */
	private double importoOfferta;
	
		
	/**
	 * Helper dell'offerta economica 
	 * Contiene tutte le info per dati impresa, amministatore del procedimento, 
	 * firmatari, concorrente, mandataria, documenti...
	 * 
	 * NB: usato per la generazione del PDF
	 */
	private WizardOffertaEconomicaHelper offertaEconomica;
	
	/**
	 * Helper della partecipazione 
	 * 
	 * NB: usato per la generazione del PDF   
	 */
	private WizardPartecipazioneHelper partecipazione;
	
	/**
	 * Helper dei documenti allegati 
	 * 
	 * NB: usato per la generazione/upload del PDF   
	 */
	private DocumentiComunicazioneHelper documenti;

	/**
	 * Indica se ci sono lotti distinti 
	 */
	private boolean lottiDistinti;
	
	/**
	 * lotti associati alla gara 
	 */
	private LottoGaraType[] lotti;
		
	/**
	 * 
	 */
	private byte[] chiaveSessione;
	
	/**
	 * 
	 */	
	private String username;
		
	/**
	 * id della comunicazione FS13
	 */
	private Long idComunicazione;	
	
	
	/**
	 * Inizializza il contenitore vuoto attribuendo i valori di default
	 */
	public WizardOffertaAstaHelper() {
		super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA, true);
		this.tempFiles = new ArrayList<File>();
		this.datiInviati = false;
		this.asta = new DettaglioAstaType();
		this.classifica = new ArrayList<DettaglioClassificaType>();
		this.rilanci = new ArrayList<DettaglioRilancioType>();
		this.importoOfferta = 0;
		this.offertaEconomica = new WizardOffertaEconomicaHelper();		
		this.partecipazione = new WizardPartecipazioneHelper();
		this.documenti = new DocumentiComunicazioneHelper();		// usato da stepUploadPdf/SendComunicazioni
		this.lottiDistinti = false;		
		this.lotti = null;
		this.chiaveSessione = null;
		this.username = null;
		this.idComunicazione = null;	// id FS13
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0 il session binding event
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		Iterator<File> iter = this.tempFiles.listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}		
		iter = this.getOffertaEconomica().getPdfGenerati().listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * @return the tempFiles
	 */
	public List<File> getTempFiles() {
		return tempFiles;
	}

	/**
	 * @return the datiInviati
	 */
	public boolean isDatiInviati() {
		return datiInviati;
	}

	/**
	 * @param datiInviati the datiInviati to set
	 */
	public void setDatiInviati(boolean datiInviati) {
		this.datiInviati = datiInviati;
	}	

	/**
	 * @return the asta
	 */
	public DettaglioAstaType getAsta() {
		return asta;
	}

	/**
	 * @param asta the asta to set
	 */
	public void setAsta(DettaglioAstaType asta) {
		this.asta = asta;
	}

	/**
	 * @return the classifica
	 */
	public List<DettaglioClassificaType> getClassifica() {
		return classifica;
	}

	/**
	 * @param classifica the classifica to set
	 */
	public void setClassifica(List<DettaglioClassificaType> classifica) {
		this.classifica = classifica;
	}
	
	/**
	 * @return the rilanci
	 */
	public List<DettaglioRilancioType> getRilanci() {
		return rilanci;
	}
	
	/**
	 * @param rilanci the rilanci to set
	 */
	public void setRilanci(List<DettaglioRilancioType> rilanci) {
		this.rilanci = rilanci;
	}
	
	/**
	 * @return the importoOfferta
	 */
	public double getImportoOfferta() {
		return importoOfferta;
	}
	
	/**
	 * @param importoOfferta the importoOfferta to set
	 */
	public void setImportoOfferta(double importoOfferta) {
		this.importoOfferta = importoOfferta;
	}

	/**
	 * @return the offertaEconomica
	 */
	public WizardOffertaEconomicaHelper getOffertaEconomica() {
		return offertaEconomica;
	}
	
	/**
	 * @param offertaEconomica the offertaEconomica to set
	 */
	public void setOffertaEconomica(WizardOffertaEconomicaHelper offertaEconomica) {
		this.offertaEconomica = offertaEconomica;
	}
	
	/**
	 * @return the partecipazione
	 */
	public WizardPartecipazioneHelper getPartecipazione() {
		return partecipazione;
	}
	/**
	 * @param partecipazione the partecipazione to set
	 */
	public void setPartecipazione(WizardPartecipazioneHelper partecipazione) {
		this.partecipazione = partecipazione;
	}
		
	/**
	 * @return the documenti
	 */
	public DocumentiComunicazioneHelper getDocumenti() {
		return documenti;
	}
	
	/**
	 * @param documenti the documenti to set
	 */
	public void setDocumenti(DocumentiComunicazioneHelper documenti) {
		this.documenti = documenti;
	}

	/**
	 * @return the lottiDistinti
	 */
	public boolean isLottiDistinti() {
		return lottiDistinti;
	}
	
	/**
	 * @param lottiDistinti the lottiDistinti to set
	 */
	public void setLottiDistinti(boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}
	
	/**
	 * @return the lotti
	 */
	public LottoGaraType[] getLotti() {
		return lotti;
	}
	
	/**
	 * @param lotti the lotti to set
	 */
	public void setLotti(LottoGaraType[] lotti) {
		this.lotti = lotti;
	}

	/**
	 * @return the chiaveSessione
	 */
	public byte[] getChiaveSessione() {
		return chiaveSessione;
	}
	
	/**
	 * @param chiaveSessione the chiaveSessione to set
	 */
	public void setChiaveSessione(byte[] chiaveSessione) {
		this.chiaveSessione = chiaveSessione;
	}	
	
	/**
	 * @return the idComunicazione
	 */
	public Long getIdComunicazione() {
		return idComunicazione;
	}
	/**
	 * @param idComunicazione the idComunicazione to set
	 */
	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}	
	
	/**
	 * @return the pdfUUID
	 */
	public String getPdfUUID() {
		return (this.offertaEconomica != null ? this.offertaEconomica.getPdfUUID() : null);
	}	
	/**
	 * @param pdfUUID the pdfUUID to set
	 */
	public void setPdfUUID(String pdfUUID) {
		if(this.offertaEconomica != null) {
			this.offertaEconomica.setPdfUUID(pdfUUID);
		}
	}

	/**
	 * @return the datiModificati
	 */
	public boolean isDatiModificati() {
		return (this.offertaEconomica != null ? this.offertaEconomica.isDatiModificati() : false);
	}
	/**
	 * @param datiModificati the datiModificati to set
	 */
	public void setDatiModificati(boolean datiModificati) {
		if(this.offertaEconomica != null) {
			this.offertaEconomica.setDatiModificati(datiModificati);
		}
	}
	
	/**
	 * @return the rigenPdf
	 */
	public boolean isRigenPdf() {
		boolean rigenera = false; 
		if(this.offertaEconomica != null) {
			rigenera = (this.offertaEconomica.isRigenPdf() ||
					    this.offertaEconomica.getPdfUUID() == null);
		}
		return rigenera; 
	}
	/**
	 * @param rigenPdf the rigenPdf to set
	 */
	public void setRigenPdf(boolean rigenPdf) {
		if(this.offertaEconomica != null) {
			this.offertaEconomica.setRigenPdf(rigenPdf);
		}
	}
	
	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per 
	 * consentire la corretta navigazione
	 */		
	@Override
	public void fillStepsNavigazione() {
		// configura la mappa degli step di navigazione
		// e i relativi target associati...
		this.clearMappaStepNavigazione();		
		this.addMappaStepNavigazione(STEP_DATI_OPERATORE, null,                		null);
		this.addMappaStepNavigazione(STEP_DATI_OFFERTA,   null,           	   		null);
		this.addMappaStepNavigazione(STEP_FIRMATARI,   	  null, 			   		null);
//		this.addMappaStepNavigazione(STEP_GENERA_PDF, 	  "successSkipGenPdf",    	"backSkipGenPdf");
		this.addMappaStepNavigazione(STEP_UPLOAD_PDF, 	  "successSkipUploadPdf", 	"backSkipUploadPdf");
		this.addMappaStepNavigazione(STEP_RIEPILOGO,  	  null, 		       	  	null);
						
		// prepara l'elendo degli step disponibili per il wizard...	
		this.stepNavigazione.add(STEP_DATI_OPERATORE);
		this.stepNavigazione.add(STEP_DATI_OFFERTA);
		this.stepNavigazione.add(STEP_FIRMATARI);
		if(this.rilanci != null && this.rilanci.size() > 0) {
//			this.stepNavigazione.add(STEP_GENERA_PDF);
			this.stepNavigazione.add(STEP_UPLOAD_PDF);
		}
		this.stepNavigazione.add(STEP_RIEPILOGO);		
	}

	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

    /**
     * Recupera l'helper dell'asta dalla sessione 
     */
    public static WizardOffertaAstaHelper fromSession() {
    	// NB: crea un'istanza temporanea separata dell'helper 
    	//     per recuperare i dati dalla sessione   
    	WizardOffertaAstaHelper instance = new WizardOffertaAstaHelper();
    	WizardOffertaAstaHelper helper = (WizardOffertaAstaHelper) instance.getFromSession();
    	instance = null;
    	return helper;
    }

	/**
	 * Crea l'helper per il wizard che gestisce la conferma dell'offerta d'asta 
	 */
	public static WizardOffertaAstaHelper getHelper(
			EncodedDataAction action,			
			String codice, 
			String codiceLotto) throws ApsException {
		
		WizardOffertaAstaHelper helper = null;
			
		Map<String, Object> session = null;
	    try {  	    
	    	session = ServletActionContext.getContext().getSession(); 
	    } catch (Exception e) {
	    	session = null;
		}	    
	    if(session != null) {
		    try {
		    	String username = null;
		    	if(action != null && action.getCurrentUser() != null) {
		    		username = action.getCurrentUser().getUsername();
		    	}	    	
			    if(null != username && !username.equals(SystemConstants.GUEST_USER_NAME)) {
			
			    	// leggi l'helper dalla sessione,
			    	// e se non esiste creane uno di nuovo...			    		
					helper = WizardOffertaAstaHelper.fromSession();
					
					// verifica se l'helper corrisponde alla gara/asta
					// ed eventualmente ricrealo... 
					if(codice != null) {
						if(helper != null) { 
							String cod = (helper.getAsta() != null && helper.getAsta().getCodice() != null ? helper.getAsta().getCodice() : "");  
							String codLotto = (helper.getAsta() != null && helper.getAsta().getCodiceLotto() != null ? helper.getAsta().getCodiceLotto() : "");						 
							if(!cod.equalsIgnoreCase(codice) ||
							   (cod.equalsIgnoreCase(codice) && !codLotto.equalsIgnoreCase(codiceLotto))) {
								helper.removeFromSession();
								helper = null;
							}
						}
					}
					
					if(helper == null) {
						// Inizializza un nuovo helper per l'offerta d'asta.
						// L'helper aste viene utilizzato anche per generare un pdf
						// basato sull'offerta; il report ha bisogno dei seguenti 
						// dati per poter essere generato: 
						//	- impresa helper 
						//  - firmatari, 
						//  - voci dettaglio, voci dettaglio non ribasso, attributi aggiuntivi, importo offerta
						//  - stazione appaltante
						//  - partecipazione
	
						// crea un nuovo helper...				
						helper = new WizardOffertaAstaHelper();						
						helper.username = username;
		
						// prepara i managers...
						File tempDir = StrutsUtilities.getTempDir(ServletActionContext.getServletContext());
						
						IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
							.getBean("AppParamManager", ServletActionContext.getRequest());
	
						IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
							.getBean(PortGareSystemConstants.BANDI_MANAGER,
									 ServletActionContext.getRequest());
					
						IAsteManager asteManager = (IAsteManager) ApsWebApplicationUtils
							.getBean(PortGareSystemConstants.ASTE_MANAGER,
								     ServletActionContext.getRequest());

						IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
							.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER,
								 ServletActionContext.getRequest());					

						// recupera i dettagli della gara e gli eventuali lotti...
						DettaglioGaraType dettGara = bandiManager.getDettaglioGara(codice);						
				
						boolean lottiDistinti = ( 
							dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
							dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO
						);
						
						LottoGaraType[] lottiGara = null;
						if(lottiDistinti) {
							lottiGara = bandiManager.getLottiGara(codice);							
						}
						
						String codiceGara = (lottiDistinti ? codiceLotto : codice);

						// recupera i detagli dell'asta...
						DettaglioAstaType asta = asteManager.getDettaglioAsta(									
								codiceLotto != null && !codiceLotto.isEmpty() ? codiceLotto : codice,
								username);
	
						// recupera l'elenco dei rilanci per l'asta...
						List<DettaglioRilancioType> rilanci = asteManager.getElencoRilanci(
								codice, 
								codiceLotto, 
								username,
								asta.getFase().toString());

						// recupera l'ultima l'offerta inviata e gli eventuali
						// prezzi unitari e la stazione appaltante...
						double importoOfferta = (asta.getImportoUltimoRilancio() != null ? asta.getImportoUltimoRilancio() : 0);
						double ribassoOfferta = (asta.getRibassoUltimoRilancio() != null ? asta.getRibassoUltimoRilancio() : 0);
						
						// recupera i dettagli della classifica...
						List<DettaglioClassificaType> classifica = asteManager.getClassifica(
								asta.getTipoClassifica(), 
								codice, 
								codiceLotto, 
								username, 
								asta.getFase().toString());

						// prepapa l'helper dell'impresa...
						WizardDatiImpresaHelper impresaHelper = ImpresaAction.getLatestDatiImpresa(
									username, 
									action, 
									appParamManager);
						
						// prepara l'helper della partecipazione
						WizardPartecipazioneHelper partecipazioneHelper = 
							WizardOffertaAstaHelper.getWizardPartecipazione(
									dettGara,
									impresaHelper,
									username, 
									bandiManager,
									comunicazioniManager);
						
						// crea helper dei dati generali dell'offerta...
						WizardOffertaEconomicaHelper offerta = 
							WizardOffertaAstaHelper.getWizardOffertaEconomicaHelper(
								session,
								username,
								codice,
								codiceLotto,
								bandiManager,
								comunicazioniManager,
								tempDir,
								partecipazioneHelper,
								impresaHelper,
								dettGara);

						offerta.setCodice(codiceGara);
						offerta.setGara((dettGara != null ? dettGara.getDatiGeneraliGara() : null));
						offerta.setImpresa(impresaHelper);
						offerta.setImportoOfferto(importoOfferta);
						offerta.setRibasso(ribassoOfferta);
						offerta.setStazioneAppaltanteBando(dettGara.getStazioneAppaltante());
						
			    		// recupera il numero dei decimali per il ribasso
			    		// prima verifica se per la gara esiste TORN.PRERIB 
			    		// e se non esiste un valore utilizza quello che 
			    		// si trova nel tabellato "A1028"
			    		Long numDecimaliRibasso = null;
			    		if(dettGara.getDatiGeneraliGara() != null && dettGara.getDatiGeneraliGara().getNumDecimaliRibasso() != null) {
			    			numDecimaliRibasso = dettGara.getDatiGeneraliGara().getNumDecimaliRibasso();
			    		}
			    		if(numDecimaliRibasso == null) {
			    			numDecimaliRibasso = bandiManager.getNumeroDecimaliRibasso();
			    		}
			    		offerta.setNumDecimaliRibasso(numDecimaliRibasso);

						// recupera il dettaglio dell'ultimo rilancio a prezzi unitari...
						List<VoceDettaglioAstaType> vociAsta = null;
						if(rilanci != null && rilanci.size() > 0) {
							vociAsta = asteManager.getPrezziUnitariRilancio(
									codice,
									codiceLotto, 
									username,
									rilanci.get(rilanci.size() - 1).getId());
						}
						sincronizzaPrezziUnitariOfferta(vociAsta, offerta);
							
						// verifica se esiste la comunicazione di supporto 
						// per le offerte d'asta FS13 in stato BOZZA (o PROCESSATA)...
						List<String> stati = new ArrayList<String>(); 
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
						
						DettaglioComunicazioneType dettComunicazioneFS13 = ComunicazioniUtilities.retrieveComunicazioneConStati(
								comunicazioniManager,
								username,
								codiceGara,
								null,
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA,
								stati);
						
						boolean offertaInviata = false;
						if(dettComunicazioneFS13 != null) {
							if(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(dettComunicazioneFS13.getStato())) {
								offertaInviata = false;
							} else {
								offertaInviata = true;
							}
							
							// recupara dall'allegato di FS13 l'UUID...
							ComunicazioneType comunicazione = comunicazioniManager
								.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, 
												  dettComunicazioneFS13.getId());
							popolaFromBozza(comunicazione, offerta);
							
							// l'allegato dell'offerta d'asta è nella comunicazione FS12... 
							// NB: FS13.Chiave3 = FS12.id è il collegamento tra FS13 e FS12
							if(dettComunicazioneFS13.getChiave3() != null && 
							   !dettComunicazioneFS13.getChiave3().isEmpty()) {
								comunicazione = comunicazioniManager.getComunicazione(
										CommonSystemConstants.ID_APPLICATIVO, 
										Long.parseLong(dettComunicazioneFS13.getChiave3()));
							
								if(comunicazione != null && 
								   comunicazione.getAllegato() != null && comunicazione.getAllegato().length > 0) {
									AllegatoComunicazioneType allegato = comunicazione.getAllegato(0);
								    try {
								    	// crea il file allegato nell'area temporanea...
								    	// ed aggiungi l'allegato ai documenti dell'helper
								    	String fn = StrutsUtilities.getTempDir(ServletActionContext.getServletContext()).getAbsolutePath()
													+ File.separatorChar + allegato.getNomeFile();
								    	File filePdf = new File(fn); 
								    	FileOutputStream fos = new FileOutputStream(filePdf);
								        fos.write(allegato.getFile());
								        fos.close();
								        
								        helper.tempFiles.add(filePdf);
								        
								        DocumentiComunicazioneHelper documenti = new DocumentiComunicazioneHelper();
								        documenti.addDocUlteriore(
								        		allegato.getDescrizione(),
								        		filePdf,
								        		null,
								        		allegato.getNomeFile(),
								        		null);

										helper.setDocumenti(documenti);
										
										// imposta l'id della comunicazione FS12
										offerta.setIdComunicazione(comunicazione.getDettaglioComunicazione().getId());
								    } catch (Exception e) {
								    	//...
								    }
								}
							}
						}
						
						// aggiorna i dati dell'helper...
						helper.setAsta(asta);
						helper.setOffertaEconomica(offerta);
						helper.setPartecipazione(partecipazioneHelper);
						helper.setIdComunicazione(dettComunicazioneFS13 != null ? dettComunicazioneFS13.getId() : null);
						helper.setDatiInviati(offertaInviata);
						helper.setLottiDistinti(lottiDistinti);
						helper.setLotti(lottiGara);
						helper.setClassifica(classifica);
						helper.setRilanci(rilanci);
						helper.setImportoOfferta(importoOfferta);
						helper.setAsta(asta);
						
						// prepara la configurazione degli steps del wizard...
						helper.fillStepsNavigazione();
					}
					
					// aggiorna l'helper in sessione...
					helper.putToSession();
			    } 
			} catch(Throwable e) {
				// rimuovi l'helper dalla sessione...
				if(helper != null) {
					helper.removeFromSession();
				}
	    		ApsSystemUtils.logThrowable(e, null, "getHelper");
			}
		}
		
		return helper;
	}

	/**
	 * Genera l'XML con i dati comuni per l'offerta d'asta e per il report.
	 * 
	 * @param document documento xml creato
	 * 
	 * @return documento aggiornato
	 * @throws IOException
	 */
	@Override
	public XmlObject getXmlDocument(
			XmlObject document,
			boolean attachFileContents, 
			boolean report) throws Exception 
	{
		BustaEconomicaType bustaEconomica = null;
		
		GaraType gara = (this.getOffertaEconomica() != null 
				         ? this.getOffertaEconomica().getGara() 
				         : null);
		
		if (document instanceof ReportOffertaEconomicaDocument) {
			ReportOffertaEconomicaType reportOffertaEconomica = ((ReportOffertaEconomicaDocument) document).addNewReportOffertaEconomica();
			bustaEconomica = (BustaEconomicaType) reportOffertaEconomica;
			
			reportOffertaEconomica.setCodiceOfferta(gara.getCodice());
			reportOffertaEconomica.setOggetto(gara.getOggetto());
			reportOffertaEconomica.setCig(gara.getCig());
			reportOffertaEconomica.setTipoAppalto(gara.getTipoAppalto());
			reportOffertaEconomica.setCriterioAggiudicazione(gara.getTipoAggiudicazione());
		} else {
			bustaEconomica = ((BustaEconomicaDocument) document).addNewBustaEconomica();
		}

		// memorizzo una data non significativa in quanto si tratta di bozza
		// (la data comunque serve in quanto il campo e' obbligatorio nel
		// tracciato xml da inviare)
		bustaEconomica.setDataPresentazione(new GregorianCalendar());

		OffertaEconomicaType offerta = bustaEconomica.addNewOfferta();

		String pdfUUID = (this.getOffertaEconomica() != null
				  	      ? this.getOffertaEconomica().getPdfUUID()
						  : null);
		if (pdfUUID != null) {
			offerta.setUuid(pdfUUID);
		}

		document.documentProperties().setEncoding("UTF-8");
		return document;
	}

//	public XmlObject getXmlDocument(XmlObject document) {
//		return getXmlDocument(document, false, true);
//	}

	/**
     * Estrae, a partire dalla comunicazione, il documento XML contenente
     * l'offerta d'asta.
     * 
     * @param comunicazione
     *            comunicazione con in allegato i dati dell'offerta
     * @return oggetto XmlObject della classe BustaEconomicaDocument
     * @throws ApsException
     * @throws XmlException
	 * @throws UnsupportedEncodingException 
	 * @throws GeneralSecurityException 
     */
    private static void popolaFromBozza(
    		ComunicazioneType comunicazione, 
    		WizardOffertaEconomicaHelper helper) 
    	throws ApsException, XmlException, UnsupportedEncodingException, GeneralSecurityException 
    {
    	if(comunicazione == null) { 
    		return;
    	}
    	
		AllegatoComunicazioneType allegato = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && allegato == null) {
		    // si cerca l'xml con i dati dell'iscrizione tra tutti gli allegati
		    if (PortGareSystemConstants.NOME_FILE_BUSTA.equals(comunicazione
			    .getAllegato()[i].getNomeFile())) {
		    	allegato = comunicazione.getAllegato()[i];
		    } else {
		    	i++;
		    }
		}
	
		if (allegato == null) {
		    // non dovrebbe succedere mai...si inserisce questo
		    // controllo per blindare il codice da eventuali
		    // comportamenti anomali
//		    throw new ApsException(
//			    "Errors.offertaTelematica.xmlOffertaNotFound");
		} else {
		    // si interpreta l'xml ricevuto
			BustaEconomicaDocument doc = BustaEconomicaDocument.Factory.parse(
					new String(allegato.getFile()));
		    BustaEconomicaType busta = doc.getBustaEconomica();
		    OffertaEconomicaType offerta = busta.getOfferta();
		    
//		    Cipher cipher = null;
//		    if (helper.getDocumenti().get getChiaveSessione() != null) {
//		    	cipher = SymmetricEncryptionUtils.getDecoder(this.getChiaveSessione(), this.username);
//		    }
//		    
//		    String uuid = new String(SymmetricEncryptionUtils.translate(cipher, offerta.getUuid().getBytes()));
		    String uuid = offerta.getUuid();
		    helper.setPdfUUID(uuid);
		}
    }
     
    /**
     * ...
     */
    private static WizardPartecipazioneHelper getWizardPartecipazione(
    		DettaglioGaraType dettGara,
    		WizardDatiImpresaHelper impresa,
    		String username,
    		IBandiManager bandiManager,
    		IComunicazioniManager comunicazioniManager) 
    {
    	WizardPartecipazioneHelper partecipazione = null;
    	try {
	    	partecipazione = InitPartecipazioneAction.createHelper(
				PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA, 
				dettGara,
				dettGara.getDatiGeneraliGara().getCodice(),
				username,
				null);
		
	    	TipoPartecipazioneType tipoPartecipazione = bandiManager
	    		.getTipoPartecipazioneImpresa(
	    				username, 
	    				dettGara.getDatiGeneraliGara().getCodice(),
	    				null);
	    	
	    	partecipazione.setRti(tipoPartecipazione.isRti());
	    	partecipazione.setDenominazioneRTI(tipoPartecipazione.getDenominazioneRti());
	    	
			// recupera dalla comunicazione l'xml con i dati dell'RTI
	    	if(tipoPartecipazione.isRti()) {
	    		// aggiungi la mandataria ai componenti RTI...
	    		IComponente mandataria = new ComponenteHelper(); 
		    	if (impresa.isLiberoProfessionista()
		    		&& StringUtils.isEmpty(impresa.getDatiPrincipaliImpresa().getPartitaIVA())) {
					// se è libero professionista e partita iva vuota =>
		    		// usa codice fiscale come chiave
		    		mandataria.setCodiceFiscale(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
		    	} else {
		    		// altrimenti usa la partita iva
		    		mandataria.setPartitaIVA(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
		    	}
	    		mandataria.setNazione(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
	    		mandataria.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
	    		mandataria.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
	    		//mandataria.setQuota(quota);
	    		partecipazione.getComponenti().add(mandataria);
	    		
	    		// recupera dalla comunicazione FS10, FS11 i componenti dell'RTI...
	    		List<String> stati = new ArrayList<String>();
	    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
	    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
	    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
	    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
	    		
	    		DettaglioComunicazioneType idComunicazione = ComunicazioniUtilities
	    			.retrieveComunicazioneConStati(
	    				comunicazioniManager,
	    				username, 
	    				dettGara.getDatiGeneraliGara().getCodice(),
	    				null,
	    				PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT,
	    				stati);
	    		if(idComunicazione == null) {
	    			idComunicazione = ComunicazioniUtilities
		    			.retrieveComunicazioneConStati(
		    				comunicazioniManager,
		    				username, 
		    				dettGara.getDatiGeneraliGara().getCodice(),
		    				null,
		    				PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT,
		    				stati);
	    		}
	    		if(idComunicazione != null) {
					ComunicazioneType comunicazione = comunicazioniManager
						.getComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,  
								idComunicazione.getId());
					TipoPartecipazioneDocument document = WizardDocumentiBustaHelper
						.getDocumentoPartecipazione(comunicazione);
					WizardPartecipazioneHelper p = new WizardPartecipazioneHelper(); 
					p.fillFromXml(document);
					
					// aggiungi le mandatarie ai componenti RTI...
					for(int i = 0; i < p.getComponenti().size(); i++) {
						partecipazione.getComponenti().add(p.getComponenti().get(i));
					}
	    		}
	    	}
    	} catch(Throwable e) {
    		ApsSystemUtils.logThrowable(e, null, "getWizardPartecipazione");
    	}
		return partecipazione;
    }
    
    /**
     * Inizializza l'helper per l'offerta economica da allegare 
     * all'offerta d'asta
     * NB: 
     *    da verificare se è possibile centralizzare l'inizializzazione, 
     *    utilizzando l'inizializzazione delle offerte economiche  
     *    (InitOffertaEconomicaAction.getInstance(...))
     */
    private static WizardOffertaEconomicaHelper getWizardOffertaEconomicaHelper(
    		Map<String, Object> session,
    		String username,
    		String codice,
    		String codiceLotto,
    		IBandiManager bandiManager,
    		IComunicazioniManager comunicazioniManager,
    		File tempDir,
    		WizardPartecipazioneHelper partecipazioneHelper,
    		WizardDatiImpresaHelper impresaHelper,
    		DettaglioGaraType dettGara) 
    {
    	WizardOffertaEconomicaHelper helperOfferta = null;

		String codiceGara = (codiceLotto != null && !codiceLotto.isEmpty()
				? codiceLotto : codice);
		
//		ArrayList<IComponente> componentiRTI = 
//			(ArrayList<IComponente>) partecipazioneHelper.getComponenti();
		ComponentiRTIList componentiRTI = new ComponentiRTIList(partecipazioneHelper.getComponenti());

		// recupera i firmatari della mandataria dal BO...
		try {
    		// metti temporaneamente in sessione partecipazione e
			// anagrafica impresa in modo da permettere a 
			// InitOffertaEconomicaAction.getInstance() 
			// la creazione di un nuovo helper offerta...
			session.put(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA, partecipazioneHelper);
			session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, impresaHelper);						
			session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);						
			helperOfferta = InitOffertaEconomicaAction.getInstance(
				session, 
				username, 
				codice, 
				codiceLotto, 
				-1,
				bandiManager, 
				comunicazioniManager, 
				tempDir);
		} catch(Throwable e) {
			// NB: traccia l'errore di mancata inizializzazione dell'offerta!!!
			ApsSystemUtils.logThrowable(e, null, "getWizardOffertaEconomicaHelper");
		} finally {
			// rimuovi dalla sessione i dati temporanei...
			session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
			session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		}
		
		// recupera i firmatari delle mandanti ...
    	FirmatarioType[] firmatariBusta = null;
    	if(partecipazioneHelper.isRti()) {
    		ComunicazioneType comunicazione = null; 
	    	try {
	    		// verifica se esiste la comunicazione per le offerte FS11C 
	    		// ("busta.xml") in stato BOZZA, DA_RPOCESSARE o PROCESSATA...
				List<String> stati = new ArrayList<String>(); 
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				
				DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities.retrieveComunicazioneConStati(
						comunicazioniManager,
						username,
						codiceGara, //codice,
						null,
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
						stati);
				if(dettaglioComunicazione != null) {
					// estrai dalla comunicazione la busta contenente i firmatari...
					comunicazione = comunicazioniManager.getComunicazione(
							CommonSystemConstants.ID_APPLICATIVO, 
							dettaglioComunicazione.getId());
					
					firmatariBusta = null;
					BustaEconomicaDocument bustaEconomica = InitOffertaEconomicaAction
						.getBustaEconomicaDocument(comunicazione);
					if(bustaEconomica != null) {
						firmatariBusta = bustaEconomica.getBustaEconomica().getFirmatarioArray();
					}
				}
	    	} catch(Throwable e) {
				String xml = null;
				try {
					xml = InitOffertaEconomicaAction.getBustaEconomicaDocumentXml(comunicazione);
				} catch (Exception t) {
					xml = null;
				}
	    		ApsSystemUtils.logThrowable(e, null, "getWizardOffertaEconomicaHelper",
	    				"Xml: " + xml);
	    	}   
    	}
	    
    	// aggiorna i firmatari delle mandanti... 
		if(firmatariBusta != null && firmatariBusta.length > 0) {
			// recupera i dati dalla FS11C...
			if(helperOfferta == null) {
				helperOfferta = new WizardOffertaEconomicaHelper();
			}
			helperOfferta.setGara(dettGara.getDatiGeneraliGara());
			helperOfferta.setImpresa(impresaHelper);
			helperOfferta.setComponentiRTI(componentiRTI);

			WizardOffertaAstaHelper.refreshFirmatari(
						helperOfferta, 
						firmatariBusta,
						bandiManager, 
						username);
		} else {
//			// se non trovi dati nella busta economica, tenta il recupero del
//			// firmatario da BO...
//			for(int i = 0; i < componentiRTI.size(); i++) {
//				//...
//			}
		}
		
    	return helperOfferta;
    }

    /**
     * crea la lista dei firmatari in caso di raggruppamento RTI
     * 
     * @param offerta 
     * 			è il wizard dell'offerta economica con valorizzati gli attributi 
     *          "impresa" e "componentiRTI"
     * @param firmatari
     * 			è l'elenco dei firmatari estratti da xml (FS11R/FS11C)
     * @param bandiManager
     * 			è il bandiManager
     * @param username
     * 			è lo username dell'utente loggato
     */
    public static void refreshFirmatari(
    		WizardOffertaEconomicaHelper offerta,
    		FirmatarioType[] firmatari,
    		IBandiManager bandiManager,
    		String username) 
    {
    	try {
    		// La gestione dei firmatari di un raggruppamento RTI è composta da
    		//
    		//  - listaFirmatariMandataria(1..K)
    	 	//  
    		// "listaFirmatariMandataria" è utilizzata dalla pagina jsp per 
    		// gestire l'elenco dei firmatari della mandataria

			WizardDatiImpresaHelper impresa = offerta.getImpresa();
			
			ComponentiRTIList componentiRTI = new ComponentiRTIList(offerta.getComponentiRTI());
			
			
			List<FirmatarioBean> firmatariMandataria = new ArrayList<FirmatarioBean>();
			
			// aggiorna la lista dei firmatari con i dati della MANDATARIA...
			if (impresa.isLiberoProfessionista()) {
			    // se la mandataria è un libero professionista allora 
				// solo 1 possibile firmatario
			    FirmatarioBean firmatario = new FirmatarioBean();
			    firmatario.setNominativo(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
			    firmatariMandataria.add(firmatario);
			} else {
			    // la mandantaria non è un libero professionita allora
				// possibili 1..N firmatari
				HashMap<String, ArrayList<ISoggettoImpresa>> tipiLista =
					new HashMap<String, ArrayList<ISoggettoImpresa>>();
				tipiLista.put(CataloghiConstants.LISTA_ALTRE_CARICHE, impresa.getAltreCaricheImpresa());
				tipiLista.put(CataloghiConstants.LISTA_DIRETTORI_TECNICI, impresa.getDirettoriTecniciImpresa());
				tipiLista.put(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI, impresa.getLegaliRappresentantiImpresa());

				for(String tipo : tipiLista.keySet()) {
	    			ArrayList<ISoggettoImpresa> lista = tipiLista.get(tipo);
	    			for(int j = 0; j < lista.size(); j++) {
	    				ISoggettoImpresa item = lista.get(j);
	    				if (item.getDataFineIncarico() == null
    						&& "1".equals(item.getResponsabileDichiarazioni())) {
	    					
    					    String cognome = StringUtils.capitalize(item.getCognome()
    						    .substring(0, 1)) + item.getCognome().substring(1);
    					    String nome = StringUtils.capitalize(item.getNome().substring(0, 1))
    						    + item.getNome().substring(1);
    					    String nominativo = new StringBuilder().append(cognome)
						    	.append(" ").append(nome).toString();
    					    
    					    // aggiorna i firmatari...
    					    SoggettoFirmatarioImpresaHelper soggetto = new SoggettoFirmatarioImpresaHelper();
							soggetto.setCognome(item.getCognome());
							soggetto.setNome(item.getNome());
							soggetto.setNominativo(nominativo);
							soggetto.setCodiceFiscale(item.getCodiceFiscale());
							soggetto.setCap(item.getCap());
							soggetto.setComune(item.getComune());
							soggetto.setIndirizzo(item.getIndirizzo());				
							soggetto.setNazione(item.getNazione());
							soggetto.setNumCivico(item.getNumCivico());
							soggetto.setProvincia(item.getProvincia());
							soggetto.setComuneNascita(item.getComuneNascita());
							soggetto.setDataNascita(item.getDataNascita());
							soggetto.setProvinciaNascita(item.getProvinciaNascita());
							soggetto.setSesso(item.getSesso());
							soggetto.setSoggettoQualifica(item.getSoggettoQualifica());
							
							componentiRTI.addFirmatario(item, soggetto);
							
    					    // aggiorna ListaFirmatariMandataria...
    					    FirmatarioBean firmatario = new FirmatarioBean();
    					    firmatario.setNominativo(nominativo);
    					    firmatario.setIndex(j);
    					    firmatario.setLista(tipo);
    					    firmatariMandataria.add(firmatario);    	
    					}
	    			}
				}				
			}
			
			// aggiorna la lista dei firmatari con i dati delle MANDANTI...					
			if(firmatari != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				
				for (int i = 0; i < firmatari.length; i++) {
					FirmatarioType item = firmatari[i];
					
					String cognome = StringUtils.capitalize(item.getCognome()
						.substring(0, 1)) + item.getCognome().substring(1);
					String nome = StringUtils.capitalize(item.getNome().substring(0, 1))
						+ item.getNome().substring(1);
					String nominativo = new StringBuilder().append(cognome)
			    		.append(" ").append(nome).toString();
					
					// aggiorna i firmatati...
					SoggettoFirmatarioImpresaHelper soggetto = new SoggettoFirmatarioImpresaHelper();
					soggetto.setCognome(item.getCognome());
					soggetto.setNome(item.getNome());
					soggetto.setNominativo(nominativo);
					soggetto.setCodiceFiscale(item.getCodiceFiscaleFirmatario());
					soggetto.setCap(item.getResidenza().getCap());
					soggetto.setComune(item.getResidenza().getComune());
					soggetto.setIndirizzo(item.getResidenza().getIndirizzo());				
					soggetto.setNazione(item.getResidenza().getNazione());
					soggetto.setNumCivico(item.getResidenza().getNumCivico());
					soggetto.setProvincia(item.getResidenza().getProvincia());
					soggetto.setComuneNascita(item.getComuneNascita());
					soggetto.setDataNascita( sdf.format(item.getDataNascita().getTime()) );
					soggetto.setProvinciaNascita(item.getProvinciaNascita());
					soggetto.setSesso(item.getSesso());
					soggetto.setSoggettoQualifica(item.getQualifica());
					
					componentiRTI.addFirmatario(item, soggetto);
				}
			}
			
			// aggiorna l'offerta...
			offerta.setListaFirmatariMandataria(firmatariMandataria);

			// imposta l'id del firmatario selezionato...
			if (componentiRTI.size() > 0) {
				// il I elemento è sempre la mandataria...
				SoggettoImpresaHelper mandataria = componentiRTI.getFirmatario(componentiRTI.get(0));
			    for (int i = 0; i < offerta.getListaFirmatariMandataria().size(); i++) {
					if (offerta.getListaFirmatariMandataria().get(i).getNominativo().equalsIgnoreCase(mandataria.getNominativo())) {
					    offerta.setIdFirmatarioSelezionatoInLista(i);
					    break;
					}
			    }
			}			
    	} catch(Throwable e) {
    		ApsSystemUtils.logThrowable(e, null, "refreshFirmatari");
    	}
    }

    
    /**
     * Dato l'elenco dei prezzi unitari dell'asta elettronica 
     * sincronizza le vociDettaglio per poter generare correttamente 
     * il report dell'offerta d'asta     
     */
    private static void sincronizzaPrezziUnitariOfferta(
    		List<VoceDettaglioAstaType> vociAsta,
    		WizardOffertaEconomicaHelper offerta) 
    {    	
    	if(vociAsta != null) {
			List<VoceDettaglioOffertaType> vociDettaglioNoRibasso = 
				new ArrayList<VoceDettaglioOffertaType>();
			List<AttributoAggiuntivoOffertaType> attributiAggiuntivi = 
				new ArrayList<AttributoAggiuntivoOffertaType>();								
			List<VoceDettaglioOffertaType> vociDettaglio = 
	    		new ArrayList<VoceDettaglioOffertaType>();
			
			// ricalcola il totale dei prezzi unitari
			double tot = 0;
	    	for(int i = 0; i < vociAsta.size(); i++) {
	    		double qta = (vociAsta.get(i).getQuantita() != null ? 
	    					  vociAsta.get(i).getQuantita() : 0.0);
	    		double prz = (vociAsta.get(i).getAstePrezzoUnitario() != null ? 
	    				      vociAsta.get(i).getAstePrezzoUnitario() : 0.0);
	    		tot += qta * prz;	    		
	    		
	    		vociDettaglio.add( (VoceDettaglioOffertaType) vociAsta.get(i) );
	    		
	    		vociDettaglio.get(i).setPrezzoUnitario(vociAsta.get(i).getAstePrezzoUnitario());
	    		vociDettaglio.get(i).setImportoUnitario(vociAsta.get(i).getAsteImportoUnitario());
	    	}
	    	
	    	offerta.setImportoOfferto(tot);
	    	offerta.setAttributiAggiuntivi(attributiAggiuntivi);
			offerta.setVociDettaglioNoRibasso(vociDettaglioNoRibasso);			
	    	offerta.setVociDettaglio(vociDettaglio);
	    	
	    	// questo è per la corretta generazione del generazione del PDF...
	    	// PS: va fatto solo dopo setVociDettaglio(...)
	    	for(int i = 0; i < vociDettaglio.size(); i++) {
	    		offerta.getPrezzoUnitario()[i] = vociDettaglio.get(i).getPrezzoUnitario();
	    		offerta.getImportoUnitario()[i] = vociDettaglio.get(i).getImportoUnitario();
	    	}	    		
    	}
    }
    
}
