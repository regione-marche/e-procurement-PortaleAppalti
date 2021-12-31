package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

/**
 * Action di gestione dell'apertura della pagina dei soggetti dell'impresa nel
 * wizard di aggiornamento dati impresa
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class OpenPageSoggettiImpresaAction extends AbstractOpenPageAction
				implements ISoggettoImpresa {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4403348715696108482L;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	private String id;
	private boolean esistente;
	private boolean solaLettura;
	private String tipoSoggetto;
	private String dataInizioIncarico;
	private String dataFineIncarico;
	private String qualifica;
	private String responsabileDichiarazioni;
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

	/**
	 * campo speciale contenente la concatenazione di tipologia soggetto e
	 * qualifica, e serve solamente per l'interfaccia web.
	 */
	private String soggettoQualifica;

	// campi di controllo per informazioni teoricamente obbligatorie ma che nel
	// caso di backoffice con dati parziali, almeno la prima volta potrebbero
	// arrivare incompleti

	private boolean readonlyTipoSoggetto;
	private boolean readonlyDataInizioIncarico;
	private boolean readonlyDataFineIncarico;
	private boolean visibleDataFineIncarico;
	private boolean readonlyQualifica;
	private boolean readonlyResponsabileDichiarazioni;
	private boolean readonlyCognome;
	private boolean readonlyNome;
	private boolean readonlyTitolo;
	private boolean readonlyCodiceFiscale;
	private boolean readonlySesso;
	private boolean readonlyIndirizzo;
	private boolean readonlyNumCivico;
	private boolean readonlyCap;
	private boolean readonlyComune;
	private boolean readonlyProvincia;
	private boolean readonlyNazione;
	private boolean readonlyDataNascita;
	private boolean readonlyComuneNascita;
	private boolean readonlyProvinciaNascita;

	private boolean readonlyTipologiaAlboProf;
	private boolean readonlyNumIscrizioneAlboProf;
	private boolean readonlyDataIscrizioneAlboProf;
	private boolean readonlyProvinciaIscrizioneAlboProf;
	private boolean readonlyTipologiaCassaPrevidenza;
	private boolean readonlyNumMatricolaCassaPrevidenza;
	
	private boolean readonlyNote;
	
	private boolean allFieldAsReadonly;

	 /**
		 * @param appParamManager
		 *            the appParamManager to set
		 */
		public void setAppParamManager(IAppParamManager appParamManager) {
			this.appParamManager = appParamManager;
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
		this.id = id;
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
	 *
	 */
	@Override
	public void setTipoSoggetto(String tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
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
		this.cognome = cognome;
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
		this.nome = nome;
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
		this.codiceFiscale = codiceFiscale;
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
		this.indirizzo = indirizzo;
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
		this.numCivico = numCivico;
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
		this.comune = comune;
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
		this.nazione = nazione;
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
		this.comuneNascita = comuneNascita;
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

	/**
	 * @return the isReadonlyDataInizioIncarico
	 */
	public boolean isReadonlyDataInizioIncarico() {
		return readonlyDataInizioIncarico;
	}

	/**
	 * @return the isReadonlyCodiceFiscale
	 */
	public boolean isReadonlyCodiceFiscale() {
		return readonlyCodiceFiscale;
	}

	@Override
	public String openPage() {
		
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			if (this.esistente && this.id != null) {
				// si verifica nel momento in cui riapriamo la pagina in seguito
				// ad un errore in fase di edit
				ISoggettoImpresa soggetto;

				if (CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
								.equals(this.tipoSoggetto)) {
					soggetto = helper.getLegaliRappresentantiImpresa().get(
									Integer.parseInt(this.id));
				} else if (CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
								.equals(this.tipoSoggetto)) {
					soggetto = helper.getDirettoriTecniciImpresa().get(
									Integer.parseInt(this.id));
				} else if (CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA
								.equals(this.tipoSoggetto)) {
					soggetto = helper.getAltreCaricheImpresa().get(
									Integer.parseInt(this.id));
				} else {
					soggetto = helper.getCollaboratoriImpresa().get(
									Integer.parseInt(this.id));
				}
				
				this.setSoggettoFields(soggetto);
			}

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_SOGGETTI);
		}
		return this.getTarget();
	}

	/**
	 * Apre la pagina svuotando i dati del form di inserimento/modifica
	 *
	 * @return target
	 */
	public String openPageClear() {
		
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			// svuota i dati inseriti nella form in modo da riaprire la
			// form pulita
			WizardDatiImpresaHelper.resetSoggettoImpresa(this);
			this.id = null;
				
			/* --- nuovo soggetto --- */
			if(((Integer)appParamManager.getConfigurationValue("incarichiCessati.estraiNumAnniPrecedenti")).intValue() > 0) {
				this.setVisibleDataFineIncarico(true);
			}
		
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_SOGGETTI);
		}
		return this.getTarget();
	}

	public String openPageModify() {
		
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			
			// popola nel bean i dati presenti nell'elemento in sessione
			// individuato da id
			ISoggettoImpresa soggetto = null;

			if (CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
							.equals(this.tipoSoggetto)) {
				soggetto = helper.getLegaliRappresentantiImpresa().get(
								Integer.parseInt(this.id));
			} else if (CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
							.equals(this.tipoSoggetto)) {
				soggetto = helper.getDirettoriTecniciImpresa().get(
								Integer.parseInt(this.id));
			} else if (CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA
							.equals(this.tipoSoggetto)) {
				soggetto = helper.getAltreCaricheImpresa().get(
								Integer.parseInt(this.id));
			} else {
				soggetto = helper.getCollaboratoriImpresa().get(
								Integer.parseInt(this.id));
			}

			WizardDatiImpresaHelper.synchronizeSoggettoImpresa(soggetto, this);
			
			this.setSoggettoFields(soggetto);

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_SOGGETTI);
		}
		return this.getTarget();
	}

	public String openPageCopy() {
		
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			// popola nel bean i dati presenti nell'elemento in sessione
			// individuato da id
			ISoggettoImpresa soggetto = null;
			
			List<ISoggettoImpresa> listaSoggetti =  helper.getSoggettiImpresa(this.tipoSoggetto);
			if(listaSoggetti != null) {
				soggetto = listaSoggetti.get(Integer.parseInt(this.id));
			}
			
			// si clona il soggetto ad esclusione dei dati dell'incarico e 
			// la sezione per l'azionista
			WizardDatiImpresaHelper.synchronizeSoggettoImpresa(soggetto, this);
			
			this.esistente = false;
			this.soggettoQualifica = null;
			this.id = null;
			this.tipoSoggetto = null;
			this.dataInizioIncarico = null;
			this.dataFineIncarico = null;
			this.setVisibleDataFineIncarico(true);
			
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_SOGGETTI);
		}
		return this.getTarget();
	}
	
	/**
	 * prepara i campi l'inserimento/modifica di un soggetto
	 * 	a) se da BO e in sola lettura e' solo visualizzato
	 *  b) se da BO e non sola lettura e' modificabile
	 *	c) se non da BO e' modificabile oppure un nuovo soggetto
	 */
	private void setSoggettoFields(ISoggettoImpresa soggetto) {
		
		this.solaLettura = soggetto.isSolaLettura();
		
		if(this.isEsistente() && soggetto.isSolaLettura()) {
			// --- soggetto cessato acquisito a BO ---
			this.readonlyTipoSoggetto                = true;
			this.readonlyDataInizioIncarico          = true;
			this.readonlyDataFineIncarico            = true;
			this.readonlyQualifica                   = true;
			this.readonlyResponsabileDichiarazioni   = true;
			this.readonlyCognome                     = true;
			this.readonlyNome                        = true;
			this.readonlyTitolo                      = true;
			this.readonlyCodiceFiscale               = true;
			this.readonlySesso                       = true;
			this.readonlyIndirizzo                   = true;
			this.readonlyNumCivico                   = true;
			this.readonlyCap                         = true;
			this.readonlyComune                      = true;
			this.readonlyProvincia                   = true;
			this.readonlyNazione                     = true;
			this.readonlyDataNascita                 = true;
			this.readonlyComuneNascita               = true;
			this.readonlyProvinciaNascita            = true;
			this.readonlyTipologiaAlboProf           = true;
			this.readonlyNumIscrizioneAlboProf       = true;
			this.readonlyDataIscrizioneAlboProf      = true;
			this.readonlyProvinciaIscrizioneAlboProf = true;
			this.readonlyTipologiaCassaPrevidenza    = true;
			this.readonlyNumMatricolaCassaPrevidenza = true;
			this.readonlyNote                        = true;
			this.setAllFieldAsReadonly(true);
			
		} else if(this.isEsistente() && !soggetto.isSolaLettura()) {
			// --- soggetto NON cessato acquisito a BO ---			
			SoggettoImpresaHelper soggettoHelper = (SoggettoImpresaHelper) soggetto;
			this.readonlyDataInizioIncarico = soggettoHelper.isReadonlyDataInizioIncarico(); 
			this.readonlyCodiceFiscale = soggettoHelper.isReadonlyCodiceFiscale();
			
		} else {
			// --- nuovo soggetto (inserito da Portal) ---
			if(((Integer)appParamManager.getConfigurationValue("incarichiCessati.estraiNumAnniPrecedenti")).intValue() > 0) {
				this.setVisibleDataFineIncarico(true);
			}
		}
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

	/**
	 * @return the readonlyTipoSoggetto
	 */
	public boolean isReadonlyTipoSoggetto() {
		return readonlyTipoSoggetto;
	}

	/**
	 * @param readonlyTipoSoggetto the readonlyTipoSoggetto to set
	 */
	public void setReadonlyTipoSoggetto(boolean readonlyTipoSoggetto) {
		this.readonlyTipoSoggetto = readonlyTipoSoggetto;
	}

	/**
	 * @return the readonlyDataFineIncarico
	 */
	public boolean isReadonlyDataFineIncarico() {
		return readonlyDataFineIncarico;
	}

	/**
	 * @param readonlyDataFineIncarico the readonlyDataFineIncarico to set
	 */
	public void setReadonlyDataFineIncarico(boolean readonlyDataFineIncarico) {
		this.readonlyDataFineIncarico = readonlyDataFineIncarico;
	}

	/**
	 * @return the readonlyQualifica
	 */
	public boolean isReadonlyQualifica() {
		return readonlyQualifica;
	}

	/**
	 * @param readonlyQualifica the readonlyQualifica to set
	 */
	public void setReadonlyQualifica(boolean readonlyQualifica) {
		this.readonlyQualifica = readonlyQualifica;
	}

	/**
	 * @return the readonlyResponsabileDichiarazioni
	 */
	public boolean isReadonlyResponsabileDichiarazioni() {
		return readonlyResponsabileDichiarazioni;
	}

	/**
	 * @param readonlyResponsabileDichiarazioni the readonlyResponsabileDichiarazioni to set
	 */
	public void setReadonlyResponsabileDichiarazioni(
			boolean readonlyResponsabileDichiarazioni) {
		this.readonlyResponsabileDichiarazioni = readonlyResponsabileDichiarazioni;
	}

	/**
	 * @return the readonlyCognome
	 */
	public boolean isReadonlyCognome() {
		return readonlyCognome;
	}

	/**
	 * @param readonlyCognome the readonlyCognome to set
	 */
	public void setReadonlyCognome(boolean readonlyCognome) {
		this.readonlyCognome = readonlyCognome;
	}

	/**
	 * @return the readonlyNome
	 */
	public boolean isReadonlyNome() {
		return readonlyNome;
	}

	/**
	 * @param readonlyNome the readonlyNome to set
	 */
	public void setReadonlyNome(boolean readonlyNome) {
		this.readonlyNome = readonlyNome;
	}

	/**
	 * @return the readonlyTitolo
	 */
	public boolean isReadonlyTitolo() {
		return readonlyTitolo;
	}

	/**
	 * @param readonlyTitolo the readonlyTitolo to set
	 */
	public void setReadonlyTitolo(boolean readonlyTitolo) {
		this.readonlyTitolo = readonlyTitolo;
	}

	/**
	 * @return the readonlySesso
	 */
	public boolean isReadonlySesso() {
		return readonlySesso;
	}

	/**
	 * @param readonlySesso the readonlySesso to set
	 */
	public void setReadonlySesso(boolean readonlySesso) {
		this.readonlySesso = readonlySesso;
	}

	/**
	 * @return the readonlyIndirizzo
	 */
	public boolean isReadonlyIndirizzo() {
		return readonlyIndirizzo;
	}

	/**
	 * @param readonlyIndirizzo the readonlyIndirizzo to set
	 */
	public void setReadonlyIndirizzo(boolean readonlyIndirizzo) {
		this.readonlyIndirizzo = readonlyIndirizzo;
	}

	/**
	 * @return the readonlyNumCivico
	 */
	public boolean isReadonlyNumCivico() {
		return readonlyNumCivico;
	}

	/**
	 * @param readonlyNumCivico the readonlyNumCivico to set
	 */
	public void setReadonlyNumCivico(boolean readonlyNumCivico) {
		this.readonlyNumCivico = readonlyNumCivico;
	}

	/**
	 * @return the readonlyCap
	 */
	public boolean isReadonlyCap() {
		return readonlyCap;
	}

	/**
	 * @param readonlyCap the readonlyCap to set
	 */
	public void setReadonlyCap(boolean readonlyCap) {
		this.readonlyCap = readonlyCap;
	}

	/**
	 * @return the readonlyComune
	 */
	public boolean isReadonlyComune() {
		return readonlyComune;
	}

	/**
	 * @param readonlyComune the readonlyComune to set
	 */
	public void setReadonlyComune(boolean readonlyComune) {
		this.readonlyComune = readonlyComune;
	}

	/**
	 * @return the readonlyProvincia
	 */
	public boolean isReadonlyProvincia() {
		return readonlyProvincia;
	}

	/**
	 * @param readonlyProvincia the readonlyProvincia to set
	 */
	public void setReadonlyProvincia(boolean readonlyProvincia) {
		this.readonlyProvincia = readonlyProvincia;
	}

	/**
	 * @return the readonlyNazione
	 */
	public boolean isReadonlyNazione() {
		return readonlyNazione;
	}

	/**
	 * @param readonlyNazione the readonlyNazione to set
	 */
	public void setReadonlyNazione(boolean readonlyNazione) {
		this.readonlyNazione = readonlyNazione;
	}

	/**
	 * @return the readonlyDataNascita
	 */
	public boolean isReadonlyDataNascita() {
		return readonlyDataNascita;
	}

	/**
	 * @param readonlyDataNascita the readonlyDataNascita to set
	 */
	public void setReadonlyDataNascita(boolean readonlyDataNascita) {
		this.readonlyDataNascita = readonlyDataNascita;
	}

	/**
	 * @return the readonlyComuneNascita
	 */
	public boolean isReadonlyComuneNascita() {
		return readonlyComuneNascita;
	}

	/**
	 * @param readonlyComuneNascita the readonlyComuneNascita to set
	 */
	public void setReadonlyComuneNascita(boolean readonlyComuneNascita) {
		this.readonlyComuneNascita = readonlyComuneNascita;
	}

	/**
	 * @return the readonlyProvinciaNascita
	 */
	public boolean isReadonlyProvinciaNascita() {
		return readonlyProvinciaNascita;
	}

	/**
	 * @param readonlyProvinciaNascita the readonlyProvinciaNascita to set
	 */
	public void setReadonlyProvinciaNascita(boolean readonlyProvinciaNascita) {
		this.readonlyProvinciaNascita = readonlyProvinciaNascita;
	}

	/**
	 * @return the readonlyTipologiaAlboProf
	 */
	public boolean isReadonlyTipologiaAlboProf() {
		return readonlyTipologiaAlboProf;
	}

	/**
	 * @param readonlyTipologiaAlboProf the readonlyTipologiaAlboProf to set
	 */
	public void setReadonlyTipologiaAlboProf(boolean readonlyTipologiaAlboProf) {
		this.readonlyTipologiaAlboProf = readonlyTipologiaAlboProf;
	}

	/**
	 * @return the readonlyNumIscrizioneAlboProf
	 */
	public boolean isReadonlyNumIscrizioneAlboProf() {
		return readonlyNumIscrizioneAlboProf;
	}

	/**
	 * @param readonlyNumIscrizioneAlboProf the readonlyNumIscrizioneAlboProf to set
	 */
	public void setReadonlyNumIscrizioneAlboProf(
			boolean readonlyNumIscrizioneAlboProf) {
		this.readonlyNumIscrizioneAlboProf = readonlyNumIscrizioneAlboProf;
	}

	/**
	 * @return the readonlyDataIscrizioneAlboProf
	 */
	public boolean isReadonlyDataIscrizioneAlboProf() {
		return readonlyDataIscrizioneAlboProf;
	}

	/**
	 * @param readonlyDataIscrizioneAlboProf the readonlyDataIscrizioneAlboProf to set
	 */
	public void setReadonlyDataIscrizioneAlboProf(
			boolean readonlyDataIscrizioneAlboProf) {
		this.readonlyDataIscrizioneAlboProf = readonlyDataIscrizioneAlboProf;
	}

	/**
	 * @return the readonlyProvinciaIscrizioneAlboProf
	 */
	public boolean isReadonlyProvinciaIscrizioneAlboProf() {
		return readonlyProvinciaIscrizioneAlboProf;
	}

	/**
	 * @param readonlyProvinciaIscrizioneAlboProf the readonlyProvinciaIscrizioneAlboProf to set
	 */
	public void setReadonlyProvinciaIscrizioneAlboProf(
			boolean readonlyProvinciaIscrizioneAlboProf) {
		this.readonlyProvinciaIscrizioneAlboProf = readonlyProvinciaIscrizioneAlboProf;
	}

	/**
	 * @return the readonlyTipologiaCassaPrevidenza
	 */
	public boolean isReadonlyTipologiaCassaPrevidenza() {
		return readonlyTipologiaCassaPrevidenza;
	}

	/**
	 * @param readonlyTipologiaCassaPrevidenza the readonlyTipologiaCassaPrevidenza to set
	 */
	public void setReadonlyTipologiaCassaPrevidenza(
			boolean readonlyTipologiaCassaPrevidenza) {
		this.readonlyTipologiaCassaPrevidenza = readonlyTipologiaCassaPrevidenza;
	}

	/**
	 * @return the readonlyNumMatricolaCassaPrevidenza
	 */
	public boolean isReadonlyNumMatricolaCassaPrevidenza() {
		return readonlyNumMatricolaCassaPrevidenza;
	}

	/**
	 * @param readonlyNumMatricolaCassaPrevidenza the readonlyNumMatricolaCassaPrevidenza to set
	 */
	public void setReadonlyNumMatricolaCassaPrevidenza(
			boolean readonlyNumMatricolaCassaPrevidenza) {
		this.readonlyNumMatricolaCassaPrevidenza = readonlyNumMatricolaCassaPrevidenza;
	}

	/**
	 * @return the readonlyNote
	 */
	public boolean isReadonlyNote() {
		return readonlyNote;
	}

	/**
	 * @param readonlyNote the readonlyNote to set
	 */
	public void setReadonlyNote(boolean readonlyNote) {
		this.readonlyNote = readonlyNote;
	}

	
	/**
	 * @return the visibleDataFineIncarico
	 */
	public boolean isVisibleDataFineIncarico() {
		return visibleDataFineIncarico;
	}

	/**
	 * @param visibleDataFineIncarico the visibleDataFineIncarico to set
	 */
	public void setVisibleDataFineIncarico(boolean visibleDataFineIncarico) {
		this.visibleDataFineIncarico = visibleDataFineIncarico;
	}

	/**
	 * @return the allFieldAsReadonly
	 */
	public boolean isAllFieldAsReadonly() {
		return allFieldAsReadonly;
	}

	/**
	 * @param allFieldAsReadonly the allFieldAsReadonly to set
	 */
	public void setAllFieldAsReadonly(boolean allFieldAsReadonly) {
		this.allFieldAsReadonly = allFieldAsReadonly;
	}

	@Override
	public void setSolaLettura(boolean solaLettura) {
		this.solaLettura = solaLettura;
		
	}

	@Override
	public boolean isSolaLettura() {
		return this.solaLettura; 
	}
	
}
