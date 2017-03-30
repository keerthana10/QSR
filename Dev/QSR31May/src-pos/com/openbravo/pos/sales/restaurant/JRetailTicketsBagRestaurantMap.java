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

package com.openbravo.pos.sales.restaurant;

import com.openbravo.pos.ticket.TicketInfo;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.openbravo.pos.sales.*;
import com.openbravo.pos.forms.*; 
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.loader.SerializerReadClass;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.ticket.MenuInfo;
import com.openbravo.pos.ticket.RetailTicketInfo;
import com.openbravo.pos.ticket.RetailTicketLineInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;


public class JRetailTicketsBagRestaurantMap extends JRetailTicketsBag {
    private static boolean status;
    private static String tableId;
    private static String newTableId;
    private static String newTableName;

//    private static final Icon ICO_OCU = new ImageIcon(JTicketsBag.class.getResource("/com/openbravo/images/edit_group.png"));
//    private static final Icon ICO_FRE = new NullIcon(22, 22);
        
    private java.util.List<Place> m_aplaces;
    private java.util.List<Floor> m_afloors;
    
    private JRetailTicketsBagRestaurant m_restaurantmap;
    private JRetailTicketsBagRestaurantRes m_jreservations;
    
    private Place m_PlaceCurrent;
    
    // State vars
    private Place m_PlaceClipboard;  
    private CustomerInfo customer;

    private DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    private DataLogicSystem dlSystem = null;
    private AppView m_app;
    private static String tableName;
    private  int noOfCovers;
    private RetailTicketInfo retailTicket;
    private String oldTableId;
    private static RetailTicketInfo ticket = null;
    private java.util.List<kotInfo> kotTicketlist;
    private RefreshTickets autoRefresh = new RefreshTickets();
    private Timer timer = new Timer(120000,autoRefresh);
  
    //private Timer timer = new Timer(10000,autoRefresh);
     private JRootApp m_RootApp;
      Logger logger = Logger.getLogger("MyLog");  
      private String SplitTableMoveId;
    private boolean movedSplitTable;
    
      

