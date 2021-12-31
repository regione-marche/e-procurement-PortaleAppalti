package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.report;

import it.eldasoft.www.Report.ReportProxy;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

/**
 * Wrapper per il client del web service per l'estrazione di report, definito per
 * iniettare mediante AOP le variazioni di endpoint a runtime.
 * 
 * @author Stefano.Sabbadin
 */
@Aspect
public class WSReportWrapper {

	/** Riferimento al web service Report. */
	private ReportProxy proxyWSReport;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/**
	 * @return the proxyWSReport
	 */
	public ReportProxy getProxyWSReport() {
		return proxyWSReport;
	}

	/**
	 * @param proxyWSReport the proxyWSReport to set
	 */
	public void setProxyWSReport(ReportProxy proxyWSReport) {
		this.proxyWSReport = proxyWSReport;
	}

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	@After ("execution(* it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager.update*(..))")
	public void setEndpoint(JoinPoint joinPoint) {
		String endpoint = (String)this.appParamManager.getConfigurationValue(AppParamManager.WS_REPORT);
		this.proxyWSReport.setEndpoint(endpoint);
	}
	
}
