package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.response.organization;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiPrincipaliImpresaHelper;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recupera i campi principali dell'azienda tramite xpath
 */
public class DgueMainCompanyDeserializer extends BaseDgueCompanyDeserializer {

    /**
     * Padova (PD)
     * Treviso (TV)
     * Venezia    (VE)
     */
    private static final Pattern CITY_AND_PROVINCE = Pattern.compile("([^(]+)\\(([^)]+)\\)\\s*");

    public DgueMainCompanyDeserializer(Document doc, XPath xpath) {
        super(doc, xpath);
    }

    public String getDenomination() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PartyName/Name");
    }
    public String getFiscalCode() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PartyIdentification/ID");
    }
    public String getVatCode() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/ServiceProviderParty/Party/PartyIdentification/ID");
    }
    public String getEmail() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/Contact/ElectronicMail");
    }
    public String getCity() {
        String city = getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/CityName");
        return StringUtils.trim(getGroupFromPattern(CITY_AND_PROVINCE, city, 1));
    }
    public String getProvince() {
        String province = getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/CityName");
        province = getGroupFromPattern(CITY_AND_PROVINCE, province, 2);
        try {
            if (StringUtils.isEmpty(province)
                    || !InterceptorEncodedData.get(InterceptorEncodedData.LISTA_PROVINCE).containsKey(province.toUpperCase()))
                province = null;
        } catch (Exception e) { province = null; }

        return StringUtils.trim(province);
    }
    private String getGroupFromPattern(Pattern pattern, String text, int group) {
        if (StringUtils.isNotEmpty(text)) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches())
                text = matcher.group(group);
        }
        return text;
    }
    /**
     * Il campo: //QualificationApplicationResponse/ContractingParty/Party/PostalAddress/StreetName <br/>
     * E' composto da {@literal <indirizzo>, <numero_civico>}
     * @return
     */
    public String getAddress() {
        String nodeContent = getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/StreetName");
        if (StringUtils.isNotEmpty(nodeContent))
            nodeContent = nodeContent.split(",")[0].trim();
        return nodeContent;
    }
    public String getCAP() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/PostalZone");
    }
    public String getNationality() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/Country/IdentificationCode");
    }
    public String getWebSite() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/ServiceProviderParty/Party/WebsiteURI");
    }
    public String getPhoneNumber() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/Contact/Telephone");
    }
    public String getFax() {
        return getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/Contact/Telefax");
    }

    /**
     * Il campo: //QualificationApplicationResponse/ContractingParty/Party/PostalAddress/StreetName <br/>
     * E' composto da {@literal <indirizzo>, <numero_civico>}
     * @return
     */
    public String getCivicNumber() {
        String nodeContent = getNodeContent("//QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/StreetName");
        if (StringUtils.isNotEmpty(nodeContent)) {
            String[] streetAndCivic = nodeContent.split(",");
            nodeContent = streetAndCivic.length == 2 ? streetAndCivic[1].trim() : null;
        }
        return nodeContent;
    }

    public void populateHelper(WizardDatiImpresaHelper datiImpresaHelper) {
        WizardDatiPrincipaliImpresaHelper datiPrincipaliImpresa = new WizardDatiPrincipaliImpresaHelper();
        datiImpresaHelper.setDatiPrincipaliImpresa(datiPrincipaliImpresa);
        datiPrincipaliImpresa.setCodiceFiscale(getFiscalCode());
        datiPrincipaliImpresa.setPartitaIVA(getVatCode());
        datiPrincipaliImpresa.setRagioneSociale(getDenomination());
        datiPrincipaliImpresa.setSitoWeb(getWebSite());
        datiPrincipaliImpresa.setTelefonoRecapito(getPhoneNumber());
        datiPrincipaliImpresa.setFaxRecapito(getFax());
        datiPrincipaliImpresa.setCapSedeLegale(getFiscalCode());
        datiPrincipaliImpresa.setIndirizzoSedeLegale(getAddress());
        datiPrincipaliImpresa.setComuneSedeLegale(getCity());
        datiPrincipaliImpresa.setProvinciaSedeLegale(getProvince());
        String nazionalita = getNationality();
        String email = getEmail();
        if ("IT".equals(nazionalita)) { //Se italiano, imposto la pec e l'ambito territoriale a ITALIANO
            datiPrincipaliImpresa.setAmbitoTerritoriale("1");
            datiPrincipaliImpresa.setEmailPECRecapito(email);
            datiPrincipaliImpresa.setEmailPECRecapitoConferma(email);
        } else {    //Se non italiano, imposto la mail e l'ambito territoriale a non italiano
            datiPrincipaliImpresa.setAmbitoTerritoriale("2");
            datiPrincipaliImpresa.setEmailRecapito(email);
            datiPrincipaliImpresa.setEmailRecapitoConferma(email);
        }
        datiPrincipaliImpresa.setNumCivicoSedeLegale(getCivicNumber());
        datiPrincipaliImpresa.setCapSedeLegale(getCAP());
    }

    public String toString() {
        return String.format(
                "%s {\n"
                        + "\tRagione sociale: %s;\n"
                        + "\tCodice fiscale: %s;\n"
                        + "\tPartita IVA: %s;\n"
                        + "\tEmail: %s;\n"
                        + "\tComune: %s;\n"
                        + "\tIndirizzo: %s;\n"
                        + "\tNumero civico: %s;\n"
                        + "\tCAP: %s;\n"
                        + "\tNazionalità: %s;\n"
                        + "\tSito web: %s;\n"
                        + "\tTelefono: %s;\n"
                        + "\tFax: %s;\n"
                    + "}"
                , getClass().getSimpleName()
                , getDenomination()
                , getFiscalCode()
                , getVatCode()
                , getEmail()
                , getCity()
                , getAddress()
                , getCivicNumber()
                , getFiscalCode()
                , getNationality()
                , getWebSite()
                , getPhoneNumber()
                , getFax()
        );
    }

}
