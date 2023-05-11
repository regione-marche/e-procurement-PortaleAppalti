package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoLotto;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoAllegatoBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.exception.ApsException;

/**
 *  Verifica se esistono anomalie nei documenti di una busta (amministrativa, tecnica, economica) 
 *  in fase di pre invio di un'offerta di gara
 *
 */
public class VerificaDocumentiCorrotti {

	private IBandiManager bandiManager;
	
	private BustaRiepilogo riepilogo;
	
	private List<String> busteKeys = new ArrayList<String>();
	
	private LinkedHashMap<String, List<DocumentoAllegatoLotto>> documentiNulli = 
				new LinkedHashMap<String, List<DocumentoAllegatoLotto>>();
	
	private LinkedHashMap<String, List<DocumentoAllegatoLotto>> documentiCorrotti =
				new LinkedHashMap<String, List<DocumentoAllegatoLotto>>();
	
	private LinkedHashMap<String, List<DocumentoAllegatoLotto>> documentiMancanti =
				new LinkedHashMap<String, List<DocumentoAllegatoLotto>>();
	
	private String eventDetailMessage;
	
	List<DocumentoAllegatoLotto> dimensioniDocumenti;
	
	/**
	 * costruttore 
	 */
	public VerificaDocumentiCorrotti(BustaRiepilogo riepilogo) {
		this.riepilogo = riepilogo;
		this.bandiManager = this.riepilogo.getGestioneBuste().getBandiManager();
	}

	/**
	 * aggiunge un documento anomalo alla lista dei documenti   
	 */
	private void add(
			String tipoBusta, 
			DocumentoAllegatoLotto documento, 
			LinkedHashMap<String, List<DocumentoAllegatoLotto>> documenti) 
	{
		if(documenti != null) {
			// key = [codice lotto];[codice lotto interno]|[tipo busta]
			String key = (StringUtils.isNotEmpty(documento.getLotto().getCodiceLotto())
						  ? documento.getLotto().getCodiceLotto() : "") 
						 + ";" +
						 (StringUtils.isNotEmpty(documento.getLotto().getCodiceInterno())
					  	  ? documento.getLotto().getCodiceInterno() : "")
					  	 + "|" + tipoBusta;
						
			List<DocumentoAllegatoLotto> listaDoc = null;
			if(listaDoc == null) {
				listaDoc = new ArrayList<DocumentoAllegatoLotto>();
				documenti.put(key, listaDoc);
			}				
			listaDoc.add(documento);
			
			if( !this.busteKeys.contains(key) ) {
				this.busteKeys.add(key);
			}
		}
	}
	
	/**
	 * aggiunge un documento nullo (allegato con W_DOCDIG.digogg = NULL)  
	 */
	public void addNullo(String codGara, String tipoBusta, DocumentoAllegatoLotto documento) {
		this.add(tipoBusta, documento, this.documentiNulli);
	}
	
	/**
	 * aggiunge un documento corrotto  
	 */
	public void addCorrotto(String codGara, String tipoBusta, DocumentoAllegatoLotto documento) {
		this.add(tipoBusta, documento, this.documentiCorrotti);
	}
	
	/**
	 * aggiunge un documento mancante (allegato non presente in W_DOCDIG)
	 */
	public void addMancante(String codGara, String codLotto, String tipoBusta, DocumentoAllegatoBean allegato) {			
		DocumentoAllegatoLotto documento = new DocumentoAllegatoLotto();
		documento.setDescrizione( allegato.getDescrizione() );
		documento.setNomefile( allegato.getNomeFile() );
		documento.setId( allegato.getId() );
		documento.setDimensione( allegato.getDimensione() );
		
		LottoGaraType lotto = new LottoGaraType();
		lotto.setCodiceGara(codGara);
		lotto.setCodiceLotto(codGara);
		lotto.setCodiceInterno("");
		if(StringUtils.isNotEmpty(codLotto) && riepilogo.getHelper().getListaCodiciInterniLotti() != null) {
			lotto.setCodiceLotto(codLotto);
			lotto.setCodiceInterno( riepilogo.getHelper().getListaCodiciInterniLotti().get(codLotto) );
		} 
		documento.setLotto(lotto);
		
		this.add(tipoBusta, documento, this.documentiMancanti);
	}
	
