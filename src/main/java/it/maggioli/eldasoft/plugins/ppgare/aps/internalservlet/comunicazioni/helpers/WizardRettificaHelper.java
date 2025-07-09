package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 *  
 */
public class WizardRettificaHelper extends WizardNuovaComunicazioneHelper {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5342707446282490180L;
	
    public static enum FasiRettifica {
    	RICHIESTA,							// 10/15
    	ACCETTAZIONE,						// 11/16
    	RIFIUTO,							// 12/17
    	RETTIFICA							// 13/18
  	}
    
	/**
	 * costruttore 
	 */
	public WizardRettificaHelper() {
		super();
				
		// ridefinisci gli step del wizard
		STEP_TESTO_COMUNICAZIONE	= "testoRettifica";
		STEP_DOCUMENTI				= "documentiRettifica"; 
		STEP_INVIO_COMUNICAZIONE	= "inviaRettifica";		
	}
		
	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la corretta navigazione
	 */
	@Override
	public void fillStepsNavigazione() {
		this.stepNavigazione.push(STEP_TESTO_COMUNICAZIONE);
		
		if( !isRichiestaRettifica() ) {
			// lo step dei "Documenti" e' visibile solo in fase di rettifica
			// e NON in fase di richiesta rettifica
			this.stepNavigazione.push(STEP_DOCUMENTI);
		}
		
		this.stepNavigazione.push(STEP_INVIO_COMUNICAZIONE);
	}

	/**
	 * restituisce true se si tratta di una richiesta di rettifica 
	 */
	public boolean isRichiestaRettifica() {
		return (getModello() != null) &&
				(getModello() == PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_TEC
				|| getModello() == PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_ECO);
	}
	
	/**
	 * restituisce true se la richietsa di rettifica e' stata accettata da BO 
	 */
	public boolean isRichiestaRettificaAccettata() {
		return (getModello() != null) &&
				(getModello() == PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_TEC
				|| getModello() == PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_ECO);
	}
	
	/**
	 * restituisce true se la richietsa di rettifica e' stata rifiutata da BO 
	 */
	public boolean isRichiestaRettificaRifiutata() {
		return (getModello() != null) &&
				(getModello() == PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_TEC
				|| getModello() == PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_ECO);
	}
	
	/**
	 * restituisce true se si tratta dell'invio della rettifica
	 */
	public boolean isInvioRettifica() {
		return (getModello() != null) &&
				(getModello() == PortGareSystemConstants.RETTIFICA_BUSTA_TEC
				|| getModello() == PortGareSystemConstants.RETTIFICA_BUSTA_ECO);
	}

	/**
	 * restituisce il modello della richiesta di rettifica associato al tipo di busta 
	 */
	public static Long getModelloRichiestaRettifica(int tipoBusta) {
		Long modello = null;
		//if(tipoBusta != null)
			if(PortGareSystemConstants.BUSTA_TECNICA == tipoBusta) {
				modello = PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_TEC;
			} else if(PortGareSystemConstants.BUSTA_ECONOMICA == tipoBusta) {
				modello = PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_ECO;
			}
		return modello;
	}
	
	public static Long getModelloRettifica(int tipoBusta) {
		Long modello = null;
		//if(tipoBusta != null)
			if(PortGareSystemConstants.BUSTA_TECNICA == tipoBusta) {
				modello = PortGareSystemConstants.RETTIFICA_BUSTA_TEC;
			} else if(PortGareSystemConstants.BUSTA_ECONOMICA == tipoBusta) {
				modello = PortGareSystemConstants.RETTIFICA_BUSTA_ECO;
			}
		return modello;
	}

    /**
     * restituisce il modello della fase della richiesta di rettifica  
     */
    public static int getBustaFromModello(Long modello) {
    	//String tipoBusta = null;
    	int tipoBusta = 0;
    	if(modello != null) {
    		if(PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_TEC == modello.longValue() 
    		   || PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_TEC == modello.longValue()
    		   || PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_TEC == modello.longValue()
    		   || PortGareSystemConstants.RETTIFICA_BUSTA_TEC == modello.longValue())
    			tipoBusta = PortGareSystemConstants.BUSTA_TECNICA;
    		if(PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_ECO == modello.longValue()
    		   || PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_ECO == modello.longValue()
    		   || PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_ECO == modello.longValue()
    		   || PortGareSystemConstants.RETTIFICA_BUSTA_ECO == modello.longValue())
    			tipoBusta = PortGareSystemConstants.BUSTA_ECONOMICA;
    	}
    	return tipoBusta;
    }
    
