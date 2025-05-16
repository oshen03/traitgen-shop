/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package traitgen.application.form.rashmika.gui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.InputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import model.MySQL;
import traitgen.application.form.pasan.gui.stockManagement;
import rashmika.model.InvoiceItem;
//import shivoni.gui.RepairDashboard;
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.util.ByteArrayDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
import traitgen.application.form.shivoni.gui.RepairAdd;

/**
 *
 * @author VICTUS
 */
public class Invoice extends javax.swing.JFrame {

    HashMap<String, InvoiceItem> invoiceitemMap = new HashMap<>();
    HashMap<String, String> paymentMap = new HashMap<>();
    HashMap<String, String> discuontMap = new HashMap<>();

//    private RepairDashboard rdFrame;
    private stockManagement stockFrame;

    private int search_state = 1;
    public String product_id;

    //product Name
    public JLabel getProductName() {
        return jLabel6;
    }

    //model
    public JLabel getModel() {
        return jLabel8;
    }

    //brand
    public JLabel getBrand() {
        return jLabel10;
    }

    //category 
    public JFormattedTextField getDiscount() {
        return jFormattedTextField1;
    }

    // waranty
    public JLabel getWaranty() {
        return jLabel14;
    }

    // avQty
    public JLabel getQty() {
        return jLabel20;
    }

    //stock Id
    public JTextField getStockId() {
        return jTextField3;
    }

    //selling Price
    public JFormattedTextField getSellingPrice() {
        return jFormattedTextField2;
    }

    public Invoice() {
        initComponents();

        loadPaymentMethod();
        loadDiscountType();
        genarateInvoiceId();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        setExtendedState(MAXIMIZED_BOTH);

        jButton12.setEnabled(false);
        paymentFeild.setEnabled(false);

        jLabel17.setText("shivoni@gmail.com");

        jPopupMenu1.add(jPanel21);
        jPopupMenu1.setFocusable(false);

        discountFeild.setEnabled(false);

        paymentMethodCombobox.setEnabled(false);

//        jCheckBox3.setSelected(true);
        jRadioButton1.setSelected(true);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTable1.setDefaultRenderer(Object.class, renderer);

        mobileLimit();
        quantityLimit();
    }

    private void load_status() {
        switch (search_state) {
            case 1:
                if (jTextField1.getText().equals("")) {
                    jTextField1.setText("Search product by name");
                    jTextField1.setForeground(new Color(153, 153, 153));
                }
            case 2:
                if (jTextField1.getText().equals("")) {
                    jTextField1.setText("Search product by barcode");
                    jTextField1.setForeground(new Color(153, 153, 153));
                }
            case 3:
                if (jTextField1.getText().equals("")) {
                    jTextField1.setText("Search product by product id");
                    jTextField1.setForeground(new Color(153, 153, 153));
                }
        }
    }

    private double total = 0;
    private int totalQty = 0;
    private double discountPrice = 0;

    public void loadInvoiceItem() {

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        total = 0;
        totalQty = 0;
        discountPrice = 0;

        for (InvoiceItem invoiceItem : invoiceitemMap.values()) {

            Vector<String> vector = new Vector<>();

            vector.add(invoiceItem.getStockId());
            vector.add(invoiceItem.getProductName());
            vector.add(invoiceItem.getModel());
            vector.add(invoiceItem.getBrand());
            vector.add(invoiceItem.getWarranty());
            vector.add(invoiceItem.getSellingPrice());
            vector.add(invoiceItem.getDiscountPrice());
            vector.add(invoiceItem.getQty());

            int itemeQty = Integer.parseInt(invoiceItem.getQty());
            totalQty += itemeQty;

            double itemDiscount = Double.parseDouble(invoiceItem.getDiscountPrice()) * Double.parseDouble(invoiceItem.getQty());
            discountPrice += itemDiscount;

            double itemTotal = Double.parseDouble(invoiceItem.getQty()) * Double.parseDouble(invoiceItem.getSellingPrice());
            total += (itemTotal - itemDiscount);

            vector.add(String.valueOf(itemDiscount));
            vector.add(String.valueOf(itemTotal - itemDiscount));
            model.addRow(vector);

        }

        totalFeild.setText(String.valueOf(total));
        jLabel28.setText(String.valueOf(totalQty));
        jLabel26.setText(String.valueOf(discountPrice));

    }

