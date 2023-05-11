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
package com.agiletec.aps.system.common.renderer;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.util.EntityAttributeIterator;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.i18n.I18nManagerWrapper;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;

/**
 * Entities rendering service.
 * @author M.Diana - W.Ambu - E.Santoboni
 */
public abstract class BaseEntityRenderer extends AbstractService implements IEntityRenderer, LogChute {

	@Override
	public void init() throws Exception {
		try {
			Velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
			Velocity.init();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "init");
			throw new ApsSystemException("Error initializing the VelocityEngine", t);
		}
		ApsSystemUtils.getLogger().debug(this.getName() + ": initialized");
	}

	@Override
	public String render(IApsEntity entity, String velocityTemplate, String langCode, boolean convertSpecialCharacters) {
		String renderedEntity = null;
		try {
			String conversionKey = null;
			if (convertSpecialCharacters) {
				conversionKey = this.convertSpecialCharacters(entity, langCode);
			}
			Context velocityContext = new VelocityContext();
			EntityWrapper entityWrapper = this.getEntityWrapper(entity);
			entityWrapper.setRenderingLang(langCode);
			velocityContext.put(this.getEntityWrapperContextName(), entityWrapper);

			I18nManagerWrapper i18nWrapper = new I18nManagerWrapper(langCode, this.getI18nManager());
			velocityContext.put("i18n", i18nWrapper);
			StringWriter stringWriter = new StringWriter();
			boolean isEvaluated = Velocity.evaluate(velocityContext, stringWriter, "render", velocityTemplate);
			if (!isEvaluated) {
				throw new ApsSystemException("Rendering error");
			}
			stringWriter.flush();
			renderedEntity = stringWriter.toString();
			if (convertSpecialCharacters) {
				this.replaceSpecialCharacters(conversionKey);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "render", "Rendering error");
			renderedEntity = "";
		} finally {
			this.getCharConversions().clear();
		}
		return renderedEntity;
	}

	protected abstract EntityWrapper getEntityWrapper(IApsEntity entity);

	protected String convertSpecialCharacters(IApsEntity entity, String langCode) {
		String entityId = (entity.getId() == null ? "***nullEntityId***" : entity.getId());
		List<TextAttributeCharReplaceInfo> configs = new ArrayList<TextAttributeCharReplaceInfo>();
		String conversionKey = entityId + System.currentTimeMillis();
		int index = 0;
		do {
			conversionKey += (index++);
		} while (null != this.getCharConversions().get(conversionKey));
		this.getCharConversions().put(conversionKey, configs);
		Lang defaultLang = this.getLangManager().getDefaultLang();
		EntityAttributeIterator attributeIter = new EntityAttributeIterator(entity);
		while (attributeIter.hasNext()) {
			AttributeInterface currAttribute = (AttributeInterface) attributeIter.next();
			if (currAttribute instanceof ITextAttribute) {
				String attributeLangCode = langCode;
				ITextAttribute renderizable = (ITextAttribute) currAttribute;
				if (renderizable.needToConvertSpecialCharacter()) {
					String textToConvert = renderizable.getTextForLang(attributeLangCode);
					if (null == textToConvert || textToConvert.trim().length() == 0) {
						attributeLangCode = defaultLang.getCode();
						textToConvert = renderizable.getTextForLang(attributeLangCode);
					}
					if (null != textToConvert && textToConvert.trim().length() > 0) {
						configs.add(new TextAttributeCharReplaceInfo(renderizable, textToConvert, attributeLangCode));
						String convertedText = this.convertSpecialCharacter(textToConvert);
						renderizable.setText(convertedText, attributeLangCode);
					}
				}
			}
		}
		return conversionKey;
	}

	protected String convertSpecialCharacter(String textToConvert) {
		String text = textToConvert;
		if (text != null && text.length()>0) {
			for (int i=0; i<this.getHtmlSpecialCharters().size(); i++) {
				HtmlSpecialCharacter specChar = this.getHtmlSpecialCharters().get(i);
				text = text.replaceAll(specChar.getCharacter(), specChar.getHtml());
			}
		}
		return text;
	}

	protected void replaceSpecialCharacters(String conversionKey) {
		List<TextAttributeCharReplaceInfo> conversions = this.getCharConversions().get(conversionKey);
		this.getCharConversions().remove(conversionKey);
		for (int i = 0; i < conversions.size(); i++) {
			TextAttributeCharReplaceInfo conversion = conversions.get(i);
			conversion.restore();
		}
	}

	@Override
	public void init(RuntimeServices rs) {
		//non fa nulla
	}

	@Override
	public boolean isLevelEnabled(int level) {
//		Level actualLevel = this.getLoggerLevel(level);
//		boolean isLevelEnabled = ApsSystemUtils.getLogger().isLoggable(actualLevel);
		boolean isLevelEnabled = false;
		switch (level) {
		case LogChute.ERROR_ID:
			isLevelEnabled = ApsSystemUtils.getLogger().isErrorEnabled();
			break;
		case LogChute.WARN_ID:
			isLevelEnabled = ApsSystemUtils.getLogger().isWarnEnabled();
			break;
		case LogChute.INFO_ID:
			isLevelEnabled = ApsSystemUtils.getLogger().isInfoEnabled();
			break;
		case LogChute.DEBUG_ID:
			isLevelEnabled = ApsSystemUtils.getLogger().isDebugEnabled();
			break;
		case LogChute.TRACE_ID:
			isLevelEnabled = ApsSystemUtils.getLogger().isTraceEnabled();
			break;
		}
		return isLevelEnabled;
	}

	@Override
	public void log(int level, String message) {
		this.log(level, message, null);
	}

	@Override
	public void log(int level, String message, Throwable t) {
		//Level actualLevel = this.getLoggerLevel(level);
		//if (ApsSystemUtils.getLogger().isLoggable(actualLevel)) {
		if (this.isLevelEnabled(level)) {
			if (t == null) {
//				ApsSystemUtils.getLogger().log(actualLevel, message);
				switch (level) {
				case LogChute.ERROR_ID:
					ApsSystemUtils.getLogger().error(message);
					break;
				case LogChute.WARN_ID:
					ApsSystemUtils.getLogger().warn(message);
					break;
				case LogChute.INFO_ID:
					ApsSystemUtils.getLogger().info(message);
					break;
				case LogChute.DEBUG_ID:
					ApsSystemUtils.getLogger().debug(message);
					break;
				case LogChute.TRACE_ID:
					ApsSystemUtils.getLogger().trace(message);
					break;
				}				
			} else {
//				ApsSystemUtils.getLogger().log(actualLevel, message, t);
				switch (level) {
				case LogChute.ERROR_ID:
					ApsSystemUtils.getLogger().error(message, t);
					break;
				case LogChute.WARN_ID:
					ApsSystemUtils.getLogger().warn(message, t);
					break;
				case LogChute.INFO_ID:
					ApsSystemUtils.getLogger().info(message, t);
					break;
				case LogChute.DEBUG_ID:
					ApsSystemUtils.getLogger().debug(message, t);
					break;
				case LogChute.TRACE_ID:
					ApsSystemUtils.getLogger().trace(message, t);
					break;
				}
			}
		}
	}

