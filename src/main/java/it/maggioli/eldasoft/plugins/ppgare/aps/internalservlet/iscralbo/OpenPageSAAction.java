package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Action di gestione delle operazioni nella pagina delle stazioni appaltanti
 * del wizard d'iscrizione all'albo
 * 
 * @author Stefano.Sabbadin
 */
public class OpenPageSAAction extends AbstractOpenPageAction {
    /**
     * UID
     */
    private static final long serialVersionUID = -7527283479159496491L;

    private IBandiManager bandiManager;

    /**
     * Sentinella che, se valorizzata con l'identificativo della pagina stessa,
     * indica che si proviene dalla pagina stessa e ci si vuole ritornare in
     * seguito a dei controlli falliti; se invece non e' valorizzato, oppure e'
     * valorizzato diversamente, vuol dire che si proviene dalla normale
     * navigazione tra le pagine del wizard
     */
	@Validate(EParamValidation.GENERIC)
    private String page;

    // dati che, se popolati, dipendono dal fatto che si vuole ricaricare la
    // pagina in seguito a dei controlli falliti
	@Validate(EParamValidation.GENERIC)
    private String[] saSelezionata;

    /** Array di appoggio per la pagina JSP per indicare lo stato dei checkbox */
    private boolean[] checkSA;

    
    public void setBandiManager(IBandiManager bandiManager) {
    	this.bandiManager = bandiManager;
    }

    public String[] getSaSelezionata() {
    	return saSelezionata;
    }

    public void setSaSelezionata(String[] saSelezionata) {
    	this.saSelezionata = saSelezionata;
    }

    public boolean[] getCheckSA() {
    	return checkSA;
    }

    public void setPage(String page) {
    	this.page = page;
    }

    
    /**
     * ... 
     */
    public String openPage() {
    	WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
    		.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

    	if (iscrizioneHelper == null) {
    		// la sessione e' scaduta, occorre riconnettersi
    		this.addActionError(this.getText("Errors.sessionExpired"));
    		this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    	} else {
    		// la sessione non e' scaduta, per cui proseguo regolarmente
    		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
    						 PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_SA);

    		try {
    			LinkedHashMap<String, String> listaStazioni = this.bandiManager
    				.getElencoStazioniAppaltantiPerIscrizione(iscrizioneHelper.getIdBando());
    			this.getMaps().put(CommonSystemConstants.LISTA_STAZIONI_APPALTANTI,
    							   listaStazioni);

    			boolean[] tmpCheckSA = new boolean[listaStazioni.size()];
    			if (PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_SA.equals(this.page)) {
    				// si arriva dalla pagina stessa, quindi se la si deve
    				// ricaricare vuol dire che si e' verificato qualche errore
    				// in fase di controllo e pertanto vanno ripresentati i dati
    				// inseriti
    				Set<String> setSASelezionate = new HashSet<String>();
    				if (this.saSelezionata != null) {
    					setSASelezionate.addAll(Arrays.asList(this.saSelezionata));
    				}
    				
    				Set<String> setChiavi = (Set<String>) listaStazioni.keySet();
    				int cont = 0;
    				for (Iterator<String> iterator = setChiavi.iterator(); iterator.hasNext();) {
    					tmpCheckSA[cont++] = setSASelezionate.contains(iterator.next());
    				}
    			} else {
    				// si arriva da un'altra pagina e pertanto si presentano i
    				// dati leggendoli dalla sessione
    				Set<String> setChiavi = (Set<String>) listaStazioni.keySet();
    				int cont = 0;
    				for (Iterator<String> iterator = setChiavi.iterator(); iterator.hasNext();) {
    					tmpCheckSA[cont++] = iscrizioneHelper
    						.getStazioniAppaltanti().contains(iterator.next());
    				}
    			}
    			this.checkSA = tmpCheckSA;
    		} catch (Throwable t) {
    			ApsSystemUtils.logThrowable(t, this, "openPage");
    			ExceptionUtils.manageExceptionError(t, this);
    			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    		}
    	}	
    	return this.getTarget();
    }
    
}
