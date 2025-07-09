package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.ASTA })
public class ProcessPagePrezziUnitariAction extends AbstractProcessPageAction implements ParameterAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1522555514780829262L;

	
	private IComunicazioniManager comunicazioniManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;

	/** 
	 * Contenitore dei prezzi unitari editati, corretti e non. 
	 */
	@Validate(EParamValidation.IMPORTO)
	private String[] prezzoUnitario;

	/**
	 * Contenitore degli importi calcolati, in corrispondenza di importi
	 * valorizzati ci sono corrispondenti prezziUnitari significativi e
	 * formalmente validi.
	 */
	@Validate(EParamValidation.IMPORTO)
	private String[] importo;

	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public String[] getPrezzoUnitario() {
		return prezzoUnitario;
	}

	public void setPrezzoUnitario(String[] prezzoUnitario) {
		this.prezzoUnitario = prezzoUnitario;
	}

	public String[] getImporto() {
		return importo;
	}

	public void setImporto(String[] importo) {
		this.importo = importo;
	}

	/**
	 * passa allo step successivo del wizard
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
		
		if (helper != null) {
			boolean controlliOK = true;

//			// ciclo su tutte le righe
//			for (int i = 0; i < helper.getVociDettaglio().size(); i++) {
//
//				// verifica obbligatorietà campi aggiuntivi
//				if (helper.getValoreAttributiAgg() != null) {
//					for (int m = 0; m < helper.getAttributiAggiuntivi().size(); m++) {
//
//						AttributoAggiuntivoOffertaType a = helper
//								.getAttributiAggiuntivi().get(m);
//						if (a.isObbligatorio()) {
//							// controllo se i valori della colonna del campo
//							// obbligatorio, sono tutti presenti
//							String[] valori = helper.getValoreAttributiAgg()
//									.get(a.getCodice());
//							// il valore i = helper.getVociDettaglio().size() è
//							// il prezzo unitario e viene verificato al passo
//							// successivo
//							if (ArrayUtils.isEmpty(valori)
//									|| valori[i] == null
//									|| valori[i].isEmpty()
//									|| (valori[i].contains("-1") && a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO)
//									|| (valori[i].contains("-1") && a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_FLAG)) {
//								controlliOK = false;
//								this.addActionError(this
//										.getText(
//												"Errors.offertaTelematica.campo.required",
//												new String[] {
//														a.getDescrizione(),
//														helper.getVociDettaglio()
//														.get(i)
//														.getCodice(),
//														String.valueOf(i + 1) }));
//							}
//						}
//					}
//				}
//				if (helper.getPrezzoUnitario()[i] == null) {
//					controlliOK = false;
//					this.addActionError(this
//							.getText(
//									"Errors.offertaTelematica.campo.required",
//									new String[] {
//											"PrezzoUnitario",
//											helper.getVociDettaglio().get(i)
//											.getCodice(),
//											String.valueOf(i + 1) }));
//				}
//			}
//
//			if (controlliOK) {
//				this.nextResultAction = InitOffertaEconomicaAction
//						.setNextResultAction(helper
//								.getNextStepNavigazione(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI));
//			} else {
//				// in caso di errori si rimane nello step corrente
//				this.nextResultAction = InitOffertaEconomicaAction
//						.setNextResultAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
//			}

		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	/**
	 * Annulla l'operazione di editing della finestra e si ricaricano i dati.
	 */
	public String undo() {
		String target = SUCCESS;
//		this.nextResultAction = InitOffertaEconomicaAction
//				.setNextResultAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
		return target;
	}

	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public String save() {
		String target = SUCCESS;
		Map<String, String[]> paramsMap;
		Map<String, String[]> valoriAltriAttributi = new HashMap<String, String[]>();

		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();

		if (helper != null) {
//			// controllare i dati e salvarli nell'helper
//			Double[] prezzi = new Double[this.importo.length];
//			Double[] importi = new Double[this.importo.length];
//			double importoTotaleOffertaPrezziUnitari = 0;
//
//			// Recupero i parametri del form
//			paramsMap = this.getParameters();
//
//			Iterator<AttributoAggiuntivoOffertaType> attAggIterator = helper
//					.getAttributiAggiuntivi().iterator();
//			while (attAggIterator.hasNext()) {
//				AttributoAggiuntivoOffertaType attr = attAggIterator.next();
//				valoriAltriAttributi.put(attr.getCodice(),
//						paramsMap.get(attr.getCodice()));
//			}
//
//			helper.setValoreAttributiAgg(valoriAltriAttributi);
//
//			for (int i = 0; i < this.importo.length; i++) {
//				String importoVoceString = StringUtils
//						.stripToNull(this.importo[i]);
//				if (importoVoceString != null) {
//					// se l'importo e' valido allora il prezzo esiste ed e'
//					// valido
//					prezzi[i] = Double.parseDouble(this.prezzoUnitario[i]);
//					importi[i] = Double.parseDouble(importoVoceString);
//					importoTotaleOffertaPrezziUnitari += importi[i];
//				}
//			}
//			helper.setPrezzoUnitario(prezzi);
//			helper.setImportoUnitario(importi);
//			BigDecimal bdImportoTotaleOffertaPrezziUnitari = BigDecimal
//					.valueOf(new Double(importoTotaleOffertaPrezziUnitari))
//					.setScale(5, BigDecimal.ROUND_HALF_UP);
//			helper.setTotaleOffertaPrezziUnitari(bdImportoTotaleOffertaPrezziUnitari
//					.doubleValue());
//
//			// calcolo il totale offerto come somma dei prezzi unitari ed altre
//			// componenti dell'importo a base di gara
//			double importoSicurezza = 0;
//			if (helper.getGara().isOffertaComprensivaSicurezza()
//					&& helper.getGara().getImportoSicurezza() != null) {
//				importoSicurezza = helper.getGara().getImportoSicurezza();
//			}
//			double importoNonSoggettoRibasso = 0;
//			if (helper.getGara().getImportoNonSoggettoRibasso() != null) {
//				importoNonSoggettoRibasso = helper.getGara()
//						.getImportoNonSoggettoRibasso();
//			}
//			double importoOneriProgettazione = 0;
//			if (!helper.getGara().isOneriSoggettiRibasso()
//					&& helper.getGara().getImportoOneriProgettazione() != null) {
//				importoOneriProgettazione = helper.getGara()
//						.getImportoOneriProgettazione();
//			}
//			double importoOfferto = helper.getTotaleOffertaPrezziUnitari()
//					+ importoSicurezza + importoNonSoggettoRibasso
//					+ importoOneriProgettazione;
//			BigDecimal bdImportoOfferto = BigDecimal.valueOf(
//					new Double(importoOfferto)).setScale(5,
//							BigDecimal.ROUND_HALF_UP);
//			helper.setImportoOfferto(bdImportoOfferto.doubleValue());
//
//			// va rigenerato il pdf, quindi elimino l'eventuale file e resetto
//			// il flag
//			helper.deleteDocumentoOffertaEconomica();
//			helper.setRigenPdf(true);
//			// sbianco lo UUID
//			helper.setPdfUUID(null);
//
//			// si rimane nel presente step, tornando alla pagina principale
//			this.nextResultAction = InitOffertaEconomicaAction
//					.setNextResultAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
//
//			// salvare la comunicazione in bozza
//			try {
//				ProcessPageDocumentiOffertaAction.sendComunicazioneBusta(this,
//						this.comunicazioniManager, helper,
//						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
//
//				/* ----- RIALLINEAMENTO BUSTA RIEPILOGATIVA ----- */
//				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
//				if(bustaRiepilogativa.getBustaEconomica() != null){
//					/* --- LOTTO UNICO --- */
//					bustaRiepilogativa.getBustaEconomica().riallineaDocumenti(helper.getDocumenti());
//				}else{
//					/* --- LOTTI DISTINTI --- */
//					//ho bisogno qua del codice lotto
//					bustaRiepilogativa.getBusteEconomicheLotti().get(helper.getCodice()).riallineaDocumenti(helper.getDocumenti());
//				}
//				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA, helper.getDocumenti());
//
//				/* ----- INVIO COMUNICAZIONE BUSTA RIEPILOGATIVA ----- */
//				//Preparazione per invio busta riepilogativa
//				String username = this.getCurrentUser().getUsername();
//				String ragioneSociale = helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
//				String nomeBusta = "busta riepilogativa";
//
//				String oggetto = this.getText("label.invioBuste.oggetto", new String[]{nomeBusta, helper.getGara().getCodice()});
//				String testo = this.getText("label.invioBuste.testo", new String[]{StringUtils.left(ragioneSociale, 200), nomeBusta});
//				String descrizioneFile = this.getText("label.invioBuste.allegatoIscrizione.descrizione", new String[]{nomeBusta});
//
//				//Invio la nuova comunicazione con gli allineamenti del caso
//				bustaRiepilogativa.sendComunicazioneBusta(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA, 
//						helper.getImpresa(), username, helper.getGara().getCodice(), ragioneSociale, nomeBusta, 
//						oggetto, testo, descrizioneFile, comunicazioniManager, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO);
//
//			} catch (ApsException t) {
//				ApsSystemUtils.logThrowable(t, this, "save");
//				ExceptionUtils.manageExceptionError(t, this);
//				target = ERROR;
//	    } catch (GeneralSecurityException e) {
//			ApsSystemUtils.logThrowable(e, this, "save");
//			ExceptionUtils.manageExceptionError(e, this);
//			target = ERROR;
//			} catch (IOException t) {
//				ApsSystemUtils.logThrowable(t, this, "save");
//				this.addActionError(this
//						.getText("Errors.cannotLoadAttachments"));
//				target = ERROR;
//			} catch (OutOfMemoryError e) {
//				ApsSystemUtils.logThrowable(e, this, "save");
//				this.addActionError(this.getText("Errors.save.outOfMemory"));
//				target = INPUT;
//			}

		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

}
