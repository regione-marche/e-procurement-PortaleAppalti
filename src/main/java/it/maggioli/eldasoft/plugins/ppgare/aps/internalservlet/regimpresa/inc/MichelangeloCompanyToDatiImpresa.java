package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc;

import java.util.LinkedHashMap;
import org.apache.commons.lang3.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IndirizzoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;

/**
 * ...
 */
public class MichelangeloCompanyToDatiImpresa {

	/**
	 * inizializza l'helper dei dati impresa con quelli restituiti da Michelangelo (SACE) 
	 * @throws Exception 
	 */	
	public static void companyToImpresaHelper(
			MichelangeloCompanyInfo company, 
			WizardDatiImpresaHelper impresa) 
	{
		// importa i dati della ditta
		// in caso di persona fisica (LegalFormDescription = 'PF' o company.Company.GeneralData.CompanyName vuoto)
		impresa.getDatiPrincipaliImpresa().setRagioneSociale(
				StringUtils.isNotEmpty(company.Company.GeneralData.CompanyName) 
				? company.Company.GeneralData.CompanyName.trim() 
				: ((company.Company.GeneralData.Name != null ? company.Company.GeneralData.Name.trim() : "") + " " 
				   + (company.Company.GeneralData.Surname != null ? company.Company.GeneralData.Surname.trim() : "").trim()));

		// CF arriva dalla richiesta di import com parametro per recuperare i dati dal servizio
		//impresa.getDatiPrincipaliImpresa().setCodiceFiscale(...); 
		
		impresa.getDatiPrincipaliImpresa().setPartitaIVA(
				StringUtils.isNotEmpty(company.Company.GeneralData.PIVA) ? company.Company.GeneralData.PIVA : null);

		impresa.getDatiPrincipaliImpresa().setTelefonoRecapito(
				company.Company.GeneralData.Telephone != null ? company.Company.GeneralData.Telephone.trim() : null);
		
		impresa.getDatiPrincipaliImpresa().setFaxRecapito(
				company.Company.GeneralData.Fax != null ? company.Company.GeneralData.Fax : null);

		// imposta il codice ditta esterno
		impresa.getDatiPrincipaliImpresa().setIdAnagraficaEsterna(
				company.Company.GeneralData.SaceGroupCode != null ? company.Company.GeneralData.SaceGroupCode.toString() : null
		);
		
		impresa.getDatiPrincipaliImpresa().setNazioneSedeLegale(decodeCountry(company.Company.GeneralData.CompanyCountry));
		
		impresa.getDatiPrincipaliImpresa().setAmbitoTerritoriale(
				"IT".equalsIgnoreCase(company.Company.GeneralData.CompanyCountry) ? "1" : "2");
		
		if(StringUtils.isNotEmpty(company.Company.GeneralData.CCIAA)) { 
			impresa.getDatiUlterioriImpresa().setIscrittoCCIAA("1");
			impresa.getDatiUlterioriImpresa().setProvinciaIscrizioneCCIAA(
					company.Company.GeneralData.CCIAA != null ? company.Company.GeneralData.CCIAA : null);
			impresa.getDatiUlterioriImpresa().setNumIscrizioneCCIAA(
					company.Company.GeneralData.CCIAANumber != null ? company.Company.GeneralData.CCIAANumber.trim() : null);
		}
		
		//decodeCompanyType(impresa, company.Company.GeneralData.CompanyType); NON UTILIZZATO
		
		impresa.getDatiUlterioriImpresa().setSettoreAttivitaEconomica(decodeTradeSector(company.Company.GeneralData.TradeSector));

		//decodeLegalForm(...)
		
		decodeLegalFormDescription(impresa, company);
		
		decodeAddress(impresa, company.Company.Address);
	}
	
//	/**
//	 * decode CompanyType
//	 * @throws Exception 
//	 */	
//	private static void decodeCompanyType(
//			WizardDatiImpresaHelper impresa, 
//			Integer companyType) 
//	{
//		// CompanyType: 
//		// usare il nuovo tabellato A1z28 per valorizzare tipologia impresa e forma giuridica
//	}
	
	/**
	 * decode Address
	 */
	private static void decodeAddress(
			WizardDatiImpresaHelper impresa, 
			//List<MichelangeloCompanyAddress> address, 
			MichelangeloCompanyAddress address) 
	{
		// imposta gli indirizzi (sede legale e indirizzi aggiuntivi)
		impresa.getIndirizziImpresa().clear();
		/*
		// ...indirizzo sede legale...
		MichelangeloCompanyAddress sedeLegale = address.stream()
			.filter(a -> "LE".equalsIgnoreCase(a.AddressType))
			.findFirst()
			.orElse(null);
		if(sedeLegale != null) {
			addIndirizzo(impresa, sedeLegale);
		}
		
		// ...indirizzi aggiuntivi...
		address.stream()
			.filter(a -> !"LE".equalsIgnoreCase(a.AddressType))
			.forEach(a -> addIndirizzo(impresa, a));
		*/		
		addIndirizzo(impresa, address);
	}
	
