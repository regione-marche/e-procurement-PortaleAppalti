package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoLottoBustaType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.DocumentazioneRichiestaGaraPlicoUnico;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

public class OpenGestioneBusteDistinteAction extends OpenGestioneBusteAction implements SessionAware{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -1913545349959529280L;
	
	private static final String SESSION_ID_WIZARD_OFFERTA = "wizardOfferta";
	private static final String SESSION_ID_DETTAGLIO_GARA = "dettaglioGara";
	
	private boolean almenoUnaBustaTecnica;
	private boolean almenoUnaBustaEconomica;
	private HashMap<String, Boolean> wizardOfferta = new LinkedHashMap<String, Boolean>();
	private boolean nessunDocPerPreq;
	private boolean nessunDocPerAmm;
	private boolean nessunDocPerTec;
	private boolean nessunDocPerEco;
	private Long lottiSelezionati;
	
	public boolean isAlmenoUnaBustaTecnica() {
		return almenoUnaBustaTecnica;
	}

	public void setAlmenoUnaBustaTecnica(boolean almenoUnaBustaTecnica) {
		this.almenoUnaBustaTecnica = almenoUnaBustaTecnica;
	}

	public boolean isAlmenoUnaBustaEconomica() {
		return almenoUnaBustaEconomica;
	}

	public void setAlmenoUnaBustaEconomica(boolean almenoUnaBustaEconomica) {
		this.almenoUnaBustaEconomica = almenoUnaBustaEconomica;
	}

	public HashMap<String, Boolean> getWizardOfferta() {
		return wizardOfferta;
	}

	public void setWizardOfferta(HashMap<String, Boolean> wizardOfferta) {
		this.wizardOfferta = wizardOfferta;
	}

	public boolean isNessunDocPerPreq() {
		return nessunDocPerPreq;
	}

	public void setNessunDocPerPreq(boolean nessunDocPerPreq) {
		this.nessunDocPerPreq = nessunDocPerPreq;
	}

	public boolean isNessunDocPerAmm() {
		return nessunDocPerAmm;
	}

	public void setNessunDocPerAmm(boolean nessunDocPerAmm) {
		this.nessunDocPerAmm = nessunDocPerAmm;
	}

	public boolean isNessunDocPerTec() {
		return nessunDocPerTec;
	}

	public void setNessunDocPerTec(boolean nessunDocPerTec) {
		this.nessunDocPerTec = nessunDocPerTec;
	}

	public boolean isNessunDocPerEco() {
		return nessunDocPerEco;
	}

	public void setNessunDocPerEco(boolean nessunDocPerEco) {
		this.nessunDocPerEco = nessunDocPerEco;
	}
	
	public Long getLottiSelezionati() {
		return lottiSelezionati;
	}

