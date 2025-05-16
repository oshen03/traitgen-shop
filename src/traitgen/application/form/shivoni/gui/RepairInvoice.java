/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package traitgen.application.form.shivoni.gui;

import java.awt.Color;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import shivoni.model.RepairInvoiceItem;


/**
 *
 * @author Anne
 */
public class RepairInvoice extends javax.swing.JFrame {

    private String repairInvoiceId;
    private double total = 0;
    private double balance = 0;
    private double payment = 0;

    String repairRequestItemId;
    String customerEmail;

    private static HashMap<String, RepairInvoiceItem> repairInvocieItemMap = new HashMap<>();
    private static HashMap<String, String> paymentMethodMap = new HashMap<>();

    public RepairInvoice(String rid, String sid, String pb, String invoiceID, String wStatus, String customer, String product) {
        initComponents();
        generateRepairId();
        loadPaymentMethod();

        //auto
        jTextField2.setText("shivoni@gmail.com");

        jTextField3.setText(rid);
        jTextField4.setText(sid);
        jTextArea1.setText(pb);
        jTextField7.setText(invoiceID);
        jTextField11.setText(product);
        if (wStatus.equals("Warranty Available")) {
            jLabel13.setForeground(Color.GREEN);

        } else {
            jLabel13.setForeground(Color.RED);
            jTextField5.setText("None");
        }
        jLabel13.setText(wStatus);
        jTextField8.setText(customer);

        jButton3.setEnabled(false);

        try {
            ResultSet rs = MySQL.executeSearch("SELECT `id`,`customer`.`email` FROM `repair_request_item` INNER JOIN `repair_request` ON `repair_request_item`.`repair_request_repair_id`=`repair_request`.`repair_id` INNER JOIN `customer` ON `repair_request`.`customer_mobile`=`customer`.`mobile` WHERE `repair_request_repair_id`='" + rid + "' AND  `invoice_id`='" + invoiceID + "' AND `stock_id`='" + sid + "' ");

            while (rs.next()) {
                this.repairRequestItemId = rs.getString("id");
                this.customerEmail = rs.getString("customer.email");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        jTextField9.setText(repairRequestItemId);
        jTextField10.setText(customerEmail);
    }

    public JTextField getjTextField5() {
        return jTextField5;
    }

    public JTextField getjTextField6() {
        return jTextField6;
    }

    public JFormattedTextField getjFormattedTextField1() {
        return jFormattedTextField1;
    }

    private void generateRepairId() {

        long timestamp = System.currentTimeMillis();
        long randomNum = new Random().nextInt(9999);

        String repairInvoiceID = timestamp + "-RII-" + randomNum;
        this.repairInvoiceId = repairInvoiceID;
        jTextField1.setText(String.valueOf(repairInvoiceId));

    }

    private void loadRepairInvocieTable() {

        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);
        total = 0;

        for (RepairInvoiceItem repairInvoiceItem : repairInvocieItemMap.values()) {

            Vector<String> vector = new Vector<>();
            vector.add(jTextField3.getText());
            vector.add(repairInvoiceItem.getServiceId());
            vector.add(repairInvoiceItem.getServiceName());
            vector.add(String.valueOf(repairInvoiceItem.getServiceCharge()));
            dtm.addRow(vector);

            total += repairInvoiceItem.getServiceCharge();
            jFormattedTextField2.setText(String.valueOf(total));

        }

    }

    private void calculate() {
        total = Double.parseDouble(jFormattedTextField2.getText());

        String paymentMethod = String.valueOf(jComboBox1.getSelectedItem());

        if (jFormattedTextField3.getText().isEmpty()) {
            payment = 0;
        } else {
            payment = Double.parseDouble(jFormattedTextField3.getText());
        }

        if (paymentMethod.equals("Cash")) {
            jFormattedTextField3.setEditable(true);

            balance = payment - total;

            if (balance < 0) {
                jButton3.setEnabled(false);
            } else {
                jButton3.setEnabled(true);
            }

        } else {
            payment = total;
            balance = 0;
            jFormattedTextField3.setText(String.valueOf(payment));
            jFormattedTextField3.setEditable(false);
            jButton3.setEnabled(true);
        }

        jFormattedTextField5.setText(String.valueOf(balance));

    }

    private void loadPaymentMethod() {
        try {

            ResultSet resulSet = MySQL.executeSearch("SELECT * FROM `payment_method`");
            Vector<String> vector = new Vector();
            // vector.add("Select");

            while (resulSet.next()) {
                vector.add(resulSet.getString("name"));
                paymentMethodMap.put(resulSet.getString("name"), resulSet.getString("id"));
            }

            DefaultComboBoxModel dcm = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(dcm);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void sendInvoiceRepair(String toEmail) {
//
//       String fromEmail = "wasanthasanjaya1234@gmail.com";
//    String password = "gmlp usso cbpp snzr";
//
//    Properties properties = new Properties();
//    properties.put("mail.smtp.auth", "true");
//    properties.put("mail.smtp.starttls.enable", "true");
//    properties.put("mail.smtp.host", "smtp.gmail.com");
//    properties.put("mail.smtp.port", "587");
//
//    Session session = Session.getInstance(properties, new Authenticator() {
//        protected PasswordAuthentication getPasswordAuthentication() {
//            return new PasswordAuthentication(fromEmail, password);
//        }
//    });
//
//
//        try {
//
//            StringBuilder emailBody = new StringBuilder();
//
//            emailBody.append("<h1 style='color: #2E86C1;'>Repair Invoice</h1>")
//                    .append("<p>Dear Customer,</p>")
//                    .append("<p>Please find the details of your repair invoice below:</p>");
//
//            emailBody.append("<h3>Repair Invocie ID :" + jTextField1.getText() + "</h3>")
//                    .append("<h3>Repair ID :" + jTextField3.getText() + "</h3>")
//                    .append("<h3>Stock ID :" + jTextField4.getText() + "</h3>")
//                    .append("<h3>Product :" + jTextField11.getText() + "</h3>");
//            
//             emailBody.append("<h2 style='color: #2874A6;'>Payment Summary</h2>")
//                 .append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>")
//                 .append("<tr><th>Total Payment</th><td>").append(jFormattedTextField2.getText()).append("</td></tr>")
//                 .append("<tr><th>Payment Method</th><td>").append(jComboBox1.getSelectedItem()).append("</td></tr>")
//                 .append("<tr><th>Paid Amount</th><td>").append(jFormattedTextField3.getText()).append("</td></tr>")
//                 .append("<tr><th>Balance</th><td>").append(jFormattedTextField5.getText()).append("</td></tr>")
//                 .append("</table>");
//
//        emailBody.append("<p>Thank you for choosing us!</p>")
//                 .append("<p><b>ABC Book Shop App</b></p>");
//
//        Message message = new MimeMessage(session);
//        message.setFrom(new InternetAddress(fromEmail));
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
//        message.setSubject("Repair Invoice");
//        message.setContent(emailBody.toString(), "text/html");
//
//            Transport.send(message);
//            JOptionPane.showMessageDialog(this, "Repair Invoice sent to "+ toEmail, "Information", JOptionPane.INFORMATION_MESSAGE);
//        } catch (MessagingException me) {
//            me.printStackTrace();
//        }
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jFormattedTextField5 = new javax.swing.JFormattedTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jButton2 = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Repair Invoice");
        setAlwaysOnTop(true);

        jPanel1.setPreferredSize(new java.awt.Dimension(888, 50));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Repaire Invoice");

        jTextField1.setEditable(false);
        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("RP Invoice ID"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 539, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(849, 200));
        jPanel2.setLayout(new java.awt.GridLayout(1, 3));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 296, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel8);

        jPanel9.setLayout(new java.awt.GridLayout(1, 2));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        jPanel9.add(jPanel16);

        jPanel15.setLayout(new java.awt.BorderLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Total Payment");
        jLabel9.setPreferredSize(new java.awt.Dimension(76, 30));
        jPanel15.add(jLabel9, java.awt.BorderLayout.PAGE_START);

        jFormattedTextField2.setEditable(false);
        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField2.setText("0");
        jFormattedTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField2ActionPerformed(evt);
            }
        });
        jPanel15.add(jFormattedTextField2, java.awt.BorderLayout.CENTER);

        jPanel9.add(jPanel15);

        jPanel2.add(jPanel9);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel7.setLayout(new java.awt.GridLayout(4, 2, 10, 10));

        jLabel11.setText("Payment Method");
        jPanel7.add(jLabel11);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jPanel7.add(jComboBox1);

        jLabel10.setText("Payment");
        jPanel7.add(jLabel10);

        jFormattedTextField3.setEditable(false);
        jFormattedTextField3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField3.setText("0");
        jFormattedTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField3KeyReleased(evt);
            }
        });
        jPanel7.add(jFormattedTextField3);

        jLabel12.setText("Balance");
        jPanel7.add(jLabel12);

        jFormattedTextField5.setEditable(false);
        jFormattedTextField5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField5.setText("0");
        jPanel7.add(jFormattedTextField5);

        jCheckBox1.setText("Email Invoice");
        jPanel7.add(jCheckBox1);

        jButton3.setText("Save Repair Invoice");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton3);

        jPanel2.add(jPanel7);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel3.setPreferredSize(new java.awt.Dimension(350, 125));
        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        jPanel5.setLayout(new java.awt.GridLayout(7, 2, 10, 10));

        jLabel4.setText("Repaire ID");
        jPanel5.add(jLabel4);

        jTextField3.setEditable(false);
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jPanel5.add(jTextField3);

        jLabel19.setText("Product");
        jPanel5.add(jLabel19);

        jTextField11.setEditable(false);
        jPanel5.add(jTextField11);

        jLabel5.setText("Stock ID");
        jPanel5.add(jLabel5);

        jTextField4.setEditable(false);
        jPanel5.add(jTextField4);

        jLabel15.setText("Invoice ID");
        jPanel5.add(jLabel15);

        jTextField7.setEditable(false);
        jPanel5.add(jTextField7);

        jLabel17.setText("Repair Request Item ID");
        jPanel5.add(jLabel17);

        jTextField9.setEditable(false);
        jPanel5.add(jTextField9);

        jLabel16.setText("Customer mobile");
        jPanel5.add(jLabel16);

        jTextField8.setEditable(false);
        jPanel5.add(jTextField8);

        jLabel18.setText("Customer Email");
        jPanel5.add(jLabel18);

        jTextField10.setEditable(false);
        jPanel5.add(jTextField10);

        jPanel3.add(jPanel5);

        jLabel6.setText("Problem");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Select Service");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.add(jPanel6);

        getContentPane().add(jPanel3, java.awt.BorderLayout.LINE_END);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel10.setLayout(new java.awt.GridLayout(1, 3));

        jPanel14.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel14.setLayout(new java.awt.GridLayout(2, 0, 0, 10));

        jTextField2.setEditable(false);
        jTextField2.setBorder(javax.swing.BorderFactory.createTitledBorder("Employee"));
        jPanel14.add(jTextField2);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Warranty Status");
        jPanel14.add(jLabel13);

        jPanel10.add(jPanel14);

        jPanel13.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel13.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        jTextField5.setEditable(false);
        jTextField5.setBorder(javax.swing.BorderFactory.createTitledBorder("Service"));
        jPanel13.add(jTextField5);

        jTextField6.setEditable(false);
        jTextField6.setBorder(javax.swing.BorderFactory.createTitledBorder("Service ID"));
        jPanel13.add(jTextField6);

        jPanel10.add(jPanel13);

        jPanel12.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel12.setLayout(new java.awt.GridLayout(2, 0, 0, 10));

        jFormattedTextField1.setEditable(false);
        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField1.setText("0");
        jPanel12.add(jFormattedTextField1);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("Add ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel12.add(jButton2);

        jPanel10.add(jPanel12);

        jPanel4.add(jPanel10, java.awt.BorderLayout.PAGE_START);

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel11.setLayout(new javax.swing.OverlayLayout(jPanel11));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repair Id", "Service Id", "Service Name", "Service Charge"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel11.add(jScrollPane2);

        jPanel4.add(jPanel11, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed
 private NewService service;
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        if (service == null || !service.isDisplayable()) {

            service = new NewService();
            service.setVisible(true);
            service.setRepairInvoice(this);

        } else {
            service.toFront();
            service.requestFocus();

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        calculate();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jFormattedTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField3KeyReleased
        calculate();
    }//GEN-LAST:event_jFormattedTextField3KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String repairInvoiceId = jTextField1.getText();
        String repairId = jTextField3.getText();
        String paidAmount = jFormattedTextField2.getText();
        String balance = jFormattedTextField5.getText();
        String invEmpEmail = jTextField2.getText();

        if (jCheckBox1.isSelected()) {
            //                sendInvoiceRepair(customerEmail);
            
        }

            //insert into - repair_complete_invoice
            try {
                //insert into - repair_complete_invoice
                MySQL.executeIUD("INSERT INTO `repair_complete_invoice`  (`id`,`paid_amount`,`repair_request_repair_id`,`repair_request_item_id`,`employee_email`) VALUES ('" + repairInvoiceId + "','" + paidAmount + "','" + repairId + "','" + repairRequestItemId + "','"+invEmpEmail+"') ");

                for (RepairInvoiceItem repInvItm : repairInvocieItemMap.values()) {

                    MySQL.executeIUD("INSERT INTO `repair_complete_invoice_has_service`  (`repair_complete_invoice_id`,`service_id`) VALUES ('" + repairInvoiceId + "','" + repInvItm.getServiceId() + "') ");

                }

                JOptionPane.showMessageDialog(this, "Successsfully Updated", "Success", JOptionPane.WARNING_MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
            
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (jTextField5.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a service", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (jTextField5.getText().equals("None")) {
            JOptionPane.showMessageDialog(this, "Please select a service", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            String serviceId = jTextField6.getText();
            String serviceName = jTextField5.getText();
            Double serviceCharge = Double.parseDouble(jFormattedTextField1.getText());

            RepairInvoiceItem repInvItm = new RepairInvoiceItem();
            repInvItm.setServiceId(serviceId);
            repInvItm.setServiceCharge(serviceCharge);
            repInvItm.setServiceName(serviceName);

            repairInvocieItemMap.put(serviceId, repInvItm);
        }

        loadRepairInvocieTable();
        calculate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            int row = jTable1.getSelectedRow();

            String removeServiceId = String.valueOf(jTable1.getValueAt(row, 1));

            if (repairInvocieItemMap.containsKey(removeServiceId)) {
                repairInvocieItemMap.remove(removeServiceId);
                loadRepairInvocieTable();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jFormattedTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField2ActionPerformed
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RepairInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RepairInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RepairInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RepairInvoice.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables

    private void clearAll() {
        jTextField5.setText("");
        jFormattedTextField1.setText("");
        jTextField6.setText("");
        jFormattedTextField3.setText("");
        repairInvocieItemMap.clear();
        loadRepairInvocieTable();
        generateRepairId();
        calculate();
        jFormattedTextField2.setText("0");
        jFormattedTextField3.setText("0");
        jFormattedTextField5.setText("0");
        jComboBox1.setSelectedIndex(0);

    }
}
