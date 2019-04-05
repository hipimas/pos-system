package controller.subCont;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import configuration.StatusBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.Brand;
import entity.Category;
import entity.Price;
import entity.Product;
import interfaces.BrandInterface;
import interfaces.CategoryInterface;
import interfaces.ProductInterface;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.BrandModel;
import model.CategoryModel;
import model.ProductModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatController implements Initializable, ProductInterface, BrandInterface, CategoryInterface {
    public AnchorPane currentPane;
    public GridPane gridPane;
    public VBox vBoxOption;
    public Label lblTitle;
    public Label lblCatCount;
    public Label lblBrandCount;
    public Label lblProductCount;
    public AnchorPane scrollSecond;
    public VBox vBoxS;
    public AnchorPane importPane;

    private ProductModel productModel;
    private BrandModel brandModel;
    private CategoryModel categoryModel;

    private Boolean isProductExport = false;
    private Boolean isBrandExport = false;
    private Boolean isCatExport = false;
    private Boolean isImportProduct = false;
    private Boolean isImportBrand = false;
    private Boolean isImportCat = false;
    private ArrayList<String> listOptions = new ArrayList<>();

    RotateTransition rt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productModel = new ProductModel();
        brandModel = new BrandModel();
        categoryModel = new CategoryModel();

        gridPane.setVisible(false);
        importPane.setVisible(false);
        loadData();
    }

    private void loadData(){
        if(!PRODUCTLIST.isEmpty()){
            PRODUCTLIST.clear();
        }
        if(!BRANDLIST.isEmpty()){
            BRANDLIST.clear();
        }
        if(!CATEGORIESLIST.isEmpty()){
            CATEGORIESLIST.clear();
        }

        Task<ObservableList<Product>> loadProduct = new Task<>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                return productModel.getProducts();
            }
        };

        loadProduct.setOnSucceeded(e -> {
            PRODUCTLIST.addAll(loadProduct.getValue());
            lblProductCount.setText(String.valueOf(PRODUCTLIST.size()));
        });

        Task<ObservableList<Category>> loadCategory = new Task<>() {
            @Override
            protected ObservableList<Category> call() throws Exception {
                return categoryModel.getAllCategories();
            }
        };

        loadCategory.setOnSucceeded(e -> {
            CATEGORIESLIST.addAll(loadCategory.getValue());
            lblCatCount.setText(String.valueOf(CATEGORIESLIST.size()));
        });

        Task<ObservableList<Brand>> loadBrand = new Task<>() {
            @Override
            protected ObservableList<Brand> call() throws Exception {
                return brandModel.getBrands();
            }
        };

        loadBrand.setOnSucceeded(e -> {
            BRANDLIST.addAll(loadBrand.getValue());
            lblBrandCount.setText(String.valueOf(BRANDLIST.size()));
        });

