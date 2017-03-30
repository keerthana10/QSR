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

package com.openbravo.pos.ticket;

import java.io.*;
import com.openbravo.pos.util.StringUtils;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.SerializableRead;
import com.openbravo.data.loader.DataWrite;
import com.openbravo.format.Formats;
import com.openbravo.data.loader.SerializableWrite;
import com.openbravo.basic.BasicException;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BillPromoRuleInfo;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.CrossProductInfo;
import com.openbravo.pos.forms.PromoRuleIdInfo;
import com.openbravo.pos.forms.PromoRuleInfo;
import com.openbravo.pos.sales.JRetailPanelEditTicket;
import com.openbravo.pos.sales.JRetailPanelHomeTicket;
import com.openbravo.pos.sales.JRetailPanelTakeAway;

import com.openbravo.pos.sales.JRetailPanelTicket;
import com.openbravo.pos.sales.JRetailTicketLines;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class RetailTicketLineInfo implements SerializableWrite, SerializableRead, Serializable {

    private static final long serialVersionUID = 6608012948284450199L;
    private String m_sTicket;
    private int m_iLine;
    private double multiply;
    private double freemultiply;
    private double price;
    private TaxInfo tax;
    private Properties attributes;
    private String productid;
    private String productionAreaType;
    private String attsetinstid;
    private double discountValue;
    public java.util.ArrayList<PromoRuleInfo> promoRuleList = null;
    public java.util.ArrayList<CrossProductInfo> crossproductList = null;
    protected PromoRuleInfo promoDetails;
    public java.util.ArrayList<PromoRuleIdInfo> promoRuleIdList;
    private double billDiscount;
    private String pName;
    private int preparationStatus;
  
    double qty =0;
    int buttonPlus;
    String pdtpromoType = "";
    protected transient DataLogicSales dlSales;
    protected transient RetailTicketInfo m_oTicket;
    protected transient JRetailTicketLines m_ticketlines;

    protected transient JRetailPanelTicket jRetailPanel;
      protected transient JRetailPanelTakeAway jRetailTakeAwayPanel;

    protected transient JRetailPanelEditTicket jRetailEditPanel;
      protected transient JRetailPanelHomeTicket jRetailHomePanel;
    private int index1;
    private double CurrentPrice;
    private int ticketType;
    private double taxRate;
    public String ticketId;
    public String lineId;
    public int lineNo;
    private String campaignId;
    private String promoId;
    private String isCrossProduct;
    private String cancelStatus;
    private String productType;
    private int isKot;
    private String tbl_orderId;
    private String addonId;
    private int primaryAddon;
    private String instruction;
    private int kotid;
    private Date kotdate;
    private String kottable;
    private String kotuser;
    //newly added variables to calculate line level service charge and service tax
    private ServiceChargeInfo charge;
    private TaxInfo servicetax;
    private TaxInfo sbtax;
    private  String discountrate="";
    // private  String DiscountAmount="";
    private String catId;
    private String kdsPrepareStatus="New";
    private String preparationTime;
    private String sosorderLineid;
    private double offerDiscount;
    private String productionArea;
    private String isBogoMain;
     private double promodiscountPercent;
     private double actualPrice;
       //added to differentiate least value and buyone-getone promotion 13/10/2016
    private String isPromoSku;
    
    
    /** Creates new TicketLineInfo */
    public RetailTicketLineInfo(String productid, double dMultiply, double dPrice, TaxInfo tax,double discountValue,String pName,String productType,String productionAreaType, Properties props,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid,TaxInfo sbTax, String product) {
   //     System.out.println("RetailTicketLineInfo 1");
        init(productid, null, dMultiply, dPrice, null,null,null,null,null,tax, discountValue,pName,productType,productionAreaType, props,null,0,null,0,null,null,null,charge,servicetax,parentCat,preparationTime,sosorderLineid, sbTax,false);
    }

    public RetailTicketLineInfo(String productid, double dMultiply, double dPrice, TaxInfo tax, double discountValue, String pName,String productType,String productionAreaType,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid, TaxInfo sbTax) {
         // System.out.println("RetailTicketLineInfo 2");
        init(productid, null, dMultiply, dPrice, null,null,null,null,null,tax,discountValue,pName,productType,productionAreaType, new Properties(),null,0,null,0,null,null,null,charge,servicetax,parentCat,preparationTime,sosorderLineid,sbTax,false);
    }

    public RetailTicketLineInfo(String productid, String productname, String producttaxcategory, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,RetailTicketInfo m_oTicket,JRetailTicketLines m_ticketlines,JRetailPanelTicket jRetailPanel,TaxInfo tax,double discountValue,String pName, String productType,String productionAreaType,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid, TaxInfo sbTax) {
        Properties props = new Properties();
          //System.out.println("RetailTicketLineInfo 3");
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        setvalues(promoRule,dlSales,m_oTicket,m_ticketlines,jRetailPanel);
        init(productid, null, dMultiply, dPrice,promoRule,dlSales,m_ticketlines,m_oTicket,jRetailPanel,tax, discountValue,pName,productType,productionAreaType, props,null,0,null,0,null,null,null,charge,servicetax,parentCat,preparationTime,sosorderLineid,sbTax,false) ;
    }
    //for buy one get one promotion
       public RetailTicketLineInfo(String productid, String productname, String producttaxcategory, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,RetailTicketInfo m_oTicket,JRetailTicketLines m_ticketlines,JRetailPanelTicket jRetailPanel,TaxInfo tax,double discountValue,String pName, String productType,String productionAreaType,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid, TaxInfo sbTax,boolean buyone) {
        Properties props = new Properties();
          //System.out.println("RetailTicketLineInfo 3");
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        setvalues(promoRule,dlSales,m_oTicket,m_ticketlines,jRetailPanel);
        init(productid, null, dMultiply, dPrice,promoRule,dlSales,m_ticketlines,m_oTicket,jRetailPanel,tax, discountValue,pName,productType,productionAreaType, props,null,0,null,0,null,null,null,charge,servicetax,parentCat,preparationTime,sosorderLineid,sbTax,buyone) ;
    }
    

    public RetailTicketLineInfo(String productname, String producttaxcategory, double dMultiply, double dPrice, TaxInfo tax, double discountValue, String pName,String productType,String productionAreaType,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid,TaxInfo sbTax) {
        Properties props = new Properties();
        //  System.out.println("RetailTicketLineInfo 4");
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        init(null, null, dMultiply, dPrice, null,null,null,null,null,tax, discountValue,pName,productType,productionAreaType,props,null,0,null,0,null,null,null,charge,servicetax,parentCat,preparationTime,sosorderLineid,sbTax,false) ;
    }

    public RetailTicketLineInfo() {
          //System.out.println("RetailTicketLineInfo 5");
        init(null, null, 0.0, 0.0, null,null,null,null,null,null, 0.0,null,null,null,new Properties(),null,0,null,0,null,null,null,null,null,null,null,null,null,false);
    }

        public RetailTicketLineInfo(ProductInfoExt product, double dMultiply, double dPrice,  java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,RetailTicketInfo m_oTicket,JRetailTicketLines m_ticketlines,JRetailPanelTicket jRetailPanel, TaxInfo tax, double discountValue,String pName,String productType,String productionAreaType,Properties attributes,String addonId,int primaryAddon,String instruction,int kotid,Date kotdate,String kottable,String kotuser,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid,TaxInfo sbTax) {
    //System.out.println("RetailTicketLineInfo 6"+"test addon"+addonId);
        String pid;
        double priceDiscount = 0;
        this.productType = productType;
         this.productionAreaType=productionAreaType;
         this.catId=parentCat;
         this.preparationTime=preparationTime;
         this.sosorderLineid=sosorderLineid;
         this.productionArea=productionArea;
        if (product == null) {
            pid = null;
        } else {
            pid = product.getID();
         
            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }
            
        setvalues(promoRule,dlSales,m_oTicket,m_ticketlines,jRetailPanel);
       
        }
        init(pid, null, dMultiply, dPrice,promoRule,dlSales,m_ticketlines,m_oTicket,jRetailPanel,tax,discountValue,pName,productType,productionAreaType, attributes,addonId,primaryAddon,instruction,kotid,kotdate,kottable,kotuser,charge,servicetax,parentCat,preparationTime,sosorderLineid,sbTax,false);
    }
       public RetailTicketLineInfo(ProductInfoExt product, double dMultiply, double dPrice,  java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,RetailTicketInfo m_oTicket,JRetailTicketLines m_ticketlines,JRetailPanelTakeAway jRetailPanel, TaxInfo tax, double discountValue,String pName,String productType,String productionAreaType,Properties attributes,String addonId,int primaryAddon,String instruction,int kotid,Date kotdate,String kottable,String kotuser,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid,TaxInfo sbTax,String mainBogo) {
    //System.out.println("RetailTicketLineInfo 6"+"test addon"+addonId);
        String pid;
        double priceDiscount = 0;
        this.productType = productType;
         this.productionAreaType=productionAreaType;
         this.catId=parentCat;
         this.preparationTime=preparationTime;
         this.sosorderLineid=sosorderLineid;
         this.productionArea=productionArea;
        if (product == null) {
            pid = null;
        } else {
            pid = product.getID();

            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }
//            setPromoRuleId(promoRule);
//            System.out.println("promoRule----init---"+promoRule.get(0).getpromoRuleId());
        setvalues(promoRule,dlSales,m_oTicket,m_ticketlines,jRetailPanel);

        }
        initTakeAway(pid, null, dMultiply, dPrice,promoRule,dlSales,m_ticketlines,m_oTicket,jRetailPanel,tax,discountValue,pName,productType,productionAreaType, attributes,addonId,primaryAddon,instruction,kotid,kotdate,kottable,kotuser,charge,servicetax,parentCat,preparationTime,sosorderLineid,sbTax,mainBogo);
    }
    public RetailTicketLineInfo(ProductInfoExt product, double dMultiply, double dPrice,  java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,RetailTicketInfo m_oTicket,JRetailTicketLines m_ticketlines,JRetailPanelHomeTicket jRetailPanel, TaxInfo tax, double discountValue,String pName,Properties attributes) {
  //System.out.println("RetailTicketLineInfo 7");
        String pid;
        double priceDiscount = 0;

        if (product == null) {
            pid = null;
        } else {
            pid = product.getID();

            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }

        setvalues(promoRule,dlSales,m_oTicket,m_ticketlines,jRetailPanel);

        }
        initHome(pid, null, dMultiply, dPrice,promoRule,dlSales,m_ticketlines,m_oTicket,jRetailPanel,tax,discountValue,pName, attributes);
    }
     public RetailTicketLineInfo(ProductInfoExt product, double dMultiply, double dPrice,  java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,RetailTicketInfo m_oTicket,JRetailTicketLines m_ticketlines,JRetailPanelEditTicket jRetailPanel, TaxInfo tax, double discountValue,String pName,Properties attributes) {
 // System.out.println("RetailTicketLineInfo 8");
        String pid;
        double priceDiscount = 0;

        if (product == null) {
            pid = null;
        } else {
            pid = product.getID();

            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }

        setvalues(promoRule,dlSales,m_oTicket,m_ticketlines,jRetailPanel);

        }
        initEdit(pid, null, dMultiply, dPrice,promoRule,dlSales,m_ticketlines,m_oTicket,jRetailPanel,tax,discountValue,pName, attributes);
    }
    private void setvalues(ArrayList<PromoRuleIdInfo> promoRule, DataLogicSales dlSales, RetailTicketInfo m_oTicket, JRetailTicketLines m_ticketlines, JRetailPanelTicket jRetailPanel) {
   //   System.out.println("RetailTicketLineInfo 9");
            this.promoRuleIdList = promoRule;
            this.dlSales = dlSales;
            this.m_oTicket = m_oTicket;
            this.m_ticketlines = m_ticketlines;
            this.jRetailPanel = jRetailPanel;
    }
    private void setvalues(ArrayList<PromoRuleIdInfo> promoRule, DataLogicSales dlSales, RetailTicketInfo m_oTicket, JRetailTicketLines m_ticketlines, JRetailPanelTakeAway jRetailPanel) {
   //   System.out.println("RetailTicketLineInfo 9");
            this.promoRuleIdList = promoRule;
            this.dlSales = dlSales;
            this.m_oTicket = m_oTicket;
            this.m_ticketlines = m_ticketlines;
            this.jRetailTakeAwayPanel = jRetailPanel;
    }
 private void setvalues(ArrayList<PromoRuleIdInfo> promoRule, DataLogicSales dlSales, RetailTicketInfo m_oTicket, JRetailTicketLines m_ticketlines, JRetailPanelHomeTicket jRetailPanel) {
       //   System.out.println("RetailTicketLineInfo 10");
            this.promoRuleIdList = promoRule;
            this.dlSales = dlSales;
            this.m_oTicket = m_oTicket;
            this.m_ticketlines = m_ticketlines;
            this.jRetailHomePanel = jRetailPanel;
    }
 private void setvalues(ArrayList<PromoRuleIdInfo> promoRule, DataLogicSales dlSales, RetailTicketInfo m_oTicket, JRetailTicketLines m_ticketlines, JRetailPanelEditTicket jRetailPanel) {
         //    System.out.println("RetailTicketLineInfo 11");
            this.promoRuleIdList = promoRule;
            this.dlSales = dlSales;
            this.m_oTicket = m_oTicket;
            this.m_ticketlines = m_ticketlines;
            this.jRetailEditPanel = jRetailPanel;
    }
   

    public RetailTicketLineInfo(RetailTicketLineInfo line) {
      // System.out.println("RetailTicketLineInfo 13"+line.getIsKot());
        init(line.productid, line.attsetinstid, line.multiply, line.price,line.promoRuleIdList,line.dlSales,line.m_ticketlines, line.m_oTicket,line.jRetailPanel,line.tax,line.discountValue,line.pName, line.productType, line.productionAreaType,(Properties) line.attributes.clone(),line.getPreparationStatus(),line.getIsKot(),line.getTbl_orderId(),line.getAddonId(),line.getPrimaryAddon(),line.getInstruction(),line.getKotid(),line.getKotdate(),line.getKottable(),line.getKotuser(),line.charge,line.servicetax,line.catId,line.getKdsPrepareStatus(),line.getPreparationTime(),line.getSosorderLineid(),line.sbtax,line.getProductionArea());
    }

    private void init(String productid, String attsetinstid, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,JRetailTicketLines m_ticketlines,RetailTicketInfo m_oTicket,JRetailPanelTicket jRetailPanel,TaxInfo tax, double discountValue,String pName,String productType,String productionAreaType, Properties attributes,String addonId,int primaryAddon,String instruction,int kotid,Date kotdate,String kottable,String kotuser,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid,TaxInfo sbTax,boolean buyone) {
     //   System.out.println("RetailTicketLineInfo 14");
        this.productid = productid;
        this.setProductionAreaType(productionAreaType);
        this.preparationTime=preparationTime;
        this.sosorderLineid=sosorderLineid;
        this.catId=parentCat;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        promoRuleIdList = promoRule;
        this.dlSales = dlSales;
        this.m_ticketlines = m_ticketlines;
        this.m_oTicket = m_oTicket;
        this.jRetailPanel = jRetailPanel;
        this.tax = tax;
        this.attributes = attributes;
        if(m_oTicket!=null){
        this.discountValue = Double.parseDouble(m_oTicket.getRate()) * 100;
        }else{
         this.discountValue=0;   
        }
        this.pName = pName;
        this.productType = productType;
        m_sTicket = null;
        m_iLine = -1;
        this.addonId=addonId;
        this.primaryAddon=primaryAddon;
        this.instruction=instruction;
        this.kotid=kotid;
        this.kotdate=kotdate;
        this.kottable=kottable;
        this.kotuser=kotuser;
        this.charge=charge;
        this.servicetax=servicetax;
        this.sbtax=sbTax;
        this.actualPrice=dPrice;
        if(buyone){
            actualPrice=0;
        }
       
     }
    private void initTakeAway(String productid, String attsetinstid, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,JRetailTicketLines m_ticketlines,RetailTicketInfo m_oTicket,JRetailPanelTakeAway jRetailPanel,TaxInfo tax, double discountValue,String pName,String productType,String productionAreaType, Properties attributes,String addonId,int primaryAddon,String instruction,int kotid,Date kotdate,String kottable,String kotuser,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String preparationTime,String sosorderLineid,TaxInfo sbTax,String mainBogo) {
     //   System.out.println("RetailTicketLineInfo 14");
        this.productid = productid;
        this.setProductionAreaType(productionAreaType);
        this.preparationTime=preparationTime;
        this.sosorderLineid=sosorderLineid;
        this.catId=parentCat;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        this.promoRuleIdList = promoRule;
        this.dlSales = dlSales;
        this.m_ticketlines = m_ticketlines;
        this.m_oTicket = m_oTicket;
        this.jRetailTakeAwayPanel = jRetailPanel;
        this.tax = tax;
        this.attributes = attributes;
        if(m_oTicket!=null){
        this.discountValue = Double.parseDouble(m_oTicket.getRate()) * 100;
        }else{
         this.discountValue=0;
        }
        this.pName = pName;
        this.productType = productType;
        m_sTicket = null;
        m_iLine = -1;
        this.addonId=addonId;
        this.primaryAddon=primaryAddon;
        this.instruction=instruction;
        this.kotid=kotid;
        this.kotdate=kotdate;
        this.kottable=kottable;
        this.kotuser=kotuser;
        this.charge=charge;
        this.servicetax=servicetax;
        this.sbtax=sbTax;
        this.isBogoMain=mainBogo;
       
     }
    //newly added
    private void init(String productid, String attsetinstid, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,JRetailTicketLines m_ticketlines,RetailTicketInfo m_oTicket,JRetailPanelTicket jRetailPanel,TaxInfo tax, double discountValue,String pName,String productType,String productionAreaType, Properties attributes,int prepStatus,int kot,String tbl_orderId,String addonId,int primaryAddon,String instruction,int kotid,Date kotdate,String kottable,String kotuser,ServiceChargeInfo charge,TaxInfo servicetax,String parentCat,String kdsPrepareStatus,String preparationTime,String sosorderLineid,TaxInfo sbTax, String productionArea) {
     //    System.out.println("RetailTicketLineInfo 15"+kot);
        this.productid = productid;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        promoRuleIdList = promoRule;
        this.dlSales = dlSales;
        this.m_ticketlines = m_ticketlines;
        this.m_oTicket = m_oTicket;
        this.jRetailPanel = jRetailPanel;
        this.tax = tax;
        this.attributes = attributes;
        this.discountValue = Double.parseDouble(m_oTicket.getRate()) * 100;
        this.pName = pName;
        this.productType = productType;
        this.setProductionAreaType(productionAreaType);
        this.preparationTime=preparationTime;
        this.catId=parentCat;
        m_sTicket = null;
        m_iLine = -1;
        this.preparationStatus=prepStatus;
        this.isKot=kot;
        this.tbl_orderId=tbl_orderId;
        this.addonId=addonId;
        this.primaryAddon=primaryAddon;
        this.instruction=instruction;
        this.kotid=kotid;
        this.kotdate=kotdate;
        this.kottable=kottable;
        this.kotuser=kotuser;
        this.charge=charge;
        this.servicetax=servicetax;
        this.kdsPrepareStatus=kdsPrepareStatus;
        this.sosorderLineid=sosorderLineid;
        this.sbtax=sbTax;
         this.productionArea=productionArea;
    }
     private void initHome(String productid, String attsetinstid, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,JRetailTicketLines m_ticketlines,RetailTicketInfo m_oTicket,JRetailPanelHomeTicket jRetailPanel,TaxInfo tax, double discountValue,String pName, Properties attributes) {
       //  System.out.println("RetailTicketLineInfo 16");
         this.productid = productid;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        promoRuleIdList = promoRule;
        this.dlSales = dlSales;
        this.m_ticketlines = m_ticketlines;
        this.m_oTicket = m_oTicket;
        this.jRetailHomePanel = jRetailPanel;
        this.tax = tax;
        this.attributes = attributes;
        this.discountValue = Double.parseDouble(m_oTicket.getRate()) * 100;
        this.pName = pName;
        this.preparationStatus = preparationStatus;
        m_sTicket = null;
        m_iLine = -1;
         this.charge=charge;
         this.servicetax=servicetax;
         this.kdsPrepareStatus=kdsPrepareStatus;
         this.sosorderLineid=sosorderLineid;
    

    }
      private void initEdit(String productid, String attsetinstid, double dMultiply, double dPrice, java.util.ArrayList<PromoRuleIdInfo> promoRule,DataLogicSales dlSales,JRetailTicketLines m_ticketlines,RetailTicketInfo m_oTicket,JRetailPanelEditTicket jRetailPanel,TaxInfo tax, double discountValue,String pName, Properties attributes) {
       //  System.out.println("RetailTicketLineInfo 17");
        this.productid = productid;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        promoRuleIdList = promoRule;
        this.dlSales = dlSales;
        this.m_ticketlines = m_ticketlines;
        this.m_oTicket = m_oTicket;
        this.jRetailEditPanel = jRetailPanel;
        this.tax = tax;
        this.attributes = attributes;
        this.discountValue = Double.parseDouble(m_oTicket.getRate()) * 100;
        this.pName = pName;
        m_sTicket = null;
        m_iLine = -1;
          this.charge=charge;
         this.servicetax=servicetax;
         

    }
