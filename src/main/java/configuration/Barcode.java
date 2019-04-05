package configuration;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import entity.Category;
import entity.Product;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.impl.upcean.UPCELogicImpl;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class Barcode {
    private String stringBarcode;
    private String UPCE;
    private String UPCA;
    private String runningNumber;
    private ObservableList<Product> ListProduct;
    private Category category;
    private BufferedImage bufferedImage;
    private int lastestNumber = 0;

    public Barcode(String barcodeString){
        this.stringBarcode = barcodeString;
        barcodeBufferedImage();
    }

    public Barcode(ObservableList<Product> products, Category category){
        this.category = category;
        this.ListProduct = products;
        createRunningNumber();
        createUPCE();
        getLatestNumber();
    }

    public String getUPCE() {
        return UPCE;
    }

    public void setUPCE(String UPCE) {
        this.UPCE = UPCE;
    }

    public String getUPCA() {
        return UPCA;
    }

    public void setUPCA(String UPCA) {
        this.UPCA = UPCA;
    }

    public String getRunnningNumber() {
        return runningNumber;
    }

    public void setRunnningNumber(String runnningNumber) {
        this.runningNumber = runnningNumber;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getStringBarcode() {
        return stringBarcode;
    }

    public void setStringBarcode(String stringBarcode) {
        this.stringBarcode = stringBarcode;
    }

    public int getLastestNumber() {
        return lastestNumber;
    }

    public void setLastestNumber(int lastestNumber) {
        this.lastestNumber = lastestNumber;
    }

    private void createRunningNumber() {
        List<Integer> listBarcodeInteger = new ArrayList<>();
        int lastAdd = 1;

        for(Product p : ListProduct){
            if(p.getBarcode().length() == 8){
                listBarcodeInteger.add(Integer.valueOf(p.getBarcode().substring(3,7)));
            }
        }
        //get the latest and higher number from array
        if(!listBarcodeInteger.isEmpty()){
            lastAdd = Collections.max(listBarcodeInteger) + 1;
        }

        this.runningNumber = String.format("%04d", lastAdd);
    }

    private void createUPCE() {
        //based the actual EAN13
        //        0 - 2
        //        1 - 3
        //        2 - 4
        //        3 - 5
        //        4 - 6
        //        5 - 7
        //        6 - 8
        //        7 - 9
        //        8 - 0
        //        9 - 1
        String full = String.format("%03d",category.getId()) + runningNumber;
        String last = String.valueOf(UPCELogicImpl.calcChecksum(full));
        int h = Integer.parseInt(last) + 2;
        int checkDigit  = (Math.abs(h) % 10);

        this.UPCE  = full+checkDigit;
    }

    private void barcodeBufferedImage(){
        BufferedImage symbol = null;
        if(stringBarcode.length() == 13 || stringBarcode.length() == 12){
            //ean
            try {
                System.out.println("ean 13" + stringBarcode.length());
                EAN13Bean code = new EAN13Bean();
                code.setQuietZone(5.0);
                code.doQuietZone(true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                code.generateBarcode(canvas, stringBarcode);
                canvas.finish();

                symbol = canvas.getBufferedImage();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.WARNING, "Masalah dalam memaparkan barkod").show();
                new StatusBar(e.getMessage(), "failed", MaterialDesignIcon.ALERT_CIRCLE);
            }
        } else if (stringBarcode.length() == 8){
            // upce
            try {
                UPCEBean code = new UPCEBean();
                code.setQuietZone(10.0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                code.generateBarcode(canvas, stringBarcode);
                canvas.finish();

                symbol = canvas.getBufferedImage();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.WARNING, "Masalah dalam memaparkan barkod").show();
                new StatusBar(e.getMessage(), "failed",MaterialDesignIcon.ALERT_CIRCLE);
            }
        }

        this.bufferedImage = symbol;
    }


    public static Collection<Barcode> getItemList() {
        Barcode h = new Barcode("1234567891231");
        return Arrays.asList( h );
    }

    private void getLatestNumber() {
        List<Integer> listBarcodeInteger = new ArrayList<>();
        int lastAdd = 1;

        for(Product p : ListProduct){
            //check barcode start only 20
            if(p.getBarcode().length() == 6 && p.getBarcode().substring(0,2).matches("20")){
                listBarcodeInteger.add(Integer.valueOf(p.getBarcode()));
            }
        }
        //get the latest and higher number from array
        if(!listBarcodeInteger.isEmpty()){
            lastAdd = Collections.max(listBarcodeInteger) + 1;
        }

        lastestNumber = lastAdd;

//        this.runningNumber = String.format("%04d", lastAdd);
    }
}
