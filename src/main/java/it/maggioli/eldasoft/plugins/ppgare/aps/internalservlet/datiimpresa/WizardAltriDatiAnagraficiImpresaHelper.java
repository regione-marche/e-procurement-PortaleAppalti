package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import org.apache.commons.lang.time.DateFormatUtils;

import com.agiletec.aps.system.common.entity.model.EntitySearchFilter;

import it.eldasoft.sil.portgare.datatypes.AltriDatiAnagraficiAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.AltriDatiAnagraficiType;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento altri dati anagrafici dell'impresa quando si tratta di libero
 * professionista.
 * 
 * @author Stefano.Sabbadin
 * @since 1.6
 */
public class WizardAltriDatiAnagraficiImpresaHelper implements
		IAltriDatiAnagraficiImpresa {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 329458897895448423L;

	private String titolo;
	private String nome;
	private String cognome;
	private String dataNascita;
	private String comuneNascita;
	private String provinciaNascita;
	private String sesso;
	private String tipologiaAlboProf;
	private String numIscrizioneAlboProf;
	private String dataIscrizioneAlboProf;
	private String provinciaIscrizioneAlboProf;
	private String tipologiaCassaPrevidenza;
	private String numMatricolaCassaPrevidenza;

	public WizardAltriDatiAnagraficiImpresaHelper() {
		this.titolo = null;
		this.nome = null;
		this.cognome = null;
		this.dataNascita = null;
		this.comuneNascita = null;
		this.provinciaNascita = null;
		this.sesso = null;
		this.tipologiaAlboProf = null;
		this.numIscrizioneAlboProf = null;
		this.dataIscrizioneAlboProf = null;
		this.provinciaIscrizioneAlboProf = null;
		this.tipologiaCassaPrevidenza = null;
		this.numMatricolaCassaPrevidenza = null;
	}

	public WizardAltriDatiAnagraficiImpresaHelper(
			AltriDatiAnagraficiAggiornabileType helper) {
		if (helper != null) {
			this.titolo = helper.getTitolo();
			this.nome = helper.getNome();
			this.cognome = helper.getCognome();
			if (helper.getDataNascita() != null)
				this.dataNascita = DateFormatUtils.format(helper
						.getDataNascita().getTimeInMillis(),
						EntitySearchFilter.DATE_PATTERN);
			this.comuneNascita = helper.getComuneNascita();
			this.provinciaNascita = helper.getProvinciaNascita();
			this.sesso = helper.getSesso();

			if (helper.isSetAlboProfessionale()) {
				this.tipologiaAlboProf = helper.getAlboProfessionale()
						.getTipologia();
				this.numIscrizioneAlboProf = helper.getAlboProfessionale()
						.getNumIscrizione();
				if (helper.getAlboProfessionale().getDataIscrizione() != null)
					this.dataIscrizioneAlboProf = DateFormatUtils
							.format(helper.getAlboProfessionale()
									.getDataIscrizione().getTimeInMillis(),
									EntitySearchFilter.DATE_PATTERN);
				this.provinciaIscrizioneAlboProf = helper
						.getAlboProfessionale().getProvinciaIscrizione();
			}

			if (helper.isSetCassaPrevidenza()) {
				this.tipologiaCassaPrevidenza = helper.getCassaPrevidenza()
						.getTipologia();
				this.numMatricolaCassaPrevidenza = helper.getCassaPrevidenza()
						.getNumMatricola();
			}
		}
	}

	public WizardAltriDatiAnagraficiImpresaHelper(AltriDatiAnagraficiType helper) {
		if (helper != null) {
			this.titolo = helper.getTitolo();
			this.nome = helper.getNome();
			this.cognome = helper.getCognome();
			if (helper.getDataNascita() != null)
				this.dataNascita = DateFormatUtils.format(helper
						.getDataNascita().getTimeInMillis(),
						EntitySearchFilter.DATE_PATTERN);
			this.comuneNascita = helper.getComuneNascita();
			this.provinciaNascita = helper.getProvinciaNascita();
			this.sesso = helper.getSesso();

			this.tipologiaAlboProf = helper.getAlboProfessionale()
					.getTipologia();
			this.numIscrizioneAlboProf = helper.getAlboProfessionale()
					.getNumIscrizione();
			if (helper.getAlboProfessionale().getDataIscrizione() != null)
				this.dataIscrizioneAlboProf = DateFormatUtils.format(helper
						.getAlboProfessionale().getDataIscrizione()
						.getTimeInMillis(), EntitySearchFilter.DATE_PATTERN);
			this.provinciaIscrizioneAlboProf = helper.getAlboProfessionale()
					.getProvinciaIscrizione();
			this.tipologiaCassaPrevidenza = helper.getCassaPrevidenza()
					.getTipologia();
			this.numMatricolaCassaPrevidenza = helper.getCassaPrevidenza()
					.getNumMatricola();
		}
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
	public String getNome() {
		return nome;
	}

	@Override
	public void setNome(String nome) {
		this.nome = nome;
		
	}

	@Override
	public String getCognome() {
		return cognome;
	}

	@Override
	public void setCognome(String cognome) {
		this.cognome = cognome; 		
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
		this.comuneNascita = comuneNascita;
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
	public String getSesso() {
		return sesso;
	}

	@Override
	public void setSesso(String sesso) {
		this.sesso = sesso;
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
	public void setProvinciaIscrizioneAlboProf(
			String provinciaIscrizioneAlboProf) {
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
	public void setNumMatricolaCassaPrevidenza(
			String numMatricolaCassaPrevidenza) {
		this.numMatricolaCassaPrevidenza = numMatricolaCassaPrevidenza;
	}

}
