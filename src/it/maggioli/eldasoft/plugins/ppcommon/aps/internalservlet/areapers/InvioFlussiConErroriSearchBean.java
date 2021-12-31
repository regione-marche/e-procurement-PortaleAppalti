package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import java.io.Serializable;

/**
 * Bean di ricerca flussi inviati errati.
 * 
 * @author Eleonora.Favaro
 */
public class InvioFlussiConErroriSearchBean implements Serializable{

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -834045469011938351L;

	/** Username, esatta (case sensitive) ed intera. */
	private String utente;
	/** Denominazione, esatta (case sensitive) ed intera. */
	private String mittente;
	/** Codice identificante il tipo comunicazione (FS*). */
	private String tipoComunicazione;
	/** Eventuale riferimento a procedura (codice gara, lotto, elenco, catalogo, ODA,...). */
	private String riferimentoProcedura;
	
	/**
	 * @return the utente
	 */
	public String getUtente() {
		return utente;
	}
	/**
	 * @param utente the utente to set
	 */
	public void setUtente(String utente) {
		this.utente = utente;
	}
	/**
	 * @return the mittente
	 */
	public String getMittente() {
		return mittente;
	}
	/**
	 * @param mittente the mittente to set
	 */
	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	/**
	 * @return the tipoComunicazione
	 */
	public String getTipoComunicazione() {
		return tipoComunicazione;
	}
	/**
	 * @param tipoComunicazione the tipoComunicazione to set
	 */
	public void setTipoComunicazione(String tipoComunicazione) {
		this.tipoComunicazione = tipoComunicazione;
	}
	/**
	 * @return the riferimentoProcedura
	 */
	public String getRiferimentoProcedura() {
		return riferimentoProcedura;
	}
	/**
	 * @param riferimentoProcedura the riferimentoProcedura to set
	 */
	public void setRiferimentoProcedura(String riferimentoProcedura) {
		this.riferimentoProcedura = riferimentoProcedura;
	}
}
