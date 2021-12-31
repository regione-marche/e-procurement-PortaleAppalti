package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;

public class ComponentiRTIList extends ArrayList<IComponente> {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1267267760300864708L;
	
	public static final Integer CHIAVE_PARTITA_IVA 		= 1;
	public static final Integer CHIAVE_CODICE_FISCALE 	= 2;
	
	// "|" è un carattere speciale e va preceduto da "\\"
	private static final String  HASHKEY_SEPARATOR 		= "\\|";

	
	////////////////////////////////////////////////////////////////////////////	
	////////////////////////////////////////////////////////////////////////////
	private class FirmatarioItem implements Serializable {
		
		public SoggettoFirmatarioImpresaHelper firmatario;
		public Object componenteRTI;
		
		public FirmatarioItem(SoggettoFirmatarioImpresaHelper firmatario, Object componenteRTI) {
			this.firmatario = firmatario;
			
			// caso particolare per firmatario (SoggettoFirmatarioImpresaHelper -> SoggettoImpresaHelper -> ISoggettoImpresa)
			// CF, PIVA impresa vanno aggiornati da CF,PIVA del componente RTI !!!
			if(firmatario instanceof SoggettoFirmatarioImpresaHelper &&
			   componenteRTI instanceof IComponente) {
				firmatario.setCodiceFiscaleImpresa(((IComponente) componenteRTI).getCodiceFiscale());				
				firmatario.setPartitaIvaImpresa(((IComponente) componenteRTI).getPartitaIVA());
			}
			
			// IComponente, ISoggettoImpresa, IDatiPrincipaliImpresa, FirmatarioType			
			if(componenteRTI instanceof FirmatarioType) {
				// non memorizzare il frammento XML, crea un IComponente equivalente...	
				this.componenteRTI = toIComponente(componenteRTI);
			} else {
				this.componenteRTI = componenteRTI;
			}
		}
	}
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	
	// n-uple <chiave firmatario, firmatario, componente rti> associate ad una RTI
	private Map<String, FirmatarioItem> firmatari = new HashMap<String, FirmatarioItem>();	
	private List<ISoggettoImpresa> listaFirmatari = new ArrayList<ISoggettoImpresa>();


	/**
	 * Lista di tutti i firmatari. Restituisce una copia della lista interna
	 * per evitare modifiche alla struttura dall'esterno.  
	 */	
	public List<ISoggettoImpresa> getListaFirmatari() {
		List<ISoggettoImpresa> tmp = new ArrayList<ISoggettoImpresa>();
		tmp.addAll(this.listaFirmatari);  
		return tmp;
	}

	/**
	 * costruttore 
	 */
	public ComponentiRTIList() {
		super();
	}
	
	public ComponentiRTIList(List<IComponente> componentiRTI) {
		super();
		for(int i = 0; i < componentiRTI.size(); i++) {
			add(componentiRTI.get(i));
		}
	}

	/**
	 * Restituisce la chiave hash relativa ad un componente della RTI
	 * NB: Mantenuta per retrocompatibilità  
	 * @throws Exception 
	 */
	public String getChiave(Object item) {
		String key = "";
		try {
			String hk = getHashKey(item);
			if(hk != null) {
				String[] k = hk.split(HASHKEY_SEPARATOR);	
				if(k != null) {
					if(k.length > 0 && StringUtils.isNotEmpty(k[0])) {
						key = k[0];		
					} else if(k.length > 1 && StringUtils.isNotEmpty(k[1])) {
						key = k[1];
					}
				}
			}
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getChiave");
		}		
		return key;
	}

