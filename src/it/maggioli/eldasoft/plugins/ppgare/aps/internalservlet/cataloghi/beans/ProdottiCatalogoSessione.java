/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaType;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiDocument;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiType;
import it.eldasoft.sil.portgare.datatypes.ListaProdottiType;
import it.eldasoft.sil.portgare.datatypes.ProdottoType;
import it.eldasoft.sil.portgare.datatypes.ReportGestioneProdottiDocument;
import it.eldasoft.sil.portgare.datatypes.ReportGestioneProdottiType;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

/**
 * Contenitore dei prodotti inseriti in bozza (J1), nuovi prodotti inseriti
 * (J2), prodotti eliminati (K), prodotti modificati (W) relativi ad uno
 * specifico catalogo
 *
 * @author marco.perazzetta
 */
public class ProdottiCatalogoSessione implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7324129001273121852L;

	private String codiceCatalogo;

	private List<WizardProdottoHelper> prodottiInseriti;
	private List<WizardProdottoHelper> prodottiModificati;
	private List<WizardProdottoHelper> prodottiEliminati;
	private List<WizardProdottoHelper> bozze;

	private DettaglioBandoIscrizioneType catalogo;

	private Long idComunicazioneBozza;
	private Long idComunicazioneModifiche;
	private Long idComunicazioneVariazioneOfferta;

	private Date dataPresentazione;

	// informazioni per la gestione del riepilogo modifiche
	private File riepilogo = null;
	private String riepilogoContentType = null;
	private String riepilogoFileName = null;
	private Integer riepilogoSize = null;

	private File xmlDati = null;

	/**
	 * Memorizzo l'ultima categoria (codice) in cui e' stato inserito un articolo
	 * di questo catalogo
	 */
	private String ultimaCategoria;

	private boolean variazioneOfferta = false;

	/**
	 * cosrtruttore
	 */
	public ProdottiCatalogoSessione(String codiceCatalogo, DettaglioBandoIscrizioneType dettCatalogo) {
		this.codiceCatalogo = codiceCatalogo;
		this.catalogo = dettCatalogo;
		this.prodottiInseriti = new ArrayList<WizardProdottoHelper>();
		this.prodottiModificati = new ArrayList<WizardProdottoHelper>();
		this.prodottiEliminati = new ArrayList<WizardProdottoHelper>();
		this.bozze = new ArrayList<WizardProdottoHelper>();
		this.dataPresentazione = null;
		this.riepilogo = null;
		this.riepilogoContentType = null;
		this.riepilogoFileName = null;
		this.riepilogoSize = null;
		this.xmlDati = null;
		this.ultimaCategoria = null;
	}

	/**
	 * @return the codiceCatalogo
	 */
	public String getCodiceCatalogo() {
		return codiceCatalogo;
	}

	/**
	 * @param codiceCatalogo the codiceCatalogo to set
	 */
	public void setCodiceCatalogo(String codiceCatalogo) {
		this.codiceCatalogo = codiceCatalogo;
	}

	/**
	 * @return the prodottiInseriti
	 */
	public List<WizardProdottoHelper> getProdottiInseriti() {
		return prodottiInseriti;
	}

	/**
	 * @param prodottiInseriti the prodottiInseriti to set
	 */
	public void setProdottiInseriti(List<WizardProdottoHelper> prodottiInseriti) {
		this.prodottiInseriti = prodottiInseriti;
	}

	/**
	 * @return the prodottiModificati
	 */
	public List<WizardProdottoHelper> getProdottiModificati() {
		return prodottiModificati;
	}

	/**
	 * @param prodottiModificati the prodottiModificati to set
	 */
	public void setProdottiModificati(List<WizardProdottoHelper> prodottiModificati) {
		this.prodottiModificati = prodottiModificati;
	}

	/**
	 * @return the prodottiEliminati
	 */
	public List<WizardProdottoHelper> getProdottiEliminati() {
		return prodottiEliminati;
	}

	/**
	 * @param prodottiEliminati the prodottiEliminati to set
	 */
	public void setProdottiEliminati(List<WizardProdottoHelper> prodottiEliminati) {
		this.prodottiEliminati = prodottiEliminati;
	}

	/**
	 * @return the bozze
	 */
	public List<WizardProdottoHelper> getBozze() {
		return bozze;
	}

	/**
	 * @param bozze the bozze to set
	 */
	public void setBozze(List<WizardProdottoHelper> bozze) {
		this.bozze = bozze;
	}

	/**
	 * @return the catalogo
	 */
	public DettaglioBandoIscrizioneType getCatalogo() {
		return catalogo;
	}

	/**
	 * @param catalogo the catalogo to set
	 */
	public void setCatalogo(DettaglioBandoIscrizioneType catalogo) {
		this.catalogo = catalogo;
	}

	/**
	 * @return the dataPresentazione
	 */
	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	/**
	 * @param dataPresentazione the dataPresentazione to set
	 */
	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	/**
	 * Ritorna il numero di prodotti inseriti per il catalogo
	 *
	 * @return numero prodotti
	 */
	public int getNumeroProdottiInseriti() {
		return this.prodottiInseriti.size();
	}

	/**
	 * Ritorna il numero di prodotti inseriti per la lista specificata dato l'id
	 * articolo
	 *
	 * @param articoloId l'id dell'articolo
	 * @return numero prodotti
	 */
	private int getNumeroProdottiLista(List<WizardProdottoHelper> lista, Long articoloId) {
		int numProdotti = 0;
		for (WizardProdottoHelper prodotto : lista) {
			if (prodotto.getArticolo() != null && prodotto.getArticolo().getIdArticolo() == articoloId.longValue()) {
				numProdotti++;
			}
		}
		return numProdotti;
	}

	/**
	 * Ritorna il numero di prodotti inseriti per il catalogo e l'articolo scelti
	 *
	 * @param articoloId l'id dell'articolo
	 * @return numero prodotti
	 */
	public int getNumeroProdottiInseriti(Long articoloId) {
		return getNumeroProdottiLista(this.prodottiInseriti, articoloId);
	}

	/**
	 * Ritorna il numero di prodotti eliminati per il catalogo
	 *
	 * @return numero prodotti
	 */
	public int getNumeroProdottiEliminati() {
		return this.prodottiEliminati.size();
	}

	/**
	 * Ritorna il numero di prodotti eliminati per il catalogo e l'articolo scelti
	 *
	 * @param articoloId l'id dell'articolo
	 * @return numero prodotti
	 */
	public int getNumeroProdottiEliminati(Long articoloId) {
		return getNumeroProdottiLista(this.prodottiEliminati, articoloId);
	}

	/**
	 * Ritorna il numero di prodotti eliminati per il catalogo
	 *
	 * @return numero prodotti
	 */
	public int getNumeroProdottiModificati() {
		return this.prodottiModificati.size();
	}

	/**
	 * Ritorna il numero di prodotti eliminati per il catalogo e l'articolo scelti
	 *
	 * @param articoloId l'id dell'articolo
	 * @return numero prodotti
	 */
	public int getNumeroProdottiModificati(Long articoloId) {
		return getNumeroProdottiLista(this.prodottiModificati, articoloId);
	}

	/**
	 * Ritorna il numero di prodotti bozza per il catalogo
	 *
	 * @return numero prodotti
	 */
	public int getNumeroProdottiBozza() {
		return this.bozze.size();
	}

	/**
	 * Ritorna il numero di prodotti bozza per il catalogo e l'articolo scelti
	 *
	 * @param articoloId l'id dell'articolo
	 * @return numero prodotti
	 */
	public int getNumeroProdottiBozza(Long articoloId) {
		return getNumeroProdottiLista(this.bozze, articoloId);
	}

	/**
	 * @return the idComunicazioneBozza
	 */
	public Long getIdComunicazioneBozza() {
		return idComunicazioneBozza;
	}

	/**
	 * @param idComunicazioneBozza the idComunicazioneBozza to set
	 */
	public void setIdComunicazioneBozza(Long idComunicazioneBozza) {
		this.idComunicazioneBozza = idComunicazioneBozza;
	}

	/**
	 * @return the riepilogo
	 */
	public File getRiepilogo() {
		return riepilogo;
	}

	/**
	 * @param riepilogo the riepilogo to set
	 */
	public void setRiepilogo(File riepilogo) {
		this.riepilogo = riepilogo;
	}

	/**
	 * @return the riepilogoContentType
	 */
	public String getRiepilogoContentType() {
		return riepilogoContentType;
	}

	/**
	 * @param riepilogoContentType the riepilogoContentType to set
	 */
	public void setRiepilogoContentType(String riepilogoContentType) {
		this.riepilogoContentType = riepilogoContentType;
	}

	/**
	 * @return the riepilogoFileName
	 */
	public String getRiepilogoFileName() {
		return riepilogoFileName;
	}

	/**
	 * @param riepilogoFileName the riepilogoFileName to set
	 */
	public void setRiepilogoFileName(String riepilogoFileName) {
		this.riepilogoFileName = riepilogoFileName;
	}

	/**
	 * @return the riepilogoSize
	 */
	public Integer getRiepilogoSize() {
		return riepilogoSize;
	}

	/**
	 * @param riepilogoSize the riepilogoSize to set
	 */
	public void setRiepilogoSize(Integer riepilogoSize) {
		this.riepilogoSize = riepilogoSize;
	}

	/**
	 * @return the xmlDati
	 */
	public File getXmlDati() {
		return xmlDati;
	}

	/**
	 * @param xmlDati the xmlDati to set
	 */
	public void setXmlDati(File xmlDati) {
		this.xmlDati = xmlDati;
	}

	/**
	 * @return the idComunicazioneModifiche
	 */
	public Long getIdComunicazioneModifiche() {
		return idComunicazioneModifiche;
	}

	/**
	 * @param idComunicazioneModifiche the idComunicazioneModifiche to set
	 */
	public void setIdComunicazioneModifiche(Long idComunicazioneModifiche) {
		this.idComunicazioneModifiche = idComunicazioneModifiche;
	}

	/**
	 * @return the ultimaCategoria
	 */
	public String getUltimaCategoria() {
		return ultimaCategoria;
	}

	/**
	 * @param ultimaCategoria the ultimaCategoria to set
	 */
	public void setUltimaCategoria(String ultimaCategoria) {
		this.ultimaCategoria = ultimaCategoria;
	}

	/**
	 * @return the variazioneOfferta
	 */
	public boolean isVariazioneOfferta() {
		return variazioneOfferta;
	}

	/**
	 * @param variazioneOfferta the variazioneOfferta to set
	 */
	public void setVariazioneOfferta(boolean variazioneOfferta) {
		this.variazioneOfferta = variazioneOfferta;
	}

	/**
	 * @return the idComunicazioneVariazioneOfferta
	 */
	public Long getIdComunicazioneVariazioneOfferta() {
		return idComunicazioneVariazioneOfferta;
	}

	/**
	 * @param idComunicazioneVariazioneOfferta the
	 * idComunicazioneVariazioneOfferta to set
	 */
	public void setIdComunicazioneVariazioneOfferta(Long idComunicazioneVariazioneOfferta) {
		this.idComunicazioneVariazioneOfferta = idComunicazioneVariazioneOfferta;
	}

	/**
	 * Crea l'oggetto documento per la generazione della stringa XML con le
	 * modifiche ai prodotti del catalogo.
	 *
	 * @param document documento da creare e popolare
	 * @param datiImpresaHelper dati dell'impresa con cui popolare il document
	 * @param tipoOperazione indica la tipologia di operazione che sto facendo
	 * (invio modifiche prodotti, salvataggio prodotti, salvataggio bozze)
	 * @return oggetto documento contenente i dati di modifica ai prodotti del
	 * catalogo
	 * @throws IOException
	 */
	public XmlObject getXmlDocument(
			XmlObject document,
			WizardDatiImpresaHelper datiImpresaHelper,
			String tipoOperazione) throws IOException 
	{
		boolean allegaDocs = !tipoOperazione.equals(CataloghiConstants.COMUNICAZIONE_INVIO_VARIAZIONE_OFFERTA);
		
		if (document instanceof GestioneProdottiDocument) {
			GestioneProdottiType doc = ((GestioneProdottiDocument) document).addNewGestioneProdotti();
			GregorianCalendar dataUfficiale = new GregorianCalendar();
			if (this.dataPresentazione != null) {
				dataUfficiale.setTime(this.dataPresentazione);
			}
			doc.setDataPresentazione(dataUfficiale);
			doc.setCodiceCatalogo(this.codiceCatalogo);
			if (CataloghiConstants.COMUNICAZIONE_SALVATAGGIO_PRODOTTI.equals(tipoOperazione)
				|| CataloghiConstants.COMUNICAZIONE_INVIO_PRODOTTI.equals(tipoOperazione)
				|| CataloghiConstants.COMUNICAZIONE_INVIO_VARIAZIONE_OFFERTA.equals(tipoOperazione)) {
				this.addProdottiAggiornati(doc.addNewAggiornamenti(), allegaDocs);
				this.addProdottiInseriti(doc.addNewInserimenti(), allegaDocs);
				this.addProdottiEliminati(doc.addNewArchiviazioni(), allegaDocs);
			}
			if (CataloghiConstants.COMUNICAZIONE_SALVATAGGIO_BOZZE.equals(tipoOperazione)
				|| CataloghiConstants.COMUNICAZIONE_SALVATAGGIO_PRODOTTI.equals(tipoOperazione)) {
				this.addProdottiBozza(doc.addNewBozze(), allegaDocs);
			}
		} else {
			ReportGestioneProdottiType doc = ((ReportGestioneProdottiDocument) document).addNewReportGestioneProdotti();
			doc.setCodiceCatalogo(this.codiceCatalogo);
			this.addProdottiAggiornati(doc.addNewAggiornamenti(), allegaDocs);
			this.addProdottiInseriti(doc.addNewInserimenti(), allegaDocs);
			this.addProdottiEliminati(doc.addNewArchiviazioni(), allegaDocs);
			this.addDatiImpresa(doc.addNewDatiImpresa(), datiImpresaHelper);
		}
		document.documentProperties().setEncoding("UTF-8");
		return document;
	}

	private void addDatiImpresa(DatiImpresaType datiImpresa, WizardDatiImpresaHelper datiImpresaHelper) {
		WizardDatiImpresaHelper.addNewImpresa(
				datiImpresaHelper.getDatiPrincipaliImpresa(),
				datiImpresaHelper.getDatiUlterioriImpresa(), 
				datiImpresaHelper.getIndirizziImpresa(),
				datiImpresaHelper.getAltriDatiAnagraficiImpresa(), 
				datiImpresaHelper.isLiberoProfessionista(),
				datiImpresa);
		
		// set dei soggetti
		if (!datiImpresaHelper.isLiberoProfessionista()) {
			WizardDatiImpresaHelper.addNewReferentiImpresa(datiImpresaHelper.getLegaliRappresentantiImpresa(), datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(datiImpresaHelper.getDirettoriTecniciImpresa(), datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(datiImpresaHelper.getAltreCaricheImpresa(), datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(datiImpresaHelper.getCollaboratoriImpresa(), datiImpresa);
		}
	}

	/**
	 * Popola i dati dei prodotti aggiornati
	 *
	 * @param prodotti lista dei prodotti aggiornati
	 * @param allegaDocumenti indica se generare i nodi dei documenti allegati al
	 * prodotto
	 */
	private void addProdottiAggiornati(
			ListaProdottiType prodotti,
			boolean allegaDocumenti) throws IOException 
	{
		for (WizardProdottoHelper prodotto : this.prodottiModificati) {
			WizardProdottoHelper.addProdotto(prodotto, prodotti.addNewProdotto(), allegaDocumenti);
		}
	}

	/**
	 * Popola i dati dei prodotti inseriti
	 *
	 * @param prodotti lista dei prodotti inseriti
	 * @param allegaDocumenti indica se generare i nodi dei documenti allegati al
	 * prodotto
	 */
	private void addProdottiInseriti(
			ListaProdottiType prodotti,
			boolean allegaDocumenti) throws IOException 
	{
		for (WizardProdottoHelper prodotto : this.prodottiInseriti) {
			WizardProdottoHelper.addProdotto(prodotto, prodotti.addNewProdotto(), allegaDocumenti);
		}
	}

	/**
	 * Popola i dati dei prodotti eliminati
	 *
	 * @param prodotti lista dei prodotti eliminati
	 * @param allegaDocumenti indica se generare i nodi dei documenti allegati al
	 * prodotto
	 */
	private void addProdottiEliminati(
			ListaProdottiType prodotti,
			boolean allegaDocumenti) throws IOException 
	{
		for (WizardProdottoHelper prodotto : this.prodottiEliminati) {
			WizardProdottoHelper.addProdotto(prodotto, prodotti.addNewProdotto(), allegaDocumenti);
		}
	}

	/**
	 * Popola i dati dei prodotti in bozza
	 *
	 * @param prodotti lista dei prodotti bozza
	 * @param allegaDocumenti indica se generare i nodi dei documenti allegati al
	 * prodotto
	 */
	private void addProdottiBozza(
			ListaProdottiType prodotti,
			boolean allegaDocumenti) throws IOException 
	{
		for (WizardProdottoHelper prodotto : this.bozze) {
			WizardProdottoHelper.addProdotto(prodotto, prodotti.addNewProdotto(), allegaDocumenti);
		}
	}

	/**
	 * Aggiunge un'istanza della classe al carrello prodotti in sessione
	 *
	 * @param carrelloProdotti carrello prodotti in sessione
	 * @param codiceCatalogo codice del catalogo su cui si sta lavorando
	 * @param cataloghiManager il manager dei cataloghi
	 * @throws ApsException
	 */
	public static void getInstance(
			CarrelloProdottiSessione carrelloProdotti,
			String codiceCatalogo, 
			ICataloghiManager cataloghiManager) throws ApsException 
	{
		ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo()
			.get(carrelloProdotti.getCurrentCatalogo());
		if (prodottiInCarrello == null) {
			DettaglioBandoIscrizioneType dettCatalogo = cataloghiManager.getDettaglioCatalogo(codiceCatalogo);
			prodottiInCarrello = new ProdottiCatalogoSessione(codiceCatalogo, dettCatalogo);
			carrelloProdotti.getListaProdottiPerCatalogo().put(codiceCatalogo, prodottiInCarrello);
		}
	}

	/**
	 * Verifica se ci sono modifiche (inserimenti, modifiche, eliminazioni) ai
	 * prodotti del catalogo corrente nel carrello.
	 *
	 * @return true se ci sono modifiche, false altrimenti
	 */
	public boolean hasModifiche() {
		return !this.getProdottiInseriti().isEmpty()
			   || !this.getProdottiModificati().isEmpty()
			   || !this.getProdottiEliminati().isEmpty();
	}

	/**
	 * Verifica se ci sono solo bozze nel carrello in sessione
	 *
	 * @return true se ci sono solo bozze, false altrimenti
	 */
	public boolean hasOnlyBozze() {
		return this.getProdottiInseriti().isEmpty()
			   && this.getProdottiModificati().isEmpty()
			   && this.getProdottiEliminati().isEmpty()
			   && !this.getBozze().isEmpty();
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista
	 */
	private boolean isPresent(List<WizardProdottoHelper> lista, Long idProdotto) {
		boolean trovato = false;
		for (WizardProdottoHelper prodotto : lista) {
			if (prodotto.getDettaglioProdotto().getId() == idProdotto) {
				trovato = true;
			}
		}
		return trovato;
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista
	 */
	private boolean isPresent(List<WizardProdottoHelper> lista, String codiceProdottoFornitore) {
		boolean trovato = false;
		for (WizardProdottoHelper prodotto : lista) {
			if (StringUtils.equalsIgnoreCase(
					prodotto.getDettaglioProdotto().getCodiceProdottoFornitore(), codiceProdottoFornitore)) {
				trovato = true;
			}
		}
		return trovato;
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista modifiche
	 *
	 * @param idProdotto id del prodotto da cercare
	 * @return true se il prodotto è già stato modificato
	 */
	public boolean prodottoGiaModificato(Long idProdotto) {
		return this.isPresent(this.prodottiModificati, idProdotto);
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista eliminazioni
	 *
	 * @param idProdotto id del prodotto da cercare
	 * @return true se il prodotto è già stato eliminato
	 */
	public boolean prodottoGiaEliminato(Long idProdotto) {
		return this.isPresent(this.prodottiEliminati, idProdotto);
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista bozze
	 *
	 * @param indice indice del prodotto da cercare nella lista bozze
	 * @return true se il prodotto è già stato messo come bozza
	 */
	public boolean prodottoGiaBozza(int indice) {
		return this.isPresent(this.bozze, new Long(indice));
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista bozze
	 *
	 * @param codiceProdottoFornitore indice del prodotto da cercare nella lista
	 * bozze
	 * @return true se il prodotto è già stato messo come bozza
	 */
	public boolean prodottoGiaInserito(String codiceProdottoFornitore) {
		return this.isPresent(this.prodottiInseriti, codiceProdottoFornitore);
	}

	/**
	 * Controlla se un dato prodotto è già inserito nella lista bozze
	 *
	 * @param codiceProdottoFornitore codice fornitore del prodotto da cercare tra
	 * le bozze
	 * @return true se il prodotto è già stato messo come bozza
	 */
	public boolean prodottoGiaBozza(String codiceProdottoFornitore) {
		return this.isPresent(this.bozze, codiceProdottoFornitore);
	}

	public void reindexProducts(String statoProdotto) {

		if (CataloghiConstants.BOZZA.equals(statoProdotto)) {
			this.bozze = reindex(this.bozze);
		} else if (CataloghiConstants.PRODOTTO_ELIMINATO.equals(statoProdotto)) {
			this.prodottiEliminati = reindex(this.prodottiEliminati);
		} else if (CataloghiConstants.PRODOTTO_INSERITO.equals(statoProdotto)) {
			this.prodottiInseriti = reindex(this.prodottiInseriti);
		} else if (CataloghiConstants.PRODOTTO_MODIFICATO.equals(statoProdotto)) {
			this.prodottiModificati = reindex(this.prodottiModificati);
		}
	}

	private static List<WizardProdottoHelper> reindex(List<WizardProdottoHelper> lista) {

		List<WizardProdottoHelper> reindexedLista = new ArrayList<WizardProdottoHelper>();
		for (int i = 0; i < lista.size(); i++) {
			WizardProdottoHelper p = lista.get(i);
			p.setIndex(i);
			reindexedLista.add(p);
		}
		return reindexedLista;
	}

	public void cleanRiepilogo() {

		if (this.getRiepilogo() != null && this.getRiepilogo().exists()) {
			this.getRiepilogo().delete();
		}
		this.setRiepilogo(null);
		this.setRiepilogoContentType(null);
		this.setRiepilogoFileName(null);
		this.setRiepilogoSize(null);
		if (this.getXmlDati() != null && this.getXmlDati().exists()) {
			this.getXmlDati().delete();
		}
		this.setXmlDati(null);
	}

	public WizardProdottoHelper addProdotto(ProdottoType prodottoType, String catalogo,
					DettaglioBandoIscrizioneType dettaglioCatalogo, String statoProdotto,
					ICataloghiManager cataloghiManager, String saveDir, int index)
					throws IOException, ApsException, XmlException {

		WizardProdottoHelper prodotto = fillProdotto(prodottoType, catalogo,
						dettaglioCatalogo, statoProdotto, cataloghiManager, saveDir, index);
		if (statoProdotto.equals(CataloghiConstants.BOZZA)) {
			this.getBozze().add(prodotto);
		} else if (statoProdotto.equals(CataloghiConstants.PRODOTTO_INSERITO)) {
			this.getProdottiInseriti().add(prodotto);
		} else if (statoProdotto.equals(CataloghiConstants.PRODOTTO_MODIFICATO)) {
			this.getProdottiModificati().add(prodotto);
		} else {
			this.getProdottiEliminati().add(prodotto);
		}
		return prodotto;
	}

	/**
	 * A partire dalla comunicazione, ricreo l'oggetto bozza nel carrello
	 *
	 * @param prodotto il prodotto nella comunicazione da processare
	 * @param catalogo il codice del catalogo in esame
	 * @param dettaglioCatalogo il dettaglio del catalogo in esame
	 * @param statoProdotto lo stato del prodotto
	 * @return l'elemento del carrello
	 * @throws IOException
	 * @throws ApsException
	 */
	private WizardProdottoHelper fillProdotto(ProdottoType prodotto, String catalogo,
					DettaglioBandoIscrizioneType dettaglioCatalogo, String statoProdotto,
					ICataloghiManager cataloghiManager, String saveDir, int index)
					throws IOException, ApsException, XmlException {

		WizardProdottoHelper prodottoHelper = new WizardProdottoHelper();
		this.setDatiProdotto(prodottoHelper, prodotto);
		this.setDocumenti(prodottoHelper, prodotto, saveDir);
		this.setDatiArticolo(prodottoHelper, prodotto, cataloghiManager);
		prodottoHelper.setStatoProdotto(statoProdotto);
		prodottoHelper.setCatalogo(dettaglioCatalogo);
		prodottoHelper.setCodiceCatalogo(catalogo);
		prodottoHelper.setIndex(index);
		return prodottoHelper;
	}

	/**
	 * Popola l'helper con i dati dell'articolo presenti nella comunicazione
	 * analizzata
	 *
	 * @param prodottoHelper helper da popolare
	 * @param bozza elemento della comunicazione
	 * @throws IOException
	 * @throws ApsException
	 */
	private void setDatiArticolo(WizardProdottoHelper prodottoHelper, ProdottoType bozza,
					ICataloghiManager cataloghiManager) throws IOException, ApsException {

		prodottoHelper.getArticolo().setIdArticolo(bozza.getIdArticolo());
		ArticoloType articolo = cataloghiManager.getArticolo(bozza.getIdArticolo());
		prodottoHelper.getArticolo().setDettaglioArticolo(articolo);
		prodottoHelper.getDettaglioProdotto().setIdArticolo(bozza.getIdArticolo());
	}

	/**
	 * Popola l'helper con i dati del prodotto presenti nella comunicazione
	 * analizzata
	 *
	 * @param prodottoHelper helper da popolare
	 * @param bozza elemento della comunicazione
	 * @throws IOException
	 * @throws ApsException
	 */
	private void setDatiProdotto(WizardProdottoHelper prodottoHelper,
					ProdottoType bozza) throws IOException, ApsException {

		it.eldasoft.www.sil.WSGareAppalto.ProdottoType prodottoType
						= prodottoHelper.getDettaglioProdotto();
		if (prodottoType == null) {
			prodottoType = new it.eldasoft.www.sil.WSGareAppalto.ProdottoType();
		}
		prodottoType.setAliquotaIVA(bozza.getAliquotaIVA());
		prodottoType.setCodiceProdottoFornitore(bozza.getCodiceProdottoFornitore());
		prodottoType.setCodiceProdottoProduttore(bozza.getCodiceProdottoProduttore());
		prodottoType.setDataScadenzaOfferta(bozza.getDataScadenzaOfferta().getTime());
		prodottoType.setDescrizioneAggiuntiva(bozza.getDescrizioneAggiuntiva());
		prodottoType.setDimensioni(bozza.getDimensioni());
		prodottoType.setGaranzia(bozza.getGaranzia());
		prodottoType.setId(bozza.getIdProdotto());
		prodottoType.setMarcaProdottoProduttore(bozza.getMarcaProdottoProduttore());
		prodottoType.setNomeCommerciale(bozza.getNomeCommerciale());
		prodottoType.setPrezzoUnitario(bozza.getPrezzoUnitario());
		prodottoType.setPrezzoUnitarioPerAcquisto(bozza.getPrezzoUnitarioPerAcquisto());
		prodottoType.setQuantitaUMAcquisto(bozza.getQuantitaUMAcquisto());
		prodottoType.setQuantitaUMPrezzo(bozza.getQuantitaUMPrezzo());
		prodottoType.setStato(bozza.getStato());
		prodottoType.setTempoConsegna(bozza.getTempoConsegna());
		prodottoHelper.setDettaglioProdotto(prodottoType);
	}

	/**
	 * Popola l'helper con i documenti presenti nella comunicazione analizzata
	 *
	 * @param documenti helper da popolare
	 * @param bozza elemento della comunicazione
	 * @throws IOException
	 */
	private void setDocumenti(WizardProdottoHelper prodottoHelper,
					ProdottoType bozza, String saveDir) 
					throws FileNotFoundException, IOException {

		DocumentoType documento;
		//certificazioni richieste
		if (bozza.getCertificazioniRichieste() != null
						&& bozza.getCertificazioniRichieste().sizeOfDocumentoArray() > 0) {
			
			for (int i = 0; i < bozza.getCertificazioniRichieste().sizeOfDocumentoArray(); i++) {

				documento = bozza.getCertificazioniRichieste().getDocumentoArray(i);
				prodottoHelper.getDocumenti().getCertificazioniRichiesteContentType()
								.add(i, documento.getContentType());
				prodottoHelper.getDocumenti().getCertificazioniRichiesteFileName()
								.add(i, documento.getNomeFile());
				File f = new File(saveDir
								+ File.separatorChar
								+ FileUploadUtilities.generateFileName());
				prodottoHelper.getDocumenti().getCertificazioniRichieste().add(i, f);
				prodottoHelper.getDocumenti().getCertificazioniRichiesteSize()
								.add(i, (int) Math.ceil(f.length() / 1024.0));
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(f);
					fos.write(documento.getFile());
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException ex) {
							//non riesco a chiudere lo stream, ma il file è già scritto
							ApsSystemUtils.logThrowable(ex, ProdottiCatalogoSessione.class, "setDocumenti");
						}
					}
				}
			}
		}

		//Schede tecniche
		if (bozza.getSchedeTecniche() != null 
						&& bozza.getSchedeTecniche().sizeOfDocumentoArray() > 0) {
			
			for (int i = 0; i < bozza.getSchedeTecniche().sizeOfDocumentoArray(); i++) {

				documento = bozza.getSchedeTecniche().getDocumentoArray(i);
				prodottoHelper.getDocumenti().getSchedeTecnicheContentType()
								.add(i, documento.getContentType());
				prodottoHelper.getDocumenti().getSchedeTecnicheFileName()
								.add(i, documento.getNomeFile());
				File f = new File(saveDir
								+ File.separatorChar
								+ FileUploadUtilities.generateFileName());
				prodottoHelper.getDocumenti().getSchedeTecniche().add(i, f);
				prodottoHelper.getDocumenti().getSchedeTecnicheSize()
								.add(i, (int) Math.ceil(f.length() / 1024.0));
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(f);
					fos.write(documento.getFile());
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException ex) {
							//non riesco a chiudere lo stream, ma il file è già scritto
							ApsSystemUtils.logThrowable(ex, ProdottiCatalogoSessione.class, "setDocumenti");
						}
					}
				}
			}
		}

		//Immagine
		if (bozza.getImmagine() != null) {
			
			documento = bozza.getImmagine();
			prodottoHelper.getDocumenti().setImmagineContentType(documento.getContentType());
			prodottoHelper.getDocumenti().setImmagineFileName(documento.getNomeFile());
			File f = new File(saveDir
							+ File.separatorChar
							+ FileUploadUtilities.generateFileName());
			prodottoHelper.getDocumenti().setImmagine(f);
			prodottoHelper.getDocumenti().setImmagineSize((int) Math.ceil(f.length() / 1024.0));
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
				fos.write(documento.getFile());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ex) {
						//non riesco a chiudere lo stream, ma il file è già scritto
							ApsSystemUtils.logThrowable(ex, ProdottiCatalogoSessione.class, "setDocumenti");
					}
				}
			}
		}
	}

	/**
	 * Ritorna dal contenitore in sessione il prodotto con id voluto
	 *
	 * @param lista la lista da cui ricavare il prodotto
	 * @param idProdotto l'id del prodotto da ricavare
	 * @return il prodotto desiderato
	 */
	public WizardProdottoHelper getProdotto(List<WizardProdottoHelper> lista, 
					Long idProdotto) {

		WizardProdottoHelper prodotto = null;
		for (WizardProdottoHelper temp : lista) {
			if (temp.getDettaglioProdotto().getId() == idProdotto) {
				prodotto = temp;
			}
		}
		return prodotto;
	}
}
