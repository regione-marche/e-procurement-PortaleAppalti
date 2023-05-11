package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import com.agiletec.aps.system.exception.ApsException;

import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.sil.WSStipule.DocumentazioneStipulaContrattiType;
import it.eldasoft.www.sil.WSStipule.ElencoStipuleContrattiOutType;
import it.eldasoft.www.sil.WSStipule.StipulaContrattoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;

/**
 * Interfaccia base per il servizio di gestione delle stipule
 * 
 * @version 1.0
 * @author Michele.dINapoli
 */
public interface IStipuleManager {

	public ElencoStipuleContrattiOutType searchStipuleContratti(
			String codiceStipula, String oggetto, Integer stato,
			String stazioneAppaltante, String usernome,int indicePrimoRecord, int maxNumRecord)
			throws ApsException;

	public StipulaContrattoType dettaglioStipulacontratto(String codiceStipula, String username,  boolean pubblicata)
			throws ApsException;
	
	public DocumentazioneStipulaContrattiType[] getDocumentiRichiestiStipulaContratto(String idStipula)
		throws ApsException;

	public AllegatoComunicazioneType getAllegatoStipula(String docId) throws ApsException;
	
	public void deleteAllegatoStipula(String docId) throws ApsException;

	public void insertAllegatoStipula(String nomeFile, Long idDocStipula,
			byte[] allegato, String note, DocumentiAllegatiFirmaBean checkFirma) throws ApsException;
	

	public void updateStipula(String codiceStipula, String username) throws ApsException;
	
}