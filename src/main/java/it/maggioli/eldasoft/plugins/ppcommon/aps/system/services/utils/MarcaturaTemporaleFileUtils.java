package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.lowagie.text.DocumentException;
import it.maggioli.eldasoft.digitaltimestamp.client.EDigitalTimestamp;
import it.maggioli.eldasoft.digitaltimestamp.client.IDigitalTimestamp;
import it.maggioli.eldasoft.digitaltimestamp.model.ITimeStampResult;
import it.maggioli.eldasoft.digitaltimestamp.model.info.BaseTimeStampInfo;
import it.maggioli.eldasoft.digitaltimestamp.model.info.MaggioliTimeStampInfo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MarcaturaTemporaleFileUtils {

	public static ITimeStampResult eseguiMarcaturaTemporale(byte[] contenutoFile, IAppParamManager appParamManager) throws ApsException, XmlException, DocumentException, IOException {
		ITimeStampResult result = null;
		BaseTimeStampInfo baseInfo = BaseTimeStampInfo.DigitalTimeStampInfoBuilder.newDigitalTimeStampInfo()
				.withProviderPassword(getProperty(appParamManager, AppParamManager.MARCATURA_PROVIDER_PASSWORD))
				.withProviderType(getProperty(appParamManager, AppParamManager.MARCATURA_PROVIDER_TYPE))
				.withProviderUrl(getProperty(appParamManager, AppParamManager.MARCATURA_PROVIDER_URL))
				.withProviderUsername(getProperty(appParamManager, AppParamManager.MARCATURA_PROVIDER_USERNAME))
				.withUrlServizio(getProperty(appParamManager, AppParamManager.MARCATURA_URL))
			.build();
		IDigitalTimestamp client = null;
		// 3 == vecchio, 1 == nuovo (auth kong), 2 == blockchain?
		if (getProperty(appParamManager, AppParamManager.MARCATURA_TYPE).equals("3")) {
			client = EDigitalTimestamp.MAGGIOLI_OLD(baseInfo);
		} else {
			client = EDigitalTimestamp.MAGGIOLI(
					MaggioliTimeStampInfo.DigitalTimeStampInfoBuilder.newDigitalTimeStampInfo()
						.withBaseInfo(baseInfo)
						.withAuthUrl(getProperty(appParamManager, AppParamManager.KONG_AUTH_URL))
						.withAuthClientId(getProperty(appParamManager, AppParamManager.KONG_AUTH_CLIENT_ID))
						.withAuthClientSecret(getProperty(appParamManager, AppParamManager.KONG_AUTH_CLIENT_SECRET))
						.withAuthType(2)
					.build()
			);
		}

		String filePath = StrutsUtilities
				.getTempDir(ServletActionContext.getServletContext()).getAbsolutePath()
				+ File.separatorChar + UUID.randomUUID().toString();
		try {
			result = client.getTimestampedFile(contenutoFile, filePath);
		} catch (Throwable t) {
			throw t;
		} finally {
			// elimina il file temporaneo, se per qualche motivo rimane qualcosa nella work...
			deleteTempFile(filePath);
		}
		return result;
	}

	private static String getProperty(IAppParamManager appParamManager, String property) {
		return (String) appParamManager.getConfigurationValue(property);
	}

	private static void deleteTempFile(String tmp) {
		try {
			File f = new File(tmp);
	        if (f != null && f.exists()) {
	        	f.delete();
	        	ApsSystemUtils.getLogger().warn("MarcaturaTemporaleFileUtils", "Eliminato file temporaneo da marcatura temporale {}", tmp);
	        }
	        f = null;
		} catch (Throwable t) {
			// NON DOVREBBE SUCCEDERE!!!
		}
	}

}