    public void genarateInvoiceId() {

        long mTime = System.currentTimeMillis();
        int customId = (int) (mTime % 1000000); // Get the last 6 digits
        String formattedId = String.format("%04d", customId); // Format to ensure it's always 6 digits

        String customText = "CM";
        String finalId = customText + formattedId;

        jTextField5.setText(finalId);

    }

    public void loadCustomer() {

        try {

            String cusMobile = jTextField2.getText();

            ResultSet resultSet = MySQL.executeSearch("SELECT DISTINCT `first_name`,`last_name`,`email` FROM `customer` WHERE `mobile` = '" + cusMobile + "'");

            if (resultSet.next()) {

                jLabel1.setText(resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
                jLabel24.setText(resultSet.getString("email"));
            } else {
                jLabel1.setText("Customer Name");
                jLabel24.setText("Customer Email");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void mobileLimit() {

        try {
            // Add a DocumentFilter to limit input to numbers and max length of 10 digits
            ((AbstractDocument) jTextField2.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    // Allow only digits, allow empty string, and limit to 10 characters
                    if (string.isEmpty() || (string.matches("[0-9]+") && fb.getDocument().getLength() + string.length() <= 10)) {
                        super.insertString(fb, offset, string, attr);
                    }
                }

                @Override
                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    // Allow only digits, allow empty string, and limit to 10 characters
                    if (text.isEmpty() || (text.matches("[0-9]+") && fb.getDocument().getLength() - length + text.length() <= 10)) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quantityLimit() {
        try {
            // Add a DocumentFilter to allow only numeric input, but also allow clearing the text
            ((AbstractDocument) jTextField4.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    // Allow only digits or an empty string
                    if (string.isEmpty() || string.matches("[0-9]+")) {
                        super.insertString(fb, offset, string, attr);
                    }
                }

                @Override
                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    // Allow only digits or an empty string
                    if (text.isEmpty() || text.matches("[0-9]+")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPaymentMethod() {

        try {

            ResultSet rs = MySQL.executeSearch("SELECT * FROM `payment_method`");

            Vector<String> vector = new Vector<>();

            while (rs.next()) {

                vector.add(rs.getString("name"));

                paymentMap.put(rs.getString("name"), rs.getString("id"));
            }

            DefaultComboBoxModel boxModel = new DefaultComboBoxModel(vector);
            paymentMethodCombobox.setModel(boxModel);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadDiscountType() {

        try {

            ResultSet rs = MySQL.executeSearch("SELECT * FROM `discount_type`");

            Vector<String> vector = new Vector<>();

            vector.add("Select");

            while (rs.next()) {

                vector.add(rs.getString("name"));

                discuontMap.put(rs.getString("name"), rs.getString("id"));

            }

            DefaultComboBoxModel boxModel = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(boxModel);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private double discount = 0;
    private double payment = 0;
    private double balance = 0;
    private String paymentMethod = "Select";

    public void calculate() {

        total = Double.parseDouble(totalFeild.getText());

        String selectDisType = String.valueOf(jComboBox1.getSelectedItem());

        int numberOfQty = (int) Double.parseDouble(jLabel28.getText());
        double discountPrice = Double.parseDouble(jLabel26.getText());
        double totalPrice = Double.parseDouble(totalFeild.getText());

        applyDiscount(selectDisType, numberOfQty, discountPrice, totalPrice);

        if (paymentFeild.getText().isEmpty()) {
            payment = 0;
        } else {
            payment = Double.parseDouble(paymentFeild.getText());
        }

        total -= discount;

        paymentMethod = String.valueOf(paymentMethodCombobox.getSelectedItem());

        if (paymentMethod.equals("Cash")) {
            //cash
            paymentFeild.setEditable(true);
            balance = payment - total;

        } else if (paymentMethod.equals("Credit")) {
            //card
            payment = total;
            balance = 0;
            paymentFeild.setText(String.valueOf(payment));
            paymentFeild.setEditable(false);

        }

        if (paymentFeild.getText().equals("0.00")) {
            jButton12.setEnabled(false);
        }else {
            jButton12.setEnabled(true);
        }
        balanceFeild.setText(String.valueOf(balance));
    }

    private double productPrice = 0;

    public void applyDiscount(String discountType, int numberOfQty, double discountTotal, double totalPrice) {

        try {

            for (InvoiceItem invoiceItem : invoiceitemMap.values()) {
                String sid = invoiceItem.getStockId();

                ResultSet rs = MySQL.executeSearch("SELECT DISTINCT * FROM `grn_item` "
                        + "INNER JOIN stock ON `grn_item`.`stock_id` = `stock`.`id` "
                        + "INNER JOIN product ON stock.product_id = product.id "
                        + "WHERE `stock`.`id` = '" + sid + "'");

                if (rs.next()) {
                    productPrice = Double.parseDouble(rs.getString("grn_item.buying_price"));
                }
            }

            double minimumSellingPrice = productPrice + (productPrice * 20 / 100);

            if (discountType.equals("Fixed") && discountTotal == 0 && numberOfQty == 1) {

                discount = Double.parseDouble(discountFeild.getText());

                if ((total - discount) < minimumSellingPrice) {
//                        discount = total - minimumSellingPrice;
                    paymentFeild.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "can not set this discount", "warning", JOptionPane.WARNING_MESSAGE);

                } else {
                    paymentFeild.setEnabled(true);

                }
            } else if (discountType.equals("Bulck")) {

                double mindiscount = total * getBulkDiscountRate(numberOfQty) / 100;

                discount = Double.parseDouble(discountFeild.getText());

//                System.out.println(mindiscount);
                if (discount > mindiscount) {

                    paymentFeild.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Mximum discount is : Rs. " + mindiscount + "", "warning", JOptionPane.WARNING_MESSAGE);

                } else {
                    paymentFeild.setEnabled(true);

                }

            } else if (discountType.equals("Percentage")) {

                double mindiscount = total * getPercentageDiscountRate(totalPrice) / 100;
                discount = Double.parseDouble(discountFeild.getText());

                if (discount > mindiscount) {

                    paymentFeild.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Mximum discount is :Rs. " + mindiscount + "", "warning", JOptionPane.WARNING_MESSAGE);

                } else {
                    paymentFeild.setEnabled(true);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private double getBulkDiscountRate(int numberOfqty) {

        double value = 0;

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `rates` INNER JOIN `rates_values` ON `rates_values`.`rates_id` = `rates`.`id`"
                    + " WHERE `rates`.`name` = 'Bulck'");

            if (resultSet.next()) {

                if (numberOfqty > Integer.parseInt(resultSet.getString("rates_values.qty"))) {
                    value = Double.parseDouble(resultSet.getString("rates.rate1"));//  10% discount for more than 5 products
                } else {
                    value = Double.parseDouble(resultSet.getString("rates.rate2")); // 5% discount for fewer products
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private double getPercentageDiscountRate(double totalPrice) {

        double value = 0;

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `rates` INNER JOIN `rates_values` ON `rates_values`.`rates_id` = `rates`.`id`"
                    + " WHERE `rates`.`name` = 'Percentage'");

            if (resultSet.next()) {

                if (totalPrice > Double.parseDouble(resultSet.getString("rates_values.price"))) {
                    value = Double.parseDouble(resultSet.getString("rates.rate1"));
                } else {
                    value = Double.parseDouble(resultSet.getString("rates.rate2"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;

    }

//    public static void sendEmail(String to, String subject, String messageText) {
//        final String from = "";
//        final String password = "jiybhwxefzeyoupg";  // Use an app-specific password
//
//        Properties properties = new Properties();
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "465");
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.ssl.enable", "true");  // For SSL
//        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");  // Force TLS 1.2
//
//        // Enable debug output
//        Session session = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(from, password);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(from));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//            message.setSubject(subject);
//            message.setText(messageText);
//            // Attempt to send the message
//            Transport.send(message);
//
//        } catch (MessagingException e) {
//            
//            e.printStackTrace();  // This will print any error in more detail
//        }
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        paymentMethodCombobox = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        discountFeild = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        paymentFeild = new javax.swing.JFormattedTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        balanceFeild = new javax.swing.JFormattedTextField();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        totalFeild = new javax.swing.JFormattedTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel21Layout.createSequentialGroup()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 158, Short.MAX_VALUE)
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField1.setText("Search product by name");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Product Name");
        jRadioButton1.setActionCommand("1");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Bar Code");
        jRadioButton2.setActionCommand("2");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Product Id");
        jRadioButton1.setActionCommand("3");
        jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton3ItemStateChanged(evt);
            }
        });

        jLabel4.setText("Employee:");

        jLabel17.setText("Employee Email");

        jLabel23.setFont(new java.awt.Font("Trebuchet MS", 1, 24)); // NOI18N
        jLabel23.setText("CM COMPUTER SHOP");

        jTextField5.setEditable(false);
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel30.setText("INVOICE ID :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(73, 73, 73)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel30)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton2)
                            .addComponent(jRadioButton3)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField5))))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(350, 357));
        jPanel2.setLayout(new java.awt.GridLayout(3, 0));

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel7.setLayout(new java.awt.GridLayout(2, 0));

        jPanel14.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel14.setLayout(new java.awt.GridLayout(2, 2, 0, 5));

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Stock Id:  ");
        jPanel14.add(jLabel11);

        jTextField3.setEditable(false);
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel14.add(jTextField3);

        jLabel5.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Product Name:  ");
        jPanel14.add(jLabel5);

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("NULL");
        jPanel14.add(jLabel6);

        jPanel7.add(jPanel14);

        jPanel15.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel15.setLayout(new java.awt.GridLayout(2, 0, 0, 5));

        jLabel7.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Model:  ");
        jPanel15.add(jLabel7);

        jLabel8.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("NULL");
        jPanel15.add(jLabel8);

        jLabel9.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Brand:  ");
        jPanel15.add(jLabel9);

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("NULL");
        jPanel15.add(jLabel10);

        jPanel7.add(jPanel15);

        jPanel2.add(jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));
        jPanel8.setLayout(new java.awt.GridLayout(2, 0));

        jPanel12.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel12.setLayout(new java.awt.GridLayout(2, 2, 5, 5));

        jLabel13.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Warranty:  ");
        jPanel12.add(jLabel13);

        jLabel14.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("NULL");
        jPanel12.add(jLabel14);

        jLabel12.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Discount (Rs.):  ");
        jPanel12.add(jLabel12);

        jFormattedTextField1.setEditable(false);
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel12.add(jFormattedTextField1);

        jPanel8.add(jPanel12);

        jPanel13.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel13.setLayout(new java.awt.GridLayout(2, 2, 0, 5));

        jLabel16.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Selling Price (Rs.):  ");
        jPanel13.add(jLabel16);

        jFormattedTextField2.setEditable(false);
        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel13.add(jFormattedTextField2);

        jLabel20.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Quantity:  ");
        jPanel13.add(jLabel20);

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });
        jPanel13.add(jTextField4);

        jPanel8.add(jPanel13);

        jPanel2.add(jPanel8);

        jPanel9.setLayout(new java.awt.GridLayout(2, 0));

        jPanel18.setLayout(new java.awt.GridLayout(2, 0));

        jButton4.setText("Add to Invoice");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel18.add(jPanel17);

        jPanel20.setLayout(new java.awt.GridLayout(1, 3, 5, 0));

        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel20.add(jButton1);

        jButton2.setText("Search");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel20.add(jButton2);

        jButton3.setText("New Customer");
        jPanel20.add(jButton3);

        jPanel18.add(jPanel20);

        jPanel9.add(jPanel18);

        jPanel19.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel19.setLayout(new java.awt.GridLayout(2, 0, 0, 10));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Customer Name");
        jPanel19.add(jLabel1);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Customer Email");
        jPanel19.add(jLabel24);

        jPanel9.add(jPanel19);

        jPanel2.add(jPanel9);

        getContentPane().add(jPanel2, java.awt.BorderLayout.LINE_END);

        jPanel3.setPreferredSize(new java.awt.Dimension(970, 200));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel5.setPreferredSize(new java.awt.Dimension(350, 250));
        jPanel5.setLayout(new java.awt.GridLayout(2, 0));

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
        jPanel10.setLayout(new java.awt.GridLayout(2, 0, 0, 20));

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });
        jPanel10.add(jTextField2);

        paymentMethodCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        paymentMethodCombobox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                paymentMethodComboboxItemStateChanged(evt);
            }
        });
        jPanel10.add(paymentMethodCombobox);

        jPanel5.add(jPanel10);

        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jPanel11.setLayout(new java.awt.GridLayout(2, 0));

        jPanel22.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel22.setLayout(new java.awt.GridLayout(1, 0));

        jButton12.setFont(new java.awt.Font("Open Sans Semibold", 0, 13)); // NOI18N
        jButton12.setText("   Payment");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel22.add(jButton12);

        jPanel11.add(jPanel22);

        jPanel23.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel23.setLayout(new java.awt.GridLayout(1, 3, 10, 0));

        jButton5.setText("Refund");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel23.add(jButton5);

        jButton7.setText("In");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel23.add(jButton7);

        jButton6.setText("Logout");
        jPanel23.add(jButton6);

        jPanel11.add(jPanel23);

        jPanel5.add(jPanel11);

        jPanel3.add(jPanel5, java.awt.BorderLayout.LINE_END);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel16.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel16.setPreferredSize(new java.awt.Dimension(400, 200));
        jPanel16.setLayout(new java.awt.GridLayout(5, 2, 0, 10));
        jPanel16.add(jLabel2);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel16.add(jComboBox1);

        jLabel19.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Discount (Rs.) :  ");
        jPanel16.add(jLabel19);

        discountFeild.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        discountFeild.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        discountFeild.setText("0.00");
        discountFeild.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        discountFeild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountFeildActionPerformed(evt);
            }
        });
        discountFeild.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                discountFeildKeyReleased(evt);
            }
        });
        jPanel16.add(discountFeild);

        jLabel21.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Payment (Rs.) :  ");
        jPanel16.add(jLabel21);

        paymentFeild.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        paymentFeild.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        paymentFeild.setText("0.00");
        paymentFeild.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        paymentFeild.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                paymentFeildKeyReleased(evt);
            }
        });
        jPanel16.add(paymentFeild);

        jLabel27.setText("---------------------------------------");
        jPanel16.add(jLabel27);

        jLabel3.setText("---------------------------------------");
        jPanel16.add(jLabel3);

        jLabel22.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Balance (Rs.) :   ");
        jPanel16.add(jLabel22);

        balanceFeild.setEditable(false);
        balanceFeild.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        balanceFeild.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balanceFeild.setText("0.00");
        balanceFeild.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jPanel16.add(balanceFeild);

        jPanel6.add(jPanel16, java.awt.BorderLayout.LINE_END);

        jPanel24.setLayout(new java.awt.BorderLayout());

        jPanel25.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 20, 1, 10));
        jPanel25.setPreferredSize(new java.awt.Dimension(220, 30));
        jPanel25.setLayout(new java.awt.GridLayout(1, 3, 5, 0));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Total Quantity");
        jPanel25.add(jLabel29);

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Total Discount (Rs.)");
        jPanel25.add(jLabel25);

        jLabel18.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Total (Rs.) :  ");
        jPanel25.add(jLabel18);

        jPanel24.add(jPanel25, java.awt.BorderLayout.PAGE_START);

        jPanel26.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel26.setLayout(new java.awt.GridLayout(1, 3, 5, 0));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("0.00");
        jLabel28.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel26.add(jLabel28);

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("0.00");
        jLabel26.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel26.add(jLabel26);

        totalFeild.setEditable(false);
        totalFeild.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        totalFeild.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        totalFeild.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        totalFeild.setText("0.00");
        totalFeild.setFont(new java.awt.Font("Trebuchet MS", 1, 18)); // NOI18N
        jPanel26.add(totalFeild);

        jPanel24.add(jPanel26, java.awt.BorderLayout.CENTER);

        jPanel6.add(jPanel24, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel6, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel4.setLayout(new javax.swing.OverlayLayout(jPanel4));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stock Id", "Product Name", "Model", "Brand", "Waranty", "Price", "Discount", "Quantity", "Total Discount", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel4.add(jScrollPane1);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased

        String search = jTextField1.getText().trim();

        if (search_state == 1) {
            if (!search.equals("")) {

                try {

                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM  `product` WHERE `name` LIKE '%" + search + "%'");

                    DefaultListModel ls = new DefaultListModel();
                    while (resultSet.next()) {
                        ls.addElement(resultSet.getString("name"));
                    }

                    jList1.setFixedCellHeight(30);
                    jList1.setModel(ls);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                jPopupMenu1.show(jTextField1, 0, jTextField1.getHeight());

            }

        } else if (search_state == 3) {
            if (!search.equals("")) {

                try {

                    ResultSet resultSet = MySQL.executeSearch("SELECT * FROM  `product`  WHERE `product`.`id` LIKE '%" + search + "%'");

                    DefaultListModel ls = new DefaultListModel();
                    while (resultSet.next()) {
                        ls.addElement(resultSet.getString("name"));
                    }

                    jList1.setFixedCellHeight(30);
                    jList1.setModel(ls);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                jPopupMenu1.show(jTextField1, 0, jTextField1.getHeight());

            }

        }

        //need to add state 3 barcode reader

    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        if (jTextField1.getText().equals("Search product by name")) {
            jTextField1.setText("");
            jTextField1.setForeground(new Color(153, 153, 153));
        } else if (jTextField1.getText().equals("Search product by barcode")) {
            jTextField1.setText("");
            jTextField1.setForeground(new Color(153, 153, 153));
        } else if (jTextField1.getText().equals("Search product by product id")) {
            jTextField1.setText("");
            jTextField1.setForeground(new Color(153, 153, 153));
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        load_status();
    }//GEN-LAST:event_jTextField1FocusLost

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        jTextField1.setText("Search product by name");
        jTextField1.setEnabled(true);
        search_state = 1;
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        jTextField1.setText("Search product by barcode Availible soon");
        jTextField1.setEnabled(false);
//        search_state = 2;
    }//GEN-LAST:event_jRadioButton2ItemStateChanged

    private void jRadioButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton3ItemStateChanged
        jTextField1.setText("Search product by product id");
        jTextField1.setEnabled(true);
        search_state = 3;

    }//GEN-LAST:event_jRadioButton3ItemStateChanged

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked

        String selected_item = jList1.getSelectedValue();

        if (evt.getClickCount() == 2) {
            jTextField1.setText("");
            try {

                ResultSet rs = MySQL.executeSearch("SELECT * FROM `product` WHERE `name` = '" + selected_item + "'");

                rs.next();

                product_id = rs.getString("id");

                Stock stock = new Stock(this, true, product_id);
                stock.setVisible(true);
                stock.setInvoice(this);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }//GEN-LAST:event_jList1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        String stockId = jTextField3.getText();
        String productName = jLabel6.getText();
        String model = jLabel8.getText();
        String brand = jLabel10.getText();
        String warranty = jLabel14.getText();
        String sellingPrice = jFormattedTextField2.getText();
        String qty = jTextField4.getText();
        String Avqty = jLabel20.getText();
        String discprice = jFormattedTextField1.getText();

        if (stockId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please Select a Prooduct", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            try {

                if (qty.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please Enter the Quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (Integer.parseInt(qty) < 0) {
                    JOptionPane.showMessageDialog(this, "Please Enter the valid Quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                } else if (Integer.parseInt(qty) > Integer.parseInt(Avqty)) {
                    JOptionPane.showMessageDialog(this, "Please Check the Availible Quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                } else {
                    InvoiceItem invoiceItem = new InvoiceItem();

                    invoiceItem.setStockId(stockId);
                    invoiceItem.setProductName(productName);
                    invoiceItem.setModel(model);
                    invoiceItem.setBrand(brand);
                    invoiceItem.setWarranty(warranty);
                    invoiceItem.setSellingPrice(sellingPrice);
                    invoiceItem.setDiscountPrice(discprice);
                    invoiceItem.setQty(qty);

                    if (invoiceitemMap.get(stockId) == null) {

                        invoiceitemMap.put(stockId, invoiceItem);

                        loadInvoiceItem();
                    } else {

                        InvoiceItem foundItems = invoiceitemMap.get(stockId);

                        int option = JOptionPane.showConfirmDialog(this, "Do you you Want to Update the Quantity of The Product :" + productName, "Message",
                                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                        if (option == JOptionPane.YES_OPTION) {

                            if (Integer.parseInt(Avqty) - 1 < Integer.parseInt(foundItems.getQty())) {
                                JOptionPane.showMessageDialog(this, "please check the availible quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                            } else {

                                int totQty = Integer.parseInt(foundItems.getQty()) + Integer.parseInt(qty);

                                if (totQty > Integer.parseInt(Avqty)) {
                                    JOptionPane.showMessageDialog(this, "out of quantity", "Warning", JOptionPane.WARNING_MESSAGE);

                                } else {
                                    foundItems.setQty(String.valueOf(totQty));
                                    loadInvoiceItem();
                                }

                            }

                        }

                    }

                    int numberOfQty = Integer.parseInt(jLabel28.getText());
                    double totDiscount = Double.parseDouble(jLabel26.getText());

                    if (invoiceitemMap.size() == 1) {

                        if (numberOfQty == 1 && totDiscount == 0) {

                            jComboBox1.setEnabled(false);
                            jComboBox1.setSelectedIndex(2);
                            discountFeild.setEnabled(true);

                        } else if (numberOfQty == 1 && totDiscount > 0) {

                            jComboBox1.setEnabled(false);
                            jComboBox1.setSelectedIndex(1);
                            discountFeild.setEnabled(false);

                        } else if (numberOfQty > 1 && totDiscount == 0) {

                            jComboBox1.setEnabled(false);
                            jComboBox1.setSelectedIndex(3);
                            discountFeild.setEnabled(true);
                        }

                    } else {

                        if (numberOfQty > 1 && totDiscount > 0) {

                            jComboBox1.setEnabled(false);
                            jComboBox1.setSelectedIndex(1);
                            discountFeild.setEnabled(false);

                        } else if (numberOfQty > 1 && totDiscount == 0) {

                            jComboBox1.setEnabled(false);
                            jComboBox1.setSelectedIndex(4);
                            discountFeild.setEnabled(true);

                        }

                    }

                    paymentFeild.setEnabled(true);
                    paymentMethodCombobox.setEnabled(true);
//                    resetitems();
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Input", "Warning", JOptionPane.WARNING_MESSAGE);

            }

        }


    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        if (stockFrame == null || !stockFrame.isDisplayable()) {
            stockFrame = new stockManagement();
            stockFrame.setVisible(true);
            stockFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        } else {
            stockFrame.toFront();
            stockFrame.requestFocus();
        }


    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped

//        loadCustomer();

    }//GEN-LAST:event_jTextField2KeyTyped

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        loadCustomer();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        resetitems();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void paymentFeildKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentFeildKeyReleased

        String paymentfield = paymentFeild.getText();

        if (paymentfield.equals("0.00")) {
            jButton12.setEnabled(false);
        } else if (!paymentfield.matches("^(0|[1-9]\\d*)?(\\.\\d+)?(?<=\\d)$")) {
            jButton12.setEnabled(false);

        } else {
            calculate();
        }


    }//GEN-LAST:event_paymentFeildKeyReleased

    private void discountFeildKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_discountFeildKeyReleased

        String discountType = String.valueOf(jComboBox1.getSelectedItem());

        if (discountType.equals("Fixed")) {
            calculate();
        } else if (discountType.equals("Bulck")) {
            calculate();
        } else if (discountType.equals("Percentage")) {
            calculate();
        }

    }//GEN-LAST:event_discountFeildKeyReleased

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {

            String customerName = jLabel1.getText();
            String customerMobile = jTextField2.getText();

            String invoiceID = jTextField5.getText();
            String employeeEmail = jLabel17.getText();
            
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            String totalAmount = totalFeild.getText();
            String paidAmount = paymentFeild.getText();
            String paymentMethodID = paymentMap.get(String.valueOf(paymentMethodCombobox.getSelectedItem()));
            String discountTypeid = discuontMap.get(String.valueOf(jComboBox1.getSelectedItem()));
            String fianlDiscount = String.valueOf(discountFeild.getText());
            String totalDisCount = jLabel26.getText();

            String remainAmount = String.valueOf(Double.parseDouble(totalAmount) - Double.parseDouble(paidAmount));

            String paidStatus = "";

            if (customerMobile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pelase Select A customer", "Warning", JOptionPane.WARNING_MESSAGE);

            } else if (!customerMobile.matches("^(?:0|94|\\+94|0094)?(?:(11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)(0|2|3|4|5|7|9)|7(0|1|2|4|5|6|7|8)\\d)\\d{6}$")) {
                JOptionPane.showMessageDialog(this, "invalid Customer mobile", "Warning", JOptionPane.WARNING_MESSAGE);

            } else if (customerName.equals("Customer Name")) {
                JOptionPane.showMessageDialog(this, "Pelase Select A customer", "Warning", JOptionPane.WARNING_MESSAGE);

            } else {

                if (Double.parseDouble(totalAmount) > Double.parseDouble(paidAmount)) {
                    paidStatus = "Advance";
                } else {
                    paidStatus = "Completed";
                    paidAmount = totalAmount;
                    remainAmount = "0";
                }

                MySQL.executeIUD("INSERT INTO `invoice` (`id`,`date`,`total_amount`,`paid_amount`,`remain_amount`,`status`,`employee_email`,`customer_mobile`,`payment_method_id`)"
                        + " VALUES ('" + invoiceID + "','" + date + "','" + totalAmount + "','" + paidAmount + "','" + remainAmount + "','"+paidStatus+"','" + employeeEmail + "','" + customerMobile + "','" + paymentMethodID + "')");

                if (jComboBox1.getSelectedIndex() == 1) {

                    MySQL.executeIUD("INSERT INTO `discount` (`discount_price`,`discount_type_id`,`invoice_id`) "
                            + " VALUES ('" + totalDisCount + "','" + discountTypeid + "','" + invoiceID + "')");
                } else {
                    MySQL.executeIUD("INSERT INTO `discount` (`discount_price`,`discount_type_id`,`invoice_id`) "
                            + " VALUES ('" + fianlDiscount + "','" + discountTypeid + "','" + invoiceID + "')");
                }

                for (InvoiceItem invoiceItem : invoiceitemMap.values()) {

                    MySQL.executeIUD("INSERT INTO `invoice_item` (`qty`,`stock_id`,`invoice_id`)"
                            + " VALUES ('" + invoiceItem.getQty() + "','" + invoiceItem.getStockId() + "','" + invoiceID + "')");

                    MySQL.executeIUD("UPDATE `stock` SET `qty` = `qty`-'" + invoiceItem.getQty() + "' WHERE `id` = '" + invoiceItem.getStockId() + "'");

                }

//                InputStream stream = this.getClass().getResourceAsStream("/report/CmInvoice.jasper");
//
//                HashMap<String, Object> parameters = new HashMap<>();
//
//                parameters.put("Parameter1", date);
//                parameters.put("Parameter2", totalFeild.getText());
//                parameters.put("Parameter3", paymentFeild.getText());
//                parameters.put("Parameter4", balanceFeild.getText());
//
//                JRTableModelDataSource dataSource = new JRTableModelDataSource(jTable1.getModel());
//
//                JasperPrint jasperPrint = JasperFillManager.fillReport(stream, parameters, dataSource);
//                JasperViewer.viewReport(jasperPrint, false);

                JOptionPane.showMessageDialog(this, "Succes add database added", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetitems();
                genarateInvoiceId();

//                String msg = "Dear Customer, "+customerName+" \n\n"
//                        + "Thank you for choosing CM SHOP! We appreciate your recent purchase of products.\n\n"
//                        + "Invoice Details:\n\n"
//                        + "Invoice Id: "+invoiceID+"\n"
////                        + "Product Name: "+productName+"\n"
//                        + "Paid Amount: Rs."+paidAmount+"\n"
//                        + "Discount Amount: Rs."+fianlDiscount+"\n"
//                        + "Payment Method: Cash \n\n"
//                        + "We hope you enjoy your new purchase! Thank you for being a valued customer.\n\n"
//                        + "Best regards,\n"
//                        + ""+employeeEmail+"\n"
//                        + "CM Shop\n"
//                        + "0765533255\n"
//                        + "cmShop@gmail.com\n"
//                        + "www.computerShop.lk";
//                
//                sendEmail(customerEmail, "Thanks for Choosing Us", msg);
//                
//                JOptionPane.showMessageDialog(this, "Email Send SuccessFully", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed

        jButton4.grabFocus();

    }//GEN-LAST:event_jTextField4ActionPerformed

    private void discountFeildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountFeildActionPerformed

        paymentFeild.grabFocus();
    }//GEN-LAST:event_discountFeildActionPerformed

    private void paymentMethodComboboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_paymentMethodComboboxItemStateChanged
        calculate();
    }//GEN-LAST:event_paymentMethodComboboxItemStateChanged

    private RepairAdd rdFrame;

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        if (rdFrame == null || !rdFrame.isDisplayable()) {
            rdFrame = new RepairAdd();
            rdFrame.setVisible(true);
            rdFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        } else {
            rdFrame.toFront();
            rdFrame.requestFocus();
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        InvoiceHistory history = new InvoiceHistory(this, true);
        history.setVisible(true);
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLaf.registerCustomDefaultsSource("traitgen.theme");
        FlatMacLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Invoice().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField balanceFeild;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JFormattedTextField discountFeild;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JFormattedTextField paymentFeild;
    private javax.swing.JComboBox<String> paymentMethodCombobox;
    private javax.swing.JFormattedTextField totalFeild;
    // End of variables declaration//GEN-END:variables

    private void resetitems() {

        jTextField3.setText("");
        jLabel6.setText("NULL");
        jLabel8.setText("NULL");
        jLabel10.setText("NULL");
        jFormattedTextField1.setText("");
        jLabel14.setText("NULL");
        jFormattedTextField2.setText("");

        jTextField4.setText("");

        jLabel20.setText("Quantity:  ");
        jPopupMenu1.add(jPanel21);
        jPopupMenu1.setFocusable(false);

        jLabel1.setText("Customer Name");
        jLabel24.setText("Customer Email");
        jTextField2.setText("");

        jLabel28.setText("0.00");
        jLabel26.setText("0.00");
        totalFeild.setText("0.00");
        discountFeild.setText("0.00");
        paymentFeild.setText("0.00");
        balanceFeild.setText("0.00");

        invoiceitemMap.clear();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        jComboBox1.setSelectedIndex(0);
        paymentMethodCombobox.setSelectedIndex(0);
        paymentMethodCombobox.setEnabled(false);

        jTextField1.grabFocus();
        jTextField1.setText("");

    }

}
