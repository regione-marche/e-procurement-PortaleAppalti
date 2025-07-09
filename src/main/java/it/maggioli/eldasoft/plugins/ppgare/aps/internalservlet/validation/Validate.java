package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The `Validate` annotation is used to mark a field for validation.<br/>
 * It can be applied to any field and provides information on the type of validation to be performed on that field.<br/>
 * The annotation has two optional elements: `value` and `error`.<br/>
 * <br/>
 * The `value` element specifies the type of validation to be performed. It takes an instance of the `EParamValidation` enum.<br/>
 * This enum contains various predefined validation types such as `NONE`, `EMAIL`, `NUMBER`, `DATE`, etc.<br/>
 * The default value of `value` is `EParamValidation.NONE`, indicating no specific validation type is specified.<br/>
 * If this field is specified for a custom class, it'll be ignored.<br/>
 * <br/>
 * The `error` element specifies the error message to be displayed when the validation fails.<br/>
 * It takes an instance of the `WithError` annotation as its default value, which contains the error message and code.<br/>
 * If no specific error message is provided, a default error message will be displayed.<br/>
 * <br/>
 * Example usage:
 * <br/>
 * ```
 * <pre>
 * public class User extends EncodedAction {
 *
 *     {@literal @}Validate(EParamValidation.EMAIL)
 *     private String email;
 *
 *     {@literal @}Validate(EParamValidation.NUMBER, error = @WithError("Invalid age", 100))
 *     private String age;
 *
 *     // getters and setters
 * }
 * </pre>
 * ```
 * <br/>
 * In the above example, the `email` field is annotated with `@Validate` and has the `EParamValidation.EMAIL` value.<br/>
 * This indicates that the field should be validated as an email address.<br/>
 * <br/>
 * The `age` field is also annotated with `@Validate`, but with `EParamValidation.NUMBER` and a specific error message and code.<br/>
 * This indicates that the field should be validated as a number, and if the validation fails, the error message "Invalid age" with code 100 will be displayed.<br/>
 * <br/>
 * <strong>Note:</strong>
 * <ul>
 *  <li>The `@Validate` annotation can only be applied to fields.</li>
 *  <li>The `EParamValidation` enum and `WithError` annotation must be imported to use this annotation.</li>
 *  <li>The `value` element can be omitted only for custom classes that contains field with `@Validate`.</li>
 *  <li>The `field` can be validated only if it's a String or String[] or List&lt;String&gt;</li>
 * </ul>
 *
 * @see it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ActionFieldValidationInterceptor
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {

    EParamValidation value() default EParamValidation.NONE;
    WithError error() default @WithError;

}
