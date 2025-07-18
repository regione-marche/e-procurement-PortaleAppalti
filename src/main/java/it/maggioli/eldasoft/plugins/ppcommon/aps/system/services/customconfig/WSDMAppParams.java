package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig;

import it.eldasoft.www.sil.WSGareAppalto.WSDMConfigType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Gestione delle configurazioni multi WSDM
 *
 * @author 
 * @since 3.8.0
 */
public class WSDMAppParams {

	private IBandiManager bandiManager;
		
	private Map<String, String> idSessions;	// coppie (id sessione, stazione appaltante)  
	private Map<String, AppParam> map;	
	private long idConfig;					// id della configurazione relativa alla stazione appaltente
	private long idConfigDefault;			// id della configurazione di default
	private long configPortale;				// 1 se la configurazione del Portale e' valida, 0 altrimenti
	private boolean defaultConfigEmpty;		// true se la configurazione di default non esiste (o e' vuota) 

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public long getIdConfig() {
		return idConfig;
	}

	public long getIdConfigDefault() {
		return idConfigDefault;
	}

	public long getConfigPortale() {
		return configPortale;
	}
	
	public boolean isDefaultConfigEmpty() {
		return defaultConfigEmpty;
	}

	/**
	 * costruttore
	 */
	public WSDMAppParams() {
		this.map = new HashMap<String, AppParam>();
		this.idSessions = new HashMap<String, String>();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	/**
	 * recupera l'ID della sessione corrente
	 */
	private String getSessionId() {
		String sessionId = "";
		try {
			if(ServletActionContext.getRequest() != null) {
				HttpSession session = ServletActionContext.getRequest().getSession(true);
				if(session != null && StringUtils.isNotEmpty(session.getId())) {
					sessionId = session.getId();
				}
			}	
		} catch (Throwable e) {
			sessionId = "";
		}
		return sessionId;
	}
	
	/**
	 * verifica se esiste una configurazione per una stazione appaltante o di default 
	 * (valida o meno)  
	 */
	public synchronized String getCurrentSA() {
		return this.idSessions.get(this.getSessionId());
	}
		
	/**
	 * verifica se esiste una configurazione per una stazione appaltante o di default 
	 * (valida o meno)  
	 */
	public synchronized boolean existsConfiguration(String name, String stazioneAppaltante) {
		boolean exists = false;
		
		// controlla solo i parametri della protocollazione ("protocollazione.wsdm.")
		int i = AppParamManager.PROTOCOLLAZIONE_WSDM_URL.lastIndexOf(".");
		String pref = (i >= 0 ? AppParamManager.PROTOCOLLAZIONE_WSDM_URL.substring(0, i+1) : "");
		String pref2 = ( i >= 0 && i < name.length() ? name.substring(0, i+1) : "");
		
		if( (StringUtils.isNotEmpty(pref) && pref2.equalsIgnoreCase(pref)) || 
			(AppParamManager.PROTOCOLLAZIONE_WSDM_URL_CONFIG.equalsIgnoreCase(name)) ) {
			// verifica se esiste almeno 1 parametro per la configurazione
			// che inizia con lo stesso prefisso ("protocollazione.wsdm")
			String p = "";
			if(StringUtils.isNotEmpty(stazioneAppaltante)) {
				p = (stazioneAppaltante + "." + pref);
			} else {
				p = pref;
			}
			Iterator e = this.map.entrySet().iterator();
		    while (e.hasNext()) {
		        Map.Entry item = (Map.Entry)e.next();
		        if(item.getKey().toString().startsWith(p)) {
		        	exists = true;
		        	break;
		        }
		    }
		}

		return exists;
	}
	
	/**
	 * verifica se esiste una configurazione per una stazione appaltante o di default 
	 * (valida o meno)  
	 */
	public synchronized boolean activeConfiguration(String stazioneAppaltante) {
		String key = null;
		if(StringUtils.isNotEmpty(stazioneAppaltante)) {
			key = stazioneAppaltante + "." + AppParamManager.PROTOCOLLAZIONE_WSDM_URL;
		} else {
			key = AppParamManager.PROTOCOLLAZIONE_WSDM_URL;
		}
		
		boolean contains = this.map.containsKey(key);
		String value = (String) this.map.get(key).getObjectValue();
		return (contains && StringUtils.isNotEmpty(value));
	}
	
	/**
	 * recupera il valore di un parametro della relativa stazione appaltante
	 */
	public synchronized Object getConfigurationValue(String name) {
		Object value = null;
	
		boolean SAConfigExists = false;
		AppParam param = null;
		
		// verifica se esiste la configurazione per la stazione appaltante...
		String sa = this.idSessions.get(this.getSessionId());
		if(StringUtils.isNotEmpty(sa)) {
			SAConfigExists = this.map.containsKey(sa + "." + AppParamManager.PROTOCOLLAZIONE_WSDM_URL);
			if(SAConfigExists) {
				param = this.map.get(sa + "." + name);
			}
		}
		if( !SAConfigExists ) {
			// ...se non esiste la configurazione per la sa, 
			// utilizza, se esiste, la configurazione di default...
			if (this.map.containsKey(AppParamManager.PROTOCOLLAZIONE_WSDM_URL)) {
				param = this.map.get(name);
			}
		}
		
		if (param != null) {
			value = param.getObjectValue();
			
			// in caso di parametro numerico/boolean non valorizzato
			// crea un valore di default
			if("I".equalsIgnoreCase(param.getType())) {
				value = this.asInteger(value);
			} else if("B".equalsIgnoreCase(param.getType())) {
				value = this.asBoolean(value);
			}
		}
			
		return value;
	}
	
	/**
	 * ...
	 */
	public boolean validConfiguration(String url) {
		// se l'URL della configurazione non e' vuoto
		// la configurazione viene considerata valida 
		return (StringUtils.isNotEmpty(url)); 
	}
	
	/**
	 * carica i parametri multi configurazione relativi alla stazione appaltante
	 */
	public synchronized void loadAppParams(String stazioneAppaltante, Map<String, AppParam> portale) {
		this.idConfig = -1;
		this.idConfigDefault = -1;
		this.configPortale = 0;
		this.defaultConfigEmpty = false;
		long idFirstConfigDefault = -1;
		String defaultUrlBo = null;			// url WSDM definita per il BO per la configurazione di default
		String defaultUrlPortale = null;	// url WSDM definita per il Portale per la configurazione di default
		try {			
			if(this.bandiManager == null) {
				this.bandiManager = (IBandiManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());
			}
			
//			// (DEPRECATA) verifica se la configurazione del Portale e' valida...
//			if (portale.get(AppParamManager.PROTOCOLLAZIONE_WSDM_URL) != null) {
//				String url = (String)portale.get(AppParamManager.PROTOCOLLAZIONE_WSDM_URL).getObjectValue();
//				if(this.validConfiguration(url)) {
//					this.configPortale = 1;
//				}
//			}
		
			// recupera i parametri della configurazione di default 
			// ovvero quella attiva con id piu' basso altrimenti quella con id piu' basso
			List<WSDMConfigType> wsdmDef = this.bandiManager.getWSDMConfig(null);
			if(wsdmDef != null && wsdmDef.size() > 0) {
				long idConf = -1;
				
				// cerca una configurazione di default...
				for (WSDMConfigType item : wsdmDef) {
					if(AppParamManager.PROTOCOLLAZIONE_WSDM_URL.equalsIgnoreCase(item.getCodice())) {
						// esiste una configurazione di default (con o senza url wsdm)
						if(idFirstConfigDefault < 0) {
							idFirstConfigDefault = item.getId();
						}
						
						// se l'URL della configurazione non e' vuoto
						// la configurazione viene considerata valida
						if(this.validConfiguration(item.getValore())) {
							idConf = item.getId();
							break;
						}
					}
					if(AppParamManager.PROTOCOLLAZIONE_WSDM_FASCICOLOPROTOCOLLO_URL.equalsIgnoreCase(item.getCodice())) {
						// esiste una configurazione di default (con o senza url wsdm)
						if(idFirstConfigDefault < 0) {
							idFirstConfigDefault = item.getId();
						}
					}
				}
				
				// ...carica la configurazione di DEFAULT con id piu' basso
				for (WSDMConfigType item : wsdmDef) {
					if(idConf == item.getId().longValue() || (idConf < 0 && idFirstConfigDefault == item.getId().longValue())) {
						if(AppParamManager.PROTOCOLLAZIONE_WSDM_URL.equalsIgnoreCase(item.getCodice())) {
							defaultUrlPortale = item.getValore();
						}
						if(AppParamManager.PROTOCOLLAZIONE_WSDM_FASCICOLOPROTOCOLLO_URL.equalsIgnoreCase(item.getCodice())) {
							defaultUrlBo = item.getValore();
						}
						try {
							this.addParam(item.getCodice(), item, portale);
						} catch (Exception e) {
							// errore di conversione del parametro
							ApsSystemUtils.getLogger().warn("Errore nella codifica del parametro applicativo " + item.getCodice() + 
									                        " ('" + item.getValore() +  "'?)" +
									                        " per la configurazione di default " + item.getId());
						}
					}
				}
				
				// configurazione di default valida (con url wsdm per il portale) 
				this.idConfigDefault = idConf;
			}
			
			// recupera i parametri della configurazione della STAZIONE APPALTANTE
			if(StringUtils.isNotEmpty(stazioneAppaltante)) {
				
				List<WSDMConfigType> wsdm = this.bandiManager.getWSDMConfig(stazioneAppaltante);
				if(wsdm != null) {
					for (WSDMConfigType item : wsdm) {
						// verifica se la configurazione della SA e' attiva...
						if(AppParamManager.PROTOCOLLAZIONE_WSDM_URL.equalsIgnoreCase(item.getCodice())) {
							if(this.validConfiguration(item.getValore())) {
								this.idConfig = wsdm.get(0).getId();
							}
						}

						// aggiungi/modifica un parametro della configurazione multipla
						try {
							this.addParam(stazioneAppaltante + "." + item.getCodice(), item, portale);
						} catch (Exception e) {
							// errore di conversione del parametro
							ApsSystemUtils.getLogger().warn("Errore nella codifica del parametro applicativo " + item.getCodice() + 
									                        " ('" + item.getValore() +  "'?)" +
									                        " per la SA " + stazioneAppaltante);
						}
					}
					
					// aggiorna l'elenco delle coppie (sessionId, stazioneAppaltante)
					String sessionId = this.getSessionId();
					if(StringUtils.isNotEmpty(sessionId)) {
						this.idSessions.put(sessionId, stazioneAppaltante);
					}
				}
			}
			
			// NB:
			// verifica se non e' stata trovata una configurazione valida per il WSDM 
			// (protocollazione.wsdm.url <> wsdm.fascicoloprotocollo.url)
			if(this.idConfig < 0) {
				// in caso di protocollazione.tipo=2 (WSDM) 
				// se NON esiste si da errore in fase di invio 
				if(this.idConfigDefault < 0 && idFirstConfigDefault > 0 && StringUtils.isNotEmpty(defaultUrlBo)) {
					if( !defaultUrlBo.equalsIgnoreCase(defaultUrlPortale) ) {
						ApsSystemUtils.getLogger().warn("loadAppParams() " + "configurazione di default vuota (" + idFirstConfigDefault + ") (wsdm.fascicoloprotocollo.url<>da protocollazione.wsdm.url)");						
						// deterina l'impostazione "AppParamManager.configWSDMNonDisponibile" in modo da generare il messaggio
						// "Attenzione: non e' possibile completare l'invio per indisponibilitÓ del sistema di protocollazione"
						this.defaultConfigEmpty = true; 
					}
				}
			}
			
			// verifica quale configurazione utilizzare...
			// Config SA > Config default > Nessuna		(la configurazione del Portale viene DEPRECATA!!!) 
			String log = null; 
			if(this.idConfig <= 0) {
				this.idConfig = this.idConfigDefault;
				log = (this.defaultConfigEmpty
					   ? "Caricata configurazione di default senza protocollazione (" + idFirstConfigDefault + ")"
					   : "Caricata configurazione di default WSDM (" + idFirstConfigDefault + ")");
			} else {
				log = "Caricata configurazione WSDM (" + this.idConfig + ")";
			}
			ApsSystemUtils.getLogger().warn("loadAppParams() " + log);
			
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadAppParams");
		}
	}
	
	/**
	 * aggiungi un parametro all'elenco delle configurazioni 
	 */
	private AppParam addParam(
			String key, 
			WSDMConfigType item, 
			Map<String, AppParam> info) 
	{
		AppParam param = null;
		if(this.map.containsKey(key)) {
			param = this.map.get(key);
		}
		if(param == null) {
			param = new AppParam();
			param.setName(item.getCodice());
			param.setDescription(item.getDescrizione());
			param.setObjectValue(null);
			param.setValue(null);
			// recupera il "tipo" del valore dalla configurazione 
			// standard "PPCOMMON_PROPERTIES"
			AppParam i = info.get(item.getCodice());
			param.setType( (i != null ? i.getType() : null) );
		}

		if("I".equalsIgnoreCase(param.getType())) {
			param.setObjectValue( item.getValore() == null ? null : Integer.valueOf(item.getValore()) );
		} else if("B".equalsIgnoreCase(param.getType())) {
			param.setObjectValue( item.getValore() == null ? null : Boolean.valueOf(item.getValore()) );
		} else {
			param.setObjectValue(item.getValore());
		}		
		//param.setValue(param.getObjectValue());  // serve ???

		this.map.put(key, param);
		
		return param;
	}
	
	/**
	 * chiudi la protocollazione i parametri multi configurazione relativi alla stazione appaltante
	 */
	public synchronized void endProtocollazione() {
		// rimuovi il session id corrente dall'elenco...
		String sessionId = this.getSessionId();
		if(StringUtils.isNotEmpty(sessionId)) {
			this.idSessions.remove(sessionId);
		}
	}
	
	
	private Object asInteger(Object value) {
		Object v = null;
		try {
			int x = ((Integer) value).intValue(); 
			v = value;
		} catch (Exception e) {
			value = new Integer(0);
		}
		return v;
	}
	
	private Object asBoolean(Object value) {
		Object v = null;
		try {
			boolean x = ((Boolean) value).booleanValue(); 
			v = value;
		} catch (Exception e) {
			value = new Boolean(false);
		}
		return v;
	}
	

}
