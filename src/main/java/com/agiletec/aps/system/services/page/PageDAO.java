/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system.services.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;
import com.agiletec.aps.system.services.pagemodel.IPageModelManager;
import com.agiletec.aps.system.services.showlettype.IShowletTypeManager;
import com.agiletec.aps.system.services.showlettype.ShowletType;
import com.agiletec.aps.util.ApsProperties;

/**
 * Data Access Object for the 'page' objects
 * @author M.Diana - E.Santoboni
 */
public class PageDAO extends AbstractDAO implements IPageDAO {

	/**
	 * Load a sorted list of the pages and the configuration of the showlets 
	 * @return the list of pages
	 */
	@Override
	public List<IPage> loadPages() {
		Connection conn = null;
		Statement stat = null;
		ResultSet res = null;
		List<IPage> pages = null;
		try {
			conn = this.getConnection();
			stat = conn.createStatement();
			res = stat.executeQuery(ALL_PAGES);
			pages = this.createPages(res);
		} catch (Throwable t) {
			processDaoException(t, "Error loading pages", "loadPages");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return pages;
	}

	/**
	 * Read & create in a single passage, for efficiency reasons, the pages and the 
	 * association of the associated showlets.
	 * @param res the result set where to extract pages information from.
	 * @return The list of the pages defined in the system
	 * @throws Throwable In case of error
	 */
	protected List<IPage> createPages(ResultSet res) throws Throwable {
		List<IPage> pages = new ArrayList<IPage>();
		Object record[] = null;
		Page page = null;
		Showlet showlets[] = null;
		int numFrames = 0;
		String prevCode = "...no previous code...";
		while (res.next()) {
			record = getRecord(res);
			String code = (String) record[3];
			if (!code.equals(prevCode)) {
				if (page != null) {
					pages.add(page);
				}
				page = this.createPage(record);
				numFrames = page.getModel().getFrames().length;
				showlets = new Showlet[numFrames];
				page.setShowlets(showlets);
				prevCode = code;
			}
			if (record[8] != null) {
				Integer pos = new Integer(record[8].toString());
				int intPos = pos.intValue();
				if (intPos >= 0 && intPos < numFrames) {
					showlets[intPos] = this.createShowlet(record, page);
				} else {
					ApsSystemUtils.getLogger().info("The position read from the database exceeds " +
							"the numer of frames defined in the model of the page '"+ page.getCode()+"'");
				}
			}
		}
		pages.add(page);
		return pages;
	}

	/**
	 * Return an array of the objects contained in the given result set. This is needed in
	 * order to put the record in a buffer because some drivers will prevent the access to the
	 * same column twice in the same result set.
	 * WARNING: to fetch the objects of the record use the same index of the result set,
	 * starting from 1 for the first column.
	 * 
	 * @param res The result set containing the objects to put in a buffer.
	 * @return An array containing the objects fetched from the given result set
	 * @throws SQLException in case of error
	 */
	protected Object[] getRecord(ResultSet res) throws SQLException {
		int size = res.getMetaData().getColumnCount();
		Object record[] = new Object[size + 1];
		for (int i = 1; i <= size; i++) {
			record[i] = res.getObject(i);
		}
		return record;
	}

	protected Page createPage(Object[] record) throws ApsSystemException {
		Page page = new Page();
		page.setCode((String) record[3]);
		page.setParentCode((String) record[1]);
		page.setPosition(Integer.parseInt(record[2].toString()));
		Integer showable = new Integer (record[4].toString());
		page.setShowable(showable.intValue() == 1);
		String modelCode = (String) record[5];
		page.setModel(this.getPageModelManager().getPageModel(modelCode));
		String titleText = (String) record[6];
		ApsProperties titles = new ApsProperties();
		try {
			titles.loadFromXml(titleText);
		} catch (Throwable t) {
			String msg = "IO error detected while parsing the titles of the page '" + page.getCode()+"'";
			ApsSystemUtils.logThrowable(t, this, "createPage", msg);
			throw new ApsSystemException(msg, t);
		}
		page.setTitles(titles);
		page.setGroup((String) record[7]);
		return page;
	}

	protected Showlet createShowlet(Object[] record, IPage page) throws ApsSystemException {
		Showlet showlet = new Showlet();
		int pos = Integer.parseInt(record[8].toString());
		String typeCode = (String) record[9];
		ShowletType type = this.getShowletTypeManager().getShowletType(typeCode);
		showlet.setType(type);
		ApsProperties config = new ApsProperties();
		String configText = (String) record[10];
		if (null != configText && configText.trim().length() > 0) {
			try {
				config.loadFromXml(configText);
			} catch (Throwable t) {
				String msg = "IO error detected while parsing the configuration of the showlet in position " +pos+
				" of the page '"+ page.getCode()+"'";
				ApsSystemUtils.logThrowable(t, this, "createShowlet", msg);
				throw new ApsSystemException(msg, t);
			}
		} else {
			config = type.getConfig();
		}
		showlet.setConfig(config);
		String contentPublished = (String) record[11];
		showlet.setPublishedContent(contentPublished);
		return showlet;
	}

	/**
	 * Insert a new page.
	 * @param page The new page to insert.
	 */
	@Override
	public void addPage(IPage page) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.addPageRecord(page, conn);
			this.addShowletForPage(page, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while adding a new page", "addPage");
		} finally {
			closeConnection(conn);
		}
	}

