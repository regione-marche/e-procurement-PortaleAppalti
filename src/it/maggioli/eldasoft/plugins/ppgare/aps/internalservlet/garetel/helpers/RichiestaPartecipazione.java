package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.RiepilogoBusteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.DocumentazioneRichiestaGaraPlicoUnico;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * Gestione invio della richiesta di partecipazione 
 * e delle relative comunicazioni FS10/FS11 e FS10R/FS11R 
 */
public class RichiestaPartecipazione {
	
	private static final String ID_LISTA_COMPLETA_LOTTI = "listaCompletaLotti";
	
	private IComunicazioniManager comunicazioniManager;
	private IBandiManager bandiManager;
	private IEventManager eventManager;
	private INtpManager ntpManager;	
	private BaseAction action;
	private String actionError; 
	private String actionTarget;
			
	// indica se è stata creata una nuova busta di riepilogo
	private boolean saveHelperToSession;
	private RiepilogoBusteHelper bustaRiepilogativa;		 			

	
	public RiepilogoBusteHelper getBustaRiepilogativa() {
		return bustaRiepilogativa;
	}
	
	public String getActionError() {
		return actionError;
	}

	public String getActionTarget() {
		return actionTarget;
	}

	
	/**
	 * costruttore 
	 */
	public RichiestaPartecipazione(
			BaseAction action,
			boolean saveHelperToSession) 
	{
		this.comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER, ServletActionContext.getRequest());
		this.bandiManager = (IBandiManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());
		this.eventManager = (IEventManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.EVENTI_MANAGER, ServletActionContext.getRequest());
		this.ntpManager = (INtpManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.NTP_MANAGER, ServletActionContext.getRequest());
		this.action = action;
		this.saveHelperToSession = saveHelperToSession;				
		this.bustaRiepilogativa = null; 		
		this.actionError = null;  
		this.actionTarget = null;
	}
	
	/**
	 * invio della richiesta partecipazione (FS11,FS11R o FS10,FS10R) 
	 * 
	 * @throws Throwable 
	 */
	public boolean send(
			String codice,
			WizardPartecipazioneHelper partecipazione,				
			RiepilogoBusteHelper bustaRiepilogativa,
			WizardDatiImpresaHelper impresa) 
		throws Throwable  
	{
		boolean inviata = false;
		
		this.actionError = null;  
		this.actionTarget = null;
		
		String nomeOperazione = null;
		switch (partecipazione.getTipoEvento()) {
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
				nomeOperazione = this.action.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_PARTECIPAZIONE");
				break;
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
				nomeOperazione = this.action.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_OFFERTA");
				break;
			case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
				nomeOperazione = this.action.getI18nLabel("BUTTON_DETTAGLIO_GARA_COMPROVA_REQUISITI");
				break;
		}
		
		// si calcola il timestamp ntp
		Date dataPresentazione = null;		
		try {
			dataPresentazione = this.ntpManager.getNtpDate();
		} catch (SocketTimeoutException e) {
			this.actionError =  this.action.getText("Errors.ntpTimeout", new String[] { nomeOperazione });
			this.actionTarget = com.opensymphony.xwork2.Action.INPUT;
		} catch (UnknownHostException e) {
			this.actionError =  this.action.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione });
			this.actionTarget = com.opensymphony.xwork2.Action.INPUT;
		} catch (ApsSystemException e) {
			this.actionError = this.action.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione });
			this.actionTarget = com.opensymphony.xwork2.Action.INPUT;
		}
		
		// si prosegue solo se il timestamp ntp e' stato calcolato
		// oppure non serve calcolarlo
		if (dataPresentazione != null) {
			if (partecipazione.getDataScadenza() != null
				&& dataPresentazione.compareTo(partecipazione.getDataScadenza()) > 0) 
			{
				this.actionError =  this.action.getText("Errors.richiestaFuoriTempoMassimo");
				this.actionTarget = CommonSystemConstants.PORTAL_ERROR;				
			} else {				
				partecipazione.setDataPresentazione(dataPresentazione);
				
				this.bustaRiepilogativa = bustaRiepilogativa;
				
				List<String> listaCompletaLotti = partecipazione.getLottiAmmessiInOfferta();
				
				Map session = ActionContext.getContext().getSession();
				
				// se gli helper vengono gestiti in sessione 
				// recuperali dalla sessione		
				if(this.saveHelperToSession) {
					bustaRiepilogativa = (RiepilogoBusteHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
					impresa = (WizardDatiImpresaHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
					listaCompletaLotti = (ArrayList<String>) session.get(ID_LISTA_COMPLETA_LOTTI);
				}
				
				String tipoComunicazione = (partecipazione.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA)
						? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT 
						: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
							 		
				boolean domandaPartecipazione = ( partecipazione.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA );
				boolean inviaOfferta = ( partecipazione.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA );
				
				if( partecipazione.isGaraTelematica() ) {
					// CREAZIONE/RIALLINEAMENTO BUSTA RIEPILOGATIVA
					// PER LOTTI CONTESTUALMENTE AL SALVATAGGIO 
					// DELLA FS11/FS10 (OFFERTE | DOMANDE PARTECIPAZIONE) 
					if((bustaRiepilogativa == null) || (!codice.equals(bustaRiepilogativa.getCodiceGara()))) {
						// creo un riepilogo completamente nuovo...
						
						if(this.saveHelperToSession) {
							session.remove(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
						}
						
						bustaRiepilogativa = new RiepilogoBusteHelper(
								this.bandiManager, 
								codice,
								null,
								impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
								partecipazione.isRti(), 
								partecipazione.isPlicoUnicoOfferteDistinte(), 
								tipoComunicazione, 
								this.action.getCurrentUser().getUsername(),
								partecipazione.getProgressivoOfferta());
						
						this.bustaRiepilogativa = bustaRiepilogativa;
						
						if(this.saveHelperToSession) {
							session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA, bustaRiepilogativa);
							session.put(ID_LISTA_COMPLETA_LOTTI, bustaRiepilogativa.getListaCompletaLotti());										
		
						}
						//this.nuovaBustaRiepilogo = true;				
					}
				}
				
				if( partecipazione.isGaraTelematica() && partecipazione.isPlicoUnicoOfferteDistinte() ) 
				{
					// recupero la lista dei lotti selezionati 				
					List<String> lottiSelezionati = new ArrayList<String>();
					Iterator<String> lottiSelezionatiIterator = partecipazione.getLotti().iterator();
					while(lottiSelezionatiIterator.hasNext()) {
						lottiSelezionati.add(lottiSelezionatiIterator.next());
					}
		
					// recupero tutta la documentazione comune ai vari lotti 
					DocumentazioneRichiestaGaraPlicoUnico documentazionePlicoUnico = 
						this.bandiManager.getDocumentiRichiestiBandoGaraPlicoUnico(
								codice, 
								impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
								partecipazione.isRti());
					
					if(listaCompletaLotti == null) {
						listaCompletaLotti = bustaRiepilogativa.retrieveListaLotti(
								this.bandiManager, 
								codice, 
								this.action.getCurrentUser().getUsername());
					}
					
					// devo apportare modifiche a un riepilogo 
					// già esistente relativamente ai lotti
					
					//if(domandaPartecipazione) {
					//	for(int i = 0; i < listaCompletaLotti.size();i++) {
					//		String codiceLotto = listaCompletaLotti.get(i);
					//	}
					//}
					
					if(inviaOfferta) {
						for(int i = 0; i < listaCompletaLotti.size(); i++) {
							String codiceLotto = listaCompletaLotti.get(i);
							
							boolean isLottoPresente = 
								bustaRiepilogativa.getBusteEconomicheLotti().get(codiceLotto) != null
								|| bustaRiepilogativa.getBusteTecnicheLotti().get(codiceLotto) != null;
							
							if( !lottiSelezionati.contains(codiceLotto) && isLottoPresente ) {
								// Lotto rimosso rispetto a configurazione precedente 												
								// NB: in caso di costo fisso il lotto economico non c'e' 
								bustaRiepilogativa.getListaLottiRimossi().add(codiceLotto);												
								bustaRiepilogativa.getBusteTecnicheLotti().put(codiceLotto, null);
								bustaRiepilogativa.getBusteEconomicheLotti().put(codiceLotto, null);
								
								// recupero la comunicazione per la busta tecnica/economica
								Long idComunicazioneTecnica = null;
								Long idComunicazioneEconomica = null;
								
								DettaglioComunicazioneType comunicazioneTecnicaPerLotto = (ComunicazioniUtilities.retrieveComunicazione(
										this.comunicazioniManager,
										this.action.getCurrentUser().getUsername(),
										codiceLotto, 
										partecipazione.getProgressivoOfferta(),
										CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA));
								if(comunicazioneTecnicaPerLotto != null) {
									idComunicazioneTecnica = comunicazioneTecnicaPerLotto.getId();
								}
								
								DettaglioComunicazioneType comunicazioneEconomicaPerLotto = (ComunicazioniUtilities
										.retrieveComunicazione(
											this.comunicazioniManager,
											this.action.getCurrentUser().getUsername(),
											codiceLotto, 
											partecipazione.getProgressivoOfferta(),
											CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
											PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA));
								if(comunicazioneEconomicaPerLotto != null) {
									idComunicazioneEconomica = comunicazioneEconomicaPerLotto.getId();
								}
		
								// se la comunicazione esiste allora la elimino
								if(idComunicazioneTecnica != null) {
									this.comunicazioniManager.deleteComunicazione(
											CommonSystemConstants.ID_APPLICATIVO, idComunicazioneTecnica);
								}
								if(idComunicazioneEconomica != null) {
									this.comunicazioniManager.deleteComunicazione(
											CommonSystemConstants.ID_APPLICATIVO, idComunicazioneEconomica);
								}
							} else if( lottiSelezionati.contains(codiceLotto) && !isLottoPresente ) { 
								// lotto inserito rispetto a configurazione precedente
								// sono andata a testare la busta economica in quanto
								// NB: in caso di costo fisso il lotto economico non c'e'
								if(this.bandiManager.isGaraConOffertaTecnica(codiceLotto)) {
									RiepilogoBustaBean bustaTecnica = bustaRiepilogativa.recuperaDocumentazioneLotto(
											documentazionePlicoUnico.getLotti(), 
											codiceLotto, 
											PortGareSystemConstants.BUSTA_TECNICA);
									if(bustaTecnica != null) {
										bustaRiepilogativa.getBusteTecnicheLotti().put(codiceLotto, bustaTecnica);
									}
								}
								
								// in caso di OEPV con costofisso, 
								// il lotto non è attivo...
								RiepilogoBustaBean bustaEconomica = bustaRiepilogativa.recuperaDocumentazioneLotto(
										documentazionePlicoUnico.getLotti(), 
										codiceLotto, 
										PortGareSystemConstants.BUSTA_ECONOMICA);
								if(bustaEconomica != null) {
									bustaRiepilogativa.getBusteEconomicheLotti().put(codiceLotto, bustaEconomica);
								}
							}
						} //for(int i = 0; i < listaCompletaLotti.size();i++) 
					} // if(richestaOfferta)
				} // if( isGaraTelematica() && isPlicoUnicoOfferteDistinte() )
					
		
				if (partecipazione.getIdComunicazioneTipoPartecipazione() != null
					&& this.comunicazioniManager.isComunicazioneProcessata(
							CommonSystemConstants.ID_APPLICATIVO,
							partecipazione.getIdComunicazioneTipoPartecipazione())) 
				{
					// si sbianca l'id comunicazione in quanto
					// si deve procedere con una nuova
					// comunicazione dato che quella che si deve 
					// aggiornare nel frattempo e' stata processata
					partecipazione.setIdComunicazioneTipoPartecipazione(null);
				}
		
				// INVIO COMUNICAZIONE FS11 / FS10
				if(this.sendComunicazione(partecipazione, codice)) {
										
					// Testata comunicazione offerta (FS11R) / partecipazione (FS10R)
					if( partecipazione.isGaraTelematica()
						&& partecipazione.isPlicoUnicoOfferteDistinte() ) 
					{
						bustaRiepilogativa.setListaCompletaLotti(listaCompletaLotti);
			
						// Invio comunicazione offerta (FS11R) / partecipazione (FS10R)
						String RICHIESTA_TIPO_RIEPILOGO = null;
						if(domandaPartecipazione) {
							RICHIESTA_TIPO_RIEPILOGO = PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO;
						}
						if(inviaOfferta) {
							RICHIESTA_TIPO_RIEPILOGO = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO;
						}
						if(RICHIESTA_TIPO_RIEPILOGO != null) {
							bustaRiepilogativa.sendComunicazioneBusta(
									CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
									impresa, 
									this.action.getCurrentUser().getUsername(), 
									codice, 
									impresa.getDatiPrincipaliImpresa().getRagioneSociale(), 
									this.comunicazioniManager,
									RICHIESTA_TIPO_RIEPILOGO,
									this.action);
						}
					}

					inviata = true;
				} 				
			}
		}
		
		return inviata;
	}
	
	/**
	 * Effettua l'invio della comunicazione al backoffice costituita da una
	 * testata e da un allegato contenente al suo interno l'XML dei dati inseriti,
	 * e poi ripulisce la sessione dai dati raccolti ed imposta la chiave del
	 * bando oggetto della partecipazione per poter ritornare al suo dettaglio
	 *
	 * @param partecipazioneHelper contenitore dei dati inseriti nel wizard
	 * 
	 * @throws IOException
	 * @throws ApsException
	 */
	private boolean sendComunicazione(
			WizardPartecipazioneHelper partecipazione, 
			String codice) 
		throws Throwable 
	{
		boolean inviata = false;
		
		String stato = (partecipazione.isGaraTelematica()
				? CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA
				: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		
		String tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE;
		if (partecipazione.isGaraTelematica()) {
			if(partecipazione.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
				tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
			}
			if(partecipazione.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
				tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
			}
		}
	
		Event evento = new Event();
		evento.setUsername(this.action.getCurrentUser().getUsername());
		evento.setIpAddress(this.action.getCurrentUser().getIpAddress());
		evento.setSessionId(this.action.getRequest().getSession().getId());
		evento.setDestination(codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setMessage(
				"Invio comunicazione " + tipoComunicazione
				+ " con id " + partecipazione.getIdComunicazioneTipoPartecipazione()
				+ " in stato " + stato
				+ " con timestamp ntp " + UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(),
										   						   UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
	
		// Invio della comunicazione
		try {
			// estrazione dei parametri necessari per i dati di testata della comunicazione
			// sicuramente lo username si estrae, in quanto l'utente per arrivare
			// qui deve essere loggato e la sessione non puo' essere scaduta
			String username = this.action.getCurrentUser().getUsername();
			String ragioneSociale = partecipazione.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
			String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
			
			String oggetto = MessageFormat.format(
						this.action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_OGGETTO"),
						new Object[] {username, partecipazione.getIdBando()});
			String testo = MessageFormat.format(
						this.action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_TESTO"),
						new Object[] {ragioneSociale200});
			String descrizioneFile = 
						this.action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_ALLEGATO_DESCRIZIONE");
			
			// FASE 1: costruzione del contenitore della comunicazione
			ComunicazioneType comunicazione = new ComunicazioneType();
	
			evento.setMessage(
					"Invio comunicazione " + tipoComunicazione
					+ " in stato " + stato
					+ " con timestamp ntp " + UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(),
											   						   UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			
			// FASE 2: popolamento della testata
			DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.createDettaglioComunicazione(
							partecipazione.getIdComunicazioneTipoPartecipazione(),
							username,
							partecipazione.getIdBando(),
							partecipazione.getProgressivoOfferta(),
							ragioneSociale,
							stato,
							oggetto, 
							testo, 
							tipoComunicazione, 
							null);
			comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
	
			// FASE 3: popolamento dell'allegato con l'xml dei dati da inviare
			setAllegatoComunicazione(
					comunicazione,
					partecipazione.getXmlDocumentTipoPartecipazione(),
					PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE,
					descrizioneFile);
	
			// FASE 4: invio comunicazione
			comunicazione.getDettaglioComunicazione().setId(comunicazioniManager.sendComunicazione(comunicazione));
			
			evento.setMessage(
					"Invio comunicazione " + tipoComunicazione
					+ " con id "  + comunicazione.getDettaglioComunicazione().getId() 
					+ " in stato " + stato
					+ " e timestamp ntp "
					+ UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(),
											   UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
	
		} catch (Throwable e) {
			evento.setError(e);
			throw e;
		} finally {
			if(!CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(stato)){
				eventManager.insertEvent(evento);
			}
		}		
	
		// FASE 5: pulizia e impostazione navigazione futura
		// se tutto e' andato a buon fine si eliminano le informazioni
		// dalla sessione ...
		// this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		// this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
		// ...e si imposta il codice del bando per la riapertura del
		// dettaglio
//		this.setCodice(partecipazione.getIdBando());
		inviata = true;

		// FASE 6: settaggio della protezione in modo da bloccare eventuali
		// nuovi invii con gli stessi dati
		partecipazione.setDatiInviati(true);
		
		return inviata;
	}
	
	/**
	 * imposta gli allegati della comunicazione 
	 */
	private void setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			XmlObject xmlDocument,
			String nomeFile, 
			String descrizioneFile) throws IOException 
	{
		AllegatoComunicazioneType allegato = ComunicazioniUtilities.createAllegatoComunicazione(
				nomeFile,
				descrizioneFile, 
				xmlDocument.toString().getBytes());
		comunicazione.setAllegato(new AllegatoComunicazioneType[]{allegato});
	}

}
