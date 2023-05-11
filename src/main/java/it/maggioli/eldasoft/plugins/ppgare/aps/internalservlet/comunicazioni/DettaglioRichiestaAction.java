package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiDocument;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.DettaglioRichiestaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DettaglioRichiestaAction extends AbstractOpenPageAction 
	implements Serializable, SessionAware, IDownloadAction 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4226818511819063556L;
	
	private static final String PORTAL_ELENCO_RICHIESTE = "backToElencoRichieste"; 
	
	private Map<String, Object> session;
	
	/** Riferimento al manager per la gestione delle comunicazioni. */
	private IComunicazioniManager comunicazioniManager;
	
	/** dettaglio della richiesta selezionata */
	private DettaglioRichiestaBean dettaglio = new DettaglioRichiestaBean();
	
	/** id della richiesta selezionata */
	private String id;		
	
	/** file presente nella work con identificato dal valore in sessione SESSION_ID_DOWNLOAD_WORK_FILE_FIRMA_DIGITALE */
	private Boolean isfile;
		
	/** codice dell'elenco */
	@Validate(EParamValidation.CODICE)
	private String codiceElenco;
	
	/** ... */
	@Validate(EParamValidation.GENERIC)
	private String multipartSaveDir;
			
	/** parametri di gestione del download degli allegati (IDownloadAction) */
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.URL)
	private String urlPage;
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;
	private InputStream inputStream;
	private boolean hasFileRiepilogoAllegati;
	private Long idBustaRiepilogo;
	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String multipartSaveDir) {
    	this.multipartSaveDir = multipartSaveDir;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Boolean getIsfile() {
		return isfile;
	}

	public void setIsfile(Boolean idfile) {
		this.isfile = idfile;
	}

	public String getCodiceElenco() {
		return codiceElenco;
	}

	public void setCodiceElenco(String codiceElenco) {
		this.codiceElenco = codiceElenco;
	}

	public DettaglioRichiestaBean getDettaglio() {
		return dettaglio;
	}

	public void setDettaglio(DettaglioRichiestaBean dettaglio) {
		this.dettaglio = dettaglio;
	}

	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void setUrlPage(String urlPage) {
		this.urlPage = urlPage;
	}
	
	public String getUrlPage() {
		return urlPage;
	}

	@Override
	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}
	
	public String getCurrentFrame() {
		return currentFrame;
	}

	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			"?actionPath=/ExtStr2/do/FrontEnd/Comunicazioni/dettaglioRichiesta.action" +
			"&currentFrame=" + this.currentFrame + 
			"&id=" + this.id + 
			"&codiceElenco=" + this.codiceElenco;
	}
	
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	
	public String getRICHIESTA_ISCRIZIONE_ALBO() {
		return PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO;
	}
	
	public String getRICHIESTA_RINNOVO_ISCRIZIONE() {
		return PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE;
	}
	
	public String getRICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO() {
		return PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO;
	}
	
	public String getRICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO() {
		return PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO;
	}
	
	public String getRICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO() {
		return PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO;
	}
		
	public boolean isHasFileRiepilogoAllegati() {
		return hasFileRiepilogoAllegati;
	}

	public void setHasFileRiepilogoAllegati(boolean hasFileRiepilogoAllegati) {
		this.hasFileRiepilogoAllegati = hasFileRiepilogoAllegati;
	}
	
	public Long getIdBustaRiepilogo() {
		return idBustaRiepilogo;
	}

	public void setIdBustaRiepilogo(Long idBustaRiepilogo) {
		this.idBustaRiepilogo = idBustaRiepilogo;
	}
	/**
	 * Estrae da una comunicazione l'allegato xml contentente i dati 
	 * della comunicazione. 
	 * 
	 * @param comunicazione comunicazione da cui estrarre l'allegato xml 
	 * @return an object of type it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType
	 */
	private it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType getAllegatoComunicazione(
			it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType comunicazione) {

		for(AllegatoComunicazioneType item : comunicazione.getAllegato()) {
			// cerca l'xml con i dati della comunicazione 
			// FS2, FS3, FS4, FS7, FS8
			if ( (PortGareSystemConstants.NOME_FILE_ISCRIZIONE.equals(item.getNomeFile()))
				|| (PortGareSystemConstants.NOME_FILE_RINNOVO_ISCRIZIONE.equals(item.getNomeFile()))
				|| (PortGareSystemConstants.NOME_FILE_AGG_ISCRIZIONE.equals(item.getNomeFile()))
				|| (PortGareSystemConstants.NOME_FILE_GESTIONE_PRODOTTI.equals(item.getNomeFile()))
				|| (PortGareSystemConstants.NOME_FILE_VARIAZIONE_OFFERTA.equals(item.getNomeFile()))
				) {
				return item;
			} 
		}		
		return null;
	}

	/**
	 * Estrae i dati dall'allegato xml della comunuicazione 
	 * ed inizializza il dettaglio della richiesta
	 * @throws IOException 
	 *  
	 */
	private void initDettaglio(it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType comunicazione) {
				
		//Long genere = (Long)this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);
				
		String tipoComunizione = comunicazione
			.getDettaglioComunicazione().getTipoComunicazione();
		
		String workPath = StrutsUtilities.getTempDir(
				this.getRequest().getSession().getServletContext(), 
				this.multipartSaveDir).getAbsolutePath();
		
		try {
			for (it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType item : comunicazione.getAllegato()) {
				
				// salta tutti i documenti che NON sono xml...
				if ( !item.getNomeFile().endsWith(".xml") ) {
					continue;
				}
				
				DocumentoType[] allegati = new DocumentoType[0];				
				Calendar dataPresentazione = null;
				
				if (PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunizione)) {
					IscrizioneImpresaElencoOperatoriDocument doc;					
					doc = IscrizioneImpresaElencoOperatoriDocument.Factory.parse(new String(item.getFile()));
//				doc.getIscrizioneImpresaElencoOperatori().getQuestionarioId();
					dataPresentazione = doc
						.getIscrizioneImpresaElencoOperatori().getDataPresentazione();
					allegati = doc.getIscrizioneImpresaElencoOperatori()
						.getDocumenti().getDocumentoArray();
					
				} 
				else if (PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE.equals(tipoComunizione)) {
					RinnovoIscrizioneImpresaElencoOperatoriDocument doc = 
						RinnovoIscrizioneImpresaElencoOperatoriDocument.Factory
							.parse(new String(item.getFile()));
					
					dataPresentazione = doc
						.getRinnovoIscrizioneImpresaElencoOperatori().getDataPresentazione();
					allegati = doc.getRinnovoIscrizioneImpresaElencoOperatori()
						.getDocumenti().getDocumentoArray();
				} 
				else if (PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO.equals(tipoComunizione)) {
					AggiornamentoIscrizioneImpresaElencoOperatoriDocument doc = 
						AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory
							.parse(new String(item.getFile()));
					
					dataPresentazione = doc
						.getAggiornamentoIscrizioneImpresaElencoOperatori().getDataPresentazione();
					if (doc.getAggiornamentoIscrizioneImpresaElencoOperatori().isSetDocumenti()) {					
						allegati = doc.getAggiornamentoIscrizioneImpresaElencoOperatori()
							.getDocumenti().getDocumentoArray();
					}
				}							
			    else if (PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO.equals(tipoComunizione)) {
			    	GestioneProdottiDocument doc = GestioneProdottiDocument
						.Factory.parse(new String(item.getFile()));
			    	
			    	dataPresentazione = doc.getGestioneProdotti().getDataPresentazione();
			    	
					// 20/05/2016 per ora non estrarre gli allegati
					//allegati = doc.getRinnovoIscrizioneImpresaElencoOperatori()
					//	.getDocumenti().getDocumentoArray();
				}			
				else if (PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO.equals(tipoComunizione)) {
					GestioneProdottiDocument doc = GestioneProdottiDocument
						.Factory.parse(new String(item.getFile()));		    	
					
			    	dataPresentazione = doc.getGestioneProdotti().getDataPresentazione();
					
					// 20/05/2016 per ora non estrarre gli allegati
					//allegati = doc.getRinnovoIscrizioneImpresaElencoOperatori()
					//	.getDocumenti().getDocumentoArray();
				}
				
				Date dataAggCom = (comunicazione.getDettaglioComunicazione().getDataAggiornamentoStato() != null
								   ? comunicazione.getDettaglioComunicazione().getDataAggiornamentoStato().getTime() 
								   : null ); 
				
				this.dettaglio.setId(this.id);
				this.dettaglio.setDataPresentazione((dataPresentazione != null ? dataPresentazione.getTime() : null) );
				this.dettaglio.setNumeroProtocollo( comunicazione.getDettaglioComunicazione().getNumeroProtocollo() );
				this.dettaglio.setDataProtocollo( comunicazione.getDettaglioComunicazione().getDataProtocollo() );
				this.dettaglio.setDataAcquisizione(dataAggCom);
				this.dettaglio.setStato( comunicazione.getDettaglioComunicazione().getStato() );
				this.dettaglio.setTipoInvio(tipoComunizione);
				
				if(allegati.length > 0) {						 
					this.dettaglio.addAllegati(
							allegati, 
							workPath, 
							comunicazione.getDettaglioComunicazione().getApplicativo(),
							Long.toString(comunicazione.getDettaglioComunicazione().getId()));
				}
				
				// esiste 1 solo documento xml come allegato della comunicazione!!! 
				break;
			}
		} catch (XmlException e) {
			ApsSystemUtils.logThrowable(e, this, "initDettaglio");
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "initDettaglio");
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "initDettaglio");
		}
	}

	/**
	 * Visualizza tutte le richieste inviate  da un operatore
	 *  
	 * @return action result (SUCCESS, ERROR, ...)
	 */
	@Override
	public String openPage() {
		this.setTarget(SUCCESS);
		try {
			UserDetails userDetails = this.getCurrentUser();
			
			if( (this.id != null && !this.id.isEmpty()) &&
				(userDetails != null && userDetails.getUsername() != null) ) {

				// se in sessione sono gi� presenti i dettagli della 
				// comunicazione con i relativi allegati  
				// rileggi dalla sessione dettaglio ed allegati della 
				// comunicazione
				this.dettaglio = (DettaglioRichiestaBean)this.session.get(ComunicazioniConstants.SESSION_ID_DETTAGLIO_COMUNICAZIONE);
				
//if(false) // DEBUG: salta la verifica del dettaglio gi� esistente in sessione, ma inserisce il file di riepilogo allegati
				if( this.dettaglio != null && 
					this.id.equals(this.dettaglio.getId()) ) {
					
					it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType comunicazione = 
						this.comunicazioniManager.
							getComunicazione(CommonSystemConstants.ID_APPLICATIVO, Long.parseLong(id));	
					this.setHasFileRiepilogoAllegati(false);
					AllegatoComunicazioneType[] allegatiComunicazioneOfferta = comunicazione.getAllegato();
					for(AllegatoComunicazioneType all : allegatiComunicazioneOfferta){
						if(all.getNomeFile().equals(PortGareSystemConstants.FILENAME_RIEPILOGO) || 
								all.getNomeFile().equals(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE)){
							this.setHasFileRiepilogoAllegati(true);
							this.setIdBustaRiepilogo(comunicazione.getDettaglioComunicazione().getId());
						}
					}
					
					// in sessione sono gi� presenti i dettagli
					// non � necessario rileggerli dal servizio 
					return this.getTarget();
				}
				
				// elimina i vecchi dati dall'area temporanea...
				if( this.dettaglio != null ) {
					this.dettaglio.valueUnbound(null);
				}

				this.dettaglio = null;
				this.dettaglio = new DettaglioRichiestaBean();
				
				// cerca i dettagli comunicazione per l'applicativo "PA"
				// relative all'utente corrente... 
				DettaglioComunicazioneType dettComunicazione = new DettaglioComunicazioneType();				
				dettComunicazione.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				dettComunicazione.setId(Long.parseLong(id));
				dettComunicazione.setChiave1(userDetails.getUsername());
								
				List<DettaglioComunicazioneType> lista = this.comunicazioniManager.
					getElencoComunicazioni(dettComunicazione);
				
				// verifica quanti dettagli comunicazioni sono stati
				// trovati... 1 dettaglio � il risultato atteso
				if(lista.size() <= 0) {
					this.addActionError("Impossibile visualizzare un dettaglio inesistente oppure di cui non si possiede la visibilit�");
					this.setTarget(PORTAL_ELENCO_RICHIESTE);		
				}
				else if(lista.size() == 1) {
					it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType comunicazione = 
						this.comunicazioniManager.
							getComunicazione(CommonSystemConstants.ID_APPLICATIVO, Long.parseLong(id));					

					AllegatoComunicazioneType allegato = getAllegatoComunicazione(comunicazione);							
					if(allegato == null) {
						// non dovrebbe succedere mai... 
						// si inserisce questo controllo per blindare 
						// il codice da eventuali comportamenti anomali										
						this.addActionError(this.getText("Errors.dettaglioInvio.xmlNotFound"));	
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);							
					} else {
						// recupera i dettagli della richiesta...
						this.initDettaglio(comunicazione);
										
						// salva nella sessione il dettaglio della comunicazione 
						// e degli allegati...
						this.session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_COMUNICAZIONE, this.dettaglio);
					}	
					this.setHasFileRiepilogoAllegati(false);
					AllegatoComunicazioneType[] allegatiComunicazioneOfferta = comunicazione.getAllegato();
					for(AllegatoComunicazioneType all : allegatiComunicazioneOfferta){
						if(all.getNomeFile().equals(PortGareSystemConstants.FILENAME_RIEPILOGO) || 
								all.getNomeFile().equals(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE)){
							this.setHasFileRiepilogoAllegati(true);
							this.setIdBustaRiepilogo(comunicazione.getDettaglioComunicazione().getId());
						}
					}
				}	
			} else {
				this.addActionError(this.getText("Errors.sessionExpired"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}			
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPage");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

		
	/**
	 * Restituisce lo stream contenente l'allegato richiesto
	 * 
	 * @return action result (SUCCESS, ERROR, ...)
	 */
	public String downloadAllegato() {
		String target = SUCCESS;
		
//		String token = StringUtils.stripToNull((String) this.appParamManager
//						.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN));

		// se in sessione sono gi� presenti i dettagli della comunicazione 
		// con i relativi allegati, rileggi dalla sessione il dettaglio...  
		this.dettaglio = (DettaglioRichiestaBean)this.session
			.get(ComunicazioniConstants.SESSION_ID_DETTAGLIO_COMUNICAZIONE);
		if( this.dettaglio == null ) {
			target = ERROR;
		} 
		
		try {	
			// cerca l'allegato richiesto 
			// ed istanzia un stream per il download
			// usa il parametro Id (corrisponde all'index della lista)
			// per individuare il file per il download...
			int i = (!StringUtils.isEmpty(this.id) ? Integer.parseInt(this.id) : -1);
			this.dettaglio.setIdAllegato(i);
			
			if(0 <= i && i < this.dettaglio.getDocumentiAllegati().length) {
				
				// verifica se � un documento marcato/firmato (.TSD, .P7M)...
				// e passa alla action della firma digitale il compito di 
				// gestire il file...
				this.filename = this.dettaglio.getDocumentiAllegati()[i].getNomefile();
				
				// SS 2019-11-12: si toglie la differenziazione per
				// reindirizzare il download dei P7M alla pagina di info firma
				// digitale in quanto per i file salvati fuori dall'xml
				// andrebbe aggiunto il file nella work per proseguire con il
				// flusso logico delle operazioni, il che risulta scomodo, ma
				// soprattutto perche' salvare nella work i file con il loro
				// nome implica che in caso di caricamento di file con lo stesso
				// nome si sovrascrivono e ne rimane scaricabile uno solo,
				// pertanto in ottica comunicazioni nel nuovo formato (file
				// fuori dagli xml) non conviene salvare i file nella work ma
				// permetterne il download una volta creato lo stream in memoria
				
				int j = this.filename.lastIndexOf(".");
				String fileext = (j < 0 ? "" : this.filename.substring(j).toLowerCase());
				
				if(".tsd".equals(fileext) || ".p7m".equals(fileext)) {
					// passa la gestione del download alla firma digitale...
					this.session.put(PortGareSystemConstants.SESSION_ID_DOWNLOAD_WORK_FILE_FIRMA_DIGITALE,
									 this.dettaglio);
					this.isfile = true;
					target = "successFirmato";
				} else {
					// gestisci un normale download di uno stream...
					File f = this.dettaglio.getFileGeneratiNellaWork().get(i);
					this.contentType = "application/octet-stream";
					this.filename = this.dettaglio.getDocumentiAllegati()[i].getNomefile();
					if (f == null && this.dettaglio.getDocumentiAllegati()[i].getUuid() != null) {
						// si scarica il documento e lo si inserisce nello stream
						ComunicazioneType risComConAllegato = comunicazioniManager
							.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
											  Long.parseLong(this.dettaglio.getId()),
											  this.dettaglio.getDocumentiAllegati()[i].getUuid());
						this.inputStream = new ByteArrayInputStream(risComConAllegato.getAllegato()[0].getFile());
						
					} else {
						// si legge il file precedentemente scaricato nella work
						this.inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(f));
					}
				}
			} else {
				// errore Id non valido!!!
				//target = INPUT;
			}
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "downloadAllegato");
			ExceptionUtils.manageExceptionError(e, this);
			// il download e' una url indipendente e non dentro una porzione di pagina
			target = ERROR;
		}	
		return target;
	}
	
}
