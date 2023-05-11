package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;

//Non necessita di validazione perchè valorizzata dalla action
public class CatalogResultBean {

    protected CategoriaBandoIscrizioneType category;
    protected String codice;
    protected String classFrom;
    protected String classTo;
    protected String nota;

    protected CatalogResultBean() { }

    public CategoriaBandoIscrizioneType getCategory() {
        return category;
    }

    public String getCodice() {
        return codice;
    }

    public String getClassFrom() {
        return classFrom;
    }

    public String getClassTo() {
        return classTo;
    }

    public String getNota() {
        return nota;
    }

    public static final class CatalogResultBeanBuilder {
        private CatalogResultBean catalogResultBean;

        private CatalogResultBeanBuilder() {catalogResultBean = new CatalogResultBean();}

        public static CatalogResultBeanBuilder newCatalogResultBuilder() {return new CatalogResultBeanBuilder();}

        public CatalogResultBeanBuilder withCategory(CategoriaBandoIscrizioneType category) {
            catalogResultBean.category = category;
            return this;
        }

        public CatalogResultBeanBuilder withCodice(String codice) {
            catalogResultBean.codice = codice;
            return this;
        }

        public CatalogResultBeanBuilder withClassFrom(String classFrom) {
            catalogResultBean.classFrom = classFrom;
            return this;
        }

        public CatalogResultBeanBuilder withClassTo(String classTo) {
            catalogResultBean.classTo = classTo;
            return this;
        }
        public CatalogResultBeanBuilder withNota(String nota) {
            catalogResultBean.nota = nota;
            return this;
        }

        public CatalogResultBean build() {return catalogResultBean;}
    }
}
