package it.maggioli.eldasoft.plugins.ppcommon.aps.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Effettua la validazione di un field di tipo Collection (List).
 */
class ValidateList extends BaseFieldValidator {

    private static final Logger log = LoggerFactory.getLogger(ValidateArray.class);

    ValidateList(Object toCheck, Field field) { super(toCheck, field); }

    @Override
    public boolean validate() throws Exception {
        Object currentValueToCheck = getCurrentValueToCheck();  //Mi faccio ritornare la lista dda controllare

        if (currentValueToCheck != null) {  //Se nullo non ho niente da controllare, quindi, il valore \u00E8 valido
            List<Object> list = (List<Object>) fieldValue;
            isValid = !hasNotValidValue(list.stream()); //Controllo se ha valori non validi
            if (hasToCleanField && !isValid) {  //Se ha valori non validi e se \u00E8 richiesto lo sbiancamento dei campi
                if (fieldValue != null) //Se non ho fatto riferimento al valore dello stack
                    list.clear();   //Non valido, quindi, svuoto l'array
                cleanStack();
            }
        } else
            isValid = true;

        return isValid;
    }

    @Override
    protected Logger getLogger() { return log; }

}
