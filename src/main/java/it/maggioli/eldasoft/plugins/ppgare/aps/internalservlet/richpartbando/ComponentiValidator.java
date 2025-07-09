package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;

import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.www.sil.WSGareAppalto.GetCfgCheckParametroOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.ProcessPageRTIAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;

/**
 * Helper per la validazione dei dati inseriti nella lista dei componenti di RT o consorziate
 *
 * @author ...
 */
public class ComponentiValidator {

	protected BaseAction action;
	protected boolean gruppiIVAAbilitati;
	protected IDatiPrincipaliImpresa datiPrincipaliImpresa;
	protected boolean isRti;
	protected List<IImpresa> imprese;			// imprese del raggruppamento temporaneo (RTI), consorziate o imprese ausiliarie da validare
	protected boolean validaConImpresaLoggata;	// valida i dati inseriti con quelli dell'impresa loggata
	
	private IRaggruppamenti raggruppamenti;
		
	/**
	 * messaggi degli errori di validazione (ridefinibili nelle classi figlie)
	 */
	protected String errorsMoreDuplicatedRagioneSociale(String[] args) {
		return (isRti
				? action.getText("Errors.rti.moreDuplicatedRagioneSociale", args)
				: action.getText("Errors.consorzio.moreDuplicatedRagioneSociale", args)
		);
	}
	
	protected String errorsDuplicatedRagioneSociale(String[] args) {
		return (isRti
				? action.getText("Errors.rti.duplicatedRagioneSociale", args)
				: action.getText("Errors.consorzio.duplicatedRagioneSociale", args)
		);
	}

	protected String errorsMoreDuplicatedCodiceFiscale(String[] args) {
		return (isRti
				? action.getText("Errors.rti.moreDuplicatedCodiceFiscale", args)
				: action.getText("Errors.consorzio.moreDuplicatedCodiceFiscale", args)
		);
	}

	protected String errorsDuplicatedCodiceFiscale(String[] args) {
		return (isRti
				? action.getText("Errors.rti.duplicatedCodiceFiscale", args)
				: action.getText("Errors.consorzio.duplicatedCodiceFiscale", args)
		);
	}

	protected String errorsSameCodiceFiscale(String[] args) {
		return action.getText("Errors.sameCodiceFiscale", args);
	}
	
	protected String errorsSameCodiceFiscaleOperatore(String[] args) {
		return action.getText("Errors.sameCodiceFiscaleOperatore", args);
	}
	
	protected String errorsWrongForeignFiscalField(String[] args) {
		return action.getText("Errors.wrongForeignFiscalField", args);
	}
	
	protected String errorsSamePartitaIVA(String[] args) {
		return action.getText("Errors.samePartitaIVA", args);
	}

	protected String errorsDuplicatedPartitaIva(String[] args) {
		return (isRti
				? action.getText("Errors.rti.duplicatedPartitaIVA", args)
				: action.getText("Errors.consorzio.duplicatedPartitaIVA", args)
		);
	}

	/**
	 * costruttori
	 */
	public ComponentiValidator() {} 
	
	public ComponentiValidator(
			IRaggruppamenti raggruppamenti,
			IDatiPrincipaliImpresa impresa,
			BaseAction action) 
	{
		//this.partecipazione = partecipazione;
		this.action = action;
		this.raggruppamenti = raggruppamenti;
		datiPrincipaliImpresa = impresa;
		isRti = (raggruppamenti != null ? raggruppamenti.isRti() : false);
		validaConImpresaLoggata = true;				
		getGruppoIvaConfig();
		imprese = getElencoImprese();
	}
	
	/**
	 * genera l'elenco delle componenti del RTI/consorziate  
	 */
	private List<IImpresa> getElencoImprese() {
		List<IImpresa> componentiRti = new ArrayList<IImpresa>();
		if(raggruppamenti != null) {
			// la partecipazione in RTI puo' essere fatta per una gara o per un elenco operatori
			List<IComponente> imprese = null;
			if(raggruppamenti instanceof WizardIscrizioneHelper) {
				// elenco operatori
				WizardIscrizioneHelper iscrizione = (WizardIscrizioneHelper) raggruppamenti;
				imprese = ( !raggruppamenti.isRti() 
						   ? iscrizione.getComponenti()
						   : iscrizione.getComponentiRTI()
				);
			} else {
				// gara
				imprese = ((WizardPartecipazioneHelper) raggruppamenti).getComponenti();
			}		
			if(imprese != null) {
				imprese.stream()
					.forEach(c -> componentiRti.add(c));
			}
		}
		return componentiRti;
	}

