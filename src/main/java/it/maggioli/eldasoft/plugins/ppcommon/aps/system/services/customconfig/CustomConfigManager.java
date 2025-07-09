package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio di gestione delle personalizzazioni clienti definite nel sistema.
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class CustomConfigManager extends AbstractService implements
		ICustomConfigManager {

	public static final String FEATURE_ACTIVE_FUNCTION = "ACT";

	public static final String FEATURE_MANDATORY = "MAN";

	public static final String FEATURE_VISIBLE = "VIS";

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 4047468779643712982L;

	/** Hash degli attributi visibili per ogni oggetto. */
	private Map<String, Set<String>> _visible = new HashMap<String, Set<String>>();
	/** Hash degli attributi obbligatori per ogni oggetto. */
	private Map<String, Set<String>> _mandatory = new HashMap<String, Set<String>>();
	/** Hash delle funzioni attive per ogni oggetto. */
	private Map<String, Set<String>> _activeFuntion = new HashMap<String, Set<String>>();

	/**
	 * Hash di tutti gli elementi per cui &egrave; gestita la
	 * configurabilit&agrave; della visibilit&agrave;.
	 */
	private Map<String, Set<String>> _allVisConfigurations = new HashMap<String, Set<String>>();

	/**
	 * Hash di tutti gli elementi per cui &egrave; gestita la
	 * configurabilit&agrave; dell'obbligatoriet&agrave;.
	 */
	private Map<String, Set<String>> _allManConfigurations = new HashMap<String, Set<String>>();

	/**
	 * Hash di tutti gli elementi per cui &egrave; gestita la
	 * configurabilit&agrave; dell'attivazione di una funzione.
	 */
	private Map<String, Set<String>> _allActConfigurations = new HashMap<String, Set<String>>();

	private ICustomConfigDAO _customConfigDAO;

	/**
	 * @param configDAO
	 *            the _customConfigDAO to set
	 */
	public void setCustomConfigDAO(ICustomConfigDAO configDAO) {
		_customConfigDAO = configDAO;
	}

	@Override
	public void init() throws Exception {
		this.loadCustomConfigs();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");

	}

	/**
	 * Caricamento da db del catalogo delle customizzazioni.
	 * 
	 * @throws ApsSystemException
	 *             In caso di errori di lettura da db.
	 */
	private void loadCustomConfigs() throws ApsSystemException {
		try {
			this._visible = new HashMap<String, Set<String>>();
			this._mandatory = new HashMap<String, Set<String>>();
			this._activeFuntion = new HashMap<String, Set<String>>();
			this._allVisConfigurations = new HashMap<String, Set<String>>();
			this._allManConfigurations = new HashMap<String, Set<String>>();
			this._allActConfigurations = new HashMap<String, Set<String>>();

			List<CustomConfig> lista = this._customConfigDAO
					.loadCustomConfigs();

			Iterator<CustomConfig> iter = lista.iterator();
			while (iter.hasNext()) {
				CustomConfig config = iter.next();

				if (FEATURE_VISIBLE.equals(config.getFeature())) {
					// si aggiunge la configurazione all'elenco di tutte le
					// configurazioni presenti
					Set<String> setAllConfigurations = this._allVisConfigurations
							.get(config.getObjectId());
					if (setAllConfigurations == null) {
						setAllConfigurations = new HashSet<String>();
						this._allVisConfigurations.put(config.getObjectId(),
								setAllConfigurations);
					}
					setAllConfigurations.add(config.getAttrib());

					// si aggiunge, se visibile al set degli elementi visibili
					if (config.isConfigValue()) {
						Set<String> setVisible = this._visible.get(config
								.getObjectId());
						if (setVisible == null) {
							setVisible = new HashSet<String>();
							this._visible.put(config.getObjectId(), setVisible);
						}
						setVisible.add(config.getAttrib());
					}
				}

				if (FEATURE_MANDATORY.equals(config.getFeature())) {
					// si aggiunge la configurazione all'elenco di tutte le
					// configurazioni presenti
					Set<String> setAllConfigurations = this._allManConfigurations
							.get(config.getObjectId());
					if (setAllConfigurations == null) {
						setAllConfigurations = new HashSet<String>();
						this._allManConfigurations.put(config.getObjectId(),
								setAllConfigurations);
					}
					setAllConfigurations.add(config.getAttrib());

					// si aggiunge, se obbligatorio al set degli elementi
					// obbligatori
					if (config.isConfigValue()) {
						Set<String> setMandatory = this._mandatory.get(config
								.getObjectId());
						if (setMandatory == null) {
							setMandatory = new HashSet<String>();
							this._mandatory.put(config.getObjectId(),
									setMandatory);
						}
						setMandatory.add(config.getAttrib());
					}
				}

				if (FEATURE_ACTIVE_FUNCTION.equals(config.getFeature())) {
					// si aggiunge la configurazione all'elenco di tutte le
					// configurazioni presenti
					Set<String> setAllConfigurations = this._allActConfigurations
							.get(config.getObjectId());
					if (setAllConfigurations == null) {
						setAllConfigurations = new HashSet<String>();
						this._allActConfigurations.put(config.getObjectId(),
								setAllConfigurations);
					}
					setAllConfigurations.add(config.getAttrib());

					// si aggiunge, se obbligatorio al set degli elementi
					// obbligatori
					if (config.isConfigValue()) {
						Set<String> setActiveFunction = this._activeFuntion
								.get(config.getObjectId());
						if (setActiveFunction == null) {
							setActiveFunction = new HashSet<String>();
							this._activeFuntion.put(config.getObjectId(),
									setActiveFunction);
						}
						setActiveFunction.add(config.getAttrib());
					}
				}
			}
		} catch (Throwable t) {
			throw new ApsSystemException(
					"Errore durante la lettura delle personalizzazioni clienti in ppcommon_customizations",
					t);
		}
	}

	@Override
	public boolean isMandatory(String objectId, String attrib) throws Exception {
		this.checkConfiguration(objectId, attrib, FEATURE_MANDATORY);
		// calcolo del valore impostato alla configurazione
		return getConfigurationValue(objectId, attrib, this._mandatory);
	}

	@Override
	public boolean isVisible(String objectId, String attrib) throws Exception {
		this.checkConfiguration(objectId, attrib, FEATURE_VISIBLE);
		return getConfigurationValue(objectId, attrib, this._visible);
	}

	@Override
	public boolean isActiveFunction(String objectId, String attrib)
			throws Exception {
		this.checkConfiguration(objectId, attrib, FEATURE_ACTIVE_FUNCTION);
		return getConfigurationValue(objectId, attrib, this._activeFuntion);
	}

	@Override
	public boolean isActiveFunction(String objectId, String attrib, boolean defValue) {
		boolean value = defValue;
		try {
			this.checkConfiguration(objectId, attrib, FEATURE_ACTIVE_FUNCTION);
			value = getConfigurationValue(objectId, attrib, this._activeFuntion);
		} catch (Exception e) {
			ApsSystemUtils.getLogger().warn("Non e' stato possibile leggere la configurazione ACT per l'objectId " + objectId + " feature " + attrib + ". Impostato default " + defValue);
			value = defValue;
		}
		return value;
	}
	
	/**
	 * Verifica l'esistenza della configurazione basata sui dati di input
	 * (chiave).
	 * 
	 * @param objectId
	 *            identificativo oggetto
	 * @param attrib
	 *            attributo
	 * @param feature
	 *            caratteristica da testare
	 * @throws Exception
	 *             eccezione ritornata se la configurazione non esiste in base
	 *             dati
	 */
	private void checkConfiguration(String objectId, String attrib,
			String feature) throws Exception {
		Map<String, Set<String>> mappa = null;
		if (FEATURE_VISIBLE.equals(feature)) {
			mappa = this._allVisConfigurations;
		} else if (FEATURE_MANDATORY.equals(feature)) {
			mappa = this._allManConfigurations;
		} else if (FEATURE_ACTIVE_FUNCTION.equals(feature)) {
			mappa = this._allActConfigurations;
		}
		if (mappa == null)
			throw new Exception("Feature " + feature
					+ " non gestita nella configurazione");

		// verifica della configurazione indicata se e' una di quelle previste
		Set<String> set = mappa.get(objectId);
		if (set == null)
			throw new Exception("ObjectId " + objectId
					+ " non presente in nessuna configurazione con feature "
					+ feature);
		if (!set.contains(attrib))
			throw new Exception("Attrib " + attrib
					+ " non presente nella configurazione per l'objectId "
					+ objectId + " con feature " + feature);
	}

	/**
	 * Calcola il valore di una configurazione.
	 * 
	 * @param objectId
	 *            identificativo oggetto
	 * @param attrib
	 *            attributo
	 * @param map
	 *            hashmap di oggetti sul quale verificare se l'attributo e'
	 *            settato
	 * @return true se la configurazione e' presente, false altrimenti
	 */
	private boolean getConfigurationValue(String objectId, String attrib,
			Map<String, Set<String>> map) {
		boolean esito = false;
		Set<String> set = map.get(objectId);
		if (set != null)
			esito = set.contains(attrib);
		return esito;
	}

	@Override
	public List<CustomConfig> getCustomConfigs() throws ApsSystemException {
		List<CustomConfig> lista = null;
		try {
			lista = this._customConfigDAO.loadCustomConfigs();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getCustomConfigs");
			throw new ApsSystemException(
					"Errore durante la lettura delle personalizzazioni clienti in ppcommon_customizations",
					t);
		}
		return lista;
	}

	@Override
	public void updateCustomConfigs(List<CustomConfig> configs)
			throws ApsSystemException {
		try {
			this._customConfigDAO.updateCustomConfigs(configs);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateCustomConfigs");
			throw new ApsSystemException(
					"Errore durante l'aggiornamento delle personalizzazioni clienti in ppcommon_customizations",
					t);
		}
		this.loadCustomConfigs();
	}

}