	private static void addIndirizzo(
			WizardDatiImpresaHelper impresa, 
			MichelangeloCompanyAddress address) 
	{
		// nel caso di indirizzo con AddressType=LE, allora si deve popolare la sede legale nei dati principali, 
		// altrimenti si crea un altro indirizzo rispettando la codifica indicata nel paragrafo 5.3.2 nel 
		// documento 2024_02_21_SACE Integrazione SAP_rev LP004.docx
		if("LE".equalsIgnoreCase(address.AddressType)) {
			// sede legale
			impresa.getDatiPrincipaliImpresa().setIndirizzoSedeLegale(address.StreetName != null ? address.StreetName.trim() : null);
			impresa.getDatiPrincipaliImpresa().setNumCivicoSedeLegale(address.StreetNumber != null ? address.StreetNumber.trim() : null);
			impresa.getDatiPrincipaliImpresa().setCapSedeLegale(address.Zip != null ? address.Zip : null);
			impresa.getDatiPrincipaliImpresa().setComuneSedeLegale(address.City != null ? address.City : null);
			impresa.getDatiPrincipaliImpresa().setProvinciaSedeLegale(address.Province != null ? address.Province : null);
			impresa.getDatiPrincipaliImpresa().setNazioneSedeLegale(decodeCountry(address.Country));
		} else {
			// indirizzi aggiuntivi
			IndirizzoImpresaHelper indirizzo = new IndirizzoImpresaHelper(address);
			indirizzo.setNazione(decodeCountry(indirizzo.getNazione()));
			impresa.getIndirizziImpresa().add(indirizzo);
		}
	}
	
	/**
	 * decode TradeSector
	 */
	private static String decodeTradeSector(String tradeSector) {
		// TradeSector: 
		// usare la codifica da interceptor  denominata "settoreAttivitaEconomica" , con l'attenzione di 
		// escludere i primi 2 caratteri (lettera alfabeto seguita da ".")
		String settoreAttivita = null;
		if(StringUtils.isNotEmpty(tradeSector)) {
			try {
				// 49.41 => 41
				LinkedHashMap<String, String> t = InterceptorEncodedData.get(InterceptorEncodedData.LISTA_SETTORE_ATTIVITA_ECONOMICA);
				tradeSector = tradeSector.trim();
				int i = tradeSector.indexOf(".");
				settoreAttivita = (i > 0 ? tradeSector.substring(i + 1) : null);
				settoreAttivita = (settoreAttivita != null ? t.get(settoreAttivita) : null);
			} catch (Exception ex) {
				ApsSystemUtils.getLogger().error("decodeTradeSector", ex);
			}
		}
		return settoreAttivita;
	}
	
	/**
	 * decode Country, CompanyCountry
	 */
	private static String decodeCountry(String codiceNazione) {
		// Country, CompanyCountry: 
		// il campo con il codice nazione deve essere gestito eseguendo una prima codifica con 
		// l'interceptor mediante il set "nazioniCodificate", in modo da ottenere la descrizione nazione, quindi usare 
		// il set "nazioni" per reperire il nostro codice a partire dalla descrizione (es: "IT" => "Italia" => "1")
		//it -> Italia -> (codice del tabellato nazioni del portale)
		String nazione = null;
		if(StringUtils.isNotEmpty(codiceNazione)) {
			try {
				//nazione = codiceNazione;
				String cod = codiceNazione.trim();
				// IT => [IT, Italia] => Italia
				LinkedHashMap<String, String> t = InterceptorEncodedData.get(InterceptorEncodedData.LISTA_NAZIONI_CODIFICATE);
				String descr = t.entrySet().stream()
						.filter(k -> cod.equalsIgnoreCase(k.getValue()))
						.findFirst()
						.map(k -> k.getKey())
						.orElse(null);
				
				// Italia => [1, Italia] => 1
				//LinkedHashMap<String, String> t2 = InterceptorEncodedData.get(InterceptorEncodedData.LISTA_CODICI_NAZIONI);
				//if(descr != null) {
				//	nazione = t2.entrySet().stream()
				//			.filter(k -> descr.equalsIgnoreCase(k.getValue()))
				//			.findFirst()
				//			.map(k -> k.getKey())
				//			.orElse(null);
				//}
				
				nazione = descr;
			} catch (Exception ex) {
				ApsSystemUtils.getLogger().error("decodeCountry", ex);
			}
		}
		return nazione;
	}

//	/**
//	 * decode LegalForm
//	 */
//	private static String decodeLegalForm(String legalForm)	{
//		if(StringUtils.isNotEmpty(legalForm)) {
//			// DA FARE
//		}
//		return legalForm;
//	}

	/**
	 * decode LegalFormDescription (solo per OE italiani)
	 */
	private static void decodeLegalFormDescription(
		WizardDatiImpresaHelper impresa, 
		MichelangeloCompanyInfo company) 
	{
		// LegalFormDescription: 
		// usare il nuovo tabellato A1z28 per valorizzare tipologia impresa e forma giuridica
		impresa.getDatiPrincipaliImpresa().setTipoImpresa(null);
		impresa.getDatiPrincipaliImpresa().setNaturaGiuridica(null);
		
		// NB: caso particolare per PF, nel tabellato A1z28 esistono 2 occorrenze (persona fisica, persona fisica estero)
		// in fase di creazione della lista si sostituisce PF per l'estero con PFE !!!
		// Vedi: InterceptorEncodedData
		String legalFormDescription = company.Company.GeneralData.LegalFormDescription;
		if("PF".equalsIgnoreCase(legalFormDescription) && !"IT".equalsIgnoreCase(company.Company.GeneralData.CompanyCountry)) {
			// OE NON italiano, persona fisica ==> per ora non si decodifica !!!
			legalFormDescription = null;
		}
 
		if(StringUtils.isNotEmpty(legalFormDescription)) {
			try {
				LinkedHashMap<String, String> t = InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_NATURA_GIURIDICA);
				String value = t.get(legalFormDescription);
				String[] v = (value != null ? value.split(",") : null);
				if(v != null && v.length > 1) { 
					impresa.getDatiPrincipaliImpresa().setTipoImpresa(v[0]);
					impresa.getDatiPrincipaliImpresa().setNaturaGiuridica(v[1] );
				}	
			} catch (Exception ex) {
				ApsSystemUtils.getLogger().error("decodeCompanyType", ex);
			}
		}
	}

}