public String setPromotionType(String id){
  //   System.out.println("RetailTicketLineInfo 18");
    int productqty=0;
    productqty =(int)getQty();
    java.util.ArrayList<String> promoId = new ArrayList<String>();
    if(promoRuleIdList!=null){

    for(int i=0;i<getPromotionRule().size();i++){
         promoId.add("'"+getPromotionRule().get(i).getpromoRuleId()+"'");
     }
     StringBuilder b = new StringBuilder();
     Iterator<?> it = promoId.iterator();
     while (it.hasNext()) {
     b.append(it.next());
     if (it.hasNext()) {
        b.append(',');
      }
    }
    String promoRuleId = b.toString();
      System.out.println("promoruleid======setpromo"+promoRuleId);
    int productCount = 0;
    try {
        productCount = dlSales.getProductCount(id, promoRuleId);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    if(productCount!=0){
        try {
            pdtpromoType = dlSales.getPromoType(id, promoRuleId);
        } catch (BasicException ex) {
            Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
   }

        return pdtpromoType;
}

 public double setDiscountValue(String id){
      // System.out.println("RetailTicketLineInfo 19");
    int productqty=0;
    productqty =(int)getQty();


    java.util.ArrayList<String> promoId = new ArrayList<String>();
     double productDiscount = 0;
 if(promoRuleIdList!=null){

     for(int i=0;i<promoRuleIdList.size();i++){
         promoId.add("'"+promoRuleIdList.get(i).getpromoRuleId()+"'");
     }
     StringBuilder b = new StringBuilder();
     Iterator<?> it = promoId.iterator();
     while (it.hasNext()) {
     b.append(it.next());
     if (it.hasNext()) {
        b.append(',');
      }
    }
         String promoRuleId = b.toString();

   // m_oTicket.setPromotionRule(promoRuleId);
    int productCount = 0;
    String promoType=null;
    String promoTypeId= null;
    int priceOffCount =0;
    int percentageOffCount = 0;
    String isPrice;
    String isPromoProduct;
    try {
        productCount = dlSales.getProductCount(id, promoRuleId);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
   
   
    if(productCount!=0){
        try {
            promoType = dlSales.getPromoType(id, promoRuleId);
        } catch (BasicException ex) {
            Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
          System.out.println("promotype"+promoType);
        if(promoType.equals("PDPrice")){
            isPrice = "Y";
            isPromoProduct = "Y";
            productDiscount = getPriceoffDiscount(id,isPrice,isPromoProduct,promoRuleId,productqty);
            
        }else if(promoType.equals("PDPercent")){
            isPrice = "N";
            isPromoProduct = "Y";

            productDiscount = getPercentageoffDiscount(id,isPrice,isPromoProduct,promoRuleId,productqty);
            promodiscountPercent= getPromoRule().get(0).getValue();
        }//includes buy get promotion and least value
        else if(promoType.equals("SIBG")){
            isPrice = "N";
            isPromoProduct = "Y";
            getBuyGetDiscount(id,isPrice,isPromoProduct,promoRuleId,productqty);
            productDiscount=0;
            
        }else if(promoType.equals("BOGO")){
       //   isPrice = "N";
          //  isPromoProduct = "N";
            getBogoDiscount(id,promoRuleId,productqty);
            productDiscount=0;
        }
    }
 }
  return productDiscount;
    }
 
 public void getBogoDiscount(String id, String promoRuleId, int productqty){
     try {
          promoRuleList = (ArrayList<PromoRuleInfo>) dlSales.getBogoPromoRuleDetails(promoRuleId,id);
     //   isBogoMain= promoRuleList.get(0).getMainBogo();
     } catch (BasicException ex) {
                Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
 }
 public double getPriceoffDiscount(String id,String isPrice, String isPromoProduct, String promoRuleId, int productqty){
  //    System.out.println("RetailTicketLineInfo 20");
     int priceOffCount =0;
      String promoTypeId = null;
      int productDiscount=0;
        try {
            promoTypeId = dlSales.getPromoTypeId(id);
        } catch (BasicException ex) {
            Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            priceOffCount = dlSales.getPriceOffCount(promoTypeId, promoRuleId,id);
        } catch (BasicException ex) {
            Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
             promoRuleList = (ArrayList<PromoRuleInfo>) dlSales.getPromoRuleDetails(promoRuleId, isPrice, isPromoProduct,id);
        } catch (BasicException ex) {
                Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
      if(promoRuleList.get(0).getBuyQty()==0){
           int discountValue=0;
            try {
                productDiscount = dlSales.getRangePriceOffValue(id, promoRuleId, (int)productqty);
            } catch (BasicException ex) {
                Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
           if(productDiscount==0){
                try {
                productDiscount = dlSales.getMaxRangePriceOffValue(id, promoRuleId);
            } catch (BasicException ex) {
                Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
           }

      }else{
        if(priceOffCount==1){
             int result = 0;
             result =  (int)(productqty / promoRuleList.get(0).getBuyQty()) * (int)promoRuleList.get(0).getValue();
             productDiscount = productDiscount + result;
        }else{
             for (PromoRuleInfo pp : promoRuleList) {
                  int result = 0;
                  result =  (int)(productqty / pp.getBuyQty()) * (int)pp.getValue();
                  productDiscount = productDiscount + result;

                  int remaining = 0;
                  remaining = productqty % (int)pp.getBuyQty();

                  if (remaining == 0) {
                    break;
                  } else {
                    productqty = remaining;
                  }

                }
              }
      }
     return productDiscount;
 }
 public double getPercentageoffDiscount(String id,String isPrice, String isPromoProduct, String promoRuleId, int productqty){
    //  System.out.println("RetailTicketLineInfo 21");
     int perOffCount =0;
    String promoTypeId = null;
    double productDiscount=0;
    try {
        promoTypeId = dlSales.getPromoTypeId(id);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
        perOffCount = dlSales.getPercentageOffCount(promoTypeId, promoRuleId,id);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
            promoRuleList = (ArrayList<PromoRuleInfo>) dlSales.getPromoRuleDetails(promoRuleId, isPrice, isPromoProduct,id);
        } catch (BasicException ex) {
            Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
     if(promoRuleList.get(0).getBuyQty()==0){
         int discountPercentage = 0;
            try {
                discountPercentage = dlSales.getRangePerOffValue(id, promoRuleId, (int)productqty);
            } catch (BasicException ex) {
                Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
            }

           if(discountPercentage==0){
                try {
                discountPercentage = dlSales.getMaxRangePerOffValue(id, promoRuleId);
            } catch (BasicException ex) {
                Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
           }
          productDiscount = productqty* (price* discountPercentage/100);
     }else{
        if(perOffCount==1){
                 double result = 0;
                 result =  (int)(productqty / promoRuleList.get(0).getBuyQty()) * (price* promoRuleList.get(0).getValue()/100);
                 productDiscount = productDiscount + result;
        }else{
             for (PromoRuleInfo pp : promoRuleList) {
                  double result = 0;
                  int remaining = 0;
                  remaining = productqty % (int)pp.getBuyQty();
                  result = (price * (productqty-remaining)* pp.getValue()/100);
                  productDiscount = productDiscount + result;

                  if (remaining == 0) {
                    break;
                  } else {
                    productqty = remaining;
                  }

                }
              }
     }
     return productDiscount;
 }

  public void getBuyGetDiscount(String id,String isPrice, String isPromoProduct, String promoRuleId, int productqty){

 // System.out.println("RetailTicketLineInfo 22");
    int buyGetCount =0;
    String promoTypeId = null;
    double productDiscount=0;
    try {
        promoTypeId = dlSales.getPromoTypeId(id);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
        buyGetCount = dlSales.getBuyGetCount(promoTypeId, promoRuleId,id);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    try {
        promoRuleList = (ArrayList<PromoRuleInfo>) dlSales.getPromoRuleDetails(promoRuleId, isPrice, isPromoProduct,id);
    } catch (BasicException ex) {
        Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
    }
    String promoTypeName = null;
     int index;
     if(getButton()==2){
      index = getIndex();
     }else{
         index = m_ticketlines.getSelectedIndex();
     }
    promoTypeName = setPromotionType(getProductID());
    double freeqty=0;
  
    if(buyGetCount!=0){

             for (PromoRuleInfo pp : promoRuleList) {
              double result = 0;
              int remaining = 0;
              result =(int)(productqty / pp.getBuyQty()) * (int)pp.getQty();
              freeqty = freeqty + result;
              remaining = (int)getQty() % (int)pp.getBuyQty();
              if (remaining == 0) {
                break;
              } else {
                productqty = remaining;
              }

          }

       if(promoRuleList.get(0).getIsSku().equals("Y")){
          if(freeqty!=0){
               
              RetailTicketLineInfo newLine = new RetailTicketLineInfo(
              getProductID(),getProductName(),getProductTaxCategoryID(),freeqty, getPrice(), promoRuleIdList,dlSales,m_oTicket, m_ticketlines,jRetailPanel, getTaxInfo(),0.0,getProductName(),getProductType(),getProductionAreaType(),getChargeInfo(),getServiceTaxInfo(),getCatId(),getPreparationTime(),getSosorderLineid(),getSBTaxInfo(),true);
              newLine.setPromoType(promoTypeName);
              newLine.setPromoRule(promoRuleList);
//            m_oTicket.insertLine(index + 1, newLine);
              newLine.setPromodiscountPercent(100);
              newLine.setOfferDiscount(getPrice());
              m_oTicket.addLine(newLine);
              m_ticketlines.addTicketLine(newLine);
              jRetailPanel.setSelectedIndex(index + 1);

              jRetailPanel.refreshTicket();

          }
        }else{

          String promoPdtId;
          try {
              promoPdtId = dlSales.getPdtPromoRuleId(productid);
              crossproductList = (ArrayList<CrossProductInfo>) dlSales.getCrossProductDetails(promoPdtId);
          } catch (BasicException ex) {
              Logger.getLogger(RetailTicketLineInfo.class.getName()).log(Level.SEVERE, null, ex);
          }
          if(freeqty!=0){
              if(crossproductList.size()==1){
                  RetailTicketLineInfo newLine = new RetailTicketLineInfo(
                  crossproductList.get(0).getProductId(),crossproductList.get(0).getProductName(),getProductTaxCategoryID(),freeqty, getPrice(),promoRuleIdList,dlSales,m_oTicket, m_ticketlines,jRetailPanel, getTaxInfo(),0,getProductName(),getProductType(),getProductionAreaType(),getChargeInfo(),getServiceTaxInfo(),getCatId(),getPreparationTime(),getSosorderLineid(),getSBTaxInfo(),false);
                  newLine.setPromoType(promoTypeName);
                  newLine.setPromoRule(promoRuleList);
//                  m_oTicket.insertLine(index + 1, newLine);
                  m_oTicket.addLine(newLine);
                  m_ticketlines.addTicketLine(newLine);
                  jRetailPanel.setSelectedIndex(index+1);
                  jRetailPanel.refreshTicket();
              }else{
                  for(int i=0;i<crossproductList.size();i++){
                      RetailTicketLineInfo newLine = new RetailTicketLineInfo(
                      crossproductList.get(i).getProductId(),crossproductList.get(i).getProductName(),getProductTaxCategoryID(),freeqty, getPrice(), promoRuleIdList,dlSales,m_oTicket, m_ticketlines,jRetailPanel,getTaxInfo(),0,getProductName(),getProductType(),getProductionAreaType(),getChargeInfo(),getServiceTaxInfo(),getCatId(),getPreparationTime(),getSosorderLineid(),getSBTaxInfo(),false);
                      newLine.setPromoType(promoTypeName);
                      newLine.setPromoRule(promoRuleList);
                      m_oTicket.insertLine(index + i, newLine);
                      jRetailPanel.setSelectedIndex(index+i);
                      jRetailPanel.refreshTicket();
                 }
              }
           }
        }
     }
  }

 public void setTicketId(String ticketId){
    this.ticketId = ticketId;
 }
 public String getTicketId(){
     return ticketId;
 }
 public void setLineId(String lineId){
    this.lineId = lineId;
 }
 public String getLineId(){
     return lineId;
 }

 public void setLineNo(int lineNo){
    this.lineNo = lineNo;
 }
 public int getLineNo(){
     return lineNo;
 }
 public void setQty(double qty){
     this.qty = qty;
 }

 public double getQty(){
     return qty;
 }

  public void setIndex(int index1){
     this.index1 = index1;
 }

 public int getIndex(){
     return index1;
 }

 public void setticketLine(RetailTicketInfo m_oTicket) {
     this.m_oTicket = m_oTicket;
 }
 
 public RetailTicketInfo getticketLine(){
     return m_oTicket;
 }
 public void setPanel(JRetailPanelTicket panel) {
     this.jRetailPanel = panel;
 }
  public JRetailPanelTicket getPanel(){
     return jRetailPanel;
 }
   public void setRetailPanel(JRetailPanelTicket jRetailPanel) {
     this.jRetailPanel = jRetailPanel;
 }
  public JRetailPanelTicket getRetailPanel(){
     return jRetailPanel;
 }
    public void setRetailTakeAwayPanel(JRetailPanelTakeAway jRetailTakeAwayPanel) {
     this.jRetailTakeAwayPanel = jRetailTakeAwayPanel;
 }
  public JRetailPanelTakeAway getRetailTakeAwayPanel(){
     return jRetailTakeAwayPanel;
 }
     public void setRetailEditPanel(JRetailPanelEditTicket jRetailEditPanel) {
     this.jRetailEditPanel = jRetailEditPanel;
 }
  public JRetailPanelEditTicket getRetailEditPanel(){
     return jRetailEditPanel;
 }
    public void setRetailHomePanel(JRetailPanelHomeTicket jRetailPanel) {
     this.jRetailHomePanel = jRetailPanel;
 }
  public JRetailPanelHomeTicket getRetailHomePanel(){
     return jRetailHomePanel;
 }
 public java.util.ArrayList<PromoRuleIdInfo> getPromoList(){
      return promoRuleIdList;
  }

 public void setPromoList(java.util.ArrayList<PromoRuleIdInfo> promoRuleIdList){
     this.promoRuleIdList = promoRuleIdList;
 }

 public java.util.ArrayList<PromoRuleInfo> getPromoRule(){
     return promoRuleList;
 }

 public void setPromoRule(java.util.ArrayList<PromoRuleInfo> promoRuleList){
     this.promoRuleList = promoRuleList;
 }
 public void setJTicketLines(JRetailTicketLines m_ticketlines) {
     this.m_ticketlines = m_ticketlines;
 }
 public JRetailTicketLines getJTicketLines(){
    return m_ticketlines;
 }
 public void setDatalogic(DataLogicSales dlSales) {
    this.dlSales = dlSales;
 }
 public DataLogicSales getDatalogic(){
     return dlSales;
 }
 public int getButton(){
     return buttonPlus;
 }
  public void setButton(int buttonPlus){
     this.buttonPlus = buttonPlus;
 }
 public void setPromoType(String pdtpromoType){
     this.pdtpromoType = pdtpromoType;
 }
 public String getPromoType(){
     return pdtpromoType==null?"":pdtpromoType;
 }
//  public void setProdType(String pdtType){
//     this.pdtType = pdtType;
// }
// public String getProdType(){
//     return pdtType==null?"":pdtType;
// }
  public void setTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_sTicket);
        dp.setInt(2, new Integer(m_iLine));
        dp.setString(3, productid);
        dp.setString(4, attsetinstid);

        dp.setDouble(5, new Double(multiply));
        dp.setDouble(6, new Double(price));

        dp.setString(7, tax.getId());
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            attributes.storeToXML(o, AppLocal.APP_NAME, "UTF-8");
            dp.setBytes(8, o.toByteArray());
        } catch (IOException e) {
            dp.setBytes(8, null);
        }
        dp.setDouble(9, new Double(discountValue));
        dp.setString(10, pName);
        dp.setInt(11, preparationStatus);
        dp.setInt(12, isKot);
        dp.setString(13, tbl_orderId);
        dp.setString(14, addonId); 
        dp.setInt(15, primaryAddon);
        dp.setString(16, instruction);
        dp.setInt(17, kotid);
        dp.setTimestamp(18, kotdate);
        dp.setString(19, kottable);
        dp.setString(20, kotuser);
         dp.setString(21, charge.getId());
    }

    public void readValues(DataRead dr) throws BasicException {
        m_sTicket = dr.getString(1);
        m_iLine = dr.getInt(2).intValue();
        productid = dr.getString(3);
        attsetinstid = dr.getString(4);

        multiply = dr.getDouble(5);
        price = dr.getDouble(6);
       
       tax = new TaxInfo(dr.getString(7), dr.getString(8), dr.getString(9), dr.getString(10), dr.getString(11), dr.getDouble(12), dr.getBoolean(13), dr.getInt(14), dr.getString(15), dr.getString(16),dr.getString(17), dr.getString(18),dr.getString(19),dr.getString(20),null,null,null);
        charge=new ServiceChargeInfo(dr.getString(21), dr.getString(8), dr.getDouble(12));
        attributes = new Properties();
        try {
            byte[] img = dr.getBytes(20);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        discountValue = dr.getDouble(21);
        pName = dr.getString(22);
        preparationStatus = dr.getInt(23);
        isKot=dr.getInt(12);
        tbl_orderId=dr.getString(13);
        addonId=dr.getString(14);
        primaryAddon=dr.getInt(15);
        instruction=dr.getString(16);
        kotid=dr.getInt(17);
        kotdate=dr.getTimestamp(18);
        kottable=dr.getString(19);
        kotuser=dr.getString(20);
    }

    public RetailTicketLineInfo copyTicketLine() {
        RetailTicketLineInfo l = new RetailTicketLineInfo();
        // l.m_sTicket = null;
        // l.m_iLine = -1;
        l.productid = productid;
        l.attsetinstid = attsetinstid;
        l.multiply = multiply;
        l.price = price;
       
        l.tax = tax;
         l.charge=charge;
        l.servicetax=servicetax;
        l.attributes = (Properties) attributes.clone();
        l.discountValue = discountValue;
        l.pName = pName;
        l.productType = productType;
        l.productionAreaType = productionAreaType;
        l.catId=catId;
        l.isKot=isKot;
        l.preparationStatus=preparationStatus;
        l.addonId=addonId;
        l.primaryAddon=primaryAddon;
        l.instruction=instruction;
        l.kotid=kotid;
        l.kotdate=kotdate;
        l.kottable=kottable;
        l.kotuser=kotuser;
        l.kdsPrepareStatus=kdsPrepareStatus;
        l.preparationTime=preparationTime;
        l.sosorderLineid=sosorderLineid;
        l.sbtax=sbtax;
         //added newly for erp tax
        l.m_oTicket=m_oTicket;
        l.discountrate=discountrate;
        //l.tbl_orderId=tbl_orderId;
        return l;
    }

    public int getTicketLine() {
        return m_iLine;
    }
     public int getTicketType() {
        return ticketType;
    }
     public void setTicketType(int ticketType){
         this.ticketType = ticketType;
     }

    public String getProductID() {
        return productid;
    }
    public void setProductId(String productid){
        this.productid = productid;
    }
     public String getDuplicateProductName() {
    return pName;
  }
     public void setDuplicateName(String pName){
         this.pName = pName;
     }

    public String getProductName() {
        return attributes.getProperty("product.name");
    }

    public String getProductAttSetId() {
        return attributes.getProperty("product.attsetid");
    }

    public String getProductAttSetInstDesc() {
        return attributes.getProperty("product.attsetdesc", "");
    }

    public void setProductAttSetInstDesc(String value) {
        if (value == null) {
            attributes.remove(value);
        } else {
            attributes.setProperty("product.attsetdesc", value);
        }
    }

    public int getPreparationStatus() {
        return preparationStatus;
    }

    public void setPreparationStatus(int preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    public String getProductAttSetInstId() {
        return attsetinstid;
    }

    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }

    public boolean isProductCom() {
        return "true".equals(attributes.getProperty("product.com"));
    }

    public String getProductTaxCategoryID() {
        return (attributes.getProperty("product.taxcategoryid"));
    }

    public String getProductCategoryID() {
        return (attributes.getProperty("product.categoryid"));
    }
  public String getProductType() {
        return productType;
    }
  public void setProductType(String dValue) {
        productType = dValue;
    }

    public double getMultiply() {
        return multiply;
    }

    public void setMultiply(double dValue) {
        multiply = dValue;
    }
    public double getFreeMultiply() {
        return freemultiply;
    }

    public void setFreeMultiply(double dValue) {
        freemultiply = dValue;
    }
    public double getPrice() {
        return price;
    }
    
//  public double getDiscount() {
//     double discountPercent = 0;
//        if(m_oTicket.getSplitValue().equals(null)){
//             discountPercent = m_oTicket.convertRatetodouble(m_oTicket.getRate()) * 100;
//        }else{
//             discountPercent = RetailTicketInfo.convertRatetodouble(RetailTicketInfo.getRate()) * 100;
//        }
//        return discountPercent;
//    }
  
   public double getDiscount() {
        String promoDiscount=m_oTicket.getM_App().getProperties().getProperty("machine.promodiscount");
          if(promoDiscount.equals("true"))  {
              return discountValue;
          }else{
              //only promo discount applicable not the normal discount(if promotion exists)
               if(getCampaignId().equals("")){ 
                    return discountValue;
               }else{
                   return 0;
               }
          }
    }
    public void setDiscount(double discount) {
        this.discountValue = discount;

    }


   public void setPrice(double dValue) {
        price = dValue;
    }

    public double getPriceTax() {
        return price * (1.0 + getTaxRate());
    }

    public void setPriceTax(double dValue) {
        price = dValue / (1.0 + getTaxRate());
    }

    public TaxInfo getTaxInfo() {
        return tax;
    }

    public void setTaxInfo(TaxInfo value) {
        tax = value;
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return attributes;
    }

    public double getTaxRate() {
        return tax == null ? 0.0 : tax.getRate();
    }
    public double getTaxValue() {
        return taxRate;
    }
    public void setTaxValue(double taxRate) {
        this.taxRate = taxRate;
    }

    public java.util.ArrayList<PromoRuleIdInfo> getPromotionRule() {
      return promoRuleIdList;
    }

    public void setPromotionRule(java.util.ArrayList<PromoRuleIdInfo> l) {
      promoRuleIdList = l;
    }
//    public double getSubValue() {
//        double value=0;
//        if(RetailTicketInfo.getRate().equals("0")||RetailTicketInfo.getRate().equals("")){
//              value= (price * multiply);
//        }else{
//              value= (price - (price * (RetailTicketInfo.convertRatetodouble(RetailTicketInfo.getRate())))) * multiply;
//        }
//        
//        return value;
//    }
    

     public double getSubValue() {
        double value=0;
        if(m_oTicket.getRate().equals("0")||m_oTicket.getRate().equals("")){
              value= (price * multiply)-getOfferDiscount();
        }
        else{
            //  value= (price - (price * getDiscount() * 0.01)) * multiply;
            if(getCampaignId().equals("")){
            value= (price - (price * getDiscount() * 0.01)) * multiply;
            }else{
                       //       value= (price -( (price * getDiscount() * 0.01)+getOfferDiscount())) * multiply;
                if(getPromodiscountPercent()!=100){
                   value= ((price - (price * getDiscount() * 0.01))*multiply)-getOfferDiscount();
                 }
            }
        }
        
        return value;
    }
     public double getSubValueBeforeDiscount() {
        double value=0;
        value= (price * multiply);
        return value;
    }

    


   public double getDiscountValue() {
        double value;
        if(getTicketType()==0){
            value= (price * multiply) - getLineDiscountPrice();
        }else{
             value= (price * multiply) + getLineDiscountPrice();
        }
        return value;
    }

 
    public double getDiscountPrice() {
        //double value= getDiscountValue()/multiply;
       double value=getPrice()-(getLineDiscountPrice()+getOfferDiscount());
        return value;
    }
    

 public double getPriceAfterDiscount() {
   //     double value= price - (price * (RetailTicketInfo.convertRatetodouble(RetailTicketInfo.getRate())));
      double value = 0;
  if(m_oTicket.getSplitValue().equals(null)){
       value = price - (price * (m_oTicket.convertRatetodouble(m_oTicket.getRate())));
  }else{
       value= price - (price * (m_oTicket.convertRatetodouble(m_oTicket.getRate())));
  }
        return value;
    }
 
 //Calculation of discount irrespective with product category

 public double getLineDiscountPrice() {
      double value = 0;
      String promoDiscount=m_oTicket.getM_App().getProperties().getProperty("machine.promodiscount");
      boolean discountStatus=false;
       if(promoDiscount.equals("true"))  {
           discountStatus=true;
       }else{
            if(getCampaignId().equals("")){ 
                discountStatus=true;
            }
       }
        if(discountStatus){
       if(getPromodiscountPercent()!=100)     {
      if(m_oTicket.iscategoryDiscount()){
      value= (price * multiply)* (m_oTicket.convertRatetodouble(this.getDiscountrate()));
      }else{
         value= (price * multiply)* (m_oTicket.convertRatetodouble(m_oTicket.getRate()));
      
      }
       }
        }
      return value;
    }
    public double getTax() {
        double taxvalue;
//        System.out.println("ticketType getTax---"+ticketType);
        if(ticketType==0){
             taxvalue= price * multiply * getTaxRate();
        }else{
            taxvalue= price * multiply*getTaxValue();
        }
         return taxvalue;
    }
//      public double getTaxWithServiceCharge() {
//        double taxvalue = 0;
////        System.out.println("ticketType getTax---"+ticketType);
//        if(ticketType==0){
//            if(m_oTicket.getRate().equals("0") || m_oTicket.getRate().equals("")){
//             taxvalue= ((price * multiply)+((price * multiply)*RetailTicketInfo.getServiceChargeRate())) * getTaxRate();
//            }else{
//                double subTotaldiscountPrice = (price - (price * (m_oTicket.convertRatetodouble(m_oTicket.getRate()))))* multiply;
//               taxvalue= ((subTotaldiscountPrice)+((subTotaldiscountPrice)*RetailTicketInfo.getServiceChargeRate())) * getTaxRate();
//            }
//        }else{
//            taxvalue= ((price * multiply)+((price * multiply)*RetailTicketInfo.getServiceChargeRate())) *getTaxValue();
//        }
//         return taxvalue;
//    }
      

      public double getTaxWithServiceCharge() {
        double taxvalue = 0;
//        System.out.println("ticketType getTax---"+ticketType);
        if(ticketType==0){
            if(m_oTicket.getRate().equals("0") || m_oTicket.getRate().equals("")){
             taxvalue= ((price * multiply)+((price * multiply)*m_oTicket.getServiceChargeRate())) * getTaxRate();
            }else{
                double subTotaldiscountPrice = (price - (price * (m_oTicket.convertRatetodouble(m_oTicket.getRate()))))* multiply;
               taxvalue= ((subTotaldiscountPrice)+((subTotaldiscountPrice)*m_oTicket.getServiceChargeRate())) * getTaxRate();
            }
        }else{
            taxvalue= ((price * multiply)+((price * multiply)*m_oTicket.getServiceChargeRate())) *getTaxValue();
        }
         return taxvalue;
    }
      

    public double getValue() {
        return price * multiply * (1.0 + getTaxRate());
    }
    public String getCampaignId() {
        try{
            campaignId = getPromoRule().get(0).getCampaignId();
        }catch(NullPointerException ex){
            campaignId =null;
        }
        return campaignId == null?"":campaignId;
    }

     public void setCampaignId(String campaignId) {

        this.campaignId =campaignId;
    }
    public String getpromoId() {
        try{
            promoId = getPromoRule().get(0).getpromoId();
        }catch(NullPointerException ex){
            promoId =null;
        }
        return promoId == null?"":promoId;
    }
     public String getIsCrossProduct() {
        try{
            isCrossProduct = getPromoRule().get(0).getIsCrossProduct();
        }catch(NullPointerException ex){
            isCrossProduct =null;
        }
        return isCrossProduct == null?"":isCrossProduct;
    }
    public String printName() {
        String pdtName =StringUtils.encodeXML(attributes.getProperty("product.name"));
        if(pdtName==null){
            pdtName = pName.replace("&", "&amp;");
        }
        return pdtName;
    }
     public String printDuplicateName() {
        return getDuplicateProductName();
    }

    public String printMultiply() {
        return Formats.DOUBLE.formatValue(multiply);
    }

    public String printDiscount() {
        return Formats.DOUBLE.formatValue(getDiscount());
    }


    public String printPrice() {
        return Formats.CURRENCY.formatValue(getPrice());
    }
    public String printActualPrice() {
        return Formats.CURRENCY.formatValue(getActualPrice());
    }
 public String printPriceLine() {
        return Formats.DoubleValue.formatValue(getPrice());
    }
    public String printPriceTax() {
        return Formats.CURRENCY.formatValue(getPriceTax());
    }

    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }

    public String printTaxRate() {
        double taxRateValue;
         if(ticketType==0){
             taxRateValue= getTaxRate();
        }else{
            taxRateValue= getTaxValue();
        }
        return Formats.PERCENT.formatValue(taxRateValue);
    }

    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
          //return Formats.CURRENCY.formatValue(getSubValueBeforeDiscount());
    }
   //used in xml ticket.line (18/4/16) ??
 public String printActualSubValue() {
      return Formats.CURRENCY.formatValue(getActualPrice()*getMultiply());
    }
    //this method to show lines in printed bill pdf without discount
    public String printSubValueBeforeDiscount() {
        return Formats.CURRENCY.formatValue(getSubValueBeforeDiscount());
    }
    
   //newly called method for reprint settled bill option (by shilpa) 
//    public String rePrintSubValue() {
//        return Formats.CURRENCY.formatValue(getrePrintSubValue());
//          
//    }
//     public double getrePrintSubValue() {
//        double value=0;
//        
//        value= (price - (price * getDiscount() * 0.01)) * multiply;
//        return value;
//    }
    
 //this method to show lines in front end without discount   
 public String printDiscountValue() {
      return Formats.CURRENCY.formatValue(getSubValueBeforeDiscount());
    }
 public int printPrepareStatus(){
     return getPreparationStatus();
 }

    public String printValue() {
        return Formats.CURRENCY.formatValue(getValue());
    }
      public String printPromoType() {
          
       return getPromoType();
    }

  public String getCancelStatus() {
        return cancelStatus;
    }
  public void setCancelStatus(String dValue) {
        cancelStatus = dValue;
    }
  
//   public String getSplitParentId() {
//        return splitParentId;
//    }
//
//    /**
//     * @param splitParentId the splitParentId to set
//     */
//    public void setSplitParentId(String splitParentId) {
//        this.splitParentId = splitParentId;
//    }

    /**
     * @return the isKot
     */
    public int getIsKot() {
        return isKot;
    }

    /**
     * @param isKot the isKot to set
     */
    public void setIsKot(int isKot) {
        this.isKot = isKot;
    }

    /**
     * @return the tbl_orderId
     */
    public String getTbl_orderId() {
        return tbl_orderId;
    }

    /**
     * @param tbl_orderId the tbl_orderId to set
     */
    public void setTbl_orderId(String tbl_orderId) {
        this.tbl_orderId = tbl_orderId;
    }
    
    public static Comparator<RetailTicketLineInfo> productNameComparator = new Comparator<RetailTicketLineInfo>() {

        @Override
        public int compare(RetailTicketLineInfo l1, RetailTicketLineInfo l2) {
            String productName1 = l1.getProductName().toUpperCase();
            String productName2 = l2.getProductName().toUpperCase();
            
            return productName1.compareTo(productName2);
        }
    };

    
    /**
     * @return the addonId
     */
    public String getAddonId() {
        return addonId;
    }

    /**
     * @param addonId the addonId to set
     */
    public void setAddonId(String addonId) {
        this.addonId = addonId;
    }

    /**
     * @return the primaryAddon
     */
    public int getPrimaryAddon() {
        return primaryAddon;
    }

    /**
     * @param primaryAddon the primaryAddon to set
     */
    public void setPrimaryAddon(int primaryAddon) {
        this.primaryAddon = primaryAddon;
    }

    /**
     * @return the instruction
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * @param instruction the instruction to set
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    
    public String printInstruction() {
          
       return getInstruction();
    }

    /**
     * @return the productionAreaType
     */
    public String getProductionAreaType() {
        return productionAreaType;
    }

    /**
     * @param productionAreaType the productionAreaType to set
     */
    public void setProductionAreaType(String productionAreaType) {
        this.productionAreaType = productionAreaType;
    }

    /**
     * @return the kotid
     */
    public int getKotid() {
        return kotid;
    }

    /**
     * @param kotid the kotid to set
     */
    public void setKotid(int kotid) {
        this.kotid = kotid;
    }
    
     public int printKotid() {
        return getKotid();
    }

    /**
     * @return the kotdate
     */
    public java.util.Date getKotdate() {
        return kotdate;
    }

    /**
     * @param kotdate the kotdate to set
     */
    public void setKotdate(java.util.Date kotdate) {
        this.kotdate = kotdate;
    }

    /**
     * @return the kottable
     */
    public String getKottable() {
        return kottable;
    }

    /**
     * @param kottable the kottable to set
     */
    public void setKottable(String kottable) {
        this.kottable = kottable;
    }

    /**
     * @return the kotuser
     */
    public String getKotuser() {
        return kotuser;
    }

    /**
     * @param kotuser the kotuser to set
     */
    public void setKotuser(String kotuser) {
        this.kotuser = kotuser;
    }
    
     //newly added methods to calculate line level service charge and service tax  
    public double getLineServiceCharge() {
        double servicecharge = 0;
//        System.out.println("ticketType getTax---"+ticketType);
        if(ticketType==0){
            if(m_oTicket.getRate().equals("0") || m_oTicket.getRate().equals("")){
             servicecharge= (price * multiply) * getServiceChargeRate();
            }else{
                double subTotaldiscountPrice = (price - (price * (m_oTicket.convertRatetodouble(m_oTicket.getRate()))))* multiply;
               servicecharge= ((subTotaldiscountPrice)+((subTotaldiscountPrice)*m_oTicket.getServiceChargeRate()));
            }
        }else{
            servicecharge= ((price * multiply)+((price * multiply)*m_oTicket.getServiceChargeRate()));
        }
         return servicecharge;
    }
    
      public double getServiceChargeRate() {
        return charge == null ? 0.0 : charge.getRate();
    }
       public String getProductServiceChargeID() {
        return (attributes.getProperty("product.servicecategoryid"));
    }
       
    public ServiceChargeInfo getChargeInfo() {
        return charge;
    }

    public void setChargeInfo(ServiceChargeInfo value) {
        charge = value;
    }
    
     public TaxInfo getServiceTaxInfo() {
        return servicetax;
    }


    public void setServiceTaxInfo(TaxInfo value) {
        servicetax = value;
    }
         public TaxInfo getSBTaxInfo() {
        return sbtax;
    }


    public void setSBTaxInfo(TaxInfo value) {
        sbtax = value;
    }

    /**
     * @return the Discountrate
     */
    public String getDiscountrate() {
      String promoDiscount=m_oTicket.getM_App().getProperties().getProperty("machine.promodiscount");
     if(promoDiscount.equals("true"))  {
          if(discountrate==""){
            discountrate="0";
        }
     }else{
          if(getCampaignId().equals("")){ 
              if(discountrate==""){
            discountrate="0";
        }
          }
     }
       
        return discountrate;
    }

    /**
     * @param Discountrate the Discountrate to set
     */
    public void setDiscountrate(String Discountrate) {
        this.discountrate = Discountrate;
    }

    /**
//     * @return the DiscountAmount
//     */
//    public String getDiscountAmount() {
//        return DiscountAmount;
//    }
//
//    /**
//     * @param DiscountAmount the DiscountAmount to set
//     */
//    public void setDiscountAmount(String DiscountAmount) {
//        this.DiscountAmount = DiscountAmount;
//    }

    
    //Calculation of discount respective to product category 
     public double getLineDiscountPriceOnProductCat() {
      double value = 0;
     System.out.println("getCampaignId()----"+getCampaignId());
     if(getPromodiscountPercent()!=100){
     if(!this.getDiscountrate().equals("") && this.getDiscountrate()!=null){
     String promoDiscount=m_oTicket.getM_App().getProperties().getProperty("machine.promodiscount");
     if(promoDiscount.equals("true"))  {
         value= (price * multiply)* (m_oTicket.convertRatetodouble(this.getDiscountrate()));
     }else{
         if(getCampaignId().equals("")){
              value= (price * multiply)* (m_oTicket.convertRatetodouble(this.getDiscountrate())); 
         }
     }
   
//     else if(!this.getDiscountAmount().equals("") && this.getDiscountAmount()!=null)
//     value= Double.parseDouble(this.getDiscountAmount());
    
     }
     }
      return value;
    }
     
     
     //Method to get subTotal respective to product category discount
     public double getSubValueOnProductCat() {
        double value=0;
        double discount=0;
        //if(this.getDiscountrate()!="" && this.getDiscountrate()!=null){
        if(!this.getDiscountrate().equals("") && this.getDiscountrate()!=null){    
            discount=Double.parseDouble(this.getDiscountrate());
             value= (price - (price *discount)) * multiply;
        }
//        else if(!this.getDiscountAmount().equals("") && this.getDiscountAmount()!=null){
//            discount=Double.parseDouble(this.getDiscountAmount());
//              value= (price - discount) * multiply;
//        }
        else{
           value= (price * multiply);  
        }
        
        return value;
    }
     
     public double getSalePrice() {
  //      double value= getDiscountValue()/multiply;\
      double salePrice;

          salePrice=(getPrice()*getMultiply())-(getLineDiscountPrice()+getOfferDiscount());
          
        return salePrice;
    }

    /**
     * @return the catId
     */
    public String getCatId() {
        return catId;
    }

    /**
     * @param catId the catId to set
     */
    public void setCatId(String parentCatId) {
        this.catId = parentCatId;
    }

    /**
     * @return the kdsPrepareStatus
     */
    public String getKdsPrepareStatus() {
        return kdsPrepareStatus;
    }

    /**
     * @param kdsPrepareStatus the kdsPrepareStatus to set
     */
    public void setKdsPrepareStatus(String kdsPrepareStatus) {
        this.kdsPrepareStatus = kdsPrepareStatus;
    }

    /**
     * @return the preparationTime
     */
    public String getPreparationTime() {
        return preparationTime;
    }

    /**
     * @param preparationTime the preparationTime to set
     */
    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    /**
     * @return the sosorderLineid
     */
    public String getSosorderLineid() {
        return sosorderLineid;
    }

    /**
     * @param sosorderLineid the sosorderLineid to set
     */
    public void setSosorderLineid(String sosorderLineid) {
        this.sosorderLineid = sosorderLineid;
    }

    public double getOfferDiscount() {
        return offerDiscount;
    }
    
 /**
     * @return the productionArea
     */
    public String getProductionArea() {
        return productionArea;
    }

    /**
     * @param productionArea the productionArea to set
     */
    public void setProductionArea(String productionArea) {
        this.productionArea = productionArea;
    }
    public void setOfferDiscount(double offerDiscount) {
        this.offerDiscount = offerDiscount;
    }
      public double getServiceTaxRate() {
        return servicetax == null ? 0.0 : servicetax.getRate();
    }
    public double getSbTaxRate() {
        return sbtax == null ? 0.0 : sbtax.getRate();
    }
    public double getServiceTaxValue() {
        double taxvalue;
        taxvalue= price * multiply * getServiceTaxRate();
         return taxvalue;
    }
       public double getSbTaxValue() {
        double taxvalue;
        taxvalue= price * multiply * getSbTaxRate();
         return taxvalue;
    }
// public ArrayList<PromoRuleIdInfo> getPromoRuleId() {
//        return promoRuleIdList;
//    }
//
//    private void setPromoRuleId(ArrayList<PromoRuleIdInfo> promoRule) {
//       promoRuleIdList=promoRule;
//    }

    /**
     * @return the isBogoMain
     */
    public String getIsBogoMain() {
        return isBogoMain;
    }

    /**
     * @param isBogoMain the isBogoMain to set
     */
    public void setIsBogoMain(String isBogoMain) {
        this.isBogoMain = isBogoMain;
    }

    /**
     * @return the promotiondiscountValue
     */
    public double getPromodiscountPercent() {
      return promodiscountPercent;
    }

    /**
     * @param promotiondiscountValue the promotiondiscountValue to set
     */
    public void setPromodiscountPercent(double promotiondiscountValue) {
        this.promodiscountPercent = promotiondiscountValue;
    }

    /**
     * @return the actualPrice
     */
    public double getActualPrice() {
        return actualPrice;
    }

    /**
     * @param actualPrice the actualPrice to set
     */
    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }
      public String getIsPromoSku() {
          try {
            isPromoSku = getPromoRule().get(0).getIsSku();
        } catch (NullPointerException ex) {
            isPromoSku = null;
        }
        return isPromoSku == null ? "" : isPromoSku;
      }
}
