package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;

import com.agiletec.aps.system.exception.ApsException;


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

	private IAppParamManager appParamManager;
	private IBandiManager bandiManager;
	private IContrattiManager contrattiManager;
	private IAvvisiManager avvisiManager;
	
	private StazioneAppaltanteBandoType stazioneAppaltante;
	private String codiceSA;
	private String codiceCig;
	
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
	 * Metodo che centralizza ed estrae alcune informazioni utili per procedere
	 * alla successiva eventuale protocollazione.
	 * 
	 * @throws ApsException
	 */
	public void setInfoPerProtocollazione(WizardNuovaComunicazioneHelper helper) throws ApsException {
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
