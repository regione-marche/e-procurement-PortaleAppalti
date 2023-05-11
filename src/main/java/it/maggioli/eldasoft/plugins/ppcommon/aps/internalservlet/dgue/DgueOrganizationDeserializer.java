package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.response.organization.DgueCompanySubjectDeserializer;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.response.organization.DgueMainCompanyDeserializer;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;

/**
 * Classe che si occupa di deserializzare l'xml dell'organizzazione DGUE e popolare il wizard di registrazione.
 */
public class DgueOrganizationDeserializer {

    private static final int NUMBER_OF_INFORMATION  = 13;

    /**
     * Deserializzazione dell'xml (dati principali impresa)
     */
    private DgueMainCompanyDeserializer    mainCompanyDeserializer;
    /**
     * Deserializzazione dell'xml (soggetti dell'impresa. Solo il primo disponibile)
     */
    private DgueCompanySubjectDeserializer companySubjectDeserializer;

    /**
     * Creo il document e l'xpath per accedere "agevolmente" ai valori dell'xml.
     * @param fileContent Il contenuto della response dgue
     */
    public DgueOrganizationDeserializer(byte[] fileContent) {
        Document doc = getXmlFileDocument(fileContent); //Istanza contenente l'xml
        XPath xpath = XPathFactory.newInstance().newXPath();    //Oggetto per accedere ai valori dell'xml tramite percorso
        mainCompanyDeserializer = new DgueMainCompanyDeserializer(doc, xpath);
        companySubjectDeserializer = new DgueCompanySubjectDeserializer(doc, xpath);
    }

    public boolean isValidXML() {
        return mainCompanyDeserializer.getNotFoundValue() + companySubjectDeserializer.getNotFoundValue() < NUMBER_OF_INFORMATION;
    }

    public DgueMainCompanyDeserializer getMainCompanyFields() {
        return mainCompanyDeserializer;
    }
    public DgueCompanySubjectDeserializer getCompanySujects() {
        return companySubjectDeserializer;
    }

    /**
     * Popola il wizard di registrazione con i dati presenti nell'xml
     * @param datiImpresaHelper
     */
    public void populateHelper(WizardDatiImpresaHelper datiImpresaHelper) {
        mainCompanyDeserializer.populateHelper(datiImpresaHelper);
        companySubjectDeserializer.populateHelper(datiImpresaHelper);
    }


    /**
     * Parsing dell'oggetto in byte in Documento java
     * @param content
     * @return
     */
    public Document getXmlFileDocument(byte[] content) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // DTD External Entity Arbitrary read file (XXE attack)
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

//            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc =  db.parse(new ByteArrayInputStream(content));
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void populateHelper(WizardDatiImpresaHelper datiImpresaHelper, byte[] fileContent) {
        new DgueOrganizationDeserializer(fileContent).populateHelper(datiImpresaHelper);
    }

    public String toString() {
        return String.format(
                "%s {\n"
                    + "\tMain company:\n%s;\n"
                    + "\tSubjects:\n%s;\n"
                + "}"
                , getClass().getSimpleName()
                , mainCompanyDeserializer
                , companySubjectDeserializer
        );
    }

}
