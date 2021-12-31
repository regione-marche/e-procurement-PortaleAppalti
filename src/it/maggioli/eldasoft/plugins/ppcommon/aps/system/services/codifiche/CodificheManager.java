package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Manager per la gestione delle codifiche ricevute dal backoffice.
 * 
 * @author Stefano.Sabbadin
 */
public class CodificheManager extends AbstractService implements ICodificheManager {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -8870916624869188889L;
	
	//************************************************************************		
	/**
	 * Gestione delle informazioni relative ad una lista della hashtable 
	 */
	private class ListInfo {
		public long updateInterval = 3600;		// list update interval in seconds
		public Date lastUpdate = new Date();	// last list update		
	}	

	private class HashList extends Hashtable<String, LinkedHashMap<String, String>> {
		
		private static final long serialVersionUID = 6745564618266744065L;
		
		private Hashtable<String, ListInfo> listInfo = new Hashtable<String, ListInfo>();
		
		private IAppParamManager paramManager;
		
		/**
		 * 
		 */
		@Override
		public boolean containsKey(Object key) {
			boolean contains = super.containsKey(key);
		
			if(this.paramManager == null) {
				this.paramManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER, ServletActionContext.getRequest());
			}

			// check if list is old and need to be reloaded...
			ListInfo info = this.listInfo.get(key);					
			if(info == null) {
				info = new ListInfo();
			}
			
			// rileggi il valore l'intervallo di refresh... 
			Integer interval = (Integer) paramManager.getConfigurationValue(AppParamManager.TABELLATI_REFRESH_INTERVAL);
			if (interval != null && interval.longValue() > 0) {
				info.updateInterval = interval.longValue();  
			}			
			
			this.listInfo.put((String)key, info);

			Calendar updateTime = Calendar.getInstance();			
			updateTime.setTime(info.lastUpdate);
			updateTime.add(Calendar.SECOND, (int)info.updateInterval);
			
			Date now = new Date();
				
			if( now.after(updateTime.getTime()) ) {
				// list too old, Need For Refresh...			
				super.remove(key);
				contains = false;
//System.out.println("CodificheManager " + key + " reloading");
			}
				
    		return contains;
    	}
		
		/**
		 * 
		 */
		public LinkedHashMap<String, String> put(String key, LinkedHashMap<String, String> value) {
			ListInfo info = this.listInfo.get(key);					
			if(info == null) {
				info = new ListInfo();
			}
			
			info.lastUpdate = new Date();
			
			this.listInfo.put(key, info);
			
			return super.put(key, value);
    	}
		
	}

	//************************************************************************
	private HashList hash;	//Hashtable<String, LinkedHashMap<String, String>> hash;
	
	@Override
	public void init() throws Exception {
		//this.hash = new Hashtable<String, LinkedHashMap<String, String>>();
		this.hash = new HashList();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public Hashtable<String, LinkedHashMap<String, String>> getCodifiche() {
		return this.hash;
	}

}
