package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans;

import java.io.Serializable;

public class DocumentoMancanteBean  implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7030354831388945372L;

	private Long id;
	private String descrizione;
	private boolean obbligatorio;
	private String uuid;
	private int modificato;		// 0=nessuna modifica | 1=inserito/modificato | 2=eliminato
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public boolean isObbligatorio() {
		return obbligatorio;
	}
	
	public void setObbligatorio(boolean obbligatorio) {
		this.obbligatorio = obbligatorio;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getModificato() {
		return modificato;
	}
	
	public void setModificato(int modificato) {
		this.modificato = modificato;
	}
	
}
