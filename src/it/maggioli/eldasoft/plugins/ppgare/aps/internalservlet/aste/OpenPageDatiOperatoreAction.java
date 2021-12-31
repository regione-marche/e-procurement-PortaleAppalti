package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di apertura della pagina di gestione ...
 *
 * @author ...
 */
public class OpenPageDatiOperatoreAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;	

	private String codice;
	private String codiceLotto;
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	private String page;

    public void setPage(String page) {
    	this.page = page;
    }    
    
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}

	/**
	 * ... 
	 */
	public String openPage() {
		this.setTarget(SUCCESS);
		try {			
			// rileggi dalla session o reinizializza l'helper dell'asta...
			// verifica se l'helper corrisponde alla gara/asta richiesta
			// ed eventualmente lo ricrea utilizzando i parametri "codice", "codiceLotto" 
			// passati da altre action in chain 
			//  - confirmAction
			//  - riepilogoAction
			WizardOffertaAstaHelper helper = WizardOffertaAstaHelper
				.getHelper(this, this.codice, this.codiceLotto);
			
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi...
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// la sessione è attiva...
				
				// verifica se l'utente ha effettuato almeno 1 rilancio...
				boolean rilanciEffettuati = (
						helper.getAsta().getDataUltimoRilancio() != null &&
						((helper.getAsta().getImportoUltimoRilancio() != null && helper.getAsta().getImportoUltimoRilancio() > 0) ||
						 (helper.getAsta().getRibassoUltimoRilancio() != null && helper.getAsta().getRibassoUltimoRilancio() != 0))
				);
					
				if(!rilanciEffettuati) {
					// nessun rilancio effettuato...
					// ...vai alla pagina di riepilogo					
					this.setTarget("successRiepilogo");
				} else {
					this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 		 WizardOffertaAstaHelper.STEP_DATI_OPERATORE);
					
					// metti in sessione l'helper relativo all'impresa...
					// NB: serve per la gestione del wizard che modifica i dati  
					//     anagrafici dell'impresa!!!
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, 
									 helper.getOffertaEconomica().getImpresa());
				}
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "openPage");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		
		return this.getTarget();
	}
	
}
