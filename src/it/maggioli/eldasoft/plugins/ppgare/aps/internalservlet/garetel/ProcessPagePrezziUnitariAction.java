package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.interceptor.ParameterAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

public class ProcessPagePrezziUnitariAction extends AbstractProcessPageAction implements ParameterAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6389606008482976487L;

	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;
	private String codice;
	private int operazione;
	
	/** Contenitore dei prezzi unitari editati, corretti e non. */
	private String[] prezzoUnitario;

	/**
	 * Contenitore degli importi calcolati, in corrispondenza di importi
	 * valorizzati ci sono corrispondenti prezziUnitari significativi e
	 * formalmente validi.
	 */
	private String[] importo;
	
	/** Contenitore dei ribassi percentuali editati, corretti e non. */
	private String[] ribassoPercentuale;

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
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

	public String[] getRibassoPercentuale() {
		return ribassoPercentuale;
	}

	public void setRibassoPercentuale(String[] ribassoPercentuale) {
		this.ribassoPercentuale = ribassoPercentuale;
	}
	
	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) session
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
			
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice, this)) {
				return CommonSystemConstants.PORTAL_ERROR;
			} 

			boolean controlliOK = true;
			
			boolean ribassiPesati = (helper.getGara().getTipoRibasso() != null &&
									 helper.getGara().getTipoRibasso() == 3);

			// ciclo su tutte le righe delle voci di dettaglio
			for (int i = 0; i < helper.getVociDettaglio().size(); i++) {

				// verifica obbligatorietà campi aggiuntivi
				if (helper.getValoreAttributiAgg() != null) {
					for (int m = 0; m < helper.getAttributiAggiuntivi().size(); m++) {

						AttributoAggiuntivoOffertaType a = helper.getAttributiAggiuntivi().get(m);
						if (a.isObbligatorio()) {
							// controllo se i valori della colonna del campo
							// obbligatorio, sono tutti presenti
							String[] valori = helper.getValoreAttributiAgg().get(a.getCodice());
							// il valore i = helper.getVociDettaglio().size() è
							// il prezzo unitario e viene verificato al passo
							// successivo
							if (ArrayUtils.isEmpty(valori)
								|| valori[i] == null
								|| valori[i].isEmpty()
								|| (a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_DATA && CalendarValidator.getInstance().validate(valori[i], "dd/MM/yyyy") == null)
								|| (a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO && valori[i].contains("-1"))
								|| (a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_FLAG && valori[i].contains("-1"))) {
								controlliOK = false;
								this.addActionError(this.getText(
												"Errors.offertaTelematica.campo.required",
												new String[] {a.getDescrizione(),
															  helper.getVociDettaglio().get(i).getCodice(),
															  String.valueOf(i + 1) }));
							}
						}
					}
				}

				// verifica il caso del ribasso pesato... 
				if (ribassiPesati) {
					if(helper.getVociDettaglio().get(i).getRibassoPercentuale() == null) {
						controlliOK = false;
						this.addActionError(this.getText(
										"Errors.offertaTelematica.campo.required",
										new String[] {"Ribasso %",
													  helper.getVociDettaglio().get(i).getCodice(),
													  String.valueOf(i + 1) }));
					}
				} else if (helper.getPrezzoUnitario()[i] == null) {
					controlliOK = false;
					this.addActionError(this.getText(
									"Errors.offertaTelematica.campo.required",
									new String[] {"Prezzo",
												  helper.getVociDettaglio().get(i).getCodice(),
												  String.valueOf(i + 1) }));
				}
				
				// ribasso percentuale...
				// ...
			}

			if (controlliOK) {
				this.nextResultAction = helper.getNextAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
			} else {
				// in caso di errori si rimane nello step corrente
				this.nextResultAction = helper.getCurrentAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
			}

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
		WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		this.nextResultAction = helper.getCurrentAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
		return target;
	}

	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public String save() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);

			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice, this)) {
				return CommonSystemConstants.PORTAL_ERROR;
			} 

			// controllare i dati e salvarli nell'helper
			// NB: importo[], prezzoUnitario e ribassoPercentuale[] 
			//     dovrebbero avere sempre le stesse dimensioni, 
			//     incaso contrario si accetta l'eccezione ava.lang.ArrayIndexOutOfBoundsException!!!
			int inputSize = this.importo.length;
			Double[] prezzi = new Double[inputSize];
			Double[] importi = new Double[inputSize];
			Double[] ribassiPercentuali = new Double[inputSize];
			double importoTotaleOffertaPrezziUnitari = 0;

			// Recupero i parametri del form
			Map<String, String[]> paramsMap = this.getParameters();
			Map<String, String[]> valoriAltriAttributi = new HashMap<String, String[]>();
			
			// attributi aggiuntivi
			Iterator<AttributoAggiuntivoOffertaType> attAggIterator = helper.getAttributiAggiuntivi().iterator();
			while (attAggIterator.hasNext()) {
				AttributoAggiuntivoOffertaType attr = attAggIterator.next();
				String[] elencoValoriAttributo = paramsMap.get(attr.getCodice());
				valoriAltriAttributi.put(attr.getCodice(), elencoValoriAttributo);
				// solo per il campo data, verifico che non ci siano date errate (31/06, 29/02 non bisestili...)
				if (attr.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_DATA) {
					for (int i = 0; i < elencoValoriAttributo.length; i++) {
						if (StringUtils.isEmpty(elencoValoriAttributo[i])) {
							elencoValoriAttributo[i] = null;
						} else if (CalendarValidator.getInstance().validate(elencoValoriAttributo[i], "dd/MM/yyyy") == null) {
							this.addActionError(this.getText(
									"Errors.offertaTelematica.campo.notValid",
									new String[] {attr.getDescrizione(),
												  helper.getVociDettaglio().get(i).getCodice(),
												  String.valueOf(i + 1),
												  elencoValoriAttributo[i] }));
							elencoValoriAttributo[i] = null;
						}
					}
				}
			}
			helper.setValoreAttributiAgg(valoriAltriAttributi);

			// peso, ribasso percentuale e ribasso pesato
			boolean ribassiPesati = (helper.getGara().getTipoRibasso() != null &&
					 				 helper.getGara().getTipoRibasso() == 3);

			if(ribassiPesati) {
				for (int i = 0; i < inputSize; i++) {
					ribassiPercentuali[i] = null;
					
					double ribassoPesato = 0;
					String ribassoPercString = (this.ribassoPercentuale[i] != null 
												? StringUtils.stripToNull(this.ribassoPercentuale[i]) : null);
					if (ribassoPercString != null) {
						ribassiPercentuali[i] = Double.parseDouble(ribassoPercString);
						if(helper.getVociDettaglio().get(i).getPeso() != null) { 
							ribassoPesato = helper.getVociDettaglio().get(i).getPeso() * ribassiPercentuali[i] / 100;
						}
					}
					
					// aggiorna nell'helper le voci di dettaglio calcolate 
					// che non vengono recuperate dal servizio (ribasso percentuale e ribasso pesato) 
					helper.getVociDettaglio().get(i).setRibassoPercentuale( ribassiPercentuali[i] );
					
					BigDecimal bdRibassoPesato = BigDecimal.valueOf(ribassoPesato)
													.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP);
					helper.getVociDettaglio().get(i).setRibassoPesato(bdRibassoPesato.doubleValue());
				}
			}

			// importo e prezzo unitario 
			for (int i = 0; i < inputSize; i++) {
				String importoVoceString = StringUtils.stripToNull(this.importo[i]);
				if (importoVoceString != null) {
					// se l'importo e' valido allora il prezzo esiste ed e' valido
					prezzi[i] = Double.parseDouble(this.prezzoUnitario[i]);
					importi[i] = Double.parseDouble(importoVoceString);
					importoTotaleOffertaPrezziUnitari += importi[i];
				}
			}
			helper.setPrezzoUnitario(prezzi);
			helper.setImportoUnitario(importi);
			BigDecimal bdImportoTotaleOffertaPrezziUnitari = BigDecimal
					.valueOf(new Double(importoTotaleOffertaPrezziUnitari))
					.setScale(5, BigDecimal.ROUND_HALF_UP);
			helper.setTotaleOffertaPrezziUnitari(bdImportoTotaleOffertaPrezziUnitari.doubleValue());

			// calcolo il totale offerto come somma dei prezzi unitari ed altre
			// componenti dell'importo a base di gara
			double importoOfferto = helper.getTotaleOffertaPrezziUnitari() +
									helper.getImportoSicurezzaNonSoggettoRibassoOneriProgettazione();			
			BigDecimal bdImportoOfferto = BigDecimal.valueOf(
					new Double(importoOfferto)).setScale(5, BigDecimal.ROUND_HALF_UP);
			helper.setImportoOfferto(bdImportoOfferto.doubleValue());

			// va rigenerato il pdf, quindi elimino l'eventuale file e resetto
			// il flag, e si sbianca lo UUID
			helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
			helper.setRigenPdf(true);
			helper.setPdfUUID(null);

			// si rimane nel presente step, tornando alla pagina principale
			this.nextResultAction = helper.getCurrentAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);

			// salvare la comunicazione in bozza
			try {
				ProcessPageDocumentiOffertaAction.sendComunicazioneBusta(
						this,
						this.comunicazioniManager,
						this.eventManager,
						helper,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

				// ----- RIALLINEAMENTO BUSTA RIEPILOGATIVA ----- 
				RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
				if(bustaRiepilogativa.getBustaEconomica() != null) {
					// --- LOTTO UNICO --- 
					bustaRiepilogativa.getBustaEconomica().riallineaDocumenti(helper.getDocumenti());
				} else {
					// --- LOTTI DISTINTI ---
					//ho bisogno qua del codice lotto
					bustaRiepilogativa.getBusteEconomicheLotti().get(helper.getCodice()).riallineaDocumenti(helper.getDocumenti());
				}
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA, helper.getDocumenti());

				// ----- INVIO COMUNICAZIONE BUSTA RIEPILOGATIVA -----
				// Preparazione per invio busta riepilogativa
				// ed invia la nuova comunicazione con gli allineamenti del caso
				bustaRiepilogativa.sendComunicazioneBusta(
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA, 
						helper.getImpresa(), 
						this.getCurrentUser().getUsername(), 
						helper.getGara().getCodice(), 
						helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale(), 
						this.comunicazioniManager, 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO,
						this);

			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				ExceptionUtils.manageExceptionError(t, this);
				target = ERROR;
		    } catch (GeneralSecurityException e) {
				ApsSystemUtils.logThrowable(e, this, "save");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			} catch (IOException t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
				target = ERROR;
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "save");
				this.addActionError(this.getText("Errors.save.outOfMemory"));
				target = INPUT;
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "save");
				ExceptionUtils.manageExceptionError(t, this);
				target = ERROR;
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

}