	/**
	 * Restiruisce la chiave hash di un componente o un soggetto impresa 
	 * @throws Exception 
	 */	
	private String getHashKey(Object item) throws Exception {
		String key = "";
		if(item != null) {
			String piva = null;
			String cf = null;
			
			if(item instanceof IComponente) {
				piva = ((IComponente) item).getPartitaIVA(); 
				cf = ((IComponente) item).getCodiceFiscale(); 
				
			} else if(item instanceof ISoggettoImpresa) {
				piva = ""; 
				cf = ((ISoggettoImpresa) item).getCodiceFiscale();
				
			} else if(item instanceof IDatiPrincipaliImpresa) {
				// 6 = Libero professionista
				piva = ("6".equals(((IDatiPrincipaliImpresa) item).getTipoImpresa()) 
						? "" 
						: ((IDatiPrincipaliImpresa) item).getPartitaIVA());
				cf = ((IDatiPrincipaliImpresa) item).getCodiceFiscale();
				
			} else if(item instanceof FirmatarioType) {
				piva = ((FirmatarioType) item).getPartitaIVAImpresa(); 
				cf = ((FirmatarioType) item).getCodiceFiscaleImpresa();
			} else {
				Exception ex = new Exception("Invalid argument type. Allowed classes for 'item' argument are IComponente, ISoggettoImpresa, IDatiPrincipaliImpresa, FirmatarioType.");
				ApsSystemUtils.logThrowable(ex, this, "getHashKey");
				throw ex;
			}
			
			// crea la chiave hash nel formato "[PIVA]|[CF]" 
			if(StringUtils.isNotEmpty(piva) || StringUtils.isNotEmpty(cf)) {
				piva = (StringUtils.isNotEmpty(piva) ? piva : "");
				cf = (piva.length() <= 0 && StringUtils.isNotEmpty(cf) ? cf : "");
				key = piva + HASHKEY_SEPARATOR.replace("\\", "") + cf;
			}
		}
		return key;
	}
	
	/**
	 * Svuota la lista dei componenti e dei firmatari 
	 */
	public void clear() {
		firmatari.clear();
		listaFirmatari.clear();
		super.clear();
	}
	
	public void add(int index, IComponente componente) {
		super.add(index, componente);
	}

	public boolean add(IComponente componente) {
		return super.add(componente);
	}
	
	public boolean remove(Object item) {
		try {
			removeFirmatario(item);
			return super.remove(item);
		} catch (Exception e) {
			return false;
		}
	}
	
	public IComponente remove(int index) {
		try {
			removeFirmatario(get(index));
			return super.remove(index);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Restituisce l'indice della mandataria di un gruppo RTI 
	 * @param key 
	 * 		  oggetto contentente PIVA/CF della mandataria   
	 * 		  L'oggetto può appartenere alle classi IComponente, SoggettoImpresaHelper, 
	 *        IDatiPrincipaliImpresa, FirmatarioType.
	 * @param firmatario
	 *        firmatario da aggiungere/inserire
	 */
	public int getMandataria(Object key) {
		int j = -1;
		try {
			String skey = getHashKey(key);
			for(int i = 0; i < this.size(); i++) {
				if(getHashKey(get(i)).equalsIgnoreCase(skey)) {
					j = i;
					break;
				}
			}
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getMandataria");
		}
		return j;
	}
	
	/**
	 * Aggiorna/inserisce un firmatario associato ad un componente 
	 * @param componenteRTI
	 * 		  componente RTI a cui associare il firmatario
	 * 		  L'oggetto può appartenere alle classi IComponente, ISoggettoImpresa, IDatiPrincipaliImpresa, FirmatarioType.
	 * @param firmatario
	 *        firmatario da aggiungere/modificare
	 */
	public void addFirmatario(Object componenteRTI, SoggettoFirmatarioImpresaHelper firmatario) {
		try {
			// calcola l'hashkey del compontente dell'RTI...
			String skey = getHashKey(componenteRTI);
			firmatari.put(skey, new FirmatarioItem(firmatario, componenteRTI));	
			
			// aggiorna le strutture ausiliarie...
			// calcola l'hashkey del firmatario associato al componente dell'RTI
			skey = getHashKey(firmatario);
			int j = -1;
			for(int i = 0; i < listaFirmatari.size(); i++) {
				if(skey.equals(getHashKey(listaFirmatari.get(i)))) {
					j = i;
					break;
				}
			}
			if(j < 0) {
				listaFirmatari.add(firmatario);
			} else {
				listaFirmatari.set(j, firmatario);
			}
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "addFirmatario");
		}
	}
	
	/**
	 * Elimina un firmatario in base alla chiave (CF/PIVA)
	 */
	public void removeFirmatario(Object key) {
		try {
			String skey = getHashKey(key);			
			firmatari.remove(skey);
			
			// aggiorna le strutture ausiliarie...
			for(int i = listaFirmatari.size() - 1; i >= 0; i--) {
				if(skey.equals(getHashKey(listaFirmatari.get(i)))) {
					listaFirmatari.remove(i);
				}
			}
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "removeFirmatario");
		}
	}

