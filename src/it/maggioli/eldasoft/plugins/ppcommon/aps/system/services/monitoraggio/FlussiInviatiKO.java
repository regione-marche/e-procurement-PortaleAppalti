package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio;

import java.util.Calendar;

/**
 * Helper contenente i dati necessari nella visualizzazione del singolo flusso errato.
 * 
 * @author Eleonora.Favaro
 */
public class FlussiInviatiKO implements Comparable<FlussiInviatiKO> {

	/** Id della comunicazione. */
	private Long idComunicazione;
	/** Data creazione della comunicazione. */
	private Calendar dataInserimento;
	/** Utente/Operatore economico che ha creato la comunicazione. */
	private String utente;
	/** Denominazione operatore economico che ha creato la comunicazione. */
	private String mittente;
	/** Tipologia di comunicazione (FS*). */
	private String tipoComunicazione;
	/** Eventuale riferimento a procedura. */
	private String riferimentoProcedura;

	/**
	 * @return the idComunicazione
	 */
	public Long getIdComunicazione() {
		return idComunicazione;
	}

	/**
	 * @param long the idComunicazione to set
	 */
	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	/**
	 * @return the dataInserimento
	 */
	public Calendar getDataInserimento() {
		return dataInserimento;
	}

	/**
	 * @param dataInserimento
	 *            the dataInserimento to set
	 */
	public void setDataInserimento(Calendar dataInserimento) {
		this.dataInserimento = dataInserimento;
	}

	/**
	 * @return the utente
	 */
	public String getUtente() {
		return utente;
	}

	/**
	 * @param utente
	 *            the utente to set
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
	 * @param tipoComunicazione
	 *            the tipoComunicazione to set
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
	 * @param riferimentoProcedura
	 *            the riferimentoProcedura to set
	 */
	public void setRiferimentoProcedura(String riferimentoProcedura) {
		this.riferimentoProcedura = riferimentoProcedura;
	}

	/**
	 * Confronta istanze della classe in modo da renderle ordinabili in una
	 * collezione.
	 * 
	 * @param o
	 *            oggetto da confrontare
	 * @return -1 se l'elemento in un ordinamento crescente viene prima del
	 *         parametro, 1 se viene dopo, 2 se sono equivalenti.
	 */
	@Override
	public int compareTo(FlussiInviatiKO o) {
		if (this.idComunicazione < o.idComunicazione) {
			return -1;
		} else if (this.idComunicazione == o.idComunicazione) {
			return 0;
		} else {
			return 1;
		}
	}

}
