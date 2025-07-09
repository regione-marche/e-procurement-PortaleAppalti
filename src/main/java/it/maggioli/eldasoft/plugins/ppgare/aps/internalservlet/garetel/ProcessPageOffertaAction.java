package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.SystemConstants;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class ProcessPageOffertaAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 801771154835185524L;
	
	private IEventManager eventManager;
	
	/** Memorizza la prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	private String nextResultAction;
	
	private int tipoBusta;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	private int operazione;
	
	// Campi presenti nella form
	@Validate(EParamValidation.IMPORTO)
	private String importoOfferto;
	@Validate(EParamValidation.PERCENTUALE)
	private String ribasso;
	@Validate(EParamValidation.PERCENTUALE)
	private String aumento;
	@Validate(EParamValidation.IMPORTO)
	private String costiSicurezzaAziendali;
	@Validate(EParamValidation.CODICE)
	private String importoManodopera;
	@Validate(EParamValidation.PERCENTUALE)
	private String percentualeManodopera;
	
	// --- PERMUTA E ASSISTENZA ---
	@Validate(EParamValidation.IMPORTO)
	private String importoOffertoPerPermuta;
	@Validate(EParamValidation.IMPORTO)
	private String importoOffertoCanoneAssistenza;
	
	private boolean obblImportoOfferto;
	
	private boolean costiManodoperaObbligatori;
	
	private boolean percentualeManodoperaVisibile;
	
	// valori in input dei criteri di valutazione presenti sul form
	@Validate(EParamValidation.GENERIC)
	private String[] criterioValutazione;
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getNextResultAction() {
		return nextResultAction;
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

	public String getImportoOfferto() {
		return importoOfferto;
	}

	public void setImportoOfferto(String importoOfferto) {
		this.importoOfferto = importoOfferto.trim();
	}

	public String getAumento() {
		return aumento;
	}

	public void setAumento(String aumento) {
		this.aumento = aumento.trim();
	}

	public String getRibasso() {
		return ribasso;
	}

	public void setRibasso(String ribasso) {
		this.ribasso = ribasso.trim();
	}

	public String getCostiSicurezzaAziendali() {
		return costiSicurezzaAziendali;
	}

	public void setCostiSicurezzaAziendali(String costiSicurezzaAziendali) {
		this.costiSicurezzaAziendali = costiSicurezzaAziendali.trim();
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

	public boolean isObblImportoOfferto() {
		return obblImportoOfferto;
	}

	public void setObblImportoOfferto(boolean obblImportoOfferto) {
		this.obblImportoOfferto = obblImportoOfferto;
	}
	
	public boolean isCostiManodoperaObbligatori() {
		return costiManodoperaObbligatori;
	}

	public void setCostiManodoperaObbligatori(boolean costiManodoperaObbligatori) {
		this.costiManodoperaObbligatori = costiManodoperaObbligatori;
	}

	public boolean isPercentualeManodoperaVisibile() {
		return percentualeManodoperaVisibile;
	}

	public void setPercentualeManodoperaVisibile(
			boolean percentualeManodoperaVisibile) {
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
	public ProcessPageOffertaAction() {
		super(BustaGara.BUSTA_ECONOMICA, //PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA,
			  WizardOffertaEconomicaHelper.STEP_OFFERTA); 
	} 

	/**
	 * ... 
	 */
	@Override
	public String next() {
		String target = SUCCESS;
		
		BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
		WizardOffertaEconomicaHelper helper = bustaEco.getHelper();		
		
		// verifica se action e dati in sessione sono sincronizzati...
		if(!helper.isSynchronizedToAction(this.codice, this)) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			if(isOffertaChanged(helper)) {
				// va rigenerato il pdf, quindi elimino l'eventuale file e resetto
				// il flag
				helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
				helper.setRigenPdf(true);
				// sbianco lo UUID
				helper.setPdfUUID(null);
			}
			
			// si aggiorna l'helper in sessione con i dati della form...
			if( helper.isCriteriValutazioneVisibili() ) {
				if(this.validateAndSetCriteriValutazione(
						helper.getListaCriteriValutazione(), 
						this.criterioValutazione,
						true)) 
				{
					if(helper.getListaCriteriValutazione() != null) {
						CriterioValutazioneOffertaType criterio = WizardOffertaTecnicaHelper.findCriterioValutazione(
								helper.getListaCriteriValutazione(), 
								PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI);
						if(criterio != null && StringUtils.isNotEmpty(criterio.getValore())) {
							this.setImportoOfferto( criterio.getValore() );
						} else {
							criterio = WizardOffertaTecnicaHelper.findCriterioValutazione(
									helper.getListaCriteriValutazione(), 
									PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO);
							if(criterio != null && StringUtils.isNotEmpty(criterio.getValore())) {
								this.setImportoOfferto( criterio.getValore() );
							}
						}
					}
				} else {
					// qualcosa non e' andato bene durante la validazione dei valori...
					target = INPUT;
				}
				
			} else { 
				if(this.getImportoOfferto() != null) {
					if(StringUtils.isBlank(this.getImportoOfferto())) {
						helper.setImportoOfferto(null);
					} else {
						helper.setImportoOfferto(Double.parseDouble(this.getImportoOfferto()));
					}
				}
				if(this.getAumento() != null) {
					if(StringUtils.isBlank(this.getAumento())) {
						helper.setAumento(null);
					} else {
						helper.setAumento(Double.parseDouble(this.getAumento()));
					}
				}
				if(this.getRibasso() != null) {
					if(StringUtils.isBlank(this.getRibasso())) {
						helper.setRibasso(null);
					} else {
						helper.setRibasso(Double.parseDouble(this.getRibasso()));
					}
				}
			}
			if(helper.isCostiManodoperaVisible()){
				helper.setCostiSicurezzaAziendali(Double.parseDouble(this.getCostiSicurezzaAziendali()));
				if(helper.isPercentualeManodoperaVisible()) {
					helper.setPercentualeManodopera(Double.parseDouble(this.getPercentualeManodopera()));
				} else {
					helper.setImportoManodopera(Double.parseDouble(this.getImportoManodopera()));
				}
			} else {
				helper.setCostiSicurezzaAziendali(null);
				helper.setImportoManodopera(null);
				helper.setPercentualeManodopera(null);
			}
			
			if(this.getImportoOffertoPerPermuta() != null) {
				if(StringUtils.isBlank(this.getImportoOffertoPerPermuta())) {
					helper.setImportoOffertoPerPermuta(null);
				} else {
					helper.setImportoOffertoPerPermuta(Double.parseDouble(this.getImportoOffertoPerPermuta()));
				}
			}

			if(this.getImportoOffertoCanoneAssistenza() != null) {
				if(StringUtils.isBlank(this.getImportoOffertoCanoneAssistenza())) {
					helper.setImportoOffertoCanoneAssistenza(null);
				} else {
					helper.setImportoOffertoCanoneAssistenza(Double.parseDouble(this.getImportoOffertoCanoneAssistenza()));
				}
			}
			
			this.nextResultAction = helper.getNextAction(WizardOffertaEconomicaHelper.STEP_OFFERTA);

			// salva l'helper in sessione...
			bustaEco.putToSession();
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	/**
	 * ... 
	 */
	@SkipValidation
	public String back() {
		String target = SUCCESS;
		
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		
		// verifica se action e dati in sessione sono sincronizzati...
		if(!helper.isSynchronizedToAction(this.codice, this)) {
			return CommonSystemConstants.PORTAL_ERROR;
		} 

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			this.nextResultAction = helper.getPreviousAction(WizardOffertaEconomicaHelper.STEP_OFFERTA);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

	/**
	 * ... 
	 */
	public void validate() {
		super.validate();
		if (this.getFieldErrors().size() > 0) {
			return;
		}
		
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		
		if (helper != null) {
			// verifica se action e dati in sessione sono sincronizzati...
			if(!helper.isSynchronizedToAction(this.codice)) {
				// salta la validazione senza aggiungere messaggi di errore alla action
				return;
			}
			
			boolean importoOffertoPresente = true;
			Integer numeroMassimoDecimali = (helper.getNumDecimaliRibasso()== null || helper.getNumDecimaliRibasso().intValue() == 0) 
											 ? 1 
											 : helper.getNumDecimaliRibasso().intValue();
			Double importoOffertoDouble = null;

			// --- CRITERI DI VALUTAZIONE ---
			if(helper.isCriteriValutazioneVisibili()) {
				importoOffertoPresente = false;
				
				if(!this.validateCriteriValutazione(
						helper.getListaCriteriValutazione(), 
						this.criterioValutazione)) {
					// in caso di errori vengono gia' aggiunti i relativi messaggi
					// per ogni campo...
					// serve per aggiornare i valori dei criteri dell'helper
				}
			
				if(helper.getListaCriteriValutazione() != null) {
					
					// cerca i criteri relativi a  
					//   - Offerta mediante prezzi unitari
					//   - Offerta mediante importo
					//	 - Offerta mediante ribasso
					int iPrezziUnitari = -1;
					int iImporto = -1;
					int iRibasso = -1;
					for(int i = 0; i < helper.getListaCriteriValutazione().size(); i++) {
						CriterioValutazioneOffertaType criterio = helper.getListaCriteriValutazione().get(i);
						switch (helper.getListaCriteriValutazione().get(i).getFormato()) {
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
								iPrezziUnitari = i;
								break;
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:
								iImporto = i;
								break;
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:
								iRibasso = i;
								break;
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
								// valida il valore minimo e massimo
								if(StringUtils.isNotEmpty(this.criterioValutazione[i])) {
									BigDecimal x = BigDecimal.valueOf(Double.valueOf(this.criterioValutazione[i]))
													.setScale(numeroMassimoDecimali, BigDecimal.ROUND_HALF_UP);
									if(criterio.getValoreMax() != null && x.doubleValue() >= criterio.getValoreMax()) {
										this.addActionError(this.getText("Errors.offertaTelematica.criteri.importoMustBeLess",
																		 new String[] {criterio.getValoreMax().toString()}));
									}
									if(criterio.getValoreMin() != null && x.doubleValue() < criterio.getValoreMin()) {
										this.addActionError(this.getText("Errors.offertaTelematica.criteri.importoMustBeGreater",
												 						 new String[] {criterio.getValoreMin().toString()}));
									}
								}
								break;
						}
					}
					
					CriterioValutazioneOffertaType criterio = null;
					
					// verifica prima "Offerta mediante prezzi unitari"...
					// se non esiste verifica "Offerta mediante importo"...
					if(iPrezziUnitari >= 0 && StringUtils.isNotEmpty(this.criterioValutazione[iPrezziUnitari])) {
						criterio = helper.getListaCriteriValutazione().get(iPrezziUnitari);
						importoOffertoDouble = Double.parseDouble(this.criterioValutazione[iPrezziUnitari]);
						importoOffertoPresente = true;
					} else if(iImporto >= 0 && StringUtils.isNotEmpty(this.criterioValutazione[iImporto])) {
						criterio = helper.getListaCriteriValutazione().get(iImporto);
						importoOffertoDouble = Double.parseDouble(this.criterioValutazione[iImporto]);
						importoOffertoPresente = true;
					}
					if(importoOffertoPresente && criterio != null) {
						switch (criterio.getFormato()) {
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:				
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:	
							case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE: 
								if(criterio.getValoreMax() != null && importoOffertoDouble >= criterio.getValoreMax()) {
									this.addActionError(this.getText("Errors.offertaTelematica.criteri.importoMustBeLess",
											 						 new String[] {criterio.getValoreMax().toString()}));
								}
								if(criterio.getValoreMin() != null && importoOffertoDouble < criterio.getValoreMin()) {
									this.addActionError(this.getText("Errors.offertaTelematica.criteri.importoMustBeGreater",
					 						 						 new String[] {criterio.getValoreMin().toString()}));
								}
								break;
						}
					}
					
					// verifica "Offerta mediante ribasso"...
					if(iRibasso >= 0 && StringUtils.isNotEmpty(this.criterioValutazione[iRibasso])) {
						criterio = helper.getListaCriteriValutazione().get(iRibasso);
						if(criterio != null) { 
							Double ribasso = Double.parseDouble(this.criterioValutazione[iRibasso]);
							if(criterio.getValoreMax() != null && ribasso > criterio.getValoreMax()) {
								this.addActionError(this.getText("Errors.offertaTelematica.criteri.ribassoMustBeLess",
																 new String[] {criterio.getDescrizione(), criterio.getValoreMax().toString()}));
							}
							if(criterio.getValoreMin() != null && ribasso < criterio.getValoreMin()) {
								this.addActionError(this.getText("Errors.offertaTelematica.criteri.ribassoMustBeGreater",
										 						 new String[] {criterio.getDescrizione(), criterio.getValoreMin().toString()}));
							}
						}
					}
				}
			}   

			// --- VALIDAZIONE SU IMPORTO OFFERTO ---
			if( !helper.isCriteriValutazioneVisibili() ) {
				if(StringUtils.isEmpty(this.getImportoOfferto()) || StringUtils.isBlank(this.getImportoOfferto())) {
					importoOffertoPresente = false;
				} else {
					importoOffertoDouble = Double.parseDouble(this.getImportoOfferto());
				}
			}
			
			// per i confronti successivi, arrotonda l'importo offerta a 2 cifre decimali
			importoOffertoDouble = (importoOffertoDouble == null
				? null
				: BigDecimal.valueOf(Double.valueOf(importoOffertoDouble)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
			);
			
			if(importoOffertoPresente) {
				if(importoOffertoDouble == 0) {
					this.addFieldError("importoOfferto", 
										this.getText("Errors.offertaTelematica.importoOfferto.zero"));
				}
				if(importoOffertoDouble > helper.getGara().getImporto() && (!helper.getGara().isAmmessaOffertaInAumento())) {
					this.addFieldError("importoOfferto", 
										this.getText("Errors.offertaTelematica.importoOfferto.nonAmmessoAumento"));
				} else {
					if(!helper.getGara().isAmmessaOffertaInAumento()) {
						if((importoOffertoDouble > helper.getGara().getImporto() && helper.getGara().isOffertaComprensivaSicurezza()) || 
						   (importoOffertoDouble > helper.getGara().getImporto() - helper.getGara().getImportoSicurezza() && (!helper.getGara().isOffertaComprensivaSicurezza()))) {
							this.addFieldError("importoOfferto", 
												this.getText("Errors.offertaTelematica.importoOfferto.superioreLimite"));
						}
					}
				}	
			}
			
			if(importoOffertoPresente && helper.isCostiSicurezzaVisible()) {
				if(Double.parseDouble(this.getCostiSicurezzaAziendali()) >= importoOffertoDouble) {
					this.addFieldError("costiSicurezzaAziendali", 
										this.getText("Errors.offertaTelematica.costiSicurezzaAziendali.importoSuperiore"));
				}
				if(helper.isPercentualeManodoperaVisible()) {
					//if(StringUtils.isNotEmpty(this.getPercentualeManodopera()) &&
					//   Double.parseDouble(this.getPercentualeManodopera()) >= percentualeOffertoDouble) {
					//		this.addFieldError("percentualeManodopera", 
					//						this.getText("Errors.offertaTelematica.percentualeManodopera.???"));
					//}
				} else {
					if(StringUtils.isNotEmpty(this.getImportoManodopera()) &&
					   Double.parseDouble(this.getImportoManodopera()) >= importoOffertoDouble) {
						this.addFieldError("importoManodopera", 
											this.getText("Errors.offertaTelematica.importoManodopera.importoSuperiore"));
					}
				}
			}
			
			// --- VALIDAZIONE RIBASSO/AUMENTO --- 
			// visibili sia percentuale in aumento che percentuale in ribasso
			if(helper.isPercentualeAumentoVisible() && helper.isPercentualeRibassoEditable()) {
				if((StringUtils.isBlank(this.getRibasso()) || (StringUtils.isEmpty(this.getRibasso()))) && (StringUtils.isBlank(this.getAumento()) || 
			        StringUtils.isEmpty(this.getAumento()))) {
					this.addFieldError("ribasso", 
										this.getText("Errors.offertaTelematica.ribassoAumento.almenoUnoValorizzato"));
				}
				if(StringUtils.isNotBlank(this.getRibasso()) && StringUtils.isNotBlank(this.getAumento())) {
					this.addFieldError("ribasso", 
										this.getText("Errors.offertaTelematica.ribassoAumento.mutuaEsclusione"));
				}
				if (helper.isPercentualeAumentoVisible() &&
					(StringUtils.isBlank(this.getRibasso()) || (StringUtils.isEmpty(this.getRibasso()))) &&
					(StringUtils.isNotBlank(this.getAumento()) || StringUtils.isNotEmpty(this.getAumento()))) {
					validateDecimals(this.aumento, "aumento", numeroMassimoDecimali);
				}
			} else if(helper.isPercentualeRibassoEditable() && helper.isPercentualeRibassoMandatory() && StringUtils.isBlank(this.getRibasso()) ) {
				//visibile solo percentuale in ribasso
				this.addFieldError("ribasso", 
									this.getText("Errors.field.required", new String[]{this.getTextFromDB("ribasso")}));	
			} else if(helper.isPercentualeRibassoEditable() &&
					  (StringUtils.isNotBlank(this.getRibasso()) || (StringUtils.isNotEmpty(this.getRibasso()))) &&
					  (StringUtils.isBlank(this.getAumento()) || StringUtils.isEmpty(this.getAumento()))) {
				//valorizzata solo percentuale in ribasso
				validateDecimals(this.ribasso, "ribasso", numeroMassimoDecimali);
			}
			
			// verifica che l'importo offerto sia >= non soggetto a ribasso + importo di sicurezza 
			// (limite inferiore per importo offerto)
			if( importoOffertoDouble != null ) {
				double importoOffertoMinimo = 0;
				if(helper.isComprensivoNonSoggettiARibasso() && 
				   helper.getGara().getImportoNonSoggettoRibasso() != null) {
					importoOffertoMinimo += helper.getGara().getImportoNonSoggettoRibasso().doubleValue();
				}
				if(helper.isComprensivoOneriSicurezza() && 
				   helper.getGara().getImportoSicurezza() != null) {
					importoOffertoMinimo += helper.getGara().getImportoSicurezza().doubleValue();
				}
				
				if(importoOffertoDouble < importoOffertoMinimo) {
					this.addFieldError("importoOfferto", 
									   this.getText("Errors.offertaTelematica.importoOfferto.inferioreLimite", 
											        new String[] {Double.toString(importoOffertoMinimo)}));
				}
			}
		}
	}

	/**
	 * ... 
	 */
	private boolean isOffertaChanged(WizardOffertaEconomicaHelper helper) {

		boolean variazioneImporto = isImportoOffertoVariato(helper);
		boolean variazioneRibasso = isRibassoVariato(helper);
		boolean variazioneAumento = isAumentoVariato(helper);
		boolean variazioneCostiSicurezzaAziendali = isCostiSicurezzaAziendaliVariato(helper);
		boolean variazioneImportoManodopera = isImportoManodoperaVariato(helper);
		boolean variazionePercentualeManodopera = isPercentualeManodoperaVariato(helper);
		boolean variazioneImportoOffertoPerPermuta = isImportoOffertoPerPermutaVariato(helper);
		boolean variazioneImportoOffertoPerCanoneAssistenza = isImportoOffertoPerCanoneAssistenzaVariato(helper);
		boolean variazioneCriteriValidazione = isCriteriValidazioneVariati(helper);
		
		return (variazioneImporto || variazioneRibasso || variazioneAumento || 
				variazioneCostiSicurezzaAziendali ||
				variazioneImportoManodopera ||
				variazionePercentualeManodopera ||
				variazioneImportoOffertoPerPermuta || 
				variazioneImportoOffertoPerCanoneAssistenza ||
				variazioneCriteriValidazione) || 
				helper.isRigenPdf() || 
				StringUtils.isEmpty(helper.getPdfUUID());
	}

	/**
	 * ... 
	 */
	private boolean isImportoOffertoVariato(WizardOffertaEconomicaHelper helper) {
		boolean isImportoOffertoPresente = StringUtils.isNotEmpty(this.getImportoOfferto());
		boolean isImportoOffertoVuoto = StringUtils.isBlank(this.getImportoOfferto());
		boolean isImportoOffertoHelperPresente = helper.getImportoOfferto()!=null;
		if(isImportoOffertoHelperPresente && !isImportoOffertoVuoto) {
			BigDecimal bd = (BigDecimal.valueOf(helper.getImportoOfferto())
					.setScale(5, BigDecimal.ROUND_HALF_UP));
			BigDecimal v = (BigDecimal.valueOf(Double.parseDouble(this.getImportoOfferto()))
					.setScale(5, BigDecimal.ROUND_HALF_UP));
			return isImportoOffertoPresente && (!v.equals(bd));
		}
		return isImportoOffertoPresente;
	}

	/**
	 * ... 
	 */
	private boolean isRibassoVariato(WizardOffertaEconomicaHelper helper) {
		boolean isRibassoPresente = StringUtils.isNotEmpty(this.getRibasso());
		boolean isRibassoVuoto = StringUtils.isBlank(this.getRibasso());
		boolean isRibassoHelperPresente = helper.getRibasso() != null;
		if(isRibassoHelperPresente && !isRibassoVuoto) {
			BigDecimal bd =(BigDecimal.valueOf(helper.getRibasso())
					.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP));
			BigDecimal v =(BigDecimal.valueOf(Double.parseDouble(this.getRibasso()))
					.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP));
			return isRibassoPresente && (!v.equals(bd));
		}
		return isRibassoPresente;
	}

	/**
	 * ... 
	 */
	private boolean isAumentoVariato(WizardOffertaEconomicaHelper helper) {
		boolean isAumentoPresente = StringUtils.isNotEmpty(this.getAumento());
		boolean isAumentoVuoto = StringUtils.isBlank(this.getAumento());
		boolean isAumentoHelperPresente = helper.getAumento() != null;
		if(isAumentoHelperPresente && !isAumentoVuoto) {
			BigDecimal bd =(BigDecimal.valueOf(helper.getAumento())
					.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP));
			BigDecimal v =(BigDecimal.valueOf(Double.parseDouble(this.getAumento()))
					.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP));
			return isAumentoPresente && (!v.equals(bd));
		}
		return isAumentoPresente;
	}

	/**
	 * ... 
	 */
	private boolean isCostiSicurezzaAziendaliVariato(WizardOffertaEconomicaHelper helper) {
		boolean isVariato = false;
		if(helper != null && helper.isCostiSicurezzaVisible()) {
			BigDecimal bdInserito = null;
			if (StringUtils.isNotEmpty(this.getCostiSicurezzaAziendali())) {
				bdInserito = (BigDecimal.valueOf(Double.parseDouble(this.getCostiSicurezzaAziendali()))
						.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			BigDecimal bdHelper = null;
			if (helper.getCostiSicurezzaAziendali() != null) {
				bdHelper =(BigDecimal.valueOf(helper.getCostiSicurezzaAziendali())
						.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			// o sono entrambe nulli, o sono entrambe valorizzati con lo stesso valore, altrimenti si considera variato
			isVariato = !((bdInserito == null && bdHelper == null) || (bdInserito != null && bdHelper != null && bdInserito.equals(bdHelper)));
		}
		return isVariato;
	}
	
	/**
	 * ... 
	 */
	private boolean isImportoManodoperaVariato(WizardOffertaEconomicaHelper helper) {
		boolean isVariato = false;
		if(helper != null && helper.isCostiManodoperaVisible()) {
			BigDecimal bdInserito = null;
			if (StringUtils.isNotEmpty(this.getImportoManodopera())) {
				bdInserito = (BigDecimal.valueOf(Double.parseDouble(this.getImportoManodopera()))
						.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			BigDecimal bdHelper = null;
			if (helper.getImportoManodopera() != null) {
				bdHelper =(BigDecimal.valueOf(helper.getImportoManodopera())
						.setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			// o sono entrambe nulli, o sono entrambe valorizzati con lo stesso valore, altrimenti si considera variato
			isVariato = !((bdInserito == null && bdHelper == null) || (bdInserito != null && bdHelper != null && bdInserito.equals(bdHelper)));
		}
		return isVariato;
	}
	
	/**
	 * ... 
	 */
	private boolean isPercentualeManodoperaVariato(WizardOffertaEconomicaHelper helper){
		boolean isVariato = false;
		if(helper != null && helper.isPercentualeManodoperaVisible()) { 
			BigDecimal bdInserito = null;
			if (StringUtils.isNotEmpty(this.getPercentualeManodopera())) {
				bdInserito = (BigDecimal.valueOf(Double.parseDouble(this.getPercentualeManodopera()))
						.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP));
			}
			BigDecimal bdHelper = null;
			if (helper.getPercentualeManodopera() != null) {
				bdHelper =(BigDecimal.valueOf(helper.getPercentualeManodopera())
						.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP));
			}
			// o sono entrambe nulli, o sono entrambe valorizzati con lo stesso valore, altrimenti si considera variato
			isVariato = !((bdInserito == null && bdHelper == null) || (bdInserito != null && bdHelper != null && bdInserito.equals(bdHelper)));
		}
		return isVariato;
	}

	// --- PERMUTA E ASSISTENZA ---
	private boolean isImportoOffertoPerPermutaVariato(WizardOffertaEconomicaHelper helper){
		if(StringUtils.isNotEmpty(this.getImportoOffertoPerPermuta())){
			boolean isImportoOffertoPerPermutaPresente = StringUtils.isNotEmpty(this.getImportoOffertoPerPermuta());
			Double importoOffertoPerPermuta = Double.parseDouble(this.getImportoOffertoPerPermuta());
			return isImportoOffertoPerPermutaPresente && !(importoOffertoPerPermuta.equals(helper.getImportoOffertoPerPermuta()));
		}
		return false;
	}
	
	/**
	 * ... 
	 */
	private boolean isImportoOffertoPerCanoneAssistenzaVariato(WizardOffertaEconomicaHelper helper){
		if(StringUtils.isNotEmpty(this.getImportoOffertoCanoneAssistenza())){
			boolean isImportoOffertoPerCanoneAssistenzaPresente = StringUtils.isNotEmpty(this.getImportoOffertoCanoneAssistenza());
			Double importoOffertoPerCanoneAssistenza =  Double.parseDouble(this.getImportoOffertoCanoneAssistenza());
			return isImportoOffertoPerCanoneAssistenzaPresente && !(importoOffertoPerCanoneAssistenza.equals(helper.getImportoOffertoCanoneAssistenza()));	
		}
		return false;
	}

	/**
	 * ... 
	 */
	private boolean isCriteriValidazioneVariati(WizardOffertaEconomicaHelper helper){
		boolean variati = false;
		if(this.getCriterioValutazione() != null) {
			for(int i = 0; i < this.getCriterioValutazione().length; i++) {
				if(helper.getListaCriteriValutazione().get(i).getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
					if( !this.getCriterioValutazione()[i].equals(helper.getListaCriteriValutazione().get(i).getValore()) ) {
						variati = true;
					}
				}
			}
		}
		return variati;
	}

	/**
	 * ... 
	 */
	private void validateDecimals(String value, String field, int numeroMassimoDecimali){
		String[] aumentoArray =value.split("\\.");
		String decimalPartString = "";
		Long decimalPart = 0L;
		if (aumentoArray.length > 1) {
			decimalPartString = aumentoArray[1];
			decimalPart = new Long(decimalPartString);
		}
		if (numeroMassimoDecimali == 0 && decimalPart > 0) {
			this.addFieldError(field, this.getText("Errors.noDecimalsNeeded", new String[]{this.getTextFromDB(field)}));
		} else if (numeroMassimoDecimali > 0 && decimalPartString.length() > numeroMassimoDecimali) {
			this.addFieldError(field, this.getText("Errors.tooManyDecimals", new String[]{this.getTextFromDB(field), numeroMassimoDecimali + ""}));
		}
	}

	/**
	 * Valida ed imposta i valori dei criteri di valutazione 
	 */
	private boolean validateAndSetCriteriValutazione(
			List<CriterioValutazioneOffertaType> listaCriteriValutazione,
			String[] valori,
			boolean validateAndSet) 
	{
		boolean validazioneOk = false;
		
		if(valori != null) {
			validazioneOk = true;
			for(int i = 0; i < valori.length; i++) {
				if(listaCriteriValutazione.get(i).getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
					if(StringUtils.isEmpty(valori[i])) {
						this.addActionError(this.getText("Errors.offertaTelematica.criteri.fieldRequired",
													     new String[] {listaCriteriValutazione.get(i).getDescrizione()}));
					} else {
						try {
							// usa la validazione ed imposta il valore solo se richiesto
							WizardOffertaHelper.setValoreCriterioValutazione(
										listaCriteriValutazione.get(i),	
										valori[i],
										validateAndSet);
						} catch (NumberFormatException e) {
							this.addFieldError(listaCriteriValutazione.get(i).getDescrizione(), 
											   this.getText(e.getMessage(), new String[]{listaCriteriValutazione.get(i).getDescrizione(), 
																					     listaCriteriValutazione.get(i).getNumeroDecimali() + ""}));
							validazioneOk = false;
						} catch (ParseException e) {
							this.addFieldError(listaCriteriValutazione.get(i).getDescrizione(), 
											   this.getText(e.getMessage(), new String[]{listaCriteriValutazione.get(i).getDescrizione(), 
																					     WizardOffertaTecnicaHelper.CRITERIO_VALUTAZIONE_TESTO_MAXLEN + ""}));
							validazioneOk = false;
						} catch (Throwable e) {
							this.addFieldError(listaCriteriValutazione.get(i).getDescrizione(), 
									   this.getText(e.getMessage(), new String[]{listaCriteriValutazione.get(i).getDescrizione()}));
							validazioneOk = false;
						}
					}
				}
			}
		}
		
		return validazioneOk; 
	}

	/**
	 * Valida i valori dei criteri di valutazione, senza impostarne il valore 
	 */
	private boolean validateCriteriValutazione(
			List<CriterioValutazioneOffertaType> listaCriteriValutazione,
			String[] valori) 
	{
		return this.validateAndSetCriteriValutazione(listaCriteriValutazione, valori, false); 
	}

//	/**
//	 * verifica la coerenza tra i totali delle voci soggette a ribasso, 
//	 * non soggette a ribasso e solo sicurezza, con i totali dell'offerta
//	 *   
//	 * @return 0 
//	 * 		gli importi dell'offerta e totali calcolati sono coerenti
//	 * 
//	 * @return > 0 (= [+ 1] [+ 2] [+ 4]) 
//	 * 		1	importo solo sicurezza != totale calcolato solo sicurezza  
//	 * 		2	importo non soggette != totale calcolato non soggette
//	 * 		4 	importo soggette a ribasso != totale calcolato soggette ribasso
//	 */
//	private int isTotaliLavorazioniCorretti(Map<String, Object> session) {
//		// recupera l'elenco dei prezzi unitari e suddividilo in
//		// soggette a ribasso e non soggette a ribasso...
//		WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper)session
//			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
//		
//		int count1 = 0;
//		if(helper.getVociDettaglio() != null) {
//			count1 = helper.getVociDettaglio().size();
//		}
//		int count2 = 0;
//		if(helper.getVociDettaglioNoRibasso() != null) {
//			count2 = helper.getVociDettaglioNoRibasso().size();
//		}
//		if(count1 <= 0 && count2 <= 0) {
//			// offerta NON gestita a prezzi unitari
//			return 0;
//		}
//		
//		double totaleSoloSicurezza = 0;
//		double totaleVociNonSoggette = 0;
//		double totaleVociSoggette = 0; 
//		
//		if(helper.getVociDettaglio() != null) {
//			for(int i = 0; i < helper.getVociDettaglio().size(); i++) {
//				double qta = helper.getVociDettaglio().get(i).getQuantita();
//				double prz = helper.getPrezzoUnitario()[i];
//				double imp = qta * prz;
//				
//				if(helper.getVociDettaglio().get(i).isSoloSicurezza()) {
//					totaleSoloSicurezza += imp;
//				} else if(helper.getVociDettaglio().get(i).isNonSoggettoRibasso()) {
//					totaleVociNonSoggette += imp;
//				} else {
//					totaleVociSoggette += imp;
//				}
//			}
//		}
//		
//		if(helper.getVociDettaglioNoRibasso() != null) {
//			for(int i = 0; i < helper.getVociDettaglioNoRibasso().size(); i++) {
//				double qta = helper.getVociDettaglioNoRibasso().get(i).getQuantita();
//				double prz = helper.getVociDettaglioNoRibasso().get(i).getPrezzoUnitario();
//				double imp = qta * prz;
//				
//				if(helper.getVociDettaglioNoRibasso().get(i).isSoloSicurezza()) {
//					totaleSoloSicurezza += imp;
//				} else if(helper.getVociDettaglioNoRibasso().get(i).isNonSoggettoRibasso()) {
//					totaleVociNonSoggette += imp;
//				} else {
//					totaleVociSoggette += imp;
//				}
//			}
//		}
//
//		// valida i totali offerta con i totali calcolati...
//		// traccia nel log la validazione...
//		int valida = 0;
//		if(helper.getGara().getImportoSicurezza() != null &&
//		   helper.getGara().getImportoSicurezza() != totaleSoloSicurezza) {
//			valida += 1;
//			ApsSystemUtils.getLogger().debug(
//					"codice " + helper.getGara().getCodice() + " " +
//					"ProcessPageOffertaAction.isTotaliLavorazioniCorretti() " +
//					"helper.getGara().getImportoSicurezza() != totaleSoloSicurezza " +
//					"(" + helper.getGara().getImportoSicurezza() + " != " + totaleSoloSicurezza + ")" );
//		}
//		if(helper.getGara().getImportoNonSoggettoRibasso() != null &&
//		   helper.getGara().getImportoNonSoggettoRibasso() != totaleVociNonSoggette) {
//			valida += 2;
//			ApsSystemUtils.getLogger().debug(
//					"codice " + helper.getGara().getCodice() + " " +
//					"ProcessPageOffertaAction.isTotaliLavorazioniCorretti() " +
//					"helper.getGara().getImportoNonSoggettoRibasso() != totaleVociNonSoggette" + 
//					"(" + helper.getGara().getImportoNonSoggettoRibasso() + " != " + totaleVociNonSoggette + ")" );
//		}
//		if(helper.getTotaleOffertaPrezziUnitari() != totaleVociSoggette) {
//			valida += 4;
//			ApsSystemUtils.getLogger().debug(
//					"codice " + helper.getGara().getCodice() + " " +
//					"ProcessPageOffertaAction.isTotaliLavorazioniCorretti() " +
//					"totaleOffertaPrezziUnitari != totaleVociSoggette" +
//					"(" + helper.getTotaleOffertaPrezziUnitari() + " != " + totaleVociSoggette + ")" );
//		}
//		
//		return valida;
//	}

}
