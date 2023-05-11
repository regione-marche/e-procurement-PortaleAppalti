package it.maggioli.eldasoft.plugins.ppcommon.struts2.validator;

import java.util.Date;

import org.apache.commons.validator.routines.DateValidator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

/**
 * Validatore di campi in formato stringa contenenti date nel formato GG/MM/AAAA
 * 
 * @author Stefano.Sabbadin
 */
public class ItalianDateValidator extends FieldValidatorSupport {

    @Override
    public void validate(Object object) throws ValidationException {
	String fieldName = getFieldName();
	Object value = this.getFieldValue(fieldName, object);
	if (!(value instanceof String)) {
	    return;
	}
	String str = ((String) value).trim();
	if (str.length() == 0) {
	    return;
	} else {
	    Date d = DateValidator.getInstance().validate((String) value,
		    "dd/MM/yyyy");
	    if (d == null) {
		addFieldError(fieldName, object);
	    }
	}
    }

}
