package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.sysparams;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;

import java.util.LinkedHashMap;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;

/**
 * Servizio di gestione delle customizzazioni dei parametri di sistema.
 * 
 * @author Stefano.Sabbadin
 */
public class ConfigParamSistemaManager extends AbstractService implements IConfigParamSistemaManager {

	/** UID. */
	private static final long serialVersionUID = -3628032399365413446L;

	/** Manager del plugin jpmail per la configurazione dei parametri di posta. */
	private IMailManager mailManager;
	
	/** Riferimento al wrapper per il web service Gare Appalto */
	private WSGareAppaltoWrapper wsGareAppalto;

	/** Manager per la gestione delle codifiche caricate dal backoffice. */
	private ICodificheManager codificheManager;
	
	/** Manager per la gestione delle personalizzazioni clienti. */
	private ICustomConfigManager customConfigManager;
	
	/**
	 * @param mailManager the mailManager to set
	 */
	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	/**
	 * @param wsGareAppalto the wsGareAppalto to set
	 */
	public void setWsGareAppalto(WSGareAppaltoWrapper wsGareAppalto) {
		this.wsGareAppalto = wsGareAppalto;
	}

	/**
	 * @param codificheManager the codificheManager to set
	 */
	public void setCodificheManager(ICodificheManager codificheManager) {
		this.codificheManager = codificheManager;
	}
	
	/**
	 * @param customConfigManager the customConfigManager to set
	 */
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public EsitoOutType syncConfigurazioneMail(String host, Integer porta,
			String protocollo, Integer timeout, boolean debug, String username, String password, String mail) {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);
		
		try {
			if (customConfigManager.isActiveFunction("IMPRESA-DATIPRINC-RECAPITI", "MAILRIF")) {
			    risultato.setEsitoOk(false);
			    risultato.setCodiceErrore("SYNC-DENIED");
			} else {
				// lettura configurazione corrente
				MailConfig config = this.mailManager.getMailConfig();
				// variazione della configurazione
				config.setSmtpHost(host);
				config.setSmtpPort(porta);
				config.setSmtpProtocol(protocollo);
				config.setSmtpTimeout(timeout);
				config.setDebug(debug);
				config.setSmtpUserName(username);
				config.setSmtpPassword(password);
				config.getSenders().put(CommonSystemConstants.SENDER_CODE, mail);
				// aggiornamento della configurazione
				this.mailManager.updateMailConfig(config);
			}
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "syncConfigurazioneMail");
		    risultato.setEsitoOk(false);
		    risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		}
		
		return risultato;
	}

	@Override
	public EsitoOutType syncUnitaMisura() {
		EsitoOutType risultato = new EsitoOutType();
		risultato.setEsitoOk(true);
		risultato.setCodiceErrore(null);
		try {
			String xml = this.wsGareAppalto.getProxyWSGare().getElencoUnitaMisura();
			LinkedHashMap<String, String> lista = InterceptorEncodedData.parseXml(xml);
			this.codificheManager.getCodifiche().put(InterceptorEncodedData.LISTA_TIPI_UNITA_MISURA, lista);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "syncUnitaMisura");
		    risultato.setEsitoOk(false);
		    risultato.setCodiceErrore("UNEXP-ERR " + e.getMessage());
		}
		
		return risultato;
	}
	
}
