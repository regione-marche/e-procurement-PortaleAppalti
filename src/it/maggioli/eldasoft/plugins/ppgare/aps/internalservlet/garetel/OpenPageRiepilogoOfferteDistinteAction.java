package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;



public class OpenPageRiepilogoOfferteDistinteAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8812214769667005258L;
	
	private Map<String, Object> session;

	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	
	private String codice;
	private String codiceGara;
	private int operazione;
	private String progressivoOfferta;

	private List<String> lotti = new ArrayList<String>();
	private List <String> docObbligatoriMancantiAmministrativa;
	private RiepilogoBusteHelper bustaRiepilogativa;
	private boolean rti;
	private String denominazioneRti;
	private boolean offertaTelematica;

	private DettaglioGaraType dettGara;
	private WizardDatiImpresaHelper datiImpresa;

	private boolean offertaTecnica;

	private HashMap<String, List<DocumentoMancanteBean>> docObbligatoriMancantiTecnica;
	private HashMap<String, List<DocumentoMancanteBean>> docObbligatoriMancantiEconomica;

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

	public boolean isOffertaTelematica() {
		return offertaTelematica;
	}

	public void setOffertaTelematica(boolean offertaTelematica) {
		this.offertaTelematica = offertaTelematica;
	}

	public HashMap<String, List<DocumentoMancanteBean>> getDocObbligatoriMancantiTecnica() {
		return docObbligatoriMancantiTecnica;
	}

	public void setDocObbligatoriMancantiTecnica(HashMap<String, List<DocumentoMancanteBean>> docObbligatoriMancantiTecnica) {
		this.docObbligatoriMancantiTecnica = docObbligatoriMancantiTecnica;
	}

	public HashMap<String, List<DocumentoMancanteBean>> getDocObbligatoriMancantiEconomica() {
		return docObbligatoriMancantiEconomica;
	}

	public void setDocObbligatoriMancantiEconomica(HashMap<String, List<DocumentoMancanteBean>> docObbligatoriMancantiEconomica) {
		this.docObbligatoriMancantiEconomica = docObbligatoriMancantiEconomica;
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
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		this.setBustaRiepilogativa((RiepilogoBusteHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA));
		this.datiImpresa = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
		// ----- TEST SU EVENTUALE INTEGRAZIONE DOCUMENTI DA BO -----
		boolean integrazioneEffettuata = false;

		// ----- INTEGRAZIONE PER BUSTA AMMINISTRATIVA ----- 
		boolean integrazioneBustaAmministrativa = bustaRiepilogativa.integraBustaAmministrativaFromBO(this.codiceGara, this.codice, bandiManager, datiImpresa, partecipazioneHelper);
		integrazioneEffettuata = integrazioneEffettuata || integrazioneBustaAmministrativa;


		for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size();i++){
			String lottoCorrente = bustaRiepilogativa.getListaCompletaLotti().get(i);
			
			// ---------- BUSTE TECNICHE ---------- 
			RiepilogoBustaBean bustaTecnicaLotto = bustaRiepilogativa.getBusteTecnicheLotti().get(lottoCorrente) ;

			// --- per ogni lotto controllo se esiste la busta tecnica --- 
			if(bustaTecnicaLotto!= null){
				boolean integrazioneBustaTecnica = bustaRiepilogativa.integraBustaTecnicaFromBO(bustaTecnicaLotto, bandiManager, this.codiceGara, lottoCorrente, datiImpresa, partecipazioneHelper);
				integrazioneEffettuata = integrazioneEffettuata || integrazioneBustaTecnica;
			}

			// ---------- BUSTE ECONOMICHE ----------
			// in caso di OEPV a costo fisso il lotto non è attivo...
			RiepilogoBustaBean bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(lottoCorrente);
			if(bustaEconomicaLotto != null) {
				boolean integrazioneBustaEconomica = bustaRiepilogativa.integraBustaEconomicaFromBO(bustaEconomicaLotto, bandiManager, this.codiceGara, lottoCorrente, datiImpresa, partecipazioneHelper);
				integrazioneEffettuata = integrazioneEffettuata || integrazioneBustaEconomica;
			}
		}
		String target = SUCCESS;
		docObbligatoriMancantiTecnica = new LinkedHashMap<String, List<DocumentoMancanteBean>>();
		docObbligatoriMancantiEconomica = new LinkedHashMap<String, List<DocumentoMancanteBean>>();

		if (null != this.getCurrentUser() && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				this.dettGara = this.bandiManager.getDettaglioGara(this.codiceGara);

				if (partecipazioneHelper != null) {
					this.rti = partecipazioneHelper.isRti();
					if (partecipazioneHelper.isRti()) {
						this.denominazioneRti = partecipazioneHelper.getDenominazioneRTI();
					}
				}

				// ----- RIEPILOGO PER LOTTI -----
				this.docObbligatoriMancantiAmministrativa = new ArrayList<String>();

				// --- busta amministrativa ---
				if(this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti() != null) {
					for(int i = 0; i < this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().size(); i++) {
						DocumentoMancanteBean docMancante = this.bustaRiepilogativa.getBustaAmministrativa().getDocumentiMancanti().get(i);
						if(docMancante.isObbligatorio()) {
							this.docObbligatoriMancantiAmministrativa.add(docMancante.getDescrizione());
						}
					}
				}

				// -- recupero solo i mancanti obbligatori per lotto --
				for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size(); i++) {
					List<DocumentoMancanteBean> documentiMancanti = null;
					if(bustaRiepilogativa.getBusteTecnicheLotti().get(bustaRiepilogativa.getListaCompletaLotti().get(i)) != null) {
						documentiMancanti = bustaRiepilogativa.getBusteTecnicheLotti().get(bustaRiepilogativa.getListaCompletaLotti().get(i)).getDocumentiMancanti();
						List<DocumentoMancanteBean> documentiObbMancanti = null; 
						if(documentiMancanti.size() > 0) {
							documentiObbMancanti = new ArrayList<DocumentoMancanteBean>();
						}
						for(int j = 0; j < documentiMancanti.size(); j++) {
							if(documentiMancanti.get(j).isObbligatorio()){
								documentiObbMancanti.add(documentiMancanti.get(j));
							}
						}
						docObbligatoriMancantiTecnica.put(bustaRiepilogativa.getListaCompletaLotti().get(i), documentiObbMancanti);
					}
				}

				// --- buste economiche --- 
				for(int i = 0; i < bustaRiepilogativa.getListaCompletaLotti().size(); i++) {
					RiepilogoBustaBean bustaEconomicaLotto = null;
					if(bustaRiepilogativa.getBusteEconomicheLotti() != null) {
						bustaEconomicaLotto = bustaRiepilogativa.getBusteEconomicheLotti().get(bustaRiepilogativa.getListaCompletaLotti().get(i));
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
						docObbligatoriMancantiEconomica.put(bustaRiepilogativa.getListaCompletaLotti().get(i), documentiObbMancanti);						
					}
				}
				
				// ----- aggiornamento FS11R post integrazione documenti da BO -----
				if(integrazioneEffettuata) {
					WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);

					bustaRiepilogativa.sendComunicazioneBusta(
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							datiImpresaHelper, 
							this.getCurrentUser().getUsername(),
							this.codiceGara,
							datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(), 
							this.comunicazioniManager,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO,
							this);
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

}
