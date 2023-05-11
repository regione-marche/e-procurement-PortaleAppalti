package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigFeatures;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Action di gestione delle operazioni nella pagina degli indirizzi dell'impresa
 * nel wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageIndirizziImpresaAction extends
				AbstractProcessPageAction implements IIndirizzoImpresa {

	/**
	 * UID
	 */
	private static final long                serialVersionUID = -7527283479159496491L;
	private CustomConfigManager customConfigManager;

	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate(EParamValidation.DIGIT)
	private String idDelete;
	@Validate(EParamValidation.TIPO_INDIRIZZO)
	private String tipoIndirizzo;
	@Validate(EParamValidation.INDIRIZZO)
	private String indirizzo;
	@Validate(EParamValidation.NUM_CIVICO)
	private String numCivico;
	@Validate(EParamValidation.CAP)
	private String cap;
	@Validate(EParamValidation.COMUNE)
	private String comune;
	@Validate(EParamValidation.PROVINCIA)
	private String provincia;
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.TELEFONO)
	private String telefono;
	@Validate(EParamValidation.FAX)
	private String fax;

	private boolean delete;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = StringUtils.stripToNull(id);
	}

	/**
	 * @return the idDelete
	 */
	public String getIdDelete() {
		return idDelete;
	}

	/**
	 * @param idDelete the idDelete to set
	 */
	public void setIdDelete(String idDelete) {
		this.idDelete = idDelete;
	}

	/**
	 * @return the tipoIndirizzo
	 */
	@Override
	public String getTipoIndirizzo() {
		return tipoIndirizzo;
	}

	/**
	 * @param tipoIndirizzo the tipoIndirizzo to set
	 */
	@Override
	public void setTipoIndirizzo(String tipoIndirizzo) {
		this.tipoIndirizzo = tipoIndirizzo;
	}

	/**
	 * @return the indirizzo
	 */
	@Override
	public String getIndirizzo() {
		return indirizzo;
	}

	/**
	 * @param indirizzo the indirizzo to set
	 */
	@Override
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo.trim();
	}

	/**
	 * @return the numCivico
	 */
	@Override
	public String getNumCivico() {
		return numCivico;
	}

	/**
	 * @param numCivico the numCivico to set
	 */
	@Override
	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico.trim();
	}

	/**
	 * @return the cap
	 */
	@Override
	public String getCap() {
		return cap;
	}

	/**
	 * @param cap the cap to set
	 */
	@Override
	public void setCap(String cap) {
		this.cap = cap;
	}

	/**
	 * @return the comune
	 */
	@Override
	public String getComune() {
		return comune;
	}

	/**
	 * @param comune the comune to set
	 */
	@Override
	public void setComune(String comune) {
		this.comune = comune.trim();
	}

	/**
	 * @return the provincia
	 */
	@Override
	public String getProvincia() {
		return provincia;
	}

	/**
	 * @param provincia the provincia to set
	 */
	@Override
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	/**
	 * @return the nazione
	 */
	@Override
	public String getNazione() {
		return nazione;
	}

	/**
	 * @param nazione the nazione to set
	 */
	@Override
	public void setNazione(String nazione) {
		this.nazione = nazione.trim();
	}

	/**
	 * @return the telefono
	 */
	@Override
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono the telefono to set
	 */
	@Override
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * @return the fax
	 */
	@Override
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	@Override
	public void setFax(String fax) {
		this.fax = fax;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	@Override
	public void validate() {
		super.validate();
		try {
			if (customConfigManager.isActiveFunction(CustomConfigFeatures.PREFISSO_TEL.getKey(), CustomConfigFeatures.PREFISSO_TEL.getValue())) {
				if (StringUtils.isNotEmpty(telefono) && !ProcessPageDatiPrincipaliImpresaAction.TELEFONO.matcher(telefono).matches())
					addFieldError("telefonoRecapito", getText("telefono.prefisso.richiesto", new String[]{ getI18nLabel("LABEL_TELEFONO") }));
				if (StringUtils.isNotEmpty(fax) && !ProcessPageDatiPrincipaliImpresaAction.TELEFONO.matcher(fax).matches())
					addFieldError("faxRecapito", getText("telefono.prefisso.richiesto", new String[]{ getI18nLabel("LABEL_FAX") }));
			}
		} catch (Exception e) {
			throw new RuntimeException("Errore durante la verifica dei dati", e);
		}

		if (this.getFieldErrors().size() > 0) {
			return;
		}
		
		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper != null) {
			try {
				boolean invalidCap = (this.cap != null && StringUtils.isNotEmpty(this.cap) && !this.cap.matches("[0-9]+"));
				boolean invalidAlphaCap = (this.cap != null && StringUtils.isNotEmpty(this.cap) && !this.cap.matches("[0-9a-zA-Z]+"));
				
				if("1".equals(helper.getDatiPrincipaliImpresa().getAmbitoTerritoriale())) {
					// operatore economico italiano
					// valida i caratteri per il cap...
					if(invalidCap) {
						this.addFieldError("cap", this.getTextFromDB("cap") + " " + 
												  this.getText("localstrings.wrongCharacters"));
					}				
				} 
				
			} catch (Throwable t) {
				throw new RuntimeException("Errore durante la verifica dei dati ", t);
			}	
		}
	}

	//@SkipValidation
	@Override
	public String next() {
		String target = SUCCESS;

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.tipoIndirizzo.length() > 0) {
				// sono stati inseriti dei dati, si procede al salvataggio
				if (StringUtils.isNotBlank(this.id)) {
					// aggiorna i dati in sessione (codice preso da modify)
					IIndirizzoImpresa indirizzo = helper.getIndirizziImpresa().get(
									Integer.parseInt(this.id));
					ProcessPageIndirizziImpresaAction.synchronizeIndirizzoImpresa(this,
									indirizzo);
				} else {
					// aggiunge i dati in sessione (codice preso da insert)
					IIndirizzoImpresa indirizzo = new IndirizzoImpresaHelper();
					ProcessPageIndirizziImpresaAction.synchronizeIndirizzoImpresa(this,
									indirizzo);
					helper.getIndirizziImpresa().add(indirizzo);
				}
			}

			// siccome vado avanti, procedo con la verifica dei record in lista
			for (int i = 0; i < helper.getIndirizziImpresa().size(); i++) {
				IIndirizzoImpresa indirizzo = helper.getIndirizziImpresa().get(i);
				if (StringUtils.isBlank(indirizzo.getTipoIndirizzo())
					|| StringUtils.isBlank(indirizzo.getIndirizzo())
					|| StringUtils.isBlank(indirizzo.getNumCivico())
					|| (StringUtils.isBlank(indirizzo.getCap()) && "ITALIA".equalsIgnoreCase(indirizzo.getNazione()))
					|| StringUtils.isBlank(indirizzo.getComune())
					|| StringUtils.isBlank(indirizzo.getNazione())
					|| (StringUtils.isBlank(indirizzo.getProvincia()) && "ITALIA".equalsIgnoreCase(indirizzo.getNazione()))) 
				{
					List<Object> arg = new ArrayList<Object>();
					arg.add(i + 1);
					this.addActionError(this.getText("Errors.datiIndirizzoIncompleti", arg));
					target = "refresh";
				}
			}

			// nel caso di libero professionista la pagina successiva e' il
			// dettaglio con altri dati del libero professionista e non la
			// pagina dei soggetti
			if (SUCCESS.equals(target) && helper.isLiberoProfessionista()) {
				target = "successLiberoProf";
			}
		}
		return target;
	}

	@SkipValidation
	public String add() {
		return "refresh";
	}

	public String insert() {
		String target = "refresh";

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			if (this.tipoIndirizzo.length() > 0) {
				IIndirizzoImpresa indirizzo = new IndirizzoImpresaHelper();
				ProcessPageIndirizziImpresaAction.synchronizeIndirizzoImpresa(
								this, indirizzo);
				helper.getIndirizziImpresa().add(indirizzo);
			}
		}
		return target;
	}

	public String save() {
		String target = "refresh";

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			IIndirizzoImpresa indirizzo = helper.getIndirizziImpresa().get(
							Integer.parseInt(this.id));
			ProcessPageIndirizziImpresaAction.synchronizeIndirizzoImpresa(this,
							indirizzo);
		}
		return target;
	}

	@SkipValidation
	public String confirmDelete() {
		String target = "refresh";
		this.delete = true;
		return target;
	}

	@SkipValidation
	public String delete() {
		String target = "refresh";

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			helper.getIndirizziImpresa().remove(Integer.parseInt(this.idDelete));
		}
		return target;
	}

	@SkipValidation
	public String modify() {
		return "modify";
	}

	/**
	 * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
	 *
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		return helper;
	}

	public static void synchronizeIndirizzoImpresa(IIndirizzoImpresa from,
					IIndirizzoImpresa to) {
		to.setTipoIndirizzo(StringUtils.stripToNull(from.getTipoIndirizzo()));
		to.setIndirizzo(StringUtils.stripToNull(from.getIndirizzo()));
		to.setNumCivico(StringUtils.stripToNull(from.getNumCivico()));
		to.setCap(StringUtils.stripToNull(from.getCap()));
		to.setComune(StringUtils.stripToNull(from.getComune()));
		to.setProvincia(StringUtils.stripToNull(from.getProvincia()));
		to.setNazione(StringUtils.stripToNull(from.getNazione()));
		to.setTelefono(StringUtils.stripToNull(from.getTelefono()));
		to.setFax(StringUtils.stripToNull(from.getFax()));
	}

	public static void resetIndirizzoImpresa(IIndirizzoImpresa indirizzo) {
		indirizzo.setTipoIndirizzo(null);
		indirizzo.setIndirizzo(null);
		indirizzo.setNumCivico(null);
		indirizzo.setCap(null);
		indirizzo.setComune(null);
		indirizzo.setProvincia(null);
		indirizzo.setNazione("Italia");
		indirizzo.setTelefono(null);
		indirizzo.setFax(null);
	}
}
