package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaAggiornabileType;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;

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
public class ProcessPageSoggettiImpresaAction extends EncodedDataAction implements ISoggettoImpresa, SessionAware {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -5594785672430610107L;

	protected Map<String, Object> session;
	private CustomConfigManager customConfigManager;
	private String id;
	private String idDelete;
	private boolean esistente;
	private boolean solaLettura;
	private String tipoSoggetto;
	private String tipoSoggettoDelete;
	private String dataInizioIncarico;
	private String dataFineIncarico;
	private String responsabileDichiarazioni;
	private String qualifica;
	private String cognome;
	private String nome;
	private String titolo;
	private String codiceFiscale;
	private String sesso;
	private String indirizzo;
	private String numCivico;
	private String cap;
	private String comune;
	private String provincia;
	private String nazione;
	private String dataNascita;
	private String comuneNascita;
	private String provinciaNascita;

	private String tipologiaAlboProf;
	private String numIscrizioneAlboProf;
	private String dataIscrizioneAlboProf;
	private String provinciaIscrizioneAlboProf;
	private String tipologiaCassaPrevidenza;
	private String numMatricolaCassaPrevidenza;

	private String note;

	private boolean obblIscrizione;

	private boolean delete;

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
	/**
	 * campo speciale contenente la concatenazione di tipologia soggetto e
	 * qualifica, e serve solamente per l'interfaccia web.
	 */
	private String soggettoQualifica;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

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
	 * @return the esistente
	 */
	@Override
	public boolean isEsistente() {
		return esistente;
	}

	/**
	 * @param esistente the esistente to set
	 */
	@Override
	public void setEsistente(boolean esistente) {
		this.esistente = esistente;
	}

	/**
	 * @return the tipoSoggetto
	 */
	@Override
	public String getTipoSoggetto() {
		return tipoSoggetto;
	}

	/**
	 * @param tipoSoggetto the tipoSoggetto to set
	 */
	@Override
	public void setTipoSoggetto(String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}

	/**
	 * @return the tipoSoggettoDelete
	 */
	public String getTipoSoggettoDelete() {
		return tipoSoggettoDelete;
	}

	/**
	 * @param tipoSoggettoDelete the tipoSoggettoDelete to set
	 */
	public void setTipoSoggettoDelete(String tipoSoggettoDelete) {
		this.tipoSoggettoDelete = tipoSoggettoDelete;
	}

	/**
	 * @return the dataInizioIncarico
	 */
	@Override
	public String getDataInizioIncarico() {
		return dataInizioIncarico;
	}

	/**
	 * @param dataInizioIncarico the dataInizioIncarico to set
	 */
	@Override
	public void setDataInizioIncarico(String dataInizioIncarico) {
		this.dataInizioIncarico = dataInizioIncarico;
	}

	/**
	 * @return the dataFineIncarico
	 */
	@Override
	public String getDataFineIncarico() {
		return dataFineIncarico;
	}

	/**
	 * @param dataFineIncarico the dataFineIncarico to set
	 */
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

	/**
	 * @return the responsabileDichiarazioni
	 */
	@Override
	public String getResponsabileDichiarazioni() {
		return responsabileDichiarazioni;
	}

	/**
	 * @param responsabileDichiarazioni the responsabileDichiarazioni to set
	 */
	@Override
	public void setResponsabileDichiarazioni(String responsabileDichiarazioni) {
		this.responsabileDichiarazioni = responsabileDichiarazioni;
	}

	/**
	 * @return the cognome
	 */
	@Override
	public String getCognome() {
		return cognome;
	}

	/**
	 * @param cognome the cognome to set
	 */
	@Override
	public void setCognome(String cognome) {
		this.cognome = cognome.trim();
	}

	/**
	 * @return the nome
	 */
	@Override
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
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

