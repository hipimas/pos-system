package controller.cashierCont;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.lowagie.text.ListItem;
import entity.Customer;
import configuration.Payment;
import entity.Product;
import interfaces.CustomerInterface;
import interfaces.ProductInterface;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import model.CustomerModel;
import model.ProductModel;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class CashierAdditionalOptionController implements Initializable {
    public AnchorPane customerPane;
    public AnchorPane paymentPane;
    public JFXButton btnCash;
    public JFXButton btnCc;
    public JFXButton btnCredit;
    public BorderPane bdPane;
    public AnchorPane priceLookPane;
    public JFXTextField txtBarcode;
    public Label lblPrice;
    public Label lblName;
    public Label lblDesc;
    public Label lblBrand;
    public Label lblInfo;
    public JFXTextField txtSearch;
    public JFXListView<Customer> listView;
    public JFXButton btnClear;

    private CustomerModel customerModel;
    private ProductModel productModel;
    private Payment payment;

    private ObservableList<Customer> CUSTOMERLIST = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerModel = new CustomerModel();
        productModel = new ProductModel();
        togglePane(null);
        payment = new Payment();

        txtBarcode.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                Product p = productModel.getProductByBarcode(newValue);
                if(p != null){
                    System.out.println(p);
                    if(p.getPrice() != null){
                        lblPrice.setText("RM " + p.getPrice().getPriceSell().toString());
                    }
                    lblName.setText(p.getName());
                    lblInfo.setText("Found a result");
                } else {
                    lblInfo.setText("No matching any result");
                    lblName.setText("");
                    lblPrice.setText("");
                }
            }
        });
        txtSearch.textProperty().addListener(changeListenerSearch);
    }


    public void customerActon(ActionEvent actionEvent) {
        togglePane(customerPane);

        if (!CUSTOMERLIST.isEmpty()) {
            CUSTOMERLIST.clear();
        }

        Task<ObservableList<Customer>> loadCustomer = new Task<ObservableList<Customer>>() {
            @Override
            protected ObservableList<Customer> call() throws Exception {
                return customerModel.getCustomers();
            }
        };

        loadCustomer.setOnSucceeded(e -> {
            CUSTOMERLIST.addAll(loadCustomer.getValue());
            listView.setItems(CUSTOMERLIST);
            listView.setCellFactory(param -> new ListItemCell());

        });

        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.submit(loadCustomer);

        exec.shutdown();
    }

    private class ListItemCell extends ListCell<Customer> {
        @Override
        protected void updateItem(Customer item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null || empty){
                setText(null);
                setGraphic(null);
            }else {
                JFXButton selectBtn = new JFXButton("Select");
                selectBtn.setId("button-accent");
                selectBtn.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("Set this as customer ?");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        payment.setCustomer(item);
                    }
                });
                setText(item.getName());
                setGraphic(selectBtn);
            }
        }
    }

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            btnClear.setVisible(true);

            FilteredList<Customer> filteredData = new FilteredList<>(CUSTOMERLIST,p -> true);
            filteredData.setPredicate(s ->{
                // If filter text is empty, display all persons.
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }

                // Compare first name and last name of every client with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if(s.getName().toLowerCase().contains(lowerCaseFilter)){
                    return true; //filter matches first name
                }
                return false; //Does not match
            });

            //Wrap the FilteredList in a SortedList.
            SortedList<Customer> sortedData = new SortedList<>(filteredData);

            //put the sorted list into the listview
            listView.setItems(sortedData);

        }
    };

    public void paymentOption(ActionEvent actionEvent) {
        togglePane(paymentPane);

        btnCash.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Set cash as payment ?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                payment.setType(Payment.getPaymentType().get(1));
            }
        });


        btnCc.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Set credit card as payment ?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                payment.setType(Payment.getPaymentType().get(2));
            }
        });

        btnCredit.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Set credit as payment ?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                payment.setType(Payment.getPaymentType().get(3));
            }
        });

        new FadeIn(paymentPane).play();
    }

    public void saveAction(ActionEvent actionEvent) {
        if(payment != null){
            if(payment.getType() != null){
                System.out.println("type have");
                CashierController.getPaymentCashier().setType(payment.getType());
            }
            if(payment.getCustomer() != null){
                System.out.println("cust have");
                CashierController.getPaymentCashier().setCustomer(payment.getCustomer());
            }
        }

        bdPane.setVisible(false);
        CashierController.isOptional.set(false);
    }

    public void cancelAction(ActionEvent actionEvent) {

//        System.out.println(Payment.getCustomer().getName());
//        System.out.println(Payment.getType());

        bdPane.setVisible(false);
        CashierController.isOptional.set(false);
    }

    public void priceLookUpAction(ActionEvent actionEvent) {
        togglePane(priceLookPane);
    }

    private void togglePane(AnchorPane anchorPane){
        customerPane.setVisible(false);
        paymentPane.setVisible(false);
        priceLookPane.setVisible(false);

        if(anchorPane != null){
            anchorPane.setVisible(true);
        }
    }

    public void btnClearAction(ActionEvent actionEvent){

    }


}
