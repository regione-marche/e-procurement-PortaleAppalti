package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;


public class BustaPrequalifica extends BustaDocumenti {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6870973896789144076L;

	/**
	 * costruttore
	 * @throws Throwable 
	 */
	public BustaPrequalifica(GestioneBuste buste) throws Throwable {
		super(buste,
			  ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA,
			  BUSTA_PRE_QUALIFICA);
	}	
	
}
