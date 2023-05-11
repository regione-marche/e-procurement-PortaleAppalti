package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.util.StrutsTypeConverter;

import com.agiletec.aps.system.ApsSystemUtils;

public class DoubleConverter extends StrutsTypeConverter {

	@SuppressWarnings("rawtypes")
	@Override
	public Object convertFromString(Map context, String[] values, Class toType) {
		try {
			if(values!=null && values.length>0) {
				return Double.valueOf(StringUtils.replace(values[0], ",", "."));
			}
		} catch (Exception e) {
			ApsSystemUtils.getLogger().warn("Inserito un valore non corretto per il pagamento.",e);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public String convertToString(Map arg0, Object arg1) {
		if(arg1==null) return null;
		return StringUtils.replace(String.valueOf(arg1), ".", ",");
	}

}