    /** Creates new form JTicketsBagRestaurant */
    public JRetailTicketsBagRestaurantMap(AppView app, RetailTicketsEditor panelticket,String businessType) {
      
        super(app, panelticket);
        this.m_App = app;
        dlReceipts = (DataLogicReceipts) app.getBean("com.openbravo.pos.sales.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
         dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        
        m_restaurantmap = new JRetailTicketsBagRestaurant(app, this);
        m_PlaceCurrent = null;
        m_PlaceClipboard = null;
        customer = null;
            
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(), 
                    "SELECT ID, NAME, IMAGE FROM FLOORS ORDER BY NAME", 
                    null, 
                    new SerializerReadClass(Floor.class));
            m_afloors = sent.list();
               
                
            
        } catch (BasicException eD) {
            m_afloors = new ArrayList<Floor>();
        }
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(), 
                    "SELECT ID, NAME, X, Y, FLOOR FROM PLACES ORDER BY FLOOR", 
                    null, 
                    new SerializerReadClass(Place.class));
            m_aplaces = sent.list();
        } catch (BasicException eD) {
            m_aplaces = new ArrayList<Place>();
        } 
        
        initComponents(); 
          
        // add the Floors containers
        if (m_afloors.size() > 1) {
            // A tab container for 2 or more floors
            JTabbedPane jTabFloors = new JTabbedPane();
            jTabFloors.applyComponentOrientation(getComponentOrientation());
            jTabFloors.setBorder(new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)));
            jTabFloors.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabFloors.setFocusable(false);
            jTabFloors.setRequestFocusEnabled(false);
            m_jPanelMap.add(jTabFloors, BorderLayout.CENTER);
            
            for (Floor f : m_afloors) {
                f.getContainer().applyComponentOrientation(getComponentOrientation());
                
                JScrollPane jScrCont = new JScrollPane();
                jScrCont.applyComponentOrientation(getComponentOrientation());
                JPanel jPanCont = new JPanel();  
                jPanCont.applyComponentOrientation(getComponentOrientation());
                
                jTabFloors.addTab(f.getName(), f.getIcon(), jScrCont);     
                jScrCont.setViewportView(jPanCont);
                jPanCont.add(f.getContainer());
            }
        } else if (m_afloors.size() == 1) {
            // Just a frame for 1 floor
            Floor f = m_afloors.get(0);
            f.getContainer().applyComponentOrientation(getComponentOrientation());
            
            JPanel jPlaces = new JPanel();
            jPlaces.applyComponentOrientation(getComponentOrientation());
            jPlaces.setLayout(new BorderLayout());
        
            jPlaces.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                    new javax.swing.border.TitledBorder(f.getName())));
            
            JScrollPane jScrCont = new JScrollPane();
            jScrCont.applyComponentOrientation(getComponentOrientation());
            JPanel jPanCont = new JPanel();
            jPanCont.applyComponentOrientation(getComponentOrientation());
            
            // jPlaces.setLayout(new FlowLayout());           
            m_jPanelMap.add(jPlaces, BorderLayout.CENTER);
            jPlaces.add(jScrCont, BorderLayout.CENTER);
            jScrCont.setViewportView(jPanCont);            
            jPanCont.add(f.getContainer());
        }   
        
        // Add all the Table buttons.
        Floor currfloor = null;
        
        
        for (Place pl : m_aplaces) {
            int iFloor = 0;
            
            if (currfloor == null || !currfloor.getID().equals(pl.getFloor())) {
                // Look for a new floor
                do {
                    currfloor = m_afloors.get(iFloor++);
                } while (!currfloor.getID().equals(pl.getFloor()));
            }

            currfloor.getContainer().add(pl.getButton());
            pl.setButtonBounds();
            pl.getButton().addActionListener(new MyActionListener(pl));
        }
        
        // Add the reservations panel
        m_jreservations = new JRetailTicketsBagRestaurantRes(app, this);
        add(m_jreservations, "res");
    }
    
    public void activate() {
        
        // precondicion es que no tenemos ticket activado ni ticket en el panel

        m_PlaceClipboard = null;
        customer = null;
        m_jbtnReservations.setVisible(false);
        loadTickets();
        timer.start();
        printState(); 
        
        m_panelticket.setRetailActiveTicket(null, null);
      
        m_restaurantmap.activate(dlReceipts);
       
        showView("map"); // arrancamos en la vista de las mesas.
        
        // postcondicion es que tenemos ticket activado aqui y ticket en el panel
    }
    
    public boolean deactivate() {
        
        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        
        if (viewTables()) {
        
            // borramos el clipboard
            m_PlaceClipboard = null;
            customer = null;

            // guardamos el ticket
            if (m_PlaceCurrent != null) {
                try {
                 // if(m_PlaceCurrent.getIsSplit()==0) {
                    System.out.println("deactivate 2nd method"+m_panelticket.getActiveTicket().isTicketOpen());
                    m_panelticket.getActiveTicket().setTicketOpen(false);
                     System.out.println("deactivate 2nd method"+m_panelticket.getActiveTicket().isTicketOpen()+m_PlaceCurrent.getId());
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());
               //   }else{
                //   dlReceipts.updateSharedSplitTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());  
                 // } 
                }catch(BasicException e){
                     new MessageInf(e).show(this);
                }
                catch (NullPointerException e) {
                    
                }                                  
 
                m_PlaceCurrent = null;
            }

            // desactivamos cositas.
            printState();     
            m_panelticket.setRetailActiveTicket(null, null);
            timer.stop();
            return true;
        } else {
                timer.stop();
            return false;
        }
        
        // postcondicion es que no tenemos ticket activado
        
    }

        
    protected JComponent getBagComponent() {
        return m_restaurantmap;
    }
    protected JComponent getNullComponent() {
        return this;
    }
   
    public void moveTicket() {
        logger.info("Move Ticket Action Table name: "+m_panelticket.getActiveTicket().getTableName());   
        System.out.println(m_panelticket.getActiveTicket().getSplitValue()+"m_panelticket.getActiveTicket().getSplitValue()");
  //adding condition for move table not to allow if the table having zero line items
     if(m_panelticket.getActiveTicket().getLinesCount()==0 ){
        showMessage(this,"Table cannot be moved because it has no items");   
     }  //and if the table having more than 1 Bill.
//     else if(!m_panelticket.getActiveTicket().getSplitValue().equals("")){
//        showMessage(this,"Table cannot be moved because it has split bills"); 
//     }//Checking whether bill is printed
     else if((m_panelticket.getActiveTicket().isPrinted()) && !(m_panelticket.getActiveTicket().isListModified())){
        showMessage(this,"Table cannot be moved because bill is printed"); 
     }
     else{
         //checking any kot item present?
          int kotcount=0;
         for( int k=0;k<m_panelticket.getActiveTicket().getLinesCount();k++) {
               if(m_panelticket.getActiveTicket().getLine(k).getIsKot()==1)  {
                  kotcount=1;
                  break;
              }
            }
        if(kotcount==0)   {
           showMessage(this,"Table cannot be moved because it has non kot items");  
        }else{
            //removing all non kot items while moving the table
           int i=0;
            while(i<m_panelticket.getActiveTicket().getLinesCount()) {    
            if(m_panelticket.getActiveTicket().getLine(i).getIsKot()==0)  {  
                m_panelticket.getActiveTicket().removeLine(i);
                i=0;
                }else{
                i++;
                }
              }
              m_panelticket.getActiveTicket().refreshTxtFields(0);
             if (m_PlaceCurrent != null) {
            try {
                 if( m_panelticket.getActiveTicket().getSplitValue().equals("Split")){
                    movedSplitTable=true;
                    m_panelticket.getActiveTicket().setSplitValue("");
                     }
                SplitTableMoveId=m_panelticket.getActiveTicket().getSplitSharedId();
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());
            } catch (BasicException e) {
                logger.info("move table exception "+e.getMessage());
                new MessageInf(e).show(this);
            }      
            
            // me guardo el ticket que quiero copiar.
            m_PlaceClipboard = m_PlaceCurrent;    
            customer = null;
            m_PlaceCurrent = null;
        }
        
        printState();
        m_panelticket.setRetailActiveTicket(null, null);
        loadTickets();
        startTimer();
        }
      } 
    }
        private void showMessage(JRetailTicketsBagRestaurantMap aThis, String msg) {
        JOptionPane.showMessageDialog(aThis, getLabelPanel(msg), "Message",
                                        JOptionPane.INFORMATION_MESSAGE);

    }
