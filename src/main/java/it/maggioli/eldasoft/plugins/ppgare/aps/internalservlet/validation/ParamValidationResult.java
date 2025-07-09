package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation;


/**
 * Risultato della validazione di un parametro con una funzione di validazione di ParamValidator
 *  
 */
public class ParamValidationResult {
	
	private String messageError = WithError.DEFAULT_TEXT_ERROR;		// default
	private String invalidPart;
	
	public String getMessageError() {
		return messageError;
	}
	
	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}
	
	public String getInvalidPart() {
		return invalidPart;
	}
	
	public void setInvalidPart(String invalidPart) {
		this.invalidPart = invalidPart;
	}	

}