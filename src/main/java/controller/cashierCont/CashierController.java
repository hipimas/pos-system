package controller.cashierCont;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import configuration.*;
import controller.menuCont.CashierNavigationController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.CartModel;
import model.CounterRegisterModel;
import model.CustomerModel;
import model.ProductModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hibernate.HibernateException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CashierController implements Initializable {

    public JFXButton btnInvoice;
    public Label lblCashier;
    public Label lblCounter;
    public Label lblPay;
    public Label lblChange;
    public JFXTextField txtAmount;
    public Label lblClock;
    public Label lblDate;
    public JFXButton btnReset;
    public JFXButton btnDelete;
    public JFXButton btnQuantity;
    public JFXButton btnCancel;
    public JFXTreeTableView<Item> treeTableView;
    public TreeTableColumn<Item, String> colQty;
    public TreeTableColumn<Item, String> colName;
    public TreeTableColumn<Item, String> colPrice;
    public TreeTableColumn<Item, String> colDiscount;
    public TreeTableColumn colBarcode;
    public Circle circleStatus;
    public Label lblStatus;

    public JFXButton btnAdd;
    public Label lblCustomer;
    public Label lblPaymentType;
    public AnchorPane currentPane;
    public Label lblAmount;
    public Label lblInfo;

    public JFXButton btnNum7;
    public JFXButton btnNum8;
    public JFXButton btnNum9;
    public JFXButton btnNum4;
    public JFXButton btnNum5;
    public JFXButton btnNum6;
    public JFXButton btnNum1;
    public JFXButton btnNum2;
    public JFXButton btnNum3;
    public JFXButton btnNum0;
    public JFXButton btnNum00;
    public JFXButton btnNum100;
    public JFXButton btnNum50;
    public JFXButton btnNum10;
    public JFXButton btnNumDot;
    public JFXButton btnBackspace;
    public JFXButton btnEnter;
    public Label lblSubtotal;
    public Label lblTax;
    public Label lblTotal;
    public GridPane gridNumber;
    public HBox hBoxSub;
    public JFXTextField txtSearch;
    public JFXButton btnClear;


    private Cart cart;
    private ProductModel productModel;
    private CartModel cartModel;
    private CustomerModel customerModel;
    private CounterRegister counterRegister;
    private CounterRegisterModel counterRegisterModel;
    private ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();

    private boolean getTotal = false;
    private boolean getCash = false;
    private boolean getChange = false;

    private boolean getQuantity = false;
    private boolean setQuantity = false;
    private Item changeItem;

    private BigDecimal cashPay = new BigDecimal(0.00);
    private BigDecimal balanceChange = new BigDecimal(0.00);
    private boolean emptyCart = true;
    private boolean editable = false;
    private boolean isSelected = false;

    private int cashierId;
    private int counterTableNo;
    private BorderPane borderPane;


    public static SimpleStringProperty statusProperty = new SimpleStringProperty("not-active");

    static BooleanProperty isOptional = new SimpleBooleanProperty();
    static Payment paymentCashier;

    public static Payment getPaymentCashier() {
        return paymentCashier;
    }

    public static void setPaymentCashier(Payment paymentCashier) {
        CashierController.paymentCashier = paymentCashier;
    }

    private Timeline timeline;

    public void initData(BorderPane borderPane) {
        this.borderPane = borderPane;

        getExitButton();
        addStartCounter();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerModel = new CustomerModel();
        counterRegisterModel = new CounterRegisterModel();
        btnClear.setVisible(false);

        cartModel = new CartModel();
        counterTableNo = 2;

        btnCancel.setVisible(false);

        lblCashier.setText("Ramli");
        lblCounter.setText(String.valueOf(counterTableNo));

        checkStatus();

        Payment payment = new Payment();
        payment.setType(Payment.getPaymentType().get(1));
        payment.setCustomer(customerModel.getCustomer(1));
        setPaymentCashier(payment);

        isOptional.addListener(changeListenerOptional);
        statusProperty.addListener(changeListenerStatus);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a");

        timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            final Calendar cal = Calendar.getInstance();
            lblClock.setText(hourFormat.format(cal.getTime()));
            lblDate.setText(sdf.format(cal.getTime()));
        }),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        productModel = new ProductModel();
        cart = new Cart();
        txtAmount.setVisible(false);

