/**
 * PortaleAlice_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.maggioli.eldasoft.plugins.ppcommon.ws;

public class PortaleAlice_ServiceLocator extends org.apache.axis.client.Service implements it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_Service {

    public PortaleAlice_ServiceLocator() {
    }


    public PortaleAlice_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PortaleAlice_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PortaleAliceSOAP
    private java.lang.String PortaleAliceSOAP_address = "http://www.eldasoft.it/";

    public java.lang.String getPortaleAliceSOAPAddress() {
        return PortaleAliceSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PortaleAliceSOAPWSDDServiceName = "PortaleAliceSOAP";

    public java.lang.String getPortaleAliceSOAPWSDDServiceName() {
        return PortaleAliceSOAPWSDDServiceName;
    }

    public void setPortaleAliceSOAPWSDDServiceName(java.lang.String name) {
        PortaleAliceSOAPWSDDServiceName = name;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType getPortaleAliceSOAP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PortaleAliceSOAP_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPortaleAliceSOAP(endpoint);
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType getPortaleAliceSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAliceSOAPStub _stub = new it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAliceSOAPStub(portAddress, this);
            _stub.setPortName(getPortaleAliceSOAPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPortaleAliceSOAPEndpointAddress(java.lang.String address) {
        PortaleAliceSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAliceSOAPStub _stub = new it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAliceSOAPStub(new java.net.URL(PortaleAliceSOAP_address), this);
                _stub.setPortName(getPortaleAliceSOAPWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PortaleAliceSOAP".equals(inputPortName)) {
            return getPortaleAliceSOAP();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "PortaleAlice");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "PortaleAliceSOAP"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PortaleAliceSOAP".equals(portName)) {
            setPortaleAliceSOAPEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
