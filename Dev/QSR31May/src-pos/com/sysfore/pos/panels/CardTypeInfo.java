//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.sysfore.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializableRead;

/**
 *
 * @author adrianromero
 * Created on 27 de febrero de 2007, 23:27
 *
 */
public class CardTypeInfo implements SerializableRead, IKeyed {
    
    private static final long serialVersionUID = 9110127845966L;
    private String m_sID;
    protected String cardType;
    
    /** Creates a new instance of RoleInfo */
    public CardTypeInfo() {
        m_sID = null;
        cardType = null;
    }
   
    public Object getKey() {
        return m_sID;
    }
    
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        cardType = dr.getString(2);
    }     
    
    public void setID(String sID) {
        m_sID = sID;
    }
    
    public String getID() {
        return m_sID;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String sValue) {
        cardType = sValue;
    }    
    
 
}
