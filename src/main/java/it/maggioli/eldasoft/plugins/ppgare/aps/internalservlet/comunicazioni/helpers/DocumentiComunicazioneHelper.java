package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class DocumentiComunicazioneHelper extends DocumentiAllegatiHelper 
	implements HttpSessionBindingListener, Serializable 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8450365797159239515L;

	// file generati contenenti il testo della comunicazione
	private List<File> fileGenerati = null;

	public DocumentiComunicazioneHelper(boolean creaDocRichiesti, boolean creaDocUlteriori) {
		super(null, null, null, creaDocRichiesti, creaDocUlteriori);
		fileGenerati = new ArrayList<File>();
	}

	public DocumentiComunicazioneHelper() {
		this(false, true);
	}

	public List<File> getFileGenerati() {
		return fileGenerati;
	}

	public void setFileGenerati(List<File> fileGenerati) {
		this.fileGenerati = fileGenerati;
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		super.valueUnbound(event);
		
		Iterator<File> iter = this.fileGenerati.listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}

}
