package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans;

import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoDocumentoAllegatoType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoDocumentoRichiestoType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;

public class RiepilogoBustaBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5059651512916672885L;

	private List<DocumentazioneRichiestaType> docRichiesti;
	private List<DocumentoAllegatoBean> documentiInseriti;
	private List<DocumentoMancanteBean> documentiMancanti;
	private String oggetto;
	private boolean modified;		// indica se il riepilogo è stato modificato e la busta di riepilogo va aggiornata

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
	
	public void resetDocumenti() {
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
		}
	}

	/**
	 * Metodo per il riallineamento dei documenti per la busta amministrativa
	 * 
	 * @param WizardDocumentiBustaHelper contenitore dei documenti caricati tramite wizard per busta amministrativa
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
		List<DocumentoAllegatoBean> oldRiepilogo = this.documentiInseriti;
		
		this.resetDocumenti();

		for(int i = 0; i < this.getDocRichiesti().size(); i++) {
			boolean trovato = false;
			for(int j = 0; j < documentiBustaHelper.getDocRichiestiId().size() && !trovato; j++) {
				if(documentiBustaHelper.getDocRichiestiId().get(j) == this.getDocRichiesti().get(i).getId()) {
					
					Long dimensione = documentiBustaHelper.getDocRichiestiSize().get(j).longValue();
					String sha = documentiBustaHelper.getDocRichiestiSha1().get(j);
					if(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO != documentiBustaHelper.getDocRichiestiStato().get(j).longValue()) {
						// se il documento NON è stato modificato 
						// recupera le info dal precedente riepilogo e  
						// se non è presente tengo comunque le info dell'helper documenti 
						DocumentoAllegatoBean doc = getAllegatoRiepilogo(oldRiepilogo, documentiBustaHelper, j, false);
						if(doc != null) {
							dimensione = (doc.getDimensione() != null ? doc.getDimensione() : dimensione);
							sha = (doc.getSha1() != null ? doc.getSha1() : sha);
						}
					}
					if(dimensione == null) {
						// NON dovrebbe mai succedere!!!
						ApsSystemUtils.getLogger().warn("Dimensione allegato richiesto non disponibile (" +
								"id=" + documentiBustaHelper.getDocRichiestiId().get(j) + " " +
								"filename=" + documentiBustaHelper.getDocRichiestiFileName().get(j) + " " +
								"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
								")"); 
					}
					
					// allinea il documento del riepilogo
					DocumentoAllegatoBean docInserito = new DocumentoAllegatoBean();
					docInserito.setId(this.getDocRichiesti().get(i).getId());
					docInserito.setNomeFile(documentiBustaHelper.getDocRichiestiFileName().get(j));
					docInserito.setDescrizione(this.getDocRichiesti().get(i).getNome());
					docInserito.setDimensione(dimensione);
					docInserito.setSha1(sha);
					docInserito.setUuid(documentiBustaHelper.getDocRichiestiUuid().get(j));
					docInserito.setStato(documentiBustaHelper.getDocRichiestiStato().get(j));							
					this.getDocumentiInseriti().add(docInserito);									
					trovato = true;
				}
			}
			
			// documento richiesto non trovato/inserito...
			// inserisci il documento tra quelli mancanti
			if( !trovato ) {	
				DocumentoMancanteBean docMancante = new DocumentoMancanteBean();
				docMancante.setId(this.getDocRichiesti().get(i).getId());
				docMancante.setDescrizione(this.getDocRichiesti().get(i).getNome());
				docMancante.setObbligatorio(this.getDocRichiesti().get(i).isObbligatorio());
				docMancante.setUuid(null);
				docMancante.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);				
				this.getDocumentiMancanti().add(docMancante);
			}		
		}

		// documenti ulteriori
		for(int i = 0; i < documentiBustaHelper.getDocUlteriori().size(); i++) {
			
			Long dimensione = documentiBustaHelper.getDocUlterioriSize().get(i).longValue();
			String sha = documentiBustaHelper.getDocUlterioriSha1().get(i);
			if(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO != documentiBustaHelper.getDocUlterioriStato().get(i).longValue()) {
				// se il documento NON è stato modificato 
				// recupera le info dal precedente riepilogo e  
				// se non è presente tengo comunque le info dell'helper documenti
				DocumentoAllegatoBean doc = getAllegatoRiepilogo(oldRiepilogo, documentiBustaHelper, i, true);
				if(doc != null) {
					dimensione = (doc.getDimensione() != null ? doc.getDimensione() : dimensione);
					sha = (doc.getSha1() != null ? doc.getSha1() : sha);
				}
			}
			if(dimensione == null) {
				// NON dovrebbe mai succedere!!!
				ApsSystemUtils.getLogger().warn("Dimensione allegato ulteriore non disponibile (" +
						"desc=" + documentiBustaHelper.getDocUlterioriDesc().get(i) + " " +
						"filename=" + documentiBustaHelper.getDocUlterioriFileName().get(i) + " " +
						"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
						")"); 
			}

			// allinea il documento del riepilogo
			DocumentoAllegatoBean docInserito = new DocumentoAllegatoBean();
			docInserito.setNomeFile(documentiBustaHelper.getDocUlterioriFileName().get(i));
			docInserito.setDescrizione(documentiBustaHelper.getDocUlterioriDesc().get(i));
			docInserito.setDimensione(dimensione);
			docInserito.setSha1(sha);
			docInserito.setUuid(documentiBustaHelper.getDocUlterioriUuid().get(i));
			docInserito.setStato(documentiBustaHelper.getDocUlterioriStato().get(i));				
			this.getDocumentiInseriti().add(docInserito);
		}
		
		// elimina il vecchio riepilogo
		oldRiepilogo.clear();
		oldRiepilogo = null;
	}

	/**
	 * recupera un documento da un riepilogo in base ad "id" o "descrizione"
	 */
	private DocumentoAllegatoBean getAllegatoRiepilogo(
			List<DocumentoAllegatoBean> riepilogo, 
			WizardDocumentiBustaHelper documentiBustaHelper, 
			int idx, 
			boolean ulteriore) 
	{
		DocumentoAllegatoBean allegato = null;
		String skey = (ulteriore ? documentiBustaHelper.getDocUlterioriDesc().get(idx) : null);
		Long lkey = (!ulteriore ? documentiBustaHelper.getDocRichiestiId().get(idx) : null);
		
		for(int i = 0; i < riepilogo.size(); i++) {
			if(ulteriore) {
				// l'id di un documento ulteriore non dovrebbe essere > 0
				long id = (riepilogo.get(i).getId() != null ? riepilogo.get(i).getId() : 0); 
				if(skey.equals(riepilogo.get(i).getDescrizione()) && id <= 0) {
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
				allegato.setDescrizione( documentiBustaHelper.getDocUlterioriDesc().get(idx) );
				allegato.setDimensione( documentiBustaHelper.getDocUlterioriSize().get(idx).longValue() );
				allegato.setNomeFile( documentiBustaHelper.getDocUlterioriFileName().get(idx) );
				allegato.setSha1( documentiBustaHelper.getDocUlterioriSha1().get(idx) );
				allegato.setStato( documentiBustaHelper.getDocUlterioriStato().get(idx) );
				allegato.setUuid( documentiBustaHelper.getDocUlterioriUuid().get(idx) );
				ApsSystemUtils.getLogger().info("Riepilogo reintegrato con il documento (" +
						"desc=" + documentiBustaHelper.getDocUlterioriDesc().get(idx) + " " +
						"filename=" + documentiBustaHelper.getDocUlterioriFileName().get(idx) + " " +
						"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
						")"); 
			} else {
				allegato.setId( documentiBustaHelper.getDocRichiestiId().get(idx) );
				//allegato.setDescrizione( NON ESISTE);
				allegato.setDimensione( documentiBustaHelper.getDocRichiestiSize().get(idx).longValue() );
				allegato.setNomeFile( documentiBustaHelper.getDocRichiestiFileName().get(idx) );
				allegato.setSha1( documentiBustaHelper.getDocRichiestiSha1().get(idx) );
				allegato.setStato( documentiBustaHelper.getDocRichiestiStato().get(idx) );
				allegato.setUuid( documentiBustaHelper.getDocRichiestiUuid().get(idx) );
				ApsSystemUtils.getLogger().info("Riepilogo reintegrato con il documento (" +
						"id=" + documentiBustaHelper.getDocRichiestiId().get(idx) + " " +
						"filename=" + documentiBustaHelper.getDocRichiestiFileName().get(idx) + " " +
						"idComunicazione=" + documentiBustaHelper.getIdComunicazione() +
						")");
			}
		}
		return allegato;
	}

}

