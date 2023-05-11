package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotare le variabili globali delle Action, quando si vuole vengano validate
 * tramite la funzione collegata all'enum specificato come parametro.
 * <br>
 * <u>Il valore dell'annotation deve essere lasciato vuoto solo in caso di oggetto di classe non primitiva (o wrapper di primitiva)</u>
 * Se si valorizza il parametro dell'annotation di una classe non primitiva, questo, parametro verr\u00E0 ignorato.
 * <br><br>
 * <strong>Attenzione:</strong> Questa annotation \u00E8 stata testata solo su oggetti String o String[] (o oggetti contenenti stringhe)
 * dovrebbe funzionare anche su oggetti di tipo diverso, per\u00F2, non verr\u00E0 resettato il campo sulla action, verr\u00E0
 * semplicemente brasato il valore sulla request.
 * <br><br>
 * <strong>Interceptor:</strong> ActionFieldValidationInterceptor;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {

    EParamValidation value() default EParamValidation.NONE;

}
