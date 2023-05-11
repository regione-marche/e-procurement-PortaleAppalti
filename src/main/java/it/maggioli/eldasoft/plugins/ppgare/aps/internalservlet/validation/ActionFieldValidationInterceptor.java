package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Interceptor per cancellare i parametri non validi.
 * \u00C8 come un filtro xss, ma, pi\u00F9 complesso, in quanto vengono fatti controlli tramite reflection sui vari
 * campi della action.
 *
 * Il settaggio tramite reflection dei parametri (di struts), viene fatto dal ParametersInterceptor tramite libreria Ognl
 * (ParametersInterceptor riga 273)
 *
 * Lo stack STRUTS \u00E8 in qualche modo collegato allo stack di valori utilizzato da JSTL, quindi, posso utilizzare
 * l'interceptor come fosse un HTTP Filter, in quanto, se rimuovo/modifico un valore dallo stack di STRUTS
 * viene rimosso anche dallo stack JSTL.
 *
 * ATTENZIONE: Questo interceptor necessita che sia abilitato il filtro http dei layout.
 *
 */
public class ActionFieldValidationInterceptor extends MethodFilterInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ActionFieldValidationInterceptor.class);

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        log.debug("START - Validating action fields");

        validate(invocation);
//        doWithSkipValidation(invocation);

        log.debug("END - Validating action field");
        return invocation.invoke();
    }

    /**
     * Se presente il skipvalidation non eseguo la validazione
     *
     * @param invocation
     */
    private void doWithSkipValidation(ActionInvocation invocation) {
        Object action = invocation.getAction();
        Collection<Method> annotatedMethods = null;
        Method method = null;
        try {
            method = action.getClass().getMethod(invocation.getProxy().getMethod());
            annotatedMethods = AnnotationUtils.getAnnotatedMethods(action.getClass(), SkipValidation.class);
        } catch (Exception e) { }

        if (annotatedMethods == null || !annotatedMethods.contains(method))
            validate(invocation);
    }

    private void validate(ActionInvocation invocation) {
        ClassFieldValidator.validate(invocation);
    }

}
