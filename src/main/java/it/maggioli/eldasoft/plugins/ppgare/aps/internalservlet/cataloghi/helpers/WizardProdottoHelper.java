package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers;

import com.agiletec.aps.system.ApsSystemUtils;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;

import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaCatalogoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IDocumentiDigitaliManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.lang.StringUtils;

/**
 * Contenitore dei dati del wizard iscrizione impresa all'albo fornitori.
 *
 * @author Stefano.Sabbadin
 */
public class WizardProdottoHelper implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2557699330060352207L;

	//private Integer stato;
	/**
	 * Chiave del bando per elenco fornitori usato per l'iscrizione
	 */
	private String codiceCatalogo;

	/**
	 * Helper per lo step di inserimento dei documenti
	 */
	private WizardDocumentiProdottoHelper documenti;

	/**
	 * Helper per lo step di inserimento categorie/prestazioni d'interesse
	 */
	private WizardArticoloHelper articolo;

	/**
	 * true se non sono previste categorie, false altrimenti
	 */
	private boolean articoliAssenti;

	/**
	 * Indica, quando settato, che si tratta di un aggiornamento di dati o
	 * documenti del prodotto; di default vale false, e quindi indica che si
	 * riferisce ad un inserimento prodotto o ad una bozza
	 */
	private boolean aggiornamento;

	/**
	 * Indica, quando settato, che sono stati modificati i documenti o l'immagine
	 * del prodotto
	 */
	private boolean aggiornatoDocumenti;

	/**
	 * Contenitore dei dati del prodotto
	 */
	private ProdottoType dettaglioProdotto;

	private DettaglioBandoIscrizioneType catalogo;

	/**
	 * Data ufficiale NTP dell'operazione effettuata
	 */
	private Date dataOperazione;

	/**
	 * stato del prodotto nel carrello
	 */
	private String statoProdotto;

	/**
	 * Per un prodotto in modifica, indica se era presente a carrello o a sistema
	 */
	private boolean inCarrello;

	/**
	 * Indice del prodotto nella corrispondente lista del carrello in base al suo
	 * stato
	 */
	private int index;

	/**
	 * Indica che il prodotto è già stato caricato per il wizard di modifica e non
	 * serve clonarlo di nuovo
	 */
	private boolean alreadyLoaded;

	/**
	 * Pagina da cui si proviene nel wizard (dettaglio prodotto, gestione
	 * prodotti, dettaglio articolo)
	 */
	private String wizardSourcePage;

	/**
	 * Flag che identifica un prodotto a sistema con una occorrezza nel carrello
	 * dei prodotti modificati
	 */
	private boolean modificato;

	/**
	 * Flag che identifica un prodotto a sistema con una occorrezza nel carrello
	 * dei prodotti eliminati
	 */
	private boolean archiviato;

	
	public String getCodiceCatalogo() {
		return codiceCatalogo;
	}

	public void setCodiceCatalogo(String codiceCatalogo) {
		this.codiceCatalogo = codiceCatalogo;
	}

	public WizardDocumentiProdottoHelper getDocumenti() {
		return documenti;
	}

	public void setDocumenti(WizardDocumentiProdottoHelper documenti) {
		this.documenti = documenti;
	}

	public boolean isAggiornamento() {
		return aggiornamento;
	}

	public void setAggiornamento(boolean aggiornamento) {
		this.aggiornamento = aggiornamento;
	}

	public boolean isAggiornatoDocumenti() {
		return aggiornatoDocumenti;
	}

	public void setAggiornatoDocumenti(boolean aggiornatoDocumenti) {
		this.aggiornatoDocumenti = aggiornatoDocumenti;
	}

	public ProdottoType getDettaglioProdotto() {
		return dettaglioProdotto;
	}

	public void setDettaglioProdotto(ProdottoType dettaglioProdotto) {
		this.dettaglioProdotto = dettaglioProdotto;
	}

	public boolean isArticoliAssenti() {
		return articoliAssenti;
	}

	public void setArticoliAssenti(boolean articoliAssenti) {
		this.articoliAssenti = articoliAssenti;
	}

	public WizardArticoloHelper getArticolo() {
		return articolo;
	}

	public void setArticolo(WizardArticoloHelper articolo) {
		this.articolo = articolo;
	}

	public DettaglioBandoIscrizioneType getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(DettaglioBandoIscrizioneType catalogo) {
		this.catalogo = catalogo;
	}

	public Date getDataOperazione() {
		return dataOperazione;
	}

	public void setDataOperazione(Date dataOperazione) {
		this.dataOperazione = dataOperazione;
	}

	public String getStatoProdotto() {
		return statoProdotto;
	}

	public void setStatoProdotto(String statoProdotto) {
		this.statoProdotto = statoProdotto;
	}

	public boolean isInCarrello() {
		return inCarrello;
	}

	public void setInCarrello(boolean inCarrello) {
		this.inCarrello = inCarrello;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isAlreadyLoaded() {
		return alreadyLoaded;
	}

	public void setAlreadyLoaded(boolean changeIn) {
		this.alreadyLoaded = changeIn;
	}

	public String getWizardSourcePage() {
		return wizardSourcePage;
	}

	public void setWizardSourcePage(String wizardSourcePage) {
		this.wizardSourcePage = wizardSourcePage;
	}

	public boolean isModificato() {
		return modificato;
	}

	public void setModificato(boolean modificato) {
		this.modificato = modificato;
	}

	public boolean isArchiviato() {
		return archiviato;
	}

	public void setArchiviato(boolean archiviato) {
		this.archiviato = archiviato;
	}
	
	/**
	 * Inizializza il contenitore vuoto attribuendo i valori di default
	 */
	public WizardProdottoHelper() {
		this.documenti = new WizardDocumentiProdottoHelper();
		this.articolo = new WizardArticoloHelper();
		this.aggiornamento = false;
		this.aggiornatoDocumenti = false;
		this.dettaglioProdotto = null;
		this.catalogo = null;
		this.inCarrello = false;
		this.wizardSourcePage = null;
		this.modificato = false;
		this.archiviato = false;
	}

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

		if (this.documenti != null) {
			Iterator<File> iter = this.documenti.getCertificazioniRichieste().listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if (file.exists()) {
					file.delete();
				}
			}
			iter = this.documenti.getSchedeTecniche().listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if (file.exists()) {
					file.delete();
				}
			}
			if (this.documenti.getImmagine() != null) {
				if (this.documenti.getImmagine().exists()) {
					this.documenti.getImmagine().delete();
				}
			}
		}
		this.documenti = null;
	}

	/**
	 * Crea un oggetto della classe ProdottoType nel contenitore dei dati del
	 * prodotto prelevando i dati dalla sorgente in input
	 *
	 * @param datiProdotto dati del prodotto
	 * @param prodotto contenitore dati del prodotto nell'xml
	 * @param allegaDocumenti indica se si deve generare il nodo degli allegati
	 * @throws java.io.IOException
	 */
	public static void addProdotto(
			WizardProdottoHelper datiProdotto,
			it.eldasoft.sil.portgare.datatypes.ProdottoType prodotto, 
			boolean allegaDocumenti) throws IOException 
	{
		prodotto.setAliquotaIVA(datiProdotto.getDettaglioProdotto().getAliquotaIVA());
		if (StringUtils.isNotBlank(datiProdotto.getDettaglioProdotto().getCodiceProdottoFornitore())) {
			prodotto.setCodiceProdottoFornitore(datiProdotto.getDettaglioProdotto().getCodiceProdottoFornitore());
		}
		if (StringUtils.isNotBlank(datiProdotto.getDettaglioProdotto().getCodiceProdottoProduttore())) {
			prodotto.setCodiceProdottoProduttore(datiProdotto.getDettaglioProdotto().getCodiceProdottoProduttore());
		}
		if (datiProdotto.getDettaglioProdotto().getDataScadenzaOfferta() != null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(datiProdotto.getDettaglioProdotto().getDataScadenzaOfferta());
			prodotto.setDataScadenzaOfferta(cal);
		}
		if (StringUtils.isNotBlank(datiProdotto.getDettaglioProdotto().getDescrizioneAggiuntiva())) {
			prodotto.setDescrizioneAggiuntiva(datiProdotto.getDettaglioProdotto().getDescrizioneAggiuntiva());
		}
		if (StringUtils.isNotBlank(datiProdotto.getDettaglioProdotto().getDimensioni())) {
			prodotto.setDimensioni(datiProdotto.getDettaglioProdotto().getDimensioni());
		}
		if (datiProdotto.getDettaglioProdotto().getGaranzia() != null) {
			prodotto.setGaranzia(datiProdotto.getDettaglioProdotto().getGaranzia());
		}
		if (datiProdotto.getDettaglioProdotto().getId() > 0) {
			prodotto.setIdProdotto(datiProdotto.getDettaglioProdotto().getId());
		}
		if (StringUtils.isNotBlank(datiProdotto.getDettaglioProdotto().getMarcaProdottoProduttore())) {
			prodotto.setMarcaProdottoProduttore(datiProdotto.getDettaglioProdotto().getMarcaProdottoProduttore());
		}
		if (StringUtils.isNotBlank(datiProdotto.getDettaglioProdotto().getNomeCommerciale())) {
			prodotto.setNomeCommerciale(datiProdotto.getDettaglioProdotto().getNomeCommerciale());
		}
		prodotto.setPrezzoUnitario(datiProdotto.getDettaglioProdotto().getPrezzoUnitario());
		prodotto.setPrezzoUnitarioPerAcquisto(datiProdotto.getDettaglioProdotto().getPrezzoUnitarioPerAcquisto());
		prodotto.setPrezzoUnitarioPer(datiProdotto.getArticolo().getDettaglioArticolo().getPrezzoUnitarioPer());
		prodotto.setQuantitaUMAcquisto(datiProdotto.getDettaglioProdotto().getQuantitaUMAcquisto());
		prodotto.setQuantitaUMPrezzo(datiProdotto.getDettaglioProdotto().getQuantitaUMPrezzo());
		prodotto.setStato(datiProdotto.getDettaglioProdotto().getStato());
		prodotto.setTempoConsegna(datiProdotto.getDettaglioProdotto().getTempoConsegna());
		//dati articolo
		prodotto.setIdArticolo(datiProdotto.getArticolo().getIdArticolo());
		prodotto.setCodiceArticolo(datiProdotto.getArticolo().getDettaglioArticolo().getCodice());
		prodotto.setTipoArticolo(datiProdotto.getArticolo().getDettaglioArticolo().getTipo());
		if (StringUtils.isNotBlank(datiProdotto.getArticolo().getDettaglioArticolo().getColore())) {
			prodotto.setColoreArticolo(datiProdotto.getArticolo().getDettaglioArticolo().getColore());
		}
		prodotto.setDescrizioneArticolo(datiProdotto.getArticolo().getDettaglioArticolo().getDescrizione());
		if (StringUtils.isNotBlank(datiProdotto.getArticolo().getDettaglioArticolo().getDescrizioneTecnica())) {
			prodotto.setDescrizioneTecnicaArticolo(datiProdotto.getArticolo().getDettaglioArticolo().getDescrizioneTecnica());
		}
		prodotto.setUnitaMisuraDetermPrezzo(datiProdotto.getArticolo().getDettaglioArticolo().getUnitaMisuraDetermPrezzo());
		prodotto.setUnitaMisuraAcquisto(datiProdotto.getArticolo().getDettaglioArticolo().getUnitaMisuraAcquisto());
		prodotto.setUnitaMisuraTempoConsegna(datiProdotto.getArticolo().getDettaglioArticolo().getUnitaMisuraTempoConsegna());
		if (allegaDocumenti) {
			WizardDocumentiProdottoHelper.addDocumenti(datiProdotto.getDocumenti(), prodotto);
		}
	}

	/**
	 * Si crea il contenitore da porre in sessione
	 *
	 * @param username La username loggato a sistema
	 * @param session La sessione dell'utente loggato
	 * @param carrelloProdotti il carrello prodotti attualmente in sessione
	 * @param cataloghiManager il manager del catalogo
	 * @param codiceCatalogo il codice del catalogo
	 * @param statoProdotto lo stato del prodotto nel carrello modifiche (se
	 * presente)
	 * @param inCarrello true se il prodotto proviene dal carrello
	 * @param aggiornamento true se si sta procedendo con un aggiornamento
	 * @param idProdotto id del prodotto a sistema
	 * @param idArticolo id dell'articolo relativo al prodotto in questione
	 * @param wizardSourcePage pagina da cui si accede al wizard
	 * @return helper da memorizzare in sessione
	 * @throws com.agiletec.aps.system.exception.ApsException
	 */
	public static WizardProdottoHelper getInstance(
			Map<String, Object> session, 
			ICataloghiManager cataloghiManager,
			CarrelloProdottiSessione carrelloProdotti, 
			String username, 
			String codiceCatalogo, 
			String statoProdotto,
			Boolean inCarrello, 
			Boolean aggiornamento, 
			Long idProdotto, 
			Long idArticolo, 
			String wizardSourcePage) throws ApsException 
	{
		WizardProdottoHelper prodottoHelper;
		if (null != session.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO)) {
			prodottoHelper = (WizardProdottoHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
			prodottoHelper.setFields(aggiornamento, statoProdotto, inCarrello, codiceCatalogo);
			prodottoHelper.setAlreadyLoaded(true);
		} else {
			prodottoHelper = new WizardProdottoHelper();
			prodottoHelper.setWizardSourcePage(wizardSourcePage);
			prodottoHelper.setFields(aggiornamento, statoProdotto, inCarrello, codiceCatalogo);
			if (idProdotto != null) {
				ProdottiCatalogoSessione prodottiInCarrello = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
				if (prodottoHelper.isInCarrello() && CataloghiConstants.PRODOTTO_MODIFICATO.equals(prodottoHelper.getStatoProdotto())) {
					prodottoHelper = prodottiInCarrello.getProdottiModificati().get(idProdotto.intValue());
				} else if (prodottoHelper.isInCarrello() && CataloghiConstants.BOZZA.equals(prodottoHelper.getStatoProdotto())) {
					prodottoHelper = prodottiInCarrello.getBozze().get(idProdotto.intValue());
				} else if (prodottoHelper.isInCarrello() && CataloghiConstants.PRODOTTO_INSERITO.equals(prodottoHelper.getStatoProdotto())) {
					prodottoHelper = prodottiInCarrello.getProdottiInseriti().get(idProdotto.intValue());
				} else if (prodottoHelper.isInCarrello() && CataloghiConstants.PRODOTTO_ELIMINATO.equals(prodottoHelper.getStatoProdotto())) {
					prodottoHelper = prodottiInCarrello.getProdottiEliminati().get(idProdotto.intValue());
				} else {
					ProdottoType prodotto = cataloghiManager.getProdotto(idProdotto);
					prodottoHelper.setDettaglioProdotto(prodotto);
					prodottoHelper.setArticolo(cataloghiManager, idArticolo != null ? idArticolo : prodottoHelper.getDettaglioProdotto().getIdArticolo());
					prodottoHelper.setModificato(prodottiInCarrello.prodottoGiaModificato(prodotto.getId()));
					prodottoHelper.setArchiviato(prodottiInCarrello.prodottoGiaEliminato(prodotto.getId()));
				}
			} else {
				prodottoHelper.setArticolo(cataloghiManager, idArticolo);
			}
			prodottoHelper.setFields(aggiornamento, statoProdotto, inCarrello, codiceCatalogo);
			DettaglioBandoIscrizioneType dettCatalogo = cataloghiManager.getDettaglioCatalogo(codiceCatalogo);
			List<CategoriaCatalogoType> listaCategorie = cataloghiManager.getCategorieArticoliOE(codiceCatalogo, username);
			prodottoHelper.setCodiceCatalogo(codiceCatalogo);
			prodottoHelper.setArticoliAssenti(listaCategorie.isEmpty());
			prodottoHelper.setCatalogo(dettCatalogo);
		}
		return prodottoHelper;
	}

	/**
	 * ... 
	 */
	private void setArticolo(ICataloghiManager cataloghiManager, Long idArticolo) throws ApsException {
		if (idArticolo != null) {
			ArticoloType articoloType = cataloghiManager.getArticolo(idArticolo);
			WizardArticoloHelper wArticoloHelper = new WizardArticoloHelper(articoloType.getId(), articoloType);
			this.setArticolo(wArticoloHelper);
		}
	}

	/**
	 * ... 
	 */
	private void setFields(Boolean aggiornamento, String stato, Boolean inCarrello, String catalogo) {
		if (aggiornamento != null) {
			this.setAggiornamento(aggiornamento);
		}
		if (StringUtils.isNotBlank(stato)) {
			this.setStatoProdotto(stato);
		}
		if (inCarrello != null) {
			this.setInCarrello(inCarrello);
		}
		if (catalogo != null) {
			this.setCodiceCatalogo(catalogo);
		}
		this.setAggiornatoDocumenti(false); //riporto il flag allo stato originale
	}

	/**
	 * ... 
	 */
	public WizardProdottoHelper clone(
			IDocumentiDigitaliManager docDigManager, 
			String tempDir,
			boolean clonaDocumenti, 
			CarrelloProdottiSessione carrelloProdotti, 
			String username) throws ApsException, IOException, CloneNotSupportedException 
	{
		WizardProdottoHelper clone = new WizardProdottoHelper();
		clone.setAggiornamento(this.aggiornamento);
		clone.setAggiornatoDocumenti(this.aggiornatoDocumenti);
		clone.setArticoliAssenti(this.articoliAssenti);
		clone.setArticolo(this.articolo);
		clone.setCatalogo(this.catalogo);
		clone.setCodiceCatalogo(this.codiceCatalogo);
		clone.setDataOperazione(this.dataOperazione);
		clone.setInCarrello(this.inCarrello);
		clone.setIndex(this.index);
		clone.setStatoProdotto(this.statoProdotto);
		clone.setWizardSourcePage(this.wizardSourcePage);
		WizardDocumentiProdottoHelper cloneDocumenti = new WizardDocumentiProdottoHelper();
		if (this.dettaglioProdotto != null) {
			clone.setDettaglioProdotto(clonaDettaglio(this.dettaglioProdotto));
		}
		if (clonaDocumenti && !isAlreadyLoaded()) {
			if (this.isInCarrello()) {
				clone.setDocumenti(this.documenti.clone());
			} else {
				if (this.dettaglioProdotto != null && this.dettaglioProdotto.getCertificazioniRichieste() != null) {
					clonaCertificazioniRichieste(cloneDocumenti, this.dettaglioProdotto.getCertificazioniRichieste(), docDigManager, tempDir, username);
				}
				if (this.dettaglioProdotto != null && this.dettaglioProdotto.getSchedeTecniche() != null) {
					clonaSchedeTecniche(cloneDocumenti, this.dettaglioProdotto.getSchedeTecniche(), docDigManager, tempDir,username);
				}
				if (this.dettaglioProdotto != null && this.dettaglioProdotto.getImmagine() != null) {
					clonaImmagine(cloneDocumenti, this.dettaglioProdotto.getImmagine(), docDigManager, tempDir,username);
				}
				clone.setDocumenti(cloneDocumenti);
			}
		} else {
			clone.setDocumenti(this.documenti);
			this.setDocumenti(null);
		}
		carrelloProdotti.setFilesReference(clone.getDocumenti());
		return clone;
	}

	/**
	 * ... 
	 */
	private static ProdottoType clonaDettaglio(ProdottoType prodotto) {
		ProdottoType clone = new ProdottoType();
		clone.setAliquotaIVA(prodotto.getAliquotaIVA());
		clone.setCertificazioniRichieste(prodotto.getCertificazioniRichieste());
		clone.setCertificazioniRichieste(prodotto.getCertificazioniRichieste());
		clone.setCodiceCatalogo(prodotto.getCodiceCatalogo());
		clone.setCodiceProdottoFornitore(prodotto.getCodiceProdottoFornitore());
		clone.setCodiceProdottoProduttore(prodotto.getCodiceProdottoProduttore());
		clone.setDataScadenzaOfferta(prodotto.getDataScadenzaOfferta());
		clone.setDescrizioneAggiuntiva(prodotto.getDescrizioneAggiuntiva());
		clone.setDimensioni(prodotto.getDimensioni());
		clone.setGaranzia(prodotto.getGaranzia());
		clone.setId(prodotto.getId());
		clone.setIdArticolo(prodotto.getIdArticolo());
		clone.setImmagine(prodotto.getImmagine());
		clone.setMarcaProdottoProduttore(prodotto.getMarcaProdottoProduttore());
		clone.setNomeCommerciale(prodotto.getNomeCommerciale());
		clone.setPrezzoUnitario(prodotto.getPrezzoUnitario());
		clone.setPrezzoUnitarioPerAcquisto(prodotto.getPrezzoUnitarioPerAcquisto());
		clone.setQuantitaUMAcquisto(prodotto.getQuantitaUMAcquisto());
		clone.setQuantitaUMPrezzo(prodotto.getQuantitaUMPrezzo());
		clone.setSchedeTecniche(prodotto.getSchedeTecniche());
		clone.setStato(prodotto.getStato());
		clone.setTempoConsegna(prodotto.getTempoConsegna());
		return clone;
	}

	/**
	 * ... 
	 */
	private static void clonaCertificazioniRichieste(
			WizardDocumentiProdottoHelper cloneDocumenti,
			DocumentoAllegatoType[] certificazioniRichieste,
			IDocumentiDigitaliManager docDigManager, 
			String tempDir, 
			String username) throws ApsException, FileNotFoundException, IOException 
	{
		List<File> listaCertificazioni = new ArrayList<File>();
		List<Integer> listaCertificazioniSize = new ArrayList<Integer>();
		List<String> listaCertificazioniContentType = new ArrayList<String>();
		List<String> listaCertificazioniFileName = new ArrayList<String>();
		
		for (DocumentoAllegatoType certificazioneRichiesta : certificazioniRichieste) {
			
			FileType fileDB = docDigManager.getDocumentoRiservato(
					CommonSystemConstants.ID_APPLICATIVO_GARE, 
					certificazioneRichiesta.getId(), 
					username);
			File f = new File(tempDir + File.separatorChar + FileUploadUtilities.generateFileName());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
				fos.write(fileDB.getFile());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ex) {
						//non riesco a chiudere lo stream, ma il file è già scritto
						ApsSystemUtils.logThrowable(ex, WizardProdottoHelper.class, "clonaCertificazioniRichieste");
					}
				}
			}
			listaCertificazioni.add(f);
			listaCertificazioniSize.add((int) Math.ceil(f.length() / 1024.0));
			listaCertificazioniContentType.add("application/octet-stream");
			listaCertificazioniFileName.add(fileDB.getNome());
		}
		cloneDocumenti.setCertificazioniRichieste(listaCertificazioni);
		cloneDocumenti.setCertificazioniRichiesteContentType(listaCertificazioniContentType);
		cloneDocumenti.setCertificazioniRichiesteFileName(listaCertificazioniFileName);
		cloneDocumenti.setCertificazioniRichiesteSize(listaCertificazioniSize);
	}

	/**
	 * ... 
	 */
	private static void clonaSchedeTecniche(
			WizardDocumentiProdottoHelper cloneDocumenti,
			DocumentoAllegatoType[] schedeTecniche,
			IDocumentiDigitaliManager docDigManager, 
			String tempDir, 
			String username) throws ApsException, FileNotFoundException, IOException 
	{
		List<File> listaSchedeTecniche = new ArrayList<File>();
		List<Integer> listalistaSchedeTecnicheSize = new ArrayList<Integer>();
		List<String> listalistaSchedeTecnicheContentType = new ArrayList<String>();
		List<String> listalistaSchedeTecnicheFileName = new ArrayList<String>();
		
		for (DocumentoAllegatoType schedaTecnica : schedeTecniche) {
			FileType fileDB = docDigManager.getDocumentoRiservato(
					CommonSystemConstants.ID_APPLICATIVO_GARE, 
					schedaTecnica.getId(), 
					username);
			File f = new File(tempDir + File.separatorChar + FileUploadUtilities.generateFileName());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
				fos.write(fileDB.getFile());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ex) {
						//non riesco a chiudere lo stream, ma il file è già scritto
						ApsSystemUtils.logThrowable(ex, WizardProdottoHelper.class, "clonaSchedeTecniche");
					}
				}
			}
			listaSchedeTecniche.add(f);
			listalistaSchedeTecnicheSize.add((int) Math.ceil(f.length() / 1024.0));
			listalistaSchedeTecnicheContentType.add("application/octet-stream");
			listalistaSchedeTecnicheFileName.add(fileDB.getNome());
		}
		cloneDocumenti.setSchedeTecniche(listaSchedeTecniche);
		cloneDocumenti.setSchedeTecnicheContentType(listalistaSchedeTecnicheContentType);
		cloneDocumenti.setSchedeTecnicheFileName(listalistaSchedeTecnicheFileName);
		cloneDocumenti.setSchedeTecnicheSize(listalistaSchedeTecnicheSize);
	}

	/**
	 * ... 
	 */
	private static void clonaImmagine(
			WizardDocumentiProdottoHelper cloneDocumenti,
			DocumentoAllegatoType immagine,
			IDocumentiDigitaliManager docDigManager, 
			String tempDir, 
			String username) throws ApsException, FileNotFoundException, IOException 
	{
		FileType fileDB = docDigManager.getDocumentoRiservato(
				CommonSystemConstants.ID_APPLICATIVO_GARE, 
				immagine.getId(),
				username);
		File f = new File(tempDir + File.separatorChar + FileUploadUtilities.generateFileName());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(fileDB.getFile());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
					//non riesco a chiudere lo stream, ma il file è già scritto
					ApsSystemUtils.logThrowable(ex, WizardProdottoHelper.class, "clonaImmagine");
				}
			}
		}
		cloneDocumenti.setImmagine(f);
		cloneDocumenti.setImmagineContentType("application/octet-stream");
		cloneDocumenti.setImmagineFileName(fileDB.getNome());
		cloneDocumenti.setImmagineSize((int) Math.ceil(f.length() / 1024.0));
	}
	
}
