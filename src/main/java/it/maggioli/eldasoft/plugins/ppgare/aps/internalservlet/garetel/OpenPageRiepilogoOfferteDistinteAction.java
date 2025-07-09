package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComunicazioneFlusso;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.pdfbox.pdmodel.font.CIDFontMapping;
import org.apache.struts2.interceptor.SessionAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


/**
 * ... 
 *
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPageRiepilogoOfferteDistinteAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8812214769667005258L;
	
	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	private int operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;

//	private List<String> lotti = new ArrayList<String>();
	private List<String> docObbligatoriMancantiAmministrativa;
	private RiepilogoBusteHelper bustaRiepilogativa;
	private boolean rti;
	@Validate(EParamValidation.DENOMINAZIONE_RTI)
	private String denominazioneRti;
	@Validate(EParamValidation.CODICE_CNEL)
	private String codiceCNEL;
	private boolean offertaTelematica;

	private DettaglioGaraType dettGara;
	private WizardDatiImpresaHelper datiImpresa;

	private boolean offertaTecnica;

	private Map<String, List<DocumentoMancanteBean>> docObbligatoriMancantiTecnica;
	private Map<String, List<DocumentoMancanteBean>> docObbligatoriMancantiEconomica;
	
	// stato delle comunicazioni delle buste
	private String statoPrequalifica;
	private String statoAmministrativa;
	private Map<String, String> statoEconomica;
	private Map<String, String> statoTecnica;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
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

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
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

	public String getCodiceCNEL() {
		return codiceCNEL;
	}

	public void setCodiceCNEL(String codiceCNEL) {
		this.codiceCNEL = codiceCNEL;
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

	public boolean isOffertaTelematica() {
		return offertaTelematica;
	}

	public void setOffertaTelematica(boolean offertaTelematica) {
		this.offertaTelematica = offertaTelematica;
	}

	public Map<String, List<DocumentoMancanteBean>> getDocObbligatoriMancantiTecnica() {
		return docObbligatoriMancantiTecnica;
	}

	public void setDocObbligatoriMancantiTecnica(Map<String, List<DocumentoMancanteBean>> docObbligatoriMancantiTecnica) {
		this.docObbligatoriMancantiTecnica = docObbligatoriMancantiTecnica;
	}

	public Map<String, List<DocumentoMancanteBean>> getDocObbligatoriMancantiEconomica() {
		return docObbligatoriMancantiEconomica;
	}

	public void setDocObbligatoriMancantiEconomica(Map<String, List<DocumentoMancanteBean>> docObbligatoriMancantiEconomica) {
		this.docObbligatoriMancantiEconomica = docObbligatoriMancantiEconomica;
	}
	
	public String getStatoPrequalifica() {
		return statoPrequalifica;
	}

	public String getStatoAmministrativa() {
		return statoAmministrativa;
	}

	public Map<String, String> getStatoEconomica() {
		return statoEconomica;
	}

	public Map<String, String> getStatoTecnica() {
		return statoTecnica;
	}

	public int getPresentaPartecipazione() { return PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA; }
	public int getInviaOfferta() { return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA; }
	public int getBUSTA_AMMINISTRATIVA() { return PortGareSystemConstants.BUSTA_AMMINISTRATIVA; }
	public int getBUSTA_TECNICA() { return PortGareSystemConstants.BUSTA_TECNICA; }
	public int getBUSTA_ECONOMICA() { return PortGareSystemConstants.BUSTA_ECONOMICA; }

	
	/**
	 * ... 
	 */
	public String openPage() throws ApsException, IOException {
		String target = SUCCESS;
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		BustaRiepilogo riepilogo = buste.getBustaRiepilogo();
		WizardPartecipazioneHelper partecipazioneHelper = buste.getBustaPartecipazione().getHelper();		
		
		// ----- TEST SU EVENTUALE INTEGRAZIONE DOCUMENTI DA BO -----
		boolean integrazioneEffettuata = riepilogo.integraBusteFromBO(); 		
		
		this.bustaRiepilogativa = riepilogo.getHelper();
		this.datiImpresa = buste.getImpresa();
		this.docObbligatoriMancantiTecnica = new LinkedHashMap<String, List<DocumentoMancanteBean>>();
		this.docObbligatoriMancantiEconomica = new LinkedHashMap<String, List<DocumentoMancanteBean>>();

		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				this.dettGara = buste.getDettaglioGara();

				getStatiBuste();
				
				if (partecipazioneHelper != null) {
					this.rti = partecipazioneHelper.isRti();
					if (partecipazioneHelper.isRti()) {
						this.denominazioneRti = partecipazioneHelper.getDenominazioneRTI();
					}
					if(buste.isInvioOfferta()) {
						// codice CNEL e' previsto solo per l'offerta
						this.codiceCNEL = partecipazioneHelper.getCodiceCNEL();
					}
				}

				// ----- RIEPILOGO PER LOTTI -----
				this.docObbligatoriMancantiAmministrativa = new ArrayList<String>();

				// --- busta amministrativa ---
				if(this.bustaRiepilogativa.getBustaAmministrativa() != null &&
				   this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti() != null) 
				{
					for(int i = 0; i < this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().size(); i++) {
						DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().get(i);
						if(docMancante.isObbligatorio()) {
							this.docObbligatoriMancantiAmministrativa.add(docMancante.getDescrizione());
						}
					}
				}

				// -- recupero solo i mancanti obbligatori per lotto --
				for(int i = 0; i < this.bustaRiepilogativa.getListaCompletaLotti().size(); i++) {
					List<DocumentoMancanteBean> documentiMancanti = null;
					if(this.bustaRiepilogativa.getBusteTecnicheLotti().get(this.bustaRiepilogativa.getListaCompletaLotti().get(i)) != null) {
						documentiMancanti = this.bustaRiepilogativa.getBusteTecnicheLotti().get(this.bustaRiepilogativa.getListaCompletaLotti().get(i)).getDocumentiMancanti();
						List<DocumentoMancanteBean> documentiObbMancanti = null; 
						if(documentiMancanti.size() > 0) {
							documentiObbMancanti = new ArrayList<DocumentoMancanteBean>();
						}
						for(int j = 0; j < documentiMancanti.size(); j++) {
							if(documentiMancanti.get(j).isObbligatorio()) {
								documentiObbMancanti.add(documentiMancanti.get(j));
							}
						}
						this.docObbligatoriMancantiTecnica.put(this.bustaRiepilogativa.getListaCompletaLotti().get(i), documentiObbMancanti);
					}
				}

				// --- buste economiche --- 
				for(int i = 0; i < this.bustaRiepilogativa.getListaCompletaLotti().size(); i++) {
					RiepilogoBustaBean bustaEconomicaLotto = null;
					if(this.bustaRiepilogativa.getBusteEconomicheLotti() != null) {
						bustaEconomicaLotto = this.bustaRiepilogativa.getBusteEconomicheLotti().get(this.bustaRiepilogativa.getListaCompletaLotti().get(i));
					}
					if(bustaEconomicaLotto != null) {
						List<DocumentoMancanteBean> documentiMancanti = bustaEconomicaLotto.getDocumentiMancanti();
						List<DocumentoMancanteBean> documentiObbMancanti = null;
						if(documentiMancanti.size() > 0) {
							documentiObbMancanti = new ArrayList<DocumentoMancanteBean>();
						}
						for(int j = 0; j < documentiMancanti.size(); j++) {
							if(documentiMancanti.get(j).isObbligatorio()) {
								documentiObbMancanti.add(documentiMancanti.get(j));
							}
						}
						this.docObbligatoriMancantiEconomica.put(this.bustaRiepilogativa.getListaCompletaLotti().get(i), documentiObbMancanti);						
					}
				}
				
				// ----- aggiornamento FS11R post integrazione documenti da BO -----
				if(integrazioneEffettuata) {
					riepilogo.send(null);
				}
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "openPage");
				ExceptionUtils.manageExceptionError(e, this);
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
	
	/**
	 * ricarica gli stati delle comunicazioni delle buste 
	 * @throws ApsException 
	 */
	private void getStatiBuste() throws ApsException {
		statoPrequalifica = "99";
		statoAmministrativa = "99";
		statoEconomica = new HashMap<String, String>();
		statoTecnica = new HashMap<String, String>();

		// cerca tutte le comunicazioni per la coppia (like username%, like codiceGara%) 
		DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
		filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filtri.setChiave1(getCurrentUser().getUsername());
		filtri.setChiave2(codiceGara);
		List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.searchElencoComunicazioni(filtri);
		
		// verifica che le comunicazioni corrispondano all'utente...
		if(comunicazioni != null) 
			comunicazioni.stream()
				.filter(c -> c.getChiave1().equals(getCurrentUser().getUsername()))
				.forEach(c -> {
					if(ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA.equals(c.getTipoComunicazione()))
						statoAmministrativa = c.getStato();
					if(ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_TECNICA.equals(c.getTipoComunicazione()))
						statoTecnica.put(c.getChiave2(), c.getStato());
					if(ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_ECONOMICA.equals(c.getTipoComunicazione()))
						statoEconomica.put(c.getChiave2(), c.getStato());
				});
	}

}
