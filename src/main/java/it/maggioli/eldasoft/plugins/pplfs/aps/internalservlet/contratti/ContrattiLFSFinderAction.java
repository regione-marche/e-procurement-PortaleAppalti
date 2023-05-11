package it.maggioli.eldasoft.plugins.pplfs.aps.internalservlet.contratti;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.eldasoft.www.sil.WSLfs.ContrattoLFSType;
import it.eldasoft.www.sil.WSLfs.ElencoContrattiLFSOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiLFSManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ContrattiLFSFinderAction extends EncodedDataAction implements
		SessionAware, ModelDriven<ContrattiLFSSearchBean> {

	/**
	 * UID 
	 */
	private static final long serialVersionUID = 3908314802542224605L;

	@Validate
	private ContrattiLFSSearchBean model = new ContrattiLFSSearchBean();
	private Map<String, Object> session;
	private ContrattoLFSType[] contratti;
	private IContrattiLFSManager contrattiManager;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String nappal;	//Codice2
	private ContrattoLFSType dettaglioContratto;
	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numSoccorsiIstruttori;
	private int numComunicazioniInviate;
	
	private Long idComunicazione;
	private Long idDestinatario;
	private IBandiManager bandiManager;
	
	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public int getNumComunicazioniRicevute() {
		return numComunicazioniRicevute;
	}

	public void setNumComunicazioniRicevute(int numComunicazioniRicevute) {
		this.numComunicazioniRicevute = numComunicazioniRicevute;
	}

	public int getNumComunicazioniRicevuteDaLeggere() {
		return numComunicazioniRicevuteDaLeggere;
	}

	public void setNumComunicazioniRicevuteDaLeggere(
			int numComunicazioniRicevuteDaLeggere) {
		this.numComunicazioniRicevuteDaLeggere = numComunicazioniRicevuteDaLeggere;
	}

	public int getNumComunicazioniArchiviate() {
		return numComunicazioniArchiviate;
	}

	public void setNumComunicazioniArchiviate(int numComunicazioniArchiviate) {
		this.numComunicazioniArchiviate = numComunicazioniArchiviate;
	}

	public int getNumComunicazioniArchiviateDaLeggere() {
		return numComunicazioniArchiviateDaLeggere;
	}

	public void setNumComunicazioniArchiviateDaLeggere(
			int numComunicazioniArchiviateDaLeggere) {
		this.numComunicazioniArchiviateDaLeggere = numComunicazioniArchiviateDaLeggere;
	}

	public int getNumSoccorsiIstruttori() {
		return numSoccorsiIstruttori;
	}

	public void setNumSoccorsiIstruttori(int numSoccorsiIstruttori) {
		this.numSoccorsiIstruttori = numSoccorsiIstruttori;
	}

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}

	public void setNumComunicazioniInviate(int numComunicazioniInviate) {
		this.numComunicazioniInviate = numComunicazioniInviate;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	public IContrattiLFSManager getContrattiManager() {
		return contrattiManager;
	}

	public void setContrattiManager(IContrattiLFSManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}

	public ContrattoLFSType[] getContratti() {
		return contratti;
	}

	public void setContratti(ContrattoLFSType[] contratti) {
		this.contratti = contratti;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public ContrattiLFSSearchBean getModel() {
		return model;
	}

	public void setModel(ContrattiLFSSearchBean model) {
		this.model = model;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;

	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getNappal() {
		return nappal;
	}

	public void setNappal(String nappal) {
		this.nappal = nappal;
	}
	
	public ContrattoLFSType getDettaglioContratto() {
		return dettaglioContratto;
	}

	public void setDettaglioContratto(ContrattoLFSType dettaglioContratto) {
		this.dettaglioContratto = dettaglioContratto;
	}

	public String open() {
		String target = SUCCESS;
		try {
			this.model = (ContrattiLFSSearchBean) this.session
					.get(PortGareSystemConstants.SESSION_ID_SEARCH_CONTRATTI_LFS);
			if (this.model == null) {
				this.model = new ContrattiLFSSearchBean();
			}
			findContratti();

		} catch (Exception e) {
			this.addActionError(this.getText(
					"Errors.contrattilfs.configuration",
					new String[] { "Contratti LFS" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "open");
		}
		return target;
	}

	public String find() {
		String target = SUCCESS;
		try {
			findContratti();
		} catch (Exception e) {
			this.addActionError(this.getText(
					"Errors.contrattilfs.configuration",
					new String[] { "Contratti LFS" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "search");
		}
		return target;
	}
	
	public String detail() {
		String target = SUCCESS;
		try {
			this.dettaglioContratto = contrattiManager.getDettaglioContrattoLFS(
					this.getCurrentUser().getUsername(),
					this.codice, 
					this.nappal); //this.nappal = "2"
			
			StatisticheComunicazioniPersonaliType stat = null;
			stat = this.bandiManager.getStatisticheComunicazioniPersonali(
					this.getCurrentUser().getUsername(), 
					this.codice, 
					this.nappal, 
					PortGareSystemConstants.ENTITA_CONTRATTO_LFS);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA, PortGareSystemConstants.ENTITA_CONTRATTO_LFS);
			this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
			this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
			this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
			this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
			this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
			this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE2_PROCEDURA, this.nappal);
		} catch (Exception e) {
			this.addActionError(this.getText(
					"Errors.contrattilfs.configuration",
					new String[] { "Contratti LFS" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "search");
		}
		return target;
	}

	public void findContratti() {
		ElencoContrattiLFSOutType risultato;
		try{
			
			
			String stazioneAppaltante = this.model.getStazioneAppaltante();
			String oggetto = this.model.getOggetto();
			String cig = this.model.getCig();
			String gara = this.model.getGara();
			String username = this.getCurrentUser().getUsername();//username = "sara01"
			String codice = this.model.getCodice();
			int startIndex = 0;
			if(this.model.getCurrentPage() > 0){
				startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
			}
			
			risultato = contrattiManager.searchContrattiLFS(stazioneAppaltante,
					oggetto,
					cig,
					gara,
					username,
					codice,
					startIndex,
					model.getiDisplayLength()
					);
			if(risultato.getElenco() != null){
				this.contratti = risultato.getElenco();
			} else {
				this.contratti =  new ContrattoLFSType[0];
			}
			this.model.processResult(contratti.length, risultato.getNum());
			this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_CONTRATTI_LFS,
							 this.model);
			
		} catch (Exception e) {
			this.addActionError(this.getText(
					"Errors.contrattilfs.configuration",
					new String[] { "Contratti LFS" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "search");
		}
	}

}
