package traitgen.application.form.other;

import java.awt.Component;
import java.awt.Container;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import model.MySQL;

/**
 *
 * @author User
 */
public class GRN extends javax.swing.JPanel {

    /**
     * Creates new form GRN
     */
    public GRN() {
        initComponents();
        loadGrnUpdate();
        loadAmountRates();
        loadBulckRates();
        quantityLimit();
        applyPriceLimitToAll(jPanel1);
        applyPriceLimitToAll(jPanel16);
        applyPriceLimitToAll(jPanel18);

        jFormattedTextField1.setEnabled(false);
        jTextField5.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField1.setEnabled(false);
        jTextField3.setEnabled(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.setDefaultRenderer(Object.class, renderer);
    }
    private double productbuyingPrice = 0;
    private double productsellingPrice = 0;
    private double minSellingPrice = 0;
    private double productdiscountPrice = 0;
    private double minDiscountPrice = 0;

    public void priceCalculate() {

        if (jFormattedTextField1.getText().isEmpty()) {

            productbuyingPrice = 0;
        } else {

            productbuyingPrice = Double.parseDouble(jFormattedTextField1.getText());
        }

        minSellingPrice = productbuyingPrice + (productbuyingPrice * 20 / 100);

        jLabel16.setText(String.valueOf(minSellingPrice));

        if (jFormattedTextField2.getText().isEmpty()) {
            productsellingPrice = 0;
        } else {
            productsellingPrice = Double.parseDouble(jFormattedTextField2.getText());
        }

        minDiscountPrice = productsellingPrice - minSellingPrice;

        jLabel17.setText(String.valueOf(minDiscountPrice));

    }

    public void loadGrnUpdate() {

        try {

            ResultSet rs = MySQL.executeSearch("SELECT * FROM `grn`"
                    + "INNER JOIN `grn_item` ON `grn_item`.`grn_id` = `grn`.`id` "
                    + "INNER JOIN `stock` ON `grn_item`.`stock_id` = `stock`.`id` "
                    + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                    + "INNER JOIN `category` ON `product`.`category_id` = `category`.`id`"
                    + "INNER JOIN `brand` ON `product`.`brand_id` = `brand`.`id`"
                    + "INNER JOIN `warranty` ON `product`.`warranty_id` = `warranty`.`id`"
                    + "INNER JOIN `supplier` ON `grn`.`Supplier_mobile` = `supplier`.`mobile`"
                    + "INNER JOIN `company_branch` ON `supplier`.`company_branch_id` = `company_branch`.`id`"
                    + "INNER JOIN `company` ON `company_branch`.`company_id` = `company_branch`.`company_id`"
                    + "LEFT JOIN invoice_item ON "
                    + "grn_item.stock_id = invoice_item.stock_id "
                    + "WHERE invoice_item.stock_id IS NULL");

            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0);

            while (rs.next()) {

                Vector<String> vector = new Vector<>();

                vector.add(rs.getString("grn.id"));
                vector.add(rs.getString("stock.id"));
                vector.add(rs.getString("product.id"));
                vector.add(rs.getString("product.name"));
                vector.add(rs.getString("product.model"));
                vector.add(rs.getString("brand.name"));
                vector.add(rs.getString("category.name"));
                vector.add(rs.getString("warranty.period"));
                vector.add(rs.getString("grn_item.buying_price"));
                vector.add(rs.getString("stock.selling_price"));
                vector.add(rs.getString("stock.discount"));
                vector.add(rs.getString("grn_item.qty"));
                vector.add(rs.getString("grn.date"));
                vector.add(rs.getString("company.name"));
                vector.add(rs.getString("company_branch.name"));
                vector.add(rs.getString("company_branch.branch_contact_number"));
                vector.add(rs.getString("supplier.fname") + " " + rs.getString("supplier.lname"));
                vector.add(rs.getString("supplier.mobile"));

                dtm.addRow(vector);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void quantityLimit() {
        try {
            // Add a DocumentFilter to allow only numeric input
            ((AbstractDocument) jTextField8.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    // Allow only digits (no length restriction here)
                    if (string.matches("[0-9]+")) {
                        super.insertString(fb, offset, string, attr);
                    }
                }

                @Override
                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    // Allow only digits (no length restriction here)
                    if (text.matches("[0-9]+")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void priceLimit(JFormattedTextField formattedTextField) {
        // Create a NumberFormatter that allows decimals
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00"); // Allows decimal points
        decimalFormat.setGroupingUsed(false); // Disable grouping (e.g., no thousands separator)

        NumberFormatter formatter = new NumberFormatter(decimalFormat);
        formatter.setValueClass(Double.class); // Use Double type to allow decimal numbers
        formatter.setAllowsInvalid(false);     // Prevent invalid input
        formatter.setMinimum(null);             // Set a minimum value (e.g., no negative prices)

        // Apply the formatter to the JFormattedTextField
        formattedTextField.setFormatterFactory(new DefaultFormatterFactory(formatter));
    }

    // Method to apply the priceLimit() to all JFormattedTextFields in a container
    public void applyPriceLimitToAll(Container container) {
        for (Component component : container.getComponents()) {
            // If the component is a container (e.g., JPanel), apply the method recursively
            if (component instanceof Container) {
                applyPriceLimitToAll((Container) component);
            }

            // If the component is a JFormattedTextField, apply the priceLimit method
            if (component instanceof JFormattedTextField) {
                priceLimit((JFormattedTextField) component);
            }
        }
    }

    public void loadBulckRates() {

        try {

            String query = "SELECT * FROM `rates` INNER JOIN `rates_values` ON `rates_values`.`rates_id` = `rates`.`id` WHERE `rates`.`name` = 'Bulck'";

            ResultSet resultSet = MySQL.executeSearch(query);

            if (resultSet.next()) {

                jLabel32.setText(resultSet.getString("rates.rate1") + "" + "%");
                jLabel41.setText(resultSet.getString("rates.rate2") + "" + "%");
                jLabel37.setText(resultSet.getString("rates_values.qty"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadAmountRates() {

        try {

            String query = "SELECT * FROM `rates` INNER JOIN `rates_values` ON `rates_values`.`rates_id` = `rates`.`id` WHERE `rates`.`name` = 'Percentage'";

            ResultSet resultSet = MySQL.executeSearch(query);

            if (resultSet.next()) {

                jLabel44.setText(resultSet.getString("rates.rate1") + "" + "%");
                jLabel48.setText(resultSet.getString("rates.rate2") + "" + "%");
                jLabel46.setText(resultSet.getString("rates_values.price"));
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
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jFormattedTextField4 = new javax.swing.JFormattedTextField();
        jFormattedTextField5 = new javax.swing.JFormattedTextField();
        jPanel18 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jFormattedTextField6 = new javax.swing.JFormattedTextField();
        jFormattedTextField7 = new javax.swing.JFormattedTextField();
        jFormattedTextField9 = new javax.swing.JFormattedTextField();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(853, 200));
        jPanel1.setLayout(new java.awt.GridLayout(1, 7));

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel7.setLayout(new java.awt.GridLayout(3, 0, 0, 10));

        jLabel1.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Update GRN");
        jPanel7.add(jLabel1);

        jTextField1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "GRN ID", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel7.add(jTextField1);

        jLabel3.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Employee");
        jLabel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Employee", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel7.add(jLabel3);

        jPanel1.add(jPanel7);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel6.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel28.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Stock Id");
        jPanel6.add(jLabel28);

        jTextField5.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel6.add(jTextField5);

        jLabel4.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Product Id");
        jPanel6.add(jLabel4);

        jTextField2.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel6.add(jTextField2);

        jPanel1.add(jPanel6);

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel10.setLayout(new java.awt.GridLayout(2, 0, 0, 10));

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("NULL");
        jLabel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Product Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel10.add(jLabel6);

        jLabel8.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 51, 51));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("NULL");
        jLabel8.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel10.add(jLabel8);

        jPanel1.add(jPanel10);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel5.setLayout(new java.awt.GridLayout(3, 0, 0, 10));

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("NULL");
        jLabel10.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Brand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel5.add(jLabel10);

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("NULL");
        jLabel12.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Category", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel5.add(jLabel12);

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("NULL");
        jLabel14.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Warranrt", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel5.add(jLabel14);

        jPanel1.add(jPanel5);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel4.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel15.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Buying Price");
        jPanel4.add(jLabel15);

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField1KeyReleased(evt);
            }
        });
        jPanel4.add(jFormattedTextField1);

        jLabel16.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Selling Price");
        jPanel4.add(jLabel16);

        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jFormattedTextField2KeyReleased(evt);
            }
        });
        jPanel4.add(jFormattedTextField2);

        jPanel1.add(jPanel4);

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel8.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel17.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Discount Price");
        jPanel8.add(jLabel17);

        jFormattedTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jFormattedTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField3ActionPerformed(evt);
            }
        });
        jPanel8.add(jFormattedTextField3);

        jLabel18.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Quantity");
        jPanel8.add(jLabel18);

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel8.add(jTextField4);

        jPanel1.add(jPanel8);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel3.setLayout(new java.awt.GridLayout(3, 0, 0, 10));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("NULL");
        jLabel20.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Company Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel3.add(jLabel20);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("NULL");
        jLabel22.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Branch", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel3.add(jLabel22);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("NULL");
        jLabel24.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Hotline", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel3.add(jLabel24);

        jPanel1.add(jPanel3);

        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel9.setLayout(new java.awt.GridLayout(4, 0, 0, 10));

        jLabel25.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Supplire Name");
        jPanel9.add(jLabel25);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("NULL");
        jPanel9.add(jLabel26);

        jLabel27.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Supplier Contact Number");
        jPanel9.add(jLabel27);

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel9.add(jTextField3);

        jPanel1.add(jPanel9);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel15.setPreferredSize(new java.awt.Dimension(1476, 280));

        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Bulck Discount Rate");

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Current Rate 1");

        jButton3.setText("Add");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("0 %");

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButton5.setText("Add");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("Current Quantity");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("0");

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Current Rate 2");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("0 %");

        jButton7.setText("Add");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jFormattedTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jFormattedTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createSequentialGroup()
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel36, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addComponent(jTextField8, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel16Layout.createSequentialGroup()
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 2, Short.MAX_VALUE))
                                .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel16Layout.createSequentialGroup()
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jFormattedTextField4)
                                .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel16Layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(jFormattedTextField5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel36)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("Total Amount Discount Rate");

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Current Rate 1");

