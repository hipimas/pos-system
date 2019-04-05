package controller.subCont;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.validation.RequiredFieldValidator;
import entity.Brand;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.BrandModel;
import validation.BrandValidator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;


public class BrandController implements Initializable {

    public AnchorPane currentPane;
    public JFXTreeTableView<Brand> treeTableView;
    public TreeTableColumn<Brand, String> colId;
    public TreeTableColumn<Brand, String> colName;
    public TreeTableColumn<Brand, String> colProduct;
    public TreeTableColumn<Brand, Boolean> colAction;
    public JFXTextField txtSearch;
    public JFXButton btnClear;
    public JFXTextField txtName;
    public Label lblTitle;

    public JFXSpinner spinner;

    private BrandModel brandModel;
    private ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();

    //validation
    private RequiredFieldValidator validatorName = new RequiredFieldValidator();
    private BrandValidator validatorBrand = new BrandValidator();

    private boolean isEdit = false;
    private Brand brand;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        brandModel = new BrandModel();
        btnClear.setVisible(false);

        loadData();
        //for filtering data in table
        txtSearch.textProperty().addListener(changeListenerSearch);
    }

    private void refreshList(){
        ObservableList<Brand> brands = FXCollections.observableArrayList(BRANDLIST);
        final TreeItem<Brand> root = new RecursiveTreeItem<>(brands, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }


    private void setCell() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getId())));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getName()));
        colProduct.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getProductList().size())));
        colAction.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getValue() != null));


        // create a cell value factory with an add button for each row in the table.
        colAction.setCellFactory(param -> new TreeTableCell<Brand, Boolean>(){
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null){
                    setGraphic(null);
                }else {
                    JFXButton editButton = new JFXButton("Edit");
                    JFXButton deleteButton = new JFXButton("Delete");
                    HBox hBoxE = new HBox(5);

                    editButton.setId("button-success");
                    deleteButton.setId("button-danger");

                    hBoxE.getChildren().setAll(editButton, deleteButton);
                    editButton.setOnAction(event -> {
                        TreeItem<Brand> treeItem = getTreeTableRow().getTreeItem();
                        brand = treeItem.getValue();
                        lblTitle.setText("Edit Jenama");
                        isEdit = true;
                        txtName.setText(treeItem.getValue().getName());
                    });
                    setGraphic(hBoxE);
                }

            }
        });

        treeTableView.getColumns().setAll(colId, colName, colProduct, colAction);
    }


    private void loadData(){
        spinner = new JFXSpinner();
        spinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        currentPane.getChildren().add(spinner);

        AnchorPane.setRightAnchor(spinner, 250.0 );
        AnchorPane.setTopAnchor(spinner, 150.0);

        if(!BRANDLIST.isEmpty()){
            BRANDLIST.clear();
        }
        Task<ObservableList<Brand>> loadBrand = new Task<>() {
            @Override
            protected ObservableList<Brand> call() throws Exception {
                return brandModel.getBrands();
            }
        };

        spinner.progressProperty().bind(loadBrand.progressProperty());
        spinner.visibleProperty().bind(loadBrand.runningProperty());

        loadBrand.setOnSucceeded(e -> {
            BRANDLIST.addAll(loadBrand.getValue());
            setCell();
            refreshList();
        });

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(loadBrand);
        exec.shutdown();
    }

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            btnClear.setVisible(true);
            treeTableView.setPredicate(new Predicate<TreeItem<Brand>>() {
                @Override
                public boolean test(TreeItem<Brand> brandTreeItem) {
                    Boolean flag = brandTreeItem.getValue().getSlug().contains(newValue.toLowerCase());
                    return flag;
                }
            });
        }
    };



    public void resetAction(ActionEvent actionEvent) {
        lblTitle.setText("Tambah Jenama");
        isEdit = false;
        txtName.clear();
        if(validatorName.getHasErrors() || validatorBrand.getHasErrors()){
            txtName.resetValidation();
        }
    }

    public void saveAction(ActionEvent actionEvent) {
        validatorName.setMessage("Sila masukkan nama");
        validatorBrand.setMessage("Jenama sudah ada!");
        txtName.getValidators().addAll(validatorName , validatorBrand);

        txtName.validate();
        if(validatorName.getHasErrors() || validatorBrand.getHasErrors()){
            return;
        }

        String name = txtName.getText();
        String slug = name.toLowerCase().trim();

        if(isEdit){
            Brand editBrand = new Brand();
            editBrand.setName(name);
            editBrand.setSlug(slug);
            brandModel.updateBrand(editBrand, brand);

            loadData();
            refreshList();
            txtName.clear();
            return;
        }


        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);

        brandModel.saveBrand(brand);
        loadData();
        refreshList();
        txtName.clear();

    }

    public void btnClearAction(ActionEvent actionEvent) {
        txtSearch.clear();
        btnClear.setVisible(false);
    }
}
