/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiDocument;
import it.eldasoft.sil.portgare.datatypes.ProdottoType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardDocumentiProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.xmlbeans.XmlException;

/**
 * Contenitore di sessione dei prodotti inseriti in bozza (J1), nuovi prodotti
 * inseriti (J2), prodotti eliminati (K), prodotti modificati (W)
 *
 * @author marco.perazzetta
 */
public class CarrelloProdottiSessione implements Serializable, HttpSessionBindingListener {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8661798863850918834L;

	/**
	 * codice del catalogo su cui si stanno effettuando modifiche
	 */
	private String currentCatalogo;

	private Map<String, ProdottiCatalogoSessione> listaProdottiPerCatalogo;

	// pdf generati per la richiesta di modifica prodotti da firmare
	private List<File> pdfGenerati = null;
	// riferimenti ai file generati nella work durante la gestione prodotti in carrello
	private List<File> fileGeneratiNellaWork;

	
	public String getCurrentCatalogo() {
		return currentCatalogo;
	}

	public void setCurrentCatalogo(String currentCatalogo) {
		this.currentCatalogo = currentCatalogo;
	}

	public Map<String, ProdottiCatalogoSessione> getListaProdottiPerCatalogo() {
		return listaProdottiPerCatalogo;
	}

	public void setListaProdottiPerCatalogo(Map<String, ProdottiCatalogoSessione> listaProdottiPerCatalogo) {
		this.listaProdottiPerCatalogo = listaProdottiPerCatalogo;
	}

	public List<File> getFileGeneratiNellaWork() {
		return fileGeneratiNellaWork;
	}

	public void setFileGeneratiNellaWork(List<File> fileGeneratiNellaWork) {
		this.fileGeneratiNellaWork = fileGeneratiNellaWork;
	}

	public List<File> getPdfGenerati() {
		return pdfGenerati;
	}

	public void setPdfGenerati(List<File> pdfGenerati) {
		this.pdfGenerati = pdfGenerati;
	}

	/**
	 * costruttore 
	 */
	public CarrelloProdottiSessione() {
		this.currentCatalogo = null;
		this.listaProdottiPerCatalogo = new HashMap<String, ProdottiCatalogoSessione>();
		this.fileGeneratiNellaWork = new ArrayList<File>();
		this.pdfGenerati = new ArrayList<File>();
	}

	/**
	 * ...
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0 the binding event
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		Iterator<File> iter = this.getPdfGenerati().listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
		iter = this.getFileGeneratiNellaWork().listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
		this.fileGeneratiNellaWork = null;
		this.pdfGenerati = null;

		for (ProdottiCatalogoSessione prodottiCatalogoSessione : this.getListaProdottiPerCatalogo().values()) {

			if (prodottiCatalogoSessione.getRiepilogo() != null && prodottiCatalogoSessione.getRiepilogo().exists()) {
				prodottiCatalogoSessione.getRiepilogo().delete();
			}
			prodottiCatalogoSessione.setRiepilogo(null);
			prodottiCatalogoSessione.setRiepilogoContentType(null);
			prodottiCatalogoSessione.setRiepilogoFileName(null);
			prodottiCatalogoSessione.setRiepilogoSize(null);
			if (prodottiCatalogoSessione.getXmlDati() != null && prodottiCatalogoSessione.getXmlDati().exists()) {
				prodottiCatalogoSessione.getXmlDati().delete();
			}
			prodottiCatalogoSessione.setXmlDati(null);
		}
	}

	/**
	 * ...
	 */
	public static CarrelloProdottiSessione getInstance(
			Map<String, Object> session, 
			String codiceCatalogo, 
			ICataloghiManager cataloghiManager) throws ApsException 
	{
		CarrelloProdottiSessione carrelloProdotti;
		if (null != session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			carrelloProdotti = (CarrelloProdottiSessione) session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			if (codiceCatalogo != null && !codiceCatalogo.equals(carrelloProdotti.getCurrentCatalogo())) {
				//Ho trovato in sessione un carrello che pero' non e' relativo al catalogo corrente, quindi sbianco l'oggetto
				carrelloProdotti = new CarrelloProdottiSessione();
			}
		} else {
			carrelloProdotti = new CarrelloProdottiSessione();
		}
		if (codiceCatalogo != null) {
			carrelloProdotti.setCurrentCatalogo(codiceCatalogo);
		}
		ProdottiCatalogoSessione.getInstance(carrelloProdotti, carrelloProdotti.getCurrentCatalogo(), cataloghiManager);
		session.put(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI, carrelloProdotti);
		return carrelloProdotti;
	}

