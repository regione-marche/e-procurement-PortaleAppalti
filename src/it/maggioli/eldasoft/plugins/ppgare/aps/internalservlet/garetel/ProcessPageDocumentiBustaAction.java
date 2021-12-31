package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di gestione delle operazioni nella pagina dei documenti delle buste
 * economica, tecnica, amministrativa
 *
 * @author Marco.Perazzetta
 */
public class ProcessPageDocumentiBustaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 647490520354773711L;

	private static final String CANCEL 							= "cancel";
	private static final String BACK							= "back";
	private static final String BACK_TO_DOCUMENTI				= "backToDocumenti";
	private static final String BACK_TO_GESTIONE_BUSTE_DISTINTE = "backToGestioneBusteDistinte";
	private static final String BACK_TO_BUSTE_TECNICHE_LOTTI 	= "backToBusteTecnicheLotti";
	private static final String BACK_TO_BUSTE_ECONOMICHE_LOTTI 	= "backToBusteEconomicheLotti";

	/**
	 * Riferimento al manager per la gestione dei parametri applicativo.
	 */
	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;
	
	private Long docRichiestoId;
	private File docRichiesto;
	private String docRichiestoContentType;
	private String docRichiestoFileName;
	private String docUlterioreDesc;
	private File docUlteriore;
	private String docUlterioreContentType;
	private String docUlterioreFileName;
	private Integer formato;

	private InputStream inputStream;

	private int tipoBusta;
	private String codice;
	private String codiceGara;
	private int operazione;
	private Date dataPresentazione;
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public Long getDocRichiestoId() {
		return docRichiestoId;
	}

	public void setDocRichiestoId(Long docRichiestoId) {
		this.docRichiestoId = docRichiestoId;
	}

	public File getDocRichiesto() {
		return docRichiesto;
	}

	public void setDocRichiesto(File docRichiesto) {
		this.docRichiesto = docRichiesto;
	}

	public String getDocRichiestoContentType() {
		return docRichiestoContentType;
	}

	public void setDocRichiestoContentType(String docRichiestoContentType) {
		this.docRichiestoContentType = docRichiestoContentType;
	}

	public String getDocRichiestoFileName() {
		return docRichiestoFileName;
	}

	public void setDocRichiestoFileName(String docRichiestoFileName) {
		this.docRichiestoFileName = docRichiestoFileName;
	}

	public String getDocUlterioreDesc() {
		return docUlterioreDesc;
	}

	public void setDocUlterioreDesc(String docUlterioreDesc) {
		this.docUlterioreDesc = docUlterioreDesc;
	}

	public File getDocUlteriore() {
		return docUlteriore;
	}

	public void setDocUlteriore(File docUlteriore) {
		this.docUlteriore = docUlteriore;
	}

	public String getDocUlterioreContentType() {
		return docUlterioreContentType;
	}

	public void setDocUlterioreContentType(String docUlterioreContentType) {
		this.docUlterioreContentType = docUlterioreContentType;
	}

	public String getDocUlterioreFileName() {
		return docUlterioreFileName;
	}

	public void setDocUlterioreFileName(String docUlterioreFileName) {
		this.docUlterioreFileName = docUlterioreFileName;
	}

	public Integer getFormato() {
		return formato;
	}

	public void setFormato(Integer formato) {
		this.formato = formato;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA;	}

	public int getBUSTA_TECNICA() {	return PortGareSystemConstants.BUSTA_TECNICA; }

	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }


	/**
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	private int getActualTotalSize(WizardDocumentiBustaHelper helper) {
		int total = 0;
		for (Integer s : helper.getDocRichiestiSize()) {
			total += s;
		}
		for (Integer s : helper.getDocUlterioriSize()) {
			total += s;
		}
		return total;
	}

	/**
	 * aggiungi un documento richiesto alla busta 
	 */
	public String addDocRich() {
		String target = BACK_TO_DOCUMENTI;
		
		boolean controlliOk = false;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = WizardDocumentiBustaHelper
				.getFromSession(this.session, this.tipoBusta);			
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
				// la sessione non e' scaduta, per cui proseguo regolarmente
			} else {
				String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
				if(StringUtils.isEmpty(codice)) {
					codice = this.codiceGara;
				}
				
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docRichiesto);
				
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(documentiBustaHelper.getDescTipoBusta() + ": documento richiesto" 
						+ " con id=" + this.docRichiestoId
						+ ", file=" + this.docRichiestoFileName
						+ ", dimensione=" + dimensioneDocumento + "KB");
				
				boolean onlyP7m = this.customConfigManager.isActiveFunction("DOCUM-FIRMATO", "ACCETTASOLOP7M");
				controlliOk = true;
				controlliOk = controlliOk && this.checkFileSize(this.docRichiesto, this.docRichiestoFileName, getActualTotalSize(documentiBustaHelper), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docRichiestoFileName, evento);												
				controlliOk = controlliOk && this.checkFileFormat(this.docRichiesto, this.docRichiestoFileName, this.formato, evento, onlyP7m);				

				if (controlliOk) {
					if (!documentiBustaHelper.getDocRichiestiId().contains(this.docRichiestoId)) {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						documentiBustaHelper.addDocRichiesto(
								this.docRichiestoId,
								this.docRichiesto,
								this.docRichiestoContentType,
								this.docRichiestoFileName,
								evento);
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
					}
				} else {
					target = INPUT;
				}
				
				this.eventManager.insertEvent(evento);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "addDocRich");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable e) {
			if (controlliOk) {
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la cifratura dell'allegato richiesto " + this.docRichiestoFileName);
			} else {
				ApsSystemUtils.logThrowable(e, this, "addDocRich", "Errore durante la verifica del formato dell'allegato richiesto " + this.docRichiestoFileName);
			}
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}

	/**
	 * aggiungi un documento ulteriore alla busta 
	 */
	public String addUltDoc() {
		String target = BACK_TO_DOCUMENTI;
		
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = WizardDocumentiBustaHelper
				.getFromSession(this.session, this.tipoBusta);
			
			if (documentiBustaHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
				// la sessione non e' scaduta, per cui proseguo regolarmente
			} else {
				String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
				if(StringUtils.isEmpty(codice)) {
					codice = this.codiceGara;
				}
				
				int dimensioneDocumento = FileUploadUtilities.getFileSize(this.docUlteriore);
				
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(documentiBustaHelper.getDescTipoBusta() + ": documento ulteriore"
						+ " con file=" + this.docUlterioreFileName
						+ ", dimensione=" + dimensioneDocumento + "KB");

				boolean controlliOk = true;
				controlliOk = controlliOk && this.checkFileDescription(this.docUlterioreDesc, evento);				
				controlliOk = controlliOk && this.checkFileSize(this.docUlteriore, this.docUlterioreFileName, getActualTotalSize(documentiBustaHelper), this.appParamManager, evento);
				controlliOk = controlliOk && this.checkFileName(this.docUlterioreFileName, evento);
				controlliOk = controlliOk && this.checkFileExtension(this.docUlterioreFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento);
				controlliOk = controlliOk && this.checkFileFormat(this.docUlteriore, this.docUlterioreFileName, null, evento, false);

				if (controlliOk) {
					if (documentiBustaHelper.getDocUlterioriDesc().contains(this.docUlterioreDesc)) {
						this.addActionError(this.getText("Errors.docUlteriorePresent"));
					} else {
						// in questo modo evito che si ripeta un inserimento
						// effettuando F5 con il browser in quanto controllo se
						// esiste gia' il documento con tale id, dato che posso
						// inserirlo solo se non e' tra quelli inseriti in
						// precedenza
						documentiBustaHelper.addDocUlteriore(
								this.docUlterioreDesc,
								this.docUlteriore,
								this.docUlterioreContentType,
								this.docUlterioreFileName,
								evento);
						
						this.docUlterioreDesc = null;
						documentiBustaHelper.setDatiModificati(true);
						
						if( !this.aggiornaAllegato() ) {
							target = INPUT;
						}
					}
				} else {
					target = INPUT;
				}
				
				this.eventManager.insertEvent(evento);
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "addUltDoc");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "addUltDoc", "Errore durante la cifratura dell'allegato ulteriore " + this.docUlterioreFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addUltDoc");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	/**
	 * ... 
	 */
	@Override
	public String cancel() {
		return CANCEL;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String back() {
		String target = BACK;
		
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		
		if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
			if (this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
				target = BACK_TO_BUSTE_TECNICHE_LOTTI;
			} else if (this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
				target = BACK_TO_BUSTE_ECONOMICHE_LOTTI;
			} else {
				target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
			}
		}
		
		return target;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		return null;
	}
	
	/**
	 * salva la busta ed invia tutti gli allegati 
	 * (usato fino alla v 1.14.5)  
	 */
	public String save() {
		String target = SUCCESS;
		
		if( this.aggiornaAllegato() ) {
			
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			
			if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
				//if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
				//	target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
				//}
			} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
				if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
					target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
				}
			} else if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
				if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
					target = BACK_TO_BUSTE_TECNICHE_LOTTI;
				} 
			} else {
				if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
					target = BACK_TO_BUSTE_ECONOMICHE_LOTTI;
				} 
			}			
		} else {
			target = INPUT;
		}
		
		return target;
	}
	
	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione 
	 */
	private boolean aggiornaAllegato() {
		String target = SUCCESS;
		try {
			WizardDocumentiBustaHelper documentiBustaHelper = WizardDocumentiBustaHelper
				.getFromSession(this.session, this.tipoBusta);
			
			String codice = (StringUtils.isNotEmpty(this.codice) ? this.codice : this.codiceGara);
			if(StringUtils.isEmpty(codice)) {
				codice = this.codiceGara;
			}
			
			target = saveDocumenti(
					documentiBustaHelper.getCodiceGara(),
					codice,
					this.operazione,
					this.tipoBusta,
					this.session,
					this);
		} catch (Throwable e) {
			target = INPUT;
		}
 
		return SUCCESS.equalsIgnoreCase(target);
	}
	
	/**
	 * salva/rimuovi un allegato ed invia la relativa comunicazione 
	 */
	public static String saveDocumenti(			 
			String codiceGara,
			String codice,
			int operazione,
			int tipoBusta,
			Map<String, Object> session,
			BaseAction action) 
	{
		String target = SUCCESS;

		boolean inviaOfferta = (operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
		boolean domandaPartecipazione = (operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);

		WizardDocumentiBustaHelper documentiBustaHelper = null;
		try {
			documentiBustaHelper = WizardDocumentiBustaHelper.getFromSession(session, tipoBusta);
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, action, "save");
			ExceptionUtils.manageExceptionError(ex, action);
			target = ERROR;
		}
		
		if (documentiBustaHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			action.addActionError(action.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// verifica se action e dati in sessione sono sincronizzati...
			if(!documentiBustaHelper.isSynchronizedToAction(codice, action)) {
				return CommonSystemConstants.PORTAL_ERROR;
			}	
			
			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER, ServletActionContext.getRequest());
			
			IEventManager eventManager = (IEventManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.EVENTI_MANAGER, ServletActionContext.getRequest());
			
			Date dataAttuale = new Date();
			documentiBustaHelper.setDataPresentazione(dataAttuale);
			
			WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
			
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

			// Fase di riallineamento 
			RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);			
			try {
				if(tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
					bustaRiepilogativa.getBustaPrequalifica().riallineaDocumenti(documentiBustaHelper);
					session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA, documentiBustaHelper);
					//if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
					//	target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
					//}
				} else if(tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
					bustaRiepilogativa.getBustaAmministrativa().riallineaDocumenti(documentiBustaHelper);
					session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA, documentiBustaHelper);
					if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()){
						target = BACK_TO_GESTIONE_BUSTE_DISTINTE;
					}
				} else if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
					if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()){
						bustaRiepilogativa.getBusteTecnicheLotti().get(codice).riallineaDocumenti(documentiBustaHelper);
						documentiBustaHelper.setCodice(codice);
						target = BACK_TO_BUSTE_TECNICHE_LOTTI;
					} else {
						bustaRiepilogativa.getBustaTecnica().riallineaDocumenti(documentiBustaHelper);
						session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA, documentiBustaHelper);
					}
				} else {
					if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
						bustaRiepilogativa.getBusteEconomicheLotti().get(codice).riallineaDocumenti(documentiBustaHelper); 
						documentiBustaHelper.setCodice(codice);
						target = BACK_TO_BUSTE_ECONOMICHE_LOTTI;
					} else {
						bustaRiepilogativa.getBustaEconomica().riallineaDocumenti(documentiBustaHelper);
						session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA, documentiBustaHelper);
					}
				}
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				//action.addActionError(action.getText("Errors.cannotRealign"));
				target = ERROR;
			}

			try {
				// ----- INVIO COMUNICAZIONI ----- 

				// Invia i Documenti della busta particolare
				sendComunicazioneBusta(
						documentiBustaHelper, 
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						comunicazioniManager,
						eventManager,
						action);

				// Preparazione per invio busta riepilogativa
				// ed invia la nuova comunicazione con gli allineamenti del caso
				String RICHIESTA_TIPO_RIEPILOGO = (domandaPartecipazione)
					? PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO
					: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO;		
				
				bustaRiepilogativa.sendComunicazioneBusta(
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA, 
						datiImpresaHelper, 
						action.getCurrentUser().getUsername(), 
						codiceGara,
						datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(), 
						comunicazioniManager, 
						RICHIESTA_TIPO_RIEPILOGO,
						action);

				session.remove(documentiBustaHelper.getNomeIdSessione());
				documentiBustaHelper.setDatiModificati(false);
				
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				ExceptionUtils.manageExceptionError(e, action);
				target = ERROR;
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				action.addActionError(action.getText("Errors.cannotLoadAttachments"));
				target = ERROR;
			} catch (GeneralSecurityException e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				target = ERROR;
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				action.addActionError(action.getText("Errors.save.outOfMemory"));
				target = INPUT;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, action, "save");
				ExceptionUtils.manageExceptionError(e, action);
				target = ERROR;
			}
		}
		
		// NB: 
		//  - fino alla 1.14.5 il "salva documenti" salva tutti i documenti 
		//    allegati in un'unica soluzione e ritorna al wizard principale... 
		//  - dalla 1.15.0 per ogni documento allegato si salva si resta nella 
		//    pagina corrente...
		if(ERROR.equalsIgnoreCase(target) ||
		   INPUT.equalsIgnoreCase(target) ||
		   CommonSystemConstants.PORTAL_ERROR.equalsIgnoreCase(target)) {
			// restiruisci come target l'errore... 
		} else {
			// altrimenti restiruisci il target richiesto...
			target = SUCCESS;
		}
		
		return target;
	}

	private static void sendComunicazioneBusta(
			WizardDocumentiBustaHelper helper, 
			String stato,
			IComunicazioniManager comunicazioniManager,
			IEventManager eventManager,
			BaseAction action) throws IOException, ApsException, GeneralSecurityException 
	{
		// sicuramente lo username si estrae, in quanto l'utente per arrivare
		// qui deve essere loggato e la sessione non puo' essere scaduta
		String username = action.getCurrentUser().getUsername();
		String ragioneSociale = helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String nomeBusta = helper.getNomeBusta();
		String oggetto = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_OGGETTO"), 
				new Object[] {nomeBusta, helper.getCodice()});
		String testo = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_TESTO"), 
				new Object[] {ragioneSociale200, nomeBusta});
		String descrizioneFile = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_ALLEGATO_DESCRIZIONE"), 
				new Object[] {nomeBusta});

		// si invia la comunicazione
