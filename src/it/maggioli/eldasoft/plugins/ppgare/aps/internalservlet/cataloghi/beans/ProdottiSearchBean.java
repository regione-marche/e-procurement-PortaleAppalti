package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import java.io.Serializable;

/**
 * Bean per la memorizzazione dei criteri di ricerca per i prodotti
 *
 * @author Marco Perazzetta
 * @since 1.8.6
 */
public class ProdottiSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8428282240995268327L;

	private boolean advancedSearch;
	private String tipologia;
	private String descrizioneArticolo;
	private String colore;
	private String codice;
	private String nome;
	private String stato;
	private String descrizioneAggiuntiva;
	private String googleLike;

	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	public void setTipologia(Integer tipologia) {
		this.tipologia = tipologia + "";
	}

	public boolean isAdvancedSearch() {
		return advancedSearch;
	}

	public void setAdvancedSearch(boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	public String getDescrizioneArticolo() {
		return descrizioneArticolo;
	}

	public void setDescrizioneArticolo(String descizioneArticolo) {
		this.descrizioneArticolo = descizioneArticolo;
	}

	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizioneAggiuntiva() {
		return descrizioneAggiuntiva;
	}

	public void setDescrizioneAggiuntiva(String descrizioneAggiuntiva) {
		this.descrizioneAggiuntiva = descrizioneAggiuntiva;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getGoogleLike() {
		return googleLike;
	}

	public void setGoogleLike(String googleLike) {
		this.googleLike = googleLike;
	}
	
	/**
	 * costruttore 
	 */
	public ProdottiSearchBean() {
		this.stato = CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO;
		this.advancedSearch = false;
	}
	
	/**
	 * costruttore 
	 */
	public ProdottiSearchBean(String codice) {
		this.stato = CataloghiConstants.STATO_PRODOTTO_IN_CATALOGO;
		this.advancedSearch = false;
		this.codice = codice;
	}

}
