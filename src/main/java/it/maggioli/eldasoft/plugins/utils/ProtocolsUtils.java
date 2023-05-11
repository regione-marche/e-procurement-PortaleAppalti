package it.maggioli.eldasoft.plugins.utils;

import org.apache.commons.lang.StringUtils;

import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;

public class ProtocolsUtils {
	
	private ProtocolsUtils() { }

	public static void setFieldsForNumix(WSDMProtocolloAllegatoType[] attachments) {
		if (attachments != null)
			for (int i = 0; i < attachments.length; i++) 
				setFieldsForNumix(attachments[i], i == 0);
	}
	
	private static void setFieldsForNumix(WSDMProtocolloAllegatoType attachment, boolean isMainDocument) {
		if (attachment != null) {
			attachment.setIsSealed(isMainDocument ? 1l : 0);
			attachment.setIsSigned(containsExtension(attachment.getNome(), "p7m") ? 1l : 0l);
			attachment.setIsCertifiedCopy(0l);	//Not supported
			attachment.setIsTimeMarked(StringUtils.equalsIgnoreCase(attachment.getTipo(), "tsd") ? 1l : 0l);
		}
	}
	
	private static boolean containsExtension(String fileName, String extension) {
		return StringUtils.containsIgnoreCase(fileName, "." + extension);
	}
	
}
