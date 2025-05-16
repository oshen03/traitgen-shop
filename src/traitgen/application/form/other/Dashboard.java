package traitgen.application.form.other;

import dashboard.dialog.Message;
import dashboard.model.ModelCard;
import dashboard.model.ModelStudent;
import dashboard.swing.icon.GoogleMaterialDesignIcons;
import dashboard.swing.icon.IconFontSwing;
import dashboard.swing.noticeboard.ModelNoticeBoard;
import dashboard.swing.table.EventAction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.ResultSet;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import model.MySQL;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import traitgen.application.Application;

public class Dashboard extends javax.swing.JPanel {

    public Dashboard() {
        initComponents();
        setOpaque(false);
        initData();
    }

    private void initData() {
        initCardData();
        

    }



private void initCardData() {
    
    // Set `null` for icons or use default placeholders if needed

    card1.setData(new ModelCard("New Student", 5100, 20, null)); // No icon
    card2.setData(new ModelCard("Income", 2000, 60, null));      // No icon
    card3.setData(new ModelCard("Expense", 3000, 80, null));     // No icon
    card4.setData(new ModelCard("Other Income", 550, 95, null)); // No icon
}



    private boolean showMessage(String message) {
        Message obj = new Message(Application.getFrames()[0], true);
        obj.showMessage(message);
        return obj.isOk();
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card1 = new dashboard.component.Card();
        jLabel1 = new javax.swing.JLabel();
        card2 = new dashboard.component.Card();
        card3 = new dashboard.component.Card();
        card4 = new dashboard.component.Card();
        piechartPanel = new javax.swing.JPanel();

        card1.setColorGradient(new java.awt.Color(211, 28, 215));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(4, 72, 210));
        jLabel1.setText("Dashboard");

        card2.setBackground(new java.awt.Color(10, 30, 214));
        card2.setColorGradient(new java.awt.Color(72, 111, 252));

        card3.setBackground(new java.awt.Color(194, 85, 1));
        card3.setColorGradient(new java.awt.Color(255, 212, 99));

        card4.setBackground(new java.awt.Color(60, 195, 0));
        card4.setColorGradient(new java.awt.Color(208, 255, 90));

        javax.swing.GroupLayout piechartPanelLayout = new javax.swing.GroupLayout(piechartPanel);
        piechartPanel.setLayout(piechartPanelLayout);
        piechartPanelLayout.setHorizontalGroup(
            piechartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 591, Short.MAX_VALUE)
        );
        piechartPanelLayout.setVerticalGroup(
            piechartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 475, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(card1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(card2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(card3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(card4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(piechartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(piechartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private dashboard.component.Card card1;
    private dashboard.component.Card card2;
    private dashboard.component.Card card3;
    private dashboard.component.Card card4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel piechartPanel;
    // End of variables declaration//GEN-END:variables
}
