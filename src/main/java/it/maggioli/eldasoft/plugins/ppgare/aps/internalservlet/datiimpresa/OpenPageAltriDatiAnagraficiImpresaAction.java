package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'apertura della pagina degli altri dati anagrafici per
 * il libero professionista (al posto della pagina dei soggetti).
 * 
 * @author Stefano.Sabbadin
 * @since 1.6
 */
public class OpenPageAltriDatiAnagraficiImpresaAction extends
		AbstractOpenPageAction implements IAltriDatiAnagraficiImpresa {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -2386014019288338412L;

	@Validate(EParamValidation.TITOLO_TECNICO)
	private String titolo;
	@Validate(EParamValidation.NOME)
	private String nome;
	@Validate(EParamValidation.COGNOME)
	private String cognome;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataNascita;
	@Validate(EParamValidation.COMUNE)
	private String comuneNascita;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaNascita;
	@Validate(EParamValidation.GENDER)
	private String sesso;
	@Validate(EParamValidation.ALBO)
	private String tipologiaAlboProf;
	@Validate(EParamValidation.NUMERO_ISCR_ALBO)
	private String numIscrizioneAlboProf;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataIscrizioneAlboProf;
	@Validate(EParamValidation.PROVINCIA)
	private String provinciaIscrizioneAlboProf;
	@Validate(EParamValidation.CASSA_PREVIDENZIALE)
	private String tipologiaCassaPrevidenza;
	@Validate(EParamValidation.NUM_CASSA_PREVIDENZIALE)
	private String numMatricolaCassaPrevidenza;

	@Override
	public String getTitolo() {
		return titolo;
	}

	@Override
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	@Override
	public String getNome() {
		return nome;
	}

	@Override
	public void setNome(String nome) {
		this.nome = nome;
		
	}

	@Override
	public String getCognome() {
		return cognome;
	}

	@Override
	public void setCognome(String cognome) {
		this.cognome = cognome; 		
	}
	
	@Override
	public String getDataNascita() {
		return dataNascita;
	}

	@Override
	public void setDataNascita(String dataNascita) {
		this.dataNascita = dataNascita;
	}

	@Override
	public String getComuneNascita() {
		return comuneNascita;
	}

	@Override
	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	@Override
	public String getProvinciaNascita() {
		return provinciaNascita;
	}

	@Override
	public void setProvinciaNascita(String provinciaNascita) {
		this.provinciaNascita = provinciaNascita;
	}

	@Override
	public String getSesso() {
		return sesso;
	}

	@Override
	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	@Override
	public String getTipologiaAlboProf() {
		return tipologiaAlboProf;
	}

	@Override
	public void setTipologiaAlboProf(String tipologiaAlboProf) {
		this.tipologiaAlboProf = tipologiaAlboProf;
	}

	@Override
	public String getNumIscrizioneAlboProf() {
		return numIscrizioneAlboProf;
	}

	@Override
	public void setNumIscrizioneAlboProf(String numIscrizioneAlboProf) {
		this.numIscrizioneAlboProf = numIscrizioneAlboProf;
	}

	@Override
	public String getDataIscrizioneAlboProf() {
		return dataIscrizioneAlboProf;
	}

	@Override
	public void setDataIscrizioneAlboProf(String dataIscrizioneAlboProf) {
		this.dataIscrizioneAlboProf = dataIscrizioneAlboProf;
	}

	@Override
	public String getProvinciaIscrizioneAlboProf() {
		return provinciaIscrizioneAlboProf;
	}

	@Override
	public void setProvinciaIscrizioneAlboProf(
			String provinciaIscrizioneAlboProf) {
		this.provinciaIscrizioneAlboProf = provinciaIscrizioneAlboProf;
	}

	@Override
	public String getTipologiaCassaPrevidenza() {
		return tipologiaCassaPrevidenza;
	}

	@Override
	public void setTipologiaCassaPrevidenza(String tipologiaCassaPrevidenza) {
		this.tipologiaCassaPrevidenza = tipologiaCassaPrevidenza;
	}

	@Override
	public String getNumMatricolaCassaPrevidenza() {
		return numMatricolaCassaPrevidenza;
	}

	@Override
	public void setNumMatricolaCassaPrevidenza(
			String numMatricolaCassaPrevidenza) {
		this.numMatricolaCassaPrevidenza = numMatricolaCassaPrevidenza;
	}

	public String openPage() {
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			// aggiorna i dati nel bean a partire da quelli presenti in sessione
			OpenPageAltriDatiAnagraficiImpresaAction
					.synchronizeAltriDatiAnagraficiImpresa(
							helper.getAltriDatiAnagraficiImpresa(), this);

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_ALTRI_DATI_ANAGR);
		}
		return this.getTarget();
	}

	public String openPageAfterError() {
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_ALTRI_DATI_ANAGR);
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

	/**
	 * Sincronizza i dati tra l'oggetto sorgente e l'oggetto di destinazione
	 * 
	 * @param from
	 *            oggetto sorgente
	 * @param to
	 *            oggetto destinazione
	 */
	public static void synchronizeAltriDatiAnagraficiImpresa(
			IAltriDatiAnagraficiImpresa from, IAltriDatiAnagraficiImpresa to) {
		to.setTitolo(from.getTitolo());
		to.setNome(from.getNome());
		to.setCognome(from.getCognome());
		to.setDataNascita(from.getDataNascita());
		to.setComuneNascita(from.getComuneNascita());
		to.setProvinciaNascita(from.getProvinciaNascita());
		to.setSesso(from.getSesso());
		to.setTipologiaAlboProf(from.getTipologiaAlboProf());
		to.setNumIscrizioneAlboProf(from.getNumIscrizioneAlboProf());
		to.setDataIscrizioneAlboProf(from.getDataIscrizioneAlboProf());
		to.setProvinciaIscrizioneAlboProf(from.getProvinciaIscrizioneAlboProf());
		to.setTipologiaCassaPrevidenza(from.getTipologiaCassaPrevidenza());
		to.setNumMatricolaCassaPrevidenza(from.getNumMatricolaCassaPrevidenza());
	}

	

}
