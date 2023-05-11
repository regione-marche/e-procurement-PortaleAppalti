package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.math.BigDecimal;

public class OpenPageOffertaAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6698784993774103558L;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	private int operazione;

	@Validate(EParamValidation.IMPORTO)
	private String importoOfferto;
	@Validate(EParamValidation.PERCENTUALE)
	private String ribasso;
	@Validate(EParamValidation.PERCENTUALE)
	private String aumento;
	@Validate(EParamValidation.IMPORTO)
	private String costiSicurezzaAziendali;
	@Validate(EParamValidation.IMPORTO)
	private String importoManodopera;
	@Validate(EParamValidation.CODICE)
	private String percentualeManodopera;
	
	/* --- PERMUTA E ASSISTENZA --- */
	@Validate(EParamValidation.IMPORTO)
	private String importoOffertoPerPermuta;
	@Validate(EParamValidation.IMPORTO)
	private String importoOffertoCanoneAssistenza;
	
	private boolean obblImportoOfferto;
	private boolean costiManodoperaObbligatori;
	@Validate(EParamValidation.GENERIC)
	private String[] criterioValutazione;
	private boolean percentualeManodoperaVisibile;

	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }
	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }
	public int getBUSTA_TECNICA() { return PortGareSystemConstants.BUSTA_TECNICA; }
	public int getBUSTA_PRE_QUALIFICA() { return PortGareSystemConstants.BUSTA_PRE_QUALIFICA; }

	public String getSTEP_PREZZI_UNITARI() { return WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI; }
	public String getSTEP_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_OFFERTA; }
	public String getSTEP_SCARICA_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA; }	
	public String getSTEP_DOCUMENTI() { return WizardOffertaEconomicaHelper.STEP_DOCUMENTI; }
		
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
	
	public boolean isCostiManodoperaObbligatori() {
		return costiManodoperaObbligatori;
	}
	
	public void setCostiManodoperaObbligatori(boolean costiManodoperaObbligatori) {
		this.costiManodoperaObbligatori = costiManodoperaObbligatori;
	}
	
	public String getImportoOfferto() {
		return importoOfferto;
	}

	public void setImportoOfferto(String importoOfferto) {
		this.importoOfferto = importoOfferto;
	}

	public String getRibasso() {
		return ribasso;
	}

	public void setRibasso(String ribasso) {
		this.ribasso = ribasso;
	}

	public String getAumento() {
		return aumento;
	}

	public void setAumento(String aumento) {
		this.aumento = aumento;
	}

	public String getCostiSicurezzaAziendali() {
		return costiSicurezzaAziendali;
	}

	public void setCostiSicurezzaAziendali(String costiSicurezzaAziendali) {
		this.costiSicurezzaAziendali = costiSicurezzaAziendali;
	}
	
	public String getImportoManodopera() {
		return importoManodopera;
	}

	public void setImportoManodopera(String importoManodopera) {
		this.importoManodopera = importoManodopera;
	}
	
	public String getPercentualeManodopera() {
		return percentualeManodopera;
	}
	
	public void setPercentualeManodopera(String percentualeManodopera) {
		this.percentualeManodopera = percentualeManodopera;
	}
	
	public boolean isObblImportoOfferto() {
		return obblImportoOfferto;
	}
	
	public String getImportoOffertoPerPermuta() {
		return importoOffertoPerPermuta;
	}

	public void setImportoOffertoPerPermuta(String importoOffertoPerPermuta) {
		this.importoOffertoPerPermuta = importoOffertoPerPermuta;
	}

	public String getImportoOffertoCanoneAssistenza() {
		return importoOffertoCanoneAssistenza;
	}

	public void setImportoOffertoCanoneAssistenza(String importoOffertoCanoneAssistenza) {
		this.importoOffertoCanoneAssistenza = importoOffertoCanoneAssistenza;
	}
	
	public boolean isPercentualeManodoperaVisibile() {
		return percentualeManodoperaVisibile;
	}
	
	public void setPercentualeManodoperaVisibile(boolean percentualeManodoperaVisibile) {
		this.percentualeManodoperaVisibile = percentualeManodoperaVisibile;
	}
	
	public String[] getCriterioValutazione() {
		return criterioValutazione;
	}
	
	public void setCriterioValutazione(String[] criterioValutazione) {
		this.criterioValutazione = criterioValutazione;
	}

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession(); 
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();

		// aggiorna i dati nel bean a partire da quelli presenti in sessione
		
		// --- CRITERI DI VALUTAZIONE ---
		if(helper.isCriteriValutazioneVisibili()) {			
			this.sincronizzaCriteriValutazione(helper);
			
			// prepare i valori da visualizzare nella form...
			if(this.criterioValutazione == null && helper.getListaCriteriValutazione() != null) {
				this.criterioValutazione = new String[helper.getListaCriteriValutazione().size()];
				for(int i = 0; i < helper.getListaCriteriValutazione().size(); i++) {
					this.criterioValutazione[i] = helper.getListaCriteriValutazione().get(i).getValore();
				}
			}
		}		
				
		// IMPORTO OFFERTO, 
		// AUMENTO, RIBASSO, 
		// COSTI SICUREZZA, 
		// IMPORTO MANODOPERA
		int numDecimaliRibasso = (helper.getNumDecimaliRibasso() != null && helper.getNumDecimaliRibasso().intValue() > 0
				? helper.getNumDecimaliRibasso().intValue()
				: 1); 
		
		BigDecimal bd = null;
		if (helper.getImportoOfferto() != null) {
			bd = BigDecimal.valueOf(new Double(helper.getImportoOfferto()))
					.setScale(5, BigDecimal.ROUND_HALF_UP);
			this.setImportoOfferto(bd.toPlainString());
		}
		
		if (helper.getAumento() != null) {
			bd = BigDecimal.valueOf(new Double(helper.getAumento())).setScale(
					numDecimaliRibasso,
					BigDecimal.ROUND_HALF_UP);
			this.setAumento(bd.toPlainString());
		}
		
		if (helper.getRibasso() != null) {
			bd = BigDecimal.valueOf(new Double(helper.getRibasso())).setScale(
					numDecimaliRibasso,
					BigDecimal.ROUND_HALF_UP);
			this.setRibasso(bd.toPlainString());
		}
		
		if (helper.getCostiSicurezzaAziendali() != null) {
			bd = BigDecimal
					.valueOf(new Double(helper.getCostiSicurezzaAziendali()))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			this.setCostiSicurezzaAziendali(bd.toPlainString());
		}
		
		if (helper.getImportoManodopera() != null) {
			bd = BigDecimal
					.valueOf(new Double(helper.getImportoManodopera()))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			this.setImportoManodopera(bd.toPlainString());
		}
		
		if (helper.getPercentualeManodopera() != null) {
			bd = BigDecimal
					.valueOf(new Double(helper.getPercentualeManodopera()))
					.setScale(5, BigDecimal.ROUND_HALF_UP);
			this.setPercentualeManodopera(bd.toPlainString());
		}
		
		this.percentualeManodoperaVisibile = helper.isPercentualeManodoperaVisible();

		this.obblImportoOfferto = helper.isImportoOffertoMandatory() && 
								  !helper.isCriteriValutazioneVisibili();
		
		this.costiManodoperaObbligatori = helper.isCostiManodoperaVisible();
		
		// --- PERMUTA E CANONE ASSISTENZA ---
		if(helper.getImportoOffertoCanoneAssistenza() != null) {
			bd = BigDecimal
					.valueOf(new Double(helper.getImportoOffertoCanoneAssistenza()))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			this.setImportoOffertoCanoneAssistenza(bd.toPlainString());	
		}
		
		if(helper.getImportoOffertoPerPermuta() != null) {
			bd = BigDecimal
					.valueOf(new Double(helper.getImportoOffertoPerPermuta()))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
			this.setImportoOffertoPerPermuta(bd.toPlainString());	
		}
		
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
					 	 WizardOffertaEconomicaHelper.STEP_OFFERTA);

		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String openPageAfterError() {
		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession(); 
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
		
		this.obblImportoOfferto = helper.isImportoOffertoMandatory() && 
		  						  !helper.isCriteriValutazioneVisibili();

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
						 WizardOffertaEconomicaHelper.STEP_OFFERTA);

		return this.getTarget();
	}
		
	/**
	 * Aggiorna i criteri "offerta a prezzi unitari", "offerta a importo"...
	 */
	private void  sincronizzaCriteriValutazione(WizardOffertaEconomicaHelper helper) {
		// Il caso particolare di G1CRIREG.FORMATO=52 sostituisce a tutti  
		// gli effetti la gestione del campo nascosto "Importo offerto", 
		// pertanto:
		//  - riportare l’eventuale somma delle lavorazioni forniture dello 
		//    step “Prezzi unitari” (se previsto) e quindi bloccarne 
		//    l’inputazione del dato con le stesse modalita' 
		//  - applicarne i medesimi controlli
		//  - qualsiasi altro controllo che referenzia il campo "Importo offerto" 
		//    deve essere riportato nel criterio con formato 52 ed anche in 
		//    quello con formato 50.
		// Inoltre lo step "Prezzi unitari" va disattivato se non e' presente 
		// il criterio di valutazione con formato 52 "Offerta complessiva 
		// mediante prezzi unitari".
		// Ogni criterio di valutazione prevede l'inserimento obbligatorio 
		// ed inoltre va controllato nel rispetto del formato e dominio
		boolean aggiorna = false;
		double qta, prz;
		double totNoRibasso;
		double totVociDettaglio;
		double totSicurezzaNonSoggettoOneri;
		double tot;
		
		if(helper.getListaCriteriValutazione() != null) {
			for(int i = 0; i < helper.getListaCriteriValutazione().size(); i++) {			
				if(helper.getListaCriteriValutazione().get(i).getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {	
					
					String valore = (helper.getListaCriteriValutazione().get(i) != null 
								? helper.getListaCriteriValutazione().get(i).getValore() 
								: null);
											
					if(helper.getListaCriteriValutazione().get(i).getFormato() == PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO) {
						///////////////////////////////////////////////////////////////////////////
						// 26/04/2019: 
						// Stessi controlli di CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI, 
						// vanno presi in considerazione gli importi di sicurezza, 
						// non soggetto a ribasso oneri progettazione
						///////////////////////////////////////////////////////////////////////////
						//helper.setImportoOfferto( Double.valueOf(valore) );
						aggiorna = false;
						totSicurezzaNonSoggettoOneri = helper.getImportoSicurezzaNonSoggettoRibassoOneriProgettazione();						
						if(totSicurezzaNonSoggettoOneri != 0) {
							aggiorna = true;
						}
						if(aggiorna) {
							tot = (valore != null 
									? Double.valueOf(valore) - totSicurezzaNonSoggettoOneri 
									: 0.0);
							tot = tot + totSicurezzaNonSoggettoOneri;
							helper.getListaCriteriValutazione().get(i).setValore(Double.toString(tot));										
							helper.setImportoOfferto(tot);
						}
					}
					
					if(helper.getListaCriteriValutazione().get(i).getFormato() == PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI) {
						aggiorna = false;
						totVociDettaglio = 0.0;	
						totSicurezzaNonSoggettoOneri = helper.getImportoSicurezzaNonSoggettoRibassoOneriProgettazione();
						if(totSicurezzaNonSoggettoOneri != 0) {
							aggiorna = true;
						}
						if(helper.getVociDettaglio() != null && helper.getVociDettaglio().size() > 0) {
							// ricalcola il totale dell'offerta a prezzi unitari...
							aggiorna = true;								
							for(int j = 0; j < helper.getVociDettaglio().size(); j++) {
								totVociDettaglio += helper.getImportoUnitario()[j];
							}
						}
						if(aggiorna) {								
							tot = totVociDettaglio + totSicurezzaNonSoggettoOneri;
							// imposta il valore del criterio con il totale dei 
							// prezzi unitari...
							if(helper.getListaCriteriValutazione().get(i).getNumeroDecimali() != null){
								tot = (BigDecimal.valueOf(
										new Double(tot)).setScale(helper.getListaCriteriValutazione().get(i).getNumeroDecimali(),
												BigDecimal.ROUND_HALF_UP)).doubleValue();
							}
							helper.setTotaleOffertaPrezziUnitari(totVociDettaglio);
							helper.setImportoOfferto(tot);
							// imposta il criterio come non editabile...
							helper.getListaCriteriValutazione().get(i).setValore(helper.getImportoOffertoNotazioneStandard());										
							helper.getCriterioValutazioneEditabile()[i] = false;							
						}					
					}
				}
			}
		}
	}

}
