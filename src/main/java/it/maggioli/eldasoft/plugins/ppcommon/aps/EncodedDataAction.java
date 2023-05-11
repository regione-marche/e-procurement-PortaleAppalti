package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.agiletec.apsadmin.system.BaseAction;

/**
 * Classe di base per la definizione di action in cui occorre inserire l'elenco
 * delle codifiche di dati tabellati per la presentazione di combobox con
 * l'elenco valori o la gestione del set di una descrizione a partire dal suo
 * tipo/stato/codice.
 *
 * @author Stefano.Sabbadin
 */
public class EncodedDataAction extends BaseAction implements IEncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6567856112278254182L;

	private Map<String, LinkedHashMap<String, String>> maps = new HashMap<String, LinkedHashMap<String, String>>();

	@Override
	public Map<String, LinkedHashMap<String, String>> getMaps() {
		return maps;
	}

	private String target = SUCCESS;

	/**
	 * @param target the target to set
	 */
	@Override
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the target
	 */
	@Override
	public String getTarget() {
		return target;
	}
}
