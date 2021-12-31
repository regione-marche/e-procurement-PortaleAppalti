package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.util.List;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;

/**
 * Interfaccia per la gestione, nei wizard presenti in sessione, dei dati
 * relativi alla presentazione di domande/richieste tramite raggruppamento con
 * RTI e relative gestioni.
 * 
 * @author Stefano.Sabbadin
 */
public interface IRaggruppamenti {

	boolean isRti();

	void setRti(boolean rti);

	String getDenominazioneRTI();

	void setDenominazioneRTI(String denominazioneRTI);

	Double getQuotaRTI();

	void setQuotaRTI(Double quotaRTI);
	
	List<IComponente> getComponenti();
	
	WizardDatiImpresaHelper getImpresa();

	IDatiPrincipaliImpresa getDatiPrincipaliImpresa();

	/**
	 * Indica se nel wizard vanno effettuati i controlli relativi alle quote di
	 * partecipazione per le RTI.
	 * 
	 * @return true se i controlli vanno effettuati, false altrimenti
	 */
	boolean checkQuota();

}
