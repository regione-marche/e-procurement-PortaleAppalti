package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.prezzi_unitari;

import com.agiletec.aps.util.ApsWebApplicationUtils;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.IntStream;

public class PrezziUnitariFields implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(PrezziUnitariFields.class);

    public static final Integer DECIMALI_MAX = 5;

    private String voce;
    private String descrizione;
    private String descrizioneEstesa;
    private String unitaDiMisura;
    private Double quantita;
    private Double prezzoBase;
    private Double prezzo;  //Editabile caso 1 e 3
    private Double importo;
    //Fino a qui caso 1
    private Double peso;
    private Double ribasso;  //Editabile caso 2
    private Double ribassoPesato;
    //Fino a qui caso 2
    private Boolean isOfferta = false;  //Editabile caso 3
    private Date dataConsegnaRichiesta;
    private String tipo;    //Editabile caso 3
    private String note;    //Editabile caso 3
    private String dataConsegnaOfferta;
    private Long decimaliRibasso;


    private transient Collection<String> tipologie = null;

    protected PrezziUnitariFields() { }

    public static List<PrezziUnitariFields> buildFromBustaEconomicaSession(WizardOffertaEconomicaHelper helper) {
        List<PrezziUnitariFields> toReturn = new ArrayList<>();

        for (int i = 0; i < helper.getVociDettaglio().size(); i++)
            toReturn.add(buildFromBustaEconomicaSession(helper, i));

        return toReturn;
    }

    /**
     *
     * Gli atttributi aggiuntivi e l'invio della comunicazione non sono gestiti. Vedi: ProcessPagePrezziUnitariAction
     *
     * @param fields
     * @param tipologia: 1 = prezzi unitari semplice, 2 = prezzi unitari con ribasso, 3 = ricerca di mercato
     */
    public static void save(List<PrezziUnitariFields> fields, int tipologia) {
        BustaEconomica bustaEco = GestioneBuste.getBustaEconomicaFromSession();
        WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
        if (CollectionUtils.isNotEmpty(fields)) {
            int index = 0;
            while (index < fields.size()) {
                fieldToSession(fields.get(index), bustaEco, index, tipologia);
                index++;
            }

            helper.setTotaleOffertaPrezziUnitari(
                roundDouble(getTotaleOffertaPrezziUnitari(fields, tipologia), 5)
            );

            helper.setImportoOfferto(
                roundDouble(helper.getTotaleOffertaPrezziUnitari() + getImpSic(helper), 5)
            );
            helper.setRigenPdf(true);
            helper.setPdfUUID(null);
        }
    }

    private static double getImpSic(WizardOffertaEconomicaHelper helper) {
        return helper.getImportoSicurezzaNonSoggettoRibassoOneriProgettazione();
    }

    private static Double getTotaleOffertaPrezziUnitari(List<PrezziUnitariFields> fields, int tipologia) {
        return fields.stream()
                .filter(field -> field.isOfferta || tipologia != GenExcelPrezziUnitariAction.PREZZI_UNITARI_RICERCA_MERCATO)
                .map(field -> field.importo)
                .filter(Objects::nonNull)   //Significa rimuovi dalla lista tutti gli elementi nulli.
                .reduce(0.0d, Double::sum); //Significa: somma tutti gli elementi della lista e ritorna la somma.
    }

    private static void fieldToSession(PrezziUnitariFields field, BustaEconomica bustaEco, int index, int tipologia) {
        WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
        VoceDettaglioOffertaType voce = helper.getVociDettaglio().get(index);

        if (tipologia == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RIBASSO) {
            voce.setRibassoPercentuale(field.ribasso);
            if(voce.getPeso() != null) {
                voce.setRibassoPesato(
                    roundDouble(field.ribassoPesato, helper.getNumDecimaliRibasso().intValue())
                );
            }
        }
        helper.getPrezzoUnitario()[index] = field.prezzo;
        helper.getImportoUnitario()[index] = field.importo;
        helper.getTipo()[index] = field.tipo;
        helper.getNote()[index] = field.note;
        voce.setNote(field.note);
        voce.setTipo(field.tipo);
        if (field.isOfferta)
            helper.getDataConsegnaOfferta()[index] = calcDataConsegnaOfferta(voce);

        if (field.isOfferta || tipologia != GenExcelPrezziUnitariAction.PREZZI_UNITARI_RICERCA_MERCATO)
            voce.setQuantitaOfferta(null);
        else
            voce.setQuantitaOfferta(0.0d);

        if (field.importo != null)
            helper.getImportoUnitarioStringaNotazioneStandard()[index] = StringUtilities.getDoubleNormalNotation(field.importo, 5);
        else
            helper.getImportoUnitarioStringaNotazioneStandard()[index] = "";

        voce.setDataConsegnaOfferta(voce.getDataConsegnaRichiesta());
    }

    private static String calcDataConsegnaOfferta(VoceDettaglioOffertaType voce) {
        return voce.getDataConsegnaRichiesta() != null
                ? CalendarValidator.getInstance().format(voce.getDataConsegnaRichiesta(), "dd/MM/yyyy")
                : null;
    }

    private static Double roundDouble(Double toRound, int scale) {
        return toRound == null
                ? null
                : BigDecimal
                    .valueOf(toRound)
                    .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }


    public String getVoce() {
        return voce;
    }


    public String getDescrizione() {
        return descrizione;
    }


    public String getDescrizioneEstesa() {
        return descrizioneEstesa;
    }


    public String getUnitaDiMisura() {
        return unitaDiMisura;
    }


    public Double getQuantita() {
        return quantita;
    }


    public Double getPrezzo() {
        return prezzo;
    }


    public Double getImporto() {
        return importo;
    }


    public Double getPrezzoBase() {
        return prezzoBase;
    }


    public Double getPeso() {
        return peso;
    }

    public Double getRibasso() {
        return ribasso;
    }


    public Double getRibassoPesato() {
        return ribassoPesato;
    }


    public Boolean getOfferta() {
        return isOfferta;
    }

    public Date getDataConsegnaRichiesta() {
        return dataConsegnaRichiesta;
    }


    public String getTipo() {
        return tipo;
    }

    public String getDataConsegnaOfferta() {
        return dataConsegnaOfferta;
    }

    public Long getDecimaliRibasso() {
        return decimaliRibasso;
    }

    public void setIsOfferta(Boolean isOfferta) {   //Taccone per ProcessPagePrezziUnitariAction
        this.isOfferta = isOfferta;
    }
    public String getTipoTextFromValue() {
        WSGareAppaltoWrapper wrapper = getGareAppaltoWrapper();
        String xml = getXmlTipologieProdotto(wrapper);
        Map<String, String> tipologia = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(xml)) {
            try {
                tipologia = InterceptorEncodedData.parseXml(xml);
                tipologie = tipologia.values();
            } catch (XmlException e) {
                throw new RuntimeException(e);
            }
        }

        return tipologia != null ? tipologia.get(tipo) : null;
    }
    public String getTipoValueFromText() {
        String toReturn = null;

        WSGareAppaltoWrapper wrapper = getGareAppaltoWrapper();
        String xml = getXmlTipologieProdotto(wrapper);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(xml)) {
            try {
                Map<String, String> tipologia = InterceptorEncodedData.parseXml(xml);
                tipologie = tipologia.values();
                toReturn = tipologia.entrySet()
                            .stream()
                                .filter(e -> e.getValue().equalsIgnoreCase(tipo))
                                .map(Map.Entry::getKey)
                                .findFirst()
                            .orElse("");
            } catch (XmlException e) {
                throw new RuntimeException(e);
            }
        }

        return toReturn;
    }

    private WSGareAppaltoWrapper getGareAppaltoWrapper() {
        return (WSGareAppaltoWrapper) ApsWebApplicationUtils
                .getBean(PortGareSystemConstants.WS_GARE_APPALTO,
                        ServletActionContext.getRequest());
    }

    private String getXmlTipologieProdotto(WSGareAppaltoWrapper wrapper) {
        try {
            return wrapper.getProxyWSGare().getTipologiaProdotto();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<String> getTipologie() {
        return tipologie;
    }

    public String getNote() {
        return note;
    }

    private static int getCurrentFieldIndex(WizardOffertaEconomicaHelper helper, PrezziUnitariFields fields) {
        List<VoceDettaglioOffertaType> dettaglio = helper.getVociDettaglio();

        return IntStream.range(0, dettaglio.size())
                .filter(index -> isFieldsReferringToThisDetails(fields, dettaglio.get(index)))
                .findAny()
            .orElse(-1);
    }

    private static boolean isFieldsReferringToThisDetails(PrezziUnitariFields fields, VoceDettaglioOffertaType dettaglio) {
        return StringUtils.equals(dettaglio.getCodice(), fields.voce) && StringUtils.equals(dettaglio.getVoce(), fields.descrizione);
    }

    private static PrezziUnitariFields buildFromBustaEconomicaSession(WizardOffertaEconomicaHelper helper, int index) {
        PrezziUnitariFields toReturn = new PrezziUnitariFields();

        log.debug("START - buildFromBustaEconomicaSession");

        VoceDettaglioOffertaType dettaglio = helper.getVociDettaglio().get(index);

        toReturn.descrizione = dettaglio.getVoce();
        toReturn.descrizioneEstesa = dettaglio.getDescrizione();
        toReturn.importo = helper.getImportoUnitario()[index];
        toReturn.prezzo = helper.getPrezzoUnitario()[index];
        toReturn.prezzoBase = dettaglio.getPrezzoUnitario();
        toReturn.quantita = dettaglio.getQuantita();
        toReturn.voce = dettaglio.getCodice();
        toReturn.unitaDiMisura = dettaglio.getUnitaMisura();

        toReturn.peso = dettaglio.getPeso();
        toReturn.ribasso = dettaglio.getRibassoPercentuale();
        toReturn.ribassoPesato = dettaglio.getRibassoPesato();

        Double qtaOfferta = dettaglio.getQuantitaOfferta();
        toReturn.isOfferta = !(qtaOfferta != null && qtaOfferta == 0.0);
        toReturn.dataConsegnaRichiesta = dettaglio.getDataConsegnaRichiesta();
        toReturn.tipo = dettaglio.getTipo();
        toReturn.note = dettaglio.getNote();

        log.trace("Found the following values in the session: {}", toReturn);
        log.debug("END - buildFromBustaEconomicaSession");

        //Valore attributi agg non sono gestiti
//        if (MapUtils.isNotEmpty(helper.getValoreAttributiAgg())) {
//            toReturn.tempoDiConsegna = helper.getValoreAttributiAgg().get("XDPRE01")[index];
//            toReturn.aliquotaIva = helper.getValoreAttributiAgg().get("XDPRE03")[index];
//            toReturn.codiceProdottoOfferto = helper.getValoreAttributiAgg().get("XDPRE04")[index];
//            toReturn.codiceProdottoDelProduttore = helper.getValoreAttributiAgg().get("XDPRE07")[index];
//            toReturn.descrizioneAggiuntiva = helper.getValoreAttributiAgg().get("XDPRE08")[index];
//        }

        return toReturn;
    }


    /**
     * I metodi che finiscono x *FromFormula devono essere richiamati per ultimi, perchè necessitano dei dati
     */
    public static class PrezziUnitariFieldsBuilder {

        private PrezziUnitariFields fields = new PrezziUnitariFields();

        public PrezziUnitariFieldsBuilder setVoce(String voce) {
            fields.voce = voce;
            return this;
        }
        public PrezziUnitariFieldsBuilder setDescrizione(String descrizione) {
            fields.descrizione = descrizione;
            return this;
        }
        public PrezziUnitariFieldsBuilder setDescrizioneEstesa(String descrizioneEstesa) {
            fields.descrizioneEstesa = descrizioneEstesa;
            return this;
        }
        public PrezziUnitariFieldsBuilder setUnitaDiMisura(String unitaDiMisura) {
            fields.unitaDiMisura = unitaDiMisura;
            return this;
        }
        public PrezziUnitariFieldsBuilder setQuantita(Double quantita) {
            fields.quantita = quantita;
            return this;
        }
        public PrezziUnitariFieldsBuilder setPrezzo(Double prezzo) {
            fields.prezzo = prezzo;
            return this;
        }
        public PrezziUnitariFieldsBuilder setImporto(Double importo) {
            fields.importo = importo;
            return this;
        }
        public PrezziUnitariFieldsBuilder setPrezzoBase(Double prezzoBase) {
            fields.prezzoBase = prezzoBase;
            return this;
        }
        public PrezziUnitariFieldsBuilder setRibasso(Double ribasso) {
            fields.ribasso = ribasso;
            return this;
        }
        public PrezziUnitariFieldsBuilder setRibassoPesato(Double ribassoPesato) {
            fields.ribassoPesato = ribassoPesato;
            return this;
        }
        public PrezziUnitariFieldsBuilder setDataConsegnaRichiesta(Date dataConsegnaRichiesta) {
            fields.dataConsegnaRichiesta = dataConsegnaRichiesta;
            return this;
        }
        public PrezziUnitariFieldsBuilder setPeso(Double peso) {
            fields.peso = peso;
            return this;
        }
        public PrezziUnitariFieldsBuilder setOfferta(Boolean offerta) {
            fields.isOfferta = offerta;
            return this;
        }
        public PrezziUnitariFieldsBuilder setTipo(String tipo) {
            fields.tipo = tipo;
            return this;
        }
        public PrezziUnitariFieldsBuilder setTipoFromText(String tipo) {
            fields.tipo = tipo;
            fields.tipo = fields.getTipoValueFromText();
            return this;
        }
        public PrezziUnitariFieldsBuilder setNote(String note) {
            fields.note = note;
            return this;
        }
        public PrezziUnitariFieldsBuilder setDataConsegnaOfferta(String dataConsegnaOfferta) {
            fields.dataConsegnaOfferta = dataConsegnaOfferta;
            return this;
        }

        public PrezziUnitariFieldsBuilder setDecimaliRibasso(Long decimaliRibasso) {
            fields.decimaliRibasso = decimaliRibasso;
            return this;
        }

        public PrezziUnitariFieldsBuilder setRibassoPesatoFromFormula(int importCase) {
            if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RIBASSO)
                fields.ribassoPesato = fields.ribasso != null ? roundDouble(fields.peso / 100d * fields.ribasso, fields.decimaliRibasso.intValue()) : 0.0d;
            return this;
        }
        public PrezziUnitariFieldsBuilder setPrezzoFromFormula(int importCase) {
            if (importCase == GenExcelPrezziUnitariAction.PREZZI_UNITARI_RIBASSO)
                fields.prezzo = fields.ribasso != null ? roundDouble(fields.prezzoBase * (1d - fields.ribasso / 100d), DECIMALI_MAX) : null;
            return this;
        }
        public PrezziUnitariFieldsBuilder setImportoFromFormula(int importCase) {
            switch (importCase) {
                case GenExcelPrezziUnitariAction.PREZZI_UNITARI_SEMPLICE:
                case GenExcelPrezziUnitariAction.PREZZI_UNITARI_RICERCA_MERCATO:
                    fields.importo = fields.prezzo != null ? fields.quantita * fields.prezzo : null;
                    break;
                case GenExcelPrezziUnitariAction.PREZZI_UNITARI_RIBASSO:
                    fields.importo = fields.ribasso != null ? fields.prezzoBase * (1d - fields.ribasso / 100d) * fields.quantita : null;
                    break;
            }

            return this;
        }

        public PrezziUnitariFields build() {
            return fields;
        }

    }

    @Override
    public String toString() {
        return "PrezziUnitariFields{" +
                "voce='" + voce + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", descrizioneEstesa='" + descrizioneEstesa + '\'' +
                ", unitaDiMisura='" + unitaDiMisura + '\'' +
                ", quantita=" + quantita +
                ", prezzoBase=" + prezzoBase +
                ", prezzo=" + prezzo +
                ", importo=" + importo +
                ", peso=" + peso +
                ", ribasso=" + ribasso +
                ", ribassoPesato=" + ribassoPesato +
                ", isOfferta=" + isOfferta +
                ", dataConsegnaRichiesta=" + dataConsegnaRichiesta +
                ", tipo='" + tipo + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
