package it.maggioli.eldasoft.plugins.ppcommon.apsadmin.customconfig;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.axis.utils.StringUtils;

public class SearchEventsBean extends BaseSearchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8563843125279678584L;
	
	/** Campo corrispondente a data da */
	private String dateFrom;
	/** Campo corrispondente a data a */
	private String dateTo;
	/** Campo corrispondente a utente */
	private String user;
	/** Campo corrispondente a destinazione */
	private String destination;
	/** Campo corrispondente a tipo */
	private String type;
	/** Campo corrispondente a livello */
	private String level;
	/** Campo corrispondente a messaggio */
	private String message;
	
	public static final int DEFAULT_ROWS_FOR_PAGE = 20;
	public static final int DEFAULT_START_ROW_INDEX = 0;
	public static final int DEFAULT_PAGES_TO_SHOW = 5;
	
	
	/**
	 * @return the dateFrom
	 */
	public String getDateFrom() {
		return dateFrom;
	}

	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public String getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	public SearchEventsBean() {
		this.setPageList(new ArrayList<Integer>());
		this.setiDisplayLength(DEFAULT_ROWS_FOR_PAGE);
		this.setiDisplayStart(DEFAULT_START_ROW_INDEX);
		this.setPagesToShow(DEFAULT_PAGES_TO_SHOW);
	}
	
	public boolean isEmpty() {
		boolean result = true;
		
		result = result && StringUtils.isEmpty(this.dateFrom);
		result = result && StringUtils.isEmpty(this.dateTo);
		result = result && StringUtils.isEmpty(this.user);
		result = result && StringUtils.isEmpty(this.destination);
		result = result && StringUtils.isEmpty(this.type);
		result = result && StringUtils.isEmpty(this.level);
		result = result && StringUtils.isEmpty(this.message);
		
		return result;
	}
	
	
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
	

	private void setStarEndPage() {

		Double ne_half = (Math.ceil((double) this.getPagesToShow() / 2));
		Double start = null;
		Double end = null;

		if(this.getCurrentPage() < this.getPagesToShow()){
			start = (double)1;
		}else{
			start = 1+(double)this.getCurrentPage() - this.getPagesToShow();
		}
		
		if(this.getCurrentPage() < ne_half-1){
			end = (double) Math.min(this.getPagesToShow(), this.getTotalPages());
		}else{
			if(this.getCurrentPage() > this.getTotalPages()-this.getPagesToShow()){
				end = (double)this.getTotalPages();
			}else{
				end = (double)this.getCurrentPage() + this.getPagesToShow()-1;
			}
		}
		
		this.setStartPage(start.intValue());
		this.setEndPage(end.intValue());
	}

}
