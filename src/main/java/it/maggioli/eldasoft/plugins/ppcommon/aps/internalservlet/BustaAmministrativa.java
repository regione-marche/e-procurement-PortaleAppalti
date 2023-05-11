package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

/**
 * ... 
 */
public class BustaAmministrativa extends BustaDocumenti {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 789085280135324363L;

	/**
	 * costruttore
	 * @throws Throwable 
	 */
	public BustaAmministrativa(GestioneBuste buste) throws Throwable {
		super(buste,
			  ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA,
			  BUSTA_AMMINISTRATIVA);
	}

}
