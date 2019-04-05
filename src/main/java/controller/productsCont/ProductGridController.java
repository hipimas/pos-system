package controller.productsCont;

import com.jfoenix.controls.*;
import configuration.Setting;
import configuration.StageView;
import controller.productsCont.AddProductController;
import controller.productsCont.EditProductController;
import controller.productsCont.ProductDetailController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.Brand;
import entity.Category;
import entity.Product;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.BrandModel;
import model.CategoryModel;
import model.ProductModel;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductGridController implements Initializable {
    public AnchorPane currentPane;
    public AnchorPane allPane;
    public AnchorPane listPane;
    public TableView<Product> tableView;
    public TableColumn<Product, String> colBarcode;
    public TableColumn<Product, String> colName;
    public TableColumn<Product, String> colPriceSell;
    public TableColumn<Product, Boolean> colAction;
    public JFXTreeView catTreeView;
    public AnchorPane catPane;
    public JFXTreeView brandTreeView;
    public AnchorPane brandPane;
    public JFXTextField txtSearch;
    public JFXButton btnClear;
    public JFXTabPane tabPane;

    private ProductModel productModel;
    private CategoryModel categoryModel;
    private BrandModel brandModel;

    private ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();
    private ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();
    private ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();

    private BorderPane borderPane;

    JFXSpinner spinner;

    public void initData(BorderPane borderPane){
        this.borderPane = borderPane;
        getExitButton();
        addProductButton();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productModel = new ProductModel();
        categoryModel = new CategoryModel();
        brandModel = new BrandModel();

        loadData();

        //filter search
        txtSearch.textProperty().addListener(changeListenerSearch);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) currentPane.getScene().getWindow();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        tableView.setItems(null);
                        if(StageView.getMap("productGrid") != null){
                            StageView.removeMap("productGrid");
                        }
                    }
                });
            }
        });


    }

    private void loadData(){
        spinner = new JFXSpinner();
        spinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        allPane.getChildren().add(spinner);
        AnchorPane.setLeftAnchor(spinner, currentPane.getWidth() / 2);
        AnchorPane.setRightAnchor(spinner, currentPane.getWidth() / 2);
        AnchorPane.setTopAnchor(spinner, 100.0);

        if (!PRODUCTLIST.isEmpty()) {
            PRODUCTLIST.clear();
        }
        if(!CATEGORIESLIST.isEmpty()){
            CATEGORIESLIST.clear();
        }
        if(!BRANDLIST.isEmpty()){
            BRANDLIST.clear();
        }


        Task<ObservableList<Product>> loadProduct = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                return productModel.getProducts();
            }
        };

        spinner.progressProperty().bind(loadProduct.progressProperty());
        spinner.visibleProperty().bind(loadProduct.runningProperty());

        loadProduct.setOnSucceeded(e -> {
            PRODUCTLIST.addAll(loadProduct.getValue());

//            new StatusBar("Data loaded completed (" + PRODUCTLIST.size() + " item showing)", "success", MaterialDesignIcon.MATERIAL_UI);

            GridView<Product> myGrid = productGridView(PRODUCTLIST);

            myGrid.prefHeightProperty().bind(allPane.heightProperty());
            myGrid.prefWidthProperty().bind(allPane.widthProperty());
            myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            myGrid.setCellHeight(280);
            myGrid.setCellWidth(180);

            allPane.getChildren().clear();
            allPane.getChildren().add(myGrid);


            colBarcode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
            colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
            colPriceSell.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice().getPriceSell())));
            colAction.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue() != null));
            colAction.setCellFactory(data -> new ActionCellItem());

            tableView.getColumns().setAll(colBarcode, colName, colPriceSell, colAction);
            tableView.setItems(PRODUCTLIST);

        });

        Task<ObservableList<Brand>> loadBrand = new Task<ObservableList<Brand>>() {
            @Override
            protected ObservableList<Brand> call() throws Exception {
                return brandModel.getBrands();
            }
        };

        loadBrand.setOnSucceeded(event -> {
            BRANDLIST.addAll(loadBrand.getValue());

            TreeItem<Brand> root = new TreeItem<>(null);
            for (Brand brand : BRANDLIST) {
                TreeItem<Brand> brandTreeItemItem = new TreeItem<>(brand);
                root.getChildren().add(brandTreeItemItem);
            }

            brandTreeView.setRoot(root);
            brandTreeView.setShowRoot(false);

            brandTreeView.setCellFactory(new Callback<TreeView<Brand>, TreeCell<Brand>>() {
                @Override
                public TreeCell<Brand> call(TreeView<Brand> param) {
                    return new TreeCell<>(){
                        @Override
                        protected void updateItem(Brand item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                Label label1 = new Label(item.getName());
                                Label label3 = new Label();
                                if(item.getProductList().size() > 0){
                                    label3.setText(String.valueOf(item.getProductList().size()));
                                }
                                label3.setPadding(new Insets(0,10,0,0));
                                Region region = new Region();
                                HBox.setHgrow(region, Priority.ALWAYS);
                                HBox hBox = new HBox(label1, region, label3);

                                setText(null);
                                setGraphic(hBox);

                                //default value
//                            setText(item.getName());
                            }
                        }
                    };
                }
            });

            brandTreeView.getSelectionModel().selectedItemProperty().addListener(changeListenerBrandTree);


        });

        Task<ObservableList<Category>> loadCategory = new Task<ObservableList<Category>>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getCategories();
            }
        };

        loadCategory.setOnSucceeded(event -> {
            CATEGORIESLIST.addAll(loadCategory.getValue());

            TreeItem<Category> root = new TreeItem<>(null);
            for (Category category : CATEGORIESLIST) {
                TreeItem<Category> categoryTreeItemItem = new TreeItem<>(category);
                if(category.getParent() == null){
                    root.getChildren().add(categoryTreeItemItem);
                    for (Category childCategory : category.getCategoryListSubcategories()) {
                        TreeItem<Category> childCategoryTreeItem = new TreeItem<>(childCategory);
                        categoryTreeItemItem.getChildren().add(childCategoryTreeItem);
                    }
                }
            }

            catTreeView.setRoot(root);
            catTreeView.setShowRoot(false);

            catTreeView.setCellFactory(new Callback<TreeView<Category>, TreeCell<Category>>() {
                @Override
                public TreeCell<Category> call(TreeView<Category> param) {
                    return new TreeCell<>() {
                        @Override
                        protected void updateItem(Category item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                Label label1 = new Label(item.getName());
                                Label label3 = new Label();
                                if(item.getParent() == null){
                                    //parent
                                    List<Category> list = item.getCategoryListSubcategories();
                                    int i = 0;
                                    for(Category p : list){
                                        i += p.getProductList().size();
                                    }
                                    if(i != 0){
                                        label3 = new Label(String.valueOf(i));
                                    }

                                } else{
                                    //child
                                    if(item.getProductList().size() > 0){
                                        label3 = new Label(String.valueOf(item.getProductList().size()));
                                    }
                                }
                                label3.setPadding(new Insets(0,10,0,0));
                                Region region = new Region();
                                HBox.setHgrow(region, Priority.ALWAYS);
                                HBox hBox = new HBox(label1, region, label3);

                                setText(null);
                                setGraphic(hBox);
                            }

                        }
                    };
                }
            });
            catTreeView.getSelectionModel().selectedItemProperty().addListener(changeListenerCatTree);

        });

        ExecutorService exec = Executors.newFixedThreadPool(3);
        exec.submit(loadProduct);
        exec.submit(loadCategory);
        exec.submit(loadBrand);

        exec.shutdown();
    }

    private GridView<Product> productGridView(ObservableList<Product> PRODUCTLIST) {

        GridView<Product> myGrid = new GridView<>();
        for (Product product : PRODUCTLIST) {
            myGrid.getItems().addAll(product);
        }

        myGrid.setCellFactory(new Callback<GridView<Product>, GridCell<Product>>() {
            @Override
            public GridCell<Product> call(GridView<Product> param) {
                return new MenuItemCell();
            }
        });

        return myGrid;
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
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();

            if(StageView.getMap("productGrid") != null){
                StageView.removeMap("productGrid");
            }
        });


        return buttonExit;
    }

    private class MenuItemCell extends GridCell<Product> {
        @Override
        protected void updateItem(Product item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else{
                setText(null);
                if(item.getImageUrl() != null){
                    VBox box = new VBox(5);
                    ImageView img_cover = null;

                    //check if database have string url for database
                    if(item.getImageUrl() == null){
                        img_cover = new ImageView(new Image(getClass().getResource("/assets/noimages.png").toExternalForm(), 160, 150, true, false));
                    }else{
                        File fileImage = new File(item.getImageUrl());
                        if(fileImage.isFile() && fileImage.exists()){
                            //have string but file not found
                            img_cover = new ImageView(new Image(fileImage.toURI().toString(),160, 150, true, false));
                        } else {
                            //find the file based on save setting on config file
                            try {
                                if(Setting.getPathImagesDefault() != null){
                                    //get name of file from save
                                    String nameFile = fileImage.getName();
                                    File fileImage2 = new File(Setting.getPathImagesDefault() + File.separatorChar + nameFile);
                                    img_cover = new ImageView(new Image(fileImage2.toURI().toString(),160, 150, true, false));
                                }
                            } catch (ConfigurationException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    Label name = new Label(item.getName());
                    name.setPadding(new Insets(5,5,5,5));
                    name.setWrapText(true);
                    Label barcode = new Label(item.getBarcode());

                    JFXButton btnView = new JFXButton("View");
                    btnView.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btnView.setId("button-icon-primary");
                    btnView.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.EYE, "25"));
                    btnView.setOnAction(new ViewClicked(item));

                    JFXButton btnEdit = new JFXButton("Edit");
                    btnEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btnEdit.setId("button-icon-success");
                    btnEdit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PENCIL_BOX_OUTLINE, "25"));
                    btnEdit.setOnAction(new EditClicked(item));

                    JFXButton btnDelete = new JFXButton("Delete");
                    btnDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btnDelete.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DELETE_FOREVER, "25"));
                    btnDelete.setId("button-icon-danger");

                    HBox hBox = new HBox(5);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().addAll(btnView, btnEdit, btnDelete);

                    Region region = new Region();
                    region.setMaxWidth(Double.MAX_VALUE);
//                    VBox.setVgrow(region, Priority.ALWAYS);

//                    box.setPadding(new Insets(5,5,5,5));
                    box.setAlignment(Pos.TOP_CENTER);
                    box.getChildren().addAll(img_cover, name, barcode, region, hBox);

                    setGraphic(box);

                }

            }
        }
    }

    private class ActionCellItem extends TableCell<Product, Boolean> {
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null){
                setGraphic(null);
                setText(null);
            }else {
                Product product = tableView.getItems().get(getIndex());

                JFXButton btnView = new JFXButton("View");
                btnView.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnView.setId("button-icon-primary");
                btnView.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.EYE, "25"));
                btnView.setOnAction(new ViewClicked(product));

                JFXButton btnEdit = new JFXButton("Edit");
                btnEdit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnEdit.setId("button-icon-success");
                btnEdit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PENCIL_BOX_OUTLINE, "25"));
                btnEdit.setOnAction(new EditClicked(product));

                JFXButton btnDelete = new JFXButton("Delete");
                btnDelete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnDelete.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.DELETE_FOREVER, "25"));
                btnDelete.setId("button-icon-danger");
                btnDelete.setOnAction(new DeleteClicked(product));

                HBox hBox = new HBox(btnView,btnEdit,btnDelete);
                hBox.setSpacing(5);
                setGraphic(hBox);
                setText(null);
            }

        }
    }

    private class EditClicked implements EventHandler<ActionEvent> {

        private Product product;

        EditClicked(Product product) {
            this.product = product;
        }

        @Override
        public void handle(ActionEvent event) {
            if(product != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/products/editProduct.fxml"));
                AnchorPane pane = null;
                try {
                    pane = fxmlLoader.load();
                    EditProductController editProductController = fxmlLoader.getController();
                    editProductController.initData(product, borderPane, currentPane);
                    borderPane.setCenter(pane);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DeleteClicked implements EventHandler<ActionEvent>{

        private Product products;

        DeleteClicked(Product product) {
            this.products = product;
        }

        @Override
        public void handle(ActionEvent event) {
            System.out.println("delete " + products.getName());
        }
    }

    private class ViewClicked implements EventHandler<ActionEvent> {

        private Product product;

        ViewClicked(Product product) {
            this.product = product;
        }

        @Override
        public void handle(ActionEvent event) {
            if(product != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/products/productDetail.fxml"));
                AnchorPane productPane = null;
                try {
                    productPane = fxmlLoader.load();
                    ProductDetailController productDetailController = fxmlLoader.getController();
                    productDetailController.initData(product, borderPane, currentPane);
//
                    borderPane.setCenter(productPane);

//                    addBackButton();
//                    //hides pagination
//                    togglePagination(true);
//                    getBackButton();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ChangeListener<TreeItem<Category>> changeListenerCatTree = new ChangeListener<TreeItem<Category>>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Category>> observable, TreeItem<Category> oldValue, TreeItem<Category> newValue) {
            if(newValue != null){
                ObservableList<Product> currentCatProductList;
                //check for the parent selection of category tree
                if(newValue.getValue().getParent() == null){
                    System.out.println("from grid" + newValue.getValue().getName() + " id " + newValue.getValue().getId());
                    currentCatProductList = FXCollections.observableArrayList();
//                    for(Product p : PRODUCTLIST ){
//                        if(p.getCategory() != null && p.getCategory().getParent().getId() == newValue.getValue().getId()){
//                            currentCatProductList.add(p);
//                        }
//                    }


                    System.out.println(newValue.getChildren());
                    if(newValue.getChildren().size() > 0){
                        for(TreeItem<Category> category : newValue.getChildren()){
                            for(Product p : PRODUCTLIST ){
                                if(p.getCategory() != null && p.getCategory().getId() == category.getValue().getId()){
                                    currentCatProductList.add(p);
                                }
                            }
                        }
                    } else {
                        //no child
                        for(Product p : PRODUCTLIST ){
                            if(p.getCategory() != null && p.getCategory().getId() == newValue.getValue().getId()){
                                currentCatProductList.add(p);
                            }
                        }
                    }

                } else {
                    //check for selection child category from category tree
                    currentCatProductList = FXCollections.observableArrayList();
                    for(Product p : PRODUCTLIST ){
                        if(p.getCategory() != null && p.getCategory().getId() == newValue.getValue().getId()){
                            currentCatProductList.add(p);
                        }
                    }
                }

                GridView<Product> myGrid = productGridView(currentCatProductList);

                myGrid.prefHeightProperty().bind(catPane.heightProperty());
                myGrid.prefWidthProperty().bind(catPane.widthProperty());
                myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                myGrid.setCellHeight(280);
                myGrid.setCellWidth(180);

                catPane.getChildren().clear();
                catPane.getChildren().add(myGrid);
            }
        }
    };

    private ChangeListener<TreeItem<Brand>> changeListenerBrandTree = new ChangeListener<TreeItem<Brand>>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Brand>> observable, TreeItem<Brand> oldValue, TreeItem<Brand> newValue) {
            ObservableList<Product> currentBrandProductList = FXCollections.observableArrayList();
            for(Product p : PRODUCTLIST ){
                if(p.getBrand().getId() == newValue.getValue().getId()){
                    currentBrandProductList.add(p);
                }
            }

            GridView<Product> myGrid = productGridView(currentBrandProductList);

            myGrid.prefHeightProperty().bind(brandPane.heightProperty());
            myGrid.prefWidthProperty().bind(brandPane.widthProperty());
            myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            myGrid.setCellHeight(280);
            myGrid.setCellWidth(180);

            brandPane.getChildren().clear();
            brandPane.getChildren().add(myGrid);
        }
    };

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            btnClear.setVisible(true);
            if (newValue.isEmpty()) {
                GridView<Product> myGrid = productGridView(PRODUCTLIST);

                myGrid.prefHeightProperty().bind(allPane.heightProperty());
                myGrid.prefWidthProperty().bind(allPane.widthProperty());
                myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                myGrid.setCellHeight(280);
                myGrid.setCellWidth(180);

                allPane.getChildren().clear();
                allPane.getChildren().add(myGrid);

//                treeTableView.setRoot(root);

            }
            else {
                System.out.println(newValue);
                String lowerCase = newValue.toLowerCase();
                tabPane.getSelectionModel().select(0);

                ObservableList<Product> productList = FXCollections.observableArrayList();
                for(Product p : PRODUCTLIST){
                    if(p.getBarcode().toLowerCase().contains(lowerCase)){
                        productList.add(p);
                    }
                    else if(p.getName().toLowerCase().contains(lowerCase)){
                        productList.add(p);
                    }
                    else if(p.getDescription().toLowerCase().contains(lowerCase)){
                        productList.add(p);
                    }
                }

                GridView<Product> myGrid = productGridView(productList);

                myGrid.prefHeightProperty().bind(allPane.heightProperty());
                myGrid.prefWidthProperty().bind(allPane.widthProperty());
                myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                myGrid.setCellHeight(280);
                myGrid.setCellWidth(180);

                allPane.getChildren().clear();
                allPane.getChildren().add(myGrid);


//                TreeItem<Category> filteredRoot = new TreeItem<>();
//                filter(root, newValue, filteredRoot);
//                treeTableView.setRoot(filteredRoot);
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

    private JFXButton getBackButton(){
        JFXButton backButton = new JFXButton("Back");
        backButton.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.ARROW_LEFT_THICK, "24"));
        backButton.setId("button-success");

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        if(toolbar != null){
            if(toolbar.getRight() != null){
                HBox hBtm = (HBox) toolbar.getRightItems().get(0);
                hBtm.setSpacing(5);
                if(hBtm.getChildren().get(0).getId().equals("button-success")){
                    hBtm.getChildren().set(0,backButton);
                }else{
                    hBtm.getChildren().add(0,backButton);
                }
            }
        }

        backButton.setOnAction(e -> {
            borderPane.setCenter(currentPane);
//            togglePagination(false);
            backButton.setVisible(false);
        });


       return backButton;
    }

    private JFXButton addProductButton(){

        JFXButton btnAdd = new JFXButton("Add");
        btnAdd.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PLUS, "25"));
        btnAdd.setId("button-primary");
        btnAdd.setOnAction(eventHandlerAddProduct);

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        if(toolbar != null){
            toolbar.getLeftItems().clear();
            toolbar.setLeftItems(btnAdd);
        }

        return btnAdd;
    }

    private EventHandler<ActionEvent> eventHandlerAddProduct = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(StageView.getMap("addProduct") != null){
                StageView.getMap("addProduct").requestFocus();
                return;
            }

            String fileFxmlUrl = "/views/products/addProduct.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileFxmlUrl));
            try{

                AnchorPane anchorPane = fxmlLoader.load();
                AddProductController addProductController = fxmlLoader.getController();
                addProductController.initData(borderPane);
//                clearAll();

                borderPane.setCenter(anchorPane);

                StageView.removeMap("productGrid");
                StageView.addMap("addProduct", ((Stage) borderPane.getScene().getWindow()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void btnClearAction(ActionEvent actionEvent) {
        txtSearch.clear();
    }
}
