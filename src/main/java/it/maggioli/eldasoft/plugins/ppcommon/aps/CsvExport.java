package it.maggioli.eldasoft.plugins.ppcommon.aps;

import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Classe per effettuare l'export (CSV, ...) di un insieme di dati
 */
public class CsvExport {

	//private static final SimpleDateFormat DDMMYYYY 	= new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat MONEY 		= new DecimalFormat("#,##0.00");
	private static final DecimalFormat MONEY5DEC 	= new DecimalFormat("#,##0.00###");
	
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private PrintStream writer = new PrintStream(stream);
    private String baseUrl;
    
    /**
     * ...
     */
    public void writeLine(Object... element) {
    	if(element != null)
    		for(int i = 0; i < element.length; i++) {
    			String value = "";
    			if(element != null)
	    			if(element[i] instanceof Calendar)
	    				value = calendarToString((Calendar)element[i]);
    				if(element[i] instanceof Date)
    					value = dateToString((Date)element[i]);
    				if(element[i] instanceof URL)
    					value = ((URL)element[i]).toString();
	    			else
	    				value = escapeString((String)element[i]);
    			
    			writer.print(value + StringUtilities.CSV_DELIMITER);
    		}
        writer.println();
    }
    
    /**
     * string to escape string
     */
    private String escapeString(String string) {
        return StringUtilities.escapeCsv(StringUtils.defaultString(string));
    }

    /**
     * date to string
     */ 
    public String calendarToString(Calendar c) {
    	return c != null ? UtilityDate.convertiData(Date.from(c.toInstant()), UtilityDate.FORMATO_GG_MM_AAAA) : "";
    }

    /**
     * ...
     */
    public String dateToString(Date v) {
    	return v != null ? UtilityDate.convertiData(v, UtilityDate.FORMATO_GG_MM_AAAA) : "";
    }
    
    /**
     * ...
     */
    public String moneyToString(Double v) {
    	return v != null ? MONEY5DEC.format(v) : "";
    }
    
    /**
     * ...
     */
    public String money5decToString(Double v) {
    	return v != null ? MONEY5DEC.format(v) : "";
    }

    /**
     * ...
     */
    public String wrapForExcel(String s) {
        return StringUtils.isNotEmpty(s) ? "\"" + s + "\"" : "";
    }
    
    /**
     * ...
     */
    public String getBaseUrl() {
    	if(StringUtils.isEmpty(baseUrl)) {
    		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    		ConfigInterface configManager = (ConfigInterface) ctx.getBean(SystemConstants.BASE_CONFIG_MANAGER);
    		baseUrl = configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
    	}
    	return baseUrl;
    }
    
    public URL getLinkTo(String url) {
    	URL link = null;
    	try {
			link = new URL(getBaseUrl() + url);
		} catch (MalformedURLException e) {
			ApsSystemUtils.getLogger().error(getClass().getName(), "getLinkTo", e);
		}
    	return link;
    }
        
    /**
     * ...
     */
    public ByteArrayInputStream getStream() {
    	return new ByteArrayInputStream(stream.toByteArray());
    }
    
}
