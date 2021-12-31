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
package com.agiletec.apsadmin.system;

import java.util.Iterator;
import java.util.Set;

import com.agiletec.aps.system.common.tree.ITreeNode;
import com.agiletec.aps.system.common.tree.TreeNode;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;

/**
 * Classe base per gli helper che gestiscono le operazioni su oggetti alberi.
 * @version 1.0
 * @author E.Santoboni
 */
public abstract class TreeNodeBaseActionHelper extends BaseActionHelper implements ITreeNodeBaseActionHelper {
	
	/**
	 * Costruisce il codice univoco di un nodo in base ai parametri specificato.
	 * Il metodo:
	 * 1) elimina i caratteri non compresi tra "a" e "z", tra "0" e "9".
	 * 2) taglia (se necessario) la stringa secondo la lunghezza massima immessa.
	 * 3) verifica se esistono entità con il codice ricavato (ed in tal caso appende il suffisso "_<numero>" fino a che non trova un codice univoco).
	 * @param title Il titolo del nuovo nodo.
	 * @param baseDefaultCode Un codice nodo di default.
	 * @param maxLength La lunghezza massima del codice.
	 * @return Il codice univoco univoco ricavato.
	 * @throws ApsSystemException In caso di errore.
	 */
	public String buildCode(String title, String baseDefaultCode, int maxLength) throws ApsSystemException {
		String uniqueCode = null;
		try {
			// punto 1
			uniqueCode = purgeString(title);
			if (uniqueCode.length() == 0) {
				uniqueCode = baseDefaultCode;
			}
			// punto 2
			if (uniqueCode.length() > maxLength) {
				uniqueCode = uniqueCode.substring(0, maxLength);
				if (uniqueCode.charAt(uniqueCode.length()-1) == '_') {
					uniqueCode = uniqueCode.substring(0, uniqueCode.length()-1);
				}
			}
			//punto 3
			if (null != this.getTreeNode(uniqueCode)) {
				int index = 0;
				String currentCode = null;
				do {
					index++;
					currentCode = uniqueCode + "_" + index;
				} while (null != this.getTreeNode(currentCode));
				uniqueCode = currentCode;
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in creazione nuovo codice", t);
		}
		return uniqueCode;
	}
	
	/**
	 * Restituisce un nodo in in base al codice.
	 * @param code Il codice del nodo da restituire.
	 * @return Il nodo richiesto.
	 */
	protected abstract ITreeNode getTreeNode(String code);
	
	/**
	 * Implementazione di default del metodo.
	 * Crea un nodo root clonando completamente l'albero degli oggetti gestiti dall'helper.
	 */
	public ITreeNode getAllowedTreeRoot(UserDetails user) throws ApsSystemException {
		TreeNode root = new TreeNode();
		ITreeNode currentRoot = this.getRoot();
		this.fillTreeNode(root, root, currentRoot);
		this.addTreeWrapper(root, null, currentRoot);
		return root;
	}
	
	private void addTreeWrapper(TreeNode currentNode, TreeNode parent, ITreeNode currentTreeNode) {
		ITreeNode[] children = currentTreeNode.getChildren();
		for (int i=0; i<children.length; i++) {
			ITreeNode newCurrentTreeNode = children[i];
			TreeNode newNode = new TreeNode();
			this.fillTreeNode(newNode, currentNode, newCurrentTreeNode);
			currentNode.addChild(newNode);
			this.addTreeWrapper(newNode, currentNode, newCurrentTreeNode);
		}
	}
	
	/**
	 * Valorizza un nodo in base alle informazioni specificate..
	 * @param nodeToValue Il nodo da valorizzare.
	 * @param parent Il nodo parente.
	 * @param realNode Il nodo dal quela estrarre le info.
	 */
	protected void fillTreeNode(TreeNode nodeToValue, TreeNode parent, ITreeNode realNode) {
		nodeToValue.setCode(realNode.getCode());
		if (null == parent) {
			nodeToValue.setParent(nodeToValue);
		} else {
			nodeToValue.setParent(parent);
		}
		Set<Object> codes = realNode.getTitles().keySet();
		Iterator<Object> iterKey = codes.iterator();
		while (iterKey.hasNext()) {
			String key = (String) iterKey.next();
			String title = realNode.getTitles().getProperty(key);
			nodeToValue.getTitles().put(key, title);
		}
	}
	
	/**
	 * Restituisce il nodo root dell'albero gestito.
	 * @return Il nodo root.
	 */
	protected abstract ITreeNode getRoot();
	
}