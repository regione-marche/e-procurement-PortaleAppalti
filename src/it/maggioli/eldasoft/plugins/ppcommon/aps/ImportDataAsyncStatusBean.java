package it.maggioli.eldasoft.plugins.ppcommon.aps;

import it.eldasoft.utils.excel.ExcelResultBean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class ImportDataAsyncStatusBean extends ExcelResultBean 
	implements Serializable, HttpSessionBindingListener {
	
	/**
	 * Uid
	 */
	private static final long serialVersionUID = -8746438625511053007L;

	// riferimenti ai file generati nella work 
	private List<File> fileGeneratiNellaWork;

	/** ... */
	private int rowCount;
	
	/** ... */
	private int rowIndex;
	
	/** elaborazione in corso */	
	private boolean running;
	
	/** elaborazione terminata senza errori (100%) */
	private boolean completed;

	/** elaborazione terminata con errori */
	private boolean interrupted;

	private List<String> erroriRiga;
	
	
	public ImportDataAsyncStatusBean() {
		super();
		this.fileGeneratiNellaWork = new ArrayList<File>();
		this.erroriRiga = new ArrayList<String>();
		this.rowCount = 0;
		this.rowIndex = 0;
		this.running = false;
		this.completed = false;
		this.interrupted = false;
		this.setInputRows(0);
		this.setSuccessRows(0);
		//this.setSkippedRows(...);		
		//this.getErrorRows(...);		
	}
	
	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * @return the rowIndex
	 */
	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * @param rowIndex the rowIndex to set
	 */
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * @return the completed
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	/**
	 * @return the interrupted
	 */
	public boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * @param interrupted the interrupted to set
	 */
	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}	
	
	/**
	 * @return the erroriRiga
	 */
	public List<String> getErroriRiga() {
		return erroriRiga;
	}
	
	/**
	 * @param erroriRiga the erroriRiga to set
	 */
	public void setErroriRiga(List<String> erroriRiga) {
		this.erroriRiga = erroriRiga;
	}	
	
	
	/**
	 * Aggiunge il riferimento al file tenmporaneo, per la cancellazione al 
	 * termine della sessione.
	 */
	public void addFileGeneratiNellaWork(File file) {
		for(int i = 0; i < this.fileGeneratiNellaWork.size(); i++) {
			String fn = this.fileGeneratiNellaWork.get(i).getAbsolutePath();			
			if(fn.equals(file.getAbsolutePath())) {
				return;
			}
		}
		this.fileGeneratiNellaWork.add(file);
	}
	
	/**
	 * valueBound
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * valueUnbound
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		//elimina tutti i file temporanei utilizzati per l'import...
		Iterator<File> iter = this.fileGeneratiNellaWork.listIterator();
		while (iter.hasNext()) {
				File file = iter.next();
//System.out.println("removing temp file " + file);				
			if (file.exists()) {
				if( !file.delete() ) {
					//errore ?!?
				}					
			}
		}
		
		this.fileGeneratiNellaWork = null;
		this.fileGeneratiNellaWork = new ArrayList<File>();
	}

//	/**
//	 * @return the fileGeneratiNellaWork
//	 */
//	public List<File> getFileGeneratiNellaWork() {
//		return fileGeneratiNellaWork;
//	}
//
//	/**
//	 * @param fileGeneratiNellaWork the fileGeneratiNellaWork to set
//	 */
//	public void setFileGeneratiNellaWork(List<File> fileGeneratiNellaWork) {
//		this.fileGeneratiNellaWork = fileGeneratiNellaWork;
//	}

}
