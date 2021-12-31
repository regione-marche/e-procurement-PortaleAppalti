package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig;

import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * implementa una lista dei documenti richiesti ed ulteriori 
 */
public class DocumentiAllegatiHelper implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	// eventuale chiave di cifratura
	protected byte[] chiaveCifratura;

	// chiave di sessione per la cifratura file
	protected byte[] chiaveSessione;
	
	// login utente, necessario per la cifratura simmetrica
	protected String username;

	// identificativo univoco della comunicazione
	protected Long idComunicazione;  
	
	// directory temporanea della webapp
	private File tempDir;
	
	// informazioni per la gestione dei documenti standard
	protected List<Long> docRichiestiId = null;
	protected List<File> docRichiesti = null;
	protected List<File> docRichiestiCifrati = null;
	protected List<String> docRichiestiContentType = null;
	protected List<String> docRichiestiFileName = null;
	protected List<Integer> docRichiestiSize = null;
	protected List<String> docRichiestiSha1 = null;
	protected List<String> docRichiestiUuid = null;
	protected List<Integer> docRichiestiStato = null;
		
	// informazioni per la gestione dei documenti da inviare in aggiunta
	protected List<String> docUlterioriDesc = null;
	protected List<File> docUlteriori = null;
	protected List<File> docUlterioriCifrati = null;
	protected List<String> docUlterioriContentType = null;
	protected List<String> docUlterioriFileName = null;
	protected List<Integer> docUlterioriSize = null;
	protected Set<String> docUlterioriNascosti = null;
	protected List<String> docUlterioriSha1 = null;
	protected List<String> docUlterioriUuid = null;
	protected List<Integer> docUlterioriStato = null;
	
	
	/**
	 * Non utilizzato ad oggi 
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param event the binding listener
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		Iterator<File> iter;
		if (this.docRichiesti != null) {
			iter = this.docRichiesti.listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if (file != null && file.exists()) {
					file.delete();
				}
			}
		}	
		if (this.docRichiestiCifrati != null) {
			iter = this.docRichiestiCifrati.listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if(file != null && file.exists()) {
					file.delete();
				}
			}
		}	
		if (this.docUlteriori != null) {
			iter = this.docUlteriori.listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if (file != null && file.exists()) {
					file.delete();
				}
			}
		}	
		if (this.docUlterioriCifrati != null) {
			iter = this.docUlterioriCifrati.listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if (file != null && file.exists()) {
					file.delete();
				}
			}
		}
	}


	/**
	 * costruttore della classe 
	 */
	public DocumentiAllegatiHelper(
			byte[] chiaveCifratura, 
			byte[] chiaveSessione, 
			String username,
			boolean creaDocRichiesti, 
			boolean creaDocUlteriori) 
	{
		this.chiaveCifratura = chiaveCifratura;
		this.chiaveSessione = chiaveSessione;		
		this.username = username;		
		this.tempDir = StrutsUtilities.getTempDir(ServletActionContext.getServletContext());
		this.idComunicazione = null; 
		
		if(creaDocRichiesti) {
			this.docRichiestiId = new ArrayList<Long>();			
			this.docRichiesti = new ArrayList<File>();
			this.docRichiestiCifrati = new ArrayList<File>();
			this.docRichiestiContentType = new ArrayList<String>();
			this.docRichiestiFileName = new ArrayList<String>();
			this.docRichiestiSize = new ArrayList<Integer>();
			this.docRichiestiSha1 = new ArrayList<String>();
			this.docRichiestiUuid = new ArrayList<String>();
			this.docRichiestiStato = new ArrayList<Integer>();
		}		
		if(creaDocUlteriori) {
			this.docUlterioriDesc = new ArrayList<String>();		
			this.docUlteriori = new ArrayList<File>();
			this.docUlterioriCifrati = new ArrayList<File>();
			this.docUlterioriContentType = new ArrayList<String>();
			this.docUlterioriFileName = new ArrayList<String>();
			this.docUlterioriSize = new ArrayList<Integer>();			
			this.docUlterioriSha1 = new ArrayList<String>();
			this.docUlterioriUuid = new ArrayList<String>();
			this.docUlterioriStato = new ArrayList<Integer>();
			this.docUlterioriNascosti = new TreeSet<String>();
		}
	}
		
	public DocumentiAllegatiHelper() {
		this(null, null, null, true, true);
	}
		
	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public List<Long> getDocRichiestiId() {
		return docRichiestiId;
	}

	public void setDocRichiestiId(List<Long> docRichiestiId) {
		this.docRichiestiId = docRichiestiId;
	}
	
	public List<File> getDocRichiesti() {
		return docRichiesti;
	}

	public void setDocRichiesti(List<File> docRichiesti) {
		this.docRichiesti = docRichiesti;
	}

	public List<File> getDocRichiestiCifrati() {
		return this.docRichiestiCifrati;
	}

	public void setDocRichiestiCifrati(List<File> docRichiestiCifrati) {
		this.docRichiestiCifrati = docRichiestiCifrati;
	}

	public List<String> getDocRichiestiContentType() {
		return docRichiestiContentType;
	}

	public void setDocRichiestiContentType(List<String> docRichiestiContentType) {
		this.docRichiestiContentType = docRichiestiContentType;
	}

	public List<String> getDocRichiestiFileName() {
		return docRichiestiFileName;
	}

	public void setDocRichiestiFileName(List<String> docRichiestiFileName) {
		this.docRichiestiFileName = docRichiestiFileName;
	}

	public List<Integer> getDocRichiestiSize() {
		return docRichiestiSize;
	}

	public void setDocRichiestiSize(List<Integer> docRichiestiSize) {
		this.docRichiestiSize = docRichiestiSize;
	}

	public List<String> getDocRichiestiSha1() {
		return docRichiestiSha1;
	}

	public void setDocRichiestiSha1(List<String> docRichiestiSha1) {
		this.docRichiestiSha1 = docRichiestiSha1;
	}

	public List<String> getDocRichiestiUuid() {
		return docRichiestiUuid;
	}

	public void setDocRichiestiUuid(List<String> docRichiestiUuid) {
		this.docRichiestiUuid = docRichiestiUuid;
	}

	public List<Integer> getDocRichiestiStato() {
		return docRichiestiStato;
	}

	public void setDocRichiestiStato(List<Integer> docRichiestiStato) {
		this.docRichiestiStato = docRichiestiStato;
	}

	public List<String> getDocUlterioriDesc() {
		return docUlterioriDesc;
	}

	public void setDocUlterioriDesc(List<String> docUlterioriDesc) {
		this.docUlterioriDesc = docUlterioriDesc;
	}

	public List<File> getDocUlteriori() {
		return docUlteriori;
	}

	public void setDocUlteriori(List<File> docUlteriori) {
		this.docUlteriori = docUlteriori;
	}

	public List<File> getDocUlterioriCifrati() {
		return this.docUlterioriCifrati;
	}

	public void setDocUlterioriCifrati(List<File> docUlterioriCifrati) {
		this.docUlterioriCifrati = docUlterioriCifrati;
	}

	public List<String> getDocUlterioriContentType() {
		return docUlterioriContentType;
	}

	public void setDocUlterioriContentType(List<String> docUlterioriContentType) {
		this.docUlterioriContentType = docUlterioriContentType;
	}

	public List<String> getDocUlterioriFileName() {
		return docUlterioriFileName;
	}

	public void setDocUlterioriFileName(List<String> docUlterioriFileName) {
		this.docUlterioriFileName = docUlterioriFileName;
	}

	public List<Integer> getDocUlterioriSize() {
		return docUlterioriSize;
	}

	public void setDocUlterioriSize(List<Integer> docUlterioriSize) {
		this.docUlterioriSize = docUlterioriSize;
	}

	public List<String> getDocUlterioriSha1() {
		return docUlterioriSha1;
	}

	public void setDocUlterioriSha1(List<String> docUlterioriSha1) {
		this.docUlterioriSha1 = docUlterioriSha1;
	}
	
	public List<String> getDocUlterioriUuid() {
		return docUlterioriUuid;
	}

	public void setDocUlterioriUuid(List<String> docUlterioriUuid) {
		this.docUlterioriUuid = docUlterioriUuid;
	}
	
	public List<Integer> getDocUlterioriStato() {
		return docUlterioriStato;
	}

	public void setDocUlterioriStato(List<Integer> docUlterioriStato) {
		this.docUlterioriStato = docUlterioriStato;
	}

	public Set<String> getDocUlterioriNascosti() {
		return docUlterioriNascosti;
	}

	public void setDocUlterioriNascosti(Set<String> docUlterioriNascosti) {
		this.docUlterioriNascosti = docUlterioriNascosti;
	}

	/**
	 * Aggiungi tutte le info relative ad un documento richiesto con un unico metodo
	 * 
	 * @param id 
	 * 		id del documento 
	 * @param file 
	 * 		file relativo al documento 
	 * @param fileCifrato 
	 * 		file cifrato relativo al documento 
	 * @param contentType 
	 * 		tipo di documnto 
	 * @param fileName 
	 * 		nome file del documento 
	 * @param size 
	 * 		dimensione del documento
	 * @param sha1 
	 * 		digest del documento non cifrato
	 * @param uuid 
	 * 		identificativo univoco del documento usato per il download 
	 * 		su richiesta del file dal servizio
	 * @param stato
	 * 		1=modificato -1=eliminato indica se il documento è stato modificato 
	 *      e quindi va inviato al servizio
	 * 
	 * @throws GeneralSecurityException 
	 */
	private void addDocRichiesto(
			Long id, 
			File file,			
			File fileCifrato,
			String contentType, 
			String fileName, 
			Integer size, 
			String sha1,
			String uuid,
			int stato) throws GeneralSecurityException 
	{
		this.docRichiestiId.add(id);
		this.docRichiesti.add(file);		
		this.docRichiestiCifrati.add(fileCifrato);
		this.docRichiestiContentType.add(validateContentType(contentType));
		this.docRichiestiFileName.add(fileName);
		this.docRichiestiSize.add(size);
		this.docRichiestiSha1.add(sha1);
		this.docRichiestiUuid.add(uuid);  		
		this.docRichiestiStato.add(stato);
	}
	
	/**
	 * Aggiungi tutte le info relative ad un documento richiesto con un unico metodo
	 * 
	 * @param id
	 * 		id del documento 
	 * @param file 
	 * 		file relativo al documento 
	 * @param contentType 
	 * 		tipo di documnto 
	 * @param fileName 
	 * 		nome file del documento 
	 * @param evento
	 * 		opzionale, è l'evento da aggiornare in caso di tracciature
	 * 
	 * @throws GeneralSecurityException 
	 */
	public void addDocRichiesto(
			Long id, 
			File file,			
			String contentType, 
			String fileName, 
			Event evento) throws GeneralSecurityException
	{
		if(this.docRichiesti == null) {
			return;
		}

		File f = new File(file.getParent() + 
				          java.io.File.separatorChar + 
				          FileUploadUtilities.generateFileName());
		file.renameTo(f);
		
		String sha = this.getDigest(f, evento);

		File fc = null;
		if (this.chiaveSessione != null) {
			// nel caso di presenza di cifratura, al caricamento di un file 
			// si procede con la sua cifratura preventiva			
			fc = cifraDocumento(f, this.chiaveSessione, this.username);			
		}

		// NB: non viene utilizzato in W_DOCDIG.TIPDOC (char[5])
		contentType = null;
		
		int size = (int) (f != null 
		          		  ? FileUploadUtilities.getFileSize(f) 
		          		  : 0); 

		this.addDocRichiesto(
				id, 
				f, 
				fc, 
				contentType, 
				fileName, 
				size, 
				sha, 
				FileUploadUtilities.generateFileName(),
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO);
	}

	public void addDocRichiesto(
			Long id, 
			File file,			
			String contentType, 
			String fileName) throws GeneralSecurityException
	{
		this.addDocRichiesto(id, file, contentType, fileName, null);
	}

	/**
	 * Aggiungi tutte le info relative ad un documento richiesto da un DocumentoType
	 * 
	 * @param documento 
	 * 		documento recuperato da una busta xml, contentente le info di un 
	 * 		allegato 
	 * @param externalFile stream con il contenuto del file se presente fuori dall'xml
	 *  
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	public void addDocRichiesto(DocumentoType documento, byte[] externalFile) 
		throws GeneralSecurityException, IOException 
	{
		if(this.docRichiesti == null) {
			return;
		}
		
		File f = null;
		File fc = null;
		byte[] contenutoXML = null;
		byte[] contenutoInChiaro = null;
		String sha = null;
		if(documento.getFile() != null) {
			contenutoXML = documento.getFile();
		} else {
			contenutoXML = externalFile;
		}
		if(contenutoXML != null) {
			try {			
				if (this.chiaveSessione != null) {
					f = null;
					fc = this.generateFileFromBytes(contenutoXML);
					contenutoInChiaro = decifraDocumento(contenutoXML, this.chiaveSessione, this.username);								
				} else { 
					f = this.generateFileFromBytes(contenutoXML);
					fc = null;
					contenutoInChiaro = contenutoXML;
				} 			
				sha = (contenutoInChiaro != null ? DigestUtils.shaHex(contenutoInChiaro) : null);
			} catch (Exception e) {
				throw new IOException(e.getMessage(), e);
			} catch (OutOfMemoryError e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		
		// è un documento che arriva da un XML, quindi da una comunicazione, 
		// quindi dal db... 
		// lo stato dei documenti letti dal servizio dovrebbe essere 0 !!! 
		this.addDocRichiesto(
				documento.getId(), 
				f, 
				fc,
				documento.getContentType(), 
				documento.getNomeFile(), 
				(int) Math.ceil(documento.getDimensione() / 1024.0),
				sha,
				documento.getUuid(),				
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO); // .STATO_DOCUMENTO_BUSTA_MODIFICATO); 
	}
	
	/**
	 * Rimuovi le info relative ad un documento richiesto con un unico metodo
	 * 
	 * @param id
	 *   	id (posizione) del documento da elmiminare
	 */
	public void removeDocRichiesto(int id) {
		if(this.docRichiesti == null) {
			return;
		}

		int index = id;
	
		this.docRichiestiId.remove(index);
		this.docRichiestiContentType.remove(index);
		this.docRichiestiFileName.remove(index);
		this.docRichiestiSize.remove(index);
		this.docRichiestiSha1.remove(index);
		this.docRichiestiUuid.remove(index);		   		
		this.docRichiestiStato.remove(index);
		File file = this.docRichiesti.remove(index);		
		File fileCriptato = this.docRichiestiCifrati.remove(index);
		if (file != null && file.exists()) {
			// si rimuove il file in chiaro...
			file.delete();
		}
		if (fileCriptato != null && fileCriptato.exists()) {
			// si rimuove il file criptato...
			fileCriptato.delete();
		}
	}

	/**
	 * Aggiungi tutte le info relative ad un documento ulteriore con un unico metodo
	 * 
	 * @param descrizione 
	 * 		descrizione del documento  
	 * @param file 
	 * 		file relativo al documento 
	 * @param fileCifrato 
	 * 		file cifrato relativo al documento 
	 * @param contentType 
	 * 		tipo di documnto 
	 * @param fileName 
	 * 		nome file del documento 
	 * @param size 
	 * 		dimensione del douumento
	 * @param sha1 
	 * 		digest del documento non cifrato
	 * @param uuid 
	 * 		identificativo univoco del documento usato per il download 
	 * 		su richiesta del file dal servizio
	 * @param stato
	 * 		1=modificato -1=eliminato indica se il documento è stato modificato 
	 *      e quindi va inviato al servizio
	 * @param nascondi
	 *		... 		 
	 * 
	 * @throws GeneralSecurityException 
	 */
	private void addDocUlteriore(
			String descrizione, 
			File file,			
			File fileCifrato,
			String contentType, 
			String fileName, 
			Integer size, 
			String sha1,
			String uuid,
			int stato, 			
			boolean nascondi) throws GeneralSecurityException
	{
		this.docUlterioriDesc.add(descrizione);
		this.docUlteriori.add(file);	
		this.docUlterioriCifrati.add(fileCifrato);
		this.docUlterioriContentType.add(validateContentType(contentType));
		this.docUlterioriFileName.add(fileName);		
		this.docUlterioriSize.add(size);
		if(nascondi) {
			this.docUlterioriNascosti.add(descrizione);
		}
		this.docUlterioriSha1.add(sha1);	
		this.docUlterioriUuid.add(uuid);		
		this.docUlterioriStato.add(stato);
	}

	/**
	 * Aggiungi tutte le info relative ad un documento ulteriore con un unico metodo
	 * 
	 * @param descrizione
	 * 		descrizione del documento 
	 * @param file 
	 * 		file relativo al documento 
	 * @param contentType 
	 * 		tipo di documnto 
	 * @param fileName 
	 * 		nome file del documento 
	 * @param evento
	 * 		opzionale, è l'evento da aggiornare in caso di tracciature
	 *
	 * @throws GeneralSecurityException 
	 */
	public void addDocUlteriore(
			String descrizione, 
			File file,			
			String contentType, 
			String fileName,
			Event evento) throws GeneralSecurityException
	{
		if(this.docUlteriori == null) {
			return;
		}
		 
		File f = new File(file.getParent() + 
				          java.io.File.separatorChar + 
				          FileUploadUtilities.generateFileName());
		file.renameTo(f);
		
		String sha = this.getDigest(f, evento);
				
		File fc = null;
		if (this.chiaveSessione != null) {
			// nel caso di presenza di cifratura, al caricamento di un file 
			// si procede con la sua cifratura preventiva			
			fc = cifraDocumento(f, this.chiaveSessione, this.username);			
		}
		
		// NB: non viene utilizzato in W_DOCDIG.TIPDOC (char[5])
		contentType = null;
		
		int size = (int) (f != null 
				          ? FileUploadUtilities.getFileSize(f) 
				          : 0);
		
		this.addDocUlteriore(				
				descrizione, 
				f, 
				fc, 
				contentType, 
				fileName, 
				size, 
				sha, 
				FileUploadUtilities.generateFileName(),
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO,
				false);
	}

	public void addDocUlteriore(
			String descrizione, 
			File file,			
			String contentType, 
			String fileName) throws GeneralSecurityException
	{
		this.addDocUlteriore(descrizione, file, contentType, fileName, null);
	}
	
	/**
	 * Aggiungi tutte le info relative ad un documento ulteriore da un DocumentoType
	 * 
	 * @param documento 
	 * 		documento recuperato da una busta xml, contentente le info di un 
	 * 		allegato 
	 * @param externalFile stream con il contenuto del file se presente fuori dall'xml
 	 * @param nascondi
 	 * 		...
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 */
	public void addDocUlteriore(DocumentoType documento, byte[] externalFile, boolean nascondi) 
		throws IOException, GeneralSecurityException
	{
		if(this.docUlteriori == null) {
			return;
		}
		
		File f = null;
		File fc = null;
		byte[] contenutoXML = null;
		byte[] contenutoInChiaro = null;
		String sha = null;
		if(documento.getFile() != null) {
			contenutoXML = documento.getFile();
		} else {
			contenutoXML = externalFile;
		}
		if(contenutoXML != null) {
			try {			
				if (this.chiaveSessione != null) {
					f = null;
					fc = this.generateFileFromBytes(contenutoXML);
					contenutoInChiaro = decifraDocumento(contenutoXML, this.chiaveSessione, this.username);								
				} else { 
					f = this.generateFileFromBytes(contenutoXML);
					fc = null;
					contenutoInChiaro = contenutoXML;
				} 			
				sha = (contenutoInChiaro != null ? DigestUtils.shaHex(contenutoInChiaro) : null);
			} catch (Exception e) {
				throw new IOException(e.getMessage(), e);
			} catch (OutOfMemoryError e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		
		this.addDocUlteriore(
				documento.getDescrizione(), 
				f, 
				fc,
				documento.getContentType(), 
				documento.getNomeFile(), 
				(int) Math.ceil(documento.getDimensione() / 1024.0),
				sha,
				documento.getUuid(), 
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO, // STATO_DOCUMENTO_BUSTA_MODIFICATO, //documento.getModificato(),				
				nascondi);
	}
	
	/**
	 * Rimuovi le info relative ad un documento richiesto con un unico metodo
	 * 
	 * @param id
	 * 		id (posizione) del documento da eliminare 
	 */
	public void removeDocUlteriore(int id) {
		if(this.docUlteriori == null) {
			return;
		}

		int index = id;
		
		this.docUlterioriDesc.remove(index);		
		this.docUlterioriContentType.remove(index);
		this.docUlterioriFileName.remove(index);
		this.docUlterioriSize.remove(index);
//		this.docUlterioriNascosti.remove(null);				//?!?
		this.docUlterioriSha1.remove(index);
		this.docUlterioriUuid.remove(index);
		this.docUlterioriStato.remove(index);
		File file = this.docUlteriori.remove(index);
		File fileCriptato = this.docUlterioriCifrati.remove(index);
		if (file != null && file.exists()) {
			// si rimuove il file in chiaro...
			file.delete();
		}
		if (fileCriptato != null && fileCriptato.exists()) {
			// si rimuove il file criptato...
			fileCriptato.delete();
		}		
	}
	
	/**
	 * Copia i documenti richiesti ed ulteriori in una lista di DocumentoType.
	 * Generalmente utilizzato per preparare l'XML dei documenti di una busta XML. 
	 * 
	 * @param listaDocumenti
	 * 		lista dei documenti da aggiornare
	 * @param attachFileContents
	 * 		indica se trasferire anche il contenuto binario dei file nella lista
	 * @param applicaCifratura
	 * 		indica se il contenuto binario è cifrato o meno
	 * 
	 */ 
	public void addDocumenti(
			ListaDocumentiType listaDocumenti, 
			boolean attachFileContents,
			boolean applicaCifratura) throws IOException 
	{
		if(applicaCifratura) {
			applicaCifratura = (this.chiaveCifratura != null ? true : false);
		}
		
		// si aggiungono i documenti richiesti
		if(this.docRichiesti != null) {
			for (int i = 0; i < this.docRichiestiId.size(); i++) {
				Long id = this.docRichiestiId.get(i);
				DocumentoType documento = listaDocumenti.addNewDocumento();
				documento.setNomeFile(this.docRichiestiFileName.get(i));
				documento.setContentType(this.docRichiestiContentType.get(i));			
				if (attachFileContents) {
					documento.setUuid(null);
					documento.setFile(null);
					if( StringUtils.isEmpty(this.docRichiestiUuid.get(i)) ) {
						if (!applicaCifratura) {
							documento.setFile(FileUtils.readFileToByteArray(this.docRichiesti.get(i)));
						} else {
							documento.setFile(FileUtils.readFileToByteArray(this.docRichiestiCifrati.get(i)));
						}
					} else {					
						documento.setUuid(this.docRichiestiUuid.get(i));
					}
				}
				documento.setId(id);
				
//				// NB: lo stato dei documenti non andrebbe registrato nel 
//				// documento xml che andrà inviato al servizio.
//				// Questo perchè lo stato serve solo lato client per gestire 
//				// quali sono i documenti da inviare o meno al servizio, 
//				// percio' prima di inviare la comunicazione si resettano gli 
//				// stati dei documenti.
//				// In caso di errore la classe mantiene lo stato dei documenti!!!
//				documento.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);	
			}
		}	
			
		// si aggiungono i documenti ulteriori
		if(this.docUlteriori != null) {
			for (int i = 0; i < this.docUlterioriDesc.size(); i++) {
				String descrizione = this.docUlterioriDesc.get(i);
				DocumentoType documento = listaDocumenti.addNewDocumento();
				documento.setNomeFile(this.docUlterioriFileName.get(i));
				documento.setContentType(this.docUlterioriContentType.get(i));
				if (attachFileContents) {
					documento.setUuid(null);
					documento.setFile(null);				
					if( StringUtils.isEmpty(this.docUlterioriUuid.get(i)) ) {
						if (!applicaCifratura) {
							documento.setFile(FileUtils.readFileToByteArray(this.docUlteriori.get(i)));
						} else {
							documento.setFile(FileUtils.readFileToByteArray(this.docUlterioriCifrati.get(i)));
						}
					} else {
						documento.setUuid(this.docUlterioriUuid.get(i));
					}
				}				
				documento.setDescrizione(descrizione);
				
//				// NB: lo stato dei documenti non andrebbe registrato nel 
//				// documento xml che andrà inviato al servizio.
//				// Questo perchè lo stato serve solo lato client per gestire 
//				// quali sono i documenti da inviare o meno al servizio, 
//				// percio' prima di inviare la comunicazione si resettano gli 
//				// stati dei documenti.
//				// In caso di errore la classe mantiene lo stato dei documenti!!!
//				documento.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);  
			}
		}
	}
	
	/**
	 * Copia i documenti richiesti ed ulteriori in una lista di DocumentoType.
	 * Generalmente utilizzato per preparare l'XML dei documenti di una busta XML.
	 *  
	 * @param listaDocumenti
	 * 		lista dei documenti da aggiornare
	 * @param attachFileContents
	 * 		indica se trasferire anche il contenuto binario dei file nella lista
	 *  
	 */
	public void addDocumenti(
			ListaDocumentiType listaDocumenti, 
			boolean attachFileContents) throws IOException 
	{
		// NB: anche se si passa applicaCifratura=true comunque il metodo
		//     generale addDocumenti(...) verifica se è effettivamente 
		//     presente o meno la cifratura prima di gestire i documenti!!! 
		this.addDocumenti(listaDocumenti, attachFileContents, true);
	}

	/**
	 * Converte i documenti allegati dell'helper in una lista di AllegatoComunicazioneType
	 * 
	 * @param documenti
	 * 		helper dei documenti da convertire 
	 * @param allegatiXml
	 * 		lista di AllegatoComunicazioneType nella quale copiare i documenti
	 * @throws Exception 
	 */
	public void documentiToAllegatiComunicazione(
			//DocumentiAllegatiHelper documenti, 
			List<AllegatoComunicazioneType> allegatiXml) 
	{		
		AllegatoComunicazioneType allegato;
		boolean cifratura = (this.chiaveCifratura != null);
		boolean fileNonCifrato = false;
		
		// aggiungi i documenti richiesti 
	    if(this.getDocRichiesti() != null) {
			for(int i = 0; i < this.getDocRichiesti().size(); i++) {			
				// prepara il contenuto in byte dell'allegato da inviare al 
				// servizio; in caso di cifratura si invia il contenuto 
				// cifrato
				byte[] contenuto = null;
				try {
					File f = this.getDocRichiesti().get(i);
					if(this.getDocRichiestiCifrati() != null && 
					   this.getDocRichiestiCifrati().size() == this.getDocRichiesti().size() &&
					   this.getDocRichiestiCifrati().get(i) != null) {
						f = this.getDocRichiestiCifrati().get(i);
					}
					if(f != null) {
						contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(f);
					} else {
						ApsSystemUtils.getLogger().debug("documentiToAllegatiComunicazione: file nullo docRichiesti " +
								"id=" + this.getDocRichiestiId().get(i) + ", " +  
								"filename=" + this.getDocRichiestiFileName().get(i) + ", " +
								"idCom=" + this.idComunicazione);
					}
				} catch(Exception ex) {			
					contenuto = null;
					ApsSystemUtils.logThrowable(ex, this, "documentiToAllegatiComunicazione");
				}
				
				//************************************************************************************************************
				// (SEPR-173)
				if(cifratura && contenuto != null) {
					// in caso di cifratura, verifica se il file e' stato cifrato!!!
					try {
						String sha1 = this.getDocRichiestiSha1().get(i);	
						String shaCriptato = DigestUtils.shaHex(contenuto);
						if(shaCriptato.equals(sha1)) {
							// sha1 == shaHex(file criptato) ==> il file non è cifrato!!!
							fileNonCifrato = true;
							ApsSystemUtils.getLogger().warn("documentiToAllegatiComunicazione: docRichiesti file non cifrato " +
									"id=" + this.getDocRichiestiId().get(i) + ", " +  
									"filename=" + this.getDocRichiestiFileName().get(i) + ", " +
									"idCom=" + this.idComunicazione);
							
							File fc = cifraDocumento(this.getDocRichiestiCifrati().get(i), this.chiaveSessione, this.username);
							contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(fc);	
						}
					} catch(Exception e) {
						ApsSystemUtils.logThrowable(e, this, "documentiToAllegatiComunicazione", "errore durante la cifratura del file");
					}
				}
				
				// crea ed aggiungi il nuovo allegato per la comunicazione
				allegato = new AllegatoComunicazioneType();
				allegato.setUuid( this.getDocRichiestiUuid().get(i) );
				allegato.setModificato( this.getDocRichiestiStato().get(i) );
				allegato.setNomeFile( this.getDocRichiestiFileName().get(i) );
				allegato.setDescrizione( null );
				allegato.setTipo( this.getDocRichiestiContentType().get(i) );				
				allegato.setFile(contenuto);
				allegatiXml.add(allegato);
			}
	    }
		
		// aggiungi i documenti ulteriori 
	    if(this.getDocUlteriori() != null) {
			for(int i = 0; i < this.getDocUlteriori().size(); i++) {
				// prepara il contenuto in byte dell'allegato da inviare al 
				// servizio; in caso di cifratura si invia il contenuto 
				// cifrato
				byte[] contenuto = null;
				try {
					File f = this.getDocUlteriori().get(i);
					if(this.getDocUlterioriCifrati() != null &&
					   this.getDocUlterioriCifrati().size() == this.getDocUlteriori().size() &&						
					   this.getDocUlterioriCifrati().get(i) != null) {
						f = this.getDocUlterioriCifrati().get(i);
					}
					if(f != null) {
						contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(f);
					} else {
						ApsSystemUtils.getLogger().debug("documentiToAllegatiComunicazione: file nullo docUlteriori " +
								"desc=" + this.getDocUlterioriDesc().get(i) + ", " +  
								"filename=" + this.getDocUlterioriFileName().get(i) + ", " +
								"idCom=" + this.idComunicazione);
					}
				} catch(Exception ex) {			
					contenuto = null;
					ApsSystemUtils.logThrowable(ex, this, "documentiToAllegatiComunicazione");
				}
				
				//************************************************************************************************************
				// (SEPR-173)
				if(cifratura && contenuto != null) {
					// in caso di cifratura, verifica se il file e' stato cifrato!!!
					try {
						String sha1 = this.getDocUlterioriSha1().get(i);	
						String shaCriptato = DigestUtils.shaHex(contenuto);
						if(shaCriptato.equals(sha1)) {
							// sha1 == shaHex(file criptato) ==> il file non è cifrato!!!
							fileNonCifrato = true;
							ApsSystemUtils.getLogger().warn("documentiToAllegatiComunicazione: docUlteriori file non cifrato " +
									"desc=" + this.getDocUlterioriDesc().get(i) + ", " +  
									"filename=" + this.getDocUlterioriFileName().get(i) + ", " +
									"idCom=" + this.idComunicazione);
							File fc = cifraDocumento(this.getDocUlterioriCifrati().get(i), this.chiaveSessione, this.username);
							contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(fc);
						}
					} catch(Exception e) {
						ApsSystemUtils.logThrowable(e, this, "documentiToAllegatiComunicazione", "errore durante la cifratura del file");
					}	
				}
				
				// crea ed aggiungi il nuovo allegato per la comunicazione
				allegato = new AllegatoComunicazioneType();
				allegato.setUuid( this.getDocUlterioriUuid().get(i) );
				allegato.setModificato( this.getDocUlterioriStato().get(i) );
				allegato.setNomeFile( this.getDocUlterioriFileName().get(i) );				
				allegato.setDescrizione( this.getDocUlterioriDesc().get(i) );
				allegato.setTipo( this.getDocUlterioriContentType().get(i) );
				allegato.setFile(contenuto);
				allegatiXml.add(allegato);
			}
	    }
	}

	/**
	 * Resetta gli stati dei documenti richiesti ed ulteriori a 0 (non modificato) 
	 * Dopo l'invio al servizio i documenti vanno marcati come gestiti 
	 * altrimenti in caso di errore, reinviando la comunicazione si rispediscono
	 * tutti i documenti marcati "modificati" 
	 */
	public void resetStatiInvio() {
		// aggiungi i documenti richiesti 
	    if(this.docRichiesti != null) {
			for(int i = 0; i < this.docRichiesti.size(); i++) {			
				// si puo' resettare il flag "stato" dell'helper altrimenti
				// il campo modificato viene memorizzato del documento xml
				// quando in realta' il documento e' gia' stato gestito				
				this.docRichiestiStato.set(i, PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
			}
	    }
		
		// aggiungi i documenti ulteriori 
	    if(this.docUlteriori != null) {
			for(int i = 0; i < this.docUlteriori.size(); i++) {
				// si puo' resettare il flag "modificato" dell'helper altrimenti
				// il campo modificato viene memorizzato del documento xml
				// quando in realta' il documento e' gia' stato gestito												
				this.docUlterioriStato.set(i, PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
			}
	    }	    
	}

	/**
	 * Genera in area temporanea un file a partire da un byte array 
	 * 
	 * @throws IOException 
	 */
	private File generateFileFromBytes(byte[] file) 
		throws IOException 
	{
		File f = null;		
		try {
			// genero un file per il contenuto in chiaro...
			f = new File(this.tempDir.getAbsolutePath() + 
					     java.io.File.separatorChar + 
					     FileUploadUtilities.generateFileName());
			org.apache.commons.io.FileUtils.writeByteArrayToFile(f, file);
		} catch (OutOfMemoryError e) {
			throw new IOException(e.getMessage());
		} finally {
//			if(f != null && f.exists()) {
//				f.delete();
//			}
		}				
		return f;
	}
	
	/**
	 * Cifra un documento nel momento in cui viene allegato
	 * 
	 * @param allegato
	 *            file allegato in chiaro
	 * @param chiaveSessione
	 *            chiave di sessione a cifratura simmetrica
	 * @param token
	 * 			  username dell'utente loggato
	 * 
	 * @return reference al file cifrato
	 * 
	 * @throws GeneralSecurityException 
	 */
	private File cifraDocumento(File allegato, byte[] chiaveSessione, String token) 
		throws GeneralSecurityException
	{		
		Cipher cipher = null;				
		FileInputStream fis = null;
		FileOutputStream fos = null;
		File fileCifrato = allegato;
		try {
			if(chiaveSessione != null && token != null) {
				cipher = SymmetricEncryptionUtils.getEncoder(chiaveSessione, token);				
				fileCifrato = new File(allegato.getParent() + 
									   File.separatorChar + 
						               FileUploadUtilities.generateFileName());
				fis = new FileInputStream(allegato.getPath());
				fos = new FileOutputStream(fileCifrato);
				SymmetricEncryptionUtils.translate(cipher, fis, fos);
				fos.close();
				fis.close();
			}
		} catch (InvalidKeyException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (NoSuchProviderException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (NoSuchPaddingException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} catch (IOException e) {
			throw new GeneralSecurityException(e.getMessage(), e);
		} finally {
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}		
		return fileCifrato;
	}

	/**
	 * Estrai l'allegato da un allegato cifrato
	 *  
	 * @throws GeneralSecurityException 
	 */
	private byte[] decifraDocumento(byte[] file, byte[] chiaveSessione, String token) 
		throws GeneralSecurityException 
	{
		byte[] contenuto = null;				
		try {
			// decripta l'allegato...
			Cipher cipher = SymmetricEncryptionUtils.getDecoder(
					chiaveSessione, 
					token);
			contenuto = SymmetricEncryptionUtils.translate(cipher, file);
			
		} catch (InvalidKeyException e) {
			throw new GeneralSecurityException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new GeneralSecurityException(e.getMessage());
		} catch (NoSuchProviderException e) {
			throw new GeneralSecurityException(e.getMessage());
		} catch (NoSuchPaddingException e) {
			throw new GeneralSecurityException(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			throw new GeneralSecurityException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new GeneralSecurityException(e.getMessage());
		}			 
		return contenuto;		
	}
	
	/**
	 * Calcola il digest di un file
	 * 
	 * @param f 
	 * 		file in chiaro del quale calcolare il digest
	 * @param evento
	 * 		opzionale, evento nel quale registrare il risultato del calcolo
	 */	
	private String getDigest(File f, Event evento) {
		String sha1 = null;
		try {
			byte[] contenuto = FileUtils.readFileToByteArray(f);
			sha1 = (contenuto != null ? DigestUtils.shaHex(contenuto) : null);
			if(evento != null) {
				evento.setMessage(evento.getMessage() + ", SHA1=" + sha1);
			}
		} catch (IOException e) {
			if(evento != null) {
				evento.setMessage(evento.getMessage() + ", SHA1=non calcolato causa errore (" + e.getMessage() + ")");
			}
			//throw e;
		}
		return sha1;
	}

	/**
	 * Restituisce il contenuto binario in chiaro di un documento allegato
	 * Se il file non è presente in area temporanea, viene richiesto al servizio 
	 * e scaricato.
	 * Se viceversa il file è già presente in area temporanea viene restituito 
	 * il contenuto di tale file.
	 * 
	 * @param listaFile
	 * 		lista dei files in chiaro
	 * @param listaFileCifrati
	 * 		lista dei files cifrati
	 * @param id
	 * 		id (posizione) del file nelle liste
	 * @param idDocumento 
	 * 		uuid del documento per il recupero del contenuto binario da 
	 * 		richiedere al servizio
	 */
	private byte[] getAllegatoComunicazione(
			List<File> listaFile, 
			List<File> listaFileCifrati, 
			int id, 
			String idDocumento)  
	{
		byte[] contenuto = null;
		try {
			// se e' la prima volta che si scarica il file, si recupera dal
			// servizio lo stream del documento e lo si salva in area 
			// temporanea...
			// se il file e' cifrato, salva in area temporanea la versione 
			// decifrata 
			File file = (id < listaFile.size() ? listaFile.get(id) : null);
			File fileCifrato = (id < listaFileCifrati.size() ? listaFileCifrati.get(id) : null);
			
			// lo stream e' gia' presente in locale o va scaricato ?
			boolean downloadFile = false; 
			if(chiaveSessione != null) {
				downloadFile = (file == null && fileCifrato == null);
			} else {
				downloadFile = (file == null);
			} 
			
			if(downloadFile) {
				contenuto = ComunicazioniUtilities.getAllegatoComunicazione(
						this.idComunicazione,
						idDocumento); 
			} else {
				// non e' necessario rileggere il contenuto in chiaro...
				// ...se file != null significa che il file è già presente 
				// nel filesystem, basta rileggerne il contenuto !!!
				if(fileCifrato != null) {
					contenuto = FileUtils.readFileToByteArray(fileCifrato);
				} else if(file != null) {
					contenuto = FileUtils.readFileToByteArray(file);
				}
			}
			
			// se necessario, salva in locale il file cifrato...
			// se e' gia' presente il file in chiaro non serve decifrare il 
			// file cifrato			
			if(chiaveSessione != null) {
				if(file == null) {					
					if(fileCifrato != null) {
						contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(fileCifrato);
					} else if(contenuto != null) {						
						fileCifrato = new File(this.tempDir.getAbsolutePath() + 
						        		   	   java.io.File.separatorChar + 
						        		   	   FileUploadUtilities.generateFileName());
						listaFileCifrati.set(id, fileCifrato);
						org.apache.commons.io.FileUtils.writeByteArrayToFile(fileCifrato, contenuto);
					} 						
					
					// decifra il file...
					contenuto = decifraDocumento(contenuto, chiaveSessione, username);
				}
			}
					
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "getAllegatoComunicazione");	
		} catch (IOException e) {
			ApsSystemUtils.logThrowable(e, this, "getAllegatoComunicazione");
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "getAllegatoComunicazione");
		}					
		return contenuto;
	}

	/**
	 * Recupera lo stream binario del documento richiesto 
	 */
	public byte[] getContenutoDocRichiesto(int id) {
		String uuid = (id < this.docRichiestiUuid.size() ? this.docRichiestiUuid.get(id) : null);
		byte[] contenuto = this.getAllegatoComunicazione(
				this.docRichiesti,
				this.docRichiestiCifrati,
				id,
				uuid);
		return contenuto; 
	}

	/**
	 * Recupera lo stream binario del documento ulteriore
	 */
	public byte[] getContenutoDocUlteriore(int id) {
		String uuid = (id < this.docUlterioriUuid.size() ? this.docUlterioriUuid.get(id) : null);
		byte[] contenuto = this.getAllegatoComunicazione(
				this.docUlteriori,
				this.docUlterioriCifrati,
				id,
				uuid);
		return contenuto;
	}
	
	/**
	 * Esegui il download di un documento
	 * 
	 * @param listaFile 
	 * 		lista dei file di appartenenza del documento 
	 * @param id 
	 * 		id (posizione) del documento nella lista dei file
	 * @param isDocRichiesto 
	 * 		TRUE indica un documento richiesto, 
	 *      FALSE indica un documento ulteriore 
	 */
	private InputStream downloadDocumento(
			List<File> listaFile, 
			int id, 
			boolean isDocRichiesto) throws ApsException, IOException, GeneralSecurityException  
	{
		File f = listaFile.get(id);
		if(f != null) {
			// non e' necessario rileggere il contenuto in chiaro...
			// ...si sta per creare lo stream sul file gia' esistente!!!
		} else {
			// se necessario, decifra e salva in locale il file in chiaro...
			byte[] contenuto = null;
			if(isDocRichiesto) { 
				contenuto = this.getContenutoDocRichiesto(id);
			} else {
				contenuto = this.getContenutoDocUlteriore(id);
			}
			if(contenuto != null) {
				f = new File(this.tempDir.getAbsolutePath() + 
						     java.io.File.separatorChar + 
						     FileUploadUtilities.generateFileName());									 
				org.apache.commons.io.FileUtils.writeByteArrayToFile(f, contenuto);
				listaFile.set(id, f);
			}
			
			// informa esplicitamente il Garbage Collector che questa memoria non 
			// serve piu'!!!
			contenuto = null;	
			//System.gc();
		}
		
		// crea lo stream del file in chiaro...
		return new FileInputStream(f);		
	}
	
	/**
	 * Esegui il download di un documento richiesto  
	 *  
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws ApsException 
	 */
	public InputStream downloadDocRichiesto(int id) 
		throws ApsException, IOException, GeneralSecurityException  
	{
		return this.downloadDocumento(this.docRichiesti, id, true);
	}	
	
	/**
	 * Esegui il download di un documento ulteriore
	 *  
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws ApsException 
	 */
	public InputStream downloadDocUlteriore(int id) 
		throws ApsException, IOException, GeneralSecurityException  
	{
		return this.downloadDocumento(this.docUlteriori, id, false);
	}
		
//	/**
//	 * Copia i documenti da un DocumentiAllegatiHelper
//	 */
//	public void copyFrom(DocumentiAllegatiHelper documenti) {
//		if(documenti != null) {
//			// documenti richiesti
//			this.setDocRichiesti(documenti.getDocRichiesti());
//			this.setDocRichiestiCifrati(documenti.getDocRichiestiCifrati());
//			this.setDocRichiestiContentType(documenti.getDocRichiestiContentType());
//			this.setDocRichiestiFileName(documenti.getDocRichiestiFileName());
//			this.setDocRichiestiId(documenti.getDocRichiestiId());
//			this.setDocRichiestiStato(documenti.getDocRichiestiStato());
//			this.setDocRichiestiSha1(documenti.getDocRichiestiSha1());
//			this.setDocRichiestiSize(documenti.getDocRichiestiSize());
//			this.setDocRichiestiUuid(documenti.getDocRichiestiUuid());
//			
//			// documenti ulteriori
//			this.setDocUlteriori(documenti.getDocUlteriori());
//			this.setDocUlterioriCifrati(documenti.getDocUlterioriCifrati());
//			this.setDocUlterioriContentType(documenti.getDocUlterioriContentType());
//			this.setDocUlterioriDesc(documenti.getDocUlterioriDesc());
//			this.setDocUlterioriFileName(documenti.getDocUlterioriFileName());
//			this.setDocUlterioriStato(documenti.getDocUlterioriStato());
//			this.setDocUlterioriNascosti(documenti.getDocUlterioriNascosti());
//			this.setDocUlterioriSha1(documenti.getDocUlterioriSha1());
//			this.setDocUlterioriSize(documenti.getDocUlterioriSize());
//			this.setDocUlterioriUuid(documenti.getDocUlterioriUuid());
//			
//			// copia l'id comunicazione per consentire il download degli allegati...
//			this.setIdComunicazione(documenti.getIdComunicazione());
//		}
//	}		

	/**
	 * valida il contentType di un allegato  
	 * il content type viene inserito nella colonna della tabella W_DOCDIG.DOCDIGTIPO VARCHAR[5] 
	 */
	private String validateContentType(String contentType) {
		String fileType = contentType;
		
		// tipi di content type riconosciuti...
		if(fileType != null) {
			if("xml".equalsIgnoreCase(fileType) ||
			   "pdf".equalsIgnoreCase(fileType) ||
			   "p7m".equalsIgnoreCase(fileType) ||
			   "xsd".equalsIgnoreCase(fileType) ||
			   "txt".equalsIgnoreCase(fileType) ) { 
				// OK
			} else {
				// tipi non riconosciuti
				if(fileType.length() > 5) {
					fileType = fileType.substring(0, 5);
				}
			}
		}
		return fileType;		
	}
	
	/**
	 * Popola l'helper con i documenti presenti nella comunicazione
	 *
	 * @param listaDocumenti la lista dei documenti da popolare
	 * 
     *
	 * @throws IOException
	 */
	protected void popolaDocumentiFromComunicazione(
			ListaDocumentiType listaDocumenti,
			long idComunicazione) 
		throws IOException 
	{
		boolean errore = false;
		
		this.setIdComunicazione(idComunicazione);
		
		// nel caso di cifratura applicata, oltre ad estrarre i documenti 
		// vanno anche posti in un contenitore di file criptati 
		DocumentoType documento;
		for (int i = 0; i < listaDocumenti.sizeOfDocumentoArray(); i++) {
			documento = listaDocumenti.getDocumentoArray(i);
			try {
				byte[] contenuto = null;
				// si recupera il contenuto se esterno all'xml
				if (documento.getUuid() != null) {
					// 13/03/2019
					// NB: non scaricare/decifrare su FileSystem 
					//     tutti gli allegati in apertura della busta
					//     Posticipa la fase di scarica/decifra al momento
					//     della richiesta di download dell'allegato!
//					contenuto = ComunicazioniUtilities
//						.getAllegatoComunicazione(idComunicazione, 
//												  documento.getUuid());
				}
				if (documento.getId() != 0) {
					this.addDocRichiesto(documento, contenuto);
				} else {
					this.addDocUlteriore(documento, contenuto, false);
				}				
			} catch (GeneralSecurityException e) {
				ApsSystemUtils.logThrowable(new GeneralSecurityException(
						(documento.getDescrizione() != null ? documento.getDescrizione() : "") + ", " +
						(documento.getNomeFile() != null ? documento.getNomeFile() : "") + " " +
						e.getMessage()), 
						this, "popolaDocumentiFromComunicazione");
				errore = true;  
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(new IOException(
						(documento.getDescrizione() != null ? documento.getDescrizione() : "") + ", " +
						(documento.getNomeFile() != null ? documento.getNomeFile() : "") + " " +
						e.getMessage()), 
						this, "popolaDocumentiFromComunicazione");
				errore = true;
//			} catch (ApsException e) {
//				ApsSystemUtils.logThrowable(e, this, "popolaDocumentiFromBozza");
			}
		}
		
		if(errore) {
			// NB: il chiamante mostra l'eccezione solo se il messaggio 
			//	   contiene "Errors.invioBuste.xmlBustaNotFound"
			//     Si simula quindi un messaggio di busta non trovata...
			throw new IOException("Errors.invioBuste.xmlBustaNotFound");
		}
	}

		
}
