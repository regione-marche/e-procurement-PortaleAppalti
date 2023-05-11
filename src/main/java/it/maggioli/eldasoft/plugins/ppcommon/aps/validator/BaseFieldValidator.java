package it.maggioli.eldasoft.plugins.ppcommon.aps.validator;

import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import it.maggioli.eldasoft.plugins.ppcommon.aps.RequestParamRemoverWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ClassFieldValidator;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import org.apache.commons.lang3.ClassUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Classe base dei validatori dei campi
 */
abstract class BaseFieldValidator implements FieldValidator {

    protected ActionInvocation invocation;  //Oggetto dell'interceptor
    protected Object           toCheck; //Istanza della classe
    //Ex di ampo valorizzato: model. (BandiFinder action); poi verr\u00E0 fatto un model.oggetto, model.cig...
    protected String           parentName = "";
    protected Field            field;   //Campo da controllare
    protected EParamValidation validator;   //Validatore per il campo
    protected Object           fieldValue;  //Valore del campo
    protected boolean          hasToCleanField = false; //Se a true, ripulisce i field non validi

    protected boolean          isValid = false;

    protected BaseFieldValidator(Object toCheck, Field field) {
        this.toCheck = toCheck;
        this.field = field;
    }

    protected abstract Logger getLogger();

    /**
     * Valida l'oggetto passato come parametro.
     *<br>
     * In caso di oggetto non primitivo/wrapper e stringa, richiamo il ricorsivamente il richiamante, per effettuare la validazione della nuova classe.
     *
     * @param valToCheck istanza dell'oggetto da validare
     * @return true=valido, false=non valido
     */
    protected boolean validateObjectValue(Object valToCheck) {
        boolean isValid = true;

        //int/Integer, char/Character, long/Long, String...
        //Attenzione: il reset viene effettuato solo nei parametri di tipo String
        if (ClassUtils.isPrimitiveOrWrapper(valToCheck.getClass()) || valToCheck instanceof String)  //Se \u00E8 primitivo e non valido setto a null
            isValid = validator.validate(valToCheck);
        else
            ClassFieldValidator.validate(invocation, valToCheck, parentName + field.getName() + ".");    //Se \u00E8 un oggetto "custom" lo considero valido, in questo modo, non lo setter\u00F2 a null, ma, valido i campi all'interno

        notifyIfNotValid(valToCheck, isValid);

        return isValid;
    }

    /**
     * Controlla se la stream di oggetti passata contiene anche 1 solo elemento non valido.
     *
     * @param stream stream di elementi da controllare
     * @return true=non valido, false = valido
     */
    protected boolean hasNotValidValue(Stream<Object> stream) {
        return stream.anyMatch(currentVal -> currentVal != null && !validateObjectValue(currentVal));
    }

    /**
     * In caso il valore del campo sia nullo, controllo che anche sullo stack il valore sia nullo.<br>
     * In caso contrario effettuo la validazione del campo sullo stack.
     *
     * @return oggetto da controllare (pu\u00F2 ritornare nulli)
     */
    protected Object getCurrentValueToCheck() {
        return invocation != null && fieldValue == null
               ? getStrutsStack().get(parentName + field.getName())
               : fieldValue;
    }

    /**
     * Ritorna lo stack di struts
     *
     * @return lo stack di struts
     */
    private Map<String, Object> getStrutsStack() {
        return (Map) invocation.getStack().getContext().get(ActionContext.PARAMETERS);
    }

    /**
     * Ripulisce stack, request e _params della BaseAction dal campo non valido.
     */
    protected void cleanStack() {
        if (invocation != null)
            cleanStackFromValue(parentName + field.getName());
    }
    /**
     * Ripulisce stack, request e _params della BaseAction dal campo non valido.
     */
    protected void cleanStackFromValue(String paramName) {
        cleanValueFromStrutsStack(paramName);   //Stack di struts
        cleanValueFromAction(paramName);    //Stack salvato sulla BaseAction
        cleanValueFromRequest(paramName);   //Stack della request
        getLogger().error("Cleaned stack from the http param: {}", paramName);
    }
    /**
     * Ripulisce lo stack.
     */
    private void cleanValueFromStrutsStack(String name) {
        Object parameters = invocation.getStack().getContext().get(ActionContext.PARAMETERS);
        if (parameters != null)
            ((Map) parameters).computeIfPresent(name, this::computeEmpty);  //Se esiste la chiave richiesta, imposto a valore vuoto il campo designato
        parameters = invocation.getStack().getContext().get("parameters");
        if (parameters != null)
            ((Map) parameters).computeIfPresent(name, this::computeEmpty);  //Se esiste la chiave richiesta, imposto a valore vuoto il campo designato

    }

    private Object computeEmpty(Object key, Object value) { return ""; }

    /**
     * Ripulisce il _params della BaseAction dal campo non valido.
     */
    private void cleanValueFromAction(String name) {
        //Se esiste la chiave richiesta, imposto a valore vuoto il campo designato
        ((BaseAction) invocation.getAction()).getParameters().computeIfPresent(name, (key, value) -> new String[0]);
    }
    /**
     * Ripulisce la request.
     */
    private void cleanValueFromRequest(String name) {
        //Mi faccio ritornare il wrapper contenente la request. Vedi: Struts2ServletDispatcher;
        Object requestWrapper = invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
        if (requestWrapper instanceof StrutsRequestWrapper) {   //StrutsRequestWrapper o MultiParRequestWrapper
            StrutsRequestWrapper strutsRequestWrapper = ((StrutsRequestWrapper) invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST));
            //Taccone fatto perchè in caso di download i filtri non vengono richiamati (il filtro LayoutSettingsFilter crea il RequestParamRemoverWrapper)
            if (!(strutsRequestWrapper.getRequest() instanceof RequestParamRemoverWrapper)) {
                RequestParamRemoverWrapper wrapper = new RequestParamRemoverWrapper((HttpServletRequest) strutsRequestWrapper.getRequest());
                strutsRequestWrapper.setRequest(wrapper);
            }
            //Converto la request in RequestParamRemoverWrapper, ovvero il wrapper impostato su: LayoutSettingsFilter.

            ((RequestParamRemoverWrapper) strutsRequestWrapper.getRequest()).addNotValidParam(name);
        } else
            throw new UnsupportedOperationException("Il wrapper di richiesta" + requestWrapper.getClass().getSimpleName() + " non \u00E8 supportato");
    }
    protected void notifyIfNotValid(Object value, boolean isValid) {
        if (!isValid)
            getLogger().error("The value \"{}\" is not valid for the field: {}.", value, parentName + field.getName());
    }


    void setInvocation(ActionInvocation invocation) {
        this.invocation = invocation;
    }

    void setParentName(String parentName) {
        this.parentName = parentName;
    }

    void setValidator(EParamValidation validator) {
        this.validator = validator;
    }

    void setHasToCleanField(boolean hasToCleanField) {
        this.hasToCleanField = hasToCleanField;
    }

    void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

}
