package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari.GenExcelPrezziUnitariAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari.PrezziUnitariFields;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.interceptor.ParameterAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari.PrezziUnitariFields.PrezziUnitariFieldsBuilder;

public class ProcessPagePrezziUnitariAction extends AbstractProcessPageAction implements ParameterAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6389606008482976487L;

	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;
	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;
	
	/** Contenitore dei prezzi unitari editati, corretti e non. */
	@Validate(EParamValidation.IMPORTO)
	private String[] prezzoUnitario;

	/**
	 * Contenitore degli importi calcolati, in corrispondenza di importi
	 * valorizzati ci sono corrispondenti prezziUnitari significativi e
	 * formalmente validi.
	 */
	@Validate(EParamValidation.IMPORTO)
	private String[] importo;
	
	/** Contenitore dei ribassi percentuali editati, corretti e non. */
	@Validate(EParamValidation.PERCENTUALE)
	private String[] ribassoPercentuale;
	
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String[] dataConsegnaOfferta;
	@Validate(EParamValidation.GENERIC)
	private String[] tipo;
	@Validate(EParamValidation.NOTE)
	private String[] note;

	@Validate(EParamValidation.GENERIC)
	private String[] voceSelezionata;		// indici delle righe di lavorazione selezionate
	
	
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
	
	public String[] getDataConsegnaOfferta() {
		return dataConsegnaOfferta;
	}

	public void setDataConsegnaOfferta(String[] dataConsegnaOfferta) {
		this.dataConsegnaOfferta = dataConsegnaOfferta;
	}

	public String[] getTipo() {
		return tipo;
	}

	public void setTipo(String[] tipo) {
		this.tipo = tipo;
	}

	public String[] getNote() {
		return note;
	}

	public void setNote(String[] note) {
		this.note = note;
	}
	
	public String[] getVoceSelezionata() {
		return voceSelezionata;
	}

	public void setVoceSelezionata(String[] voceSelezionata) {
		this.voceSelezionata = voceSelezionata;
	}

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
			
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice, this)) {
				return CommonSystemConstants.PORTAL_ERROR;
			} 

			boolean controlliOK = true;
			
			boolean ribassiPesati = (helper.getGara().getTipoRibasso() != null &&
									 helper.getGara().getTipoRibasso() == 3);
			boolean negoziata8 = ("8".equals(helper.getGara().getIterGara()));
			
			// ciclo su tutte le righe delle voci di dettaglio
			for (int i = 0; i < helper.getVociDettaglio().size(); i++) {

				// verifica se una riga e' selezionata
				boolean checked = true; 
				if(negoziata8) {
					// se una voce non e' selezionata allora "quantitaOfferta" = 0.0
					checked = !(helper.getVociDettaglio().get(i).getQuantitaOfferta() != null && helper.getVociDettaglio().get(i).getQuantitaOfferta() == 0.0);
				}

				// verifica obbligatoriet� campi aggiuntivi
				if (helper.getValoreAttributiAgg() != null) {
					for (int m = 0; m < helper.getAttributiAggiuntivi().size(); m++) {

						AttributoAggiuntivoOffertaType a = helper.getAttributiAggiuntivi().get(m);
						if (a.isObbligatorio()) {
							// controllo se i valori della colonna del campo
							// obbligatorio, sono tutti presenti
							String[] valori = helper.getValoreAttributiAgg().get(a.getCodice());
							// il valore i = helper.getVociDettaglio().size() �
							// il prezzo unitario e viene verificato al passo
							// successivo
							if (ArrayUtils.isEmpty(valori)
								|| valori[i] == null
								|| valori[i].isEmpty()
								|| (a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_DATA && CalendarValidator.getInstance().validate(valori[i], "dd/MM/yyyy") == null)
								|| (a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO && valori[i].contains("-1"))
								|| (a.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_FLAG && valori[i].contains("-1"))) 
							{
								controlliOK = false;
								addActionError(getText(
												"Errors.offertaTelematica.campo.required",
												new String[] { a.getDescrizione(),
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
						addFieldError(
								"ribassoPercentuale_" + i
								, getText(
										"Errors.offertaTelematica.campo.required",
										new String[] { "Ribasso %",
											  helper.getVociDettaglio().get(i).getCodice(),
											  String.valueOf(i + 1) })
								);
					}
				} else if (helper.getPrezzoUnitario()[i] == null) {
					boolean continua = true;
					if(negoziata8) {
						continua = checked;
					}
					if(continua) {
						controlliOK = false;
						addFieldError(
							"prezzoUnitario_" + i
							, getText(
									"Errors.offertaTelematica.campo.required",
									new String[] { "Prezzo",
										  helper.getVociDettaglio().get(i).getCodice(),
										  String.valueOf(i + 1) })
							);
					}
				}
				
				// ribasso percentuale...
				// ...
				
				// data consegna offerta, tipo e prezzo unitario
				if(negoziata8) {
					if(checked) {
						// data consegna offerta
//						if (helper.getVociDettaglio().get(i).getDataConsegnaOfferta() == null) {
//							controlliOK = false;
//							addFieldError(
//									"dataConsegnaOfferta_" + i
//									, getText(
//											"Errors.offertaTelematica.campo.required",
//											new String[] { "Data consegna offerta",
//												  helper.getVociDettaglio().get(i).getCodice(),
//												  String.valueOf(i + 1) })
//									);
//						}
						
						// tipo
						if(StringUtils.isEmpty(helper.getVociDettaglio().get(i).getTipo())) {
							controlliOK = false;
							addFieldError(
									"tipo_" + i
									, getText(
											"Errors.offertaTelematica.campo.required",
											new String[] { "Tipo",
												  helper.getVociDettaglio().get(i).getCodice(),
												  String.valueOf(i + 1) })
									);
						}
						
						// prezzo unitario <= prezzo a base d'asta e prezzo unitario > 0
						Double prz = null;
						try {
							prz = helper.getPrezzoUnitario()[i];
						} catch (Exception ex) {
							// non dovrebbe mai succedere
							ApsSystemUtils.getLogger().error("ProcessPagePrezziUnitari.next", ex);
						}
						if(prz != null &&
						   helper.getVociDettaglio().get(i).getPrezzoUnitarioBaseGara() != null &&
						   (prz > helper.getVociDettaglio().get(i).getPrezzoUnitarioBaseGara() || prz <= 0)) 
						{
							controlliOK = false;
							if(prz <= 0) {
								this.addActionError(this.getText(
										"Errors.offertaTelematica.importoOfferto.zero") + " (" + helper.getVociDettaglio().get(i).getCodice() + ")");
							} else { 
								this.addActionError(this.getText(
										"Errors.offertaTelematica.importoOfferto.superioreBaseAsta",
										new String[] {helper.getVociDettaglio().get(i).getCodice()}));
							}
						}
					}
				}
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
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();		
		this.nextResultAction = helper.getCurrentAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
		return target;
	}

	/**
	 * Salva i dati inseriti nella form e torna alla pagina principale.
	 */
	public String save() {
		String target = SUCCESS;

		try {
			if (null != this.getCurrentUser()
				&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))
			{
				BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
				WizardOffertaEconomicaHelper helper = bustaEco.getHelper();

				// verifica se action e dati in sessione sono sincronizzati...
				if(!helper.isSynchronizedToAction(this.codice, this))
					return CommonSystemConstants.PORTAL_ERROR;

				int currentCase = GenExcelPrezziUnitariAction.getExportCase();

				// ITERGARA = 8 (negoziata, ricerche di mercato)
				// si recuperano i valori per tutte le righe presenti (selezionate e non)
				// verifica se c'e' almeno una voce selezionata...
				if(currentCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RICERCA_MERCATO && this.voceSelezionata == null) {
					this.addActionError(this.getText(
							"Errors.offertaTelematica.nessunaVoceSelezionata"));
					return INPUT;
				}

				// crea la lista di tutte le voci...
				List<PrezziUnitariFields> voci = creaVoci(currentCase, helper);

				// Salvo i nuovi dati in sessione, ad esclusione degli attributi aggiuntivi
				PrezziUnitariFields.save(voci, currentCase);

				if (CollectionUtils.isNotEmpty(helper.getAttributiAggiuntivi()))
					helper.setValoreAttributiAgg(
							helper.getAttributiAggiuntivi()
								.stream()
								.map(attr -> attribToPair(attr, helper))
							.collect(Collectors.toMap(Pair::getKey, Pair::getValue))
					);

				helper.deleteDocumentoOffertaEconomica(this, eventManager);

				// si rimane nel presente step, tornando alla pagina principale
				this.nextResultAction = helper.getCurrentAction(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);

				// salvare la comunicazione in bozza
				bustaEco.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);

			} else {
				addActionError(getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} catch (IOException t) {
			target = error("Errors.cannotLoadAttachments", t);
		} catch (OutOfMemoryError e) {
			error("Errors.save.outOfMemory", e);
			target = INPUT;
		} catch (Throwable t) {
			target = error(null, t);
		}

		return target;
	}

	/**
	 * crea l'elenco delle voci (selezionate e non) dei prezzi unitari modificati nella pagina 
	 */
	private List<PrezziUnitariFields> creaVoci(int currentCase, WizardOffertaEconomicaHelper helper) {
		// prepara la mappa delle coppie (i, j)  i=indice helper.vociDettalio, j=indice voceSelezionata
		HashMap<Integer, Integer> indexMap = new HashMap<>();
		if (ArrayUtils.isNotEmpty(voceSelezionata)) {
			// iter gara = 8 prezzi con voci selezionabili
			int index = 0;
			while (index < voceSelezionata.length) {
				indexMap.put(toInteger(voceSelezionata[index]), index);
				index++;
			}
		}
		
		// compila l'elenco delle voci (selezionate e non)
		List<PrezziUnitariFields> voci = new ArrayList<>();
		for(int i = 0; i < helper.getVociDettaglio().size(); i++) {  
			// recupera l'indice j dei campi passati dalla action
			Integer j = (ArrayUtils.isNotEmpty(voceSelezionata) ? indexMap.get(i) : new Integer(i));
			j = j != null ? j : new Integer(-1);
			
			PrezziUnitariFields field = new PrezziUnitariFieldsBuilder()
				.setDecimaliRibasso(helper.getNumDecimaliRibasso())
				.setPeso(helper.getVociDettaglio().get(i).getPeso())
				.setPrezzoBase(helper.getVociDettaglio().get(i).getPrezzoUnitario())
				.setRibasso(hasToConvert(ribassoPercentuale, j) ? toDouble(ribassoPercentuale[j]) : null)
				.setQuantita(helper.getVociDettaglio().get(i).getQuantita())
				.setPrezzo(hasToConvert(prezzoUnitario, j) ? toDouble(prezzoUnitario[j]) : null)
				.setNote(ArrayUtils.isNotEmpty(note) && 0 <= j && j < note.length ? note[j] : null)
				.setTipo(ArrayUtils.isNotEmpty(tipo) && 0 <= j && j < tipo.length ? tipo[j] : null)
				.setImportoFromFormula(currentCase)			// (campo calcolato) Valorizza l'importo in vase alla tipologia di prezzo unitario
				.setRibassoPesatoFromFormula(currentCase)	// (campo calcolato) Non fa niente se non e' un ribasso
				.setPrezzoFromFormula(currentCase)			// (campo calcolato) Non fa niente se non e' un ribasso
			.build();

			// imposta il flag "selezionato"
			field.setIsOfferta(j != -1);
			
			voci.add(field);
		}
		
		return voci;
	}

	private boolean hasToConvert(String[] array, int j) {
		return ArrayUtils.isNotEmpty(array) && 0 <= j && j < array.length && StringUtils.isNotEmpty(array[j]);
	}
	
	private int toInteger(String toConvert) {
		return Integer.parseInt(toConvert);
	}
	
	private Double toDouble(String toConvert) {
		return Double.parseDouble(toConvert);
	}
	
	private String error(String textKey, Throwable t) {
		ApsSystemUtils.logThrowable(t, this, "save");
		if (StringUtils.isNotEmpty(textKey)) {
			addActionError(getText("Errors.save.outOfMemory"));
		} else {
			ExceptionUtils.manageExceptionError(t, this);
		}
		return ERROR;
	}

	private Pair<String, String[]> attribToPair(AttributoAggiuntivoOffertaType attr, WizardOffertaEconomicaHelper helper) {
		String[] elencoValoriAttributo = getParameters().get(attr.getCodice());
		// solo per il campo data, verifico che non ci siano date errate (31/06, 29/02 non bisestili...)
		if (attr.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_DATA) {
			validateDateAttribute(attr, elencoValoriAttributo, helper);
		}
		return Pair.of(attr.getCodice(), elencoValoriAttributo);
	}

	private void validateDateAttribute(AttributoAggiuntivoOffertaType attr, String[] elencoValoriAttributo, WizardOffertaEconomicaHelper helper) {
		for (int i = 0; i < elencoValoriAttributo.length; i++) {
			if (StringUtils.isEmpty(elencoValoriAttributo[i])) {
				elencoValoriAttributo[i] = null;
			} else if (CalendarValidator.getInstance().validate(elencoValoriAttributo[i], "dd/MM/yyyy") == null) {
				addActionError(this.getText(
						"Errors.offertaTelematica.campo.notValid",
						new String[] {
								attr.getDescrizione(),
								helper.getVociDettaglio().get(i).getCodice(),
								String.valueOf(i + 1),
								elencoValoriAttributo[i] })
				);
				elencoValoriAttributo[i] = null;
			}
		}
	}

//	private static Function<AttributoAggiuntivoOffertaType, String> getGetAttrCodice() {
//		return AttributoAggiuntivoOffertaType::getCodice;
//	}

}
