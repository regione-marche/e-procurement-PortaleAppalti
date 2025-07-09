package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc;

import java.util.List;

/**
 * tracciato dei dati restituiti dal servizio Michelangelo di SACE
 * Vedi classe MichelangeloCompanyInfo (Company)
 */
public class MichelangeloCompany {
	
	public MichelangeloCompanyAddress Address;
	//public List<MichelangeloCompanyAddress> Address;	// KO
	public List<MichelangeloCompanySource> Sources;
	public MichelangeloCompanyGeneralData GeneralData;
	
}
