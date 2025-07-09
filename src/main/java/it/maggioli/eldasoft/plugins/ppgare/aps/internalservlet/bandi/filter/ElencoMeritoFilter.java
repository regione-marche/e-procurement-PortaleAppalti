package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.filter;

import it.eldasoft.www.sil.WSGareAppalto.ElencoOperatoriAbilitatiSearch;
import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;

public class ElencoMeritoFilter extends BaseSearchBean {

    @Validate(EParamValidation.CODICE_FISCALE)
    private String fiscalCode;
    @Validate(EParamValidation.COMUNE)
    private String city;
    @Validate(EParamValidation.PROVINCIA)
    private String province;
    @Validate(EParamValidation.RAGIONE_SOCIALE)
    private String businessName;
    @Validate(EParamValidation.RATING_LEGALITA)
    private String rating;
    @Validate(EParamValidation.CODICE_CATEGORIA)
    private String category;
    @Validate(EParamValidation.CODICE)
    private String classification;
    @Validate(EParamValidation.CODICE)
    private String codElenco;
    private String stato;


    public ElencoOperatoriAbilitatiSearch toSearch() {
        ElencoOperatoriAbilitatiSearch toReturn = new ElencoOperatoriAbilitatiSearch();

        toReturn.setBusinessName(businessName);
        toReturn.setCity(city);
        toReturn.setFiscalCode(fiscalCode);
        toReturn.setRating(rating);
        toReturn.setCategory(category);
        toReturn.setClassification(classification);
        toReturn.setProvince(province);
        toReturn.setStato(StringUtils.isEmpty(stato) ? null : StringUtils.equals(stato, "1"));
        toReturn.setCodElenco(codElenco);
        toReturn.setMaxNumRecord(getiDisplayLength());
        toReturn.setIndicePrimoRecord(getCurrentPage() > 0 ?  getiDisplayLength() * (getCurrentPage() - 1) : 0);

        return toReturn;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getBusinessName() {
        return businessName;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getClassification() {
        return classification;
    }
    public void setClassification(String classification) {
        this.classification = classification;
    }
    public String getStato() {
        return stato;
    }
    public void setStato(String stato) {
        this.stato = stato;
    }
    public String getCodElenco() {
        return codElenco;
    }
    public void setCodElenco(String codElenco) {
        this.codElenco = codElenco;
    }

}