	private void getGruppoIvaConfig() {
		gruppiIVAAbilitati = false;
		try {
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							 ServletActionContext.getRequest());
			GetCfgCheckParametroOutType check = wrapper.getProxyWSGare()
					.getCfgCheckGruppoIvaAbilitato();
			if (check.getErrore() != null)
				throw new RemoteException(check.getErrore());
			gruppiIVAAbilitati = check.getCheckAbilitato();
		} catch (Exception ex) {
		}
	}	

	/**
	 * normalizza CF, P. IVA e Id fiscale estero
	 */
	protected void normalizeCfPivaIdfiscale(IImpresa a) {
		if(a != null) {
			if (StringUtils.isNotEmpty(a.getCodiceFiscale()))
				a.setCodiceFiscale(a.getCodiceFiscale().toUpperCase());
			if (StringUtils.isNotEmpty(a.getPartitaIVA()))
				a.setPartitaIVA(a.getPartitaIVA().toUpperCase());
			if (StringUtils.isNotEmpty(a.getIdFiscaleEstero()))
				a.setIdFiscaleEstero(a.getIdFiscaleEstero().toUpperCase());
		}
	}

	/**
	 * validazione dei campi richiesti
	 */
	public boolean validateRequiredInputField(IImpresa form, String id) {
		ICodificheManager codificheManager = (ICodificheManager)ApsWebApplicationUtils
				.getBean(CommonSystemConstants.CACHE_CODIFICHE, action.getRequest());
	
		// controllo la partita iva, che puo' essere facoltativa per il
		// libero professionista ed impresa sociale se previsto da configurazione, mentre per
		// tutti gli altri casi risulta obbligatoria
		boolean isLiberoProfessionista = codificheManager
				.getCodifiche()
				.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
				.containsKey(form.getTipoImpresa());
		
		boolean isImpresaSociale = codificheManager
				.getCodifiche()
				.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_SOCIALE)
				.containsKey(form.getTipoImpresa());
		
		boolean isPartitaIVAObbligatoriaLiberoProfessionista = !codificheManager
				.getCodifiche()
				.get(InterceptorEncodedData.CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA)
				.containsValue("1");
		
		boolean isPartitaIVAObbligatoriaImpresaSociale = !codificheManager
				.getCodifiche()
				.get(InterceptorEncodedData.CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE)
				.containsValue("1");
		
		boolean OEItaliano = !"2".equalsIgnoreCase(form.getAmbitoTerritoriale());
		
		normalizeCfPivaIdfiscale(form);
		
		if(StringUtils.isEmpty(form.getNazione()) && OEItaliano) {
			form.setNazione("ITALIA");
		}
	
		if (StringUtils.isBlank(id)) {
			// id vuoto
			if ((StringUtils.isNotBlank(form.getRagioneSociale())
				 || StringUtils.isNotBlank(form.getCodiceFiscale())
				 || StringUtils.isNotBlank(form.getTipoImpresa()))) 
			{
				if (OEItaliano) {
					if (StringUtils.isBlank(form.getPartitaIVA())
						&& ((!isLiberoProfessionista && !isImpresaSociale)
							 || (isLiberoProfessionista && isPartitaIVAObbligatoriaLiberoProfessionista)
							 || (isImpresaSociale && isPartitaIVAObbligatoriaImpresaSociale))) 
					{
						action.addFieldError("partitaIVA",
								action.getText("Errors.requiredstring", new String[] {action.getTextFromDB("partitaIVA")}));
					}
				}
			}
			
		} else {
			// id con valore (form in aggiornamento)
			if (StringUtils.isBlank(form.getRagioneSociale())) {
				action.addFieldError("ragioneSociale", 
						action.getText("Errors.requiredstring", new String[]{action.getTextFromDB("ragioneSociale")}));
			}
	
			// controllo la partita iva, che puo' essere facoltativa per il
			// libero professionista e impresa sociale se previsto da
			// configurazione, mentre per tutti gli altri casi risulta
			// obbligatoria.
			// si ripete il controllo perche' esiste il campo
			// Nazione sempre fillato che va trascurato e bisogna capire se il
			// form e' di un record esistente che e' stato svuotato di tutti i dati
			if (OEItaliano) {
				if (StringUtils.isBlank(form.getPartitaIVA())
					&& ((!isLiberoProfessionista && !isImpresaSociale)
						 || (isLiberoProfessionista && isPartitaIVAObbligatoriaLiberoProfessionista)
						 || (isImpresaSociale && isPartitaIVAObbligatoriaImpresaSociale))) 
				{
					action.addFieldError("partitaIVA", 
							action.getText("Errors.requiredstring", new String[]{action.getTextFromDB("partitaIVA")}));
				}
				if (StringUtils.isBlank(form.getCodiceFiscale())) {
					action.addFieldError("codiceFiscale", 
							action.getText("Errors.requiredstring", new String[]{action.getTextFromDB("codiceFiscale")}));
				}
			}
	
			if (StringUtils.isBlank(form.getTipoImpresa())) {
				action.addFieldError("tipoImpresa", 
						action.getText("Errors.requiredstring", new String[]{action.getTextFromDB("tipoImpresa")}));
			}
		}
		
		return (action.getFieldErrors().size() <= 0);
	}

	/**
	 * validazione del campo ragione sociale
	 */
	public boolean validateInputRagioneSociale(IImpresa form, String id) {
		// verifica se il nuovo operatore inserito in editing e' duplicato
		// verifica se la ragione sociale e' duplicata
		normalizeCfPivaIdfiscale(form);
		
		if (StringUtils.isNotBlank(form.getRagioneSociale())) {
			// verifica se il componente e' in aggiornamento o inserimento e non aggiungerlo alla lista...
			String ragsocInaggiornamento = (StringUtils.isNotBlank(id) && imprese.get(new Integer(id)) != null
											? imprese.get(new Integer(id)).getRagioneSociale() 
											: null);
			
			List<String> listaRagioniSociali = imprese.stream()
					.map(IImpresa::getRagioneSociale)								// crea la lista delle ragioni sociali
					.map(String::toUpperCase)
					.filter(r -> !r.equalsIgnoreCase(ragsocInaggiornamento))		// non aggiungere il componente in aggiornamento
					.collect(Collectors.toList());
			
			// verifica se uno dei componenti ha la ragione sociale duplicata
			Map<String, Long> count = listaRagioniSociali.stream()
					//.collect(ragsoc -> Collectors.groupingBy(ragsoc, Collectors.counting()));
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			
			for (Map.Entry<String, Long> item : count.entrySet()) {
				if(item.getValue() > 1) {
					action.addFieldError("ragioneSociale", errorsMoreDuplicatedRagioneSociale(new String[] {item.getKey()}));
					break;
				}
			}
			
			// verifica se il componente inserito/modificato ha la ragione sociale duplicata
			if(listaRagioniSociali.contains(form.getRagioneSociale().toUpperCase())) {
				action.addFieldError("ragioneSociale", errorsDuplicatedRagioneSociale(new String[] {form.getRagioneSociale()}));
			}
		}
		
		return (action.getFieldErrors().size() <= 0);
	}

	/**
	 * validazione del campo codice fiscale
	 */
	public boolean validateInputCodiceFiscale(IImpresa form, String id) {
		// 1 o NULL indica operatore italiano
		boolean OEItaliano = !"2".equalsIgnoreCase(form.getAmbitoTerritoriale());
		boolean impresaItaliana = "ITALIA".equalsIgnoreCase(form.getNazione());
		
		normalizeCfPivaIdfiscale(form);

		// controlli con la MANDATARIA/CONSORZIO (impresa loggata)
		// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi

		// verifica se il CF del componente inserito/modificato e' uguale a quello dell'impresa loggata
		if (OEItaliano) {
			
			// verifica il formato del codice fiscale
			if (!"".equals(form.getCodiceFiscale())
				&& !(UtilityFiscali.isValidCodiceFiscale(form.getCodiceFiscale(), impresaItaliana) ||
					 UtilityFiscali.isValidPartitaIVA(form.getCodiceFiscale(), impresaItaliana))) 
			{
				if (impresaItaliana) {
					action.addFieldError("codiceFiscale",
							action.getText("Errors.wrongField", new String[] {action.getTextFromDB("codiceFiscale")}));
				} else {
					action.addFieldError("codiceFiscale", 
							errorsWrongForeignFiscalField(new String[] {action.getTextFromDB("codiceFiscale")}));
				}
				return false;
			}

			// controlli con la MANDATARIA/CONSORZIO (impresa loggata)
			// verifica se il codice fiscale inserito e' uguale a quello dell'impresa loggata
			if(validaConImpresaLoggata) {
				if (StringUtils.isNotBlank(form.getCodiceFiscale())) {
					boolean stessoCf = (datiPrincipaliImpresa.getCodiceFiscale().equalsIgnoreCase(form.getCodiceFiscale()));
					
					// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi
					boolean stessaPi = (
							StringUtils.isNotBlank(form.getPartitaIVA()) && StringUtils.isNotBlank(datiPrincipaliImpresa.getPartitaIVA())
							&& datiPrincipaliImpresa.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA())
					);
					
					if( (!gruppiIVAAbilitati && stessoCf) || (gruppiIVAAbilitati && stessoCf && stessaPi) ) {
						action.addFieldError("codiceFiscale", errorsSameCodiceFiscale(new String[] {form.getCodiceFiscale()}));
						return false;
					}
				}
			}
		}

		// *************************************************************************************************
		// controlla l'univocita' del codice fiscale
		// 1) nessuna mandante italiana oppure estera puo' avere il medesimo codice fiscale della mandataria
		// 2) nessuna mandante italiana puo' avere codice fiscale di altra mandante italiana
		// 3) nessuna mandante estera puo' avere identificativo fiscale di altra mandante estera
		if (StringUtils.isNotBlank(form.getCodiceFiscale()) || StringUtils.isNotBlank(form.getIdFiscaleEstero())) {
			List<String> listaCodiciFiscali = new ArrayList<String>();
			List<String> listaIdFiscaleEstero = new ArrayList<String>();

			// controlla l'univocita' del codice fiscale
			// 1) nessuna mandante italiana oppure estera puo' avere il medesimo codice fiscale della mandataria
			// 2) nessuna mandante italiana puo' avere codice fiscale di altra mandante italiana
			// 3) nessuna mandante estera puo' avere identificativo fiscale di altra mandante estera
			//
			// NB: in caso di RTI
			// - per la gare la lista "imprese" contiene solo l'elenco delle mandati 
			//	 (e le jsp puntano agli id delle mandanti in modo corretto)
			// - per gli elenchi operatore la lista "imprese" contiene la mandataria e le mandanti 
			//	 (perche' nelle jsp gli indici usano gli di alle mandanti in modo leggermente diverso dalle offerte!!!)
			// QUINDI per gli elenchi operatore, ignora il primo elemento della lista (che e' sempre la mandataria) per evitare un falso errore
			int i0 = (raggruppamenti != null && raggruppamenti instanceof WizardIscrizioneHelper && isRti ? 1 : 0);
			
			for (int i = i0; i < imprese.size(); i++) {
				IImpresa componente = imprese.get(i);
				
				// calcola le chiavi per il CF
				String cfMandataria = IImpresa.getChiaveCF(datiPrincipaliImpresa.getCodiceFiscale(), datiPrincipaliImpresa.getNazioneSedeLegale());
				String cfMandante = IImpresa.getChiaveCF(componente);
				String idFiscaleMandante = IImpresa.getChiaveCF(componente);
	
				// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi
				boolean stessaPiMandataria = (
						StringUtils.isNotBlank(form.getPartitaIVA()) && StringUtils.isNotBlank(datiPrincipaliImpresa.getPartitaIVA())
						&& datiPrincipaliImpresa.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA())
				);

				// 1) nessuna mandante italiana oppure estera puo' avere il medesimo codice fiscale della mandataria
				if(validaConImpresaLoggata) {
					if (cfMandante.equalsIgnoreCase(cfMandataria) || idFiscaleMandante.equalsIgnoreCase(cfMandataria)) {
						if( (!gruppiIVAAbilitati) || (gruppiIVAAbilitati && stessaPiMandataria) ) {
							if (cfMandante.equalsIgnoreCase(cfMandataria)) {
								action.addFieldError("codiceFiscale", errorsSameCodiceFiscaleOperatore(new String[] {componente.getCodiceFiscale()}));
							} else {
								action.addFieldError("idFiscaleEstero", errorsSameCodiceFiscaleOperatore(new String[] {componente.getIdFiscaleEstero()}));
							}
							return false;
						}
					}
				}
	
				boolean operItaliano = !"2".equals(componente.getAmbitoTerritoriale());
				if (operItaliano) {
					// 2) nessuna mandante italiana puo' avere codice fiscale di altra mandante italiana
					// operatore italiano
					if (!listaCodiciFiscali.contains(cfMandante)) {
						if (StringUtils.isNotBlank(componente.getCodiceFiscale())) {
							listaCodiciFiscali.add(cfMandante);
						}
					} else {
						action.addFieldError("codiceFiscale", errorsMoreDuplicatedRagioneSociale(new String[] {componente.getCodiceFiscale()}));	
						return false;
					}
	
				} else {
					// 3) nessuna mandante estera puo' avere identificativo fiscale di altra mandante estera
					// operatore UE o extra UE
					if (!listaIdFiscaleEstero.contains(idFiscaleMandante)) {
						if (StringUtils.isNotBlank(componente.getIdFiscaleEstero())) {
							listaIdFiscaleEstero.add(idFiscaleMandante);
						}
					} else {
						action.addFieldError("idFiscaleEstero", errorsMoreDuplicatedCodiceFiscale(new String[] {componente.getIdFiscaleEstero()}));
						return false;
					}
				}
	
				// verifica se il componente e' in aggiornamento o inserimento...
				if (StringUtils.isNotBlank(id)) {
					IImpresa oggettoInAggiornamento = imprese.get(new Integer(id));
					if (StringUtils.isNotEmpty(oggettoInAggiornamento.getIdFiscaleEstero())) {
						listaIdFiscaleEstero.remove(IImpresa.getChiaveCF(oggettoInAggiornamento));
					} else {
						listaCodiciFiscali.remove(IImpresa.getChiaveCF(oggettoInAggiornamento));
					}
				}
			}

			// controlli con la MANDATARIA/CONSORZIO (impresa loggata)
			// verifica se la ditta inserita/modificata ha CF/Id fiscale uguale all'impresa loggata
			// 1) nessuna mandante italiana oppure estera puo' avere il medesimo codice fiscale della mandataria
			String cfMandataria = IImpresa.getChiaveCF(datiPrincipaliImpresa.getCodiceFiscale(), datiPrincipaliImpresa.getNazioneSedeLegale());
			String cfMandante = IImpresa.getChiaveCF(form.getCodiceFiscale(), form.getNazione());
			String idFiscale = IImpresa.getChiaveCF(form.getIdFiscaleEstero(), form.getNazione());
			
			if(validaConImpresaLoggata) {
				boolean stessoCf = (cfMandante.equalsIgnoreCase(cfMandataria) || idFiscale.equalsIgnoreCase(cfMandataria));
				
				// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi
				boolean stessaPi = (
						StringUtils.isNotBlank(form.getPartitaIVA()) && StringUtils.isNotBlank(datiPrincipaliImpresa.getPartitaIVA())
						&& datiPrincipaliImpresa.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA())
				);
				
				if( (!gruppiIVAAbilitati && stessoCf) || (gruppiIVAAbilitati && stessoCf && stessaPi) ) {
					if (cfMandante.equalsIgnoreCase(cfMandataria)) {
						action.addFieldError("codiceFiscale", errorsSameCodiceFiscaleOperatore(new String[] {form.getCodiceFiscale()}));
					} else {
						action.addFieldError("idFiscaleEstero", errorsSameCodiceFiscaleOperatore(new String[] {form.getIdFiscaleEstero()}));
					}
					return false;
				}
			}

			// verifica se il codice fiscale e' duplicato
			if (StringUtils.isNotBlank(form.getCodiceFiscale())) {
				if (listaCodiciFiscali.contains(form.getCodiceFiscale().toUpperCase())) {
					action.addFieldError("codiceFiscale", errorsDuplicatedCodiceFiscale(new String[] {form.getCodiceFiscale()}));					
				}
			}

			// verifica se l'id fiscale estero e' duplicato
			if (StringUtils.isNotBlank(form.getIdFiscaleEstero())) {
				if (listaIdFiscaleEstero.contains(form.getIdFiscaleEstero().toUpperCase())) {
					action.addFieldError("idFiscaleEstero", errorsDuplicatedCodiceFiscale(new String[] {form.getIdFiscaleEstero()}));			
				}
			}
		}
		
		return (action.getFieldErrors().size() <= 0);
	}
	
	/**
	 * validazione del campo partita iva
	 */
	public boolean validateInputPartitaIVA(IImpresa form, String id) {
		// 1 o NULL indica operatore italiano
		boolean OEItaliano = !"2".equalsIgnoreCase(form.getAmbitoTerritoriale());
		boolean impresaItaliana = ("ITALIA".equalsIgnoreCase(form.getNazione()));
		
		normalizeCfPivaIdfiscale(form);
				
		if (OEItaliano) {
			// verifica il formato della partita iva
			if (!"".equals(form.getPartitaIVA()) && !UtilityFiscali.isValidPartitaIVA(form.getPartitaIVA(), impresaItaliana)) {
				if (impresaItaliana) {
					action.addFieldError("partitaIVA", 
							action.getText("Errors.wrongField", new String[]{action.getTextFromDB("partitaIVA")}));
				} else {
					action.addFieldError("partitaIVA", errorsWrongForeignFiscalField(new String[] {action.getTextFromDB("partitaIVA")}));
				}
			}

			// controlli con la MANDATARIA/CONSORZIO (impresa loggata)
			// verifica se la partita iva inserita e' uguale a quello dell'impresa loggata
			if(validaConImpresaLoggata) {
				if (StringUtils.isNotBlank(form.getPartitaIVA()) && StringUtils.isNotBlank(datiPrincipaliImpresa.getPartitaIVA())) {
					
					boolean stessaPi = (datiPrincipaliImpresa.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA()));
					
					// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi
					boolean stessoCf = (
							(StringUtils.isNotBlank(form.getCodiceFiscale()) && StringUtils.isNotBlank(datiPrincipaliImpresa.getCodiceFiscale()))
							&& datiPrincipaliImpresa.getCodiceFiscale().equalsIgnoreCase(form.getCodiceFiscale())
					);
	
					if( (!gruppiIVAAbilitati && stessaPi) || (gruppiIVAAbilitati && stessaPi && stessoCf) ) {
						action.addFieldError("partitaIVA", errorsSamePartitaIVA(new String[] {form.getPartitaIVA()}));
					}
				}
			}
			
			// controlli tra l'impresa inserita e l'elenco di quelle gia' inserite
			if (StringUtils.isNotEmpty(form.getPartitaIVA())) {
				// verifica se il componente e' in aggiornamento o inserimento...
				IImpresa oggettoInAggiornamento = (StringUtils.isNotBlank(id) ? imprese.get(new Integer(id)) : null);
				boolean impresaEsistente = false;
				if( !gruppiIVAAbilitati ) {
					// verifica se tra le ditte che hanno P.I. non vuota, ne esiste gia' una con la stessa P.I. inserita
					impresaEsistente = imprese.stream()
							.filter(i -> StringUtils.isNotEmpty(i.getPartitaIVA()) ) 
							.anyMatch(i -> i != oggettoInAggiornamento
						 			 	   && i.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA()));
				} else {
					// verifica se tra le ditte che hanno P.I. non vuota, ne esiste gia' una con stessa P.I. e CF inseriti
					impresaEsistente = imprese.stream()
							.filter(i -> StringUtils.isNotEmpty(i.getPartitaIVA()) && StringUtils.isNotEmpty(i.getCodiceFiscale())) 
							.anyMatch(i -> i != oggettoInAggiornamento 
										   && i.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA()) 
									       && i.getCodiceFiscale().equalsIgnoreCase(form.getCodiceFiscale()));
				}
				if(impresaEsistente) {
					action.addFieldError("partitaIVA", errorsDuplicatedPartitaIva(new String[] {form.getPartitaIVA()}));
				}
			}
		}
		
		return (action.getFieldErrors().size() <= 0);
	}

	/**
	 * verifica se ci sono componenti RTI che fatto parte delle imprese ausiliarie
	 * @param componente (optional) componente RTI/consorziata  da verificare
	 */
	public IImpresa isComponentiPresentiInImpreseAusiliarie(IComponente... componenteRti) {
		IImpresa impresa = null;
		
		// PORTAPPALT-1220: i controlli incrociati tra lista RT e ausiliarie sono disabilitati!!!
//		// in caso di consorziate esecutrici NON si controllano le imprese ausiliarie
//		if( !partecipazione.getImpresa().isConsorzio()
//			&& partecipazione.getImpreseAusiliarie() != null) 
//		{
//			IComponente componente = (componenteRti.length > 0 ? componenteRti[0] : null);
//			
//			// verifica se esistono compoenti RTI presenti anche come imprese ausiliaria...
//			impresa = imprese.stream()
//					.filter(rti -> partecipazione.getImpreseAusiliarie().stream()
//									.anyMatch(i -> IImpresa.equals(rti, i, gruppiIVAAbilitati))
//					)
//					.findFirst()
//					// ...se non ne esistono verifica se il componente e' gia' presente come impresa ausiliaria...
//					.orElse(partecipazione.getImpreseAusiliarie().stream()
//							.filter(rti -> IImpresa.equals(componente, rti, gruppiIVAAbilitati))
//							.findFirst()
//							.orElse(null)
//					);
//		}
		
		return impresa;
	}
	

}
