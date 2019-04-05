package controller.cashierCont;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import entity.Customer;
import interfaces.CustomerInterface;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;

import java.net.URL;
import java.util.ResourceBundle;

public class CashierOptionController implements Initializable, CustomerInterface {
    public AnchorPane customerPane;
    public AnchorPane paymentPane;
    public JFXButton btnCash;
    public JFXButton btnCc;
    public JFXButton btnCredit;
    public JFXTreeTableView<Customer> treeTableView;
    public TreeTableColumn<Customer, String> colId;
    public TreeTableColumn<Customer, String> colName;
    public TreeTableColumn<Customer, String> colAdd;
    public TreeTableColumn<Customer, String> colNo;
    public TreeTableColumn<Customer, String> colType;

    private CustomerModel customerModel;

    private String paymentType;
    private Customer customer;

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerModel = new CustomerModel();
        customerPane.setVisible(false);
        paymentPane.setVisible(false);

        treeTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Customer>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<Customer>> observable, TreeItem<Customer> oldValue, TreeItem<Customer> newValue) {
                System.out.println(newValue);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Set this as customer ?");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK) {
                    //do stuff
                    customer = newValue.getValue();
                    System.out.println("here " + customer + " " + customer.getName());
                }
            }
        });

    }

    public void customerActon(ActionEvent actionEvent) {
        customerPane.setVisible(true);
        paymentPane.setVisible(false);

        loadData();
        setCell();
        refreshList();

        new FadeIn(customerPane).play();
    }

    public void paymentOption(ActionEvent actionEvent) {
        paymentPane.setVisible(true);
        customerPane.setVisible(false);

        btnCash.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Set cash as payment ?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                paymentType = "cash";
            }
        });

        btnCc.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Set credit card as payment ?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                paymentType = "credit card";
            }
        });

        btnCredit.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Set credit as payment ?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                paymentType = "credit";
            }
        });

        new FadeIn(paymentPane).play();
    }

    private void setCell(){
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getId())));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getName()));
        colAdd.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getAddress()));

        treeTableView.getColumns().setAll(colId, colName, colAdd);
    }

    private void refreshList(){
        ObservableList<Customer> customers = FXCollections.observableArrayList(CUSTOMERLIST);
        final TreeItem<Customer> root = new RecursiveTreeItem<>(customers, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    private void loadData(){
        if (!CUSTOMERLIST.isEmpty()) {
            CUSTOMERLIST.clear();
        }
        CUSTOMERLIST.addAll(customerModel.getCustomers());
    }

    public void saveAction(ActionEvent actionEvent) {
        Stage stage = (Stage) customerPane.getScene().getWindow();
        stage.close();
    }

    public void cancelAction(ActionEvent actionEvent) {
        customer = null;
        paymentType = null;

        Stage stage = (Stage) customerPane.getScene().getWindow();
        stage.close();
    }
}
