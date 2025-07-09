package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Action di apertura della pagina di gesione dei componenti una rti o di un
 * consorzio.
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class OpenPageAvvalimentoAction extends AbstractOpenPageAction implements IImpresaAusiliaria {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5155351804046990553L;


	private IBandiManager bandiManager;

	/**
	 * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
	 * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
	 * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
	 * valorizzato diversamente, vuol dire che si proviene dalla normale
	 * navigazione tra le pagine del wizard
	 */
	@Validate(EParamValidation.GENERIC)
	private String page;
	
	private Integer avvalimento;

	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.TIPO_IMPRESA)
	private String tipoImpresa;
	@Validate(EParamValidation.AMBITO_TERRITORIALE)
	private String ambitoTerritoriale;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscale;
	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIVA;
	@Validate(EParamValidation.GENERIC)
	private String idFiscaleEstero;
	@Validate(EParamValidation.GENERIC)
	private String avvalimentoPer;
	
	private boolean readOnly;
	
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Integer getAvvalimento() {
		return avvalimento;
	}

	public void setAvvalimento(Integer avvalimento) {
		this.avvalimento = avvalimento;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

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
	public String getTipoImpresa() {
		return tipoImpresa;
	}

	@Override
	public void setTipoImpresa(String tipoImpresa) {
		this.tipoImpresa = tipoImpresa;
	}

	@Override
	public String getAmbitoTerritoriale() {
		return ambitoTerritoriale;
	}

	@Override
	public void setAmbitoTerritoriale(String ambitoTerritoriale) {
		this.ambitoTerritoriale = ambitoTerritoriale;
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

	@Override
	public String getIdFiscaleEstero() {
		return idFiscaleEstero;
	}

	@Override
	public void setIdFiscaleEstero(String idFiscaleEstero) {
		this.idFiscaleEstero = idFiscaleEstero;
	}	
	
	@Override
	public String getAvvalimentoPer() {
		return avvalimentoPer;
	}

	@Override
	public void setAvvalimentoPer(String avvalimentoPer) {
		this.avvalimentoPer = avvalimentoPer;
	}


	/**
	 * recupera dalla sessione l'helper dell'avvalimento
	 */
	private WizardPartecipazioneHelper getHelperFromSession() {
		GestioneBuste buste = GestioneBuste.getFromSession();
		return (WizardPartecipazioneHelper) (buste != null ? buste.getBustaPartecipazione().getHelper() : null);
	}
	
	/**
	 * ...
	 */
	private void initPage(WizardPartecipazioneHelper partecipazione) {
		avvalimento = (partecipazione.isAvvalimento() ? 1 : 0);
		readOnly = false;	//= (StringUtils.isNotEmpty(id)); ???
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
				 		 PortGareSystemConstants.WIZARD_PAGINA_AVVALIMENTO);
	}
	
	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		try {
			WizardPartecipazioneHelper partecipazione = getHelperFromSession();
			
			if (partecipazione == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente carica
				initPage(partecipazione);
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "openPage");
			this.addActionError(this.getText("Errors.unexpected"));
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	/**
	 * Apre la pagina svuotando i dati del form di inserimento/modifica
	 *
	 * @return target
	 */
	public String openPageClear() {
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		try {
			if (partecipazione == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente.
				// svuota i dati inseriti nella form in modo da riaprire la
				// form pulita.
				ImpresaAusiliariaHelper.reset(this);
				this.id = null;
				
				initPage(partecipazione);
			}
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "openPageClear");
			this.addActionError(this.getText("Errors.unexpected"));
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String openPageModify() {
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if (partecipazione == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente.
			// popola nel bean i dati presenti nell'elemento in sessione
			// individuato da id.
			IImpresaAusiliaria impresaAusiliaria = partecipazione.getImpreseAusiliarie().get(Integer.parseInt(this.id));
			ImpresaAusiliariaHelper.copyTo(impresaAusiliaria, this);

			initPage(partecipazione);
		}
		return this.getTarget();
	}
	
}