//        lblCustomer.setText(customerName);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) lblCashier.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    timeline.stop();
                    isOptional.removeListener(changeListenerOptional);
                    statusProperty.removeListener(changeListenerStatus);
                    System.out.println("system exit");

                    StageView.removeMap("pos");
                });

                txtSearch.requestFocus();
                stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                    if(e.getCode() == KeyCode.ENTER) {
                        System.out.println("You pressed enter");
                        btnEnter.fire();
                        e.consume();
                    }
                });
            }
        });

        loadData();

        //filter search
        txtSearch.textProperty().addListener(changeListenerSearch);

    }

    private void loadData() {
        if (!PRODUCTLIST.isEmpty()) {
            PRODUCTLIST.clear();
        }

        Task<ObservableList<Product>> loadProduct = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                return productModel.getProducts();
            }
        };

        loadProduct.setOnSucceeded(e -> {
            PRODUCTLIST.addAll(loadProduct.getValue());
        });

        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.submit(loadProduct);

        exec.shutdown();
    }

    private ChangeListener<Boolean> changeListenerOptional = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(!newValue){
               System.out.println("show grid");
               gridNumber.setVisible(true);
               hBoxSub.getChildren().set(1, gridNumber);

               if(paymentCashier != null){
                   if(paymentCashier.getCustomer() != null){
                       System.out.println("after close option cust " + paymentCashier.getCustomer().getName());
//                       customer = paymentCashier.getCustomer();
                       lblCustomer.setText(paymentCashier.getCustomer().getName());
                   }
                   if(!paymentCashier.getType().isEmpty()){
                       System.out.println("after close option type " + paymentCashier.getType());
//                       typePay = paymentCashier.getType();
                       lblPaymentType.setText(paymentCashier.getType());
                   }
               }
           }
        }
    };

    private ChangeListener<String> changeListenerStatus = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue.equals("active")){
                lblStatus.setText("active");
                circleStatus.setFill(Color.GREEN);
            }
            if(newValue.equals("lock")){
                lblStatus.setText("lock");
                circleStatus.setFill(Color.DEEPPINK);
                currentPane.setDisable(true);
            }
            if(newValue.equals("unlock")){
                currentPane.setDisable(false);
                //get prev status
                if(Pref.getPrevStatus().equals("active")){
                    lblStatus.setText("active");
                    circleStatus.setFill(Color.GREEN);
                    Pref.setStatus("active");
                }
                if(Pref.getPrevStatus().equals("not-active")){
                    lblStatus.setText("not-active");
                    circleStatus.setFill(Color.GREY);
                    Pref.setStatus("not-active");
                }
            }
            if(newValue.equals("not-active")){
                lblStatus.setText("not-active");
                circleStatus.setFill(Color.GREY);
            }
        }
    };

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            btnClear.setVisible(true);
            if (!newValue.isEmpty()) {
                System.out.println(newValue);
                String lowerCase = newValue.toLowerCase();

                for(Product product : PRODUCTLIST){
                    if(product.getBarcode().toLowerCase().equals(lowerCase)){
                        Item item = new Item(product, 1);
                        cart.addCart(item);
                        txtSearch.clear();
                        refreshList();
                        setCellTable();
                        calculateView();
                        cancelAction();

                        editable = true;
                        getQuantity = true;
                        if(getTotal){
                            getTotal = false;
                            txtAmount.clear();
                        }

                        treeTableView.refresh();
                    }
                }


            }
        }
    };

    private void checkStatus() {
//        System.out.println("status " + Pref.getStatus());
        if(Pref.getStatus() != null){
            if(Pref.getStatus().equals("active")){
                lblStatus.setText("active");
                circleStatus.setFill(Color.GREEN);
            }
            if(Pref.getStatus().equals("not-active")){
                lblStatus.setText("not-active");
                circleStatus.setFill(Color.GREY);
            }
            if(Pref.getStatus().equals("lock")){
                lblStatus.setText("lock");
                circleStatus.setFill(Color.DEEPPINK);
                currentPane.setDisable(true);
            }
        }
    }

    //show info at counter menu for current status
    private void showInfo(Boolean getTotal, String process, String amount){
        if(getTotal){
            lblInfo.setText(process);
            lblAmount.setText(cart.getTotal().toString());
            txtAmount.setVisible(false);
            lblAmount.setVisible(true);
        } else {
            lblInfo.setText(process);
            lblAmount.setText(amount);
        }
        new FadeIn(lblInfo).play();
        new FadeIn(lblAmount).play();
    }

    private void calculateView() {
        lblSubtotal.setText(cart.getSubTotal().toString());
        lblTax.setText(cart.getChargesTax().toString());
        lblTotal.setText(cart.getTotal().toString());

        getTotal = true;
        showInfo(true, "TOTAL", cart.getTotal().toString());
    }

    private void refreshList() {
        ObservableList<Item> items = FXCollections.observableArrayList(Cart.getItemList());
        final TreeItem<Item> root = new RecursiveTreeItem<>(items, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
        System.out.println("refresh the list cart " + Cart.getItemList());
    }

    private void setCellTable(){
        ObservableList<Item> items = FXCollections.observableArrayList(Cart.getItemList());
        final TreeItem<Item> root = new RecursiveTreeItem<>(items, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);

        colQty.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getValue().getQuantity())));
        colQty.setStyle("-fx-alignment: CENTER;");
        colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue().getName()));
        colPrice.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getValue().getTotal())));
