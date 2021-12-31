package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers;

import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import java.io.Serializable;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati categorie d'interesse
 *
 * @author Stefano.Sabbadin
 */
public class WizardArticoloHelper implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4738312267418549118L;

	/**
	 * Codice articolo selezionato
	 */
	private Long idArticolo;

	private ArticoloType dettaglioArticolo;

	public Long getIdArticolo() {
		return idArticolo;
	}

	public void setIdArticolo(Long idArticolo) {
		this.idArticolo = idArticolo;
	}

	public ArticoloType getDettaglioArticolo() {
		return dettaglioArticolo;
	}

	public void setDettaglioArticolo(ArticoloType dettaglioArticolo) {
		this.dettaglioArticolo = dettaglioArticolo;
	}

	/**
	 * costruttore 
	 */
	public WizardArticoloHelper() {
		this.idArticolo = null;
		this.dettaglioArticolo = null;
	}

	/**
	 * costruttore 
	 */
	public WizardArticoloHelper(Long idArticolo, ArticoloType dettaglioArticolo) {
		this.idArticolo = idArticolo;
		this.dettaglioArticolo = dettaglioArticolo;
	}

}
