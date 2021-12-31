/**
 * PortaleAlice_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.maggioli.eldasoft.plugins.ppcommon.ws;

public interface PortaleAlice_Service extends javax.xml.rpc.Service {
    public java.lang.String getPortaleAliceSOAPAddress();

    public it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType getPortaleAliceSOAP() throws javax.xml.rpc.ServiceException;

    public it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType getPortaleAliceSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
