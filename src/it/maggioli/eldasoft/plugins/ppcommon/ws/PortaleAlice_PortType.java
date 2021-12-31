/**
 * PortaleAlice_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.maggioli.eldasoft.plugins.ppcommon.ws;

public interface PortaleAlice_PortType extends java.rmi.Remote {
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType inserisciImpresa(java.lang.String username, java.lang.String email, java.lang.String pec, java.lang.String denominazione, java.lang.String codFiscalePerControlli, java.lang.String partitaIVAPerControlli) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType eliminaImpresa(java.lang.String username) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType aggiornaImpresa(java.lang.String username, java.lang.String email, java.lang.String pec, java.lang.String denominazione, java.lang.String codFiscalePerControlli, java.lang.String partitaIVAPerControlli) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType attivaImpresa(java.lang.String username, java.lang.String denominazione) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType inviaMailAttivazioneImpresa(java.lang.String username, java.lang.String email) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType getUtenteDelegatoImpresa(java.lang.String username) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType aggiornaUtenteDelegatoImpresa(java.lang.String username, java.lang.String delegato) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType sincronizzaConfigurazioneMail(java.lang.String host, java.lang.Integer porta, it.maggioli.eldasoft.plugins.ppcommon.ws.ProtocolloMail protocollo, java.lang.Integer timeout, boolean debug, java.lang.String username, java.lang.String password, java.lang.String mail) throws java.rmi.RemoteException;
    public boolean esisteImpresa(java.lang.String username) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType uploadDatasetAppalti(int anno, java.lang.String codFiscaleStazAppaltante, byte[] datasetCompresso) throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType sincronizzaUnitaMisura() throws java.rmi.RemoteException;
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType uploadRssBandi(byte[] datasetCompresso) throws java.rmi.RemoteException;
}