private JPanel getLabelPanel(String msg) {
    JPanel panel = new JPanel();
    Font font = new Font("Verdana", Font.BOLD, 12);
    panel.setFont(font);
    panel.setOpaque(true);
   // panel.setBackground(Color.BLUE);
    JLabel label = new JLabel(msg, JLabel.LEFT);
    label.setForeground(Color.RED);
    label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    panel.add(label);

    return panel;
}
    public boolean viewTables(CustomerInfo c) {
        // deberiamos comprobar si estamos en reservations o en tables...
        if (m_jreservations.deactivate()) {
            showView("map"); // arrancamos en la vista de las mesas.
            
            m_PlaceClipboard = null;    
            customer = c;     
            printState();
            
            return true;
        } else {
            return false;
        }        
    }
    
    public boolean viewTables() {
        return viewTables(null);
    }
     
    
    
    public void newTicket() {
      // guardamos el ticket
        if (m_PlaceCurrent != null) {
          // if there is no kot items  
           logger.info("newTicket Action Table name: "+m_panelticket.getActiveTicket().getTableName()); 
        if(m_panelticket.getActiveTicket().getOrderId()==0){
         deleteTicket();  
        }//if kot done but cancelled all lines
      else if(m_panelticket.getActiveTicket().getOrderId()!=0 && m_panelticket.getActiveTicket().getLinesCount()==0){
            m_panelticket.getActiveTicket().setUser(m_App.getAppUserView().getUser().getUserInfo()); // El usuario que lo cobra
            m_panelticket.getActiveTicket().setActiveCash(m_App.getActiveCashIndex());
            m_panelticket.getActiveTicket().setActiveDay(m_App.getActiveDayIndex());
            m_panelticket.getActiveTicket().setDate(new Date()); //
            String ticketDocument;
            ticketDocument = m_App.getProperties().getStoreName()+"-"+m_App.getProperties().getPosNo()+"-"+m_panelticket.getActiveTicket().getTicketId();
            String reason="Splitted with zero lines/cancelled all kot lines";
                try {
                  dlSales.saveRetailCancelTicket(m_panelticket.getActiveTicket(), m_App.getProperties().getStoreName(),ticketDocument,"Y", m_App.getInventoryLocation(),reason, "", m_App.getProperties().getPosNo(),"N");
                } catch (BasicException ex) {
                    logger.info("newTicket saveRetailCancelTicket exception "+ex.getMessage());
                Logger.getLogger(JRetailPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                }              
                if(m_panelticket.getActiveTicket().getSplitValue().equals("")){
                    deleteTicket();
                }else{
                    try {    
                        dlReceipts.deleteSharedSplitTicket(m_panelticket.getActiveTicket().getPlaceId(),m_panelticket.getActiveTicket().getSplitSharedId());
                        } catch (BasicException ex) {
                            logger.info("newTicket deleteSharedSplitTicket exception "+ex.getMessage());
                  Logger.getLogger(JRetailPanelTicket.class.getName()).log(Level.SEVERE, null, ex);
                       }
                } 
      }//if partially kot and partially non kot items present
      else{    
         System.out.println("newTicket delete non kot items testing on clicking table"+m_panelticket.getActiveTicket().getOrderId());   
          int i=0;
            while(i<m_panelticket.getActiveTicket().getLinesCount()) {    
            if(m_panelticket.getActiveTicket().getLine(i).getIsKot()==0)  {  
                m_panelticket.getActiveTicket().removeLine(i);
                i=0;
                }else{
                i++;
                }
              }
              m_panelticket.getActiveTicket().refreshTxtFields(0);  
                m_panelticket.getActiveTicket().setTicketOpen(false);
                if(  m_panelticket.getActiveTicket().getLinesCount()==0){
                    deleteTicket();  
                }else{ 
                  try {  
              // if(m_PlaceCurrent.getIsSplit()==0){    
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());                
//                }else{
//                 dlReceipts.updateSharedSplitTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket());     
//               }
               } catch (BasicException e) {
                     logger.info("newTicket updateSharedTicket exception "+e.getMessage());
                new MessageInf(e).show(this); // maybe other guy deleted it
               } 
            m_PlaceCurrent = null;
               }
            }
        printState();     
        m_panelticket.setRetailActiveTicket(null, null);
      }
    }
    
    
    public void newHoldTicket(RetailTicketInfo ticket){
       if (m_PlaceCurrent != null) { 
        try {
            dlSales.SaveHoldTicket(ticket);
        } catch (BasicException ex) {
            Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        m_PlaceCurrent = null;
        }        
        
        printState();     
        m_panelticket.setRetailActiveTicket(null, null);
    }
            
      public void newsplitTicket(RetailTicketInfo ticket1,RetailTicketInfo ticket2) {
        
        // guardamos el ticket
        if (m_PlaceCurrent != null) {
               logger.info("newsplitTicket Action Table name: "+m_panelticket.getActiveTicket().getTableName()); 
         m_panelticket.getActiveTicket().refreshTxtFields(0);                
            try {
                 System.out.println("split value testing issue "+ticket1.getSplitValue()+""+ticket2.getSplitValue());
                System.out.println("bill no testing now 2"+ticket1.getLinesCount()+""+m_PlaceCurrent.getId()+""+ticket1.getSplitSharedId());
                ticket1.setTicketOpen(false);
                ticket2.setTicketOpen(false);
                System.out.println(" first"+ticket1.isTicketOpen()+ticket1.getSplitSharedId());
                System.out.println("second "+ticket2.isTicketOpen()+ticket2.getSplitSharedId());
                dlReceipts.updateSharedSplitTicket(m_PlaceCurrent.getId(), ticket1);
               //  splitId= UUID.randomUUID().toString().replaceAll("-", "");
              //  ticket2.setSplitSharedId(splitId);
                dlReceipts.insertRetailSharedTicket(m_PlaceCurrent.getId(), ticket2);
            } catch (BasicException e) {
                 logger.info("newsplitTicket insertRetailSharedTicket exception "+e.getMessage());
                new MessageInf(e).show(this); // maybe other guy deleted it
            }              
            m_PlaceCurrent.setIsSplit(1); 
            m_PlaceCurrent = null;
        }
        
        printState();     
        m_panelticket.setRetailActiveTicket(null, null);
    }
      
      public void deleteTicket() {
     
        if (m_PlaceCurrent != null) {
            
            String id = m_PlaceCurrent.getId();
          try {
            System.out.println("checking object value "+m_panelticket.getActiveTicket()) ;
            dlReceipts.deleteSharedTicket(id);
            m_PlaceCurrent.setPeople(false);
               
            } catch (BasicException e) {
                 logger.info("deleteTicket in map class exception "+e.getMessage());
                new MessageInf(e).show(this);
            }       
            m_PlaceCurrent = null;
        }        
        
        printState();     
        m_panelticket.setRetailActiveTicket(null, null);
        loadTickets();
    }
        
//    public void deleteSplitTicket(String splitId) {
//     
//        if (m_PlaceCurrent != null) {
//            
//            String id = m_PlaceCurrent.getId();
//            RetailTicketInfo ticket = m_panelticket.getActiveTicket();
//          try {
//              System.out.println("checking object value "+m_panelticket.getActiveTicket()) ;
//           if(splitId.equals("")){
//                  dlReceipts.deleteSharedTicket(id);
//                m_PlaceCurrent.setPeople(false);
//               }else{
//                dlReceipts.deleteSharedSplitTicket(id,splitId);
//                m_PlaceCurrent.setPeople(true);
//              }  
//            } catch (BasicException e) {
//                new MessageInf(e).show(this);
//            }       
//            m_PlaceCurrent = null;
//        }        
//        
//        printState();     
//        m_panelticket.setRetailActiveTicket(null, null);
//         loadTickets();
//    }
     public void deleteCancelTicket() {
        String id = m_PlaceCurrent.getId();
        if (m_PlaceCurrent != null) {
   logger.info("deleteCancelTicket Action Table name: "+m_panelticket.getActiveTicket().getTableName()); 
              RetailTicketInfo ticket = m_panelticket.getActiveTicket();
//              String billNo = Integer.toString(ticket.getTicketId()); //get the billNo for this table
//              String isKotPresent = null;
//                try {
//                    isKotPresent = dlReceipts.isKotEntered(billNo); //determind is any KOT order has been sent go koT yet?
//                } catch (BasicException ex) {
//                    Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
//                }
//              
//              if(isKotPresent.equals("0")){//if NO KOT has been sent yet, delete this ticket  w/o asking for cancellation reason!
//                  cancelTicketBeforeKot(ticket);//call to method for deletion w/o asking reason and setting JCREditor setFlag as true.
//              }
//              else{
              java.util.List<RetailTicketLineInfo> panelLines = ticket.getLines();
              JCancelReasonEditor.showMessage(this, dlReceipts, ticket,"Y","N",dlSales,dlSystem,m_App,m_PlaceCurrent.getName(),m_PlaceCurrent);
             System.out.println("ticket---"+panelLines.size());

     //   }
        /*if setFlag is true for JCREditor, then delete records from sharedTickets & set its People as false
         , this Place object as null and this Active ticket as null*/
        if(JCancelReasonEditor.getFlag()==true){
          try {
            if(ticket.getSplitValue().equals("Split")){
            dlReceipts.deleteSharedSplitTicket(id,ticket.getSplitSharedId());
            m_PlaceCurrent.setPeople(true);
            }else{
            dlReceipts.deleteSharedTicket(id);
            m_PlaceCurrent.setPeople(false);
            }
            m_PlaceCurrent = null;
            printState();
            m_panelticket.setRetailActiveTicket(null, null);
             loadTickets();
          
          }catch (BasicException e) {
               logger.info("deletecancelTicket in map class exception "+e.getMessage());
                new MessageInf(e).show(this);
            }    
        }
        }
    }

    /*method to cancel the ticket same way as earlier, just w/o asking for Cancellation reason and hardcoding
      it as "Others" and cancellation comment as "Cancelled before KOT".
      Note: this ticket will still be stored into database , so document no will be consumed anyhow.*/
//     private void cancelTicketBeforeKot(RetailTicketInfo ticket){ //pass RetailTicketInfo pojo as argumnt
//           String reason = "Cancelled before KOT";//hardcode cancellation comment
//           //set attributes of POJO before cancellation
//           ticket.setUser(m_App.getAppUserView().getUser().getUserInfo()); // El usuario que lo cobra
//           ticket.setActiveCash(m_App.getActiveCashIndex());
//           ticket.setActiveDay(m_App.getActiveDayIndex());
//           ticket.setDate(new Date()); 
//            String ticketDocument;
//            ticketDocument = m_App.getProperties().getStoreName()+"-"+m_App.getProperties().getPosNo()+"-"+ticket.getTicketId();
//            String reasonId = null;
//             try {
//                 reasonId = dlReceipts.getReasonId("Others"); //hardcode reason
//            } catch (BasicException ex) {
//                Logger.getLogger(JCancelReasonEditor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            java.util.List<RetailTicketLineInfo> panelLines = ticket.getLines(); //get all the lines items in this ticket
//            try {
//                dlSales.saveRetailCancelTicket(ticket, m_App.getProperties().getStoreName(), ticketDocument, "Y", m_App.getInventoryLocation(), reason, reasonId, m_App.getProperties().getPosNo(), "N");
//            } catch (BasicException ex) {
//                Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//             String kotTicketId = null;
//                Integer kotCount=0;
//                int kotTicket=0;
//
//            try {
//                kotTicketId = (dlReceipts.getkotTicketId("Y"));
//
//            } catch (BasicException ex) {
//                Logger.getLogger(JCancelReasonEditor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//                if(kotTicketId==null){
//                kotTicket=1;
//            }else{
//                   kotTicket=Integer.parseInt(kotTicketId);
//                   kotTicket = kotTicket+1;
//            }
//            try {
//                dlReceipts.deleteKot(ticket.getId());
//            } catch (BasicException ex) {
//                Logger.getLogger(JCancelReasonEditor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            for(int i=0;i<ticket.getLinesCount();i++){
//                   dlReceipts.insertCancelledKot(ticket.getId(),ticket.getDate(),ticket.getTicketId(),panelLines.get(i).getProductID(), "N",(-1*panelLines.get(i).getMultiply()) ,kotTicket,"Y",reason,reasonId,ticket.getPlaceId(),ticket.getUser().getId());
//            }
//             try {
//                kotTicketlist = dlReceipts.getKot(ticket.getId());
//             } catch (BasicException ex) {
//                Logger.getLogger(JCancelReasonEditor.class.getName()).log(Level.SEVERE, null, ex);
//             }
//             kotInfo kInfolist =  new kotInfo();
//             kInfolist.setKotId(kotTicket);
//
//             ticket.setKotLines(kotTicketlist);
//             try {
//                dlReceipts.updateKotIsprinted(ticket.getId());
//              } catch (BasicException ex) {
//                Logger.getLogger(JCancelReasonEditor.class.getName()).log(Level.SEVERE, null, ex);
//
//       }
//          JCancelReasonEditor.setFlag = true; //set setFlag variable of class JCREditor directly.
//     }

//    public void loadTickets() {
//
//        Set<String> atickets = new HashSet<String>();
//        
//        try {
//            java.util.List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
//
//            for (SharedTicketInfo ticket : l) {
//                atickets.add(ticket.getId());
//            }
//        } catch (BasicException e) {
//            new MessageInf(e).show(this);
//        }            
//            
//        for (Place table : m_aplaces) {
//            table.setPeople(atickets.contains(table.getId()));
//        }
//
//    }
     
public void loadTickets() { 
        // list for all sharedtickets database table entries
        java.util.List<SharedTicketInfo> runningTablesList = null; 
        // Map for Partial detail of SharedTicketInfo Object i.e <ID as key and ISPRINTED as value> 
        Map<String, String> runningTablesMap = new HashMap<String, String>(); 
        String key = null;
        String value = null;
        Boolean isPrinted = null;
        Boolean isModified = null;
        try {
            runningTablesList = dlReceipts.getSharedTicketList();//retrieve sharedticketes data from db 
            //Iterate over all sharedtickets and create HashMap for <ID,ISPRINTED> details
            for (SharedTicketInfo sharedTicket : runningTablesList) {
                //atickets.add(ticket.getId());
                key = sharedTicket.getId();
                isPrinted = sharedTicket.getPrinted();
                isModified = sharedTicket.getModified();
                //if((retailTicket.isPrinted() & retailTicket.isListModified()) || !retailTicket.isPrinted())
                if((isPrinted & isModified ) || !(isPrinted) ) 
                    value = "RED";
                else
                    value = "BLUE";
                runningTablesMap.put(key, value); //add partial detail in Map.
            }
        } catch (BasicException e) {
            logger.info("loadTickets in map class exception "+e.getMessage());
            new MessageInf(e).show(this);
        }            
        
        //Iterate of all the tables present in this floor.
        for (Place table : m_aplaces) {
            //table.setPeople(atickets.contains(table.getId()));
            if(runningTablesMap.containsKey(table.getId())){//if current table is present in my map as key
                //retrieve the value of same key entry from map of shared tickets
                String busyTableColor = runningTablesMap.get(table.getId());
                //If current table is already being printed ,display yellow button ,if not then red button.
                table.setPeople(true);            
                table.setColor(busyTableColor);
            }
            else{
                //if table is not busy or occupied , then display button as Green color.
                table.setPeople(false);
                table.setColor("CYAN");
            }            
        }       
    }

  public void startTimer() {
        System.out.println("startTimer"); 
      timer.start();
   }
   
    public void stopTimer() {
      System.out.println("stopTimer");  
      timer.stop();
   }
    
   
    private void printState() {
        
        if (m_PlaceClipboard == null) {
            if (customer == null) {
                // Select a table
                m_jText.setText(null);
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(true);
                }
                m_jbtnReservations.setEnabled(true);
            } else {
                // receive a customer
                m_jText.setText(AppLocal.getIntString("label.restaurantcustomer", new Object[] {customer.getName()}));
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(!place.hasPeople());
                }                
                m_jbtnReservations.setEnabled(false);
            }
        } else {
            // Moving or merging the receipt to another table
            m_jText.setText(AppLocal.getIntString("label.restaurantmove", new Object[] {m_PlaceClipboard.getName()}));
            // Enable all empty tables and origin table.
            for (Place place : m_aplaces) {
                place.getButton().setEnabled(true);
            }  
            m_jbtnReservations.setEnabled(false);
        }
    }   
    
    public RetailTicketInfo getTicketInfo(Place place) {
         RetailTicketInfo ticket=null;
          SharedTicketNameInfo sharedticket=null;
//          try {
//           sharedticket= dlReceipts.getRetailSharedTicket1(place.getId());
//           System.out.println(sharedticket+"split bill new method sharedticket ");
//          
//         ticket=sharedticket.getContent();
//         }
         try {
           ticket= dlReceipts.getRetailSharedTicket(place.getId());
          }
          catch (BasicException e) {
               logger.info("getTicketInfo in map class exception "+e.getMessage());
            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this);
            return null;
        }
           System.out.println("retriving content from  db"+ticket);
          return ticket;
    }
    
    //method to get multiple tickets belongs to same table
    public java.util.List<SharedSplitTicketInfo> getSplitTicketInfo(Place place) {
          System.out.println(place.getIsSplit()+"split bill new method "+place.getId());
          java.util.List<SharedSplitTicketInfo> splitticket=null;
          try {
           splitticket= dlReceipts.getRetailSharedSplitTicket(place.getId()); 
          // System.out.println(splitticket.get(0).getId()+"getsplitmetho testing"); 
           }catch (BasicException e) {
                  logger.info("getSplitTicketInfo in map class exception "+e.getMessage());
            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this);
            return null;
        }
          return splitticket;
    }
    
     //method to get content of selected ticket of split bill pop up
    public RetailTicketInfo getTicketInfo(Place place,String splitId) {
          System.out.println(place.getIsSplit()+"split bill new method ");
//          SharedTicketNameInfo sharedticket=null;
//          try {
//           sharedticket= dlReceipts.getRetailSharedTicket(place.getId());
//           ticket=sharedticket.getContent();
//          }
          RetailTicketInfo ticket=null;
          try {
           System.out.println(splitId+place.getId()+"split bill getTicketInfo ");   
           ticket= dlReceipts.getRetailSharedTicketSplit(place.getId(),splitId);
          }catch (BasicException e) {
              logger.info("getTicketInfo in map class exception "+e.getMessage());
            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this);
            return null;
        }
          return ticket;
    }
  
    private void setActivePlace(Place place, RetailTicketInfo ticket) {
        
        System.out.println("m_PlaceCurrent --"+place.hasPeople());
         this.retailTicket = ticket;
         m_PlaceCurrent = place;
         //set place id to the newly adding shared ticket
         ticket.setPlaceid(place.getId());
         //when no people
        if(place.hasPeople()==false){
            System.out.println("place.hasPeople() 1"+place.getId());
            setStatus(true);
             JTableCover.showMessage(JRetailTicketsBagRestaurantMap.this, dlReceipts, ticket,place,m_panelticket,this,true);
            
        }else{
            //when move table
            if(getNewTableId()!=null){
                 ticket.setOldTableName(ticket.getTableName());    
                 System.out.println("place.hasPeople() 2");
                   System.out.println("testing move table"+getOldTableId()+getNewTableId());
                 m_panelticket.setRetailActiveTicket(ticket, m_PlaceCurrent.getName());
                ticket.setPlaceid(getNewTableId());
                setTable(getNewTableName());
                dlReceipts.updateTableName(getOldTableId(), getNewTableId());
                 dlSales.insertActionsLog("Move Table", ticket.getUser().getId(), m_App.getProperties().getPosNo(),ticket.getTicketId(),new Date(), getOldTableId(), getNewTableId(), null);
                m_restaurantmap.setTableName(getNewTableId());
                ticket.setNoOfCovers(getTableCovers(getNewTableId()));
               if(movedSplitTable){
                     try {
                         dlReceipts.insertTableCovers(UUID.randomUUID().toString(), getOldTableId(), ticket.getNoOfCovers());
                     } catch (BasicException ex) {
                         Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     movedSplitTable=false;
                }
                //added new line to indicate that the table has been moved
              setNewTableId(null);

            }//when people
            else{
                 //adding logic to split ticket to hold multiple tickets of same table
                java.util.List<SharedSplitTicketInfo> splitticket = getSplitTicketInfo(place); 
                 if(splitticket.size()>1){
                     int status=0;
                      for(SharedSplitTicketInfo splitInfo:splitticket){
                    ticket=getTicketInfo(place,splitInfo.getSplitId());
                  
                          System.out.println("loop value = "+place.getId());
                    if(ticket.isTicketOpen() ){
                        System.out.println("ticket open status"+ticket.isTicketOpen()+ticket.getSplitSharedId());
                        status=1;
                         JOptionPane.showMessageDialog(JRetailTicketsBagRestaurantMap.this,ticket.printUser() +" has already logged in this Table!", "Order in Progress", JOptionPane.INFORMATION_MESSAGE);
                            break;
                    }
                }if(status==0){
                 String splitId= JSplitBillPanel.showMessage(JRetailTicketsBagRestaurantMap.this,dlReceipts,splitticket,place,m_panelticket);
                 System.out.println("testing window 5"+splitId);
                 ticket=getTicketInfo(place,splitId);
                 ticket.setPlaceid(place.getId());
                 System.out.println("split value testing issue IN ACTIVE PLACE "+ticket.getSplitValue());  
                }else{
                   ticket=null; 
                }
                }else{
                     //if there is only last split bill ticket
                    ticket.setSplitValue("");  
                 }
                 if(ticket!=null) {
                System.out.println("place.hasPeople() 3");
                setStatus(false);
                m_panelticket.setRetailActiveTicket(ticket, m_PlaceCurrent.getName());
                ticket.setPlaceid(m_PlaceCurrent.getId());
                setTable(m_PlaceCurrent.getName());
                ticket.setNoOfCovers(getTableCovers(m_PlaceCurrent.getId()));
                }
             }
        }

            setTableId(place.getId());
             m_restaurantmap.setTableName(place.getId());
   
     
    }

    public int getTableCovers(String tableId){
        int noOfCovers = 0;
            try {
                try{
                noOfCovers = dlReceipts.getTableCovers(tableId);
                }catch (NullPointerException ex) {
                    noOfCovers =2;
                }
            } catch (BasicException ex) {
                logger.info("getTableCovers in map class exception "+ex.getMessage());
                Logger.getLogger(JTableCover.class.getName()).log(Level.SEVERE, null, ex);
            }

        return noOfCovers;
    }
    public static String getTable(){
        return tableName;
    }
    public void setTable(String tableName){
        this.tableName = tableName;
    }
 public static String getTableId(){
        return tableId;
    }
    public void setTableId(String tableId){
        this.tableId = tableId;
    }
    public static String getNewTableId(){
        return newTableId;
    }
    public void setNewTableId(String newTableId){
        this.newTableId = newTableId;
    }
    public static String getNewTableName(){
        return newTableName;
    }
    public void setNewTableName(String newTableName){
        this.newTableName = newTableName;
    }
    public  String getOldTableId(){
        return oldTableId;
    }
    public void setOldTableId(String oldTableId){
        this.oldTableId = oldTableId;
    }
     public static boolean getStatus(){
        return status;
    }
    public void setStatus(boolean status){
        this.status = status;
    }
     public  int getNoOfCovers(){
        return noOfCovers;
    }
    public void setNoOfCovers(int noOfCovers){
        this.noOfCovers = noOfCovers;
    }
    private void showView(String view) {
        CardLayout cl = (CardLayout)(getLayout());
        cl.show(this, view);  
    }

    
    public String getDocumentNo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCovers() {
             setStatus(false);
             JTableCover.showMessage(JRetailTicketsBagRestaurantMap.this, dlReceipts, retailTicket,m_PlaceCurrent,m_panelticket,this,false);

    }

  

    
 
    private class MyActionListener implements ActionListener {
        
        private Place m_place;
        
        public MyActionListener(Place place) {
            m_place = place;
        }
        
        public void actionPerformed(ActionEvent evt) {    
            System.out.println("11");
            if (m_PlaceClipboard == null) {  
                  System.out.println("12");
                if (customer == null) {
                    // tables
                  System.out.println("13");
                    // check if the sharedticket is the same
                  //adding logic to split ticket to hold multiple tickets of same table
                  RetailTicketInfo ticket = getTicketInfo(m_place);
                  System.out.println("ticket object testing"+ticket);
                    // if not people or new table
                    if (ticket == null && !m_place.hasPeople()) {
                        // Empty table and checked
                     logger.info(m_App.getAppUserView().getUser().getName().toString()+" is trying to login the new table");
                        // table occupied
                        ticket = new RetailTicketInfo();
//                        m_place.setPeople(true);
                        ticket.setTicketOpen(true);
                        ticket.setUser(m_App.getAppUserView().getUser().getUserInfo());
                      try {
                            dlReceipts.insertRetailSharedTicket(m_place.getId(), ticket);
                          } catch (BasicException e) {
                            logger.info("actionPerformed in map class exception 1 "+e.getMessage());
                            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
                        }                     
                    
                        setActivePlace(m_place, ticket);

                    } 
                    //will never occur? occurs before refreshing
                    else if (ticket == null  && m_place.hasPeople()) {
                        System.out.println("15 --- ticketnull"+m_place.getId());
                         loadTickets();
                         return;
//                          String tokenId = null;
//                        try {
//                            tokenId = dlReceipts.getTokenId(m_place.getId());
//                        } catch (BasicException ex) {
//                            Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                            String money = m_App.getActiveCashIndex();
//                        try {
//                            // BEGIN TRANSACTION
//                            ticket = dlSales.loadOrderTicket(tokenId, money);
//                        } catch (BasicException ex) {
//                            Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                       if(ticket!=null){
//                        try {
//                            dlReceipts.updateOrderSharedTicket(m_place.getId(), ticket);
//                        } catch (BasicException e) {
//                            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
//                        }
//                            setActivePlace(m_place, ticket);
//                       }else{
//                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JRetailTicketsBagRestaurantMap.this);
//                            m_place.setPeople(false);
//                            loadTickets();
//                       }
                    } else if (ticket != null && !m_place.hasPeople()) {
                     logger.info("Condition 1 "+m_place.getName()+" Table is occupied by "+ticket.printUser()+" printed the lines "+ticket.getLinesCount()+ " and "+m_App.getAppUserView().getUser().getName().toString()+" is trying to login the table");   
                     System.out.println("16");
//                        // The table is now full
////                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JRetailTicketsBagRestaurantMap.this);
                          JOptionPane.showMessageDialog(JRetailTicketsBagRestaurantMap.this,ticket.printUser() +" has already logged in this Table!", "Order in Progress", JOptionPane.INFORMATION_MESSAGE);
                        m_place.setPeople(true);
                        loadTickets();
                        return;

                    } else { // both != null
                        java.util.List<SharedSplitTicketInfo> splitticket = getSplitTicketInfo(m_place); 
                        System.out.println("16 split");
                     if(splitticket.size()==1){ 
                         System.out.println(ticket.isTicketOpen());
                        if(ticket.isTicketOpen()){
                        logger.info("Condition 2 "+m_place.getName()+" Table is occupied by "+ticket.printUser()+" printed the lines "+ticket.getLinesCount()+ " and "+m_App.getAppUserView().getUser().getName().toString()+" is trying to login the table");   
                            JOptionPane.showMessageDialog(JRetailTicketsBagRestaurantMap.this,ticket.printUser() +" has already logged in this Table!", "Order in Progress", JOptionPane.INFORMATION_MESSAGE);
                             return;
                        }
                        logger.info("Condition 3 "+m_place.getName()+" Table is occupied by "+ticket.printUser()+" printed the lines "+ticket.getLinesCount()+ " and "+m_App.getAppUserView().getUser().getName().toString()+" is trying to login the table");   
                        ticket.setUser(m_App.getAppUserView().getUser().getUserInfo());
                        ticket.setTicketOpen(true);
                        try {
                          dlReceipts.updateSharedTicket(m_place.getId(), ticket);
                      } catch (BasicException ex) {
                             logger.info("actionPerformed in map class exception 2 "+ex.getMessage());
                          Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
                      }
                     }
                      
                      
                     String  kitchendisplay=  m_App.getProperties().getProperty("machine.kitchendisplay");
                     if(kitchendisplay.equals("true")){
                          System.out.println("17");
                          String tokenId = null;
                        try {
                            tokenId = dlReceipts.getTokenId(m_place.getId());
                        } catch (BasicException ex) {
                             logger.info("actionPerformed in map class exception 3 "+ex.getMessage());
                            Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
                        }
                            String money = m_App.getActiveCashIndex();
                        try {
                            // BEGIN TRANSACTION
                            ticket = dlSales.loadSharedOrderTicket(tokenId, money);
                        } catch (BasicException ex) {
                             logger.info("actionPerformed in map class exception 4 "+ex.getMessage());
                            Logger.getLogger(JRetailTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
                        }
                       if(ticket!=null){
                        try {
                            dlReceipts.updateOrderSharedTicket(m_place.getId(), ticket);
                        } catch (BasicException e) {
                             logger.info("actionPerformed in map class exception 5"+e.getMessage());
                            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
                        }
                         setActivePlace(m_place, ticket);
                                System.out.println("ticket----"+ticket.getLinesCount());
                       }else{
                         ticket = getTicketInfo(m_place);
                        try {
                            dlReceipts.updateSharedTicket(m_place.getId(), ticket);
                        } catch (BasicException e) {
                             logger.info("actionPerformed in map class exception 6 "+e.getMessage());
                            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
                        }
                     }}else{
                        setActivePlace(m_place, ticket);
                     }
                     //  }
                        // Full table                
                        // m_place.setPeople(true); // already true                           
                      
//                 ticket.setTicketOpen(false);
                 
                    }
                }//till here 13
                //will never execute as there is no concept of customer
                else {
                    // receiving customer.
                      System.out.println("18");
                    // check if the sharedticket is the same
                    RetailTicketInfo ticket = getTicketInfo(m_place);
                    if (ticket == null) {
                          System.out.println("19");
                        // receive the customer
                        // table occupied
                        ticket = new RetailTicketInfo();
                        
                        try {
                            ticket.setCustomer(customer.getId() == null
                                    ? null
                                    : dlSales.loadCustomerExt(customer.getId()));
                        } catch (BasicException e) {
                            logger.info("actionPerformed in map class exception 7 "+e.getMessage());
                            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), e);
                            msg.show(JRetailTicketsBagRestaurantMap.this);
                        }                     
                        
                        try {
                            dlReceipts.insertRetailSharedTicket(m_place.getId(), ticket);
                        } catch (BasicException e) {
                            logger.info("actionPerformed in map class exception 8 "+e.getMessage());
                            new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
                        }                     
                        m_place.setPeople(true);
                        
                        m_PlaceClipboard = null;
                        customer = null;     
                        
                        setActivePlace(m_place, ticket);                        
                    } else {
                        // TODO: msg: The table is now full
                        new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JRetailTicketsBagRestaurantMap.this);
                        m_place.setPeople(true);
                        m_place.getButton().setEnabled(false);
                    }
                }
            }   // till 12
            //concept of m_PlaceClipboard
            else {
                  System.out.println("20");
                // check if the sharedticket is the same
                RetailTicketInfo ticketclip = getTicketInfo(m_PlaceClipboard);
                if (ticketclip == null) {
                    new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JRetailTicketsBagRestaurantMap.this);
                    m_PlaceClipboard.setPeople(false);
                    m_PlaceClipboard = null;
                    customer = null;
                    printState();
                } else {
                    // tenemos que copiar el ticket del clipboard
                    if (m_PlaceClipboard == m_place) {
                        // the same button. Canceling.
                        Place placeclip = m_PlaceClipboard;                       
                        m_PlaceClipboard = null;
                        customer = null;
                        printState();
                        setActivePlace(placeclip, ticketclip);
                    } else if (!m_place.hasPeople()) {
                        // Moving the receipt to an empty table
                        RetailTicketInfo ticket = getTicketInfo(m_place);

                        if (ticket == null) {
                            try {
                                dlReceipts.insertRetailSharedTicket(m_place.getId(), ticketclip);
                                m_place.setPeople(true);
                               // dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                 dlReceipts.deleteSharedSplitTicket(m_PlaceClipboard.getId(),SplitTableMoveId);
                                m_PlaceClipboard.setPeople(false);
                                setNewTableId(m_place.getId());
                                setNewTableName(m_place.getName());
                                setOldTableId(m_PlaceClipboard.getId());
                            } catch (BasicException e) {
                                logger.info("actionPerformed in map class exception 9 "+e.getMessage());
                                new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
                            }

                            m_PlaceClipboard = null;
                            customer = null;
                            printState();

                            // No hace falta preguntar si estaba bloqueado porque ya lo estaba antes
                            // activamos el ticket seleccionado
                            setActivePlace(m_place, ticketclip);
                        } else {
                            // Full table
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JRetailTicketsBagRestaurantMap.this);
                            m_PlaceClipboard.setPeople(true);
                            printState();
                        }
                    } else {
                           System.out.println("22");
                        // Merge the lines with the receipt of the table
                        RetailTicketInfo ticket = getTicketInfo(m_place);

                        if (ticket == null) {
                            // The table is now empty
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JRetailTicketsBagRestaurantMap.this);
                            m_place.setPeople(false); // fixed
                        } else {
                             JOptionPane.showMessageDialog(JRetailTicketsBagRestaurantMap.this, getLabelPanel("Cannot Merge the Tables"), "Message",
                                       JOptionPane.INFORMATION_MESSAGE);
                        //    asks if you want to merge tables
//                            if (JOptionPane.showConfirmDialog(JRetailTicketsBagRestaurantMap.this, AppLocal.getIntString("message.mergetablequestion"), AppLocal.getIntString("message.mergetable"), JOptionPane.YES_NO_OPTION)
//                                    == JOptionPane.YES_OPTION){                                 
//                                // merge lines ticket
//                                  try {
//                                    //cancelling the older table ticket
//                                    RetailTicketInfo movedTicket=ticketclip;  
//                                    System.out.println("movedTicket.getTicketId()"+movedTicket.getTicketId());
//                                    movedTicket.setUser(m_App.getAppUserView().getUser().getUserInfo()); // El usuario que lo cobra
//                                    movedTicket.setActiveCash(m_App.getActiveCashIndex());
//                                    movedTicket.setActiveDay(m_App.getActiveDayIndex());
//                                    movedTicket.setDate(new Date());  
//                                    String ticketDocument = m_App.getProperties().getStoreName()+"-"+m_App.getProperties().getPosNo()+"-"+movedTicket.getTicketId();
//                                    dlSales.saveRetailCancelTicket(movedTicket,m_App.getProperties().getStoreName(),ticketDocument,"Y", m_App.getInventoryLocation(),"Moved Bill", "", m_App.getProperties().getPosNo(), "Y");
//                                    dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
//                                    m_PlaceClipboard.setPeople(false);
//                                    if (ticket.getCustomer() == null) {
//                                    ticket.setCustomer(ticketclip.getCustomer());
//                                    }
//                                    System.out.println("ticketclip.getLines()--"+ticketclip.getLines());
//                                    for(RetailTicketLineInfo line : ticketclip.getLines()) {
//                                        ticket.addLine(line);
//                                    }
//                                    dlReceipts.updateSharedTicket(m_place.getId(), ticket);
//                                } catch (BasicException e) {
//                                    new MessageInf(e).show(JRetailTicketsBagRestaurantMap.this); // Glup. But It was empty.
//                                }
//
//                                m_PlaceClipboard = null;
//                                customer = null;
//                                printState();
//
//                                setActivePlace(m_place, ticket);
//                            } else { 
                                // Cancel merge operations
//                                Place placeclip = m_PlaceClipboard;                       
//                                m_PlaceClipboard = null;
//                                customer = null;
//                                printState();
//                                setActivePlace(placeclip, ticketclip);                                   
//                       //     }

                        }                                
                    }
                }
            }
           
        }
    }  
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnReservations = new javax.swing.JButton();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();
        m_jbtnLogout = new javax.swing.JButton();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        m_jbtnReservations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtnReservations.setText(AppLocal.getIntString("button.reservations")); // NOI18N
        m_jbtnReservations.setFocusPainted(false);
        m_jbtnReservations.setFocusable(false);
        m_jbtnReservations.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnReservations.setRequestFocusEnabled(false);
        m_jbtnReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReservationsActionPerformed(evt);
            }
        });

        m_jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/reload.png"))); // NOI18N
        m_jbtnRefresh.setText("Reload");
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });

        m_jbtnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/logoutapp.png"))); // NOI18N
        m_jbtnLogout.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnLogout.setFocusPainted(false);
        m_jbtnLogout.setFocusable(false);
        m_jbtnLogout.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnLogout.setRequestFocusEnabled(false);
        m_jbtnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(m_jbtnReservations)
                .addGap(5, 5, 5)
                .addComponent(m_jbtnRefresh)
                .addGap(5, 5, 5)
                .addComponent(m_jText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(m_jbtnLogout)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(m_jbtnReservations))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jbtnRefresh)
                    .addComponent(m_jbtnLogout)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(m_jText))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 20, Short.MAX_VALUE))
        );

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(m_jPanelMap, "map");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed

        m_PlaceClipboard = null;
        customer = null;
        loadTickets();     
        printState();   
        
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed


   private class RefreshTickets implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
         loadTickets();
          }

    }
   
 
   
    private void m_jbtnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReservationsActionPerformed

        showView("res");
        m_jreservations.activate();
        
    }//GEN-LAST:event_m_jbtnReservationsActionPerformed

    private void m_jbtnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnLogoutActionPerformed
        m_RootApp = (JRootApp) m_App;
        m_RootApp.closeAppView();
    }//GEN-LAST:event_m_jbtnLogoutActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnLogout;
    private javax.swing.JButton m_jbtnRefresh;
    private javax.swing.JButton m_jbtnReservations;
    // End of variables declaration//GEN-END:variables
    
}
