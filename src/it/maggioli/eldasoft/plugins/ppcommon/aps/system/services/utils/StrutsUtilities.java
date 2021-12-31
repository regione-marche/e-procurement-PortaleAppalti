package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import com.agiletec.aps.system.ApsSystemUtils;

public class StrutsUtilities {

    /**
     * Ritorna la directory per i file temporanei, prendendola da
     * javax.servlet.context.tempdir (area temporanea nella work della
     * webapplication)
     * 
     * @return path alla directory per i file temporanei
     */
    public static File getTempDir(ServletContext context) {
	return (File) context.getAttribute("javax.servlet.context.tempdir");
    }

    /**
     * Ritorna la directory per i file temporanei, prendendola da
     * struts.multipart.saveDir (struts.properties) se valorizzata
     * correttamente, altrimenti da javax.servlet.context.tempdir
     * 
     * @return path alla directory per i file temporanei
     */
    public static File getTempDir(ServletContext context,
	    String strutsMultipartSaveDir) {
	File tempDir = StrutsUtilities.getTempDir(context);
	String saveDir = strutsMultipartSaveDir.trim();
	if (!saveDir.equals("")) {
	    File multipartSaveDir = new File(saveDir);

	    if (!multipartSaveDir.exists()) {

		String logMessage;
		try {
		    logMessage = "Could not find multipart save directory '"
			    + multipartSaveDir.getCanonicalPath() + "'.";
		} catch (IOException e) {
		    logMessage = "Could not find multipart save directory '"
			    + multipartSaveDir.toString() + "'.";
		}
		ApsSystemUtils.getLogger().warn(logMessage);
	    } else {
		tempDir = multipartSaveDir;
	    }
	}
	return tempDir;
    }

}
