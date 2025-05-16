/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package traitgen.application.form.other;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import traitgen.application.Application;

/**
 *
 * @author User
 */
public class Repair extends javax.swing.JPanel {

    /**
     * Creates new form Repair
     */
    public Repair() {
        initComponents();
        loadRepairInvoiceHistory();
        loadRepairStatus();
        MostReturn();
        MostRepaired();
        dailyReturn();
        monthlyReturn();
        loadRepairProductCount();
//        createGraph();
DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.setDefaultRenderer(Object.class, renderer);

    }

    private void loadRepairInvoiceHistory() {

        try {

            String query = "SELECT * FROM `repair_complete_invoice` inner join `repair_request_item` on `repair_request_item`.`id`=`repair_complete_invoice`.`repair_request_item_id` INNER JOIN `repair_request` on `repair_request`.`repair_id`=`repair_request_item`.`repair_request_repair_id` ";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0);

            while (rs.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(rs.getString("repair_complete_invoice.id"));
                vector.add(rs.getString("repair_complete_invoice.repair_request_repair_id"));
                vector.add(rs.getString("repair_complete_invoice.repair_request_item_id"));
                vector.add(rs.getString("repair_request_item.stock_id"));
                vector.add(rs.getString("repair_request_item.invoice_id"));
                vector.add(rs.getString("repair_complete_invoice.paid_amount"));
                vector.add(rs.getString("repair_request.customer_mobile"));
                vector.add(rs.getString("repair_request.employee_email"));
                vector.add(rs.getString("repair_complete_invoice.employee_email"));

                dtm.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void MostReturn() {

        try {

            ResultSet rs = MySQL.executeSearch("select return_product.stock_id,SUM(return_product.qty) AS total_return,product.name from `return_product` inner join `stock` on `stock`.`id`=`return_product`.`stock_id` inner join `product` on `product`.`id`= `stock`.`product_id` group by `stock_id` order by `total_return` DESC LIMIT 1 ");
            while (rs.next()) {
                jLabel8.setText(rs.getString("product.name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MostRepaired() {

        try {

            ResultSet rs = MySQL.executeSearch("select repair_request_item.stock_id,SUM(repair_request_item.qty) AS total_repair,product.name from `repair_request_item` inner join `stock` on `stock`.`id`=`repair_request_item`.`stock_id` inner join `product` on `product`.`id`= `stock`.`product_id` group by `stock_id` order by `total_repair` DESC LIMIT 1 ");
            while (rs.next()) {
                jLabel12.setText(rs.getString("name"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dailyReturn() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);

        try {

            ResultSet rs = MySQL.executeSearch("select return_product.return_date,SUM(return_product.qty) AS total_return from `return_product` inner join `stock` on `stock`.`id`=`return_product`.`stock_id` inner join `product` on `product`.`id`= `stock`.`product_id` group by `return_date` order by  `return_date` DESC LIMIT 1");
            while (rs.next()) {
                if (rs.getString("return_date").equals(today)) {
                    jLabel14.setText("total_return");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void monthlyReturn() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String month = sdf.format(date);

        try {

            ResultSet rs = MySQL.executeSearch("select DATE_FORMAT(return_product.return_date,'%Y-%m') AS month,SUM(return_product.qty) AS total_return from `return_product` inner join `stock` on `stock`.`id`=`return_product`.`stock_id` inner join `product` on `product`.`id`= `stock`.`product_id` group by `month` order by  `month` DESC LIMIT 1");
            while (rs.next()) {
                if (rs.getString("month").equals(month)) {
                    jLabel16.setText("total_return");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> status = new ArrayList<>();
    private List<Double> qty = new ArrayList<>();

    private void loadRepairStatus() {
        try {
            ResultSet rs = MySQL.executeSearch(
                    "SELECT repair_status.name AS repair_name, SUM(repair_request_item.qty) AS total_qty "
                    + "FROM repair_request_item "
                    + "INNER JOIN repair_status ON repair_status.id = repair_request_item.repair_status_id "
                    + "GROUP BY repair_name"
            );

            while (rs.next()) {
                String repairName = rs.getString("repair_name");
                Double totalQty = rs.getDouble("total_qty");

                status.add(repairName);
                qty.add(totalQty);
            }

            createPieChart();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (int i = 0; i < status.size(); i++) {
            dataset.setValue(status.get(i), qty.get(i));
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Repair Product Status",
                dataset,
                true,
                true,
                false
        );

        // Set chart background to transparent
        pieChart.setBackgroundPaint(null);

        // Customize pie chart colors
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(null);  // Set plot background to transparent
        plot.setOutlinePaint(null);     // Remove outline
        plot.setSectionPaint(0, Color.GRAY); // Set pie section color to gray (customize if multiple sections)
        
        pieChart.getTitle().setPaint(Color.GRAY);

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(400, 400));
        jPanel5.removeAll();
        jPanel5.add(chartPanel);
        jPanel5.revalidate();
        jPanel5.repaint();
    }

    private List<String> product = new ArrayList<>();
    private List<Integer> count = new ArrayList<>();

    private void loadRepairProductCount() {

        try {

            ResultSet rs = MySQL.executeSearch("select repair_request_item.stock_id,SUM(repair_request_item.qty) AS total_repair,product.name from `repair_request_item` inner join `stock` on `stock`.`id`=`repair_request_item`.`stock_id` inner join `product` on `product`.`id`= `stock`.`product_id` group by `stock_id` order by `total_repair`");
            while (rs.next()) {
                product.add(rs.getString("name"));
                count.add(rs.getInt("total_repair"));
            }

            createGraph();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JFrame chartFrame;

    private void createGraph() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < product.size(); i++) {
            dataset.addValue(count.get(i), "Product Name", product.get(i));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Repair Products",
                "Product Name",
                "count",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        barChart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.GRAY);
        barChart.setBackgroundPaint(null);
        barChart.getCategoryPlot().setBackgroundPaint(null);
        barChart.getTitle().setPaint(Color.GRAY);

        barChart.getCategoryPlot().getDomainAxis().setLabelPaint(Color.BLACK);
        barChart.getCategoryPlot().getRangeAxis().setLabelPaint(Color.BLACK);
        barChart.getCategoryPlot().getDomainAxis().setTickLabelPaint(Color.BLACK);
        barChart.getCategoryPlot().getRangeAxis().setTickLabelPaint(Color.BLACK);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        jPanel6.add(chartPanel);
        jPanel6.revalidate();
        jPanel6.repaint();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Repair Management");

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel5.setLayout(new javax.swing.OverlayLayout(jPanel5));

        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel6.setLayout(new javax.swing.OverlayLayout(jPanel6));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Not Availible");
        jLabel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Most Repaired Product", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel2.add(jLabel12);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Not Availible");
        jLabel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monthly Return Product Count", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13))); // NOI18N
        jPanel2.add(jLabel16);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Not Availible");
        jLabel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Most Return Product", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        jPanel2.add(jLabel8);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Not Availible");
        jLabel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Daily Return Product Count", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13))); // NOI18N
        jPanel2.add(jLabel14);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repair Invoice ID", "Repair ID", "Repair Item ID", "Stock ID", "Invoice ID", "Paid Amount", "Customer", "Assigned Employee", "Invoice Employee"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
