package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotare le variabili globali delle Action, quando si vuole vengano validate
 * tramite la funzione collegata all'enum specificato come parametro.
 * <br>
 * <u>....</u>
 * <br><br>
 * <strong>Interceptor:</strong> UserClusterInterceptor;
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FlussiAccessiDistinti {

	EFlussiAccessiDistinti[] value() default EFlussiAccessiDistinti.NONE;
	
}
