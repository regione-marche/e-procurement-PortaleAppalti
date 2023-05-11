package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampInfo;
import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampResult;
import it.maggioli.eldasoft.digitaltimestamp.client.DigitalTimeStampClient;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.exception.ApsException;
import com.lowagie.text.DocumentException;

public class MarcaturaTemporaleFileUtils {

	public static DigitalTimeStampResult eseguiMarcaturaTemporale(byte[] contenutoFile, IAppParamManager appParamManager) throws ApsException, XmlException, DocumentException, IOException{
		String username =  (String)appParamManager.getConfigurationValue("marcaturaTemp.provider.username");
		String password =  (String)appParamManager.getConfigurationValue("marcaturaTemp.provider.password");
		String providerType =  (String)appParamManager.getConfigurationValue("marcaturaTemp.provider.tipo");
		String providerUrl =  (String)appParamManager.getConfigurationValue("marcaturaTemp.provider.url");
		String url =  (String)appParamManager.getConfigurationValue("marcaturaTemp.url");
		DigitalTimeStampInfo info = new DigitalTimeStampInfo();
		String filePath = StrutsUtilities.getTempDir(
				ServletActionContext.getServletContext())	.getAbsolutePath()
				+ File.separatorChar + UUID.randomUUID().toString();
		info.setProviderUsername(username);
		info.setProviderPassword(password);
		info.setProviderType(providerType);
		info.setProviderUrl(providerUrl);
		info.setUrlServizio(url);
		DigitalTimeStampClient client = new DigitalTimeStampClient(info);
		DigitalTimeStampResult result = client.digitalTimeStampFile(contenutoFile, filePath);
		return result;
	}
	

	

}
