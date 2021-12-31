package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste;

import java.util.List;
import java.util.Map;

import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.eldasoft.www.sil.WSAste.DettaglioFaseAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioRilancioType;
import it.eldasoft.www.sil.WSAste.VoceDettaglioAstaType;


/**
 * Interfaccia base per il servizio di gestione delle aste.
 * 
 * @version 1.0
 * @author 
 */
public interface IAsteManager {

	/**
	 * @throws ApsException 
	 */
	public List<DettaglioClassificaType> getClassifica(
		    java.lang.Integer tipoClassifica,
		    java.lang.String codice,
		    java.lang.String codiceLotto,
		    java.lang.String username,
		    java.lang.String faseAsta) throws ApsException;
	
	/**
	 * @throws ApsException 
	 */
	public List<DettaglioFaseAstaType> getFasiAsta(
			java.lang.String codice) throws ApsException;
	
	/**
	 * @throws ApsException 
	 */
	public DettaglioAstaType getDettaglioAsta(
			java.lang.String codice,
			java.lang.String username) throws ApsException;

	/**
	 * @throws ApsException 
	 */
	public List<DettaglioRilancioType> getElencoRilanci(
			String codice, 
			String codiceLotto, 
			String username,
			String numeroFase) throws ApsException;

	/**
	 * @throws ApsException 
	 */
	public Long insertRilancioAsta(
			String codice,
			String codiceLotto, 
			String username, 
			Double offerta,
			List<VoceDettaglioAstaType> prezziUnitari) throws ApsException;

	/**
	 * @throws ApsException 
	 */
	public Map<String, String> getElencoTerminiAsta() throws ApsException;
	
	/**
	 * @throws ApsException 
	 */
	public Map<String, String> getElencoTipiClassifica() throws ApsException;

	/**
	 * @throws ApsException 
	 */
	public List<VoceDettaglioAstaType> getPrezziUnitariPrimoRilancio(
			String codice,
			String codiceLotto, 
			String username) throws ApsException;

	/**
	 * @throws ApsException 
	 */
	public List<VoceDettaglioAstaType> getPrezziUnitariRilancio(
			String codice,
			String codiceLotto, 
			String username,
			Long idRilancio) throws ApsException;
	
}
