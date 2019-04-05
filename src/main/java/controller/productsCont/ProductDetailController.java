package controller.productsCont;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToolbar;
import configuration.Setting;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import configuration.Barcode;
import entity.PriceAdditional;
import configuration.PriceMap;
import entity.Product;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductDetailController implements Initializable {
    public AnchorPane currentPane;
    public ImageView imageBarcode;
    public ImageView imageProduct;
    public Label pBarcode;
    public Label pName;
    public Label pDescription;
    public Label pSell;
    public Label pBrand;
    public ColumnConstraints grid01;
    public VBox vBoxImage;
    public Label pBuy;
    public Label pSellType;
    public Label pMainCategory;
    public Label pSubCategory;
    public Label pBuyType;
    public Label pUnitSell;
    public VBox additionalVBox;

    private PriceMap priceMap = new PriceMap();
    private Product product;
    private BorderPane borderPane;
    private AnchorPane previousPane;

    public void initData(Product product, BorderPane borderPane, AnchorPane previousPane) {
        this.product = product;
        this.borderPane = borderPane;
        this.previousPane = previousPane;

        pBarcode.setText(product.getBarcode());
        pBarcode.setWrapText(true);
        pName.setText(product.getName());
        pName.setWrapText(true);
        pDescription.setText(product.getDescription());
        pDescription.setWrapText(true);
        pBrand.setText(product.getBrand().getName());

//        System.out.println("test additiona prices" + product.getPrice().getPriceAdditional());
//        System.out.println(priceMap.getTypeBuyMap().entrySet());
//        System.out.println("test " + priceMap.getTypeBuyMap());

        if(product.getCategory() != null){
            if(product.getCategory().getParent() != null){
                pMainCategory.setText(product.getCategory().getParent().getName());
                pSubCategory.setText(product.getCategory().getName());
            }else{
                pMainCategory.setText(product.getCategory().getName());
                pSubCategory.setText("-");
            }
        }

        if(product.getPrice() != null){
            if(product.getPrice().getPriceTypeBuy() != 0){
                pBuy.setText(product.getPrice().getPriceBuy().toString());
                for (Map.Entry<Integer, String> entry : priceMap.getTypeBuyMap().entrySet()) {
                    if(product.getPrice().getPriceTypeBuy() == entry.getKey()){
                        pBuyType.setText(entry.getValue());
                    }
                }
            }

            if(product.getPrice().getPriceTypeSell() != 0){
                pSell.setText(product.getPrice().getPriceSell().toString());
                for (Map.Entry<Integer, String> entry : priceMap.getTypeSellMap().entrySet()) {
                    if(product.getPrice().getPriceTypeSell() == entry.getKey()){
                        pSellType.setText(entry.getValue());
                    }
                }
            }

            if(product.getPrice().getUnitSell() != 0){
                pUnitSell.setText(String.valueOf(product.getPrice().getUnitSell()));
            }

            if(product.getPrice().getPriceAdditional() != null){
                Label label = new Label("Additional Price");
                additionalVBox.getChildren().add(label);

                for(PriceAdditional p : product.getPrice().getPriceAdditional()){


                    HBox hBox = new HBox(5);
                    Label label1 = new Label("Jenis : " + priceMap.getValueSell(p.getPriceTypeSell()));
                    Label label2 = new Label("Unit :" + String.valueOf(p.getUnitSell()));
                    Label label3 = new Label("Harga : " + p.getPriceSell().toString());
                    hBox.getChildren().addAll(label1, label2, label3);

                    additionalVBox.getChildren().add(hBox);
                }

            }

        }

        String imageUrl = product.getImageUrl();
        if(imageUrl == null || imageUrl.trim().isEmpty()){
            imageProduct.setImage(new Image(getClass().getResource("/assets/noimages.png").toExternalForm(), 250, 250, true, true));
        }else{
            File fileImage = new File(product.getImageUrl());
            if(fileImage.isFile() && fileImage.exists()){
                //have string but file not found
                imageProduct.setImage(new Image(fileImage.toURI().toString(),250, 250, true, false));
            } else {
                //find the file based on save setting on config file
                try {
                    if(Setting.getPathImagesDefault() != null){
                        //get name of file from save
                        String nameFile = fileImage.getName();
                        File fileImage2 = new File(Setting.getPathImagesDefault() + File.separatorChar + nameFile);
                        imageProduct.setImage(new Image(fileImage2.toURI().toString(),250, 250, true, false));
                    }
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }

//        ean = "1234567891234";
        String stringBarcode = product.getBarcode();
        Barcode b = new Barcode(stringBarcode);
        BufferedImage symbol = b.getBufferedImage();
        if(symbol != null){
            imageBarcode.setImage(SwingFXUtils.toFXImage(symbol, null));
            imageBarcode.setFitWidth(250);
            imageBarcode.setPreserveRatio(true);
        }


        addBackButton();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void deleteAction(ActionEvent actionEvent) {
    }

    public void editAction(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/products/editProduct.fxml"));
        AnchorPane pane = null;
        try {
            pane = fxmlLoader.load();
            EditProductController editProductController = fxmlLoader.getController();
            editProductController.initData(product, borderPane, currentPane);
            borderPane.setCenter(pane);

            //no need to provide back button since update in page itself to clear combo box value
//                    addBackButton();
                    //hide pagination
//                    togglePagination(true);
//                    getBackButton();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addBackButton() {
        //add additional back button to layout
        JFXButton backButton = new JFXButton("Back");
        backButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT, "24"));
        backButton.setId("button-success");

        backButton.setOnAction(e -> {
            borderPane.setCenter(previousPane);
            System.out.println(previousPane.getId());
            backButton.setVisible(false);
            System.out.println("from view page back");
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

}

//write to png file
//            FileOutputStream fos = new FileOutputStream("C:\\Users\\MIRITPC\\Desktop\\jas\\New folder\\"+image_name);
//            fos.write(baos.toByteArray());
//            fos.flush();
//            fos.close();