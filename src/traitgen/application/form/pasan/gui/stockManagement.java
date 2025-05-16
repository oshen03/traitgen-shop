/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package traitgen.application.form.pasan.gui;


//import Admin.gui.Dashboard;
//import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
//import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
//import com.myapp.themes.MyCustomLaf;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import traitgen.application.form.rashmika.gui.Invoice;
import raven.toast.Notifications;

public class stockManagement extends javax.swing.JFrame {

    private Invoice invoice;

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    DecimalFormat df = new DecimalFormat("0.00");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public void loadCategories2() {

        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `category`");

            Vector v = new Vector();
            v.add("Select Category");

            while (rs.next()) {
                v.add(rs.getString("name"));
            }

            DefaultComboBoxModel dcm = new DefaultComboBoxModel(v);
            combobox2.setModel(dcm);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadBrand2() {

        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `brand`");

            Vector v = new Vector();
            v.add("Select brand");

            while (rs.next()) {
                v.add(rs.getString("name"));
            }

            DefaultComboBoxModel dcm = new DefaultComboBoxModel(v);
            combobox1.setModel(dcm);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void lowStock() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT DISTINCT * FROM `stock` "
                    + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                    + "WHERE `stock`.`qty` <= 5 ORDER BY `product`.`name` ASC");

            DefaultListModel<String> ls = new DefaultListModel<>();
            while (rs.next()) {
                String productName = rs.getString("product.name");
                int quantity = rs.getInt("qty");
                //double sellingPrice = rs.getDouble("selling_price");
                ls.addElement("- : " + productName + "  " + quantity + "");
            }

            SwingUtilities.invokeLater(() -> {
                jList1.setModel(ls);
                jList1.setFixedCellHeight(32);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadStock() {
        String outofqty = null;
        try {
            ResultSet rs = MySQL.executeSearch("SELECT DISTINCT `stock`.`id`,`product`.`id`,`warranty`.`period`,`category`.`name`,`brand`.`name`,`product`.`name`"
                    + ",`product`.`model`,`stock`.`qty`,`grn_item`.`buying_price`"
                    + ",`stock`.`selling_price`,`grn_item`.`buying_price` FROM `stock` "
                    + " INNER JOIN `grn_item` ON `grn_item`.`stock_id` = `stock`.`id` "
                    + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id`"
                    + " INNER JOIN `brand` ON `product`.`brand_id` = `brand`.`id` "
                    + " INNER JOIN `warranty` ON `product`.`warranty_id` = `warranty`.`id` "
                    + "INNER JOIN category ON `product`.`category_id` = `category`.id ORDER BY `product`.`name` ASC");
            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0);

            while (rs.next()) {

                Vector v = new Vector();

                v.add(rs.getString("stock.id"));
                v.add(rs.getString("product.id"));
                v.add(rs.getString("product.name"));
                v.add(rs.getString("product.model"));
                v.add(rs.getString("category.name"));
                v.add(rs.getString("brand.name"));
                v.add(rs.getString("warranty.period"));
                v.add(rs.getString("grn_item.buying_price"));
                v.add(rs.getString("stock.selling_price"));
                v.add(rs.getString("stock.qty"));

                dtm.addRow(v);

                if (rs.getString("stock.qty").equals("0")) {
                    if (outofqty == null) {
                        outofqty = "";
                    }

                    outofqty += rs.getString("product.name") + "\n";
                }
            }

            //System.out.println(outofqty);
            if (outofqty == null) {

            } else {
                StringBuffer sb = new StringBuffer(outofqty);
                sb.deleteCharAt(sb.length() - 1);
                //JOptionPane.showMessageDialog(this, sb + "\n These products are out of stock", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            jTable1.setModel(dtm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SellandCost() {
        double cost = 0;
        double sell = 0;

        int rowCount = jTable1.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            try {
                // Retrieve the cost and selling price from the table, making sure values are not null
                Object costObj = jTable1.getValueAt(i, 7);
                Object sellObj = jTable1.getValueAt(i, 8);

                if (costObj != null && sellObj != null) {
                    double c = Double.parseDouble(costObj.toString());
                    double s = Double.parseDouble(sellObj.toString());

                    cost += c;
                    sell += s;
                } else {
                    // System.out.println("Null value encountered at row " + i);
                }

            } catch (NumberFormatException e) {
                // Handle the case where a cell value can't be parsed as a double
                JOptionPane.showMessageDialog(this, "Error parsing cost/sell values at row " + i,
                        "Parsing Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Ensure df (DecimalFormat) is initialized correctly somewhere in your class
        jCost.setText(df.format(cost));
        jTotal.setText(df.format(sell));
    }

    public void loadCategories() {

        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `category`");

            Vector v = new Vector();
            v.add("Select Category");

            while (rs.next()) {
                v.add(rs.getString("name"));
            }

            DefaultComboBoxModel dcm = new DefaultComboBoxModel(v);
            combobox2.setModel(dcm);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadProducts() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `product` "
                    + "INNER JOIN `brand` ON `product`.`brand_id`=`brand`.`id`"
                    + "INNER JOIN `warranty` ON `product`.`warranty_id`=`warranty`.`id` "
                    + "INNER JOIN `category` ON `product`.`category_id`=`category`.`id`");
            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                Vector v = new Vector();
                v.add(rs.getString("id"));
                v.add(rs.getString("name"));
                v.add(rs.getString("model"));
                v.add(rs.getString("warranty.period"));
                 v.add(rs.getString("category.name"));
                v.add(rs.getString("brand.name"));
                dtm.addRow(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadProducts();
        }
    }

    private void searchStock() {
    try {
        // Retrieve input values from UI components
        String category = combobox2.getSelectedItem() != null ? combobox2.getSelectedItem().toString() : "Select Category";
        String brand = combobox1.getSelectedItem() != null ? combobox1.getSelectedItem().toString() : "Select Brand";
        String barcode = jTextField1.getText();
        String product_name = jTextField4.getText();
        String sp_min = jTextField5.getText();
        String sp_max = jTextField6.getText();

        Vector<String> queryVector = new Vector<>();

        // Build query conditions based on input values
        if (!"Select Category".equals(category)) {
            queryVector.add("`category`.`name`='" + category + "'");
        }
        if (!"Select Brand".equals(brand)) {
            queryVector.add("`brand`.`name`='" + brand + "'");
        }
        if (!barcode.isEmpty()) {
            queryVector.add("`product`.`id` LIKE '%" + barcode + "%'");
        }
        if (!product_name.isEmpty()) {
            queryVector.add("`product`.`name` LIKE '%" + product_name + "%'");
        }
        if (!sp_min.isEmpty()) {
            queryVector.add(!sp_max.isEmpty()
                    ? "`stock`.`selling_price` BETWEEN '" + sp_min + "' AND '" + sp_max + "'"
                    : "`stock`.`selling_price`>='" + sp_min + "'");
        }
        if (!sp_max.isEmpty() && sp_min.isEmpty()) {
            queryVector.add("`stock`.`selling_price`<='" + sp_max + "'");
        }

        // Construct the WHERE clause
        String wherequery = queryVector.isEmpty() ? "" : "WHERE " + String.join(" AND ", queryVector);

        // Construct the SQL query
        String sql = "SELECT `stock`.`id`, `product`.`id` AS product_id, `product`.`name`, `product`.`model`, "
                + "`category`.`name` AS category_name, `brand`.`name` AS brand_name, `warranty`.`period`, "
                + "`grn_item`.`buying_price`, `stock`.`selling_price`, `stock`.`qty` "
                + "FROM `stock` "
                + "INNER JOIN `grn_item` ON `grn_item`.`stock_id` = `stock`.`id` "
                + "INNER JOIN `product` ON `stock`.`product_id` = `product`.`id` "
                + "INNER JOIN `brand` ON `product`.`brand_id` = `brand`.`id` "
                + "INNER JOIN `warranty` ON `product`.`warranty_id` = `warranty`.`id` "
                + "INNER JOIN `category` ON `product`.`category_id` = `category`.`id` "
                + wherequery;

        // Execute the query
        ResultSet rs = MySQL.executeSearch(sql);

        // Populate the table with the result set
        DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
        dtm.setRowCount(0);

        while (rs.next()) {
            Vector<String> row = new Vector<>();
            row.add(rs.getString("stock.id"));
            row.add(rs.getString("product_id"));
            row.add(rs.getString("product.name"));
            row.add(rs.getString("product.model"));
            row.add(rs.getString("category_name"));
            row.add(rs.getString("brand_name"));
            row.add(rs.getString("warranty.period"));
            row.add(rs.getString("grn_item.buying_price"));
            row.add(rs.getString("stock.selling_price"));
            row.add(rs.getString("stock.qty"));
            dtm.addRow(row);
        }

        jTable1.setModel(dtm);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void resetFields() {
        //jComboBox3.setSelectedIndex(0);
        combobox1.setSelectedIndex(0);
        //jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        jCost.setText("");
        jTotal.setText("");
        jTable1.clearSelection();
        loadStock();
    }
    private void resetFields1() {
        jLabel43.setText("");
        jLabel44.setText("");
        jCost.setText("");
        jTotal.setText("");
        jTable1.clearSelection();
        jTextField7.setText("");
        loadStock();
    }
    
    public stockManagement() {
        initComponents();
        searchStock();
        loadBrand2();
        loadCategories();
        loadCategories2();
        loadProducts();
        //loadSuppliers();
        lowStock();
        loadStock();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlCard3 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel15 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jCost = new javax.swing.JLabel();
        jTotal = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jButton24 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        combobox1 = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        combobox2 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel40.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 102, 102));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Update Stock");

        jButton25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton25.setText("Update");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jLabel43.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock ID "));

        jLabel44.setBorder(javax.swing.BorderFactory.createTitledBorder("Buying Price"));

        jTextField7.setBorder(javax.swing.BorderFactory.createTitledBorder("New  Price "));
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField7KeyTyped(evt);
            }
        });

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));

        jList1.setFont(new java.awt.Font("Open Sans Semibold", 1, 14)); // NOI18N
        jList1.setForeground(new java.awt.Color(255, 0, 0));
        jScrollPane2.setViewportView(jList1);

        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 0, 270, 630));

        jLabel39.setFont(new java.awt.Font("Open Sans Semibold", 1, 12)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Low Stock Remains");
        jPanel13.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 141, 275, 20));

        jScrollPane4.setBackground(new java.awt.Color(153, 255, 204));

        jTable1.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock ID", "Product ID", "Product Name", "Model", "Category", "Brand", "Waranty", "Buying Price", "Selling Price", "Quantity"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(jTable1);

        jPanel15.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel24.setFont(new java.awt.Font("Open Sans Semibold", 1, 14)); // NOI18N
        jLabel24.setText("Sale Price");

        jLabel25.setFont(new java.awt.Font("Open Sans Semibold", 1, 14)); // NOI18N
        jLabel25.setText("Cost Price");

        jLabel26.setFont(new java.awt.Font("Open Sans", 1, 13)); // NOI18N
        jLabel26.setText("Total Cost :");

        jCost.setFont(new java.awt.Font("Open Sans", 0, 13)); // NOI18N

        jTotal.setFont(new java.awt.Font("Bahnschrift", 0, 13)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Open Sans", 1, 13)); // NOI18N
        jLabel27.setText("Total :");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCost, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addComponent(jTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel16.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel28.setFont(new java.awt.Font("Open Sans Semibold", 1, 18)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Search");

        jButton24.setText("Reset");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 10, 10));

        jTextField6.setFont(new java.awt.Font("Open Sans Semibold", 0, 12)); // NOI18N
        jTextField6.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Selling Price"));
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField6KeyTyped(evt);
            }
        });
        jPanel1.add(jTextField6);

        jTextField5.setFont(new java.awt.Font("Open Sans Semibold", 0, 12)); // NOI18N
        jTextField5.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Selling Price"));
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField5KeyTyped(evt);
            }
        });
        jPanel1.add(jTextField5);

        jPanel2.setLayout(new java.awt.GridLayout(2, 1, 10, 10));

        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Id "));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        jPanel2.add(jTextField1);

        combobox1.setFont(new java.awt.Font("Open Sans Semibold", 0, 12)); // NOI18N
        combobox1.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Brand "));
        combobox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combobox1ItemStateChanged(evt);
            }
        });
        combobox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox1ActionPerformed(evt);
            }
        });
        jPanel2.add(combobox1);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1, 10, 10));

        jTextField4.setFont(new java.awt.Font("Open Sans Semibold", 0, 12)); // NOI18N
        jTextField4.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Name"));
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });
        jPanel3.add(jTextField4);

        combobox2.setFont(new java.awt.Font("Open Sans Semibold", 0, 12)); // NOI18N
        combobox2.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Category "));
        combobox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                combobox2ItemStateChanged(evt);
            }
        });
        combobox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox2ActionPerformed(evt);
            }
        });
        jPanel3.add(combobox2);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout pnlCard3Layout = new javax.swing.GroupLayout(pnlCard3);
        pnlCard3.setLayout(pnlCard3Layout);
        pnlCard3Layout.setHorizontalGroup(
            pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard3Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlCard3Layout.setVerticalGroup(
            pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCard3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCard3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlCard3Layout.createSequentialGroup()
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCard3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCard3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        String buyingPrice = jLabel44.getText();
        String newPrice = jTextField7.getText();

        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            //JOptionPane.showMessageDialog(this, "Please select a stock", "Warning", JOptionPane.WARNING_MESSAGE);
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_RIGHT, "Please select a stock");
        } else if (!Pattern.compile("(0)|([1-9][0-9]*|(([1-9][0-9]*)[.]([0]*[1-9][0-9]*))|([0][.]([0]*[1-9][0-9]*)))").matcher(newPrice).matches()) {
            //JOptionPane.showMessageDialog(this, "Invalid Selling Price", "Warning", JOptionPane.WARNING_MESSAGE);
            Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_RIGHT, "Invalid Selling Price");
        } else {

            String stock_id = jTable1.getValueAt(selectedRow, 0).toString();

            if (Double.parseDouble(newPrice) <= Double.parseDouble(buyingPrice)) {

                int x = JOptionPane.showConfirmDialog(this, "New price <= buying price. Do you want to continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (x == JOptionPane.YES_OPTION) {
                    try {
                        MySQL.executeIUD("UPDATE `stock` SET `selling_price`='" + newPrice + "' WHERE `id`='" + stock_id + "'");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                try {
                    MySQL.executeIUD("UPDATE `stock` SET `selling_price`='" + newPrice + "' WHERE `id`='" + stock_id + "'");
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_RIGHT, "Stock Selling price updated");
                    resetFields1();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SellandCost();
            jLabel44.setText("");
            jTextField7.setText("");

            loadStock();
           

        }
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

        int selectedRow = jTable1.getSelectedRow();
        
        if (selectedRow != -1) {

            String stock_id = jTable1.getValueAt(selectedRow, 0).toString();
            String buying_price = jTable1.getValueAt(selectedRow, 7).toString();
            jLabel44.setText(buying_price);
            jLabel43.setText(stock_id);

        }

        if (evt.getClickCount() == 2) {

            if (invoice != null) {

                invoice.getStockId().setText(String.valueOf(jTable1.getValueAt(selectedRow, 0)));
//                invoice.getProductId().setText(String.valueOf(jTable1.getValueAt(selectedRow, 1)));
                invoice.getProductName().setText(String.valueOf(jTable1.getValueAt(selectedRow, 2)));
                invoice.getModel().setText(String.valueOf(jTable1.getValueAt(selectedRow, 3)));

            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased

        searchStock();
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased

        searchStock();
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyTyped

        String price = jTextField5.getText();
        String text = price + evt.getKeyChar();

        if (!Pattern.compile("(0|0[.]|0[.][0-9]*)|[1-9]|[1-9][0-9]*|[1-9][0-9]*[.]|[1-9][0-9]*[.][0-9]*").matcher(text).matches()) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField5KeyTyped

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased

        searchStock();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jTextField6KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyTyped

        String price = jTextField6.getText();
        String text = price + evt.getKeyChar();

        if (!Pattern.compile("(0|0[.]|0[.][0-9]*)|[1-9]|[1-9][0-9]*|[1-9][0-9]*[.]|[1-9][0-9]*[.][0-9]*").matcher(text).matches()) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField6KeyTyped

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed

        resetFields();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        searchStock();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1KeyReleased

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        searchStock();
    }//GEN-LAST:event_jTextField1KeyTyped

    private void combobox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combobox2ActionPerformed

    private void combobox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combobox2ItemStateChanged
        searchStock();
    }//GEN-LAST:event_combobox2ItemStateChanged

    private void combobox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combobox1ActionPerformed

    private void combobox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_combobox1ItemStateChanged

        searchStock();
    }//GEN-LAST:event_combobox1ItemStateChanged

    private void jTextField7KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyTyped
        SellandCost();
    }//GEN-LAST:event_jTextField7KeyTyped

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
       SellandCost();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        Dashboard cp = new Dashboard();
//        //cp.setEmail(email); // Pass email to the ControlePanel
//        cp.setVisible(true);
//        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacLightLaf.setup();
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new stockManagement().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combobox1;
    private javax.swing.JComboBox<String> combobox2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JLabel jCost;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel jTotal;
    private javax.swing.JPanel pnlCard3;
    // End of variables declaration//GEN-END:variables
}
