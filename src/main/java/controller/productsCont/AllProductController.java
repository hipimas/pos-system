package controller.productsCont;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import configuration.StatusBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import entity.Brand;
import entity.Category;
import entity.Product;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import model.BrandModel;
import model.CategoryModel;
import model.ProductModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllProductController implements Initializable {

    public AnchorPane mainPane;
    public JFXTabPane tabPane;
    public JFXTreeTableView<Product> treeTableView;
    public TreeTableColumn<Product, String> colBarcode;
    public TreeTableColumn<Product, String> colName;
    public TreeTableColumn<Product, String> colDescription;
    public TreeTableColumn<Product, String> colSell;
    public TreeTableColumn<Product, String> colBuy;
    public TreeTableColumn<Product, Boolean> colAction;
    public ScrollPane scrollPane;
    public TilePane tilePane;
    public AnchorPane paneTop;
    public JFXTextField txtSearch;
    public JFXSpinner spinner;
    public JFXTreeView<Category> catTreeView;
    public ScrollPane catScrollPane;
    public TilePane catTilePane;
    public JFXTreeView<Brand> brandTreeView;
    public TilePane brandTilePane;
    public ScrollPane brandScrollPane;

    private ProductModel productModel;
    private CategoryModel categoryModel;
    private BrandModel brandModel;
    private BorderPane borderPane;

    private int perPage = 50;
    private int paginationAll = 0;
    private int paginationCat = 0;
    private int paginationBrand = 0;

    private ObservableList<Product> currentCatProductList = null;
    private ObservableList<Product> currentBrandProductList = null;

    RotateTransition rt;

    private final ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();
    private final ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();
    private final ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();

    TreeItem<Product> root;


    public void initData(AnchorPane mainPane) {
        this.borderPane = (BorderPane) mainPane.getChildren().get(0);

        JFXToolbar toolbar = (JFXToolbar) this.borderPane.getBottom();
        if(toolbar != null){
            if(toolbar.getRight() != null){
                HBox hBtm = (HBox) toolbar.getRightItems().get(0);
                JFXButton btnExit = (JFXButton) hBtm.getChildren().get(0);
                btnExit.setOnAction(event -> {
                    System.out.println("exit from button");

                    treeTableView.getColumns().clear();
                    root.setValue(null);

                    txtSearch.textProperty().removeListener(changeListenerTextSearch);
                    tabPane.getSelectionModel().selectedItemProperty().removeListener(changeListenerTabPane);
                    brandTreeView.getSelectionModel().selectedItemProperty().removeListener(changeListenerBrandTree);
                    catTreeView.getSelectionModel().selectedItemProperty().removeListener(changeListenerCatTree);

                    rt.stop();


                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    stage.close();
                });

            }
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        spinner.setVisible(false);
        productModel = new ProductModel();
        categoryModel = new CategoryModel();
        brandModel = new BrandModel();

        loadData();

        txtSearch.textProperty().addListener(changeListenerTextSearch);
        tabPane.getSelectionModel().selectedItemProperty().addListener(changeListenerTabPane);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                    mainPane.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        System.out.println("exit from close window");
                        rt.stop();

                        treeTableView.getColumns().clear();
                        root.setValue(null);

                        txtSearch.textProperty().removeListener(changeListenerTextSearch);
                        tabPane.getSelectionModel().selectedItemProperty().removeListener(changeListenerTabPane);
                        brandTreeView.getSelectionModel().selectedItemProperty().removeListener(changeListenerBrandTree);
                        catTreeView.getSelectionModel().selectedItemProperty().removeListener(changeListenerCatTree);
                    }
                });
            }
        });

    }

    private ChangeListener<String> changeListenerTextSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(tabPane.getSelectionModel().getSelectedItem().getText().equals("Senarai")){
                treeTableView.setPredicate(e -> {
                    String lowerCase = newValue.toLowerCase();
                    if(e.getValue().getBarcode().toLowerCase().contains(lowerCase)){
                        return true;
                    }
                    if(e.getValue().getName().toLowerCase().contains(lowerCase)){
                        return true;
                    }
                    return e.getValue().getDescription().toLowerCase().contains(lowerCase);
                });
            }
            else if(tabPane.getSelectionModel().getSelectedItem().getText().equals("Semua")){
                String lowerCase = newValue.toLowerCase();
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
                togglePagination(true);
                addBelow( productList, tilePane, scrollPane, 0);
            }
        }
    };

    private ChangeListener<Tab> changeListenerTabPane = new ChangeListener<Tab>() {
        @Override
        public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
            if(newValue.getText().equals("Senarai")){
                //hides pagination
                togglePagination(true);
            }
            if(newValue.getText().equals("Semua")){
                if(PRODUCTLIST.size() != 0){
                    addBelow(PRODUCTLIST, tilePane, scrollPane, paginationAll);
                }
            }
            if(newValue.getText().equals("Jenama")){
                //check  whether selected brand Tree
                if(brandTreeView.getSelectionModel().getSelectedItem() != null){
                    if(currentBrandProductList.size() != 0){
                        addBelow(currentBrandProductList, brandTilePane, brandScrollPane,paginationBrand);
                    } else {
                        // hides pagination
                        togglePagination(true);
                    }
                } else {
                    // hides pagination
                    togglePagination(true);
                }
            }

            if(newValue.getText().equals("Kategori")){
                if(catTreeView.getSelectionModel().getSelectedItem() != null){
                    if(currentCatProductList.size() != 0){
                        addBelow(currentCatProductList, catTilePane, catScrollPane, paginationCat);
                    } else {
                        // hides pagination
                        togglePagination(true);
                    }
                } else {
                    // hides pagination
                    togglePagination(true);
                }
            }
        }
    };

    private class VBoxClicked implements EventHandler<MouseEvent> {

        private Product product;

        VBoxClicked(Product product) {
            this.product = product;
        }

        @Override
        public void handle(MouseEvent event) {
            if(product != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/products/productDetail.fxml"));
                AnchorPane productPane = null;
                try {
                    productPane = fxmlLoader.load();
                    ProductDetailController productDetailController = fxmlLoader.getController();
                    productDetailController.initData(product, borderPane, mainPane);

                    borderPane.setCenter(productPane);

                    addBackButton();
                    //hides pagination
                    togglePagination(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DetailsClicked implements EventHandler<ActionEvent> {

        private Product product;

        DetailsClicked(Product product) {
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
                    productDetailController.initData(product, borderPane, mainPane);

                    borderPane.setCenter(productPane);

                    addBackButton();
                    //hides pagination
                    togglePagination(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class EditClicked implements EventHandler<ActionEvent>{

        private Product products;

        EditClicked(Product product) {
            this.products = product;
        }

        @Override
        public void handle(ActionEvent event) {
            if(products != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/products/editProduct.fxml"));
                AnchorPane pane = null;
                try {
                    pane = fxmlLoader.load();
                    EditProductController editProductController = fxmlLoader.getController();
                    editProductController.initData(products, borderPane, mainPane);
                    borderPane.setCenter(pane);

//                    addBackButton();
                    //hide pagination
                    togglePagination(true);
                    new FadeIn(pane).play();
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

    private void createGrid(List<Product> productList, TilePane currentTilePane, ScrollPane currentScrollPane) {
        currentTilePane.setPadding(new Insets(20, 10, 20, 10));
        currentTilePane.setVgap(10);
        currentTilePane.setHgap(10);
        currentTilePane.setId("tilePane");
        if(currentTilePane.getChildren().size() > 0){

//            System.out.println("still have data");

            currentTilePane.getChildren().clear();
        }

        for (int i=0; i<productList.size() ; i++) {

            Label[] labelName = new Label[productList.size()];
            Label[] labelBarcode = new Label[productList.size()];
            Label[] labelPrice = new Label[productList.size()];
            ImageView[] imageViews = new ImageView[productList.size()];
            VBox vBoxes[] = new VBox[productList.size()];
            HBox hBoxes[] = new HBox[productList.size()];
            JFXButton editBtn[] = new JFXButton[productList.size()];
            JFXButton deleteBtn[] = new JFXButton[productList.size()];

            labelName[i] = new Label();
            labelBarcode[i] = new Label();
            labelPrice[i] = new Label();
            hBoxes[i] = new HBox(5);

            if(productList.get(i).getImageUrl() == null || productList.get(i).getImageUrl().trim().isEmpty()){
                imageViews[i] = new ImageView(new Image(getClass().getResource("/assets/noimages.png").toExternalForm(), 170, 150, true, false));
            } else {
                File fileImage = new File(productList.get(i).getImageUrl());
                imageViews[i] = new ImageView(new Image(fileImage.toURI().toString(), 170, 150, true, false));
            }

            labelName[i].setText(productList.get(i).getName());
            labelName[i].setWrapText(true);
            labelBarcode[i].setText(productList.get(i).getBarcode());
            editBtn[i] = new JFXButton("Edit");
            deleteBtn[i] = new JFXButton("Delete");
            editBtn[i].setId("button-success");
            editBtn[i].setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));

            deleteBtn[i].setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
            deleteBtn[i].setId("button-danger");
            hBoxes[i].getChildren().addAll(editBtn[i], deleteBtn[i]);

            vBoxes[i] = new VBox(5);
            vBoxes[i].setAlignment(Pos.CENTER);
            vBoxes[i].setPadding(new Insets(5,5,5,5));
            vBoxes[i].setPrefWidth(170);
            vBoxes[i].setId("vBoxGrey");
            vBoxes[i].getChildren().addAll(imageViews[i], labelName[i], labelBarcode[i], labelPrice[i], hBoxes[i]);
            currentTilePane.getChildren().add(vBoxes[i]);

            //add listener to vBox and button
            vBoxes[i].setOnMouseClicked(new VBoxClicked(productList.get(i)));
            editBtn[i].setOnAction(new EditClicked(productList.get(i)));
            deleteBtn[i].setOnAction(new DeleteClicked(productList.get(i)));
        }
        currentScrollPane.setContent(currentTilePane);
    }

    private void refreshList(){
        ObservableList<Product> products = FXCollections.observableArrayList(PRODUCTLIST);
//        final TreeItem<Product> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);
        root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(null);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    private void setCell(){
        colBarcode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getBarcode()));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getName()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getDescription()));
//        colSell.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getPrice_sell())));
//        colBuy.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getPrice_purchase())));

        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        colAction.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Product, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<Product, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue() != null);
            }
        });

        // create a cell value factory with an add button for each row in the table.
        colAction.setCellFactory(param -> new TreeTableCell<Product, Boolean>(){
            JFXButton viewButton = new JFXButton("View");
            JFXButton editButton = new JFXButton("Edit");
            JFXButton deleteButton = new JFXButton("Delete");

            HBox pane = new HBox(viewButton,editButton,deleteButton);
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null){
                    setGraphic(null);
                }else {
                    viewButton.setId("button-accent");
                    editButton.setId("button-success");
                    deleteButton.setId("button-danger");
                    pane.setSpacing(5);

                    TreeItem<Product> treeItem = treeTableView.getTreeItem(getIndex());
                    Product products = treeItem.getValue();
                    viewButton.setOnAction(new DetailsClicked(products));
                    editButton.setOnAction(new EditClicked(products));
                    deleteButton.setOnAction(new DeleteClicked(products));

                    setGraphic(pane);
                }

            }
        });


        treeTableView.getColumns().setAll(colBarcode, colName, colDescription, colSell, colBuy, colAction);
    }

    private void createTree(ObservableList<Category> categories){
        ///normal setup without recursive
        TreeItem<Category> root = new TreeItem<>(null);
        for (Category category : categories) {
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
    }

    private ChangeListener<TreeItem<Category>> changeListenerCatTree = new ChangeListener<TreeItem<Category>>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Category>> observable, TreeItem<Category> oldValue, TreeItem<Category> newValue) {
            if(newValue != null){
                //check for the parent selection of category tree
                if(newValue.getValue().getParent() == null){
                    currentCatProductList = FXCollections.observableArrayList();
                    for(Product p : PRODUCTLIST ){
                        if(p.getCategory() != null && p.getCategory().getParent().getId() == newValue.getValue().getId()){
                            currentCatProductList.add(p);
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

                paginationCat = 0;
                addBelow(currentCatProductList, catTilePane, catScrollPane, paginationCat);

                if(currentCatProductList.size() == 0){
                    togglePagination(true);
                }
            }
        }
    };

    private void createBrandTree(List<Brand> brands){
        ///normal setup without recursive
        TreeItem<Brand> root = new TreeItem<>(null);
        for (Brand brand : brands) {
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
    }

    private ChangeListener<TreeItem<Brand>> changeListenerBrandTree = new ChangeListener<TreeItem<Brand>>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Brand>> observable, TreeItem<Brand> oldValue, TreeItem<Brand> newValue) {
            currentBrandProductList = FXCollections.observableArrayList();
            for(Product p : PRODUCTLIST ){
                if(p.getBrand().getId() == newValue.getValue().getId()){
                    currentBrandProductList.add(p);
                }
            }

            paginationBrand = 0;
            addBelow(currentBrandProductList, brandTilePane, brandScrollPane, paginationBrand);
            if(currentBrandProductList.size() == 0){
                togglePagination(true);
            }
        }
    };

    private void loadData(){
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

        rt = new RotateTransition(Duration.millis(800), StatusBar.getIconView());
        rt.setByAngle(360);
        rt.setCycleCount(Timeline.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);

        loadProduct.runningProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                new StatusBar("Loading all product", "run",MaterialDesignIcon.LOADING);
                rt.play();
            }
        });


        loadProduct.setOnSucceeded(e -> {
            PRODUCTLIST.addAll(loadProduct.getValue());
            addBelow(PRODUCTLIST, tilePane, scrollPane, 0);
            setCell();
            refreshList();
            rt.stop();
            StatusBar.getIconView().setRotate(0);
            new StatusBar("Data loaded completed (" + PRODUCTLIST.size() + " item showing)", "success", MaterialDesignIcon.MARKER_CHECK);
        });

        Task<ObservableList<Category>> loadCategory = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getCategories();
            }
        };

        loadCategory.setOnSucceeded(e -> {
            CATEGORIESLIST.addAll(loadCategory.getValue());
            createTree(CATEGORIESLIST);
        });

        Task<ObservableList<Brand>> loadBrand = new Task<>() {
            @Override
            protected ObservableList<Brand> call() throws Exception {
                return brandModel.getBrands();
            }
        };

        loadBrand.setOnSucceeded(e -> {
            BRANDLIST.addAll(loadBrand.getValue());
            createBrandTree(BRANDLIST);
        });

