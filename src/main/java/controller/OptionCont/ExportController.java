package controller.OptionCont;

import configuration.StatusBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import entity.Product;
import interfaces.ProductInterface;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.ProductModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportController implements Initializable, ProductInterface {
    public AnchorPane currentPane;

    private ProductModel productModel;

    String fileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productModel = new ProductModel();
    }

    public void saveAction(ActionEvent actionEvent) {
        Stage stage = (Stage) currentPane.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            System.out.println(selectedDirectory.getAbsolutePath());
            fileName = selectedDirectory.getAbsolutePath();
//            lblPath.setText(selectedDirectory.getAbsolutePath());
//            pathImage = selectedDirectory.getAbsolutePath();
        }

        if(!PRODUCTLIST.isEmpty()){
            PRODUCTLIST.clear();
        }

        Task<ObservableList<Product>> loadProduct = new Task<ObservableList<Product>>() {
            @Override
            protected ObservableList<Product> call() throws Exception {
                return productModel.getProducts();
            }
        };

        loadProduct.setOnRunning(event -> {
            new StatusBar("Progress collecting data...", "success", MaterialDesignIcon.LOADING);
        });

        loadProduct.setOnSucceeded(event -> {
            PRODUCTLIST.addAll(loadProduct.getValue());
            new StatusBar("Saving data into file...", "failed",MaterialDesignIcon.DOWNLOAD);
            writeFile();
        });

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(loadProduct);

        service.shutdown();



    }

    private void writeFile(){

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "Product List");

        Row headerRow = sheet.createRow(0);
        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("ID");
        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("Barcode");
        Cell cell3 = headerRow.createCell(2);
        cell3.setCellValue("Name");
        Cell cell4 = headerRow.createCell(3);
        cell4.setCellValue("Description");

        int rownum;
        Row row = null;
        Cell cell = null;

        for (rownum = 1; rownum <= PRODUCTLIST.size(); rownum++) {
            row = sheet.createRow(rownum);
            cell = row.createCell(0);
            cell.setCellValue(PRODUCTLIST.get(rownum - 1).getId());
            cell = row.createCell(1);
            cell.setCellValue(PRODUCTLIST.get(rownum - 1).getBarcode());
            cell = row.createCell(2);
            cell.setCellValue(PRODUCTLIST.get(rownum - 1).getName());
            cell = row.createCell(3);
            cell.setCellValue(PRODUCTLIST.get(rownum - 1).getDescription());
        }

        sheet.setColumnWidth(0,3000);
        sheet.setColumnWidth(1,5000);
//        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName+"/xssf_example.xlsx");
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            new StatusBar("File a successfully created", "success", MaterialDesignIcon.DOWNLOAD);
        } catch (IOException e) {
            new StatusBar("File created failed !." + e.getMessage(), "failed",MaterialDesignIcon.ALERT_CIRCLE);
            e.printStackTrace();
        }

    }
}
