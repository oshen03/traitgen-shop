/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package traitgen.application.form.shivoni.gui;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.MySQL;

/**
 *
 * @author VICTUS
 */
public class FindRepairs extends javax.swing.JFrame {

    /**
     * Creates new form FindRepairs
     */
    public FindRepairs() {
        initComponents();
        loadRepairProducts();
    }

    public void foreignLoadRepairProducts(){
        loadRepairProducts();
        
    }
    
    
    public JTextField getjTextField1(){
        return jTextField1;
    }
    
    private void loadRepairProducts(){
        
        
        String repairID = jTextField5.getText();
        String empEmail = jTextField6.getText();
        String invoiceId = jTextField1.getText();
        String cusMobile = jTextField2.getText();
        
       String query = "SELECT * FROM `repair_request_item` " +
    "INNER JOIN `repair_request` ON `repair_request_item`.`repair_request_repair_id` = `repair_request`.`repair_id` " +
    "INNER JOIN `stock` ON `stock`.`id` = `repair_request_item`.`stock_id` " +
    "INNER JOIN `invoice` ON `invoice`.`id` = `repair_request_item`.`invoice_id` " +
    "INNER JOIN `product` ON `product`.`id` = `stock`.`product_id` " +
    "INNER JOIN `warranty` ON `warranty`.`id` = `product`.`warranty_id` " +
    "INNER JOIN `repair_status` ON `repair_request_item`.`repair_status_id` = `repair_status`.`id` " +
    "INNER JOIN ( " +
        "SELECT `invoice_id`, `stock_id`, MAX(`repair_iteration`) AS `max_iteration` " +
        "FROM `repair_request_item` " +
        "GROUP BY `invoice_id`, `stock_id` " +
    ") AS latest_repair ON `repair_request_item`.`invoice_id` = latest_repair.`invoice_id` " +
    "AND `repair_request_item`.`stock_id` = latest_repair.`stock_id` " +
    "AND `repair_request_item`.`repair_iteration` = latest_repair.`max_iteration`";

        
        List<String> conditions = new ArrayList<>();
        
        if(!repairID.isEmpty()){
            conditions.add("`repair_id` LIKE '"+repairID+"%' ");
        }
        
        if(!empEmail.isEmpty()){
            conditions.add("`repair_request`.`employee_email` LIKE '"+empEmail+"%' ");
        }
        
         if(!invoiceId.isEmpty()){
            conditions.add("`repair_request_item`.`invoice_id` LIKE '"+invoiceId+"%' ");
        }
         
          if(!cusMobile.isEmpty()){
            conditions.add("`repair_request`.`customer_mobile` LIKE '"+cusMobile+"%' ");
        }
         
        
        String sort = String.valueOf(jComboBox3.getSelectedItem());
        if(sort == "Select"){
           
        }else if(sort == "Pending"){
            conditions.add("`repair_request_item`.`repair_status_id`='1'");
        }else if(sort =="Checked"){
             conditions.add("`repair_request_item`.`repair_status_id`='2'");
        }else if(sort =="Completed"){
             conditions.add("`repair_request_item`.`repair_status_id`='3'");
        }else if(sort =="Collected"){
             conditions.add("`repair_request_item`.`repair_status_id`='4'");
        }else if(sort =="Canceled"){
             conditions.add("`repair_request_item`.`repair_status_id`='5'");
        }
        
        //else if(sort =="with Warenty"){
       //      conditions.add("`repair_request_item`.`repair_status_id`='6'");
       // }
            
        
        
        
        if(!conditions.isEmpty()){
            query += " WHERE" +String.join(" AND", conditions);
        }
        
        try {
            
            ResultSet rs = MySQL.executeSearch(query);
            
            DefaultTableModel dtm = (DefaultTableModel) jTable2.getModel();
            dtm.setRowCount(0);
            
            while(rs.next()){
                Vector<String> vector = new Vector<>();
                vector.add(rs.getString("repair_request.repair_id"));
                 vector.add(rs.getString("repair_request_item.invoice_id"));
                vector.add(rs.getString("repair_request_item.stock_id"));
               
                vector.add(rs.getString("product.name"));
                vector.add(rs.getString("repair_request_item.submit_date"));
                
                vector.add(rs.getString("repair_request.employee_email"));
                vector.add(rs.getString("repair_request_item.problem"));
                vector.add(rs.getString("warranty.period"));
                vector.add(rs.getString("invoice.date"));
                vector.add(rs.getString("repair_status.name"));
                vector.add(rs.getString("repair_request_item.qty"));
                vector.add(rs.getString("repair_request_item.repair_iteration"));
                vector.add(rs.getString("repair_request.customer_mobile"));
                
                dtm.addRow(vector);
                
               
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    
    
    
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jTextField2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel9.setText("Find Repair");

        jLabel10.setText("Repair ID");

        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });

        jLabel1.setText("Invoice ID");

        jLabel11.setText("Employee email ");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel12.setText("Status");

        jLabel2.setText("Customer mobile");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "Pending", "Checked", "Completed", "Collected", "Canceled" }));
        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jButton3.setText("Clear All");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addComponent(jTextField1))
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox3, 0, 157, Short.MAX_VALUE)
                    .addComponent(jTextField2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setLayout(new javax.swing.OverlayLayout(jPanel2));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repaire ID", "Invoice ID", "Stock ID", "Product Name", "Submit Date", "Employee Email", "Problem", "Warranty", " Purchased Date", "Status", "Quantity", "Repair Term", "Customer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
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
        jScrollPane2.setViewportView(jTable2);

        jPanel2.add(jScrollPane2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        // TODO add your handling code here:
        loadRepairProducts();
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        loadRepairProducts();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        // TODO add your handling code here:
        loadRepairProducts();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
        loadRepairProducts();
    }//GEN-LAST:event_jComboBox3ItemStateChanged

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        loadRepairProducts();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jTextField5.setText("");
        jTextField6.setText("");
        jTable2.clearSelection();
        jComboBox3.setSelectedIndex(0);
        jTextField1.setEnabled(true);
        jTextField1.setText("");
    }//GEN-LAST:event_jButton3ActionPerformed

    private RepairCompletion rc;
    
    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int row = jTable2.getSelectedRow();

        if(evt.getClickCount()==2){

            if(rc==null || !rc.isDisplayable()){
                rc = new RepairCompletion(this,String.valueOf(jTable2.getValueAt(row, 0)),String.valueOf(jTable2.getValueAt(row, 1)),String.valueOf(jTable2.getValueAt(row, 2)),String.valueOf(jTable2.getValueAt(row, 3)),String.valueOf(jTable2.getValueAt(row, 4)),String.valueOf(jTable2.getValueAt(row, 5)),String.valueOf(jTable2.getValueAt(row, 6)),String.valueOf(jTable2.getValueAt(row, 7)),String.valueOf(jTable2.getValueAt(row, 8)),String.valueOf(jTable2.getValueAt(row, 9)),String.valueOf(jTable2.getValueAt(row, 12)) );
                rc.setVisible(true);
                this.setEnabled(false);

                rc.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent){

                        setEnabled(true);
                        toFront();

                    }
                });

            }else{
                rc.toFront();
                rc.requestFocus();
            }

        }

    }//GEN-LAST:event_jTable2MouseClicked

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
            java.util.logging.Logger.getLogger(FindRepairs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FindRepairs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FindRepairs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FindRepairs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FindRepairs().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
