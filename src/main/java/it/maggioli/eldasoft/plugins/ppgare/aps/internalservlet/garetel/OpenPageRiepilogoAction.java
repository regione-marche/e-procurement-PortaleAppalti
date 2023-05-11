package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaAmministrativa;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPrequalifica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
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
public class OpenPageRiepilogoAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private Map<String, Object> session;

	private IBandiManager bandiManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;

//	private List<String> lotti = new ArrayList<String>();
	private List<String> docObbligatoriMancantiAmministrativa;
	private List<String> docObbligatoriMancantiTecnica;
	private List<String> docObbligatoriMancantiEconomica;
	private List<String> docObbligatoriMancantiPrequalifica;
	private RiepilogoBusteHelper bustaRiepilogativa;
	private boolean rti;
	@Validate(EParamValidation.DENOMINAZIONE_RTI)
	private String denominazioneRti;
	private boolean offertaTelematica;
	
	private DettaglioGaraType dettGara;
	private WizardDatiImpresaHelper datiImpresa;

	private boolean offertaTecnica;
	
	private boolean costoFisso;

	private String statoEconomica;
	private String statoAmministrativa;
	private String statoTecnica;
	private String statoPrequalifica;


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
	
	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
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

//	public List<String> getLotti() {
//		return lotti;
//	}

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

	public String getStatoEconomica() {
		return statoEconomica;
	}

	public String getStatoAmministrativa() {
		return statoAmministrativa;
	}

	public String getStatoTecnica() {
		return statoTecnica;
	}

	public String getStatoPrequalifica() {
		return statoPrequalifica;
	}

	/**
	 * ... 
	 */
	public String openPage() {
		String target = SUCCESS;

		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				boolean domandaPartecipazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);


				GestioneBuste buste = GestioneBuste.getFromSession();
				this.dettGara = buste.getDettaglioGara();
				this.datiImpresa = buste.getImpresa();
				WizardPartecipazioneHelper partecipazioneHelper = buste.getBustaPartecipazione().getHelper();
				BustaRiepilogo bustaRiepilogo = buste.getBustaRiepilogo();

				BustaEconomica bustaEco = buste.getBustaEconomica();
				BustaAmministrativa bustaAmm = buste.getBustaAmministrativa();
				BustaTecnica bustaTec = buste.getBustaTecnica();
				BustaPrequalifica bustaPreq = buste.getBustaPrequalifica();

				statoEconomica = bustaEco != null ? bustaEco.getComunicazioneFlusso().getStato() : "";
				statoTecnica = bustaTec != null ? bustaTec.getComunicazioneFlusso().getStato() : "";
				statoAmministrativa = bustaAmm != null ? bustaAmm.getComunicazioneFlusso().getStato() : "";
				statoPrequalifica = bustaPreq != null ? bustaPreq.getComunicazioneFlusso().getStato() : "";

				this.setOffertaTelematica(this.dettGara.getDatiGeneraliGara().isOffertaTelematica());
				this.setOffertaTecnica((boolean) this.bandiManager.isGaraConOffertaTecnica(this.codice));
				this.setCostoFisso(this.dettGara.getDatiGeneraliGara().getCostoFisso() != null 
                                   && this.dettGara.getDatiGeneraliGara().getCostoFisso() == 1);
				
				if (partecipazioneHelper != null) {
					this.rti = partecipazioneHelper.isRti();
					if (partecipazioneHelper.isRti()) {
						this.denominazioneRti = partecipazioneHelper.getDenominazioneRTI();
					}
//				} else {
//					retrieveDatiRTI();
				}

				this.setBustaRiepilogativa(bustaRiepilogo.getHelper());
				
				this.docObbligatoriMancantiAmministrativa = new ArrayList<String>();
				this.docObbligatoriMancantiTecnica = new ArrayList<String>();
				this.docObbligatoriMancantiEconomica = new ArrayList<String>();
				this.docObbligatoriMancantiPrequalifica = new ArrayList<String>();

				bustaRiepilogo.integraBusteFromBO();
				
				if(domandaPartecipazione) {
					// --- INTEGRAZIONE BO PER BUSTA PREQUALIFICA ---														
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
					if(this.bustaRiepilogativa.getBustaAmministrativa() != null &&
					   this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti() != null)
					{
						for(int i = 0; i < this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().size();i++){
							DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().get(i);
							if(docMancante.isObbligatorio()){
								this.docObbligatoriMancantiAmministrativa.add(docMancante.getDescrizione());
							}
						}
					}
					
					// --- INTEGRAZIONE BO BUSTA TECNICA --- 
					if(this.bustaRiepilogativa.getBustaTecnica().getDocumentiMancanti() != null){
						for(int i = 0; i < this.bustaRiepilogativa.getBustaTecnica().getDocumentiMancanti().size();i++){
							DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaTecnica().getDocumentiMancanti().get(i);
							if(docMancante.isObbligatorio()){
								this.docObbligatoriMancantiTecnica.add(docMancante.getDescrizione());
							}
						}
					}
					
					// --- INTEGRAZIONE BO BUSTA ECONOMICA --- 
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
