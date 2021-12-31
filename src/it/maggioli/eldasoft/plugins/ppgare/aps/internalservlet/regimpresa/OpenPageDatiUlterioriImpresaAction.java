package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import org.apache.commons.lang.StringUtils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di gestione dell'apertura della pagina dei dati ulteriori dell'impresa
 * nel wizard di registrazione impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class OpenPageDatiUlterioriImpresaAction
	extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.OpenPageDatiUlterioriImpresaAction 
{
    /**
     * UID
     */
    private static final long serialVersionUID = 1993519356135477685L;
    
	@Override
	public String openPage() {
		// gestione standard dell'apertura...
		this.setTarget( super.openPage() );
		
		// ...aggiungi dei controlli aggiuntivi per la registrazione... 
		WizardDatiImpresaHelper helper = getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente

//			// In registrazione, nel caso di libero professionista o societa' 
//			// di professionisti (TIPIMP.IMPR <= 5) impostare ISCRCCIAA a "No"
//			boolean liberoProfessionista = this.getMaps()
//				.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE)
//				.containsKey(helper.getDatiPrincipaliImpresa().getTipoImpresa());
//
//			if(liberoProfessionista && 
//               StringUtils.isEmpty(helper.getDatiUlterioriImpresa().getIscrittoCCIAA()) ) {
//				
//				helper.getDatiUlterioriImpresa().setIscrittoCCIAA("0");  // 0=No
				
			if( StringUtils.isEmpty(helper.getDatiUlterioriImpresa().getIscrittoCCIAA()) ) {        	  
        	  	helper.getDatiUlterioriImpresa().setIscrittoCCIAA("1");  // 1=Si
			}
          
			OpenPageDatiUlterioriImpresaAction.synchronizeDatiUlterioriImpresa(
					helper.getDatiUlterioriImpresa(), this);
		
			this.session.put(
					PortGareSystemConstants.SESSION_ID_PAGINA,
			 	    PortGareSystemConstants.WIZARD_AGGIMPRESA_PAGINA_DATI_ULT_IMPRESA);
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
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		return helper;
    }
}
