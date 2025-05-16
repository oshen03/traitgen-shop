/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package traitgen.application.form.other;

import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 *
 * @author User
 */
public class Customer extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(Customer.class.getName());

    private void setLogger() {
        try {
            // Create the directory if it does not exist
            java.nio.file.Path logDir = java.nio.file.Paths.get("Log Reports");
            if (!java.nio.file.Files.exists(logDir)) {
                java.nio.file.Files.createDirectories(logDir);
            }

            // Only add a file handler if none is added
            if (logger.getHandlers().length == 0) {
                FileHandler fileHandler = new FileHandler("Log Reports/Customer Dashboard Log Report.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
                logger.setLevel(Level.ALL);  // Set level to capture all logs
                logger.info("Customer Dashboard Logger initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Logger initialization failed", e);
        }
    }

    /**
     * Creates new form Customer
     */
    public Customer() {
        initComponents();
        setupPieChart();
        loadTable();
        setLogger();
    }

    private void loadTable() {
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);
        try {
            ResultSet result = MySQL.executeSearch("SELECT * FROM `customer`");
            while (result.next()) {
                Vector<Object> vector = new Vector<>();
                vector.add(result.getString("first_name"));
                vector.add(result.getString("last_name"));
                vector.add(result.getString("mobile"));
                vector.add(result.getString("registered_date"));
                vector.add(result.getString("email"));

                dtm.addRow(vector);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error loading customer data: {0}", e.getMessage());
        }
    }

     
    private PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            try (ResultSet rs = MySQL.executeSearch("SELECT cus_support_ticket_status_id, COUNT(*) AS count FROM customer_support WHERE cus_support_ticket_status_id IN (1, 2) GROUP BY cus_support_ticket_status_id")) {
                while (rs.next()) {
                    int id = rs.getInt("cus_support_ticket_status_id");
                    int count = rs.getInt("count");
                    if (id == 1) {
                        dataset.setValue("Pending", count);
                    } else if (id == 2) {
                        dataset.setValue("Completed", count);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }
    private static final java.awt.Color PIE_COLOR_1 = new java.awt.Color(87, 123, 193,200);
    private static final java.awt.Color PIE_COLOR_2 = new java.awt.Color(255, 235, 0,180);
    private static final java.awt.Color PIE_COLOR_3 = new java.awt.Color(255, 255, 255, 1);

    private JFreeChart createChart(PieDataset dataset) {
       JFreeChart chart = ChartFactory.createPieChart(
                "Customer Support Tickets",
                dataset,
                true, // include legend
                true,
                false
        );

        // Enable anti-aliasing for smoother edges
        chart.setAntiAlias(true);

        // Customize the chart
        chart.setBackgroundPaint(PIE_COLOR_3);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
       plot.setOutlineVisible(false);
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelFont(new java.awt.Font("Calibri", java.awt.Font.BOLD, 12));
        plot.setLabelPaint(java.awt.Color.WHITE);
        plot.setSectionPaint("Pending", PIE_COLOR_1);
        plot.setSectionPaint("Completed", PIE_COLOR_2);
        plot.setShadowPaint(null);
        plot.setLabelGap(0.02);

        return chart;
    }
    
    

    private void setupPieChart() {
        PieDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 400)); 
        piechartPanel.setLayout(new BorderLayout());
        piechartPanel.add(chartPanel, BorderLayout.CENTER);
        piechartPanel.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        piechartPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "First Name", "Last Name", "Mobile No", "Date of Registration", "Email"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout piechartPanelLayout = new javax.swing.GroupLayout(piechartPanel);
        piechartPanel.setLayout(piechartPanelLayout);
        piechartPanelLayout.setHorizontalGroup(
            piechartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        piechartPanelLayout.setVerticalGroup(
            piechartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 182, Short.MAX_VALUE)
        );

        jButton1.setFont(new java.awt.Font("Helvetica LT Std", 1, 14)); // NOI18N
        jButton1.setText("New Customer Registration");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Helvetica LT Std", 1, 14)); // NOI18N
        jButton2.setText("Customer Support");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE)
                        .addGap(15, 15, 15))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(piechartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addGap(27, 27, 27))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(piechartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 39, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel piechartPanel;
    // End of variables declaration//GEN-END:variables
}
