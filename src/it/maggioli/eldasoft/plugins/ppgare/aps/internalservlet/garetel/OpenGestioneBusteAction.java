package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Action per le operazioni di invio buste di gare telematiche
 *
 * @version 1.0
 * @author Marco.Perazzetta
 *
 */
public class OpenGestioneBusteAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1693287506907078618L;

	protected IComunicazioniManager comunicazioniManager;
	protected IBandiManager bandiManager;
	protected IAppParamManager appParamManager;

	protected Map<String, Object> session;

	protected String codiceGara;
	protected String codice;
	protected int operazione;
	protected String progressivoOfferta;
	protected String nuovaOfferta;
	private DettaglioComunicazioneType comunicazione;
	private DettaglioComunicazioneType comunicazioneBustaPreq;
	private DettaglioComunicazioneType comunicazioneBustaAmm;
	private DettaglioComunicazioneType comunicazioneBustaTec;
	private DettaglioComunicazioneType comunicazioneBustaEco;
	private DettaglioComunicazioneType comunicazioneBustaRie;
	private DettaglioComunicazioneType comunicazioneDomandaPartecipazione;
	private boolean bustaPreqAlreadySent;
	private boolean bustaAmmAlreadySent;
	private boolean bustaTecAlreadySent;
	private boolean bustaEcoAlreadySent;
	private boolean offertaTelematica;
	private boolean offertaTecnica;
	private boolean protocollazioneMailFallita;
	private boolean costoFisso;

	private String multipartSaveDir;
	
	public Map<String, Object> getSession() {
		return session;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return comunicazioniManager;
	}

	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public IAppParamManager getAppParamManager(){
		return this.appParamManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
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
	
	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}
	
	public String getNuovaOfferta() {
		return nuovaOfferta;
	}

	public void setNuovaOfferta(String nuovaOfferta) {
		this.nuovaOfferta = nuovaOfferta;
	}

	public DettaglioComunicazioneType getComunicazione() {
		return comunicazione;
	}

	public void setComunicazione(DettaglioComunicazioneType comunicazione) {
		this.comunicazione = comunicazione;
	}

	public boolean isOffertaTelematica() {
		return offertaTelematica;
	}

	public void setOffertaTelematica(boolean offertaTelematica) {
		this.offertaTelematica = offertaTelematica;
	}

	public boolean isOffertaTecnica() {
		return offertaTecnica;
	}

	public void setOffertaTecnica(boolean offertaTecnica) {
		this.offertaTecnica = offertaTecnica;
	}

	public DettaglioComunicazioneType getComunicazioneBustaAmm() {
		return comunicazioneBustaAmm;
	}

	public void setComunicazioneBustaAmm(DettaglioComunicazioneType comunicazioneBustaAmm) {
		this.comunicazioneBustaAmm = comunicazioneBustaAmm;
	}

	public DettaglioComunicazioneType getComunicazioneBustaTec() {
		return comunicazioneBustaTec;
	}

	public void setComunicazioneBustaTec(DettaglioComunicazioneType comunicazioneBustaTec) {
		this.comunicazioneBustaTec = comunicazioneBustaTec;
	}

	public DettaglioComunicazioneType getComunicazioneBustaEco() {
		return comunicazioneBustaEco;
	}

	public void setComunicazioneBustaEco(DettaglioComunicazioneType comunicazioneBustaEco) {
		this.comunicazioneBustaEco = comunicazioneBustaEco;
	}

	public DettaglioComunicazioneType getComunicazioneBustaPreq() {
		return comunicazioneBustaPreq;
	}

	public void setComunicazioneBustaPreq(DettaglioComunicazioneType comunicazioneBustaPreq) {
		this.comunicazioneBustaPreq = comunicazioneBustaPreq;
	}

	public DettaglioComunicazioneType getComunicazioneDomandaPartecipazione() {
		return comunicazioneDomandaPartecipazione;
	}

	public void setComunicazioneDomandaPartecipazione(
			DettaglioComunicazioneType comunicazioneDomandaPartecipazione) {
		this.comunicazioneDomandaPartecipazione = comunicazioneDomandaPartecipazione;
	}

	public boolean isBustaAmmAlreadySent() {
		return bustaAmmAlreadySent;
	}

	public void setBustaAmmAlreadySent(boolean bustaAmmAlreadySent) {
		this.bustaAmmAlreadySent = bustaAmmAlreadySent;
	}

	public boolean isBustaTecAlreadySent() {
		return bustaTecAlreadySent;
	}

	public void setBustaTecAlreadySent(boolean bustaTecAlreadySent) {
		this.bustaTecAlreadySent = bustaTecAlreadySent;
	}

	public boolean isBustaEcoAlreadySent() {
		return bustaEcoAlreadySent;
	}

	public void setBustaEcoAlreadySent(boolean bustaEcoAlreadySent) {
		this.bustaEcoAlreadySent = bustaEcoAlreadySent;
	}

	public boolean isBustaPreqAlreadySent() {
		return bustaPreqAlreadySent;
	}

	public void setBustaPreqAlreadySent(boolean bustaPreqAlreadySent) {
		this.bustaPreqAlreadySent = bustaPreqAlreadySent;
	}

	public DettaglioComunicazioneType getComunicazioneBustaRie() {
		return comunicazioneBustaRie;
	}

	public void setComunicazioneBustaRie(DettaglioComunicazioneType comunicazioneBustaRie) {
		this.comunicazioneBustaRie = comunicazioneBustaRie;
	}
		
	public boolean isCostoFisso() {
		return costoFisso;
	}

	public void setCostoFisso(boolean costoFisso) {
		this.costoFisso = costoFisso;
	}
	
	public boolean isProtocollazioneMailFallita() {
		return protocollazioneMailFallita;
	}

	public void setProtocollazioneMailFallita(boolean protocollazioneMailFallita) {
		this.protocollazioneMailFallita = protocollazioneMailFallita;
	}

	
	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}

	public int getBUSTA_AMMINISTRATIVA() {
		return PortGareSystemConstants.BUSTA_AMMINISTRATIVA;
	}

	public int getBUSTA_TECNICA() {
		return PortGareSystemConstants.BUSTA_TECNICA;
	}

	public int getBUSTA_ECONOMICA() {
		return PortGareSystemConstants.BUSTA_ECONOMICA;
	}

	public int getBUSTA_PRE_QUALIFICA() {
		return PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
	}


	/**
	 * Apertura pagina di gestione dei prodotti
	 *
	 * @return
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		if(StringUtils.isEmpty(this.progressivoOfferta)) {
			this.progressivoOfferta = "1";
		}
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// pulizia della sessione dall'accesso ad una gara precedente
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);
				
				boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA); 
				boolean invioOfferta = !domandaPartecipazione; //(this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
				
				String RICHIESTA_TIPO_RIEPILOGO = (domandaPartecipazione)
						? PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO
						: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO;
				
				this.setComunicazioneBustaRie(ComunicazioniUtilities.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codice, 
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						RICHIESTA_TIPO_RIEPILOGO));

				// carica, all'accesso alla funzione, i dati aggiornati dell'impresa
				// CESSATI
				WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(), this, this.appParamManager);
				
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA,
								 datiImpresaHelper);

				DettaglioGaraType gara = this.bandiManager.getDettaglioGara(this.codice);
				this.offertaTelematica = gara.getDatiGeneraliGara().isOffertaTelematica();
				this.offertaTecnica = (boolean) this.bandiManager.isGaraConOffertaTecnica(this.codice);
				this.costoFisso = (boolean) (gara.getDatiGeneraliGara().getCostoFisso() == 1);

				boolean invioOffertaGaraRistretta = invioOfferta
					&& WizardPartecipazioneHelper.isGaraRistretta(gara.getDatiGeneraliGara().getIterGara());

				String tipoComunicazione = (domandaPartecipazione)
						? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT 
						: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
				
				this.comunicazione = ComunicazioniUtilities.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codice, 
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						tipoComunicazione);
				if(this.comunicazione == null){
					this.comunicazione = ComunicazioniUtilities.retrieveComunicazione(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(),
							this.codice, 
							this.progressivoOfferta,
							CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
							tipoComunicazione);
				}
				
				WizardPartecipazioneHelper wizardPartecipazione = (WizardPartecipazioneHelper)this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
							
				// nel caso di una gara ristretta
				// recupero la comunicazione della domanda di partecipazione
				this.setComunicazioneDomandaPartecipazione(null);
				if (invioOffertaGaraRistretta) {
					this.setComunicazioneDomandaPartecipazione(
							this.retriveComunicazioneDomandaPartecipazione(this.codice));
				}

				if (wizardPartecipazione == null) { 
					// ogni volta che si ripassa per la gestione buste, se non e'
					// presente il wizard di partecipazione lo si ricarica
					// completando la sola parte relativa all'XML estratto
					// e l'editabilita' dei dati RTI

					// si cerca l'eventuale tipo di partecipazione se gia' presente in backoffice
					TipoPartecipazioneType tipoPartecipazione = this.bandiManager
							.getTipoPartecipazioneImpresa(
									this.getCurrentUser().getUsername(), 
									this.codice,
									this.progressivoOfferta);

					// NB: il wizard di partecipazione si elimina dalla sessione
					// quando si termina lo step corrispondente, per cui sono
					// costretto a rileggere i dati con questo if
					wizardPartecipazione = this.createPartecipazioneHelper(
							this.codice, 
							this.operazione, 
							gara, 
							this.progressivoOfferta,
							tipoPartecipazione);
						
					if(this.comunicazione != null || this.comunicazioneDomandaPartecipazione != null) {
						if(this.comunicazione != null) {
							// assegna l'id comunicazione solo se si tratta 
							// della comunicazione di gara. 
							// nel caso di gara ristretta in fase di offerta  
							// se non esiste ancora la comunicazione FS11 
							// NON utilizzare l'id della preesistente
							wizardPartecipazione.setIdComunicazioneTipoPartecipazione(this.comunicazione.getId());
						}
					
						Long idComunicazione = (this.comunicazione != null 
												? this.comunicazione.getId() 
												: this.comunicazioneDomandaPartecipazione.getId());
						
						ComunicazioneType comunicazioneCompleta = this.comunicazioniManager.getComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								idComunicazione);
						TipoPartecipazioneDocument document = WizardDocumentiBustaHelper
							.getDocumentoPartecipazione(comunicazioneCompleta);
						wizardPartecipazione.fillFromXml(document);
					}
					
					// SPOSTATO in createPartecipazioneHelper()
					//if (wizardPartecipazione.isGaraTelematica() && tipoPartecipazione == null) {
					//	// finche' non invio posso editare i dati correnti sempre se non mi arrivano 
					//	// le informazioni di partecipazione direttamente dal back office (vedasi le 
					//	// negoziate senza previa pubblicazione del bando, in cui le ditte sono inserite
					//	// gia' come rti oppure no, per cui neanche la prima volta che uso la funzione 
					//	// posso editare le informazioni relative alla composizione RTI).
					//	wizardPartecipazione.setEditRTI(true);
					//}
					if (invioOffertaGaraRistretta) {
						// per le gare ristrette in fase di invio offerta
						// se l’impresa si è presentata come RTI, 
						// non è più possibile modificare la composizione
						if( tipoPartecipazione != null && tipoPartecipazione.isRti() ) {
							wizardPartecipazione.setEditRTI( !tipoPartecipazione.isRti() );
							wizardPartecipazione.setRtiPartecipazione( tipoPartecipazione.isRti() );
						}
					}
					
					this.session.put(
							PortGareSystemConstants.SESSION_ID_DETT_PART_GARA,
							wizardPartecipazione);	
				}
				
				this.setProtocollazioneMailFallita(false);
				
				DettaglioComunicazioneType comunicazioneDaProtocollare = ComunicazioniUtilities
						.retrieveComunicazione(
								this.comunicazioniManager,
								this.getCurrentUser().getUsername(),
								this.codice,
								this.progressivoOfferta,
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
								tipoComunicazione);
				
				if(comunicazioneDaProtocollare != null) {
					this.setProtocollazioneMailFallita(this.comunicazioniManager.getComunicazione(
									CommonSystemConstants.ID_APPLICATIVO, 
									comunicazioneDaProtocollare.getId())!= null);
				}
				
				if (domandaPartecipazione) {
					// provo a ricavare la comunicazione per la busta di prequalifica
					this.setComunicazioneBustaPreq(ComunicazioniUtilities.retrieveComunicazione(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(),
							this.codice, 
							this.progressivoOfferta,
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA));
					if (this.getComunicazioneBustaPreq() == null) {
						this.setBustaPreqAlreadySent(ComunicazioniUtilities.retrieveComunicazione(
								this.comunicazioniManager,
								this.getCurrentUser().getUsername(),
								this.codice, 
								this.progressivoOfferta,
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA) != null);
					}
				} 
				
				// verifico se precedentemente è stata inviata una domanda 
				// di partecipazione (gara ristretta) quindi recupero la 
				// comunicazione FS10 per verifica quali lotti sono stati 
				// abilitati
				if (invioOffertaGaraRistretta) {
					List<String> lotti = this.retriveLottiAmmessiGaraRistretta(
							this.codice, 
							gara.getDatiGeneraliGara().getTipologia()==PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO,
							wizardPartecipazione);
					//if(lotti != null) {
						//lottiAttivi.clear();
						//for(int i = 0; i < lotti.size(); i++) {
						//	lottiAttivi.add(lotti.get(i));
						//}
					//}
				}

				if (invioOfferta) {
					// provo a ricavare la comunicazione per la busta amministrativa
					this.setComunicazioneBustaAmm( retriveComunicazioneBustaAmministrativa(this.codice) );
			
					// provo a ricavare la comunicazione per la busta tecnica
					if (this.offertaTecnica) {
						this.setComunicazioneBustaTec( retriveComunicazioneBustaTecnica(this.codice) );
					}
					
					// provo a ricavare la comunicazione per la busta economica
					this.setComunicazioneBustaEco( retriveComunicazioneBustaEconomica(this.codice, this.offertaTelematica) );					
				}

				
				// nella gara a lotto unico i documenti stanno nel lotto, 
				// nella gara ad offerta unica stanno nella gara
				String codiceLotto = null;
				if (gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
					codiceLotto = gara.getDatiGeneraliGara().getCodice();
				}
				

				if (this.comunicazione != null) {
					RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
					
					// verifica se l'helper della busta di riepilogo è sincronizzato
					boolean riepilogoHelperRefresh = 
						getComunicazioneBustaRie() == null || 
						bustaRiepilogativa == null ||
					    (bustaRiepilogativa != null && !bustaRiepilogativa.getCodiceGara().equals(this.getCodiceGara())) ||							// codice gara diverso
					    (bustaRiepilogativa != null && wizardPartecipazione.isRti() && bustaRiepilogativa.getUltimiFirmatariInseriti() == null);	// rti <=> non rti o viceversa
					
					if(riepilogoHelperRefresh) {
						this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
						bustaRiepilogativa = new RiepilogoBusteHelper(
								bandiManager, 
								this.codice,
								codiceLotto,
								datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
								wizardPartecipazione.isRti(), 
								false,
								tipoComunicazione, 								// Offerta | Partecipazione
								this.getCurrentUser().getUsername(),
								this.progressivoOfferta);
						this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA, 
										 bustaRiepilogativa);
					}

					if(this.getComunicazioneBustaRie() == null) {
						if(this.getComunicazioneBustaPreq() != null) {
							WizardDocumentiBustaHelper bustaPrequalifica = recuperaDatiBusta(this.getComunicazioneBustaPreq(), this.getBUSTA_PRE_QUALIFICA(), gara);
							bustaRiepilogativa.getBustaPrequalifica().riallineaDocumenti(bustaPrequalifica);
						}
						
						if(this.getComunicazioneBustaAmm() != null) {
							WizardDocumentiBustaHelper bustaAmministrativa = recuperaDatiBusta(this.getComunicazioneBustaAmm(), this.getBUSTA_AMMINISTRATIVA(), gara);
							bustaRiepilogativa.getBustaAmministrativa().riallineaDocumenti(bustaAmministrativa);
						}

						if (invioOfferta) {
							if(this.getComunicazioneBustaTec() != null) {
								WizardDocumentiBustaHelper bustaTecnica = recuperaDatiBusta(this.getComunicazioneBustaTec(), this.getBUSTA_TECNICA(), gara);
								bustaRiepilogativa.getBustaTecnica().riallineaDocumenti(bustaTecnica);
							}
	
							if(this.getComunicazioneBustaEco() != null) {
								WizardDocumentiBustaHelper bustaEconomica = recuperaDatiBustaEconomica(this.getComunicazioneBustaEco());
								bustaRiepilogativa.getBustaEconomica().riallineaDocumenti(bustaEconomica);
							}
						}
						
						// Creazione e invio della comunicazione FS10R/FS11R 
						// con allegato l'xml di riepilogo delle varie buste/lotti
						bustaRiepilogativa.sendComunicazioneBusta(
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
								datiImpresaHelper, 
								this.getCurrentUser().getUsername(), 
								this.codice,
								datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(), 
								this.comunicazioniManager,
								RICHIESTA_TIPO_RIEPILOGO,
								this);	
					} else {
						// Recupero la comunicazione riepilogativa
						ComunicazioneType comunicazioneSalvata = comunicazioniManager.getComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								this.getComunicazioneBustaRie().getId());

						bustaRiepilogativa.setIdComunicazione(comunicazioneSalvata.getDettaglioComunicazione().getId());

						// Popolamento dell'helper di riepilogo gli ultimi dati 
						// presenti all'interno dell'xml
						if (domandaPartecipazione) {
							RiepilogoBustePartecipazioneDocument documento = getBustePartecipazioneDocument(comunicazioneSalvata);

							if(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica() != null) { 
								bustaRiepilogativa.getBustaPrequalifica().popolaBusta(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica());
	
								// INTEGRAZIONE BO PER BUSTA PREQUALIFICA 
								bustaRiepilogativa.integraBustaPrequalificaFromBO(codice, codiceLotto, bandiManager, datiImpresaHelper, wizardPartecipazione);
							}
						}
						
						if (invioOfferta) {
							RiepilogoBusteOffertaDocument documento = getBusteOffertaDocument(comunicazioneSalvata);
							
							if(documento.getRiepilogoBusteOfferta().getBustaAmministrativa() != null) {
								bustaRiepilogativa.getBustaAmministrativa().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaAmministrativa());
	
								// INTEGRAZIONE BO PER BUSTA AMMINISTRATIVA 
								bustaRiepilogativa.integraBustaAmministrativaFromBO(codice, codiceLotto, bandiManager, datiImpresaHelper, wizardPartecipazione);
							}
							if(documento.getRiepilogoBusteOfferta().getBustaTecnica() != null) {
								bustaRiepilogativa.getBustaTecnica().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaTecnica());
								
								// INTEGRAZIONE BO PER BUSTA TECNICA
								bustaRiepilogativa.integraBustaTecnicaFromBO(bustaRiepilogativa.getBustaTecnica(), bandiManager, codice, codiceLotto, datiImpresaHelper, wizardPartecipazione);						
							}
							if(documento.getRiepilogoBusteOfferta().getBustaEconomica() != null) {
								bustaRiepilogativa.getBustaEconomica().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaEconomica());
	
								// INTEGRAZIONE BO BUSTA ECONOMICA 
								bustaRiepilogativa.integraBustaEconomicaFromBO(bustaRiepilogativa.getBustaEconomica(), bandiManager, codice, codiceLotto, datiImpresaHelper, wizardPartecipazione);
							}
						}
					} //if(this.getComunicazioneBustaRie() == null)
					
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA, bustaRiepilogativa);	
				} // if (this.comunicazione != null)
			
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "open");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (XmlException ex) {
				ApsSystemUtils.logThrowable(ex, this, "open");
				ExceptionUtils.manageExceptionError(ex, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "open");
				this.addActionError(this.getText("Errors.invioBuste.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * ricava la comunicazione della busta amministrativa 
	 * @throws ApsException 
	 */
	protected DettaglioComunicazioneType retriveComunicazioneBustaAmministrativa(
			String codice) throws ApsException 
	{
		DettaglioComunicazioneType comunicazione = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				codice, 
				this.progressivoOfferta,
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
				PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA);
		if (comunicazione == null) {
			this.setBustaAmmAlreadySent(ComunicazioniUtilities.retrieveComunicazione(
					this.comunicazioniManager,
					this.getCurrentUser().getUsername(),
					codice, 
					this.progressivoOfferta,
					CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA) != null);
		}
		return comunicazione;
	}

	/**
	 * ricava la comunicazione della busta tecnica 
	 * @throws ApsException
	 */
	private DettaglioComunicazioneType retriveComunicazioneBustaTecnica(
			String codice) throws ApsException 
	{
		DettaglioComunicazioneType comunicazione = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				codice, 
				this.progressivoOfferta,
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
				PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
		if (comunicazione == null) {
			this.setBustaTecAlreadySent(ComunicazioniUtilities.retrieveComunicazione(
					this.comunicazioniManager,
					this.getCurrentUser().getUsername(),
					codice, 
					this.progressivoOfferta,
					CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA) != null);
		}
		return comunicazione;
	}
	
	/**
	 * ricava la comunicazione della busta economica 
	 * @throws ApsException
	 */
	private DettaglioComunicazioneType retriveComunicazioneBustaEconomica(
			String codice,
			boolean offertaTelematica) throws ApsException 
	{
//		String tipoComunicazioneOfferta = (this.offertaTelematica)
//			? PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA_OFFERTA_TELEMATICA
//			: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;
		String tipoComunicazioneOfferta = 
				PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;

		DettaglioComunicazioneType comunicazione = ComunicazioniUtilities.retrieveComunicazione(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(),
				codice, 
				null,
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
				tipoComunicazioneOfferta);
		if (comunicazione == null) {
			this.setBustaEcoAlreadySent(ComunicazioniUtilities.retrieveComunicazione(
					this.comunicazioniManager,
					this.getCurrentUser().getUsername(),
					this.codice,
					null,
					CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
					tipoComunicazioneOfferta) != null);
		}
		return comunicazione;
	}
	
	/** Metodo per recuperare i documenti dalle comunicazioni per le buste amministrativa e tecnica
	 * 	@param DettaglioComunicazioneType dettaglio della comunicazione
	 *  @param int tipo della busta di cui recuprare i documenti allegati
	 *  @param DettaglioGaraType gara
	 *  @param WizardDatiImpresaHelper dati dell'impresa
	 * 
	 */
	protected WizardDocumentiBustaHelper recuperaDatiBusta(
			DettaglioComunicazioneType dettaglioComunicazione, 
			int tipoBusta, 
			DettaglioGaraType dettGara) 
	{
		WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
		
		WizardDocumentiBustaHelper documentiBustaHelper = null;
		if (dettaglioComunicazione != null) {
			try {
				documentiBustaHelper = WizardDocumentiBustaHelper.getInstance(
						this.session,
						this.codiceGara,
						this.codice, 
						tipoBusta, 
						this.operazione,
						this.progressivoOfferta);
				try {
					documentiBustaHelper.setDatiDocumenti(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(), 
							dettGara, 
							datiImpresaHelper,
							this.getTempDir().getAbsolutePath());
					
					// (3.2.0) verifica ed integra la busta di riepilogo FS10R/FS11R...
					bustaRiepilogativa.verificaIntegraDatiDocumenti(documentiBustaHelper);
					
				} catch (Throwable ex) {
					if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
						this.addActionError(this.getText("Errors.invioBuste.xmlBustaNotFound", new String[]{documentiBustaHelper.getNomeBusta()}));
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					}
				}
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "recuperaDatiBusta");
				ExceptionUtils.manageExceptionError(e, this);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "recuperaDatiBusta");
				ExceptionUtils.manageExceptionError(e, this);
			}
		}

		return documentiBustaHelper;
	}

	/** Metodo per recuperare i documenti dalle comunicazioni per le buste amministrativa e tecnica
	 * 	@param DettaglioComunicazioneType dettaglio della comunicazione
	 */
	protected WizardDocumentiBustaHelper recuperaDatiBustaEconomica(
			DettaglioComunicazioneType dettaglioComunicazione) 
	{
		WizardDocumentiBustaHelper documentiBustaHelper = null;

		if (dettaglioComunicazione != null) {
			ComunicazioneType comunicazione = null;
			try {
				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				
				comunicazione = comunicazioniManager.getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO,
						dettaglioComunicazione.getId());

				documentiBustaHelper = WizardDocumentiBustaHelper.getInstance(
						this.session, 
						this.codiceGara,
						this.codice, 
						this.getBUSTA_ECONOMICA(), 
						this.operazione,
						this.progressivoOfferta,
						comunicazione.getDettaglioComunicazione());
				
				// NB: 
				//   in caso di busta cifrata è necessario caricare gli allegati
				//   in DocRichiestiCifrati e in DocUlterioriCifrati
//				boolean cifratura = (comunicazione.getDettaglioComunicazione().getSessionKey() != null);

				BustaEconomicaDocument economicaDoc = getBustaEconomicaDocument(comunicazione);

				documentiBustaHelper.popolaDocumentiFromComunicazione(
						economicaDoc.getBustaEconomica().getDocumenti(), 
						comunicazione.getDettaglioComunicazione().getId());
				
				documentiBustaHelper.setAlreadyLoaded(true);
				
				// verifica ed integra la busta di riepilogo FS10R/FS11R...
				bustaRiepilogativa.verificaIntegraDatiDocumenti(documentiBustaHelper);

			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "recuperaDatiBustaEconomica");
				ExceptionUtils.manageExceptionError(e, this);
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "recuperaDatiBustaEconomica");
				ExceptionUtils.manageExceptionError(e, this);
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "recuperaDatiBustaEconomica");
				ExceptionUtils.manageExceptionError(e, this);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "recuperaDatiBustaEconomica");
				ExceptionUtils.manageExceptionError(e, this);
			}
		}
		return documentiBustaHelper;
	}

	/**
	 * Estrae, a partire dalla comunicazione, il documento XML contenente
	 * l'offerta.
	 * 
	 * @param comunicazione
	 *            comunicazione con in allegato i dati dell'offerta
	 * @return oggetto XmlObject della classe BustaEconomicaDocument
	 * @throws ApsException
	 * @throws XmlException
	 */
	public static BustaEconomicaDocument getBustaEconomicaDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
		BustaEconomicaDocument documento = null;
		AllegatoComunicazioneType allegatoIscrizione = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && allegatoIscrizione == null) 
		{
			// si cerca l'xml con i dati dell'iscrizione tra
			// tutti gli allegati
			if (PortGareSystemConstants.NOME_FILE_BUSTA.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoIscrizione = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}

		if (allegatoIscrizione == null) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
			throw new ApsException("Errors.offertaTelematica.xmlOffertaNotFound");
		} else {
			// si interpreta l'xml ricevuto
			documento = BustaEconomicaDocument.Factory.parse(
					new String(allegatoIscrizione.getFile()));
		}
		return documento;
	}

	/**
	 * @param multipartSaveDir the multipartSaveDir to set
	 */
	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	/**
	 * Ritorna la directory per i file temporanei, prendendola da
	 * struts.multipart.saveDir (struts.properties) se valorizzata correttamente,
	 * altrimenti da javax.servlet.context.tempdir
	 *
	 * @return path alla directory per i file temporanei
	 */
	private File getTempDir() {
		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), this.multipartSaveDir);
	}

	/**
	 * ... 
	 */
	public static RiepilogoBustePartecipazioneDocument getBustePartecipazioneDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
		return RiepilogoOffertaAction.getBustePartecipazioneDocument(comunicazione);
	}	

	/**
	 * ... 
	 */
	public static RiepilogoBusteOffertaDocument getBusteOffertaDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
		return RiepilogoOffertaAction.getBusteOffertaDocument(comunicazione);
	}	
	

	/**
	 * Per una gara ristretta,  estrae la comunicazione della domanda 
	 * di partecipazione. 
	 * 
	 * @param codice
	 * 			è il codice della gara ristretta
	 * @return comunicazione della domanda di partecipazione della gara ristretta
	 * 		   NULL se la gara non è ristretta
	 *  
	 * @throws ApsException
	 */	
	protected DettaglioComunicazioneType retriveComunicazioneDomandaPartecipazione(String codice)	 
		throws ApsException 
	{
		List<String> stati = new ArrayList<String>();
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
		stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		
		return ComunicazioniUtilities.retrieveComunicazioneConStati(
				this.comunicazioniManager,
				this.getCurrentUser().getUsername(), 
				codice,
				this.progressivoOfferta,
				PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT,
				stati);
	}
	
	/**
	 * Estrae i lotti ammessi/abilitati per una gara ristretta. 
	 * 
	 * @param codice
	 *          è il codice della gara ristretta
	 * @param plicoUnico
	 * 			indica se la gara e' a plico unico
	 * @return lista dei lotti selezionati in partecipazione per una gara ristretta
	 * 		   NULL se la gara non è ristretta
	 *  
	 * @throws ApsException
	 */
	protected List<String> retriveLottiAmmessiGaraRistretta(
			String codice, 
			boolean plicoUnico, 
			WizardPartecipazioneHelper partecipazione)
		throws ApsException 
	{
		List<String> lottiAttivi = null;
		
		LottoGaraType[] lotti = null;
		if(plicoUnico) {
			lotti = this.bandiManager.getLottiGaraPlicoUnicoPerRichiesteOfferta(
					this.getCurrentUser().getUsername(), codice);
		} else {
			lotti = this.bandiManager.getLottiGaraPerRichiesteOfferta(
					this.getCurrentUser().getUsername(), codice);
		}
		
		// per le gare ristrette (itergare = 2, 4) 
		// in caso di nuova offerta per una RTI recupera i lotti ammessi
		// di tutte le offerte
		if("1".equals(this.nuovaOfferta)) {
			if(partecipazione != null) {
				//...
			}
		}
		
		if(lotti != null) {
			// imposta i lotti abilitati per l'offerta
			lottiAttivi = new ArrayList<String>();
			for(int i = 0; i < lotti.length; i++) {
				lottiAttivi.add(lotti[i].getCodiceLotto());
			}
			
			// aggiorna i lotti ammessi in sessione
			if(partecipazione == null) {
				// non dovrebbe succedere mai...
				// per le gare ristrette l'helper arriva dalla sessione 
				// o viene creato in base alla precedente domanda di partecipazione
				this.addActionError("OpenGestineBusteDistinte.retriveLottiAmmessiGaraRistretta(): send offer with partecipation NULL ('partecipazione==null')");
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				partecipazione.setLottiAmmessiInOfferta(lottiAttivi);
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA, partecipazione);
			}
		}
		return lottiAttivi;
	}

	/**
	 * Crea il contenitore della partecipazione da porre in sessione
	 * 
	 * @param codice 
	 * @param tipoEvento tipologia di evento
	 * @param dettGara dettaglio della gara estratta dal backoffice
	 * @param progressivoOfferta 
	 * @param tipoPartecipazione 
	 * @return helper da memorizzare in sessione
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	protected WizardPartecipazioneHelper createPartecipazioneHelper(
			String codice, 
			int tipoEvento, 
			DettaglioGaraType dettGara,
			String progressivoOfferta, 
			TipoPartecipazioneType tipoPartecipazione) 
		throws ApsException, XmlException 
	{
		if(StringUtils.isEmpty(progressivoOfferta)) {
			progressivoOfferta = "1";
		}
		
		boolean ristretta = ("2".equals(dettGara.getDatiGeneraliGara().getTipoProcedura()) || 
							 "4".equals(dettGara.getDatiGeneraliGara().getTipoProcedura()));
		
		boolean garaLotti = false;
		if(dettGara != null && dettGara.getDatiGeneraliGara() != null) {
			garaLotti =	dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE
						|| (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO);
		}
		
		// NB: 
		// in caso di gara a lotti solo il progressivo #1 può presentare come "singola"
		// mentre tutti gli altri progressivo possono presentare solo come "rti"
		boolean partecipaInRti = (garaLotti && Long.parseLong(progressivoOfferta) > 1);

		WizardPartecipazioneHelper helper = new WizardPartecipazioneHelper();
		helper.setIdBando(codice);
		helper.setDescBando(dettGara.getDatiGeneraliGara().getOggetto());
		helper.setTipoEvento(tipoEvento);
		helper.setGaraTelematica(dettGara.getDatiGeneraliGara().isProceduraTelematica());
		helper.setTipoProcedura(Integer.parseInt(dettGara.getDatiGeneraliGara().getTipoProcedura()));
		helper.setIterGara(Integer.parseInt(dettGara.getDatiGeneraliGara().getIterGara()));
		helper.setProgressivoOfferta(progressivoOfferta);
		helper.setDenominazioneRTIReadonly(true);
		
		switch (dettGara.getDatiGeneraliGara().getTipologia()) {
		case PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE:
			helper.setLottiDistinti(true);
			break;
//		case PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO: 
//			&& dettGara.getDatiGeneraliGara().getBusteDistinte()) {
//			helper.setPlicoUnicoOfferteDistinte(true);
//			if(!helper.isGaraTelematica()) {
//				helper.getLotti().add(this.codice);
//			}
//			break;
//		default:
//			// nel caso di lotto unico o piu' lotti con offerte
//			// distinte si inserisce direttamente come codice lotto
//			// il codice del bando stesso
//			helper.getLotti().add(codice);
//			break;
		}

		// per RTI si utilizzano i dati della partecipazione presenti in backoffice
		if (tipoPartecipazione != null) {
			// ristrette e negoziate
			helper.setRti(tipoPartecipazione.isRti());
			helper.setDenominazioneRTI(tipoPartecipazione.getDenominazioneRti());
			
			if (!tipoPartecipazione.isRti()) {
				// permetto l'editing dei componenti se sono invitato come singola impresa
				helper.setEditRTI(true);
			} 
			
			// vanno recuperati da BO anche i componenti di RTI o le 
			// consorziate esecutrici ???
			// ...
			// helper.getComponenti().add( new IComponente() );
			// ...
		
			// NB: dal BO per le gare negoziate editRti=False !!! 
			helper.setRtiBO(tipoPartecipazione.isRti());
		} else {
			// aperte
			if(dettGara.getDatiGeneraliGara().isProceduraTelematica()) {
				helper.setEditRTI(true);
			}
			
			// per le gare ristrette a lotti (itegara = 2, 4)
			// in caso di una nuova offerta si abilita lo step "forma di partecipazione" !!!
			if(ristretta) {
				helper.setEditRTI(true);
				helper.setRtiPartecipazione(false);
			}

			// forza la partecipazione in RTI per i progressivi > 1 !!!
			if(partecipaInRti) {
				helper.setRti(true);						// imposta il tipo partecipazione come RTI
				helper.setEditRTI(true);					// abilita lo step "Forma partecipazione"
				helper.setRtiPartecipazione(false);
				helper.setDenominazioneRTIReadonly(false);	// rendi la denominazione RTI editabile
			}
		}
		
		return helper;
	}

}
