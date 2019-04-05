package controller.productsCont;

import com.jfoenix.controls.*;
import com.jfoenix.validation.DoubleValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import configuration.PriceMap;
import configuration.Setting;
import configuration.StageView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entity.*;
import interfaces.BrandInterface;
import interfaces.CategoryInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProductController implements Initializable, BrandInterface, CategoryInterface {
    public JFXTextField txtBarcode;
    public JFXTextField txtName;
    public JFXTextArea txtDesc;
    public JFXTextField txtBuy;
    public JFXDatePicker txtDate;
    public JFXComboBox<Brand> comboBrand;
    public JFXButton btnImage;
    public ImageView imageView;
    public JFXComboBox<String> comboBuyType;
    public JFXComboBox<String> comboSellType;
    public JFXTextField txtSell;
    public MenuButton categoryBtn;
    public Label lblCatMsg;
    public JFXTextField txtUnit;

    //validation
    private RequiredFieldValidator requiredFieldValidatorBrand = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorBuy = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorSell = new RequiredFieldValidator();
    private RequiredFieldValidator requiredFieldValidatorUnitSell = new RequiredFieldValidator();
    private RequiredFieldValidator validatorBarcode = new RequiredFieldValidator();
    private ProductBarcodeValidator validatorProductBarcode = new ProductBarcodeValidator();
    private RequiredFieldValidator validatorName = new RequiredFieldValidator();
    private RequiredFieldValidator validatorDescription = new RequiredFieldValidator();

    private DoubleValidator doubleValidatorBuy = new DoubleValidator();
    private DoubleValidator doubleValidatorSell = new DoubleValidator();
    private DoubleValidator doubleValidatorUnitSell = new DoubleValidator();


    //set the value
    private String name;
    private String barcode;
    private String desc;

    private int sellPriceType = 0;
    private int buyPriceType = 0;
    private int unitSell;
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
    private String currentDateTime = null;
    private boolean isImageChange = false;

    private Product product = new Product();
    private PriceMap priceMap = new PriceMap();

    private TreeItem<Category> root = new TreeItem<>();
    private TreeView<Category> tree = new TreeView<>();
    private Category currentCat = new Category();
    private Brand currentBrand = new Brand();
    private BorderPane borderPane;
    private AnchorPane previousPane;

    public void initData(Product product, BorderPane borderPane, AnchorPane previousPane){
        this.product = product;
        this.borderPane = borderPane;
        this.previousPane = previousPane;

        txtBarcode.setText(product.getBarcode());
        txtName.setText(product.getName());
        txtDesc.setText(product.getDescription());

        loadData();

        this.currentCat = product.getCategory();
        this.currentBrand = product.getBrand();

        //additional product prices in different table
        if(product.getPrice() != null){
            if(product.getPrice().getPriceTypeBuy() != 0 && product.getPrice().getPriceBuy() != null){
                txtBuy.setText(product.getPrice().getPriceBuy().toString());
                comboBuyType.setValue(priceMap.getValueBuy(product.getPrice().getPriceTypeSell()));
            }

            if(product.getPrice().getPriceTypeSell() != 0 && product.getPrice().getPriceSell() != null){
                txtSell.setText(product.getPrice().getPriceSell().toString());
                comboSellType.setValue(priceMap.getValueSell(product.getPrice().getPriceTypeSell()));
            }

            if(product.getPrice().getUnitSell() != 0){
                txtUnit.setText(String.valueOf(product.getPrice().getUnitSell()));
                unitSell = product.getPrice().getUnitSell();
            }
        }

        //set image if present
        imageUrl = product.getImageUrl();
        if(imageUrl == null || imageUrl.trim().isEmpty()){
            imageView.setImage(new Image(getClass().getResource("/assets/noimages.png").toExternalForm(), 170, 150, true, false));
        }else{
            File fileImage = new File(product.getImageUrl());
            imageView.setImage(new Image(fileImage.toURI().toString(),170, 150, true, false));
        }

        //set date
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateUrl = product.getDate();
        LocalDate dt = LocalDate.parse(dateUrl, dateTimeFormatter);
        txtDate.setValue(dt);

        addBackButton();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        brandModel = new BrandModel();
        categoryModel = new CategoryModel();
        productModel = new ProductModel();
        lblCatMsg.setVisible(false);
//        loadData();

        if(Setting.getPathFolderImages()!= null){
            dest = new File(Setting.getPathFolderImages());
        }

        //insert data into comboBox
        ObservableMap<Integer, String> itemsSell = FXCollections.observableMap(priceMap.getTypeSellMap());
        comboSellType.getItems().setAll(itemsSell.values());

        ObservableMap<Integer, String> itemsBuy = FXCollections.observableMap(priceMap.getTypeBuyMap());
        comboBuyType.getItems().setAll(itemsBuy.values());

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

        //setting date display
        String pattern = "dd/MM/yyyy";
        txtDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });


    }


    private void addBackButton() {
        //add additional back button to layout
        JFXButton backButton = new JFXButton("Back");
        backButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT, "24"));
        backButton.setId("button-success");

        backButton.setOnAction(e -> {
            borderPane.setCenter(previousPane);
            System.out.println(previousPane.getId());


            togglePagination(false);
            backButton.setVisible(false);

            comboBrand.setItems(null);
            comboSellType.setItems(null);
            comboBuyType.setItems(null);
            imageView.setImage(null);
            categoryBtn.getItems().clear();
            System.out.println("from edit page back");
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

    private void loadData(){
        if(!BRANDLIST.isEmpty()){
            BRANDLIST.clear();
        }
        if(!CATEGORIESLIST.isEmpty()){
            CATEGORIESLIST.clear();
        }

        Task<ObservableList<Brand>> taskBrand = new Task<>() {
            @Override
            protected ObservableList<Brand> call() throws Exception {
                return brandModel.getBrandsOnly();
            }

        };
        taskBrand.setOnSucceeded(e -> {
            BRANDLIST.addAll(taskBrand.getValue());
            comboBrand.setItems(BRANDLIST);

            if(product.getBrand() != null){
                for (Brand brand: BRANDLIST) {
                    if (brand.getId() == product.getBrand().getId()) {
                        comboBrand.setValue(brand);
                        break;
                    }
                }
            }

        });


        Task<ObservableList<Category>> taskCategory = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getCategories();
            }
        };
        taskCategory.setOnSucceeded(e -> {
            CATEGORIESLIST.addAll(taskCategory.getValue());
            createTree(taskCategory.getValue());
        });

        ExecutorService exec = Executors.newFixedThreadPool(2);
//        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(taskBrand);
        exec.submit(taskCategory);
        exec.shutdown();
    }

    private void createTree(ObservableList<Category> categories){
        //normal setup without recursive
//        root = new TreeItem<>(null);
        root.setExpanded(true);
        for (Category category : categories) {
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
                            if(item.getParent() == null){
                                setEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                    System.out.println("as this " + param.getSelectionModel().getSelectedItem().isExpanded());
                                    if(param.getSelectionModel().getSelectedItem().isExpanded()){
                                        param.getSelectionModel().getSelectedItem().expandedProperty().set(false);
                                    }else{
                                        param.getSelectionModel().getSelectedItem().expandedProperty().set(true);
                                    }
                                });
                            }
                        }
                    }
                };
            }
        });

        CustomMenuItem customMenuItem = new CustomMenuItem(tree);
        customMenuItem.setHideOnClick(false);
        categoryBtn.getItems().setAll(customMenuItem);

        if(product.getCategory() != null){
            System.out.println(product.getCategory().getName());


            /////////////check here /////////////////////////////////

            if(product.getCategory().getParent() == null){
                tree.getSelectionModel().select(0);
                categoryBtn.setText(tree.getSelectionModel().getSelectedItem().getValue().getName());
            } else {
                for(TreeItem<Category> a: root.getChildren()){
                    for(TreeItem<Category> b : a.getChildren()){
                        if(product.getCategory().getId() == b.getValue().getId()){
                            tree.getSelectionModel().select(b);
                            tree.scrollTo(tree.getRow(b));
                            categoryBtn.setText(b.getValue().getName());
                            break;
                        }
                    }
                }
            }
        }

        tree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                customMenuItem.setHideOnClick(false);
                if(newValue.isLeaf()){
                    currentCat = newValue.getValue();
                    categoryBtn.setText(currentCat.getName());
                    customMenuItem.setHideOnClick(true);
                }
            }
        });


    }

    private void addValidation(){
        if(validatorProductBarcode.getHasErrors()){
            txtBarcode.resetValidation();
            validatorProductBarcode = new ProductBarcodeValidator();
        }

        validatorBarcode.setMessage("Masukkan barcode");
        txtBarcode.getValidators().setAll(validatorBarcode);

        //the new barcode must not be same as prev
        if(!product.getBarcode().contains(txtBarcode.getText().trim())){
            //9556001000750
            validatorProductBarcode.setMessage("Barkod sudah ada di dalam data!");
            System.out.println("new barcode " + txtBarcode.getText());
            txtBarcode.getValidators().setAll(validatorProductBarcode);
        }

        validatorName.setMessage("Masukkan nama");
        txtName.getValidators().add(validatorName);

        validatorDescription.setMessage("Masukkan deskripsi");
        txtDesc.getValidators().add(validatorDescription);

        doubleValidatorBuy.setMessage("Hanya amaun sahaja");
        txtBuy.getValidators().setAll(doubleValidatorBuy);

        doubleValidatorSell.setMessage("Hanya amaun sahaja");
        txtSell.getValidators().setAll(doubleValidatorSell);

        requiredFieldValidatorBrand.setMessage("Sila pilih");
        comboBrand.setValidators(requiredFieldValidatorBrand);

        requiredFieldValidatorBuy.setMessage("Sila pilih");
        comboBuyType.setValidators(requiredFieldValidatorBuy);

        requiredFieldValidatorSell.setMessage("Sila pilih");
        comboSellType.setValidators(requiredFieldValidatorSell);

        requiredFieldValidatorUnitSell.setMessage("Masukkangkan unit");
        doubleValidatorUnitSell.setMessage("Hanya angka");
        txtUnit.getValidators().setAll(requiredFieldValidatorUnitSell, doubleValidatorUnitSell);

    }

    public void saveProductAction(ActionEvent actionEvent) {

        addValidation();
        txtBarcode.validate();
        txtName.validate();
        txtDesc.validate();
        txtBuy.validate();
        txtSell.validate();
        txtUnit.validate();
        comboBrand.validate();
        comboBuyType.validate();
        comboSellType.validate();


        //validation for empty fields
        if(validatorBarcode.getHasErrors() || validatorProductBarcode.getHasErrors()
                || validatorName.getHasErrors() || validatorDescription.getHasErrors()
                || requiredFieldValidatorBuy.getHasErrors() || doubleValidatorBuy.getHasErrors()
                || requiredFieldValidatorBrand.getHasErrors()
                || requiredFieldValidatorSell.getHasErrors() || doubleValidatorSell.getHasErrors()){
            return;
        }

        name = txtName.getText().trim();
        barcode = txtBarcode.getText().trim();
        desc = txtDesc.getText();
        buyAmount = new BigDecimal(txtBuy.getText());
        sellAmount = new BigDecimal(txtSell.getText());
        currentBrand = comboBrand.getSelectionModel().getSelectedItem();
        unitSell = Integer.parseInt(txtUnit.getText());

        //images was change
        if(isImageChange){
            //images change and barcode not change

            //check if previous image null{
            if( product.getImageUrl() == null){
                String fileExtension = file.getName().substring( file.getName().lastIndexOf(".") + 1);
                imageUrl = dest.toPath().resolve(barcode+ "." +fileExtension).toString();
                saveCopyFile(barcode, fileExtension);
            } else {
                File ff = new File(product.getImageUrl());
                //delete the current images
                boolean delete = ff.delete();

                if(file != null){
                    String fileExtension = file.getName().substring( file.getName().lastIndexOf(".") + 1);
                    imageUrl = dest.toPath().resolve(barcode+ "." +fileExtension).toString();
                    saveCopyFile(barcode, fileExtension);
                }
            }

        }

        //barcode change and images not change.
        if(!product.getBarcode().equals(barcode) && !isImageChange){
//            File dest = new File("C:\\Users\\acer\\IdeaProjects\\Image\\");
            File ff = new File(product.getImageUrl());
            String fileExtension = ff.getName().substring( ff.getName().lastIndexOf(".") + 1);
            imageUrl = dest.toPath().resolve(barcode+ "." +fileExtension).toString();

            try {
                Files.copy(ff.toPath(), dest.toPath().resolve(barcode+ "." +fileExtension));
                //delete the current images
                boolean delete = ff.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        Product editProduct = new Product();
        editProduct.setBarcode(barcode);
        editProduct.setName(name);
        editProduct.setDescription(desc);
        editProduct.setDate(currentDateTime);
        editProduct.setImageUrl(imageUrl);
        editProduct.setBrand(currentBrand);
        editProduct.setCategory(currentCat);

        Price price = new Price();
        price.setPriceTypeBuy(buyPriceType);
        price.setPriceBuy(buyAmount);
        price.setPriceTypeSell(sellPriceType);
        price.setPriceSell(sellAmount);
        price.setUnitSell(unitSell);
        editProduct.setPrice(price);

        productModel.updateProduct(editProduct, product);
        resetProductAction(actionEvent);

        comboBuyType.setItems(null);
        comboSellType.setItems(null);
        comboBrand.setItems(null);
        categoryBtn.getItems().clear();

//        borderPane.setCenter(mainPane);
        Stage stage = (Stage) txtBarcode.getScene().getWindow();
        stage.close();

        StageView.removeMap("productGrid");
    }

    private void saveCopyFile(String barcode, String fileExtension) {
//        File dest = new File("C:\\Users\\acer\\IdeaProjects\\Image");
        try {
            Files.copy(file.toPath(), dest.toPath().resolve(barcode+ "." +fileExtension));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetProductAction(ActionEvent actionEvent) {
        System.out.println("clear field and validation");
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
    }

    private void clearValidation(){
        comboBrand.resetValidation();
        comboSellType.resetValidation();
        comboBuyType.resetValidation();
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
            isImageChange = true;

            txtName.requestFocus();
        }
    }

    public void resetAction(ActionEvent actionEvent) {
    }
}