//        ExecutorService exec = Executors.newSingleThreadExecutor();
        ExecutorService exec = Executors.newFixedThreadPool(3);
        exec.submit(loadProduct);
        exec.submit(loadCategory);
        exec.submit(loadBrand);
        exec.shutdown();


    }

    public void saveAction(ActionEvent actionEvent) {
        if(isProductExport){
            exportProduct();
        }
        if(isBrandExport){
            exportBrand();
        }
        if(isCatExport){
            exportCat();
        }

    }

    private void exportCat() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "Categories List");

        Row headerRow = sheet.createRow(0);
        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("ID");
        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("Name");
        Cell cell3 = headerRow.createCell(2);
        cell3.setCellValue("Slug");
        Cell cell4 = headerRow.createCell(3);
        cell4.setCellValue("Parent");
        Cell cell5 = headerRow.createCell(4);
        cell5.setCellValue("Parent ID");

        int rownum;
        Row row = null;
        Cell cell = null;

        for (rownum = 1; rownum <= CATEGORIESLIST.size(); rownum++) {
            row = sheet.createRow(rownum);
            cell = row.createCell(0);
            cell.setCellValue(CATEGORIESLIST.get(rownum - 1).getId());
            cell = row.createCell(1);
            cell.setCellValue(CATEGORIESLIST.get(rownum - 1).getName());
            cell = row.createCell(2);
            cell.setCellValue(CATEGORIESLIST.get(rownum - 1).getSlug());
            cell = row.createCell(3);
            if(CATEGORIESLIST.get(rownum - 1).getParent() != null){
                cell.setCellValue(CATEGORIESLIST.get(rownum - 1).getParent().getName());
            } else{
                cell.setCellValue("-");
            }
            cell = row.createCell(4);
            if(CATEGORIESLIST.get(rownum - 1).getParent() != null){
                cell.setCellValue(CATEGORIESLIST.get(rownum - 1).getParent().getId());
            } else{
                cell.setCellValue(0);
            }

        }

        sheet.setColumnWidth(0,3000);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.setColumnWidth(4,3000);

        FileChooser fileChooser = new FileChooser();

        //Set extension filter to .xlsx files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        Stage stage = (Stage) lblTitle.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        //If file is not null, write to file using output stream.
        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
                workbook.write(outputStream);
                cancelAction();
                new StatusBar("File successfully created", "success", MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE_OUTLINE);
            }
            catch (IOException e) {
                e.printStackTrace();
                new StatusBar("File failed to created.." + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
            }
        }
    }

    private void exportBrand() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "Brand List");

        Row headerRow = sheet.createRow(0);
        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("ID");
        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("Name");
        Cell cell3 = headerRow.createCell(2);
        cell3.setCellValue("Slug");

        int rownum;
        Row row = null;
        Cell cell = null;

        for (rownum = 1; rownum <= BRANDLIST.size(); rownum++) {
            row = sheet.createRow(rownum);
            cell = row.createCell(0);
            cell.setCellValue(BRANDLIST.get(rownum - 1).getId());
            cell = row.createCell(1);
            cell.setCellValue(BRANDLIST.get(rownum - 1).getName().toUpperCase());
            cell = row.createCell(2);
            cell.setCellValue(BRANDLIST.get(rownum - 1).getSlug());
        }

        sheet.setColumnWidth(0,3000);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        FileChooser fileChooser = new FileChooser();

        //Set extension filter to .xlsx files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        Stage stage = (Stage) lblTitle.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        //If file is not null, write to file using output stream.
        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
                workbook.write(outputStream);
                cancelAction();
                new StatusBar("File successfully created", "success", MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE_OUTLINE);
            }
            catch (IOException e) {
                e.printStackTrace();
                new StatusBar("File failed to created.." + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
            }
        }
    }

    private void exportProduct() {
        Workbook workbook = null;
        if(!listOptions.isEmpty()){
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            workbook.setSheetName(0, "Product List");

            Row headerRow = sheet.createRow(0);
            int j = 0;
            for (int i = 0; i <listOptions.size() ; i++) {
//                System.out.println(listOptions.get(i));
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(listOptions.get(i));
                j++;
            }
            Cell cell5 = headerRow.createCell(j);
            cell5.setCellValue("Brand Name");
            Cell cell6 = headerRow.createCell(j + 1);
            cell6.setCellValue("Category name");

            int rownum;
            Row row = null;
            Cell cell = null;
            for (rownum = 1; rownum <= PRODUCTLIST.size(); rownum++) {
                row = sheet.createRow(rownum);
                if(listOptions.contains("id")){
                    int position = listOptions.indexOf("id");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getId());
                }
                if(listOptions.contains("barcode")){
                    int position = listOptions.indexOf("barcode");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getBarcode());
                }
                if(listOptions.contains("name")){
                    int position = listOptions.indexOf("name");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getName());
                }
                if(listOptions.contains("desc")){
                    int position = listOptions.indexOf("desc");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getDescription());
                }
                if(listOptions.contains("imageURL")){
                    int position = listOptions.indexOf("imageURL");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getImageUrl());
                }
                if(listOptions.contains("priceSell")){
                    int position = listOptions.indexOf("priceSell");
                    cell = row.createCell(position);
                    cell.setCellValue("" + PRODUCTLIST.get(rownum - 1).getPrice().getPriceSell());
                }
                if(listOptions.contains("priceBuy")){
                    int position = listOptions.indexOf("priceBuy");
                    cell = row.createCell(position);
                    cell.setCellValue("" + PRODUCTLIST.get(rownum - 1).getPrice().getPriceBuy());
                }
                if(listOptions.contains("brand")){
                    int position = listOptions.indexOf("brand");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getBrand().getId());

                }
                if(listOptions.contains("cat")){
                    int position = listOptions.indexOf("cat");
                    cell = row.createCell(position);
                    cell.setCellValue(PRODUCTLIST.get(rownum - 1).getCategory().getId());
                }

                cell = row.createCell(j);
                cell.setCellValue(PRODUCTLIST.get(rownum - 1).getBrand().getName());

                cell = row.createCell(j + 1);
                cell.setCellValue(PRODUCTLIST.get(rownum - 1).getCategory().getName());
            }


            for (int i = 0; i <listOptions.size() ; i++) {
                sheet.autoSizeColumn(i);
            }

            sheet.autoSizeColumn(j);
            sheet.autoSizeColumn(j + 1);

        }


        FileChooser fileChooser = new FileChooser();

        //Set extension filter to .xlsx files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        Stage stage = (Stage) lblTitle.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        //If file is not null, write to file using output stream.
        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
                assert workbook != null;
                workbook.write(outputStream);
                cancelAction();
                new StatusBar("File successfully created", "success", MaterialDesignIcon.CHECKBOX_MARKED_CIRCLE_OUTLINE);
            }
            catch (IOException e) {
                e.printStackTrace();
                new StatusBar("File failed to created.." + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
            }
        }
    }

    public void cancelAction() {
        vBoxOption.getChildren().clear();
        allFalse();
        listOptions.clear();
        gridPane.setVisible(false);
    }

    public void productExportAction(ActionEvent actionEvent) {
        allFalse();
        lblTitle.setText("Export product list");

        JFXCheckBox checkBoxID = new JFXCheckBox("ID");
        checkBoxID.setSelected(true);
        checkBoxID.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("id");
            } else {
                listOptions.remove("id");
            }
        });

        JFXCheckBox checkBoxBarcode = new JFXCheckBox("Barcode");
        checkBoxBarcode.setSelected(true);
        checkBoxBarcode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("barcode");
            } else {
                listOptions.remove("barcode");
            }
        });

        JFXCheckBox checkBoxName = new JFXCheckBox("Name");
        checkBoxName.setSelected(true);
        checkBoxName.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("name");
            } else {
                listOptions.remove("name");
            }
        });

        JFXCheckBox checkBoxDesc = new JFXCheckBox("Description");
        checkBoxDesc.setSelected(true);
        checkBoxDesc.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("desc");
            } else {
                listOptions.remove("desc");
            }
        });

        JFXCheckBox checkBoxPriceSell = new JFXCheckBox("Price Sell");
        checkBoxPriceSell.setSelected(true);
        checkBoxPriceSell.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("priceSell");
            } else {
                listOptions.remove("priceSell");
            }
        });

        JFXCheckBox checkBoxPriceBuy = new JFXCheckBox("Price Buy");
        checkBoxPriceBuy.setSelected(true);
        checkBoxPriceBuy.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("priceBuy");
            } else {
                listOptions.remove("priceBuy");
            }
        });

        JFXCheckBox checkBoxBrand = new JFXCheckBox("Brand");
        checkBoxBrand.setSelected(true);
        checkBoxBrand.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("brand");
            } else {
                listOptions.remove("brand");
            }
        });

        JFXCheckBox checkBoxCat = new JFXCheckBox("Category");
        checkBoxCat.setSelected(true);
        checkBoxCat.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                listOptions.add("cat");
            } else {
                listOptions.remove("cat");
            }
        });

        vBoxOption.getChildren().clear();
        HBox hBox1 = new HBox(10);
        hBox1.getChildren().addAll(checkBoxID, checkBoxBarcode, checkBoxName, checkBoxDesc);
        HBox hBox2 = new HBox(10);
        hBox2.getChildren().addAll(checkBoxPriceSell, checkBoxPriceBuy, checkBoxBrand, checkBoxCat);

        vBoxOption.getChildren().addAll(hBox1, hBox2);

        isProductExport = true;
        listOptions.add("id");
        listOptions.add("barcode");
        listOptions.add("name");
        listOptions.add("desc");
        listOptions.add("imageURL");
        listOptions.add("priceSell");
        listOptions.add("priceBuy");
        listOptions.add("brand");
        listOptions.add("cat");
    }

    public void brandExportAction(ActionEvent actionEvent) {
        allFalse();
        isBrandExport = true;
        lblTitle.setText("Export brand list");
    }

    public void catExportAction(ActionEvent actionEvent) {
        allFalse();
        isCatExport = true;
        lblTitle.setText("Export categories list");
    }

    private void allFalse(){
        isProductExport = false;
        isCatExport = false;
        isBrandExport = false;
        vBoxOption.getChildren().clear();
        gridPane.setVisible(true);
    }

    private void importAllFalse(){
        isImportProduct = false;
        isImportBrand = false;
        isImportCat = false;
    }

    public void importProductAction(ActionEvent actionEvent) {
        importAllFalse();
        isImportProduct = true;
        importPane.setVisible(true);

    }

    public void importBrandAction(ActionEvent actionEvent) {
        importAllFalse();
        isImportBrand = true;
        importPane.setVisible(true);
    }

    public void importCatAction(ActionEvent actionEvent) {
        importAllFalse();
        isImportCat = true;
        importPane.setVisible(true);
    }

    public void browseAction(ActionEvent actionEvent) {
        if(isImportProduct){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");

            ProductModel productModel = new ProductModel();

            // Show open file dialog
            File file = fileChooser.showOpenDialog(lblTitle.getScene().getWindow());
            if (file != null) {
                if(!PRODUCTLIST.isEmpty()){
                    for (int i = 0; i <PRODUCTLIST.size() ; i++) {
                        productModel.deleteAllProduct(PRODUCTLIST.get(i));
                    }
                }

                productModel.alterTableID();

                try {
                    FileInputStream fis = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    Row rowNum;
                    for (int i = 1; i <sheet.getLastRowNum() + 1 ; i++) {
                        rowNum = sheet.getRow(i);
//                        int id = (int) rowNum.getCell(0).getNumericCellValue();
                        String barcode = rowNum.getCell(1).getStringCellValue();
                        String name = rowNum.getCell(2).getStringCellValue();
                        String desc = rowNum.getCell(3).getStringCellValue();
                        String imageURL = rowNum.getCell(4).getStringCellValue();
                        BigDecimal priceSell = new BigDecimal(rowNum.getCell(5).getStringCellValue());
                        BigDecimal priceBuy = new BigDecimal(rowNum.getCell(6).getStringCellValue());
                        int brandId = (int) rowNum.getCell(7).getNumericCellValue();
                        int catId = (int) rowNum.getCell(8).getNumericCellValue();

                        Product product = new Product();
                        product.setName(name);
                        product.setBarcode(barcode);
                        product.setDescription(desc);
                        product.setImageUrl(imageURL);

                        Price price = new Price();
                        price.setPriceBuy(priceBuy);
                        price.setPriceSell(priceSell);
                        price.setPriceTypeBuy(4);
                        price.setPriceTypeSell(4);
                        price.setUnitSell(1);

                        Category category = categoryModel.getCategory(catId);
                        Brand brand = brandModel.getBrand(brandId);

                        product.setPrice(price);
                        product.setBrand(brand);
                        product.setCategory(category);

                        productModel.saveProductImport(product);

//                        System.out.println(product);
                    }

                    workbook.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if(isImportBrand){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");

            BrandModel brandModel = new BrandModel();
            // Show open file dialog
            File file = fileChooser.showOpenDialog(lblTitle.getScene().getWindow());
            if (file != null) {
                if(!BRANDLIST.isEmpty()){
                    for (int i = 0; i <BRANDLIST.size() ; i++) {
                        brandModel.deleteAllBrand(BRANDLIST.get(i));
                    }
                }

                brandModel.alterTableID();

                try {
                    FileInputStream fis = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    Row rowNum;
                    for (int i = 1; i <sheet.getLastRowNum() + 1 ; i++) {
                        rowNum = sheet.getRow(i);
//                        int id = (int) rowNum.getCell(0).getNumericCellValue();
                        String name = rowNum.getCell(1).getStringCellValue();
                        String slug = rowNum.getCell(2).getStringCellValue();

                        Brand brand  = new Brand();
                        brand.setName(name);
                        brand.setSlug(slug);

                        System.out.println(brand);
                        brandModel.saveBrandImport(brand);
                    }

                    workbook.close();
                    fis.close();
                    isImportBrand = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if(isImportCat){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");

            CategoryModel categoryModel = new CategoryModel();

            // Show open file dialog
            File file = fileChooser.showOpenDialog(lblTitle.getScene().getWindow());
            if (file != null) {
                JFXSpinner spinner = new JFXSpinner();
                spinner.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

                importPane.getChildren().add(spinner);
                AnchorPane.setLeftAnchor(spinner, importPane.getWidth() / 2);
                AnchorPane.setTopAnchor(spinner, 10.0);

//                new StatusBar("Loading", "success", MaterialDesignIcon.LOADING);
//
//                rt = new RotateTransition(Duration.millis(800), StatusBar.getIconView());
//                rt.setByAngle(360);
//                rt.setCycleCount(Timeline.INDEFINITE);
//                rt.setInterpolator(Interpolator.LINEAR);
//                rt.setNode(StatusBar.getIconView());
//                rt.play();

                categoryModel.removeAllCategoriesProduct();

                if (!CATEGORIESLIST.isEmpty()) {
                    for (int i = 0; i < CATEGORIESLIST.size(); i++) {
                        if (CATEGORIESLIST.get(i).getParent() != null) {
                            categoryModel.deleteAllCategory(CATEGORIESLIST.get(i));
                        }
                    }

                    for (int i = 0; i < CATEGORIESLIST.size(); i++) {
                        if (CATEGORIESLIST.get(i).getParent() == null) {
                            categoryModel.deleteAllCategory(CATEGORIESLIST.get(i));
                        }
                    }
                }

                categoryModel.alterTableID();

                try {
                    FileInputStream fis = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(0);

                    Row rowNum;
                    for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                        rowNum = sheet.getRow(i);
//                        int newid = (int) rowNum.getCell(0).getNumericCellValue();
                        String name = rowNum.getCell(1).getStringCellValue();
                        String slug = rowNum.getCell(2).getStringCellValue();
                        String parent = rowNum.getCell(3).getStringCellValue();
                        int pId = (int) rowNum.getCell(4).getNumericCellValue();

                        Category category = new Category();
                        category.setName(name);
                        category.setSlug(slug);

                        if (pId == 0) {
                            category.setParent(null);
                        } else {
                            Category catParent = categoryModel.getCategory(pId);
                            category.setParent(catParent);
                        }
                        categoryModel.saveCategoryImport(category);
                    }

                    workbook.close();
                    fis.close();
                    isImportCat = false;


//                    rt.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


}
