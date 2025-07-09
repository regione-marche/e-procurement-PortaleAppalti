package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.exception.ApsException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import it.eldasoft.www.sil.WSGareAppalto.AbilitazioniGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.BaseDgueAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.WSOperazioniGeneraliWrapper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IAltriDatiAnagraficiImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.dgue.dto.AnagraficaOE;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class DgueAction extends BaseDgueAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1490855177622325069L;
	
	protected static final Logger log = LoggerFactory.getLogger(DgueAction.class);

	protected String json;
	protected InputStream inputStream;

	protected IComunicazioniManager comunicazioniManager;
	protected WSOperazioniGeneraliWrapper opGenerali;

	public String execute() {
		long start = System.currentTimeMillis();
		String method = getRequest().getMethod();
		log.debug("DgueAction - request method: {}", method);
		
		try {
			//La prima richiesta inviata dal browser è di tipo OPTION
			//Questa richiesta serve al browser per capire che tipo di richieste sono accettate, in questo caso
			//GET e OPTION (come specificato nell'addResponseHeader)
			this.addResponseHeader();
			if(HttpMethod.OPTIONS.equals(method))
				return SUCCESS;

			Map<String, String> properties = getValidDgueProperties(false);
			if(properties.get(DGUE_SYMKEY) == null || properties.get(DGUE_JWTKEY) == null)
				throw new Exception("Missing params with category " + DGUE_CATEGORY);
			JSONObject decodedData = getDecodedData(false);
			log.debug("decodedData: {}", decodedData);

			//Controllo l'autenticazione
			String username = checkAuthentication(decodedData);

			// prendo la comunicazione + devo cercare se e' rti o meno
			WizardRegistrazioneImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
					username
					, this
			);
			
			// recupero il codice gara
			String codice = decodedData.getString(JSON_CODICE);
			String progressivoOfferta = decodedData.getString(JSON_PROGRESSIVO_OFFERTA);
			if(StringUtils.isBlank(progressivoOfferta)) {
				log.warn("progressivoOfferta non impostata nel token,  username:{}, codice: {}",username,codice);
				throw new Exception("Progressivo Offerta non impostata per DGUE.");
			}
			
			AbilitazioniGaraType agt = bandiManager.getAbilitazioniGara(username, codice);
			log.debug("agt.isRichInvioOfferta(): {}", agt.isRichInvioOfferta());
			log.debug("agt.isRichPartecipazione(): {}", agt.isRichPartecipazione());
			if (agt == null || (!agt.isRichInvioOfferta() && !agt.isRichPartecipazione())) {
				log.warn("Utente con Username '{}' ha cercato di accedere ai dati di una gara, con codice {}, cui non ha abilitazione.", username, codice);
				throw new Exception("Accesso ai dati di una gara senza abilitazione per DGUE .");
			}
			
			String tipoComunicazione =
					agt.isRichInvioOfferta()
						? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT
						: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;

			//Recupero la comunicazione per recuperarmi i dati dell'azienda
			DettaglioComunicazioneType comunicazione = ComunicazioniUtilities.retrieveComunicazione(
					comunicazioniManager
					, username
					, codice
					, progressivoOfferta
					, CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA
					, tipoComunicazione
			);
			if (comunicazione == null)
				throw new Exception("Comunicazione " + tipoComunicazione + " in bozza non presente.");

			log.trace("comunicazione: {}", comunicazione);

			//Recupero il wizard per recuperasi i dati dell'azienda
			WizardPartecipazioneHelper wizardPartecipazione = new WizardPartecipazioneHelper(
					comunicazioniManager.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, comunicazione.getId())
			);

			opGenerali = (WSOperazioniGeneraliWrapper)
					WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext())
							.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI);


			//Immetto i dati dell'azienda in un JSON
			JSONObject jsonObj = JSONObject.fromObject(
					createAnagraficaOE(datiImpresaHelper, wizardPartecipazione)
			);

			//Recupero il file da inviare all'm-dgue
			FileType requestFileToSend = getDgueRequestDocument(decodedData);
			JSONObject infoOE = new JSONObject();
			infoOE.put(JSON_DGUE_REQUEST, Base64.encode(getUnpackedFileBytes(requestFileToSend)));
			infoOE.put(JSON_LOTTI, retrieveCigLotti(wizardPartecipazione, codice, tipoComunicazione));
			infoOE.put(JSON_INFO_OE, jsonObj);
			infoOE.put("serviceProviderURL", StringUtils.trimToNull(retrieveServiceUrl("")));

			log.debug("infoOE.containsKey({}): {}", JSON_DGUE_REQUEST, infoOE.containsKey(JSON_DGUE_REQUEST));
			log.trace("anagOE: {}", jsonObj);

			JSONObject data = new JSONObject();
			data.put(JSON_DATA, infoOE);
			json = data.toString();
			log.trace("this.json: {}", json);
			inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		} catch(Base64DecodingException e) {
			log.error("Error on parsing data", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch(IllegalArgumentException | SignatureException | MalformedJwtException | ExpiredJwtException e) {
			log.error("RequestError", e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch(Exception e) {
			log.error("GeneralError", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			if(inputStream == null) {
				inputStream = new InputStream() {
					@Override
					public int read() throws IOException {
						return -1;
					}
				};
			}
			log.debug("DgueAction - Time execution: {} ms",(System.currentTimeMillis() - start));
		}

		return SUCCESS;
	}

	private Set<String> retrieveCigLotti(
			WizardPartecipazioneHelper wizardPartecipazione
			, String codice
			, String tipoComunicazione
	) throws ApsException {
		LottoGaraType[] lgta = bandiManager.getLottiGara(codice);
		return ArrayUtils.isNotEmpty(lgta)
				? retrieveCIGLotti(lgta, tipoComunicazione, wizardPartecipazione)
				: new HashSet<>();
	}

	/**
	 * Crea l'anagraficaOE con i dati dell'impresa
	 * @param datiImpresaHelper
	 * @param wizardPartecipazione
	 * @return
	 */
	private AnagraficaOE createAnagraficaOE(
			WizardRegistrazioneImpresaHelper datiImpresaHelper
			, WizardPartecipazioneHelper wizardPartecipazione
	) throws RemoteException {
		AnagraficaOE toReturn = new AnagraficaOE();


		toReturn.setRuolo("Unica Offerente / Sole contractor");//Sole contractor


		log.trace("wizardPartecipazione: {}", wizardPartecipazione);
		if (wizardPartecipazione.isRti())
			valorizeRTIFields(toReturn, wizardPartecipazione);

		setMainCompanyDataIntoJsonObject(toReturn, datiImpresaHelper.getDatiPrincipaliImpresa(), opGenerali);

		//partecipanti se presenti
		toReturn.setLegaleRappresentanti(
				new ArrayList<ISoggettoImpresa>() {	{
					addAll(dtoDGUESogg(datiImpresaHelper.getLegaliRappresentantiImpresa()));
					addAll(dtoDGUESogg(datiImpresaHelper.getAltreCaricheImpresa()));
					addAll(dtoDGUESogg(datiImpresaHelper.getCollaboratoriImpresa()));
					addAll(dtoDGUESogg(datiImpresaHelper.getDirettoriTecniciImpresa()));
					if (datiImpresaHelper.getAltriDatiAnagraficiImpresa() != null
							&& StringUtils.isNotEmpty(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getCognome()))
						add(altriDatiAnagraficiToSoggImpresa(datiImpresaHelper.getAltriDatiAnagraficiImpresa()));
				} }
		);

		Map<String, String> tipiSoggetti = getMaps().get(InterceptorEncodedData.LISTA_SOGGETTI_TIPI_SOGGETTO);
		toReturn.getLegaleRappresentanti().forEach(legRappr -> legRappr.setTipoSoggetto(tipiSoggetti.get(legRappr.getTipoSoggetto())));

		return toReturn;
	}

	private Collection<? extends ISoggettoImpresa> dtoDGUESogg(ArrayList<ISoggettoImpresa> soggetti) {
		soggetti.stream()
				.filter(it -> it.getNazione() != null)
			.forEach(this::nazioneSoggettoToDGUE);
		return soggetti;
	}

	private void nazioneSoggettoToDGUE(ISoggettoImpresa soggetto) {
		try {
			soggetto.setNazione(opGenerali.getProxyWSOPGenerali().getNazioniCodificateDGUE(soggetto.getNazione()));
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	private ISoggettoImpresa altriDatiAnagraficiToSoggImpresa(IAltriDatiAnagraficiImpresa altriDatiAnagraficiImpresa) {
		SoggettoImpresaHelper toReturn = new SoggettoImpresaHelper();

		toReturn.setNome(altriDatiAnagraficiImpresa.getNome());
		toReturn.setCognome(altriDatiAnagraficiImpresa.getCognome());
		toReturn.setSesso(altriDatiAnagraficiImpresa.getSesso());
		toReturn.setTitolo(altriDatiAnagraficiImpresa.getTitolo());
		toReturn.setDataNascita(altriDatiAnagraficiImpresa.getDataNascita());
		toReturn.setTipologiaCassaPrevidenza(altriDatiAnagraficiImpresa.getTipologiaCassaPrevidenza());
		toReturn.setDataIscrizioneAlboProf(altriDatiAnagraficiImpresa.getDataIscrizioneAlboProf());
		toReturn.setProvinciaIscrizioneAlboProf(altriDatiAnagraficiImpresa.getProvinciaIscrizioneAlboProf());
		toReturn.setNumIscrizioneAlboProf(altriDatiAnagraficiImpresa.getNumIscrizioneAlboProf());
		toReturn.setNumMatricolaCassaPrevidenza(altriDatiAnagraficiImpresa.getNumMatricolaCassaPrevidenza());
		toReturn.setProvinciaNascita(altriDatiAnagraficiImpresa.getProvinciaNascita());
		toReturn.setComuneNascita(altriDatiAnagraficiImpresa.getComuneNascita());

		return toReturn;
	}

	/**
	 * Imposta i dati principali dell'impresa nell' AnagraficaOE
	 * @param anagraficaOE
	 * @param mainData
	 */
	private static void setMainCompanyDataIntoJsonObject(AnagraficaOE anagraficaOE, IDatiPrincipaliImpresa mainData, WSOperazioniGeneraliWrapper opGenerali) throws RemoteException {
		anagraficaOE.setCap(mainData.getCapSedeLegale());
		if (StringUtils.isNotEmpty(mainData.getProvinciaSedeLegale()))
			anagraficaOE.setCitta(mainData.getComuneSedeLegale() + " (" + mainData.getProvinciaSedeLegale() + ")");
		else
			anagraficaOE.setCitta(mainData.getComuneSedeLegale());
		String indirizzo = mainData.getIndirizzoSedeLegale() + ", " + mainData.getNumCivicoSedeLegale();
		anagraficaOE.setIndirizzo(indirizzo);

		if (mainData.getNazioneSedeLegale() != null)
			anagraficaOE.setNazione(opGenerali.getProxyWSOPGenerali().getNazioniCodificateDGUE(mainData.getNazioneSedeLegale()));
		if (StringUtils.equalsIgnoreCase(mainData.getNazioneSedeLegale(), "ITALIA"))
			anagraficaOE.setPartitaIva(mainData.getPartitaIVA());
		if (StringUtils.equalsIgnoreCase(mainData.getNazioneSedeLegale(), "ITALIA"))
			anagraficaOE.setCodiceFiscale(mainData.getCodiceFiscale());
		anagraficaOE.setRagioneSociale(mainData.getRagioneSociale());

		String email = mainData.getEmailPECRecapito() != null
						? mainData.getEmailPECRecapito()
						: mainData.getEmailRecapito();
		anagraficaOE.setEmail(email);
		anagraficaOE.setWebSite(mainData.getSitoWeb());
		anagraficaOE.setCodiceClassifica(mainData.getMicroPiccolaMediaImpresa());
		//TODO aggiungere i dati numero dipendenti / turnover
		anagraficaOE.setTelefono(mainData.getTelefonoRecapito());
	}

	/**
	 * Recupera la lista dei cig dei lotti
	 * @param lgta
	 * @param tipoComunicazione
	 * @param wizardPartecipazione
	 * @return
	 */
	public Set<String> retrieveCIGLotti(LottoGaraType[] lgta, String tipoComunicazione, WizardPartecipazioneHelper wizardPartecipazione) {
		boolean isInvioOvverta = StringUtils.equals(tipoComunicazione, PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT);
		TreeSet<String> codLottoStr = wizardPartecipazione.getLotti();
		return Arrays.stream(lgta)
				.filter(lotto -> !isInvioOvverta || codLottoStr.contains(lotto.getCodiceLotto()))
				.map(LottoGaraType::getCig)
				.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	/**
	 * Valorizza nella anagraficaOE i campi relativi al RTI
	 * @param anag
	 * @param wizardPartecipazione
	 */
	private void valorizeRTIFields(AnagraficaOE anag, WizardPartecipazioneHelper wizardPartecipazione) {
		anag.setRuolo("Mandataria / Lead Entity"); //Lead Entity
		// aggiungere i dati delle partecipanti
		anag.setComponentiRti(wizardPartecipazione.getComponenti());
		anag.setDenominazioneRTI(wizardPartecipazione.getDenominazioneRTI());
		Map<String, String> tipiImpresa = getMaps().get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO);
		anag.getComponentiRti()
				.forEach(comp -> comp.setTipoImpresa(tipiImpresa.get(comp.getTipoImpresa())));
	}

	protected void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public InputStream getInputStream() {
		return this.inputStream;
	}
	
	public String getJson() {
		return json;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

}
