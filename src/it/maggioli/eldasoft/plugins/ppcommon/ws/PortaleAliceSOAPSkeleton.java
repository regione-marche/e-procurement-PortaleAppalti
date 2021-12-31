/**
 * PortaleAliceSOAPSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.maggioli.eldasoft.plugins.ppcommon.ws;

public class PortaleAliceSOAPSkeleton implements it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType, org.apache.axis.wsdl.Skeleton {
    private it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "email"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "pec"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "denominazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "codFiscalePerControlli"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "partitaIVAPerControlli"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("inserisciImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "InserisciImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/InserisciImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("inserisciImpresa") == null) {
            _myOperations.put("inserisciImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("inserisciImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("eliminaImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EliminaImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/EliminaImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("eliminaImpresa") == null) {
            _myOperations.put("eliminaImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("eliminaImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "email"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "pec"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "denominazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "codFiscalePerControlli"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "partitaIVAPerControlli"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("aggiornaImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "AggiornaImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/AggiornaImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("aggiornaImpresa") == null) {
            _myOperations.put("aggiornaImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("aggiornaImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "denominazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("attivaImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "AttivaImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/AttivaImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("attivaImpresa") == null) {
            _myOperations.put("attivaImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("attivaImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "email"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("inviaMailAttivazioneImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "InviaMailAttivazioneImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/InviaMailAttivazioneImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("inviaMailAttivazioneImpresa") == null) {
            _myOperations.put("inviaMailAttivazioneImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("inviaMailAttivazioneImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getUtenteDelegatoImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "RisultatoStringaOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "GetUtenteDelegatoImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/GetUtenteDelegatoImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getUtenteDelegatoImpresa") == null) {
            _myOperations.put("getUtenteDelegatoImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getUtenteDelegatoImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "delegato"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("aggiornaUtenteDelegatoImpresa", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "AggiornaUtenteDelegatoImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/AggiornaUtenteDelegatoImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("aggiornaUtenteDelegatoImpresa") == null) {
            _myOperations.put("aggiornaUtenteDelegatoImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("aggiornaUtenteDelegatoImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("esisteImpresa", _params, new javax.xml.namespace.QName("", "parameters"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsisteImpresa"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/EsisteImpresa");
        _myOperationsList.add(_oper);
        if (_myOperations.get("esisteImpresa") == null) {
            _myOperations.put("esisteImpresa", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("esisteImpresa")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "host"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "porta"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.Integer.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "protocollo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "ProtocolloMail"), it.maggioli.eldasoft.plugins.ppcommon.ws.ProtocolloMail.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "timeout"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.Integer.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "debug"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "username"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "mail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("sincronizzaConfigurazioneMail", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "SincronizzaConfigurazioneMail"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/SincronizzaConfigurazioneMail");
        _myOperationsList.add(_oper);
        if (_myOperations.get("sincronizzaConfigurazioneMail") == null) {
            _myOperations.put("sincronizzaConfigurazioneMail", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("sincronizzaConfigurazioneMail")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "anno"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "codFiscaleStazAppaltante"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datasetCompresso"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("uploadDatasetAppalti", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "UploadDatasetAppalti"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/UploadDatasetAppalti");
        _myOperationsList.add(_oper);
        if (_myOperations.get("uploadDatasetAppalti") == null) {
            _myOperations.put("uploadDatasetAppalti", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("uploadDatasetAppalti")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("sincronizzaUnitaMisura", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "SincronizzaUnitaMisura"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/SincronizzaUnitaMisura");
        _myOperationsList.add(_oper);
        if (_myOperations.get("sincronizzaUnitaMisura") == null) {
            _myOperations.put("sincronizzaUnitaMisura", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("sincronizzaUnitaMisura")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "datasetCompresso"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("uploadRssBandi", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "EsitoOutType"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/PortaleAlice/", "UploadRssBandi"));
        _oper.setSoapAction("http://www.eldasoft.it/PortaleAlice/UploadRssBandi");
        _myOperationsList.add(_oper);
        if (_myOperations.get("uploadRssBandi") == null) {
            _myOperations.put("uploadRssBandi", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("uploadRssBandi")).add(_oper);
    }

    public PortaleAliceSOAPSkeleton() {
        this.impl = new it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAliceSOAPImpl();
    }

    public PortaleAliceSOAPSkeleton(it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType impl) {
        this.impl = impl;
    }
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType inserisciImpresa(java.lang.String username, java.lang.String email, java.lang.String pec, java.lang.String denominazione, java.lang.String codFiscalePerControlli, java.lang.String partitaIVAPerControlli) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.inserisciImpresa(username, email, pec, denominazione, codFiscalePerControlli, partitaIVAPerControlli);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType eliminaImpresa(java.lang.String username) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.eliminaImpresa(username);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType aggiornaImpresa(java.lang.String username, java.lang.String email, java.lang.String pec, java.lang.String denominazione, java.lang.String codFiscalePerControlli, java.lang.String partitaIVAPerControlli) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.aggiornaImpresa(username, email, pec, denominazione, codFiscalePerControlli, partitaIVAPerControlli);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType attivaImpresa(java.lang.String username, java.lang.String denominazione) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.attivaImpresa(username, denominazione);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType inviaMailAttivazioneImpresa(java.lang.String username, java.lang.String email) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.inviaMailAttivazioneImpresa(username, email);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType getUtenteDelegatoImpresa(java.lang.String username) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType ret = impl.getUtenteDelegatoImpresa(username);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType aggiornaUtenteDelegatoImpresa(java.lang.String username, java.lang.String delegato) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.aggiornaUtenteDelegatoImpresa(username, delegato);
        return ret;
    }

    public boolean esisteImpresa(java.lang.String username) throws java.rmi.RemoteException
    {
        boolean ret = impl.esisteImpresa(username);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType sincronizzaConfigurazioneMail(java.lang.String host, java.lang.Integer porta, it.maggioli.eldasoft.plugins.ppcommon.ws.ProtocolloMail protocollo, java.lang.Integer timeout, boolean debug, java.lang.String username, java.lang.String password, java.lang.String mail) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.sincronizzaConfigurazioneMail(host, porta, protocollo, timeout, debug, username, password, mail);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType uploadDatasetAppalti(int anno, java.lang.String codFiscaleStazAppaltante, byte[] datasetCompresso) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.uploadDatasetAppalti(anno, codFiscaleStazAppaltante, datasetCompresso);
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType sincronizzaUnitaMisura() throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.sincronizzaUnitaMisura();
        return ret;
    }

    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType uploadRssBandi(byte[] datasetCompresso) throws java.rmi.RemoteException
    {
        it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType ret = impl.uploadRssBandi(datasetCompresso);
        return ret;
    }

}
