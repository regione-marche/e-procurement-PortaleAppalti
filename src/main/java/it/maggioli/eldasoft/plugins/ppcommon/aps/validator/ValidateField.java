package it.maggioli.eldasoft.plugins.ppcommon.aps.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Effettua la validazione di un campo singolo.
 */
class ValidateField extends BaseFieldValidator {

    private static final Logger log = LoggerFactory.getLogger(ValidateArray.class);

    ValidateField(Object toCheck, Field field) { super(toCheck, field); }

    @Override
    public boolean validate() throws Exception {
        Object currentValueToCheck = getCurrentValueToCheck();  //Mi faccio ritornare la lista da controllare

        if (currentValueToCheck != null) {
            isValid = validateObjectValue(currentValueToCheck); //Controllo se ha valori non validi
            if (hasToCleanField && !isValid) {  //Se ha valori non validi e se \u00E8 richiesto lo sbiancamento dei campi
                if (fieldValue != null) { //Se non ho fatto riferimento al valore dello stack
                    if (currentValueToCheck instanceof String)
                        field.set(toCheck, cleanValue(currentValueToCheck.toString()));
                    else
                        log.debug("Al momento e' possibile solamente impostare il valore ai campi stringa");
                }
                cleanStack();
            }
        } else
            isValid = true;

        return isValid;
    }
    
    private String cleanValue(String currentValue) {
		String value = null;
		if(invalidPart != null) {
			value = currentValue;
			for(int i = 0; i < invalidPart.length(); i++) {
				String ch = ("\\.[]{}()<>*+-=!?^$|".indexOf(invalidPart.charAt(i)) >= 0 ? "\\" : "") + invalidPart.substring(i, i + 1);
				value = value.replaceAll(ch, "");
			}
			fieldValue = value;
		}
		return value; 
	}

    @Override
    protected Logger getLogger() { return log; }

}
