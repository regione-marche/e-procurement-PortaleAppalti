package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSessionBindingEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Contenitore dei dati del wizard dell'offerta di un'asta
 *
 * @author 
 */
public class WizardOffertaAstaHelper extends WizardOffertaHelper { 
	/**
	 * UID
	 */
	private static final long serialVersionUID = -146133273707377947L;

	/**
	 * definizione degli step del wizard   
	 */
	public static final String STEP_DATI_OPERATORE 	= "datioperatore";
	public static final String STEP_DATI_OFFERTA 	= "datiofferta";
	public static final String STEP_FIRMATARI		= "firmatari";
	public static final String STEP_UPLOAD_PDF 		= "uploadpdf";
	public static final String STEP_RIEPILOGO 		= "riepilogo";
	
	public String getSTEP_DATI_OPERATORE() { return STEP_DATI_OPERATORE; }
	public String getSTEP_DATI_OFFERTA() { return STEP_DATI_OFFERTA; }
	public String getSTEP_FIRMATARI() { return STEP_FIRMATARI; }
	public String getSTEP_UPLOAD_PDF() { return STEP_UPLOAD_PDF; }
	public String getSTEP_RIEPILOGO() { return STEP_RIEPILOGO; }
	
	
	public static final String PDF_OFFERTA_ASTA_DESCRIPTION = "Conferma offerta finale"; 
	 
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
	 * Indica se ci sono lotti distinti 
	 */
	private boolean lottiDistinti;
	
