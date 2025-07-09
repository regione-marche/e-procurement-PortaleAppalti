package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Action per le operazioni sugli avvisi di gara. Implementa le operazioni CRUD
 * sulle schede.
 * 
 * @version 
 * @author 
 */
public class AvvisiComunicazioniAttiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6740878386277951040L;

	private Map<String, Object> session;

	private IAvvisiManager avvisiManager;
	private IBandiManager bandiManager;
    private IPageManager pageManager;
    
    private Boolean avvisiComunicazioniAtti = new Boolean(true);
	@Validate(EParamValidation.CODICE)
	private String codice;
	private DettaglioAvvisoType dettaglioAvviso;
	private DocumentoAllegatoType[] attiDocumenti;
	private Long idComunicazione;
	private Long idDestinatario;
	
	/**
	 * true se il men&ugrave; elenchi operatori economici risulta visibile,
	 * false altrimenti. Serve per bloccare il pulsante che da un dettaglio
	 * avviso va al dettaglio elenco.
	 */
	private boolean menuElenchiVisibile;
	
	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numComunicazioniInviate;
	private Long genere;
	
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public Boolean getAvvisiComunicazioniAtti() {
		return avvisiComunicazioniAtti;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public DettaglioAvvisoType getDettaglioAvviso() {
		return dettaglioAvviso;
	}

	public void setDettaglioAvviso(DettaglioAvvisoType dettaglioAvviso) {
		this.dettaglioAvviso = dettaglioAvviso;
	}
	
	public boolean isMenuElenchiVisibile() {
		return menuElenchiVisibile;
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

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}

	public void setNumComunicazioniInviate(int numComunicazioniInviate) {
		this.numComunicazioniInviate = numComunicazioniInviate;
	}

	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}
	
	public DocumentoAllegatoType[] getAttiDocumenti() {
		return attiDocumenti;
	}

	public void setAttiDocumenti(DocumentoAllegatoType[] attiDocumenti) {
		this.attiDocumenti = attiDocumenti;
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

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio
	 * bando.
	 * 
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		// se si proviene dall'EncodedDataAction di InitIscrizione con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if ("block".equals(this.getTarget()) || "successAvviso".equalsIgnoreCase(this.getTarget()))
			this.setTarget(SUCCESS);
		try {
			DettaglioAvvisoType gara = this.avvisiManager.getDettaglioAvviso(this.codice);
			this.setDettaglioAvviso(gara);
			
			if(gara == null) {
				addActionMessage(getText("avviso.inDefinizione"));
				setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				IPage page = this.pageManager.getPage("ppgare_oper_economici");
				this.menuElenchiVisibile = page.isShowable();
				
				// comunicazioni ricevute, archiviate, inviate, ...
				StatisticheComunicazioniPersonaliType stat = this.bandiManager
					.getStatisticheComunicazioniPersonali(this.getCurrentUser().getUsername(), this.codice,null, null);
				 
				this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
				this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
				this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
				this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
				this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
				
				this.setGenere(this.bandiManager.getGenere(this.codice));
				
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
				this.session.put("dettaglioPresente", true);
				
				this.setTarget(SUCCESS);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	/**
	 * visualizza altri documenti per un avviso
	 */
	public String viewAltriDocumenti() {
		try {
			DettaglioAvvisoType avviso = this.avvisiManager.getDettaglioAvviso(this.codice);
//			if (isAccessoNonConsentitoDatiProcedura(gara)) {
//				// blocco l'accesso ai dati quando si forza la url 
//				// di accesso senza avere l'autorizzazione a consultare il dato
//				this.addActionError(this.getText("Errors.notAllowed"));
//				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
//			} else {
				// popolo i dati da visualizzare
				this.setDettaglioAvviso(avviso);
				DocumentoAllegatoType[] attiDocs = this.bandiManager.getAttiDocumentiBandoGara(this.codice);
				this.setAttiDocumenti(attiDocs);
//			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewAltriDocumenti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
