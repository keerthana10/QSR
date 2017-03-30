//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
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
package com.openbravo.pos.sales;

import com.openbravo.basic.BasicException;
import com.openbravo.pos.customers.CustomerInfoExt;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.ticket.TaxInfo;
import com.openbravo.pos.ticket.RetailTicketInfo;
import com.openbravo.pos.ticket.RetailTicketLineInfo;
import com.openbravo.pos.ticket.TaxStructureInfo;
import com.openbravo.pos.ticket.TicketTaxInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class RetailTaxesLogic {

    private List<TaxInfo> taxlist;
    public AppView m_App;
    protected DataLogicSales dlSales;
    private Map<String, TaxesLogicElement> taxtrees;
    private RetailTicketInfo ticket;
    private Map<String, TaxStructureInfo> taxMap = null;
    // added temporarily
    private String productCatId = null;

    public RetailTaxesLogic(List<TaxInfo> taxlist, AppView app) {
        this.taxlist = taxlist;
        m_App = app;
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        taxtrees = new HashMap<String, TaxesLogicElement>();

        // Order the taxlist by Application Order...
        List<TaxInfo> taxlistordered = new ArrayList<TaxInfo>();
        taxlistordered.addAll(taxlist);
        Collections.sort(taxlistordered, new Comparator<TaxInfo>() {
            public int compare(TaxInfo o1, TaxInfo o2) {
                if (o1.getApplicationOrder() < o2.getApplicationOrder()) {
                    return -1;
                } else if (o1.getApplicationOrder() == o2.getApplicationOrder()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        // Generate the taxtrees
        HashMap<String, TaxesLogicElement> taxorphans = new HashMap<String, TaxesLogicElement>();

        for (TaxInfo t : taxlistordered) {

            TaxesLogicElement te = new TaxesLogicElement(t);

            // get the parent
            TaxesLogicElement teparent = taxtrees.get(t.getParentID());
            if (teparent == null) {
                // orphan node
                teparent = taxorphans.get(t.getParentID());
                if (teparent == null) {
                    teparent = new TaxesLogicElement(null);
                    taxorphans.put(t.getParentID(), teparent);
                }
            }

            teparent.getSons().add(te);

            // Does it have orphans ?
            teparent = taxorphans.get(t.getId());
            if (teparent != null) {
                // get all the sons
                te.getSons().addAll(teparent.getSons());
                // remove the orphans
                taxorphans.remove(t.getId());
            }

            // Add it to the tree...
            taxtrees.put(t.getId(), te);
        }
    }

    public void calculateTaxes(RetailTicketInfo ticket) throws TaxesException {
        this.ticket = ticket;
        List<TicketTaxInfo> tickettaxes = new ArrayList<TicketTaxInfo>();

        for (RetailTicketLineInfo line : ticket.getLines()) {
            // Differentiating the older bill and new bill to set service charge    
            //  null value to check split bill and "" to 
            if (ticket.getErrMsg() == ("") || ticket.getErrMsg() == (null)) {
                ticket.setServiceChargeRate(line.getServiceChargeRate());
            }
            tickettaxes = sumLineTaxes(tickettaxes, calculateTaxes(line, ticket));
        }

        ticket.setTaxes(tickettaxes);
    }

    public void calculateTakeAwayTaxes(RetailTicketInfo ticket) throws TaxesException {
        this.ticket = ticket;
        List<TicketTaxInfo> tickettaxes = new ArrayList<TicketTaxInfo>();
        taxMap = new HashMap<String, TaxStructureInfo>();
        for (RetailTicketLineInfo line : ticket.getLines()) {
            // Differentiating the older bill and new bill to set service charge
            //  null value to check split bill and "" to
            //  if(ticket.getErrMsg()==("") || ticket.getErrMsg()==(null)) {
            if (line.getPromodiscountPercent() != 100) {
                ticket.setServiceChargeRate(0.00);
                //  }
                tickettaxes = sumLineTaxes(tickettaxes, calculateTaxes(line, ticket));
            }
        }
        taxMap = null;
        ticket.setTaxes(tickettaxes);
    }

    public List<TicketTaxInfo> calculateTaxes(RetailTicketLineInfo line) throws TaxesException {

        TaxesLogicElement taxesapplied = getTaxesApplied(line.getTaxInfo());
        return calculateLineTaxes(line.getSubValue(), taxesapplied);
    }

    public List<TicketTaxInfo> calculateTaxes(RetailTicketLineInfo line, RetailTicketInfo ticket) throws TaxesException {

        productCatId = line.getProductCategoryID();
        TaxesLogicElement taxesapplied = getTaxesApplied(line.getTaxInfo());
        if (ticket.iscategoryDiscount()) {
            return calculateLineTaxes(line.getSubValueOnProductCat(), taxesapplied, ticket);
        } else {
            return calculateLineTaxes(line.getSubValue(), taxesapplied, ticket);
        }
    }

    private List<TicketTaxInfo> calculateLineTaxes(double base, TaxesLogicElement taxesapplied) {

        List<TicketTaxInfo> linetaxes = new ArrayList<TicketTaxInfo>();

        if (taxesapplied.getSons().isEmpty()) {
            TicketTaxInfo tickettax = new TicketTaxInfo(taxesapplied.getTax());
            //   tickettax.add(base);
//            if(ticket.getStoreName().equals("WMC- Brewpub") || ticket.getStoreName().equals("WMC- Terrace")  || ticket.getStoreName().equals("Habanero")){
//              tickettax.addTaxWithoutServiceCharge(base,ticket);
//            }else{
//              tickettax.add(base,ticket);
//            }
            tickettax.addErpStdTax(base, ticket, taxMap);
            linetaxes.add(tickettax);
        } else {
            double acum = base;

            for (TaxesLogicElement te : taxesapplied.getSons()) {

                List<TicketTaxInfo> sublinetaxes = calculateLineTaxes(
                        te.getTax().isCascade() ? acum : base,
                        te);
                linetaxes.addAll(sublinetaxes);
                acum += sumTaxes(sublinetaxes);
            }
        }

        return linetaxes;
    }

    private List<TicketTaxInfo> calculateLineTaxes(double base, TaxesLogicElement taxesapplied, RetailTicketInfo ticket) {
        List<TicketTaxInfo> linetaxes = new ArrayList<TicketTaxInfo>();
        if (taxesapplied.getSons().isEmpty()) {
            TicketTaxInfo tickettax = new TicketTaxInfo(taxesapplied.getTax());
            // tickettax.add(base);
//            System.out.println("ticket.getStoreName()--"+ticket.getStoreName());
//            if(ticket.getStoreName().equals("WMC- Brewpub") || ticket.getStoreName().equals("WMC- Terrace")  || ticket.getStoreName().equals("Habanero")){
//              tickettax.addTaxWithoutServiceCharge(base,ticket);   
//            }else{
//             tickettax.add(base,ticket);
//            }
//            linetaxes.add(tickettax);
//            tickettax.setTaxAmount(ticket.getTaxValue());
            tickettax.addErpStdTax(base, ticket, taxMap);
        } else {
            double acum = base;
            Collections.sort(taxesapplied.getSons(), TaxesLogicElement.CustomComparator);
            for (TaxesLogicElement te : taxesapplied.getSons()) {
                //added temporarily to avoid beverage and drinks category product's taxes 20/06/2016
//                boolean nonBevarageTax = true;;
//                if (("QSR-Ahmedabad").equals(m_App.getProperties().getStoreName())) {
//                    if ((productCatId.equals("856E65D2A7464D2B924CDF2D0C8BDC03") || productCatId.equals("B746DBCB1FC643269B4F2999001445F7")) && ((te.getTax().getName().equals("VAT F&B 12.5%_AMD")) || (te.getTax().getName().equals("Addtnl tax - 2.5%_AMD")))) {
//                     nonBevarageTax = false;
//                    }
//                }
              //  if (nonBevarageTax) {
                    taxMap.put(te.getTax().getId(), new TaxStructureInfo(te.getTax().getBaseAmt(), te.getTax().getRate(), 0, false));
                    List<TicketTaxInfo> sublinetaxes = calculateLineTaxes(
                            te.getTax().isCascade() ? acum : base,
                            te);
                    linetaxes.addAll(sublinetaxes);
                    acum += sumTaxes(sublinetaxes);
           //     }

            }
        }

        return linetaxes;
    }

    private TaxesLogicElement getTaxesApplied(TaxInfo t) throws TaxesException {
        String id = null;
        if (t == null) {

            try {
                id = dlSales.getTaxId();
            } catch (BasicException ex) {
                Logger.getLogger(RetailTaxesLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            id = t.getId();
        }

        return taxtrees.get(id);
    }

    private double sumTaxes(List<TicketTaxInfo> linetaxes) {

        double taxtotal = 0.0;

        for (TicketTaxInfo tickettax : linetaxes) {
            taxtotal += tickettax.getTax();

        }
        return taxtotal;
    }

    private List<TicketTaxInfo> sumLineTaxes(List<TicketTaxInfo> list1, List<TicketTaxInfo> list2) {
        for (TicketTaxInfo tickettax : list2) {
            //Changed the sum logic based on tax rate 
            //  TicketTaxInfo i = searchTicketTax(list1, tickettax.getTaxInfo().getRate());
            TicketTaxInfo i = searchTicketTax(list1, tickettax.getTaxInfo().getName());
            if (i == null) {
                list1.add(tickettax);
            } else {
                i.addErpStdTax(tickettax.getSubTotal(), ticket, taxMap);
//               // i.add(tickettax.getSubTotal());
//                if(ticket.getStoreName().equals("WMC- Brewpub") || ticket.getStoreName().equals("WMC- Terrace")  || ticket.getStoreName().equals("Habanero")){
//                    i.addTaxWithoutServiceCharge(tickettax.getSubTotal(),ticket);
//                }else{     
//                i.add(tickettax.getSubTotal(),ticket);
//                }
            }
        }
        return list1;
    }
    //The sum logic based on tax name 

    private TicketTaxInfo searchTicketTax(List<TicketTaxInfo> l, String name) {
        for (TicketTaxInfo tickettax : l) {
            if (name.equals(tickettax.getTaxInfo().getName())) {
                return tickettax;
            }
        }
        return null;
    }
    //Changed the sum logic based on tax rate 

    private TicketTaxInfo searchTicketTax(List<TicketTaxInfo> l, double rate) {
        for (TicketTaxInfo tickettax : l) {
            if (rate == (tickettax.getTaxInfo().getRate())) {
                return tickettax;
            }
        }
        return null;
    }

//    public double getTaxRate(String tcid) {
//        return getTaxRate(tcid, null);
//    }
//    
//    public double getTaxRate(TaxCategoryInfo tc) {
//        return getTaxRate(tc, null);
//    }
//    public double getTaxRate(TaxCategoryInfo tc, CustomerInfoExt customer) {
//        
//        if (tc == null) {
//            return 0.0;
//        } else {
//            return getTaxRate(tc.getID(), customer);          
//        }
//    }
//    
//    public double getTaxRate(String tcid, CustomerInfoExt customer) {
//        
//        if (tcid == null) {
//            return 0.0;
//        } else {
//            TaxInfo tax = getTaxInfo(tcid, customer);
//            if (tax == null) {
//                return 0.0;
//            } else {
//                return tax.getRate();
//            }            
//        }
//    }
//    
//    public TaxInfo getTaxInfo(String tcid) {
//        return getTaxInfo(tcid, null);
//    }
//    
//    public TaxInfo getTaxInfo(TaxCategoryInfo tc) {
//        return getTaxInfo(tc.getID(), null);
//    }
//    
//    public TaxInfo getTaxInfo(TaxCategoryInfo tc, CustomerInfoExt customer) {  
//        return getTaxInfo(tc.getID(), customer);
//    }    
    public TaxInfo getTaxInfo(String tcid, CustomerInfoExt customer, String isTakeAway,String isExempt) {
        //added to assign exempt tax to aeriated drinks and beverages 01/09/2016
        TaxInfo defaulttax = null;
        if(isExempt.equals("Y")){
            for (TaxInfo tax : taxlist) {
            if(tax.getName().equals("Exempt")){
                defaulttax=tax;
                return  tax;
              }
            }
        }else{
        //added the valid from date validation 
        String latestParentTax = "";
        try {
            latestParentTax = dlSales.getLatestParentTax(tcid, isTakeAway);
            System.out.println("latestParentTax----"+latestParentTax);
        } catch (BasicException ex) {
            Logger.getLogger(RetailTaxesLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
         defaulttax = null;

        for (TaxInfo tax : taxlist) {
            if (tax.getParentID() == null && tax.getTaxCategoryID().equals(tcid) && tax.getTakeAway().equals(isTakeAway) && tax.getId().equals(latestParentTax)) {
                System.out.println("matching??");
                if ((customer == null || customer.getTaxCustCategoryID() == null) && tax.getTaxCustCategoryID() == null) {
                    return tax;
                } else if (customer != null && customer.getTaxCustCategoryID() != null && customer.getTaxCustCategoryID().equals(tax.getTaxCustCategoryID())) {
                    return tax;
                }

                if (tax.getTaxCustCategoryID() == null) {
                    defaulttax = tax;
                }
            }
        }

        // No tax found
        
    }
        return defaulttax;
    }
}
