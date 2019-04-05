package controller.productsCont;

import com.jfoenix.controls.*;
import com.jfoenix.validation.DoubleValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import configuration.Barcode;
import configuration.PriceMap;
import configuration.StageView;
import configuration.StatusBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.BrandModel;
import model.CategoryModel;
import model.ProductModel;
import validation.ProductBarcodeValidator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddProductController implements Initializable {
    public JFXTextField txtBarcode;
    public JFXTextField txtName;
    public JFXTextArea txtDesc;
    public JFXTextField txtBuy;
    public JFXDatePicker txtDate;
    public JFXComboBox<Brand> comboBrand;
    public JFXButton btnImage;
    public ImageView imageView;
    public JFXTextField txtSell;
    public JFXComboBox<String> comboBuyType;
    public JFXComboBox<String> comboSellType;
    public MenuButton categoryBtn;
    public Label lblCatMsg;
    public JFXCheckBox checkBarcode;
    public VBox secondVBox;
    public JFXTextField txtUnit;

    //validation
    private RequiredFieldValidator requiredFieldValidatorBrand = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorBuy = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorSell = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorUnit = new RequiredFieldValidator();
    private RequiredFieldValidator validatorBarcode = new RequiredFieldValidator();
    private ProductBarcodeValidator validatorProductBarcode = new ProductBarcodeValidator();
    private RequiredFieldValidator validatorName = new RequiredFieldValidator();
    private RequiredFieldValidator validatorDescription = new RequiredFieldValidator();

    private DoubleValidator doubleValidatorBuy = new DoubleValidator();
    private DoubleValidator doubleValidatorSell = new DoubleValidator();
    private DoubleValidator doubleValidatorUnit = new DoubleValidator();

    //set the value
    private String name = null;
    private String barcode = null;
    private String desc = null;

    private int sellPriceType = 0;
    private int buyPriceType = 0;
    private int unitSell = 1;
    private BigDecimal buyAmount = null;
    private BigDecimal sellAmount = null;

    private BrandModel brandModel;
    private CategoryModel categoryModel;
    private ProductModel productModel;
    private FileChooser fileChooser = new FileChooser();
    private File file;
    private Image image;
    private String imageUrl = null;
    private File dest = new File("C:\\Users\\acer\\IdeaProjects\\Image");
//    private File dest;
    private String currentDateTime = null;

    private boolean isCat = false;
    private boolean isAutoBarcode = false;

    //get Map list
    private PriceMap priceMap;
    private Category category;
    private Brand brand;

    private TreeView<Category> tree;

    int i = 0;

    private BorderPane borderPane;

    private ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();
    private ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();
    private ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();


    public void initData(BorderPane borderPane) {
        this.borderPane = borderPane;

        bottomExit(true);
        bottomAllProduct(true);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblCatMsg.setVisible(false);

        priceMap = new PriceMap();
        brandModel = new BrandModel();
        categoryModel = new CategoryModel();
        productModel = new ProductModel();
        checkBarcode.setDisable(true);


        loadData();

//        if(Config.getPathFolderImages()!= null){
//            dest = new File(Config.getPathFolderImages());
//        }

        loadPriceDetail();

        //listener for comboBoxSellType when change the selected and inserted the required field
        comboSellType.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null){
                sellPriceType = priceMap.getKeySell(newval);
            }
        });

        //listener for comboBoxBuyType when change the selected and inserted the required field
        comboBuyType.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null){
                buyPriceType = priceMap.getKeyBuy(newval);
            }
        });

        //set the string name for display combo box and get id
        comboBrand.setConverter(new StringConverter<>() {
            @Override
            public String toString(Brand object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Brand fromString(String string) {
                return comboBrand.getItems().stream().filter(i -> i.getName().equals(string)).findAny().orElse(null);
            }
        });

        checkBarcode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                isAutoBarcode = true;
                Barcode barcode = new Barcode(PRODUCTLIST, category);
                txtBarcode.setText(String.valueOf(barcode.getLastestNumber()));
                txtBarcode.setEditable(false);
            } else{
                isAutoBarcode = false;
                txtBarcode.setEditable(true);
            }
        });

        Platform.runLater(() -> {
           Stage stage = (Stage) comboBrand.getScene().getWindow();
           stage.setOnCloseRequest(event -> {
               clearAll();
           });
        });
    }

    private void createTree() {
        //normal setup without recursive
        TreeItem<Category> root = new TreeItem<>(null);
        root.setExpanded(true);

        for (Category category : CATEGORIESLIST) {
            if(category.getParent() == null){
                TreeItem<Category> mainItem = new TreeItem<>(category);
                root.getChildren().add(mainItem);
                mainItem.setExpanded(true);
                for (Category subCat : category.getCategoryListSubcategories()) {
                    TreeItem<Category> subItem = new TreeItem<>(subCat);
                    mainItem.getChildren().add(subItem);
                }
            }

        }

        tree = new TreeView<>(root);
        tree.setRoot(root);
        tree.setShowRoot(false);
        tree.prefWidthProperty().bind(categoryBtn.widthProperty());

        tree.setCellFactory(new Callback<TreeView<Category>, TreeCell<Category>>() {
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
                            setText(item.getName());
                        }
                    }
                };
            }
        });

        CustomMenuItem customMenuItem = new CustomMenuItem(tree);
        customMenuItem.setHideOnClick(false);
        categoryBtn.getItems().setAll(customMenuItem);

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //to avoid because clear selection
            if(newValue != null){
                if(newValue.isLeaf()){
                    System.out.println(newValue.getValue().getName());
                    customMenuItem.hideOnClickProperty().set(true);
                    categoryBtn.setText(newValue.getValue().getName());
                    category = newValue.getValue();
                    if(isAutoBarcode){
                        Barcode barcode = new Barcode(PRODUCTLIST, category);
                        System.out.println(barcode.getLastestNumber());
                        txtBarcode.setText(String.valueOf(barcode.getLastestNumber()));
                    }
                }else{
                    customMenuItem.hideOnClickProperty().set(false);
                    if(oldValue == null){
                        tree.getSelectionModel().select(0);
                    }else{
                        tree.getSelectionModel().select(oldValue);
                        category = oldValue.getValue();
                        if(isAutoBarcode){
                            Barcode barcode = new Barcode(PRODUCTLIST, category);
                            System.out.println(barcode.getLastestNumber());
                            txtBarcode.setText(String.valueOf(barcode.getLastestNumber()));
                        }
                    }
                }
                isCat = true;
            } else {
                category = null;
            }
        });
    }

    private void loadPriceDetail(){
        //insert data into comboBox
        ObservableMap<Integer, String> itemsSell = FXCollections.observableMap(priceMap.getTypeSellMap());
        comboSellType.getItems().setAll(itemsSell.values());

        ObservableMap<Integer, String> itemsBuy = FXCollections.observableMap(priceMap.getTypeBuyMap());
        comboBuyType.getItems().setAll(itemsBuy.values());
    }

    private void loadData(){
        if(!BRANDLIST.isEmpty()){
            BRANDLIST.clear();
        }
        if(!CATEGORIESLIST.isEmpty()){
            CATEGORIESLIST.clear();
        }
        if(!PRODUCTLIST.isEmpty()){
            PRODUCTLIST.clear();
        }
        Task<ObservableList<Brand>> loadBrand = new Task<>() {
            @Override
            protected ObservableList<Brand> call() throws Exception {
                return brandModel.getBrandsOnly();
            }
        };
        Task<ObservableList<Category>> loadCategory = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getCategories();
            }
        };
        Task<ObservableList<Product>> loadProduct = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                return productModel.getProducts();
            }
        };

        loadBrand.setOnSucceeded(e-> {
            BRANDLIST.addAll(loadBrand.getValue());
            comboBrand.setItems(null);
            comboBrand.setItems(BRANDLIST);
        });

        loadCategory.setOnSucceeded(e -> {
            CATEGORIESLIST.addAll(loadCategory.getValue());
            createTree();
            checkBarcode.setDisable(false);
        });

        loadProduct.setOnSucceeded(e -> {
            PRODUCTLIST.addAll(loadProduct.getValue());
        });

