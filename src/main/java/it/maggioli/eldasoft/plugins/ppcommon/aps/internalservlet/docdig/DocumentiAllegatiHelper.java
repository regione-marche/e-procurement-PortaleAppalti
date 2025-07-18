package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario.AnomalieQuestionarioInfo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.RiepilogoBusteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.*;
import java.security.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cambiata gestione degli allegati richiesti ed ulteriori. <br/>
 * Metodi deprecati da versione 3.24.0-SNAPSHOT<br/>
 * <br/>
 * Nota: Volendo e' possibile rimpiazzare i metodi di addDocRichiesto e addDocUlteriore con dei metodi generici.<br/>
 * <br/>
 * Le mappe di liste contengono i vari field che prime erano presenti e che adesso sono stati convertiti in una singola
 * lista di oggetti (Attachment). Non appena anche le jsp verranno sistemate, e' consigliabile rimuovere le mappe e tutti
 * i metodi deprecati (e i loro utilizzi).
 */
@SuppressWarnings({ "unchecked", "deprecated" })
public class DocumentiAllegatiHelper implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6159419810289336922L;

	private static final Logger LOGGER = ApsSystemUtils.getLogger();

	public static final String QUESTIONARIO_DESCR				= "questionario";
	public static final String QUESTIONARIO_GARE_FILENAME 		= "questionario.json";
	public static final String QUESTIONARIO_ELENCHI_FILENAME 	= "questionario.json";
	public static final String QUESTIONARIO_PDFRIEPILOGO		= "PDF Riepilogo";		// solo per quello automatico

	//START - Constanti inserite nelle cache delle liste di allegati (temporanea)
	private static final String ATTACHMENT_ID 			= "ID";
	private static final String ATTACHMENT_FILE 		= "FILE";
	private static final String ATTACHMENT_CIFRATI 		= "CIFRATI";
	private static final String ATTACHMENT_CONTENT_TYPE = "CONTENT_TYPE";
	private static final String ATTACHMENT_FILE_NAME 	= "FILE_NAME";
	private static final String ATTACHMENT_SIZE 		= "SIZE";
	private static final String ATTACHMENT_SHA1 		= "SHA1";
	private static final String ATTACHMENT_UUID 		= "UUID";
	private static final String ATTACHMENT_STATO 		= "STATO";
	private static final String ATTACHMENT_IS_VISIBLE 	= "IS_VISIBLE";
	private static final String ATTACHMENT_DESC 		= "DESC";
	private static final String ATTACHMENT_NASCOSTI 	= "NASCOSTI";
	private static final String ATTACHMENT_FIRMA 		= "FIRMA";
	//END - Constanti inserite nelle cache delle liste di allegati (temporanea)


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
	
	// questionario associato alla busta presente in BO (tabella QFORM)
	protected QuestionarioType questionarioAssociato;
	private String questionarioFileName;					// memorizza il nomefile relativo all'eventuale questionario associato

	// Lista degli allegati richiesti
	private List<Attachment> requiredDocs;
	// Lista degli allegati facoltativi
	private List<Attachment> additionalDocs;
	
	// lista di tutti i file temporanei scaricati nella work del file system, da rimuovere a fine sessione
	private List<File> tempFiles;

	// Taccone utilizzato per mantenere i vari getter degli attachment quando erano liste distinte e non intaccare troppo le performance
	// Contiene in memoria tutto c'ho che prima veniva restituito da getFileRichiesto, getIdFileRichiesto....
	// Servono solamente per le jsp, perche' lato java sono stati rimossi tutti i riferimenti ai metodi deprecati
	@Deprecated
	private final Map<String, List> required_attachment_cache = new HashMap<>();
	@Deprecated
	private final Map<String, List> additional_attachment_cache = new HashMap<>();

	/**
	 * Non utilizzato ad oggi
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent event) {	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param event the binding listener
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (CollectionUtils.isNotEmpty(requiredDocs))
			requiredDocs.forEach(Attachment::deleteAndNullifyFiles);
		if (CollectionUtils.isNotEmpty(additionalDocs))
			additionalDocs.forEach(Attachment::deleteAndNullifyFiles);
		for(File f : this.tempFiles) { 
			if(f != null && f.exists()) f.delete();
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
			boolean creaDocUlteriori
	) {
		this.chiaveCifratura = chiaveCifratura;
		this.chiaveSessione = chiaveSessione;
		this.username = username;
		this.tempDir = getTempDir();
		this.tempFiles = new ArrayList<File>();
		this.idComunicazione = null;
		this.questionarioAssociato = null;
		this.questionarioFileName = null;
		if (creaDocRichiesti)
			clearDocRichiesti();
		if (creaDocUlteriori)
			clearDocUlteriori();
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

	@Deprecated
	public List<Long> getDocRichiestiId() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_ID, Attachment::getId);
	}
	@Deprecated
	public List<File> getDocRichiesti() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_FILE, Attachment::getFile);
	}
	@Deprecated
	public List<File> getDocRichiestiCifrati() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_CIFRATI, Attachment::getFileCifrati);
	}
	@Deprecated
	public List<String> getDocRichiestiContentType() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_CONTENT_TYPE, Attachment::getContentType);
	}
	@Deprecated
	public List<String> getDocRichiestiFileName() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_FILE_NAME, Attachment::getFileName);
	}
	@Deprecated
	public List<Integer> getDocRichiestiSize() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_SIZE, Attachment::getSize);
	}
	@Deprecated
	public List<String> getDocRichiestiSha1() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_SHA1, Attachment::getSha1);
	}
	@Deprecated
	public List<String> getDocRichiestiUuid() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_UUID, Attachment::getUuid);
	}
	@Deprecated
	public List<Integer> getDocRichiestiStato() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_STATO, Attachment::getStato);
	}
	@Deprecated
	public List<Boolean> getDocRichiestiVisibile() {
		return getCachedOrCompute(required_attachment_cache, requiredDocs, ATTACHMENT_IS_VISIBLE, Attachment::getIsVisible);
	}

//	public void setDocRichiestiVisibile(List<Boolean> docRichiestiVisibile) {
//		this.docRichiestiVisibile = docRichiestiVisibile;
//	}
	@Deprecated
	public List<String> getDocUlterioriDesc() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_DESC, Attachment::getDesc);
	}
	@Deprecated
	public List<File> getDocUlteriori() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_FILE, Attachment::getFile);
	}
	@Deprecated
	public List<File> getDocUlterioriCifrati() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_CIFRATI, Attachment::getFileCifrati);
	}
	@Deprecated
	public List<String> getDocUlterioriContentType() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_CONTENT_TYPE, Attachment::getContentType);
	}
	@Deprecated
	public List<String> getDocUlterioriFileName() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_FILE_NAME, Attachment::getFileName);
	}
	@Deprecated
	public List<Integer> getDocUlterioriSize() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_SIZE, Attachment::getSize);
	}
	@Deprecated
	public List<String> getDocUlterioriSha1() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_SHA1, Attachment::getSha1);
	}
	@Deprecated
	public List<String> getDocUlterioriUuid() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_UUID, Attachment::getUuid);
	}
	@Deprecated
	public List<Integer> getDocUlterioriStato() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_STATO, Attachment::getStato);
	}
	@Deprecated
	public Set<String> getDocUlterioriNascosti() {
		return additionalDocs.stream().map(Attachment::getNascosto).collect(Collectors.toSet());
	}
	@Deprecated
	public List<Boolean> getDocUlterioriVisibile() {
		return getCachedOrCompute(additional_attachment_cache, additionalDocs, ATTACHMENT_IS_VISIBLE, Attachment::getIsVisible);
	}

	@Deprecated
	public List<DocumentiAllegatiFirmaBean> getDocRichiestiFirmaBean() {
		return required_attachment_cache.computeIfAbsent(ATTACHMENT_FIRMA, key -> requiredDocs.stream().map(Attachment::getFirmaBean).collect(Collectors.toList()));
	}

	@Deprecated
	public List<DocumentiAllegatiFirmaBean> getDocUlterioriFirmaBean() {
		return additional_attachment_cache.computeIfAbsent(ATTACHMENT_FIRMA, key -> additionalDocs.stream().map(Attachment::getFirmaBean).collect(Collectors.toList()));
	}

	private <T> List getCachedOrCompute(Map<String, List> cache, List<Attachment> attachments, String cacheKey, Function<Attachment, T> mapFun) {
		return cache.computeIfAbsent(cacheKey, key -> attachments.stream().map(mapFun).collect(Collectors.toList()));
	}

//	public void setDocUlterioriVisibile(List<Boolean> docUlterioriVisibile) {
//		this.docUlterioriVisibile = docUlterioriVisibile;
//	}

	public QuestionarioType getQuestionarioAssociato() {
		return questionarioAssociato;
	}

	public void setQuestionarioAssociato(QuestionarioType questionarioAssociato) {
		this.questionarioAssociato = questionarioAssociato;
	}

	@Deprecated
	private void clearCache(Map<String, List> cache) {
		cache.put(ATTACHMENT_ID, new ArrayList<>());
		cache.put(ATTACHMENT_FILE, new ArrayList<>());
		cache.put(ATTACHMENT_CIFRATI, new ArrayList<>());
		cache.put(ATTACHMENT_CONTENT_TYPE, new ArrayList<>());
		cache.put(ATTACHMENT_FILE_NAME, new ArrayList<>());
		cache.put(ATTACHMENT_SIZE, new ArrayList<>());
		cache.put(ATTACHMENT_SHA1, new ArrayList<>());
		cache.put(ATTACHMENT_UUID, new ArrayList<>());
		cache.put(ATTACHMENT_STATO, new ArrayList<>());
		cache.put(ATTACHMENT_IS_VISIBLE, new ArrayList<>());
		cache.put(ATTACHMENT_DESC, new ArrayList<>());
		cache.put(ATTACHMENT_NASCOSTI, new ArrayList<>());
		cache.put(ATTACHMENT_FIRMA, new ArrayList<>());
	}

	private void clearDocRichiesti() {
		requiredDocs = new ArrayList<>();
		clearCache(required_attachment_cache);
	}
	
	private void clearDocUlteriori() {
		additionalDocs = new ArrayList<>();
		clearCache(additional_attachment_cache);
	}

	/**
	 * pulisce/resetta l'elenco dei documenti
	 */
	public void clear() {
		this.clearDocRichiesti();
		clearCache(required_attachment_cache);
		this.clearDocUlteriori();
		clearCache(additional_attachment_cache);
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
	 * 		1=modificato -1=eliminato, indica se il documento e' stato modificato/eliminato
	 *      e quindi va inviato al servizio
	 *
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
			int stato, 
			DocumentiAllegatiFirmaBean checkFirma
	) {
		// per poter aggiungere un documento richiesto 
		// e' prima necessario che l'utente rimuova quello precedentemente allegato
		requiredDocs.add(
				Attachment.AttachmentBuilder
					.init()
						.withId(id)
						.withFile(file)
						.withFileCifrati(fileCifrato)
						.withContentType( validateContentType(contentType, checkFirma, fileName) )
						.withFileName(fileName)
						.withSize(size)
						.withSha1(sha1)
						.withUuid(uuid)
						.withStato(stato)
						.withIsVisible(isDocVisible(fileName))
						.withFirmaBean(checkFirma)
					.build()
			);		
		addToCache(required_attachment_cache, id, null, file, fileCifrato, contentType, fileName, size, sha1, uuid, stato, checkFirma, false);
	}

	// Non appena possibile questo metodo deve essere dismesso
	@Deprecated
	private void addToCache(
			Map<String, List> cache, 
			Long id, 
			String descrizione,
			File file, 
			File fileCifrato, 
			String contentType,
			String fileName, 
			Integer size, 
			String sha1, 
			String uuid, 
			int stato,
			DocumentiAllegatiFirmaBean checkFirma,
			boolean nascosto) 
	{
		// id >= 0 indica un allegato e' un documento richiesto, altrimenti e' ulteriore
		boolean isRichiesto = (id != null && id.longValue() >= 0);
		
		boolean exists = (isRichiesto
						  ? cache.get(ATTACHMENT_ID).indexOf(id) >= 0
						  : cache.get(ATTACHMENT_UUID).indexOf(uuid) >= 0);
		if( !exists ) {
			// (QCompiler) aggiorna le mappe mantenendo l'ordinamento relativo a UUID
			int index = -1;
			if(!isRichiesto && StringUtils.isNotEmpty(questionarioFileName)) {
				// cerca la posizione dove inserire l'allegato in ordine di UUID...
				List<String> uuidList = cache.get(ATTACHMENT_UUID);
				for(int i = 0; i < uuidList.size(); i++) {
					if(uuid.compareTo(uuidList.get(i)) < 0) {
						index = i;
						break;
					}
				}
			}
			
			addIfPresent(cache, ATTACHMENT_ID, id, index);
			addIfPresent(cache, ATTACHMENT_FILE, file, index);
			addIfPresent(cache, ATTACHMENT_CIFRATI, fileCifrato, index);
			addIfPresent(cache, ATTACHMENT_CONTENT_TYPE, contentType, index);
			addIfPresent(cache, ATTACHMENT_FILE_NAME, fileName, index);
			addIfPresent(cache, ATTACHMENT_SIZE, size, index);
			addIfPresent(cache, ATTACHMENT_SHA1, sha1, index);
			addIfPresent(cache, ATTACHMENT_UUID, uuid, index);
			addIfPresent(cache, ATTACHMENT_STATO, stato, index);
			addIfPresent(cache, ATTACHMENT_FIRMA, checkFirma, index);
			addIfPresent(cache, ATTACHMENT_IS_VISIBLE, isDocVisible(fileName), index);
			if( !isRichiesto ) {
				addIfPresent(cache, ATTACHMENT_DESC, descrizione, index);
				addIfPresent(cache, ATTACHMENT_NASCOSTI, (nascosto ? descrizione : null), index);
			}
		}
	}
	@Deprecated
	// Non appena possibile questo metodo deve essere dismesso
	private void addIfPresent(Map<String, List> cache, String key, Object toAdd, int position) {
		cache.computeIfPresent(key, (kkey, list) -> {
			if(position >= 0) {
				list.add(position, toAdd);
			} else {
				list.add(toAdd);
			}
			return list;
		});
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
	 * 		opzionale, e' l'evento da aggiornare in caso di tracciature
	 * @throws GeneralSecurityException
	 */
	public void addDocRichiesto(
			Long id,
			File file,
			String contentType,
			String fileName,
			Event evento, 
			DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException
	{
		if (requiredDocs == null)
			return;

		File f = generateTempFile(file);

		String sha = this.getDigest(f, evento);

		File fc = null;
		if (this.chiaveSessione != null) {
			// nel caso di presenza di cifratura, al caricamento di un file
			// si procede con la sua cifratura preventiva
			fc = cifraDocumento(f, this.chiaveSessione, this.username);
		}

		// NB: non viene utilizzato in W_DOCDIG.TIPDOC (char[5])
		contentType = null;

		int size = (int) FileUploadUtilities.getFileSize(f);

		this.addDocRichiesto(
				id,
				f,
				fc,
				contentType,
				fileName,
				size,
				sha,
				FileUploadUtilities.generateFileName(),
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO,
				checkFirma
		);
	}

	/**
	 * Aggiungi tutte le info relative ad un documento richiesto con un unico metodo
	 *
	 * @param id
	 * 		id del documento
	 * @param file
	 * 		byte array relativo allo stream del documento
	 * @param contentType
	 * 		tipo di documnto
	 * @param fileName
	 * 		nome file del documento
	 * @param evento
	 * 		opzionale, e' l'evento da aggiornare in caso di tracciature
	 * @throws IOException
	 *
	 * @throws GeneralSecurityException
	 */
	public void addDocRichiesto(
			Long id,
			byte[] file,
			String contentType,
			String fileName,
			Event evento,
			DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException, IOException
	{
		if (requiredDocs == null)
			return;

		// crea un file temporaneo per scaricare lo stream binario del file
		// il temporanero verra' eliminato al termine dell'helper o della sessione
		File f = this.generateFileFromBytes(file);

		this.addDocRichiesto(id, f,	contentType, fileName, evento,checkFirma);
	}

	public void addDocRichiesto(
			Long id,
			File file,
			String contentType,
			String fileName, 
			DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException
	{
		this.addDocRichiesto(id, file, contentType, fileName, null,checkFirma);
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
	public void addDocRichiesto(DocumentoType documento, byte[] externalFile, DocumentiAllegatiFirmaBean checkFirma)
		throws GeneralSecurityException, IOException
	{
		if (requiredDocs == null)
			return;

		File f = null;
		File fc = null;
		byte[] contenutoXML = null;
		byte[] contenutoInChiaro = null;
		String sha = null;
		if (documento.getFile() != null)
			contenutoXML = documento.getFile();
		else
			contenutoXML = externalFile;

		if (contenutoXML != null) {
			try {
				if (this.chiaveSessione != null) {
					fc = this.generateFileFromBytes(contenutoXML);
					contenutoInChiaro = decifraDocumento(contenutoXML, this.chiaveSessione, this.username);
				} else {
					f = this.generateFileFromBytes(contenutoXML);
					contenutoInChiaro = contenutoXML;
				}
				sha = (contenutoInChiaro != null ? DigestUtils.shaHex(contenutoInChiaro) : null);
			} catch (Exception | OutOfMemoryError e) {
				throw new IOException(e.getMessage(), e);
			}
		}

		// e' un documento che arriva da un XML, quindi da una comunicazione,
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
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO,	//PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO
				checkFirma
		); 
	}

	/**
	 * Rimuovi le info relative ad un documento richiesto con un unico metodo
	 *
	 * @param id
	 *   	id (posizione) del documento da elmiminare
	 */
	public void removeDocRichiesto(int id) {
		removeAttachment(requiredDocs, id);
	}

	/**
	 * Aggiungi tutte le info relative ad un documento ulteriore con un unico metodo
	 *
	 * @param index
	 * 		posizione del domcumento
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
	 * 		1=modificato -1=eliminato indica se il documento e' stato modificato
	 *      e quindi va inviato al servizio
	 * @param nascondi
	 *
	 *
	 * @throws GeneralSecurityException
	 */
	private void addDocUlteriore(
			int index,
			String descrizione,
			File file,
			File fileCifrato,
			String contentType,
			String fileName,
			Integer size,
			String sha1,
			String uuid,
			int stato,
			boolean nascondi,
			DocumentiAllegatiFirmaBean firmaCheck
	) throws GeneralSecurityException {
		LOGGER.debug("Aggiorno il doc con index: {}, firmaCheck: {}", index, firmaCheck);

		Attachment.AttachmentBuilder builder =
				Attachment.AttachmentBuilder
					.init()
						.withFile(file)
						.withFileCifrati(fileCifrato)
						.withContentType( validateContentType(contentType, firmaCheck, fileName) )
						.withFileName(fileName)
						.withSize(size)
						.withSha1(sha1)
						.withUuid(uuid)
						.withStato(stato)
						.withIsVisible(isDocVisible(fileName))
						.withDesc(descrizione)
						.withFirmaBean(firmaCheck);

		if(index < 0 || index >= additionalDocs.size())	
			// aggiungi un documento
			additionalDocs.add(
					builder
						.withNascondi(nascondi)
					.build()
			);
		else {	
			// modifica un documento
			// prima di aggiornare il documento esistente si eliminano dal file system i vecchi files  
//			additionalDocs.get(index).deleteAndNullifyFiles();
			additionalDocs.set(
				index
				, builder.build()
			);
		}

		// se viene aggiunto il documento del json del questionario
		// memorizza il nome file associato al questionario
		if (QUESTIONARIO_DESCR.equalsIgnoreCase(descrizione))
			this.questionarioFileName = fileName;
		
		addToCache(additional_attachment_cache, -1L, descrizione, file, fileCifrato, contentType, fileName, size, sha1, uuid, stato, firmaCheck, nascondi);
	}

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
			boolean nascondi,
			DocumentiAllegatiFirmaBean checkFirma
	) throws GeneralSecurityException {
		addDocUlteriore(-1, descrizione, file, fileCifrato, contentType, fileName, size, sha1, uuid, stato, nascondi, checkFirma);
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
	 * @param uuid
	 * 		uuid da utilizzare per il documento
	 * @param evento
	 * 		opzionale, e' l'evento da aggiornare in caso di tracciature
	 *
	 * @throws GeneralSecurityException
	 */
	private void addDocUlteriore(
			int index,
			String descrizione,
			File file,
			String contentType,
			String fileName,
			String uuid,
			Event evento,
			DocumentiAllegatiFirmaBean checkFirma
	) throws GeneralSecurityException {
		if (additionalDocs == null)
			return;

		if (StringUtils.isEmpty(uuid))
			uuid = FileUploadUtilities.generateFileName();

		File f = generateTempFile(file);

		File fc = null;
		if (this.chiaveSessione != null) {
			// nel caso di presenza di cifratura, al caricamento di un file
			// si procede con la sua cifratura preventiva
			fc = cifraDocumento(f, this.chiaveSessione, this.username);
		}

		int size = FileUploadUtilities.getFileSize(f);
		String sha1 = getDigest(f, evento);

		// aggiungi un documento o modifica un documento esistente
		addDocUlteriore(
			index < 0 || index >= additionalDocs.size() ? -1 : index	// aggiungi un nuovo documento
			, descrizione
			, f
			, fc
			, null // NB: non viene utilizzato in W_DOCDIG.TIPDOC (char[5]) (content type)
			, fileName
			, size	//size
			, sha1 	//SHA1
			, uuid
			, PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_MODIFICATO
			, false
			, checkFirma
		);
	}

	public void addDocUlteriore(
			String descrizione,
			byte[] data,
			String contentType,
			String fileName,
			String uuid,
			Event evento, 
			DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException, IOException 
	{
		if (additionalDocs == null)
			return;

		// crea un file temporaneo per scaricare lo stream binario del file
		// il temporanero verra' eliminato al termine dell'helper o della sessione
		File file = generateFileFromBytes(data);
		addDocUlteriore(-1, descrizione, file, contentType, fileName, uuid, evento, checkFirma);
	}

	public void addDocUlteriore(
			String descrizione,
			File file,
			String contentType,
			String fileName, DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException	{
		addDocUlteriore(descrizione, file, contentType, fileName, null, checkFirma);
	}

	public void addDocUlteriore(
			String descrizione,
			File file,
			String contentType,
			String fileName,
			Event evento, DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException {
		addDocUlteriore(-1, descrizione, file, contentType, fileName, null, evento, checkFirma);
	}

	/**
	 * Aggiungi tutte le info relative ad un documento ulteriore da un DocumentoType
	 *
	 * @param documento
	 * 		documento recuperato da una busta xml, contentente le info di un
	 * 		allegato
	 * @param externalFile stream con il contenuto del file se presente fuori dall'xml
 	 * @param nascondi
 	 *
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void addDocUlteriore(DocumentoType documento, byte[] externalFile, boolean nascondi, DocumentiAllegatiFirmaBean checkFirma)
		throws IOException, GeneralSecurityException
	{
		if(additionalDocs == null)
			return;

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
			} catch (Exception | OutOfMemoryError e) {
				throw new IOException(e.getMessage(), e);
			}
		}

		int size = (int) Math.ceil(documento.getDimensione() / 1024.0);

		addDocUlteriore(
				documento.getDescrizione(),
				f,
				fc,
				documento.getContentType(),
				documento.getNomeFile(),
				size,
				sha,
				documento.getUuid(),
				PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO, // STATO_DOCUMENTO_BUSTA_MODIFICATO, //documento.getModificato(),
				nascondi, 
				checkFirma
		);
	}

	/**
	 * Rimuovi le info relative ad un documento richiesto con un unico metodo
	 *
	 * @param id
	 * 		id (posizione) del documento da eliminare
	 */
	public void removeDocUlteriore(int id) {
		removeAttachment(additionalDocs, id);
	}

	public void removeDocUlteriore(Attachment attachment) {
		int index = additionalDocs.indexOf(attachment);
		removeAttachment(additionalDocs, index);
	}

	/**
	 * rimuovi l'allegato ed aggiorna la cache dei documenti 
	 */
	public void removeAttachment(List<Attachment> attachments, int index) {
		if(attachments == null)
			return;

		String uuid = attachments.get(index).getUuid();
		attachments.get(index).deleteAndNullifyFiles();
		attachments.remove(index);
		if(attachments == requiredDocs) {
			//removeFromDeprecatedCache(required_attachment_cache, index);
			removeFromDeprecatedCache(required_attachment_cache, uuid);
		} else if(attachments == additionalDocs) {
			//removeFromDeprecatedCache(additional_attachment_cache, index);
			removeFromDeprecatedCache(additional_attachment_cache, uuid);
		}
	}
	
	@Deprecated
	//private void removeFromDeprecatedCache(Map<String, List> cache, int index) {
	private void removeFromDeprecatedCache(Map<String, List> cache, String uuid) {
		int index = cache.get(ATTACHMENT_UUID).indexOf(uuid);
		if(index >= 0) 
			removeFromDeprecatedCache(cache, index);
	}
	
	@Deprecated
	private void removeFromDeprecatedCache(Map<String, List> cache, int index) {
		removeFromDeprecatedCache(cache, ATTACHMENT_ID, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_FILE, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_CIFRATI, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_CONTENT_TYPE, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_FILE_NAME, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_SIZE, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_SHA1, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_UUID, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_STATO, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_IS_VISIBLE, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_DESC, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_NASCOSTI, index);
		removeFromDeprecatedCache(cache, ATTACHMENT_FIRMA, index);
	}
	
	private void removeFromDeprecatedCache(Map<String, List> cache, String key, int index) {
		cache.computeIfPresent(key, (kkey, value) -> {
			if (value.size() > index)
				value.remove(index);
			return value;
		});
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
	 * 		indica se il contenuto binario e' cifrato o meno
	 *
	 */
	public void addDocumenti(
			ListaDocumentiType listaDocumenti,
			boolean attachFileContents,
			boolean applicaCifratura) throws IOException
	{
		if(applicaCifratura)
			applicaCifratura = (this.chiaveCifratura != null ? true : false);

		// si aggiungono i documenti richiesti
		if(CollectionUtils.isNotEmpty(requiredDocs))
			addAttachmentToXML(listaDocumenti, requiredDocs, attachFileContents, applicaCifratura);

		// si aggiungono i documenti ulteriori
		if(CollectionUtils.isNotEmpty(additionalDocs))
			addAttachmentToXML(listaDocumenti, additionalDocs, attachFileContents, applicaCifratura);

	}

	private void addAttachmentToXML(ListaDocumentiType listaDoc, List<Attachment> attachments, boolean attachFileContents, boolean applicaCifratura) {
		attachments.forEach(attachment -> addAttachmentToXML(listaDoc, attachment, attachFileContents, applicaCifratura));
	}

	private void addAttachmentToXML(ListaDocumentiType listaDoc, Attachment attachment, boolean attachFileContents, boolean applicaCifratura) {
		try {
			DocumentoType document = listaDoc.addNewDocumento();
			document.setNomeFile(attachment.getFileName());
			document.setContentType(attachment.getContentType());
			if (attachFileContents) {
				document.setUuid(null);
				document.setFile(null);
				if (StringUtils.isEmpty(attachment.getUuid())) {
					if (!applicaCifratura)
						document.setFile(FileUtils.readFileToByteArray(attachment.getFile()));
					else
						document.setFile(FileUtils.readFileToByteArray(attachment.getFileCifrati()));
				} else {
					document.setUuid(attachment.getUuid());
				}
			}
			if (attachment.getFirmaBean() != null && attachment.getFirmaBean().getFirmacheck() != null) {
				document.setFirmacheck(attachment.getFirmaBean().getFirmacheck().toString());
				Date firmacheckts = attachment.getFirmaBean().getFirmacheckts();
				if (firmacheckts != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(firmacheckts);
					document.setFirmacheckts(calendar);
				}
			}
			if (attachment.getId() != null)
				document.setId(attachment.getId());
			else
				document.setDescrizione(attachment.getDesc());
		} catch (IOException io) {
			throw new RuntimeException(io);
		}

//				// NB: lo stato dei documenti non andrebbe registrato nel
//				// documento xml che andr� inviato al servizio.
//				// Questo perche' lo stato serve solo lato client per gestire
//				// quali sono i documenti da inviare o meno al servizio,
//				// percio' prima di inviare la comunicazione si resettano gli
//				// stati dei documenti.
//				// In caso di errore la classe mantiene lo stato dei documenti!!!
//				documento.setModificato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
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
			boolean attachFileContents) throws IOException {
		// NB: anche se si passa applicaCifratura=true comunque il metodo
		//     generale addDocumenti(...) verifica se e' effettivamente
		//     presente o meno la cifratura prima di gestire i documenti!!!
		addDocumenti(listaDocumenti, attachFileContents, true);
	}

	/**
	 * Converte i documenti allegati dell'helper in una lista di AllegatoComunicazioneType
	 *
	 * @param allegatiXml
	 * 		lista di AllegatoComunicazioneType nella quale copiare i documenti
	 * @throws Exception
	 */
	public void documentiToAllegatiComunicazione(
			//DocumentiAllegatiHelper documenti,
			List<AllegatoComunicazioneType> allegatiXml) throws Exception
	{
		boolean cifratura = (this.chiaveCifratura != null);
		List<AllegatoComunicazioneType> docs = new ArrayList<AllegatoComunicazioneType>();  

		// aggiungi i documenti richiesti
	    if (requiredDocs != null)
	    	requiredDocs.stream()
	    			.map(a -> attachmentToAllegatoComType(a, cifratura))
	    			.filter(a -> a != null)
	    			.forEach(docs::add);
	    
		// aggiungi i documenti ulteriori
	    if (additionalDocs != null)
	    	additionalDocs.stream()
				.map(a -> attachmentToAllegatoComType(a, cifratura))
				.filter(a -> a != null)
				.forEach(docs::add);
	    
	    // verifica se ci sono stati errori nella conversione dei documenti
	    int n = ((requiredDocs != null ? requiredDocs.size() : 0) + (additionalDocs != null ? additionalDocs.size() : 0));
	    if(docs.size() < n) {
	    	String errMsg = "DocumentiAllegatiHelper.documentiToAllegatiComunicazione() errore in fase di conversione dei documenti allegati negli allegati per la comunicazione ("
						    + "username: " + username 
						    + ", idcom: " + (idComunicazione != null ? idComunicazione.toString() : "???")
						    + ")";
	    	LOGGER.error(errMsg);
	    	throw new Exception(errMsg);
	    }
	    
	    docs.stream().forEach(allegatiXml::add);
	}

	/**
	 * converti un Attachment in un AllegatoComunicazioneType
	 */
	private AllegatoComunicazioneType attachmentToAllegatoComType(Attachment attachment, boolean cifratura) {
		AllegatoComunicazioneType toReturn = null;
		boolean error = false;
		
		// prepara il contenuto in byte dell'allegato da inviare al
		// servizio; in caso di cifratura si invia il contenuto cifrato
		byte[] contenuto = null;
		try {
			File f = attachment.getFile();
			if(attachment.getFileCifrati() != null)
				f = attachment.getFileCifrati();
			if (f != null)
				contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(f);
			else
				LOGGER.debug("attachmentToAllegatoComType: file nullo docUlteriori "
							 + "desc={}, filename={}, idCom={}"
							, attachment.getDesc(), attachment.getFileName(), this.idComunicazione);
		} catch(Exception ex) {
			error = true;
			contenuto = null;
			ApsSystemUtils.logThrowable(ex, this, "attachmentToAllegatoComType");
		}

		//************************************************************************************************************
		// (SEPR-173)
		if(cifratura && contenuto != null) {
			// in caso di cifratura, verifica se il file e' stato cifrato!!!
			try {
				String sha1 = attachment.getSha1();
				String shaCriptato = DigestUtils.shaHex(contenuto);
				if(shaCriptato.equals(sha1)) {
					// sha1 == shaHex(file criptato) ==> il file non e' cifrato!!!
//							fileNonCifrato = true;
					LOGGER.warn("attachmentToAllegatoComType: docUlteriori file non cifrato " +
										"desc=" + attachment.getDesc() + ", " +
										"id=" + attachment.getId() + ", " +
										"filename=" + attachment.getFileName() + ", " +
										"idCom=" + this.idComunicazione);
					File fc = cifraDocumento(attachment.getFileCifrati(), this.chiaveSessione, this.username);
					contenuto = org.apache.commons.io.FileUtils.readFileToByteArray(fc);
				}
			} catch(Exception e) {
				error = true;
				ApsSystemUtils.logThrowable(e, this, "attachmentToAllegatoComType", "errore durante la cifratura del file");
			}
		}

		// crea ed aggiungi il nuovo allegato per la comunicazione
		if( !error ) {
			toReturn = new AllegatoComunicazioneType();
			toReturn.setUuid(attachment.getUuid());
			toReturn.setModificato(attachment.getStato());
			toReturn.setNomeFile(attachment.getFileName());
			toReturn.setDescrizione(attachment.getDesc());
			toReturn.setId(attachment.getId());
			toReturn.setTipo(attachment.getContentType());
			toReturn.setFile(contenuto);
			if (attachment.getFirmaBean() != null) {
				DocumentiAllegatiFirmaBean firmabean = attachment.getFirmaBean();
//				logger.debug("firmacheck[{}]: {}",i,firmabean);
				if (firmabean != null) {
					toReturn.setFirmacheck(Boolean.TRUE.equals(firmabean.getFirmacheck()) ? "1" : "2");
					if (firmabean.getFirmacheckts() != null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(firmabean.getFirmacheckts());
						toReturn.setFirmacheckts(calendar);
					}
				}
			}
		}

		return toReturn;
	}

	/**
	 * cerca un UUID tra gli allegati della comunicazione
	 */
	private int findUuid(ComunicazioneType comunicazione, String uuid) {
		int i = -1;
		for(int j = 0; j < comunicazione.getAllegato().length; j++) {
			if(uuid.equals(comunicazione.getAllegato()[j].getUuid())) {
				i = j;
				break;
			}
		}
		return i;
	}

	/**
	 * Resetta gli stati dei documenti richiesti ed ulteriori a 0 (non modificato)
	 * Dopo l'invio al servizio i documenti vanno marcati come gestiti
	 * altrimenti in caso di errore, reinviando la comunicazione si rispediscono
	 * tutti i documenti marcati "modificati"
	 */
	public synchronized void resetStatiInvio(ComunicazioneType comunicazioneInviata) {
		if(comunicazioneInviata != null && comunicazioneInviata.getAllegato() != null) {
			resetAttachmentStatus(requiredDocs, comunicazioneInviata);
			resetAttachmentStatus(additionalDocs, comunicazioneInviata);
		}
	}

	private void resetAttachmentStatus(List<Attachment> attachments, ComunicazioneType comunicazioneInviata) {
		if (CollectionUtils.isNotEmpty(attachments)) {
			attachments
				.stream()
					// si puo' resettare il flag "stato" dell'helper altrimenti
					// il campo modificato viene memorizzato del documento xml
					// quando in realta' il documento e' gia' stato gestito
					.filter(attachment -> findUuid(comunicazioneInviata, attachment.getUuid()) != -1)
				.forEach(attachment -> attachment.setStato(PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO));
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
			// genera un file temporaneo dello stream binario...
			f = writeByteArrayToFile(file);
		} catch (OutOfMemoryError e) {
			throw new IOException(e.getMessage());
		} finally {
			// NB: quando viene aggiunto a docRichiesti o docUlteriori
			//	   viene eliminato automaticamente al termine dell'helper o della sessione
		}
		return f;
	}

	/**
	 * crea un nuovo file temporaneo da un byte array 
	 * 
	 * @throws IOException 
	 */
	private File writeByteArrayToFile(byte[] data) throws IOException {
		File f = new File(this.tempDir.getAbsolutePath() + 
						  java.io.File.separatorChar + 
						  FileUploadUtilities.generateFileName());
		org.apache.commons.io.FileUtils.writeByteArrayToFile(f, data);
		this.tempFiles.add(f);
		return f;
	}

	/**
	 * genera un nuovo file temporaneo e rinomina il file originale 
	 */
	private static synchronized File generateTempFile(File file) {
		LOGGER.debug("generateTempFile begin {}", file.getAbsolutePath());
		File f = new File(file.getParent() + java.io.File.separatorChar + FileUploadUtilities.generateFileName());
		if (!file.renameTo(f)) {
			LOGGER.error("Error while renaming the file {} into {}", file.getAbsolutePath(), f.getAbsolutePath());
			f = file;
		}
		LOGGER.debug("generateTempFile end {}", f.getAbsolutePath());
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
	 * @throws Exception 
	 */
	private File cifraDocumento(File allegato, byte[] chiaveSessione, String token)
		throws GeneralSecurityException 
	{
		File fileCifrato = allegato;
		// verifica se la cifratura e' attiva
		if (chiaveSessione != null && token != null) {
			FileOutputStream fos = null;
			try (FileInputStream fis = new FileInputStream(allegato.getPath())) {
				// genera lo stream assocato al file temporaneo del file cifrato
				Object[] oefs = openEncodedFileStream(allegato);
				fileCifrato = (File)oefs[0];
				fos = (FileOutputStream)oefs[1];
				tempFiles.add(fileCifrato);
				Cipher cipher = SymmetricEncryptionUtils.getEncoder(chiaveSessione, token);
				SymmetricEncryptionUtils.translate(cipher, fis, fos);
			} catch(Exception e) {
				throw new GeneralSecurityException(e.getMessage(), e);
			} finally {
				if(fos != null) {
					try {
						fos.flush();
						fos.close();
					} catch (Exception ex) {
						LOGGER.error("cifraDocumento", "cifraDocumento", ex);
					}
				}
			}
			
			// verifica se la dimensione del file cifrato e' compatibile con la dimensione del file in chiaro
			if(fileCifrato != null && fileCifrato != allegato) {
				int size = (int) FileUploadUtilities.getFileSize(allegato);
				int sizefc = (int) FileUploadUtilities.getFileSize(fileCifrato);
				if(Math.abs(size - sizefc) > 1) {	// |size-sizefc| > 1KB
					LOGGER.error("cifraDocumento",
							"La dimensione del file cifrato non e' compatibile con quella del file originario ("
							+ fileCifrato.getAbsolutePath() + " " + sizefc + ", " 
							+ allegato.getAbsolutePath() + " " + size +
							")");
//					throw new GeneralSecurityException(
//							"La dimensione del file cifrato non e' compatibile con quella del file originario ("
//							+ fileCifrato.getAbsolutePath() + " " + sizefc + ", " 
//							+ allegato.getAbsolutePath() + " " + size + 
//							")");
				}
			}				
		}
		return fileCifrato;
	}

	private static synchronized	Object[] openEncodedFileStream(File allegato) throws FileNotFoundException {
		// genera il file temporaneo e associalo allo stream come un'operazione atomica!!!
		LOGGER.debug("openEncodedFileStream begin {}", allegato.getAbsolutePath());
		Object[] out = new Object[2];  
		out[0] = new File(allegato.getParent(), FileUploadUtilities.generateFileName());
		out[1] = new FileOutputStream((File)out[0]);
		LOGGER.debug("openEncodedFileStream end {}", allegato.getAbsolutePath());
		return out;
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

		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException |
				 InvalidAlgorithmParameterException | UnsupportedEncodingException e) {
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
	 * Se il file non e' presente in area temporanea, viene richiesto al servizio
	 * e scaricato.
	 * Se viceversa il file e' gia' presente in area temporanea viene restituito
	 * il contenuto di tale file.
	 *
	 * @param listaAttachments
	 * 		lista dei files in chiaro
	 * @param idDocumento
	 * 		uuid del documento per il recupero del contenuto binario da
	 * 		richiedere al servizio
	 */
	private byte[] getAllegatoComunicazione(
			Attachment attachment,
			String idDocumento
	) {
		byte[] contenuto = null;
		try {
			// se e' la prima volta che si scarica il file, si recupera dal
			// servizio lo stream del documento e lo si salva in area
			// temporanea...
			// se il file e' cifrato, salva in area temporanea la versione
			// decifrata
			File file = attachment.getFile();
			File fileCifrato = attachment.getFileCifrati();
			boolean decifra = (chiaveSessione != null);

			// lo stream e' gia' presente in locale o va scaricato dal WS ?
			boolean downloadFromWS = false;
			ApsSystemUtils.getLogger().debug("chiaveSessione.isNull? {}",(chiaveSessione==null));
			if(chiaveSessione != null) {
				downloadFromWS = (file == null && fileCifrato == null);
			} else {
				downloadFromWS = (file == null);
			}

			ApsSystemUtils.getLogger().debug("devo scaricare il file richiesto? downloadFromWS: {}",downloadFromWS);
			if(downloadFromWS) {
				// recupera il contenuto dell'allegato dal WS
				contenuto = ComunicazioniUtilities.getAllegatoComunicazione(
						this.idComunicazione,
						idDocumento);
			} else {
				// se file != null (o fileCriptato != null) significa che il file e' gia'presente
				// nel file system, basta rileggerne il contenuto !!!
				ApsSystemUtils.getLogger().debug("file != null ?: {}",file != null);
				if(file != null) {
					ApsSystemUtils.getLogger().debug("contenuto da file NON cifrato.");
					contenuto = FileUtils.readFileToByteArray(file);
					decifra = false;
				} else {				
					ApsSystemUtils.getLogger().debug("fileCifrato != null ?: {}",fileCifrato != null);
					if(fileCifrato != null) {
						ApsSystemUtils.getLogger().debug("contenuto da file cifrato.");
						contenuto = FileUtils.readFileToByteArray(fileCifrato);
					}
				}
			}

			// se necessario, salva in locale il file cifrato...
			// se e' gia' presente il file in chiaro non serve recuperare 
			// il contenuto decifrato dal file cifrato
			if(chiaveSessione != null && contenuto != null && decifra) {
				ApsSystemUtils.getLogger().debug("Aggiorno il fileCifrato nella busta? {}",downloadFromWS);
				if(downloadFromWS) {
					fileCifrato = writeByteArrayToFile(contenuto);
					attachment.setFileCifrati(fileCifrato);
				}
				// decifra il contenuto del file...
				ApsSystemUtils.getLogger().debug("Decifro il file");
				contenuto = decifraDocumento(contenuto, chiaveSessione, username);
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
		String uuid = (id < requiredDocs.size() ? requiredDocs.get(id).getUuid() : null);
		return getAllegatoComunicazione(
				requiredDocs.get(id),
				uuid
		);
	}
	public byte[] getContenutoDocRichiesto(Attachment attachment) {
		String uuid = attachment.getUuid();
		return getAllegatoComunicazione(
				attachment,
				uuid
		);
	}
	/**
	 * Recupera lo stream binario del documento richiesto
	 */
	public byte[] getContenutoDoc(Attachment attachment) {
		String uuid = (attachment.getUuid());
		return getAllegatoComunicazione(
				attachment,
				uuid
		);
	}

	/**
	 * Recupera lo stream binario del documento ulteriore
	 */
	public byte[] getContenutoDocUlteriore(int id) {
		String uuid = (id < additionalDocs.size() ? additionalDocs.get(id).getUuid() : null);
		byte[] contenuto = this.getAllegatoComunicazione(
				additionalDocs.get(id),
				uuid);
		return contenuto;
	}
	public byte[] getContenutoDocUlteriore(Attachment attachment) {
		return getAllegatoComunicazione(
				attachment,
				attachment.getUuid()
		);
	}

	/**
	 * Esegui il download di un documento
	 *
	 * @param attachments
	 * 		lista dei file di appartenenza del documento
	 */
	private InputStream downloadDocumento(
			List<Attachment> attachments,
			Attachment attachment) throws ApsException, IOException, GeneralSecurityException
	{
		File f = attachment.getFile();

		if (f != null) {
			// non e' necessario rileggere il contenuto in chiaro...
			// ...si sta per creare lo stream sul file gia' esistente!!!
		} else {
			// se necessario, decifra e salva in locale il file in chiaro...
			byte[] contenuto = getContenutoDoc(attachment);
			if(contenuto != null) {
				f = writeByteArrayToFile(contenuto);
				attachment.setFile(f);
			}

			// informa esplicitamente il Garbage Collector 
			// che questa memoria non serve piu'!!!
			contenuto = null;
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
		throws ApsException, IOException, GeneralSecurityException {
		return downloadDocumento(requiredDocs, requiredDocs.get(id));
	}
	public InputStream downloadDocRichiesto(Attachment attachment)
			throws ApsException, IOException, GeneralSecurityException {
		return downloadDocumento(requiredDocs, attachment);
	}

	/**
	 * Esegui il download di un documento ulteriore
	 *
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws ApsException
	 */
	public InputStream downloadDocUlteriore(int id)
		throws ApsException, IOException, GeneralSecurityException {
		return downloadDocumento(additionalDocs, additionalDocs.get(id));
	}
	public InputStream downloadDocUlteriore(Attachment attachment)
			throws ApsException, IOException, GeneralSecurityException {
		return downloadDocumento(additionalDocs, attachment);
	}

	/**
	 * Esegue il download generido di un documento 
	 */
	public InputStream downloadDoc(List<Attachment> docs, int id)
			throws ApsException, IOException, GeneralSecurityException {
		return downloadDocumento(docs, docs.get(id));
	}

	/**
	 * converte l'indice di "additional_attachment_cache"  nell'indice in "additionalDocs"
	 * NB: utilizzato solo nella pagina di riepilogo dei QForm
	 */
	public int getIndexDocumento(int id, Map<String, List> cache, List<Attachment> attachments) {
		String uuid = (String)cache.get(ATTACHMENT_UUID).get(id);
		if(StringUtils.isNotEmpty(uuid)) {
			id = attachments.stream()
				.map(Attachment::getUuid)
				.collect(Collectors.toList())
				.indexOf(uuid);
		} else {
			id = -1;
		}
		return id;
	}

	public int getIndexDocRichiesto(int id) {
		return getIndexDocumento(id, required_attachment_cache, requiredDocs);
	}
	
	public int getIndexDocUlteriore(int id) {
		return getIndexDocumento(id, additional_attachment_cache, additionalDocs);
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
	public static String validateContentType(String contentType, DocumentiAllegatiFirmaBean checkFirma, String filename) {
		String fileType = contentType;

		// normalizza il content type in base al tipo di documento (es: pdf/a validato)
		if(checkFirma != null) {
			if (checkFirma.isPdfaCompliant()) {
				fileType = "pdf/a";
			} else if(StringUtils.isNotEmpty(filename) && filename.toUpperCase().endsWith(".PDF")) {
				fileType = "pdf";
			}
		}
		
		// tipi di content type riconosciuti...
		if (fileType != null) {
			if ("xml".equalsIgnoreCase(fileType) ||
			   "pdf".equalsIgnoreCase(fileType) ||
			   "pdf/a".equalsIgnoreCase(fileType) ||
			   "p7m".equalsIgnoreCase(fileType) ||
			   "xsd".equalsIgnoreCase(fileType) ||
			   "txt".equalsIgnoreCase(fileType)) {
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
	 * @throws IOException
	 */
	public void popolaDocumentiFromXml(
			ListaDocumentiType listaDocumenti,
			long idComunicazione)
		throws IOException
	{
		boolean errore = false;

		this.setIdComunicazione(idComunicazione);

		this.clearDocRichiesti();
		this.clearDocUlteriori();

		// nel caso di cifratura applicata, oltre ad estrarre i documenti
		// vanno anche posti in un contenitore di file criptati
		DocumentoType documento;
		for (int i = 0; i < listaDocumenti.sizeOfDocumentoArray(); i++) {
			documento = listaDocumenti.getDocumentoArray(i);
			try {
				byte[] contenuto = null;
				// si recupera il contenuto se esterno all'xml
//				if (documento.getUuid() != null) {
					// 13/03/2019
					// NB: non scaricare/decifrare su FileSystem
					//     tutti gli allegati in apertura della busta
					//     Posticipa la fase di scarica/decifra al momento
					//     della richiesta di download dell'allegato!
//					contenuto = ComunicazioniUtilities
//						.getAllegatoComunicazione(idComunicazione,
//												  documento.getUuid());
//				}
				DocumentiAllegatiFirmaBean daf = null;
				if (StringUtils.isNotBlank(documento.getFirmacheck())) {
					daf = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.valueOf(documento.getFirmacheck()));
					if (documento.getFirmacheckts()!=null) {
						daf.firmacheckts(documento.getFirmacheckts().getTime());
					}
				}
				if (documento.getId() != 0) {
					this.addDocRichiesto(documento, contenuto, daf);
				} else {
					this.addDocUlteriore(documento, contenuto, false, daf);
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

		if (errore) {
			// NB: il chiamante mostra l'eccezione solo se il messaggio
			//	   contiene "Errors.invioBuste.xmlBustaNotFound"
			//     Si simula quindi un messaggio di busta non trovata...
			throw new IOException("Errors.invioBuste.xmlBustaNotFound");
		}
	}

	/**
	 * Determina se un documento e' visibile o meno in base ad alcuni criteri
	 * - il tipo di estensione (.json, ... => documento non visibile)
	 * - ...
	 */
	private Boolean isDocVisible(String filename) {
		String ext = FilenameUtils.getExtension(filename);
		if ("JSON".equalsIgnoreCase(ext)) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	/**
	 * richiede un nuovo UUID
	 */
	public static String getUuid() {
		return FileUploadUtilities.generateFileName();
	}

	/**
	 * (QCompiler) indica se e' l'helper ha un questionario associato e
	 * va attivata la gestione "questioniario"
	 */
	public boolean isGestioneQuestionario() {
		boolean allegatoQuestionario = this.isQuestionarioAllegato();
		if (allegatoQuestionario) {
			// l'allegato della comunicazione esiste gia'
			return true;
		} else {
			// aggiungi l'allegato alla comunicazione
			return this.questionarioAssociato != null && this.idComunicazione == null;
		}
	}

	/**
	 * (QCompiler) indica se e' presente l'allegato del questionario
	 * tra i documenti dell'helper
	 */
	public boolean isQuestionarioAllegato() {
		return CollectionUtils.isNotEmpty(additionalDocs) && isQuestElenchiOrGare(additionalDocs);
	}
	private boolean isQuestElenchiOrGare(List<Attachment> attachments) {
		return attachments.stream()
				.anyMatch(attachment ->
						  QUESTIONARIO_ELENCHI_FILENAME.equalsIgnoreCase(attachment.getFileName())
						  || QUESTIONARIO_GARE_FILENAME.equalsIgnoreCase(attachment.getFileName())
				);
	}

	/**
	 * (QCompiler) aggiorna il contenuto del documento questionario
	 * 
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void updateQuestionario(
			String questionarioJson,
			RiepilogoBusteHelper bustaRiepilogativa)
		throws IOException, GeneralSecurityException 
	{
		if (CollectionUtils.isEmpty(additionalDocs)) 
			return;
		
		// sincronizza il caso in cui un "autosave" ed un'operazione dell'utente possano sovrapporsi  
		synchronized (this) {			
			boolean presente = false;
			for(int i = 0; i < additionalDocs.size(); i++) {
				if (QUESTIONARIO_GARE_FILENAME.equalsIgnoreCase(additionalDocs.get(i).getFileName()) ||
				    QUESTIONARIO_ELENCHI_FILENAME.equalsIgnoreCase(additionalDocs.get(i).getFileName())) 
				{
					// recupera i vecchi temporanei associati a "questionario.json" 
					// per eliminarli dopo aver aggiunto la nuova versione... 
					File oldFile = additionalDocs.get(i).getFile();
					File oldFileCifrato = additionalDocs.get(i).getFileCifrati();
					if(oldFile != null) this.tempFiles.add(oldFile);
					if(oldFileCifrato != null) this.tempFiles.add(oldFileCifrato);

					// crea un file temporaneo (UTF-8) per scaricare lo stream binario del "questionario.json"
					// (il temporanero verra' eliminato al termine dell'helper o della sessione)										
					File f = new File(this.tempDir.getAbsolutePath() + java.io.File.separatorChar + FileUploadUtilities.generateFileName());			
					PrintWriter w = new PrintWriter(f, "UTF-8");
					w.print(questionarioJson);
					w.flush();
					w.close();
					this.tempFiles.add(f);

					// aggiorna il contenuto dell'allegato del questionario...
					addDocUlteriore(
							i
							, additionalDocs.get(i).getDesc()
							, f
							, additionalDocs.get(i).getContentType()
							, additionalDocs.get(i).getFileName()
							, additionalDocs.get(i).getUuid()
							, null
							, null
					);

					// elimina i vecchi temporanei associati a "questionario.json"...
					if(oldFile != null && oldFile.exists()) oldFile.delete();
					if(oldFileCifrato != null && oldFileCifrato.exists()) oldFileCifrato.delete();
					
					presente = true;
					break;
				}
			}

			// NB:
			// se l'allegato del questionario non e' stato trovato
			// si segnala l'anomalia e si corregge l'errore
			// aggiungendo un nuovo allegato per il questionario
			boolean correggi = false;
			if ( !presente ) {
				// traccia il mancato aggiornamento del questionario
				ApsSystemUtils.getLogger().error("updateQuestionario", "Questionario non trovato tra i documenti allegati (" + this.questionarioFileName + ")");

				// correggi riaggiungendo il nuovo questionario
				if (StringUtils.isNotEmpty(this.questionarioFileName)) {
					String uuid = FileUploadUtilities.generateFileName();

					this.addDocUlteriore(
							QUESTIONARIO_DESCR,
							questionarioJson.getBytes(),
							"JSON",
							this.questionarioFileName,
							uuid,
							null,
							null);
					correggi = true;

					ApsSystemUtils.getLogger().warn("updateQuestionario - {}", "Nuovo questionario riallegato alla comunicazione (" + questionarioFileName + ")");
				}
			}
			
			// riaggiorna il json del questionario...	
			if (presente || correggi) {
				if (this.questionarioAssociato != null) {
					this.questionarioAssociato.setOggetto(questionarioJson);
				}
			}
		}
	}

	/**
	 * (QCompiler) aggiorna il contenuto del documento questionario 
	 */
	public void updateQuestionario(String questionario) throws IOException, GeneralSecurityException {
		this.updateQuestionario(questionario, null);
	}

	/**
	 * (QCompiler) restituisce il questionario relativo all'allegato "questionario.json" (gare, elenchi, ...)
	 *  
	 * @throws Exception
	 */
	private QCQuestionario getQuestionarioAllegato(String nomeFile, int tipoBusta) {
		QCQuestionario q = null;
		
		for (int i = 0; i < additionalDocs.size(); i++) {
			if (nomeFile.equalsIgnoreCase(additionalDocs.get(i).getFileName())) {
				// verifico se esiste gia' una copia dell'allegato su fs 
				// altrimenti la richiedo al servizio (che restiruisce sempre in UTF-8) 
				// in caso di cifratura ottengo lo stream viene ricalcolato in chiaro
				try (InputStream is = this.downloadDocUlteriore(i)) {
					if (is != null) {
						try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
							int next = is.read();
							while (next > -1) {
								bos.write(next);
								next = is.read();
							}
							bos.flush();
							
							String json = bos.toString("UTF-8");
							
							if (QUESTIONARIO_GARE_FILENAME.equals(nomeFile)) {
								q = new QCQuestionario(tipoBusta, json);
							} else if (QUESTIONARIO_ELENCHI_FILENAME.equals(nomeFile)) {
								q = new QCQuestionario(json);
							} 
						}
					}
				} catch (Exception e) {
					// NON DOVREBBE MAI SUCCEDERE
					ApsSystemUtils.getLogger().error("getQuestionarioAllegato", e);
				}
				break;
			}
		}
		return q;
	}

	/**
	 * (QCompiler) recupera "questionario.json" per le gare
	 */
	public QCQuestionario getQuestionarioGare(int tipoBusta) {
		return this.getQuestionarioAllegato(QUESTIONARIO_GARE_FILENAME, tipoBusta);
	}

	/**
	 * (QCompiler) recupera "questionario.json" per gli elenchi
	 */
	public QCQuestionario getQuestionarioElenchi() {
		return this.getQuestionarioAllegato(QUESTIONARIO_ELENCHI_FILENAME, -1);
	}

	/**
	 * (QCompiler) verifica per le gare se i documenti del questionario.json e dell'helper documenti sono sincronizzati
	 */
	public List<AnomalieQuestionarioInfo> validaQuestionarioGare(int tipoBusta) {
		List<AnomalieQuestionarioInfo> anomalie = null;
		QCQuestionario q = getQuestionarioAllegato(QUESTIONARIO_GARE_FILENAME, tipoBusta);
		if( !q.validateAllegati(this) )
			anomalie = q.getValidateInfo();
		return anomalie;
	}
	
	/**
	 * (QCompiler) verifica per gli elenchi se i documenti del questionario.json e dell'helper documenti sono sincronizzati
	 */	
	public List<AnomalieQuestionarioInfo> validaQuestionarioElenchi() {
		List<AnomalieQuestionarioInfo> anomalie = null;
		QCQuestionario q = getQuestionarioAllegato(QUESTIONARIO_ELENCHI_FILENAME, -1);
		if( !q.validateAllegati(this) )
			anomalie = q.getValidateInfo();
		return anomalie;
	}
	
	/**
	 * (QCompiler) sincronizza gli allegati tra sessione e db
	 * NB: evita duplicazioni in caso il servizio non riesca a fare persistenza sul db  
	 */
	public void questionarioFixAttachments() {
		// recupera tutti i documenti ulteriori che sono stati variati (modificati/eliminati)
		// e rimuovili dall'elenco dei documenti in sessione in modo da annullare 
		// le ultime modifiche ai documenti in sessione ignora l'allegato "questionario"
		if (CollectionUtils.isNotEmpty(requiredDocs)) {
			requiredDocs.removeIf(
					a -> !QUESTIONARIO_DESCR.equalsIgnoreCase(a.getDesc())
					 	 && a.getStato().intValue() > PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
		}
		
		if (CollectionUtils.isNotEmpty(additionalDocs)) {
			additionalDocs.removeIf(
					a -> !QUESTIONARIO_DESCR.equalsIgnoreCase(a.getDesc())
						 && a.getStato().intValue() > PortGareSystemConstants.STATO_DOCUMENTO_BUSTA_NESSUNO);
		}
	}

	/**
	 * (QCompiler) restituisce l'allegato del "pdf di riepilogo" per i questionari con riepilogo automatico   
	 */
	public Attachment getQuestionarioPdfRiepilogoAutomatico() {
		return additionalDocs
				.stream()
				.filter(Objects::nonNull)			// restiruisce solo gli oggetti NON nulli
				.filter(a -> QUESTIONARIO_PDFRIEPILOGO.equalsIgnoreCase(a.getDesc()) && (a.getUuid() != null && a.getUuid().startsWith("999999")))
				.findFirst()
				.orElse(null);
	}
	
	
	public void addAdditionalDoc(Attachment attachment) {
		additionalDocs.add(attachment);
		addToCache(
				additional_attachment_cache
				, attachment.getId()
				, attachment.getDesc()
				, attachment.getFile()
				, attachment.getFileCifrati()
				, attachment.getContentType()
				, attachment.getFileName()
				, attachment.getSize()
			    , attachment.getSha1()
			    , attachment.getUuid()
			    , attachment.getStato()
			    , attachment.getFirmaBean()
			    , StringUtils.isNotEmpty(attachment.getNascosto())
		);
	}

	/**
	 * Nuova gestione degli allegati richiesti.
	 *
	 * @return Lista di allegati richiesti
	 */
	public List<Attachment> getRequiredDocs() {
		return requiredDocs;
	}

	/**
	 * Nuova gestione degli allegati ulteriori.
	 *
	 * @return Lista di allegati ulteriori
	 */
	public List<Attachment> getAdditionalDocs() {
		return additionalDocs;
	}

	/**
	 * restituisce il totale degli allegati richiesti inseriti
	 * 
	 * @return totale dei documenti richiesti inseriti
	 */
	public int getRequiredDocsCount() {
		return (requiredDocs != null ? requiredDocs.size() : 0);
	}
	
	/**
	 * restituisce il totale degli allegati ulteriori inseriti
	 * 
	 * @return totale dei documenti ulteriori inseriti
	 */
	public int getAdditionalDocsCount() {
		return (additionalDocs != null ? additionalDocs.size() : 0);
	}

	/**
	 * Restituisce il totale di tutti i doucumenti inseriti 
	 * 
	 * @return totale dei documenti allegati 
	 */
	public int getDocsCount() {
		return (requiredDocs != null ? requiredDocs.size() : 0) + (additionalDocs != null ? additionalDocs.size() : 0);
	}
	/**
	 * Restituisce la somma delle dimensioni dei documenti allegati 
	 * 
	 * @return somma totale dei documenti allegati 
	 */
	public int getTotalSize() {
		return Attachment.sumSize(requiredDocs) + Attachment.sumSize(additionalDocs);
	}

	/**
	 * verifica se i documenti richiesti delle busta sono ancora definiti in BO
	 * Se BO e i documenti richiesti non corrispondono (cancellati o archiviati)
	 * vanno rimossi dall'helper dei documenti
	 */
	public boolean correggiDocumentiRichiestiConBO(List<DocumentazioneRichiestaType> documentiRichiestiDB) { 
		int correzioni = 0;
		for(int i = requiredDocs.size() - 1; i >= 0; i--) {
			Attachment a = requiredDocs.get(i);
			boolean existsInBO = documentiRichiestiDB.stream()
									.anyMatch(docBO -> a.getId().longValue() == docBO.getId());
			if( !existsInBO ) {
				LOGGER.warn("DocumentiAllegatiHelper: il documento richiesto {} e' stato rimosso in quanto non e' definito in BO", a.getFileName());
				removeDocRichiesto(i);
				correzioni++;
			}
		}
		return (correzioni == 0);
	}

	/**
	 * restituisce il temporary path della webapp 
	 */
	private File getTempDir() {
		File tmp = null;
		try {
			tmp = StrutsUtilities.getTempDir(ServletActionContext.getServletContext());
		} catch (Throwable t) {
			tmp = null;
		}
		if(tmp == null)
			tmp = StrutsUtilities.getTempDir(SpringAppContext.getServletContext());
		return tmp;
	}

}
