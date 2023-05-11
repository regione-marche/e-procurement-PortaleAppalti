package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoLottoBustaType;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampResult;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.MarcaturaTemporaleFileUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.RiepilogoBusteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoAllegatoBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.DocumentazioneRichiestaGaraPlicoUnico;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.lowagie.text.DocumentException;

/**
 * ...
 */
public class BustaRiepilogo extends BustaGara {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2095423049767268915L;

	
	private RiepilogoBusteHelper helper;
	private boolean almenoUnaBustaTecnica;
	private boolean almenoUnaBustaEconomica;
		

	public RiepilogoBusteHelper getHelper() {
		return helper;
	}
	
	public void setHelper(RiepilogoBusteHelper helper) {
		this.helper = helper;
	}
	
	public boolean isAlmenoUnaBustaTecnica() {
		return almenoUnaBustaTecnica;
	}

	public boolean isAlmenoUnaBustaEconomica() {
		return almenoUnaBustaEconomica;
	}


	/**
	 * costruttore
	 */
	public BustaRiepilogo(
			GestioneBuste buste,
			int tipoBusta)
	{		
		super(buste, null, tipoBusta);
		
		this.almenoUnaBustaTecnica = false;
		this.almenoUnaBustaEconomica = false;

		// inizializza la comunicazione associata al riepilogo FS10R / FS11R
		String tipoComunicazione = (tipoBusta == BUSTA_RIEPILOGO_PRE 
									? ComunicazioneFlusso.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO
									: ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_RIEPILOGO);
		this.comunicazioneFlusso.getDettaglioComunicazione().setTipoComunicazione(tipoComunicazione);
		
		// inizializza l'helper
		this.helper = null;
	}
	
	/**
	 * ... 
	 */
	@Override
	public boolean get() throws Throwable {
		GestioneBuste.traceLog("BustaRiepilogo.get");		
		boolean continua = super.get();
		if(continua) {
			continua = continua && this.initHelper();
		}
		return continua;
	}

	/**
	 * invia la comunicazione al servizio
	 * @param busta 
	 * 			busta documenti (opzionale) per effettuare il riallineamento del riepilogo prima dell'invio 
	 */
	public boolean send(BustaDocumenti busta) throws Throwable {
		GestioneBuste.traceLog("BustaRiepilogo.send " + this.comunicazioneFlusso.getTipoComunicazione());		
		boolean continua = true;
	
		// riallinea il riepilogo alla busta
		if(busta != null) {
			continua = continua && this.riallineaDocumenti(busta);
		}
		
		// invia la busta di riepilogo al servizio
		if(continua) {
			BaseAction action = GestioneBuste.getAction();
			WizardDatiImpresaHelper impresa = this.gestioneBuste.getImpresa();
			
			String ragioneSociale = impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			String oggetto = MessageFormat.format(
						action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_OGGETTO"),
						new Object[] { this.username, this.codiceGara });
			
			DettaglioComunicazioneType dettaglioComunicazione = this.comunicazioneFlusso.getDettaglioComunicazione();
			dettaglioComunicazione.setApplicativo(ComunicazioneFlusso.ID_APPLICATIVO);
			dettaglioComunicazione.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			dettaglioComunicazione.setChiave1(this.username);
			dettaglioComunicazione.setChiave2(this.codiceGara);
			dettaglioComunicazione.setChiave3(this.progressivoOfferta);
			dettaglioComunicazione.setMittente(StringUtils.left(ragioneSociale, 60));
			dettaglioComunicazione.setOggetto(oggetto);
			
			String xmlDescription = MessageFormat.format(
					action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_ALLEGATO_DESCRIZIONE"), 
					new Object[] { this.getDescrizioneBusta() });
			
			Long id = this.comunicazioneFlusso.send(
					this.helper.getXmlDocument(), 
					xmlDescription,
					null);
			
			continua = continua && (id != null && id.longValue() > 0);
		}
		
		return continua;
	}
	
