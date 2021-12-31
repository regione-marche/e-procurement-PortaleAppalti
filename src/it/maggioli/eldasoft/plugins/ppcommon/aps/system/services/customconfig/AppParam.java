package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import java.io.Serializable;

/**
 * Rappresentazione di una configurazione dell'applicativo. Oltre ai campi
 * presenti nella tabella di origine, &grave; presente un flag per indicare la
 * configurazione risultante &egrave; ottenuta come risorsa JNDI oppure no.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public class AppParam implements Serializable {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 3097707165059705335L;

	/** Nome del parametro. */
	private String name;
	/** Descrizione. */
	private String description;
	/** Tipologia: I=Intero S=Stringa, L=Link, B=Booleano), */
	private String type;
	/** Valore del parametro. */
	private String value;
	/** Valore del parametro tipizzato. */
	private Object objectValue;
	/** True se il parametro è letto mediante JNDI, false altrimenti. */
	private boolean jndi;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the objectValue
	 */
	public Object getObjectValue() {
		return objectValue;
	}

	/**
	 * @param objectValue
	 *            the objectValue to set
	 */
	public void setObjectValue(Object objectValue) {
		this.objectValue = objectValue;
	}

	/**
	 * @return the jndi
	 */
	public boolean isJndi() {
		return jndi;
	}

	/**
	 * @param jndi
	 *            the jndi to set
	 */
	public void setJndi(boolean jndi) {
		this.jndi = jndi;
	}

}
