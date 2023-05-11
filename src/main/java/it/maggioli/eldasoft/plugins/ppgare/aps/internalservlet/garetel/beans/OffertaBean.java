package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans;

import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;

import java.io.Serializable;
import java.util.List;

public class OffertaBean  implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5345948786681087943L;
	
	private Long progressivoOfferta;
	private String concorrente;
	private String tipoPartecipazione;
	private List<String> mandanti;
	private List<LottoGaraType> lotti;
	private String stato;
	private boolean annullamento;		// resetta FS11,FS11R ed elimina le FS11A, FS11B, FS11C
	private boolean eliminazione;		// elimina tutte le FS11*
	private boolean rettifica;			// rettifica una offerta/domanda inviata
	
	public Long getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(Long progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}
	
	public String getConcorrente() {
		return concorrente;
	}

	public void setConcorrente(String concorrente) {
		this.concorrente = concorrente;
	}

	public String getTipoPartecipazione() {
		return tipoPartecipazione;
	}
	
	public void setTipoPartecipazione(String tipoPartecipazione) {
		this.tipoPartecipazione = tipoPartecipazione;
	}
	
	public List<String> getMandanti() {
		return mandanti;
	}
	
	public void setMandanti(List<String> mandanti) {
		this.mandanti = mandanti;
	}
	
	public List<LottoGaraType> getLotti() {
		return lotti;
	}
	
	public void setLotti(List<LottoGaraType> lotti) {
		this.lotti = lotti;
	}
	
	public String getStato() {
		return stato;
	}
	
	public void setStato(String stato) {
		this.stato = stato;
	}

	public boolean isAnnullamento() {
		return annullamento;
	}

	public void setAnnullamento(boolean annullamento) {
		this.annullamento = annullamento;
	}

	public boolean isEliminazione() {
		return eliminazione;
	}

	public void setEliminazione(boolean eliminazione) {
		this.eliminazione = eliminazione;
	}

	public boolean isRettifica() {
		return rettifica;
	}

	public void setRettifica(boolean rettifica) {
		this.rettifica = rettifica;
	}
	
}