package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans;

import it.eldasoft.sil.portgare.datatypes.DocumentoType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlException;

public class DettaglioRichiestaBean implements Serializable, HttpSessionBindingListener {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3428355981701699325L;

	private static final boolean MOSTRA_DESCRIZIONE_FILE_ALLEGATI = false;
	
	/** proprietà visibili del bean */
	private String id;
	private Date dataPresentazione;
	private String numeroProtocollo;
	private Date dataProtocollo;
	private Date dataAcquisizione;
	private String tipoInvio;
	private String stato;
	private it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType[] documentiAllegati;

	/** riferimenti ai file generati nella work durante la gestione prodotti in carrello */
	private List<File> fileGeneratiNellaWork;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType[] getDocumentiAllegati() {
		return documentiAllegati;
	}

	public void setDocumentiAllegati(
			it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType[] documentiAllegati) {
		this.documentiAllegati = documentiAllegati;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public Date getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(Date dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public Date getDataAcquisizione() {
		return dataAcquisizione;
	}

	public String getTipoInvio() {
		return tipoInvio;
	}

	public void setTipoInvio(String tipoInvio) {
		this.tipoInvio = tipoInvio;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}
	
	public void setDataAcquisizione(Date dataAcquisizione) {
		this.dataAcquisizione = dataAcquisizione;
	}
			
	public List<File> getFileGeneratiNellaWork() {
		return fileGeneratiNellaWork;
	}

	public void setFileGeneratiNellaWork(List<File> fileGeneratiNellaWork) {
		this.fileGeneratiNellaWork = fileGeneratiNellaWork;
	}

	
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		// ...
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0 the binding event
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {

		// rimuovi i file dell'area work
		String dirPath = null;
		
		if( this.fileGeneratiNellaWork != null )
		{
			Iterator<File> iter = this.getFileGeneratiNellaWork().listIterator();
			while (iter.hasNext()) {
				File file = iter.next();			
				if (file != null && file.exists()) {
					file.delete();
					if(dirPath == null) {
						dirPath = file.getAbsolutePath()
							.substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
					}
				}
			}
		}
		
		this.fileGeneratiNellaWork = null;		
		this.documentiAllegati = null;
		
		// elimina la cartella temporanea...
		if(dirPath != null) {
			try {
				FileUtils.deleteDirectory( new File(dirPath) );
			} catch (IOException e) {}
		}
	}


	/**
	 * aggiungi gli allegati XML all'area di work
	 *
	 * @param allegati lista degli allegati della comunicazione
	 * @param tipoComunicazione tipo di comunicazione (FS2, FS3, FS4) 
	 * 
	 * @exception XmlException, IOException
	 */
	public void addAllegati(DocumentoType[] allegati, String workPath, String applicativo, String id)
		throws XmlException, IOException {
		
		if(allegati == null) 
			return;
		if(allegati.length <= 0) 
			return;		
			
		// esamina gli allegati e salva su file una copia 
		// (fileGeneratiNellaWork)...
		this.fileGeneratiNellaWork = new ArrayList<File>();
		this.documentiAllegati = new it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType[allegati.length];
		
		try {
			String rootPath = workPath + File.separator;
			String allegatiPath = applicativo + "-" + id + "_xmldec";
			
			File dir = new File(rootPath + allegatiPath);
			boolean dirExists = dir.exists();
			
			for(int i = 0; i < allegati.length; i++) {

				// crea il link in documentiAllegati...
				it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType doc = 
					new it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType();					

				long length = -1;
				if (allegati[i].getUuid() == null) {
					// salva una copia del file in area di work...
					if (!dirExists) {
						dirExists = dir.mkdir();
					}
					String fn = dir.getPath() + File.separator + allegati[i].getNomeFile();
					FileOutputStream fos = new FileOutputStream(fn);
					// recupero direttamente dal tracciato interno all'xml
					fos.write(allegati[i].getFile());
					fos.close();

					// aggiungi a fileGeneratiNellaWork il riferimento al file...
					File f = new File(fn);
					this.fileGeneratiNellaWork.add(f);
					length = f.length();
				} else {
					// il file viene scaricato nel momento in cui si richiede il download
					doc.setUuid(allegati[i].getUuid());
					this.fileGeneratiNellaWork.add(null);
				}
				
				// mostra il nome file se la descrizione è vuota
				// ed aggiungi la dimensione del file alla descrizione
				String descrizione = "";
				if(MOSTRA_DESCRIZIONE_FILE_ALLEGATI) {
					descrizione = allegati[i].getDescrizione();
					if( (descrizione == null) ||
						(descrizione != null && descrizione.isEmpty()) ) {
						descrizione = allegati[i].getNomeFile();
					}
				} else {
					// mostra sempre il nome file 
					descrizione = allegati[i].getNomeFile();
				}
				if (length != -1) {
					descrizione = descrizione + " (" + toFileSize(length) + ")";
				}

				doc.setId((long)i);					
				doc.setNomefile(allegati[i].getNomeFile());					
				doc.setUrl(null);
				doc.setDescrizione(descrizione);
				
				this.documentiAllegati[i] = doc;
			}	
		} catch (Exception e) {
			throw new IOException("DettaglioaddAllegati.addAllegati IOException", e);			  
		}
	}

	
	/**
	 * formatta la dimensione di un file in stile Windows 
	 *
	 * @param size dimensione da formattare  
	 */	
	public static String toFileSize(long size) {
		DecimalFormat _1GB_fmt = new DecimalFormat("#,###,##0");
		double _1KB = 1024.0;					//1024				
		double _1MB = 1048576.0;				//1024*1024
		double _1GB = 1073741824.0;				//1024*1024*1024
		//double _1TB = 1099511627776.0;			//1024*1024*1024*1024		
		if(size < _1KB) {							
			return _1GB_fmt.format((double)size) + " bytes";
		} 
		else if(size < _1MB) {							
			return _1GB_fmt.format((double)(size)/_1KB) + " KB";
		}
		else if(size < _1GB) {							
			return _1GB_fmt.format((double)(size)/_1MB) + " MB";
		}
		else {							
			return _1GB_fmt.format((double)(size)/_1GB) + " GB";
		}
	}
	
}
