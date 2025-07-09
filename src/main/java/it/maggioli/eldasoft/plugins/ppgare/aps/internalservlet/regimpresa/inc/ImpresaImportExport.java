package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.DgueOrganizationDeserializer;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IIndirizzoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IndirizzoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiPrincipaliImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiUlterioriImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * Gestione di import/export dei dati impresa da/su file XML  
 * 
 * @author ...
 */
public class ImpresaImportExport {
	
	private Document document;				// document xml
	private String crcBuffer;				// buffer per il calcolo del crc
	private String[] xmlFields;				// elenco dei campi gestiti

	private static final Logger LOG = ApsSystemUtils.getLogger();
	
	private static final String XML_VERSION 					= "2";
	
	private static final char SEPARATOR 						= '|';
	
	private static final String TAG_IMPRESA 					= "Impresa";
	private static final String TAG_VERSION 					= "Version";
	private static final String TAG_CRC							= "Hash";
	private static final String TAG_DATIPRINCIPALI 				= "DatiPrincipali";
	private static final String TAG_ALTRIINDIRIZZI				= "AltriIndirizzi";
	private static final String TAG_ALTRIDATIANAGRAFICI			= "AltriDatiAnagrafici";
	private static final String TAG_LIBEROPROFESSIONISTA		= "LiberoProfessionista";
	private static final String TAG_DATIULTERIORI				= "DatiUlteriori";
	private static final String TAG_INDIRIZZO					= "Indirizzo";
	private static final String TAG_LISTA_LEGALIRAPPRESENTANTI 	= "legaliRappresentantiImpresa";
	private static final String TAG_LISTA_DIRETTORITECNICI 		= "direttoriTecniciImpresa";
	private static final String TAG_LISTA_ALTRECARICHE			= "altreCaricheImpresa";
	private static final String TAG_LISTA_COLLABORATORI			= "collaboratoriImpresa";
	private static final String TAG_SOGGETTO					= "Soggetto";

	// gestione dei tabellati
	private InterceptorEncodedData encodedData = new InterceptorEncodedData();
	private HashMap<String, String> tabellati = new HashMap<String, String>();

	private List<String> readXmlErrors;
	
	private URI michelangelo_base_uri;			// servizio Michelangelo (SACE)
	private Integer errorCode;					// servizio Michelangelo
	private String errorDescription;			// servizio Michelangelo
	
	/**
	 * ImportExportException 
	 */
	public class ImportExportException extends Exception {
		private static final long serialVersionUID = -6416233679729305535L;
		public ImportExportException() { super(); }
	    public ImportExportException(String message) { super(message); }
	    public ImportExportException(String message, Throwable cause) { super(message, cause); }
	    public ImportExportException(Throwable cause) { super(cause); }
	}
	
	/**
	 * WrongCRCException 
	 */
	public class WrongCRCException extends Exception {
		private static final long serialVersionUID = 1104596858690891600L;
		public WrongCRCException() { super(); }
	    public WrongCRCException(String message) { super(message); }
	    public WrongCRCException(String message, Throwable cause) { super(message, cause); }
	    public WrongCRCException(Throwable cause) { super(cause); }
	}
	

