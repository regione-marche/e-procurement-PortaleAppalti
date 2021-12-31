package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;

/**
 * Action di gestione del tipo di registrazione dei dati impresa
 * 
 * @author 
 * @since 2.0
 */
public class OpenPageTipoRegistrazioneImpresaAction extends EncodedDataAction
	implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8155172020209203256L;
	
	protected Map<String, Object> session;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public String openPage() {
		return SUCCESS; 
	}

}
