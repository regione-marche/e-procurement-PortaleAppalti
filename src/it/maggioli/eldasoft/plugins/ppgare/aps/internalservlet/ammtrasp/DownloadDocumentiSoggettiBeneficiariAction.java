package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.FileType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.Allegato;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ZipUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeTrasparenzaManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action per la gestione del download di documenti allegati all'esito nel
 * prospetto beneficiari.
 * 
 * @version 1.8.2
 * @author Stefano.Sabbadin
 */
public class DownloadDocumentiSoggettiBeneficiariAction extends BaseAction implements SessionAware, IDownloadAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1273089816534519931L;

	private ILeggeTrasparenzaManager leggeTrasparenzaManager;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	private String codice;
	private String codben;

	private Map<String, Object> session;

	private InputStream inputStream;
	private String filename;
	
	/** Riferimento alla url della pagina per visualizzare eventuali problemi emersi. */
	private String urlPage;

	/** Riferimento al frame della pagina in cui visualizzare il contenuto della risposta. */
	private String currentFrame;	

	/**
	 * @param leggeTrasparenzaManager the leggeTrasparenzaManager to set
	 */
	public void setLeggeTrasparenzaManager(
			ILeggeTrasparenzaManager leggeTrasparenzaManager) {
		this.leggeTrasparenzaManager = leggeTrasparenzaManager;
	}

	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @return the codice
	 */
	public String getCodice() {
		return codice;
	}

	/**
	 * @param codice
	 *            the codice to set
	 */
	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	/**
	 * @return the codben
	 */
	public String getCodben() {
		return codben;
	}

	/**
	 * @param codben the codben to set
	 */
	public void setCodben(String codben) {
		this.codben = codben;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	@Override
	public String getUrlErrori() {
		return this.urlPage+"?last=1&actionPath=/ExtStr2/do/FrontEnd/AmmTrasp/searchSoggettiBeneficiari.action&currentFrame="+this.currentFrame;
	}

	/**
	 * Restituisce lo stream contenente uno zip contenente i documenti allegati
	 * per la trasparenza per l'esito in oggetto.
	 */
	public String downloadDocumenti() {
		String target = SUCCESS;

		String token = StringUtils.stripToNull((String) this.appParamManager
						.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN));
		try {
			// estrazione file
			List<FileType> listaDocumenti = this.leggeTrasparenzaManager
					.getDocumentiBeneficiario(token, this.codice, this.codben);
			
			List<Allegato> files = new ArrayList<Allegato>();
			for (FileType documento : listaDocumenti) {
				files.add(new Allegato(documento.getNome(), documento.getFile()));
			}
			
			// assegnazione alla response del contenuto zippato
			this.inputStream = new ByteArrayInputStream(ZipUtilities.getZip(files));

			// definizione delle altre info ausiliarie per il completamento del
			// download
			this.filename = this.codice + ".zip";

		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumenti");
			ExceptionUtils.manageExceptionError(e, this);
			session.put(ERROR_DOWNLOAD, this.getActionErrors().toArray()[0]);
			target = INPUT;
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "downloadDocumenti");
			ExceptionUtils.manageExceptionError(e, this);
			session.put(ERROR_DOWNLOAD, this.getActionErrors().toArray()[0]);
			target = INPUT;
		}

		return target;
	}

}
