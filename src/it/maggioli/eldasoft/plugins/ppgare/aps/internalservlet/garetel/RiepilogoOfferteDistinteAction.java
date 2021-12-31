package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoLottoBustaType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

public class RiepilogoOfferteDistinteAction extends RiepilogoOffertaAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2379803814911736060L;
	
	private boolean almenoUnaBustaTecnica;
	private List<Long> listaIdBusteTec;
	private List<Long> listaIdBusteEco;

	public boolean isAlmenoUnaBustaTecnica() {
		return almenoUnaBustaTecnica;
	}

	public void setAlmenoUnaBustaTecnica(boolean almenoUnaBustaTecnica) {
		this.almenoUnaBustaTecnica = almenoUnaBustaTecnica;
	}

	public List<Long> getListaIdBusteTec() {
		return listaIdBusteTec;
	}

	public void setListaIdBustaTec(List<Long> listaIdBusteTec) {
		this.listaIdBusteTec = listaIdBusteTec;
	}

	public List<Long> getListaIdBusteEco() {
		return listaIdBusteEco;
	}

	public void setListaIdBusteEco(List<Long> listaIdBusteEco) {
		this.listaIdBusteEco = listaIdBusteEco;
	}

	/**
	 * ...
	 */
	public String openPage() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				boolean domandaPartecipazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
				boolean invioOfferta = !domandaPartecipazione;
				
				String tipoComunicazione = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA)				
						? PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT 
						: PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;

				String RICHIESTA_TIPO_RIEPILOGO = (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA)				
						? PortGareSystemConstants.RICHIESTA_TIPO_PREQUALIFICA_RIEPILOGO
						: PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO;
				
				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
				if("1".equals(this.getFromListaOfferte())) {
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				}

				DettaglioComunicazioneType comunicazioneOfferta = ComunicazioniUtilities
					.retrieveComunicazioneConStati(
							this.getComunicazioniManager(),
							this.getCurrentUser().getUsername(), 
							this.getCodice(), 
							this.getProgressivoOfferta(),
							tipoComunicazione, 
							stati);

				if (comunicazioneOfferta == null) {
					this.setTarget(INPUT);
					this.addActionError(this.getText("Errors.riepilogoOfferta.notFound"));
				} else {
					retrieveDatiRTI(comunicazioneOfferta.getId());
					this.setDettGara(this.getBandiManager().getDettaglioGara(this.getCodice()));

					this.setDatiImpresa((WizardDatiImpresaHelper) this.getSession()
							.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA));
					if (this.getDatiImpresa() == null) {
						// --- CESSATI --- 
						this.setDatiImpresa(ImpresaAction.getLatestDatiImpresa(
								this.getCurrentUser().getUsername(), this, this.getAppParamManager())); 
					}
				}

				// ----- FS11R/FS10R PRESENTE : RILETTURA E RIPOPOLAMENTO -----
				DettaglioComunicazioneType comunicazioneBustaRie = ComunicazioniUtilities.retrieveComunicazione(
						this.getComunicazioniManager(),
						this.getCurrentUser().getUsername(),
						this.getCodice(), 
						this.getProgressivoOfferta(),
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						RICHIESTA_TIPO_RIEPILOGO);

				ComunicazioneType comunicazioneSalvata = null;
				
				// Recupero la comunicazione riepilogativa
				comunicazioneSalvata = this.getComunicazioniManager()
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
										  comunicazioneBustaRie.getId());
				
				this.setBustaRiepilogativa(new RiepilogoBusteHelper(
						this.getBandiManager(), 
						this.getCodice(), 
						null, 
						this.getDatiImpresa().getDatiPrincipaliImpresa().getTipoImpresa(), 
						this.isRti(), 
						true, 
						tipoComunicazione,
						this.getCurrentUser().getUsername(),
						this.getProgressivoOfferta()));

				if(comunicazioneBustaRie != null){
					
					RiepilogoLottoBustaType[] listaLotti = null;
					
					if(domandaPartecipazione) {
						RiepilogoBustePartecipazioneDocument documento = getBustePartecipazioneDocument(comunicazioneSalvata);
						this.getSession().put(PortGareSystemConstants.SESSION_ID_RIEPILOGO_BUSTE, documento);

						if(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica() != null) {
							this.getBustaRiepilogativa().getBustaPrequalifica().popolaBusta(documento.getRiepilogoBustePartecipazione().getBustaPrequalifica());
						}
						//listaLotti = documento.getRiepilogoBustePartecipazione().getLottoArray();														
						//listaLotti = f(comunicazioneOfferta)
					}
					
					if(invioOfferta) {
						RiepilogoBusteOffertaDocument documento = getBusteOffertaDocument(comunicazioneSalvata);												
						this.getSession().put(PortGareSystemConstants.SESSION_ID_RIEPILOGO_BUSTE, documento);

						if(documento.getRiepilogoBusteOfferta().getBustaAmministrativa() != null) {
							this.getBustaRiepilogativa().getBustaAmministrativa().popolaBusta(documento.getRiepilogoBusteOfferta().getBustaAmministrativa());
						}
						listaLotti = documento.getRiepilogoBusteOfferta().getLottoArray();
					}
					
					// prepara i lotti della busta di riepilogo
					this.getBustaRiepilogativa().setListaCompletaLotti(new ArrayList<String>());
					
					if(listaLotti != null) {
						for(int i = 0; i < listaLotti.length; i++) {
							this.getBustaRiepilogativa().getListaCompletaLotti().add(listaLotti[i].getCodiceLotto());
							this.getBustaRiepilogativa().getListaCodiciInterniLotti().put(listaLotti[i].getCodiceLotto(), listaLotti[i].getCodiceInterno());
						}
					}
					
					if(domandaPartecipazione) {
						// provo a ricavare la comunicazione per la busta di prequalifica
						DettaglioComunicazioneType comunicazioneBustaPreq = ComunicazioniUtilities.retrieveComunicazioneConStati(
								this.getComunicazioniManager(),
								this.getCurrentUser().getUsername(), 
								this.getCodice(), 
								this.getProgressivoOfferta(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA, 
								stati);

						if (comunicazioneBustaPreq != null) {
							this.setBustaPrequalificaCifrata(comunicazioneBustaPreq.getSessionKey() != null);
							this.setIdBustaPreq(comunicazioneBustaPreq.getId());
						}
					}

					if(invioOfferta) {
						// provo a ricavare la comunicazione per la busta amministrativa
						DettaglioComunicazioneType comunicazioneBustaAmm = ComunicazioniUtilities.retrieveComunicazioneConStati(
								this.getComunicazioniManager(),
								this.getCurrentUser().getUsername(), 
								this.getCodice(), 
								this.getProgressivoOfferta(),
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA, 
								stati);

						if (comunicazioneBustaAmm != null) {
							this.setBustaAmministrativaCifrata(comunicazioneBustaAmm.getSessionKey() != null);
							this.setIdBustaAmm(comunicazioneBustaAmm.getId());
						}
						
						for(int i = 0; i < listaLotti.length;i++) {
							RiepilogoBustaType bustaTecnica = listaLotti[i].getBustaTecnica();
							if(this.getBustaRiepilogativa().getBusteTecnicheLotti() != null){
								RiepilogoBustaBean bustaTecnicaLotto = new RiepilogoBustaBean();
								if((boolean) this.getBandiManager().isGaraConOffertaTecnica(listaLotti[i].getCodiceLotto())) {
									// cambiare la condizione e cercarla da busta 
									// riepilogativa per problemi performance 
									// se possibile
									bustaTecnicaLotto.setOggetto(listaLotti[i].getOggetto());
									this.getBustaRiepilogativa().popolaBusteLotti(bustaTecnica, bustaTecnicaLotto);
									this.getBustaRiepilogativa().getBusteTecnicheLotti().put(listaLotti[i].getCodiceLotto(), bustaTecnicaLotto);
									this.setAlmenoUnaBustaTecnica(true);
								}
							}
						}
						
						for(int i = 0; i < listaLotti.length;i++) {
							RiepilogoBustaType bustaEconomica = listaLotti[i].getBustaEconomica();
							if(this.getBustaRiepilogativa().getBusteEconomicheLotti() != null) {
								RiepilogoBustaBean bustaEconomicaLotto = new RiepilogoBustaBean();
								bustaEconomicaLotto.setOggetto(listaLotti[i].getOggetto());
								this.getBustaRiepilogativa().popolaBusteLotti(bustaEconomica, bustaEconomicaLotto);
								this.getBustaRiepilogativa().getBusteEconomicheLotti().put(listaLotti[i].getCodiceLotto(), bustaEconomicaLotto);
							}
						}
						
						DettaglioComunicazioneType comunicazioniTec = null;
						DettaglioComunicazioneType comunicazioniEco = null;
					
						if(isAlmenoUnaBustaTecnica()) {
							this.listaIdBusteTec = new ArrayList<Long>();
							for(int i = 0; i < this.getBustaRiepilogativa().getListaCompletaLotti().size(); i++) {
								// ---------- BUSTE TECNICHE ---------- 
								comunicazioniTec = ComunicazioniUtilities.retrieveComunicazioneConStati(
										this.getComunicazioniManager(), 
										this.getCurrentUser().getUsername(), 
										this.getBustaRiepilogativa().getListaCompletaLotti().get(i), 
										this.getProgressivoOfferta(),
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
										stati);
							
								if (comunicazioniTec != null) {
									this.setBustaTecnicaCifrata(comunicazioniTec.getSessionKey() != null);
									this.listaIdBusteTec.add(comunicazioniTec.getId());
								}
							}
						}

						this.listaIdBusteEco = new ArrayList<Long>();
						for(int i = 0; i < this.getBustaRiepilogativa().getListaCompletaLotti().size(); i++) {
							// ---------- BUSTE ECONOMICHE ----------
							comunicazioniEco = ComunicazioniUtilities.retrieveComunicazioneConStati(
									this.getComunicazioniManager(), 
									this.getCurrentUser().getUsername(), 
									this.getBustaRiepilogativa().getListaCompletaLotti().get(i),
									this.getProgressivoOfferta(),
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
									stati);

							if (comunicazioniEco != null) {
								this.setBustaEconomicaCifrata(comunicazioniEco.getSessionKey() != null);
								this.listaIdBusteEco.add(comunicazioniEco.getId());
							}
						}
					}
				}

				Date dataAttuale = null;
				this.setAbilitaRettifica(false);

				if (domandaPartecipazione 
				    && this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneDomanda() != null) {
					try {
						dataAttuale = this.getNtpManager().getNtpDate();
						Date dataTermine = InitIscrizioneAction.calcolaDataOra(
								this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneDomanda(),
								this.getDettGara().getDatiGeneraliGara().getOraTerminePresentazioneDomanda(),
								true);
						if (dataAttuale.compareTo(dataTermine) <= 0) {
							this.setAbilitaRettifica(true);
						}
					} catch (Throwable e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}
				if (invioOfferta
					&& this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneOfferta() != null) {
					try {
						//if (dataAttuale == null) {						 ???
						//	dataAttuale = this.getNtpManager().getNtpDate(); ???
						//}													 ???
						dataAttuale = this.getNtpManager().getNtpDate();
						Date dataTermine = InitIscrizioneAction.calcolaDataOra(
								this.getDettGara().getDatiGeneraliGara().getDataTerminePresentazioneOfferta(),
								this.getDettGara().getDatiGeneraliGara().getOraTerminePresentazioneOfferta(),
								true);
						if (dataAttuale.compareTo(dataTermine) <= 0) {
							this.setAbilitaRettifica(true);
						}
					} catch (Throwable e) {
						// non si fa niente, si usano i dati ricevuti dal
						// servizio e quindi i test effettuati nel dbms server
					}
				}

			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openPage");
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