	/**
	 * ...
	 */
	public WizardDocumentiProdottoHelper getDocumentiProdotto(String statoProdotto, int index) {
		WizardDocumentiProdottoHelper documentiHelper = null;
		
		if (CataloghiConstants.BOZZA.equals(statoProdotto)) {
			documentiHelper = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getBozze().get(index).getDocumenti();
		} else if (CataloghiConstants.PRODOTTO_MODIFICATO.equals(statoProdotto)) {
			documentiHelper = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiModificati().get(index).getDocumenti();
		} else if (CataloghiConstants.PRODOTTO_ELIMINATO.equals(statoProdotto)) {
			documentiHelper = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiEliminati().get(index).getDocumenti();
		} else {
			documentiHelper = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiInseriti().get(index).getDocumenti();
		}
		return documentiHelper;
	}

	/**
	 * ...
	 */
	public void setFilesReference(WizardDocumentiProdottoHelper documenti) {
		if (documenti.getImmagine() != null) {
			this.getFileGeneratiNellaWork().add(documenti.getImmagine());
		}
		for (File file : documenti.getCertificazioniRichieste()) {
			this.getFileGeneratiNellaWork().add(file);
		}
		for (File file : documenti.getSchedeTecniche()) {
			this.getFileGeneratiNellaWork().add(file);
		}
	}

	/**
	 * ...
	 */
	public void addFileReference(File f) {
		if (f != null) {
			this.getFileGeneratiNellaWork().add(f);
		}
	}

	/**
	 * ...
	 */
	private void deleteFilesReference(WizardDocumentiProdottoHelper documenti) {
		if (documenti != null) {
			if (documenti.getImmagine() != null) {
				this.getFileGeneratiNellaWork().remove(documenti.getImmagine());
				if (documenti.getImmagine().exists()) {
					documenti.getImmagine().delete();
				}
			}
			for (File file : documenti.getCertificazioniRichieste()) {
				this.getFileGeneratiNellaWork().remove(file);
				if (file.exists()) {
					file.delete();
				}
			}
			for (File file : documenti.getSchedeTecniche()) {
				this.getFileGeneratiNellaWork().remove(file);
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}

	/**
	 * ...
	 */
	public void aggiungiAModificati(WizardProdottoHelper prodotto) {
		prodotto.setStatoProdotto(CataloghiConstants.PRODOTTO_MODIFICATO);

		Long prodottoIdArticoloModificato = prodotto.getArticolo().getIdArticolo();
		List <WizardProdottoHelper> listaProdottiModificati = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiModificati();
		boolean prodottoPresente = false;
		
		for(int i = 0; i < listaProdottiModificati.size() ; i++){
			if(prodottoIdArticoloModificato == listaProdottiModificati.get(i).getArticolo().getIdArticolo())
			{
				prodottoPresente = true;
				break;
			}
		}
		
		if((listaProdottiModificati.isEmpty()) || (!prodottoPresente)) {
			prodotto.setIndex(this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiModificati().size());
			prodotto.setInCarrello(true);
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiModificati().add(prodotto);
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).reindexProducts(prodotto.getStatoProdotto());
		}
	}

	/**
	 * ...
	 */
	public void aggiungiAInseriti(WizardProdottoHelper prodotto) {
		prodotto.setStatoProdotto(CataloghiConstants.PRODOTTO_INSERITO);
		prodotto.setIndex(this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiInseriti().size());
		prodotto.setInCarrello(true);
		this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiInseriti().add(prodotto);
		this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).reindexProducts(prodotto.getStatoProdotto());
	}

