/**
 * PortaleAliceSOAPImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.maggioli.eldasoft.plugins.ppcommon.ws;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.sysparams.IConfigParamSistemaManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.dsappalti.IDatasetAppaltiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.rssbandi.IRSSBandiManager;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;



public class PortaleAliceSOAPImpl implements
		it.maggioli.eldasoft.plugins.ppcommon.ws.PortaleAlice_PortType {
	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType inserisciImpresa(
			java.lang.String username, java.lang.String email,
			java.lang.String pec, java.lang.String denominazione,
			java.lang.String codFiscalePerControlli,
			java.lang.String partitaIVAPerControlli)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.insertImpresa(username, email, pec, denominazione,
				codFiscalePerControlli, partitaIVAPerControlli);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType eliminaImpresa(
			java.lang.String username) throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.deleteImpresa(username);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType aggiornaImpresa(
			java.lang.String username, java.lang.String email,
			java.lang.String pec, java.lang.String denominazione,
			java.lang.String codFiscalePerControlli,
			java.lang.String partitaIVAPerControlli)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.updateImpresa(username, email, pec, denominazione,
				codFiscalePerControlli, partitaIVAPerControlli);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType attivaImpresa(
			java.lang.String username, java.lang.String denominazione)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.activateImpresa(username, denominazione);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType inviaMailAttivazioneImpresa(
			java.lang.String username, java.lang.String email)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.sendActivationMailImpresa(username, email);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.RisultatoStringaOutType getUtenteDelegatoImpresa(java.lang.String username) throws java.rmi.RemoteException {
		//lettura delegate user data unsername dell'impresa 
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.getUtenteDelegatoImpresa(username);
    }

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType aggiornaUtenteDelegatoImpresa(java.lang.String username, java.lang.String delegato) throws java.rmi.RemoteException {
		//aggiornamento delegate user data unsername dell'impresa (cambio soggetto fisico che opera per quell'impresa) 
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.aggiornaUtenteDelegatoImpresa(username, delegato);
    }

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType sincronizzaConfigurazioneMail(
			java.lang.String host, java.lang.Integer porta,
			it.maggioli.eldasoft.plugins.ppcommon.ws.ProtocolloMail protocollo,
			java.lang.Integer timeout, boolean debug, 
			java.lang.String username, java.lang.String password,
			java.lang.String mail) throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IConfigParamSistemaManager manager = (IConfigParamSistemaManager) ctx
				.getBean("ConfigParamSistemaManager");
		return manager.syncConfigurazioneMail(host, porta,
				protocollo.getValue(), timeout, debug, username, password, mail);
	}

	public boolean esisteImpresa(java.lang.String username)
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IRegistrazioneImpresaManager manager = (IRegistrazioneImpresaManager) ctx
				.getBean("RegistrazioneImpresaManager");
		return manager.esisteImpresa(username);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType uploadDatasetAppalti(
			int anno, java.lang.String codFiscaleStazAppaltante,
			byte[] datasetCompresso) throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IDatasetAppaltiManager manager = (IDatasetAppaltiManager) ctx
				.getBean("DatasetAppaltiManager");
		return manager.uploadDatasetAppalti(anno, codFiscaleStazAppaltante,
				datasetCompresso);
	}

	public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType sincronizzaUnitaMisura()
			throws java.rmi.RemoteException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
		IConfigParamSistemaManager manager = (IConfigParamSistemaManager) ctx
				.getBean("ConfigParamSistemaManager");
		return manager.syncUnitaMisura();
	}
	
    public it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType uploadRssBandi(byte[] datasetCompresso) throws java.rmi.RemoteException {
    	ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(SpringAppContext.getServletContext());
    	IRSSBandiManager manager = (IRSSBandiManager) ctx
				.getBean("RSSBandiManager");
    	return manager.uploadRssBandi(datasetCompresso);
    }
    
	
}
