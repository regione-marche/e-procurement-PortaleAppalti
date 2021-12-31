package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

/**
 * Action di gestione dell'apertura della pagina di riepilogo del wizard di
 * partecipazione ad una gara
 *
 * @author Stefano.Sabbadin
 */
public class OpenPageRiepilogoAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;

	private String codice;
	private int operazione;

	private List<String> lotti = new ArrayList<String>();
	private List <String> docObbligatoriMancantiAmministrativa;
	private List <String> docObbligatoriMancantiTecnica;
	private List <String> docObbligatoriMancantiEconomica;
	private List <String> docObbligatoriMancantiPrequalifica;
	private RiepilogoBusteHelper bustaRiepilogativa;
	private boolean rti;
	private String denominazioneRti;
	private boolean offertaTelematica;
	
	private DettaglioGaraType dettGara;
	private WizardDatiImpresaHelper datiImpresa;

	private boolean offertaTecnica;
	
	private boolean costoFisso;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public boolean isRti() {
		return rti;
	}

	public void setRti(boolean rti) {
		this.rti = rti;
	}

	public String getDenominazioneRti() {
		return denominazioneRti;
	}

	public void setDenominazioneRti(String denominazioneRti) {
		this.denominazioneRti = denominazioneRti;
	}

	public List<String> getLotti() {
		return lotti;
	}

	public boolean isOffertaTecnica() {
		return offertaTecnica;
	}

	public void setOffertaTecnica(boolean offertaTecnica) {
		this.offertaTecnica = offertaTecnica;
	}

	public DettaglioGaraType getDettGara() {
		return dettGara;
	}

	public void setDettGara(DettaglioGaraType dettGara) {
		this.dettGara = dettGara;
	}

	public WizardDatiImpresaHelper getDatiImpresa() {
		return datiImpresa;
	}

	public void setDatiImpresa(WizardDatiImpresaHelper datiImpresa) {
		this.datiImpresa = datiImpresa;
	}
	
	public RiepilogoBusteHelper getBustaRiepilogativa() {
		return bustaRiepilogativa;
	}

	public void setBustaRiepilogativa(RiepilogoBusteHelper bustaRiepilogativa) {
		this.bustaRiepilogativa = bustaRiepilogativa;
	}

	public List <String> getDocObbligatoriMancantiAmministrativa() {
		return docObbligatoriMancantiAmministrativa;
	}

	public void setDocObbligatoriMancantiAmministrativa(List <String> docObbligatoriMancantiAmministrativa) {
		this.docObbligatoriMancantiAmministrativa = docObbligatoriMancantiAmministrativa;
	}

	public List <String> getDocObbligatoriMancantiTecnica() {
		return docObbligatoriMancantiTecnica;
	}

	public void setDocObbligatoriMancantiTecnica(List <String> docObbligatoriMancantiTecnica) {
		this.docObbligatoriMancantiTecnica = docObbligatoriMancantiTecnica;
	}

	public List <String> getDocObbligatoriMancantiEconomica() {
		return docObbligatoriMancantiEconomica;
	}

	public void setDocObbligatoriMancantiEconomica(List <String> docObbligatoriMancantiEconomica) {
		this.docObbligatoriMancantiEconomica = docObbligatoriMancantiEconomica;
	}
	
	public List<String> getDocObbligatoriMancantiPrequalifica() {
		return docObbligatoriMancantiPrequalifica;
	}

	public void setDocObbligatoriMancantiPrequalifica(List<String> docObbligatoriMancantiPrequalifica) {
		this.docObbligatoriMancantiPrequalifica = docObbligatoriMancantiPrequalifica;
	}

	public boolean isOffertaTelematica() {
		return offertaTelematica;
	}

	public void setOffertaTelematica(boolean offertaTelematica) {
		this.offertaTelematica = offertaTelematica;
	}
	
	public boolean isCostoFisso() {
		return costoFisso;
	}

	public void setCostoFisso(boolean costoFisso) {
		this.costoFisso = costoFisso;
	}

	public int getPresentaPartecipazione() { return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA; }

	public int getInviaOfferta() { return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA; }

	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }

	public int getBUSTA_TECNICA() { return PortGareSystemConstants.BUSTA_TECNICA; }

	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }
	
	public int getBUSTA_PRE_QUALIFICA() { return PortGareSystemConstants.BUSTA_PRE_QUALIFICA; }
	

	/**
	 * ... 
	 */
	public String openPage() {
		String target = SUCCESS;

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

			try {
				boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA); 				
				
				this.dettGara = this.bandiManager.getDettaglioGara(this.codice);
				this.datiImpresa = (WizardDatiImpresaHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);

				this.setOffertaTelematica(this.dettGara.getDatiGeneraliGara().isOffertaTelematica());
				this.setOffertaTecnica((boolean) this.bandiManager.isGaraConOffertaTecnica(this.codice));
				this.setCostoFisso(this.dettGara.getDatiGeneraliGara().getCostoFisso() != null 
                                   && this.dettGara.getDatiGeneraliGara().getCostoFisso() == 1);
				
				WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

				if (partecipazioneHelper != null) {
					this.rti = partecipazioneHelper.isRti();
					if (partecipazioneHelper.isRti()) {
						this.denominazioneRti = partecipazioneHelper.getDenominazioneRTI();
					}
//				} else {
//					retrieveDatiRTI();
				}

				this.setBustaRiepilogativa ((RiepilogoBusteHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA));
				
				this.docObbligatoriMancantiAmministrativa = new ArrayList<String>();
				this.docObbligatoriMancantiTecnica = new ArrayList<String>();
				this.docObbligatoriMancantiEconomica = new ArrayList<String>();
				this.docObbligatoriMancantiPrequalifica = new ArrayList<String>();

				if(domandaPartecipazione) {
					// --- INTEGRAZIONE BO PER BUSTA PREQUALIFICA ---
					bustaRiepilogativa.integraBustaPrequalificaFromBO(this.codice, this.codice, bandiManager, datiImpresa, partecipazioneHelper);
					
					if(this.bustaRiepilogativa.getBustaPrequalifica().getDocumentiMancanti() != null){
						for(int i = 0; i < this.bustaRiepilogativa.getBustaPrequalifica().getDocumentiMancanti().size();i++){
							DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaPrequalifica().getDocumentiMancanti().get(i);
							if(docMancante.isObbligatorio()){
								this.docObbligatoriMancantiPrequalifica.add(docMancante.getDescrizione());
							}
						}
					}
					
				} else {
					// --- INTEGRAZIONE BO PER BUSTA AMMINISTRATIVA ---
					bustaRiepilogativa.integraBustaAmministrativaFromBO(this.codice, this.codice, bandiManager, datiImpresa, partecipazioneHelper);
					
					if(this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti() != null){
						for(int i = 0; i < this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().size();i++){
							DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().get(i);
							if(docMancante.isObbligatorio()){
								this.docObbligatoriMancantiAmministrativa.add(docMancante.getDescrizione());
							}
						}
					}					
					
					//List<DocumentazioneRichiestaType> docCompletaTecnica = bustaRiepilogativa.retrieveDocumentazionerichiestaDb(bandiManager, codice, codice, datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(),  this.isRti(), PortGareSystemConstants.BUSTA_TECNICA);
					// --- INTEGRAZIONE BO BUSTA TECNICA --- 
					bustaRiepilogativa.integraBustaTecnicaFromBO(bustaRiepilogativa.getBustaTecnica(), bandiManager, this.codice, this.codice, datiImpresa, partecipazioneHelper);
					
					if(this.bustaRiepilogativa.getBustaTecnica().getDocumentiMancanti() != null){
						for(int i = 0; i < this.bustaRiepilogativa.getBustaTecnica().getDocumentiMancanti().size();i++){
							DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaTecnica().getDocumentiMancanti().get(i);
							if(docMancante.isObbligatorio()){
								this.docObbligatoriMancantiTecnica.add(docMancante.getDescrizione());
							}
						}
					}
					
					// --- INTEGRAZIONE BO BUSTA ECONOMICA --- 
					bustaRiepilogativa.integraBustaEconomicaFromBO(bustaRiepilogativa.getBustaEconomica(), bandiManager, this.codice, this.codice, datiImpresa, partecipazioneHelper);
					
					if(this.bustaRiepilogativa.getBustaEconomica().getDocumentiMancanti() != null){
						for(int i = 0; i < this.bustaRiepilogativa.getBustaEconomica().getDocumentiMancanti().size();i++){
							DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaEconomica().getDocumentiMancanti().get(i);
							if(docMancante.isObbligatorio()){
								this.docObbligatoriMancantiEconomica.add(docMancante.getDescrizione());
							}
						}
					}
				}
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "openPage");
				this.addActionError(this.getText("Errors.send.outOfMemory"));
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "openPage");
				ExceptionUtils.manageExceptionError(e, this);
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

}
