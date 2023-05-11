package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.dgue.response.organization;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserializza i soggetti dell'azienda, ma, salva sull'helper solo il primo (viene salvato come legale rappresentante)
 */
//public class DgueCompanySubjectDeserializer extends BaseDgueCompanyDeserializer implements Iterable<DgueCompanySubjectDeserializer.CompanySubject> {
public class DgueCompanySubjectDeserializer extends BaseDgueCompanyDeserializer {

    /**
     * Lista di soggetti trovati
     */
    private List<CompanySubject> subjects = new ArrayList<>(10);

    public DgueCompanySubjectDeserializer(Document doc, XPath xpath) {
        super(doc, xpath);
        loadSubjects();
    }

    /**
     * Cerco tutti i soggetti presenti nell'xml
     */
    public void loadSubjects() {
        //
        NodeList operatorParty = getNodeList("//QualificationApplicationResponse/EconomicOperatorParty/Party/PowerOfAttorney");
        if (operatorParty != null && operatorParty.getLength() > 0) {
            int index = -1;
            while (++index < operatorParty.getLength())
                subjects.add(new CompanySubject((NodeList) operatorParty.item(index)));
        }
    }

    /**
     * Se è stato trovato un soggetto, lo imposto a Legale Rappresentante, ignoro gli altri.
     * @param datiImpresaHelper
     */
    public void populateHelper(WizardDatiImpresaHelper datiImpresaHelper) {
        if (!subjects.isEmpty()) {
            ISoggettoImpresa legale = subjects.get(0).toSoggettoImpresa();
            legale.setSoggettoQualifica(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI);
            legale.setTipoSoggetto(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE);
            datiImpresaHelper.getLegaliRappresentantiImpresa().add(legale);
//            Disattivato perchè non conoscendo la qualifica si avrebbero problemi poi in fase di modifica.
//            if (subjects.size() > 1)
//                datiImpresaHelper.getAltreCaricheImpresa().addAll(
//                        subjects.subList(1, subjects.size())
//                                .stream()
//                                    .map(CompanySubject::toSoggettoImpresa)
//                                .collect(Collectors.toList())
//                );
        }
    }

//    @Override
//    public Iterator<CompanySubject> iterator() {
//        return subjects.iterator();
//    }
//
//    @Override
//    public void forEach(Consumer<? super CompanySubject> action) {
//        Iterable.super.forEach(action);
//    }
//
//    @Override
//    public Spliterator<CompanySubject> spliterator() {
//        return Iterable.super.spliterator();
//    }

    public String toString() {
        return subjects.toString();
    }

    /**
     * Classe che rappresenta un singolo soggetto e che si occupa di estrarne i valori.
     * Non viene utilizzato l'xpath, infatti sono stati specificati i namespace.
     */
    protected static class CompanySubject extends BaseDgueCompanyDeserializer {

