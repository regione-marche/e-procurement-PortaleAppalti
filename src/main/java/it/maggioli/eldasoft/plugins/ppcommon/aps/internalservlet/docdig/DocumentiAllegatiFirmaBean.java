package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe che contiene i dati della firma di un file
 * @author gabriele.nencini
 *
 */
public class DocumentiAllegatiFirmaBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3333426144557530575L;
	
	private Boolean firmacheck = false;
	private Date firmacheckts;
	private boolean pdfaCompliant;
	
	public DocumentiAllegatiFirmaBean firmacheck(Boolean firmacheck) {
		this.firmacheck = firmacheck;
		return this;
	}
	
	public DocumentiAllegatiFirmaBean firmacheckts(Date firmacheckts) {
		this.firmacheckts = firmacheckts;
		return this;
	}

	public Boolean getFirmacheck() {
		return firmacheck;
	}

	public Date getFirmacheckts() {
		return firmacheckts;
	}
	
	public boolean isPdfaCompliant() {
		return pdfaCompliant;
	}

	public void setPdfaCompliant(boolean pdfaCompliant) {
		this.pdfaCompliant = pdfaCompliant;
	}

	@Override
	public String toString() {
		return "DocumentiAllegatiFirmaBean [" + (firmacheck != null ? "firmacheck=" + firmacheck + ", " : "")
				+ (firmacheckts != null ? "firmacheckts=" + firmacheckts : "") + "]";
	}
	
}
