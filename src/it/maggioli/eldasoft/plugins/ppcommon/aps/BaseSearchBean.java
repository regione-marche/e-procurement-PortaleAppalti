package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;

/**
 * Classe di base per la definizione di action con cui popolare tabelle paginate
 *
 * @author Marco Perazzetta
 */
public abstract class BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8784999520523677962L;

	public static final int DEFAULT_ROWS_FOR_PAGE = 10;
	public static final int DEFAULT_START_ROW_INDEX = 0;
	public static final int DEFAULT_PAGES_TO_SHOW = 5;

	/**
	 * Indice della pagina correntemente estratta (la prima pagina corrisponde
	 * all'indice 0).
	 */
	private int currentPage;
	/**
	 * Numero totale di pagine estratte dalla query.
	 */
	private int totalPages;
	/**
	 * Numero totale di record visualizzati con filtro
	 */
	private int iTotalDisplayRecords;
	/**
	 * Numero totale di record estraibili
	 */
	private int iTotalRecords;
	/**
	 * Indice dell'ultimo record della pagina selezionata rispetto al numero
	 * totale di record estratti.
	 */
	private int endIndex;
	private int pagesToShow;
	private Boolean hasNext;
	private Boolean hasPrevious;
	private int startPage;
	private int endPage;
	private List<Integer> pageList;

	public BaseSearchBean() {
		this.pageList = new ArrayList<Integer>();
		this.setiDisplayLength(DEFAULT_ROWS_FOR_PAGE);
		this.setiDisplayStart(DEFAULT_START_ROW_INDEX);
		this.setPagesToShow(DEFAULT_PAGES_TO_SHOW);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public int getPagesToShow() {
		return pagesToShow;
	}

	public void setPagesToShow(int pagesToShow) {
		this.pagesToShow = pagesToShow;
	}

	public Boolean getHasNext() {
		return hasNext;
	}

	public void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	public Boolean getHasPrevious() {
		return hasPrevious;
	}

	public void setHasPrevious(Boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	//--- PARAMETRI UTILIZZATI DA DATABLE ---
//	/**
//	 * Sequence di richiesta inviata dal plugin DataTable, lo stesso valore deve
//	 * essere ritornato in risposta
//	 */
////	public String sEcho;
//	
//	/**
//	 * Testo usato per filtrare il risultato
//	 */
////	public String sSearch;
	
	/**
	 * Numero di record che dovrebbero essere mostrati in tabella 
	 * (risultati per pagina)
	 */
	private Integer iDisplayLength;
	
	/**
	 * Primo record che dovrebbe essere mostrato (in paginazione)
	 */
	private Integer iDisplayStart;

	public Integer getiDisplayLength() {
		return iDisplayLength;
	}

	public void setiDisplayLength(Integer iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}

	public Integer getiDisplayStart() {
		return iDisplayStart;
	}

	public void setiDisplayStart(Integer iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}

	public List<Integer> getPageList() {
		return pageList;
	}

	public void setPageList(List<Integer> pageList) {
		this.pageList = pageList;
	}

	public int getiTotalRecords() {
		return iTotalRecords;
	}
	
	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

//	/**
//	 * Numero di colonne della tabella
//	 */
////	public int iColumns;
//	
//	/**
//	 * Numero di colonne usate nell'ordinamento
//	 */
////	public int iSortingCols;
//	
//	/**
//	 * Indice di colonna usata per l'ordinamento
//	 */
////	public int iSortColumnIndex;
//	
//	/**
//	 * Tipo di ordinamento ("asc" o "desc")
//	 */
////	public String sSortDirection;
//	
//	/**
//	 * Comma separated list dei nomi di colonna
//	 */
////	public String sColumns;
	
	/**
	 * Calcolo tutti i parametri utili alla paginazione
	 *
	 * @param iTotalRecords numero totale di record senza filtro
	 * @param iTotalDisplayRecords numero totale di record filtrati
	 */
	public void processResult(int iTotalRecords, int iTotalDisplayRecords) {
		this.setiTotalDisplayRecords(iTotalDisplayRecords);
		this.setiTotalRecords(iTotalRecords);

		if (this.getCurrentPage() == 0) {
			this.setCurrentPage(1);
		}

		if (hasMorePages()) {
			this.setTotalPages(getTotalPagesNumber());
		} else {
			this.setTotalPages(1);
		}

		this.setHasNext(hasNextPage());
		this.setHasPrevious(hasPreviousPage());

		if (!isFirstPage()) {
			this.setiDisplayStart(getStartIndexNumber());
		} else {
			this.setiDisplayStart(0);
		}

		setStarEndPage();
		setEndIndex(this.getiDisplayStart() + iTotalDisplayRecords);

		this.setPageList(new ArrayList<Integer>());
		for (int i = this.getStartPage(); i <= this.getEndPage(); i++) {
			this.getPageList().add(i);
		}
	}

	public boolean hasMorePages() {
		return this.getiTotalDisplayRecords() > this.getiDisplayLength();
	}

	public Integer getTotalPagesNumber() {
		return (new Double(Math.ceil(((Integer) this.getiTotalDisplayRecords()).doubleValue() / this.getiDisplayLength())).intValue());
	}

	public boolean hasNextPage() {
		return this.getCurrentPage() < this.getTotalPages();
	}

	public boolean hasPreviousPage() {
		return this.getCurrentPage() > 1;
	}

	public boolean isFirstPage() {
		return this.getCurrentPage() <= 1;
	}

	public boolean isLastPage() {
		return this.getCurrentPage() >= this.getTotalPages();
	}

	public int getNextPageNumber() {
		return this.getCurrentPage() + 1;
	}

	public int getPreviousPageNumber() {
		return this.getCurrentPage() - 1;
	}

	public int getStartIndexNumber() {
		return this.getCurrentPage() > 1 ? ((this.getCurrentPage() - 1) * this.getiDisplayLength()) : 0;
	}

	public int getLastIndexNumber() {
		int fullPage = getStartIndexNumber() + this.getiDisplayLength();
		return (this.getiTotalDisplayRecords() > 0 && this.getiTotalDisplayRecords() < fullPage) ? this.getiTotalDisplayRecords() : fullPage;
	}

	private void setStarEndPage() {
		Double ne_half = (Math.ceil((double) this.getPagesToShow() / 2));
		Double upper_limit = (double) this.getTotalPages() - this.getPagesToShow();
		Double start = (this.getCurrentPage() > ne_half) 
						? (Math.max(Math.min(this.getCurrentPage() - ne_half, upper_limit), ne_half)) 
						: 1;
		Double end = (this.getCurrentPage() > ne_half) 
						? Math.min(this.getCurrentPage() + ne_half, this.getTotalPages())
						: Math.min(this.getPagesToShow(), this.getTotalPages());
		this.setStartPage(start.intValue());
		this.setEndPage(end.intValue());
	}
	
	/**
	 * copia dalla sessione solo le proprietà necessarie
	 * a mantenere lo stato della navigazione
	 * Salva alcune proprietà dalla vecchia istanza 
	 * e copiale nella nuova
	 */
	public void restoreFrom(Object source) {
		int currentPage = this.getCurrentPage();
		
		// gestisci i valori delle proprietà della classe figlia 
		// (Bandi, Esiti, Avvisi)...
		Field[] fields = source.getClass().getDeclaredFields();
		for (int i=0; i < fields.length; i++) {
			try {
				//String element = fields[i].getName();
				fields[i].setAccessible(true);
				Object value = fields[i].get(source);
				fields[i].set(this, value);  
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		// gestisci le proprietà della classe base...
		BaseSearchBean src = (BaseSearchBean) source; 
		this.setiDisplayLength(src.getiDisplayLength());
		this.setCurrentPage(currentPage);
	}
	
}
