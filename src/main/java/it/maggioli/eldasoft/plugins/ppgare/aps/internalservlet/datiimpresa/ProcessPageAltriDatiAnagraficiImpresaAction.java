package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.aps.system.ApsSystemUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;

/**
 * Action di gestione delle operazioni nella pagina degli altri dati anagrafici
 * per il libero professionista.
 * 
 * @author Stefano.Sabbadin
 * @since 1.6
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.MODIFICA_IMPRESA, EFlussiAccessiDistinti.REGISTRAZIONE_IMPRESA,
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class ProcessPageAltriDatiAnagraficiImpresaAction extends
		AbstractProcessPageAction implements IAltriDatiAnagraficiImpresa {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -7404328854376923205L;
	private CustomConfigManager customConfigManager;

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
	
	private boolean obblIscrizione;

	public void setCustomConfigManager(CustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}
	
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
	
	/**
	 * @return the obblIscrizione
	 */
	public boolean isObblIscrizione() {
		return obblIscrizione;
	}

	/**
	 * @param obblIscrizione the obblIscrizione to set
	 */
	public void setObblIscrizione(boolean obblIscrizione) {
		this.obblIscrizione = obblIscrizione;
	}

	@Override
	public void validate() {
		WizardDatiImpresaHelper helper = getSessionHelper();
		if (helper != null && !helper.isLiberoProfessionista() && StringUtils.isEmpty(sesso))
			addFieldError("sesso", getText("localstrings.requiredstring"));
	}

	@Override
	public String next() {
		String target = SUCCESS;

		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// aggiorna i dati in sessione
			OpenPageAltriDatiAnagraficiImpresaAction
					.synchronizeAltriDatiAnagraficiImpresa(this,
							helper.getAltriDatiAnagraficiImpresa());
		}
		
		try {
			if(!customConfigManager.isVisible("IMPRESA-DATIULT", "STEP")){

				target = "successNoDatiUlteriori";
			}
		}catch (Exception e) {
			// Configurazione sbagliata
			ApsSystemUtils.logThrowable(e, this, "next",
					"Errore durante la ricerca delle proprietà di visualizzazione dello step dati ulteriori");
		}
		
		return target;
	}

	@Override
	public String back(){
		String target = "back";
		try {
			if(!customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP")){
				target = "backNoIndirizzi";
			}
		}catch (Exception e) {
			// Configurazione sbagliata
			ApsSystemUtils.logThrowable(e, this, "next",
					"Errore durante la ricerca delle proprietà di visualizzazione dello step ulteriori dati impresa");
		}
		return target;
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
