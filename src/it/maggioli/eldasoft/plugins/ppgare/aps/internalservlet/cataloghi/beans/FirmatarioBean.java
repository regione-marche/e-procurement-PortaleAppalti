package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Bean per composizione della lista dei possibili soggetti firmatari del
 * ripilogo modifiche prodotti di un catalogo
 *
 * @author Marco Perazzetta
 */
public class FirmatarioBean implements Serializable {

	private static final long serialVersionUID = -8117325934295967836L;

	private String nominativo;
	private Integer index;
	private String lista;

	public String getNominativo() {
		return nominativo;
	}

	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}

	/**
	 * costruttore
	 */
	public FirmatarioBean() {
	}

	/**
	 * costruttore
	 */
	public FirmatarioBean(String firmatario) {
		this.lista = StringUtils.isNotBlank(firmatario) ? StringUtils.trim(StringUtils.substringBefore(firmatario, "-")) : null;
		this.index = StringUtils.isNotBlank(firmatario) ? Integer.parseInt(StringUtils.trim(StringUtils.substringAfter(firmatario, "-"))) : null;
	}

	/**
	 * costruttore
	 */
	public FirmatarioBean(FirmatarioType firmatarioType) {
		this.nominativo = firmatarioType.getCognome() + " " + firmatarioType.getNome();
	}

}
