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
package com.agiletec.apsadmin.system.entity.attribute.manager;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.apsadmin.system.entity.attribute.AttributeTracer;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This abstract class is the base for the managers of all attributes.
 * For the 'complex' attributes this class must be directly extended, otherwise
 * -for 'simple' attributes- this class is extended by the managers of the 
 * 'mono-language' and 'multi-language' Attributes.
 * @author E.Santoboni
 */
public abstract class AbstractAttributeManager implements AttributeManagerInterface {
	
	@Override
	public void checkEntityAttribute(ActionSupport action, Map<String, 
			AttributeManagerInterface> attributeManagers, AttributeInterface attribute, IApsEntity entity) {
		this.setAttributeManagers(attributeManagers);
		this.checkAttribute(action, attribute, new AttributeTracer(), entity);
	}
	
	/**
	 * Basic method for attribute checking.
	 * This method knows in advance all the possible combinations of attributes in the system;
	 * if new combinations are needed is it necessary to modify this method and
	 * define the coherency checks and the control logic.
	 * This method must be modified, together with the 'tracer' class, if new 'complex'
	 * attributes are added so to properly manages the messages related to the new combinations
	 * available.
	 * With 'complex' attributes this method must be always extended.
	 *  
	 * @param action The action to fill with the appropriate errors, if any
	 * @param attribute The current attribute, both 'simple' or 'complex', to check.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one. 
	 * @param entity The entity to check.
	 */
	protected void checkAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		if (tracer.isMonoListElement()) {
			if (tracer.isCompositeElement()) {
				this.checkMonoListCompositeElement(action, attribute, tracer, entity);
			} else {
				this.checkMonoListElement(action, attribute, tracer, entity);
			}
		} else if (tracer.isListElement()) {
			this.checkListElement(action, attribute, tracer, entity);
		} else {
			this.checkSingleAttribute(action, attribute, tracer, entity);
		}
	}
	
	/**
	 * Check the simple attribute when it is inserted as an element of a 'composite' attribute
	 * in a monolist.
	 * Extend this method in the helper class of the attribute which, due to its nature, 
	 * requires further checks other the "general" (standard) ones,
	 * when inserted in a composite attribute of a monolist. 
	 * 
	 * @param action The action to fill with the proper error messages, if any.
	 * @param attribute The current Attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param entity The entity to check.
	 */
	protected void checkMonoListCompositeElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		if (!this.isValidMonoListCompositeElement(attribute, tracer)) {
			this.addFieldError(action, attribute, tracer, this.getMonoListCompositeElementNotValidMessage(), null);
		} else if (attribute.isRequired() && this.getState(attribute, tracer) != VALUED_ATTRIBUTE_STATE) {
			this.addFieldError(action, attribute, tracer, this.getRequiredAttributeMessage(), null);
		}
	}
	
	/**
	 * This method defines a standard of validity for an attribute inserted as an
	 * element of a composite attribute in a monolist.
	 * This method must be extended in all the attribute managers which must
	 * enforce the standard validity checks with additional controls when inserting
	 * a new element of a composite attribute in a monolist.
	 * 
	 * @param attribute The current attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @return true If the element is valid, false otherwise.
	 */
	protected boolean isValidMonoListCompositeElement(AttributeInterface attribute, AttributeTracer tracer) {
		return this.getState(attribute, tracer) != INCOMPLETE_ATTRIBUTE_STATE;
	}
	
	/**
	 * This return the key of the message to return if the attribute of a composite
	 * element in a monolist is not valid.
	 * If the nature of the method requires particular message is necessary to override this method.
	 * 
	 * @return The message to return.
	 */
	protected String getMonoListCompositeElementNotValidMessage() {
		return this.getInvalidAttributeMessage();
	}

	/**
	 * Check the 'simple' attribute when inserted as element in a monolist.
	 * Extend this method in the helper class of the attribute which, due to its nature, 
	 * requires further checks other the "general" (standard) ones,
	 * when inserted as element of a monolist.
	 * 
	 * @param action The action to fill with the proper error messages, if any.
	 * @param attribute The current attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param entity The entity to check.
	 */
	protected void checkMonoListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		if (!this.isValidMonoListElement(attribute, tracer)) {
			this.addFieldError(action, attribute, tracer, this.getMonoListElementNotValidMessage(), null);
		}
	}
	
	/**
	 * This method defines a standard of validity for an attribute inserted as an
	 * element in a monolist.
	 * This method must be extended in all the attribute managers which must
	 * enforce the standard validity checks with additional controls when inserting
	 * a new element in a monolist.
	 * 
	 * @param attribute The current attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @return true If the element is valid, false otherwise.
	 */
	protected boolean isValidMonoListElement(AttributeInterface attribute, AttributeTracer tracer) {
		return this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE;
	}
	
	/**
	 * This return the key of the message to return if the attribute element in a 
	 * monolist is not valid. If the nature of the method requires particular message
	 * is necessary to override this method. 
	 * 
	 * @return The message to return.
	 */
	protected String getMonoListElementNotValidMessage() {
		return this.getInvalidAttributeMessage();
	}
	
	/**
	 * Check the validity of the simple attribute when it is inserted in a multi-language list.
	 * Extend this method in the helper class of the attribute which, due to its nature, 
	 * requires further checks other the "general" (standard) ones, when inserted as 
	 * element of a multi-language list.
	 * 
	 * @param action The action to fill with the proper error messages, if any.
	 * @param attribute The current attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param entity The entity to check.
	 */
	protected void checkListElement(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		if (!this.isValidListElement(attribute, tracer)) {
			this.addFieldError(action, attribute, tracer, this.getListElementNotValidMessage(), null);
		}
	}
	
	/**
	 * This method defines a standard of validity for an attribute inserted as an
	 * element in a multi-language list.
	 * This method must be extended in all the attribute managers which must
	 * enforce the standard validity checks with additional controls when inserting
	 * a new element in a multi-language list.
	 * 
	 * @param attribute The current attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @return true If the element is valid, false otherwise.
	 */
	protected boolean isValidListElement(AttributeInterface attribute, AttributeTracer tracer) {
		return this.getState(attribute, tracer) == VALUED_ATTRIBUTE_STATE;
	}
	
	/**
	 * This return the key of the message to return if the attribute element in a 
	 * multi-language list is not valid.
	 * If the nature of the method requires particular message is necessary to override this method. 
	 * 
	 * @return The message to return.
	 */
	protected String getListElementNotValidMessage() {
		return this.getInvalidAttributeMessage();
	}
	
	/**
	 * Return the key of the message to retrieve when an attribute is not valid.
	 * If a customized message is needed eg. due to the nature of the attribute, extend this method.
	 * @return The key of the message to return. 
	 */
	protected String getInvalidAttributeMessage() {
		return "EntityAttribute.fieldError.invalidAttribute";
	}
	
	/**
	 * This method implements the validity criteria of an attribute, both simple or
	 * complex; the only check performed here is whether the attribute is mandatory or not. 
	 * If the attribute needs further checks other than those by default, extend this method.
	 * 
	 * @param action The action to fill with the proper error messages, if any.
	 * @param attribute The current attribute to check, both 'simple' or 'complex'.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param entity The entity to check.
	 */
	protected void checkSingleAttribute(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, IApsEntity entity) {
		if (attribute.isRequired() && this.getState(attribute, tracer) == EMPTY_ATTRIBUTE_STATE) {
			this.addFieldError(action, attribute, tracer, this.getRequiredAttributeMessage(), null);
		}
	}
	
	/**
	 * Return the key of the message used when a mandatory attribute is not populated.
	 * If a customized message is needed eg. due to the nature of the attribute, extend this method.
	 * 
	 * @return The key of the message to return.
	 */
	protected String getRequiredAttributeMessage() {
		return "EntityAttribute.fieldError.required";
	}
	
	/**
	 * Add an error message related to a field in a form.
	 * 
	 * @param action The action where the error message is added
	 * @param fieldName The name of the field.
	 * @param messageKey The key of the error message.
	 * @param args The arguments of the error message.
	 */
	protected void addFieldError(ActionSupport action, String fieldName, String messageKey, String[] args) {
		action.addFieldError(fieldName, action.getText(messageKey, args));
	}
	
	/**
	 * Add an error message related to a field in a form.
	 * 
	 * @param action The action where the error message is added.
	 * @param attribute The current attribute (simple or complex) to which the error message is appended.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param messageKey The key of the error message.
	 * @param args The arguments of the error message.
	 */
	protected void addFieldError(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer, String messageKey, String[] args) {
		String messageAttributePositionPrefix = this.createErrorMessageAttributePositionPrefix(action, attribute, tracer);
		String messageError = null;
		if (args != null) {
			messageError = action.getText(messageKey, args);
		} else {
			messageError = action.getText(messageKey);
		}
		String formFieldName = tracer.getFormFieldName(attribute);
		action.addFieldError(formFieldName, messageAttributePositionPrefix + " " + messageError);
	}
	
	private String createErrorMessageAttributePositionPrefix(ActionSupport action, AttributeInterface attribute, AttributeTracer tracer) {
		if (tracer.isMonoListElement()) {
			if (tracer.isCompositeElement()) {
				String[] args = {tracer.getParentAttribute().getName(), String.valueOf(tracer.getListIndex()+1), attribute.getName()};
				return action.getText("EntityAttribute.compositeListAttributeElement.errorMessage.prefix", args);
			} else {
				String[] args = {attribute.getName(), String.valueOf(tracer.getListIndex()+1)};
				return action.getText("EntityAttribute.monolistAttributeElement.errorMessage.prefix", args);
			}
		} else if (tracer.isCompositeElement()) {
			String[] args = {tracer.getParentAttribute().getName(), attribute.getName()};
			return action.getText("EntityAttribute.compositeAttributeElement.errorMessage.prefix", args);
		} else if (tracer.isListElement()) {
			String[] args = {attribute.getName(), tracer.getListLang().getDescr(), String.valueOf(tracer.getListIndex()+1)};
			return action.getText("EntityAttribute.listAttributeElement.errorMessage.prefix", args);
		} else {
			String[] args = {attribute.getName()};
			return action.getText("EntityAttribute.singleAttribute.errorMessage.prefix", args);
		}
	}
	
	/**
	 * Return the status of the current attribute.
	 * The status can be: EMPTY, INCOMPLETE, POPULATED.
	 * INCOMPLETE applies only to those simple attributes composed by two or more elements
	 * (eg. the 'image' attribute is composed by a resource and a description).
	 * This method applies to both simple and complex attributes.
	 * This check does neither imply that the attribute is valid nor complete: eg. for
	 * the 'number' attribute this method does not check for the given string to be a number,
	 * but it rather checks for the presence of the string; in the List attribute at least one
	 * element -whether correct or not- must be present. 
	 * 
	 * @param attribute The current attribute (simple or complex) to check.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @return The code representing the current status
	 */
	protected abstract int getState(AttributeInterface attribute, AttributeTracer tracer);
	
	@Override
	public void updateEntityAttribute(AttributeInterface attribute, Map<String, AttributeManagerInterface> attributeManagers, HttpServletRequest request) {
		this.setAttributeManagers(attributeManagers);
		this.updateAttribute(attribute, new AttributeTracer(), request);
	}
	
	/**
	 * Updates the attribute with the criteria specified in the content editing form.
	 * 
	 * @param attribute The current attribute (simple or complex) to check.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param request The request.
	 */
	protected abstract void updateAttribute(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request);
	
	/**
	 * Return the value of the current attribute passed from the form.
	 * 
	 * @param attribute The current attribute (simple or complex) to check.
	 * @param tracer The 'tracer' class needed to find the position of the attribute inside a 'composite' one.
	 * @param request The request.
	 * @return The value passed in the form
	 */
	protected String getValueFromForm(AttributeInterface attribute, AttributeTracer tracer, HttpServletRequest request) {
		String formFieldName = tracer.getFormFieldName(attribute);
		return request.getParameter(formFieldName);
	}
	
	protected AttributeManagerInterface getManager(String typeCode) {
		AbstractAttributeManager manager = (AbstractAttributeManager) this.getAttributeManagers().get(typeCode);
		AbstractAttributeManager clone = null;
		try {
			Class attributeClass = Class.forName(manager.getClass().getName());
			clone = (AbstractAttributeManager) attributeClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not clone the attribute manager '" + this.getClass().getName() + "'");
		}
		clone.setAttributeManagers(this.getAttributeManagers());
		manager.setExtraPropertyTo(clone);
		return clone;
	}
	
	/**
	 * Set the extra properties in the given manager.
	 * This method is used when creating a manager to handle the attribute element of a complex
	 * attribute and must be implemented when setting extra attributes.
	 * @param manager The manager to create.
	 */
	protected void setExtraPropertyTo(AttributeManagerInterface manager) {
		//nothing to do
	}
	
	private Map<String, AttributeManagerInterface> getAttributeManagers() {
		return this._attributeManagers;
	}
	
	private void setAttributeManagers(Map<String, AttributeManagerInterface> attributeManagers) {
		this._attributeManagers = attributeManagers;
	}
	
	private Map<String, AttributeManagerInterface> _attributeManagers;
	
	/**
	 * Constant code describing the status of the empty attribute.
	 */
	protected final int EMPTY_ATTRIBUTE_STATE = 0;
	
	/**
	 * Constant code describing the status of the incomplete attribute (not properly populated). Please note
	 * that this status cannot be never accepted by the system.
	 */
	protected final int INCOMPLETE_ATTRIBUTE_STATE = 1;
	
	/**
	 * Constant code describing the status of the valued attribute.
	 */
	protected final int VALUED_ATTRIBUTE_STATE = 2;
	
}