	/**
	 * dimensione della lista dei documenti anomali  
	 */
	public int size() {
		return this.documentiNulli.size() + this.documentiCorrotti.size() + this.documentiMancanti.size(); 
	}
		
	public String getEventDetailMessage() { 
		return this.eventDetailMessage;
	}
	
	/**
	 * aggiunge agli errore della action l'elenco delle anomalie rilevate nei documenti delle buste   
	 */
	public void addActionErrors(EncodedDataAction action) {
		this.eventDetailMessage = "";
		
		for(String key : this.busteKeys) {
			// key -> G00001;01|FS11C o G00001;|FS11C o 
			String[] s = key.split("\\|");
			String tipoBusta = (s.length >= 2 ? s[1] : "");
			s = s[0].split(";");
			String lotto = (s != null && s.length >= 1 && StringUtils.isNotEmpty(s[0]) ? s[0] + " " : "");
			String lotto2 = (s != null && s.length >= 2 && StringUtils.isNotEmpty(s[1]) ? s[1] : "");
			
			String descrBusta = "";
			if(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA.equals(tipoBusta)) {
				descrBusta = PortGareSystemConstants.BUSTA_PRE;
			} else if(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA.equals(tipoBusta)) {
				descrBusta = PortGareSystemConstants.BUSTA_AMM;
			} else if(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA.equals(tipoBusta)) {
				descrBusta = PortGareSystemConstants.BUSTA_TEC;
			} else if(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA.equals(tipoBusta)) {
				descrBusta = PortGareSystemConstants.BUSTA_ECO;
			}
				
			// documenti nulli
			List<DocumentoAllegatoLotto> docs = this.documentiNulli.get(key);
			String lst1 = "";
			if(docs != null) {
				for(int j = 0; j < docs.size(); j++) {
					lst1 += docs.get(j).getNomefile() + 
							"(" + (StringUtils.isNotEmpty(docs.get(j).getDescrizione()) ? docs.get(j).getDescrizione() : "") + ")" + 
							(j < docs.size() - 1 ? "," : "");
				}
			}
			
			// documenti corrotti
			docs = this.documentiCorrotti.get(key);
			String lst2 = "";
			if(docs != null) {
				for(int j = 0; j < docs.size(); j++) {
					lst2 += docs.get(j).getNomefile() + 
							"(" + (StringUtils.isNotEmpty(docs.get(j).getDescrizione()) ? docs.get(j).getDescrizione() : "") + ")" + 
							(j < docs.size() - 1 ? "," : "");
				}
			}
			
			// documenti mancanti (aggiungi comunque nella lista di quelli corrotti)
			docs = this.documentiMancanti.get(key);
			String lst3 = "";
			if(docs != null) {
				for(int j = 0; j < docs.size(); j++) {
					lst3 += docs.get(j).getNomefile() + 
							"(" + (StringUtils.isNotEmpty(docs.get(j).getDescrizione()) ? docs.get(j).getDescrizione() : "") + ")" + 
							(j < docs.size() - 1 ? "," : "");
				}
			}
			
			if(lst1.length() > 0 && (lst2.length() + lst3.length()) > 0 ) {
				lst1 += ", ";
			}
			if(lst2.length() > 0 && lst3.length() > 0 ) {
				lst2 += ", ";
			}
			
			// G0023L001 FS11B: documenti nulli CI.pdf, DGUE.pdf, documenti corrotti OffertaTecnica.pdf.p7m, Export.xls;
			// event trace message
			this.eventDetailMessage += lotto + tipoBusta + ": " + 
				(lst1.length() > 0 ? "documenti nulli " : "") + lst1 + " " + 
				(lst2.length() > 0 ? "documenti corrotti " : "") + lst2 + " " + 
				(lst3.length() > 0 ? "documenti mancanti " : "") + lst3 +
				";" + "\n";
			
			// action error message
			action.addActionError( action.getText("Errors.invioBuste.nullDocuments", 
					new String[] { descrBusta + (StringUtils.isNotEmpty(lotto2) ? " lotto " + lotto2 : "") }) + " " +
					lst1 + lst2 + lst3);
		}
	}

