/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package traitgen.application.form.shivoni.gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.Dimension;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import shivoni.model.RepairProductItem;

/**
 *
 * @author Anne
 */
public class RepairAdd extends javax.swing.JFrame {

    private HashMap<String, RepairProductItem> repairProductMap = new HashMap<>();

    public RepairAdd() {
        initComponents();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        setExtendedState(MAXIMIZED_BOTH);
        
        loadInvoiceItems();
        generateRepairProductId();
        jTextField2.setText("shivoni@gmail.com");
    }

    public JTextField getjTextField3() {
        return jTextField3;
    }

    private String repairID;

    private void generateRepairProductId() {

        long timestamp = System.currentTimeMillis();
        long randomNum = new Random().nextInt(9999);

        String repairID = timestamp + "RI" + randomNum;
        this.repairID = repairID;
        jTextField1.setText(String.valueOf(repairID));

    }

    private void loadInvoiceItems() {

        try {
            String query = "SELECT * FROM `invoice_item` INNER JOIN `stock` ON `stock`.`id`=`invoice_item`.`stock_id` INNER JOIN `invoice` ON `invoice`.`id`=`invoice_item`.`invoice_id`  INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` INNER JOIN `warranty` ON `warranty`.`id`=`product`.`warranty_id` INNER JOIN `customer` ON `customer`.`mobile`=`invoice`.`customer_mobile` ";

            String iid = jTextField3.getText();
            String customer = jTextField6.getText();

            List<String> conditions = new ArrayList<>();

            if (!iid.isEmpty()) {
                conditions.add("`invoice`.`id` LIKE '" + iid + "%'");
            }
            
            if(!customer.isEmpty()){
                conditions.add("`invoice`.`customer_mobile` LIKE '"+customer+"%'  ");
            }
                

            conditions.add("`return_statement`='yes'");

            if (!conditions.isEmpty()) {
                query += " WHERE" + String.join(" AND", conditions);
            }

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0);

            while (rs.next()) {
                Vector<String> vector = new Vector<>();

                vector.add(rs.getString("invoice.id"));
                vector.add(rs.getString("invoice.employee_email"));
                vector.add(rs.getString("stock.id"));
                vector.add(rs.getString("product.name"));
                vector.add(rs.getString("stock.selling_price"));
                vector.add(rs.getString("invoice_item.qty"));
                vector.add(rs.getString("warranty.period"));
                vector.add(rs.getString("warranty.conditions"));
                vector.add(rs.getString("invoice.date"));

                ResultSet rs2 = MySQL.executeSearch("select * from `repair_request_item` where `invoice_id`='" + rs.getString("invoice.id") + "' and `stock_id`='" + rs.getString("stock.id") + "'  ");

                if (rs2.next()) {
                    vector.add("yes");
                } else {
                    vector.add("no");
                }

                vector.add(rs.getString("invoice.customer_mobile"));

                dtm.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void invokeLoadInvoiceItems() {
        loadInvoiceItems();

    }

    private void loadOldRepairInvoiceItems() {

        try {

            int row = jTable2.getSelectedRow();
            //  String oldRepairId = String.valueOf(jTable2.getValueAt(row, 0));
            String oldRepairInvoiceId = String.valueOf(jTable2.getValueAt(row, 1));
            String oldRepairStockId = String.valueOf(jTable2.getValueAt(row, 2));

            String query = "SELECT * FROM `invoice_item` INNER JOIN `stock` ON `stock`.`id`=`invoice_item`.`stock_id` INNER JOIN `invoice` ON `invoice`.`id`=`invoice_item`.`invoice_id`  INNER JOIN `product` ON `product`.`id`=`stock`.`product_id` INNER JOIN `warranty` ON `warranty`.`id`=`product`.`warranty_id` WHERE `invoice_id`='" + oldRepairInvoiceId + "' AND `stock_id`='" + oldRepairStockId + "' ";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0);

            while (rs.next()) {
                Vector<String> vector = new Vector<>();

                vector.add(rs.getString("invoice.id"));
                vector.add(rs.getString("invoice.employee_email"));
                vector.add(rs.getString("stock.id"));
                vector.add(rs.getString("product.name"));
                vector.add(rs.getString("stock.selling_price"));
                vector.add(rs.getString("invoice_item.qty"));
                vector.add(rs.getString("warranty.period"));
                vector.add(rs.getString("warranty.conditions"));
                vector.add(rs.getString("invoice.date"));

                ResultSet rs2 = MySQL.executeSearch("select * from `repair_request_item` where `invoice_id`='" + rs.getString("invoice.id") + "' and `stock_id`='" + rs.getString("stock.id") + "'  ");

                if (rs2.next()) {
                    vector.add("yes");
                } else {
                    vector.add("no");
                }

                dtm.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadRepairProductItem() {
        DefaultTableModel dtm2 = (DefaultTableModel) jTable3.getModel();
        dtm2.setRowCount(0);

        for (RepairProductItem repairPI : repairProductMap.values()) {
            Vector<String> vector = new Vector<>();

            vector.add(repairPI.getRepairID());
            vector.add(String.valueOf(repairPI.getStockId()));
            vector.add(repairPI.getProblem());
            vector.add(String.valueOf(repairPI.getQty()));
            vector.add(String.valueOf(repairPI.getInvoiceId()));
            vector.add(repairPI.getCusEmail());
            dtm2.addRow(vector);

        }

    }

    private void loadOldRepairProducts(String oldInvoiceId) {

        try {

            String query = "SELECT * FROM `repair_request_item` inner join `repair_status` on `repair_status`.`id`=`repair_request_item`.`repair_status_id` inner join `repair_request` on `repair_request`.`repair_id`=`repair_request_item`.`repair_request_repair_id` WHERE `repair_request_item`.`invoice_id` like '" + oldInvoiceId + "%'";

            List<String> conditions = new ArrayList();

            int row = jTable1.getSelectedRow();
            if (row != -1) {
                String oldStockId = String.valueOf(jTable1.getValueAt(row, 2));

                conditions.add("`repair_request_item`.`stock_id` LIKE '" + oldStockId + "%' ");

                if (!conditions.isEmpty()) {
                    query += "AND" + String.join(" AND", conditions);
                }
            }

            query += "ORDER BY `repair_request_item`.`repair_iteration` DESC ";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel dtm = (DefaultTableModel) jTable2.getModel();
            dtm.setRowCount(0);

            while (rs.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(rs.getString("repair_request.repair_id"));
                vector.add(rs.getString("repair_request_item.invoice_id"));
                vector.add(rs.getString("stock_id"));
                vector.add(rs.getString("repair_status.name"));
                vector.add(rs.getString("repair_request_item.repair_iteration"));
                dtm.addRow(vector);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Repair");
        setAlwaysOnTop(true);
        getContentPane().setLayout(new java.awt.GridLayout(2, 0));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(500, 184));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel9.setLayout(new java.awt.GridLayout(3, 0, 0, 10));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setText("Old Repair");
        jPanel9.add(jLabel6);

        jTextField5.setBorder(javax.swing.BorderFactory.createTitledBorder("Search by Invoice Id"));
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });
        jPanel9.add(jTextField5);

        jPanel3.add(jPanel9, java.awt.BorderLayout.PAGE_START);

        jPanel11.setLayout(new javax.swing.OverlayLayout(jPanel11));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repair Id", "Invoice ID", "Stock ID", "Repair Status", "Repair Term"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable2);

        jPanel11.add(jScrollPane3);

        jPanel3.add(jPanel11, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3, java.awt.BorderLayout.LINE_END);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel4.setLayout(new java.awt.GridLayout(3, 2, 10, 10));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Log New Repair");
        jPanel4.add(jLabel1);

        jTextField1.setEditable(false);
        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("Repair ID"));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel4.add(jTextField1);

        jTextField3.setBorder(javax.swing.BorderFactory.createTitledBorder("Invoice ID"));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });
        jPanel4.add(jTextField3);

        jTextField6.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer"));
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });
        jPanel4.add(jTextField6);

        jTextField2.setEditable(false);
        jTextField2.setBorder(javax.swing.BorderFactory.createTitledBorder("Employee"));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jPanel4.add(jTextField2);

        jTextField4.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantity"));
        jPanel4.add(jTextField4);

        jPanel10.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel8.setLayout(new javax.swing.OverlayLayout(jPanel8));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice ID", "Employee Email(sold)", "StockID", "Product Name", "Price", "Qty", "Warranty", "W.Conditions", "P.Date", "Repair History", "customer Mobile"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
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

        jPanel8.add(jScrollPane2);

        jPanel10.add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel10, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel5.setPreferredSize(new java.awt.Dimension(881, 80));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("Problem"));
        jScrollPane1.setViewportView(jTextArea1);

        jButton4.setText("Add New Repair");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel12.setPreferredSize(new java.awt.Dimension(881, 50));

        jButton3.setText("Clear All");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setText("Save New Repair");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(244, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.add(jPanel12, java.awt.BorderLayout.PAGE_END);

        jPanel13.setLayout(new javax.swing.OverlayLayout(jPanel13));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repair ID", "Stock ID", "Problem", "Qty", "Invoice ID", "Customer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTable3);

        jPanel13.add(jScrollPane4);

        jPanel2.add(jPanel13, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int row = jTable1.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            boolean canAdd = false;

            if (jTable2.getRowCount() > 0) {
                jTable2.setRowSelectionInterval(0, 0);
                String oldRepairStatus = String.valueOf(jTable2.getValueAt(0, 3));
                if (!oldRepairStatus.equals("Collected")) {
                    JOptionPane.showMessageDialog(this, "This product is still in repair process", "Warning", JOptionPane.WARNING_MESSAGE);

                } else {
                    canAdd = true;
                }
            } else {
                canAdd = true;
            }

            if (canAdd) {
                if (jTable3.getRowCount() > 0 && !String.valueOf(jTable3.getValueAt(0, 5)).equals(String.valueOf(jTable1.getValueAt(row, 10)))) {

                    JOptionPane.showMessageDialog(this, "Please add repair items from same Invoice Id", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (jTextField4.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please type quantity", "Warning", JOptionPane.WARNING_MESSAGE);
                } else if (!jTextField4.getText().matches("^\\d+$")) {
                    JOptionPane.showMessageDialog(this, "Invalid Quantity ", "Warning", JOptionPane.WARNING_MESSAGE);
                } else if (jTextArea1.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please type the problem", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {

                    Double repairqty = Double.parseDouble(jTextField4.getText());
                    Double invoiceItemQty = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 5)));

                    if (repairqty > invoiceItemQty) {
                        JOptionPane.showMessageDialog(this, "Add valid qty less than " + invoiceItemQty, "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {

                        RepairProductItem rpi = new RepairProductItem();
                        rpi.setRepairID(repairID);
                        rpi.setEmpEmail(String.valueOf(jTextField2.getText()));
                        rpi.setInvoiceId(String.valueOf(jTable1.getValueAt(row, 0)));
                        rpi.setProblem(jTextArea1.getText());
                        rpi.setQty(jTextField4.getText());
                        rpi.setStockId(String.valueOf(jTable1.getValueAt(row, 2)));
                        rpi.setCusEmail(String.valueOf(jTable1.getValueAt(row, 10)));

                        String uniqueKey = String.valueOf(jTable1.getValueAt(row, 0)) + "-" + String.valueOf(jTable1.getValueAt(row, 2));

                        if (repairProductMap.get(uniqueKey) == null) {
                            repairProductMap.put(uniqueKey, rpi);
                        } else {
                            RepairProductItem found = repairProductMap.get(uniqueKey);

                            int option = JOptionPane.showConfirmDialog(this, "Do you want to update quantity of the repair product: " + String.valueOf(jTable1.getValueAt(row, 3)), "Warning ", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                            if (option == JOptionPane.YES_OPTION) {
                                Double newQty = Double.parseDouble(found.getQty()) + repairqty;

                                if (newQty > invoiceItemQty) {
                                    JOptionPane.showMessageDialog(this, "Add valid qty less than " + invoiceItemQty, "Warning", JOptionPane.WARNING_MESSAGE);
                                } else {

                                    found.setQty(String.valueOf(newQty));
                                }
                            }

                        }

                        loadRepairProductItem();
                        jTextArea1.setText("");
                        // jTextField4.setText("");
                        jTextField3.setText("");
                    }
                }
            }
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed

    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        loadInvoiceItems();
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        loadInvoiceItems();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        jTextField4.setText("1");

        int row = jTable1.getSelectedRow();

        String invoiceId = String.valueOf(jTable1.getValueAt(row, 0));

        jTextField3.setText(String.valueOf(jTable1.getValueAt(row, 0)));
        jTextField6.setText(String.valueOf(jTable1.getValueAt(row, 10)));

        if (evt.getClickCount() == 1) {

            loadOldRepairProducts(invoiceId);

        } else if (evt.getClickCount() == 2) {

            if (String.valueOf(jTable1.getValueAt(row, 9)).equals("yes")) {

                int option = JOptionPane.showConfirmDialog(this, "Do you want to view repair history?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (option == JOptionPane.YES_OPTION) {

                    if (fr == null || !fr.isDisplayable()) {
                        fr = new FindRepairs();
                        fr.setVisible(true);
                        fr.getjTextField1().setText(invoiceId);
                        fr.getjTextField1().setEnabled(false);

                    } else {
                        fr.toFront();
                        fr.requestFocus();

                    }
                }

            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        loadOldRepairProducts(jTextField5.getText());

        jTable1.clearSelection();
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked

        int row = jTable2.getSelectedRow();

        String status = String.valueOf(jTable2.getValueAt(row, 3));

        if (status.equals("Collected")) {

            loadOldRepairInvoiceItems();
        } else {

            JOptionPane.showMessageDialog(this, "Product is not valid for repair", "Warning", JOptionPane.WARNING_MESSAGE);

        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        clearAll();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        if (jTextField4.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please add a repair product", "Warning", JOptionPane.WARNING_MESSAGE);

        } else {

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            String empEmail = jTextField2.getText();

            try {

                //insert into repair request table
                MySQL.executeIUD("INSERT INTO `repair_request` (`repair_id`,`employee_email`,`customer_mobile`) VALUES ('" + this.repairID + "','" + empEmail + "','" + jTextField6.getText() + "') ");

                for (RepairProductItem rpi : repairProductMap.values()) {

                    int repairIteration = 1;

                    ResultSet rs = MySQL.executeSearch("SELECT `repair_iteration` from `repair_request_item` where `stock_id`='" + rpi.getStockId() + "' AND `invoice_id`='" + rpi.getInvoiceId() + "' ORDER BY `submit_date` ASC  ");

                    while (rs.next()) {
                        repairIteration = rs.getInt("repair_iteration") + 1;
                    }

                    //insert into repair request item table
                    MySQL.executeIUD("insert into `repair_request_item` (`problem`,`qty`,`submit_date`,`repair_status_id`,`stock_id`,`repair_request_repair_id`,`invoice_id`,`repair_iteration`) values ('" + rpi.getProblem() + "','" + rpi.getQty() + "','" + date + "','1','" + rpi.getStockId() + "','" + rpi.getRepairID() + "','" + rpi.getInvoiceId() + "','" + repairIteration + "') ");

                }

                clearAllFinal();

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private FindRepairs fr;
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatMacDarkLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RepairAdd().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables

    private void clearAll() {

        generateRepairProductId();

        jTextField5.setText("");

        jTable1.clearSelection();
        //  jTable3.clearSelection();
        jTextField4.setText("");
        jTextField3.setText("");
        jTextArea1.setText("");
        jTextField3.setText("");
        loadInvoiceItems();
        loadRepairProductItem();
        //   repairProductMap.clear();
        loadOldRepairProducts(jTextField5.getText());

    }

    private void clearAllFinal() {
        
          generateRepairProductId();

        jTextField5.setText("");

        jTable1.clearSelection();
       jTable3.clearSelection();
        jTextField4.setText("");
        jTextField3.setText("");
        jTextArea1.setText("");
        jTextField3.setText("");
        jTextField6.setText("");
        loadInvoiceItems();
        loadRepairProductItem();
        repairProductMap.clear();
        loadOldRepairProducts(jTextField5.getText());
        
    }
}
