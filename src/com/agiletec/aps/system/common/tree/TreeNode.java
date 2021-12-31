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
package com.agiletec.aps.system.common.tree;

import java.io.Serializable;

import com.agiletec.aps.util.ApsProperties;

/**
 * A node of a tree. 
 * The node is the basic information a tree and contains all the 
 * minimum information necessary for its definition.
 * @author E.Santoboni
 */
public class TreeNode implements ITreeNode, Serializable {
	
	@Override
	public String getCode() {
		return _code;
	}
	public void setCode(String code) {
		this._code = code;
	}
	
	@Override
	public boolean isRoot() {
		return (null == this.getParent() || this.getCode().equals(this.getParent().getCode()));
	}
	
	@Override
	public ITreeNode getParent() {
		return _parent;
	}
	public void setParent(ITreeNode parent) {
		this._parent = parent;
	}
	
	@Override
	public ITreeNode[] getChildren() {
		return _children;
	}
	public void setChildren(ITreeNode[] children) {
		this._children = children;
	}
	
	/**
	 * Adds a node to nodes in a lower level. 
	 * The new node is inserted in the final position.
	 * @param treeNode The node to add.
	 */
	public void addChild(ITreeNode treeNode) {
		int len = this._children.length;
		ITreeNode[] newChildren = new ITreeNode[len + 1];
		for(int i=0; i < len; i++){
			newChildren[i] = this._children[i];
		}
		newChildren[len] = treeNode;
		this._children = newChildren;
	}
	
	@Override
	public int getPosition() {
		return _position;
	}
	protected void setPosition(int position) {
		this._position = position;
	}
	
	@Override
	public ApsProperties getTitles() {
		return _titles;
	}
	
	/**
	 * Set the titles of the node. 
	 * @param titles A set of properties with the titles, 
	 * where the keys are the codes of language.
	 */
	public void setTitles(ApsProperties titles) {
		this._titles = titles;
	}
	
	@Override
	public void setTitle(String langCode, String title) {
		this.getTitles().setProperty(langCode, title);
	}
	
	@Override
	public String getTitle(String langCode) {
		return this.getTitles().getProperty(langCode);
	}
	
	@Override
	public String getFullTitle(String langCode) {
		return this.getFullTitle(langCode, " / ");
	}
	
	@Override
	public String getFullTitle(String langCode, String separator) {
		String title = this.getTitles().getProperty(langCode);
		ITreeNode parent = this.getParent();
		if (parent != null && parent.getParent() != null && 
				!parent.getCode().equals(parent.getParent().getCode())) {
			String parentTitle = this.getParent().getFullTitle(langCode, separator);
			title = parentTitle + separator + title;
		}
		return title;
	}
	
	@Override
	public String toString() {
		return "Node: " + this.getCode();
	}
	
	private String _code;
	
	private ITreeNode _parent;
	
	private ITreeNode[] _children = new ITreeNode[0];
	
	private int _position = -1;
	
	private ApsProperties _titles = new ApsProperties();
	
}