        /**
         * Pattern della data di nascita dell'xml
         */
        private final static Pattern DGUE_DATE_FORMAT = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})\\+\\d{1,2}:\\d{1,2}");

        //Root del nodo del soggetto
        private NodeList subjectRoot;
        //Root del nodo residenza
        private NodeList residenceAddressRoot;
        //Root del nodo contatti
        private NodeList contactRoot;

        public CompanySubject(NodeList subject) {
            super(subject);
            subjectRoot = getSubjectRoot(subject);
            residenceAddressRoot = (NodeList) findNode(subjectRoot, "cac:ResidenceAddress");
            contactRoot = (NodeList) findNode(subjectRoot, "cac:Contact");
        }

        private NodeList getSubjectRoot(NodeList subject) {
            NodeList agentParty = (NodeList) findNode(subject, "cac:AgentParty");
            return (NodeList) findNode(agentParty, "cac:Person");
        }

        public String getName() {
            return getContent(findNode(subjectRoot, "cbc:FirstName"));
        }
        public String getSurname() {
            return getContent(findNode(subjectRoot, "cbc:FamilyName"));
        }

        /**
         * Converto la data in input alla data prevista dal form di registrazione
         * @return dd/mm/yyyy
         */
        public String getBirthDate() {
            String birthDate = getContent(findNode(subjectRoot, "cbc:BirthDate"));
            return StringUtils.isNotEmpty(birthDate) ? parseDateString(birthDate) : birthDate;
        }

        public String getBirthPlace() {
            return getContent(findNode(subjectRoot, "cbc:BirthplaceName"));
        }
        public String getTelephone() {
            return getContent(findNode(contactRoot, "cbc:Telephone"));
        }
        public String getEmail() {
            return getContent(findNode(contactRoot, "cbc:ElectronicMail"));
        }
        /**
         * Il campo: //QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/StreetName <br/>
         * E' composto da {@literal <indirizzo>, <numero_civico>}
         * @return
         */
        public String getAddress() {
            String nodeContent = getContent(findNode(residenceAddressRoot, "cbc:StreetName"));
            if (StringUtils.isNotEmpty(nodeContent))
                nodeContent = nodeContent.split(",")[0].trim();
            return nodeContent;
        }
        /**
         * Il campo: //QualificationApplicationResponse/EconomicOperatorParty/Party/PostalAddress/StreetName <br/>
         * E' composto da {@literal <indirizzo>, <numero_civico>}
         * @return
         */
        public String getCivicNumber() {
            String nodeContent = getContent(findNode(residenceAddressRoot, "cbc:StreetName"));
            if (StringUtils.isNotEmpty(nodeContent)) {
                String[] addressAndCivic = nodeContent.split(",");
                nodeContent = addressAndCivic.length == 2 ? addressAndCivic[1].trim() : null;
            }
            return nodeContent;
        }

        public String getCityName() {
            return getContent(findNode(residenceAddressRoot, "cbc:CityName"));
        }
        public String getCap() {
            return getContent(findNode(residenceAddressRoot, "cbc:PostalZone"));
        }
        public String getNationality() {
            return getContent(findNode((NodeList) findNode(residenceAddressRoot, "cac:Country"), "cbc:IdentificationCode"));
        }

        public String getContent(Node node) {
            return node != null ? node.getTextContent() : null;
        }

        /**
         * Creo un soggetto impresa a partire dalla classe corrente.
         * @return
         */
        public ISoggettoImpresa toSoggettoImpresa() {
            ISoggettoImpresa soggettoImpresa = new SoggettoImpresaHelper();

            soggettoImpresa.setNome(getName());
            soggettoImpresa.setCognome(getSurname());
            soggettoImpresa.setDataNascita(getBirthDate());
            soggettoImpresa.setComuneNascita(getBirthPlace());
//            soggettoImpresa.setTelefono ???
//            soggettoImpresa.setEmail ???
            soggettoImpresa.setIndirizzo(getAddress());
            soggettoImpresa.setComune(getCityName());
            soggettoImpresa.setCap(getCap());
            soggettoImpresa.setNazione(getNationality());
            soggettoImpresa.setNumCivico(getCivicNumber());

//            soggettoImpresa.setTipoSoggetto(CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA);
//            soggettoImpresa.setSoggettoQualifica(CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI);

            return soggettoImpresa;
        }

        /**
         * Converto la data in input in: dd/mm/yyyy; (conversione effettuata tramite Regex)
         * @param birthDate
         * @return
         */
        private String parseDateString(String birthDate) {
            Matcher matcher = DGUE_DATE_FORMAT.matcher(birthDate);
            return matcher.matches()
                    ? String.format("%s/%s/%s", matcher.group(3), matcher.group(2), matcher.group(1))
                    : birthDate;
        }

        public String toString() {
            return String.format(
                    "%s {\n"
                        + "\tName: %s;\n"
                        + "\tSurname: %s;\n"
                        + "\tBirthDate: %s;\n"
                        + "\tBirthPlace: %s;\n"
                        + "\tTelephone: %s;\n"
                        + "\tEmail: %s;\n"
                        + "\tAddress: %s;\n"
                        + "\tCity Name: %s;\n"
                        + "\tCap: %s;\n"
                        + "\tNationality: %s;\n"
                    + "}"
                    , getClass().getSimpleName()
                    , getName()
                    , getSurname()
                    , getBirthDate()
                    , getBirthPlace()
                    , getTelephone()
                    , getEmail()
                    , getAddress()
                    , getCityName()
                    , getCap()
                    , getNationality()
            );
        }

    }

}