	/**
	 * inizializza l'helper associato al riepilogo 
	 */
	@Override
	protected boolean initHelper() throws Throwable {
		GestioneBuste.traceLog("BustaRiepilogo.initHelper");
		this.helper = null;
		try {
			EncodedDataAction action = (EncodedDataAction)GestioneBuste.getAction();
			WizardDatiImpresaHelper impresa = this.gestioneBuste.getImpresa();
			BustaPartecipazione bustaPartecipazione = this.gestioneBuste.getBustaPartecipazione();
			WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper();
			DettaglioGaraType gara = this.gestioneBuste.getDettaglioGara();
			IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
						
			boolean integrazioneEffettuata = false;		// dei documenti o dei firmatari
			List<String> lottiAttivi = bustaPartecipazione.getLottiAttivi();
			
			// NB: crea il riepilogo in base al tipo di comunizione 
			//     indicata dalla forma di partecipazione (FS11/FS10) 
			this.helper = new RiepilogoBusteHelper(
				bandiManager, 
				this.codiceGara, 
				null, 
				impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
				partecipazione.isRti(), 
				partecipazione.isPlicoUnicoOfferteDistinte(),
				bustaPartecipazione.comunicazioneFlusso.getTipoComunicazione(),  // FS11/FS10 
				this.username,
				this.progressivoOfferta,
				lottiAttivi);
			
			boolean garaLotti = (
					this.gestioneBuste.isInvioOfferta() &&
					partecipazione.isGaraTelematica() && partecipazione.isPlicoUnicoOfferteDistinte() &&
					this.helper.getListaCompletaLotti() != null);
			
			// integra, per le gare a lotti, le buste di riepilogo associate ai lotti
			// NB: la lista dei letti viene recuperata in creazione dell'helper !!!
			
			// nella gara a lotto unico i documenti stanno nel lotto, 
			// nella gara ad offerta unica stanno nella gara
			String codiceLotto = null;
			if(gara != null &&
			   gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) 
			{
				codiceLotto = gara.getDatiGeneraliGara().getCodice();
			}
			
			// recupera il documento XML del riepilogo dalla comunicazione
			if(this.comunicazioneFlusso.getId() > 0) {
				String xml = this.comunicazioneFlusso.getXmlDoc();
				if(xml == null) {
					// non dovrebbe succedere mai...
					String errMsg = (this.getTipoBusta() == BUSTA_RIEPILOGO_PRE
							? "Errors.partecipazioneTelematica.xmlPartecipazioneNotFound"
							: "Errors.offertaTelematica.xmlOffertaNotFound");
					throw new ApsException(GestioneBuste.getAction().getText(errMsg));
				}
				
				if(this.getTipoBusta() == BUSTA_RIEPILOGO_PRE) { 
					// riepilogo di una domanda di partecipazione
					RiepilogoBustePartecipazioneDocument doc = RiepilogoBustePartecipazioneDocument.Factory.parse(xml);
					
					if(doc.getRiepilogoBustePartecipazione().getBustaPrequalifica() != null) {
						if(this.helper.getBustaPrequalifica() == null) {
							this.helper.setBustaPrequalifica( new RiepilogoBustaBean() );
						}
						this.helper.getBustaPrequalifica().popolaBusta(doc.getRiepilogoBustePartecipazione().getBustaPrequalifica());
						this.helper.setPrimoAccessoPrequalificaEffettuato(doc.getRiepilogoBustePartecipazione().getBustaPrequalifica().getPresaVisioneDocumenti());
						this.helper.integraBustaPrequalificaFromBO(
								this.codiceGara, 
								codiceLotto, 
								bandiManager, 
								impresa, 
								partecipazione);
					}
				} else {
					// riepilogo di un'invio offerta
					RiepilogoBusteOffertaDocument doc = RiepilogoBusteOffertaDocument.Factory.parse(xml);
					
					if(doc.getRiepilogoBusteOfferta().getBustaAmministrativa() != null) {
						if(this.helper.getBustaAmministrativa() == null) {
							this.helper.setBustaAmministrativa( new RiepilogoBustaBean() );
						}
						this.helper.getBustaAmministrativa().popolaBusta(doc.getRiepilogoBusteOfferta().getBustaAmministrativa());
						this.helper.setPrimoAccessoAmministrativaEffettuato(doc.getRiepilogoBusteOfferta().getBustaAmministrativa().getPresaVisioneDocumenti());
						this.helper.integraBustaAmministrativaFromBO(
								this.codiceGara, 
								codiceLotto, 
								bandiManager, 
								impresa, 
								partecipazione);
					}
					
					// verifica se e' una gara a lotti o meno					
					if( !garaLotti ) {
						// gara NO lotti
						//						
						if(doc.getRiepilogoBusteOfferta().getBustaTecnica() != null) {
							if(this.helper.getBustaTecnica() == null) {
								this.helper.setBustaTecnica( new RiepilogoBustaBean() );
							}
							this.helper.getBustaTecnica().popolaBusta(doc.getRiepilogoBusteOfferta().getBustaTecnica());
							this.helper.setPrimoAccessoTecnicaEffettuato(doc.getRiepilogoBusteOfferta().getBustaTecnica().getPresaVisioneDocumenti());
							this.helper.integraBustaTecnicaFromBO(
									this.helper.getBustaTecnica(), 
									this.codiceGara, 
									codiceLotto,
									bandiManager,
									impresa, 
									partecipazione);
						}
						if(doc.getRiepilogoBusteOfferta().getBustaEconomica() != null) {
							if(this.helper.getBustaEconomica() == null) {
								this.helper.setBustaEconomica( new RiepilogoBustaBean() );
							}
							this.helper.getBustaEconomica().popolaBusta(doc.getRiepilogoBusteOfferta().getBustaEconomica());
							this.helper.setPrimoAccessoEconomicaEffettuato(doc.getRiepilogoBusteOfferta().getBustaEconomica().getPresaVisioneDocumenti());
							this.helper.integraBustaEconomicaFromBO(
									this.helper.getBustaEconomica(), 
									this.codiceGara, 
									codiceLotto,
									bandiManager,
									impresa, 
									partecipazione);
						}
					} else {
						// gara a LOTTI
						//
						// recupera i lotti attivi per la gara...
						// NB: per le RISTRETTE in fase di offerta i lotti ammessi 
						//     sono quelli presentati in prequalifica
//						List<String> listaCompletaLotti = this.helper.getListaCompletaLotti();
						
						RiepilogoLottoBustaType[] lottiXml = doc.getRiepilogoBusteOfferta().getLottoArray();
						if(lottiXml != null) {
							// recupera la documentazione del plico unico...
							DocumentazioneRichiestaGaraPlicoUnico documentazionePlicoUnico = bandiManager
									.getDocumentiRichiestiBandoGaraPlicoUnico(
											this.codiceGara, 
											impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
											partecipazione.isRti());
							
							for(int i = 0; i < lottiXml.length; i++) {
								String codLotto = lottiXml[i].getCodiceLotto();
								String oggetto = lottiXml[i].getOggetto();
								
								if( !lottiAttivi.contains(codLotto) ) {
									// Lotto NON ammesso
									// aggiorna il wizard...
									partecipazione.getLotti().remove(codLotto);
								} else {
									// Lotto ammesso
									// integra la busta tecnica...
									RiepilogoBustaType bustaTecnica = lottiXml[i].getBustaTecnica();
									if(this.helper.getBusteTecnicheLotti() != null) {
										boolean isGaraConOffertaTecnica = (boolean) bandiManager.isGaraConOffertaTecnica(codLotto);
										
										RiepilogoBustaBean bustaTecnicaLotto = this.helper.getBusteTecnicheLotti().get(codLotto);										
										
										if(isGaraConOffertaTecnica) {
											bustaTecnicaLotto = this.helper.recuperaDocumentazioneLotto(
													documentazionePlicoUnico.getLotti(), 
													codLotto,
													PortGareSystemConstants.BUSTA_TECNICA);

											integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaTecnicaFromBO(
													bustaTecnicaLotto,
													this.codiceGara, 
													codLotto,
													bandiManager,
													impresa, 
													partecipazione);
				
											if(StringUtils.isNotEmpty(this.helper.getDocumentazioneMancanteError())) {
												// se non mancano i documenti della busta tecnica segnala l'anomalia
												String msg = action.getText(this.helper.getDocumentazioneMancanteError(),
										 				  	 				new String[] { codLotto });
												action.addActionError(msg); 
												ApsSystemUtils.getLogger().error(msg);
												action.setTarget(CommonSystemConstants.PORTAL_ERROR);	//INPUT
											} else {
												if (oggetto != null) {
													// nel caso di busta con QFORM la recuperaDocumentazioneLotto(...) 
													// estrae i dati dal riepilogo e aggiorna l'oggetto 
													// pertanto si aggiorna qui l'oggetto se fosse ancora nullo 
													// (non sara' nel caso di questionario visto che e' stato settato poco sopra)
													bustaTecnicaLotto.setOggetto(oggetto);
												}
												this.helper.popolaBusteLotti(bustaTecnica, bustaTecnicaLotto);
												this.almenoUnaBustaTecnica = true;
											}
										}
		
										this.helper.getBusteTecnicheLotti().put(codLotto, bustaTecnicaLotto);
//										if(this.helper.getBusteTecnicheLotti().get(codiceLotto) != null) {
//											this.nessunDocPerTec = this.nessunDocPerTec && this.helper.getBusteTecnicheLotti().get(codiceLotto).getDocumentiInseriti().isEmpty();
//										}	
										if(bustaTecnica != null) {
											this.helper.getPrimoAccessoTecnicheEffettuato().put(codLotto, bustaTecnica.getPresaVisioneDocumenti());
										}
									}
									
									// integra la busta economica
									RiepilogoBustaType bustaEconomica = lottiXml[i].getBustaEconomica();
									if(this.helper.getBusteEconomicheLotti() != null) {
										// in caso di OEPV con costofisso, 
										// il lotto non è attivo quindi NULL...
//										RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(codLotto);
										RiepilogoBustaBean bustaEconomicaLotto = this.helper.recuperaDocumentazioneLotto(
												documentazionePlicoUnico.getLotti(), 
												codLotto, 
												PortGareSystemConstants.BUSTA_ECONOMICA);
										
										if(StringUtils.isNotEmpty(this.helper.getDocumentazioneMancanteError())) {
											// se non mancano i documenti della busta economica segnala l'anomalia
											String msg = action.getText(this.helper.getDocumentazioneMancanteError(),
													 				    new String[] { codLotto });
											action.addActionError(msg); 
											ApsSystemUtils.getLogger().error(msg);
											action.setTarget(CommonSystemConstants.PORTAL_ERROR);	//INPUT
										} else if(bustaEconomicaLotto != null) {
											if (oggetto != null) {
												// nel caso di busta con qform la recuperaDocumentazioneLotto estrae i dati dal riepilogo e aggiorna
												// l'oggetto pertanto si aggiorna qui l'oggetto se fosse ancora nullo (non sara' nel caso di
												// questionario visto che e' stato settato poco sopra)
												bustaEconomicaLotto.setOggetto(oggetto);
											}
											this.helper.popolaBusteLotti(bustaEconomica, bustaEconomicaLotto);
											integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaEconomicaFromBO(
													bustaEconomicaLotto, 
													this.getCodiceGara(), 
													codLotto,
													bandiManager,
													impresa, 
													partecipazione);
											this.helper.getBusteEconomicheLotti().put(codLotto, bustaEconomicaLotto);
											this.almenoUnaBustaEconomica = true;
										}
										
//										if(this.helper.getBusteEconomicheLotti().get(codiceLotto) != null) {
//											this.nessunDocPerEco = this.nessunDocPerEco && this.helper.getBusteEconomicheLotti().get(codiceLotto).getDocumentiInseriti().isEmpty();
//										}
										if(bustaEconomica != null) {
											this.helper.getPrimoAccessoEconomicheEffettuato().put(codLotto, bustaEconomica.getPresaVisioneDocumenti());
										}
									}
								}
							}
						}
						
						// recupera gli ultimi firmatari inseriti...
						FirmatarioType[] ultimiFirmatariUtilizzati = null;
		                if(this.gestioneBuste.isInvioOfferta()) {
		                	ultimiFirmatariUtilizzati = doc.getRiepilogoBusteOfferta().getFirmatarioArray();
		                }
		                
		                // ripopola hint per firmatari in caso di RTI...
						if(partecipazione.isRti()) {
							if(ultimiFirmatariUtilizzati != null && ultimiFirmatariUtilizzati.length > 0) {
								// ripopola la lista dei firmatari
								this.helper.getUltimiFirmatariInseriti().clear();
								
								for(int i = 0; i < ultimiFirmatariUtilizzati.length; i++) {
									SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper(ultimiFirmatariUtilizzati[i]);
									this.helper.getUltimiFirmatariInseriti().add(firmatario);
								}
								
								integrazioneEffettuata = true;
							}
						}
					}
					
					// codice gara 
					// e lista lotti attivati all'ultimo giro
					this.helper.setCodiceGara(this.codiceGara);
					//this.helper.setListaCompletaLotti(lottiAttivi);	<== BUG!!!
					
					// per le gare a lotti...
					// in caso di OEPV con costo fisso il lotto non va abilitato
					// "disabilita" i lotti di OEPV con costo fisso...	
					if(this.helper.getBusteEconomicheLotti() != null) {
						for(int j = 0; j < gara.getLotto().length; j++) {
							if(gara.getLotto(j).getCostoFisso() != null && gara.getLotto(j).getCostoFisso() == 1) {
								// lotto OEPV con costo fisso... 
								this.helper.getBusteEconomicheLotti().remove(gara.getLotto(j).getCodiceLotto());
							}
						}
					}
				}
			}
			
			// NB: in caso di solo upload documenti 
			//     imposta il primo accesso per le buste tecniche ed economiche
			if(this.soloUploadDocumenti) {
				if(this.tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
					this.helper.setPrimoAccessoPrequalificaEffettuato(true);
				}
				if(this.tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
					this.helper.setPrimoAccessoAmministrativaEffettuato(true);
				}
				if(this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
					if( !this.helper.getPrimoAccessoTecnicheEffettuato().get(this.codiceLotto)) {
						// gara a lotti
						this.helper.getPrimoAccessoTecnicheEffettuato().put(this.codiceLotto, true);
					} else if(this.helper.getBustaTecnica() != null) {
						// gara NO lotti
						this.helper.setPrimoAccessoTecnicaEffettuato(true);
					}
				} 
				if(this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
					if( !this.helper.getPrimoAccessoEconomicheEffettuato().get(this.codiceLotto)) {
						// gara a lotti
						this.helper.getPrimoAccessoEconomicheEffettuato().put(this.codiceLotto, true);
					} else if(this.helper.getBustaEconomica() != null) {
						// gara NO lotti
						this.helper.setPrimoAccessoEconomicaEffettuato(true);
					}
				}
				this.helper.setModified(true);
			}

			// aggiornamento FS11R/FS10R post integrazione documenti da BO in stato BOZZA 
			if(integrazioneEffettuata && this.comunicazioneFlusso.getId() > 0) {
				this.comunicazioneFlusso.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);				
				this.send(null);
			}
			
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("BustaRiepilogo", "initHelper", t);
			this.helper = null;
			throw t;
		}
		return (this.helper != null);
	}
	
	/**
	 * sincronizza i documenti del del riepilogo con i documenti di una busta
	 * @throws IOException 
	 */
	protected boolean riallineaDocumenti(BustaDocumenti busta) {
		GestioneBuste.traceLog("BustaRiepilogo.riallineaDocumenti");
		boolean continua = true;
		if(busta != null) {
			try {
				// riallinea i documenti del riepilogo in base alla busta...
				if(busta.getTipoBusta() == BUSTA_PRE_QUALIFICA) {
					this.helper.getBustaPrequalifica().riallineaDocumenti( busta.getHelperDocumenti() );
					
				} else if(busta.getTipoBusta() == BUSTA_AMMINISTRATIVA) {
					this.helper.getBustaAmministrativa().riallineaDocumenti( busta.getHelperDocumenti() );				
	
				} else if(busta.getTipoBusta() == BUSTA_TECNICA) {
					if(this.helper.getBustaTecnica() != null) {
						// gara senza lotti
						this.helper.getBustaTecnica().riallineaDocumenti( busta.getHelperDocumenti() );
					} else {
						// gara a lotti
						RiepilogoBustaBean r = this.helper.getBusteTecnicheLotti().get(busta.getCodiceLotto());
						if(r != null) {
							r.riallineaDocumenti( busta.getHelperDocumenti() );
						}
					}
					
				} else if(busta.getTipoBusta() == BUSTA_ECONOMICA) {
					if(this.helper.getBustaEconomica() != null) {
						// gara senza lotti
						this.helper.getBustaEconomica().riallineaDocumenti( busta.getHelperDocumenti() );
					} else {
						// gara a lotti
						RiepilogoBustaBean r = this.helper.getBusteEconomicheLotti().get(busta.getCodiceLotto());
						if(r != null) {
							r.riallineaDocumenti( busta.getHelperDocumenti() );
						}
					}
				}
			} catch (Throwable t) {
				continua = false;
				ApsSystemUtils.getLogger().error("BustaRiepilogo", "riallineaDocumenti", t);
			}
		}
		return continua;
	}

	/**
	 * verifica e integra i documenti del riepilogo in base ad una data busta  
	 * @throws Throwable 
	 */
	public boolean verificaIntegraDocumenti(BustaDocumenti busta) throws Throwable {
		GestioneBuste.traceLog("BustaRiepilogo.verificaIntegraDocumenti");
		boolean continua = true;
		try {	
			// verifica ed integra i documenti del riepilogo...
			continua = this.helper.verificaIntegraDatiDocumenti(busta, busta.getCodiceLotto()); //this.codiceLotto);
			
			// invia la comunicazione aggiornata del riepilogo in stato BOZZA...
			if(continua && this.helper.isModified()) {
				this.comunicazioneFlusso.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				this.send(null);
			}
		} catch (Throwable t) {
			continua = false;
			ApsSystemUtils.getLogger().error("BustaRiepilogo", "verificaIntegraDocumenti", t);
			throw t;
		}
		return continua;
	}
	
	/**
	 * integra da BO le buste di riepilogo 
	 * @throws ApsException 
	 */
	public boolean integraBusteFromBO() throws ApsException {
		GestioneBuste.traceLog("BustaRiepilogo.integraBusteFromBO");
		if(this.helper == null) { 
			return false;
		}
		
		boolean integrazioneEffettuata = false;
		
		if(this.gestioneBuste.isDomandaPartecipazione()) {
			// --- ricalcolo gli eventuali documenti mancanti --- 
			integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaPrequalificaFromBO(
					this.codiceGara, 
					this.codiceGara, 
					this.gestioneBuste.getBandiManager(), 
					this.gestioneBuste.getImpresa(), 
					this.gestioneBuste.getBustaPartecipazione().getHelper());
		}
		
		if(this.gestioneBuste.isInvioOfferta()) {
			RiepilogoBustaBean bustaTecnica = this.helper.getBustaTecnica();
			RiepilogoBustaBean bustaEconomica = this.helper.getBustaEconomica();
			boolean garaLotti = (bustaTecnica == null && bustaEconomica == null &&
								 this.helper.getListaCompletaLotti() != null &&
								 this.helper.getListaCompletaLotti().size() > 0);
	
			if( !garaLotti ) {
				// gara NO lotti
				// --- ricalcolo gli eventuali documenti mancanti --- 
				if( !this.gestioneBuste.getDettaglioGara().getDatiGeneraliGara().isNoBustaAmministrativa() ) {
					integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaAmministrativaFromBO(
							this.codiceGara, 
							this.codiceGara, 
							this.gestioneBuste.getBandiManager(), 
							this.gestioneBuste.getImpresa(), 
							this.gestioneBuste.getBustaPartecipazione().getHelper());
				}
				
				if(bustaTecnica != null) {
					integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaTecnicaFromBO(
							bustaTecnica, 
							this.codiceGara, 
							this.codiceGara,
							this.gestioneBuste.getBandiManager(),
							this.gestioneBuste.getImpresa(), 
							this.gestioneBuste.getBustaPartecipazione().getHelper());
				}
				
				if(bustaEconomica != null) {
					integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaEconomicaFromBO(
							bustaEconomica, 
							this.codiceGara, 
							this.codiceGara, 
							this.gestioneBuste.getBandiManager(),
							this.gestioneBuste.getImpresa(), 
							this.gestioneBuste.getBustaPartecipazione().getHelper());
				}			
			} else {
				// --- per ogni lotto controllo se esiste la busta tecnica ---
				// gare a lotti...
				boolean integrazioneBustaAmministrativa = this.helper.integraBustaAmministrativaFromBO(
						this.codiceGara, 
						null, 
						this.gestioneBuste.getBandiManager(),
						this.gestioneBuste.getImpresa(), 
						this.gestioneBuste.getBustaPartecipazione().getHelper());
				integrazioneEffettuata = integrazioneEffettuata || integrazioneBustaAmministrativa;

				for(int i = 0; i < this.helper.getListaCompletaLotti().size(); i++) {
					String lottoCorrente = this.helper.getListaCompletaLotti().get(i);
					
					// BUSTE TECNICHE
					// per ogni lotto controllo se esiste la busta tecnica
					RiepilogoBustaBean bustaTecnicaLotto = this.helper.getBusteTecnicheLotti().get(lottoCorrente);
					if(bustaTecnicaLotto!= null) {
						integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaTecnicaFromBO(
								bustaTecnicaLotto,
								this.codiceGara, 
								lottoCorrente,
								this.gestioneBuste.getBandiManager(),
								this.gestioneBuste.getImpresa(), 
								this.gestioneBuste.getBustaPartecipazione().getHelper());
					}

					// BUSTE ECONOMICHE
					// ricalcolo gli eventuali documenti mancanti
					RiepilogoBustaBean bustaEconomicaLotto = this.helper.getBusteEconomicheLotti().get(lottoCorrente);
					if(bustaEconomicaLotto != null) {
						integrazioneEffettuata = integrazioneEffettuata || this.helper.integraBustaEconomicaFromBO(
								bustaEconomicaLotto, 
								this.codiceGara, 
								lottoCorrente,
								this.gestioneBuste.getBandiManager(),
								this.gestioneBuste.getImpresa(), 
								this.gestioneBuste.getBustaPartecipazione().getHelper());
					}
				}
			}
		}
		
		return integrazioneEffettuata;
	}

	/**
	 * integra i lotti in base ai lotti selezionati in partecipazione
	 * @throws ApsException 
	 */
	public void integraLottiFromPartecipazione() 
		throws ApsException 
	{
		BustaPartecipazione bustaPartecipazione = this.gestioneBuste.getBustaPartecipazione();
		WizardPartecipazioneHelper partecipazione = bustaPartecipazione.getHelper();
		WizardDatiImpresaHelper impresa = this.gestioneBuste.getImpresa();
		IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
		
		// recupero la lista dei lotti selezionati in partecipazione
		List<String> lottiSelezionati = new ArrayList<String>();
		if(partecipazione != null && partecipazione.getLotti() != null) {
			Iterator<String> lottiSelezionatiIterator = partecipazione.getLotti().iterator();
			while(lottiSelezionatiIterator.hasNext()) {
				lottiSelezionati.add(lottiSelezionatiIterator.next());
			}
		} else {
			ApsSystemUtils.getLogger().warn("integraLottiFromPartecipazione", 
											"lotti non trovati in WizardPartecipazioneHelper.getLotti() per " + this.gestioneBuste.getCodiceGara() + ", utente " + this.gestioneBuste.getUsername());
		}
	
		// recupero la documentazione comune a tutti i lotti 
		DocumentazioneRichiestaGaraPlicoUnico documentazionePlicoUnico = 
			bandiManager.getDocumentiRichiestiBandoGaraPlicoUnico(
					this.codiceGara, 
					impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazione.isRti());
	
//		//if(domandaPartecipazione) {
//		//	for(int i = 0; i < listaCompletaLotti.size();i++) {
//		//		String codiceLotto = listaCompletaLotti.get(i);
//		//	}
//		//}		
	
		List<QuestionarioType> questionari = bandiManager.getQuestionari(
				this.codiceGara, 
				null, 
				null);

		// per tutti i lotti abilitati in partecipazione 
		// crea il riepilogo per le buste economiche/tecniche di ogni lotto
//		Iterator<String> lotti = this.helper.getListaCodiciInterniLotti().keySet().iterator();
//		while(lotti.hasNext()) {
//			String lotto = lotti.next();

		for(int i = 0; i < this.helper.getListaCompletaLotti().size(); i++) {
			String lotto = this.helper.getListaCompletaLotti().get(i);
			
			boolean isLottoPresente = 
				(this.helper.getBusteEconomicheLotti().get(lotto) != null || 
				 this.helper.getBusteTecnicheLotti().get(lotto) != null);
			
			// rimuovi eventuali lotti non piu' previsti dalla partecipazione...
			if( !lottiSelezionati.contains(lotto) /*&& isLottoPresente*/ ) {
				// rimuovi il lotto dal riepilogo...
				this.helper.getListaLottiRimossi().add(lotto);
				this.helper.getBusteTecnicheLotti().put(lotto, null);
				this.helper.getBusteEconomicheLotti().put(lotto, null);
				
				// elimina eventuali comunicazioni esistenti...
				// recupero le comunicazioni relative alla busta del lotto (tecnica/economica)
				Long idComunicazioneTecnica = null;
				DettaglioComunicazioneType comunicazioneTecnicaPerLotto = this.comunicazioneFlusso
					.find(this.username,
						  lotto, 
						  this.progressivoOfferta,
						  PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
						  CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				if(comunicazioneTecnicaPerLotto != null) {
					idComunicazioneTecnica = comunicazioneTecnicaPerLotto.getId();
				}		
				
				DettaglioComunicazioneType comunicazioneEconomicaPerLotto = this.comunicazioneFlusso
					.find(this.username,
						  lotto, 
						  this.progressivoOfferta,
						  PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
						  CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				Long idComunicazioneEconomica = null;
				if(comunicazioneEconomicaPerLotto != null) {
					idComunicazioneEconomica = comunicazioneEconomicaPerLotto.getId();
				}

				// se esistono comunicazioni associate ai lotti che non sono previsti
				// elimina le comunicazioni
				if(idComunicazioneTecnica != null) {
					this.comunicazioneFlusso.delete(idComunicazioneTecnica);
				}
				if(idComunicazioneEconomica != null) {
					this.comunicazioneFlusso.delete(idComunicazioneEconomica);
				}
			}
				
			// aggiungi un lotto previsto dalla partecipazione...
			if( !isLottoPresente && lottiSelezionati.contains(lotto) ) {
				// NB: in caso di costo fisso il lotto economico non c'e'
				if(bandiManager.isGaraConOffertaTecnica(lotto)) {
					// aggiungi la busta del lotto previsto nella partecipazione
					RiepilogoBustaBean bustaTecnica = this.helper.recuperaDocumentazioneLotto(
							documentazionePlicoUnico.getLotti(), 
							lotto, 
							PortGareSystemConstants.BUSTA_TECNICA);
					this.helper.getBusteTecnicheLotti().put(lotto, bustaTecnica);
					
					// Questionari
					if(bustaTecnica != null) {
						boolean questionario = this.helper.verificaPresenzaQuestionario(
								PortGareSystemConstants.BUSTA_TECNICA, 
								lotto, 
								questionari);
						bustaTecnica.setQuestionarioPresente(questionario);
					}
				}
				
				// aggiungi la busta del lotto previsto nella partecipazione
				// in caso di OEPV con costo fisso, il lotto non e' previsto...
				RiepilogoBustaBean bustaEconomica = this.helper.recuperaDocumentazioneLotto(
						documentazionePlicoUnico.getLotti(), 
						lotto, 
						PortGareSystemConstants.BUSTA_ECONOMICA);
				this.helper.getBusteEconomicheLotti().put(lotto, bustaEconomica);

				// Questionari
				if(bustaEconomica != null) {
					boolean questionario = this.helper.verificaPresenzaQuestionario(
							PortGareSystemConstants.BUSTA_ECONOMICA, 
							lotto, 
							questionari);
					bustaEconomica.setQuestionarioPresente(questionario);
				}
			} 
		}
	}

	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	public RiepilogoBustePartecipazioneDocument getRiepilogoPartecipazioneDocument() throws XmlException {
		RiepilogoBustePartecipazioneDocument doc = null;
		String xml = this.comunicazioneFlusso.getXmlDoc();
		if(StringUtils.isNotEmpty(xml)) {
			try {
				doc = RiepilogoBustePartecipazioneDocument.Factory.parse(xml);
			} catch(XmlException e) {
				doc = null;
			}
		}
		return doc; 
	}

	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	public RiepilogoBusteOffertaDocument getRiepilogoOffertaDocument() throws XmlException {
		RiepilogoBusteOffertaDocument doc = null;
		String xml = this.comunicazioneFlusso.getXmlDoc();
		if(StringUtils.isNotEmpty(xml)) {
			try {
				doc = RiepilogoBusteOffertaDocument.Factory.parse(xml);
			} catch(XmlException e) {
				doc = null;
			}
		}
		return doc; 
	}

	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	@Override
	public XmlObject getBustaDocument() throws XmlException {
		XmlObject doc = null;
		if(this.gestioneBuste.isInvioOfferta()) {
			doc = this.getRiepilogoOffertaDocument();
		} else {
			doc = this.getRiepilogoPartecipazioneDocument();
		}
		return doc;
	}
	
	/**
	 * Restituisce il pdf di riepilogo delle buste da allegare alla FS11/FS10
	 * In caso di marcatura temporale restituisce il file con marcatura temporale 
	 * @throws Exception 
	 */
	public byte[] getPdfRiepilogoBuste() throws Exception {
		byte[] fileRiepilogo = null;

		EncodedDataAction action = (EncodedDataAction)GestioneBuste.getAction();
		IAppParamManager appParamManager = this.gestioneBuste.getAppParamManager();
		BustaPartecipazione bustaPartecipazione = this.gestioneBuste.getBustaPartecipazione();
		ComunicazioneType comunicazionePartecipazione = bustaPartecipazione.comunicazioneFlusso.getComunicazione();
		
		// verifica se tra gli allegati della partecipazione 
		// e' gia' presente il pdf di riepilogo (marcato o meno)...
		boolean existPdfRiepilogo = false;
		boolean existsFileMarcatura = false;
		AllegatoComunicazioneType[] allegati = comunicazionePartecipazione.getAllegato();
		for (int i = 0; i < allegati.length; i++) {
			if(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE.equalsIgnoreCase(allegati[i].getNomeFile()) ||				
			   PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE.equalsIgnoreCase(allegati[i].getNomeFile())) 
			{
				fileRiepilogo = allegati[i].getFile();
				existPdfRiepilogo = true;
				existsFileMarcatura = PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE.equalsIgnoreCase(allegati[i].getNomeFile());
				break;
			}
		}

		// se non esiste il pdf di riepilogo, lo creo...
		if( !existPdfRiepilogo ) {
			fileRiepilogo = this.createPdfRiepilogoBuste();
		}

		// se e' attiva la marcatura temporale, 
		// applico la marcatura temporale al pdf di riepilogo...
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
			.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
					 ServletActionContext.getRequest());
		boolean marcaturaTemporaleAttiva = customConfigManager.isActiveFunction("INVIOFLUSSI", "MARCATEMPORALE"); 
		if(marcaturaTemporaleAttiva) {
			if( !existsFileMarcatura ) {
				// crea un nuovo file di riepilogo...
				try {
					//DigitalTimeStampResult resultMarcatura = this.getFileMarcatoTemporalmente(riepilogo);					
					DigitalTimeStampResult resultMarcatura = MarcaturaTemporaleFileUtils.eseguiMarcaturaTemporale(fileRiepilogo, appParamManager);
					if( !resultMarcatura.getResult() ) {
						ApsSystemUtils.getLogger().error(
								"Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage=" + resultMarcatura.getErrorMessage(),
								new Object[] { this.username, "marcaturaTemporale" });
						throw new ApsException("Errore in fase di marcatura temporale. ErrorCode = " + resultMarcatura.getErrorCode() + " ErrorMessage=" + resultMarcatura.getErrorMessage());
					}
					fileRiepilogo = resultMarcatura.getFile();
				} catch(Exception e) {
					ApsSystemUtils.logThrowable(e, this, "marcaturaTemporale");
					action.addActionError(action.getText("Errors.marcatureTemporale.generic")); 
					action.setTarget(CommonSystemConstants.PORTAL_ERROR);
					throw e;
				}
			}
		}
			
		// aggiungi il nuovo allegato alla comunicazione di partecipazione FS11/FS10...
		if( !existPdfRiepilogo ) {
			AllegatoComunicazioneType pdfRiepilogo = new AllegatoComunicazioneType();
			pdfRiepilogo.setFile(fileRiepilogo);
			if(marcaturaTemporaleAttiva) {
				pdfRiepilogo.setNomeFile(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE);
				pdfRiepilogo.setTipo("tsd");
				pdfRiepilogo.setDescrizione("File di riepilogo allegati con marcatura temporale");
			} else {
				pdfRiepilogo.setNomeFile(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE);
				pdfRiepilogo.setTipo("pdf");
				pdfRiepilogo.setDescrizione("File di riepilogo allegati");
			}
			
			// aggiungo il file agli allegati della comunicazione
			AllegatoComunicazioneType[] newAllegati = new AllegatoComunicazioneType[allegati.length + 1];
			for (int i = 0; i < allegati.length; i++) {
				newAllegati[i] = allegati[i];
			}
			newAllegati[allegati.length] = pdfRiepilogo;
			comunicazionePartecipazione.setAllegato(newAllegati);
			
			fileRiepilogo = pdfRiepilogo.getFile();
		}
		
		return fileRiepilogo;
	}

	/**
	 * crea il pdf di riepilogo dei documenti delle buste (preq, amm, tec, eco)  
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private byte[] createPdfRiepilogoBuste() 
		throws DocumentException, IOException 
	{
		StringBuilder sb = new StringBuilder();

		boolean garaLotti = (this.helper.getBusteEconomicheLotti() != null || this.helper.getBusteTecnicheLotti() != null); 
		if( !garaLotti ) {
			// gara NO lotti
			Map<String, RiepilogoBustaBean> riepilogoBuste = new HashMap<String, RiepilogoBustaBean>();
			riepilogoBuste.put("BUSTA PREQUALIFICA", this.helper.getBustaPrequalifica());
			riepilogoBuste.put("BUSTA AMMINISTRATIVA", this.helper.getBustaAmministrativa());
			riepilogoBuste.put("BUSTA TECNICA", this.helper.getBustaTecnica());
			riepilogoBuste.put("BUSTA ECONOMICA", this.helper.getBustaEconomica());
			
			for(String tipologiaBusta : riepilogoBuste.keySet()) {
				if(riepilogoBuste.get(tipologiaBusta) != null) {
					String digestBusta = this.getDigestFileBusta(riepilogoBuste.get(tipologiaBusta));
					if(digestBusta != null) {
						sb.append(tipologiaBusta);
						sb.append("\n");
						sb.append(digestBusta);
					}
				}
			}
		} else {
			// gara A LOTTI
			// digest della busta di prequalifica
			String digestBustaPrequalifica = this.getDigestFileBusta(this.helper.getBustaPrequalifica());
			if(digestBustaPrequalifica != null) {
				sb.append("BUSTA PREQUALIFICA");
				sb.append("\n");
				sb.append(digestBustaPrequalifica);
			}
			
			// digest della buste amministrativa 
			String digestBustaAmministrativa = this.getDigestFileBusta(this.helper.getBustaAmministrativa());
			if(digestBustaAmministrativa != null) {
				sb.append("BUSTA AMMINISTRATIVA");
				sb.append("\n");
				sb.append(digestBustaAmministrativa);
			}

			// digest delle buste tecniche
			for(int i = 0; i < this.helper.getListaCompletaLotti().size(); i++) {
				String lotto = this.helper.getListaCompletaLotti().get(i);
				RiepilogoBustaBean bustaTecnicaLotto = this.helper.getBusteTecnicheLotti().get(lotto); 
				if (bustaTecnicaLotto != null) {
					String digest = this.getDigestFileBusta(bustaTecnicaLotto);
					if (digest != null) {
						sb.append("BUSTA TECNICA PER IL LOTTO " + lotto);
						sb.append("\n");
						sb.append(digest);
					}
				}
			}

			// digest delle buste economiche
			for(int i = 0; i < this.helper.getListaCompletaLotti().size(); i++) {
				String lotto = this.helper.getListaCompletaLotti().get(i);
				RiepilogoBustaBean bustaEconomicaLotto = this.helper.getBusteEconomicheLotti().get(lotto);
				if (bustaEconomicaLotto != null) {
					String digest = this.getDigestFileBusta(bustaEconomicaLotto);
					if (digest != null) {
						sb.append("BUSTA ECONOMICA PER IL LOTTO " + lotto);
						sb.append("\n");
						sb.append(digest);
					}
				}
			}
			
//			if(digestBusteTecniche.size() > 0) {
//				List<String> sortedKeys = new ArrayList<String>(digestBusteTecniche.keySet());
//				Collections.sort(sortedKeys);
//				for(String lotto: sortedKeys) {
//					sb.append("\n");
//					sb.append("BUSTA TECNICA PER IL LOTTO " + lotto);
//					sb.append("\n");
//					sb.append(new String(digestBusteTecniche.get(lotto)));
//				}
//			}
//			
//			if(digestBusteEconomiche.size() > 0) {
//				List<String> sortedKeys=new ArrayList<String>(digestBusteEconomiche.keySet());
//				Collections.sort(sortedKeys);
//				for(String lotto: sortedKeys) {
//					sb.append("\n");
//					sb.append("BUSTA ECONOMICA PER IL LOTTO " + lotto);
//					sb.append("\n");
//					sb.append(new String(digestBusteEconomiche.get(lotto)));
//				}
//			}
		}
				
		byte[] contenutoPdf = UtilityStringhe.string2Pdf(sb.toString());
		return contenutoPdf;
	}
	
	/**
	 * crea un allegato con l'elenco degli SHA1 e dei relativi filename associati ai documenti del riepilogo   
	 */
	public String getDigestFileBusta(RiepilogoBustaBean busta) {
		String contenuto = null;
		StringBuilder sb = new StringBuilder();
		if(busta != null && busta.getDocumentiInseriti().size() > 0) {
			for (DocumentoAllegatoBean allegato : busta.getDocumentiInseriti()) {
				sb.append(allegato.getSha1());
				sb.append(" *");
				sb.append(allegato.getNomeFile());
				sb.append("\n");
			}
			if (sb.length() > 0) {
				// 04/04/2018: si genera il testo solo quando esistono documenti allegati
				sb.append("\n");
				contenuto = sb.toString();
			}
		}
		return contenuto;
	}

}

