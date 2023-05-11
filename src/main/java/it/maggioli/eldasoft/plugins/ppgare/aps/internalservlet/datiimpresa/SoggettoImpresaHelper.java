package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaType;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;

/**
 * Bean di memorizzazione di un singolo soggetto di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class SoggettoImpresaHelper implements ISoggettoImpresa {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6614308312162785808L;

	private static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("DD/MM/yyyy");
	
	private boolean esistente;
	private boolean solaLettura;
	
	/**
	 * campo speciale contenente la concatenazione di tipologia soggetto e
	 * qualifica, e serve solamente per l'interfaccia web.
	 */
	private String soggettoQualifica;	

    private String tipoSoggetto;
    private String dataInizioIncarico;
    private String dataFineIncarico;
    private String qualifica;
    private String responsabileDichiarazioni;
    private String cognome;
    private String nome;
    private String nominativo; 
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

    // campi di controllo per informazioni teoricamente obbligatorie ma che nel
    // caso di backoffice con dati parziali, almeno la prima volta potrebbero
    // arrivare incompleti
    private boolean readonlyDataInizioIncarico;
    //private boolean readonlyCognome;
    //private boolean readonlyNome;
    private boolean readonlyCodiceFiscale;
    //private boolean readonlySesso;
    //private boolean readonlyIndirizzo;
    //private boolean readonlyNumCivico;
    //private boolean readonlyCap;
    //private boolean readonlyComune;
    //private boolean readonlyProvincia;
    //private boolean readonlyNazione;
    //private boolean readonlyDataNascita;
    //private boolean readonlyComuneNascita;
    //private boolean readonlyProvinciaNascita;

    /**
	 * costruttore 
	 */
	public SoggettoImpresaHelper() {
		this.soggettoQualifica = null;
		this.tipoSoggetto = null;
		this.esistente = false;
		this.dataInizioIncarico = null;
		this.dataFineIncarico = null;
		this.qualifica = null;
		this.responsabileDichiarazioni = null;
		this.cognome = null;
		this.nome = null;
		this.nominativo = null;
		this.titolo = null;
		this.codiceFiscale = null;
		this.sesso = null;

		this.indirizzo = null;
		this.numCivico = null;
		this.cap = null;
		this.comune = null;
		this.provincia = null;
		this.nazione = "Italia";

		this.dataNascita = null;
		this.comuneNascita = null;
		this.provinciaNascita = null;

		this.tipologiaAlboProf = null;
		this.numIscrizioneAlboProf = null;
		this.dataIscrizioneAlboProf = null;
		this.provinciaIscrizioneAlboProf = null;
		this.tipologiaCassaPrevidenza = null;
		this.numMatricolaCassaPrevidenza = null;

		this.note = null;

		this.readonlyDataInizioIncarico = false;
		//this.readonlyCognome = false;
		//this.readonlyNome = false;
		this.readonlyCodiceFiscale = false;
		//this.readonlySesso = false;
		//this.readonlyIndirizzo = false;
		//this.readonlyNumCivico = false;
		//this.readonlyCap = false;
		//this.readonlyComune = false;
		//this.readonlyProvincia = false;
		//this.readonlyNazione = false;
		//this.readonlyDataNascita = false;
		//this.readonlyComuneNascita = false;
		//this.readonlyProvinciaNascita = false;
	}

	/**
	 * costruttore 
	 */
	public SoggettoImpresaHelper(ReferenteImpresaAggiornabileType referente, String tipoSoggetto) {
		this.esistente = referente.getEsistente();
		
		if (referente.getDataInizioIncarico() != null)
			this.dataInizioIncarico = DateFormatUtils.format(referente
					.getDataInizioIncarico().getTimeInMillis(),
					EntitySearchFilter.DATE_PATTERN);
		if (referente.getDataFineIncarico() != null)
			this.dataFineIncarico = DateFormatUtils.format(referente
					.getDataFineIncarico().getTimeInMillis(),
					EntitySearchFilter.DATE_PATTERN);

		this.tipoSoggetto = tipoSoggetto;
		this.qualifica = referente.getQualifica();
		this.soggettoQualifica = this.tipoSoggetto
				+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI
				+ StringUtils.stripToEmpty(this.qualifica);

		this.responsabileDichiarazioni = referente.getResponsabileDichiarazioni();
		this.cognome = referente.getCognome();
		this.nome = referente.getNome();
		this.titolo = referente.getTitolo();
		this.codiceFiscale = referente.getCodiceFiscale();
		this.sesso = referente.getSesso();

		this.indirizzo = referente.getResidenza().getIndirizzo();
		this.numCivico = referente.getResidenza().getNumCivico();
		this.cap = referente.getResidenza().getCap();
		this.comune = referente.getResidenza().getComune();
		this.provincia = referente.getResidenza().getProvincia();
		this.nazione = referente.getResidenza().getNazione();

		if (referente.getDataNascita() != null)
			this.dataNascita = DateFormatUtils.format(referente
					.getDataNascita().getTimeInMillis(),
					EntitySearchFilter.DATE_PATTERN);
		this.comuneNascita = referente.getComuneNascita();
		this.provinciaNascita = referente.getProvinciaNascita();

		if (referente.isSetAlboProfessionale()) {
			this.tipologiaAlboProf = referente.getAlboProfessionale()
					.getTipologia();
			this.numIscrizioneAlboProf = referente.getAlboProfessionale()
					.getNumIscrizione();
			if (referente.getAlboProfessionale().getDataIscrizione() != null)
				this.dataIscrizioneAlboProf = DateFormatUtils.format(referente
						.getAlboProfessionale().getDataIscrizione()
						.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
			this.provinciaIscrizioneAlboProf = referente.getAlboProfessionale()
					.getProvinciaIscrizione();
		}
		if (referente.isSetCassaPrevidenza()) {
			this.tipologiaCassaPrevidenza = referente.getCassaPrevidenza()
					.getTipologia();
			this.numMatricolaCassaPrevidenza = referente.getCassaPrevidenza()
					.getNumMatricola();
		}

		this.note = referente.getNote();

		this.readonlyDataInizioIncarico = (this.esistente && referente
				.getDataInizioIncarico() != null);
		this.readonlyCodiceFiscale = (this.esistente
				&& StringUtils.stripToNull(referente.getCodiceFiscale()) != null && 
				UtilityFiscali.isValidCodiceFiscale(referente.getCodiceFiscale(), "Italia".equalsIgnoreCase(referente.getResidenza().getNazione())));
		this.setSolaLettura(referente.getSolaLettura());
//		this.readonlyCognome = (this.esistente && StringUtils
//				.stripToNull(referente.getCognome()) != null);
//		this.readonlyNome = (this.esistente && StringUtils
//				.stripToNull(referente.getNome()) != null);
//		this.readonlySesso = (this.esistente && StringUtils
//				.stripToNull(referente.getSesso()) != null);
//		this.readonlyIndirizzo = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getIndirizzo()) != null);
//		this.readonlyNumCivico = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getNumCivico()) != null);
//		this.readonlyCap = (this.esistente && StringUtils.stripToNull(referente
//				.getResidenza().getCap()) != null);
//		this.readonlyComune = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getComune()) != null);
//		this.readonlyProvincia = (this.esistente && (StringUtils
//				.stripToNull(referente.getResidenza().getProvincia()) != null || !"Italia"
//				.equalsIgnoreCase(referente.getResidenza().getNazione())));
//		this.readonlyNazione = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getNazione()) != null);
//		this.readonlyDataNascita = (this.esistente && referente
//				.getDataNascita() != null);
//		this.readonlyComuneNascita = (this.esistente && StringUtils
//				.stripToNull(referente.getComuneNascita()) != null);
//		this.readonlyProvinciaNascita = (this.esistente && StringUtils
//				.stripToNull(referente.getProvinciaNascita()) != null);
	}
	
	/**
	 * costruttore 
	 */
	public SoggettoImpresaHelper(ReferenteImpresaType referente, String tipoSoggetto) {
		this.esistente = referente.getEsistente();
		this.dataInizioIncarico = DateFormatUtils.format(referente
				.getDataInizioIncarico().getTimeInMillis(),
				EntitySearchFilter.DATE_PATTERN);

		if (referente.getDataFineIncarico() != null)
			this.dataFineIncarico = DateFormatUtils.format(referente
					.getDataFineIncarico().getTimeInMillis(),
					EntitySearchFilter.DATE_PATTERN);
		this.tipoSoggetto = tipoSoggetto;
		this.qualifica = referente.getQualifica();
		this.soggettoQualifica = this.tipoSoggetto
				+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI
				+ StringUtils.stripToEmpty(this.qualifica);
		this.responsabileDichiarazioni = referente.getResponsabileDichiarazioni();

		this.cognome = referente.getCognome();
		this.nome = referente.getNome();
		this.titolo = referente.getTitolo();
		this.codiceFiscale = referente.getCodiceFiscale();
		this.sesso = referente.getSesso();

		this.indirizzo = referente.getResidenza().getIndirizzo();
		this.numCivico = referente.getResidenza().getNumCivico();
		this.cap = referente.getResidenza().getCap();
		this.comune = referente.getResidenza().getComune();
		this.provincia = referente.getResidenza().getProvincia();
		this.nazione = referente.getResidenza().getNazione();

		if (referente.getDataNascita() != null)
			this.dataNascita = DateFormatUtils.format(referente
					.getDataNascita().getTimeInMillis(),
					EntitySearchFilter.DATE_PATTERN);
		this.comuneNascita = referente.getComuneNascita();
		this.provinciaNascita = referente.getProvinciaNascita();

		this.tipologiaAlboProf = referente.getAlboProfessionale()
				.getTipologia();
		this.numIscrizioneAlboProf = referente.getAlboProfessionale()
				.getNumIscrizione();
		if (referente.getAlboProfessionale().getDataIscrizione() != null)
			this.dataIscrizioneAlboProf = DateFormatUtils.format(referente
					.getAlboProfessionale().getDataIscrizione()
					.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
		this.provinciaIscrizioneAlboProf = referente.getAlboProfessionale()
				.getProvinciaIscrizione();
		this.tipologiaCassaPrevidenza = referente.getCassaPrevidenza()
				.getTipologia();
		this.numMatricolaCassaPrevidenza = referente.getCassaPrevidenza()
				.getNumMatricola();

		this.note = referente.getNote();
		this.readonlyDataInizioIncarico = (this.esistente && referente
				.getDataInizioIncarico() != null);
		this.readonlyCodiceFiscale = (this.esistente
				&& StringUtils.stripToNull(referente.getCodiceFiscale()) != null && 
				UtilityFiscali.isValidCodiceFiscale(referente.getCodiceFiscale(), "Italia".equalsIgnoreCase(referente.getResidenza().getNazione())));
		this.setSolaLettura(referente.getSolaLettura());
//		this.readonlyCognome = (this.esistente && StringUtils
//				.stripToNull(referente.getCognome()) != null);
//		this.readonlyNome = (this.esistente && StringUtils
//				.stripToNull(referente.getNome()) != null);
//		this.readonlySesso = (this.esistente && StringUtils
//				.stripToNull(referente.getSesso()) != null);
//		this.readonlyIndirizzo = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getIndirizzo()) != null);
//		this.readonlyNumCivico = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getNumCivico()) != null);
//		this.readonlyCap = (this.esistente && StringUtils.stripToNull(referente
//				.getResidenza().getCap()) != null);
//		this.readonlyComune = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getComune()) != null);
//		this.readonlyProvincia = (this.esistente && (StringUtils
//				.stripToNull(referente.getResidenza().getProvincia()) != null || !"Italia"
//				.equalsIgnoreCase(referente.getResidenza().getNazione())));
//		this.readonlyNazione = (this.esistente && StringUtils
//				.stripToNull(referente.getResidenza().getNazione()) != null);
//		this.readonlyDataNascita = (this.esistente && referente
//				.getDataNascita() != null);
//		this.readonlyComuneNascita = (this.esistente && StringUtils
//				.stripToNull(referente.getComuneNascita()) != null);
//		this.readonlyProvinciaNascita = (this.esistente && StringUtils
//				.stripToNull(referente.getProvinciaNascita()) != null);
	}

	/**
	 * costruttore 
	 */
	public SoggettoImpresaHelper(FirmatarioType firmatario) {
		this.esistente = true;
		this.nome = firmatario.getNome();
		this.cognome = firmatario.getCognome();
		this.codiceFiscale = firmatario.getCodiceFiscaleFirmatario();
		this.comuneNascita = firmatario.getComuneNascita();
		this.dataNascita = (firmatario.getDataNascita() != null 
							? DDMMYYYY.format(firmatario.getDataNascita().getTime())
							: null);
		this.provinciaNascita = firmatario.getProvinciaNascita();		
		//this.soggettoQualifica = this.tipoSoggetto
		//						 + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI
		//						 + StringUtils.stripToEmpty(this.qualifica);
		this.soggettoQualifica = firmatario.getQualifica();
		this.sesso = firmatario.getSesso();	
		this.comuneNascita = firmatario.getComuneNascita();
		
		this.cap = firmatario.getResidenza().getCap();
		this.indirizzo = firmatario.getResidenza().getIndirizzo();
		this.numCivico = firmatario.getResidenza().getNumCivico();
		this.comune = firmatario.getResidenza().getComune();
		this.nazione = firmatario.getResidenza().getNazione();
		this.provincia = firmatario.getResidenza().getProvincia();
		this.nominativo = firmatario.getCognome() + " " + firmatario.getNome();

		this.readonlyCodiceFiscale = (this.esistente && 
				StringUtils.stripToNull(this.codiceFiscale) != null && 
				UtilityFiscali.isValidCodiceFiscale(this.codiceFiscale, "Italia".equalsIgnoreCase(this.nazione)));		
	}

	/**
	 * costruttore 
	 */
	public SoggettoImpresaHelper(ISoggettoImpresa firmatario) {
		super();
		this.copyFrom(firmatario);
		this.setEsistente(firmatario.isEsistente());
	}
	
	/**
	 * copia i dati da un ISoggettoImpresa 
	 */
	public void copyFrom(ISoggettoImpresa source) {
		this.esistente = true;	//source.isEsistente();
		
		this.soggettoQualifica = source.getSoggettoQualifica();	

	    this.tipoSoggetto = source.getTipoSoggetto();
	    this.dataInizioIncarico = getDataInizioIncarico();
	    this.dataFineIncarico = source.getDataFineIncarico();
	    this.qualifica = source.getQualifica();
	    this.responsabileDichiarazioni = getResponsabileDichiarazioni();
	    this.cognome = source.getCognome();
	    this.nome = source.getNome();
	    this.nominativo = this.cognome + " " + this.nome;
	    this.titolo = source.getTitolo();
	    this.codiceFiscale = source.getCodiceFiscale();
	    this.sesso = source.getSesso();

	    this.indirizzo = source.getIndirizzo();
	    this.numCivico = source.getNumCivico();
	    this.cap = source.getCap();
	    this.comune = source.getComune();
	    this.provincia = source.getProvincia();
	    this.nazione = source.getNazione();

	    this.dataNascita = source.getDataNascita();
	    this.comuneNascita = source.getComuneNascita();
	    this.provinciaNascita = source.getProvinciaNascita();

	    this.tipologiaAlboProf = source.getTipologiaAlboProf();
	    this.numIscrizioneAlboProf = source.getNumIscrizioneAlboProf();
	    this.dataIscrizioneAlboProf = source.getDataIscrizioneAlboProf();
	    this.provinciaIscrizioneAlboProf = source.getProvinciaIscrizioneAlboProf();
	    this.tipologiaCassaPrevidenza = source.getTipologiaCassaPrevidenza();
	    this.numMatricolaCassaPrevidenza = source.getNumMatricolaCassaPrevidenza();
	    
	    this.note = source.getNote();

		this.readonlyCodiceFiscale = (this.esistente && 
				StringUtils.stripToNull(this.codiceFiscale) != null && 
				UtilityFiscali.isValidCodiceFiscale(this.codiceFiscale, "Italia".equalsIgnoreCase(this.nazione)));		
	}
	
    public boolean isEsistente() {
    	return esistente;
    }

    public void setEsistente(boolean esistente) {
    	this.esistente = esistente;
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
			if (s.hasNext())
				this.qualifica = StringUtils.stripToNull(s.next());
		}
	}

    public String getTipoSoggetto() {
    	return tipoSoggetto;
    }

    public void setTipoSoggetto(String tipoSoggetto) {
    	this.tipoSoggetto = tipoSoggetto;
    }

    public String getDataInizioIncarico() {
    	return dataInizioIncarico;
    }

    public void setDataInizioIncarico(String dataInizioIncarico) {
    	this.dataInizioIncarico = dataInizioIncarico;
    }

    public String getDataFineIncarico() {
    	return dataFineIncarico;
    }

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

    public String getCognome() {
    	return cognome;
    }

    public void setCognome(String cognome) {
    	this.cognome = cognome;
    }

    public String getNome() {
    	return nome;
    }

    public void setNome(String nome) {
    	this.nome = nome;
    }

	public String getNominativo() {
		return nominativo;
	}

	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}

	@Override
    public String getTitolo() {
		return titolo;
	}

    @Override
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

    public String getCodiceFiscale() {
    	return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
    	this.codiceFiscale = codiceFiscale;
    }

    public String getSesso() {
    	return sesso;
    }

    public void setSesso(String sesso) {
    	this.sesso = sesso;
    }

    public String getIndirizzo() {
    	return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
    	this.indirizzo = indirizzo;
    }

    public String getNumCivico() {
    	return numCivico;
    }

    public void setNumCivico(String numCivico) {
    	this.numCivico = numCivico;
    }

    public String getCap() {
    	return cap;
    }

    public void setCap(String cap) {
    	this.cap = cap;
    }

    public String getComune() {
    	return comune;
    }

    public void setComune(String comune) {
    	this.comune = comune;
    }

    public String getProvincia() {
    	return provincia;
    }

    public void setProvincia(String provincia) {
    	this.provincia = provincia;
    }

    public String getNazione() {
    	return nazione;
    }

    public void setNazione(String nazione) {
    	this.nazione = nazione;
    }

    public String getDataNascita() {
    	return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
    	this.dataNascita = dataNascita;
    }

    public String getComuneNascita() {
    	return comuneNascita;
    }

    public void setComuneNascita(String comuneNascita) {
    	this.comuneNascita = comuneNascita;
    }
    
    public String getProvinciaNascita() {
		return provinciaNascita;
	}

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
     * @return the isReadonlyDataInizioIncarico
     */
    public boolean isReadonlyDataInizioIncarico() {
    	return readonlyDataInizioIncarico;
    }

