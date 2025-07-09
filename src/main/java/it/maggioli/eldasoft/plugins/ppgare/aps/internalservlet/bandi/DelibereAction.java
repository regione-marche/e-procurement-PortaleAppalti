package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSGareAppalto.AbilitazioniGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DeliberaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.EspletGaraOperatoreType;
import it.eldasoft.www.sil.WSGareAppalto.EspletamentoElencoOperatoriSearch;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.OpenLottiDistintiAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.EspletGaraViewAccessoDocumentiLottiAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Action per le operazioni sulle delibere. Implementa le operazioni CRUD sulle
 * schede.
 *
 * @version ...
 * @author ...
 */
public class DelibereAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6369494589081286064L;
	
    private IBandiManager bandiManager;
    private ConfigInterface configManager;
    private IAppParamManager appParamManager;
    private Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private DettaglioGaraType dettaglioGara;
	private DeliberaType delibera;
		
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public DettaglioGaraType getDettaglioGara() {
		return dettaglioGara;
	}

	public void setDettaglioGara(DettaglioGaraType dettaglioGara) {
		this.dettaglioGara = dettaglioGara;
	}

	public DeliberaType getDelibera() {
		return delibera;
	}

	public void setDelibera(DeliberaType delibera) {
		this.delibera = delibera;
	}

	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}
	
	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio bando.
	 *
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		setTarget(SUCCESS);
		
		// se si proviene dall'EncodedDataAction di InitIscrizione con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		try {
			DettaglioGaraType gara = bandiManager.getDettaglioGara(this.codice);
			setDettaglioGara(gara);
			
			DeliberaType dettaglioDelibera = bandiManager.getDettaglioDelibera(this.codice);
			setDelibera(dettaglioDelibera);
			
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "view");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
