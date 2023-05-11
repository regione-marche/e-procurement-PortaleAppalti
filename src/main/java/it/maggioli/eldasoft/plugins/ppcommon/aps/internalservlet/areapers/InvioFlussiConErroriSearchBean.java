package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

/**
 * Bean di ricerca flussi inviati errati.
 * 
 * @author Eleonora.Favaro
 */
public class InvioFlussiConErroriSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -834045469011938351L;

	/** Username, esatta (case sensitive) ed intera. */
	@Validate(EParamValidation.USERNAME)
	private String utente;
	/** Denominazione, esatta (case sensitive) ed intera. */
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String mittente;
	/** Codice identificante il tipo comunicazione (FS*). */
	@Validate(EParamValidation.TIPO_COMUNICAZIONE)
	private String tipoComunicazione;
	/** Eventuale riferimento a procedura (codice gara, lotto, elenco, catalogo, ODA,...). */
	@Validate(EParamValidation.CODICE)
	private String riferimentoProcedura;
	
	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}

	public String getTipoComunicazione() {
		return tipoComunicazione;
	}

	public void setTipoComunicazione(String tipoComunicazione) {
		this.tipoComunicazione = tipoComunicazione;
	}

	public String getRiferimentoProcedura() {
		return riferimentoProcedura;
	}

	public void setRiferimentoProcedura(String riferimentoProcedura) {
		this.riferimentoProcedura = riferimentoProcedura;
	}
	
}