//        colDiscount.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getValue().getDiscountAmount())));

        colDiscount.setCellValueFactory(cellData -> {
            TreeItem<Item> rowItem = cellData.getValue();
            return new SimpleStringProperty((rowItem.getValue().getPromotionDiscount().add(rowItem.getValue().getBulkPriceDiscount()).toString()));
        });

        treeTableView.getColumns().setAll(colQty, colName,colPrice,colDiscount);
//        treeTableView.setFixedCellSize(35.0);

    }

    public void addProductAction(ActionEvent actionEvent) throws ConfigurationException {
        if(!getChange)
        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/cashier/addCart.fxml"));

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/cashier/gridCart.fxml"));
            Parent root1 = fxmlLoader.load();
            if(Setting.getPathTheme() != null){
                String cssColor = this.getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm();
                String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();

                root1.getStylesheets().addAll(cssColor, cssComponent);
            }
            Stage stage = new Stage();
            stage.setTitle("All Product / Add to cashier");
            stage.setScene(new Scene(root1));

//            AddCartController addCartController = fxmlLoader.getController();
//            stage.showAndWait();
//
//            List<Item> addList = addCartController.getCartList();
//            if(addList.size() > 0){
//                for(Item item : addList){
//                    cart.addCart(item);
//                }
//            }

            GridCartController gridCartController = fxmlLoader.getController();
            stage.showAndWait();

            List<Item> addList = gridCartController.getCartList();
            if(addList.size() > 0){
                for(Item item : addList){
                    cart.addCart(item);
                }
            }

            addList.clear();
            refreshList();
            setCellTable();
            calculateView();
            cancelAction();
            editable = true;
            getQuantity = true;
            if(getTotal){
                getTotal = false;
                txtAmount.clear();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void invoiceAction(ActionEvent actionEvent) {
        try {
            String reportName = "/report/printInvoice.jrxml";
            File file = new File(this.getClass().getResource(reportName).getFile());
            JasperDesign jasperDesign = JRXmlLoader.load (file.getAbsolutePath());
            JasperReport jr = JasperCompileManager.compileReport(jasperDesign);

            HashMap<String, Object> para = new HashMap<>();
            para.put("cashier", lblCashier.getText());
            para.put("counter", lblCounter.getText());
            para.put("subtotal", cart.getSubTotal());
            para.put("grandTotal", cart.getTotal());
            para.put("item", cart.getNumberOfItems());
            para.put("cash", cashPay);
            para.put("change", balanceChange);

            JRBeanCollectionDataSource jcs = new JRBeanCollectionDataSource(Cart.getItemList());
            JasperPrint jp = JasperFillManager.fillReport(jr, para,jcs);
            JasperViewer.viewReport(jp, false);

        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    public void enterAction(ActionEvent actionEvent) {
        BigDecimal amountTotal;
        if(Cart.getItemList().size() > 0){
            System.out.println("not empty cart");
            txtAmount.requestFocus();
            emptyCart = false;
            amountTotal = cart.getTotal();
            getTotal = true;
            getQuantity = true;

            if(getChange){
                System.out.println("change was done should clear");
                btnEnter.setText("ENTER");
                btnEnter.setId("button-success");
                lblAmount.setTextFill(Color.BLACK);
                resetAction(actionEvent);
                getChange = false;
                return;
            }

            if(setQuantity){
                int newQuantity;
                System.out.println("change was done at quantity " + txtAmount.getText());
                if(txtAmount.getText().isEmpty() || txtAmount.getText().equals("0")){
                    cancelAction();
                }else{
                    newQuantity = Integer.parseInt(txtAmount.getText());

                    if(newQuantity == 0){
                        cancelAction();
                        return;
                    }
                    cart.updateCartQuantity(changeItem, newQuantity);
                    btnCancel.setVisible(false);
                    setQuantity = false;
                    txtAmount.clear();

                    refreshList();
                    calculateView();
                    numberButtonListener(false);
                }
                return;
            }

        }else {
            emptyCart = true;
            getTotal = false;

            numberButtonListener(false);
            System.out.println("empty cart");
            amountTotal = new BigDecimal(0.00);
        }

        //first enter to insert amount for paying item
        if(getTotal && !emptyCart){
            System.out.println("calculate to total and get cash");

//            getTotal = false;
            showInfo(false, "CASH", "0.00");

            lblAmount.setVisible(false);
            txtAmount.setVisible(true);
            txtAmount.requestFocus();

            ChangeListener<String> txtListener = (observable, oldValue, newValue) -> {
                if(newValue.isEmpty()){
                    return;
                }
                if(setQuantity){
                    if (!newValue.matches("\\d*\\.")) {
//                        System.out.println("enter quantity " + txtAmount.getText());
                        txtAmount.setText(newValue.replaceAll("^(\\d*)|\\D", "$1"));
                    }
                }else{
                    if (!newValue.matches("\\d*\\.")) {
//                        System.out.println("enter amount payable " + txtAmount.getText());
                        txtAmount.setText(newValue.replaceAll("^(\\d*\\.)|\\D", "$1"));
                    }
                }
            };

            txtAmount.textProperty().addListener(txtListener);

            numberButtonListener(true);

            if(txtAmount.getText().isEmpty()){
//                System.out.println("empty cash, please insert amount payable");
                txtAmount.requestFocus();
                return;
            }else{
                getCash = true;
            }
        }

        if(getCash){
            getTotal = false;
            numberButtonListener(false);
            editable = false;
            getQuantity = false;

//            System.out.println("here the action after get cash");
            cashPay = new BigDecimal(txtAmount.getText()).setScale(2,RoundingMode.FLOOR);

            //get the change of balance
            balanceChange = cashPay.subtract(amountTotal);

            //should do a validation for insufficient payment

            //show info for payment and change balance at screen
            txtAmount.setVisible(false);
            lblAmount.setVisible(true);
            lblAmount.setTextFill(Color.GREEN);

            showInfo(false, "Change", balanceChange.toString());

            lblPay.setText(cashPay.toString());
            lblChange.setText(balanceChange.toString());
            cart.getTotalItem();

            CartTransaction cartTransaction = new CartTransaction();
            cartTransaction.setSubTotal(cart.getSubTotal());
            cartTransaction.setTaxAmount(cart.getChargesTax());
            cartTransaction.setTotalAmount(cart.getTotal());
            cartTransaction.setTotalItem(cart.getTotalItem());

            // set payment type
            cartTransaction.setPaymentType(paymentCashier.getType());

            //to save the list of item in separate table
            cartTransaction.setItemList(Cart.getItemList());


            counterRegister = counterRegisterModel.getCounterRegister(Pref.getCounterId());
            cartTransaction.setCounterRegister(counterRegister);
            cartTransaction.getCustomerList().add(paymentCashier.getCustomer());

//            System.out.println("paying item customer " + paymentCashier.getCustomer().getName());
//            System.out.println("paying using " + paymentCashier.getType());

            //print invoice
            //save transaction
            if(Pref.getStatus() != null){
                if(Pref.getStatus().equals("active")){
                    cartTransaction.setUserId(Pref.getUserId());
                    try {
                        cartModel.saveCart(cartTransaction);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Berjaya");
                        alert.setHeaderText(null);
                        alert.setContentText("Jualan ditambah");
                        alert.showAndWait();
                    }catch (HibernateException r){
                        r.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Peringatan !");
                        alert.setContentText("Masalah dalam menambah jualan!");

                        alert.showAndWait();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Peringatan !");
                    alert.setContentText("Counter belum active sila aktifkan!");

                    alert.showAndWait();
                }

            } else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan !");
                alert.setContentText("Counter belum active sila aktifkan!");

                alert.showAndWait();
            }

            if(paymentCashier.getCustomer().getId() != 1){
                paymentCashier.setCustomer(customerModel.getCustomer(1));
                paymentCashier.setType(Payment.getPaymentType().get(1));
                lblCustomer.setText(paymentCashier.getCustomer().getName());
                lblPaymentType.setText(paymentCashier.getType());
            }

            //clear the cart and clear all field
            getChange = true;
            getCash = false;

            //change text of btn enter with color for new counter if clicked
            btnEnter.setText("NEXT");
            btnEnter.setId("button-accent");
            txtSearch.requestFocus();
        }


    }

    public void cancelAction() {
        btnCancel.setVisible(false);
        setQuantity = false;
        txtAmount.clear();
        refreshList();
        calculateView();
        numberButtonListener(false);
    }

    public void resetAction(ActionEvent actionEvent) {
        cart.clearItem();
        refreshList();
        calculateView();

        //set counter for cash and balance to default / no item in cart
        if(getTotal){
            getTotal = false;
            txtAmount.clear();
            txtAmount.setVisible(false);
        }

        editable = false;
        getCash = false;
        getChange = false;
        emptyCart = true;

        getQuantity = false;
        setQuantity = false;

        cashPay = new BigDecimal(0.00).setScale(2,RoundingMode.FLOOR);
        balanceChange = new BigDecimal(0.00).setScale(2,RoundingMode.FLOOR);
        lblPay.setText(cashPay.toString());
        lblChange.setText(balanceChange.toString());

        btnEnter.setId("button-success");
        btnEnter.setText("ENTER");

        //reset the customer and payment method
        paymentCashier.setCustomer(customerModel.getCustomer(1));
        paymentCashier.setType(Payment.getPaymentType().get(1));
        lblCustomer.setText(paymentCashier.getCustomer().getName());
        lblPaymentType.setText(paymentCashier.getType());

        numberButtonListener(false);
    }

    public void deleteAction(ActionEvent actionEvent) {
        if(editable){
            TreeItem<Item> selected = treeTableView.getSelectionModel().getSelectedItem();
            if(selected != null){
                Item selectedItem = treeTableView.getSelectionModel().getSelectedItem().getValue();
                cart.deleteItem(selectedItem);
                treeTableView.getProperties().remove(selectedItem);

                cancelAction();
            }
        }
    }

    public void quantityAction(ActionEvent actionEvent) {
            if(getQuantity){
                Object p =  treeTableView.getSelectionModel().selectedItemProperty().get();

//                System.out.println("button quantity was clicked " + p);
                if(p != null){
                    changeItem = treeTableView.getSelectionModel().getSelectedItem().getValue();
//                    System.out.println("button click with item name "+ changeItem.getName() + " with " + changeItem.getQuantity());
                    //change the info
                    showInfo(false, "Quantity", String.valueOf(changeItem.getQuantity()));

                    setQuantity = true;
                    txtAmount.clear();
                    txtAmount.setText(String.valueOf(changeItem.getQuantity()));

                    btnCancel.setVisible(true);
                    new FadeIn(btnCancel).play();

                    lblAmount.setVisible(false);
                    txtAmount.setVisible(true);
                    txtAmount.requestFocus();
                    new FadeIn(txtAmount).play();
                    //enable keypad for number and backspace
                    numberButtonListener(true);

                    //enable enter
                    //update cart
                    //calculate total

                }
            }


    }

    private void numberButtonListener(Boolean keypadActive){

        //backspace action
        btnBackspace.setOnAction(event -> {
            if(keypadActive) {
                if (!txtAmount.getText().trim().isEmpty()) {
                    txtAmount.requestFocus();
                    txtAmount.setText(txtAmount.getText().substring(0, txtAmount.getText().length() - 1));
                    txtAmount.end();

                } else {
                    txtAmount.requestFocus();
                }
            }
        });


        //0 number
        btnNum0.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("0"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        //special double 00
        btnNum00.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("00"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum1.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("1"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum2.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("2"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum3.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("3"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum4.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("4"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum5.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("5"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum6.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("6"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum7.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("7"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum8.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("8"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum9.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("9"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum10.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("10"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum50.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("50"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

        btnNum100.setOnAction(event -> {
            if(keypadActive){
                txtAmount.setText(txtAmount.getText().concat("100"));
                txtAmount.requestFocus();
                txtAmount.end();
            }
        });

    }

    public void optionAction(ActionEvent actionEvent) throws ConfigurationException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/cashier/cashierOption.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();

            Stage stage = new Stage();
            if(Setting.getPathTheme() != null){
                String cssColor = this.getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm();
                String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();

                root1.getStylesheets().setAll(cssColor, cssComponent);

            }
            stage.setTitle("Cashier Options");
            stage.setScene(new Scene(root1));
            stage.showAndWait();

            CashierOptionController cashierOptionController = fxmlLoader.getController();
            if(cashierOptionController.getPaymentType() != null){
                paymentCashier.setType(cashierOptionController.getPaymentType());
                lblPaymentType.setText(CashierController.getPaymentCashier().getType());
            }
            if(cashierOptionController.getCustomer() != null){
                System.out.println(cashierOptionController.getCustomer().getName());
                paymentCashier.setCustomer(cashierOptionController.getCustomer());
                lblCustomer.setText(cashierOptionController.getCustomer().getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void additionalOptionAction(ActionEvent actionEvent) throws ConfigurationException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/cashier/cashierAdditionalOption.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
            if(Setting.getPathTheme() != null){
                String cssColor = this.getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm();
                String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();

                root1.getStylesheets().addAll(cssColor, cssComponent);
            }

            isOptional.setValue(true);
            hBoxSub.getChildren().set(1, root1);
            HBox.setHgrow(root1, Priority.ALWAYS);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JFXButton getExitButton() {
        JFXButton buttonExit = new JFXButton("Close");
        buttonExit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CLOSE, "24"));
        buttonExit.setId("button-warning");

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        if(toolbar != null){
            HBox hBox = new HBox(5);
            hBox.getChildren().add(buttonExit);
            toolbar.setRightItems(hBox);
        }

        buttonExit.setOnAction(e -> {
            timeline.stop();
            isOptional.removeListener(changeListenerOptional);
            statusProperty.removeListener(changeListenerStatus);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();

            StageView.removeMap("pos");

        });
        return buttonExit;
    }

    private JFXButton addStartCounter() {

        JFXButton btnAdd = new JFXButton("Add");
        btnAdd.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnAdd.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.WALLET, "25"));
        btnAdd.setId("button-primary");

        CashierNavigationController cashierNavigationController = new CashierNavigationController();
        cashierNavigationController.initData(borderPane, btnAdd);

        String fileDrawerFxmlUrl = "/views/menu/drawerCashierMenu.fxml";
        try {
            FXMLLoader fxmlLoaderDrawer = new FXMLLoader(getClass().getResource(fileDrawerFxmlUrl));
            fxmlLoaderDrawer.setController(cashierNavigationController);
            JFXDrawer leftMenu = fxmlLoaderDrawer.load();

            String fileDrawerItemFxmlUrl = "/views/menu/drawerCashierItem.fxml";
            FXMLLoader fxmlLoaderDrawerItem = new FXMLLoader(getClass().getResource(fileDrawerItemFxmlUrl));
            fxmlLoaderDrawerItem.setController(cashierNavigationController);
            HBox hBox = fxmlLoaderDrawerItem.load();
            leftMenu.setSidePane(hBox);

//            borderPane.setLeft(leftMenu);
//            BorderPane.setAlignment(borderPane.getLeft(), Pos.BOTTOM_CENTER);
//            btnAdd.setOnAction(event -> {
//                borderPane.setLeft(leftMenu);
//                BorderPane.setAlignment(borderPane.getLeft(), Pos.BOTTOM_CENTER);
//            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        if(toolbar != null){
            toolbar.getLeftItems().clear();
            toolbar.setLeftItems(btnAdd);
        }

        return btnAdd;
    }

    public void btnClearAction(ActionEvent actionEvent) {
        txtSearch.clear();
        btnClear.setVisible(false);
    }
}
