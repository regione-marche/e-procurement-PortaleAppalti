package it.maggioli.eldasoft.plugins.ppcommon.aps.validator;

/**
 * Interfaccia visibile al di fuori del package rappresentate i validatori di campo
 */
public interface FieldValidator {

    /**
     * Valida il campo e se richiesto lo ripulisce
     * @return true=valido, false=non valido
     * @throws Exception Le varie eccezioni della Reflection
     */
    boolean validate() throws Exception;

}
