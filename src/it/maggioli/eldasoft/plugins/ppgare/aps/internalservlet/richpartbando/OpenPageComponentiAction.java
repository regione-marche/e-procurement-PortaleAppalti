package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;

/**
 * Action di apertura della pagina di gesione dei componenti una rti o di un
 * consorzio.
 *
 * @author Stefano.Sabbadin
 */
public class OpenPageComponentiAction extends AbstractOpenPageAction implements IComponente {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4930477557650725322L;

	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	private String page;

	private String id;
	private String nazione;
	private String ragioneSociale;
	private String codiceFiscale;
	private String partitaIVA;
	private String strQuota;				// Quota acquisita in formato stringa
	private Double quota;					// Quota di partecipazione della mandante alla RTI nella gara
	private String strQuotaRTI;				// Quota RTI della mandataria acquisita in formato stringa
	private Double quotaRTI;				// Quota di partecipazione della mandataria dell'RTI nella gara
	private String tipoImpresa;				// Tipologia di impresa consorziata o mandante
	private boolean liberoProfessionista;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getRagioneSociale() {
		return ragioneSociale;
	}

	@Override
	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	@Override
	public String getNazione() {
		return nazione;
	}

	@Override
	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	@Override
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	@Override
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	@Override
	public String getPartitaIVA() {
		return partitaIVA;
	}

	@Override
	public void setPartitaIVA(String partitaIVA) {
		this.partitaIVA = partitaIVA;
	}

	public String getStrQuota() {
		return strQuota;
	}

	public void setStrQuota(String strQuota) {
		this.strQuota = strQuota;
	}

	@Override
	public Double getQuota() {
		return quota;
	}

	@Override
	public void setQuota(Double quota) {
		this.quota = quota;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getStrQuotaRTI() {
		return strQuotaRTI;
	}

	public void setStrQuotaRTI(String strQuotaRTI) {
		this.strQuotaRTI = strQuotaRTI;
	}

	public Double getQuotaRTI() {
		return quotaRTI;
	}

	public void setQuotaRTI(Double quotaRTI) {
		this.quotaRTI = quotaRTI;
	}

	@Override
	public String getTipoImpresa() {
		return tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	public boolean isLiberoProfessionista() {
		return liberoProfessionista;
	}

	public void setLiberoProfessionista(boolean liberoProfessionista) {
		this.liberoProfessionista = liberoProfessionista;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		IRaggruppamenti helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			if (StringUtils.isNotBlank(this.getTipoImpresa())) {
				this.setLiberoProfessionista(this.getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
								.containsKey(this.getTipoImpresa()));
			}
			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							PortGareSystemConstants.WIZARD_PAGINA_COMPONENTI);
			this.quotaRTI = helper.getQuotaRTI();
		}
		return this.getTarget();
	}

	/**
	 * Apre la pagina svuotando i dati del form di inserimento/modifica
	 *
	 * @return target
	 */
	public String openPageClear() {
		IRaggruppamenti helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente.
			// svuota i dati inseriti nella form in modo da riaprire la
			// form pulita.
			ProcessPageComponentiAction.resetComponente(this);
			this.setLiberoProfessionista(false);
			this.quotaRTI = helper.getQuotaRTI();
			this.id = null;
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							PortGareSystemConstants.WIZARD_PAGINA_COMPONENTI);
		}
		return this.getTarget();
	}

	public String openPageModify() {
		IRaggruppamenti helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente.
			// popola nel bean i dati presenti nell'elemento in sessione
			// individuato da id.
			IComponente componente = helper.getComponenti().get(
							Integer.parseInt(this.id));
			ProcessPageComponentiAction.synchronizeComponente(componente, this);
			if (StringUtils.isNotBlank(this.getTipoImpresa())) {
				this.setLiberoProfessionista(this.getMaps()
								.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
								.containsKey(this.getTipoImpresa()));
			}
			this.quotaRTI = helper.getQuotaRTI();
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							PortGareSystemConstants.WIZARD_PAGINA_COMPONENTI);
		}
		return this.getTarget();
	}

	/**
	 * Ritorna l'helper in sessione utilizzato per la memorizzazione dei dati
	 * sulla partecipazione in RTI.
	 * 
	 * @return helper contenente i dati per la gestione di RTI e componenti
	 */
	protected IRaggruppamenti getSessionHelper() {
		return (WizardPartecipazioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
	}

}
