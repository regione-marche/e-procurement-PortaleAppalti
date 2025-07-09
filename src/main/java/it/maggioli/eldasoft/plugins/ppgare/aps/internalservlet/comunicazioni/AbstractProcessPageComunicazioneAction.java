package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import java.util.Map;

import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiLFSManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;


/**
 * Classe di base in cui rendere disponibili i metodi comuni ad alcune azioni sulle comunicazioni.
 * 
 * @author stefano.sabbadin
 */
public abstract class AbstractProcessPageComunicazioneAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5641977067765041699L;

	protected IAppParamManager appParamManager;
	protected IBandiManager bandiManager;
	protected IContrattiManager contrattiManager;
	protected IAvvisiManager avvisiManager;
	protected IContrattiLFSManager contrattiLFSManager;
	
	private StazioneAppaltanteBandoType stazioneAppaltante;
	@Validate(EParamValidation.GENERIC)
	private String codiceSA;
	@Validate(EParamValidation.CIG)
	private String codiceCig;
	
	protected WizardNuovaComunicazioneHelper helper;
	protected String helperSessionId;
	
	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public IContrattiManager getContrattiManager() {
		return contrattiManager;
	}

	public void setContrattiManager(IContrattiManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}

	public IAvvisiManager getAvvisiManager() {
		return avvisiManager;
	}

	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}
	
	public IContrattiLFSManager getContrattiLFSManager() {
		return contrattiLFSManager;
	}

	public void setContrattiLFSManager(IContrattiLFSManager contrattiLFSManager) {
		this.contrattiLFSManager = contrattiLFSManager;
	}

	public StazioneAppaltanteBandoType getStazioneAppaltante() {
		return stazioneAppaltante;
	}
	
	public String getCodiceSA() {
		return codiceSA;
	}
	
	public String getCodiceCig() {
		return codiceCig;
	}

	
	/**
	 * costruttore generico
	 */
	public AbstractProcessPageComunicazioneAction(WizardNuovaComunicazioneHelper helper, String helperSessionId) {
		super();
		this.helper = helper;
		this.helperSessionId = helperSessionId;
	}
	
	public AbstractProcessPageComunicazioneAction() {
		this(null, null);
	}
		
	protected Object getWizardFromSession() {
		helper = (WizardNuovaComunicazioneHelper) session.get(helperSessionId);
		return helper;
	}
	
	protected void putWizardToSession() {
		session.put(helperSessionId, helper);
	}

	/**
	 * Metodo che centralizza ed estrae alcune informazioni utili per procedere
	 * alla successiva eventuale protocollazione.
	 * 
	 * @throws ApsException
	 */
	public void setInfoPerProtocollazione(WizardNuovaComunicazioneHelper helper) throws ApsException {				
		if(PortGareSystemConstants.ENTITA_CONTRATTO_LFS.equalsIgnoreCase(helper.getEntita())) {
			// gestione per contrati LFS
//			ContrattoLFSType dettaglioContrattoLFS = contrattiLFSManager.getDettaglioContrattoLFS(helper.getCodice(), helper.getCodice2());
//			if(dettaglioContrattoLFS != null) {
//				this.codiceSA = dettaglioContrattoLFS.getStazioneAppaltante();
//				this.codiceCig = dettaglioContrattoLFS.getCig();
//			}	
		} else if(PortGareSystemConstants.ENTITA_GENERICA_RISERVATA.equalsIgnoreCase(helper.getEntita())) {
			this.codiceSA = helper.getStazioneAppaltante();
		} else {
			// gestione standard
			DettaglioGaraType dettaglioGara = bandiManager.getDettaglioGara(helper.getCodice());
			if(dettaglioGara != null) {
				this.stazioneAppaltante = dettaglioGara.getStazioneAppaltante();
				this.codiceSA = dettaglioGara.getStazioneAppaltante().getCodice();
				this.codiceCig = bandiManager.getCigBando(helper.getCodice());
			} else {
				DettaglioBandoIscrizioneType dettaglioIscrizione = bandiManager.getDettaglioBandoIscrizione(helper.getCodice());			
				if(dettaglioIscrizione != null) {
					this.stazioneAppaltante = dettaglioIscrizione.getStazioneAppaltante();
					this.codiceSA = dettaglioIscrizione.getStazioneAppaltante().getCodice();
				} else {
					ContrattoType dettaglioContratto = contrattiManager.getDettaglioContratto(helper.getCodice());
					if(dettaglioContratto != null) {
						// TODO: al momento, per come e' implementata, questa funzione di nicchia non recupera la StazioneAppaltanteBandoType ma solo la denominazione 
						this.codiceSA = dettaglioContratto.getStazioneAppaltante();
						this.codiceCig = dettaglioContratto.getCig();
					} else {
						DettaglioAvvisoType dettaglioAvviso = avvisiManager.getDettaglioAvviso(helper.getCodice());
						if(dettaglioAvviso != null && dettaglioAvviso.getStazioneAppaltante() != null) {
							this.stazioneAppaltante = dettaglioAvviso.getStazioneAppaltante();
							this.codiceSA = dettaglioAvviso.getStazioneAppaltante().getCodice();
						} else {
							// se non trovo ne un dettaglioGara, ne un dettaglioIscrizione,
							// ne un dettaglioContratto... allora cerco nella gara
							// anche se non e' pubblicata sul PortaleAppalti !!!
							// NB: 
							//   in caso di protocollazione non e' possibile rispondere
							// 	 ad una comunicazione inviata dall'ente su una gara 
							//   non ancora pubblicata!!!
							// FIX: estraggo allora il dato dell'eventuale
							// comunicazione a cui rispondo visto che non ho
							// reperito il dettaglio corrispondente
							this.codiceSA = helper.getStazioneAppaltante();
						}
					}
				}
			}
		}
	}
	
}