	private void initTabellati() {
		tabellati.put("NaturaGiuridica", InterceptorEncodedData.LISTA_TIPI_NATURA_GIURIDICA);
		tabellati.put("VatGroup", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("TipoImpresa", InterceptorEncodedData.LISTA_TIPI_IMPRESA);
		tabellati.put("ProvinciaSedeLegale", InterceptorEncodedData.LISTA_PROVINCE);
		tabellati.put("NazioneSedeLegale", InterceptorEncodedData.LISTA_NAZIONI);
		tabellati.put("TipoIndirizzo", InterceptorEncodedData.LISTA_TIPI_INDIRIZZO);
		tabellati.put("Provincia", InterceptorEncodedData.LISTA_PROVINCE);
		tabellati.put("Nazione", InterceptorEncodedData.LISTA_NAZIONI);
		tabellati.put("ProvinciaNascita", InterceptorEncodedData.LISTA_PROVINCE);
		tabellati.put("TipologiaAlboProf", InterceptorEncodedData.LISTA_TIPI_ALBO_PROFESSIONALE);
		tabellati.put("ProvinciaIscrizioneAlboProf", InterceptorEncodedData.LISTA_PROVINCE);
		tabellati.put("TipologiaCassaPrevidenza", InterceptorEncodedData.LISTA_TIPOLOGIE_ASSISTENZA);
		tabellati.put("Esistente", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("SoggettoQualifica", InterceptorEncodedData.LISTA_SOGGETTI_TIPI_QUALIFICA);
		tabellati.put("Qualifica", InterceptorEncodedData.LISTA_SOGGETTI_TIPI_QUALIFICA);
		tabellati.put("TipoSoggetto", InterceptorEncodedData.LISTA_SOGGETTI_TIPI_SOGGETTO);
		tabellati.put("ResponsabileDichiarazioni", InterceptorEncodedData.LISTA_SINO);
		// NB: in caso di omonimia tra campi di sezioni diverse, si esplicita il tipo di tabellato associato al campo della sezione !!!
		//     (i.e. solo "Titolo" delle sezioni "Soggetto", "AltriDatiAnagrafici" e "LiberoProfessionista" possono condividere
		//      lo stesso tabellato LISTA_TIPI_TITOLO_TECNICO !!!)
		tabellati.put("Soggetto.Titolo", InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO);	
		tabellati.put(TAG_ALTRIDATIANAGRAFICI + ".Titolo", InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO); 
		tabellati.put(TAG_LIBEROPROFESSIONISTA + ".Titolo", InterceptorEncodedData.LISTA_TIPI_TITOLO_TECNICO);
		tabellati.put("Sesso", InterceptorEncodedData.LISTA_SESSI);
		tabellati.put("SolaLettura", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("IscrittoCCIAA", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("ProvinciaIscrizioneCCIAA", InterceptorEncodedData.LISTA_PROVINCE);
		tabellati.put("SoggettoNormativeDURC", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("SettoreProduttivoDURC", InterceptorEncodedData.LISTA_SETTORI_PRODUTTIVI);
		tabellati.put("OrganismoCertificatoreSOA", InterceptorEncodedData.LISTA_CERTIFICATORI_SOA);
		tabellati.put("OrganismoCertificatoreISO", InterceptorEncodedData.LISTA_CERTIFICATORI_ISO);
		tabellati.put("IscrittoWhitelistAntimafia", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("IscrittoAnagrafeAntimafiaEsecutori", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("RinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("IscrittoElencoSpecialeProfessionisti", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("IscrittoAnagrafeAntimafiaEsecutori", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("PossiedeRatingLegalita", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("RatingLegalita", InterceptorEncodedData.LISTA_RATING_LEGALE);
		tabellati.put("AggiornamentoRatingLegalita", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("SocioUnico", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("RegimeFiscale", InterceptorEncodedData.LISTA_TIPI_REGIME_FISCALE);
		tabellati.put("AggiornamentoWhitelistAntimafia", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("SettoreAttivitaEconomica", InterceptorEncodedData.LISTA_SETTORE_ATTIVITA_ECONOMICA);
		tabellati.put("AssunzioniObbligate", InterceptorEncodedData.LISTA_SINO);
		tabellati.put("ClasseDimensioneDipendenti", InterceptorEncodedData.LISTA_CLASSI_DIMENSIONE);
		tabellati.put("TipoSocietaCooperativa", InterceptorEncodedData.LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE);	

		// attributi della classe da gestire in export/import 
		// vengono riportati nell'ordine visualizzato dal wizard dei dati impresa
		// e vengono scritti nello stesso ordine nel file xml
		this.xmlFields = new String[] {
			// DatiPrincipali
			TAG_DATIPRINCIPALI + ":RagioneSociale",
			TAG_DATIPRINCIPALI + ":TipoImpresa",
			TAG_DATIPRINCIPALI + ":NaturaGiuridica",
			TAG_DATIPRINCIPALI + ":VatGroup",
			TAG_DATIPRINCIPALI + ":CodiceFiscale",
			TAG_DATIPRINCIPALI + ":PartitaIVA", 
			TAG_DATIPRINCIPALI + ":IndirizzoSedeLegale",
			TAG_DATIPRINCIPALI + ":NumCivicoSedeLegale",
			TAG_DATIPRINCIPALI + ":CapSedeLegale",
			TAG_DATIPRINCIPALI + ":ComuneSedeLegale",
			TAG_DATIPRINCIPALI + ":ProvinciaSedeLegale",
			TAG_DATIPRINCIPALI + ":NazioneSedeLegale", 
			TAG_DATIPRINCIPALI + ":SitoWeb",
			TAG_DATIPRINCIPALI + ":TelefonoRecapito",
			TAG_DATIPRINCIPALI + ":FaxRecapito",
			TAG_DATIPRINCIPALI + ":CellulareRecapito",
			TAG_DATIPRINCIPALI + ":EmailRecapito", 
			TAG_DATIPRINCIPALI + ":EmailRecapitoConferma",
			TAG_DATIPRINCIPALI + ":EmailPECRecapito",
			TAG_DATIPRINCIPALI + ":EmailPECRecapitoConferma",
			TAG_DATIPRINCIPALI + ":TipoSocietaCooperativa",
				// Altri indirizzi [...]
	    	TAG_ALTRIINDIRIZZI + ":TipoIndirizzo",
	    	TAG_ALTRIINDIRIZZI + ":Indirizzo",
	    	TAG_ALTRIINDIRIZZI + ":NumCivico",
	    	TAG_ALTRIINDIRIZZI + ":Cap",
	    	TAG_ALTRIINDIRIZZI + ":Comune",
	    	TAG_ALTRIINDIRIZZI + ":Provincia",
	    	TAG_ALTRIINDIRIZZI + ":Nazione",
	    	TAG_ALTRIINDIRIZZI + ":Telefono",
	    	TAG_ALTRIINDIRIZZI + ":Fax",
	    	// Altri dati anagrafici [...]
	    	TAG_ALTRIDATIANAGRAFICI + ":SoggettoQualifica",			// questo e' il campo da esportare/importare con il valore della "Qualifica"
	    	TAG_ALTRIDATIANAGRAFICI + ":Qualifica", 				// questo viene incluso ma non contiene il valore effettivo
	    	TAG_ALTRIDATIANAGRAFICI + ":DataInizioIncarico",
	    	TAG_ALTRIDATIANAGRAFICI + ":DataFineIncarico",
	    	TAG_ALTRIDATIANAGRAFICI + ":ResponsabileDichiarazioni",
	    	TAG_ALTRIDATIANAGRAFICI + ":Cognome",
	    	TAG_ALTRIDATIANAGRAFICI + ":Nome",
	    	TAG_ALTRIDATIANAGRAFICI + ":Titolo",
	    	TAG_ALTRIDATIANAGRAFICI + ":DataNascita",
	    	TAG_ALTRIDATIANAGRAFICI + ":ComuneNascita",
	    	TAG_ALTRIDATIANAGRAFICI + ":ProvinciaNascita",
	    	TAG_ALTRIDATIANAGRAFICI + ":Sesso",
	    	TAG_ALTRIDATIANAGRAFICI + ":CodiceFiscale",
	    	TAG_ALTRIDATIANAGRAFICI + ":Indirizzo",
	    	TAG_ALTRIDATIANAGRAFICI + ":NumCivico",
	    	TAG_ALTRIDATIANAGRAFICI + ":Cap",
	    	TAG_ALTRIDATIANAGRAFICI + ":Comune",
	    	TAG_ALTRIDATIANAGRAFICI + ":Provincia",
	    	TAG_ALTRIDATIANAGRAFICI + ":Nazione",
	    	TAG_ALTRIDATIANAGRAFICI + ":TipologiaAlboProf",
	    	TAG_ALTRIDATIANAGRAFICI + ":NumIscrizioneAlboProf",
	    	TAG_ALTRIDATIANAGRAFICI + ":DataIscrizioneAlboProf",
	    	TAG_ALTRIDATIANAGRAFICI + ":ProvinciaIscrizioneAlboProf",
	    	TAG_ALTRIDATIANAGRAFICI + ":TipologiaCassaPrevidenza",
	    	TAG_ALTRIDATIANAGRAFICI + ":NumMatricolaCassaPrevidenza",
	    	TAG_ALTRIDATIANAGRAFICI + ":Note",
	    	// Altri dati anagrafici-Libero Professionista 
	    	// La sezione "LiberoProfessionista" serve per separare gli AltriDatiAngrafici
	    	// in caso di libero professionista ma la sezione esportata e' sempre "AltriDatiAngrafici"
	    	TAG_LIBEROPROFESSIONISTA + ":Titolo",
	    	TAG_LIBEROPROFESSIONISTA + ":Cognome",
	    	TAG_LIBEROPROFESSIONISTA + ":Nome",
	    	TAG_LIBEROPROFESSIONISTA + ":DataNascita",
	    	TAG_LIBEROPROFESSIONISTA + ":ComuneNascita",
	    	TAG_LIBEROPROFESSIONISTA + ":Sesso",
	    	TAG_LIBEROPROFESSIONISTA + ":ProvinciaNascita",
	    	TAG_LIBEROPROFESSIONISTA + ":TipologiaAlboProf",
	    	TAG_LIBEROPROFESSIONISTA + ":NumIscrizioneAlboProf",
	    	TAG_LIBEROPROFESSIONISTA + ":DataIscrizioneAlboProf",
	    	TAG_LIBEROPROFESSIONISTA + ":ProvinciaIscrizioneAlboProf",
	    	TAG_LIBEROPROFESSIONISTA + ":NumMatricolaCassaPrevidenza",
	    	TAG_LIBEROPROFESSIONISTA + ":TipologiaCassaPrevidenza",
	    	// Dati ulteriori
	    	TAG_DATIULTERIORI + ":IscrittoCCIAA",
	    	TAG_DATIULTERIORI + ":NumIscrizioneCCIAA",
			TAG_DATIULTERIORI + ":DataIscrizioneCCIAA",
			TAG_DATIULTERIORI + ":NumRegistroDitteCCIAA",
			TAG_DATIULTERIORI + ":DataDomandaIscrizioneCCIAA",
			TAG_DATIULTERIORI + ":ProvinciaIscrizioneCCIAA",
			TAG_DATIULTERIORI + ":SoggettoNormativeDURC",
			TAG_DATIULTERIORI + ":SettoreProduttivoDURC",
			TAG_DATIULTERIORI + ":NumIscrizioneINPS",
			TAG_DATIULTERIORI + ":PosizContributivaIndividualeINPS",
			TAG_DATIULTERIORI + ":DataIscrizioneINPS",
			TAG_DATIULTERIORI + ":LocalitaIscrizioneINPS",
			TAG_DATIULTERIORI + ":CodiceCNEL",
			TAG_DATIULTERIORI + ":NumIscrizioneINAIL",
			TAG_DATIULTERIORI + ":PosizAssicurativaINAIL",
			TAG_DATIULTERIORI + ":DataIscrizioneINAIL",
			TAG_DATIULTERIORI + ":LocalitaIscrizioneINAIL",
			TAG_DATIULTERIORI + ":NumIscrizioneCassaEdile",
			TAG_DATIULTERIORI + ":OggettoSociale",
			TAG_DATIULTERIORI + ":CodiceCassaEdile",
			TAG_DATIULTERIORI + ":DataIscrizioneCassaEdile",
			TAG_DATIULTERIORI + ":LocalitaIscrizioneCassaEdile",
			TAG_DATIULTERIORI + ":AltriIstitutiPrevidenziali",
			TAG_DATIULTERIORI + ":NumIscrizioneSOA",
			TAG_DATIULTERIORI + ":DataIscrizioneSOA",
			TAG_DATIULTERIORI + ":DataScadenzaQuinquennaleSOA",
			TAG_DATIULTERIORI + ":DataVerificaTriennaleSOA",
			TAG_DATIULTERIORI + ":DataScadenzaTriennaleSOA",
			TAG_DATIULTERIORI + ":DataScadenzaIntermediaSOA",
			TAG_DATIULTERIORI + ":OrganismoCertificatoreSOA",
			TAG_DATIULTERIORI + ":NumIscrizioneISO",
			TAG_DATIULTERIORI + ":DataScadenzaISO",
			TAG_DATIULTERIORI + ":OrganismoCertificatoreISO",
			TAG_DATIULTERIORI + ":IscrittoWhitelistAntimafia",
			TAG_DATIULTERIORI + ":SedePrefetturaWhitelistAntimafia",
			TAG_DATIULTERIORI + ":SezioniIscrizioneWhitelistAntimafia",
			TAG_DATIULTERIORI + ":DataIscrizioneWhitelistAntimafia",
			TAG_DATIULTERIORI + ":DataScadenzaIscrizioneWhitelistAntimafia",
			TAG_DATIULTERIORI + ":AggiornamentoWhitelistAntimafia",
			TAG_DATIULTERIORI + ":IscrittoAnagrafeAntimafiaEsecutori",
			TAG_DATIULTERIORI + ":DataScadenzaIscrizioneAnagrafeAntimafiaEsecutori",
			TAG_DATIULTERIORI + ":RinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori",
			TAG_DATIULTERIORI + ":IscrittoElencoSpecialeProfessionisti",
			TAG_DATIULTERIORI + ":PossiedeRatingLegalita", 
			TAG_DATIULTERIORI + ":RatingLegalita",
			TAG_DATIULTERIORI + ":DataScadenzaPossessoRatingLegalita",
			TAG_DATIULTERIORI + ":AggiornamentoRatingLegalita",
			TAG_DATIULTERIORI + ":AltreCertificazioniAttestazioni",
			TAG_DATIULTERIORI + ":CodiceIBANCCDedicato",
			TAG_DATIULTERIORI + ":CodiceBICCCDedicato",
			TAG_DATIULTERIORI + ":SoggettiAbilitatiCCDedicato",
			TAG_DATIULTERIORI + ":SocioUnico",
			TAG_DATIULTERIORI + ":RegimeFiscale",
			TAG_DATIULTERIORI + ":SettoreAttivitaEconomica",
			TAG_DATIULTERIORI + ":ZoneAttivita",
			TAG_DATIULTERIORI + ":AssunzioniObbligate",
			TAG_DATIULTERIORI + ":Anni",
			TAG_DATIULTERIORI + ":NumDipendenti",
			TAG_DATIULTERIORI + ":ClasseDimensioneDipendenti",
			TAG_DATIULTERIORI + ":UlterioriDichiarazioni"
//			// generali
//			"DittaIndividuale",			???
//			"LiberoProfessionista",		???
//			"Consorzio"					???
		};
	}	

	/**
	 * recupera il tabellato associato al campo 
	 * @throws Exception 
	 */
	private LinkedHashMap<String, String> getTabellato(String field) 
		throws Exception 
	{
		LinkedHashMap<String, String> map = null;
		String table = this.tabellati.get(field);
		if(StringUtils.isNotEmpty(table)) {
			map = this.encodedData.get(table);
		}
		return map;
	}
	
	/**
	 * scrivi un tag in un documento xml 
	 */
	private Element writeXml(Element parent, String field, Object value) {
		Element e = this.document.createElement(field);
		parent.appendChild(e);
		
		String val = "";
		if(value != null) {
			if(value instanceof Boolean) {
				val = Boolean.toString((Boolean)value); 
			} else if(value instanceof Integer) {
				val = Integer.toString((Integer)value);
			} else if(value instanceof Long) {
				val = Long.toString((Long)value);
			} else if(value.getClass().isArray()) {
				for(int i = 0; i < Array.getLength(value); i++) {
					Object v = Array.get(value, i);
					val = val + (v == null ? "" : v) + SEPARATOR;
				}
			} else {
				val = (String)value;
			}
		}
	
		// da codice a descrizione...
		if(StringUtils.isNotEmpty(val)) {
			try {
				LinkedHashMap<String, String> map = getTabellato(field);
				if(map == null) {
					// cerca il tabellato come "Parent.Field" (i.e. "Soggetto.Titolo", "AltriDatiAnagrafici.Titolo", ...)
					// vedi sopra, la definizione dei tabellati per campo
					map = getTabellato(parent.getNodeName() + "." + field);
				}
				if(map != null) {
					String v = map.get(val);
					if(val != null && v == null) {
						LOG.debug("writeXml {} valore non decodificato per '{}'=>???", 
								parent.getNodeName() + "." + field, val);
					}
					val = (v != null ? v : "");
				}
			} catch(Exception ex) {
				val = "";
			}
		}
		e.setTextContent(val);
		
		this.crcBuffer = this.crcBuffer + val + ";";
		LOG.debug("writeXml " + field + "=" + val);
		return e;
	}
	
	/**
	 * leggi da xml il valore di un tag 
	 */
	private String readXml(Element parent, String field, boolean evalCrc) {
		NodeList nodes = parent.getElementsByTagName(field);
		
		String value = "";
		if(nodes != null && nodes.getLength() > 0) {
			value = nodes.item(0).getTextContent();
		} else {
			if(evalCrc) {
				LOG.warn("Importazione dati operatore economico: campo '" + field + "' non trovato");
			}
		}
		
		String newValue = value;
		
		// da descrizione a codice...
		if(StringUtils.isNotEmpty(value)) {
			try {
				LinkedHashMap<String, String> map = getTabellato(field);
				if(map == null) {
					// prova a cercare come "Parent:Field" (i.e. "Soggetto.Titolo")
					map = getTabellato(parent.getNodeName() + "." + field);
				}
				if(map != null) {
					newValue = "";
					for( Map.Entry<String, String> item : map.entrySet() ) {
						if(value.equalsIgnoreCase( item.getValue() )) {
							newValue = item.getKey();
							break;
						}
					}
					if(StringUtils.isEmpty(newValue)) {
						this.readXmlErrors.add(field + "|" + value);
					}
				}
			} catch(Exception ex) {
				newValue = "";
			}
		}
		
		if(evalCrc) {
			this.crcBuffer = this.crcBuffer + value + ";";
			LOG.debug("readXml " + field + "=" + value);
		}
		
		return newValue;
	}
	
	/**
	 * leggi da xml il valore di un tag e calcola il CRC 
	 */
	private String readXml(Element parent, String field) {
		return readXml(parent, field, true);
	}

	/**
	 * restituisce il valore di una proprietï¿½ dell'oggetto "source"
	 */
	private Object getPropertyValue(Object source, String field) {
		Object value = null;
		for (Method method : source.getClass().getDeclaredMethods()) {
			// cerca i getter...
		    if (Modifier.isPublic(method.getModifiers())
		        && method.getParameterTypes().length == 0 
		        && method.getReturnType() != void.class
		        && (method.getName().startsWith("get") || method.getName().startsWith("is"))) {
		    	
		    	String attr = (method.getName().startsWith("get") 
		    		           ? method.getName().substring(3) 		//getNome
		    		           : method.getName().substring(2));	//isNome
		    	
		    	if(field.equalsIgnoreCase(attr)) {
					try {
						value = method.invoke(source);
					} catch (Exception ex) {
						value = null;
					}
		    		break;
		    	}
		    }
		}
		return value;
	}
	
	/**
	 * inizializza gli attributi di un oggetto con i valori presenti in un documento xml
	 * e restituisce gli eventuali errori per i campi tabellati non decodificati 
	 */
	private boolean readFields(Element source, Object target, String section) {
		this.readXmlErrors = new ArrayList<String>();
		for(int j = 0; j < this.xmlFields.length; j++) {
			
			// trova la sezione XML...
			String sec = getXmlSection(this.xmlFields[j]);
			String fld = getXmlField(this.xmlFields[j]);
    		
    		if(sec.equalsIgnoreCase(section)) {
				for (Method method : target.getClass().getDeclaredMethods()) {
				    if (Modifier.isPublic(method.getModifiers())
				        && method.getParameterTypes().length == 1 
				        && method.getReturnType() == void.class
				        && (method.getName().startsWith("set"))) {
				    	try {
				    		// recupera il setter dell'attributo dell'oggetto...
				    		String attr = method.getName().substring(3);
		    				if(attr.equalsIgnoreCase(fld)) {
		    					// leggi il valore del campo...
		    					String value = readXml(source, attr);
					    		
					    		// recupera il tipo dell'attributo...
					    		Field field = target.getClass().getDeclaredField(
					    				StringUtils.lowerCase(attr.substring(0,1)) + attr.substring(1));
					    		
						    	if(field.getType().isArray()) {
						    		// imposta il valore dell'attributo dell'oggetto...
						    		// ...converti la stringa in un array  
						    		// ed imposta il valore dell'attributo dell'oggetto...
						    		String[] s = value.split("\\"+SEPARATOR, -1);
						    		Class itemCls = field.getType().getComponentType();
						    		
						    		// NON funziona, va in eccezione sul tipo... 
						    		//Object[] v = new Object[s.length];
						    		//for(int i = 0; i < s.length; i++) v[i] = s[i];
					    			//method.invoke(target, new Object[] {v});
						    		
						    		//*************************************************************
						    		// WORKAROUND: 
						    		// esiste un metodo che NON richiede la dichiarazione 
						    		// esplicita del tipo (reflection) ???
						    		if(itemCls == Integer.class) {
						    			Integer[] v = new Integer[s.length];
						    			for(int i = 0; i < s.length; i++) v[i] = (int)toLong(s[i]);
						    			method.invoke(target, new Object[] {v});
					    			} else if(itemCls == int.class) {
					    				int[] v = new int[s.length];
						    			for(int i = 0; i < s.length; i++) v[i] = (int)toLong(s[i]);
						    			method.invoke(target, new Object[] {v});
					    			} else if(itemCls == Long.class) {
					    				Long[] v = new Long[s.length];
						    			for(int i = 0; i < s.length; i++) v[i] = toLong(s[i]);
						    			method.invoke(target, new Object[] {v});
					    			} else if(itemCls == long.class) {
					    				long[] v = new long[s.length];
						    			for(int i = 0; i < s.length; i++) v[i] = toLong(s[i]);
						    			method.invoke(target, new Object[] {v});
					    			} else if(itemCls == Boolean.class) {
					    				Boolean[] v = new Boolean[s.length];
						    			for(int i = 0; i < s.length; i++) v[i] = toBool(s[i]);
						    			method.invoke(target, new Object[] {v});
					    			} else if(itemCls == boolean.class) {
					    				boolean[] v = new boolean[s.length];
						    			for(int i = 0; i < s.length; i++) v[i] = toBool(s[i]);
						    			method.invoke(target, new Object[] {v});
					    			} else {
						    			method.invoke(target, new Object[] {s});
					    			}
						    		//*************************************************************
						    	} else {
						    		// imposta il valore dell'attributo dell'oggetto...
						    		method.invoke(target, value);
						    	}
				    		}
						} catch (Exception ex) {
							ApsSystemUtils.logThrowable(ex, this, "readFields()");
						}	
					}
				}
			}
		}
		
		// traccia eventuali errori nella decodifica dei campi tabellati
		boolean ok = true;
		if(this.readXmlErrors != null && this.readXmlErrors.size() > 0) {
			ok = false;
			for(int i = 0; i < this.readXmlErrors.size(); i++) {
				String[] t = this.readXmlErrors.get(i).split("\\|");
				addFieldWarning(t[0], target, t[1]);
			}
		}
		
		return ok;
	}
	
	private Boolean toBool(String value) {
		try {
			return Boolean.parseBoolean(value);
		} catch(Exception ex) {
			return null;
		}
	}
	
	private long toLong(String value) {
		try {
			return Long.parseLong(value);
		} catch(Exception ex) {
			return 0;
		}
	}
	
	private String getXmlSection(String xmlField) {
		String[] v = xmlField.split(":");
		return (v != null && v.length > 1 ? v[0] : "");
	}
	
	private String getXmlField(String xmlField) {
		String[] v = xmlField.split(":");
		return (v != null && v.length > 1 ? v[1] : xmlField);
	}
	
	/**
	 * Emporta i dati dell'impresa su file to xml  
	 * @throws ParserConfigurationException 
	 * @throws Exception 
	 */
	public boolean exportToXml(WizardRegistrazioneImpresaHelper impresa, OutputStream xmlos, String xmlFileName) 
		throws Exception 
	{
		boolean exportDone = false;
		try {
			CRC32 crc = new CRC32(); 
			this.crcBuffer = "";
			
			initTabellati();
				        
			// costruisci l'xml
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			this.document = docBuilder.newDocument();
			
			// root elements
			Element root = this.document.createElement(TAG_IMPRESA);
			this.document.appendChild(root);
			
			// aggiungi il campo versione... 
			Element e = this.document.createElement(TAG_VERSION);
			root.appendChild(e);
			e.setTextContent(XML_VERSION);
			
			// aggiungi il campo CRC...
			Element eCrc = this.document.createElement(TAG_CRC);
			root.appendChild(eCrc);
			eCrc.setTextContent("");
			
			// esporta solo i campi selezionati nell'ordine stabilito...
			int xmlIndex = 0;
			int xmlSectionStart = -1;
			String lastSection = "";
			while (xmlIndex < this.xmlFields.length) { 
				
				String section = getXmlSection(this.xmlFields[xmlIndex]);
				String field = getXmlField(this.xmlFields[xmlIndex]);
	    		
	    		// crea la sezione XML...
				// (nel caso particolare del "LiberoProfessionista" si crea nell'xml la sezione "AltriDatiAnagrafici") 
				NodeList nodes = (TAG_LIBEROPROFESSIONISTA.equalsIgnoreCase(section)
						? this.document.getElementsByTagName(TAG_ALTRIDATIANAGRAFICI)
						: this.document.getElementsByTagName(section)
				);
				e = (Element) (nodes != null && nodes.getLength() > 0 ? nodes.item(0) : null);
				if(e == null) {
					e = this.document.createElement(section);
					root.appendChild(e);
				}
				
	    		if(e != null) {
		    		if(TAG_DATIPRINCIPALI.equalsIgnoreCase(section)) {
		    			// Dati principali
		    			if(impresa.getDatiPrincipaliImpresa() != null) {
			    			writeXml(e, field, getPropertyValue(impresa.getDatiPrincipaliImpresa(), field));
		    			}
		    			
		    		} else if(TAG_ALTRIINDIRIZZI.equalsIgnoreCase(section)) {
		    			// Altri indirizzi
		    			if(impresa.getIndirizziImpresa() != null) {
			    			// la prima volta che arrivo in questa sezione
			    			// memorizza l'inizio...
			    			if(!section.equalsIgnoreCase(lastSection)) {
			    				xmlSectionStart = xmlIndex;
			    			}
			    			
			    			// esporta tutti gli elementi della lista...
			    			for(int i = 0; i < impresa.getIndirizziImpresa().size(); i++) {
			    				// crea il nuovo elemento della lista...
			    				Element item = this.document.createElement(TAG_INDIRIZZO);
			    				item.setAttribute("Id", Integer.toString(i + 1));
								e.appendChild(item);
								
								// scrivi nell'xml tutti i valori dell'elemento
								String sec = section;
				    			xmlIndex = xmlSectionStart;
				    			while (xmlIndex < this.xmlFields.length && sec.equalsIgnoreCase(section)) {
				    				sec = getXmlSection(this.xmlFields[xmlIndex]);
				    				if(sec.equalsIgnoreCase(section)) {
				    					field = getXmlField(this.xmlFields[xmlIndex]);
				    					writeXml(item, field, getPropertyValue(impresa.getIndirizziImpresa().get(i), field));
				    					xmlIndex++;
				    				}
				    			}
				    			xmlIndex--;
			    			}
		    			}
		    			
		    		} else if(TAG_LIBEROPROFESSIONISTA.equalsIgnoreCase(section)) {
		    			// Altri dati
		    			
		    			if(impresa.isLiberoProfessionista()) {
							// LIBERO PROFESSIONISTA
		    				if(impresa.getAltriDatiAnagraficiImpresa() != null) {
		    					writeXml(e, field, getPropertyValue(impresa.getAltriDatiAnagraficiImpresa(), field));
							}
		    			}
		    			
		    		} else if(TAG_ALTRIDATIANAGRAFICI.equalsIgnoreCase(section)) {
		    			// Altri dati
		    			if(!impresa.isLiberoProfessionista()) {
		    				// NON LIBERO PROFESSIONISTA
	    				
		    				// la prima volta che arrivo in questa sezione
			    			// memorizza l'inizio...
			    			if(!section.equalsIgnoreCase(lastSection)) {
			    				xmlSectionStart = xmlIndex;
			    			}
			    			
							for(int t = 1; t <= 4; t++) {
								// recupera la lista di soggetti da elaborare...
								Element elist = null; 
								List<ISoggettoImpresa> list = null;
								if(t == 1) {
									elist = this.document.createElement(TAG_LISTA_LEGALIRAPPRESENTANTI);
									list = impresa.getLegaliRappresentantiImpresa();
								} else if(t == 2) {
									elist = this.document.createElement(TAG_LISTA_DIRETTORITECNICI);
									list = impresa.getDirettoriTecniciImpresa();
								} else if(t == 3) {
									elist = this.document.createElement(TAG_LISTA_ALTRECARICHE);
									list = impresa.getAltreCaricheImpresa();
								} else if(t == 4) {
									elist = this.document.createElement(TAG_LISTA_COLLABORATORI);
									list = impresa.getCollaboratoriImpresa();
								}
								
								if(list != null) {
									e.appendChild(elist);
									
					    			// esporta tutti gli elementi della lista...
					    			for(int i = 0; i < list.size(); i++) {
					    				// crea il nuovo elemento della lista...
					    				Element item = this.document.createElement(TAG_SOGGETTO);
					    				item.setAttribute("Id", Integer.toString(i + 1));
										elist.appendChild(item);
										
										// scrivi nell'xml tutti i valori dell'elemento
										String sec = section;
						    			xmlIndex = xmlSectionStart;
										while (xmlIndex < this.xmlFields.length && sec.equalsIgnoreCase(section)) {
						    				sec = getXmlSection(this.xmlFields[xmlIndex]);
						    				if(sec.equalsIgnoreCase(section)) { 
							    				field = getXmlField(this.xmlFields[xmlIndex]);
							    				writeXml(item, field, getPropertyValue(list.get(i), field));
							    				xmlIndex++;
						    				}
						    			}
										xmlIndex--;
					    			}
								}
							}
						}	
		    			
		    		} else if(TAG_DATIULTERIORI.equalsIgnoreCase(section)) {
		    			// Dati ulteriori
		    			if(impresa.getDatiUlterioriImpresa() != null) {
		    				writeXml(e, field, getPropertyValue(impresa.getDatiUlterioriImpresa(), field));
		    			}
		    		}
	    		}
	    		
	    		//...
	    		lastSection = section; 
	    		xmlIndex++;
			}
	    	
			// generali
			writeXml(root, "DittaIndividuale", impresa.isDittaIndividuale());
			writeXml(root, "LiberoProfessionista", impresa.isLiberoProfessionista());
			writeXml(root, "Consorzio", impresa.isConsorzio());
			//...
			
			// aggiorna il campo CRC...
			crc.update(crcBuffer.getBytes());
			eCrc.setTextContent(Long.toString(crc.getValue()));
			
			// scrivi il file xml...
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult stream = new StreamResult(xmlos);
			transformer.transform(source, stream);
			exportDone = true;
			
		} catch (Exception ex) {
			LOG.error("exportToXml() " + ex.getMessage() + " (filename=" + xmlFileName + ")");
			throw ex;
		} 
		
		return exportDone;
	}

	/**
	 * Import dati impresa da XML (esportato da altro PortaleAppalti)  
	 * @throws ImportExportException 
	 * @throws WrongCRCException 
	 */
	public boolean importFromXml(File xmlFile, String xmlFileName, WizardRegistrazioneImpresaHelper impresa, boolean isSpidBusiness)
		throws WrongCRCException, Exception 
	{
		boolean result = false;
		try {
			CRC32 crc = new CRC32();
			this.crcBuffer = "";
				        
			initTabellati();
			
			// apri il file XML...
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			// DTD External Entity Arbitrary read file (XXE attack)
			docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			this.document = docBuilder.parse(xmlFile);
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			this.document.getDocumentElement().normalize();
			
			// get root element
			NodeList nodes = null;
			Element root = null; 
			try {
				nodes = this.document.getElementsByTagName(TAG_IMPRESA);
				root = (Element) (nodes != null && nodes.getLength() > 0 ? nodes.item(0) : null);
			} catch (Exception e) {
				throw new Exception(getText("Errors.importXml.tagNotFound", new String[] {TAG_IMPRESA}));
			}
			
			// verifica la versione...
			long version = toLong(readXml(root, TAG_VERSION, false));
			if(version <= 0) {
				// file di import non valido
				throw new Exception(getText("Errors.importXml.xmlNotValid", new String[] {xmlFileName}));
			} else if(version > Long.parseLong(XML_VERSION)) {
				// versione file non gestita
				throw new Exception(getText("Errors.importXml.xmlWrongVersion", new String[] {xmlFileName}));
			}
			
			// leggi il crc del file xml...
			long ecrc = toLong(readXml(root, TAG_CRC, false));
			
			// inizializza il crcBuffer, ora!!! 
			this.crcBuffer = "";
			
			boolean dittaIndividuale = Boolean.parseBoolean( readXml(root, "DittaIndividuale", false) );
			boolean liberoProfessionista = Boolean.parseBoolean( readXml(root, "LiberoProfessionista", false) );
			boolean consorzio = Boolean.parseBoolean( readXml(root, "Consorzio", false) );

			// importa i dati impresa...
			nodes = root.getElementsByTagName(TAG_DATIPRINCIPALI);
			Element e = (Element)(nodes != null && nodes.getLength() > 0 ? nodes.item(0) : null);
			if(e != null) {
				WizardDatiPrincipaliImpresaHelper datiPrincipaliImpresa = new WizardDatiPrincipaliImpresaHelper();
				readFields(e, datiPrincipaliImpresa, TAG_DATIPRINCIPALI);
				if (isSpidBusiness)
					checkImportedData(impresa, datiPrincipaliImpresa);
				impresa.setDatiPrincipaliImpresa(datiPrincipaliImpresa);
			}
			
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
			.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
					 ServletActionContext.getRequest());
			try {
				if(customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP")){
					nodes = root.getElementsByTagName(TAG_ALTRIINDIRIZZI);
					e = (Element) (nodes != null ? nodes.item(0) : null);
					if(e != null) {
						ArrayList<IIndirizzoImpresa> indirizziImpresa = new ArrayList<IIndirizzoImpresa>();
						
						nodes = e.getChildNodes();
						for(int i = 0; i < nodes.getLength(); i++) {
							e = (Element) nodes.item(i);
							IndirizzoImpresaHelper indirizzo = new IndirizzoImpresaHelper();
							if( readFields(e, indirizzo, TAG_ALTRIINDIRIZZI) ) {
								indirizziImpresa.add(indirizzo);
							}
						}
						
						impresa.setIndirizziImpresa(indirizziImpresa);
					}
				}
			} catch (Exception ex) {
				LOG.error(ex.getMessage());
			}
			
			nodes = root.getElementsByTagName(TAG_ALTRIDATIANAGRAFICI);
			Element p = (Element)(nodes != null ? nodes.item(0) : null);
			if(p != null) {
				if(liberoProfessionista) {
					// LIBERO PROFESSIONISTA
					if(impresa.getAltriDatiAnagraficiImpresa() != null) {
						readFields(p, impresa.getAltriDatiAnagraficiImpresa(), TAG_LIBEROPROFESSIONISTA);
					}
				} else {
					ArrayList<ISoggettoImpresa> legaliRappresentantiImpresa = null; 
					ArrayList<ISoggettoImpresa> direttoriTecniciImpresa = null; 
					ArrayList<ISoggettoImpresa> altreCaricheImpresa = null;
					ArrayList<ISoggettoImpresa> collaboratoriImpresa = null;
					
					for(int t = 1; t <= 4; t++) {
						ArrayList<ISoggettoImpresa> lista = null;
						if(t == 1) {
							legaliRappresentantiImpresa = new ArrayList<ISoggettoImpresa>();
							lista = legaliRappresentantiImpresa;
							nodes = p.getElementsByTagName(TAG_LISTA_LEGALIRAPPRESENTANTI);
						} else if(t == 2) {
							direttoriTecniciImpresa = new ArrayList<ISoggettoImpresa>();
							lista = direttoriTecniciImpresa; 
							nodes = p.getElementsByTagName(TAG_LISTA_DIRETTORITECNICI);
						} else if(t == 3) {
							altreCaricheImpresa = new ArrayList<ISoggettoImpresa>();
							lista = altreCaricheImpresa; 
							nodes = p.getElementsByTagName(TAG_LISTA_ALTRECARICHE);
						} else if(t == 4) {
							collaboratoriImpresa = new ArrayList<ISoggettoImpresa>();
							lista = collaboratoriImpresa; 
							nodes = p.getElementsByTagName(TAG_LISTA_COLLABORATORI);
						}	
						
						e = (Element)(nodes != null ? nodes.item(0) : null);
						if(lista != null && e != null) {
							nodes = e.getChildNodes();
							for(int i = 0; i < nodes.getLength(); i++) {
								Node node = nodes.item(i);
								if (node.getNodeType() == Node.ELEMENT_NODE) {
									e = (Element) nodes.item(i);

									SoggettoImpresaHelper soggetto = new SoggettoImpresaHelper();
									if (readFields(e, soggetto, TAG_ALTRIDATIANAGRAFICI)) {
										// correggi il tipoSoggetto in base alla lista di appartenenza
										if(StringUtils.isEmpty(soggetto.getTipoSoggetto())) {
											soggetto.setTipoSoggetto(t + "");
										}
										lista.add(soggetto);
									}
								}
							}
						}
					}
					
					impresa.setLegaliRappresentantiImpresa(legaliRappresentantiImpresa);
					impresa.setDirettoriTecniciImpresa(direttoriTecniciImpresa);
					impresa.setAltreCaricheImpresa(altreCaricheImpresa);
					impresa.setCollaboratoriImpresa(collaboratoriImpresa);
				}
			}
			
			nodes = root.getElementsByTagName(TAG_DATIULTERIORI);
			e = (Element)(nodes != null ? nodes.item(0) : null);
			if(e != null) {
				WizardDatiUlterioriImpresaHelper datiUlterioriImpresa = new WizardDatiUlterioriImpresaHelper();
				readFields(e, datiUlterioriImpresa, TAG_DATIULTERIORI);
				impresa.setDatiUlterioriImpresa(datiUlterioriImpresa);
			}
			
			// importa prima i dati generali
			impresa.setDittaIndividuale(Boolean.parseBoolean( readXml(root, "DittaIndividuale") ));
			impresa.setLiberoProfessionista(Boolean.parseBoolean( readXml(root, "LiberoProfessionista") ));
			impresa.setConsorzio(Boolean.parseBoolean( readXml(root, "Consorzio") ));
			//...
			
			// verifica il crc del file di import...
			crc.update(crcBuffer.getBytes());
			if(crc.getValue() != ecrc) {
				throw new WrongCRCException(getText("Errors.importXml.crcNotValid", new String[] {xmlFileName}));
			}
			
			result = true;
			
		} catch (SAXException ex) {
			//Errors.importXml.importNotDefined=Importazione dati operatore economico non riuscita
			LOG.error(ex.getMessage());
			throw new Exception(getText("Errors.importXml.importNotDefined") + ":" +
								ex.getMessage() + " (filename=" + xmlFileName + ")");
		} catch (IOException ex) {
			LOG.error(ex.getMessage());
			throw new Exception(getText("Errors.importXml.importNotDefined") + ":" + 
								ex.getMessage() + " (filename=" + xmlFileName + ")");
		} catch (ParserConfigurationException ex) {
			LOG.error(ex.getMessage());
			throw new Exception(getText("Errors.importXml.importNotDefined") + ":" + 
								ex.getMessage() + " (filename=" + xmlFileName + ")");
		} catch (NullPointerException ex) {
			LOG.error(ex.getMessage());
			throw new Exception(getText("Errors.importXml.crcNotValid", new String[] {xmlFileName}));
		} catch (WrongCRCException ex) {
			result = true;
			LOG.warn(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
			throw ex;
		}		
		return result;
	}

	private void checkImportedData(WizardRegistrazioneImpresaHelper session, WizardDatiPrincipaliImpresaHelper imported) throws Exception {
		IDatiPrincipaliImpresa sessionMainData = session.getDatiPrincipaliImpresa();
		if (!StringUtils.equalsIgnoreCase(sessionMainData.getRagioneSociale(), imported.getRagioneSociale())
				|| !StringUtils.equalsIgnoreCase(sessionMainData.getCodiceFiscale(), imported.getCodiceFiscale())
				|| !StringUtils.equalsIgnoreCase(sessionMainData.getPartitaIVA(), imported.getPartitaIVA()))
			throw new Exception("I dati anagrafici dell'impresa devono corrispondere a quelli ritornati da SPID");
	}

	/**
	 * Import dati impresa da modello DGUE in formato XML  
	 * @throws Exception 
	 */
	public boolean importFromDGUE(File xmlFile, String xmlFileName, WizardRegistrazioneImpresaHelper impresa) 
		throws Exception {
		boolean result = false;

		try {
//			DgueOrganizationDeserializer.populateHelper(impresa, Files.readAllBytes(xmlFile.toPath()));
			DgueOrganizationDeserializer serializer = new DgueOrganizationDeserializer(Files.readAllBytes(xmlFile.toPath()));
			serializer.populateHelper(impresa);
			if (!serializer.isValidXML()) {
				throw new RuntimeException(getText("Errors.importXmlDgue.formatNotSupported"));
			} else
				result = true;
		} catch (Exception ex) {
			LOG.error("importFromDGUE() " + ex.getMessage() + " (filename=" + xmlFileName + ")");
			throw ex;
		}

		return result;
	}

	/**
	 * PORTAPPALT-1049 - Import dati impresa da servizio Michelangelo (SACE)  
	 * @throws Exception 
	 */
	public boolean importFromMichelangelo(String codiceFiscale, WizardRegistrazioneImpresaHelper impresa) 
		throws Exception 
	{
		boolean result = false;
		errorCode = 0;
		errorDescription = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IAppParamManager appParamManager = (IAppParamManager) ctx.getBean(CommonSystemConstants.APP_PARAM_MANAGER);
			
			String url = (String)appParamManager.getConfigurationValue(AppParamManager.MICHELANGELO_BASEURL);
			if(StringUtils.isNotEmpty(url)) {
				michelangelo_base_uri = UriBuilder.fromUri((String)appParamManager.getConfigurationValue(AppParamManager.MICHELANGELO_BASEURL))
						.build();
				
				impresa.getDatiPrincipaliImpresa().setCodiceFiscale(codiceFiscale);
				
				Integer codiceSoggetto = MichelangeloGetCodiceSoggetto(codiceFiscale);
				
				MichelangeloCompanyInfo company = MichelangeloGetDatiImpresa(codiceSoggetto);
				
				// mappa i dati nell'helper impresa
				if(company != null) {
					MichelangeloCompanyToDatiImpresa.companyToImpresaHelper(company, impresa);
				}
			} else {
				LOG.error("importFromMichelangelo() " + AppParamManager.MICHELANGELO_BASEURL + " è vuoto o invalido.");
			}
		} catch (Exception ex) {
			LOG.error("importFromMichelangelo() " + ex.getMessage());
			throw ex;
		}
		return result;
	}

	/**
	 * richiede il codiceSoggetto al servizio Michelangelo (SACE)
	 */
	private Integer MichelangeloGetCodiceSoggetto(String codiceFiscale) throws Exception {
		LOG.debug("MichelangeloGetCodiceSoggetto BEGIN");
		Integer codiceSoggetto = null;
		try {
			ClientConfig config = new ClientConfig();
	        Client client = ClientBuilder.newClient(config);
	        
	        // body content
	        String body = "{" 
	        	+ "\"codiceFonte\": " + "89" + "," 
	  			+ "\"codiceFiscale\": \"" + codiceFiscale + "\"" 
	        + "}";
	        
	        // POST https://wstest.sacesrv.it/services/soggettoInfo
	        Response response = client
		    		.target(michelangelo_base_uri).path("soggettoInfo")
		    		.request(MediaType.APPLICATION_JSON)
		    		.accept(MediaType.APPLICATION_JSON)
		    		.post(Entity.entity(body, MediaType.APPLICATION_JSON));
	        
	        // mappa la risposta JSON
			String json = response.readEntity(String.class);
			ObjectMapper om = new ObjectMapper();
			HashMap<String, Object> fields = (HashMap<String, Object>) om.readValue(json, HashMap.class);
			
			if(fields != null) {
				HashMap<String, Object> transactionResult = (HashMap<String, Object>)fields.get("transactionResult");			
				errorCode = (Integer)(transactionResult != null ? transactionResult.get("errorCode") : null);
				errorDescription = (String)(transactionResult != null ? transactionResult.get("errorDescription") : null);
				codiceSoggetto = (Integer)fields.get("codiceSoggetto");
				LOG.debug("MichelangeloGetCodiceSoggetto codiceSoggetto=" + codiceSoggetto + ", errorCode=" + errorCode + ", errorDescription=" + errorDescription);				
	        } 
			
			if(codiceSoggetto == null || (codiceSoggetto != null && codiceSoggetto.intValue() <= 0)) {
				errorCode = 1;
				errorDescription = "La piattaforma Michelangelo non ha reperito l'identificativo della tua impresa (" + codiceFiscale + ")";
				LOG.error("MichelangeloGetCodiceSoggetto() " + errorDescription);
	        }
			
		} catch (Exception ex) {
			errorCode = 1;
			errorDescription = "Piattaforma Michelangelo non raggiungibile, riprovare più tardi";
			LOG.error("MichelangeloGetCodiceSoggetto() " + ex.getMessage());
			//throw ex;
		}
		LOG.debug("MichelangeloGetCodiceSoggetto END");
		return codiceSoggetto;
	}
	
	/**
	 * richiede i dati dell'impresa al servizio Michelangelo (SACE)
	 */
	private MichelangeloCompanyInfo MichelangeloGetDatiImpresa(Integer codiceSoggetto) throws Exception {
		LOG.debug("MichelangeloGetDatiImpresa BEGIN");
		MichelangeloCompanyInfo company = null;
		if(codiceSoggetto != null && codiceSoggetto.longValue() != 0) {
			try {
				ClientConfig config = new ClientConfig();
		        Client client = ClientBuilder.newClient(config);
		        
		        // GET https://wstest.sacesrv.it/services/company/[codiceSoggetto]
		        Response response = client
			    		.target(michelangelo_base_uri).path("services").path("company").path(codiceSoggetto.toString())
			    		.request(MediaType.APPLICATION_JSON)
			    		.get();
		        
		        // mappa la risposta JSON
				String json = response.readEntity(String.class);
				ObjectMapper om = new ObjectMapper();
				company = (MichelangeloCompanyInfo) om.readValue(json, MichelangeloCompanyInfo.class);
				
				if(company == null) {
					errorCode = 1;
					errorDescription = "Piattaforma Michelangelo non raggiungibile, riprovare più tardi";
					LOG.error("MichelangeloGetDatiImpresa() " + errorDescription);
				} else {
					if(company.Header.IsError) {
						errorCode = company.Header.ReturnValue;
						errorDescription = "La piattaforma Michelangelo ha rilevato un errore in fase di reperimento dei dati: " + company.Header.Message;
						LOG.error("MichelangeloGetDatiImpresa() " + errorDescription);
					}
				}
			} catch (Exception ex) {
				errorCode = 1;
				errorDescription = "Piattaforma Michelangelo non raggiungibile, riprovare più tardi";
				LOG.error("MichelangeloGetDatiImpresa() " + ex.getMessage());
				//throw ex;
			}
		}
		LOG.debug("MichelangeloGetDatiImpresa END");
		return company;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * getText(aTextName) i18n localization 
	 */
	private String getText(String aTextName) {
		ActionSupport action = (ActionSupport)ActionContext.getContext().getActionInvocation().getAction();
		return action.getText(aTextName);
	}
	
	/**
	 * getText(aTextName, args[]) i18n localization 
	 */
	private String getText(String aTextName, String[] args) {
		ActionSupport action = (ActionSupport)ActionContext.getContext().getActionInvocation().getAction();
		return action.getText(aTextName, args);
	}

	/**
	 * traccia i campi tabellati per i quali non si trova la decodifica
	 */
	private void addFieldWarning(String field, Object target, String value) {
		if(target != null) {
			// aggiungi degli warning per i campi tabellati non decodificati 
			// i.e. "QualificaSoggetto", "TipoIndirizzo"
			String errorMessage = null;
			if("SoggettoQualifica".equalsIgnoreCase(field) && target instanceof SoggettoImpresaHelper) {
				SoggettoImpresaHelper soggetto = (SoggettoImpresaHelper) target;
				errorMessage = getText("Warnings.importXml.invalidSoggettoQualifica", 
									   new String[] {soggetto.getCognome(), soggetto.getNome(), value});
				
			} else if("TipoIndirizzo".equalsIgnoreCase(field) && target instanceof IndirizzoImpresaHelper) {
				IndirizzoImpresaHelper indir = (IndirizzoImpresaHelper) target;
				errorMessage = getText("Warnings.importXml.invalidTipoIndirizzo", 
									   new String[] {indir.getIndirizzo(), indir.getNumCivico(), indir.getComune(), value});
			}
			
			if(StringUtils.isNotEmpty(errorMessage)) {
				ActionSupport action = (ActionSupport)ActionContext.getContext().getActionInvocation().getAction();
				action.addActionMessage(errorMessage);
				LOG.warn(errorMessage);
			}
		}
	}


}