//		sendComunicazione(helper, username, ragioneSociale,
//				stato, helper.getTipoComunicazioneBusta(),
//				oggetto, testo, PortGareSystemConstants.NOME_FILE_BUSTA,
//				descrizioneFile);
		
		//String codice = (!StringUtils.isEmpty(this.codiceGara) ? this.codiceGara : this.codice);
		String codice = helper.getCodice();
		
		// traccia l'evento di salvataggio...
		Event evento = new Event();
		evento.setUsername(action.getCurrentUser().getUsername());
		evento.setDestination(codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
		evento.setIpAddress(action.getCurrentUser().getIpAddress());
		evento.setSessionId(action.getRequest().getSession().getId());
		evento.setMessage("Salvataggio comunicazione " + helper.getTipoComunicazioneBusta() 
				+ " con id " + helper.getIdComunicazione() 
				+ " in stato " + stato);
		
		try {		
			// FASE 1: costruzione del contenitore della comunicazione
			ComunicazioneType comunicazione = new ComunicazioneType();
	
			// FASE 2: popolamento della testata
			DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
				.createDettaglioComunicazione(
						helper.getIdComunicazione(),
						username, 
						helper.getCodice(), 
						helper.getProgressivoOfferta(),
						ragioneSociale, 
						stato, 
						oggetto, 
						testo,
						helper.getTipoComunicazioneBusta(), 
						null);
	
			if (helper.getChiaveSessione() != null) {
				dettaglioComunicazione.setSessionKey(
						EncryptionUtils.encodeSessionKey(helper.getChiaveSessione(), username));
			}
			comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
	
			SendBusteAction.setAllegatoComunicazione(
					comunicazione, 
					helper,
					PortGareSystemConstants.NOME_FILE_BUSTA, 
					descrizioneFile);
	
			// FASE 4: invio comunicazione
			comunicazione.getDettaglioComunicazione().setId(comunicazioniManager.sendComunicazione(comunicazione));
			
			evento.setMessage("Salvataggio comunicazione " + helper.getTipoComunicazioneBusta()
							  + " con id " + comunicazione.getDettaglioComunicazione().getId() 
							  + " in stato " + comunicazione.getDettaglioComunicazione().getStato());
			
			helper.resetStatiInvio();
		
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} catch (GeneralSecurityException e) {
			evento.setError(e);
			throw e;
		} catch (Throwable e) {
			evento.setError(e);
			throw new ApsException( e.getMessage() );
		} finally {
			if(evento != null) {
				eventManager.insertEvent(evento);
			}
		}		
	}
	
}
