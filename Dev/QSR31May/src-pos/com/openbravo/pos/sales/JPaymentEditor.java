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
package com.openbravo.pos.sales;

import com.openbravo.basic.BasicException;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfoExt;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.payment.JPaymentNotifier;
import com.openbravo.pos.payment.PaymentInfoCard;
import com.openbravo.pos.payment.PaymentInfoCash;
import com.openbravo.pos.payment.PaymentInfoList;
import com.openbravo.pos.sales.shared.JTicketsBagShared;
import com.openbravo.pos.ticket.RetailTicketInfo;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.util.RoundUtils;
import com.sysfore.pos.hotelmanagement.BusinessTypeInfo;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

public class JPaymentEditor extends JDialog {

    private static boolean completed = false;
    private JPaymentNotifier m_notifier;
    public javax.swing.JDialog dEdior = null;
    private Properties dbp = new Properties();
    private DataLogicReceipts dlReceipts = null;
    private DataLogicCustomers dlCustomers = null;
    private AppView m_app;
    public String[] strings = {""};
    public DefaultListModel model = null;
    public java.util.List<DiscountRateinfo> list = null;
    public boolean updateMode = false;
    static Component parentLocal = null;
    static RetailTicketInfo tinfoLocal = null;
    public static String userRole = null;
    private static DataLogicReceipts localDlReceipts = null;
    public static JRetailPanelTicket JRetailPanelTicket;
    public static JRetailPanelTakeAway JRetailPanelTakeAway;
    private boolean enablity;
    int x = 350;
    int y = 200;
    int width = 650;
    int height = 400;
    public static String tinfotype;
    public PaymentInfoList m_aPaymentInfo;
    PaymentInfoCash cash = null;
    PaymentInfoCard card = null;
    double totalAmount = 0;
    double cashAmount = 0;
    double cardAmount = 0;
    private double m_dPaid;
    private double m_dTotal;
    private int payMode = 0;
    Logger logger = Logger.getLogger("MyLog");
    public static String retailBusinessType;
    private DataLogicSales dlsales = null;
    private String otherPaymentMode;
    private String description;
    public static boolean showMessage(Component parent, DataLogicReceipts dlReceipts, RetailTicketInfo tinfo, JRetailPanelTicket retailTicket, String businessType) {
        System.out.println("split bill sharedid testing 2" + tinfo.getSplitSharedId());
        localDlReceipts = dlReceipts;
        parentLocal = parent;
        tinfoLocal = tinfo;
        JRetailPanelTicket = retailTicket;
        retailBusinessType = businessType;
        boolean completed = showMessage(parent, dlReceipts, 1);
        return completed;

    }

    public static boolean showMessage(Component parent, DataLogicReceipts dlReceipts, RetailTicketInfo tinfo, JRetailPanelTakeAway retailTicket, String businessType) {
        System.out.println("split bill sharedid testing 2" + tinfo.getSplitSharedId());
        localDlReceipts = dlReceipts;
        parentLocal = parent;
        tinfoLocal = tinfo;
        JRetailPanelTakeAway = retailTicket;
        retailBusinessType = businessType;
        boolean completed = showMessage(parent, dlReceipts, 1);
        return completed;
    }

    public static boolean isCompleted() {
        return completed;
    }

    public static void setCompleted(boolean completed) {
        JPaymentEditor.completed = completed;
    }

    private static boolean showMessage(Component parent, DataLogicReceipts dlReceipts, int x) {

        Window window = getWindow(parent);
        JPaymentEditor myMsg;
        if (window instanceof Frame) {
            myMsg = new JPaymentEditor((Frame) window, true);
        } else {
            myMsg = new JPaymentEditor((Dialog) window, true);
        }
        boolean completed = myMsg.init(dlReceipts);
        return completed;
    }

    private JPaymentEditor(Frame frame, boolean b) {
        super(frame, true);
        setBounds(x, y, width, height);

    }

    private JPaymentEditor(Dialog dialog, boolean b) {
        super(dialog, true);
        setBounds(x, y, width, height);

    }

    private void printState() {
        m_jChangeEuros.setDoubleValue(null);
        String billamount = m_jTxtBillAmount.getText().toString();
        double billAmt = tinfoLocal.getTakeAwayTotal();
        //   Double billamt=Double.parseDouble(billamount.substring(4));
        System.out.println(billAmt + "billamt");
        Double cashValue;
        if (m_jCash.getDoubleValue() == null) {
            cashValue = 0.0;
        } else {
            cashValue = m_jCash.getDoubleValue();
        }


        //        m_dPaid = cashValue;

        Double differenceAmt = billAmt - new Double(cashValue);
        Double change = new Double(cashValue) - billAmt;
        System.out.println("totalCashCardtotalCashCard" + change);
        if (differenceAmt > 0) {
            m_jCard.setDoubleValue(differenceAmt);
        } else {
            m_jCard.setDoubleValue(null);
            m_jChangeEuros.setDoubleValue(change);
        }


    }

