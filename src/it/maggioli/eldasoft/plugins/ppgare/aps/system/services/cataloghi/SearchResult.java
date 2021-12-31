package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper del risultato di una ricerca. Contiene il numero di elementi totali
 * estraibili senza applicare paginazione e filtri, il numero totale di elementi
 * estraibili senza applicare paginazione, ma applicando i filtri di ricerca, ed
 * i record effettivamente estratti.
 *
 * @author Stefano.Sabbadin, Marco Perazzetta
 * @param <T> la tipologia di dato contenuto nel result set
 *
 */
public class SearchResult<T> implements Serializable {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -3524971946369706685L;

	/**
	 * Numero totale di record estraibili senza applicare filtri e paginazione.
	 */
	private int numTotaleRecord;
	/**
	 * Numero totale di record estraibili senza applicare paginazione ma
	 * applicando i filtri richiesti.
	 */
	private int numTotaleRecordFiltrati;
	/**
	 * Dati estratti, eventualmente considerando la paginazione indicata.
	 */
	private List<T> dati;

	public SearchResult() {
		this.numTotaleRecord = 0;
		this.numTotaleRecordFiltrati = 0;
		this.dati = new ArrayList<T>();
	}

	/**
	 * @return the numTotaleRecord
	 */
	public int getNumTotaleRecord() {
		return numTotaleRecord;
	}

	/**
	 * @param numTotaleRecord the numTotaleRecord to set
	 */
	public void setNumTotaleRecord(int numTotaleRecord) {
		this.numTotaleRecord = numTotaleRecord;
	}

	/**
	 * @return the numTotaleRecordFiltrati
	 */
	public int getNumTotaleRecordFiltrati() {
		return numTotaleRecordFiltrati;
	}

	/**
	 * @param numTotaleRecordFiltrati the numTotaleRecordFiltrati to set
	 */
	public void setNumTotaleRecordFiltrati(int numTotaleRecordFiltrati) {
		this.numTotaleRecordFiltrati = numTotaleRecordFiltrati;
	}

	/**
	 * @return the dati
	 */
	public List<T> getDati() {
		return dati;
	}

	/**
	 * @param dati the dati to set
	 */
	public void setDati(List<T> dati) {
		this.dati = dati;
	}
}
