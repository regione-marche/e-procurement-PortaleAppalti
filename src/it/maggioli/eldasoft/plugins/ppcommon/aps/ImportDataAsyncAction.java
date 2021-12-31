package it.maggioli.eldasoft.plugins.ppcommon.aps;

import it.eldasoft.utils.excel.ExcelException;
import it.eldasoft.utils.utility.UtilityExcel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public abstract class ImportDataAsyncAction extends BaseAction  
	implements SessionAware {

	/**
	 * Uid
	 */
	private static final long serialVersionUID = 4537082821721793285L;
	
	private static final int IMPORT_ROWCOUNT_PER_CYCLE = 5;  	 // default = 5
	
	private Map<String, Object> session;

	private ImportDataAsyncStatusBean status;

	
	public Map<String, Object>getSession() {
		return this.session;		
	}
	
	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;		
	}

	/**
	 * @return the status
	 */
	public ImportDataAsyncStatusBean getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ImportDataAsyncStatusBean status) {
		this.status = status;
	}

	/**
	 * @return the inputStream
	 */
	public abstract InputStream getInputStream();

	/**
	 * @param inputStream the inputStream to set
	 */
	public abstract void setInputStream(InputStream inputStream);
	

	/**
	 * Gestisce tutta l'operazione di import asincrono di un file di dati.
	 * Suddivide l'operazione di importazione in più parti, elaborando un 
	 * numero limitato di righe del file di dati ad ogni iterazione.    
	 * Durante l'elabotrazione al client viene restuita una risposta in 
	 * formato JSON contenente le informazioni sullo stato di avanzamento 
	 * dell'import (rowIndex, rowCount, isRunning, isCompleted, ...)
	 *
	 * @param sessionResultKey chiave in sessione per la memorizzazione dello stato
	 * @param rowPerCycle numero di righe elaborate ad ogni esecuzione della action
	 * @param firstDataRow indice della prima riga di dati utile (0,1,2,3,... etc)
	 * @param allegatoFileName nome del file da elaborare
	 * @param nomeFoglioExcel identificatifo del foglio nel file Excel
	 * @param versioneModelloExcel versione del file Excel 
	 * 
	 * @return SUCCESS se la risposta è stata generata correttamente
	 */
	public String importDataAsync(String sessionResultKey, 
									int rowPerCycle, 
									int firstDataRow,
									String allegatoFileName,
									String nomeFoglioExcel, 
									int versioneModelloExcel) 
		throws ExcelException {
		
		String target = Action.SUCCESS;
		
		boolean removeFromSession = false;
		this.status = null;
		File allegato = null;
		Object sheet = null; 
		
		try {
			allegato = new File(allegatoFileName);
			
			sheet = UtilityExcel.leggiFoglioExcelPerImportazione(
					allegatoFileName, nomeFoglioExcel, allegato, versioneModelloExcel);					
			
			// leggi dalla sessione lo stato corrente dell'elaborazione...
			// se non si trova lo stato elaborazione in sessione
			// allora si inizializza una nuova elaborazione...
			this.status = null;
			this.status = (ImportDataAsyncStatusBean)this.session.get(sessionResultKey);
			if (this.status == null) {
				this.status = new ImportDataAsyncStatusBean();
				this.status.setRowCount(UtilityExcel.ricavaNumeroRigheFoglio(sheet));
				this.status.setRowIndex(firstDataRow-1);
				
				importDataBegin();
			} 			

			// aggiungi il file all'elenco dei file temporanei da rimuovere
			// dall'area di work, al termine dell'elaborazione
			this.status.addFileGeneratiNellaWork(new File(allegatoFileName));

			// NB: gli indici per le celle del foglio Excel sono 0-based
			// quindi se il foglio ha dimensioni [N,M] allora l'accesso alle 
			// celle è cells[0..N-1, 0..M-1]
			int rowIndex = this.status.getRowIndex() + 1;	// ultima riga elaborata
			
			for (int i = 0; i < rowPerCycle; i++) {
				
				// verifica se sono finite le righe disponibili...
				if( rowIndex >= this.status.getRowCount() ) {
					this.status.setCompleted(true);
					break;
				}
				
				this.status.setRowIndex(rowIndex);
				
//System.out.println("importDataAsync " + (this.status.getRowIndex()+1)+"/"+this.status.getRowCount());
				
				// leggi 1 nuova riga dal file...
				this.status.setErroriRiga(new ArrayList<String>());
				Object row = null;
				try {
					row = UtilityExcel.leggiRiga(sheet, rowIndex);
					if (row == null || UtilityExcel.rigaVuota(row)) {
						// fine dati da importare, che devono essere contigui
						this.status.setCompleted(true);
						break;
					}
				} catch (ExcelException ex) {
					row = null;
					this.status.getErroriRiga().add(UtilityExcel.errore(ex));
				}
				
				// elabora la riga corrente...
				try {
					if( row != null && 
						!Action.SUCCESS.equalsIgnoreCase(importDataRow(sheet, this.status)) ) {
						this.status.setInterrupted(true);
						break;
					}
				} catch (Exception ex) {
					this.status.getErroriRiga().add(ex.getMessage());
				}
				
				rowIndex++;
			}
			
			// aggiorna lo stato dell'elaborazione...
			this.status.setRunning( !this.status.isCompleted() && 
					                !this.status.isInterrupted() );

		} catch (Exception ex) {
			this.addActionError(ex.getMessage());
			removeFromSession = true;
			target = Action.INPUT;			
		} 
		sheet = null;
				
		// prepara la risposta in formato JSON...		
		try {
			
			JSONObject json = new JSONObject();
			//json.putAll(maps);
			json.put("rowIndex", (this.status != null ? Integer.toString(this.status.getRowIndex()) : "0"));
			json.put("rowCount", (this.status != null ? Integer.toString(this.status.getRowCount()) : "0"));
			json.put("isCompleted", (this.status != null ? Boolean.toString(this.status.isCompleted()) : "false"));
			json.put("isInterrupted", (this.status != null ? Boolean.toString(this.status.isInterrupted()) : "false"));
			json.put("isRunning", (this.status != null ? Boolean.toString(this.status.isRunning()) : "false"));
			json.put("inputRows", (this.status != null ? Integer.toString(this.status.getInputRows()) : "0"));
			json.put("successRows", (this.status != null ? Integer.toString(this.status.getSuccessRows()) : "0"));
			json.put("skippedRows", (this.status != null ? this.status.getSkippedRows() : "0"));				
			json.put("errorRows", (this.status != null ? this.status.getErrorRows() : "0"));

			target = importDataUpdateStatus(json);
			
			// invia allo stream di risposta della action...
			this.setInputStream( new ByteArrayInputStream(json.toString().getBytes("UTF-8")) );
			
			json.clear();
			json = null;
			
		} catch (Exception ex) {
			this.addActionError(ex.getMessage());
			target = Action.INPUT;
		}		
		
		// salva lo stato dell'elaborazione in sessione...
		// e verifica se l'elaborazione è conclusa...
		if(this.status != null) {				
			if(this.status.isRunning()) {
				this.session.put(sessionResultKey, this.status);				
			} else {
				removeFromSession = true;
			}
			
			if( this.status.isCompleted() || this.status.isInterrupted() ) {
				importDataEnd();
				//removeFromSession = true; // ???
			}
		}
		
		// al termine dell'elaborazione, rimuovi dalla sessione lo stato...  
		if(removeFromSession) {
			this.session.remove(sessionResultKey);			
		}
		
		return target;
	}

	public String importDataAsync(String sessionResultKey,
									int firstDataRow,
									String allegatoFileName, 
									String nomeFoglioExcel, 
									int versioneModelloExcel) 
		throws ExcelException {
		return this.importDataAsync(
				sessionResultKey, IMPORT_ROWCOUNT_PER_CYCLE, firstDataRow,
				allegatoFileName, nomeFoglioExcel, versioneModelloExcel);
	}
	

	/**
	 * Inizializza l'importazione dei dati.
	 * Se necessario l'implementazione di base può essere reimplementata nella 
	 * classe figlia.
	 *      
	 * @return SUCCESS se l'inizializzazione dell'import è andata a buon fine
	 */
	public String importDataBegin() {
		return Action.SUCCESS;
	}
	

	/**
	 * Conclude l'importazione dei dati, indipendentemente dal successo o meno
	 * di tutta l'importazione. 
	 * Se necessario l'implementazione di base può essere reimplementata nella 
	 * classe figlia.
	 *      
	 * @return SUCCESS se la conclusione dell'import è andata a buon fine
	 */
	public String importDataEnd() {
		return Action.SUCCESS;
	}
	
	
	/**
	 * Aggiorna lo stato corrente dell'import, inviando una risposta in 
	 * formato JSON.
	 * 
	 * @return SUCCESS se la risposta è stata generata correttamente
	 */
	public String importDataUpdateStatus(JSONObject json) {				
		return Action.SUCCESS;
	}

	
	/**
	 * Importa una singola riga del file di dati (xls, ..., etc).
	 * Va implementata nella classe che eredita.
	 * 
	 * @return SUCCESS se la riga è stata impiortata correttamente.
	 */
	public abstract String importDataRow(Object sheet, ImportDataAsyncStatusBean status)
		throws ExcelException;
	
}
