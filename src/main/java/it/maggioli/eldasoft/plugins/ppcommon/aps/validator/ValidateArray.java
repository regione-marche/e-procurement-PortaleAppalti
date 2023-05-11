package it.maggioli.eldasoft.plugins.ppcommon.aps.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Effettua la validazione di un field di tipo Array.
 */
class ValidateArray extends BaseFieldValidator {

    private static final Logger log = LoggerFactory.getLogger(ValidateArray.class);

    ValidateArray(Object toCheck, Field field) { super(toCheck, field); }

    @Override
    public boolean validate() throws Exception {
        Object currentValueToCheck = getCurrentValueToCheck();  //Mi faccio ritornare la lista dda controllare

        if (currentValueToCheck != null) {  //Se nullo non ho niente da controllare, quindi, il valore \u00E8 valido
            Object[] array = (Object[]) currentValueToCheck;
            isValid = !hasNotValidValue(Arrays.stream(array));  //Controllo se ha valori non validi
            if (hasToCleanField && !isValid) {  //Se ha valori non validi e se \u00E8 richiesto lo sbiancamento dei campi
                if (fieldValue != null) //Se non ho fatto riferimento al valore dello stack
                    field.set(toCheck, Array.newInstance(currentValueToCheck.getClass().getComponentType(), 0));    //Non valido, quindi, creo un nuovo array vuoto
                cleanStack();
            }
        } else
            isValid = true;

        return isValid;
    }

    @Override
    protected Logger getLogger() { return log; }

}
