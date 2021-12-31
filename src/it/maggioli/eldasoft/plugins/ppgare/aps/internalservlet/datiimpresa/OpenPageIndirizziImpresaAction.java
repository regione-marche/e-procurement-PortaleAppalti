package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'apertura della pagina degli indirizzi dell'impresa
 * nel wizard di aggiornamento dati impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class OpenPageIndirizziImpresaAction extends AbstractOpenPageAction
	implements IIndirizzoImpresa {

    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private String id;
    private String tipoIndirizzo;
    private String indirizzo;
    private String numCivico;
    private String cap;
    private String comune;
    private String provincia;
    private String nazione;
    private String telefono;
    private String fax;

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return the tipoIndirizzo
     */
    public String getTipoIndirizzo() {
	return tipoIndirizzo;
    }

    /**
     * @param tipoIndirizzo
     *            the tipoIndirizzo to set
     */
    public void setTipoIndirizzo(String tipoIndirizzo) {
	this.tipoIndirizzo = tipoIndirizzo;
    }

    /**
     * @return the indirizzo
     */
    public String getIndirizzo() {
	return indirizzo;
    }

    /**
     * @param indirizzo
     *            the indirizzo to set
     */
    public void setIndirizzo(String indirizzo) {
	this.indirizzo = indirizzo;
    }

    /**
     * @return the numCivico
     */
    public String getNumCivico() {
	return numCivico;
    }

    /**
     * @param numCivico
     *            the numCivico to set
     */
    public void setNumCivico(String numCivico) {
	this.numCivico = numCivico;
    }

    /**
     * @return the cap
     */
    public String getCap() {
	return cap;
    }

    /**
     * @param cap
     *            the cap to set
     */
    public void setCap(String cap) {
	this.cap = cap;
    }

    /**
     * @return the comune
     */
    public String getComune() {
	return comune;
    }

    /**
     * @param comune
     *            the comune to set
     */
    public void setComune(String comune) {
	this.comune = comune;
    }

    /**
     * @return the provincia
     */
    public String getProvincia() {
	return provincia;
    }

    /**
     * @param provincia
     *            the provincia to set
     */
    public void setProvincia(String provincia) {
	this.provincia = provincia;
    }

    /**
     * @return the nazione
     */
    public String getNazione() {
	return nazione;
    }

    /**
     * @param nazione
     *            the nazione to set
     */
    public void setNazione(String nazione) {
	this.nazione = nazione;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
	return telefono;
    }

    /**
     * @param telefono
     *            the telefono to set
     */
    public void setTelefono(String telefono) {
	this.telefono = telefono;
    }

    /**
     * @return the fax
     */
    public String getFax() {
	return fax;
    }

    /**
     * @param fax
     *            the fax to set
     */
    public void setFax(String fax) {
	this.fax = fax;
    }

    public String openPage() {
	WizardDatiImpresaHelper helper = getSessionHelper();

	if (helper == null) {
	    // la sessione e' scaduta, occorre riconnettersi
	    this.addActionError(this.getText("Errors.sessionExpired"));
	    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
	} else {
	    // la sessione non e' scaduta, per cui proseguo regolarmente

	    this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
		    PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_INDIRIZZI);
	}
	return this.getTarget();
    }

    /**
     * Apre la pagina svuotando i dati del form di inserimento/modifica
     * 
     * @return target
     */
    public String openPageClear() {
	WizardDatiImpresaHelper helper = getSessionHelper();

	if (helper == null) {
	    // la sessione e' scaduta, occorre riconnettersi
	    this.addActionError(this.getText("Errors.sessionExpired"));
	    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
	} else {
	    // la sessione non e' scaduta, per cui proseguo regolarmente

	    // svuota i dati inseriti nella form in modo da riaprire la
	    // form pulita
	    ProcessPageIndirizziImpresaAction.resetIndirizzoImpresa(this);
	    this.id = null;

	    this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
		    PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_INDIRIZZI);
	}
	return this.getTarget();
    }

    public String openPageModify() {
	WizardDatiImpresaHelper helper = getSessionHelper();

	if (helper == null) {
	    // la sessione e' scaduta, occorre riconnettersi
	    this.addActionError(this.getText("Errors.sessionExpired"));
	    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
	} else {
	    // la sessione non e' scaduta, per cui proseguo regolarmente

	    // popola nel bean i dati presenti nell'elemento in sessione
	    // individuato da id
	    IIndirizzoImpresa indirizzo = helper.getIndirizziImpresa().get(
		    Integer.parseInt(this.id));
	    ProcessPageIndirizziImpresaAction.synchronizeIndirizzoImpresa(
		    indirizzo, this);

	    this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
		    PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_INDIRIZZI);
	}
	return this.getTarget();
    }

    /**
     * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
     * 
     * @return helper contenente i dati dell'impresa
     */
    protected WizardDatiImpresaHelper getSessionHelper() {
	WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
		.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
	return helper;
    }

}
