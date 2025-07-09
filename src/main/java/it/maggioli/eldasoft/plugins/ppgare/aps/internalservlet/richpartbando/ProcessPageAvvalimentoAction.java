package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

/**
 * Action di gestione delle operazioni nella pagina dei componenti di una RTI o
 * di un consorzio.
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageAvvalimentoAction extends AbstractProcessPageAction implements IImpresaAusiliaria {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7031549633801710869L;

	private ICodificheManager codificheManager;
	private ICustomConfigManager customConfigManager;

	private Integer avvalimento;
	
	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate(EParamValidation.DIGIT)
	private String idDelete;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.TIPO_IMPRESA)
	private String tipoImpresa;
	@Validate(EParamValidation.AMBITO_TERRITORIALE)
	private String ambitoTerritoriale;
	@Validate(EParamValidation.NAZIONE)
	private String nazione;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscale;
	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIVA;
	@Validate(EParamValidation.GENERIC)
	private String idFiscaleEstero; 
	@Validate(EParamValidation.GENERIC)
	private String avvalimentoPer;
		
	private boolean delete;
	private boolean readOnly;

	public void setCodificheManager(ICodificheManager manager) {
		this.codificheManager = manager;
	}

	public ICodificheManager getCodificheManager() {
		return codificheManager;
	}
	
	public ICustomConfigManager getCustomConfigManager() {
		return customConfigManager;
	}

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
	public Integer getAvvalimento() {
		return avvalimento;
	}

	public void setAvvalimento(Integer avvalimento) {
		this.avvalimento = avvalimento;
	}

	public boolean isDelete() {
		return delete;
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

	public String getIdDelete() {
		return idDelete;
	}

	public void setIdDelete(String idDelete) {
		this.idDelete = idDelete;
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
		return this.idFiscaleEstero;
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
	private void updateHelperSessione() {
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if(partecipazione == null) {
			return;
		}
		
		partecipazione.setAvvalimento( (avvalimento != null && avvalimento == 1) );
	}
	
	/**
	 * inserisci/aggiorna un'impresa ausiliaria della lista 
	 */
	private boolean addImpresaAusiliaria(String id) {
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		ImpreseAusiliarieValidator validator = new ImpreseAusiliarieValidator(
				partecipazione,
				partecipazione.getDatiPrincipaliImpresa(),
				partecipazione.isRti(),
				this);
		boolean insert = (StringUtils.isEmpty(id));
		
		IImpresaAusiliaria impresaAusiliaria = (insert 
				? new ImpresaAusiliariaHelper(this) 
				: partecipazione.getImpreseAusiliarie().get(Integer.parseInt(id)));

		// verifica se l'impresa ausiliaria fa parte dell'RTI...
		IImpresa impresaInRti = validator.isImpreseAusiliariePresentiInRti(impresaAusiliaria);
		if(impresaInRti != null) {
			String cf = "2".equals(impresaInRti.getAmbitoTerritoriale()) 
								   ? impresaInRti.getIdFiscaleEstero()
								   : impresaInRti.getCodiceFiscale();
			this.addActionError(this.getText("Errors.impreseAusiliarie.impresaPresenteInRTI",
					 						 new String[] { impresaInRti.getRagioneSociale(), cf }));
		} else if (this.ragioneSociale.length() > 0) {
			if(insert) {
				// insert
				partecipazione.getImpreseAusiliarie().add(impresaAusiliaria);
			} else {
				// update
				ImpresaAusiliariaHelper.copyTo(this, impresaAusiliaria);
			}
		}

		return (impresaInRti == null);
	}
	
	/**
	 * ... 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void validate() {
		updateHelperSessione();
		
		super.validate();

		if (readOnly) {
			// Caso readOnly, quindi, l'utente si ritrova già con i dati valorizzati
			clearFieldErrors();	
		} else {
			try {
				// VALIDAZIONE E CONTROLLI CAMPI
				WizardPartecipazioneHelper partecipazione = getHelperFromSession();
				ImpreseAusiliarieValidator validator = new ImpreseAusiliarieValidator(
						partecipazione,
						partecipazione.getDatiPrincipaliImpresa(),
						partecipazione.isRti(),
						this);
				boolean controlliOK = true;
				
				// controllo la partita iva, che puo' essere facoltativa per il
				// libero professionista ed impresa sociale se previsto da configurazione, 
				// mentre per tutti gli altri casi risulta obbligatoria
				controlliOK = controlliOK && validator.validateRequiredInputField(this, id); 
	
				if (this.getFieldErrors().size() > 0) {
					return;
				}

				// CONTROLLI APPLICATIVI SUI DATI
				controlliOK = controlliOK && validator.validateInputRagioneSociale(this, id);
				controlliOK = controlliOK && validator.validateInputCodiceFiscale(this, id); 
				controlliOK = controlliOK && validator.validateInputPartitaIVA(this, id);
				
				if(!controlliOK) {
					return;
				}	
				
				// verifica che le imprese ausiliarie non compaiano gia' nell'RTI
				IImpresa impresaInRti = validator.isImpreseAusiliariePresentiInRti();
				if(impresaInRti != null) {
					String cf = "2".equals(impresaInRti.getAmbitoTerritoriale()) 
										   ? impresaInRti.getIdFiscaleEstero()
										   : impresaInRti.getCodiceFiscale();
					this.addActionError(this.getText("Errors.impreseAusiliarie.impresaPresenteInRTI",
													 new String[] { impresaInRti.getRagioneSociale(), cf }));
					return;
				}
				
			} catch (Throwable t) {
				throw new RuntimeException("Errore durante la verifica dei dati richiesti per l'impresa "
						+ this.getRagioneSociale(), t);
			}
		}
	}
		
	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if(partecipazione != null 
		   && (null != this.getCurrentUser() && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))
		) {
			updateHelperSessione();
			
			// sono stati inseriti dei dati per una impresa ausiliaria, si procede al salvataggio
			if (!readOnly && this.ragioneSociale.length() > 0) {
				if( !addImpresaAusiliaria(this.id) ) {
					target = INPUT;
				}
			}
			
			// se c'e' avvalimento, verifica che ci sia almeno 1 impresa nell'elenco
			// se NON c'è avvalimento si resetta la lista delle imprese ausiliarie
			if(partecipazione.isAvvalimento()) {
				int n = (partecipazione.getImpreseAusiliarie() != null ? partecipazione.getImpreseAusiliarie().size() : 0);
				if(n <= 0) {
					this.addActionError(this.getText("Errors.impreseAusiliarie.noImpreseAusiliariaDefinite"));
					target = INPUT;
				}
			} else {
				partecipazione.getImpreseAusiliarie().clear();
			}
			
			if(SUCCESS.equals(target)) {
				String newTarget = partecipazione.getNextStepTarget(WizardPartecipazioneHelper.STEP_AVVALIMENTO);
				target = (newTarget != null ? newTarget : target);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = "back"; 

		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if (partecipazione != null 
			&& (null != this.getCurrentUser() && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))) 
		{
			String newTarget = partecipazione.getPreviousStepTarget(WizardPartecipazioneHelper.STEP_AVVALIMENTO);
			target = (newTarget != null ? newTarget : target);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String add() {
		return "refresh";
	}

	/**
	 * inserisci una nuova impresa ausiliaria
	 */
	public String insert() {
		String target = "refresh";
		
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if (partecipazione == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			updateHelperSessione();
			
			if( !addImpresaAusiliaria(null) ) {
				target = INPUT;
			}
		}
		
		return target;
	}

	/**
	 * aggiorna un'impresa ausiliaria della lista
	 */
	public String save() {
		String target = "refresh";
		
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if (partecipazione == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			updateHelperSessione();
			
			if( !addImpresaAusiliaria(this.id) ) {
				target = INPUT;
			}
		}
		
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String confirmDelete() {
		String target = "refresh";
		this.delete = true;
		//updateHelperSessione();
		return target;
	}

	/**
	 * elimina un'impresa ausiliaria della lista 
	 */
	@SkipValidation
	public String delete() {
		String target = "refresh";
		
		WizardPartecipazioneHelper partecipazione = getHelperFromSession();
		if (partecipazione == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			updateHelperSessione();
			
			if (this.idDelete != null) {
				partecipazione.getImpreseAusiliarie().remove(Integer.parseInt(this.idDelete));
			}
		}
		
		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String modify() {
		return "modify";
	}

}
