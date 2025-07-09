package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta gli allegati (Ulteriori e richiesti)<br/>
 * Contiene alcuni metodi comuni utilizzati comunemente per gli oggetti di questo tipo.
 */
public class Attachment implements Serializable {
    /**
	 * UID
     */
	private static final long serialVersionUID = 7168332919790967007L;
	
	protected Long    id;								// solo richiesto
    protected File    file;								// richiesto e ulteriore
    protected File    fileCifrati;						// richiesto e ulteriore
    protected String  contentType;						// richiesto e ulteriore
    protected String  fileName;							// richiesto e ulteriore
    protected Integer size;								// richiesto e ulteriore
    protected String  sha1;								// richiesto e ulteriore
    protected String  uuid;								// richiesto e ulteriore
    protected Integer stato;							// richiesto e ulteriore
    protected Boolean isVisible;    					// richiesto e ulteriore
    protected String  desc;								// solo ulteriore
    protected String  nascosto;							// solo ulteriore
    /**
     * Informazioni sulle firme dell'allegato
     */
    protected DocumentiAllegatiFirmaBean firmaBean;		// richiesto e ulteriore
    

    /**
     * Ritorna il valore l'indice della prima occorrenza trovata che passa la condizione indicata.<br/>
     * Viene fatto un == o in caso di stringa StringUtils.equals.<br/>
     * Ex: indexOf(new ArrayList(), Attachment::getUuid, "123e4567-e89b-12d3-a456-426614174000") //Ricerca per Uuid<br/>
     * Ex: indexOf(new ArrayList(), Attachment::getFileName, "documento.pdf") //Ricerca per filename<br/>
     *
     * @param attachments lista di allegati da controllare
     * @param fun MethodReference di un getter nella classe Attachment
     * @param ob L'oggetto da cercare nella lista
     * @return L'indice della prima occorrenza o -1 se non viene trovato
     * @param <T> il tipo di ob e anche il tipo di ritorna della funzione.
     *
     */
    public static <T> int indexOf(List<Attachment> attachments, Function<Attachment, T> fun, T ob) {
        return indexOf(
                attachments
                , attachment -> {   //FunctionalInterface di tipo Predicate (serve per testare una condizione)
                    //Questo e' il controllo che verra' effettuato ad ogni iterazione del loop per controllare se ha trovato l'oggetto
                    if (ob instanceof String)   //Con le stringhe non posso utilizzare il ==, o utilizzo .equals, o le StringUtils
                        return StringUtils.equals((String) fun.apply(attachment), (String) ob) ;
                    else
                        return fun.apply(attachment) == ob;
                }
        );
    }

    /**
     *
     * @param attachments
     * @return Ritorna la somma della dimensione di tutti gli allegati della lista
     */
    public static int sumSize(List<Attachment> attachments) {
        return CollectionUtils.isEmpty(attachments)
                ? 0
                : attachments
                       .parallelStream()
                            .filter(Objects::nonNull)   	//Mi faccio ritornare tutti gli allegati non nulli
                            .map(Attachment::getSize)   	//Converto la stream di allegati in stream di dimensioni
                       .reduce(0, Integer::sum);    		//Sommo tutte le dimensioni contenute nella stream
    }

    /**
     * crea il riepilogo degli allegati...
     */
    public static String toPdfRiepilogo(List<Attachment> attachments) {
        return CollectionUtils.isEmpty(attachments)
                ? ""
                : attachments
                       .stream()
                            .filter(Objects::nonNull)   		//Rimuovo dalla stream gli attachment nulli
                            .map(Attachment::toPdfRiepilogo)    //Converto gli attachmenti in linee di riepilogo
                       .collect(Collectors.joining("\n"));  	//Concateno le varie linee
    }

    private static String toPdfRiepilogo(Attachment attachment) {
        return String.format("%s *%s", attachment.getSha1(), attachment.getFileName());
    }

    /**
     *
     * @param attachments lista di allegati dove verrà cercata la condizione
     * @param condition La condizione che espleterà se è stato trovato l'elemento
     * @return  L'indice dell'elemento rispettivamente alla lista, se non viene trovato viene ritornato -1
     */
    private static int indexOf(List<Attachment> attachments, Predicate<Attachment> condition) {
        int index = -1;

        if (CollectionUtils.isNotEmpty(attachments))
            for (int i = 0; i < attachments.size(); i++)
                if (attachments.get(i) != null && condition.test(attachments.get(i))) {
                    index = i;
                    break;
                }

        return index;
    }

    public void deleteAndNullifyFiles() {
        if (file != null && file.exists()) file.delete();
        if (fileCifrati != null && fileCifrati.exists()) fileCifrati.delete();
        
        file = null;
        fileCifrati = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFileCifrati() {
        return fileCifrati;
    }

    public void setFileCifrati(File fileCifrati) {
        this.fileCifrati = fileCifrati;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getStato() {
        return stato;
    }

    public void setStato(Integer stato) {
        this.stato = stato;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    public DocumentiAllegatiFirmaBean getFirmaBean() {
        return firmaBean;
    }

    public void setFirmaBean(DocumentiAllegatiFirmaBean firmaBean) {
        this.firmaBean = firmaBean;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public String getNascosto() {
        return nascosto;
    }

    public void setNascosto(String nascosto) {
        this.nascosto = nascosto;
    }

    /**
     * Invece di utilizzare un costruttore con tutti i parametri o dei semplici setter,
     * ho optato per un PatternBuilder, così facendo volendo sarebbe anche semplice aggiungere un eventuale validazione
     * degli allegati, basta richiamare un validate nel metodo build (È solo un esempio)
     */
    public static final class AttachmentBuilder {
        private final Attachment attachment;

        private AttachmentBuilder() { attachment = new Attachment(); }

        public static AttachmentBuilder init() {return new AttachmentBuilder();}

        public AttachmentBuilder withId(Long id) {
            attachment.setId(id);
            return this;
        }
        public AttachmentBuilder withFile(File file) {
            attachment.setFile(file);
            return this;
        }
        public AttachmentBuilder withFileCifrati(File fileCifrati) {
            attachment.setFileCifrati(fileCifrati);
            return this;
        }
        public AttachmentBuilder withContentType(String contentType) {
            attachment.setContentType(contentType);
            return this;
        }
        public AttachmentBuilder withFileName(String fileName) {
            attachment.setFileName(fileName);
            return this;
        }
        public AttachmentBuilder withSize(Integer size) {
            attachment.setSize(size);
            return this;
        }
        public AttachmentBuilder withSha1(String sha1) {
            attachment.setSha1(sha1);
            return this;
        }
        public AttachmentBuilder withUuid(String uuid) {
            attachment.setUuid(uuid);
            return this;
        }
        public AttachmentBuilder withStato(Integer stato) {
            attachment.setStato(stato);
            return this;
        }
        public AttachmentBuilder withFirmaBean(DocumentiAllegatiFirmaBean firmaBean) {
            attachment.setFirmaBean(firmaBean);
            return this;
        }
        public AttachmentBuilder withDesc(String desc) {
            attachment.setDesc(desc);
            return this;
        }
        public AttachmentBuilder withIsVisible(Boolean isVisible) {
            attachment.setIsVisible(isVisible);
            return this;
        }
        public AttachmentBuilder withNascondi(boolean nascondi) {
            if (nascondi)
                attachment.setNascosto(attachment.desc);
            return this;
        }
        public Attachment build() {return attachment;}

    }
}
