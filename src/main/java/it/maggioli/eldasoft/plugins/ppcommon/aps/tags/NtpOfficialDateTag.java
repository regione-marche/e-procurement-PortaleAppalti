/**
 * 
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps.tags;



import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag per la generazione della data/ora con connessione ad un server NTP
 * 
 * @author Stefano.Sabbadin
 */
public class NtpOfficialDateTag extends TagSupport {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -3335122273101569392L;

	@Override
	public int doStartTag() throws JspException {
		try {
			INtpManager ntpManager = 
				(INtpManager) ApsWebApplicationUtils.getBean(CommonSystemConstants.NTP_MANAGER, this.pageContext);
			// richiede l'ora ufficiale
			Date cal = ntpManager.getNtpDate();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(cal);
			// costruisce le informazioni da passare alla pagina
			NtpOfficialDate ntpDate = new NtpOfficialDate();
			ntpDate.setDate(cal);
			ntpDate.setDay(gc.get(Calendar.DAY_OF_MONTH));
			ntpDate.setMonth(gc.get(Calendar.MONTH) + 1);
			ntpDate.setYear(gc.get(Calendar.YEAR));
			ntpDate.setHours(gc.get(Calendar.HOUR_OF_DAY));
			ntpDate.setMinutes(gc.get(Calendar.MINUTE));
			ntpDate.setSeconds(gc.get(Calendar.SECOND));
			this.pageContext.setAttribute(this.getVar(), ntpDate);
		} catch (SocketTimeoutException e) {
			// costruisce un oggetto con l'errore 
			NtpOfficialDate ntpDate = new NtpOfficialDate();
			ntpDate.setError("Data non rilevata: timeout scaduto");
			this.pageContext.setAttribute(this.getVar(), ntpDate);
		} catch (UnknownHostException e) {
			// costruisce un oggetto con l'errore 
			NtpOfficialDate ntpDate = new NtpOfficialDate();
			ntpDate.setError("Data non rilevata: servizio non raggiungibile");
			this.pageContext.setAttribute(this.getVar(), ntpDate);
		} catch (Throwable e) {
			throw new JspException(
					"Errore durante la richiesta della data/ora ufficiale", e);
		}

		return super.doStartTag();
	}

	/**
	 * @return the _var
	 */
	public String getVar() {
		return _var;
	}

	/**
	 * @param _var
	 *            the _var to set
	 */
	public void setVar(String _var) {
		this._var = _var;
	}

	private String _var;

}
