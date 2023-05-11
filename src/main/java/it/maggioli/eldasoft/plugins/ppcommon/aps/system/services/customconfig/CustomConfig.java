package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.io.Serializable;

/**
 * Rappresentazione di una customizzazione / personalizzazione cliente
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class CustomConfig implements Serializable {

    /**
     * UID
     */
    private static final long serialVersionUID = 2274150065005270218L;

    /**
     * @return the _objectId
     */
    public String getObjectId() {
	return _objectId;
    }

    /**
     * @param id
     *            the _objectId to set
     */
    public void setObjectId(String id) {
	_objectId = id;
    }

    /**
     * @return the _attrib
     */
    public String getAttrib() {
	return _attrib;
    }

    /**
     * @param _attrib
     *            the _attrib to set
     */
    public void setAttrib(String _attrib) {
	this._attrib = _attrib;
    }

    /**
     * @return the _feature
     */
    public String getFeature() {
	return _feature;
    }

    /**
     * @param _feature
     *            the _feature to set
     */
    public void setFeature(String _feature) {
	this._feature = _feature;
    }

    /**
     * @return the _configValue
     */
    public boolean isConfigValue() {
	return _configValue;
    }

    /**
     * @param value
     *            the _configValue to set
     */
    public void setConfigValue(boolean value) {
	_configValue = value;
    }

    /**
     * Identificativo dell'oggetto a cui viene applicata una configurazione ad
     * un suo attributo
     */
    public String _objectId;

    /** Attributo dell'oggetto da configurare */
    public String _attrib;

    /** Caratteristica da configurare */
    public String _feature;

    /** Valore configurato */
    public boolean _configValue = false;
}