//	private Level getLoggerLevel(int logChuteLevel) {
//		Level actualLevel = Level.FINE;
//		switch (logChuteLevel) {
//		case LogChute.WARN_ID:
//			actualLevel = Level.WARNING;
//			break;
//		case LogChute.INFO_ID:
//			actualLevel = Level.INFO;
//			break;
//		case LogChute.DEBUG_ID:
//			actualLevel = Level.INFO;
//			break;
//		case LogChute.ERROR_ID:
//			actualLevel = Level.SEVERE;
//			break;
//		}
//		return actualLevel;
//	}

	protected List<HtmlSpecialCharacter> getHtmlSpecialCharters() {
		return _htmlSpecialCharters;
	}
	public void setHtmlSpecialCharters(List<HtmlSpecialCharacter> htmlSpecialCharters) {
		this._htmlSpecialCharters = htmlSpecialCharters;
	}

	protected II18nManager getI18nManager() {
		return _i18nManager;
	}
	public void setI18nManager(II18nManager i18nManager) {
		this._i18nManager = i18nManager;
	}

	protected ILangManager getLangManager() {
		return _langManager;
	}
	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}

	protected String getEntityWrapperContextName() {
		if (null == this._entityWrapperContextName) {
			return DEFAULT_ENTITY_WRAPPER_CTX_NAME;
		}
		return _entityWrapperContextName;
	}
	public void setEntityWrapperContextName(String entityWrapperContextName) {
		this._entityWrapperContextName = entityWrapperContextName;
	}

	protected Map<String, List<TextAttributeCharReplaceInfo>> getCharConversions() {
		return _charConversions;
	}

	private List<HtmlSpecialCharacter> _htmlSpecialCharters;

	private II18nManager _i18nManager;
	private ILangManager _langManager;

	private String _entityWrapperContextName;

	private Map<String, List<TextAttributeCharReplaceInfo>> _charConversions = new HashMap<String, List<TextAttributeCharReplaceInfo>>();

	protected static final String DEFAULT_ENTITY_WRAPPER_CTX_NAME = "entity";

}