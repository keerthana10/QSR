

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
public class BogoInfo implements SerializableRead, SerializableWrite , Serializable {

    private int quantity;
    private String productId;
    private String isBogo;


  
  

  public void readValues(DataRead dr) throws BasicException {
      quantity = dr.getInt(1);
       productId = dr.getString(2);
        isBogo=dr.getString(3);

    }
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setInt(1, quantity);
        dp.setString(2, productId);
        dp.setString(3, isBogo);
    }

    public String getProductId(){
        return productId;
    }
    public void setProductId(String campaignId){
        this.productId = campaignId;
    }

    public int getQty(){
        return quantity;
    }
    public void setQty(int quantity){
        this.quantity = quantity;
    }

    /**
     * @return the isBogo
     */
    public String getIsBogo() {
        return isBogo;
    }

    /**
     * @param isBogo the isBogo to set
     */
    public void setIsBogo(String isBogo) {
        this.isBogo = isBogo;
    }


}