    private void CashActionState() {
        m_jChangeEuros.setDoubleValue(null);
        String billamount = m_jTxtBillAmount.getText().toString();
        double billAmt = tinfoLocal.getTakeAwayTotal();
        //   Double billamt=Double.parseDouble(billamount.substring(4));

        Double cashValue;
        if (m_jCash.getDoubleValue() == null) {
            cashValue = 0.0;
        } else {
            cashValue = m_jCash.getDoubleValue();
        }
        Double cardValue;
         
         if(cashValue>billAmt){
             cardValue=0.0;
            m_jCard.setDoubleValue(0.0);
         }else{
             cardValue =billAmt-cashValue;
       m_jCard.setDoubleValue(cardValue);
         }
       

        Double differenceAmt = billAmt - (new Double(cashValue) + new Double(cardValue) );
        Double change = (new Double(cashValue) + new Double(cardValue)) - billAmt;
        
        if (differenceAmt <= 0) {
            m_jChangeEuros.setDoubleValue(change);
        }
        
  if(change>cashValue){
            showMessage(this, "Change should not exceeds the entered Cash value  ");
            m_jCash.reset();
           m_jCard.reset();
           m_jChangeEuros.reset();
        }

    }
    
     private void CardActionState() {
        m_jChangeEuros.setDoubleValue(null);
        String billamount = m_jTxtBillAmount.getText().toString();
        double billAmt = tinfoLocal.getTakeAwayTotal();
        //   Double billamt=Double.parseDouble(billamount.substring(4));

        Double cashValue;
        if (m_jCash.getDoubleValue() == null) {
            cashValue = 0.0;
        } else {
            cashValue = m_jCash.getDoubleValue();
        }
        Double cardValue;
        if (m_jCard.getDoubleValue() == null) {
            cardValue = 0.0;
        } else {
            cardValue = m_jCard.getDoubleValue();
        }
        
         
       

        Double differenceAmt = billAmt - (new Double(cashValue) + new Double(cardValue) );
        Double change = (new Double(cashValue) + new Double(cardValue)) - billAmt;
        
        if (differenceAmt <= 0) {
            m_jChangeEuros.setDoubleValue(change);
        }
        
if(cardValue>(billAmt-cashValue)){
          showMessage(this, "The values entered should not exceeds the total bill amount");  
          m_jCash.reset();
           m_jCard.reset();
           m_jChangeEuros.reset();
        }else if(change>cashValue){
            showMessage(this, "Change should not exceeds the entered Cash value  ");
            m_jCash.reset();
           m_jCard.reset();
           m_jChangeEuros.reset();
        }

    }

    public boolean init(DataLogicReceipts dlReceipts) {
        initComponents();
        m_jSplit.setVisible(false);
        m_jBtnCheque.setVisible(false);
        m_jBtnStaff.setVisible(false);
        m_jBtnComplimentary.setVisible(false);
        m_jBtnVoucher.setVisible(false);
        m_jBtnVcloud.setVisible(false);
        jLabelVoucher.setVisible(false);
        m_jVoucher.setVisible(false);
        m_jLblVcloud.setVisible(false);
        m_jVcloud.setVisible(false);
        m_jVcloud.setVisible(false);
         m_jVoucher.setVisible(false);
        jVoucherPanel.setVisible(false);
        jChequePanel.setVisible(false);
        m_jTxtBillAmount.setEditable(false);
       
        if (retailBusinessType.equals("Sales")) {
             double billAmt = tinfoLocal.getTotal();
            m_jTxtBillAmount.setText(Double.toString(tinfoLocal.getTotal()));
        } else {
             double billAmt = tinfoLocal.getTakeAwayTotal();
            m_jTxtBillAmount.setText(tinfoLocal.printTakeAwayTotal());
        }
        m_jCash.addPropertyChangeListener("Edition", new CashActionPerformState());
        m_jCard.addPropertyChangeListener("Edition", new CardActionPerformState());
     //   m_jVoucher.addPropertyChangeListener("Edition", new ChangeValueState());
      //  m_jVcloud.addPropertyChangeListener("Edition", new ChangeValueState());
        System.out.println("m_jCash+m_jCard" + m_jCash.getDoubleValue());
        m_jCash.addEditorKeys(m_jkeys);
        m_jCard.addEditorKeys(m_jkeys);
        m_jVoucher.addEditorKeys(m_jkeys);
        m_jVoucherNum.addEditorKeys(m_jkeys);
        m_jChequeNum.addEditorKeys(m_jkeys);
        m_jVcloud.addEditorKeys(m_jkeys);
        m_jCash.setVisible(true);
        m_jCard.setVisible(true);
        m_jVoucherNum.setVisible(false);
       
        m_jVcloud.setEnabled(false);
        m_jCash.setEnabled(true);
        m_jCard.setEnabled(true);
        m_jVoucher.setEnabled(false);
        m_jChequeNum.setVisible(false);
        m_jTxtCash.setEditable(false);
        m_jTxtCard.setEditable(false);
        m_jTxtCash.setVisible(false);
        m_jTxtCard.setVisible(false);
        m_jTxtVoucherSplit.setVisible(false);
        m_jTxtVoucherNum.setEditable(false);
        m_jTxtChequeNum.setEditable(false);
        // m_jVcloud.setEditable(false);
        m_jVcloud.setVisible(false);
        m_jVoucher1.setVisible(false);
        m_jTxtTips.addEditorKeys(m_jkeys);
        m_jTxtTips.setVisible(false);
        m_jTxtVoucherSplit.setEditable(false);
        jLabelTips.setVisible(false);
        setTitle("Payment Editor");
        setVisible(true);
        File file = new File(System.getProperty("user.home") + "/openbravopos.properties");
        AppConfig ap = new AppConfig(file);
        ap.load();
        return completed;
    }

    private class RecalculateState implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            printState();
        }
    }

    private class CashActionPerformState implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            CashActionState();
        }
    }
     private class CardActionPerformState implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            CardActionState();
        }
    }

