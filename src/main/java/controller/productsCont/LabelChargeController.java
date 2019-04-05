package controller.productsCont;

import com.jfoenix.controls.*;
import com.jfoenix.validation.DoubleValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import configuration.*;
import controller.cashierCont.GridCartController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.*;
import interfaces.BrandInterface;
import interfaces.CategoryInterface;
import interfaces.ProductInterface;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import model.BrandModel;
import model.CategoryModel;
import model.ProductModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.krysalis.barcode4j.impl.upcean.EAN13LogicImpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LabelChargeController implements Initializable {
    public Label lblBarcode;
    public Label lblName;
    public Label lblPriceType;
    public Label lblUnitSell;
    public Label lblPriceSell;
    public ImageView imageProduct;
    public ImageView imageBarcode;
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
    public JFXButton btnBackspace;
    public JFXButton btnNum100;
    public JFXButton btnNum50;
    public JFXButton btnNum10;
    public JFXButton btnNumDot;
    public Label lblType;
    public JFXTextField txtTypeUnit;
    public Label lblCalculate;
    public Label lblTotal;
    public JFXTreeView<Category> catTreeView;
    public JFXTreeView<Brand> brandTreeView;
    public JFXTabPane tabPane;
    public AnchorPane currentPane;
    public AnchorPane allPane;
    public AnchorPane catPane;
    public AnchorPane brandPane;
    public JFXTextField txtSearch;
    public JFXButton btnClear;

    private ProductModel productModel;
    private CategoryModel categoryModel;
    private BrandModel brandModel;
    private PriceMap priceMap;

    private Product currentSelectedProduct;

    private RequiredFieldValidator requiredFieldValidatorUnit = new RequiredFieldValidator();
    private DoubleValidator doubleValidatorUnit = new DoubleValidator();

    private int typePrice = 0;
    private BigDecimal priceAmount = new BigDecimal(0.00);
    private BorderPane borderPane;

    private ObservableList<Product> currentCatProductList = null;
    private ObservableList<Product> currentBrandProductList = null;

    public JFXSpinner spinner;

    private ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();
    private ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();
    private ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();

    RotateTransition rotateTransition;

    public void initData(BorderPane borderPane) {
        this.borderPane = borderPane;

        bottomExit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productModel = new ProductModel();
        categoryModel = new CategoryModel();
        brandModel = new BrandModel();
        priceMap = new PriceMap();

        btnClear.setVisible(false);

        loadData();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    rotateTransition.stop();
                    brandTreeView.getRoot().setValue(null);
                    catTreeView.getRoot().setValue(null);

                    StageView.removeMap("labelCharge");
                });
            }
        });

        //filter search
        txtSearch.textProperty().addListener(changeListenerSearch);

    }

    private void loadData(){
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

        rotateTransition = new RotateTransition(Duration.millis(800), StatusBar.getIconView());
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        loadProduct.runningProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                new StatusBar("Loading all product", "run",MaterialDesignIcon.LOADING);
                rotateTransition.play();
            }
        });


        loadProduct.setOnSucceeded(e -> {
            //filter only product prices set by shop
            PRODUCTLIST.addAll(loadProduct.getValue());

            GridView<Product> myGrid = productGridView(PRODUCTLIST);

            myGrid.prefHeightProperty().bind(allPane.heightProperty());
            myGrid.prefWidthProperty().bind(allPane.widthProperty());
            myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            myGrid.setCellHeight(200);
            myGrid.setCellWidth(130);

            allPane.getChildren().add(myGrid);

            rotateTransition.stop();
            StatusBar.getIconView().setRotate(0);
            new StatusBar("Data loaded completed", "success", MaterialDesignIcon.MARKER_CHECK);
        });

        Task<ObservableList<Category>> loadCategory = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getCategories();
            }
        };

        loadCategory.setOnSucceeded(e -> {
            CATEGORIESLIST.addAll(loadCategory.getValue());;
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
                    btnAdd.setOnAction(new ViewClicked(item));

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

    private class ViewClicked implements EventHandler<ActionEvent>{
        private final Product product;

        ViewClicked(Product item) {
            this.product = item;
        }

        @Override
        public void handle(ActionEvent event) {
            if(product != null){
                currentSelectedProduct = product;
                lblBarcode.setText(product.getBarcode());
                lblName.setText(product.getName());
                if(product.getPrice() != null){
                    for (Map.Entry<Integer, String> entry : priceMap.getTypeSellMap().entrySet()) {
                        if(product.getPrice().getPriceTypeSell() == entry.getKey()){
                            lblPriceType.setText(entry.getValue());
                            typePrice = entry.getKey();
                            lblType.setText("Jumlah/ Berat / " + entry.getValue());
                            lblCalculate.setText(": " + String.valueOf(product.getPrice().getUnitSell()) + " " + entry.getValue());
                            lblTotal.setText(": RM "+ product.getPrice().getPriceSell().toString());
                        }
                    }
                    lblUnitSell.setText(String.valueOf(product.getPrice().getUnitSell()));
                    lblPriceSell.setText(product.getPrice().getPriceSell().toString());
                    //set default for the reset
                    imageBarcode.setImage(null);
                    txtTypeUnit.clear();
                    txtTypeUnit.resetValidation();

                    //enable keypad for number and backspace
                    numberButtonListener(true);
                }

                if(!product.getImageUrl().isEmpty() || product.getImageUrl() == null){
                    File fileImage = new File(product.getImageUrl());
                    imageProduct.setImage(new Image(fileImage.toURI().toString()));
                }
            }
        }
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

                            //default value
//                            setText(item.getName());
                        }

                    }
                };
            }
        });

        catTreeView.getSelectionModel().selectedItemProperty().addListener(changeListenerCatTree);
    }

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

    private ChangeListener<TreeItem<Category>> changeListenerCatTree = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends TreeItem<Category>> observable, TreeItem<Category> oldValue, TreeItem<Category> newValue) {
            if (newValue != null) {
                ObservableList<Product> currentCatProductList;
                //check for the parent selection of category tree
                if (newValue.getValue().getParent() == null) {
                    System.out.println("from grid" + newValue.getValue().getName() + " id " + newValue.getValue().getId());
                    currentCatProductList = FXCollections.observableArrayList();

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

    private void numberButtonListener(Boolean keypadActive){

        //backspace action
        btnBackspace.setOnAction(event -> {
            if(keypadActive) {
//                System.out.println("backspace action");
                if (!txtTypeUnit.getText().trim().isEmpty()) {
                    txtTypeUnit.requestFocus();
                    txtTypeUnit.setText(txtTypeUnit.getText().substring(0, txtTypeUnit.getText().length() - 1));
                    txtTypeUnit.end();

                } else {
                    txtTypeUnit.requestFocus();
                }
            }
        });


        //0 number
        btnNum0.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("0"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
                System.out.println("button 0 action");
            }
        });

        //special double 00
        btnNum00.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("00"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
                System.out.println("button 00 action");
            }
        });

        btnNum1.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("1"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 1 action");
            }
        });

        btnNum2.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("2"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 2 action");
            }
        });

        btnNum3.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("3"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 3 action");
            }
        });

        btnNum4.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("4"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 4 action");
            }
        });

        btnNum5.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("5"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 5 action");
            }
        });

        btnNum6.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("6"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 6 action");
            }
        });

        btnNum7.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("7"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 7 action");
            }
        });

        btnNum8.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("8"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 8 action");
            }
        });

        btnNum9.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("9"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 9 action");
            }
        });

        btnNum10.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("10"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 10 action");
            }
        });

        btnNum50.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("50"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 50 action");
            }
        });

        btnNum100.setOnAction(event -> {
            if(keypadActive){
                txtTypeUnit.setText(txtTypeUnit.getText().concat("100"));
                txtTypeUnit.requestFocus();
                txtTypeUnit.end();
//                System.out.println("button 100 action");
            }
        });

    }

    public void generateBarcodeAction(ActionEvent actionEvent) {
        if(currentSelectedProduct != null){
            if(currentSelectedProduct.getBarcode().length() == 13){
                Barcode barcode = new Barcode(currentSelectedProduct.getBarcode());
                BufferedImage symbol = barcode.getBufferedImage();
                if(symbol != null){
                    imageBarcode.setImage(SwingFXUtils.toFXImage(symbol, null));
                    imageBarcode.setPreserveRatio(true);
                }
            } else {
                String currentP = currentSelectedProduct.getBarcode();
                String digitP = String.valueOf(EAN13LogicImpl.calcChecksum(currentP));
                int checkDigit  = (Math.abs(Integer.parseInt(digitP)) % 10);
                System.out.println("current p " + currentP + " produc" + currentSelectedProduct.getName());
                System.out.println("check digit " + digitP);

                requiredFieldValidatorUnit.setMessage("Masukkan angka");
                doubleValidatorUnit.setMessage("Hanya Angka");
                txtTypeUnit.getValidators().setAll(requiredFieldValidatorUnit,doubleValidatorUnit);

                txtTypeUnit.validate();

                if(requiredFieldValidatorUnit.getHasErrors() || doubleValidatorUnit.getHasErrors()){
                    new StatusBar("Kemaskini atau ubah borang", "failed",MaterialDesignIcon.ALERT_CIRCLE);
                    return;
                }

                BigDecimal price = getPriceCalculation(txtTypeUnit.getText(), currentSelectedProduct);

                //make sure the price not more than 999.99
                if(price.compareTo(new BigDecimal(1000.00)) > -1){
                    new Alert(Alert.AlertType.WARNING,"Harga mencapai maksimun,kurangkan unit belian").show();
                    new StatusBar("Max price", "failed",MaterialDesignIcon.ALERT_CIRCLE);
                    lblTotal.setText("");
                    lblCalculate.setText("");
                    imageBarcode.setImage(null);
                    return;
                }

                String h1 = price.toString().replaceAll("\\.", "");
                String priceFormat = String.format("%05d",Integer.parseInt(h1));
                String full  = currentP + digitP + priceFormat;
                System.out.println("full " + full);


                String test = "2000015011203";
                Barcode barcode = new Barcode(full);
                BufferedImage symbol = barcode.getBufferedImage();
                if(symbol != null){
                    imageBarcode.setImage(SwingFXUtils.toFXImage(symbol, null));
                    imageBarcode.setPreserveRatio(true);
                }

            }

        }
    }

    private BigDecimal getPriceCalculation(String unit, Product product) {
        int unitProduct = product.getPrice().getUnitSell();
        BigDecimal chargeMultiply =  new BigDecimal(unit).divide(new BigDecimal(unitProduct)).setScale(2, RoundingMode.CEILING);
        System.out.println("multiply " + chargeMultiply);

        priceAmount = product.getPrice().getPriceSell().multiply(chargeMultiply).setScale(2, RoundingMode.CEILING);

        if(priceAmount.compareTo(new BigDecimal(1000.00)) < 1){
            lblCalculate.setText(": " + chargeMultiply.toString() + " * " + product.getPrice().getPriceSell().toString());
            lblTotal.setText(": RM " + priceAmount);
        }

        return priceAmount;
    }

    public void printAction(ActionEvent actionEvent) {
        try {
            String reportName = "/report/barcodeSetE.jrxml";
            File file = new File(this.getClass().getResource(reportName).getFile());
            JasperDesign jasperDesign = JRXmlLoader.load (file.getAbsolutePath());
            JasperReport jr = JasperCompileManager.compileReport(jasperDesign);


//            Barcode c = new Barcode("123456789123");
            HashMap<String, Object> para = new HashMap<>();
            para.put("stringBarcode", "1234567891231");
            para.put("productName", "apple Iphone-X MAS 64GB");
            para.put("productPrice", "RM 19.99");
            para.put("unit", "250g");
            para.put("currentDate", "OCT-04-18");

            JRBeanCollectionDataSource jcs = new JRBeanCollectionDataSource(Barcode.getItemList());
            JasperPrint jp = JasperFillManager.fillReport(jr, para, jcs);
//            JRBeanCollectionDataSource jcs = new JRBeanCollectionDataSource(Barcode.getItemList());
//            JasperPrint jp = JasperFillManager.fillReport(jr, para,jcs);
            JasperViewer.viewReport(jp, false);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void labelDesignAction(ActionEvent actionEvent) {
        try {
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("/views/barcode/labelDesign.fxml"));
            Scene scene = new Scene(fxmlLoader);

            if (Setting.getPathTheme() == null || !Setting.getPathTheme().isEmpty()) {
                URL url = this.getClass().getResource("/css/" + Setting.getPathTheme());
                scene.getStylesheets().add(url.toExternalForm());
            }

            Stage stage = new Stage();
            stage.setTitle("ABC");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void btnClearAction(ActionEvent actionEvent){
        txtSearch.clear();
    }

    private void bottomExit() {

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        JFXButton btnExit = new JFXButton("Exit");
        btnExit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CLOSE, "25"));
        btnExit.setId("button-warning");

        btnExit.setOnAction(event -> {
            rotateTransition.stop();
            brandTreeView.getRoot().setValue(null);
            catTreeView.getRoot().setValue(null);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();

            StageView.removeMap("labelCharge");
        });

        if(toolbar != null){
            toolbar.setRightItems(btnExit);
        }
    }
}
