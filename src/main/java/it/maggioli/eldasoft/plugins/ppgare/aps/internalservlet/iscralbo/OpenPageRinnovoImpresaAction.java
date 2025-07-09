package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ... 
 * 
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO,
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO  
	})
public class OpenPageRinnovoImpresaAction extends OpenPageImpresaAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 7263566260303736980L;
	
	public String getSTEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO() {
		return WizardRinnovoHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO;
	}
	
	/**
	 * ...
	 */
	public String openPage() {
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, WizardRinnovoHelper.STEP_IMPRESA);
		return this.getTarget();
	}
	
}
