package it.maggioli.eldasoft.plugins.ppcommon.aps;

import com.agiletec.apsadmin.system.TokenInterceptor;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ParamValidationResult;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ParamValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Classe per effettuare la validazione XSS di un header da Interceptor o filtro HTTP
 */
public class XSSValidation {

    /**
     * Lista dei parametri da validare tramite filtro HTTP con rispettivo validatore
     *
     * sulla validazione controllo se il parametro con nome header \u00E8 presente nella mappa. Se esiste, utilizzo
     * la funzione (il validatore) ritornata dalla mappa per validare il valore dell'header
     */
    public static final Map<String, Function<String, ParamValidationResult>> PARAMS_VALIDATOR = new HashMap<>();

    /*
      Inizializzo la lista di validatori
     */
    static {
        PARAMS_VALIDATOR.put("ext", ParamValidator::isDigit);
        PARAMS_VALIDATOR.put("currentFrame", ParamValidator::isDigit);
        PARAMS_VALIDATOR.put("actionPath", ParamValidator::isUnlimitedTextValid);
        PARAMS_VALIDATOR.put(TokenInterceptor.getStrutsTokenName(), ParamValidator::isTokenValid);
    }

    /**
     * Controlla se il parametro passato deve essere validato tramite filtro HTTP
     * @param param
     * @return
     */
    public static boolean hasToBeChecked(Map.Entry<String, Object> param) {
        return hasToBeChecked(param.getKey(), param.getValue());
    }
    /**
     * Controlla se il parametro passato deve essere validato tramite filtro HTTP
     * @param key
     * @param value
     * @return
     */
    public static boolean hasToBeChecked(String key, Object value) {
        return value != null && PARAMS_VALIDATOR.containsKey(key);
    }

    /**
     * Controlla se il parametro non \u00E8 valido
     * @param param
     * @return true = il parametro non \u00E8 valido
     */
    public static boolean isNotValid(Map.Entry<String, Object> param) {
        return isNotValid(param.getKey(), param.getValue());
    }
    /**
     * Controlla se il parametro non \u00E8 valido
     * @param key
     * @param value
     * @return true = il parametro non \u00E8 valido
     */
    public static boolean isNotValid(String key, Object value) {
        return value.getClass().isArray()
               ? checkArray(key, (Object[]) value)
               : checkField(key, value);
    }

    /**
     * Controlla se esiste anche un solo elemento dell'array non valido
     * @param key
     * @param array
     * @return
     */
    private static boolean checkArray(String key, Object[] array) {
        return Arrays.stream(array).anyMatch(val -> checkField(key, val));
    }

    /**
     * Controlla se esiste il campo richiesto \u00E8 valido
     * @param key
     * @param value
     * @return true se il valore del campo NON e' valido, false viceversa 
     */
    private static boolean checkField(String key, Object value) {
		// NB: PARAMS_VALIDATOR.get(key).apply(function) 
		//     applica la funzione di validazione in base ad una regex o ad un altro tipo di validazione
		//     ed in caso di errore la funzione restituisce la porzione della stringa che genera l'errore di validazione
    	ParamValidationResult ret = PARAMS_VALIDATOR.get(key).apply(unescapeHtml(value.toString()));
    	return value != null && ret != null && StringUtils.isNotEmpty(ret.getInvalidPart());
    }

    /**
     * unescape del payload contenuto in un parametro
     * @param value valore su cui effettuare l'escape
     */
    public static String unescapeHtml(String value) {
        try {
            // unescape %NN o %NNNN per ripristinare un valore "in chiaro"...
            StringBuilder sb = new StringBuilder();
            int j = -1;
            int i = 0;
            while (i < value.length()) {
                char c = value.charAt(i);
                if (c == '%') {
                    j = i;
                } else if(j >= 0) {
                    if(c >= '0' && c <= '9') {
                        sb.append(c);
                    } else if(sb.length() > 0) {
                        value = value.substring(0, j) + ((char)Integer.parseInt(sb.toString(), 16)) + value.substring(i);
                        i = j + 1;
                        j = -1;
                    }
                }
                i++;
            }

            // unescape all chars like &#xNNNN
            value = StringEscapeUtils.unescapeHtml4(value);

            // rimuovi i caratteri invalidi/"speciali" [0-31] eccetto \r \n...
            for (byte b = 0; b < 32; b++) {
                if (b != 10 && b != 13) {
                    value = value.replaceAll(new String(new byte[]{b}), "");
                }
            }
        } catch (Exception ex) {
            value = "";
        }

        return value;
    }

}
