package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithError {

    public static final String DEFAULT_TEXT_ERROR 			= "Errors.validation";
    public static final String DEFAULT_TEXT_ERROR_MALWARE	= "Errors.validation.malware";

    /**
     * Viene recuperato dai .properties
     * @return
     */
    String messageError() default DEFAULT_TEXT_ERROR;

    /**
     * Viene recuperato tramite il getTextFromDB della BaseAction
     * @return
     */
    String fieldLabel() default "";

}
