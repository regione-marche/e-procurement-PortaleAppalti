package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;

/**
 * Bean di memorizzazione di un singolo firmatario di un'impresa
 * 
 * @author ... 
 * @since  ...
 */
public class SoggettoFirmatarioImpresaHelper extends SoggettoImpresaHelper {	
    /**
	 * UID
	 */
	private static final long serialVersionUID = 8447250986536392345L;
	
	private String partitaIvaImpresa;
	private String codiceFiscaleImpresa;
	
	
    public String getPartitaIvaImpresa() {
		return partitaIvaImpresa;
	}

	public void setPartitaIvaImpresa(String partitaIvaImpresa) {
		this.partitaIvaImpresa = partitaIvaImpresa;
	}

	public String getCodiceFiscaleImpresa() {
		return codiceFiscaleImpresa;
	}

	public void setCodiceFiscaleImpresa(String codiceFiscaleImpresa) {
		this.codiceFiscaleImpresa = codiceFiscaleImpresa;
	}

	/**
	 * costruttore
	 */
	public SoggettoFirmatarioImpresaHelper() {
		this.partitaIvaImpresa = null;
		this.codiceFiscaleImpresa = null;
	}
	
	/**
	 * costruttore
	 */
	public SoggettoFirmatarioImpresaHelper(FirmatarioType firmatario) {
		super(firmatario);
		this.partitaIvaImpresa = firmatario.getPartitaIVAImpresa();
		this.codiceFiscaleImpresa = firmatario.getCodiceFiscaleImpresa();
	}
	
	/**
	 * costruttore
	 */
	public SoggettoFirmatarioImpresaHelper(ISoggettoImpresa firmatario) {
		super(firmatario);
	}

}