//    public boolean isReadonlyCognome() {
//	return readonlyCognome;
//    }
//
//    public boolean isReadonlyNome() {
//	return readonlyNome;
//    }

    public boolean isReadonlyCodiceFiscale() {
    	return readonlyCodiceFiscale;
    }

	public boolean isSolaLettura() {
		return solaLettura;
	}

	public void setSolaLettura(boolean solaLettura) {
		this.solaLettura = solaLettura;
	}

//    public boolean isReadonlySesso() {
//	return readonlySesso;
//    }
//
//    public boolean isReadonlyIndirizzo() {
//	return readonlyIndirizzo;
//    }
//
//    public boolean isReadonlyNumCivico() {
//	return readonlyNumCivico;
//    }
//
//    public boolean isReadonlyCap() {
//	return readonlyCap;
//    }
//
//    public boolean isReadonlyComune() {
//	return readonlyComune;
//    }
//
//    public boolean isReadonlyProvincia() {
//	return readonlyProvincia;
//    }
//
//    public boolean isReadonlyNazione() {
//	return readonlyNazione;
//    }
//
//    public boolean isReadonlyDataNascita() {
//	return readonlyDataNascita;
//    }
//
//    public boolean isReadonlyComuneNascita() {
//	return readonlyComuneNascita;
//    }
//
//	public boolean isReadonlyProvinciaNascita() {
//		return readonlyProvinciaNascita;
//	}

}
