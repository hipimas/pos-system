package controller.customerCont;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.validation.RequiredFieldValidator;
import entity.Customer;
import interfaces.CustomerInterface;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.CustomerModel;
import validation.CustomerValidator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;


public class CustomerController implements Initializable, CustomerInterface {

    public JFXTreeTableView<Customer> treeTableView;
    public TreeTableColumn<Customer, String> colId;
    public TreeTableColumn<Customer, String> colName;
    public TreeTableColumn<Customer, String> colContact;
    public TreeTableColumn<Customer, String> colAdd;
    public TreeTableColumn<Customer, Boolean> colAction;
    public JFXTextField txtSearch;
    public JFXTextField txtAdd;
    public JFXSpinner spinner;
    public Label lblTitle;
    public JFXButton btnClear;

    private CustomerModel customerModel;

    //validation
    private RequiredFieldValidator validatorName = new RequiredFieldValidator();
    private CustomerValidator validatorCustomer = new CustomerValidator();

    private boolean isEdit = false;
    private Customer customer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerModel = new CustomerModel();
        btnClear.setVisible(false);

        loadData();
        txtSearch.textProperty().addListener(changeListenerSearch);
    }

    private void refreshList(){
        ObservableList<Customer> customers = FXCollections.observableArrayList(CUSTOMERLIST);
        final TreeItem<Customer> root = new RecursiveTreeItem<>(customers, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }


    private void setCell() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getId())));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getName()));
        colAction.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getValue() != null));


        // create a cell value factory with an add button for each row in the table.
        colAction.setCellFactory(param -> new TreeTableCell<Customer, Boolean>(){
            JFXButton editButton = new JFXButton("Edit");
            JFXButton deleteButton = new JFXButton("Delete");
            HBox hBoxE = new HBox(5);

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                editButton.setId("button-success");
                deleteButton.setId("button-danger");

                hBoxE.getChildren().setAll(editButton, deleteButton);
                editButton.setOnAction(event -> {
                    TreeItem<Customer> treeItem = getTreeTableRow().getTreeItem();
                    customer = treeItem.getValue();
                    lblTitle.setText("Edit Jenama");
                    isEdit = true;
                    txtAdd.setText(treeItem.getValue().getName());
                });

                if(item == null){
                    setGraphic(null);
                }else {
                    setGraphic(hBoxE);
                }

            }
        });

        treeTableView.getColumns().setAll(colId, colName, colAction);
    }


    private void loadData(){
        if(!CUSTOMERLIST.isEmpty()){
            CUSTOMERLIST.clear();
        }
        Task<ObservableList<Customer>> loadCustomer = new Task<>() {
            @Override
            protected ObservableList<Customer> call() throws Exception {
                return customerModel.getCustomers();
            }
        };

        loadCustomer.setOnSucceeded(e -> {
            CUSTOMERLIST.addAll(loadCustomer.getValue());
            setCell();
            refreshList();
        });

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(loadCustomer);
        exec.shutdown();
    }

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            btnClear.setVisible(true);
            treeTableView.setPredicate(new Predicate<TreeItem<Customer>>() {
                @Override
                public boolean test(TreeItem<Customer> brandTreeItem) {
                    Boolean flag = brandTreeItem.getValue().getName().toLowerCase().contains(newValue.toLowerCase());
                    return flag;
                }
            });
        }
    };

    public void resetAction(ActionEvent actionEvent) {
        lblTitle.setText("Tambah Pengguna");
        isEdit = false;
        txtAdd.clear();
        if(validatorName.getHasErrors() || validatorCustomer.getHasErrors()){
            txtAdd.resetValidation();
        }
    }

    public void saveAction(ActionEvent actionEvent) {
        validatorName.setMessage("Sila masukkan nama");
        validatorCustomer.setMessage("Pengguna sudah ada!");
        txtAdd.getValidators().addAll(validatorName , validatorCustomer);

        txtAdd.validate();
        if(validatorName.getHasErrors() || validatorCustomer.getHasErrors()){
            return;
        }

        String name = txtAdd.getText();
        String slug = name.toLowerCase().trim();

//        if(isEdit){
//            Brand editBrand = new Brand();
//            editBrand.setName(name);
//            editBrand.setSlug(slug);
//            customerModel.updateBrand(editBrand, brand);
//
//            loadData();
//            refreshList();
//            txtName.clear();
//            return;
//        }

//
//        Brand brand = new Brand();
//        brand.setName(name);
//        brand.setSlug(slug);
//
//        brandModel.saveBrand(brand);
//        loadData();
//        refreshList();
//        txtName.clear();

    }

    public void btnSearchAction(ActionEvent actionEvent) {
        txtSearch.clear();
        btnClear.setVisible(false);
    }
}