	protected void addPageRecord(IPage page, Connection conn) throws ApsSystemException {
		int position = 1;
		if (page.getParent().getChildren()!= null ){
			position = page.getParent().getChildren().length + 1;
		}
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(ADD_PAGE);
			stat.setString(1, page.getCode());
			stat.setString(2, page.getParent().getCode());
			if (page.isShowable()) {
				stat.setInt(3, 1);
			} else {
				stat.setInt(3, 0);
			}
			stat.setInt(4, position);
			stat.setString(5, page.getModel().getCode());
			stat.setString(6, page.getTitles().toXml());
			stat.setString(7, page.getGroup());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error adding a new page record", "addPageRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Delete the page identified by the given code.
	 * @param page The page to delete.
	 */
	@Override
	public void deletePage(IPage page) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteShowlets(page.getCode(), conn);
			this.deletePageRecord(page.getCode(), conn);
			this.shiftPages(page.getParentCode(), page.getPosition(), conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error deleting the page", "deletePage");
		} finally {
			closeConnection(conn);
		}
	}

	protected void deletePageRecord(String pageCode, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_PAGE);
			stat.setString(1, pageCode);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error deleting a page record", "deletePageRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Delete the showlets associated to a page.
	 * @param codePage The code of the page containing the showlets to delete.
	 * @throws ApsSystemException In case of database error
	 */
	protected void deleteShowlets(String codePage, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(DELETE_SHOWLETS_FOR_PAGE);
			stat.setString(1, codePage);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error while deleting showlets", "deleteShowlets");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Decrement by one the position of a group of pages to compact the positions after a deletion
	 * @param parentCode the code of the parent of the pages to compact.
	 * @param position The empty position which needs to be compacted.
	 * @param conn The connection to the database
	 * @throws ApsSystemException In case of database error
	 */
	protected void shiftPages(String parentCode, int position, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(SHIFT_PAGE);
			stat.setString(1, parentCode);
			stat.setInt(2, position);
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error while compating positions",
			"shiftPages");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	/**
	 * Updates the position for the page movement
	 * @param pageDown The page to move downwards
	 * @param pageUp The page to move upwards
	 */
	@Override
	public void updatePosition(IPage pageDown, IPage pageUp) {
		Connection conn = null;
		PreparedStatement stat = null;
		PreparedStatement stat2 = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);

			stat = conn.prepareStatement(MOVE_DOWN);
			stat.setString(1, pageDown.getCode());
			stat.executeUpdate();

			stat2 = conn.prepareStatement(MOVE_UP);
			stat2.setString(1, pageUp.getCode());
			stat2.executeUpdate();

			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error detected while updating positions", "updatePosition");
		} finally {
			closeDaoResources(null, stat);
			closeDaoResources(null, stat2, conn);
		}
	}

	/**
	 * Updates a page record in the database.
	 * @param page The page to update
	 */
	@Override
	public void updatePage(IPage page) {
		Connection conn = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			this.deleteShowlets(page.getCode(), conn);
			this.updatePageRecord(page, conn);
			this.addShowletForPage(page, conn);
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error while updating the page", "updatePage");
		} finally {
			closeConnection(conn);
		}
	}

	protected void updatePageRecord(IPage page, Connection conn) throws ApsSystemException {
		PreparedStatement stat = null;
		try {
			stat = conn.prepareStatement(UPDATE_PAGE);
			stat.setString(1, page.getParentCode());
			if (page.isShowable()) {
				stat.setInt(2, 1);
			} else {
				stat.setInt(2, 0);
			}
			stat.setString(3, page.getModel().getCode());
			stat.setString(4, page.getTitles().toXml());
			stat.setString(5, page.getGroup());
			stat.setString(6, page.getCode());
			stat.executeUpdate();
		} catch (Throwable t) {
			processDaoException(t, "Error while updating the page record", "updatePageRecord");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	protected void addShowletForPage(IPage page, Connection conn) throws ApsSystemException {
		if (null == page.getShowlets()) return;
		PreparedStatement stat = null;
		try {
			Showlet[] showlets = page.getShowlets();
			stat = conn.prepareStatement(ADD_SHOWLET_FOR_PAGE);
			for (int i = 0; i < showlets.length; i++) {
				Showlet showlet = showlets[i];
				if (showlet != null) {
					stat.setString(1, page.getCode());
					stat.setInt(2, i);
					stat.setString(3, showlet.getType().getCode());
					String config = null;
					if (null != showlet.getConfig()) {
						config = showlet.getConfig().toXml();
					}
					stat.setString(4, config);
					stat.setString(5, showlet.getPublishedContent());
					stat.addBatch();
					stat.clearParameters();
				}
			}
			stat.executeBatch();
		} catch (Throwable t) {
			processDaoException(t, "Error while inserting the showlets in a page",
					"addShowletForPage");
		} finally {
			closeDaoResources(null, stat);
		}
	}

	@Override
	public void removeShowlet(String pageCode, int pos) {
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(REMOVE_SHOWLET_FROM_FRAME);
			stat.setString(1, pageCode);
			stat.setInt(2, pos);
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "" +
					"Error removing the showlet in the page '" +pageCode + "' in the frame " + pos, "removeShowlet");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	@Override
	public void joinShowlet(String pageCode, Showlet showlet, int pos) {
		this.removeShowlet(pageCode, pos);
		Connection conn = null;
		PreparedStatement stat = null;
		try {
			conn = this.getConnection();
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(ADD_SHOWLET_FOR_PAGE);
			stat.setString(1, pageCode);
			stat.setInt(2, pos);
			stat.setString(3, showlet.getType().getCode());
			String config = null;
			if (null != showlet.getConfig()) {
				config = showlet.getConfig().toXml();
			}
			stat.setString(4, config);
			stat.setString(5, showlet.getPublishedContent());
			stat.executeUpdate();
			conn.commit();
		} catch (Throwable t) {
			this.executeRollback(conn);
			processDaoException(t, "Error adding a showlet in the frame " +pos+" of the page '"+pageCode+"'", "joinShowlet");
		} finally {
			closeDaoResources(null, stat, conn);
		}
	}

	protected IPageModelManager getPageModelManager() {
		return _pageModelManager;
	}
	public void setPageModelManager(IPageModelManager pageModelManager) {
		this._pageModelManager = pageModelManager;
	}

	protected IShowletTypeManager getShowletTypeManager() {
		return _showletTypeManager;
	}
	public void setShowletTypeManager(IShowletTypeManager showletTypeManager) {
		this._showletTypeManager = showletTypeManager;
	}

	private IPageModelManager _pageModelManager;
	private IShowletTypeManager _showletTypeManager;

	// attenzione: l'ordinamento deve rispettare prima l'ordine delle pagine
	// figlie nelle pagine madri, e poi l'ordine delle showlet nella pagina.
	private static final String ALL_PAGES = 
		"SELECT p.parentcode, p.pos, p.code, p.showinmenu, "
		+ "p.modelcode, p.titles, p.groupcode, "
		+ "s.framepos, s.showletcode, s.config, s.publishedcontent "
		+ "FROM pages p LEFT JOIN showletconfig s ON p.code = s.pagecode "
		+ "ORDER BY p.parentcode, p.pos, p.code, s.framepos";

	private static final String ADD_PAGE = 
		"INSERT INTO pages(code, parentcode, showinmenu, pos, modelcode, titles, groupcode) VALUES (?,?,?,?,?,?,?)";

	private static final String DELETE_PAGE = 
		"DELETE FROM pages WHERE code = ? ";

	private static final String DELETE_SHOWLETS_FOR_PAGE = 
		"DELETE FROM showletconfig WHERE pagecode = ? ";

	private static final String REMOVE_SHOWLET_FROM_FRAME = 
		DELETE_SHOWLETS_FOR_PAGE + " AND framepos = ? ";

	private static final String MOVE_UP = 
		"UPDATE pages SET pos = (pos - 1) WHERE code = ? ";

	private static final String MOVE_DOWN = 
		"UPDATE pages SET pos = (pos + 1) WHERE code = ? ";

	private static final String UPDATE_PAGE = 
		"UPDATE pages SET parentcode = ? , showinmenu = ? , modelcode = ? , titles = ? , groupcode = ? WHERE code = ? ";

	private static final String SHIFT_PAGE = 
		"UPDATE pages SET pos = (pos - 1) WHERE parentcode = ? AND pos > ? ";

	private static final String ADD_SHOWLET_FOR_PAGE = 
		"INSERT INTO showletconfig (pagecode, framepos, showletcode, config, publishedcontent) VALUES ( ?, ?, ?, ?, ?)";

}