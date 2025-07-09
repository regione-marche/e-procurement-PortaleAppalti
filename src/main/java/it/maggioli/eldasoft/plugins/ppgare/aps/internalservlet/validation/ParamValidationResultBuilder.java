package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;

/**
 * ...
 * 
 */
public class ParamValidationResultBuilder {
	
    private ParamValidationResult instance;

    /**
     * Classe statica, quindi, elimina la possibilita' di istanziarla
     */
    private ParamValidationResultBuilder() { }

    /**
     * ... 
     */
    public static ParamValidationResultBuilder newValidator() {
    	ParamValidationResultBuilder builder = null;
    	try {
    		builder = new ParamValidationResultBuilder();
        	builder.instance = new ParamValidationResult();
            return builder;	
    	} catch (Throwable t) {
			// NON DOVREBBE MAI SUCCEDERE!!!
    		throw t; 
		}
    }

    /**
     * ...
     */
    public ParamValidationResultBuilder setMessageError(String value) {
        instance.setMessageError(value);
        return this;
    }

    /**
     * ...
     */
    public ParamValidationResultBuilder setInvalidPart(String value) {
        instance.setInvalidPart(value);
        return this;
    }

    /**
     * ...
     */
    public ParamValidationResult build() {
        return instance;
    }

}