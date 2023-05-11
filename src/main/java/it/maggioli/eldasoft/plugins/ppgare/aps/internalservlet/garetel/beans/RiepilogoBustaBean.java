package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoDocumentoAllegatoType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoDocumentoRichiestoType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RiepilogoBustaBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5059651512916672885L;

	private List<DocumentazioneRichiestaType> docRichiesti;
	private List<DocumentoAllegatoBean> documentiInseriti;
	private List<DocumentoMancanteBean> documentiMancanti;
	private String oggetto;
	private boolean modified;					// indica se il riepilogo e' stato modificato e la busta di riepilogo va aggiornata
	private boolean questionarioPresente;		// indica se e' attiva la gestione del questionario
	private boolean questionarioCompletato;		// indica se l'eventuale questionario e' completato
	private long questionarioId;				// Id del questionario
	
	public List<DocumentazioneRichiestaType> getDocRichiesti() {
		return docRichiesti;
	}
	
	public void setDocRichiesti(List<DocumentazioneRichiestaType> docRichiesti) {
		this.docRichiesti = docRichiesti;
	}
	
	public List<DocumentoAllegatoBean> getDocumentiInseriti() {
		return documentiInseriti;
	}
	
	public void setDocumentiInseriti(List<DocumentoAllegatoBean> documentiInseriti) {
		this.documentiInseriti = documentiInseriti;
	}
	
	public List<DocumentoMancanteBean> getDocumentiMancanti() {
		return documentiMancanti;
	}
	
	public void setDocumentiMancanti(List<DocumentoMancanteBean> documentiMancanti) {
		this.documentiMancanti = documentiMancanti;
	}
	
	public String getOggetto() {
		return oggetto;
	}
	
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public boolean isQuestionarioPresente() {
		return questionarioPresente;
	}

	public void setQuestionarioPresente(boolean questionarioPresente) {
		this.questionarioPresente = questionarioPresente;
	}

	public boolean isQuestionarioCompletato() {
		return questionarioCompletato;
	}

	public void setQuestionarioCompletato(boolean questionarioCompletato) {
		this.questionarioCompletato = questionarioCompletato;
	}

	public long getQuestionarioId() {
		return questionarioId;
	}

	public void setQuestionarioId(long questionarioId) {
		this.questionarioId = questionarioId;
	}

	/**
	 * costruttore generale  
	 */
	public RiepilogoBustaBean(
			List<DocumentazioneRichiestaType> listaDocumentiRichiesti, 
			String oggetto, 
			boolean setOggetto) 
	{
		this.documentiInseriti = new ArrayList<DocumentoAllegatoBean>();
		this.documentiMancanti = new ArrayList<DocumentoMancanteBean>();
		this.docRichiesti = new ArrayList<DocumentazioneRichiestaType>();
		this.oggetto = oggetto;
		this.modified = false;
		this.questionarioPresente = false;
		this.questionarioCompletato = false;
		this.questionarioId = 0;
		
		if(listaDocumentiRichiesti != null) {
			for(int i = 0; i < listaDocumentiRichiesti.size(); i++) {
				this.docRichiesti.add(listaDocumentiRichiesti.get(i));
				if(setOggetto) {
					DocumentoMancanteBean docMancante = new DocumentoMancanteBean();
					docMancante.setDescrizione(listaDocumentiRichiesti.get(i).getNome());
					docMancante.setId(listaDocumentiRichiesti.get(i).getId());
					docMancante.setObbligatorio(listaDocumentiRichiesti.get(i).isObbligatorio());
					this.documentiMancanti.add(docMancante);
				}
			}
		}
	}

	/**
	 * costruttore senza valorizzazione dei doc richiesti utilizzato in caso 
	 * di riepilogo offerta post invio per lotti distinti
	 */
	public RiepilogoBustaBean() {
		this(null, null, false);
	}

	/**
	 * costruttore 
	 */
	public RiepilogoBustaBean(List<DocumentazioneRichiestaType> listaDocumentiRichiesti) {
		this(listaDocumentiRichiesti, null, false);
	}

	/**
	 * costruttore 
	 */
	public RiepilogoBustaBean(List<DocumentazioneRichiestaType> listaDocumentiRichiesti, String oggetto) {
		this(listaDocumentiRichiesti, oggetto, true);
	}
	
	private void resetDocumenti() {
		this.documentiInseriti = new ArrayList<DocumentoAllegatoBean>();
		this.documentiMancanti = new ArrayList<DocumentoMancanteBean>();
	}

	public void popolaBusta(RiepilogoBustaType bustaDaComunicazione) {
		if(bustaDaComunicazione != null) {
			//lista dei documenti che risultano inseriti presi da comunicazione di riepilogo
			RiepilogoDocumentoAllegatoType[] docInseriti = bustaDaComunicazione.getDocumentoAllegatoArray();
			//lista dei documenti richiesti mancanti presi da comunicazione di riepilogo
			RiepilogoDocumentoRichiestoType[] docMancanti = bustaDaComunicazione.getDocumentoRichiestoMancanteArray();
			this.resetDocumenti();
			
			boolean questionarioJson = false; 

			// Documenti inseriti 
			for(int i = 0; i < docInseriti.length; i++) {
				DocumentoAllegatoBean doc = new DocumentoAllegatoBean();
				doc.setDescrizione(docInseriti[i].getDescrizione());
				doc.setDimensione(docInseriti[i].getDimensione());
				doc.setId(docInseriti[i].getId());
				doc.setNomeFile(docInseriti[i].getNomeFile());
				doc.setSha1(docInseriti[i].getSha1());
				if(docInseriti[i].isSetUuid() && StringUtils.isNotEmpty(docInseriti[i].getUuid())) {
					// NB: dalla 3.2.0
					doc.setUuid(docInseriti[i].getUuid());
				}
				//doc.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
				this.getDocumentiInseriti().add(doc);
				
				// verifica se e' presente la gestione dei questionari 
				if(DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME.equalsIgnoreCase(docInseriti[i].getNomeFile())) {
					questionarioJson = true;
				}
			}
			
			// Documenti mancanti
			for(int i = 0; i < docMancanti.length; i++) {
				DocumentoMancanteBean doc = new DocumentoMancanteBean();
				doc.setDescrizione(docMancanti[i].getDescrizione());
				doc.setId(docMancanti[i].getId());
				doc.setObbligatorio(docMancanti[i].getObbligatorio());
				//doc.setUuid(...);			???
				//doc.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
				this.getDocumentiMancanti().add(doc);
			}

			// (Questionari - QFORM)
			// inizializza i questionari solo se sono presenti nel documento XML 
			if(bustaDaComunicazione.isSetQuestionarioCompletato()) {
				this.questionarioPresente = (questionarioJson || this.questionarioPresente);
				this.questionarioCompletato = false;
				this.questionarioId = 0;
				// se e' attivo un questionario, verifica lo stato...
				if(this.questionarioPresente) {
					this.questionarioCompletato = bustaDaComunicazione.getQuestionarioCompletato();
					this.questionarioId = bustaDaComunicazione.getQuestionarioId();
				}
			}
		}
	}
	
	/**
	 * Metodo per il riallineamento dei documenti per la busta amministrativa
	 * 
	 * @param documentiBustaHelper contenitore dei documenti caricati tramite wizard per busta amministrativa
	 * @throws IOException 
	 * 
	 */
	public void riallineaDocumenti(WizardDocumentiBustaHelper documentiBustaHelper) throws IOException {
		// NB: 
		// la sincronizzazione dei documenti richiesti ed ulteriori viene effettuata 
		// su un helper documenti, quindi in base ai dati restituiti dal servizio  
		// per i quali la dimensione viene calcolata in base alla dimensione 
		// del contentuto in W_DOCDIG.DIGOGG !!!
		// Ciò significa che il riepilogo si aggiorna in base alla dimensione 
		// del contenuto del db.
		// In caso di più allegati corrotti (dimensione nulla o non corrispondente 
		// al file originariamente caricato), quando si corregge un allegato 
		// eliminandolo e ricaricandolo, il riepilogo viene sincronizzato e 
		// tutte le dimensioni degli allegati (anche quelli ancora corrotti) 
		// vengono aggiornate, nascondendo le anomalie!!!
		// Per ovviare al problema si aggiornano le dimensioni degli allegati
		// del riepilogo solo per gli allegati "modificati", mentre per gli altri 
		// allegati si recupera la dimensione dal riepilogo precedente. 
		final List<DocumentoAllegatoBean> oldRiepilogo = this.documentiInseriti;
		
		this.resetDocumenti();
		
		// verifica quali documenti richiesti sono stati inseriti e quali risultano mancanti
		Map<Long, Attachment> allegati = documentiBustaHelper.getRequiredDocs().stream()
				.collect(Collectors.toMap( Attachment::getId, Function.identity() ));
		
		this.getDocRichiesti()
			.stream()
				.collect(Collectors.groupingBy( att -> allegati.containsKey(att.getId()) ))
			.forEach((isNotNew, attachments) -> realignDocuments(isNotNew, allegati, attachments, documentiBustaHelper));

		// documenti ulteriori
		documentiBustaHelper.getAdditionalDocs()
				.stream()
					.map(attachment -> toDocumentoAllegatoBean(attachment, documentiBustaHelper, oldRiepilogo))
				.forEach(documentiInseriti::add);

		// elimina il vecchio riepilogo
		oldRiepilogo.clear();
//		oldRiepilogo = null;
		
		// (Questionari - QFORM)
		// se è presente la gestione, allinea le info relative al questionario...
		if(documentiBustaHelper.getQuestionarioAssociato() == null) {
			this.questionarioPresente = false;
			this.questionarioCompletato = false;
			this.questionarioId = 0;
		}
	}

	private void realignDocuments(Boolean isNotNew, Map<Long, Attachment> attachments, List<DocumentazioneRichiestaType> docRichiesti, WizardDocumentiBustaHelper documentiBustaHelper) {
		if (CollectionUtils.isNotEmpty(docRichiesti)) {
			if (isNotNew)
				docRichiesti
						.stream()
							.map(it -> toDocumentoAllegatoBeanWithOld(attachments.get(it.getId()), it, documentiBustaHelper))
						.forEach(documentiInseriti::add);
			else
				docRichiesti
						.stream()
							.map(it -> oldToMissingDoc(it))
						.forEach(documentiMancanti::add);
		}
	}

	// documento richiesto non trovato/inserito...
	// inserisci il documento tra quelli mancanti
	private DocumentoMancanteBean oldToMissingDoc(DocumentazioneRichiestaType docRich) {
		DocumentoMancanteBean docMancante = new DocumentoMancanteBean();

		docMancante.setId(docRich.getId());
		docMancante.setDescrizione(docRich.getNome());
		docMancante.setObbligatorio(docRich.isObbligatorio());
		docMancante.setUuid(null);
		docMancante.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);

		return docMancante;
	}

	private DocumentoAllegatoBean toDocumentoAllegatoBeanWithOld(Attachment attachment, DocumentazioneRichiestaType docRich, WizardDocumentiBustaHelper documentiBustaHelper) {
		DocumentoAllegatoBean docInserito = new DocumentoAllegatoBean();

		Long dimensione = attachment.getSize().longValue();
		String sha = attachment.getSha1();
		if(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO != attachment.getStato().longValue()) {
			// se il documento NON è stato modificato
			// recupera le info dal precedente riepilogo e
			// se non è presente tengo comunque le info dell'helper documenti
			//documentiInseriti corrisponde ad oldRiepilogo
			DocumentoAllegatoBean doc = getAllegatoRiepilogo(documentiInseriti, documentiBustaHelper, attachment, false);
			if(doc != null) {
				dimensione = (doc.getDimensione() != null ? doc.getDimensione() : dimensione);
				sha = (doc.getSha1() != null ? doc.getSha1() : sha);
			}
		}
		if(dimensione == null) {
			// NON dovrebbe mai succedere!!!
			ApsSystemUtils.getLogger().warn("Dimensione allegato richiesto non disponibile (" +
													"id=" + attachment.getId() + " " +
													"filename=" + attachment.getFileName() + " " +
													"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
													")");
		}

		// allinea il documento del riepilogo
		docInserito.setId(docRich.getId());
		docInserito.setNomeFile(attachment.getFileName());
		docInserito.setDescrizione(docRich.getNome());
		docInserito.setDimensione(dimensione);
		docInserito.setSha1(sha);
		docInserito.setUuid(attachment.getUuid());
		docInserito.setStato(attachment.getStato());

		return docInserito;
	}

	private DocumentoAllegatoBean toDocumentoAllegatoBean(Attachment attachment, WizardDocumentiBustaHelper documentiBustaHelper, List<DocumentoAllegatoBean> oldRiepilogo) {
		DocumentoAllegatoBean toReturn = new DocumentoAllegatoBean();

		Long dimensione = attachment.getSize().longValue();
		String sha = attachment.getSha1();
		if(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO != attachment.getStato().longValue()) {
			// se il documento NON è stato modificato
			// recupera le info dal precedente riepilogo e
			// se non è presente tengo comunque le info dell'helper documenti
			DocumentoAllegatoBean doc = getAllegatoRiepilogo(oldRiepilogo, documentiBustaHelper, attachment, true);
			if(doc != null) {
				dimensione = (doc.getDimensione() != null ? doc.getDimensione() : dimensione);
				sha = (doc.getSha1() != null ? doc.getSha1() : sha);
			}
		}
		if(dimensione == null) {
			// NON dovrebbe mai succedere!!!
			ApsSystemUtils.getLogger()
					.warn("Dimensione allegato ulteriore non disponibile (" +
							"desc=" + attachment.getDesc() + " " +
							"filename=" + attachment.getFileName() + " " +
							"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
							")"
					);
		}

		// allinea il documento del riepilogo
		toReturn.setNomeFile(attachment.getFileName());
		toReturn.setDescrizione(attachment.getDesc());
		toReturn.setDimensione(dimensione);
		toReturn.setSha1(sha);
		toReturn.setUuid(attachment.getUuid());
		toReturn.setStato(attachment.getStato());

		return toReturn;
	}

	/**
     * recupera un documento da un riepilogo in base ad "id" nel caso di documento richiesto, a "uuid" nel caso di documento ulteriore. UUID
     * e' sempre univoco, a differenza della descrizione che nel caso di qform potremmo avere gruppi diversi che permettono di caricare file
     * con stesse descrizioni.
     */
	private DocumentoAllegatoBean getAllegatoRiepilogo(
			List<DocumentoAllegatoBean> riepilogo, 
			WizardDocumentiBustaHelper documentiBustaHelper, 
			Attachment attachment,
			boolean ulteriore) 
	{
		DocumentoAllegatoBean allegato = null;
		// 11/10/2021: si sostituisce UUID a descrizione per i documenti ulteriori
		String skey = (ulteriore ? attachment.getUuid() : null);
		Long lkey = (!ulteriore ? attachment.getId() : null);
		
		for(int i = 0; i < riepilogo.size(); i++) {
			if(ulteriore) {
				// l'id di un documento ulteriore non dovrebbe essere > 0
				long id = (riepilogo.get(i).getId() != null ? riepilogo.get(i).getId() : 0); 
				if(skey.equals(riepilogo.get(i).getUuid()) && id <= 0) {
					allegato = riepilogo.get(i);
					break;
				}
			} else {
				if(lkey == riepilogo.get(i).getId()) {
					allegato = riepilogo.get(i);
					break;
				}
			}
		}
		if(allegato == null) {
			// se manca il documento nel riepilogo... 
			// ...reintegra recuperando i dati da documentiBustaHelper
			allegato = new DocumentoAllegatoBean();
			if(ulteriore) {
				//allegato.setId( NON ESISTE );
				allegato.setDescrizione(attachment.getDesc());
				allegato.setDimensione(attachment.getSize().longValue());
				allegato.setNomeFile(attachment.getFileName());
				allegato.setSha1(attachment.getSha1());
				allegato.setStato(attachment.getStato());
				allegato.setUuid(attachment.getUuid());
				ApsSystemUtils.getLogger().info("Riepilogo reintegrato con il documento (" +
						"desc=" + attachment.getDesc() + " " +
						"filename=" + attachment.getFileName() + " " +
						"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
						")"); 
			} else {
				allegato.setId(attachment.getId());
				//allegato.setDescrizione( NON ESISTE);
				allegato.setDimensione(attachment.getSize().longValue());
				allegato.setNomeFile(attachment.getFileName());
				allegato.setSha1(attachment.getSha1());
				allegato.setStato(attachment.getStato());
				allegato.setUuid(attachment.getUuid());
				ApsSystemUtils.getLogger().info("Riepilogo reintegrato con il documento (" +
						"id=" + attachment.getId() + " " +
						"filename=" + attachment.getFileName() + " " +
						"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
						")");
			}
		}
		return allegato;
	}


}

