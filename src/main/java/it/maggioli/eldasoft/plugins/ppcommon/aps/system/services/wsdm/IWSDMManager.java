package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm;

import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

import com.agiletec.aps.system.exception.ApsException;

/**
 * Interfaccia per la gestione dell'interfacciamento con il servizio di
 * protocollazione.
 * 
 * @version 1.11.2
 * @author Stefano.Sabbadin
 */
public interface IWSDMManager {
	
	/**
	 * identificativi dei tipi di sistemi di protocollazione 
	 */
	public static final String CODICE_SISTEMA_TITULUS 			= "TITULUS";
	public static final String CODICE_SISTEMA_ARCHIFLOW 		= "ARCHIFLOW";
	public static final String CODICE_SISTEMA_ARCHIFLOWFA 		= "ARCHIFLOWFA";
	public static final String CODICE_SISTEMA_JIRIDE 			= "JIRIDE";
	public static final String CODICE_SISTEMA_JPROTOCOL 		= "JPROTOCOL";
	public static final String CODICE_SISTEMA_FOLIUM 			= "FOLIUM";
	public static final String CODICE_SISTEMA_JDOC				= "JDOC";
	public static final String CODICE_SISTEMA_DOCER				= "DOCER";
	public static final String CODICE_SISTEMA_ENGINEERINGDOC	= "ENGINEERINGDOC";
	public static final String CODICE_SISTEMA_LAPIS				= "LAPISOPERA";

	/**
	 * ...
	 */
    public static final String PROT_AUTOMATICA_PORTALE_APPALTI 	= "PROT. AUTOMATICA PORTALE APPALTI"; 

    /**
	 * ...
	 */
	WSDMLoginAttrType getLoginAttr();
	
	/**
	 * ...
	 */
	WSDMProtocolloDocumentoType inserisciProtocollo(
			WSDMLoginAttrType loginAttr, 
			WSDMProtocolloDocumentoInType protocolloIn) throws ApsException;

}

