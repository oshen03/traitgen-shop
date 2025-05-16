/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package traitgen.application.form.shivoni.gui;

import com.sun.source.tree.ParenthesizedTree;
import java.awt.Component;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import traitgen.application.form.shivoni.gui.FindRepairs;

/**
 *
 * @author Anne
 */
public class RepairCompletion extends javax.swing.JFrame {

    private static HashMap<String, String> repairStatusMap = new HashMap<>();

    private String repairId;
    private String invoiceId;
    private String sid;
    private String productName;
    private String submitDate;
    private String empEmail;
    private String problem;
    private String warranty;
    private String purchDate;
    private String customer;
    private String repairRequestItemId;

    private JFrame parent;

    public RepairCompletion(JFrame parent, String repairId, String invoiceId, String sid, String productName, String submitDate, String empEmail, String problem, String warranty, String purchasedDate, String repairStatus, String customer) {
        initComponents();
        this.repairId = repairId;
        this.invoiceId = invoiceId;
        this.sid = sid;
        this.productName = productName;
        this.problem = problem;
        this.submitDate = submitDate;
        this.empEmail = empEmail;
        this.warranty = warranty;
        this.purchDate = purchasedDate;
        this.customer = customer;

        this.parent = parent;

        jTextField1.setText(this.repairId);
        jTextField3.setText(this.sid);
        jTextField5.setText(this.warranty);
        jTextField2.setText(this.invoiceId);
        jTextField4.setText(this.productName);
        jTextArea1.setText(this.problem);
        jTextField7.setText(this.submitDate);
        jTextField10.setText(this.empEmail);
        jTextField6.setText(this.purchDate);
        jLabel10.setText(repairStatus);
        jTextField8.setText(this.customer);

        jDateChooser1.setEnabled(false);
        jDateChooser2.setEnabled(false);

        loadRepairStatus();
        loadRepairStatusTable();
        loadRepairCompletionDetails();
        loadInvoiceEnable();

        try {

            ResultSet rs = MySQL.executeSearch("SELECT * FROM `repair_complete_invoice` INNER JOIN `repair_request_item` ON `repair_complete_invoice`.`repair_request_item_id` = `repair_request_item`.`id` WHERE `repair_complete_invoice`.`repair_request_repair_id`='" + jTextField1.getText() + "' AND `invoice_id`='" + this.invoiceId + "' AND `stock_id`='" + this.sid + "'   ");

            while (rs.next()) {
                jButton3.setEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadRepairStatus() {
        try {

            ResultSet rs = MySQL.executeSearch("select * from `repair_status`");

            Vector<String> vector = new Vector<>();
            vector.add("Select");

            while (rs.next()) {
                vector.add(rs.getString("name"));
                repairStatusMap.put(rs.getString("name"), rs.getString("id"));
            }

            DefaultComboBoxModel dcm = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(dcm);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void loadRepairStatusTable() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `repair_request_item` INNER JOIN `repair_status` ON `repair_status`.`id`=`repair_request_item`.`repair_status_id` WHERE `invoice_id`='" + this.invoiceId + "' AND `stock_id`='" + this.sid + "' ");

            DefaultTableModel dtm2 = (DefaultTableModel) jTable1.getModel();
            dtm2.setRowCount(0);

            while (rs.next()) {
                Vector<String> vector = new Vector<>();

                vector.add(rs.getString("repair_request_item.repair_request_repair_id"));
                vector.add(rs.getString("repair_request_item.stock_id"));
                vector.add(rs.getString("repair_status.name"));
                vector.add(rs.getString("repair_request_item.repair_iteration"));
                dtm2.addRow(vector);

            }

            jTable1.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                    Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    //   int row1 = jTable1.getSelectedRow();
                    // Check if the repairId matches in this row
                    String repairIdInRow = (String) table.getValueAt(row, 0); // Assuming first column contains `repair_request_repair_id`

                    if (repairIdInRow.equals(jTextField1.getText())) {
                        cell.setForeground(java.awt.Color.GREEN); // Set text color to green
                        cell.setFont(cell.getFont().deriveFont(java.awt.Font.BOLD));

                    } else {
                        cell.setForeground(java.awt.Color.BLACK); // Default color
                    }
                    return cell;
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*  private void loadTextfieldEnable(){
  *      
  *      if(jLabel10.getText().equals("Completed")){
  *           jDateChooser1.setEnabled(true);
  *      }else if(jLabel10.getText().equals("Collected")){
  *          jDateChooser2.setEnabled(true);
   *     }
    *    
  *  }
     */
    private void loadRepairCompletionDetails() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `repair_request_item` INNER JOIN `repair_status` ON `repair_status`.`id`=`repair_request_item`.`repair_status_id` WHERE `invoice_id`='" + this.invoiceId + "' AND `stock_id`='" + this.sid + "' AND `repair_request_repair_id`='" + jTextField1.getText() + "' ");

            while (rs.next()) {
                jTextArea3.setText(rs.getString("note"));
                jDateChooser1.setDate(rs.getDate("completed_date"));
                jDateChooser2.setDate(rs.getDate("collected_date"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInvoiceEnable() {
        try {
            ResultSet rs1 = MySQL.executeSearch("SELECT * FROM `repair_complete_invoice` INNER JOIN `repair_request_item` ON `repair_complete_invoice`.`repair_request_item_id` = `repair_request_item`.`id` WHERE `repair_complete_invoice`.`repair_request_repair_id`='" + jTextField1.getText() + "' AND `invoice_id`='" + this.invoiceId + "' AND `stock_id`='" + this.sid + "'   ");

            if (rs1.next()) {
                jLabel17.setText("Invoice done");
            } else {
                jLabel17.setText("Invoice not done");
            }

            String repairInvoiceStatus = jLabel17.getText();
            if (repairInvoiceStatus.equals("Invoice not done")) {
                jButton3.setEnabled(true);
                jButton1.setEnabled(true);
                jButton2.setEnabled(true);
                jButton4.setEnabled(true);
                jButton5.setEnabled(true);
                jRadioButton1.setEnabled(true);
                jRadioButton2.setEnabled(true);
            } else if (repairInvoiceStatus.equals("Invoice done")) {
                jButton1.setEnabled(false);
                jButton2.setEnabled(false);
                jButton3.setEnabled(false);
                jButton4.setEnabled(false);
                jButton5.setEnabled(false);
                jRadioButton1.setEnabled(false);
                jRadioButton2.setEnabled(false);

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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jPanel13 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Repair Completion");
        setAlwaysOnTop(true);

        jPanel3.setLayout(new java.awt.GridLayout(1, 3));

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel9.setLayout(new java.awt.GridLayout(4, 1, 0, 10));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Repair Completion Details");
        jPanel9.add(jLabel1);

        jTextField1.setEditable(false);
        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("Repair ID"));
        jPanel9.add(jTextField1);

        jTextField2.setEditable(false);
        jTextField2.setBorder(javax.swing.BorderFactory.createTitledBorder("Invoice ID"));
        jPanel9.add(jTextField2);

        jTextField8.setEditable(false);
        jTextField8.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer"));
        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });
        jPanel9.add(jTextField8);

        jPanel3.add(jPanel9);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel10.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jTextField3.setEditable(false);
        jTextField3.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock ID"));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jPanel10.add(jTextField3);

        jTextField4.setEditable(false);
        jTextField4.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Name"));
        jPanel10.add(jTextField4);

        jTextField5.setEditable(false);
        jTextField5.setForeground(new java.awt.Color(255, 0, 0));
        jTextField5.setBorder(javax.swing.BorderFactory.createTitledBorder("Warranty"));
        jPanel10.add(jTextField5);

        jTextField6.setEditable(false);
        jTextField6.setForeground(new java.awt.Color(255, 0, 0));
        jTextField6.setBorder(javax.swing.BorderFactory.createTitledBorder("Purches Date"));
        jPanel10.add(jTextField6);

        jPanel3.add(jPanel10);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton4.setText("Clear All");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));

        jLabel10.setText("Not Completed");
        jLabel10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jLabel10FocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.add(jPanel8);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel6.setPreferredSize(new java.awt.Dimension(400, 106));
        jPanel6.setLayout(new java.awt.GridLayout(3, 0));

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel11.setLayout(new java.awt.GridLayout(4, 2, 10, 10));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Assigned Employee");
        jPanel11.add(jLabel12);

        jTextField10.setEditable(false);
        jTextField10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField10ActionPerformed(evt);
            }
        });
        jPanel11.add(jTextField10);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Created Date");
        jPanel11.add(jLabel8);

        jTextField7.setEditable(false);
        jTextField7.setForeground(new java.awt.Color(255, 0, 0));
        jPanel11.add(jTextField7);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Completed Date");
        jPanel11.add(jLabel9);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Collected Date");
        jPanel11.add(jLabel11);

        jDateChooser1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jDateChooser1MouseClicked(evt);
            }
        });
        jDateChooser1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jDateChooser1KeyPressed(evt);
            }
        });
        jPanel11.add(jDateChooser1);
        jPanel11.add(jDateChooser2);

        jPanel6.add(jPanel11);

        jLabel15.setText("Warranty");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Available");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Not Available");

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jButton2.setText("Add Note");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.add(jPanel13);

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane4.setViewportView(jTextArea3);

        jButton5.setText("Save");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton3.setText("Repaire Invoice");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel14);

        jPanel5.add(jPanel6, java.awt.BorderLayout.LINE_END);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(509, 45));

        jButton1.setText("Change Repair Status");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "Pending", "Checked", "Completed", "Collected", "Canceled", "with Warenty" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jComboBox1KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(73, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(jComboBox1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setLayout(new javax.swing.OverlayLayout(jPanel2));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repaire ID", "Stock ID", "Repaire Status", "Repair Term"
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

        jPanel2.add(jScrollPane2);

        jPanel7.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTextArea2.setText("");
        jButton5.setEnabled(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date completedDate = jDateChooser1.getDate();
        Date collectedDate = jDateChooser2.getDate();
        //  String statusChangedDate = sdf.format(date);

        String status = String.valueOf(jComboBox1.getSelectedItem());

        try {

            if (status.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please select a Status", "warning", JOptionPane.WARNING_MESSAGE);
            } else {
                if (status.equals("Completed")) {

                    if (completedDate != null) {
                        MySQL.executeIUD("update `repair_request_item` set `repair_status_id`='" + repairStatusMap.get(status) + "' ,`completed_date`='" + sdf.format(completedDate) + "'  WHERE `repair_request_repair_id`='" + jTextField1.getText() + "' AND `stock_id`='" + jTextField3.getText() + "' AND `invoice_id`='" + jTextField2.getText() + "' ");
                    } else {
                        JOptionPane.showMessageDialog(this, "Completed date cannot be null.", "error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (status.equals("Collected")) {
                    if (completedDate == null) {
                        JOptionPane.showMessageDialog(this, "Please add repair complete day before collection.", "warning", JOptionPane.WARNING_MESSAGE);
                        jDateChooser2.setDate(null);
                    } else{

                        MySQL.executeIUD("update `repair_request_item` set `repair_status_id`='" + repairStatusMap.get(status) + "' ,`collected_date`='" + sdf.format(collectedDate) + "'  WHERE `repair_request_repair_id`='" + jTextField1.getText() + "' AND `stock_id`='" + jTextField3.getText() + "' AND `invoice_id`='" + jTextField2.getText() + "' ");
                    }
                } else {

                    MySQL.executeIUD("update `repair_request_item` set `repair_status_id`='" + repairStatusMap.get(status) + "' WHERE `repair_request_repair_id`='" + jTextField1.getText() + "' AND `stock_id`='" + jTextField3.getText() + "' AND `invoice_id`='" + jTextField2.getText() + "' ");
                }
                loadRepairStatusTable();

                jLabel10.setText(status);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        String selectedStatus = String.valueOf(jComboBox1.getSelectedItem());
        Date completedDate = jDateChooser1.getDate();
        Date collectedDate = jDateChooser2.getDate();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String statusChangedDate = sdf.format(date);

        if (selectedStatus.equals("Completed")) {
            jDateChooser1.setDate(date);
            jButton5.setEnabled(true);
        } else if (selectedStatus.equals("Collected")) {
            jDateChooser2.setDate(date);
            jButton5.setEnabled(false);

        } else {
            jDateChooser1.setDate(null);
            jDateChooser2.setDate(null);
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1KeyTyped

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {

            int row = jTable1.getSelectedRow();

            try {
                ResultSet rs = MySQL.executeSearch("SELECT * FROM `repair_request_item` inner join `repair_request` on `repair_request_item`.`repair_request_repair_id`=`repair_request`.`repair_id` inner join `stock` on `stock`.`id`=`repair_request_item`.`stock_id` inner join `invoice` on `invoice`.`id`=`repair_request_item`.`invoice_id` inner join `product` on `product`.`id`=`stock`.`product_id` INNER JOIN `warranty` ON `warranty`.`id`=`product`.`warranty_id` inner join `repair_status` on `repair_status`.`id`=`repair_request_item`.`repair_status_id` WHERE `repair_request_repair_id`='" + String.valueOf(jTable1.getValueAt(row, 0)) + "' AND `repair_request_item`.`stock_id`='" + String.valueOf(jTable1.getValueAt(row, 1)) + "' AND `repair_request_item`.`invoice_id`='" + jTextField2.getText() + "'  ");

                if (rs.next()) {

                    jTextField1.setText(rs.getString("repair_request_repair_id"));
                    jTextField3.setText(rs.getString("repair_request_item.stock_id"));
                    jTextField5.setText(rs.getString("warranty.period"));
                    jTextField2.setText(rs.getString("repair_request_item.invoice_id"));
                    jTextField4.setText(rs.getString("product.name"));
                    jTextField6.setText(rs.getString("invoice.date"));
                    jTextArea1.setText(rs.getString("repair_request_item.problem"));
                    jTextField7.setText(rs.getString("repair_request_item.submit_date"));
                    jTextField10.setText(rs.getString("repair_request.employee_email"));
                    jTextArea3.setText(rs.getString("note"));

                    jLabel10.setText(String.valueOf(jTable1.getValueAt(row, 2)));

                    loadRepairStatusTable();
                    loadRepairCompletionDetails();

                }

                loadInvoiceEnable();

            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10ActionPerformed

    private void jDateChooser1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDateChooser1MouseClicked

        if (jLabel10.getText().equals("Completed")) {
            jDateChooser1.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Product status is not 'Completed'. You cannot select a date.", "Warning", JOptionPane.WARNING_MESSAGE);
            jDateChooser1.setEnabled(false);
        }
    }//GEN-LAST:event_jDateChooser1MouseClicked

    private void jDateChooser1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDateChooser1KeyPressed

    }//GEN-LAST:event_jDateChooser1KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String note = jTextArea2.getText();

        if (note.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a note", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            String prevTxt = jTextArea3.getText();
            if (prevTxt.isEmpty()) {
                jTextArea3.setText(note);

            } else {
                jTextArea3.setText(prevTxt + " , " + note);

            }

            jTextArea2.setText("");

        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        if (jTextArea3.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a note", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (jLabel10.getText().equals("Completed")) {

            //   if(jDateChooser1.getDate() ==null){
                //       JOptionPane.showMessageDialog(this, "Please type completed date", "Warning", JOptionPane.WARNING_MESSAGE);
                //     }else{
                try {

                    Date completedDate = jDateChooser1.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    MySQL.executeIUD("UPDATE `repair_request_item` SET `completed_date`='" + sdf.format(completedDate) + "',`note`='" + jTextArea3.getText() + "' WHERE `repair_request_repair_id`='" + jTextField1.getText() + "' AND `invoice_id`='" + jTextField2.getText() + "' AND `stock_id`='" + jTextField3.getText() + "' ");
                    loadRepairCompletionDetails();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // }

        } else {

            try {

                MySQL.executeIUD("UPDATE `repair_request_item` SET `note`='" + jTextArea3.getText() + "' WHERE `repair_request_repair_id`='" + jTextField1.getText() + "' AND `invoice_id`='" + jTextField2.getText() + "' AND `stock_id`='" + jTextField3.getText() + "' ");
                loadRepairCompletionDetails();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Date completedDate = jDateChooser1.getDate();
        Date collectedDate = jDateChooser2.getDate();

        if (jLabel10.getText().equals("Collected")) {

            if (jDateChooser2.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Please add collected date", "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (!jRadioButton1.isSelected() && !jRadioButton2.isSelected()) {

                JOptionPane.showMessageDialog(this, "Please select warranty status", "Warning", JOptionPane.WARNING_MESSAGE);

            } else {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                try {

                    MySQL.executeIUD("UPDATE `repair_request_item` SET `completed_date`='" + sdf.format(completedDate) + "',`collected_date`='" + sdf.format(collectedDate) + "',`note`='" + jTextArea3.getText() + "' WHERE `repair_request_repair_id`='" + jTextField1.getText() + "' AND `invoice_id`='" + jTextField2.getText() + "' AND `stock_id`='" + jTextField3.getText() + "' ");

                    if (repairInvoice == null || !repairInvoice.isDisplayable()) {

                        if (jRadioButton1.isSelected()) {
                            this.warrantyStatus = "Warranty Available";
                        } else {
                            this.warrantyStatus = "No Warranty";
                        }

                        repairInvoice = new RepairInvoice(jTextField1.getText(), jTextField3.getText(), jTextArea1.getText(), jTextField2.getText(), warrantyStatus, jTextField8.getText(),jTextField4.getText());
                        repairInvoice.setVisible(true);
                        this.dispose();
                        parent.dispose();

                    } else {
                        repairInvoice.toFront();
                        repairInvoice.requestFocus();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        } else {
            JOptionPane.showMessageDialog(this, "Product status is not 'Collected'. You cannot save repair invoice.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jLabel10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jLabel10FocusGained

    }//GEN-LAST:event_jLabel10FocusGained

    private RepairInvoice repairInvoice;

    public static JFrame findRepairs;

    private String warrantyStatus;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