	public void setLottiSelezionati(Long lottiSelezionati) {
		this.lottiSelezionati = lottiSelezionati;
	}

	
	public int getBUSTA_PRE_QUALIFICA() {
		return PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
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
	
	
	/**
	 * Apertura pagina di gestione delle offerte a buste distinte
	 *
	 * @return
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// pulizia della sessione dall'accesso ad una gara precedente 
				// che potrebbe essere stata a offerta unica
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
				this.session.remove(SESSION_ID_DETTAGLIO_GARA);
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				
				if(StringUtils.stripToNull(this.codice) != null) {
					// fatto così per gestire il ritorno all'openGestioneBusteDistinte 
					// da wizard inizio compilazione offerta
					this.codiceGara = this.getCodice();
				}
				
				// numero dei lotti selezionati in partecipazione
				this.lottiSelezionati = 0L;
				
				boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
				boolean invioOfferta = !domandaPartecipazione; //(this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
				
				String RICHIESTA_TIPO_RIEPILOGO = (domandaPartecipazione)
					? PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO
					: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO;
				
				// ----- HELPERS GENERICI ----- 
				this.setComunicazioneBustaRie(ComunicazioniUtilities.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codiceGara,
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						RICHIESTA_TIPO_RIEPILOGO));

				// carica, all'accesso alla funzione, i dati aggiornati dell'impresa
				// --- CESSATI ---
				WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
						this.getCurrentUser().getUsername(), this, this.appParamManager);
				
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA,
						 		 datiImpresaHelper);

				DettaglioGaraType gara = this.bandiManager.getDettaglioGara(this.codiceGara);
				this.session.put(SESSION_ID_DETTAGLIO_GARA, gara);
				this.setOffertaTelematica(gara.getDatiGeneraliGara().isOffertaTelematica());
				this.setOffertaTecnica((boolean) this.bandiManager.isGaraConOffertaTecnica(this.codiceGara));
				
				boolean invioOffertaGaraRistretta = invioOfferta
					&& WizardPartecipazioneHelper.isGaraRistretta(gara.getDatiGeneraliGara().getIterGara());
				
				String tipoComunicazione = (domandaPartecipazione)
						? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT
						: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
				
				this.setComunicazione(ComunicazioniUtilities.retrieveComunicazione(
						this.comunicazioniManager,
						this.getCurrentUser().getUsername(),
						this.codiceGara, 
						this.progressivoOfferta,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						tipoComunicazione));
				
				if(this.getComunicazione() == null) {
					// Caso di errore invio mail -> la FS10/FS11 risulteranno in stato da protocollare, 
					// mentre le altre in bozza =< devo abilitare unicamente l'invio dell'offerta
					this.setComunicazione(ComunicazioniUtilities.retrieveComunicazione(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(),
							this.codiceGara,
							this.progressivoOfferta,
							CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
							tipoComunicazione));
				}
				
				WizardPartecipazioneHelper wizardPartecipazione = (WizardPartecipazioneHelper)this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

				// nel caso di una gara ristretta
				// recupero la comunicazione della domanda di partecipazione
				this.setComunicazioneDomandaPartecipazione(null);
				if (invioOffertaGaraRistretta) {
					this.setComunicazioneDomandaPartecipazione(
							this.retriveComunicazioneDomandaPartecipazione(this.codiceGara));
				}
				
				if (wizardPartecipazione == null) {
					// ogni volta che si ripassa per la gestione buste, se non è
					// presente il wizard di partecipazione lo si ricarica
					// completando la sola parte relativa all'XML estratto
					// e l'editabilita' dei dati RTI

					// si cerca l'eventuale tipo di partecipazione se gia' presente in backoffice
					TipoPartecipazioneType tipoPartecipazione = this.bandiManager
							.getTipoPartecipazioneImpresa(
									this.getCurrentUser().getUsername(), 
									this.codiceGara,
									this.progressivoOfferta);

					// NB: il wizard di partecipazione si elimina dalla sessione
					// quando si termina lo step corrispondente, per cui sono
					// costretto a rileggere i dati con questo if
					wizardPartecipazione = this.createPartecipazioneHelper(
							this.codiceGara, 
							this.getOperazione(), 
							gara,
							this.progressivoOfferta,
							tipoPartecipazione);
					
					//wizardPartecipazione.setPlicoUnicoOfferteDistinte(
					//		gara.getDatiGeneraliGara().getTipologia()==PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO );
					wizardPartecipazione.setPlicoUnicoOfferteDistinte(true);
					
					if(this.getComunicazione() != null || this.getComunicazioneDomandaPartecipazione() != null) {
						if(this.getComunicazione() != null) {
							// assegna l'id comunicazione solo se si tratta 
							// della comunicazione di gara. 
							// nel caso di gara ristretta in fase di offerta  
							// se non esiste ancora la comunicazione FS11 
							// NON utilizzare l'id della preesistente
							wizardPartecipazione.setIdComunicazioneTipoPartecipazione(this.getComunicazione().getId());
						}
					
						Long idComunicazione = (this.getComunicazione() != null 
								? this.getComunicazione().getId() 
								: this.getComunicazioneDomandaPartecipazione().getId());
						ComunicazioneType comunicazioneCompleta = 
							this.comunicazioniManager.getComunicazione(
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
						// e la denominazione dell'RTI viene mantenuta anche per l'offerta
						if( tipoPartecipazione != null && tipoPartecipazione.isRti() ) {
							wizardPartecipazione.setEditRTI( !tipoPartecipazione.isRti() );
							wizardPartecipazione.setRtiPartecipazione( tipoPartecipazione.isRti() );
						}
					}
					
					// aggiorna il numero dei lotti selezionati in partecipazione
					if(wizardPartecipazione != null) {
						if(wizardPartecipazione.getLotti() != null) {
							this.lottiSelezionati = new Long(wizardPartecipazione.getLotti().size());
						}
					}
					
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA,
								     wizardPartecipazione);
				}
				
				this.nessunDocPerPreq = true;
				this.nessunDocPerAmm = true;
				this.nessunDocPerTec = true;
				this.nessunDocPerEco = true;
				this.setProtocollazioneMailFallita(false);
				this.setBustaAmmAlreadySent(false);
				this.setBustaTecAlreadySent(false);
				this.setBustaEcoAlreadySent(false);
				
				DettaglioComunicazioneType comunicazioneDaProtocollare = ComunicazioniUtilities
						.retrieveComunicazione(
								this.comunicazioniManager,
								this.getCurrentUser().getUsername(),
								this.codiceGara,
								wizardPartecipazione.getProgressivoOfferta(),
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE,
								tipoComunicazione);
			
				if(comunicazioneDaProtocollare != null){
					this.setProtocollazioneMailFallita(this.comunicazioniManager.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, comunicazioneDaProtocollare.getId())!= null);
				}
				
				RiepilogoBusteHelper bustaRiepilogativa  = null;
				List<String> lottiAttivi = new ArrayList<String>();
				if(wizardPartecipazione != null) {
					bustaRiepilogativa = (RiepilogoBusteHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
					
					// verifica se l'helper della busta di riepilogo è sincronizzato
					boolean riepilogoHelperRefresh = 
						getComunicazioneBustaRie() == null || 
						bustaRiepilogativa == null ||
					    (bustaRiepilogativa != null && !bustaRiepilogativa.getCodiceGara().equals(this.getCodiceGara())) ||							// codice gara diverso
					    (bustaRiepilogativa != null && wizardPartecipazione.isRti() && bustaRiepilogativa.getUltimiFirmatariInseriti() == null);	// rti <=> non rti o viceversa
					
					if(riepilogoHelperRefresh) {
						bustaRiepilogativa = new RiepilogoBusteHelper(
								this.bandiManager, 
								this.codiceGara, 
								null,
								datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
								wizardPartecipazione.isRti(), 
								wizardPartecipazione.isPlicoUnicoOfferteDistinte(),
								tipoComunicazione,
								this.getCurrentUser().getUsername(),
								this.progressivoOfferta);
						
						this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA, 
										 bustaRiepilogativa);
					}
					bustaRiepilogativa.setImpresa(datiImpresaHelper);
					
					if(domandaPartecipazione) {
						if(bustaRiepilogativa.getBustaPrequalifica() != null) {
							this.nessunDocPerPreq = bustaRiepilogativa.getBustaPrequalifica().getDocumentiInseriti().isEmpty();
						}
					}
					if(invioOfferta) {
						if(bustaRiepilogativa.getBustaAmministrativa() != null) {
							this.nessunDocPerAmm = bustaRiepilogativa.getBustaAmministrativa().getDocumentiInseriti().isEmpty();
						}
					}

					Iterator<String> lottiAttiviIterator = wizardPartecipazione.getLotti().iterator();
					while(lottiAttiviIterator.hasNext()) {
						lottiAttivi.add(lottiAttiviIterator.next());
					}

					//if(domandaPartecipazione) {
					//	...
					//}
					if(invioOfferta) {
						for(int i = 0; i < lottiAttivi.size(); i++) {
							if(bustaRiepilogativa.getBusteTecnicheLotti() != null) {
								if(bustaRiepilogativa.getBusteTecnicheLotti().get(lottiAttivi.get(i)) != null){
									this.nessunDocPerTec = this.nessunDocPerTec && bustaRiepilogativa.getBusteTecnicheLotti().get(lottiAttivi.get(i)).getDocumentiInseriti().isEmpty();
								}
							}
							if(bustaRiepilogativa.getBusteEconomicheLotti() != null) {
								if(bustaRiepilogativa.getBusteEconomicheLotti().get(lottiAttivi.get(i)) != null){
									this.nessunDocPerEco = this.nessunDocPerEco && bustaRiepilogativa.getBusteEconomicheLotti().get(lottiAttivi.get(i)).getDocumentiInseriti().isEmpty();
								}
							}
						}
					}
					
					// in caso di OEPV con costo fisso il lotto non va abilitato
					// "disabilita" i lotti di OEPV con costo fisso...	
					if(bustaRiepilogativa.getBusteEconomicheLotti() != null) {
						for(int j = 0; j < gara.getLotto().length; j++) {
							if(gara.getLotto(j).getCostoFisso() != null && gara.getLotto(j).getCostoFisso() == 1) {
								// lotto OEPV con costo fisso... 
								bustaRiepilogativa.getBusteEconomicheLotti().remove(gara.getLotto(j).getCodiceLotto());
							}
						}
					}
				}
				
				// verifico se precedentemente è stata inviata una domanda 
				// di partecipazione (gara ristretta) quindi recupero la 
				// comunicazione FS10 per verifica quali lotti sono stati 
				// abilitati
				if (invioOffertaGaraRistretta) {
					List<String> lotti = this.retriveLottiAmmessiGaraRistretta(
							this.codiceGara, 
							gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO,
							wizardPartecipazione);
					if(lotti != null) {
						// preparo l'elenco dei lotti abilitati in fase 
						// di domanda di partecipazione  
						lottiAttivi.clear();
						for(int i = 0; i < lotti.size(); i++) {
							lottiAttivi.add(lotti.get(i));
						}
					}
				}

				boolean integrazioneEffettuata = false;
				
				// se non esiste la comunicazione FS11R/FS10R allora non si 
				// e' ancora confermata lo step di "Inizia compilazione offerta"
				// e il pulsante "Busta economica" non va nascosto, finche'
				// non sono stati confermati i lotti
				if(gara.getLotto().length > 1 && this.getComunicazioneBustaRie() == null) {
					this.setAlmenoUnaBustaEconomica(true);
				}
				
				if(this.getComunicazione() != null) {
					this.setAlmenoUnaBustaTecnica(false);
					this.setAlmenoUnaBustaEconomica(false);
					
					for(int i = 0; i < lottiAttivi.size(); i++) {
						this.wizardOfferta.put(lottiAttivi.get(i), gara.getDatiGeneraliGara().isOffertaTelematica());
					}					
					this.session.put(SESSION_ID_WIZARD_OFFERTA, this.wizardOfferta);
					
					// codice gara 
					// e lista lotti attivati all'ultimo giro
					bustaRiepilogativa.setCodiceGara(this.codiceGara);
					bustaRiepilogativa.setListaCompletaLotti(lottiAttivi);

					// FS11R/FS10R PRESENTE : RILETTURA E RIPOPOLAMENTO 
					// recupero la comunicazione riepilogativa
					ComunicazioneType comunicazioneSalvata = this.comunicazioniManager
							.getComunicazione(
									CommonSystemConstants.ID_APPLICATIVO,
									this.getComunicazioneBustaRie().getId());

					bustaRiepilogativa.setIdComunicazione(comunicazioneSalvata.getDettaglioComunicazione().getId());

					FirmatarioType[] ultimiFirmatariUtilizzati = null;
					
					if(domandaPartecipazione) {
						RiepilogoBustePartecipazioneDocument documento = getBustePartecipazioneDocument(comunicazioneSalvata);
						
						// busta prequalifica
						if(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica() != null) {
							bustaRiepilogativa.getBustaPrequalifica().popolaBusta(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica());
							bustaRiepilogativa.integraBustaPrequalificaFromBO(this.getCodiceGara(), null, this.bandiManager, datiImpresaHelper, wizardPartecipazione);
						}
					}
					
					if(invioOfferta) {
						RiepilogoBusteOffertaDocument documento = getBusteOffertaDocument(comunicazioneSalvata);
						
						ultimiFirmatariUtilizzati = documento.getRiepilogoBusteOfferta().getFirmatarioArray();
						
						// busta amministrativa
						if(documento.getRiepilogoBusteOfferta().getBustaAmministrativa() != null) {
							bustaRiepilogativa.getBustaAmministrativa().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaAmministrativa());
							bustaRiepilogativa.integraBustaAmministrativaFromBO(this.getCodiceGara(), null, this.bandiManager, datiImpresaHelper, wizardPartecipazione);
						}
					
						RiepilogoLottoBustaType[] listaLotti = documento.getRiepilogoBusteOfferta().getLottoArray();						
						DocumentazioneRichiestaGaraPlicoUnico documentazionePlicoUnico = 
							this.bandiManager
								.getDocumentiRichiestiBandoGaraPlicoUnico(
										this.codiceGara, 
										datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
										wizardPartecipazione.isRti());
						
						for(int i = 0; i < listaLotti.length; i++) {
							String codiceLotto = listaLotti[i].getCodiceLotto();
							
							if( !lottiAttivi.contains(codiceLotto) ) {
								// --- Lotto non ammesso ---
								// aggiorna il wizard...
								wizardPartecipazione.getLotti().remove(codiceLotto);
							} else {
								// --- Lotto ammesso ---  
								
								// busta tecnica
								RiepilogoBustaType bustaTecnica = listaLotti[i].getBustaTecnica();
								if(bustaRiepilogativa.getBusteTecnicheLotti() != null) {
									RiepilogoBustaBean bustaTecnicaLotto = bustaRiepilogativa.getBusteTecnicheLotti().get(listaLotti[i].getCodiceLotto());
									integrazioneEffettuata = integrazioneEffettuata || bustaRiepilogativa.integraBustaTecnicaFromBO(bustaTecnicaLotto, this.bandiManager, this.getCodiceGara(), listaLotti[i].getCodiceLotto(), datiImpresaHelper, wizardPartecipazione);
		
									if((boolean) this.bandiManager.isGaraConOffertaTecnica(listaLotti[i].getCodiceLotto())){
										bustaTecnicaLotto = bustaRiepilogativa.recuperaDocumentazioneLotto(
												documentazionePlicoUnico.getLotti(), 
												listaLotti[i].getCodiceLotto(),
												PortGareSystemConstants.BUSTA_TECNICA);
										
										if(StringUtils.isNotEmpty(bustaRiepilogativa.getDocumentazioneMancanteError())) {
											// se non mancano i documenti della busta tecnica segnala l'anomalia
											String msg = this.getText(bustaRiepilogativa.getDocumentazioneMancanteError(),
									 				  				  new String[] {listaLotti[i].getCodiceLotto()});
											this.addActionError(msg); 
											ApsSystemUtils.getLogger().error(msg);
											this.setTarget(CommonSystemConstants.PORTAL_ERROR);	//INPUT
										} else {
											bustaTecnicaLotto.setOggetto(listaLotti[i].getOggetto());
											bustaRiepilogativa.popolaBusteLotti(bustaTecnica, bustaTecnicaLotto);
											this.setAlmenoUnaBustaTecnica(true);
										}
									}
		
									bustaRiepilogativa.getBusteTecnicheLotti().put(listaLotti[i].getCodiceLotto(), bustaTecnicaLotto);
									if(bustaRiepilogativa.getBusteTecnicheLotti().get(codiceLotto) != null){
										this.nessunDocPerTec = this.nessunDocPerTec && bustaRiepilogativa.getBusteTecnicheLotti().get(codiceLotto).getDocumentiInseriti().isEmpty();
									}
		
									if(bustaTecnica != null) {
										bustaRiepilogativa.getPrimoAccessoTecnicheEffettuato().put(listaLotti[i].getCodiceLotto(), bustaTecnica.getPresaVisioneDocumenti());
									}
								}
								
								// busta economica
								RiepilogoBustaType bustaEconomica = listaLotti[i].getBustaEconomica();
								if(bustaRiepilogativa.getBusteEconomicheLotti() != null) {
//									RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(listaLotti[i].getCodiceLotto());
									
									// in caso di OEPV con costofisso, 
									// il lotto non è attivo quindi NULL...  
									RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.recuperaDocumentazioneLotto(
											documentazionePlicoUnico.getLotti(), 
											listaLotti[i].getCodiceLotto(), 
											PortGareSystemConstants.BUSTA_ECONOMICA);
									
									if(StringUtils.isNotEmpty(bustaRiepilogativa.getDocumentazioneMancanteError())) {
										// se non mancano i documenti della busta economica segnala l'anomalia
										String msg = this.getText(bustaRiepilogativa.getDocumentazioneMancanteError(),
												 				  new String[] {listaLotti[i].getCodiceLotto()});
										this.addActionError(msg); 
										ApsSystemUtils.getLogger().error(msg);
										this.setTarget(CommonSystemConstants.PORTAL_ERROR);	//INPUT
									} else if(bustaEconomicaLotto != null) {
										bustaEconomicaLotto.setOggetto(listaLotti[i].getOggetto());
										bustaRiepilogativa.popolaBusteLotti(bustaEconomica, bustaEconomicaLotto);
										integrazioneEffettuata = integrazioneEffettuata || bustaRiepilogativa.integraBustaEconomicaFromBO(bustaEconomicaLotto, this.bandiManager, this.getCodiceGara(), listaLotti[i].getCodiceLotto(), datiImpresaHelper, wizardPartecipazione);
										bustaRiepilogativa.getBusteEconomicheLotti().put(listaLotti[i].getCodiceLotto(),bustaEconomicaLotto);
										this.setAlmenoUnaBustaEconomica(true);
									}
									
									if(bustaRiepilogativa.getBusteEconomicheLotti().get(codiceLotto) != null) {
										this.nessunDocPerEco = this.nessunDocPerEco && bustaRiepilogativa.getBusteEconomicheLotti().get(codiceLotto).getDocumentiInseriti().isEmpty();
									}
									
									if(bustaEconomica != null) {
										bustaRiepilogativa.getPrimoAccessoEconomicheEffettuato().put(listaLotti[i].getCodiceLotto(), bustaEconomica.getPresaVisioneDocumenti());
									}
								}
							}
						}
	
						this.nessunDocPerAmm = bustaRiepilogativa.getBustaAmministrativa().getDocumentiInseriti().isEmpty();
						
						// aggiorna il wizard in sessione
						this.session.put(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA,
										 wizardPartecipazione);
					}

					// --- RIPOPOLAMENTO HINT PER FIRMATARI IN CASO DI RTI ---
					if(wizardPartecipazione.isRti()) {
						if(ultimiFirmatariUtilizzati != null && ultimiFirmatariUtilizzati.length > 0) {
							// ripopola la lista dei firmatari
							bustaRiepilogativa.getUltimiFirmatariInseriti().clear();
							
							for(int i = 0; i < ultimiFirmatariUtilizzati.length; i++) {
								SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper(ultimiFirmatariUtilizzati[i]);
								bustaRiepilogativa.getUltimiFirmatariInseriti().add(firmatario);
							}
							
							integrazioneEffettuata = true;
						}
					}
					
					// verifica se le buste amministrativa, tecnica, economica sono già state inviate
					// ed imposta i flag bustaAmmAlreadySent, bustaTecAlreadySent, BustaEcoAlreadySent
					// per l'abilitazione nel menu dei pulsanti delle buste 
					this.setOffertaTelematica( gara.getDatiGeneraliGara().isOffertaTelematica() );
					this.setOffertaTecnica( (boolean) this.bandiManager.isGaraConOffertaTecnica(this.codiceGara) );
					
					// provo a ricavare la comunicazione per la busta amministrativa
					this.setComunicazioneBustaAmm( retriveComunicazioneBustaAmministrativa(this.codiceGara) );
					
					// NB: per le buste tecniche ed economiche dei lotti 
					// il controllo viene spostato nell'apertura della 
					// lista dei lotti (vedi OpenPageListaBusteDistinteAction) !!!
				}
				
				// ----- aggiornamento FS11R/FS10R post integrazione documenti da BO -----
				if(integrazioneEffettuata) {
					bustaRiepilogativa.sendComunicazioneBusta(
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							datiImpresaHelper, 
							this.getCurrentUser().getUsername(),  
							this.codiceGara,
							datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(), 
							this.comunicazioniManager,
							RICHIESTA_TIPO_RIEPILOGO,		//PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO); ??? BUG ???
							this);
				}
				
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA, bustaRiepilogativa);	

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

}
