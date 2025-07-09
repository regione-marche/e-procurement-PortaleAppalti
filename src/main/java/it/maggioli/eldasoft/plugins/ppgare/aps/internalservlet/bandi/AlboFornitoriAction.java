package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import it.eldasoft.sil.portgare.datatypes.AltriDatiAnagraficiAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComunicazioneFlusso;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import java.util.*;
import java.util.function.Predicate;

/**
 * Action per le operazioni sugli albi fornitori SAP.
 *
 * @version ...
 * @author ...
 */
public class AlboFornitoriAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6584149293886192303L;

	private Map<String, Object> session;	
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;
	
	private DettaglioComunicazioneType dettComunicazione;
	@Validate(EParamValidation.ID_COMUNICAZIONE)
	private String idComunicazione;
	@ValidationNotRequired
	private String urlAlboFornitori;
	private Boolean funzionNonAccessibile;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public DettaglioComunicazioneType getDettComunicazione() {
		return dettComunicazione;
	}

	public void setDettComunicazione(DettaglioComunicazioneType dettComunicazione) {
		this.dettComunicazione = dettComunicazione;
	}
	
	public String getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(String idComunicazione) {
		this.idComunicazione = idComunicazione;
	}
	
	public String getUrlAlboFornitori() {
		return urlAlboFornitori;
	}

	public void setUrlAlboFornitori(String urlAlboFornitori) {
		this.urlAlboFornitori = urlAlboFornitori;
	}
	
	public Boolean getFunzionNonAccessibile() {
		return funzionNonAccessibile;
	}

	public void setFunzionNonAccessibile(Boolean funzionNonAccessibile) {
		this.funzionNonAccessibile = funzionNonAccessibile;
	}

	
	/**
	 * apri la pagina per la gestione dell'albo fornitori
	 */
	public String open() {
		this.setTarget(SUCCESS);
		
		this.funzionNonAccessibile = false;
		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
				
				this.dettComunicazione = ComunicazioniUtilities.retrieveComunicazioneConStati(
						comunicazioniManager
						, getCurrentUser().getUsername()
						, null
						, null
						, PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO_FORNITORI
						, stati
				);
				
				this.urlAlboFornitori = (String)this.appParamManager.getConfigurationValue(AppParamManager.URL_ALBO_FORNITORI_ESTERNI);

				if (dettComunicazione == null) {
					setTarget(checkAccettazioneConsensi());
					setTarget(checkSoggettoRichiedente());
				}

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "open");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			//this.addActionError(this.getText("Errors.sessionExpired"));
			//this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			this.funzionNonAccessibile = true;
		}
		
		return this.getTarget();
	}

	private String checkSoggettoRichiedente() throws ApsException {
		String target = getTarget();

		if (SUCCESS.equals(target)) {
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.BANDI_MANAGER,
							 ServletActionContext.getRequest());

			DatiImpresaDocument datiImpresa = bandiManager.getDatiImpresa(getCurrentUser().getUsername(), null);

			DettaglioComunicazioneType fs1 = retrieveLastFS1();
			RegistrazioneImpresaDocument attachment = retrieveRegAttachment(fs1);
			target = hasValidApplicant(datiImpresa, attachment);
		}
		if (!SUCCESS.equals(target)) {
			session.put(SystemConstants.SESSION_ACCETTAZIONE_CONSENSI, getCurrentUser().getUsername());
			switch (target) {
				case "INVALID_APPLICANT":
					addActionError(getText("missing.applicant"));
					break;
				case "ACCETTAZIONE_CONSENSI":
					addActionError(getText("consent.acceptance.required"));
					break;
			}
		}

		return target;
	}

	private String hasValidApplicant(DatiImpresaDocument datiImpresa, RegistrazioneImpresaDocument attachment) {
		String target = SUCCESS;
		if (datiImpresa == null || attachment == null) {
			target = "INVALID_APPLICANT";
		} else if (attachment.getRegistrazioneImpresa().getSoggettoRichiedente() == null
			|| attachment.getRegistrazioneImpresa().getSoggettoRichiedente().isEmpty()
			|| isInvalidApplicant(datiImpresa, attachment.getRegistrazioneImpresa().getSoggettoRichiedente())) 
		{
			target = "INVALID_APPLICANT";
		}
		return target;
	}

	/**
	 * Check if the applicant setted is valid
	 * @return
	 */
	private boolean isInvalidApplicant(DatiImpresaDocument datiImpresa, String soggettoRichiedente) {
		return "6".equals(datiImpresa.getDatiImpresa().getImpresa().getTipoImpresa())
				? !isValidApplicantForFreelancer(
						datiImpresa.getDatiImpresa().getImpresa().getAltriDatiAnagrafici()
						, datiImpresa.getDatiImpresa().getImpresa().getCodiceFiscale()
						, soggettoRichiedente
					)
				: !isValidApplicantForNonFreelancer(datiImpresa, soggettoRichiedente);
	}
	
	/**
	 * Chekc if the the applicant for the non freelancer company is valid (so if exist a valid and not expired subject)
	 *
	 * @param impresaType
	 * @return
	 */
	private boolean isValidApplicantForNonFreelancer(DatiImpresaDocument impresaType, String soggettoRichiedente) {
		return isValidSubject(impresaType.getDatiImpresa().getLegaleRappresentanteArray(), soggettoRichiedente)
				|| isValidSubject(impresaType.getDatiImpresa().getDirettoreTecnicoArray(), soggettoRichiedente)
				|| isValidSubject(impresaType.getDatiImpresa().getCollaboratoreArray(), soggettoRichiedente)
				|| isValidSubject(impresaType.getDatiImpresa().getAltraCaricaArray(), soggettoRichiedente);
	}
	
	private boolean isValidSubject(ReferenteImpresaAggiornabileType[] referenti, String applicant) {
		return Arrays.stream(referenti)
				.parallel()
				.filter(not(this::isLegalRepresentitiveExpired))
				.filter(this::isAbleToSign)
				.anyMatch(curr -> applicant.equals(applicantFromLegalRepresentative(curr)));
	}
	
	/**
	 * Check if the Legal representative can sign
	 * @param legal
	 * @return
	 */
	private boolean isAbleToSign(ReferenteImpresaAggiornabileType legal) {
		return "1".equalsIgnoreCase(legal.getResponsabileDichiarazioni());
	}
	
	private String applicantFromLegalRepresentative(ReferenteImpresaAggiornabileType legal) {
		return String.format("%s %s (%s)", legal.getNome(), legal.getCognome(), legal.getCodiceFiscale());
	}
	
	/**
	 * Controlla se il legale rappresentante ad oggi ha ancora l'incarico
	 * @param legal
	 * @return
	 */
	private boolean isLegalRepresentitiveExpired(ReferenteImpresaAggiornabileType legal) {
		return legal.getDataFineIncarico() != null && legal.getDataFineIncarico().before(Calendar.getInstance());
	}
	
	static <T> Predicate<T> not(Predicate<? super T> target) {
		Objects.requireNonNull(target);
		return (Predicate<T>)target.negate();
	}
	
	/**
	 * Check if the freelancer has a valid applicant
	 *
	 * @param soggettoRichiedente
	 * @param impresaType
	 * @return
	 */
	private boolean isValidApplicantForFreelancer(AltriDatiAnagraficiAggiornabileType impresaType, String fiscalCode, String soggettoRichiedente) {
		return impresaType != null
				&& impresaType.getCognome() != null
				&& impresaType.getNome() != null
				&& (soggettoRichiedente.equals(
						String.format(
								"%s %s (%s)"
								, impresaType.getNome()
								, impresaType.getCognome()
								, fiscalCode
						)
				)
				|| soggettoRichiedente.equals(
						String.format(
								"%s %s (%s)"
								, impresaType.getCognome()
								, impresaType.getNome()
								, fiscalCode
						)
				));
	}

	private RegistrazioneImpresaDocument retrieveRegAttachment(DettaglioComunicazioneType com) throws ApsException {
		ComunicazioneType comunicazione = (com != null 
				? comunicazioniManager.getComunicazione(com.getApplicativo(), com.getId(), null)
				: null);
		
		RegistrazioneImpresaDocument regImp = (comunicazione != null 
				? retrieveRegObjectFS1(comunicazione)
				: null);

		return regImp;
	}
	
	private RegistrazioneImpresaDocument retrieveRegObjectFS1(ComunicazioneType com) {
		if (com != null && com.getAllegato() != null)
			return Arrays.stream(com.getAllegato())
					.filter(attachment -> "dati_reg.xml".equalsIgnoreCase(attachment.getNomeFile()))
					.map(this::xmlToObject)
					.filter(Objects::nonNull)
					.findFirst()
				.orElse(null);
		return null;
	}
	
	private RegistrazioneImpresaDocument xmlToObject(AllegatoComunicazioneType attachment) {
		try {
			return RegistrazioneImpresaDocument.Factory.parse(new String(attachment.getFile()));
		} catch (Exception e) {
			return null;
		}
	}

	private DettaglioComunicazioneType retrieveLastFS1() throws ApsException {
		DettaglioComunicazioneType dettComunicazione = null;
		
		DettaglioComunicazioneType filter = new DettaglioComunicazioneType();
		filter.setChiave1(getCurrentUser().getUsername());
		filter.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_ACCETTAZIONE_CONSENSI);
		filter.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		filter.setTipoComunicazione(ComunicazioneFlusso.RICHIESTA_REGISTRAZIONE_PORTALE);
		
		List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(filter);
		if(comunicazioni != null) {
			dettComunicazione = comunicazioni.stream()
						.max(Comparator.comparing(DettaglioComunicazioneType::getId))
					.orElse(null);
		}
		return dettComunicazione;
	}
	
	private String checkAccettazioneConsensi() {
		String target = getTarget();

		if (SUCCESS.equals(target)) {
			Integer piattaformaVersion = appParamManager.getConfigurationValueIntDef(AppParamManager.CLAUSOLEPIATTAFORMA_VERSIONE, 0);
			UserDetails user = getCurrentUser();
			Integer userVersion = (user.getAcceptanceVersion() != null && user.getAcceptanceVersion() > 0 ? user.getAcceptanceVersion() : 0);
			if (userVersion < piattaformaVersion)
				target = "ACCETTAZIONE_CONSENSI";
		}

		return target;
	}

	/**
	 * invia la richiesta d'iscrizione all'albo fornitori
	 */
	public String richiestaIscrizione() {
		this.setTarget(SUCCESS);

		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			this.setTarget(SUCCESS);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String confermaRichiestaIscrizione() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// verifica se esiste la comunicazione in stato DA PROCESSARE (5) o PROCESSATA (6)...
				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				
				this.dettComunicazione = ComunicazioniUtilities.retrieveComunicazioneConStati(
						this.comunicazioniManager, 
						this.getCurrentUser().getUsername(), 
						null, 
						null, 
						PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO_FORNITORI, 
						stati);

				if(dettComunicazione == null) {
					// invia una nuova comunicazione in stato DA PROCESSARE (5)
					this.sendComunicazione();
				} else {
					// la comunicazione esiste gia'...
					//this.addActionError(this.getText("Errors.???"));
					this.setTarget(INPUT);
				}
				
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "richiestaIscrizione");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * ...
	 */
	public String annullaRichiestaIscrizione() {
		this.setTarget(SUCCESS);
		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			this.setTarget(SUCCESS);
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	/**
	 * ...
	 */
	public String accediAlbo() {
		this.setTarget(SUCCESS);

		if (null != this.getCurrentUser() && 
			!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// verifica se esiste la comunicazione in stato PROCESSATA (6)...
				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				
				this.dettComunicazione = ComunicazioniUtilities.retrieveComunicazioneConStati(
						this.comunicazioniManager, 
						this.getCurrentUser().getUsername(), 
						null, 
						null, 
						PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO_FORNITORI, 
						stati);

				this.urlAlboFornitori = (String)this.appParamManager.getConfigurationValue(AppParamManager.URL_ALBO_FORNITORI_ESTERNI);
				
				if(this.dettComunicazione != null) {
					// apri l'url presente in ppcommons_properties
					this.setTarget(SUCCESS);
				} else {
					//this.addActionError(this.getText("Errors.???"));
					this.setTarget(INPUT);
				}

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "accediAlbo");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}
		
		return this.getTarget();
	}

	/**
	 * ...
	 * @throws Throwable 
	 */
	private void sendComunicazione() throws Throwable {
		Event evento = new Event();
		evento.setUsername(this.getCurrentUser().getUsername());
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO_FORNITORI);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Invio comunicazione richiesta iscrizione albo fornitori "
				+ PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE
				+ " in stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		
		try {
			// prepara ed invia la comunicazione FS15...
			GregorianCalendar now = new GregorianCalendar();
				
			DettaglioComunicazioneType dettCom = new DettaglioComunicazioneType();
			dettCom.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			dettCom.setChiave1(this.getCurrentUser().getUsername());
			dettCom.setDataInserimento(now);
			dettCom.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			dettCom.setOggetto("Richiesta iscrizione ad albo fornitori");
			//dettCom.setTesto(???);
			dettCom.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO_FORNITORI);
			dettCom.setDataAggiornamentoStato(now);
			
			ComunicazioneType comunicazione = new ComunicazioneType();
			comunicazione.setDettaglioComunicazione(dettCom);
			
			Long id = this.comunicazioniManager.sendComunicazione(comunicazione);
			
			evento.setMessage("Invio comunicazione richiesta iscrizione albo fornitori "
					+ PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO_FORNITORI
					+ " con id " + id
					+ " in stato " + comunicazione.getDettaglioComunicazione().getStato());
			
		} catch (Throwable t) {
			evento.setError(t);
			throw t;
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}
	
}
