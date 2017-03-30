
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
public class BuyGetPriceInfo implements SerializableRead, SerializableWrite , Serializable {

    private double price;
    private String productid;
    private String taxCat;
    private String category;
    private double quantity;
    private String attributesId;
    private String campaignId;
    private double taxRate;
    private double serviceTax;
    private double sbtax;


  public BuyGetPriceInfo() {
     // this.promoRuleId = promoRuleId;
    }

  public void readValues(DataRead dr) throws BasicException {
      productid = dr.getString(1);
      price = dr.getDouble(2);
      category = dr.getString(3);
      taxCat = dr.getString(4);
      quantity = dr.getDouble(5);
      attributesId = dr.getString(6);
      campaignId = dr.getString(7);
      taxRate = dr.getDouble(8);
      serviceTax = dr.getDouble(9);
      sbtax = dr.getDouble(10);
    }

  public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, productid);
        dp.setDouble(2, price);
        dp.setString(3, category);
        dp.setString(4, taxCat);
        dp.setDouble(5, quantity);
        dp.setString(6, attributesId);
        dp.setString(7, campaignId);
        dp.setDouble(8, taxRate);
         dp.setDouble(9, serviceTax);
         dp.setDouble(10, sbtax);

    }

   public double getPrice(){
        return price;
    }
    public void setPrice(double price){
        this.price = price;
    }
    public String getProductID(){
        return productid;
    }
    public void setProductId(String productid){
        this.productid = productid;
    }
     public String getTaxCat(){
        return taxCat;
    }
    public void setTaxCat(String taxCat){
        this.taxCat = taxCat;
    }
     public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public double getQuantity(){
        return quantity;
    }
    public void setQuantity(double quantity){
        this.quantity = quantity;
    }
    public String getAttributesID(){
        return attributesId;
    }
    public void setAttributesId(String attributesId){
        this.attributesId = attributesId;
    }
    public String getCampaignId(){
        return campaignId;
    }
    public void setCampaignId(String campaignId){
        this.campaignId = campaignId;
    }
    public double getTaxRate(){
        return taxRate;
    }
    public void setTaxRate(double taxRate){
        this.taxRate = taxRate;
    }

    public double getSbtax() {
        return sbtax;
    }

    public void setSbtax(double sbtax) {
        this.sbtax = sbtax;
    }

    public double getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(double serviceTax) {
        this.serviceTax = serviceTax;
    }

}
