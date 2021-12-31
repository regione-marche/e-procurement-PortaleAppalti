package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import java.io.Serializable;

/**
 * Bean per la memorizzazione dei criteri di ricerca per gli articoli
 *
 * @author Marco Perazzetta
 * @since 1.8.6
 */
public class ArticoliSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8117325934295933836L;

	private String tipo;
	private String codice;
	private String descrizione;
	private String colore;
	private String codiceCategoria;
	//se true, filtra gli articoli delle sole categorie per cui l'impresa e' 
	//abilitata per quel catalogo
	private boolean mieiArticoli;


	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public String getCodiceCategoria() {
		return codiceCategoria;
	}

	public void setCodiceCategoria(String codiceCategoria) {
		this.codiceCategoria = codiceCategoria;
	}

	public boolean isMieiArticoli() {
		return mieiArticoli;
	}

	public void setMieiArticoli(boolean mieiArticoli) {
		this.mieiArticoli = mieiArticoli;
	}

	/**
	 * costruttore 
	 */
	public ArticoliSearchBean() {
	}

}
