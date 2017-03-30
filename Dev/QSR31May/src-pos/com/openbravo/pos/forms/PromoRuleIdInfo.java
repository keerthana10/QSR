/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.SerializableWrite;
import java.io.Serializable;

/**
 *
 * @author preethi
 */
public class PromoRuleIdInfo implements SerializableRead, SerializableWrite , Serializable {

    private String promoRuleId;
    private String promoType;


  public PromoRuleIdInfo() {
     // this.promoRuleId = promoRuleId;
    }

  public void readValues(DataRead dr) throws BasicException {
       promoRuleId = dr.getString(1);
        promoType=dr.getString(2);

    }
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, promoRuleId);     
        dp.setString(2, promoType);
    }

    public String getpromoRuleId(){
        return promoRuleId;
    }
    public void setpromoRuleId(String promoRuleId){
        this.promoRuleId = promoRuleId;
    }

    /**
     * @return the promoType
     */
    public String getPromoType() {
        return promoType;
    }

    /**
     * @param promoType the promoType to set
     */
    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }
}