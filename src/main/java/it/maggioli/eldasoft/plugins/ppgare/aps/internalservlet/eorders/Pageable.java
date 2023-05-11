package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import java.util.List;

public interface Pageable<T> {
	/**
	 * Returns the number of total pages.
	 *
	 * @return the number of total pages
	 */
	int getTotalPages();

	/**
	 * Returns the total amount of elements.
	 *
	 * @return the total amount of elements
	 */
	long getTotalElements();
	
	/**
	 * Returns the number of the current Slice. Is always non-negative.
	 *
	 * @return the number of the current Slice.
	 */
	int getNumber();

	/**
	 * Returns the size of the Slice.
	 *
	 * @return the size of the Slice.
	 */
	int getSize();

	/**
	 * Returns the number of elements currently on this Slice.
	 *
	 * @return the number of elements currently on this Slice.
	 */
	int getNumberOfElements();

	/**
	 * Returns the page content as {@link List}.
	 *
	 * @return
	 */
	List<T> getContent();

	/**
	 * Returns whether the Slice has content at all.
	 *
	 * @return
	 */
	boolean hasContent();

	/**
	 * Returns whether the current Slice is the first one.
	 *
	 * @return
	 */
	boolean isFirst();

	/**
	 * Returns whether the current Slice is the last one.
	 *
	 * @return
	 */
	boolean isLast();

	/**
	 * Returns if there is a next Slice.
	 *
	 * @return if there is a next Slice.
	 */
	boolean hasNext();

	/**
	 * Returns if there is a previous Slice.
	 *
	 * @return if there is a previous Slice.
	 */
	boolean hasPrevious();
}