    /**
     * restituisce il modello della fase della richiesta di rettifica  
     */
    public static long getFaseRettifica(int tipoBusta, FasiRettifica fase) {
    	if(PortGareSystemConstants.BUSTA_TECNICA == tipoBusta) {
    		if(fase == FasiRettifica.RICHIESTA) return PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_TEC;
    		if(fase == FasiRettifica.ACCETTAZIONE) return PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_TEC;
    		if(fase == FasiRettifica.RIFIUTO) return PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_TEC;
    	    if(fase == FasiRettifica.RETTIFICA) return PortGareSystemConstants.RETTIFICA_BUSTA_TEC;
    	} else if(PortGareSystemConstants.BUSTA_ECONOMICA == tipoBusta) {
    		if(fase == FasiRettifica.RICHIESTA) return PortGareSystemConstants.RICHIESTA_RETTIFICA_BUSTA_ECO;
    		if(fase == FasiRettifica.ACCETTAZIONE) return PortGareSystemConstants.ACCETTAZIONE_RETTIFICA_BUSTA_ECO;
			if(fase == FasiRettifica.RIFIUTO) return PortGareSystemConstants.RIFIUTO_RETTIFICA_BUSTA_ECO;
			if(fase == FasiRettifica.RETTIFICA) return PortGareSystemConstants.RETTIFICA_BUSTA_ECO;
    	}
    	return 0;
    }
    
	/**
	 * crea un helper per richiesta/invio di rettifica
	 */
	private static WizardRettificaHelper newWizard(
			boolean richiestaRettifica
			, String username
			, String codiceGara
			, String codiceLotto
			, String progressivoOfferta
			, Long tipoBusta
			, int operazione
	) throws ApsException {
		WizardRettificaHelper helper = null;
		if(tipoBusta == null) {
			throw new ApsException("WizardRettificaHelper.newWizard(...) tipo busta mancante");
		} else {
			try {
				helper = new WizardRettificaHelper();
				
				BaseAction action = (BaseAction) ActionContext.getContext().getActionInvocation().getAction();
				
				String oggetto = null;
				String testo = null;
				Long modello = null;
				String lblOggetto = null;
				String lblTesto = null;
				if(richiestaRettifica) {
					// richiesta rettifica
					if(tipoBusta == BustaGara.BUSTA_TECNICA) 
						lblOggetto = "LABEL_RETTIFICA_RICHIESTA_OGGETTO_TEC";
					if(tipoBusta == BustaGara.BUSTA_ECONOMICA)
						lblOggetto = "LABEL_RETTIFICA_RICHIESTA_OGGETTO_ECO";

					lblTesto = "LABEL_RETTIFICA_RICHIESTA_TESTO"; 
					
					modello = WizardRettificaHelper.getModelloRichiestaRettifica(tipoBusta.intValue());
				} else {
					// invio rettifica
					if(tipoBusta == BustaGara.BUSTA_TECNICA)
						lblOggetto = "LABEL_RETTIFICA_INVIO_OGGETTO_TEC";
					if(tipoBusta == BustaGara.BUSTA_ECONOMICA)
						lblOggetto = "LABEL_RETTIFICA_INVIO_OGGETTO_ECO";
					
					lblTesto = "LABEL_RETTIFICA_INVIO_TESTO"; 
	
					modello = WizardRettificaHelper.getModelloRettifica(tipoBusta.intValue());
					
					// calcola la chiave di sessione e la chiave di cifratura per la comunicazione di rettifica
					helper.getDocumenti().refreshChiaveSessione(
							username
							, codiceGara
							, codiceLotto
							, tipoBusta.intValue()
					);
				}
				
				oggetto = action.getI18nLabelFromDefaultLocale(lblOggetto);
				if(StringUtils.isNotEmpty(codiceLotto)) 
					oggetto = oggetto + " (" + action.getI18nLabelFromDefaultLocale("LABEL_LOTTO") + " " + codiceLotto + ")";
					
				testo = MessageFormat.format(action.getI18nLabelFromDefaultLocale(lblTesto), 
						 							new Object[] { BustaGara.getDescrizioneBusta(tipoBusta.intValue()).toLowerCase()
						 										   , codiceGara });
				
				helper.setOggetto(oggetto);
				helper.setTesto(testo);
				helper.setCodice(codiceGara);
				helper.setCodice2(codiceLotto);
				helper.setProgressivoOfferta(progressivoOfferta);
				helper.setModello(modello);
				helper.setTipoBusta(new Long(tipoBusta));
				helper.setOperazione(operazione);
				helper.setDataScadenza(null);
				
			} catch (Exception ex) {
				ApsSystemUtils.getLogger().error("WizardRettificaHelper.newWizard(...)", ex);
				throw new ApsException(ex.getMessage());
			}
		}
		return helper;
	}
	
	public static WizardRettificaHelper creaRichiestaRettifica(
			String username
			, String codiceGara
			, String codiceLotto
			, String progressivoOfferta
			, Long tipoBusta
			, int operazione
	) throws ApsException {
		return newWizard(true, username, codiceGara, codiceLotto, progressivoOfferta, tipoBusta, operazione);
	}
	
	public static WizardRettificaHelper creaInvioRettifica(
			String username
			, String codiceGara
			, String codiceLotto
			, String progressivoOfferta
			, Long tipoBusta
			, int operazione
	) throws ApsException {
		return newWizard(false, username, codiceGara, codiceLotto, progressivoOfferta, tipoBusta, operazione);
	}
	
}
