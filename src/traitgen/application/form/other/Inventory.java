/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package traitgen.application.form.other;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import raven.toast.Notifications;
import traitgen.application.form.amantha.EmployeeRegistration;
import traitgen.application.form.amantha.SalaryCalculation;

/**
 *
 * @author User
 */
public class Inventory extends javax.swing.JPanel {

    /**
     * Creates new form Inventory
     */
    public Inventory() {
        initComponents();
        loadTable();
        loadEmployeeType();
        setLogger();
        loadGenderComboBox();
        loadEmployeeTypeComboBox();
        loadGenderComboBox();
        jButton1.setEnabled(false);
        jPanel4.setVisible(false);
        jButton7.setEnabled(false);
        loadEmployees();

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.setDefaultRenderer(Object.class, renderer);
        jTable2.setDefaultRenderer(Object.class, renderer);
    }

    private static HashMap<String, String> GenderTypeMap = new HashMap<>();
    private static HashMap<String, String> EmployeeTypeMap = new HashMap<>();

    private static final Logger logger = Logger.getLogger(EmployeeRegistration.class.getName());

    private void setLogger() {
        try {
            FileHandler fileHandler = new FileHandler("Log Reports/Employee Registration Log Report.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);

            logger.info("Employee Reg Logger initialized");

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Logger failed to initialize", e);

        }
    }

    private void loadGenderComboBox() {
        try {
            ResultSet result = MySQL.executeSearch("SELECT * FROM `gender`");
            Vector<String> vector = new Vector();
            vector.add("Select");

            while (result.next()) {
                vector.add(result.getString("gender_type"));
                GenderTypeMap.put(result.getString("gender_type"), result.getString("id"));
            }
            DefaultComboBoxModel dcm = new DefaultComboBoxModel(vector);
            jComboBox2.setModel(dcm);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Gender combo box load error", e);
        }
    }

    private void loadEmployeeTypeComboBox() {
        try {
            ResultSet result = MySQL.executeSearch("SELECT * FROM `employee_type`");
            Vector<String> vector = new Vector();
            vector.add("Select");

            while (result.next()) {
                vector.add(result.getString("name"));
                EmployeeTypeMap.put(result.getString("name"), result.getString("id"));
            }
            DefaultComboBoxModel dcm = new DefaultComboBoxModel(vector);
            jComboBox3.setModel(dcm);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Type combo box load error", e);
        }
    }

    private void loadTotalAttendance(String empEmail) {
        try {
            // SQL Query to count total attendance
            String query = "SELECT SUM(count) AS total_attendance FROM attendance_count WHERE employee_email = '" + empEmail + "'";
            ResultSet resultSet = MySQL.executeSearch(query);

            int totalAttendance = 0;

            if (resultSet.next()) {
                totalAttendance = resultSet.getInt("total_attendance");
                jLabel4.setText(String.valueOf(totalAttendance));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTotalLeave(String empEmail) {
        try {
            // SQL Query to count total leave records
            String query = "SELECT SUM(leave_count) AS total_leave FROM attendance_count WHERE employee_email = '" + empEmail + "'";
            ResultSet resultSet = MySQL.executeSearch(query);

            int totalLeave = 0;
            if (resultSet.next()) {
                totalLeave = resultSet.getInt("total_leave");
                jLabel12.setText(String.valueOf(totalLeave));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTotalPayments(String empEmail) {
        try {
            // SQL Query to sum total payment amounts
            String query = "SELECT IFNULL(SUM(net_pay), 0) AS total_payments FROM salary_record WHERE employee_email = '" + empEmail + "'";
            ResultSet resultSet = MySQL.executeSearch(query);

            double totalPayments = 0.0;
            if (resultSet.next()) {
                totalPayments = resultSet.getDouble("total_payments");
                jLabel13.setText("Rs. " + String.format("%.2f", totalPayments));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEmployeeType() {
        try {

            ResultSet rs = MySQL.executeSearch("SELECT * FROM `employee_type`");

            DefaultComboBoxModel model = (DefaultComboBoxModel) jComboBox1.getModel();
            model.removeAllElements();

            Vector<String> v = new Vector<>();
            v.add("Select");

            while (rs.next()) {
                v.add(rs.getString("name"));
            }

            model.addAll(v);
            jComboBox1.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTable() {
        String searchText = jTextField1.getText().trim();
        String mobileText = jTextField2.getText().trim();
        String selectedSort = (String) jComboBox1.getSelectedItem();
        try {
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();

            dtm.setRowCount(0);

            String query = "SELECT * FROM `employee` "
                    + "INNER JOIN `employee_type` ON `employee`.`employee_type_id`=`employee_type`.`id` "
                    + "INNER JOIN `gender` ON `employee`.`gender_id`=`gender`.`id`";

            boolean whereClauseAdded = false;

            if (!searchText.isEmpty()) {
                if (whereClauseAdded) {
                    query += " AND `employee`.`first_name` LIKE '%" + searchText + "%'";
                } else {
                    query += " WHERE `employee`.`first_name` LIKE '%" + searchText + "%'";
                    whereClauseAdded = true;
                }
            }

            if (!mobileText.isEmpty()) {
                if (whereClauseAdded) {
                    query += " AND `employee`.`mobile` LIKE '%" + mobileText + "%'";
                } else {
                    query += " WHERE `employee`.`mobile` LIKE '%" + mobileText + "%'";
                    whereClauseAdded = true;
                }
            }

//            int empTypeId = (Integer) jComboBox1.getSelectedIndex();
//            String empType = (String) jComboBox1.getSelectedItem();
//
//            if (empTypeId != 0) {
//
//                if (!query.isEmpty()) {
//
//                    query += " AND `employee`.`employee_type_id`='" + empTypeId + "' ";
//                                    whereClauseAdded = true;
//
//                } else {
//                    query += " WHERE `employee`.`employee_type_id`='" + empTypeId + "'";
//                                    whereClauseAdded = true;
//
//                }
//
//            }
            query += "ORDER BY `first_name` ASC";

            ResultSet result = MySQL.executeSearch(query);

            while (result.next()) {
                Vector<String> vector = new Vector<>();

                vector.add(result.getString("email"));
                vector.add(result.getString("first_name"));
                vector.add(result.getString("last_name"));
                vector.add(result.getString("nic"));
                vector.add(result.getString("mobile"));
                vector.add(result.getString("register_date"));
                vector.add(result.getString("employee_type.name"));
                vector.add(result.getString("gender.gender_type"));

                dtm.addRow(vector);

            }
            for (int i = 0; i < jTable1.getColumnModel().getColumnCount(); i++) {
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            logger.warning("Table load error");
        }
    }

    private void loadEmployees() {

        try {
            String query = "SELECT * FROM `employee` "
                    + "INNER JOIN `employee_type` ON `employee`.`employee_type_id`=`employee_type`.`id` "
                    + "INNER JOIN `gender` ON `employee`.`gender_id`=`gender`.`id`";

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();

            model.setRowCount(0);

            ResultSet result = MySQL.executeSearch(query);

            while (result.next()) {
                Vector<String> vector = new Vector<>();

                vector.add(result.getString("email"));
                vector.add(result.getString("password"));
                vector.add(result.getString("first_name"));
                vector.add(result.getString("last_name"));
                vector.add(result.getString("nic"));
                vector.add(result.getString("mobile"));
                vector.add(result.getString("register_date"));
                vector.add(result.getString("employee_type.name"));
                vector.add(result.getString("gender.gender_type"));
                vector.add(result.getString("status"));

                model.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadEmployeeDetails(String empEmail) {

        try {

            String query = "SELECT * FROM `attendance_count` INNER JOIN `employee` ON `attendance_count`.`employee_email` = `employee`.`email` "
                    + " WHERE `employee_email` = '" + empEmail + "'";

            ResultSet rs = MySQL.executeSearch(query);

            if (rs.next()) {

                jLabel5.setText(rs.getString("employee.first_name") + " " + rs.getString("employee.last_name"));
                jLabel6.setText(rs.getString("attendance_count.count"));
                jLabel7.setText(String.valueOf(21 - Integer.parseInt(rs.getString("attendance_count.count"))));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void reset() {
        jTextField11.setEditable(true);
        jTextField11.setText("");
        jTextField1.setEnabled(true);
        jTextField12.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");

        jButton6.setEnabled(true);
        jButton7.setEnabled(false);

        jComboBox2.setSelectedIndex(0);
        jComboBox3.setSelectedIndex(0);
        
        jTable2.clearSelection();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        jPanel1.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("0");
        jLabel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Total Attendence", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(37, 75));
        jPanel1.add(jLabel4);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("0");
        jLabel12.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Total Leaves", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel1.add(jLabel12);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Rs. 0.00");
        jLabel13.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Total Payments", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel1.add(jLabel13);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Human Resources Management");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Email", "Password", "First Name", "Last Name", "NIC", "Mobile", "Registered Date", "Type", "Gender", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel7.setLayout(new java.awt.GridLayout(1, 4, 10, 10));

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setText("Add");
        jButton6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton6);

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton7.setText("Update");
        jButton7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel7.add(jButton7);

        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton8.setText("Reset");
        jButton8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton8);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton4.setText("Back ");
        jButton4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton4);

        jPanel9.setLayout(new java.awt.GridLayout(4, 2, 10, 10));

        jTextField11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Email ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel9.add(jTextField11);

        jTextField12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Password", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jTextField12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField12ActionPerformed(evt);
            }
        });
        jPanel9.add(jTextField12);

        jTextField3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "First Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel9.add(jTextField3);

        jTextField4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Last Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel9.add(jTextField4);

        jTextField5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nic", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jPanel9.add(jTextField5);

        jTextField6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mobile", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel9.add(jTextField6);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gender", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel9.add(jComboBox2);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Employee Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });
        jPanel9.add(jComboBox3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 783, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(218, 218, 218)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(167, Short.MAX_VALUE)))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Email", "First Name", "Last Name", "NIC", "Mobile", "Registered Date", "Type", "Gender"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filter ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Search by name ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        jPanel2.add(jTextField1);

        jTextField2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Search by mobile", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });
        jPanel2.add(jTextField2);

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Search by type", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jPanel2.add(jComboBox1);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(162, 162, 162))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setLayout(new java.awt.GridLayout(8, 1, 20, 10));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Employee Name");
        jLabel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Employee Name", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel5.add(jLabel5);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Current Attendance");
        jLabel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Current Attendance", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel5.add(jLabel6);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Current Leave");
        jLabel7.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Current Leave", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel5.add(jLabel7);

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Mark Attendance");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton1);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("p.Status");
        jLabel11.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Status", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel5.add(jLabel11);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setText("Add Leave");
        jButton2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton2);

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton3.setText("Register New Employe");
        jButton3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton3);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setText("Salary ");
        jButton5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased

    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        loadTable();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int row = jTable1.getSelectedRow();

        jButton1.setEnabled(true);

        if (row != -1) {

            String empEmail = String.valueOf(jTable1.getValueAt(row, 0));

            loadEmployeeDetails(empEmail);
            loadTotalAttendance(empEmail);
            loadTotalLeave(empEmail);
            loadTotalPayments(empEmail);
        }

        //        if (evt.getButton() == MouseEvent.BUTTON3) {
        //            int row = jTable1.rowAtPoint(evt.getPoint());
        //            jTable1.setRowSelectionInterval(row, row);
        //            jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
        //            UpdateInfo.setText("Update Info");
        //        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        int row = jTable1.getSelectedRow();

        String email = String.valueOf(jTable1.getValueAt(row, 0));
        String dateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please Select The employee", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            try {
                //Attendance is marked for the day
                ResultSet attendanceResult = MySQL.executeSearch("SELECT * FROM `attendence` WHERE  `employee_email` = '" + email + "' AND `date` = '" + dateTime + "'");
                if (attendanceResult.next()) {
                    //Do nothing
                    //                JOptionPane.showMessageDialog(this, "Already Mark", "Inform", JOptionPane.INFORMATION_MESSAGE);
                    Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Attendance Already Marked");

                } else {
                    MySQL.executeIUD("INSERT INTO `attendence` (`date`,`employee_email`) VALUES ('" + dateTime + "','" + email + "')");
                    //Attendance count +
                    ResultSet count = MySQL.executeSearch("SELECT * FROM `attendance_count` WHERE `employee_email` = '" + email + "'");
                    if (count.next()) {
                        int currentCount = count.getInt("count");
                        int newCount = currentCount + 1;
                        MySQL.executeIUD("UPDATE `attendance_count` SET `count`= '" + newCount + "' WHERE `employee_email` = '" + email + "'");

                        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Attendance Marked Success");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int row = jTable1.getSelectedRow();

        String email = String.valueOf(jTable1.getValueAt(row, 0));
        String dateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {

            ResultSet attendanceResult = MySQL.executeSearch("SELECT * FROM `attendence` WHERE  `employee_email` = '" + email + "' AND `date` = '" + dateTime + "'");
            if (attendanceResult.next()) {

                if (attendanceResult.getString("attendance_status_id").equals("2")) {

                    Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Can not Give leave Today");
                } else {
                    Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "This employee Present Today");

                }
                //Do nothing
                //                JOptionPane.showMessageDialog(this, "Already Mark", "Inform", JOptionPane.INFORMATION_MESSAGE);

            } else {

                MySQL.executeIUD("INSERT INTO `attendence` (`date`,`employee_email`,`attendance_status_id`) VALUES ('" + dateTime + "','" + email + "','2')");

                ResultSet count = MySQL.executeSearch("SELECT * FROM `attendance_count` WHERE `employee_email` = '" + email + "'");
                if (count.next()) {
                    int currentCount = count.getInt("leave_count");

                    if (currentCount == 5) {
                        Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Leave Limit Completed");

                    } else {

                        int newCount = currentCount + 1;
                        MySQL.executeIUD("UPDATE `attendance_count` SET `leave_count`= '" + newCount + "' WHERE `employee_email` = '" + email + "'");

                        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "leave Given Success");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked

        jButton7.setEnabled(true);
        jButton6.setEnabled(false);
        
        int row = jTable2.getSelectedRow();
        
        jTextField11.setEditable(false);

        jTextField11.setText(String.valueOf(jTable2.getValueAt(row, 0)));
        jTextField12.setText(String.valueOf(jTable2.getValueAt(row, 1)));
        jTextField3.setText(String.valueOf(jTable2.getValueAt(row, 2)));
        jTextField4.setText(String.valueOf(jTable2.getValueAt(row, 3)));
        jTextField5.setText(String.valueOf(jTable2.getValueAt(row, 4)));
        jTextField6.setText(String.valueOf(jTable2.getValueAt(row, 5)));
        jComboBox3.setSelectedItem(String.valueOf(jTable2.getValueAt(row, 7)));
        jComboBox2.setSelectedItem(String.valueOf(jTable2.getValueAt(row, 8)));
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jPanel4.setVisible(true);
        jPanel8.setVisible(false);
        jPanel1.setVisible(false);
        jPanel5.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jPanel4.setVisible(false);
        jPanel8.setVisible(true);
        jPanel1.setVisible(true);
        jPanel5.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField12ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String email = jTextField11.getText();
        String password = jTextField12.getText();
        String fname = jTextField3.getText();
        String lname = jTextField4.getText();
        String nic = jTextField5.getText();
        String mobile = jTextField6.getText();
        String gender = (String) jComboBox2.getSelectedItem();
        String type = (String) jComboBox3.getSelectedItem();
        String dateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));

        String emailValid = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        String mobileValid = "^07[01245678][0-9]{7}$";
        String nicValid = "^-?\\d+$";

        if (email.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter the email");
        } else if (!email.matches(emailValid)) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter a valid email");
        } else if (password.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter a password");
        } else if (fname.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter the employee's first name");
        } else if (lname.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter the employee's last name");
        } else if (nic.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter the NIC");
        } else if (!nic.matches(nicValid)) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter a valid NIC");
        } else if (mobile.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter a mobile number");
        } else if (!mobile.matches(mobileValid)) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Enter a valid mobile number");
        } else if (gender.equals("Select")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Select a gender");
        } else if (type.equals("Select")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_CENTER, "Select the employee type");
        } else {
//            Success
            try {
                MySQL.executeIUD("INSERT INTO `employee` (`email`,`password`,`first_name`,`last_name`,`nic`,`mobile`,`register_date`,`employee_type_id`,`gender_id`) VALUES ('" + email + "','" + password + "','" + fname + "','" + lname + "','" + nic + "','" + mobile + "','" + dateTime + "','" + EmployeeTypeMap.get(type) + "','" + GenderTypeMap.get(gender) + "') ");
                MySQL.executeIUD("INSERT INTO `attendance_count` (`employee_email`,`year`,`month`,`count`) VALUES ('" + email + "','" + year + "','" + month + "',0)");
                Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_CENTER, "Employee Registered");
                logger.log(Level.INFO, "An Employee {0} registered", email);

                //reset();
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(Level.SEVERE, "SQL Error when registering employee", e);
            }

        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        reset();
        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_CENTER, "Fields Cleared");
    }//GEN-LAST:event_jButton8ActionPerformed
    private SalaryCalculation sc;
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (sc == null || !sc.isDisplayable()) {
            sc = new SalaryCalculation();
            sc.setVisible(true);
            sc.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        } else {
            sc.toFront();
            sc.requestFocus();
        }
    }//GEN-LAST:event_jButton5ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
