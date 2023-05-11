package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
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
	private static final long serialVersionUID = -6551609577540320576L;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	
	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	@Validate(EParamValidation.GENERIC)
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
			// rileggi l'helper dell'asta...
			// verifica se l'helper corrisponde alla gara/asta richiesta
			// ed eventualmente lo ricrea utilizzando i parametri "codice", "codiceLotto" 
			// passati da altre action in chain: 
			//  - confirmAction
			//  - riepilogoAction
			// NB: alla chiusura dell'asta, quando si passa alla faase di conferma dell'offerta
			//	   si ricrea l'helper in modo da rileggere tutti i dati definiti da BO come
			//	   eventuali documenti richiesti
			new WizardOffertaAstaHelper().removeFromSession();
			WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.getHelper(
						this, 
						this.codice, 
						this.codiceLotto);
			
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi...
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// la sessione e' attiva...
				
				// se non ci sono rilanci esiste sempre il rilancio generato dall'offerta di gara
				if(helper.getRilanci() != null && helper.getRilanci().size() > 0) {
					if(helper.getAsta().getDataUltimoRilancio() == null ||
					   (helper.getAsta().getImportoUltimoRilancio() == null && helper.getAsta().getRibassoUltimoRilancio() == null))
					{
						// nessun rilancio presente !!!
						helper.getAsta().setDataUltimoRilancio( helper.getAsta().getDataOraApertura() );
						helper.getAsta().setImportoUltimoRilancio( helper.getRilanci().get(0).getImporto() );
						helper.getAsta().setRibassoUltimoRilancio( helper.getRilanci().get(0).getRibasso() );
					}
				}
					 
				// verifica se l'utente ha effettuato almeno 1 rilancio...
				boolean rilanciEffettuati = (
						helper.getAsta().getDataUltimoRilancio() != null &&
						((helper.getAsta().getImportoUltimoRilancio() != null && helper.getAsta().getImportoUltimoRilancio() > 0) ||
						 (helper.getAsta().getRibassoUltimoRilancio() != null && helper.getAsta().getRibassoUltimoRilancio() != 0))						
				);				
					
				if( !rilanciEffettuati ) {
					// nessun rilancio effettuato...
					// ...vai alla pagina di riepilogo
					this.addActionError(this.getText("Errors.nessunRilancio"));
					this.setTarget("successRiepilogo");
				} else {
					// vai al wizard per la generazione del PDF e per l'invio di FS12+FS13
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
