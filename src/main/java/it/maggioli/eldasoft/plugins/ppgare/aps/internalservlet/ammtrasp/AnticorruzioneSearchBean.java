package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Bean per la memorizzazione dei criteri di ricerca per i dati sull'anticorruzione.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.2
 */
public class AnticorruzioneSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6331991081344550343L;

	@Validate(EParamValidation.DIGIT)
	private String anno;
	@Validate(EParamValidation.CIG)
	private String cig;
	@Validate(EParamValidation.GENERIC)
	private String proponente;
	@Validate(EParamValidation.OGGETTO)
	private String oggetto;
	@Validate(EParamValidation.GENERIC)
	private String partecipante;
	@Validate(EParamValidation.GENERIC)
	private String aggiudicatario;

	/**
	 * @return the anno
	 */
	public String getAnno() {
		return anno;
	}

	/**
	 * @param anno the anno to set
	 */
	public void setAnno(String anno) {
		this.anno = StringUtils.stripToNull(anno);
	}

	/**
	 * @return the cig
	 */
	public String getCig() {
		return cig;
	}

	/**
	 * @param cig the cig to set
	 */
	public void setCig(String cig) {
		this.cig = StringUtils.stripToNull(cig);
	}

	/**
	 * @return the proponente
	 */
	public String getProponente() {
		return proponente;
	}

	/**
	 * @param proponente the proponente to set
	 */
	public void setProponente(String proponente) {
		this.proponente = StringUtils.stripToNull(proponente);
	}

	/**
	 * @return the oggetto
	 */
	public String getOggetto() {
		return oggetto;
	}

	/**
	 * @param oggetto the oggetto to set
	 */
	public void setOggetto(String oggetto) {
		this.oggetto = StringUtils.stripToNull(oggetto);
	}

	/**
	 * @return the partecipante
	 */
	public String getPartecipante() {
		return partecipante;
	}

	/**
	 * @param partecipante the partecipante to set
	 */
	public void setPartecipante(String partecipante) {
		this.partecipante = StringUtils.stripToNull(partecipante);
	}

	/**
	 * @return the aggiudicatario
	 */
	public String getAggiudicatario() {
		return aggiudicatario;
	}

	/**
	 * @param aggiudicatario the aggiudicatario to set
	 */
	public void setAggiudicatario(String aggiudicatario) {
		this.aggiudicatario = StringUtils.stripToNull(aggiudicatario);
	}

}