	/**
	 * verifica l'integrita' dei documenti della busta
	 * @throws ApsException 
	 */
	public boolean isDocumentiCorrottiPresenti() 
		throws ApsException 
	{
		boolean controlliOk = true;
		
		boolean garaLotti = (this.riepilogo.getHelper().getListaCompletaLotti() != null &&
							 this.riepilogo.getHelper().getListaCompletaLotti().size() > 0);
		boolean noBustaAmm = this.riepilogo.getGestioneBuste().getDettaglioGara().getDatiGeneraliGara().isNoBustaAmministrativa();
		
		if( !garaLotti ) {
			// gara NO lotti
			if(this.riepilogo.getGestioneBuste().isDomandaPartecipazione()) {
				controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
						this.riepilogo.getCodiceGara(), 
						this.riepilogo.getUsername(),
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA,
						this.riepilogo.getHelper().getBustaPrequalifica());
			} else {
				if( !noBustaAmm ) {
					controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
							this.riepilogo.getCodiceGara(), 
							this.riepilogo.getUsername(),
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
							this.riepilogo.getHelper().getBustaAmministrativa());
				}
				
				controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
						this.riepilogo.getCodiceGara(), 
						this.riepilogo.getUsername(),
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
						this.riepilogo.getHelper().getBustaTecnica());
				
				controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
						this.riepilogo.getCodiceGara(), 
						this.riepilogo.getUsername(),
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
						this.riepilogo.getHelper().getBustaEconomica());
			}
		} else {
			// gara a lotti
			//if(domandaPartecipazione) {
			//} else {
			if( !noBustaAmm ) {
				controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
						this.riepilogo.getCodiceGara(), 
						this.riepilogo.getUsername(),
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
						riepilogo.getHelper().getBustaAmministrativa());
			}
			
			controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
					this.riepilogo.getCodiceGara(), 
					this.riepilogo.getUsername(),
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA,
					riepilogo.getHelper().getBusteTecnicheLotti());
			
			controlliOk = controlliOk & !this.isDocumentiCorrottiPresenti(
					this.riepilogo.getCodiceGara(), 
					this.riepilogo.getUsername(),
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA,
					riepilogo.getHelper().getBusteEconomicheLotti());
		}
		
		// NB: se i "controlli non sono ok", allora esistono documenti corrotti presenti !!!!
		return (!controlliOk); 
	}

	/**
	 * verifica l'integrita' dei documenti della busta
	 * @throws ApsException 
	 */
	private boolean isDocumentiCorrottiPresenti(
			String codiceGara, 
			String username, 
			String tipoBusta,
			Object riepilogo) throws ApsException 
	{		  		
		boolean documentiCorrotti = false;

		// 1) verifica se esistono documenti nulli...
		if(!documentiCorrotti) {
			documentiCorrotti = verificaDocumentiNulli(
					codiceGara, 
					username, 
					tipoBusta,
					riepilogo);	
		}

		// ...recupera l'elenco dei documenti con le loro dimensioni...
		// ...e verifica se utilizzare la verifica per UUID o per dimensione minimizzata...
		// (NB: utilizza la verifica per UUID solo se tutti i documenti hanno uuid impostato!!!)
		this.dimensioniDocumenti = this.bandiManager.checkDimensioneDocumenti(
				codiceGara,
				username,
				tipoBusta);
		
		boolean useUuid = false;
		if(this.dimensioniDocumenti != null) {
			useUuid = true;
			for(int i = 0; i < this.dimensioniDocumenti.size(); i++) {
				if(PortGareSystemConstants.NOME_FILE_BUSTA.equals(this.dimensioniDocumenti.get(i).getNomefile())) {
					// ignora l'allegato "busta.xml" 
				} else if( StringUtils.isEmpty(this.dimensioniDocumenti.get(i).getUuid()) ) {
					useUuid = false;
				}
			}
		}
		
		// 2) verifica i documenti corrotti/"mancanti"...
		if(!documentiCorrotti) {
			if(useUuid) {
				// (3.2.0) verifica le dimensioni per UUID...
				documentiCorrotti = verificaDocumentiPerUUID(
						codiceGara, 
						username, 
						tipoBusta,
						riepilogo);
			} else {
				// (fino a 3.1.x) verifica se esistono documenti corrotti con   
				// match per filename ed utilizza la verifica per dimenzione minimizzata
				documentiCorrotti = verificaDocumentiPerDimensioneMinimizzata(
						codiceGara, 
						username, 
						tipoBusta,
						riepilogo);	
			}
		}
		
		return documentiCorrotti;
	}

	/**
	 * verifica l'integrità dei documenti della busta
	 * @throws ApsException 
	 */
	private boolean verificaDocumentiNulli(
			String codiceGara, 
			String username, 
			String tipoBusta,
			Object riepilogo) throws ApsException 
	{		  		
		List<DocumentoAllegatoLotto> docNulli = this.bandiManager.checkDocumentiNulli(
				codiceGara,
				username,
				tipoBusta);
		
		if(docNulli != null && docNulli.size() > 0) {
			// esistono documenti senza contenuto...
			for(int i = 0; i < docNulli.size(); i++) {
				this.addNullo(codiceGara, tipoBusta, docNulli.get(i));	
			}
		}
		
		return (docNulli != null && docNulli.size() > 0);
	}

	/**
	 * verifica l'integrità della dimensione dei documenti della busta
	 * @throws ApsException 
	 */
	private boolean verificaDocumentiPerDimensioneMinimizzata(
			String codiceGara, 
			String username, 
			String tipoBusta,
			Object riepilogo) throws ApsException
	{	
		boolean documentiCorrotti = false; 

		// segnala i documenti e blocca l'invio...
		if(this.dimensioniDocumenti != null && this.dimensioniDocumenti.size() > 0) {
			//boolean isGaraLotti = (riepilogo instanceof LinkedHashMap);
			
			// crea lista di allegati del riepilogo che sono stati verificati 
			List<DocumentoAllegatoBean> allegatiVerificati = new ArrayList<DocumentoAllegatoBean>();
			
			// verifica per ogni documento la dimensione registrata 
			for(int i = 0; i < this.dimensioniDocumenti.size(); i++) {
				
				// trova il lotto nel quale cercare documento nel riepilogo...
				RiepilogoBustaBean lotto = null;
				if(riepilogo instanceof RiepilogoBustaBean) {
					// gara a lotto unico
					lotto = (RiepilogoBustaBean) riepilogo; 
				} else if(riepilogo instanceof LinkedHashMap) {
					// gara a lotti
					LinkedHashMap<String, RiepilogoBustaBean> lotti = (LinkedHashMap<String, RiepilogoBustaBean>) riepilogo;
					lotto = lotti.get( this.dimensioniDocumenti.get(i).getLotto().getCodiceLotto() );
				}
				
				// per comporre la chiave di ricerca verifica se il documento è 
				// doc richiesto 	("nomefile|descrizione")
				// doc ulteriore 	("nomefile|")
				String key1 = this.dimensioniDocumenti.get(i).getNomefile() + "|" +
	  						  (StringUtils.isNotEmpty(this.dimensioniDocumenti.get(i).getDescrizione()) ? this.dimensioniDocumenti.get(i).getDescrizione() : "");

				// cerca nel riepilogo l'allegato con lo stesso nome 
				// del documento presente in W_DOCDIG, e nel caso di
				// allegati con lo stesso nome cerca quello che minimizza 
				// la distanza in termini di dimensione
				DocumentoAllegatoBean allegatoSimile = null;
				double dMinima = (this.dimensioniDocumenti.get(i).getDimensione() != null 
						          ? this.dimensioniDocumenti.get(i).getDimensione() : 0) * 1024.0;
				
				if(lotto != null) {
					for(DocumentoAllegatoBean allegato : lotto.getDocumentiInseriti()) {
						// salta gli allegati già considerati...
						if( !allegatiVerificati.contains(allegato) ) {
							// verifica se l'allegato del riepilogo è un documento richiesto?
							boolean isDocRichiesto = false; 
							for(DocumentazioneRichiestaType richiesto : lotto.getDocRichiesti()) {
								if(allegato.getDescrizione().equalsIgnoreCase(richiesto.getNome())) {
									isDocRichiesto = true;
									break;
								}
							}
						
							String key2 = allegato.getNomeFile() + "|" + 
										  (isDocRichiesto ? "" : allegato.getDescrizione());
							if( key1.equals(key2) ) {
								// in caso di allegati con lo stesso nome, 
								// cerca l'allegato che minimizza la distanza in termini di dimensione
								double a = (double) (this.dimensioniDocumenti.get(i).getDimensione() != null 
										             ? this.dimensioniDocumenti.get(i).getDimensione() : 0);
								double b = (double) allegato.getDimensione() * 1024.0;
								double d = Math.abs(a - b);
								if(d < dMinima) {
									allegatoSimile = allegato;
									dMinima = d;
								}
							}
						}
					}
				}
				
				// verifica se il documento trovato nel riepilogo 
				// ha dimensione compatibile con quella in W_DOCDIG
				if(allegatoSimile != null) {
					if( !allegatiVerificati.contains(allegatoSimile) ) {
						// Verifica la dimensione dell'allegato con il riepilogo
						// la validazione viene calcolata come distanza da una data tolleranza
						// la distanza (errore) può essere relativo od assoluto:
						//   1) |a-b|/|b| errore relativo tra dimensione allegato e dimensione del riepilogo
						//   2) |a-b|     errore assoluto tra dimensione allegato e dimensione del riepilogo
						// l'errore relativo non sembra adatto a files di piccole dimensioni (1/2KB)
						double a = (double) (this.dimensioniDocumenti.get(i).getDimensione() != null 
											 ? this.dimensioniDocumenti.get(i).getDimensione() : 0);
						double b = (double) allegatoSimile.getDimensione() * 1024.0;
						double d = Math.abs(a - b);
						if( a > 0 && d > 1024 ) {	// |a-b| <= 1024  le dimensioni vengono considerate equivalenti
							this.addCorrotto(codiceGara, tipoBusta, this.dimensioniDocumenti.get(i));
							documentiCorrotti = true;
						}
						
						allegatiVerificati.add(allegatoSimile);
					}
				}
				if(allegatoSimile == null) {
					// NB: caso in cui il documento è presente in W_DOCDIG
					//     ma non esiste nel riepilogo.
					//     Sembra non si sia mai presentato!
					//     Eventualmente andrà gestito in queto punto
				}
			}
			
			// Recupera la lista di tutti i documenti presenti nel riepilogo
			// e verifica se esistono documenti del riepilogo non presenti 
			// tra i documenti in W_DOCDIG!!!
			if(!documentiCorrotti) {
				documentiCorrotti = checkAllegatiMancanti(
						codiceGara, 
						username, 
						tipoBusta,
						riepilogo,
						allegatiVerificati);
			}
		}
		
		return documentiCorrotti;
	}

	/**
	 * verifica se esistono nel riepilogo documenti non verificati 
	 */
	private boolean checkAllegatiMancanti(
			String codiceGara, 
			String username, 
			String tipoBusta,
			Object riepilogo,
			List<DocumentoAllegatoBean> allegatiVerificati) 
	{
		boolean documentiCorrotti = false;
		
		// Recupera la lista di tutti i documenti presenti nel riepilogo
		// e verifica se esistono documenti del riepilogo non presenti tra i documenti in W_DOCDIG!!!
		if(riepilogo instanceof RiepilogoBustaBean) {
			// gara a lotto unico
			RiepilogoBustaBean lotto = (RiepilogoBustaBean) riepilogo;
			for(DocumentoAllegatoBean allegatoRiepologo : lotto.getDocumentiInseriti()) {
				if( !allegatiVerificati.contains( allegatoRiepologo ) ) {
					// allegato non presente in W_DOCDIG!!!
					this.addMancante(codiceGara, "", tipoBusta, allegatoRiepologo);
					documentiCorrotti = true;
				}
			}
		} else if(riepilogo instanceof LinkedHashMap) {
			// gara a lotti
			HashMap<String, RiepilogoBustaBean> lotti = (LinkedHashMap<String, RiepilogoBustaBean>) riepilogo;
			Iterator iter = lotti.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry item = (Map.Entry) iter.next();
				RiepilogoBustaBean lotto = (RiepilogoBustaBean) item.getValue();
				if(lotto != null) {
					for(DocumentoAllegatoBean allegatoRiepologo : lotto.getDocumentiInseriti()) {
						if( !allegatiVerificati.contains( allegatoRiepologo ) ) {
							// allegato non presente in W_DOCDIG!!!
							this.addMancante(codiceGara, (String)item.getKey(), tipoBusta, allegatoRiepologo);
							documentiCorrotti = true;
						}
					}
				}
			}
		}		
		
		return documentiCorrotti;
	}

	/**
	 * verifica la presenza di documenti corrotti utilizzando l'UUID
	 * per il match tra riepilogo e busta 
	 */
	private boolean verificaDocumentiPerUUID(
			String codiceGara, 
			String username, 
			String tipoBusta,
			Object riepilogo) throws ApsException
	{	
		boolean documentiCorrotti = false; 

		if(this.dimensioniDocumenti != null && this.dimensioniDocumenti.size() > 0) {
			// crea lista di allegati del riepilogo che sono stati verificati 			
			List<DocumentoAllegatoBean> allegatiVerificati = new ArrayList<DocumentoAllegatoBean>();
			
			// verifica per ogni documento la dimensione registrata
			for(int i = 0; i < this.dimensioniDocumenti.size(); i++) {				
				String uuid = (StringUtils.isNotEmpty(this.dimensioniDocumenti.get(i).getUuid()) 
								? this.dimensioniDocumenti.get(i).getUuid() : null);
				
				if(uuid != null) {
					// trova il lotto nel quale cercare documento nel riepilogo...
					RiepilogoBustaBean lotto = null;
					if(riepilogo instanceof RiepilogoBustaBean) {
						// gara a lotto unico
						lotto = (RiepilogoBustaBean) riepilogo; 
					} else if(riepilogo instanceof LinkedHashMap) {
						// gara a lotti
						LinkedHashMap<String, RiepilogoBustaBean> lotti = (LinkedHashMap<String, RiepilogoBustaBean>) riepilogo;
						lotto = lotti.get( this.dimensioniDocumenti.get(i).getLotto().getCodiceLotto() );
					}
					
					// trova l'allegato nel riepilogo per UUID...
					if(lotto != null) {
						for(int j = 0; j < lotto.getDocumentiInseriti().size(); j++) {
							if(uuid.equals(lotto.getDocumentiInseriti().get(j).getUuid())) {
								// trovato l'allegato presente nel riepilogo 
								DocumentoAllegatoBean allegato = lotto.getDocumentiInseriti().get(j);
								
								// Verifica la dimensione dell'allegato con il riepilogo
								// la validazione viene calcolata come distanza da una data tolleranza
								// la distanza (errore) può essere relativo od assoluto:
								//   1) |a-b|/|b| errore relativo tra dimensione allegato e dimensione del riepilogo
								//   2) |a-b|     errore assoluto tra dimensione allegato e dimensione del riepilogo
								// l'errore relativo non sembra adatto a files di piccole dimensioni (1/2KB)
								double a = (double) (this.dimensioniDocumenti.get(i).getDimensione() != null 
													 ? this.dimensioniDocumenti.get(i).getDimensione() : 0);
								double b = (double) allegato.getDimensione() * 1024.0;
								double d = Math.abs(a - b);
								if( a > 0 && d > 1024 ) {	// |a-b| <= 1024  le dimensioni vengono considerate equivalenti
									this.addCorrotto(codiceGara, tipoBusta, this.dimensioniDocumenti.get(i));
									documentiCorrotti = true;
								}
								
								allegatiVerificati.add(allegato);
								break;
							}
						}
					}
				}
			}
			
			// Recupera la lista di tutti i documenti presenti nel riepilogo
			// e verifica se esistono documenti del riepilogo non presenti 
			// tra i documenti in W_DOCDIG!!!
			if(!documentiCorrotti) {
				documentiCorrotti = checkAllegatiMancanti(
						codiceGara, 
						username, 
						tipoBusta,
						riepilogo,
						allegatiVerificati);
			} 
		}
		
		return documentiCorrotti;
	}

}

