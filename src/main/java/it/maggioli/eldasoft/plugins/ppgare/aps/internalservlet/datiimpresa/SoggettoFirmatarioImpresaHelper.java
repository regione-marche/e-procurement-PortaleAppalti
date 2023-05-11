package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

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

	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIvaImpresa;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscaleImpresa;
	private String idFiscaleEsteroImpresa;				// manca nell'XML del firmatario !!!
	
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
	
//	public String getIdFiscaleEsteroImpresa() {
//		return codiceFiscaleImpresa;
//	}
//
//	public void setIdFiscaleEsteroImpresa(String idFiscaleEsteroImpresa) {
//		this.codiceFiscaleImpresa = idFiscaleEsteroImpresa;
//	}

	/**
	 * costruttore
	 */
	public SoggettoFirmatarioImpresaHelper() {
		this.partitaIvaImpresa = null;
		this.codiceFiscaleImpresa = null;
		this.idFiscaleEsteroImpresa = null;
	}
	
	/**
	 * costruttore
	 */
	public SoggettoFirmatarioImpresaHelper(FirmatarioType firmatario) {
		super(firmatario);
		this.partitaIvaImpresa = firmatario.getPartitaIVAImpresa();
		this.codiceFiscaleImpresa = firmatario.getCodiceFiscaleImpresa();
//		this.idFiscaleEsteroImpresa = firmatario.get...;		// manca nell'XML del firmatario !!!
	}
	
	/**
	 * costruttore
	 */
	public SoggettoFirmatarioImpresaHelper(ISoggettoImpresa firmatario) {
		super(firmatario);
	}

}
