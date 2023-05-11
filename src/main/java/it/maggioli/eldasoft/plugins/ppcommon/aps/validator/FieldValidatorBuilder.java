package it.maggioli.eldasoft.plugins.ppcommon.aps.validator;

import com.opensymphony.xwork2.ActionInvocation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Builder che crea un oggetto per effettuare la validazione del campo scelto e se richiesto ripulisce il campo
 * qualora il campo non sia valido. (Ripulisce anche lo stack e la request se specificato)
 *
 */
public class FieldValidatorBuilder {

    private BaseFieldValidator instance;

    private FieldValidatorBuilder() { }

    public static FieldValidatorBuilder newValidator(Object classInstance, Field field) throws Exception {
        FieldValidatorBuilder builder = new FieldValidatorBuilder();

        if (field.getType().isArray())
            builder.instance = new ValidateArray(classInstance, field);
        else if (field.getType().isAssignableFrom(Collection.class))
            builder.instance = new ValidateList(classInstance, field);
        else
            builder.instance = new ValidateField(classInstance, field);

        builder.instance.setFieldValue(field.get(classInstance));

        return builder;
    }

    /**
     * Valorizzare in caso si voglia ripulire lo stack di struts e fare degli eventuali controlli su campi definiti solo su request.
     *
     * @param invocation oggetto dell'interceptor
     */
    public FieldValidatorBuilder setInvocation(ActionInvocation invocation) {
        instance.setInvocation(invocation);
        return this;
    }

    public FieldValidatorBuilder setParentName(String parentName) {
        instance.setParentName(parentName);
        return this;
    }

    /**
     * @param validator enum contenente la funzione di validazione
     */
    public FieldValidatorBuilder setValidator(EParamValidation validator) {
        instance.setValidator(validator);
        return this;
    }

    /**
     * Se passato a true, in caso il field non sia valido, ripulisco il campo e lo stack.
     */
    public FieldValidatorBuilder cleanFieldIfNotValid(boolean hasToCleanField) {
        instance.setHasToCleanField(hasToCleanField);
        return this;
    }

    /**
     * Il metodo: build; pu\u00F2 ritornare 3 tipi di oggetto: ValidateArray, ValidateList e ValidateField; dipende dal tipo del field.
     *
     * Tutte le classi ritornabili dal: build; estendono la classe BaseFieldValidator che possiede l'interfaccia FieldValidator.
     * L'unico oggetto visibile da fuori del package \u00E8: FieldValidator; quindi, il build ritorna
     *
     * @return l'istanza che far\u00E0 la validazione
     */
    public FieldValidator build() {
        return instance;
    }

}