	/**
	 * @return the codiceFiscale
	 */
	@Override
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * @param codiceFiscale the codiceFiscale to set
	 */
	@Override
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale.trim().toUpperCase();
	}

	/**
	 * @return the sesso
	 */
	@Override
	public String getSesso() {
		return sesso;
	}

	/**
	 * @param sesso the sesso to set
	 */
	@Override
	public void setSesso(String sesso) {
		this.sesso = sesso;
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
	 * @return the dataNascita
	 */
	@Override
	public String getDataNascita() {
		return dataNascita;
	}

	/**
	 * @param dataNascita the dataNascita to set
	 */
	@Override
	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	/**
	 * @return the comuneNascita
	 */
	@Override
	public String getComuneNascita() {
		return comuneNascita;
	}

	/**
	 * @param comuneNascita the comuneNascita to set
	 */
	@Override
	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita.trim();
	}

	/**
	 * @return the provinciaNascita
	 */
	@Override
	public String getProvinciaNascita() {
		return provinciaNascita;
	}

	/**
	 * @param provinciaNascita the provinciaNascita to set
	 */
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

	/**
	 * @return the obblIscrizione
	 */
	public boolean isObblIscrizione() {
		return obblIscrizione;
	}

	/**
	 * @param obblIscrizione the obblIscrizione to set
	 */
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
	
	/**
	 * @return the delete
	 */
	public boolean isDelete() {
		return delete;
	}
	
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
			
			if(this.soggettoQualifica.length() > 0) {
				// sono stati inseriti dei dati, si procede al salvataggio						
				if (this.id != null) {
					// aggiorna i dati in sessione (codice preso da save)
					ISoggettoImpresa soggetto = null;					

					List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(this.tipoSoggetto);					
					if(listaSoggetti != null) { 
						if( !helper.isSoggettoDuplicato(								
								this.codiceFiscale, 
								this.soggettoQualifica,
								this.dataInizioIncarico,
								this.dataFineIncarico,
								listaSoggetti,
								Integer.parseInt(this.id),
								username) ) {
							soggetto = listaSoggetti.get(Integer.parseInt(this.id));
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
					
					List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(this.tipoSoggetto);					
					if(listaSoggetti != null) {					
						if( !helper.isSoggettoDuplicato(								
								soggetto.getCodiceFiscale(), 
								this.soggettoQualifica,
								this.dataInizioIncarico,
								this.dataFineIncarico,
								listaSoggetti,
								-1,
								username) ) {
							listaSoggetti.add(soggetto);
							addSoggetto = true;
						}
					}
				}
			}

			// ci deve essere almeno un legale rappresentante
			if (helper.getLegaliRappresentantiImpresa().isEmpty()) {
				this.addActionError(this.getText("Errors.legaleRappresentanteRequired"));
				target = INPUT;
			}

			// ci deve essere almeno un soggetto autorizzato alla firma delle dichiarazioni
			int numeroResponsabili = helper.contaResponsabiliDichiarazioni();
			if (numeroResponsabili == 0) {
				this.addActionError(this.getText("Errors.0responsabiliDichiarazioni"));
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
							"Errore durante la ricerca delle proprietà di visualizzazione dello step ulteriori dati impresa");
				}
				
				
			}
			
		}
		return target;
	}

	/**
	 * Annulla la procedura guida
	 *
	 * @return ta.
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
					"Errore durante la ricerca delle proprietà di visualizzazione dello step ulteriori dati impresa");
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
				// verifica solo i soggetti che NON provengono da B.O.
				if (StringUtils.stripToNull(soggetto.getDataInizioIncarico()) == null
					|| StringUtils.stripToNull(soggetto.getCognome()) == null
					|| StringUtils.stripToNull(soggetto.getNome()) == null
					|| StringUtils.stripToNull(soggetto.getCodiceFiscale()) == null
					|| StringUtils.stripToNull(soggetto.getSesso()) == null
					|| StringUtils.stripToNull(soggetto.getIndirizzo()) == null
					|| StringUtils.stripToNull(soggetto.getNumCivico()) == null
					|| (StringUtils.stripToNull(soggetto.getCap()) == null && "Italia".equalsIgnoreCase(soggetto.getNazione()))
					|| StringUtils.stripToNull(soggetto.getComune()) == null
					|| !( (!"Italia".equalsIgnoreCase(soggetto.getNazione()) && StringUtils.stripToNull(soggetto.getProvincia()) == null) ||
						  ("Italia".equalsIgnoreCase(soggetto.getNazione()) && StringUtils.stripToNull(soggetto.getProvincia()) != null) )
					|| StringUtils.stripToNull(soggetto.getNazione()) == null
					|| StringUtils.stripToNull(soggetto.getDataNascita()) == null
					|| StringUtils.stripToNull(soggetto.getComuneNascita()) == null) {
	
					if (CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
									.equals(soggetto.getTipoSoggetto())) {
						qualifica = "Legale rappresentante";
					} else if (CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
									.equals(soggetto.getTipoSoggetto())) {
						qualifica = "Direttore tecnico";
					} else if (CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE
									.equals(soggetto.getTipoSoggetto())) {
						qualifica = this.getMaps().get("tipiCollaborazione")
										.get(soggetto.getQualifica());
					} else if (CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA
									.equals(soggetto.getTipoSoggetto())) {
						qualifica = this.getMaps().get("tipiAltraCarica")
										.get(soggetto.getSoggettoQualifica());
					}
					this.addActionError(this.getText(
									"Errors.datiSoggettoIncompleti",
									new String[]{
										(soggetto.getCognome() + " " + soggetto
										.getNome()), qualifica}));
					
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
	 * Effettua i controlli sulle date incarico del soggetto editato
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
			this.addFieldError("dataInizioIncarico", this
							.getText("Errors.invalidDataFineNextDataInizioIncarico"));
		}
		
		boolean impresaItaliana = "ITALIA".equalsIgnoreCase(this.getNazione());
		
		if (!"".equals(this.getCodiceFiscale())
			&& !UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), impresaItaliana)) {
			this.addFieldError(
							"codiceFiscale",
							this.getText("Errors.wrongField",
											new String[]{this.getTextFromDB("codiceFiscale")}));
		}
		if (CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE.equals(this.tipoSoggetto) && "1".equals(this.responsabileDichiarazioni)) {
			this.addFieldError(
							"responsabileDichiarazioni",
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
						this.codiceFiscale, 
						this.soggettoQualifica,
						this.dataInizioIncarico,
						this.dataFineIncarico,						
						username);
				
				if(soggettoDuplicato != null) {
					String denominazione = null;
					String inizioIncarito = null;
					String fineIncarito = null;
					if(soggettoDuplicato instanceof ISoggettoImpresa) { 
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
						(!StringUtils.isEmpty(inizioIncarito) ? this.getI18nLabel("LABEL_DA_DATA") + " " + inizioIncarito + " " : "") +
						(!StringUtils.isEmpty(fineIncarito) ? this.getI18nLabel("LABEL_A_DATA") + " " + fineIncarito : "");
					
					this.addActionError(this.getText("Errors.soggettoDuplicato", 
							new String[] {periodoIncarico} ));
				}
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
							soggetto.getDataInizioIncarico(),
							soggetto.getDataFineIncarico(),
							listaSoggetti,
							-1,
							username) ) {
						soggetto.setSolaLettura(false);
						listaSoggetti.add(soggetto);
					}
				}
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
			// aggiorna i dati in sessione solo se non si modifica un record
			// definendo un duplicato
			ISoggettoImpresa soggetto = null;
			String username = (this.getCurrentUser() != null ? this.getCurrentUser().getUsername() : "");
			
			List<ISoggettoImpresa> listaSoggetti = helper.getSoggettiImpresa(this.tipoSoggetto);
			if(listaSoggetti != null) {
				if( !helper.isSoggettoDuplicato(						
						this.codiceFiscale, 
						this.soggettoQualifica,
						this.dataInizioIncarico,
						this.dataFineIncarico,
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
			helper.removeSoggettoImpresa(
					this.tipoSoggettoDelete, 
					Integer.parseInt(this.idDelete));
		}
		return target;
	}

	@SkipValidation
	public String modify() {
		return "modify";
	}

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