	/**
	 * Restituisce il soggetto impresa associato a PIVA/CF del componente  
	 * @param key
	 * 		  chiave di riferimento (CF/PIVA) del firmatario da cercare
	 * @return 
	 * 		  il SoggettoImpresaHelper relativo al firmatario trovato 
	 */
	public SoggettoFirmatarioImpresaHelper getFirmatario(Object key) {
		SoggettoFirmatarioImpresaHelper firmatario = null;
		try {
			String skey = getHashKey(key);		
			for (Map.Entry<String, FirmatarioItem> item : firmatari.entrySet()) {
				if(item.getValue() != null) {
					FirmatarioItem f = (FirmatarioItem) item.getValue();
					String k;
					if(key instanceof IComponente) {
						k = getHashKey(f.componenteRTI);
					} else if(key instanceof IDatiPrincipaliImpresa) {
						k = getHashKey(f.componenteRTI);
					} else { 
						k = getHashKey(f.firmatario);
					}
					if(skey.equalsIgnoreCase(k) ) {
						firmatario = f.firmatario;
						break;
					}				
				}
			}
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getFirmatario");
		}
		return firmatario;
	}
	
	/**
	 * Restituisce il componente RTI a cui è associato il firmatario  
	 * @param firmatario
	 * 		  firmatario per cui trovare il componente RTI
	 * @return 
	 * 		  il IComponente relativo al firmatario  
	 */
	public IComponente getComponenteRTI(SoggettoImpresaHelper firmatario) {
		IComponente componente = null;
		try {
			String skey = getHashKey(firmatario);
			for (Map.Entry<String, FirmatarioItem> item : firmatari.entrySet()) {
				if(item.getValue() != null) {
					FirmatarioItem f = (FirmatarioItem) item.getValue();
					if(skey.equalsIgnoreCase(getHashKey(f.firmatario))) {
						componente = toIComponente(f.componenteRTI);
						break;
					}
				}
			}
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getComponenteRTI");
		}
		return componente;
	}

	/**
	 * converte un oggetto IDatiPrincipaliImpresa, ISoggettoImpresa, ... 
	 * in un IComponente 
	 */
	private IComponente toIComponente(Object obj) {
		IComponente componente = null;
		if(obj instanceof FirmatarioType) {
			FirmatarioType src = (FirmatarioType) obj;
			componente = new ComponenteHelper();
			componente.setCodiceFiscale(src.getCodiceFiscaleImpresa());
			componente.setPartitaIVA(src.getPartitaIVAImpresa());
			componente.setNazione(src.getNazione());
			componente.setQuota(null);
			componente.setRagioneSociale(src.getRagioneSociale());
			componente.setTipoImpresa(src.getTipoImpresa());
			
		} else if(obj instanceof IDatiPrincipaliImpresa) {
			IDatiPrincipaliImpresa src = (IDatiPrincipaliImpresa) obj;
			componente = new ComponenteHelper();
			componente.setCodiceFiscale(src.getCodiceFiscale());
			componente.setPartitaIVA(src.getPartitaIVA());
			componente.setNazione(src.getNazioneSedeLegale());
			componente.setRagioneSociale(src.getRagioneSociale());
			componente.setTipoImpresa(src.getTipoImpresa());
			
		} else if(obj instanceof ISoggettoImpresa) {
			ISoggettoImpresa src = (ISoggettoImpresa) obj;
			componente = new ComponenteHelper();
			componente.setCodiceFiscale(src.getCodiceFiscale());
			componente.setNazione(src.getNazione());
			componente.setRagioneSociale(StringUtils.trim(
									 (StringUtils.isNotEmpty(src.getCognome()) ? src.getCognome() : "") + " " + 
									 (StringUtils.isNotEmpty(src.getNome()) ? src.getNome() : "") ));
		} else {
			componente = (IComponente) obj;
		}
		return componente;
	}

}
