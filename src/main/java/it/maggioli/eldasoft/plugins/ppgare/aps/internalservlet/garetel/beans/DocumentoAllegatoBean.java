package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans;

import java.io.Serializable;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

public class DocumentoAllegatoBean  implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5208066364588825169L;

	private Long id;
	@Validate(EParamValidation.FILE_NAME)		// SERVE ?
	private String nomeFile;
	@Validate(EParamValidation.DESCRIZIONE)		// SERVE ?
	private String descrizione;
	private Long dimensione;
	@Validate(EParamValidation.SHA)				// SERVE ?
	private String sha1;
	@Validate(EParamValidation.UUID)			// SERVE ?
	private String uuid;		// se valorizzato l'allegato e' "esterno" al documento xml della busta	
	private int stato;   		// 0=nessuna modifica | 1=inserito/modificato | -1=eliminato
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNomeFile() {
		return nomeFile;
	}
	
	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}
	
	public String getDescrizione() {
		return descrizione;
	}
	
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public Long getDimensione() {
		return dimensione;
	}
	
	public void setDimensione(Long dimensione) {
		this.dimensione = dimensione;
	}
	
	public String getSha1() {
		return sha1;
	}
	
	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getStato() {
		return stato;
	}
	
	public void setStato(int stato) {
		this.stato = stato;
	}
}
