package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.GenPDFOffertaEconomicaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import org.apache.xmlbeans.XmlObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.ASTA })
public class GenPDFOffertaAstaAction extends GenPDFOffertaEconomicaAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 3605039063997442086L;

	@Override
	public String getUrlErrori() {
		return this.urlPage
				+ "?actionPath=/ExtStr2/do/FrontEnd/Aste/openPageFirmatari.action"	
				+ "&currentFrame=" + this.currentFrame;
	}

	/**
	 * costruttore del report 
	 */
	public GenPDFOffertaAstaAction() {
		super("OffertaAsta",
		      "/ReportOffertaEconomica");
	}

	@Override
	protected boolean reportInit() {
		boolean continua = true; 
	
		WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession();
		
		// BUG FIX: non viene passato il parametro da OpenPageFirmatariAction... 
		//          perche'?!? 
		//          trova il firmatariSelezionato in base a idFirmatarioSelezionatoInLista
		if (this.firmatarioSelezionato == null && helperAsta != null) {
			if(helperAsta.getOffertaEconomica().getIdFirmatarioSelezionatoInLista() >= 0 && 
			   helperAsta.getOffertaEconomica().getListaFirmatariMandataria().size() > 0) {
				String firmatarioSel = 
					helperAsta.getOffertaEconomica().getListaFirmatariMandataria()
						.get(helperAsta.getOffertaEconomica().getIdFirmatarioSelezionatoInLista()).getNominativo() 
					+ " - " + helperAsta.getOffertaEconomica().getIdFirmatarioSelezionatoInLista();
				this.firmatarioSelezionato = firmatarioSel;
			}
		}
	
		boolean userLogged = (null != this.getCurrentUser()
	        && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME));
				
		if (helperAsta == null || !userLogged) {
			// la sessione e' scaduta, occorre riconnettersi
			session.put(ERROR_DOWNLOAD, 
						this.getText("Errors.sessionExpired"));
			this.setTarget(INPUT);
			continua = false;
		} else if (this.firmatarioSelezionato == null) {
			session.put(ERROR_DOWNLOAD, 
						this.getText("Errors.firmatarioNotSet"));
			this.setTarget(INPUT);
			continua = false;
		} else {
			WizardOffertaEconomicaHelper helper = helperAsta.getOffertaEconomica();

			FirmatarioBean firmatario = null;
			if (helper.getComponentiRTI().size() == 0) {
				// Imposta il firmatario selezionato partendo da una stringa in formato
				//		"[descrizione firmatario] - [id firmatario]"  
				helper.setFirmatarioSelezionato(this.firmatarioSelezionato);
				firmatario = helper.getFirmatarioSelezionato();
			}
			
			// NB: il report per l'offerta d'asta e' replicato dal report 
			// dell'offerta economica !!!
			// il wizard dell'asta contiene un wizard per l'offerta economica
			// che viene inizializzato con i dati da passare al report
		}
			
		return continua;
	}

	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession();

		if(helperAsta != null) {
			WizardOffertaEconomicaHelper helper = helperAsta.getOffertaEconomica();
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
			WizardPartecipazioneHelper partecipazioneHelper = helperAsta.getPartecipazione();
			
			boolean isRTI = (helper.getComponentiRTI().size() > 0);
			
			params.put("LIBERO_PROFESSIONISTA", datiImpresaHelper.isLiberoProfessionista());
			params.put("TIMESTAMP", UtilityDate.convertiData( 
					this.ntpManager.getNtpDate(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			params.put("IS_RTI", isRTI);
			params.put("DENOMINAZIONE_RTI",partecipazioneHelper.getDenominazioneRTI());
			params.put("IMPNORIBASSO", helper.getGara().getImportoNonSoggettoRibasso());
			params.put("IMPSICUREZZA", helper.getGara().getImportoSicurezza());
			params.put("SICINC", helper.getGara().isOffertaComprensivaSicurezza());
			params.put("IMPONERIPROGETT", helper.getGara().getImportoOneriProgettazione());
			params.put("NOME_CLIENTE", (String) this.appParamManager
					.getConfigurationValue(AppParamManager.NOME_CLIENTE));
	
			if(helperAsta.isLottiDistinti()) {
				// DA FARE...
				params.put("CODICE_INTERNO_LOTTO", helperAsta.getAsta().getCodiceLotto());
				params.put("OGGETTO_LOTTO", helperAsta.getAsta().getCodiceLotto());
			}
			
			params.put("PUNTIORDINANTIVISIBILI", this.customConfigManager
						.isVisible("GARE-DOCUMOFFERTA", "PUNTIORDIISTR"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			params.put("DATA_RILANCIO", (String) sdf.format(helperAsta.getAsta().getDataUltimoRilancio().getTime()));
			params.put("TIPO_OFFERTA", (String) helperAsta.getAsta().getTipoOfferta().toString());	// 1=ribasso 2=importo
		}
	}

	@Override
	protected void reportCompleted() {
		try {
			WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession();

			if(SUCCESS.equals(this.getTarget()) && helperAsta != null) {
	
				// si marca che la rigenerazione del pdf non e' necessaria e
				// si setta l'uuid del file generato
				helperAsta.setPdfUUID(this.uuid);
				helperAsta.setRigenPdf(false);
	
				// si elimina l'eventuale precedente pdf allegato
				if (helperAsta.isDocOffertaPresente()) {
					helperAsta.deleteDocumentoOfferta(this, this.eventManager);
				}
	
				helperAsta.getDocumenti().setDatiModificati(false);
				helperAsta.setDatiModificati(false);
	
				// invia una comunicazione FS13 in stato BOZZA con 
				// un allegato XML contenente UUID del pdf generato...
				// La comunicazione FS13 e' un supporto alla comunicazione 
				// FS12 che verrà inviata al termine del wizard di invio 
				// dell'offerta d'asta.
				// NB: il collegamento tra FS13 ed FS12 è
				//     FS13.chiave3 = FS12.id
				ComunicazioniUtilities comunicazione = new ComunicazioniUtilities(
						this, 
						this.getRequest().getSession().getId());
				
				// invia la FS13 con l'xml dell'offerta d'asta in stato BOZZA 
				comunicazione.sendComunicazioneGenerazioneOffertaAsta(
						helperAsta,
						false);
				
				// salva in sessione...
				helperAsta.putToSession();
			}
			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "reportCompleted");
			session.put(ERROR_DOWNLOAD,
						"Si e' verificato un errore inaspettato durante l'elaborazione dell'operazione: "
						+ e.getMessage());
			this.setTarget(INPUT);
		}
	}

	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws Exception {
		XmlObject document = null;
		
		WizardOffertaAstaHelper helperAsta = WizardOffertaAstaHelper.fromSession();
		if(helperAsta != null) {
			document = super.generaXmlConDatiTipizzati(helperAsta.getOffertaEconomica());
		}
		
		return document;
	}	

}



