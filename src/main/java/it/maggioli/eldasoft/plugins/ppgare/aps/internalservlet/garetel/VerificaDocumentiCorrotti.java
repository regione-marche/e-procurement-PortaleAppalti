package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoLotto;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario.AnomalieQuestionarioInfo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoAllegatoBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aopalliance.aop.AspectException;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;

/**
 *  Verifica se esistono anomalie nei documenti di una busta (amministrativa, tecnica, economica) 
 *  in fase di pre invio di un'offerta di gara
 *
 */
public class VerificaDocumentiCorrotti {
	
	private static final Logger LOG = LoggerFactory.getLogger(VerificaDocumentiCorrotti.class);
	
	private IBandiManager bandiManager;
	
	private IComunicazioniManager comunicazioniManager;
	
	private BustaRiepilogo bustaRiepilogo;
	private GestioneBuste buste;
	private BaseAction action;
	private Event event;
	private boolean verificaBustaSingola;
	
	// gestione errori per i documenti corrotti (corrotti, mancanti, nulli)
	private List<String> busteKeys = new ArrayList<String>();
	
	private LinkedHashMap<String, List<DocumentoAllegatoLotto>> documentiNulli = 
				new LinkedHashMap<String, List<DocumentoAllegatoLotto>>();
	
	private LinkedHashMap<String, List<DocumentoAllegatoLotto>> documentiCorrotti =
				new LinkedHashMap<String, List<DocumentoAllegatoLotto>>();
	
	private LinkedHashMap<String, List<DocumentoAllegatoLotto>> documentiMancanti =
				new LinkedHashMap<String, List<DocumentoAllegatoLotto>>();
	
	private LinkedHashMap<String, List<String>> erroriQuestionari =
				new LinkedHashMap<String, List<String>>();
	
	private List<DocumentoAllegatoLotto> dimensioniDocumenti;	// lista delle dimensione dei documenti presenti su DB

	// cache delle info relative ai documenti in W_DOCDIG, utili per identificare le tipologia delle anomalie (corrotto, nullo, mancante)
	private int lastTipoBusta;
	private List<DocumentoAllegatoLotto> docDimensioniBO;
	private List<DocumentoAllegatoLotto> docNulliBO;

