package controller.cashierCont;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import configuration.Setting;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.Brand;
import entity.Category;
import entity.Item;
import entity.Product;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.BrandModel;
import model.CategoryModel;
import model.ProductModel;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GridCartController implements Initializable {

    public JFXTreeTableView<Item> treeTableView;
    public TreeTableColumn<Item, String> colQuantity;
    public TreeTableColumn<Item, String> colBarcode;
    public TreeTableColumn<Item, String> colNameItem;
    public JFXButton btnSave;
    public JFXButton btCancel;
    public JFXTreeView<Category> catTreeView;
    public JFXTabPane tabPane;
    public JFXTreeView<Brand> brandTreeView;
    public AnchorPane allPane;
    public TableView<Product> tableViewList;
    public TableColumn<Product, String> colBarcodeList;
    public TableColumn<Product, String> colNameList;
    public TableColumn<Product, String> colPriceList;
    public TableColumn<Product, Boolean> colActionList;
    public AnchorPane brandPane;
    public AnchorPane catPane;
    public BorderPane currentBorderPane;
    public JFXTextField txtSearch;
    public JFXButton btnClear;

    public List<Item> getCartList(){
        return pList;
    }
    private List<Item> pList;

    private JFXSnackbar snackBar;
    private ProductModel productModel;
    private CategoryModel categoryModel;
    private BrandModel brandModel;

    private ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();
    private ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();
    private ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();

    private JFXSpinner spinner;
    public BorderPane borderPane;

    public void initData(BorderPane borderPane){
        this.borderPane = borderPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        snackBar = new JFXSnackbar(currentBorderPane);
        productModel = new ProductModel();
        categoryModel = new CategoryModel();
        brandModel = new BrandModel();

        pList = new ArrayList<>();

        loadData();

        //filter search
        txtSearch.textProperty().addListener(changeListenerSearch);
    }

    private void loadData() {
        spinner = new JFXSpinner();
        spinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        allPane.getChildren().add(spinner);


        AnchorPane.setLeftAnchor(spinner, allPane.getWidth() / 2);
        AnchorPane.setRightAnchor(spinner, allPane.getWidth() / 2);
        AnchorPane.setTopAnchor(spinner, 50.0);

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

            GridView<Product> myGrid = productGridView(PRODUCTLIST);

            myGrid.prefHeightProperty().bind(allPane.heightProperty());
            myGrid.prefWidthProperty().bind(allPane.widthProperty());
            myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            myGrid.setCellHeight(200);
            myGrid.setCellWidth(130);

            allPane.getChildren().add(myGrid);

            colBarcodeList.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBarcode()));
            colNameList.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
            colPriceList.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice().getPriceSell())));
            colActionList.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue() != null));
            colActionList.setCellFactory(data -> new ActionCellItemList());

            tableViewList.getColumns().setAll(colBarcodeList, colNameList, colPriceList, colActionList);
            tableViewList.setItems(PRODUCTLIST);
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

                    if(item.getImageUrl() == null){
                        img_cover = new ImageView(new Image(getClass().getResource("/assets/noimages.png").toExternalForm(), 100, 100, true, false));
                    }else{
                        File fileImage = new File(item.getImageUrl());
                        if(fileImage.isFile() && fileImage.exists()){
                            //have string but file not found
                            img_cover = new ImageView(new Image(fileImage.toURI().toString(),100, 80, true, false));
                        } else {
                            //find the file based on save setting on config file
                            try {
                                if(Setting.getPathImagesDefault() != null){
                                    //get name of file from save
                                    String nameFile = fileImage.getName();
                                    File fileImage2 = new File(Setting.getPathImagesDefault() + File.separatorChar + nameFile);
                                    img_cover = new ImageView(new Image(fileImage2.toURI().toString(),100, 80, true, false));
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

                    JFXButton btnAdd = new JFXButton("Add");
                    btnAdd.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    btnAdd.setId("button-icon-success");
                    btnAdd.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PLUS, "25"));
                    btnAdd.setOnAction(new AddClicked(item));

                    HBox hBox = new HBox(5);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().addAll(btnAdd);

                    Region region = new Region();
                    region.setMaxWidth(Double.MAX_VALUE);
                    box.setAlignment(Pos.TOP_CENTER);
                    box.getChildren().addAll(img_cover, name, barcode, region, hBox);

                    setGraphic(box);

                }

            }
        }
    }

    private class ActionCellItemList extends TableCell<Product, Boolean> {
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(item == null){
                setGraphic(null);
                setText(null);
            }else {
                Product product = tableViewList.getItems().get(getIndex());

                JFXButton btnAdd = new JFXButton("Add");
                btnAdd.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                btnAdd.setId("button-icon-success");
                btnAdd.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.PLUS, "25"));
                btnAdd.setOnAction(new AddClicked(product));


                HBox hBox = new HBox(btnAdd);
                hBox.setSpacing(5);
                setGraphic(hBox);
                setText(null);
            }
        }
    }

    private class AddClicked implements EventHandler<ActionEvent>{

        private Product product;

        AddClicked(Product product) {
            this.product = product;
        }

        @Override
        public void handle(ActionEvent event) {
            if(product != null){
                Item item = new Item(product, 1);
                boolean addProduct = true;
                for (Item aPList : pList) {
                    if (aPList.getBarcode().equals(item.getBarcode())) {
                        int oldQty = aPList.getQuantity();
                        int newQty = oldQty + 1;
                        aPList.setQuantity(newQty);
                        addProduct = false;
                    }
                }
                if(addProduct){
                    pList.add(item);
                }
                setCellTable();
                snackBar.show("barang di tambah", 1000);
            }
        }
    }

    private ChangeListener<TreeItem<Category>> changeListenerCatTree = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Category>> observable, TreeItem<Category> oldValue, TreeItem<Category> newValue) {
            if (newValue != null) {
                ObservableList<Product> currentCatProductList;
                //check for the parent selection of category tree
                if (newValue.getValue().getParent() == null) {
                    System.out.println("from grid" + newValue.getValue().getName() + " id " + newValue.getValue().getId());
                    currentCatProductList = FXCollections.observableArrayList();
//                    for(Product p : PRODUCTLIST ){
//                        if(p.getCategory() != null && p.getCategory().getParent().getId() == newValue.getValue().getId()){
//                            currentCatProductList.add(p);
//                        }
//                    }


                    System.out.println(newValue.getChildren());
                    if (newValue.getChildren().size() > 0) {
                        for (TreeItem<Category> category : newValue.getChildren()) {
                            for (Product p : PRODUCTLIST) {
                                if (p.getCategory() != null && p.getCategory().getId() == category.getValue().getId()) {
                                    currentCatProductList.add(p);
                                }
                            }
                        }
                    } else {
                        //no child
                        for (Product p : PRODUCTLIST) {
                            if (p.getCategory() != null && p.getCategory().getId() == newValue.getValue().getId()) {
                                currentCatProductList.add(p);
                            }
                        }
                    }

                } else {
                    //check for selection child category from category tree
                    currentCatProductList = FXCollections.observableArrayList();
                    for (Product p : PRODUCTLIST) {
                        if (p.getCategory() != null && p.getCategory().getId() == newValue.getValue().getId()) {
                            currentCatProductList.add(p);
                        }
                    }
                }

                GridView<Product> myGrid = productGridView(currentCatProductList);

                myGrid.prefHeightProperty().bind(catPane.heightProperty());
                myGrid.prefWidthProperty().bind(catPane.widthProperty());
                myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                myGrid.setCellHeight(200);
                myGrid.setCellWidth(130);

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

            myGrid.setCellHeight(200);
            myGrid.setCellWidth(130);

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

                myGrid.setCellHeight(200);
                myGrid.setCellWidth(130);

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

                myGrid.setCellHeight(200);
                myGrid.setCellWidth(130);

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

    private void setCellTable(){
        colQuantity.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getValue().getQuantity())));
        colQuantity.setStyle("-fx-alignment: CENTER;");
        colBarcode.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue().getBarcode()));
        colNameItem.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue().getName()));

        ObservableList<Item> items = FXCollections.observableArrayList(pList);
        TreeItem<Item> root = new RecursiveTreeItem<>(items, RecursiveTreeObject::getChildren);
        treeTableView.getColumns().setAll(colQuantity,colBarcode,colNameItem);
        treeTableView.setFixedCellSize(35.0);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    public void saveAction(ActionEvent actionEvent) {
        PRODUCTLIST.clear();
        CATEGORIESLIST.clear();
        BRANDLIST.clear();

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    public void btCancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        pList.clear();

        PRODUCTLIST.clear();
        CATEGORIESLIST.clear();
        BRANDLIST.clear();

        stage.close();
    }

    public void btnClearAction(ActionEvent actionEvent) {
        txtSearch.clear();
    }


}

