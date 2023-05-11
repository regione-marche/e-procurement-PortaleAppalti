package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc;

import java.io.File;
import java.io.InputStream;

/**
 * Interfaccia Questionari (QCompiler) per le action relative ai questionari
 * 
 */
public interface IProcessPageQuestionario {

	/** 
	 * paremetri di INPUT 
	 */
	public Long getIdCom();
	public void setIdCom(Long idCom);
	
	public String getUuid();
	public void setUuid(String uuid);
	
	public String[] getUuids();
	public void setUuids(String[] uuids);
	
	public boolean getSigned();
	public void setSigned(boolean signed);
	
	public boolean getSummary();
	public void setSummary(boolean summary);
	
	public String getDescription();	
	public void setDescription(String description);

	public String getAttachmentFileName();
	public void setAttachmentFileName(String attachmentFileName);
	
	public File getAttachmentData();
	public void setAttachmentData(File attachmentData);
	
	public String getAttachmentOrder();
	public void setAttachmentOrder(String value);
	
	public String getForm();
	public void setForm(String form);
	
	/** 
	 * paremetri di OUTPUT 
	 */	
	public String getContentType();
	public void setContentType(String contentType);
	
	public String getFilename();
	public void setFilename(String filename);
	
	public InputStream getInputStream();
	public void setInputStream(InputStream inputStream);
	
	
	/**
	 * initQuestionario.action
	 * 
	 * (QCompiler) inizializza il questionario, creando la comunicazione con l'allegato "questionario.json"
	 */
	public String initQuestionario();

	
	/**
	 * addQCDocument.action
	 * 
	 * (QCompiler) aggiungi un allegato ad una comunicazione
	 * 	IN
	 * 	 	idCom				id della comunicazione
	 *		description			descrizione dell'allegato
	 * 		attachmentFileName	nome file dell'allegato
	 * 		attachmentData		contenuto binario dell'allegato
	 * 		uuid				uuid del documento aggiunto
	 * 		signed				True se il file e' firmato digitalmente
	 * 		summary				True se il file e' il pdf firmato del riepilogo questionario 
	 * 	 	form				json del questionario (formato base64)
	 * 
	 *	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *			uuid			uuid del documento aggiunto
	 *		  }
	 */
	public String addDocumento();

	
	/**
	 * deleteQCDocument.action
	 * 
     * (QCompiler) elimina un documento allegato da una comunicazione
     * 	IN
     * 		idCom				codice della gara
	 * 		uuids				lista di UUID degli allegati comunicazione da eliminare
	 * 		form				json del questionario
	 * 
	 * 	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *		  }
	 */
	public String deleteAllegato();
	
	
	/**
	 * downloadQCDocument.action
	 * 
	 * (QCompiler) scarica un documento allegato in una comunicazione
	 * 	IN
	 * 		idCom				codice della gara
	 * 		uuid				UUID dell'allegato comunicazione da scaricare
	 * 
	 * 	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *			filename		nome file del allegato 
	 *		  	filedata		contenuto binario dell'allegato in formato Base64
	 *		  }
	 */
	public String downloadAllegato();


	/**
	 * loadQCForm.action
	 * 
	 * (QCompiler) ritorna il questionario dalla comunicazione
	 * 	IN
	 * 		idCom				codice della gara
	 * 
	 * 	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *			form			json del questionario 
	 *		  }
	 */
	public String loadQuestionario();
	 
	
	/**
	 * saveQCForm.action
	 * 
	 * (QCompiler) salva il questionario nella comunicazione
	 * 	IN
	 * 		idCom				codice della gara
	 * 		form				json del questionario
	 * 
	 * 	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *		  }
	 */
	public String saveQuestionario();
	
	/**
	 * newUuidQC.action
	 * 
	 * (QCompiler) restituisce un nuovo uuid
	 *  IN
	 *  	attachmentOrder
	 *  		nel formato SSSZ00GR00QU00 dove
	 *  		SS   N. struttura (formattato a 2 cifre)
	 * 			SZ00 N. sezione (formattato a 2 cifre) + N. sequenza cardinalita’ sezione (formattato a 2 cifre) 
	 *			GR00 N. gruppo (formattato a 2 cifre) + N. sequenza cardinalita’ gruppo (formattato a 2 cifre)  
	 * 			QU00 N. quesito (formattato a 2 cifre) + N. sequenza cardinalita’ quesito (formattato a 2 cifre)
	 *  
	 * 	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *			uuid			uuid generato (nel formato SSSZ00GR00QU00+YYMMDDhhmssNNNNN)
	 *		  }
	 */
	public String getNewUuid();
	
	/**
	 * redirectToDGUE.action
	 * 
	 * (QCompiler) restituisce l'url per il redirect al DGUE
	 *  IN
	 *  	idCom				id della comunicazione
	 *  
	 * 	OUT
	 * 		response.json
	 *		  {
	 *		  	returnCode		true se l'operazione e' stata eseguita senza errori, false altrimenti
	 *		  	message			messaggio relativo all'operazione eseguita o eventuale messaggio di errore
	 *			form 			url di redirect del DGUE
	 *		  } 
	 */
	public String redirectToDGUE();

}