//    private class ChangeValueState implements PropertyChangeListener {
//
//        public void propertyChange(PropertyChangeEvent evt) {
//            ChangeState();
//        }
//    }
//
//    private void ChangeState() {
//        m_jChangeEuros.setDoubleValue(null);
//        String billamount = m_jTxtBillAmount.getText().toString();
//        double billAmt = tinfoLocal.getTotal();
//        //   Double billamt=Double.parseDouble(billamount.substring(4));
//
//        Double cashValue;
//        if (m_jCash.getDoubleValue() == null) {
//            cashValue = 0.0;
//        } else {
//            cashValue = m_jCash.getDoubleValue();
//        }
//        Double cardValue;
//        if (m_jCard.getDoubleValue() == null) {
//            cardValue = 0.0;
//        } else {
//            cardValue = m_jCard.getDoubleValue();
//        }
//
//        Double voucherValue;
//        if (m_jVoucher.getDoubleValue() == null) {
//            voucherValue = 0.0;
//        } else {
//            voucherValue = m_jVoucher.getDoubleValue();
//        }
//        Double vCloudValue;
//        if (m_jVcloud.getDoubleValue() == null) {
//            vCloudValue = 0.0;
//        } else {
//            vCloudValue = m_jVcloud.getDoubleValue();
//        }
//
//        Double differenceAmt = billAmt - (new Double(cashValue) + new Double(cardValue) + new Double(voucherValue) + new Double(vCloudValue));
//        Double change = (new Double(cashValue) + new Double(cardValue) + new Double(voucherValue) + new Double(vCloudValue)) - billAmt;
//
//        if (differenceAmt <= 0) {
//            m_jChangeEuros.setDoubleValue(change);
//        }
//
//      
//
//    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_jTxtBillAmount = new javax.swing.JTextField();
        m_jBtnCash = new javax.swing.JButton();
        m_jBtnCard = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        m_jSplit = new javax.swing.JButton();
        m_jPay = new javax.swing.JButton();
        m_jClose = new javax.swing.JButton();
        m_jkeys = new com.openbravo.editor.JEditorNumberKeys();
        jLabel4 = new javax.swing.JLabel();
        m_jChangeEuros = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jBtnStaff = new javax.swing.JButton();
        m_jBtnVoucher = new javax.swing.JButton();
        m_jBtnCheque = new javax.swing.JButton();
        jViewPanel = new javax.swing.JPanel();
        jPaymentPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_jCash = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jCard = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jTxtCash = new javax.swing.JTextField();
        m_jTxtCard = new javax.swing.JTextField();
        jLabelVoucher = new javax.swing.JLabel();
        m_jVoucher = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jTxtVoucherSplit = new javax.swing.JTextField();
        m_jLblVcloud = new javax.swing.JLabel();
        m_jVoucher1 = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jCard1 = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jVcloud = new com.openbravo.editor.JEditorCurrencyPositive();
        jVoucherPanel = new javax.swing.JPanel();
        jVLabel = new javax.swing.JLabel();
        jVNumLabel = new javax.swing.JLabel();
        m_jVoucherNum = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jTxtVoucherNum = new javax.swing.JTextField();
        m_jTxtVoucher = new javax.swing.JTextField();
        jChequePanel = new javax.swing.JPanel();
        jCLabel = new javax.swing.JLabel();
        jCNumLabel = new javax.swing.JLabel();
        m_jChequeNum = new com.openbravo.editor.JEditorCurrencyPositive();
        m_jTxtChequeNum = new javax.swing.JTextField();
        m_jTxtCheque = new javax.swing.JTextField();
        m_jBtnVcloud = new javax.swing.JButton();
        m_jTxtTips = new com.openbravo.editor.JEditorCurrencyPositive();
        jLabelTips = new javax.swing.JLabel();
        m_jBtnComplimentary = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                WindowClosing(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(711, 490));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Bill Amount");

        m_jTxtBillAmount.setFont(new java.awt.Font("Tahoma", 1, 12));

        m_jBtnCash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/Tcash.png"))); // NOI18N
        m_jBtnCash.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jBtnCash.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jBtnCash.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnCashActionPerformed(evt);
            }
        });

        m_jBtnCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/Tcard.png"))); // NOI18N
        m_jBtnCard.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jBtnCard.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jBtnCard.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnCardActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/Other Payments.png"))); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(90, 40));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        m_jSplit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/split-payment.png"))); // NOI18N
        m_jSplit.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jSplit.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jSplit.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jSplitActionPerformed(evt);
            }
        });

        m_jPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/paytoit.png"))); // NOI18N
        m_jPay.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jPay.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jPay.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jPayActionPerformed(evt);
            }
        });

        m_jClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/Tclose.png"))); // NOI18N
        m_jClose.setPreferredSize(new java.awt.Dimension(51, 42));
        m_jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseActionPerformed(evt);
            }
        });

        m_jkeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jkeysActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel4.setText("Change");

        m_jChangeEuros.setEnabled(false);

        m_jBtnStaff.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/staff.png"))); // NOI18N
        m_jBtnStaff.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jBtnStaff.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jBtnStaff.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnStaffActionPerformed(evt);
            }
        });

        m_jBtnVoucher.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/voucher.png"))); // NOI18N
        m_jBtnVoucher.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jBtnVoucher.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jBtnVoucher.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnVoucher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnVoucherActionPerformed(evt);
            }
        });

        m_jBtnCheque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cheque.png"))); // NOI18N
        m_jBtnCheque.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jBtnCheque.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jBtnCheque.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnChequeActionPerformed(evt);
            }
        });

        jPaymentPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 204, 204)));
        jPaymentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Cash");
        jPaymentPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 15, 72, 20));

        jLabel3.setText("Card");
        jPaymentPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 40, 20));

        m_jCash.setPreferredSize(new java.awt.Dimension(200, 25));
        m_jCash.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_jCashMouseClicked(evt);
            }
        });
        m_jCash.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                m_jCashFocusGained(evt);
            }
        });
        m_jCash.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jCashKeyTyped(evt);
            }
        });
        jPaymentPanel.add(m_jCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 180, -1));

        m_jCard.setPreferredSize(new java.awt.Dimension(200, 25));
        jPaymentPanel.add(m_jCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 180, -1));

        m_jTxtCash.setPreferredSize(new java.awt.Dimension(6, 25));
        jPaymentPanel.add(m_jTxtCash, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 170, -1));
        jPaymentPanel.add(m_jTxtCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 170, 25));

        jLabelVoucher.setText("Voucher");
        jPaymentPanel.add(jLabelVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 60, 40));

        m_jVoucher.setPreferredSize(new java.awt.Dimension(200, 25));
        jPaymentPanel.add(m_jVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 180, -1));

        m_jTxtVoucherSplit.setPreferredSize(new java.awt.Dimension(6, 25));
        jPaymentPanel.add(m_jTxtVoucherSplit, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 170, -1));

        m_jLblVcloud.setText("Vcloud");
        jPaymentPanel.add(m_jLblVcloud, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 154, 70, 30));

        m_jVoucher1.setPreferredSize(new java.awt.Dimension(200, 25));
        jPaymentPanel.add(m_jVoucher1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 180, -1));

        m_jCard1.setPreferredSize(new java.awt.Dimension(200, 25));
        jPaymentPanel.add(m_jCard1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 180, -1));

        m_jVcloud.setPreferredSize(new java.awt.Dimension(200, 25));
        m_jVcloud.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_jVcloudMouseClicked(evt);
            }
        });
        m_jVcloud.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                m_jVcloudFocusGained(evt);
            }
        });
        m_jVcloud.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jVcloudKeyTyped(evt);
            }
        });
        jPaymentPanel.add(m_jVcloud, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, 180, -1));

        jVoucherPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 204, 204)));
        jVoucherPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jVLabel.setText("Voucher");
        jVoucherPanel.add(jVLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 15, 90, 20));

        jVNumLabel.setText("Voucher No.");
        jVoucherPanel.add(jVNumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 58, 90, 20));

        m_jVoucherNum.setPreferredSize(new java.awt.Dimension(200, 25));
        jVoucherPanel.add(m_jVoucherNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 58, 170, -1));
        jVoucherPanel.add(m_jTxtVoucherNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 170, 25));

        m_jTxtVoucher.setEditable(false);
        m_jTxtVoucher.setPreferredSize(new java.awt.Dimension(6, 25));
        jVoucherPanel.add(m_jTxtVoucher, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 140, -1));

        jChequePanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(204, 204, 204)));
        jChequePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jCLabel.setText("Amount");
        jChequePanel.add(jCLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 15, 90, 20));

        jCNumLabel.setText("Cheque  No.");
        jChequePanel.add(jCNumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 58, 90, 20));

        m_jChequeNum.setPreferredSize(new java.awt.Dimension(200, 25));
        jChequePanel.add(m_jChequeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 58, 170, -1));
        jChequePanel.add(m_jTxtChequeNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 170, 25));

        m_jTxtCheque.setEditable(false);
        m_jTxtCheque.setPreferredSize(new java.awt.Dimension(6, 25));
        jChequePanel.add(m_jTxtCheque, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 140, -1));

        org.jdesktop.layout.GroupLayout jViewPanelLayout = new org.jdesktop.layout.GroupLayout(jViewPanel);
        jViewPanel.setLayout(jViewPanelLayout);
        jViewPanelLayout.setHorizontalGroup(
            jViewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jViewPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPaymentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 288, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(jViewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jViewPanelLayout.createSequentialGroup()
                    .add(16, 16, 16)
                    .add(jVoucherPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(13, Short.MAX_VALUE)))
            .add(jViewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jViewPanelLayout.createSequentialGroup()
                    .add(16, 16, 16)
                    .add(jChequePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(13, Short.MAX_VALUE)))
        );
        jViewPanelLayout.setVerticalGroup(
            jViewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPaymentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
            .add(jViewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jVoucherPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
            .add(jViewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jChequePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
        );

        m_jBtnVcloud.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/VCloud.png"))); // NOI18N
        m_jBtnVcloud.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnVcloud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnVcloudActionPerformed(evt);
            }
        });

        jLabelTips.setText("Tips");

        m_jBtnComplimentary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/Non-Chargable.png"))); // NOI18N
        m_jBtnComplimentary.setMaximumSize(new java.awt.Dimension(90, 40));
        m_jBtnComplimentary.setMinimumSize(new java.awt.Dimension(90, 40));
        m_jBtnComplimentary.setPreferredSize(new java.awt.Dimension(90, 40));
        m_jBtnComplimentary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBtnComplimentaryActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(m_jTxtBillAmount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(47, 47, 47)
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(m_jBtnCard, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(m_jBtnCash, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(m_jBtnVcloud, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(m_jBtnVoucher, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(m_jBtnComplimentary, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(m_jBtnStaff, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, m_jBtnCheque, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jViewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jChangeEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(m_jkeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
            .add(jPanel1Layout.createSequentialGroup()
                .add(159, 159, 159)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabelTips)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 48, Short.MAX_VALUE)
                        .add(m_jTxtTips, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(212, 212, 212))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(m_jSplit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(m_jPay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(m_jClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(182, Short.MAX_VALUE))))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {m_jChangeEuros, m_jTxtBillAmount}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jChangeEuros, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(m_jTxtBillAmount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jkeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jViewPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(m_jBtnCash, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(m_jBtnCard, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                        .add(m_jBtnCheque, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(m_jBtnStaff, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(m_jBtnComplimentary, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(m_jBtnVoucher, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_jTxtTips, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(m_jBtnVcloud, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabelTips)))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(m_jPay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(m_jClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(m_jSplit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(26, 26, 26))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 490, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 29, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleParent(this);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-642)/2, (screenSize.height-557)/2, 642, 557);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jBtnCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnCashActionPerformed
        String billAmt = m_jTxtBillAmount.getText().toString();
        if (retailBusinessType.equals("Sales")) {
            JTipsDialog.showMessage(parentLocal, dlsales, tinfoLocal.getTicketId(), tinfoLocal);
        }
        System.out.println("split bill sharedid testing 3" + tinfoLocal.getSplitSharedId());
        System.out.println("split value testing issue IN PAYMENT EDITOR " + tinfoLocal.getSplitValue());
        int res = JOptionPane.showConfirmDialog(this, "Payment of " + billAmt + " will be received as Cash.Click Ok to confirm.", AppLocal.getIntString("title.editor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            m_jPay.setEnabled(false);
            m_jClose.setEnabled(false);
            logger.info("Start Settle Bill Cash Button :" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
            logger.info("No. of Line Items during Cash Settlement : " + tinfoLocal.getLinesCount());
            m_jCash.setVisible(false);
            m_jCard.setVisible(false);
            m_jTxtCash.setVisible(true);
            m_jTxtCard.setVisible(true);
            m_jTxtCash.setText(tinfoLocal.printTakeAwayTotal());
            m_jTxtCard.setEnabled(false);
            m_jTxtCash.setEnabled(true);
            m_jVoucher.setEnabled(false);
            m_jVoucher.setVisible(false);
            m_jVcloud.setVisible(false);
            m_jTxtVoucherSplit.setVisible(true);
            m_jTxtVoucherSplit.setEnabled(false);
            if (retailBusinessType.equals("Sales")) {
                JRetailPanelTicket.cashPayment(1, tinfoLocal);// TODO add your handling code here:
            } else {
                JRetailPanelTakeAway.cashPayment(1, tinfoLocal);// TODO add your handling code here:
            }

            completed = true;
            System.out.println("within cash" + tinfoLocal.isTicketOpen());
            dispose();
            //logger.info("End Settle Bill Cash Button :"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));  
        }

    }//GEN-LAST:event_m_jBtnCashActionPerformed

    private void m_jBtnCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnCardActionPerformed
        String billAmt = m_jTxtBillAmount.getText().toString();
        if (retailBusinessType.equals("Sales")) {
            JTipsDialog.showMessage(parentLocal, dlsales, tinfoLocal.getTicketId(), tinfoLocal);
        }
        int res = JOptionPane.showConfirmDialog(this, "Payment of " + billAmt + " will be received as Card.Click Ok to confirm.", AppLocal.getIntString("title.editor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            m_jPay.setEnabled(false);
             m_jClose.setEnabled(false);
            logger.info("Start Settle Bill Card Button :" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
            logger.info("No. of Line Items during Card Settlement : " + tinfoLocal.getLinesCount());
            m_jCash.setVisible(false);
            m_jCard.setVisible(false);
            m_jTxtCash.setVisible(true);
            m_jTxtCard.setVisible(true);
            m_jTxtCash.setEnabled(false);
            m_jTxtCard.setEnabled(true);
            m_jVoucher.setVisible(false);
            m_jVcloud.setVisible(false);
            m_jTxtCard.setText(tinfoLocal.printTakeAwayTotal());
            if (retailBusinessType.equals("Sales")) {
                JRetailPanelTicket.cardPayment(1, tinfoLocal);// TODO add your handling code here:
            } else {
                JRetailPanelTakeAway.cardPayment(1, tinfoLocal);// TODO add your handling code here:
            }

            completed = true;
            dispose();
            logger.info("End Settle Bill Card Button :" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
        }
    }//GEN-LAST:event_m_jBtnCardActionPerformed

    private void m_jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseActionPerformed
        completed = false;
        dispose();
    }//GEN-LAST:event_m_jCloseActionPerformed

    private void m_jSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jSplitActionPerformed

        jVoucherPanel.setVisible(false);
        jChequePanel.setVisible(false);
        jPaymentPanel.setVisible(true);
        m_jCash.setVisible(true);
        m_jCard.setVisible(true);
       // m_jVcloud.setEnabled(true);
       m_jCash.setEnabled(true);
        m_jCard.setEnabled(true);
     //   m_jVoucher.setEnabled(true);// TODO add your handling code here:
        m_jTxtCash.setVisible(false);
        m_jTxtCard.setVisible(false);
        m_jVoucherNum.reset();
        m_jChequeNum.reset();
    //    m_jVoucher.setVisible(true);
     //   m_jVcloud.setVisible(true);
        if (retailBusinessType.equals("Sales")) {
            m_jTxtTips.setVisible(true);
            jLabelTips.setVisible(true);
        } else {
            m_jTxtTips.setVisible(false);
            jLabelTips.setVisible(false);
        }

        payMode = 3;


    }//GEN-LAST:event_m_jSplitActionPerformed

    private void m_jPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jPayActionPerformed
        double totalBillValue = 0;
        String tipAmount = m_jTxtTips.getText().toString();
        System.out.println("tipAmount---" + tipAmount);
        //this is for cheque payment
//        if (payMode == 1) {
//            String chequeNo = null;
//            Pattern pattern = Pattern.compile(".*[^0-9].*");
//            String input = m_jChequeNum.getText();
//            if (m_jChequeNum.getText().equals("") || pattern.matcher(input).matches()) {
//                showMessage(this, "Please enter the valid Cheque Number");
//            } else {
//                chequeNo = m_jChequeNum.getText();
//                m_jChequeNum.reset();
//                if (retailBusinessType.equals("Sales")) {
//                    JRetailPanelTicket.chequePayment(1, tinfoLocal, chequeNo);// TODO add your handling code here:
//                } else {
//                    JRetailPanelTakeAway.chequePayment(1, tinfoLocal, chequeNo);
//
//                }
//
//                completed = true;
//                dispose();
//            }
//
//        } //this is for voucher payment
//        else if (payMode == 2) {
//            String voucherno = null;
//            Pattern pattern = Pattern.compile(".*[^0-9].*");
//            String input = m_jVoucherNum.getText();
//            if (m_jVoucherNum.getText().equals("") || pattern.matcher(input).matches()) {
//                showMessage(this, "Please enter the valid Voucher Number");
//            } else {
//                voucherno = m_jVoucherNum.getText();
//                if (retailBusinessType.equals("Sales")) {
//                    JRetailPanelTicket.voucherPayment(1, tinfoLocal, voucherno);// TODO add your handling code here:
//                } else {
//                    JRetailPanelTakeAway.voucherPayment(1, tinfoLocal, voucherno);
//
//                }
//
//                m_jVoucherNum.reset();
//                completed = true;
//                dispose();
//            }
//
//        } //this is for split payment
//        else {
            double cashAmount = 0;
            double cardAmount = 0;
            double voucherAmount = 0;
            double vCloudAmount = 0;
            if (m_jCash.getText().equals("")) {
                cashAmount = 0;
            } else {
                try {
                    cashAmount = Double.parseDouble(m_jCash.getText());
                } catch (NumberFormatException ex) {
                    showMessage(this, "Please enter the valid cash amount");
                }
            }

            if (m_jCard.getText().equals("")) {
                cardAmount = 0;
            } else {
                try {
                    cardAmount = Double.parseDouble(m_jCard.getText());
                } catch (NumberFormatException ex) {
                    showMessage(this, "Please enter the valid card amount");
                }
            }

            if (m_jVoucher.getText().equals("")) {
                voucherAmount = 0;
            } else {
                try {
                    voucherAmount = Double.parseDouble(m_jVoucher.getText());
                } catch (NumberFormatException ex) {
                    showMessage(this, "Please enter the valid Voucher amount");
                }
            }

            if (m_jVcloud.getText().equals("")) {
                vCloudAmount = 0;
            } else {
                try {
                    vCloudAmount = Double.parseDouble(m_jVcloud.getText());
                } catch (NumberFormatException ex) {
                    showMessage(this, "Please enter the valid Vcloud amount");
                }
            }

            if (m_jCash.getText().equals("") && m_jCard.getText().equals("") && m_jVoucher.getText().equals("") && m_jVcloud.getText().equals("")) {
                showMessage(this, "Please enter the tender types");
            } else {
                totalBillValue = cashAmount + cardAmount + voucherAmount + vCloudAmount;

                if (retailBusinessType.equals("Sales")) {
                    if (!tipAmount.equals("")) {
                        tinfoLocal.setTipAmt(Double.parseDouble(tipAmount));
                    }
                    System.out.println("tinfoLocal--" + tinfoLocal.getTipAmt());
                    JRetailPanelTicket.settleBill(totalBillValue, cashAmount, cardAmount, voucherAmount, vCloudAmount);
                    // TODO add your handling code here:
                    if (JRetailPanelTicket.getClosePayment() == true) {
                        completed = true;
                        dispose();
                    }
                } else {
                   JRetailPanelTakeAway.settleBill(totalBillValue, cashAmount, cardAmount, voucherAmount, vCloudAmount);
                    // TODO add your handling code here:
                    if (JRetailPanelTakeAway.getClosePayment() == true) {
                        completed = true;
                        dispose();
                    }
                }

            }
       // }

    }//GEN-LAST:event_m_jPayActionPerformed

    private void WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_WindowClosing
        completed = false;
        dispose();
    }//GEN-LAST:event_WindowClosing

    private void m_jBtnStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnStaffActionPerformed

        String billAmt = m_jTxtBillAmount.getText().toString();
        int res = JOptionPane.showConfirmDialog(this, "Payment of " + billAmt + " will be received as Staff.Click Ok to confirm.", AppLocal.getIntString("title.editor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            m_jCash.setVisible(false);
            m_jCard.setVisible(false);
            m_jVcloud.setVisible(false);
            m_jTxtCash.setVisible(true);
            m_jTxtCard.setVisible(true);
            m_jTxtCash.setEnabled(false);
            m_jTxtCard.setEnabled(true);
            m_jTxtTips.setVisible(true);
            m_jTxtCard.setText(tinfoLocal.printTotal());
            if (retailBusinessType.equals("Sales")) {
                JRetailPanelTicket.staffPayment(1, tinfoLocal);
            } else {
                JRetailPanelTakeAway.staffPayment(1, tinfoLocal);
            }
            // TODO add your handling code here:
            completed = true;
            dispose();
        }
    }//GEN-LAST:event_m_jBtnStaffActionPerformed

    private void m_jBtnComplimentaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnComplimentaryActionPerformed
        String billAmt = m_jTxtBillAmount.getText().toString();
        int res = JOptionPane.showConfirmDialog(this, "Payment of " + billAmt + " will be received as Complimentary.Click Ok to confirm.", AppLocal.getIntString("title.editor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            m_jCash.setVisible(false);
            m_jCard.setVisible(false);
            m_jTxtCash.setVisible(true);
            m_jTxtCard.setVisible(true);
            m_jTxtCash.setEnabled(false);
            m_jTxtCard.setEnabled(true);
            m_jTxtTips.setVisible(true);
            m_jTxtVoucherSplit.setVisible(true);
            m_jTxtVoucherSplit.setEnabled(false);
            m_jVcloud.setVisible(false);
            m_jTxtCard.setText(tinfoLocal.printTotal());
            if (retailBusinessType.equals("Sales")) {
                JRetailPanelTicket.complimentaryPayment(1, tinfoLocal);
            } else {
                JRetailPanelTakeAway.complimentaryPayment(1, tinfoLocal);
            }
            // TODO add your handling code here:
            completed = true;
            dispose();
        }
    }//GEN-LAST:event_m_jBtnComplimentaryActionPerformed

    private void m_jBtnVoucherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnVoucherActionPerformed
        jPaymentPanel.setVisible(false);
        jChequePanel.setVisible(false);
        jVoucherPanel.setVisible(true);
        m_jTxtVoucher.setText(m_jTxtBillAmount.getText().toString());
        m_jTxtVoucherNum.setVisible(false);
        m_jVoucherNum.setVisible(true);        // TODO add your handling code here:
        m_jCash.reset();
        m_jCard.reset();
        m_jChequeNum.reset();
        payMode = 2;
    }//GEN-LAST:event_m_jBtnVoucherActionPerformed

    private void m_jBtnChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnChequeActionPerformed
        jPaymentPanel.setVisible(false);
        jVoucherPanel.setVisible(false);
        jChequePanel.setVisible(true);
        m_jTxtCheque.setText(m_jTxtBillAmount.getText().toString());
        m_jTxtChequeNum.setVisible(false);
        m_jChequeNum.setVisible(true);        // TODO add your handling code here:
        m_jCash.reset();
        m_jCard.reset();
        m_jVoucherNum.reset();
        payMode = 1;
    }//GEN-LAST:event_m_jBtnChequeActionPerformed

    private void m_jCashMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_jCashMouseClicked
    }//GEN-LAST:event_m_jCashMouseClicked

    private void m_jCashFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jCashFocusGained
        System.out.println("m_jCashFocusGained");
        m_jCard.setVisible(false);
        m_jTxtCard.setVisible(true);
        m_jTxtCard.setEnabled(true);
        m_jTxtVoucherSplit.setVisible(true);
        m_jTxtVoucherSplit.setEnabled(true);
        m_jTxtCard.setText(Formats.CURRENCY.formatValue(tinfoLocal.getTotal() - Double.parseDouble(m_jCash.getText())));

        // m_jCard.setEnabled(false);
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jCashFocusGained

    private void m_jCashKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jCashKeyTyped
        System.out.println("m_jCashKeyTyped");
        m_jCard.setVisible(false);
        m_jTxtCard.setVisible(true);
        m_jTxtCard.setEnabled(true);
        m_jTxtVoucherSplit.setVisible(true);
        m_jTxtVoucherSplit.setEnabled(true);
        // m_jTxtCard.setText(Formats.CURRENCY.formatValue(tinfoLocal.getTotal()-Double.parseDouble(m_jCash.getText())));
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jCashKeyTyped

    private void m_jkeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jkeysActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jkeysActionPerformed

    private void m_jBtnVcloudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBtnVcloudActionPerformed


        String billAmt = m_jTxtBillAmount.getText().toString();
        int res = JOptionPane.showConfirmDialog(this, "Payment of " + billAmt + " will be received as Vcloud.Click Ok to confirm.", AppLocal.getIntString("title.editor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            logger.info("Start Settle Bill Vcloud Button :" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
            logger.info("No. of Line Items during Vcloud Settlement : " + tinfoLocal.getLinesCount());
            m_jCash.setVisible(false);
            m_jCard.setVisible(false);
            m_jVcloud.setVisible(false);
            m_jTxtCash.setVisible(true);
            m_jTxtCard.setVisible(true);
            m_jTxtCash.setEnabled(false);
            m_jTxtCard.setEnabled(true);
            m_jTxtCard.setText(tinfoLocal.printTotal());
            if (retailBusinessType.equals("Sales")) {
                JRetailPanelTicket.vCloudPayment(1, tinfoLocal);
            } else {
                JRetailPanelTakeAway.vCloudPayment(1, tinfoLocal);
            }
            // TODO add your handling code here:
            completed = true;
            dispose();
            logger.info("End Settle Bill Vcloud Button :" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_m_jBtnVcloudActionPerformed

    private void m_jVcloudMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_jVcloudMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jVcloudMouseClicked

    private void m_jVcloudFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jVcloudFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jVcloudFocusGained

    private void m_jVcloudKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jVcloudKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jVcloudKeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String billAmt= m_jTxtBillAmount.getText().toString();
        boolean status=JOtherPaymentsPanel.showMessage(parentLocal, localDlReceipts, tinfoLocal.getTicketId(), tinfoLocal,this);
        //   int res = JOptionPane.showConfirmDialog(this, "Payment of "+billAmt+ " will be received as Vcloud.Click Ok to confirm.", AppLocal.getIntString("title.editor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        //    if (res == JOptionPane.OK_OPTION) {
        if(status){
            logger.info("Start Settle Bill Vcloud Button :"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
            logger.info("No. of Line Items during Vcloud Settlement : "+ tinfoLocal.getLinesCount());
            m_jCash.setVisible(false);
          //  m_jCboPaymentMode.setVisible(false);
            m_jCard.setVisible(false);
            m_jVcloud.setVisible(false);
            m_jTxtCash.setVisible(true);
            m_jTxtCard.setVisible(true);
            m_jTxtCash.setEnabled(false);
            m_jTxtCard.setEnabled(true);
            m_jTxtCard.setText(tinfoLocal.printTotal());
            if(retailBusinessType.equals("Sales")){
                JRetailPanelTicket.otherPayment(1,tinfoLocal,"OtherPayments",getOtherPaymentMode(),"");
            }else{
                JRetailPanelTakeAway.otherPayment(1,tinfoLocal,"OtherPayments",getOtherPaymentMode(),"");
            }
            // TODO add your handling code here:
            completed=true;
            dispose();
            logger.info("End Settle Bill Vcloud Button :"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
            //}
        }        // TODO add your handling code here:
}//GEN-LAST:event_jButton1ActionPerformed

    public void paymentDetail() {
        m_aPaymentInfo = new PaymentInfoList();
        System.out.println("paymentDetail");
        if (!m_jCash.getText().equals("")) {
            totalAmount = cashAmount + cardAmount;
            double change = totalAmount - tinfoLocal.getTotal();
            cash = new PaymentInfoCash(tinfoLocal.getTotal(), (Double.parseDouble(m_jCash.getText())), change);
            if (cash != null) {
                m_aPaymentInfo.add(cash);

            }
        }
        if (!m_jCard.getText().equals("")) {
            card = new PaymentInfoCard(tinfoLocal.getTotal(), (Double.parseDouble(m_jCard.getText())));
            if (card != null) {
                m_aPaymentInfo.add(card);

            }
        }

    }

    private void showMessage(JPaymentEditor aThis, String msg) {
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
  public String getOtherPaymentMode() {
        return otherPaymentMode;
    }

    public void setOtherPaymentMode(String otherPaymentMode) {
        this.otherPaymentMode = otherPaymentMode;
    }
    public String getOtherDescription() {
        return description;
    }

    public void setOtherDescription(String description) {
        this.description = description;
    }
    private void calculateDiscount() {
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jCLabel;
    private javax.swing.JLabel jCNumLabel;
    private javax.swing.JPanel jChequePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTips;
    private javax.swing.JLabel jLabelVoucher;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPaymentPanel;
    private javax.swing.JLabel jVLabel;
    private javax.swing.JLabel jVNumLabel;
    private javax.swing.JPanel jViewPanel;
    private javax.swing.JPanel jVoucherPanel;
    private javax.swing.JButton m_jBtnCard;
    private javax.swing.JButton m_jBtnCash;
    private javax.swing.JButton m_jBtnCheque;
    private javax.swing.JButton m_jBtnComplimentary;
    private javax.swing.JButton m_jBtnStaff;
    private javax.swing.JButton m_jBtnVcloud;
    private javax.swing.JButton m_jBtnVoucher;
    private com.openbravo.editor.JEditorCurrencyPositive m_jCard;
    private com.openbravo.editor.JEditorCurrencyPositive m_jCard1;
    private com.openbravo.editor.JEditorCurrencyPositive m_jCash;
    private com.openbravo.editor.JEditorCurrencyPositive m_jChangeEuros;
    private com.openbravo.editor.JEditorCurrencyPositive m_jChequeNum;
    private javax.swing.JButton m_jClose;
    private javax.swing.JLabel m_jLblVcloud;
    private javax.swing.JButton m_jPay;
    private javax.swing.JButton m_jSplit;
    private javax.swing.JTextField m_jTxtBillAmount;
    private javax.swing.JTextField m_jTxtCard;
    private javax.swing.JTextField m_jTxtCash;
    private javax.swing.JTextField m_jTxtCheque;
    private javax.swing.JTextField m_jTxtChequeNum;
    private com.openbravo.editor.JEditorCurrencyPositive m_jTxtTips;
    private javax.swing.JTextField m_jTxtVoucher;
    private javax.swing.JTextField m_jTxtVoucherNum;
    private javax.swing.JTextField m_jTxtVoucherSplit;
    private com.openbravo.editor.JEditorCurrencyPositive m_jVcloud;
    private com.openbravo.editor.JEditorCurrencyPositive m_jVoucher;
    private com.openbravo.editor.JEditorCurrencyPositive m_jVoucher1;
    private com.openbravo.editor.JEditorCurrencyPositive m_jVoucherNum;
    private com.openbravo.editor.JEditorNumberKeys m_jkeys;
    // End of variables declaration//GEN-END:variables

    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }
    /**
     * @return the enablity
     */
}