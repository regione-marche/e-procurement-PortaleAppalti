package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 * 
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageOffertaTecnicaAction extends AbstractOpenPageAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 7409866432236735561L;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	private int operazione;

	@Validate(EParamValidation.GENERIC)
	private String[] criterioValutazione;
	
	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }
	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }
	public int getBUSTA_TECNICA() { return PortGareSystemConstants.BUSTA_TECNICA; }
	public int getBUSTA_PRE_QUALIFICA() { return PortGareSystemConstants.BUSTA_PRE_QUALIFICA; }
	
	public String getSTEP_OFFERTA() { return WizardOffertaTecnicaHelper.STEP_OFFERTA; }
	public String getSTEP_SCARICA_OFFERTA() { return WizardOffertaTecnicaHelper.STEP_SCARICA_OFFERTA; }	
	public String getSTEP_DOCUMENTI() { return WizardOffertaTecnicaHelper.STEP_DOCUMENTI; }
		
	public String getCodice() {
		return codice;
	}
	
	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getCodiceGara() {
		return codiceGara;
	}
	
	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}
	
	public int getOperazione() {
		return operazione;
	}
	
	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}
	
	public String[] getCriterioValutazione() {
		return criterioValutazione;
	}
	
	public void setCriterioValutazione(String[] criterioValutazione) {
		this.criterioValutazione = criterioValutazione;
	}
	
	/**
	 * ...
	 */
	@Override
	public String openPage() {		
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper(); 
		
		// --- CRITERI DI VALUTAZIONE ---
		if(helper.isCriteriValutazioneVisibili()) {
			if(this.criterioValutazione == null && helper.getListaCriteriValutazione() != null) {
				this.criterioValutazione = new String[helper.getListaCriteriValutazione().size()];
				for(int i = 0; i < helper.getListaCriteriValutazione().size(); i++) {
					this.criterioValutazione[i] = helper.getListaCriteriValutazione().get(i).getValore();
				}
			}
		}
				
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
					 	 WizardOffertaTecnicaHelper.STEP_OFFERTA);

		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String openPageAfterError() {		
		WizardOffertaTecnicaHelper helper = GestioneBuste.getBustaTecnicaFromSession().getHelper();
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
						 WizardOffertaTecnicaHelper.STEP_OFFERTA);

		return this.getTarget();
	}

}
