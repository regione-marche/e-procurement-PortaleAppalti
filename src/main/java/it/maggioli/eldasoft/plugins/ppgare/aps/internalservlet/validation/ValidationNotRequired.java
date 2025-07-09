package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Semplice annotazione simbolica non storicizzata a runtime, indicante che un field non necessita di validazione:
 * Es:
 * - Field senza setter
 * - Valore del field assegnato nella stessa classe
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface ValidationNotRequired { }