//        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExecutorService exec = Executors.newFixedThreadPool(3);
        exec.submit(loadProduct);
        exec.submit(loadCategory);
        exec.submit(loadBrand);
        exec.shutdown();
    }

    private void addBackButton() {
        //add additional back button to layout
        JFXButton backButton = new JFXButton("Back");
        backButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT, "24"));
        backButton.setId("button-success");

        backButton.setOnAction(e -> {
            borderPane.setCenter(mainPane);
            togglePagination(false);
            backButton.setVisible(false);
        });


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
    }

    private void togglePagination(boolean b) {
        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        if(toolbar != null){
            if(toolbar.getCenter() != null){
                if(b){
                    Pagination pagination = (Pagination) toolbar.getCenter();
                    pagination.setVisible(false);
                } else {
                    Pagination pagination = (Pagination) toolbar.getCenter();
                    pagination.setVisible(true);
                }
            }
        }
    }

    private void addBelow(ObservableList<Product> productList, TilePane currentTilePane, ScrollPane currentScrollPane, int selected) {

        final int ITEMS_PER_PAGE = 50 ;
        final int NUM_PAGES = (int) Math.ceil((double) productList.size() / ITEMS_PER_PAGE );

//        System.out.println("num pages " + NUM_PAGES);

        Pagination pagination = new Pagination();
        pagination.setPageCount(NUM_PAGES);

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();

//        System.out.println("current tilePane showing for tile " + currentTilePane.getId());
//        System.out.println("current scrollPane showing for tile " + currentScrollPane.getId());
//        System.out.println("prev was selected at index " + selected);

        pagination.setCurrentPageIndex(selected);
        if(selected == 0){
            List<Product> currentList;
            if(productList.size() < ITEMS_PER_PAGE){
                currentList = productList.subList(0,productList.size());
            } else {
                currentList = productList.subList(0,ITEMS_PER_PAGE);
            }
            createGrid(currentList, currentTilePane, currentScrollPane);
        }

        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            int start = newValue.intValue() * ITEMS_PER_PAGE;
            int end = start + ITEMS_PER_PAGE ;
            if(end > productList.size()){
                end = productList.size();
            }
//            System.out.println("from pagination " + newValue.intValue() + " button was "+ (newValue.intValue() +1) +" item from " + start + " end " + end);
            List<Product> subList = productList.subList(start,end);

            createGrid(subList, currentTilePane, currentScrollPane);

            if(currentScrollPane.getId().equals("scrollPane")){
                paginationAll = newValue.intValue();
            }
            if(currentScrollPane.getId().equals("catScrollPane")){
                paginationCat = newValue.intValue();
            }
            if(currentScrollPane.getId().equals("brandScrollPane")){
               paginationBrand = newValue.intValue();
            }

        });

        toolbar.setCenter(pagination);
    }



}
