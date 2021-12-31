package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.net.ConnectException;

import org.apache.axis.AxisFault;
import org.apache.xmlbeans.XmlException;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Classe di utilita' per la definizione del messaggio utente in seguito ad
 * un'eccezione
 *
 * @author Stefano.Sabbadin
 */
public class ExceptionUtils {

	public static void manageExceptionError(Throwable e, ActionSupport action) {
		
		if (e instanceof AxisFault) {
			manageAxisFault(action, (AxisFault) e);
		} else if (e.getCause() instanceof AxisFault) {
			AxisFault f = (AxisFault) e.getCause();
			manageAxisFault(action, f);
		} else if (e instanceof XmlException) {
			action.addActionError(action.getText("Errors.service.readDataWrong") + ": " + e.getMessage());
		} else if (e instanceof RuntimeException) {
			action.addActionError(action.getText("Errors.unexpected") + ": " + e.getMessage());
		} else {
			action.addActionError(action.getText("Errors.unexpected"));
		}
	}

	private static void manageAxisFault(ActionSupport action, AxisFault f) {
		
		if (f.detail instanceof ConnectException) {
			action.addActionError(action.getText("Errors.service.unreachable"));
		} else {
			action.addActionError(action.getText("Errors.service.handshake"));
		}
	}
}