        jButton8.setText("Add");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("0 %");

        jButton9.setText("Add");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("Current Total");

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("0");

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("Current Rate 2");

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("0 %");

        jButton10.setText("Add");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jFormattedTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jFormattedTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jFormattedTextField9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel18Layout.createSequentialGroup()
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                        .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel18Layout.createSequentialGroup()
                            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jFormattedTextField6)
                                .addComponent(jFormattedTextField7))
                            .addGap(18, 18, 18)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel18Layout.createSequentialGroup()
                            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                .addComponent(jFormattedTextField9))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel18Layout.createSequentialGroup()
                                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jFormattedTextField6)
                        .addGap(41, 41, 41)
                        .addComponent(jFormattedTextField7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel45)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel17.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel17.setLayout(new javax.swing.OverlayLayout(jPanel17));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel17.add(jScrollPane2);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1496, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        add(jPanel11, java.awt.BorderLayout.PAGE_END);

        jPanel12.setLayout(new java.awt.BorderLayout());

        jPanel13.setPreferredSize(new java.awt.Dimension(1496, 40));

        jButton2.setText("Update Grn");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap(624, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(54, 54, 54))
        );

        jPanel12.add(jPanel13, java.awt.BorderLayout.PAGE_START);

        jPanel19.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel19.setLayout(new javax.swing.OverlayLayout(jPanel19));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Grn Id", "Stock Id", "Product Id", "Product Name", "Model", "Brand", "Category", "Waranty", "Buying Price", "Selling Price", "Discount", "Quantity", "GRN date", "Company Name", "Branch", "Hotline", "Supplier Name", "Supplier Contact"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
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

        jPanel19.add(jScrollPane1);

        jPanel12.add(jPanel19, java.awt.BorderLayout.CENTER);

        add(jPanel12, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jFormattedTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField1KeyReleased
        priceCalculate();
    }//GEN-LAST:event_jFormattedTextField1KeyReleased

    private void jFormattedTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jFormattedTextField2KeyReleased
        priceCalculate();
    }//GEN-LAST:event_jFormattedTextField2KeyReleased

    private void jFormattedTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextField3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        String rate1 = jFormattedTextField4.getText();

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `rates` WHERE `name` = 'Bulck'");

            if (rate1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the Rate 1", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (Double.parseDouble(rate1) < 0) {
                JOptionPane.showMessageDialog(this, "invalid Rate", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                if (resultSet.next()) {

                    if (Double.parseDouble(resultSet.getString("rate2")) < Double.parseDouble(rate1)) {
                        MySQL.executeIUD("UPDATE `rates` SET `rate1` = '" + rate1 + "' WHERE `name` = 'Bulck'");

                        JOptionPane.showMessageDialog(this, "Update Success", "Succsess", JOptionPane.INFORMATION_MESSAGE);
                        loadBulckRates();
                        resetRate();
                    } else {
                        JOptionPane.showMessageDialog(this, "Over Rate 2", "Warning", JOptionPane.WARNING_MESSAGE);

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        String qty = jTextField8.getText();

        try {

            if (qty.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the Rate 1", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (Integer.parseInt(qty) < 0) {
                JOptionPane.showMessageDialog(this, "invalid Price", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                MySQL.executeIUD("UPDATE `rates_values` SET `qty` = '" + qty + "' WHERE `rates_id` = '1'");

                JOptionPane.showMessageDialog(this, "Update Success", "Succsess", JOptionPane.INFORMATION_MESSAGE);
                loadBulckRates();
                resetRate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String rate2 = jFormattedTextField5.getText();

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `rates` WHERE `name` = 'Bulck'");

            if (rate2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the Rate 1", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (Double.parseDouble(rate2) < 0) {
                JOptionPane.showMessageDialog(this, "invalid Rate", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                if (resultSet.next()) {

                    if (Double.parseDouble(resultSet.getString("rate1")) > Double.parseDouble(rate2)) {

                        MySQL.executeIUD("UPDATE `rates` SET `rate2` = '" + rate2 + "' WHERE `name` = 'Bulck'");

                        JOptionPane.showMessageDialog(this, "Update Success", "Succsess", JOptionPane.INFORMATION_MESSAGE);
                        loadBulckRates();
                        resetRate();
                    } else {
                        JOptionPane.showMessageDialog(this, "Over Rate 2", "Warning", JOptionPane.WARNING_MESSAGE);

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed

        String rate1 = jFormattedTextField6.getText();

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `rates` WHERE `name` = 'Percentage'");

            if (rate1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the Rate 1", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (Double.parseDouble(rate1) < 0) {
                JOptionPane.showMessageDialog(this, "invalid Rate", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                if (resultSet.next()) {

                    if (Double.parseDouble(resultSet.getString("rate2")) < Double.parseDouble(rate1)) {
                        MySQL.executeIUD("UPDATE `rates` SET `rate1` = '" + rate1 + "' WHERE `name` = 'Percentage'");

                        JOptionPane.showMessageDialog(this, "Update Success", "Succsess", JOptionPane.INFORMATION_MESSAGE);
                        loadAmountRates();
                        resetRate();
                    } else {
                        JOptionPane.showMessageDialog(this, "Over Rate 2", "Warning", JOptionPane.WARNING_MESSAGE);

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        String price = jFormattedTextField9.getText();

        try {

            if (price.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the Rate 1", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (Double.parseDouble(price) < 0) {
                JOptionPane.showMessageDialog(this, "invalid Price", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                MySQL.executeIUD("UPDATE `rates_values` SET `price` = '" + price + "' WHERE `rates_id` = '2'");

                JOptionPane.showMessageDialog(this, "Update Success", "Succsess", JOptionPane.INFORMATION_MESSAGE);
                loadAmountRates();
                resetRate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String rate2 = jFormattedTextField7.getText();

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `rates` WHERE `name` = 'Percentage'");

            if (rate2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please Enter the Rate 1", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if (Double.parseDouble(rate2) < 0) {
                JOptionPane.showMessageDialog(this, "invalid Rate", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                if (resultSet.next()) {

                    if (Double.parseDouble(resultSet.getString("rate1")) > Double.parseDouble(rate2)) {
                        MySQL.executeIUD("UPDATE `rates` SET `rate2` = '" + rate2 + "' WHERE `name` = 'Percentage'");

                        JOptionPane.showMessageDialog(this, "Update Success", "Succsess", JOptionPane.INFORMATION_MESSAGE);
                        loadAmountRates();
                        resetRate();
                    } else {
                        JOptionPane.showMessageDialog(this, "Over Rate 2", "Warning", JOptionPane.WARNING_MESSAGE);

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        jButton2.setEnabled(true);

        int row = jTable1.getSelectedRow();

        try {

            jTextField1.setText(String.valueOf(jTable1.getValueAt(row, 0)));
            jTextField5.setText(String.valueOf(jTable1.getValueAt(row, 1)));
            jTextField2.setText(String.valueOf(jTable1.getValueAt(row, 2)));
            jLabel6.setText(String.valueOf(jTable1.getValueAt(row, 3)));
            jLabel8.setText(String.valueOf(jTable1.getValueAt(row, 4)));
            jLabel10.setText(String.valueOf(jTable1.getValueAt(row, 5)));
            jLabel12.setText(String.valueOf(jTable1.getValueAt(row, 6)));
            jLabel14.setText(String.valueOf(jTable1.getValueAt(row, 7)));
            jFormattedTextField1.setText(String.valueOf(jTable1.getValueAt(row, 8)));
            jFormattedTextField2.setText(String.valueOf(jTable1.getValueAt(row, 9)));
            jFormattedTextField3.setText(String.valueOf(jTable1.getValueAt(row, 10)));
            jTextField4.setText(String.valueOf(jTable1.getValueAt(row, 11)));
            jLabel20.setText(String.valueOf(jTable1.getValueAt(row, 13)));
            jLabel22.setText(String.valueOf(jTable1.getValueAt(row, 14)));
            jLabel24.setText(String.valueOf(jTable1.getValueAt(row, 15)));
            jLabel26.setText(String.valueOf(jTable1.getValueAt(row, 16)));
            jTextField3.setText(String.valueOf(jTable1.getValueAt(row, 17)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String employee = jLabel3.getText();

        String GrnId = jTextField1.getText();
        String stockId = jTextField5.getText();
        String productId = jTextField2.getText();
        String productName = jLabel6.getText();
        String model = jLabel8.getText();
        String brand = jLabel10.getText();
        String category = jLabel12.getText();
        String waranty = jLabel14.getText();
        String buyingPrice = jFormattedTextField1.getText();
        String sellingPrice = jFormattedTextField2.getText();
        String discountPrice = jFormattedTextField3.getText();
        String quantity = jTextField4.getText();
        String supplierContact = jTextField3.getText();

        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Loging first", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (productId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Select a Prooduct", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            try {
                if (buyingPrice.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please Enter the Buying Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (Double.parseDouble(buyingPrice) < 0) {
                    JOptionPane.showMessageDialog(this, "Please Check the Buying Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (!buyingPrice.matches("^\\d+(\\.\\d+)?$")) {
                    JOptionPane.showMessageDialog(this, "Please Check(symbols) the Buying Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (sellingPrice.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please Enter the Buying Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (Double.parseDouble(sellingPrice) < 0) {
                    JOptionPane.showMessageDialog(this, "Please Check the Selling Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (!sellingPrice.matches("^\\d+(\\.\\d+)?$")) {
                    JOptionPane.showMessageDialog(this, "Please Check(symbols) the Selling Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (discountPrice.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please Enter the Dicount Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (Double.parseDouble(discountPrice) < 0) {
                    JOptionPane.showMessageDialog(this, "Please Check the Discount Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (!discountPrice.matches("^\\d+(\\.\\d+)?$")) {
                    JOptionPane.showMessageDialog(this, "Please Check(symbols) the Discount Price", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (quantity.equals("0")) {
                    JOptionPane.showMessageDialog(this, "Please Enter the Quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (Integer.parseInt(quantity) < 0) {
                    JOptionPane.showMessageDialog(this, "Please Enter the valid Quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (supplierContact.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please Select A Supplier", "Warning", JOptionPane.WARNING_MESSAGE);

                } else {

                    try {

                        ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `grn_item` "
                            + "INNER JOIN `stock` ON `grn_item`.`stock_id` = `stock`.`id` "
                            + "WHERE `buying_price` = '" + buyingPrice + "'"
                            + " AND `stock`.`selling_price` = '" + sellingPrice + "' "
                            + " AND `stock`.`qty` = '" + quantity + "'"
                            + " AND `stock`.`discount` = '" + discountPrice + "'");

                        if (resultSet.next()) {
                            JOptionPane.showMessageDialog(this, "Can't Update change any prices or quantity to update", "Warning", JOptionPane.WARNING_MESSAGE);
                        } else {

                            MySQL.executeIUD("UPDATE `grn_item` SET `qty` = '" + quantity + "' WHERE `grn_id` = '" + GrnId + "' AND `stock_id` = '" + stockId + "'");

                            MySQL.executeIUD("UPDATE `stock` SET `selling_price` = '" + sellingPrice + "',`discount` = '" + discountPrice + "',`qty` = '" + quantity + "'"
                                + " WHERE `stock`.`id` = '" + stockId + "'");

                            JOptionPane.showMessageDialog(this, "Grn Aded Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                            loadGrnUpdate();
                            reset();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Input", "Warning", JOptionPane.WARNING_MESSAGE);

            }

        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        reset();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JFormattedTextField jFormattedTextField6;
    private javax.swing.JFormattedTextField jFormattedTextField7;
    private javax.swing.JFormattedTextField jFormattedTextField9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
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
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables

    private void reset() {
        jTextField2.setText("");
        jLabel16.setText("Selling Price");
        jLabel17.setText("Discount Price");
        jLabel6.setText("NULL");
        jLabel8.setText("NULL");
        jLabel10.setText("NULL");
        jLabel12.setText("NULL");
        jLabel14.setText("NULL");
        jFormattedTextField1.setValue(null);
        jFormattedTextField2.setValue(null);
        jTextField4.setText("0");
        jTextField5.setText("");
        jFormattedTextField3.setValue(null);
        jLabel20.setText("NULL");
        jLabel22.setText("NULL");
        jLabel24.setText("NULL");
        jLabel26.setText("NULL");
        jTextField3.setText("");

        jTable1.clearSelection();

        jButton2.setEnabled(false);

    }

    private void resetRate() {

        jFormattedTextField4.setValue(null);
        jFormattedTextField5.setValue(null);
        jFormattedTextField6.setValue(null);
        jFormattedTextField7.setValue(null);
        jFormattedTextField9.setValue(null);

        jTextField8.setText("");

    }

}
