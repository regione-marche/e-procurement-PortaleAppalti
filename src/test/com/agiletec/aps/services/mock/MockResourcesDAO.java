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
package test.com.agiletec.aps.services.mock;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractDAO;

/**
 * @version 1.0
 * @author M.Casari
 */
public class MockResourcesDAO extends AbstractDAO {
	
    /**
     * 
     * @param descr Codice contenuto.
     * @throws ApsSystemException In caso di errore nell'accesso al db.
     */
    public int deleteResource(String descr) throws ApsSystemException {
    	Connection conn = null;
        PreparedStatement stat = null;
        int res = -1;
        try {
        	conn = this.getConnection();
            stat = conn.prepareStatement("delete from resources where descr = ?");
            stat.setString(1, descr);
            res = stat.executeUpdate();
        } catch (Throwable t) {
            processDaoException(t, "Errore in cancellazione risorsa ", "deleteResource");
        } finally {
            closeDaoResources(null, stat, conn);
        }
        return res;
    }    
}
