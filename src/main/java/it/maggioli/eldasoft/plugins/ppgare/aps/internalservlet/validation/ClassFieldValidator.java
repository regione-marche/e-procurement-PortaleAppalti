package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionInvocation;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.validator.FieldValidatorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <strong>Effettua la validazione delle variabili globali delle action o della request se il campo è nullo.</strong>
 * <br>
 * <br>
 * Il metodo validate(ActionInvocation invocation) \u00E8 il cuore della classe, effettua la validazione di una action (qualsiasi classe),
 * facendosi ritornare con la reflection la lista di field (pubblici o privati) di una action (e dei suoi padri) e controllando
 * che i campi con annotation @Validate siano validi secondo il validatore contenuto nell'enum passato come parametro.
 * <br>
 * <u>In caso di analisi di oggetto complesso</u>, ovvero non di una Stringa standard, ma, di un oggetto contentente stringhe,
 * il metodo validate(Object classInstance) sar\u00E0 <u>chiamato ricorsivamente</u>, finch\u00E9 non ci saranno pi\u00F9 oggetti complessi da
 * controllare.
 * <br>
 * <strong>Tipi di campi non supportati</strong> (non gestiti perch\u00E8 attualmente non necessario): Set, Map.
 * <br>
 * Oltre a settare a stringa vuota i valori nella action, <strong>pulisco anche i valori nello stack e nella request</strong>.
 * In questo modo non rischio di incorrere in problemi del tipo: \u00E8 stato utilizzato JSTL e non STRUTS.
 *
 */
public final class ClassFieldValidator {

    private static final Logger log = LoggerFactory.getLogger(ClassFieldValidator.class);

    /**
     * Verranno analizzate le classi padri, per recuperare i campi (pubblici e privati), finch\u00E8 non viene trovata una di quelle elencate.
     * In questo modo avr\u00E0 meno campi e classi da analizzare, velocizzando la validazione.
     */
    private static final List<Class<?>> SEARCH_UNTIL_CLASS = Arrays.asList(
        EncodedDataAction.class
        , BaseAction.class
        , AbstractProcessPageAction.class
        , AbstractOpenPageAction.class
        , Object.class
    );

    private ClassFieldValidator() { }   //\u00C8 una classe statica, quindi, tolgo la possibilit\u00E0 di istanziarla

    /**
     * Imposta a null tramite reflection i campi con annotation ParamValidation non validi.
     * Viene chiamato ricorsivamente in caso la Action contenga un campo da validare non primitivo (o wrapper di primitivo).
     * Es: FatturaAction->indirizzi \u00C8 una lista di IIndirizzoImpresa, quindi, devo nuovamente recuperarmi con la reflection
     * i campi da validare.
     * <br>
     * Oltre a ripulire le variabili dell'istanza, ripulisce anche lo stack e la request da quello stesso valore.
     *
     * @param invocation parametro dell'interceptor
     */
    public static void validate(ActionInvocation invocation) {
        validate(invocation, invocation.getAction(), "");
    }

    /**
     *
     * @param invocation paremtro dell'interceptor
     * @param toValidate Istanza della classe che necessita della validazione dei field.
     * @param parentFieldName nome del padre in caso sia stato elaborato un oggetto custom
     */
    public static void validate(ActionInvocation invocation, Object toValidate, String parentFieldName) {
        log.debug("START - Checking fields to validate");

        List<Field> fields = getAllFieldsIncludingParent(toValidate.getClass());    //Lista di campi della Action (compresi padri)
        fields.forEach(field -> {
            Validate annotation = field.getAnnotation(Validate.class);
            if (annotation != null) {   //Se null non effettuo la validazione perch\u00E8 non richiesta
                try {
                    boolean accessible = field.isAccessible();

                    field.setAccessible(true);

                    //Effettuo la validazione del campo corrente
                    FieldValidatorBuilder.newValidator(toValidate, field)
                            .setValidator(annotation.value())
                            .setInvocation(invocation)
                            .setParentName(parentFieldName)
                            .cleanFieldIfNotValid(true) //Ripulisco il campo se non valido
                        .build()
                    .validate();    //Metodo che mi permette di effettuare la validazione (e la pulizia) dei parametri

                    field.setAccessible(accessible);
                } catch (Exception e) {
                    log.error("Error: ", e);
                    throw new RuntimeException(e);
                }
            }
        });

        log.debug("END - Checking fields to validate");
    }

    /**
     * Mi ritorna tutti i field con qualsiasi visibili\u00E0, inclusi quelli dei parent
     * (fino a che non arriva a uno dei parent contenuti in <strong>SEARCH_UNTIL_CLASS</strong>)
     * <br/>
     * \u00C8 necessario recuperarsi i fields dai padri, in caso la Action estenda un'altra Action con dei campi da validare.
     * @param startClass
     * @return
     */
    private static List<Field> getAllFieldsIncludingParent(Class<?> startClass) {
        List<Field> toReturn = new ArrayList<>(100);    //Confido non ci siano pi\u00F9 di 100 variabili globali

        Class<?> currentClass = startClass; //Classe di partenza
        do
            toReturn.addAll(Arrays.asList(currentClass.getDeclaredFields()));    //Recupero i campi dalla classe corrente
        while ((currentClass = currentClass.getSuperclass()) != null  //Controllo se la classe corrente ha dei parent
                    && !SEARCH_UNTIL_CLASS.contains(currentClass));  //Controllo se il padre appena trovato fa parte di una delle classi che identifica la fine dell'analisi

        return toReturn;
    }

}
