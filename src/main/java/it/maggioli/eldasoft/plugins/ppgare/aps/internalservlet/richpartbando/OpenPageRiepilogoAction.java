package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard di
 * partecipazione ad una gara
 * 
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class OpenPageRiepilogoAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;
	
	/* elenco dei lotti selezionati */
	private List<String> lotti = new ArrayList<>();
	private List<String> codiciInterni = new ArrayList<>();
	
	/* elenco dei lotti non ammessi (o non selezionati in fase di domanda di partecipazione) */
	private List<String> lottiNonAmmessi = new ArrayList<>();
	private List<String> lottiNonAmmessiCodice = new ArrayList<>();
	
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public List<String> getLotti() {
		return lotti;
	}

	public List<String> getCodiciInterni() {
		return codiciInterni;
	}

	public void setCodiciInterni(List<String> codiciInterni) {
		this.codiciInterni = codiciInterni;
	}

	public List<String> getLottiNonAmmessi() {
		return lottiNonAmmessi;
	}

	public void setLottiNonAmmessi(List<String> lottiNonAmmessi) {
		this.lottiNonAmmessi = lottiNonAmmessi;
	}

	public List<String> getLottiNonAmmessiCodice() {
		return lottiNonAmmessiCodice;
	}

	public void setLottiNonAmmessiCodice(List<String> lottiNonAmmessiCodice) {
		this.lottiNonAmmessiCodice = lottiNonAmmessiCodice;
	}
	
	
	public int getPresentaPartecipazione() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}

	/**
	 * costruttore
	 */ 
	public String openPage() {
		String target = SUCCESS;
		
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();
		
		if (partecipazioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_PAGINA_RIEPILOGO);

			// si estraggono le denominazioni dei lotti selezionati
			try {
				List<String> lottiAmmessi = partecipazioneHelper.getLottiAmmessiInOfferta();
				boolean invioOffertaGaraRitretta = (lottiAmmessi != null && lottiAmmessi.size() > 0);
				
				if(invioOffertaGaraRitretta) {
					partecipazioneHelper.getLotti().clear();
					for(int i = 0; i < lottiAmmessi.size(); i++) {
						partecipazioneHelper.getLotti().add(lottiAmmessi.get(i));
					}
				}
				
				this.lottiNonAmmessi = new ArrayList<>();
				this.lottiNonAmmessiCodice = new ArrayList<>();
				
				partecipazioneHelper.getLottiBarcodeFiles().clear();
				if (partecipazioneHelper.isLottiDistinti() || partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {					
					LottoGaraType[] lotti = this.bandiManager
							.getLottiGara(partecipazioneHelper.getIdBando());
					for (int i = 0; i < lotti.length; i++) {
						if (partecipazioneHelper.getLotti().contains(lotti[i].getCodiceLotto())) {
							partecipazioneHelper.getOggettiLotti().add(lotti[i].getOggetto());
							partecipazioneHelper.getCodiciInterniLotti().add(lotti[i].getCodiceInterno());
							this.lotti.add(lotti[i].getOggetto());
							this.codiciInterni.add(lotti[i].getCodiceInterno());
						} else {
							// non aggiungo il lotto fittizio
							if(lotti[i].getCodiceInterno() != null) {
								this.lottiNonAmmessi.add(lotti[i].getOggetto());
								this.lottiNonAmmessiCodice.add(lotti[i].getCodiceInterno());
							}
						}
					}
				} else {
					this.lotti.add(partecipazioneHelper.getDescBando());
				}
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openPage");
				ExceptionUtils.manageExceptionError(e, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		}
		return target;
	}

}
