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
package com.agiletec.plugins.jacms.aps.system.services.resource.model;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.imageresizer.IImageResizer;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.util.IImageDimensionReader;

/**
 * Classe rappresentante una risorsa Image.
 * @author W.Ambu - E.Santoboni
 */
public class ImageResource extends AbstractMultiInstanceResource {
	
    /**
     * Restituisce il path dell'immagine (relativa al size immesso).
     * La stringa restituita è comprensiva del folder della risorsa e 
     * del nome del file dell'istanza richiesta.
     * @param size Il size dell'istanza.
     * @return Il path dell'immagine.
     */
    public String getImagePath(String size) {
    	ResourceInstance instance = (ResourceInstance) this.getInstances().get(size);
    	String path = this.getUrlPath(instance);
    	return path;
    }
    
    @Override
	public ResourceInterface getResourcePrototype() {
		ImageResource resource = (ImageResource) super.getResourcePrototype();
		resource.setImageDimensionReader(this.getImageDimensionReader());
		resource.setImageResizerClasses(this.getImageResizerClasses());
		return resource;
	}
    
	/**
     * Aggiunge un'istanza alla risorsa, indicizzandola in base 
	 * al size dell'istanza sulla mappa delle istanze.
     * @param instance L'istanza da aggiungere alla risorsa.
     */
    @Override
	public void addInstance(ResourceInstance instance) {
    	String key = String.valueOf(instance.getSize());
    	this.getInstances().put(key, instance);
    }
    
    @Override
	public String getInstanceFileName(String masterFileName, int size, String langCode) {
    	String instanceFileName = this.getRevisedInstanceFileName(masterFileName);
    	String baseName = instanceFileName.substring(0, instanceFileName.indexOf("."));
    	String extension = instanceFileName.substring(instanceFileName.lastIndexOf('.')+1).trim();
    	StringBuffer fileName = new StringBuffer(baseName);
    	if (size >= 0) {
    		fileName.append("_d").append(size);
    	}
    	if (langCode != null) {
    		fileName.append("_").append(langCode);
    	}
    	fileName.append(".").append(extension);
    	return fileName.toString();
	}
    
    @Override
	public ResourceInstance getInstance(int size, String langCode) {
    	return (ResourceInstance) this.getInstances().get(String.valueOf(size));
	}
    
    @Override
	public void saveResourceInstances(ResourceDataBean bean) throws ApsSystemException {
		String masterImageFileName = this.getInstanceFileName(bean.getFileName(), 0, null);
		String baseDiskFolder = this.getInstanceHelper().getResourceDiskFolder(this);
		String masterFilePath = baseDiskFolder + masterImageFileName;
		this.getInstanceHelper().save(masterFilePath, bean);
    	ResourceInstance instance = new ResourceInstance();
    	instance.setSize(0);
    	instance.setFileName(masterImageFileName);
    	
    	String mimeType = bean.getMimeType();
    	instance.setMimeType(mimeType);
    	instance.setFileLength(bean.getFileSize() + " Kb");
    	this.addInstance(instance);
    	
    	//viene utilizzata per fare i diversi resize
    	ImageIcon imageIcon = new ImageIcon(masterFilePath);
    	
    	Map<Integer, ImageResourceDimension> dimensions = this.getImageDimensionReader().getImageDimensions();
    	Iterator<ImageResourceDimension> iterDimensions = dimensions.values().iterator();
    	
    	while (iterDimensions.hasNext()) {
    		ImageResourceDimension dimension = iterDimensions.next();
    		this.saveResizedImage(bean, imageIcon, dimension, mimeType, baseDiskFolder);
    	}
    }
	
	private void saveResizedImage(ResourceDataBean bean, ImageIcon imageIcon, 
			ImageResourceDimension dimension, String mimeType, String baseDiskFolder) throws ApsSystemException {
		if (dimension.getIdDim() == 0) {
			//salta l'elemento con id zero che non va ridimensionato
			return;
		}
		String imageName = this.getInstanceFileName(bean.getFileName(), dimension.getIdDim(), null);
		String filePath = baseDiskFolder + imageName;
		
		IImageResizer resizer = this.getImageResizer(filePath);
		resizer.saveResizedImage(imageIcon, filePath, dimension);
		
		ResourceInstance resizedInstance = new ResourceInstance();
		resizedInstance.setSize(dimension.getIdDim());
		resizedInstance.setFileName(imageName);
		resizedInstance.setMimeType(mimeType);
		File fileTemp = new File(filePath);
		long realLength = fileTemp.length()/1000;
		resizedInstance.setFileLength(String.valueOf(realLength) + " Kb");
		this.addInstance(resizedInstance);
	}
	
	private IImageResizer getImageResizer(String filePath) {
		String extension = this.getInstanceHelper().getFileExtension(filePath).toLowerCase();
		String resizerClassName = this.getImageResizerClasses().get(extension);
		if (null == resizerClassName) {
			resizerClassName = this.getImageResizerClasses().get("DEFAULT_RESIZER");
		}
		try {
			Class resizerClass = Class.forName(resizerClassName);
			return (IImageResizer) resizerClass.newInstance();
		} catch (Throwable t) {
			throw new RuntimeException("Errore in creazione resizer da classe '" 
					+ resizerClassName + "' per immagine tipo '" + extension + "'", t);
		}
	}
    
    protected IImageDimensionReader getImageDimensionReader() {
		return _imageDimensionReader;
	}
	public void setImageDimensionReader(IImageDimensionReader imageDimensionReader) {
		this._imageDimensionReader = imageDimensionReader;
	}
    
	protected Map<String, String> getImageResizerClasses() {
		return _imageResizerClasses;
	}
	public void setImageResizerClasses(Map<String, String> imageResizerClasses) {
		this._imageResizerClasses = imageResizerClasses;
	}
    
    private IImageDimensionReader _imageDimensionReader;
    
    private Map<String, String> _imageResizerClasses;
	
}