//        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExecutorService exec = Executors.newFixedThreadPool(3);
        exec.submit(loadBrand);
        exec.submit(loadCategory);
        exec.submit(loadProduct);

        exec.shutdown();
    }

    private void addValidation(){
        validatorBarcode.setMessage("Masukkan barcode");
        validatorProductBarcode.setMessage("Barkod sudah ada di dalam data!");
        txtBarcode.getValidators().setAll(validatorBarcode, validatorProductBarcode);

        validatorName.setMessage("Masukkan nama");
        txtName.getValidators().add(validatorName);

        validatorDescription.setMessage("Masukkan deskripsi");
        txtDesc.getValidators().add(validatorDescription);

        doubleValidatorBuy.setMessage("Hanya amaun sahaja");
        txtBuy.getValidators().setAll(doubleValidatorBuy);

        doubleValidatorSell.setMessage("Hanya amaun sahaja");
        txtSell.getValidators().setAll(doubleValidatorSell);

        requiredFieldValidatorBrand.setMessage("Sila pilih");
        comboBrand.getValidators().add(requiredFieldValidatorBrand);

        requiredFieldValidatorBuy.setMessage("Sila pilih");
        comboBuyType.getValidators().add(requiredFieldValidatorBuy);

        requiredFieldValidatorSell.setMessage("Sila pilih");
        comboSellType.getValidators().add(requiredFieldValidatorSell);

        requiredFieldValidatorUnit.setMessage("Masukkan angka");
        doubleValidatorUnit.setMessage("Hanya nilai digit");
        txtUnit.getValidators().setAll(requiredFieldValidatorUnit, doubleValidatorUnit);

    }

    public void saveProductAction(ActionEvent actionEvent) {
        addValidation();
        txtBarcode.validate();
        txtName.validate();
        txtDesc.validate();
        txtBuy.validate();
        txtSell.validate();
        comboBrand.validate();
        comboBuyType.validate();
        comboSellType.validate();
        txtUnit.validate();

        if(!isCat){
            lblCatMsg.setVisible(true);
        }else{
            lblCatMsg.setVisible(false);
        }

        if(validatorBarcode.getHasErrors() || validatorProductBarcode.getHasErrors()
                || validatorName.getHasErrors() || validatorDescription.getHasErrors()
                || requiredFieldValidatorBuy.getHasErrors() || doubleValidatorBuy.getHasErrors()
                || requiredFieldValidatorBrand.getHasErrors()
                || requiredFieldValidatorSell.getHasErrors() || doubleValidatorSell.getHasErrors()
                || !isCat
                || requiredFieldValidatorUnit.getHasErrors() || doubleValidatorUnit.getHasErrors()
        ){
            new StatusBar("Masalah dalam borang, Sila ubah atau kemaskini", "failed", MaterialDesignIcon.ALERT_CIRCLE);
            return;
        }


        name = txtName.getText().trim();
        barcode = txtBarcode.getText().trim();
        desc = txtDesc.getText();
        buyAmount = new BigDecimal(txtBuy.getText().trim());
        sellAmount = new BigDecimal(txtSell.getText().trim());
        brand = comboBrand.getSelectionModel().getSelectedItem();
        unitSell = Integer.parseInt(txtUnit.getText().trim());

        if(file != null){
            String fileExtension = file.getName().substring( file.getName().lastIndexOf(".") + 1);
            imageUrl = dest.toPath().resolve(barcode+ "." +fileExtension).toString();
            saveCopyFile(barcode, fileExtension);
        }

        if(txtDate.getValue() == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            currentDateTime = df.format(date);
        }else{
            try {
                DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String textdate = dateTimeFormatter.format(txtDate.getValue());

                LocalTime s = LocalTime.now();
                String startUserDateString = textdate+" "+s;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date startUserDate = df.parse(startUserDateString);
                currentDateTime = df.format(startUserDate);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        Product product = new Product();
        product.setBarcode(barcode);
        product.setName(name);
        product.setDescription(desc);
        product.setImageUrl(imageUrl);
        product.setDate(currentDateTime);

        product.setBrand(brand);

        Price price = new Price();
        price.setPriceTypeBuy(buyPriceType);
        price.setPriceBuy(buyAmount);
        price.setPriceTypeSell(sellPriceType);
        price.setPriceSell(sellAmount);
        price.setUnitSell(unitSell);

        product.setPrice(price);

        product.setCategory(category);

        ProductModel productModel = new ProductModel();
        productModel.saveProduct(product);
        resetProductAction(actionEvent);
        loadData();

    }

    private void saveCopyFile(String barcode, String fileExtension) {
        try {
            Files.copy(file.toPath(), dest.toPath().resolve(barcode+ "." +fileExtension));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetProductAction(ActionEvent actionEvent) {
        txtBarcode.clear();
        txtName.clear();
        txtDesc.clear();
        txtBuy.clear();
        txtSell.clear();
        txtUnit.clear();
        txtDate.setValue(null);
        imageView.setImage(null);
        comboBrand.getSelectionModel().clearSelection();
        comboBuyType.getSelectionModel().clearSelection();
        comboSellType.getSelectionModel().clearSelection();
        clearValidation();

        lblCatMsg.setVisible(false);
        isCat = false;
        tree.getSelectionModel().clearSelection();
        categoryBtn.setText("Kategori");
        checkBarcode.setSelected(false);
    }

    private void clearValidation(){
        comboBrand.resetValidation();
        comboBuyType.resetValidation();
        comboSellType.resetValidation();
        txtBarcode.resetValidation();
        txtName.resetValidation();
        txtDesc.resetValidation();
        txtBuy.resetValidation();
        txtSell.resetValidation();
        txtUnit.resetValidation();
    }

    public void browseAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnImage.getScene().getWindow();
        //single File selection
        file = fileChooser.showOpenDialog(stage);
        if(file != null){
            //path, prefWidth, prefHeight, preserveRatio, smooth
            image = new Image(file.toURI().toString(), 150,150, true, true);

            imageView.setImage(image);
            System.out.println("image was selected");

            txtName.requestFocus();
        }
    }

    public void addMorePriceAction(ActionEvent actionEvent) {
        secondVBox.getChildren().add(createVBox());
    }

    private HBox createVBox() {
        i++;

        HBox hBox = new HBox(5);
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        //insert data into comboBox
        ObservableMap<Integer, String> itemsSell = FXCollections.observableMap(priceMap.getTypeSellMap());
        comboBox.getItems().setAll(itemsSell.values());
        comboBox.prefWidth(100);
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(comboBox.getValue());
            }
        });

        JFXTextField textField1 = new JFXTextField();
        textField1.setPromptText("Item");
        textField1.setLabelFloat(true);
//        textField1.maxWidth(30);
//        textField1.prefWidth(30);

        JFXTextField textField2 = new JFXTextField();
        textField2.setPromptText("RM");
        textField2.setLabelFloat(true);
//        textField2.maxWidth(30);
//        textField2.prefWidth(30);

        hBox.getChildren().addAll(comboBox, textField1, textField2);
        hBox.setAlignment(Pos.CENTER);
        hBox.setHgrow(comboBox, Priority.ALWAYS);
//        hBox.setHgrow(textField1, Priority.ALWAYS);
//        hBox.setHgrow(textField2, Priority.ALWAYS);
        hBox.setMaxWidth(300);


        return hBox;
    }

    private void clearAll(){
        comboBrand.setItems(null);
        comboBuyType.setItems(null);
        comboSellType.setItems(null);

        BRANDLIST.clear();
        CATEGORIESLIST.clear();
        PRODUCTLIST.clear();
        categoryBtn.getItems().clear();

        StageView.removeMap("addProduct");
    }

    private EventHandler<ActionEvent> eventHandlerExit = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            clearAll();

           Stage stage = (Stage) borderPane.getScene().getWindow();
           stage.close();
        }
    };

    private EventHandler<ActionEvent> eventHandlerAllProduct = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(StageView.getMap("productGrid") != null){
                StageView.getMap("productGrid").requestFocus();
                return;
            }

            String fileFxmlUrl = "/views/products/productGrid.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileFxmlUrl));
            try{

                AnchorPane anchorPane = fxmlLoader.load();
                ProductGridController productGridController = fxmlLoader.getController();
                productGridController.initData(borderPane);
                clearAll();

                borderPane.setCenter(anchorPane);
                StageView.addMap("productGrid", ((Stage) borderPane.getScene().getWindow()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void bottomAllProduct(boolean show) {
        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();

        JFXButton btnAll = new JFXButton("All Product");
        btnAll.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.SHOPPING, "25"));
        btnAll.setId("button-primary");

        btnAll.setOnAction(eventHandlerAllProduct);

        if(toolbar != null){
            if(show){
                toolbar.getLeftItems().clear();
                toolbar.setLeftItems(btnAll);
            } else {
                toolbar.getLeftItems().clear();
            }

        }
    }

    private void bottomExit(boolean show) {

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        JFXButton btnExit = new JFXButton("Exit");
        btnExit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CLOSE, "25"));
        btnExit.setId("button-warning");

        btnExit.setOnAction(eventHandlerExit);

        if(toolbar != null){
            if(show){
                toolbar.getRightItems().clear();
                toolbar.setRightItems(btnExit);
            } else {
                toolbar.getRightItems().clear();
            }

        }
    }

}
