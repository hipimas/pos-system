package controller.subCont;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.Category;
import entity.Product;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.CategoryModel;
import validation.CategoryValidator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryController implements Initializable {
    public AnchorPane currentPane;
    public JFXTreeTableView<Category> treeTableView;
    public TreeTableColumn<Category, String> colCat;
    public TreeTableColumn<Category, String> colSub;
    public TreeTableColumn<Category, String> colId;
    public TreeTableColumn<Category, String> colProduct;
    public TreeTableColumn<Category, Boolean> colAction;
    public Label lblTitle;
    public VBox mainVBox;
    public VBox vBoxAddMain;
    public VBox vBoxAddSub;
    public VBox vBoxEditMain;
    public VBox vBoxEditSub;
    public VBox vBoxOption;
    public JFXTextField txtMain;
    public JFXTextField txtSub;
    public JFXTextField txtEditMain;
    public JFXCheckBox checkMain;
    public JFXComboBox<Category> comboCheckSub;
    public JFXComboBox<Category> comboEditMain;
    public JFXTextField txtEditSub;
    public JFXCheckBox checkSub;
    public JFXComboBox<Category> comboAddMain;
    public JFXTextField txtSearch;
    public JFXButton btnClear;
    public VBox vBoxControl;

    public JFXSpinner spinner;
    private CategoryModel categoryModel;

    //validation for add
    private RequiredFieldValidator requiredFieldValidatorMain = new RequiredFieldValidator();
    private CategoryValidator categoryValidatorMain = new CategoryValidator();
    private CategoryValidator categoryValidatorSub = new CategoryValidator();
    private RequiredFieldValidator requiredFieldValidatorSub = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorSubCombo = new RequiredFieldValidator();

    //validation for edit
    private RequiredFieldValidator requiredFieldValidatorEditMain = new RequiredFieldValidator();
    private CategoryValidator categoryValidatorEditMain = new CategoryValidator();
    private RequiredFieldValidator requiredFieldValidatorEditSub = new RequiredFieldValidator();
    private CategoryValidator categoryValidatorEditSub = new CategoryValidator();
    private RequiredFieldValidator requiredFieldValidatorEditSubCombo = new RequiredFieldValidator();

    //validation for change as main to sub
    private RequiredFieldValidator requiredFieldValidatorChangeSub = new RequiredFieldValidator();

    private boolean addParent = false;
    private boolean addChild = false;
    private boolean editTextParent = false;
    private boolean editTextChild = false;
    private boolean childChangeNewParent = false;
    private boolean changeParentAsChild = false;
    private boolean childChangeAsParent = false;

    private ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();
    private TreeItem<Category> root;

    private TreeItem<Category> treeItemSelectedEdit;
    private TreeItem<Category> treeItemSelectedDelete;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryModel = new CategoryModel();
        vBoxControl.setVisible(false);

        vBoxAddMain.setVisible(false);
        vBoxAddSub.setVisible(false);
        vBoxEditMain.setVisible(false);
        vBoxEditSub.setVisible(false);

        loadData();

        //parent edit listener and converter
        checkMain.selectedProperty().addListener(new ParentChangeListener());
        comboCheckSub.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        //child edit listener and converter
        checkSub.selectedProperty().addListener(new ChildChangeListener());
        comboEditMain.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        //converter parent for adding new child
        comboAddMain.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        //filter search
        txtSearch.textProperty().addListener(changeListenerSearch);
    }

    private void loadData() {
        spinner = new JFXSpinner();
        spinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        currentPane.getChildren().add(spinner);

        AnchorPane.setRightAnchor(spinner, 250.0 );
        AnchorPane.setTopAnchor(spinner, 150.0);

        if(!CATEGORIESLIST.isEmpty()){
            CATEGORIESLIST.clear();
        }

        Task<ObservableList<Category>> loadCategory = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getCategories();
            }

        };

        spinner.progressProperty().bind(loadCategory.progressProperty());
        spinner.visibleProperty().bind(loadCategory.runningProperty());

        loadCategory.setOnSucceeded(e -> {
            CATEGORIESLIST.addAll(loadCategory.getValue());
            createTableView();
        });

        ExecutorService exec = Executors.newFixedThreadPool(1);
        exec.submit(loadCategory);
        exec.shutdown();


    }

    private void createTableView() {
        colId.setCellValueFactory(data -> new SimpleStringProperty("" + data.getValue().getValue().getId()));
        colCat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getName()));
        colSub.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getParent().getValue() != null ? data.getValue().getParent().getValue().getName() : " - " ));
        colAction.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue() != null));
        colAction.setCellFactory(data -> new TreeItemCell());

        treeTableView.getColumns().setAll(colId, colCat, colSub, colAction);

        root = new TreeItem<>();
        for (Category category : CATEGORIESLIST) {
            if(category.getParent() == null){
                TreeItem<Category> categoryTreeItem = new TreeItem<>(category);
                root.getChildren().add(categoryTreeItem);
                categoryTreeItem.setExpanded(true);
                for(Category childCat: category.getCategoryListSubcategories()){
                    TreeItem<Category> childCategoryTreeItem = new TreeItem<>(childCat);
                    categoryTreeItem.getChildren().add(childCategoryTreeItem);
                }
            }
        }

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    private class TreeItemCell extends TreeTableCell<Category, Boolean> {
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null){
                setGraphic(null);
                setText(null);
            }else {
                TreeItem<Category> treeItem = getTreeTableRow().getTreeItem();

                JFXButton btnView = new JFXButton("View");
                btnView.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnView.setId("button-icon-primary");
                btnView.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.EYE, "25"));
                btnView.setOnAction(new ViewClicked(treeItem));

                JFXButton btnEdit = new JFXButton("Edit");
                btnEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnEdit.setId("button-icon-success");
                btnEdit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PENCIL_BOX_OUTLINE, "25"));
                btnEdit.setOnAction(new EditClicked(treeItem));

                JFXButton btnDelete = new JFXButton("Delete");
                btnDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnDelete.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DELETE_FOREVER, "25"));
                btnDelete.setId("button-icon-danger");
                btnDelete.setOnAction(new DeleteClicked(treeItem));

                HBox hBox = new HBox(btnView,btnEdit,btnDelete);
                hBox.setSpacing(5);

                setGraphic(hBox);
                setText(null);
            }

        }
    }

    private class EditClicked implements EventHandler<ActionEvent> {
        TreeItem<Category> currentSelectTreeItem;

        EditClicked(TreeItem<Category> treeItem) {
            this.currentSelectTreeItem = treeItem;
        }

        @Override
        public void handle(ActionEvent event) {
            System.out.println("from edit click " + currentSelectTreeItem.getValue().getName() + " id:" + currentSelectTreeItem.getValue().getId());
            lblTitle.setText("Edit Kategori");

            //set the selected
            treeItemSelectedEdit = currentSelectTreeItem;

            //clear all display box
            falseOther();
            clearOther();
            clearError();

            if(currentSelectTreeItem.getParent().getValue() != null){
                //edit child
                System.out.println("edit sub cat " + treeItemSelectedEdit.getValue());
                txtEditSub.setText(currentSelectTreeItem.getValue().getName());
                vBoxControl.setVisible(true);
                vBoxEditSub.setVisible(true);
                editTextChild = true;
                childChangeNewParent = true;

                //add parent
                ObservableList<Category> categories = FXCollections.observableArrayList();
                for(TreeItem<Category> parent : root.getChildren()){
                    categories.add(parent.getValue());
                }
                comboEditMain.setItems(null);
                comboEditMain.setItems(categories);
                comboEditMain.getSelectionModel().select(currentSelectTreeItem.getParent().getValue());

//                requiredFieldValidatorEditSub.setMessage("Masukkan nama");
//                categoryValidatorEditSub.setMessage("Kategori sudah ada");
//                requiredFieldValidatorEditSubCombo.setMessage("Sila Pilih");
//                txtEditSub.getValidators().addAll(requiredFieldValidatorEditSub, categoryValidatorEditSub);
//                comboEditMain.getValidators().add(requiredFieldValidatorEditSubCombo);

            } else {
                //edit parent
                System.out.println("edit parent");
                txtEditMain.setText(currentSelectTreeItem.getValue().getName());
                vBoxControl.setVisible(true);
                vBoxEditMain.setVisible(true);
                comboCheckSub.setVisible(false);

                editTextParent = true;
//                requiredFieldValidatorEditMain.setMessage("Masukkan nama");
//                categoryValidatorEditMain.setMessage("Kategori sudah ada");
//                txtEditMain.getValidators().addAll(requiredFieldValidatorEditMain, categoryValidatorEditMain);
            }

        }
    }

    private class DeleteClicked implements EventHandler<ActionEvent>{

        TreeItem<Category> currentSelectTreeItem;

        DeleteClicked(TreeItem<Category> treeItem) {
            this.currentSelectTreeItem = treeItem;
        }

        @Override
        public void handle(ActionEvent event) {
            System.out.println("from delete click " + currentSelectTreeItem.getValue().getName() + " id:" + currentSelectTreeItem.getValue().getId());
            lblTitle.setText("Delete Kategori");

            //set the selected
            treeItemSelectedDelete = currentSelectTreeItem;

            //clear all display box
            falseOther();
            clearOther();
            clearError();

            if(currentSelectTreeItem.getParent().getValue() != null){
                //delete child
                System.out.println("delete child");
                if(currentSelectTreeItem.getValue().getProductList().size() > 0){
                    //error parent have product
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Current categories have  "+ currentSelectTreeItem.getValue().getProductList().size() +" product! Remove product associate with the categories before continue");
                    alert.showAndWait();

                    if(alert.getResult() == ButtonType.OK){
                        backAction(event);
                    }
                } else {
                    //proceed for delete parent

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure, process cant be undo..");
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK){
                        System.out.println("proceed");
                        categoryModel.deleteCategory(currentSelectTreeItem.getValue());
                        backAction(event);
                        loadData();
                        clearText();
                    } else if(alert.getResult() == ButtonType.CANCEL){
                        System.out.println("cancel");
                    }
                }


            } else {
                //delete parent
                System.out.println("delete parent");

                if(currentSelectTreeItem.getChildren().size() > 0){
                    //error parent have child categories

                    Alert alert = new Alert(Alert.AlertType.WARNING, "Current categories have "+ currentSelectTreeItem.getChildren().size() +" child! Remove child before continue");
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK){
                        backAction(event);
                    }
                }

                else if(currentSelectTreeItem.getValue().getProductList().size() > 0){
                    //error parent have product

                    Alert alert = new Alert(Alert.AlertType.WARNING, "Current categories have "+ currentSelectTreeItem.getValue().getProductList().size() +" product! Remove product associate with the categories before continue");
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK){
                        backAction(event);
                    }
                } else {
                    //proceed delete child

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure, process cant be undo..");
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK){
                        System.out.println("proceed");
                        categoryModel.deleteCategory(currentSelectTreeItem.getValue());
                        backAction(event);
                        loadData();
                        clearText();
                    } else if(alert.getResult() == ButtonType.CANCEL){
                        System.out.println("cancel");
                    }
                }
            }

        }
    }

    private class ViewClicked implements EventHandler<ActionEvent> {

        ViewClicked(TreeItem<Category> categoryTreeItem) {
        }

        @Override
        public void handle(ActionEvent event) {

        }
    }

    private class ParentChangeListener implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            //clear all error
            clearError();

            if(newValue){
                if(treeItemSelectedEdit.getChildren().size() > 0){
                    Alert alert = new Alert(Alert.AlertType.WARNING,"Current parent have child, please move all child before proceed. Current child: " + treeItemSelectedEdit.getChildren().size() );
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.OK) {
                        checkMain.selectedProperty().set(false);
                    }

                } else {
                    System.out.println("proceed changes");
                    comboCheckSub.setVisible(true);
                    changeParentAsChild = true;


                    //displaying  item for current parent except this edit parent
                    ObservableList<Category> categories = FXCollections.observableArrayList();
                    for(TreeItem<Category> parent : root.getChildren()){
                        if(parent != treeItemSelectedEdit){
                            categories.add(parent.getValue());
                        }
                    }

                    //set display item in check sub for new selection
                    comboCheckSub.setItems(null);
                    comboCheckSub.setItems(categories);

                    //add validation
                    requiredFieldValidatorChangeSub.setMessage("Sila pilih");
                    comboCheckSub.getValidators().add(requiredFieldValidatorChangeSub);
                }
            } else {
                //cancel parent to child
                comboCheckSub.setItems(null);
                comboCheckSub.setVisible(false);

                changeParentAsChild = false;
                editTextParent = true;
            }
        }
    }

    private class ChildChangeListener implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            //clear all error
            clearError();

            if(newValue){
                System.out.println(treeItemSelectedEdit);
                comboEditMain.setDisable(true);
                childChangeNewParent = false;
                childChangeAsParent = true;

                txtEditSub.setText(treeItemSelectedEdit.getValue().getName());
                comboEditMain.getSelectionModel().select(treeItemSelectedEdit.getParent().getValue());
            }else {
                comboEditMain.setDisable(false);
                childChangeNewParent = true;
                editTextChild = true;
                childChangeAsParent = false;
            }
        }
    }

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            btnClear.setVisible(true);
            if (newValue.isEmpty()) {
                treeTableView.setRoot(root);
            }
            else {
                TreeItem<Category> filteredRoot = new TreeItem<>();
                filter(root, newValue, filteredRoot);
                treeTableView.setRoot(filteredRoot);
            }
        }
    };

    private void filter(TreeItem<Category> root, String filter, TreeItem<Category> filteredRoot) {
        for (TreeItem<Category> child : root.getChildren()) {
            TreeItem<Category> filteredChild = new TreeItem<>();
            filteredChild.setValue(child.getValue());
            filteredChild.setExpanded(true);
            filter(child, filter, filteredChild );
            if (!filteredChild.getChildren().isEmpty() || filteredChild.getValue().getSlug().contains(filter.toLowerCase())) {
//                System.out.println(filteredChild.getValue() + " matches.");
                filteredRoot.getChildren().add(filteredChild);
            }
        }
    }

    private void clearOther(){
        vBoxOption.setVisible(false);
        vBoxAddMain.setVisible(false);
        vBoxAddSub.setVisible(false);
        vBoxEditSub.setVisible(false);
        vBoxEditMain.setVisible(false);

        //clear check selected
        checkMain.setSelected(false);
        checkSub.setSelected(false);

        //clear comboBox
        comboAddMain.setItems(null);
        comboCheckSub.setItems(null);
        comboEditMain.setItems(null);
    }

    private void falseOther(){
        addParent = false;
        addChild = false;
        editTextParent = false;
        editTextChild = false;
        childChangeNewParent = false;
        changeParentAsChild = false;
        childChangeAsParent = false;
    }

    private void clearError() {
        txtSub.resetValidation();
        txtMain.resetValidation();
        comboAddMain.resetValidation();
        txtEditMain.resetValidation();
        txtEditSub.resetValidation();
        txtEditSub.getValidators().clear();
        comboEditMain.resetValidation();
        comboCheckSub.resetValidation();
    }

    private void clearText(){
        txtMain.clear();
        txtSub.clear();
        txtEditSub.clear();
        txtEditMain.clear();
    }

    public void btnMainAction(ActionEvent actionEvent) {
        clearOther();
        falseOther();

        lblTitle.setText("Tambah Kategori Utama");
        vBoxControl.setVisible(true);

        vBoxAddMain.setVisible(true);
        addParent = true;

        requiredFieldValidatorMain.setMessage("Masukkan nama");
        categoryValidatorMain.setMessage("Kategori sudah ada");
        txtMain.getValidators().addAll(requiredFieldValidatorMain, categoryValidatorMain);
    }

    public void btnSubAction(ActionEvent actionEvent) {
        clearOther();
        falseOther();
        lblTitle.setText("Tambah Sub Kategori");
        vBoxControl.setVisible(true);


        addChild = true;
        vBoxAddSub.setVisible(true);

        ObservableList<Category> categories = FXCollections.observableArrayList();
        for(TreeItem<Category> category : root.getChildren()){
            categories.add(category.getValue());
        }

        comboAddMain.setItems(null);
        comboAddMain.setItems(categories);

        requiredFieldValidatorSub.setMessage("Masukkan Nama");
        categoryValidatorSub.setMessage("Kategori sudah ada");
        txtSub.getValidators().setAll(requiredFieldValidatorSub, categoryValidatorSub);

        requiredFieldValidatorSubCombo.setMessage("Sila Pilih");
        comboAddMain.getValidators().add(requiredFieldValidatorSubCombo);
    }

    public void backAction(ActionEvent actionEvent) {
        clearOther();
        falseOther();
        vBoxControl.setVisible(false);

        lblTitle.setText("Tambah Kategori");
        vBoxOption.setVisible(true);
    }

    public void saveAction(ActionEvent actionEvent) {
        System.out.println("////////////////before/////////////////////");
        System.out.println( "value of : addParent " + addParent);
        System.out.println( "value of : addChild " + addChild);
        System.out.println( "value of : editTextParent " + editTextParent);
        System.out.println( "value of : editTextChild " + editTextChild);
        System.out.println( "value of : childChangeNewParent " + childChangeNewParent);
        System.out.println( "value of : changeParentAsChild " + changeParentAsChild);
        System.out.println( "value of : childChangeAsParent " + childChangeAsParent);
        System.out.println("/////////////////before////////////////////");

        //add parent
        if(addParent){
            txtMain.validate();

            if(requiredFieldValidatorMain.getHasErrors() || categoryValidatorMain.getHasErrors()){
                return;
            }

            System.out.println("add new main kategori " + txtMain.getText());
            Category newMain = new Category();
            newMain.setName(txtMain.getText());
            newMain.setSlug(txtMain.getText().toLowerCase());
            categoryModel.saveCategory(newMain);
            backAction(actionEvent);
            loadData();
            clearText();
            System.out.println("done add new cat parent");
        }

        //add child
        if(addChild){
            txtSub.validate();
            comboAddMain.validate();

            if(requiredFieldValidatorSub.getHasErrors() || categoryValidatorSub.getHasErrors() || requiredFieldValidatorSubCombo.getHasErrors()){
                txtSub.requestFocus();
                return;
            }

            System.out.println("add new sub kategori " + txtSub.getText() + " parent " + comboAddMain.getValue());
            Category newSub = new Category();
            newSub.setName(txtSub.getText());
            newSub.setSlug(txtSub.getText().toLowerCase());
            newSub.setParent(comboAddMain.getValue());
            categoryModel.saveCategory(newSub);
            backAction(actionEvent);
            loadData();
            clearText();
            System.out.println("done add new cat cild");
        }

        //edit child - change text and and child change as parent
        if(editTextChild && childChangeAsParent){
            Category prevCategory = treeItemSelectedEdit.getValue();

            requiredFieldValidatorEditSub.setMessage("Masukkan nama");
            categoryValidatorEditSub.setMessage("Kategori sudah ada");
            requiredFieldValidatorEditSubCombo.setMessage("Sila Pilih");

            comboEditMain.getValidators().add(requiredFieldValidatorEditSubCombo);
            txtEditSub.getValidators().add(requiredFieldValidatorEditSub);

            txtEditSub.validate();
            if(requiredFieldValidatorEditSub.getHasErrors()){
                System.out.println("Empty field");
                return;
            }

            if(!prevCategory.getSlug().equals(txtEditSub.getText().toLowerCase())){
                txtEditSub.getValidators().add(categoryValidatorEditSub);

                txtEditSub.validate();

                if(categoryValidatorEditSub.getHasErrors()){
                    System.out.println("error edit text that category already same name");
                    return;
                }
            }

            if(!prevCategory.getSlug().equals(txtEditSub.getText().toLowerCase())){
                System.out.println("new name " + txtEditSub.getText());
                Category editSubCat = new Category();
                editSubCat.setName(txtEditSub.getText());
                editSubCat.setSlug(txtEditSub.getText().toLowerCase());
                categoryModel.updateCategory(editSubCat, prevCategory, "updateName");
            }

            System.out.println("change as new parent: " + prevCategory.getName());
            categoryModel.updateCategory(null, prevCategory, "parentNull");

            backAction(actionEvent);
            loadData();
            clearText();
        }

        //edit child - change text and child change new parent
        if(editTextChild && childChangeNewParent){
            System.out.println("edit sub text and change new parent");
            Category category = comboEditMain.getSelectionModel().getSelectedItem();

            Category prevCategory = treeItemSelectedEdit.getValue();
            if(prevCategory.getParent().getId() == category.getId() && !txtEditSub.getText().isEmpty() && txtEditSub.getText().toLowerCase().equals(prevCategory.getSlug())){
                System.out.println("no changes of parent");
                backAction(actionEvent);
                clearText();
                clearError();
                return;
            } else {

                requiredFieldValidatorEditSub.setMessage("Masukkan nama");
                categoryValidatorEditSub.setMessage("Kategori sudah ada");
                requiredFieldValidatorEditSubCombo.setMessage("Sila Pilih");

                comboEditMain.getValidators().add(requiredFieldValidatorEditSubCombo);
                txtEditSub.getValidators().add(requiredFieldValidatorEditSub);

                txtEditSub.validate();
                if(requiredFieldValidatorEditSub.getHasErrors()){
                    System.out.println("Empty field");
                    return;
                }

                if(!prevCategory.getSlug().equals(txtEditSub.getText().toLowerCase())){
                   txtEditSub.getValidators().add(categoryValidatorEditSub);

                   txtEditSub.validate();

                   if(categoryValidatorEditSub.getHasErrors()){
                       System.out.println("error edit text that category already same name");
                       return;
                   }

                }

                //change to new parent
                if(prevCategory.getParent().getId() != category.getId()){
                    System.out.println("done update parent old p: " + prevCategory.getParent().getName() + " to :" + category.getName());
                    categoryModel.updateCategory(category, prevCategory, "updateParent");
                }

                //update name
                if(!prevCategory.getName().toLowerCase().equals(txtEditSub.getText().toLowerCase())){
                    System.out.println("done update name old name: " + prevCategory.getName() + " to :" + txtEditSub.getText());
                    Category editSubCat = new Category();
                    editSubCat.setName(txtEditSub.getText());
                    editSubCat.setSlug(txtEditSub.getText().toLowerCase());
                    categoryModel.updateCategory(editSubCat, prevCategory, "updateName");
                }

                backAction(actionEvent);
                loadData();
                clearText();
                System.out.println("done edit child name and change new parent");

            }
        }


        //edit parent
        if(editTextParent && !changeParentAsChild){
            Category prevCategory = treeItemSelectedEdit.getValue();
            if(!txtEditMain.getText().isEmpty() && txtEditMain.getText().toLowerCase().equals(prevCategory.getSlug())){
                System.out.println("parent edit text no changes");
                backAction(actionEvent);
                clearText();
                clearError();
                return;
            }

            requiredFieldValidatorEditMain.setMessage("Masukkan nama");
            categoryValidatorEditMain.setMessage("Kategori sudah ada");
            txtEditMain.getValidators().addAll(requiredFieldValidatorEditMain, categoryValidatorEditMain);

            txtEditMain.validate();

            if(requiredFieldValidatorEditMain.getHasErrors() || categoryValidatorEditMain.getHasErrors()){
                return;
            }

            System.out.println("dane changes name old: " + prevCategory.getName() + " to : " + txtEditMain.getText());

            Category editMainCat = new Category();
            editMainCat.setName(txtEditMain.getText());
            editMainCat.setSlug(txtEditMain.getText().toLowerCase());
            categoryModel.updateCategory(editMainCat, prevCategory, "updateName");
            backAction(actionEvent);
            loadData();
            clearText();
        }

        //edit parent text and change as child
        if(editTextParent && changeParentAsChild){
            Category prevCategory = treeItemSelectedEdit.getValue();

            requiredFieldValidatorChangeSub.setMessage("Sila pilih");
            comboCheckSub.getValidators().add(requiredFieldValidatorChangeSub);

            requiredFieldValidatorEditMain.setMessage("Masukkan nama");
            txtEditMain.getValidators().addAll(requiredFieldValidatorEditMain);

            txtEditMain.validate();
            comboCheckSub.validate();

            if(requiredFieldValidatorChangeSub.getHasErrors() || requiredFieldValidatorEditMain.getHasErrors()){
                return;
            }

            if(!txtEditMain.getText().toLowerCase().equals(prevCategory.getSlug())){
                categoryValidatorEditMain.setMessage("Kategori sudah ada");
                txtEditMain.getValidators().add(categoryValidatorEditMain);

                txtEditMain.validate();
                if(categoryValidatorEditMain.getHasErrors()){
                    return;
                }

                //update name
                System.out.println("changes name old: " +prevCategory.getName() + " to new: " + txtEditMain.getText());
                Category editMainCat = new Category();
                editMainCat.setName(txtEditMain.getText());
                editMainCat.setSlug(txtEditMain.getText().toLowerCase());
                categoryModel.updateCategory(editMainCat, prevCategory, "updateName");
            }

            //change as child
            System.out.println("update main to sub old: null of " + prevCategory.getName() + " new parent: " + comboCheckSub.getValue().getName());
                categoryModel.updateCategory(comboCheckSub.getValue(), prevCategory, "updateParent");
            backAction(actionEvent);
            loadData();
            clearText();

        }

        System.out.println("////////////////after/////////////////////");
        System.out.println( "value of : addParent " + addParent);
        System.out.println( "value of : addChild " + addChild);
        System.out.println( "value of : editTextParent " + editTextParent);
        System.out.println( "value of : editTextChild " + editTextChild);
        System.out.println( "value of : childChangeNewParent " + childChangeNewParent);
        System.out.println( "value of : changeParentAsChild " + changeParentAsChild);
        System.out.println( "value of : childChangeAsParent " + childChangeAsParent);
        System.out.println("/////////////////after////////////////////");

    }

    public void resetAction(ActionEvent actionEvent) {
    }

    public void btnClearAction(ActionEvent actionEvent) {
        txtSearch.clear();
        btnClear.setVisible(false);
    }


}