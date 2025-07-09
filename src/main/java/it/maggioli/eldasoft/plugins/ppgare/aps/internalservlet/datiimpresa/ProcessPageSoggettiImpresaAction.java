package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaAggiornabileType;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.WithError;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Action di gestione delle operazioni nella pagina dei soggetti dell'impresa
 * nel wizard di aggiornamento dati impresa.<br/>
 * <b>NOTA: per sfruttare l'iniezione di dati tipizzati, non si estende
 * AbstractProcessPageAction bens&igrave; EncodedDataAction aggiungendo
 * l'implementazione dell'interfaccia SessionAware</b>.
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.MODIFICA_IMPRESA, EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA,
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageSoggettiImpresaAction extends EncodedDataAction implements ISoggettoImpresa, SessionAware {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -5594785672430610107L;

	protected Map<String, Object> session;
	private CustomConfigManager customConfigManager;
	@Validate(EParamValidation.DIGIT)
	private String id;
	@Validate(EParamValidation.DIGIT)
	private String idDelete;
	private boolean esistente;
	private boolean solaLettura;
	@Validate(EParamValidation.DIGIT)
	private String tipoSoggetto;
	@Validate(EParamValidation.DIGIT)
	private String tipoSoggettoDelete;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataInizioIncarico;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataFineIncarico;
	@Validate(EParamValidation.SI_NO)
	private String responsabileDichiarazioni;
	@Validate(EParamValidation.QUALIFICA)
	private String qualifica;				// (vedi soggettoQualifica)
	@Validate(EParamValidation.COGNOME)
	private String cognome;
	@Validate(EParamValidation.NOME)
	private String nome;
	@Validate(EParamValidation.TITOLO_TECNICO)
	private String titolo;
	@Validate(value = EParamValidation.CODICE_FISCALE_O_IDENTIFICATIVO, error = @WithError(fieldLabel = "CODICE_FISCALE_O_IDENTIFICATIVO"))
	private String codiceFiscale;
	@Validate(EParamValidation.GENDER)
	private String sesso;
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
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataNascita;
	@Validate(EParamValidation.COMUNE)
	private String comuneNascita;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaNascita;

	@Validate(EParamValidation.ALBO)
	private String tipologiaAlboProf;
	@Validate(EParamValidation.NUMERO_ISCR_ALBO)
	private String numIscrizioneAlboProf;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataIscrizioneAlboProf;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaIscrizioneAlboProf;
	@Validate(EParamValidation.CASSA_PREVIDENZIALE)
	private String tipologiaCassaPrevidenza;
	@Validate(EParamValidation.NUM_CASSA_PREVIDENZIALE)
	private String numMatricolaCassaPrevidenza;

	@Validate(EParamValidation.NOTE)
	private String note;

	private boolean obblIscrizione;

	private boolean delete;

	/**
	 * campo speciale contenente la concatenazione di tipologia soggetto e
	 * qualifica, e serve solamente per l'interfaccia web.
	 * (legali e direttori (1-,2-), altre cariche (3-1, 3-2, ...), collaboratori (4-1, 4-2, ...) )
	 */
	@Validate(EParamValidation.SPEC_SOGG_QUALIFICA)
	private String soggettoQualifica;

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = StringUtils.stripToNull(id);
	}

	public String getIdDelete() {
		return idDelete;
	}

	public void setIdDelete(String idDelete) {
		this.idDelete = idDelete;
	}

	@Override
	public boolean isEsistente() {
		return esistente;
	}

	@Override
	public void setEsistente(boolean esistente) {
		this.esistente = esistente;
	}

	@Override
	public String getTipoSoggetto() {
		return tipoSoggetto;
	}

	@Override
	public void setTipoSoggetto(String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}

	public String getTipoSoggettoDelete() {
		return tipoSoggettoDelete;
	}

	public void setTipoSoggettoDelete(String tipoSoggettoDelete) {
		this.tipoSoggettoDelete = tipoSoggettoDelete;
	}

	@Override
	public String getDataInizioIncarico() {
		return dataInizioIncarico;
	}

	@Override
	public void setDataInizioIncarico(String dataInizioIncarico) {
		this.dataInizioIncarico = dataInizioIncarico;
	}

	@Override
	public String getDataFineIncarico() {
		return dataFineIncarico;
	}

	@Override
	public void setDataFineIncarico(String dataFineIncarico) {
		this.dataFineIncarico = dataFineIncarico;
	}

	@Override
	public String getQualifica() {
		return qualifica;
	}

	@Override
	public void setQualifica(String qualifica) {
		this.qualifica = qualifica;
	}

	@Override
	public String getResponsabileDichiarazioni() {
		return responsabileDichiarazioni;
	}

	@Override
	public void setResponsabileDichiarazioni(String responsabileDichiarazioni) {
		this.responsabileDichiarazioni = responsabileDichiarazioni;
	}

	@Override
	public String getCognome() {
		return cognome;
	}

	@Override
	public void setCognome(String cognome) {
		this.cognome = cognome.trim();
	}

	@Override
	public String getNome() {
		return nome;
	}

	@Override
	public void setNome(String nome) {
		this.nome = nome.trim();
	}

	@Override
	public String getTitolo() {
		return titolo;
	}

	@Override
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	@Override
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	@Override
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale.trim().toUpperCase();
	}

	@Override
	public String getSesso() {
		return sesso;
	}

	@Override
	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	@Override
	public String getIndirizzo() {
		return indirizzo;
	}

	@Override
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo.trim();
	}

	@Override
	public String getNumCivico() {
		return numCivico;
	}

	@Override
	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico.trim();
	}

	@Override
	public String getCap() {
		return cap;
	}

	@Override
	public void setCap(String cap) {
		this.cap = cap;
	}

	@Override
	public String getComune() {
		return comune;
	}

	@Override
	public void setComune(String comune) {
		this.comune = comune.trim();
	}

	@Override
	public String getProvincia() {
		return provincia;
	}

	@Override
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	@Override
	public String getNazione() {
		return nazione;
	}

	@Override
	public void setNazione(String nazione) {
		this.nazione = nazione.trim();
	}

	@Override
	public String getDataNascita() {
		return dataNascita;
	}

	@Override
	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	@Override
	public String getComuneNascita() {
		return comuneNascita;
	}

	@Override
	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita.trim();
	}

	@Override
	public String getProvinciaNascita() {
		return provinciaNascita;
	}

	@Override
	public void setProvinciaNascita(String provinciaNascita) {
		this.provinciaNascita = provinciaNascita;
	}

	@Override
	public String getTipologiaAlboProf() {
		return tipologiaAlboProf;
	}

	@Override
	public void setTipologiaAlboProf(String tipologiaAlboProf) {
		this.tipologiaAlboProf = tipologiaAlboProf;
	}

	@Override
	public String getNumIscrizioneAlboProf() {
		return numIscrizioneAlboProf;
	}

	@Override
	public void setNumIscrizioneAlboProf(String numIscrizioneAlboProf) {
		this.numIscrizioneAlboProf = numIscrizioneAlboProf;
	}

	@Override
	public String getDataIscrizioneAlboProf() {
		return dataIscrizioneAlboProf;
	}

	@Override
	public void setDataIscrizioneAlboProf(String dataIscrizioneAlboProf) {
		this.dataIscrizioneAlboProf = dataIscrizioneAlboProf;
	}

	@Override
	public String getProvinciaIscrizioneAlboProf() {
		return provinciaIscrizioneAlboProf;
	}

	@Override
	public void setProvinciaIscrizioneAlboProf(String provinciaIscrizioneAlboProf) {
		this.provinciaIscrizioneAlboProf = provinciaIscrizioneAlboProf;
	}

	@Override
	public String getTipologiaCassaPrevidenza() {
		return tipologiaCassaPrevidenza;
	}

	@Override
	public void setTipologiaCassaPrevidenza(String tipologiaCassaPrevidenza) {
		this.tipologiaCassaPrevidenza = tipologiaCassaPrevidenza;
	}

	@Override
	public String getNumMatricolaCassaPrevidenza() {
		return numMatricolaCassaPrevidenza;
	}

	@Override
	public void setNumMatricolaCassaPrevidenza(String numMatricolaCassaPrevidenza) {
		this.numMatricolaCassaPrevidenza = numMatricolaCassaPrevidenza;
	}

	@Override
	public String getNote() {
		return note;
	}

	@Override
	public void setNote(String note) {
		this.note = note;
	}

	public boolean isObblIscrizione() {
		return obblIscrizione;
	}

	public void setObblIscrizione(boolean obblIscrizione) {
		this.obblIscrizione = obblIscrizione;
	}

	@Override
	public String getSoggettoQualifica() {
		return soggettoQualifica;
	}

	@Override
	public void setSoggettoQualifica(String soggettoQualifica) {
		this.soggettoQualifica = soggettoQualifica;
		
		if (StringUtils.isNotEmpty(this.soggettoQualifica)) {
			Scanner s = new Scanner(this.soggettoQualifica);
			s.useDelimiter(CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI);
			this.tipoSoggetto = s.next();
			if (s.hasNext()) {
				this.qualifica = StringUtils.stripToNull(s.next());
			}
		}
	}

	@Override
	public void setSolaLettura(boolean solaLettura) {
		this.solaLettura = solaLettura;
	}

	@Override
	public boolean isSolaLettura() {
		return this.solaLettura;
	}
	
	public boolean isDelete() {
		return delete;
	}

	/**
	 * Effettua i controlli di validazione sui dati modificati/inseriti del soggetto
	 */
	@Override
	public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		Calendar dataInizio = null;
		if (StringUtils.isNotBlank(this.getDataInizioIncarico())) {
			dataInizio = CalendarValidator.getInstance().validate(
							this.getDataInizioIncarico(), "dd/MM/yyyy");
			if (dataInizio != null
				&& dataInizio.compareTo(new GregorianCalendar()) > 0) {
				// data inizio > oggi
				this.addFieldError("dataInizioIncarico", this.getText(
								"Errors.invalidDataIncarico",
								new String[]{this.getTextFromDB("dataInizioIncarico")}));
			}
		}
		Calendar dataFine = null;
		if (StringUtils.isNotBlank(this.getDataFineIncarico())) {
			dataFine = CalendarValidator.getInstance().validate(
							this.getDataFineIncarico(), "dd/MM/yyyy");
			if (dataFine != null && dataFine.compareTo(new GregorianCalendar()) > 0) {
				// data fine > oggi
				this.addFieldError("dataFineIncarico", this.getText(
								"Errors.invalidDataIncarico",
								new String[]{this.getTextFromDB("dataFineIncarico")}));
			}
		}
		if (dataInizio != null && dataFine != null && dataInizio.compareTo(dataFine) > 0) {
			// data inizio > data fine
			this.addFieldError("dataInizioIncarico", 
								this.getText("Errors.invalidDataFineNextDataInizioIncarico"));
		}
		
		boolean impresaItaliana = "ITALIA".equalsIgnoreCase(nazione);
		if (!impresaItaliana)
			this.provincia = null;
		
		// OE Italiano: verifica la lunghezza del CF (16 caratteri)
		if (impresaItaliana) {
			if(StringUtils.isNotEmpty(codiceFiscale) && codiceFiscale.length() > 16) {
				this.addFieldError("codiceFiscale", this.getText(
									"Errors.stringlength", 
									new String[] { this.getTextFromDB("codiceFiscale"), "16" }));
			}
		}
		
		if (!"".equals(codiceFiscale)) {
			if( !UtilityFiscali.isValidCodiceFiscale(codiceFiscale, impresaItaliana) ) {
				this.addFieldError("codiceFiscale", this.getText(
									"Errors.wrongField",
									new String[]{this.getTextFromDB("codiceFiscale")}));
			}
		}

		if (CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE.equals(this.tipoSoggetto) && "1".equals(this.responsabileDichiarazioni)) {
			this.addFieldError("responsabileDichiarazioni",
							   this.getText("Errors.notCollaboratoreResponsabileDichiarazioni"));
		}
		
		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper != null) {
			int idSoggetto = (StringUtils.isNotBlank(this.id) ? Integer.parseInt(this.id) : -1);
			String username = (this.getCurrentUser() != null ? this.getCurrentUser().getUsername() : "");
			
			List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(this.tipoSoggetto);
			if (listaSoggetti != null) {
				Object soggettoDuplicato = helper.isSoggettoDuplicato(
						listaSoggetti,
						idSoggetto,
						codiceFiscale,
						soggettoQualifica,
						nome,
						cognome,
						dataNascita,
						comuneNascita,
						provinciaNascita,
						sesso,
						dataInizioIncarico,
						dataFineIncarico,
						username);

				String denominazione = null;
				String inizioIncarito = null;
				String fineIncarito = null;
				boolean isError = false;

				if(soggettoDuplicato != null) {
					isError = soggettoDuplicato instanceof String;
					if (!isError) {
						if (soggettoDuplicato instanceof ISoggettoImpresa) {
							// duplicato presente a video...
//						if(idSoggetto >= 0) {
//							this.addActionError(this.getText("Errors.updateSoggettoDuplicato"));
//						} else {
//							this.addActionError(this.getText("Errors.insertSoggettoDuplicato"));
//						}
							ISoggettoImpresa soggetto = (ISoggettoImpresa) soggettoDuplicato;
							denominazione = soggetto.getCognome() + " " + soggetto.getNome();
							inizioIncarito = soggetto.getDataInizioIncarico();
							fineIncarito = soggetto.getDataFineIncarico();
						} else {
							// duplicato presente in B.O.
							ReferenteImpresaAggiornabileType soggetto = (ReferenteImpresaAggiornabileType) soggettoDuplicato;
							SimpleDateFormat DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
							denominazione = soggetto.getCognome() + " " + soggetto.getNome();
							inizioIncarito = (soggetto.getDataInizioIncarico() != null
											  ? DDMMYYYY.format(soggetto.getDataInizioIncarico().getTime())
											  : "");
							fineIncarito = (soggetto.getDataFineIncarico() != null
											? DDMMYYYY.format(soggetto.getDataFineIncarico().getTime())
											: "");
						}
						String periodoIncarico =
								(!StringUtils.isEmpty(inizioIncarito) ? getI18nLabel("LABEL_DA_DATA") + " " + inizioIncarito + " " : "") +
										(!StringUtils.isEmpty(fineIncarito) ? getI18nLabel("LABEL_A_DATA") + " " + fineIncarito : "");
						addActionError(getText("Errors.soggettoDuplicato",
											   new String[] { periodoIncarico }));
					} else
						addActionError(getText((String) soggettoDuplicato));
				} else if (overrideSubjects(helper))
					addActionMessage(getText("warning.soggetti.sovrascritti"));
			
			}
			
			// verifica il cap...
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
		}
	}

	/**
	 * passa allo step successivo dei dati impresa
	 */
	//@SkipValidation
	public String next() {
		String target = SUCCESS;
		
		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			String username = (this.getCurrentUser() != null ? this.getCurrentUser().getUsername() : "");

			boolean addSoggetto = false; 
			
			if(soggettoQualifica.length() > 0) {
				// sono stati inseriti dei dati, si procede al salvataggio
				if (id != null) {
					// aggiorna i dati in sessione (codice preso da save)
					ISoggettoImpresa soggetto = null;

					List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(tipoSoggetto);
					if (listaSoggetti != null) {
						if (!helper.isSoggettoDuplicato(
								codiceFiscale,
								soggettoQualifica,
								nome,
								cognome,
								dataNascita,
								comuneNascita,
								provinciaNascita,
								sesso,
								dataInizioIncarico,
								dataFineIncarico,
								listaSoggetti,
								Integer.parseInt(id),
								username)) {
							soggetto = listaSoggetti.get(Integer.parseInt(id));
						}
					}
					
					if(soggetto != null) {
						WizardDatiImpresaHelper.synchronizeSoggettoImpresa(
								this,
								soggetto);
					}
				} else {
					// aggiunge i dati in sessione (codice preso da insert)
					ISoggettoImpresa soggetto = new SoggettoImpresaHelper();
					
					WizardDatiImpresaHelper.synchronizeSoggettoImpresa(
							this,
							soggetto);
					
					List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(tipoSoggetto);
					if(listaSoggetti != null) {
						if( !helper.isSoggettoDuplicato(
								soggetto.getCodiceFiscale(), 
								soggettoQualifica,
								nome,
								cognome,
								dataNascita,
								comuneNascita,
								provinciaNascita,
								sesso,
								dataInizioIncarico,
								dataFineIncarico,
								listaSoggetti,
								-1,
								username) ) {
							listaSoggetti.add(soggetto);
//							if (overrideSubjects(helper))
//								addActionMessage("Messaggio di warning");
							addSoggetto = true;
						}
					}
				}
			}

			// ci deve essere almeno un legale rappresentante
			if (helper.getLegaliRappresentantiImpresa().isEmpty()) {
				addActionError(getText("Errors.legaleRappresentanteRequired"));
				target = INPUT;
			}

			// ci deve essere almeno un soggetto autorizzato alla firma delle dichiarazioni e un legale rappresentante attivo
			int numeroResponsabili = helper.contaResponsabiliDichiarazioni();
			boolean atLeatOneLegaleRappresentanteActive = helper.getLegaliRappresentantiImpresa().stream().anyMatch(el ->  StringUtils.isEmpty(el.getDataFineIncarico()));
			if (numeroResponsabili == 0) {
				this.addActionError(this.getText("Errors.0responsabiliDichiarazioni"));
				target = INPUT;
			}

			if (!atLeatOneLegaleRappresentanteActive) {
				this.addActionError(this.getText("Errors.legaleRappresentanteRequiredAttivo"));
				target = INPUT;
			}

			target = checkDatiObbligatoriSoggetti(
							helper.getLegaliRappresentantiImpresa(), 
							helper.getDatiPrincipaliImpresa().getNaturaGiuridica(),
							target);
			target = checkDatiObbligatoriSoggetti(
							helper.getDirettoriTecniciImpresa(), 
							helper.getDatiPrincipaliImpresa().getNaturaGiuridica(),
							target);
			target = checkDatiObbligatoriSoggetti(
							helper.getAltreCaricheImpresa(), 
							helper.getDatiPrincipaliImpresa().getNaturaGiuridica(),
							target);
			target = checkDatiObbligatoriSoggetti(
							helper.getCollaboratoriImpresa(), 
							helper.getDatiPrincipaliImpresa().getNaturaGiuridica(),
							target);
			
			// nel caso in cui si aggiunga un nuovo soggetto ma ci siano degli 
			// errori di validazione, post inserimento, 
			// il soggetto è già stato inserito in lista e i campi della form
			// vanno ripresentati vuoti...
			if(!SUCCESS.equals(target) && addSoggetto) {
				target = "refresh";
			}
			if(SUCCESS.equals(target)){
				
				try {
					if(!customConfigManager.isVisible("IMPRESA-DATIULT", "STEP")){
						target = "successNoDatiUlteriori";
					}
				}catch (Exception e) {
					// Configurazione sbagliata
					ApsSystemUtils.logThrowable(e, this, "next",
							"Errore durante la ricerca delle propriet? di visualizzazione dello step ulteriori dati impresa");
				}
				
				
			}
			
		}
		return target;
	}

	private boolean overrideSubjects(WizardDatiImpresaHelper helper) {
		return overrideSubjects(helper.getLegaliRappresentantiImpresa())
				| overrideSubjects(helper.getAltreCaricheImpresa())
				| overrideSubjects(helper.getDirettoriTecniciImpresa())
				| overrideSubjects(helper.getCollaboratoriImpresa());
	}
	private boolean overrideSubjects(List<ISoggettoImpresa> soggettoImpresa) {
		return CollectionUtils.isNotEmpty(soggettoImpresa)
				&& IntStream.range(0, soggettoImpresa.size())
					.filter(index ->StringUtils.equals(soggettoImpresa.get(index).getCodiceFiscale(), codiceFiscale))
					.mapToObj(index -> overrideSubject(soggettoImpresa.get(index), index))
				.anyMatch(i -> i);
	}

	private boolean overrideSubject(ISoggettoImpresa soggettoImpresa, int index) {
		boolean overrided = false;

		if (!StringUtils.equals(nome, soggettoImpresa.getNome())
				&& !(StringUtils.isEmpty(nome) && StringUtils.isEmpty(soggettoImpresa.getNome()))) {
			soggettoImpresa.setNome(nome);
			overrided  = true;
		}
		if (!StringUtils.equals(cognome, soggettoImpresa.getCognome())
				&& !(StringUtils.isEmpty(cognome) && StringUtils.isEmpty(soggettoImpresa.getCognome()))) {
			soggettoImpresa.setCognome(cognome);
			overrided  = true;
		}
		if (!StringUtils.equals(sesso, soggettoImpresa.getSesso())
				&& !(StringUtils.isEmpty(sesso) && StringUtils.isEmpty(soggettoImpresa.getSesso()))) {
			soggettoImpresa.setSesso(sesso);
			overrided  = true;
		}
		if (!StringUtils.equals(titolo, soggettoImpresa.getTitolo())
				&& !(StringUtils.isEmpty(titolo) && StringUtils.isEmpty(soggettoImpresa.getTitolo()))) {
			soggettoImpresa.setTitolo(titolo);
			overrided  = true;
		}
		if (!StringUtils.equals(indirizzo, soggettoImpresa.getIndirizzo())
				&& !(StringUtils.isEmpty(indirizzo) && StringUtils.isEmpty(soggettoImpresa.getIndirizzo()))) {
			soggettoImpresa.setIndirizzo(indirizzo);
			overrided  = true;
		}
		if (!StringUtils.equals(comune, soggettoImpresa.getComune())
				&& !(StringUtils.isEmpty(comune) && StringUtils.isEmpty(soggettoImpresa.getComune()))) {
			soggettoImpresa.setComune(comune);
			overrided  = true;
		}
		if (!StringUtils.equals(numCivico, soggettoImpresa.getNumCivico())
				&& !(StringUtils.isEmpty(numCivico) && StringUtils.isEmpty(soggettoImpresa.getNumCivico()))) {
			soggettoImpresa.setNumCivico(numCivico);
			overrided  = true;
		}
		if (!StringUtils.equals(cap, soggettoImpresa.getCap())
				&& !(StringUtils.isEmpty(cap) && StringUtils.isEmpty(soggettoImpresa.getCap()))) {
			soggettoImpresa.setCap(cap);
			overrided  = true;
		}
		if (!StringUtils.equals(nazione, soggettoImpresa.getNazione())
				&& !(StringUtils.isEmpty(nazione) && StringUtils.isEmpty(soggettoImpresa.getNazione()))) {
			soggettoImpresa.setNazione(nazione);
			overrided  = true;
		}
		if (!StringUtils.equals(provincia, soggettoImpresa.getProvincia())
				&& !(StringUtils.isEmpty(provincia) && StringUtils.isEmpty(soggettoImpresa.getProvincia()))) {
			soggettoImpresa.setProvincia(provincia);
			overrided  = true;
		}
		if (!StringUtils.equals(dataNascita, soggettoImpresa.getDataNascita())
				&& !(StringUtils.isEmpty(dataNascita) && StringUtils.isEmpty(soggettoImpresa.getDataNascita()))) {
			soggettoImpresa.setDataNascita(dataNascita);
			overrided  = true;
		}
		if (!StringUtils.equals(comuneNascita, soggettoImpresa.getComuneNascita())
				&& !(StringUtils.isEmpty(comuneNascita) && StringUtils.isEmpty(soggettoImpresa.getComuneNascita()))) {
			soggettoImpresa.setIndirizzo(comuneNascita);
			overrided  = true;
		}
		if (!StringUtils.equals(provinciaNascita, soggettoImpresa.getProvinciaNascita())
				&& !(StringUtils.isEmpty(provinciaNascita) && StringUtils.isEmpty(soggettoImpresa.getProvinciaNascita()))) {
			soggettoImpresa.setProvinciaNascita(provinciaNascita);
			overrided  = true;
		}
		if (!StringUtils.equals(tipologiaAlboProf, soggettoImpresa.getTipologiaAlboProf())
				&& !(StringUtils.isEmpty(tipologiaAlboProf) && StringUtils.isEmpty(soggettoImpresa.getTipologiaAlboProf()))) {
			soggettoImpresa.setTipologiaAlboProf(tipologiaAlboProf);
			overrided  = true;
		}
		if (!StringUtils.equals(numIscrizioneAlboProf, soggettoImpresa.getNumIscrizioneAlboProf())
				&& !(StringUtils.isEmpty(numIscrizioneAlboProf) && StringUtils.isEmpty(soggettoImpresa.getNumIscrizioneAlboProf()))) {
			soggettoImpresa.setNumIscrizioneAlboProf(numIscrizioneAlboProf);
			overrided  = true;
		}
		if (!StringUtils.equals(dataIscrizioneAlboProf, soggettoImpresa.getDataIscrizioneAlboProf())
				&& !(StringUtils.isEmpty(dataIscrizioneAlboProf) && StringUtils.isEmpty(soggettoImpresa.getDataIscrizioneAlboProf()))) {
			soggettoImpresa.setDataIscrizioneAlboProf(dataIscrizioneAlboProf);
			overrided  = true;
		}
		if (!StringUtils.equals(provinciaIscrizioneAlboProf, soggettoImpresa.getProvinciaIscrizioneAlboProf())
				&& !(StringUtils.isEmpty(provinciaIscrizioneAlboProf) && StringUtils.isEmpty(soggettoImpresa.getProvinciaIscrizioneAlboProf()))) {
			soggettoImpresa.setProvinciaIscrizioneAlboProf(provinciaIscrizioneAlboProf);
			overrided  = true;
		}
		if (!StringUtils.equals(tipologiaCassaPrevidenza, soggettoImpresa.getTipologiaCassaPrevidenza())
				&& !(StringUtils.isEmpty(tipologiaCassaPrevidenza) && StringUtils.isEmpty(soggettoImpresa.getTipologiaCassaPrevidenza()))) {
			soggettoImpresa.setTipologiaCassaPrevidenza(tipologiaCassaPrevidenza);
			overrided  = true;
		}
		if (!StringUtils.equals(numMatricolaCassaPrevidenza, soggettoImpresa.getNumMatricolaCassaPrevidenza())
				&& !(StringUtils.isEmpty(numMatricolaCassaPrevidenza) && StringUtils.isEmpty(soggettoImpresa.getNumMatricolaCassaPrevidenza()))) {
			soggettoImpresa.setNumMatricolaCassaPrevidenza(numMatricolaCassaPrevidenza);
			overrided  = true;
		}
		if (!StringUtils.equals(note, soggettoImpresa.getNote())
				&& !(StringUtils.isEmpty(note) && StringUtils.isEmpty(soggettoImpresa.getNote()))) {
			soggettoImpresa.setNote(note);
			overrided  = true;
		}

		boolean isCurrent = id != null
							&& Integer.parseInt(id) == index
							&& StringUtils.equals(tipoSoggetto, soggettoImpresa.getTipoSoggetto());

		return !isCurrent && overrided;
	}

	/**
	 * Annulla la procedura guida
	 *
	 * @return 
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * Torna allo step precedente della procedura guidata.
	 *
	 * @return
	 */
	public String back() {
		String target = "back";
		try {
			if(!customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP")){
				target = "backNoIndirizzi";
			}
		}catch (Exception e) {
			// Configurazione sbagliata
			ApsSystemUtils.logThrowable(e, this, "next",
					"Errore durante la ricerca delle proprieta' di visualizzazione dello step ulteriori dati impresa");
		}
		return target;
	}

	/**
	 * Verifica, per i soggetti gestiti dal wizard, se tutti hanno i dati
	 * obbligatori valorizzati. Il problema non nasce dagli utenti inseriti o
	 * modificati da interfaccia bens&igrave; dai soggetti ricevuti dal backoffice
	 * ed inseriti in tale ambiente, in quanto potrebbero essere privi di alcuni
	 * dati obbligatori. Il portale prevede che i dati gestiti in esso siano
	 * consistenti, e quindi ne forza la valorizzazione.
	 *
	 * @param soggetti soggetti da controllare
	 * @param naturaGiuridica natura giuridica dell'impresa
	 * @param target target iniziale
	 * @return target eventualmente modificato
	 */
	private String checkDatiObbligatoriSoggetti(
			ArrayList<ISoggettoImpresa> soggetti, 
			String naturaGiuridica, 
			String target) {
		
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
			.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
					 ServletActionContext.getRequest());
		
		String qualifica = null;
		for (int i = 0; i < soggetti.size(); i++) {
			ISoggettoImpresa soggetto = soggetti.get(i);
			if( !soggetto.isSolaLettura() ) {
				
				// verifica solo i soggetti editabili che provengono da BO oppure appena inseriti
				if (CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE.equals(soggetto.getTipoSoggetto())) {
					qualifica = "Legale rappresentante";
				} else if (CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO.equals(soggetto.getTipoSoggetto())) {
					qualifica = "Direttore tecnico";
				} else if (CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE.equals(soggetto.getTipoSoggetto())) {
					qualifica = this.getMaps().get("tipiCollaborazione").get(soggetto.getSoggettoQualifica());
				} else if (CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA.equals(soggetto.getTipoSoggetto())) {
					qualifica = this.getMaps().get("tipiAltraCarica").get(soggetto.getSoggettoQualifica());
				}
				
				if ((StringUtils.stripToNull(soggetto.getTipoSoggetto()) == null ||
					 (StringUtils.stripToNull(soggetto.getTipoSoggetto()) != null && StringUtils.stripToNull(qualifica) == null))	
					|| StringUtils.stripToNull(soggetto.getDataInizioIncarico()) == null
					|| StringUtils.stripToNull(soggetto.getCognome()) == null
					|| StringUtils.stripToNull(soggetto.getNome()) == null
					|| StringUtils.stripToNull(soggetto.getCodiceFiscale()) == null
					//|| StringUtils.stripToNull(soggetto.getSesso()) == null
					|| StringUtils.stripToNull(soggetto.getIndirizzo()) == null
					|| StringUtils.stripToNull(soggetto.getNumCivico()) == null
					|| (StringUtils.stripToNull(soggetto.getCap()) == null && "Italia".equalsIgnoreCase(soggetto.getNazione()))
					|| StringUtils.stripToNull(soggetto.getComune()) == null
					|| !( (!"Italia".equalsIgnoreCase(soggetto.getNazione()) && StringUtils.stripToNull(soggetto.getProvincia()) == null) ||
						  ("Italia".equalsIgnoreCase(soggetto.getNazione()) && StringUtils.stripToNull(soggetto.getProvincia()) != null) )
					|| StringUtils.stripToNull(soggetto.getNazione()) == null
					|| StringUtils.stripToNull(soggetto.getDataNascita()) == null
					|| StringUtils.stripToNull(soggetto.getComuneNascita()) == null) {
	
					this.addActionError(this.getText(
									"Errors.datiSoggettoIncompleti",
									new String[]{
										(soggetto.getCognome() + " " + soggetto.getNome()), qualifica}));
					
					target = (!soggetto.isSolaLettura() ? INPUT : target);
				}
			}
			try {
				if ("7".equals(naturaGiuridica)
					&& CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE
									.equals(soggetto.getTipoSoggetto())
					&& "2".equals(soggetto.getQualifica())
					&& customConfigManager.isMandatory(
									"IMPRESA-DATIANAGR-SEZ", "ISCRIZIONEALBOPROF")) {
					if (!(StringUtils.isNotBlank(soggetto.getTipologiaAlboProf())
						  && StringUtils.isNotBlank(soggetto.getNumIscrizioneAlboProf())
						  && StringUtils.isNotBlank(soggetto.getDataIscrizioneAlboProf())
						  && StringUtils.isNotBlank(soggetto.getProvinciaIscrizioneAlboProf()))) {
						qualifica = this.getMaps().get("tipiCollaborazione").get(soggetto.getSoggettoQualifica());
						this.addActionError(this.getText(
										"Errors.datiSoggettoIncompleti",
										new String[]{
											(soggetto.getCognome() + " " + soggetto.getNome()), 
											 qualifica}));
						
						target = (!soggetto.isSolaLettura() ? INPUT : target);
					}
				}
			} catch (Exception e) {
				this.addActionError("Configurazione mancante");
				// il fatto che si faccia il check su una configurazione scritta
				// male deve provocare il fallimento della validazione in modo
				// da accorgermene
				target = INPUT;
			}
		}
		return target;
	}

	/**
	 * aggiungi un nuovo soggetto impresa
	 */
	@SkipValidation
	public String add() {
		return "refresh";
	}

	/**
	 * inserisci un nuovo soggetto impresa
	 */
	public String insert() {
		String target = "refresh";
		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.soggettoQualifica.length() > 0) {
				// aggiorna i dati in sessione solo se non si inserisce un
				// duplicato per la stessa tipologia di incarico
				ISoggettoImpresa soggetto = new SoggettoImpresaHelper();
				WizardDatiImpresaHelper.synchronizeSoggettoImpresa(
						this, 
						soggetto);
				
				String username = (this.getCurrentUser() != null ? this.getCurrentUser().getUsername() : "");
				
				List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(this.tipoSoggetto);
				if(listaSoggetti != null) {
					if( !helper.isSoggettoDuplicato(
							soggetto.getCodiceFiscale(), 
							soggetto.getSoggettoQualifica(), 
							soggetto.getNome(),
							soggetto.getCognome(),
							soggetto.getDataNascita(),
							soggetto.getComuneNascita(),
							soggetto.getProvinciaNascita(),
							soggetto.getSesso(),
							soggetto.getDataInizioIncarico(),
							soggetto.getDataFineIncarico(),
							listaSoggetti,
							-1,
							username) ) {
						soggetto.setSolaLettura(false);
//						if (overrideSubjects(helper))
//							addActionMessage("Messaggio di warning");
						listaSoggetti.add(soggetto);
					}
				}
			}
		}
		return target;
	}

	/**
	 * salva i dati di un soggetto impresa
	 */
	public String save() {
		String target = "refresh";

		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione solo se non si modifica un record
			// definendo un duplicato
			ISoggettoImpresa soggetto = null;
			String username = (this.getCurrentUser() != null ? this.getCurrentUser().getUsername() : "");
			
			List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(this.tipoSoggetto);
			if(listaSoggetti != null) {
				if( !helper.isSoggettoDuplicato(
						codiceFiscale,
						soggettoQualifica,
						nome,
						cognome,
						dataNascita,
						comuneNascita,
						provinciaNascita,
						sesso,
						dataInizioIncarico,
						dataFineIncarico,
						listaSoggetti,
						Integer.parseInt(this.id),
						username) ) {
					soggetto = listaSoggetti.get(Integer.parseInt(this.id));
				}
			}

			WizardDatiImpresaHelper.synchronizeSoggettoImpresa(
					this,
					soggetto);
			
//			if(soggetto.getDataFineIncarico()!=null){
//				if(!helper.getListaCessatiDaInterfaccia().contains(soggetto)){
//					helper.getListaCessatiDaInterfaccia().add(soggetto);
//				}
//			}
		}
		return target;
	}

	/**
	 * visualizza la domanda di conferma per la cancellazione di un soggetto impresa
	 */
	@SkipValidation
	public String confirmDelete() {
		String target = "refresh";
		this.delete = true;
		return target;
	}

	/**
	 * elimina un soggetto impresa
	 */
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
			helper.removeSoggettoImpresa(
					this.tipoSoggettoDelete, 
					Integer.parseInt(this.idDelete));
		}
		return target;
	}

	/**
	 * modifica i dati di un soggetto impresa
	 */
	@SkipValidation
	public String modify() {
		return "modify";
	}

	/**
	 * copia i dati di un soggetto impresa per un nuovo soggetto
	 */
	@SkipValidation
	public String copy() {
		return "copy";
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

}