	/**
	 * lotti associati alla gara 
	 */
	private LottoGaraType[] lotti;
		

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
		this.partecipazione = new WizardPartecipazioneHelper();
		this.offertaEconomica = new WizardOffertaEconomicaHelper();
		this.documenti = this.offertaEconomica.getDocumenti();
		this.lottiDistinti = false;
		this.lotti = null;
		this.idComunicazione = null;	// idcom di FS13
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		super.valueBound(arg0);
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0 il session binding event
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		try {
			super.valueUnbound(arg0);
			
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
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().warn("WizardOffertaAstaHelper.valueUnbound", t);
		}
	}

	public boolean isDatiInviati() {
		return datiInviati;
	}

	public void setDatiInviati(boolean datiInviati) {
		this.datiInviati = datiInviati;
	}	

	public DettaglioAstaType getAsta() {
		return asta;
	}

	public void setAsta(DettaglioAstaType asta) {
		this.asta = asta;
	}

	public List<DettaglioClassificaType> getClassifica() {
		return classifica;
	}

	public void setClassifica(List<DettaglioClassificaType> classifica) {
		this.classifica = classifica;
	}
	
	public List<DettaglioRilancioType> getRilanci() {
		return rilanci;
	}
	
	public void setRilanci(List<DettaglioRilancioType> rilanci) {
		this.rilanci = rilanci;
	}
	
	public double getImportoOfferta() {
		return importoOfferta;
	}
	
	public void setImportoOfferta(double importoOfferta) {
		this.importoOfferta = importoOfferta;
	}

	public WizardOffertaEconomicaHelper getOffertaEconomica() {
		return offertaEconomica;
	}
	
	public void setOffertaEconomica(WizardOffertaEconomicaHelper offertaEconomica) {
		this.offertaEconomica = offertaEconomica;
	}
	
	public WizardPartecipazioneHelper getPartecipazione() {
		return partecipazione;
	}

	public void setPartecipazione(WizardPartecipazioneHelper partecipazione) {
		this.partecipazione = partecipazione;
	}

	public boolean isLottiDistinti() {
		return lottiDistinti;
	}
	
	public void setLottiDistinti(boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}
	
	public LottoGaraType[] getLotti() {
		return lotti;
	}
	
	public void setLotti(LottoGaraType[] lotti) {
		this.lotti = lotti;
	}
	
	public String getPdfUUID() {
		return (this.offertaEconomica != null ? this.offertaEconomica.getPdfUUID() : null);
	}	

	public void setPdfUUID(String pdfUUID) {
		if(this.offertaEconomica != null) {
			this.offertaEconomica.setPdfUUID(pdfUUID);
		}
	}

	public boolean isDatiModificati() {
		return (this.offertaEconomica != null ? this.offertaEconomica.isDatiModificati() : false);
	}

	public void setDatiModificati(boolean datiModificati) {
		if(this.offertaEconomica != null) {
			this.offertaEconomica.setDatiModificati(datiModificati);
		}
	}
	
	public boolean isRigenPdf() {
		boolean rigenera = false; 
		if(this.offertaEconomica != null) {
			rigenera = (this.offertaEconomica.isRigenPdf() ||
					    this.offertaEconomica.getPdfUUID() == null);
		}
		return rigenera; 
	}

	public void setRigenPdf(boolean rigenPdf) {
		if(this.offertaEconomica != null) {
			this.offertaEconomica.setRigenPdf(rigenPdf);
		}
	}
	
	public WizardDocumentiBustaHelper getDocumenti() {
		documenti = (this.offertaEconomica != null ? this.offertaEconomica.getDocumenti() : null);
		return documenti;
	} 
	
	/**
	 * ...
	 */
	public byte[] getChiaveSessione() {
		return (this.getDocumenti() != null ? this.getDocumenti().getChiaveSessione() : null);
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

	private static final WizardOffertaAstaHelper factory = new WizardOffertaAstaHelper();

    /**
     * Recupera l'helper dell'asta dalla sessione 
     */
    public static WizardOffertaAstaHelper fromSession() {
    	return (WizardOffertaAstaHelper) factory.getFromSession();
    }
    
	/**
	 * Crea l'helper per il wizard che gestisce la conferma dell'offerta d'asta 
	 */
	public static WizardOffertaAstaHelper getHelper(
			EncodedDataAction action,
			String codiceGara, 
			String codiceLotto) throws ApsException 
	{	
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
					if(codiceGara != null) {
						if(helper != null) { 
							String cod = (helper.getAsta() != null && helper.getAsta().getCodice() != null ? helper.getAsta().getCodice() : "");  
							String codLotto = (helper.getAsta() != null && helper.getAsta().getCodiceLotto() != null ? helper.getAsta().getCodiceLotto() : "");						 
							if(!cod.equalsIgnoreCase(codiceGara) ||
							   (cod.equalsIgnoreCase(codiceGara) && !codLotto.equalsIgnoreCase(codiceLotto))) {
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

						String progOfferta = null;
						if(StringUtils.isNotEmpty(codiceLotto)) {
							// recupera il "progressivoOfferta" dalla comunicazione (PROCESSATA->6) relativa al lotto...
							// ogni lotto puo' essere associato ad un unico plico offerta !!!
							ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
							IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
							
							DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
							filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
							filtri.setChiave1(username);
							filtri.setChiave2(codiceLotto);
							filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
							filtri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
							
							List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(filtri);
							if(comunicazioni != null && comunicazioni.size() > 0) {
								progOfferta = comunicazioni.get(0).getChiave3();
							}
						}
						
						// crea un nuovo helper...
						helper = new WizardOffertaAstaHelper();
						helper.getDocumenti().setUsername(username);
						
			    		// recupera dalla comunicazione FS10, FS11 i componenti dell'RTI...
			    		List<String> stati = new ArrayList<String>();
			    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
			    		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
			    		
			    		GestioneBuste buste = new GestioneBuste(
			    				username,
			    				codiceGara,
			    				progOfferta,
			    				PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA); 
			    		buste.get(stati);
			    		
			    		BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
			    		WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper(); 
			    		WizardDatiImpresaHelper impresa = buste.getImpresa();
			    		DettaglioGaraType dettGara = buste.getDettaglioGara();
			    		IBandiManager bandiManager = buste.getBandiManager();
			    		IComunicazioniManager comunicazioniManager = buste.getComunicazioniManager();
			    		
			    		// recupera i dati relativi all'asta...
						IAsteManager asteManager = (IAsteManager) ApsWebApplicationUtils
							.getBean(PortGareSystemConstants.ASTE_MANAGER,
									 ServletActionContext.getRequest());

						boolean lottiDistinti = ( 
							dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
							dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO
						);
						
						LottoGaraType[] lottiGara = null;
						if(lottiDistinti) {
							lottiGara = bandiManager.getLottiGara(codiceGara);
						}
						
						String codice = (StringUtils.isNotEmpty(codiceLotto) ? codiceLotto : codiceGara);
						
						// recupera i detagli dell'asta...
						DettaglioAstaType asta = asteManager.getDettaglioAsta(codice, username);
	
						// recupera l'elenco dei rilanci dell'utente per l'asta...
						List<DettaglioRilancioType> rilanci = asteManager.getElencoRilanci(
								codiceGara, 
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
								codiceGara, 
								codiceLotto, 
								username, 
								asta.getFase().toString());

						// i dati dell'offerta d'asta sono simili a quelli dell'offerta economica, quindi
						// crea helper dei dati generali dell'offerta...
						WizardOffertaEconomicaHelper offerta = buste.getBustaEconomica().getHelper();
						offerta.setGara((dettGara != null ? dettGara.getDatiGeneraliGara() : null));
						offerta.setProgressivoOfferta(progOfferta);
						offerta.setImpresa(impresa);
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

						// se l'asta e' a prezzi unitari, recupera il dettaglio dell'ultimo rilancio...
						if(rilanci != null && rilanci.size() > 0) {
							List<VoceDettaglioAstaType> vociAsta = asteManager.getPrezziUnitariRilancio(
									codiceGara,
									codiceLotto, 
									username,
									rilanci.get(rilanci.size() - 1).getId());
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
						    	
						    	// questo e' per la corretta generazione del generazione del PDF...
						    	// PS: va fatto solo dopo setVociDettaglio(...)
						    	for(int i = 0; i < vociDettaglio.size(); i++) {
						    		offerta.getPrezzoUnitario()[i] = vociDettaglio.get(i).getPrezzoUnitario();
						    		offerta.getImportoUnitario()[i] = vociDettaglio.get(i).getImportoUnitario();
						    	}
					    	}
				    	}
				    	
						// FS13: carica la comunicazione con i dati dell'offerta dell'asta (xml)
						// verifica se esiste la comunicazione di supporto FS13
						// per le offerte d'asta in stato BOZZA (o PROCESSATA)...
						stati = new ArrayList<String>(); 
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
						stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
						
						DettaglioComunicazioneType dettComunicazioneFS13 = ComunicazioniUtilities.retrieveComunicazioneConStati(
								comunicazioniManager,
								username, 
								codiceLotto, 
								null,
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA, 
								stati);
						
						boolean offertaInviata = false;
						String idComunicazioneFS12 = null;
						if(dettComunicazioneFS13 != null) {
							offertaInviata = !(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(dettComunicazioneFS13.getStato()));
							idComunicazioneFS12 = dettComunicazioneFS13.getChiave3();
							
							// carica la i dati dell'offerta d'asta come se fosse una busta economica
				    		// (i dati dell'offerta d'asta sono simili a quelli dell'offerta economica)
							// carica la comunicazione FS13 e popola l'helper con il documento XML
							BustaEconomica bustaAsta = new BustaEconomica(buste);
							
							bustaAsta.getComunicazioneFlusso().getDettaglioComunicazione().setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_OFFERTA_ASTA);					
						    bustaAsta.setProgressivoOfferta(null);// FS13 non ha progressivo
						    bustaAsta.get(stati, (stati == null), codiceLotto, null);
							
							helper.setPdfUUID(bustaAsta.getHelper().getPdfUUID());
							offerta.setPdfUUID(bustaAsta.getHelper().getPdfUUID());
							offerta.setComponentiRTI(bustaAsta.getHelper().getComponentiRTI());
						}
						
						// FS12: viene inviata solo all'invio definitivo dell'offerta d'asta
						// l'allegato PDF dell'offerta d'asta viene caricato nella comunicazione FS12...
						// recupera l'allegato dalla FS12 con l'id memorizzato nella FS13.COMKEY3 !!!
						if(StringUtils.isNotEmpty(idComunicazioneFS12)) {
							ComunicazioneType comunicazioneFS12 = comunicazioniManager.getComunicazione(
									CommonSystemConstants.ID_APPLICATIVO, 
									Long.parseLong(idComunicazioneFS12));
							
							AllegatoComunicazioneType pdfOffertaAsta = (
									comunicazioneFS12 != null && comunicazioneFS12.getAllegato() != null && 
									comunicazioneFS12.getAllegato().length > 0 
									? comunicazioneFS12.getAllegato()[0] 
									: null);
							
							if(pdfOffertaAsta != null) {
							    try {
							    	// recupera il pdf dell'offerta d'asta dalla comunicazione FS12...
							    	// crea il file allegato nell'area temporanea...
							    	String fn = StrutsUtilities.getTempDir(ServletActionContext.getServletContext()).getAbsolutePath()
												+ File.separatorChar + pdfOffertaAsta.getNomeFile();
							    	File filePdf = new File(fn); 
							    	FileOutputStream fos = new FileOutputStream(filePdf);
							        fos.write(pdfOffertaAsta.getFile());
							        fos.close();
							        helper.tempFiles.add(filePdf);
							        
							    	// aggiungi l'allegato ai documenti dell'offerta...
							        // e marca che e' presente il documento pdf dell'offerta
							        offerta.getDocumenti().addDocUlteriore(
							        		pdfOffertaAsta.getDescrizione(),
							        		filePdf,
							        		null,
							        		pdfOffertaAsta.getNomeFile(),
							        		null);
							        offerta.getDocumenti().setDocOffertaPresente(true);
									
									// imposta l'ID della comunicazione FS12
									offerta.setIdComunicazione(comunicazioneFS12.getDettaglioComunicazione().getId());
							    } catch (Exception e) {
							    	ApsSystemUtils.getLogger().error("getHelper", e);
							    }
							}
						}
						
						// aggiorna i dati dell'helper...
						helper.setAsta(asta);
						helper.setOffertaEconomica(offerta);
						helper.setDocumenti(offerta.getDocumenti()); 
						helper.setPartecipazione(partecipazione);
						helper.setIdComunicazione(dettComunicazioneFS13 != null ? dettComunicazioneFS13.getId() : null);
						helper.setDatiInviati(offertaInviata);
						helper.setLottiDistinti(lottiDistinti);
						helper.setLotti(lottiGara);
						helper.setClassifica(classifica);
						helper.setRilanci(rilanci);
						helper.setImportoOfferta(importoOfferta);
						
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
     * popola l'helper recuperando i dati dal documento xml della comunicazione
     */
	@Override
	public void popolaFromXml(BustaDocumenti busta) throws Throwable {
		// copia i dati dall'helper della busta
		BustaEconomica bustaEco = (BustaEconomica)busta;
		this.pdfUUID = bustaEco.getHelper().getPdfUUID();
		this.offertaEconomica = bustaEco.getHelper();
	}
	
	/**
	 * genera il documento xml dell'asta (si riutilizza la generazione dell'offerta ecomomica) 
	 */
	public XmlObject getXmlDocument(XmlObject document, boolean attachFileContents, boolean report) 
		throws IOException, GeneralSecurityException 
	{
		return this.offertaEconomica.getXmlDocument(document, attachFileContents, report);
	}

	/**
	 * verifica se e' presente il documento dell'offerta d'asta 
	 */
	public boolean isDocOffertaPresente() {
    	// marca se e' presente il documento per l'offerta
		this.documenti.setDocOffertaPresente(false);
		long idOfferta = (this.idOfferta != null ? this.idOfferta.longValue() : -1);
		for (Attachment attachment : documenti.getAdditionalDocs()) {
			if(attachment.getDesc() != null
			   && PDF_OFFERTA_ASTA_DESCRIPTION.equalsIgnoreCase(attachment.getDesc()))
			{
				this.documenti.setDocOffertaPresente(true);
				break;
			}
		}
		return this.documenti.isDocOffertaPresente();
	}

	/**
	 * elimina il documento dell'offerta d'asta 
	 */
	public void deleteDocumentoOfferta(
			BaseAction action,
			IEventManager eventManager) 
	{
		Event evento = null;
		String message = "";
		if(action != null && eventManager != null) {
			evento = new Event();
		}
		
		WizardDocumentiBustaHelper documenti = this.getDocumenti();
		boolean offertaRimossa = false;
		
		//if(this.idOfferta != null && this.idOfferta.longValue() > 0) {
			for (int i = 0; i < documenti.getAdditionalDocs().size() && !offertaRimossa; i++) {
				if(documenti.getAdditionalDocs().get(i) != null
				   && PDF_OFFERTA_ASTA_DESCRIPTION.equalsIgnoreCase(documenti.getAdditionalDocs().get(i).getDesc()))
				{
					// traccia l'evento di eliminazione di un file...
					if(evento != null) {
						message += "cancellazione forzata offerta per variazione dati inseriti, " +
								   "file=" + documenti.getAdditionalDocs().get(i).getFileName() + ", " +
							       "dimensione=" + documenti.getAdditionalDocs().get(i).getSize() + "KB";
					}
					
					// elimina il documento ed il file associato (in chiaro o cifrato)
					documenti.removeDocUlteriore(i);
					documenti.setDocOffertaPresente(false);
					offertaRimossa = true;
				}
			}
		//}
		
		if(evento != null && offertaRimossa) {
			evento.setUsername(action.getCurrentUser().getUsername());
			evento.setDestination(documenti.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(action.getCurrentUser().getIpAddress());
			evento.setSessionId(action.getRequest().getSession().getId());
			evento.setMessage("Busta economica : " + message);
			eventManager.insertEvent(evento);
		}
	}

}
