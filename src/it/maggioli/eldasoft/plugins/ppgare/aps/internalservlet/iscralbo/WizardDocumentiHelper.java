package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati per i documenti
 *
 * @author Stefano.Sabbadin
 */
public class WizardDocumentiHelper extends DocumentiAllegatiHelper implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -747416711915050922L;

    // pdf generati per la domanda d'iscrizione e altre stampe pdf
	private List<File> pdfGenerati = null;

	public WizardDocumentiHelper() {
		super(null, null, null, true, true);
		this.pdfGenerati = new ArrayList<File>();
	}

	public List<File> getPdfGenerati() {
		return pdfGenerati;
	}
	
	/**
	 * popola l'helper di documenti con l'elenco dei documenti recuperati da una comunicazione
	 */
	public void popolaDocumentiFromComunicazione(
			ListaDocumentiType listaDocumenti, 
			long idComunicazione) 
		throws IOException 
	{
		super.popolaDocumentiFromComunicazione(listaDocumenti, idComunicazione);
	}
	
}