	// gestione errori presa visione e documenti richiesti 
	private boolean erroriPresaVisioneDocRichiesti;		
	private int countVerificaBuste;						// usato per evitare di ricalcolare/aggiungere errori doppi alla action 
	private int countDocumentiTecnicheMancanti;			// usato per evitare di ricalcolare/aggiungere errori doppi alla action
	private int countDocumentiEconomicheMancanti;		// usato per evitare di ricalcolare/aggiungere errori doppi alla action
	
	
	/**
	 * costruttore 
	 */
	public VerificaDocumentiCorrotti(Event evento) {
		event = evento;
		buste = GestioneBuste.getFromSession();
		bustaRiepilogo = buste.getBustaRiepilogo();
		bandiManager = bustaRiepilogo.getGestioneBuste().getBandiManager();
		comunicazioniManager = bustaRiepilogo.getGestioneBuste().getComunicazioniManager();
		action = GestioneBuste.getAction();
		countVerificaBuste = 0;
		countDocumentiTecnicheMancanti = 0;
		countDocumentiEconomicheMancanti = 0;
		verificaBustaSingola = true;
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
			String codiceLotto = ""; 
			String codiceInterno = "";
			if(documento.getLotto() != null) {
				codiceLotto = StringUtils.isNotEmpty(documento.getLotto().getCodiceLotto()) ? documento.getLotto().getCodiceLotto() : "";
				codiceInterno = StringUtils.isNotEmpty(documento.getLotto().getCodiceInterno()) ? documento.getLotto().getCodiceInterno() : "";
			}
			
			// key = [codice lotto];[codice lotto interno]|[tipo busta]
			String key = codiceLotto + ";" + codiceInterno + "|" + tipoBusta;
			
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
	private void addNullo(String codGara, String tipoBusta, DocumentoAllegatoLotto documento) {
		this.add(tipoBusta, documento, this.documentiNulli);
		log("addNullo() {} {}", codGara, documento.getNomefile());
	}
	
	/**
	 * aggiunge un documento corrotto (allegato con dimensione W_DOCDIG.DIGOGG <> dimensione del riepilogo) 
	 */
	private void addCorrotto(String codGara, String tipoBusta, DocumentoAllegatoLotto documento) {
		this.add(tipoBusta, documento, this.documentiCorrotti);
		log("addCorrotto() {} {}", codGara, documento.getNomefile());
	}
	
//	private void addCorrotto(String codGara, String codLotto, String tipoBusta, DocumentoAllegatoBean documento) {
//		DocumentoAllegatoLotto d = new DocumentoAllegatoLotto();
//		d.setDescrizione(documento.getDescrizione());
//		d.setDimensione(documento.getDimensione());
//		d.setId(documento.getId());
//		d.setNomefile(documento.getNomeFile());
//		d.setUuid(documento.getUuid());
//		d.setLotto(null);
//		if(StringUtils.isNotEmpty(codLotto)) {
//			d.setLotto(new LottoGaraType());
//			d.getLotto().setCodiceGara(codGara);
//			d.getLotto().setCodiceLotto(codLotto);
//		}
//		addCorrotto(codGara, tipoBusta, d);
//	}
	
	private void addCorrotto(String codGara, String codLotto, String tipoBusta, Attachment documento) {
		DocumentoAllegatoLotto d = new DocumentoAllegatoLotto();
		d.setDescrizione(documento.getDesc());
		d.setDimensione((documento.getSize() != null ? documento.getSize().longValue() : null));
		d.setId(documento.getId());
		d.setNomefile(documento.getFileName());
		d.setUuid(documento.getUuid());
		d.setLotto(null);
		if(StringUtils.isNotEmpty(codLotto)) {
			d.setLotto(new LottoGaraType());
			d.getLotto().setCodiceGara(codGara);
			d.getLotto().setCodiceLotto(codLotto);
		}
		addCorrotto(codGara, tipoBusta, d);
	}
	
	/**
	 * aggiunge un documento mancante (allegato non presente in W_DOCDIG)
	 */
	private void addMancante(String codGara, String codLotto, String tipoBusta, DocumentoAllegatoBean allegato) {			
		DocumentoAllegatoLotto documento = new DocumentoAllegatoLotto();
		documento.setDescrizione( allegato.getDescrizione() );
		documento.setNomefile( allegato.getNomeFile() );
		documento.setId( allegato.getId() );
		documento.setDimensione( allegato.getDimensione() );
		
		LottoGaraType lotto = new LottoGaraType();
		lotto.setCodiceGara(codGara);
		lotto.setCodiceLotto(codGara);
		lotto.setCodiceInterno("");
		if(StringUtils.isNotEmpty(codLotto) && bustaRiepilogo.getHelper().getListaCodiciInterniLotti() != null) {
			lotto.setCodiceLotto(codLotto);
			lotto.setCodiceInterno( bustaRiepilogo.getHelper().getListaCodiciInterniLotti().get(codLotto) );
		} 
		documento.setLotto(lotto);
		
		this.add(tipoBusta, documento, this.documentiMancanti);
		
		log("addMancante() {} {}", codGara, documento.getNomefile());
	}
	
	private void addMancante(String codGara, String codLotto, String tipoBusta, Attachment allegato) {
		DocumentoAllegatoBean d = new DocumentoAllegatoBean();
		d.setDescrizione(allegato.getDesc());
		d.setDimensione(allegato.getSize() != null ? allegato.getSize().longValue() : null);
		d.setId(allegato.getId());
		d.setNomeFile(allegato.getFileName());
		d.setUuid(allegato.getUuid());
		addMancante(codGara, codLotto, tipoBusta, d);
	}

	/**
	 * aggiunge un errore relativo ad un questionari
	 */
	private void addErroreQuestionario(String codGara, String codLotto, String errore) {			
		String codice = (!codGara.equalsIgnoreCase(codLotto) && StringUtils.isNotEmpty(codLotto) ? codLotto : codGara);
		List<String> errori = erroriQuestionari.get(codice);
		if(errori == null) {
			errori = new ArrayList<String>();
			erroriQuestionari.put(codice, errori);
		}
		errori.add(errore);
		log("addErroreQuestionario() {} {}", codice, errore);
	}	
	
	public boolean isErroriPresenti() {
		return (erroriPresaVisioneDocRichiesti) 
			   || (documentiNulli.size() + documentiCorrotti.size() + documentiMancanti.size() > 0)
			   || (erroriQuestionari.size() > 0);
	}
	
	/**
	 * trace debug message 
	 */
	private void log(String msg, Object... params) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("VerificaDocumentiCorrotti", msg, params);
//			System.out.println("VerificaDocumentiCorrotti." + String.format(msg.replaceAll("\\{\\}", "%s"), params));				
		}
	}	

	/**
	 * aggiunge agli errore della action l'elenco delle anomalie rilevate nei documenti delle buste   
	 */
	public void addActionErrors(EncodedDataAction action, Event event) {
		String eventDetailMessage = "";
		
		// documenti corrotti/mancanti/nulli
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
			eventDetailMessage += lotto + tipoBusta + ": " + 
				(lst1.length() > 0 ? "documenti nulli " : "") + lst1 + " " + 
				(lst2.length() > 0 ? "documenti corrotti " : "") + lst2 + " " + 
				(lst3.length() > 0 ? "documenti mancanti " : "") + lst3 +
				";" + "\n";
			
			// action error message
			action.addActionError( action.getText("Errors.invioBuste.nullDocuments", 
					new String[] { descrBusta + (StringUtils.isNotEmpty(lotto2) ? " lotto " + lotto2 : "") }) + " " +
					lst1 + lst2 + lst3);
		}
		
		// Questionari - QFORM
		if(erroriQuestionari.size() > 0) {
			Iterator i = erroriQuestionari.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry item = (Map.Entry) i.next();
				List<String> errori = (List<String>)item.getValue();
				if(errori != null) {
					for(String msg : errori) {
						eventDetailMessage += (StringUtils.isNotEmpty(eventDetailMessage) ? "\n" : "") + msg;						
						action.addActionError(msg);
					}
				}
			}
		}

		// aggiorna la tracciatura dell'evento
		if(event != null) {
			if(StringUtils.isNotEmpty(eventDetailMessage)) { 
				event.setDetailMessage(eventDetailMessage);
				event.setLevel(Event.Level.ERROR);
			}
		}
	}

	/**
	 * crea un evento di "CHECKINVIO" 
	 */
	public static Event createNewEvent(BustaDocumenti... busta) {
		GestioneBuste buste = GestioneBuste.getFromSession();
		BaseAction action = GestioneBuste.getAction();
		String codice = buste.getDettaglioGara().getDatiGeneraliGara().getCodice();
		String nomeBusta = "";
		if(busta.length > 0 && busta[0] != null) {
			codice = (StringUtils.isNotEmpty(busta[0].getCodiceLotto()) ? busta[0].getCodiceLotto() : busta[0].getCodiceGara());
			nomeBusta = " per la " + BustaGara.getDescrizioneBusta(busta[0].getTipoBusta());
		}
		
		Event evento = new Event(); 
		evento.setUsername(action.getCurrentUser().getUsername());
		evento.setDestination(codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.CHECK_INVIO);
		evento.setIpAddress(action.getCurrentUser().getIpAddress());
		evento.setSessionId(action.getRequest().getSession().getId());
		evento.setMessage("Controlli preinvio dati " 
				+ (buste.isDomandaPartecipazione() ? "domanda di partecipazione" : "richiesta di presentazione offerta")
				+ nomeBusta
		);
		return evento;
	}

	/**
	 * verifica e valida l'integrita' dei documenti di tutte le buste 
	 * per documenti corrotti/mancanti/nulli e per la validita' dei questionari
	 * @throws ApsException 
	 */
	public boolean validate() throws ApsException {
		boolean controlliOk = true;
		
		countVerificaBuste = 0;
		countDocumentiTecnicheMancanti = 0;
		countDocumentiEconomicheMancanti = 0;
		
		// la verifica riepilogo, busta, db e' costosa se fatta per tutti i lotti 
		// (sono costretto a ricaricare i dati di ogni lotto), quindi viene effettuata
		// solo in caso di verifica della busta singola  
		verificaBustaSingola = false;
		
		boolean garaLotti = (bustaRiepilogo.getHelper().getListaCompletaLotti() != null &&
							 bustaRiepilogo.getHelper().getListaCompletaLotti().size() > 0);

		// verifica documenti corrotti/mancanti/nulli nelle buste ed errori nei questionari
		if(buste.isDomandaPartecipazione()) {
			// domanda di partecipazione
			if(buste.isBustaPrequalificaPrevista()) {
				controlliOk = controlliOk & validate(buste.getBustaPrequalifica());
			}
		} else {
			// offerta di gara 
			if(buste.isBustaAmministrativaPrevista()) {
				controlliOk = controlliOk & validate(buste.getBustaAmministrativa());
			}
			if(buste.isBustaTecnicaPrevista()) {
				if( !garaLotti ) {
					// gara NO lotti
					controlliOk = controlliOk & validate(buste.getBustaTecnica());
				} else {
					// gara a lotti
					if(bustaRiepilogo.getHelper().getBusteTecnicheLotti() != null) {
						for(String codLotto : bustaRiepilogo.getHelper().getListaCompletaLotti()) {
							controlliOk = controlliOk & validate(buste.getBustaTecnica(), codLotto);
						}
					}
				}
			}
			if(buste.isBustaEconomicaPrevista()) {
				if( !garaLotti ) {
					// gara NO lotti
					controlliOk = controlliOk & validate(buste.getBustaEconomica());
				} else {
					// gara a lotti
					if(bustaRiepilogo.getHelper().getBusteEconomicheLotti() != null) {
						for(String codLotto : bustaRiepilogo.getHelper().getListaCompletaLotti()) {
							controlliOk = controlliOk & validate(buste.getBustaEconomica(), codLotto); 
						}
					}
				}
			}
		}
		
		log("validate({}) => {}", buste.getCodiceGara(), controlliOk);
		return controlliOk;
	}

	/**
	 * verifica e valida l'integrita' dei documenti di una busta o di un lotto
	 * @param busta 		riferimento alla busta
	 * @param codiceLotto 	codice del lotto da esaminare, per busta di prequalifica e amministrativa viene ignorato
	 * @throws ApsException 
	 */
	public boolean validate(BustaDocumenti busta, String codiceLotto) throws ApsException {
		boolean controlliOk = true;

		if(busta == null
		   || bustaRiepilogo.getHelper() == null)
		{
			controlliOk = false;
		}

		boolean garaLotti = (bustaRiepilogo.getHelper().getListaCompletaLotti() != null &&
	 			 bustaRiepilogo.getHelper().getListaCompletaLotti().size() > 0);

		// se il lotto non e' previsto, salta il controllo (solo per gare a lotti)
		boolean verifica = true;
		if(garaLotti 
		   && StringUtils.isNotEmpty(codiceLotto)
		   && (PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta() || PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()) 
		   && buste.getBustaPartecipazione().getLottiAttivi().indexOf(codiceLotto) < 0) 
		{
			verifica = false;
		}

		if(controlliOk && verifica) {
			log("validate({}, {}, {})", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()));
			
			// verifica il primo accesso alla busta e documenti obbligatori/richiesti...
			// verifica se ci sono documenti corrotti/mancanti/nulli nelle buste
			// (Questionari - QFORM) controlla se e' presente un questionario e se lo stato e' "completato"
			// (Questionari - QFORM) verifica se e' cambiata la modulistica dei questionari
			if(controlliOk) {
				if(buste.isBustaPrequalificaPrevista()) { 
					if(PortGareSystemConstants.BUSTA_PRE_QUALIFICA == busta.getTipoBusta()) {
						controlliOk = controlliOk && verificaPresaVisioneDocumentiObbligatoriBusta(busta, null);
						if(controlliOk)
							controlliOk = controlliOk && !isDocumentiCorrottiPresenti(busta);
						if(controlliOk)
							controlliOk = controlliOk && verificaQuestionarioBusta(busta);
						// QFORM
						if(busta.getHelperDocumenti().isQuestionarioAllegato()) 
							if(controlliOk)
								controlliOk = controlliOk && validateQuestionarioJson(busta);
					}
				}
						
				if(buste.isBustaAmministrativaPrevista()) {
					if(PortGareSystemConstants.BUSTA_AMMINISTRATIVA == busta.getTipoBusta()) {
						if( !buste.getDettaglioGara().getDatiGeneraliGara().isNoBustaAmministrativa() ) {
							controlliOk = controlliOk && verificaPresaVisioneDocumentiObbligatoriBusta(busta, null);
							if(controlliOk)
								controlliOk = controlliOk && !isDocumentiCorrottiPresenti(busta);
							if(controlliOk)
								controlliOk = controlliOk && verificaQuestionarioBusta(busta);
							// QFORM
							if(busta.getHelperDocumenti().isQuestionarioAllegato())
								if(controlliOk)
									controlliOk = controlliOk && validateQuestionarioJson(busta);
						}
					}
				}
					
				if(buste.isBustaTecnicaPrevista()) {
					if(PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta() && buste.isBustaTecnicaPrevista()) {
						controlliOk = controlliOk && verificaPresaVisioneDocumentiObbligatoriBusta(busta, codiceLotto);
						if(!garaLotti) {
							if(controlliOk)
								controlliOk = controlliOk && !isDocumentiCorrottiPresenti(busta);
							if(controlliOk)
								controlliOk = controlliOk && verificaQuestionarioBusta(busta);
							// QFORM
							if(busta.getHelperDocumenti().isQuestionarioAllegato())
								if(controlliOk)
									controlliOk = controlliOk && validateQuestionarioJson(busta);
						} else {
							if(controlliOk)
								controlliOk = controlliOk && !isDocumentiCorrottiPresenti(busta, codiceLotto);
							if(controlliOk)
								controlliOk = controlliOk && verificaQuestionarioBusta(busta, codiceLotto);
							// QFORM (solo verifica busta singola)
							if(busta.getHelperDocumenti().isQuestionarioAllegato())
								if(controlliOk && verificaBustaSingola)
									controlliOk = controlliOk && validateQuestionarioJson(busta, codiceLotto);
						}
					}
				}
					
				if(buste.isBustaEconomicaPrevista()) {
					if(PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta() && buste.isBustaEconomicaPrevista()) {
						controlliOk = controlliOk && verificaPresaVisioneDocumentiObbligatoriBusta(busta, codiceLotto);
						if(!garaLotti) {
							if(controlliOk)
								controlliOk = controlliOk && !isDocumentiCorrottiPresenti(busta);
							if(controlliOk)
								controlliOk = controlliOk && verificaQuestionarioBusta(busta);
							// QFORM
							if(busta.getHelperDocumenti().isQuestionarioAllegato())
								if(controlliOk)
									controlliOk = controlliOk && validateQuestionarioJson(busta);
						} else {
							if(controlliOk)
								controlliOk = controlliOk && !isDocumentiCorrottiPresenti(busta, codiceLotto);
							if(controlliOk)
								controlliOk = controlliOk && verificaQuestionarioBusta(busta, codiceLotto);
							// QFORM (solo verifica busta singola)
							if(busta.getHelperDocumenti().isQuestionarioAllegato())
								if(controlliOk && verificaBustaSingola)
									controlliOk = controlliOk && validateQuestionarioJson(busta, codiceLotto);
						}
					}
				}
			}
			
			log("validate({}, {}, {}) => {}", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()), controlliOk);
		}
		
		return controlliOk;
	}

	public boolean validate(BustaDocumenti busta) throws ApsException {
		return validate(busta, null);
	}
	
	/**
	 * verifica l'integrita' dei documenti tra questionario.json e busta
	 * @param busta 		riferimento alla busta
	 * @param codiceLotto   codice del lotto
	 * @throws ApsException 
	 */	
	private boolean validateQuestionarioJson(BustaDocumenti busta, String codiceLotto) throws ApsException {
		boolean continua = true;
		List<AnomalieQuestionarioInfo> anomalie = busta.getHelperDocumenti().validaQuestionarioGare(busta.getTipoBusta());
		if(anomalie != null && anomalie.size() > 0) {
			continua = false;
			anomalie.stream()
				.forEach(d -> addErroreQuestionario(
								busta.getCodiceGara()
								, (codiceLotto != null ? codiceLotto : "")
								, action.getText("Errors.invioBuste.invalidQform",
												 new String[] {d.getFilename() 
														 	   , busta.getDescrizioneBusta() 
														 	   , (codiceLotto != null ? codiceLotto : "")
														 	   , d.getUuid()})
								)
				);
		}
		return continua;
	}	

	private boolean validateQuestionarioJson(BustaDocumenti busta) throws ApsException {
		return validateQuestionarioJson(busta, null);
	}
	
	/**
	 * verifica l'integrita' dei documenti della busta (corrotti/mancanti/nulli)
	 * @param busta 		riferimento alla busta
	 * @param codiceLotto 	codice del lotto da esaminare, per busta di prequalifica e amministrativa viene ignorato
	 * @throws ApsException 
	 */
	private boolean isDocumentiCorrottiPresenti(BustaDocumenti busta, String codiceLotto) throws ApsException {
		boolean documentiCorrotti = false;
		
		if(busta == null) {
			return documentiCorrotti;
		}		
		if(bustaRiepilogo.getHelper() == null) {
			return documentiCorrotti;
		}
		
		log("isDocumentiCorrottiPresenti({} {} {})", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()));
		
		// riepilogo puo' essere RiepilogoBustaBean o LinkedHashMap<String, RiepilogoBustaBean>
		Object riepilogo = null;
		if(PortGareSystemConstants.BUSTA_PRE_QUALIFICA == busta.getTipoBusta()) {
			riepilogo = bustaRiepilogo.getHelper().getBustaPrequalifica();
		} else if(PortGareSystemConstants.BUSTA_AMMINISTRATIVA == busta.getTipoBusta()) {
			riepilogo = bustaRiepilogo.getHelper().getBustaAmministrativa();
		} else if(PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta()) {
			riepilogo = (bustaRiepilogo.getHelper().getBusteTecnicheLotti() != null 
						? bustaRiepilogo.getHelper().getBusteTecnicheLotti()
						: bustaRiepilogo.getHelper().getBustaTecnica());
		} else if(PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()) {
			riepilogo = (bustaRiepilogo.getHelper().getBusteEconomicheLotti() != null 
					  	? bustaRiepilogo.getHelper().getBusteEconomicheLotti()
					   	: bustaRiepilogo.getHelper().getBustaEconomica());
		}
		if(riepilogo == null) {
			return documentiCorrotti;
		}
		
		String codiceGara = busta.getCodiceGara(); 
		String username = busta.getUsername(); 
		String tipoBusta = BustaGara.getTipoComunicazione(busta.getTipoBusta());

		// resetta la cache dei documenti (dimensioni e nulli) quando cambia il tipo di busta
		// in caso di gara a lotti per ogni tipo busta, la cache contiene sempre i documenti di tutti i lotti
		if(lastTipoBusta != busta.getTipoBusta()) {
			log("reset cache documenti");
			docDimensioniBO = null;
			docNulliBO = null;
		}
		lastTipoBusta = busta.getTipoBusta();
		
		// 1) verifica se esistono documenti nulli...
		if(!documentiCorrotti) {
			documentiCorrotti = verificaDocumentiNulli(
					codiceGara,
					codiceLotto,
					username, 
					tipoBusta);
		}

		// ...recupera l'elenco dei documenti con le loro dimensioni...
		// ...e verifica se utilizzare la verifica per UUID o per dimensione minimizzata...
		// (NB: utilizza la verifica per UUID solo se tutti i documenti hanno uuid impostato!!!)
		dimensioniDocumenti = checkDimensioneDocumenti(
				codiceGara,
				codiceLotto,
				username,
				tipoBusta);
		
		boolean useUuid = false;
		if(dimensioniDocumenti != null) {
			useUuid = true;
			for(int i = 0; i < dimensioniDocumenti.size(); i++) {
				if(PortGareSystemConstants.NOME_FILE_BUSTA.equals(dimensioniDocumenti.get(i).getNomefile())) {
					// ignora l'allegato "busta.xml" 
				} else if( StringUtils.isEmpty(dimensioniDocumenti.get(i).getUuid()) ) {
					useUuid = false;
				}
			}
		}
		
		// 2) verifica i documenti "corrotti"/"mancanti"...
		if(!documentiCorrotti) {
			if(useUuid) {
				// (3.2.0) verifica le dimensioni per UUID...
				documentiCorrotti = verificaDocumentiPerUUID(
						codiceGara, 
						codiceLotto,
						username, 
						tipoBusta,
						riepilogo);
			} else {
				// (fino a 3.1.x) verifica se esistono documenti corrotti con   
				// match per filename ed utilizza la verifica per dimenzione minimizzata
				documentiCorrotti = verificaDocumentiPerDimensioneMinimizzata(
						codiceGara, 
						codiceLotto,
						username, 
						tipoBusta,
						riepilogo);
			}
		}
		
		// 3) verifica se i documenti sono sincronizzati tra riepilogo (FS10R/FS11R) e busta (FS10A/FS11A,FS11B,FS11C)
		if(!documentiCorrotti) {
			documentiCorrotti = !verificaDocumentiRiepilogoBustaDB(busta, codiceLotto);
		}
		
		log("isDocumentiCorrottiPresenti({} {} {}) => {}", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()), documentiCorrotti);
		return documentiCorrotti;
	}
	
	private boolean isDocumentiCorrottiPresenti(BustaDocumenti busta) throws ApsException {
		return isDocumentiCorrottiPresenti(busta, null);
	}
	
	/**
	 * verifica la presenza di eventuali documenti nulli (W_DOCDIG.DIGOGG = NULL)
	 * @throws ApsException 
	 */
	private boolean verificaDocumentiNulli(
			String codiceGara, 
			String codiceLotto,
			String username, 
			String tipoBusta) throws ApsException 
	{		  	
		log("verificaDocumentiNulli({} {} {} {}) ", codiceGara, codiceLotto, username, tipoBusta);
		
		// verifica se i documenti nulli associati al tipo di busta sono gia' stati caricati
		// altrimenti carica tutti i documenti (di tutti i lotti) relativi al tipo di busta
		if(docNulliBO == null) {
			docNulliBO = this.bandiManager.checkDocumentiNulli(
					codiceGara,
					username,
					tipoBusta);
			log("carica cache documenti nulli {}", (docDimensioniBO != null ? docDimensioniBO.size() : "null"));
		}
		
		// in caso di lotto, estrai solo i documenti relativi al lotto in esame...
		List<DocumentoAllegatoLotto> docNulli = docNulliBO;
		if(StringUtils.isNotEmpty(codiceLotto) && docNulliBO != null) {
			docNulli = docNulliBO.stream()
					.filter(d -> d.getLotto() != null && codiceLotto.equalsIgnoreCase(d.getLotto().getCodiceLotto()))
					.collect(Collectors.toList());
			
			log("recupera documenti nulli per lotto {} {}", codiceLotto, (docNulli != null ? docNulli.size() : "null"));
		}
		
		if(docNulli != null && docNulli.size() > 0) {
			// esistono documenti senza contenuto...
			for(int i = 0; i < docNulli.size(); i++) {
				this.addNullo(codiceGara, tipoBusta, docNulli.get(i));
			}
		}
	
		log("verificaDocumentiNulli({} {} {} {}) => {}", codiceGara, codiceLotto, username, tipoBusta, (docNulli != null ? docNulli.size() : "NULL"));
		return (docNulli != null && docNulli.size() > 0);
	}

	/**
	 * recupera le dimensioni degli allegati da W_DOCDIG
	 * @throws ApsException 
	 */
	private List<DocumentoAllegatoLotto> checkDimensioneDocumenti(
			String codiceGara, 
			String codiceLotto,
			String username, 
			String tipoBusta) throws ApsException
	{
		log("checkDimensioneDocumenti({} {} {} {})", codiceGara, codiceLotto, username, tipoBusta);
		
		if(docDimensioniBO == null) {
			docDimensioniBO = this.bandiManager.checkDimensioneDocumenti(
					codiceGara,
					username,
					tipoBusta);
			log("carica cache dimensione documenti {}", (docDimensioniBO != null ? docDimensioniBO.size() : "null"));
		}
		
		List<DocumentoAllegatoLotto> dimensioni = docDimensioniBO;
		if(StringUtils.isNotEmpty(codiceLotto) && docDimensioniBO != null) {
			dimensioni = docDimensioniBO.stream()
					.filter(d -> d.getLotto() != null && codiceLotto.equalsIgnoreCase(d.getLotto().getCodiceLotto()))
					.collect(Collectors.toList());
			log("recupera dimensione documenti per lotto {} {}", codiceLotto, (dimensioni != null ? dimensioni.size() : "null"));
		}
		
		log("checkDimensioneDocumenti({} {} {} {}) => {}", codiceGara, codiceLotto, username, tipoBusta, (dimensioni != null ? dimensioni.size() : "NULL"));
		return dimensioni;
	}
	/**
	 * verifica l'integrità della dimensione dei documenti della busta
	 * @throws ApsException 
	 */
	private boolean verificaDocumentiPerDimensioneMinimizzata(
			String codiceGara, 
			String codiceLotto,
			String username, 
			String tipoBusta,
			Object riepilogo) throws ApsException
	{	
		log("verificaDocumentiPerDimensioneMinimizzata({} {} {})", codiceGara, codiceLotto, tipoBusta);		
		boolean documentiCorrotti = false;
		
		// segnala i documenti e blocca l'invio...
		if(dimensioniDocumenti != null && dimensioniDocumenti.size() > 0) {
			//boolean isGaraLotti = (riepilogo instanceof LinkedHashMap);
			
			// crea lista di allegati del riepilogo che sono stati verificati 
			List<DocumentoAllegatoBean> allegatiVerificati = new ArrayList<DocumentoAllegatoBean>();
			
			// verifica per ogni documento la dimensione registrata 
			for(int i = 0; i < dimensioniDocumenti.size(); i++) {
				
				// trova il lotto nel quale cercare documento nel riepilogo...
				RiepilogoBustaBean lotto = null;
				if(riepilogo instanceof RiepilogoBustaBean) {
					// gara NO lotti
					lotto = (RiepilogoBustaBean) riepilogo; 
				} else if(riepilogo instanceof LinkedHashMap) {
					// gara a lotti
					LinkedHashMap<String, RiepilogoBustaBean> lotti = (LinkedHashMap<String, RiepilogoBustaBean>) riepilogo;
					lotto = lotti.get( dimensioniDocumenti.get(i).getLotto().getCodiceLotto() );
				}
				
				// per comporre la chiave di ricerca verifica se il documento è 
				// doc richiesto 	("nomefile|descrizione")
				// doc ulteriore 	("nomefile|")
				String key1 = dimensioniDocumenti.get(i).getNomefile() + "|" +
	  						  (StringUtils.isNotEmpty(dimensioniDocumenti.get(i).getDescrizione()) 
	  						  ? dimensioniDocumenti.get(i).getDescrizione() : "");

				// cerca nel riepilogo l'allegato con lo stesso nome 
				// del documento presente in W_DOCDIG, e nel caso di
				// allegati con lo stesso nome cerca quello che minimizza 
				// la distanza in termini di dimensione
				DocumentoAllegatoBean allegatoSimile = null;
				double dMinima = (dimensioniDocumenti.get(i).getDimensione() != null 
						          ? dimensioniDocumenti.get(i).getDimensione() : 0) * 1024.0;
				
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
								double a = (double) (dimensioniDocumenti.get(i).getDimensione() != null 
										             ? dimensioniDocumenti.get(i).getDimensione() : 0);
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
						double a = (double) (dimensioniDocumenti.get(i).getDimensione() != null 
											 ? dimensioniDocumenti.get(i).getDimensione() : 0);
						double b = (double) allegatoSimile.getDimensione() * 1024.0;
						double d = Math.abs(a - b);
						if( a > 0 && d > 1024 ) {	// |a-b| <= 1024  le dimensioni vengono considerate equivalenti
							this.addCorrotto(codiceGara, tipoBusta, dimensioniDocumenti.get(i));
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
						codiceLotto,
						username, 
						tipoBusta,
						riepilogo,
						allegatiVerificati);
			}
		}
		
		log("verificaDocumentiPerDimensioneMinimizzata({} {} {}) => {}", codiceGara, codiceLotto, tipoBusta, documentiCorrotti);
		return documentiCorrotti;
	}

	/**
	 * verifica se esistono nel riepilogo documenti non verificati 
	 */
	private boolean checkAllegatiMancanti(
			String codiceGara, 
			String codiceLotto,
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
				if( !allegatiVerificati.contains(allegatoRiepologo) ) {
					// allegato non presente in W_DOCDIG!!!
					this.addMancante(codiceGara, "", tipoBusta, allegatoRiepologo);
					documentiCorrotti = true;
				}
				
				// controlla FS11x corrisponde a DB...
			}
			
		} else if(riepilogo instanceof LinkedHashMap) {
			// gara a lotti
			HashMap<String, RiepilogoBustaBean> mlotti = (LinkedHashMap<String, RiepilogoBustaBean>) riepilogo;
			Iterator iter = mlotti.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry item = (Map.Entry) iter.next();
				String cod = (String)item.getKey();
				RiepilogoBustaBean lotto = (RiepilogoBustaBean) item.getValue();
				
				// nel caso di verifica su una singola busta verifica solo il lotto specifico "codiceLotto"  				
				if(StringUtils.isNotEmpty(codiceLotto) && !codiceLotto.equalsIgnoreCase(cod)) {
					// annulla tutti i lotti tranne quello specifico
					lotto = null;
				}
				
				if(lotto != null) {
					for(DocumentoAllegatoBean allegatoRiepilogo : lotto.getDocumentiInseriti()) {
						if( !allegatiVerificati.contains( allegatoRiepilogo ) ) {
							// allegato non presente in W_DOCDIG!!!
							this.addMancante(codiceGara, cod, tipoBusta, allegatoRiepilogo);
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
			String codiceLotto,
			String username, 
			String tipoBusta,
			Object riepilogo) throws ApsException
	{	
		log("verificaDocumentiPerUUID({} {} {})", codiceGara, codiceLotto, tipoBusta);
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
						codiceLotto,
						username, 
						tipoBusta,
						riepilogo,
						allegatiVerificati);
			}
		}
		
		log("verificaDocumentiPerUUID({} {} {}) => {}", codiceGara, codiceLotto, tipoBusta, documentiCorrotti);
		return documentiCorrotti;
	}

	/**
	 * verifica la sincronizzazione tra i documenti di riepilogo, busta e db 
	 */
	private boolean verificaDocumentiRiepilogoBustaDB(BustaDocumenti busta, String codiceLotto) {
		boolean controlliOk = true;
		
		// NB: per le gare a lotti, 
		// i documenti delle buste ECO e TEC non vengono caricati per ogni lotto
		// quindi non sono disponibili per poter fare il controllo
		// altrimenti per le gare a lotti dovrei caricare ogni volta i dati di busta TEC/ECO per ogni lotto 
		// per poter eseguire la verifica
		boolean verifica = true;
		if(!verificaBustaSingola
		   && (PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta() || PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()))
		{
			verifica = false;
		}
		
		if(verifica) {
			log("verificaDocumentiRiepilogoBustaDB({} {}) {}", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()));
	
			// qui riepilogo e db sono allineati... 
			// quindi verifica se la busta e' allineata a riepilogo e db
			try {
				RiepilogoBustaBean riepilogo = bustaRiepilogo.getHelper().getRiepilogoBusta(busta.getTipoBusta(), codiceLotto);
	
				if(riepilogo != null) {
					// 1) trova tutti gli allegati della busta che non corrispondono a nessun allegato del riepilogo
					//    e trova tutti gli allegati del riepilogo che non corrispondono a nessun allegato della busta
					List<Object> mancanti = new ArrayList<>();
					
					busta.getHelperDocumenti().getAdditionalDocs().stream()
						.filter(a -> isDocumentiRiepilogoBustaDBBustaMancante(a, riepilogo))
						.forEach(a -> mancanti.add(a));
					
					busta.getHelperDocumenti().getRequiredDocs().stream()
						.filter(a -> isDocumentiRiepilogoBustaDBBustaMancante(a, riepilogo))
						.forEach(a -> mancanti.add(a));
					
					riepilogo.getDocumentiInseriti().stream()
						.filter(r -> isDocumentiRiepilogoBustaDBRiepilogoMancante(r, busta.getHelperDocumenti().getAdditionalDocs())
									 && isDocumentiRiepilogoBustaDBRiepilogoMancante(r, busta.getHelperDocumenti().getRequiredDocs()))
						.forEach(r -> mancanti.add(r));
					
					// 2) trova tutti gli allegati della busta che hanno dimensione diversa dal corrispondente allegato nel DB
					List<Attachment> dimensioneErrata = new ArrayList<>();
					
					busta.getHelperDocumenti().getAdditionalDocs().stream()
						.filter(a -> a.getUuid() != null && a.getSize() != null)
						.filter(a -> isDocumentiRiepilogoBustaDBBustaDimensioneErrata(a))
						.forEach(a -> dimensioneErrata.add(a));
						
					busta.getHelperDocumenti().getRequiredDocs().stream()
						.filter(a -> a.getUuid() != null && a.getSize() != null)
						.filter(a -> isDocumentiRiepilogoBustaDBBustaDimensioneErrata(a))
						.forEach(a -> dimensioneErrata.add(a));
					
					controlliOk = (mancanti.size() <= 0 && dimensioneErrata.size() <= 0);
		
					// aggiungi gli errori come documenti corrotti (mancanti)
					String tipoBusta = BustaGara.getTipoComunicazione(busta.getTipoBusta());
					mancanti.stream()
						.forEach(a -> { 
							if(a instanceof Attachment) { 
								addMancante(busta.getCodiceGara(), codiceLotto, tipoBusta, (Attachment)a);
							} else if(a instanceof DocumentoAllegatoBean) {
								addMancante(busta.getCodiceGara(), codiceLotto, tipoBusta, (DocumentoAllegatoBean)a);
							}
						});
					
					dimensioneErrata.stream()
						.forEach(a -> addCorrotto(busta.getCodiceGara(), codiceLotto, tipoBusta, a));
					
					log("verificaDocumentiRiepilogoBustaDB {} {} {} {} {} ", busta.getCodiceGara(), codiceLotto, tipoBusta, mancanti.size(), dimensioneErrata.size());
				}
			} catch (Throwable t) {
				controlliOk = false;
				ApsSystemUtils.logThrowable(t, action, "verificaDocumentiRiepilogoBustaDB");
				action.addActionError(t.getMessage());
			}
			
			log("verificaDocumentiRiepilogoBustaDB({} {}) => {}", busta.getCodiceGara(), codiceLotto, controlliOk);
		}
		return controlliOk;
	}

	private boolean isDocumentiRiepilogoBustaDBBustaMancante(Attachment attachment, RiepilogoBustaBean riepilogo) {
		return riepilogo.getDocumentiInseriti().stream()
			.map(DocumentoAllegatoBean::getUuid)
			.noneMatch(uuid -> uuid.equalsIgnoreCase(attachment.getUuid()));
	}
	
	private boolean isDocumentiRiepilogoBustaDBRiepilogoMancante(DocumentoAllegatoBean docRiepilogo, List<Attachment> attachments) {  
		return attachments.stream()
			.map(Attachment::getUuid)
			.noneMatch(uuid -> uuid.equalsIgnoreCase(docRiepilogo.getUuid()));
	}
	
	private boolean isDocumentiRiepilogoBustaDBBustaDimensioneErrata(Attachment attachment) {  
		return dimensioniDocumenti.stream()
			.filter(db -> db.getUuid() != null && db.getDimensione() != null)
			.filter(db -> db.getUuid().equalsIgnoreCase(attachment.getUuid()))
			.anyMatch(db -> Math.abs(db.getDimensione().doubleValue() - attachment.getSize().doubleValue()*1024.0) > 1024);
	}

	/**
	 * (Questionari - QFORM)
	 * verifica se una data busta ha un questionario associato e completato
	 * e se la modulistica e' stata cambiata dall'ultima compilazione
	 * @param busta 		riferimento alla busta
	 * @param codiceLotto 	codice del lotto da esaminare, per busta di prequalifica e amministrativa viene ignorato 
	 */
	private boolean verificaQuestionarioBusta(
			BustaDocumenti busta, 
			String codiceLotto) 
	{
		log("verificaQuestionarioBusta({} {} {})", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()));
		
		boolean controlliOk = true;
		boolean modulisticaCambiata = false;
		
		// recupera la busta di riepilogo in base al tipo di busta
		BustaRiepilogo bustaRiepilogo = (buste != null ? buste.getBustaRiepilogo() : null);
		RiepilogoBusteHelper helper = (bustaRiepilogo != null ? bustaRiepilogo.getHelper() : null);
		
		// verifica se il questionario e' completo e se il pdf di riepilogo esiste...
		if(helper != null) {
			BaseAction action = GestioneBuste.getAction();
			try {
				String codiceGara = busta.getCodiceGara();
				codiceLotto = (StringUtils.isNotEmpty(codiceLotto) ? codiceLotto : busta.getCodiceGara());				
				
				// recupera l'helper di riepilogo della busta/lotto
				RiepilogoBustaBean riepilogoHelper = (bustaRiepilogo != null
						? helper.getRiepilogoBusta(busta.getTipoBusta(), codiceLotto)
						: null);
				
				// verifica se e' attiva la gestione del questionario per la gara
				if(riepilogoHelper != null && riepilogoHelper.isQuestionarioPresente()) {
					log("verificaQuestionarioBusta({} {} {}) => questionario trovato", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()));
					
					// recupera la descrizione completa della busta (lotto compreso)
					String descBusta = busta.getDescrizioneBusta();
					if(PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta() 
					   || PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()) 
					{
						String codiceInternoLotto = (helper.getListaCodiciInterniLotti() != null
								? helper.getListaCodiciInterniLotti().get(codiceLotto)
								: null);
						if(StringUtils.isNotEmpty(codiceInternoLotto)) {
							descBusta += " (" + action.getI18nLabel("LABEL_LOTTO") + " " + codiceInternoLotto + ")";
						}
					}

					// recupera il questionario dal BO...
					QuestionarioType q = WizardDocumentiBustaHelper.getQuestionarioAssociatoBO(
							codiceLotto,
							codiceGara, 
							null, 
							busta.getTipoBusta());

					// verifica se e' cambiata la modulistica del questionario...
					long idModello = (q != null ? q.getId() : 0);
					long idBusta = riepilogoHelper.getQuestionarioId();
					if( idModello != idBusta ) {
						modulisticaCambiata = true;
					}
					
					// verifica se il questionario non e' variato ed e' completo...
					if(!modulisticaCambiata && riepilogoHelper.isQuestionarioCompletato()) {
						// questionario completato
						controlliOk = true;
					} else {
						// questionario incompleto o modulistica cambiata...
						controlliOk = false;
						addErroreQuestionario(codiceGara, codiceLotto, action.getText("Errors.invioBuste.nullSurvey", new String[] {descBusta}));
					}
					
					// verifica se esite l'allegato del "riepilogo pdf" automatico 
					// Si verifica solo il PDF di riepilogo automatico, dato per quello manuale 
					// si tratta di una allegato obbligatorio che va caricato
					// altrimenti QForm non permette di procedere allo step successivo!!!
					if(controlliOk) {
						if(riepilogoHelper.isRiepilogoPdfAuto()) {
							// deve esistere un allegato con descrizione "Pdf riepilogo" con prefisso UUID "999999..."
							DocumentoAllegatoBean pdfRiepilogo = riepilogoHelper.getDocumentiInseriti()
									.stream()
									.filter(a -> DocumentiAllegatiHelper.QUESTIONARIO_PDFRIEPILOGO.equalsIgnoreCase(a.getDescrizione())
												 && (a.getUuid() != null && a.getUuid().startsWith("999999")))
									.findFirst()
									.orElse(null);
							
							if(pdfRiepilogo == null) {
								// allegato del riepilogo PDF automatico NON trovato!!!
								controlliOk = false;
								addErroreQuestionario(codiceGara, codiceLotto, action.getText("Errors.invioBuste.pdfSummaryNotFound", new String[] {descBusta}));
							}
							
						}
					}
				}
			} catch (Throwable t) {
				controlliOk = false;
				ApsSystemUtils.logThrowable(t, action, "isQuestionarioCompleto");
				addErroreQuestionario(busta.getCodiceGara(), codiceLotto, t.getMessage());
			}
		}
		
		// verifica se la modulistica e' cambiata...
		//if(modulisticaCambiata) {
		//	...
		//}
		
		log("verificaQuestionarioBusta({} {} {}) => {}", busta.getCodiceGara(), codiceLotto, BustaGara.getTipoComunicazione(busta.getTipoBusta()), controlliOk);
		return controlliOk;
	}

	private boolean verificaQuestionarioBusta(BustaDocumenti busta) {
		return verificaQuestionarioBusta(busta, null);
	}

	/**
	 * verifica la presa visione e i documenti obbligatori/richiesti di una busta 
	 * @throws XmlException 
	 * @throws ApsException 
	 * @throws IOException 
	 */	
	private boolean verificaPresaVisioneDocumentiObbligatoriBusta(BustaDocumenti busta, String codiceLotto) throws ApsException {
		boolean controlliOk = true;
		try {
			RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper();
			
			boolean garaLotti = (bustaRiepilogo.getHelper().getListaCompletaLotti() != null &&
								 bustaRiepilogo.getHelper().getListaCompletaLotti().size() > 0);
			
			DettaglioGaraType dettGara = buste.getDettaglioGara();
			
			boolean sospesa = ("4".equals(dettGara.getDatiGeneraliGara().getStato()));
			
			boolean costoFisso = (dettGara.getDatiGeneraliGara() != null && dettGara.getDatiGeneraliGara().getCostoFisso() != null
								  ? dettGara.getDatiGeneraliGara().getCostoFisso() == 1 
								  : false);

			List<CriterioValutazioneOffertaType> criteri = null;
			if (dettGara.getListaCriteriValutazione() != null) {
				criteri = Arrays.asList(buste.getDettaglioGara().getListaCriteriValutazione()); 
			}

			boolean apriOEPVBustaTecnica = WizardOffertaHelper.isCriteriValutazioneVisibili(
					PortGareSystemConstants.CRITERIO_TECNICO, 
					dettGara.getDatiGeneraliGara(),
					criteri);
	 
			// controlla se e' stato effettuato un accesso di presa visione documenti
			// controlla i documenti obbligatori della busta
			if(!garaLotti) {
				// gara NO lotti
				//
				if(buste.isDomandaPartecipazione()) {
					// busta prequalifica
					if(PortGareSystemConstants.BUSTA_PRE_QUALIFICA == busta.getTipoBusta()) {
						// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
						if( controlliOk && !bustaRiepilogo.getHelper().getPrimoAccessoPrequalificaEffettuato() ) {
							action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
																 new String[] { PortGareSystemConstants.BUSTA_PRE }));
							controlliOk = false;
						}

						// verifica i documenti obbligatori
						if(controlliOk)
							controlliOk = checkBusta(busta);
					}
				}  
		
				if(buste.isInvioOfferta()) {
					// busta amministrativa
					if(PortGareSystemConstants.BUSTA_AMMINISTRATIVA == busta.getTipoBusta()) {
						if( !buste.getDettaglioGara().getDatiGeneraliGara().isNoBustaAmministrativa() ) {
							// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
							if( controlliOk && !bustaRiepilogo.getHelper().getPrimoAccessoAmministrativaEffettuato() ) {
								action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
																	 new String[] { PortGareSystemConstants.BUSTA_AMM }));
								controlliOk = false;
							}
							
							// verifica i documenti obbligatori
							if(controlliOk)
								controlliOk = checkBusta(busta);
						}
					}
			
					// busta tecnica
					if(PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta()) {
						if (bandiManager.isGaraConOffertaTecnica(busta.getCodiceGara())) {
							// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
							if( !bustaRiepilogo.getHelper().getPrimoAccessoTecnicaEffettuato() ) {
								action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
																	 new String[] { PortGareSystemConstants.BUSTA_TEC }));
								controlliOk = false;
							}
							if (controlliOk) {
								if (apriOEPVBustaTecnica) {
									controlliOk = controlliOk && checkBustaOfferta(busta);
								} else {
									controlliOk = controlliOk && checkBusta(busta);
								}
							}
						}
					}
		
					// busta economica
					// in caso di OEPV a costo fisso non va controllata
					// la parte economica...
					if(PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()) {
						if(!costoFisso) {
							// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
							if( !bustaRiepilogo.getHelper().getPrimoAccessoEconomicaEffettuato() ) {
								action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
																	 new String[] { PortGareSystemConstants.BUSTA_ECO }));
								controlliOk = false;
							}
							if (controlliOk) {
								if (buste.getDettaglioGara().getDatiGeneraliGara().isOffertaTelematica()) {
									controlliOk = controlliOk && checkBustaOfferta(busta);
								} else {
									controlliOk = controlliOk && checkBusta(busta);
								}
							}
						}
					}
				}
				
			} else {
				// gara a lotti
				//
				if(PortGareSystemConstants.BUSTA_PRE_QUALIFICA == busta.getTipoBusta()) {
					if(buste.isDomandaPartecipazione()) {
						if(riepilogo.getBustaPrequalifica() != null) {
							boolean documentiPrequalificaMancanti = false;
							for(int i = 0; i < riepilogo.getBustaPrequalifica().getDocumentiMancanti().size() && !documentiPrequalificaMancanti; i++) {
								documentiPrequalificaMancanti = documentiPrequalificaMancanti || riepilogo.getBustaPrequalifica().getDocumentiMancanti().get(i).isObbligatorio();
							}
							
							// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
							if( !riepilogo.getPrimoAccessoPrequalificaEffettuato() ) {
								action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
																	 new String[] { PortGareSystemConstants.BUSTA_PRE }));
								controlliOk = false;
							}
							
							// verifica i documenti obbligatori
							if(documentiPrequalificaMancanti) {
								action.addActionError(action.getText("Errors.invioBuste.changesNotSent", 
																	 new String[] { PortGareSystemConstants.BUSTA_PRE }));
								controlliOk = false;
							}
						}	
					}
				}
				
				if(PortGareSystemConstants.BUSTA_AMMINISTRATIVA == busta.getTipoBusta()) {
					if( !dettGara.getDatiGeneraliGara().isNoBustaAmministrativa() ) {
						if(riepilogo.getBustaAmministrativa() != null) {
							boolean documentiAmministrativaMancanti = false;
							for(int i = 0; i < riepilogo.getBustaAmministrativa().getDocumentiMancanti().size() && !documentiAmministrativaMancanti; i++) {
								documentiAmministrativaMancanti = documentiAmministrativaMancanti || riepilogo.getBustaAmministrativa().getDocumentiMancanti().get(i).isObbligatorio();
							}
							
							// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
							if( !riepilogo.getPrimoAccessoAmministrativaEffettuato() ) {
								action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
																	 new String[] { PortGareSystemConstants.BUSTA_AMM }));
								controlliOk = false;
							}
							
							// verifica i documenti obbligatori
							if(documentiAmministrativaMancanti) {
								action.addActionError(action.getText("Errors.invioBuste.changesNotSent",
																	 new String[] { PortGareSystemConstants.BUSTA_AMM }));
								controlliOk = false;
							}
						}
					}
				}
				
				// LOTTI BUSTE TECNICHE
				if(PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta()) {
					// per ogni lotto, verifica se c'e stata la "presa visione" o se ci sono documenti mancanti...
					if(verificaBustaSingola) {
						// verifica un singolo lotto (check singola busta)
						controlliOk = controlliOk && checkSingoloLotto(busta, codiceLotto);
					} else {
						// verifica tutti i lotti (check dell'invio offerta)
						List<String> lottiTecnicheMancanti = checkPresaVisioneDocumentiMancantiLotti(
							riepilogo,
							riepilogo.getPrimoAccessoTecnicheEffettuato(),
							riepilogo.getBusteTecnicheLotti(),
							null);
					
						if(lottiTecnicheMancanti.size() > 0) {
							countDocumentiTecnicheMancanti++;
							if(countDocumentiTecnicheMancanti == 1)
								action.addActionError(action.getText("Errors.invioBusteLotti.dataNotSent", new String[] { "Buste tecniche" }));
							controlliOk = false;
						}
					}
				}
					
				// LOTTI BUSTE ECONOMICHE
				if(PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()) {
					// per ogni lotto, verifica se c'e stata la "presa visione" o se ci sono documenti mancanti...
					if(verificaBustaSingola) {
						// verifica un singolo lotto (check singola busta)
						controlliOk = controlliOk && checkSingoloLotto(busta, codiceLotto);
					} else {
						// verifica tutti i lotti (check dell'invio offerta)
						List<String> lottiEconimicheMancanti = checkPresaVisioneDocumentiMancantiLotti(
							riepilogo,
							riepilogo.getPrimoAccessoEconomicheEffettuato(),
							riepilogo.getBusteEconomicheLotti(),
							null);
					
						if(lottiEconimicheMancanti.size() > 0) {
							countDocumentiEconomicheMancanti++;
							if(countDocumentiEconomicheMancanti == 1) 
								action.addActionError(action.getText("Errors.invioBusteLotti.dataNotSent", new String[] { "Buste economiche" }));
							controlliOk = false;
						}
					}
				}
			}

			// NB: la verifica dei documenti mancanti e dei lotti in altre offerte
			// e' eseguita su tutte le buste e quindi viene eseguita solo UNA VOLTA
			// e non nella verifica di ogni busta/lotto!!!
			countVerificaBuste++;
			if(countVerificaBuste == 1) {
				if( !controlliOk && !sospesa ) {
					String documentiMancanti = getListaDocumentiMancanti();
					if(StringUtils.isNotEmpty(documentiMancanti)) {
						if(event != null) {
							event.setDetailMessage(documentiMancanti);
							event.setLevel(Event.Level.ERROR);
						}
					}
				}
				
				// per gare ristrette in fase di domanda di partecipazione
				// controlla se i lotti dell'offerta sono utilizzati anche in altre offerte...
				//if(controlliOk && buste.isDomandaPartecipazione()) {
				if(controlliOk) {
					boolean lottiInAltreOfferte = isLottiPresentiInAltreOfferte(); 
					if (lottiInAltreOfferte) {
						// alcuni lotti dell'offerta sono presenti in altre offerte...
						if(event != null) {
							event.setDetailMessage("Lotti presenti in altre offerte");
							event.setLevel(Event.Level.ERROR);
						}
						controlliOk = false;
					}
				}
			}
	
		} catch (ApsException | XmlException | IOException e) {
			controlliOk = false;
			throw new ApsException(e.getMessage());
		}		
		
		erroriPresaVisioneDocRichiesti = !controlliOk;
		
 		return controlliOk;
	}

	/**
	 * verifica i documenti richiesti di una busta (solo documenti) 
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private boolean checkBusta(BustaDocumenti busta) throws ApsException, XmlException, IOException {
		boolean bustaOk = true;
		
		String nomeBusta = BustaGara.getDescrizioneBusta(busta.getTipoBusta());
		String tipoComunicazione = BustaGara.getTipoComunicazione(busta.getTipoBusta());
		// in caso di lotto la comunicazione ha come codice il lotto (busta.codiceLotto)  
		String codice = (StringUtils.isNotEmpty(busta.getCodiceLotto()) && !busta.getCodiceLotto().equalsIgnoreCase(busta.getCodiceGara()) 
				? busta.getCodiceLotto()
				: busta.getCodiceGara()
		);

		// verifica se la busta/lotto e' gia' stata inviata (DA PROCESSARE)
		DettaglioComunicazioneType dettComunicazioneBustaInviata = ComunicazioniUtilities
				.retrieveComunicazione(
						comunicazioniManager,
						busta.getUsername(),
						codice,
						busta.getProgressivoOfferta(),
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						tipoComunicazione);

		if (dettComunicazioneBustaInviata != null) {
			action.addActionMessage(action.getText("Errors.send.alreadySent", new String[] { nomeBusta }));
		} else {
			// verifico se l'ente ha specificato dei documenti obbligatori
			// da inserire nella busta, altrimenti bypasso il check su
			// questa busta
			WizardPartecipazioneHelper partecipazioneHelper = buste.getBustaPartecipazione().getHelper();
			
			// nella gara a lotto unico i documenti stanno nel lotto, 
			// mentre nella gara ad offerta unica stanno nella gara
			String codiceLotto = null;
			if (buste.getDettaglioGara().getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
				codiceLotto = buste.getDettaglioGara().getDatiGeneraliGara().getCodice();
			}
			
			List<DocumentazioneRichiestaType> documentiRichiesti = this.bandiManager
					.getDocumentiRichiestiBandoGara(
							busta.getCodiceGara(), 
							codiceLotto,
							buste.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(), 
							partecipazioneHelper.isRti(), 
							busta.getTipoBusta() + "");

			if (hasDocObbligatori(documentiRichiesti)) {

				// verifico che ci siano i documenti richiesti per la busta
				DettaglioComunicazioneType dettComunicazioneBusta = ComunicazioniUtilities
						.retrieveComunicazione(
								comunicazioniManager,
								busta.getUsername(),
								codice,
								busta.getProgressivoOfferta(),
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
								tipoComunicazione);

				if (dettComunicazioneBusta != null) {
					ComunicazioneType comunicazioneBusta = comunicazioniManager
							.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
											  dettComunicazioneBusta.getId());
					bustaOk = checkDocumenti(comunicazioneBusta, nomeBusta, documentiRichiesti);
				} else {
					bustaOk = false;
					action.addActionError(action.getText("Errors.invioBuste.dataNotSent",
													 	 new String[] { nomeBusta }));
				}
			}
		}
		
		return bustaOk;
	}
	
	private boolean hasDocObbligatori(List<DocumentazioneRichiestaType> documentiRichiesti) {
		boolean found = false;
		for (DocumentazioneRichiestaType documentoRichiesto : documentiRichiesti) {
			if (documentoRichiesto.isObbligatorio()) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	private boolean checkDocumenti(
			ComunicazioneType comunicazioneBusta,
			String nomeBusta,
			List<DocumentazioneRichiestaType> documentiRichiesti) throws XmlException, IOException 
	{
		boolean controlliOk = true;
		try {
			DocumentazioneBustaDocument bustaDocument = BustaDocumenti.getBustaDocument(comunicazioneBusta);
			ListaDocumentiType documenti = bustaDocument.getDocumentazioneBusta().getDocumenti();
			controlliOk = checkDocumentiBustaXml(
					nomeBusta, 
					documenti, 
					documentiRichiesti);
		} catch (Throwable ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
				action.addActionError(action.getText("Errors.invioBuste.xmlBustaNotFound", new String[] { nomeBusta }));
			} else {
				action.addActionError(ex.getMessage());
			}
			((EncodedDataAction)action).setTarget(CommonSystemConstants.PORTAL_ERROR);
			controlliOk = false;
		}
		return controlliOk;
	}

	private boolean checkDocumentiBustaXml(
			String nomeBusta,
			ListaDocumentiType documenti,
			List<DocumentazioneRichiestaType> documentiRichiesti)
		throws ApsException 
	{
		boolean controlliOk = true;

		for (int i = 0; i < documentiRichiesti.size(); i++) {
			boolean docFound = false;
			if (!controlliOk) {
				break;
			}
			if (documentiRichiesti.get(i).isObbligatorio()) {
				for (int j = 0; j < documenti.sizeOfDocumentoArray(); j++) {
					DocumentoType documento = documenti.getDocumentoArray(j);
					if (documento.getId() != 0 && documento.getId() == documentiRichiesti.get(i).getId()) {
						docFound = true;
						break;
					}
				}
				if (!docFound) {
					controlliOk = false;
					action.addActionError(action.getText("Errors.docRichiestoObbligatorioNotFound",
													     new String[] { nomeBusta, documentiRichiesti.get(i).getNome() }));
				}
			}
		}

		for (int j = 0; j < documenti.sizeOfDocumentoArray(); j++) {
			DocumentoType documento = documenti.getDocumentoArray(j);
			if (documento.getNomeFile().length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				controlliOk = false;
				if (documento.getId() != 0) {
					action.addActionError(action.getText("Errors.docRichiestoOverflowFileNameLength",
														 new String[] { nomeBusta, documento.getNomeFile() }));
				} else {
					action.addActionError(action.getText("Errors.docUlterioreOverflowFileNameLength",
														 new String[] { nomeBusta, documento.getNomeFile() }));
				}
			}
		}
		return controlliOk;
	}

	/**
	 * restituisce la lista dei documenti presenti in una richiesta di partecipazione/offerta
	 */
	private String getListaDocumentiMancanti() {
		String documenti = "";
		String s;
		
		RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper();

		if(buste.isDomandaPartecipazione()) {
			// domanda di partecipazione
			s = getCountDocumentiMancanti(riepilogo.getBustaPrequalifica());
			if(s != null) {
				documenti += "- prequalifica: " + s + "\n";
			}
		} else {
			// offerta di gara
			s = getCountDocumentiMancanti(riepilogo.getBustaAmministrativa());
			if(s != null) {
				documenti += "- amministrativa: " + s + "\n";
			}
			
			boolean garaLotti = riepilogo.getListaCompletaLotti() != null && 
			                    riepilogo.getListaCompletaLotti().size() > 0;
			if(garaLotti) {
				// GARA A LOTTI
				for(int i = 0; i < riepilogo.getListaCompletaLotti().size(); i++){
					String lotto = riepilogo.getListaCompletaLotti().get(i);
					
					s = getCountDocumentiMancanti(riepilogo.getBusteTecnicheLotti().get(lotto));
					if(s != null) {
						documenti += "- lotto " + lotto + " tecnica: " + s + "\n";
					}
					s = getCountDocumentiMancanti(riepilogo.getBusteEconomicheLotti().get(lotto));
					if(s != null) {
						documenti += "- lotto " + lotto + " economica: " + s + "\n";
					}
				}
			} else {
				// GARA LOTTO UNICO
				s = getCountDocumentiMancanti(riepilogo.getBustaTecnica());
				if(s != null) {
					documenti += "- tecnica: " + s + "\n";
				}
				
				s = getCountDocumentiMancanti(riepilogo.getBustaEconomica());
				if(s != null) {
					documenti += "- economica: " + s + "\n";
				}
			}
		}	
		
		if(StringUtils.isNotEmpty(documenti)) {
			documenti = "Alcuni documenti richiesti risultano mancanti nelle seguenti buste: \n" + documenti;
		}
		
		return documenti;
	}	
	
	/**
	 * verifica se esistono documenti richiesti mancanti in una busta
	 */
	private String getCountDocumentiMancanti(RiepilogoBustaBean busta) {
		String msg = null;
		String ids = "";
		int n = 0;
		if(busta != null && busta.getDocumentiMancanti() != null && busta.getDocRichiesti() != null) {
			for(int j = 0; j < busta.getDocRichiesti().size(); j++) {
				boolean trovato = false;
				for(int i = 0; i < busta.getDocumentiMancanti().size() && !trovato; i++) {
					trovato = (busta.getDocRichiesti().get(j).isObbligatorio() &&
							   busta.getDocumentiMancanti().get(i).getId() == busta.getDocRichiesti().get(j).getId());
				}
				if( trovato ) {
					n++;
					ids = ids + (StringUtils.isNotEmpty(ids) ? ", " : "") + busta.getDocRichiesti().get(j).getId();
				}
			}
		}
		if(n > 0) {
			msg = n + " (id's: " + ids + ")";
		}
		return msg;
	}

	/**
	 * per le gare a lotti, verifica se i lotti dell'offerta sono utilizzati in altre offerte 
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	private boolean isLottiPresentiInAltreOfferte() throws ApsException {
		boolean lottiDuplicati = false;
		
		WizardPartecipazioneHelper partecipazione = buste.getBustaPartecipazione().getHelper();
		
		if(partecipazione.getLotti() != null) {
			// recupera l'elenco dei lotti dalla gara per evitare la rilettura dal servizio...
			//LottoDettaglioGaraType[] listaLotti = gara.getLotto();
			HashMap<String, LottoDettaglioGaraType> listaLotti = new HashMap<String, LottoDettaglioGaraType>(); 
			for(LottoDettaglioGaraType lotto : buste.getDettaglioGara().getLotto()) {
				listaLotti.put(lotto.getCodiceLotto(), lotto);
			}
			
			boolean invioOfferta = (buste.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);				 
			String tipoComunicazione = (invioOfferta
					? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
					: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT);
	
			String progressivoOfferta = (StringUtils.isNotEmpty(partecipazione.getProgressivoOfferta()) 
					? partecipazione.getProgressivoOfferta() 
					: "");
	 
			// recupera l'elenco delle comunicazioni FS11/FS10 relative ai plici inviati ("progressivi offerta")...
			DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
			criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteriRicerca.setChiave1(buste.getUsername());
			criteriRicerca.setChiave2(partecipazione.getIdBando());
			//criteriRicerca.setChiave3(*);
			//criteriRicerca.setStato(*);
			criteriRicerca.setTipoComunicazione(tipoComunicazione);
			List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);
			
			if(comunicazioni != null && comunicazioni.size() > 0) {
				
				// NB: in caso di gara ristretta in fase di domanda di partecipazione
				// recupera i dati della partecipazione dalla comunicazione...
				if(buste.isRistretta() && buste.isDomandaPartecipazione())	{
					ApsSystemUtils.getLogger().debug("Gara ristretta in fase di prequalifica, " + 
													 "recupera partecipazione (IdComunicazioneTipoPartecipazione=?)... UNDEFINED");					
				}
				
				// controlla se ci sono lotti duplicati nelle altre domande/offerte...
				for(int i = 0; i < comunicazioni.size(); i++) {
					String progOfferta = comunicazioni.get(i).getChiave3();
					
					if( !progressivoOfferta.equals(progOfferta) ) {
					
						ComunicazioneType comunicazione = comunicazioniManager
							.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
										  	  comunicazioni.get(i).getId());
						WizardPartecipazioneHelper p = null;
						try {
							p = new WizardPartecipazioneHelper(comunicazione);
						} catch (XmlException e) {
							throw new AspectException(e.getMessage());
						}
						if(p != null && p.getLotti() != null) {
							// verifica se ci sono lotti presenti in un'altra offerta
							Iterator<String> lotti = p.getLotti().iterator();
							while(lotti.hasNext()) {
								String codLotto = lotti.next();
								if(partecipazione.getLotti().contains(codLotto)) {
									lottiDuplicati = true;
									
									// trova le informazioni del lotto... 
									// NB: il lotto dovrebbe SEMPRE esistere nella lista !!!
									LottoDettaglioGaraType info = listaLotti.get(codLotto);
									String lotto = (info != null ? info.getCodiceInterno() : "");
									String keyMsg = (invioOfferta
													 ? "Errors.invioBusteLotti.lottoDuplicatoInAltraOfferta"
													 : "Errors.invioBusteLotti.lottoDuplicatoInAltraDomanda");
									action.addActionError(action.getText(keyMsg, new String[] { lotto, "#" + progOfferta }));
								}
							}
						}
					}
				}
			}
		}
		
		return lottiDuplicati;
	}
	
	/**
	 * verifica di una busta non solo documenti quindi tecnica o economica 
	 * @throws ApsException 
	 * @throws IOException 
	 * @throws XmlException 
	 */
	private boolean checkBustaOfferta(BustaGara busta) throws ApsException, XmlException, IOException {
		boolean bustaOk = true;
		
		String descrBusta = "";
		if(PortGareSystemConstants.BUSTA_TECNICA == busta.getTipoBusta()) {
			descrBusta = PortGareSystemConstants.BUSTA_TEC;
		} else if(PortGareSystemConstants.BUSTA_ECONOMICA == busta.getTipoBusta()) {
			descrBusta = PortGareSystemConstants.BUSTA_ECO;	
		}
		
		String tipoComunicazione = BustaGara.getTipoComunicazione(busta.getTipoBusta());
		
		List<DocumentazioneRichiestaType> documentiRichiesti;
		DettaglioComunicazioneType dettComunicazioneBustaInviata = ComunicazioniUtilities
				.retrieveComunicazione(
						comunicazioniManager,
						busta.getUsername(),
						busta.getCodiceGara(),
						busta.getProgressivoOfferta(),
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						tipoComunicazione);

		if (dettComunicazioneBustaInviata != null) {
			action.addActionMessage(action.getText("Errors.send.alreadySent", new String[] { descrBusta }));
		} else {
			// verifico se l'ente ha specificato dei documenti obbligatori
			// da inserire nella busta, altrimenti bypasso il check su
			// questa busta
			
			// nella gara a lotto unico i documenti stanno nel lotto, nella gara
			// ad offerta unica stanno nella gara
			String codiceLotto = null;
			if (buste.getDettaglioGara().getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
				codiceLotto = buste.getDettaglioGara().getDatiGeneraliGara().getCodice();
			}
			
			documentiRichiesti = this.bandiManager.getDocumentiRichiestiBandoGara(
					busta.getCodiceGara(), 
					codiceLotto,
					buste.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(), 
					buste.getBustaPartecipazione().getHelper().isRti(),
					busta.getTipoBusta() + "");

			// verifico che ci siano i documenti richiesti per la busta
			DettaglioComunicazioneType dettComunicazioneBusta = ComunicazioniUtilities
					.retrieveComunicazione(
							comunicazioniManager, 
							busta.getUsername(), 
							busta.getCodiceGara(),
							busta.getProgressivoOfferta(),
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							tipoComunicazione);

			if (dettComunicazioneBusta != null) {
				ComunicazioneType comunicazioneBusta = comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, dettComunicazioneBusta.getId());
		
				bustaOk = checkDocumentiOffertaXml(
						busta,
						comunicazioneBusta, 
						descrBusta,
						documentiRichiesti);
			} else {
				bustaOk = false;
				action.addActionError(action.getText("Errors.invioBuste.dataNotSent", new String[] { descrBusta }));
			}
		}
		return bustaOk;
	}

	/**
	 * verifica della documento XML della busta tecnica/economica 
	 */
	private boolean checkDocumentiOffertaXml(
			BustaGara busta,
			ComunicazioneType comunicazioneBusta,
			String nomeBusta,
			List<DocumentazioneRichiestaType> documentiRichiesti) throws XmlException, IOException 
	{
		boolean controlliOk = true;
		try {
			ListaDocumentiType documenti = null;
			if(busta instanceof BustaTecnica) {
				BustaTecnica bustaTec = (BustaTecnica)busta;
				BustaTecnicaDocument bustaDocument = (BustaTecnicaDocument)bustaTec.getBustaDocument();
				documenti = bustaDocument.getBustaTecnica().getDocumenti();
			} else if(busta instanceof BustaEconomica) {
				BustaEconomica bustaEco = (BustaEconomica)busta;
				BustaEconomicaDocument bustaDocument = (BustaEconomicaDocument)bustaEco.getBustaDocument(); //getBustaEconomicaDocument();
				documenti = bustaDocument.getBustaEconomica().getDocumenti();
			}
			if(documenti == null) {
				controlliOk = false;
			}
			if(controlliOk) {
				controlliOk = controlliOk && 
						checkDocumentiBustaXml(nomeBusta, documenti, documentiRichiesti);
			}
		} catch (Throwable ex) {
			if (ex.getMessage() != null && ex.getMessage().equals("Errors.invioBuste.xmlBustaNotFound")) {
				action.addActionError(action.getText("Errors.invioBuste.xmlBustaNotFound", new String[] { nomeBusta }));
			} else {
				action.addActionError(ex.getMessage());
			}
			((EncodedDataAction)action).setTarget(CommonSystemConstants.PORTAL_ERROR);
			controlliOk = false;
		}
		return controlliOk;
	}

	/**
	 * restistuisce la lista dei lotti che presentano anomalie di presa visione di tutti i lotti o documenti obbligatori mancanti
	 */
	private List<String> checkPresaVisioneDocumentiMancantiLotti(
			RiepilogoBusteHelper riepilogo,
			LinkedHashMap<String, Boolean> primoAccessoEffettuato,
			LinkedHashMap<String, RiepilogoBustaBean> lotti,
			String codiceLotto) 
	{
		boolean documentiMancanti = false;
		List<String> listaLotti = new ArrayList<String>();
		if(StringUtils.isNotEmpty(codiceLotto)) {
			documentiMancanti = checkPresaVisioneDocumentiMancantiLotto(lotti.get(codiceLotto), primoAccessoEffettuato, codiceLotto);
			if(documentiMancanti) {
				listaLotti.add(codiceLotto);
			}
		} else {
			for(int i = 0; i < riepilogo.getListaCompletaLotti().size() && !documentiMancanti; i++) {
				String codLotto = riepilogo.getListaCompletaLotti().get(i);
				documentiMancanti = documentiMancanti || checkPresaVisioneDocumentiMancantiLotto(lotti.get(codLotto), primoAccessoEffettuato, codLotto);
				if(documentiMancanti) {
					listaLotti.add(codLotto);
				}
			}
		}
		return listaLotti;
	}

	private boolean checkPresaVisioneDocumentiMancantiLotto(
			RiepilogoBustaBean lotto, 
			LinkedHashMap<String, Boolean> primoAccessoEffettuato, 
			String codiceLotto) 
	{
		boolean documentiMancanti = false;
		if(lotto != null) {
			if( !primoAccessoEffettuato.get(codiceLotto) ) {
				documentiMancanti = true;
			} else {
				for(DocumentoMancanteBean d : lotto.getDocumentiMancanti()) {
					documentiMancanti = documentiMancanti || d.isObbligatorio();
				}
			}
		}
		return documentiMancanti;
	}

	/**
	 * verifica busta per il singolo lotto ("Verifica busta" del lotto) 
	 * @throws IOException 
	 * @throws XmlException 
	 * @throws ApsException 
	 */
	private boolean checkSingoloLotto(BustaDocumenti lotto, String codiceLotto) throws ApsException, XmlException, IOException {
		boolean controlliOk = true;
		
		RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper();
		RiepilogoBustaBean riepilogoLotto = null;
		LinkedHashMap<String, Boolean> primoAccessoEffettuato = null;
		
		if (PortGareSystemConstants.BUSTA_TECNICA == lotto.getTipoBusta()) {
			riepilogoLotto = riepilogo.getBusteTecnicheLotti().get(codiceLotto);
			primoAccessoEffettuato = riepilogo.getPrimoAccessoTecnicheEffettuato();
		} else if (PortGareSystemConstants.BUSTA_ECONOMICA == lotto.getTipoBusta()) {
			riepilogoLotto = riepilogo.getBusteEconomicheLotti().get(codiceLotto);
			primoAccessoEffettuato = riepilogo.getPrimoAccessoEconomicheEffettuato();
		}
		
		if(riepilogoLotto != null) {
			// per prima cosa si controlla se e' stato effettuato un accesso di presa visione documenti
			if( controlliOk && !primoAccessoEffettuato.get(codiceLotto) ) {
				action.addActionError(action.getText("Errors.invioBuste.dataNotSent", 
													 new String[] { BustaGara.getDescrizioneBusta(lotto.getTipoBusta()) }));
				controlliOk = false;
			}
			
			// verifica i documenti obbligatori
			if(controlliOk)
				controlliOk = checkBusta(lotto);
		}
		
		return controlliOk;
	}
	
}

