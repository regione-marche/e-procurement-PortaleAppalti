package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.safilter;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * Esegue l'impostazioni della stazione appaltante da utilizzare per tutta la 
 * web application.
 * 
 * @author 
 * @since 
 */
public class ChooseStazioneAppaltanteAction extends EncodedDataAction {	
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 1L;
	
	private IBandiManager bandiManager;
	
	private String stazioneAppaltante;
	
	private HashMap<String, List<String>> listaStazioniAppaltanti;
		
	/**
	 * @param bandiManager the bandiManager to set
	 */
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	/**
	 * @return the stazioneAppaltante
	 */
	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	/**
	 * @param stazioneAppaltante the stazioneAppaltante to set
	 */
	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}

	/**
	 * @return the listaStazioniAppaltanti
	 */
	public HashMap<String, List<String>> getListaStazioniAppaltanti() {
		return listaStazioniAppaltanti;
	}

	public String open() {
		String target = SUCCESS; 
		try {
			this.stazioneAppaltante = this.getCodiceStazioneAppaltante();
		
			this.listaStazioniAppaltanti = new LinkedHashMap<String, List<String>>();
		
			LinkedHashMap<String, String> listaSa = this.getMaps()
				.get(InterceptorEncodedData.LISTA_STAZIONI_APPALTANTI);
			
			List<DettaglioStazioneAppaltanteType> elenco = this.bandiManager
				.getStazioniAppaltanti();
			
			for(int i = 0; i < elenco.size(); i++) {				
				String sa = listaSa.get( elenco.get(i).getCodice() );
				if(sa != null) {				
					List<String> values = new ArrayList<String>();
					values.add(elenco.get(i).getCodiceFiscale());
					values.add(elenco.get(i).getDenominazione());
					this.listaStazioniAppaltanti.put(elenco.get(i).getCodice(), values);
				}
			}

		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "open");
			this.addActionError(this.getText("Errors.noAuthorities"));
			target = ERROR;
		}
		return target;
	}
	
	public String confirm() {		
		if(StringUtils.isEmpty(this.stazioneAppaltante)) {
			this.stazioneAppaltante = "";	// imposta il filtro a "Tutte le stazioni appaltanti"
		}
		this.setCodiceStazioneAppaltante(this.stazioneAppaltante);
		return SUCCESS;
	}

}
