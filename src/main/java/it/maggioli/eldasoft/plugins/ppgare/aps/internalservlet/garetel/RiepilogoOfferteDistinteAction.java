package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoLottoBustaType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

/**
 * ...
 * 
 */
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
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				GestioneBuste buste = new GestioneBuste(
						this.getCurrentUser().getUsername(),
						this.getCodice(), 
						this.getProgressivoOfferta(),
						this.getOperazione());
				
				BustaPartecipazione bustaPartecipazione = buste.getBustaPartecipazione();
				BustaPrequalifica bustaPrequalifica = buste.getBustaPrequalifica();
				BustaAmministrativa bustaAmministrativa = buste.getBustaAmministrativa();
				//BustaEconomica bustaEconomica = buste.getBustaEconomica();
				//BustaTecnica bustaTecnica = buste.getBustaTecnica();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();
				
				boolean domandaPartecipazione = buste.isDomandaPartecipazione();
				boolean invioOfferta = buste.isInvioOfferta();

				// recupera la partecipazione FS11/FS10...
				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
				if("1".equals(this.getFromListaOfferte())) {
					stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				}
				
				bustaPartecipazione.get(stati);

				if (bustaPartecipazione.getId() <= 0) {
					this.setTarget(INPUT);
					this.addActionError(this.getText("Errors.riepilogoOfferta.notFound"));
				} else {
					this.setDettGara(buste.getDettaglioGara());
					
					this.setDatiImpresa(buste.getImpresa());
					if (this.getDatiImpresa() == null) {
						// --- CESSATI ---
						this.setDatiImpresa( buste.getLastestDatiImpresa() ); 
					}
				}
				
				this.setHasFileRiepilogoAllegati(bustaPartecipazione.isHasFileRiepilogoAllegati());
				this.setRiepilogoAllegatiFirmato(bustaPartecipazione.isRiepilogoAllegatiFirmato());	
				this.setIdBustaRiepilogo(bustaPartecipazione.getIdFileRiepilogoAllegati());
				if(bustaPartecipazione.getHelper() != null) {
					this.setRti( bustaPartecipazione.getHelper().isRti() );
					this.setDenominazioneRti( bustaPartecipazione.getHelper().getDenominazioneRTI() );
				}
				
				bustaRiepilogo.get();
				this.setBustaRiepilogativa(bustaRiepilogo.getHelper());
				
				if(bustaRiepilogo.getId() > 0) {
					RiepilogoLottoBustaType[] listaLotti = null;
					if(domandaPartecipazione) {
						RiepilogoBustePartecipazioneDocument documento = bustaRiepilogo.getRiepilogoPartecipazioneDocument();
						//listaLotti = documento.getRiepilogoBustePartecipazione().getLottoArray();
					}
					if(invioOfferta) {
						RiepilogoBusteOffertaDocument documento = bustaRiepilogo.getRiepilogoOffertaDocument();
						listaLotti = documento.getRiepilogoBusteOfferta().getLottoArray();
					}
					if(listaLotti != null) {
						//...
					}
				
					if(domandaPartecipazione) {
						// provo a ricavare la comunicazione per la busta di prequalifica
						bustaPrequalifica.get(stati);
						
						if(bustaPrequalifica.getId() > 0) {
							this.setBustaPrequalificaCifrata(bustaPrequalifica.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null);
							this.setIdBustaPreq(bustaPrequalifica.getId());
						}
					}

					if(invioOfferta) {
						// provo a ricavare la comunicazione per la busta amministrativa
						bustaAmministrativa.get(stati);
						
						if(bustaAmministrativa.getId() > 0) {
							this.setBustaAmministrativaCifrata(bustaAmministrativa.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null);
							this.setIdBustaAmm(bustaAmministrativa.getId());
						}
						
						this.setAlmenoUnaBustaTecnica(bustaRiepilogo.isAlmenoUnaBustaTecnica());

						// ---------- BUSTE TECNICHE ----------
						if(this.isAlmenoUnaBustaTecnica()) {
							this.listaIdBusteTec = new ArrayList<Long>();
							for(int i = 0; i < this.getBustaRiepilogativa().getListaCompletaLotti().size(); i++) {
								DettaglioComunicazioneType comunicazioniTec = ComunicazioniUtilities.retrieveComunicazioneConStati(
										this.getComunicazioniManager(), 
										this.getCurrentUser().getUsername(), 
										this.getBustaRiepilogativa().getListaCompletaLotti().get(i), 
										this.getProgressivoOfferta(),
										PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA, 
										stati);
							
								if (comunicazioniTec != null) {
									this.setBustaTecnicaCifrata(comunicazioniTec.getSessionKey() != null);
								}
								
								// per la JSP serve un "id" per ogni lotto, quindi aggiungo sempre l'informazione 
								// e nel caso il lotto non esista, inserisco un id nullo 
								listaIdBusteTec.add(comunicazioniTec != null ? comunicazioniTec.getId() : null);
							}
						}

						// ---------- BUSTE ECONOMICHE ----------
						this.listaIdBusteEco = new ArrayList<Long>();
						for(int i = 0; i < this.getBustaRiepilogativa().getListaCompletaLotti().size(); i++) {
							DettaglioComunicazioneType comunicazioniEco = ComunicazioniUtilities.retrieveComunicazioneConStati(
									this.getComunicazioniManager(), 
									this.getCurrentUser().getUsername(), 
									this.getBustaRiepilogativa().getListaCompletaLotti().get(i),
									this.getProgressivoOfferta(),
									PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
									stati);

							if (comunicazioniEco != null) {
								this.setBustaEconomicaCifrata(comunicazioniEco.getSessionKey() != null);
							}
							
							// per la JSP serve un "id" per ogni lotto, quindi aggiungo sempre l'informazione 
							// e nel caso il lotto non esista, inserisco un id nullo 
							listaIdBusteEco.add(comunicazioniEco != null ? comunicazioniEco.getId() : null);
						}
					}
				}

				this.setAbilitaRettifica( rettificaAbilitata() );

				// richieste soccorso istruttorio per rettifica buste
				richiestaInvioRettificaAbilitata(buste);

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
