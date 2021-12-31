package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di apertura della pagina di gestione dei dati di una RTI.
 *
 * @author Stefano.Sabbadin
 */
public class OpenPageRTIAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private IComunicazioniManager comunicazioniManager;
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	private String page;

	public String getPage() {
		return page;
	}

	/**
	 * true se impresa si presenta come mandataria in una RTI, false altrimenti
	 */
	private Integer rti;
	
	private String denominazioneRTI;
	
	private Boolean	plicoImpresaSingolaPresente;

	
	public Integer getRti() {
		return rti;
	}
	
	public void setRti(Integer rti) {
		this.rti = rti;
	}

	public String getDenominazioneRTI() {
		return denominazioneRTI;
	}

	public void setDenominazioneRTI(String denominazioneRTI) {
		this.denominazioneRTI = denominazioneRTI;
	}
	
	public Boolean getPlicoImpresaSingolaPresente() {
		return plicoImpresaSingolaPresente;
	}

	public void setPlicoImpresaSingolaPresente(Boolean plicoImpresaSingolaPresente) {
		this.plicoImpresaSingolaPresente = plicoImpresaSingolaPresente;
	}

	/**
	 * ...
	 */
	public String openPage() {
		String target = SUCCESS;
		
		IRaggruppamenti helper = this.getSessionHelper();
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) helper;
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				// la sessione non e' scaduta, per cui proseguo regolarmente
				this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
								 PortGareSystemConstants.WIZARD_PAGINA_RTI);

				this.plicoImpresaSingolaPresente = false;
				boolean forzaRti = false;
				
				// per le gare a lotti verifica la configurazione dell'RTI
				if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
					this.verificaFormaPartecipazione(partecipazioneHelper);
					
					// NB: 
					// in caso di gara a lotti se il progressivo offerta > 1
					// allora l'inserimento non puo' essere come singola
					// ma deve essere come rti !!!
					forzaRti = (str2Long(partecipazioneHelper.getProgressivoOfferta()) > 1);
				}
				
				// predisponi lo step per l'inserimento di una rti
				if (!PortGareSystemConstants.WIZARD_PAGINA_RTI.equals(this.page) ||
				    this.plicoImpresaSingolaPresente ||
				    forzaRti) 
				{
					this.rti = (helper.isRti() ? 1 : 0);
					this.denominazioneRTI = helper.getDenominazioneRTI();
				}
				
				// lo step STEP_COMPONENTI dipende dinamicamente dalla pagina JSP
				// abilita sempre lo step dei componenti in "OpenPageRTIAction"  
				// mentre in "ProcessPageRTIAction" viene deciso se lo step componenti
				// è abilitato nella navigazione del wizard 
				partecipazioneHelper.abilitaStepNavigazione(
						WizardPartecipazioneHelper.STEP_COMPONENTI, true);
				
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "openPage");
				this.addActionError(this.getText("Errors.unexpected"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		return target;
	}

	/**
	 * Ritorna l'helper in sessione utilizzato per la memorizzazione dei dati
	 * sulla partecipazione in RTI.
	 * 
	 * @return helper contenente i dati per la gestione di RTI e componenti
	 */
	protected IRaggruppamenti getSessionHelper() {
		return (WizardPartecipazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
	}

	/**
	 * Verifica per le gare a lotti come abilitare il tipo di partecipazione 
	 * (singola o rti) in base alle altre offerte associate alla gara 
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	private void verificaFormaPartecipazione(WizardPartecipazioneHelper helper) 
		throws ApsException, XmlException 
	{
		// per gare a lotti 
		// NON e' consentito presentare 1 sola partecipazione come singola impresa
		// per gare ristrette, se in prequalifica non c'e' la domanda come partecipazione singola
		// allora in offerta NON e' possibile presentare offerta come singola
		boolean domandaPartecipazione = (helper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		boolean invioOfferta = !domandaPartecipazione;
		
		String tipoComunicazione = domandaPartecipazione
				? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT
				: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
		
		boolean ristretta = (2 == helper.getIterGara() || 4 == helper.getIterGara());
		
		String progressivoOfferta = helper.getProgressivoOfferta();
		
		// recupera l'elenco delle comunicazioni FS11/FS10
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
		criteriRicerca.setChiave2(helper.getIdBando());
		//criteriRicerca.setChiave3(*);
		//criteriRicerca.setStato(*);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		
		boolean esistePartecipazioneSingola = false;
		if(comunicazioni != null && comunicazioni.size() > 0) {
			for(int i = 0; i < comunicazioni.size(); i++) {
				if( !progressivoOfferta.equals(comunicazioni.get(i).getChiave3()) ) {
				
					ComunicazioneType comunicazione = this.comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
									  	  comunicazioni.get(i).getId());
					
					WizardPartecipazioneHelper p = new WizardPartecipazioneHelper(comunicazione);
					if( !p.isRti() ) {
						// esiste una comunicazione con partecipazione impresa singola
						// quindi non e' possibile presentarne un'altra come singola
						esistePartecipazioneSingola = true;
						break;
					}
				}
			}
		}
		
		// per le gare ristrette inoltre se in prequalifica 
		// non c'e' la domanda come singola non e' possibile 
		// presentare 1 nuova offerta come singola!!!
		boolean esistePrequalificaSingola = false;
		if(ristretta && invioOfferta) {
			criteriRicerca = new DettaglioComunicazioneType();
			criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
			criteriRicerca.setChiave2(helper.getIdBando());
			criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
			comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteriRicerca);
			
			if(comunicazioni != null && comunicazioni.size() > 0) {
				for(int i = 0; i < comunicazioni.size(); i++) {
					//if( !progressivoOfferta.equals(comunicazioni.get(i).getChiave3()) ) {
					
					ComunicazioneType comunicazione = this.comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
									  	  comunicazioni.get(i).getId());
					
					WizardPartecipazioneHelper p = new WizardPartecipazioneHelper(comunicazione);
					if( !p.isRti() ) {
						// esiste una comunicazione con partecipazione impresa singola
						// quindi e' possibile presentare offerta come singola
						esistePrequalificaSingola = true;
						break;
					}
					//}
				}
			}
		}
	
		// verifica come configurare il tipo di partecipazione...
		boolean abilitaSoloRti = false;
		
		// aperta, negoziata, ristrette...
		if(esistePartecipazioneSingola) {
			// esiste gia' un'altra partecipazione come singola
			// quindi la partecipazione corrente puo' avere solo offerta in rti
			abilitaSoloRti = true;
			this.plicoImpresaSingolaPresente = true;
		}
		
		// ristretta in fase di offerta...
		if(ristretta && invioOfferta) {
			if(!esistePrequalificaSingola) {
				// NON esiste una domanda di prequalifica come singola (FS10)
				// quindi la partecipazione corrente puo' avere solo offerta in rti
				abilitaSoloRti = true;
			}
		}
		
		// se il progressivo e' > 1 presenta solo come rti...
		if(str2Long(progressivoOfferta) > 1) {
			abilitaSoloRti = true;
		}
		
		helper.setDenominazioneRTIReadonly(!helper.isEditRTI());
		if(abilitaSoloRti) {
			helper.setRti(true);
			helper.setEditRTI(false);
			helper.setDenominazioneRTIReadonly(false);
		}
	}

	private long str2Long(String value) {
		try {
			return Long.parseLong(value);
		} catch(Exception e) {
			return 0;
		}
	}

}
