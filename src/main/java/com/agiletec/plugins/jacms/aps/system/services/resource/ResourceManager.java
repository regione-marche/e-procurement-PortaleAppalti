/*
*
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
* This file is part of jAPS software.
* jAPS is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.aps.system.services.resource;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.category.CategoryUtilizer;
import com.agiletec.aps.system.services.category.ICategoryManager;
import com.agiletec.aps.system.services.group.GroupUtilizer;
import com.agiletec.aps.system.services.keygenerator.IKeyGeneratorManager;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceDataBean;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceRecordVO;
import com.agiletec.plugins.jacms.aps.system.services.resource.parse.ResourceHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Servizio gestore tipi di risorse (immagini, audio, video, etc..).
 * @author W.Ambu - E.Santoboni
 */
public class ResourceManager extends AbstractService 
		implements IResourceManager, GroupUtilizer, CategoryUtilizer {
	
	@Override
	public void init() throws Exception {
    	ApsSystemUtils.getLogger().debug(this.getClass().getName() + 
        		": inizializzati " + _resourceTypes.size() + " tipi di risorsa");
	}
	
	/**
     * Crea una nuova istanza di un tipo di risorsa del tipo richiesto. Il nuovo
     * tipo di risorsa è istanziato mediante clonazione del prototipo corrispondente.
     * @param typeCode Il codice del tipo di risorsa richiesto, come definito in configurazione.
     * @return Il tipo di risorsa istanziato (vuoto).
     */
	@Override
	public ResourceInterface createResourceType(String typeCode) {
    	ResourceInterface resource = (ResourceInterface) _resourceTypes.get(typeCode);
        return resource.getResourcePrototype();
    }
    
    /**
     * Restituisce la lista delle chiavi dei tipi risorsa presenti nel sistema.
     * @return La lista delle chiavi dei tipi risorsa esistenti.
     */
	@Override
	public List<String> getResourceTypeCodes() {
    	return new ArrayList<String>(this._resourceTypes.keySet());
    }
    
    /**
     * Salva una risorsa nel db con incluse nel filesystem, indipendentemente dal tipo.
     * @param bean L'oggetto detentore dei dati della risorsa da inserire.
     * @throws ApsSystemException
     */
	@Override
	public ResourceInterface addResource(ResourceDataBean bean) throws ApsSystemException {
    	ResourceInterface resource = this.createResourceType(bean.getResourceType());
    	try {
    		resource.setDescr(bean.getDescr());
    		resource.setMainGroup(bean.getMainGroup());
    		resource.setCategories(bean.getCategories());
    		resource.saveResourceInstances(bean);
    		this.addResource(resource);
    	} catch (Throwable t) {
    		resource.deleteResourceInstances();
			throw new ApsSystemException("Errore in salvataggio risorsa", t);
    	}
    	return resource;
    }
    
    /**
     * Salva una risorsa nel db, indipendentemente dal tipo.
     * @param resource La risorsa da salvare.
     * @throws ApsSystemException in caso di errore nell'accesso al db.
     */
	@Override
	public void addResource(ResourceInterface resource) throws ApsSystemException {
    	try {
    		IKeyGeneratorManager keyGenerator = (IKeyGeneratorManager) this.getService(SystemConstants.KEY_GENERATOR_MANAGER);
			int id = keyGenerator.getUniqueKeyCurrentValue();
    		resource.setId(String.valueOf(id));
    		this.getResourceDAO().addResource(resource);
    	} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addResource");
			throw new ApsSystemException("Errore in salvataggio risorsa", t);
    	}
    }
    
    /**
	 * Aggiorna una risorsa nel db.
	 * @param resource Il contenuto da aggiungere o modificare.
	 * @throws ApsSystemException in caso di errore nell'accesso al db.
	 */	
	@Override
	public void updateResource(ResourceInterface resource) throws ApsSystemException {
		try {
			this.getResourceDAO().updateResource(resource);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateResource");
			throw new ApsSystemException("Errore in aggiornamento Risorsa", t);
		}
	}
	
    /**
     * Carica una lista di identificativi di risorse 
	 * in base al tipo, ad una parola chiave e dalla categoria della risorsa. 
	 * @param type Tipo di risorsa da cercare.
	 * @param text Testo immesso per il raffronto con la descrizione della risorsa. null o 
	 * stringa vuota nel caso non si voglia ricercare le risorse per parola chiave. 
	 * @param categoryCode Il codice della categoria delle risorse. null o 
	 * stringa vuota nel caso non si voglia ricercare le risorse per categoria.
	 * @param groupCodes I codici dei gruppi utenti consentiti tramite il quale 
	 * filtrare le risorse. Nel caso che la collezione di codici sia nulla o vuota, 
	 * non verrà eseguito la selezione per gruppi.
     * @return La lista di identificativi di risorse.
     * @throws ApsSystemException In caso di errore nell'accesso al db.
     */
	@Override
	public List<String> searchResourcesId(String type, String text, 
    		String categoryCode, Collection<String> groupCodes) throws ApsSystemException {
    	List<String> resources = null;
    	try {
    		resources = this.getResourceDAO().searchResourcesId(type, text, categoryCode, groupCodes);
    	} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "searchResourcesId");
			throw new ApsSystemException("Errore in estrazione id risorsa", t);
    	}
    	return resources;
    }
    
    /**
     * Restituisce la risorsa con l'id specificato.
     * @param id L'identificativo della risorsa da caricare.
     * @return La risorsa cercata. null se non vi è nessuna risorsa con l'identificativo immesso.
     * @throws ApsSystemException in caso di errore nell'accesso al db.
     */
	@Override
	public ResourceInterface loadResource(String id) throws ApsSystemException {
    	ResourceInterface resource = null;
    	try {
    		ResourceRecordVO resourceVo = this.getResourceDAO().loadResourceVo(id);
    		if (null != resourceVo) {
    			resource = this.createResource(resourceVo);
    		}
    	} catch (ApsSystemException e) {
    		throw e;
    	}
    	return resource;
    }
    
    /**
     * Metodo di servizio. Restituisce una risorsa 
     * in base ai dati del corrispondente record del db.
     * @param resourceVo Il vo relativo al record del db.
     * @return La risorsa valorizzata.
     * @throws ApsSystemException
     */
    private ResourceInterface createResource(ResourceRecordVO resourceVo) throws ApsSystemException {
		String resourceType = resourceVo.getResourceType();
		String resourceXML = resourceVo.getXml();
		ResourceInterface resource = this.createResourceType(resourceType);
		this.fillEmptyResourceFromXml(resource, resourceXML);
		resource.setMainGroup(resourceVo.getMainGroup());
		return resource;
	}
    
    /**
     * Valorizza una risorsa prototipo con gli elementi 
     * dell'xml che rappresenta una risorsa specifica. 
     * @param resource Il prototipo di risorsa da specializzare con gli attributi dell'xml.
     * @param xml L'xml della risorsa specifica. 
     * @throws ApsSystemException
     */	
    protected void fillEmptyResourceFromXml(ResourceInterface resource, String xml) throws ApsSystemException {
    	try {
			SAXParserFactory parseFactory = SAXParserFactory.newInstance();
			parseFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    		SAXParser parser = parseFactory.newSAXParser();
    		InputSource is = new InputSource(new StringReader(xml));
    		ResourceHandler handler = new ResourceHandler(resource, this.getCategoryManager());
    		parser.parse(is, handler);
    	} catch (Throwable t) {
    		ApsSystemUtils.logThrowable(t, this, "fillEmptyResourceFromXml");
    		throw new ApsSystemException("Errore in caricamento risorsa", t);
    	}
    }
    
    /**
     * Cancella una risorsa dal db ed i file di ogni istanza dal filesystem.
     * @param resource La risorsa da cancellare.
     * @throws ApsSystemException in caso di errore nell'accesso al db.
     */
    @Override
	public void deleteResource(ResourceInterface resource) throws ApsSystemException {
    	try {
    		this.getResourceDAO().deleteResource(resource.getId());
    		resource.deleteResourceInstances();
    	} catch (Throwable t) {
			throw new ApsSystemException("Errore in cancellazione risorsa", t);
    	}
    }
    
    @Override
	public List getGroupUtilizers(String groupName) throws ApsSystemException {
    	List<ResourceInterface> utilizers = new ArrayList<ResourceInterface>();
    	List<String> allowedGroups = new ArrayList<String>(1);
    	allowedGroups.add(groupName);
    	List<String> resourcesId = this.searchResourcesId("", "", "", allowedGroups);
    	for (int i=0; i<resourcesId.size(); i++) {
    		String id = resourcesId.get(i);
    		ResourceInterface resource = this.loadResource(id);
    		if (null != resource) utilizers.add(resource);
    	}
    	return utilizers;
	}
    
    @Override
	public List getCategoryUtilizers(String categoryCode) throws ApsSystemException {
    	List<ResourceInterface> utilizers = new ArrayList<ResourceInterface>();
    	List<String> resourcesId = this.searchResourcesId("", "", categoryCode, null);
    	for (int i=0; i<resourcesId.size(); i++) {
    		String id = resourcesId.get(i);
    		ResourceInterface resource = this.loadResource(id);
    		if (null != resource) utilizers.add(resource);
    	}
    	return utilizers;
	}
    
	/**
     * Restutuisce il dao in uso al manager.
     * @return Il dao in uso al manager.
     */
    public IResourceDAO getResourceDAO() {
		return _resourceDao;
	}
    
    /**
     * Setta il dao in uso al manager.
     * @param resourceDao Il dao in uso al manager.
     */
	public void setResourceDAO(IResourceDAO resourceDao) {
		this._resourceDao = resourceDao;
	}
	
	public void setResourceTypes(Map<String, ResourceInterface> resourceTypes) {
		this._resourceTypes = resourceTypes;
	}
    
	protected ICategoryManager getCategoryManager() {
		return _categoryManager;
	}
	public void setCategoryManager(ICategoryManager categoryManager) {
		this._categoryManager = categoryManager;
	}
	
	/**
     * Mappa dei prototipi dei tipi di risorsa
     */
    private Map<String, ResourceInterface> _resourceTypes;
    
    private IResourceDAO _resourceDao;
    
    private ICategoryManager _categoryManager;
    
}