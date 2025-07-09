package it.maggioli.eldasoft.plugins.utils;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

import java.io.Serializable;

/**
 * The OrderableField class represents an orderable field used for sorting objects.<br/>
 * Use it on an action to integrate the represent a table column sortable.<br/>
 */
public class OrderableField implements Serializable {

    /**
     * The orderable variable is used to store an object of type EOrderable.<br/>
     * <br/>
     * EOrderable is an enum that represents the order of sorting for a collection of objects.<br/>
     * It provides methods to obtain a comparator for ordering objects in ascending or descending order.<br/>
     * <br/>
     * Example usage:<br/>
     * <pre>
     * OrderableField orderableField = new OrderableField();
     * EOrderable orderable = EOrderable.DESC; // or EOrderable.ASC
     * orderableField.setOrderable(orderable);
     *
     * EOrderable retrievedOrderable = orderableField.getOrderable();
     * </pre>
     */
    private EOrderable orderable;
    /**
     * The identifier variable is a private String field annotated with @Validate(EParamValidation.ORDERABLE_IDENTIFIER).<br/>
     * It stores the identifier of an object and is used for validation purposes.<br/>
     * <br/>
     * It identify the column that has to be sorted.<br/>
     * <br/>
     * EParamValidation.ORDERABLE_IDENTIFIER is an enum value that specifies the validation rule for the identifier.<br/>
     * <br/>
     * <strong>Regex:</strong> <i>"(?i)[a-z\\d]"</i><br/>
     * <br/>
     * <strong>Example usage:</strong><br/>
     * <pre>
     * OrderableField orderableField = new OrderableField();
     * orderableField.setIdentifier("exampleIdentifier");
     *
     * String retrievedIdentifier = orderableField.getIdentifier();
     * </pre>
     * @see it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ParamValidator
     */
    @Validate(EParamValidation.ORDERABLE_IDENTIFIER)
    private String identifier;

    public EOrderable getOrderable() {
        return orderable;
    }
    public void setOrderable(EOrderable orderable) {
        this.orderable = orderable;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