	/**
	 * ...
	 */
	public void aggiungiABozze(WizardProdottoHelper prodotto) {
		prodotto.setStatoProdotto(CataloghiConstants.BOZZA);
		prodotto.setIndex(this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getBozze().size());
		prodotto.setInCarrello(true);
		this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getBozze().add(prodotto);
		this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).reindexProducts(prodotto.getStatoProdotto());
	}

	/**
	 * ...
	 */
	public void populateListaProdottiDaDocumento(
			ComunicazioneType comunicazione,
			DettaglioBandoIscrizioneType dettagliocatalogo, 
			GestioneProdottiDocument documento,
			ICataloghiManager cataloghiManager, 
			String saveDir) throws IOException, ApsException, XmlException 
	{
		ProdottiCatalogoSessione prodottiInCatalogo = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo());
		
		if (documento.getGestioneProdotti().getBozze() != null) {
			int i = 0;
			for (ProdottoType prodotto : documento.getGestioneProdotti().getBozze().getProdottoArray()) {
				WizardProdottoHelper bozza = prodottiInCatalogo.addProdotto(
						prodotto, 
						this.getCurrentCatalogo(), 
						dettagliocatalogo,
						CataloghiConstants.BOZZA, 
						cataloghiManager, 
						saveDir, 
						i++);
				this.setFilesReference(bozza.getDocumenti());
			}
		}
		if (documento.getGestioneProdotti().getInserimenti() != null) {
			int i = 0;
			for (ProdottoType prodotto : documento.getGestioneProdotti().getInserimenti().getProdottoArray()) {
				WizardProdottoHelper prodottoInserito = prodottiInCatalogo.addProdotto(
						prodotto, 
						this.getCurrentCatalogo(), 
						dettagliocatalogo,
						CataloghiConstants.PRODOTTO_INSERITO, 
						cataloghiManager, 
						saveDir, 
						i++);
				this.setFilesReference(prodottoInserito.getDocumenti());
			}
		}
		if (documento.getGestioneProdotti().getAggiornamenti() != null) {
			int i = 0;
			for (ProdottoType prodotto : documento.getGestioneProdotti().getAggiornamenti().getProdottoArray()) {
				WizardProdottoHelper prodottoModificato = prodottiInCatalogo.addProdotto(
						prodotto, 
						this.getCurrentCatalogo(), 
						dettagliocatalogo,
						CataloghiConstants.PRODOTTO_MODIFICATO, 
						cataloghiManager, 
						saveDir, 
						i++);
				this.setFilesReference(prodottoModificato.getDocumenti());
			}
		}
		if (documento.getGestioneProdotti().getArchiviazioni() != null) {
			int i = 0;
			for (ProdottoType prodotto : documento.getGestioneProdotti().getArchiviazioni().getProdottoArray()) {
				WizardProdottoHelper prodottoEliminato = prodottiInCatalogo.addProdotto(
						prodotto, 
						this.getCurrentCatalogo(), 
						dettagliocatalogo,
						CataloghiConstants.PRODOTTO_ELIMINATO, 
						cataloghiManager, 
						saveDir, 
						i++);
				this.setFilesReference(prodottoEliminato.getDocumenti());
			}
		}
		prodottiInCatalogo.setIdComunicazioneBozza(comunicazione.getDettaglioComunicazione().getId());
	}

	/**
	 * ...
	 */
	public void rimuoviDaBozze(WizardProdottoHelper prodotto) {
		try {
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getBozze().remove(prodotto.getIndex());
			this.deleteFilesReference(prodotto.getDocumenti());
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).reindexProducts(CataloghiConstants.BOZZA);
		} catch (Throwable ex) {
			// non fare nulla 
		}
	}

	/**
	 * ...
	 */
	public void rimuoviDaModificati(WizardProdottoHelper prodotto) {

		try {
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiModificati().remove(prodotto.getIndex());
			this.deleteFilesReference(prodotto.getDocumenti());
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).reindexProducts(CataloghiConstants.PRODOTTO_MODIFICATO);
		} catch (Throwable ex) {
			// non fare nulla
		}
	}

	/**
	 * ...
	 */
	public void rimuoviDaInseriti(WizardProdottoHelper prodotto) {
		try {
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).getProdottiInseriti().remove(prodotto.getIndex());
			this.deleteFilesReference(prodotto.getDocumenti());
			this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo()).reindexProducts(CataloghiConstants.PRODOTTO_INSERITO);
		} catch (Throwable ex) {
			// non fare nulla
		}
	}

	/**
	 * ...
	 */
	public int calculateProdottiCaricatiOE(Long articoloId) {
		int numeroProdottiInseriti = 0;
		int numeroProdottiEliminati = 0;
		ProdottiCatalogoSessione prodottiCatalogo = this.getListaProdottiPerCatalogo().get(this.getCurrentCatalogo());
		
		if (prodottiCatalogo != null) {
			numeroProdottiInseriti = prodottiCatalogo.getNumeroProdottiInseriti(articoloId);
			numeroProdottiEliminati = prodottiCatalogo.getNumeroProdottiEliminati(articoloId);
			if (prodottiCatalogo.getXmlDati() != null) {
				try {
					// si interpreta l'xml ricevuto
					GestioneProdottiDocument documento = GestioneProdottiDocument.Factory.parse(prodottiCatalogo.getXmlDati());
					it.eldasoft.sil.portgare.datatypes.ProdottoType[] prodottiInseriti = documento.getGestioneProdotti().getInserimenti().getProdottoArray();
					for (it.eldasoft.sil.portgare.datatypes.ProdottoType prodotto : prodottiInseriti) {
						if (prodotto.getIdArticolo() == articoloId) {
							numeroProdottiInseriti++;
						}
					}
					it.eldasoft.sil.portgare.datatypes.ProdottoType[] prodottiEliminati = documento.getGestioneProdotti().getArchiviazioni().getProdottoArray();
					for (it.eldasoft.sil.portgare.datatypes.ProdottoType prodotto : prodottiEliminati) {
						if (prodotto.getIdArticolo() == articoloId) {
							numeroProdottiEliminati++;
						}
					}
				} catch (XmlException ex) {
					ApsSystemUtils.logThrowable(ex, this, "calculateProdottiCaricatiOE");
				} catch (IOException ex) {
					ApsSystemUtils.logThrowable(ex, this, "calculateProdottiCaricatiOE");
				} catch (Throwable ex) {
					ApsSystemUtils.logThrowable(ex, this, "calculateProdottiCaricatiOE");
				}
			}
		}		
		return numeroProdottiInseriti - numeroProdottiEliminati;
	}
	
}
