package com.agiletec.apsadmin.system;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.ValueStack;
import it.maggioli.eldasoft.plugins.ppcommon.aps.XSSValidation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * ... 
 */
public class XSSParameterInterceptor extends ParametersInterceptor {

	private static final Logger log = LoggerFactory.getLogger(XSSParameterInterceptor.class);

	/**
	 * Reflected CrossSite Scripting filter 
	 */
		
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String result = this.doIntercept(invocation);
		
		super.setAcceptParamNames("[a-zA-Z0-9\\.\\]\\[\\(\\)_'\\s]+");
		return result;
	}
	
	@Override
	public void setParameters(Object action, ValueStack stack, final Map<String, Object> parameters) {
		parameters.entrySet()
				.stream()
				.filter(XSSValidation::hasToBeChecked)
				.filter(XSSValidation::isNotValid)
			.forEach(this::setEmptyValue);

		super.setParameters(action, stack, parameters);
	}

	private void setEmptyValue(Map.Entry<String, Object> param) {
		if (param.getValue().getClass().isArray()) {
			log.error("The values: {}; for the field {} are not valid", StringUtils.join((Object[]) param.getValue(), ","), param.getKey());
			param.setValue(Array.newInstance(param.getValue().getClass(), 0));
		} else {
			log.error("The value {} for the field {} is not valid", param.getValue(), param.getKey());
			param.setValue("");
		}
	}
	
	@Override
	protected boolean isAccepted(String paramName){
		return Pattern.compile("[a-zA-Z0-9\\.\\]\\[\\(\\)_'\\s]+").matcher(paramName).matches();
	}

}
