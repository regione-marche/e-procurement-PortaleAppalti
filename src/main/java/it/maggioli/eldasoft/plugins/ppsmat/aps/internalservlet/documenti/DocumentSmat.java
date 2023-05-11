package it.maggioli.eldasoft.plugins.ppsmat.aps.internalservlet.documenti;

import java.io.Serializable;

public class DocumentSmat implements Serializable {
	/**
	 * UId 
	 */
	private static final long serialVersionUID = -1068699648916325371L;

	private String codice_sta;
	
	private String descrizione_sta;

	public String getCodice_sta() {
		return codice_sta;
	}

	public void setCodice_sta(String codice_sta) {
		this.codice_sta = codice_sta;
	}

	public String getDescrizione_sta() {
		return descrizione_sta;
	}

	public void setDescrizione_sta(String descrizione_sta) {
		this.descrizione_sta = descrizione_sta;
	}
	
}