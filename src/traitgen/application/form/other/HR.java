/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package traitgen.application.form.other;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleEdge;
import traitgen.application.form.pasan.gui.ProductRegistration;
import traitgen.application.form.pasan.gui.SuplierRegistration;
import traitgen.application.form.pasan.gui.companyRegistration;
import traitgen.application.form.pasan.gui.stockManagement;

/**
 *
 * @author User
 */
public class HR extends javax.swing.JPanel {

    public void showPieChart() {

        showTopSellingProductsBarChart();
        // Create dataset
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        try {
            // Execute query to get the sum of quantity and product name
            ResultSet rs = MySQL.executeSearch("SELECT SUM(qty) AS total_qty, product.name FROM stock "
                    + "INNER JOIN product ON stock.product_id = product.id GROUP BY product.name");

            // Populate the pie dataset with all rows in the result set
            while (rs.next()) {
                String productName = rs.getString("name");
                double totalQuantity = rs.getDouble("total_qty");
                pieDataset.setValue(productName, totalQuantity);  // Add each product and total quantity to the dataset
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create chart with dynamically retrieved data
        JFreeChart piechart = ChartFactory.createPieChart("Product Stock Quantities", pieDataset, true, true, false);

        // Customize pie chart appearance
        PiePlot piePlot = (PiePlot) piechart.getPlot();
//    piePlot.setBackgroundPaint(Color.white);

        // Show percentages in the slices
        piePlot.setLabelGenerator(new PieSectionLabelGenerator() {
            private final DecimalFormat df = new DecimalFormat("0.0%");

            @Override
            public String generateSectionLabel(PieDataset dataset, Comparable key) {
                Number value = dataset.getValue(key);
                if (value != null) {
                    double total = 0;

                    // Calculate total
                    for (Object objKey : dataset.getKeys()) {
                        // Cast objKey to Comparable
                        Comparable<?> comparableKey = (Comparable<?>) objKey;
                        total += dataset.getValue(comparableKey).doubleValue(); // Use Comparable to get the value
                    }

                    double percentage = value.doubleValue() / total;
                    return key + " (" + df.format(percentage) + ")";
                }
                return null;
            }

            @Override
            public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
                return new AttributedString(generateSectionLabel(dataset, key));
            }
        });

        // Set font and outline for better readability
        piePlot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
//    piePlot.setLabelBackgroundPaint(new Color(220, 220, 220));
//    piePlot.setLabelOutlinePaint(Color.white);

        // Set legend position for a list-like display of categories
        piechart.getLegend().setPosition(RectangleEdge.BOTTOM.RIGHT);

        // Generate random colors for each section
        Random random = new Random();
        for (Object objKey : pieDataset.getKeys()) {
            Comparable<?> key = (Comparable<?>) objKey;  // Cast to Comparable
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            piePlot.setSectionPaint(key, color);
        }

        // Display chart in panel
        ChartPanel pieChartPanel = new ChartPanel(piechart);
        panelBarChart.removeAll();
        panelBarChart.add(pieChartPanel, BorderLayout.CENTER);
        panelBarChart.validate();
    }

    public void showTopSellingProductsBarChart() {
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        try {
            // Query to fetch the top-selling products
            ResultSet rs = MySQL.executeSearch("SELECT product.name AS product_name, SUM(invoice_item.qty) AS total_qty "
                    + "FROM `invoice_item` "
                    + "INNER JOIN stock ON `invoice_item`.`stock_id` = `stock`.`id` "
                    + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                    + "GROUP BY `product`.`name` "
                    + "ORDER BY total_qty DESC LIMIT 10");

            // Populate the dataset
            while (rs.next()) {
                String productName = rs.getString("product_name");
                double totalQty = rs.getDouble("total_qty");
                barDataset.addValue(totalQty, "Sales Volume", productName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the Bar Chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Top-Selling Products", "Product", "Quantity Sold", barDataset, PlotOrientation.VERTICAL, true, true, false);

        // Get the plot and customize it
        CategoryPlot barPlot = barChart.getCategoryPlot();

        // Set the background color of the plot
        barPlot.setBackgroundPaint(new Color(240, 240, 240)); // Light gray background
        barPlot.setRangeGridlinePaint(new Color(200, 200, 200)); // Darker gray gridlines

        // Set the background color of the chart
        barChart.setBackgroundPaint(new Color(255, 255, 255)); // White background

        // Customize the renderer if needed
        BarRenderer renderer = (BarRenderer) barPlot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Custom bar color

        // Display in the panel
        ChartPanel barChartPanel = new ChartPanel(barChart);
        barChartPanel.setBackground(new Color(255, 255, 255)); // White background for the chart panel

        // Set the background color of jPanel5
        jPanel5.setBackground(new Color(255, 255, 255)); // Set to your desired color

        jPanel5.removeAll();
        jPanel5.add(barChartPanel, BorderLayout.CENTER);
        jPanel5.validate();
    }

    public void startClock() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                Time.setText(timeFormat.format(new Date()));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date.setText(dateFormat.format(new Date()));
            }
        });

        timer.start();
    }

    private void lowStock() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT DISTINCT * FROM `stock` INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` WHERE `stock`.`qty` <= 3 ORDER BY `product`.`name` ASC");

            DefaultListModel<String> ls = new DefaultListModel<>();
            while (rs.next()) {
                String productName = rs.getString("product.name");
                int quantity = rs.getInt("qty");
                //double sellingPrice = rs.getDouble("selling_price");
                ls.addElement("!!  " + productName + "  " + quantity + "");
            }

            SwingUtilities.invokeLater(() -> {
                jList1.setModel(ls);
                jList1.setFixedCellHeight(32);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HR() {
        initComponents();
        lowStock();
        startClock();
        showPieChart();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        panelBarChart = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        Time = new javax.swing.JLabel();
        Date = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Inventory Dashboard");

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel7.setLayout(new java.awt.GridLayout(4, 1, 10, 10));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Product Management");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton1);

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton6.setText("Supplier Management");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton6);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton5.setText("Stock Management");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton5);

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton7.setText("Company Registration ");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton7);

        panelBarChart.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelBarChart.setLayout(new java.awt.BorderLayout());

        jList1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 2, true), "Low Stock Products ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(255, 0, 0))); // NOI18N
        jList1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "LOw 05" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel5.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBarChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(panelBarChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        Time.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Time.setText("TIME ");

        Date.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        Date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Date.setText("DATE");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Date, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Time, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date)
                    .addComponent(Time))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private ProductRegistration pr;
    private stockManagement sm;
    private SuplierRegistration sum;
    private companyRegistration cr;

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (pr == null || !pr.isDisplayable()) {
            pr = new ProductRegistration();
            pr.setVisible(true);
        } else {
            pr.toFront();
            pr.requestFocus();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (sum == null || !sum.isDisplayable()) {
            sum = new SuplierRegistration();
            sum.setVisible(true);
        } else {
            sum.toFront();
            sum.requestFocus();
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (sm == null || !sm.isDisplayable()) {
            sm = new stockManagement();
            sm.setVisible(true);
        } else {
            sm.toFront();
            sm.requestFocus();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        if (cr == null || !cr.isDisplayable()) {
            cr = new companyRegistration();
            cr.setVisible(true);
        } else {
            cr.toFront();
            cr.requestFocus();
        }
    }//GEN-LAST:event_jButton7ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Date;
    private javax.swing.JLabel Time;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelBarChart;
    // End of variables declaration//GEN-END:variables
}
