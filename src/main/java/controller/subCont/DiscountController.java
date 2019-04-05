package controller.subCont;

import com.jfoenix.controls.*;
import configuration.StageView;
import connectivity.HibernateUtil;
import configuration.Discount;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import entity.Product;
import entity.Promotion;
import interfaces.ProductInterface;
import interfaces.PromotionInterface;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import model.ProductModel;
import model.PromotionModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscountController implements Initializable , ProductInterface, PromotionInterface {
    public VBox vBoxProduct;
    public JFXComboBox<Product> comboProduct;
    public Label lblBarcode;
    public Label lblName;
    public Label lblPricePurchase;
    public Label lblPriceSell;
    public Label lblQuantity;
    public VBox vBoxPromotion;
    public JFXComboBox<Promotion> comboPromo;
    public Label lblPromoName;
    public Label lblComboItem;
    public Label lblAmountDiscount;
    public Label lblPromoDateStart;
    public Label lblPromoDateEnd;
    public VBox vBoxInfo;
    public JFXToggleButton toggleProduct;
    public JFXToggleButton togglePromotion;
    public Label lblProduct;
    public Label lblPromotion;
    public JFXTreeTableView treeTableCalculate;
    public TreeTableColumn<Discount, String> colQty;
    public TreeTableColumn<Discount, String> colPrice;
    public TreeTableColumn<Discount, String> colSubtotal;
    public TreeTableColumn<Discount, String> colDiscount;
    public TreeTableColumn<Discount, String> colTotal;

    private BorderPane borderPane;

    private ProductModel productModel;
    private PromotionModel promotionModel;

    private boolean setProduct = false;
    private boolean setPromo = false;

    private Product product;
    private Promotion promotion;

    public void initData(BorderPane borderPane) {
        this.borderPane = borderPane;

        bottomExit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productModel = new ProductModel();
        promotionModel = new PromotionModel();

        loadData();

        comboProduct.setItems(PRODUCTLIST);
        //set the string name for display combo box and get id
        comboProduct.setConverter(new StringConverter<>() {
            @Override
            public String toString(Product product) {
                return product == null ? null : product.getName();
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        });

        //listener for comboBox when change the selected
        comboProduct.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null){
                System.out.println("new value" + newval);
                lblBarcode.setText(newval.getBarcode());
                lblName.setText(newval.getName());
                product = newval;
            }
        });


        comboPromo.setItems(PROMOTIONSLIST);
        //set the string name for display combo box and get id
        comboPromo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Promotion promotion) {
                return promotion == null ? null : promotion.getName();
            }

            @Override
            public Promotion fromString(String string) {
                return null;
            }
        });

        //listener for comboBox when change the selected
        comboPromo.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null){
                System.out.println("new value" + newval);
                lblPromoName.setText(newval.getName());
                lblComboItem.setText(newval.getCondition());
                lblPromoDateStart.setText(newval.getStartDate());
                lblPromoDateEnd.setText(newval.getEndDate());

                switch (newval.getPromoType()){
                    case 1:
                        lblAmountDiscount.setText("RM " + newval.getDiscountAmount().toString());
                        break;
                    case 2:
                        lblAmountDiscount.setText(String.valueOf(newval.getDiscountPercent()) + " %");
                        break;
                }
                promotion = newval;
            }
        });

        toggleProduct.selectedProperty().addListener((observable, oldValue, newValue) ->{
            System.out.println("change " + newValue);
            setProduct = newValue;
        });

        togglePromotion.selectedProperty().addListener((observable, oldValue, newValue) ->{
            System.out.println("change " + newValue);
            setPromo = newValue;
        });

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                comboProduct.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        comboProduct.setItems(null);
                        comboPromo.setItems(null);
                        StageView.removeMap("discount");
                    }
                });
            }
        });

    }

    private void loadData() {
        if (!PRODUCTLIST.isEmpty()) {
            PRODUCTLIST.clear();
        }
        PRODUCTLIST.addAll(productModel.getProducts());

        if (!PROMOTIONSLIST.isEmpty()) {
            PROMOTIONSLIST.clear();
        }
        PROMOTIONSLIST.addAll(promotionModel.getPromotion());

    }

    public void calculateAction(ActionEvent actionEvent) {
        if(product != null && promotion != null){
            if(setProduct && setPromo){
//                Discount discount = new Discount(product, promotion);
                System.out.println("calculate");
                lblProduct.setText(product.getName());
                lblPromotion.setText(promotion.getName());

//                setCell();
                refreshList(product, promotion);
            } else {
                lblProduct.setText("Produk \n p");
                lblPromotion.setText("Promosi");
            }
        }
    }

    private void refreshList(Product product, Promotion promotion){

        Discount discountRoot = new Discount();
        int quantitytest = Integer.parseInt(promotion.getCondition());

        Discount[] discount = new Discount[100];
        for (int i = 1; i < 100 ; i++) {
            discount[i] = new Discount(product, promotion, i);
        }
//        Discount discount1 = new Discount(product, promotion, 1);
//        Discount discount2 = new Discount(product, promotion,quantitytest);
//        Discount discount3 = new Discount(product, promotion,quantitytest + 1);
//        Discount discount4 = new Discount(product, promotion,quantitytest * 2);
//        Discount discount5 = new Discount(product, promotion,quantitytest * 3);
//        Discount discount6 = new Discount(product, promotion,quantitytest * 4);
//        Discount discount7 = new Discount(product, promotion,quantitytest * 8);
//        Discount discount8 = new Discount(product, promotion,quantitytest * 10);

        TreeItem<Discount> itemRoot = new TreeItem<>(discountRoot);

        TreeItem[] item = new TreeItem[100];
        for (int i = 1; i <discount.length ; i++) {
            item[i] = new TreeItem<>(discount[i]);
            itemRoot.getChildren().addAll(item[i]);
        }
//        TreeItem<Discount> item1 = new TreeItem<>(discount1);
//        TreeItem<Discount> item2 = new TreeItem<>(discount2);
//        TreeItem<Discount> item3 = new TreeItem<>(discount3);
//        TreeItem<Discount> item4 = new TreeItem<>(discount4);
//        TreeItem<Discount> item5 = new TreeItem<>(discount5);
//        TreeItem<Discount> item6 = new TreeItem<>(discount6);
//        TreeItem<Discount> item7 = new TreeItem<>(discount7);
//        TreeItem<Discount> item8 = new TreeItem<>(discount8);
//        itemRoot.getChildren().addAll(item1, item2, item3, item4, item5, item6, item7, item8);

        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("priceSell"));
        colSubtotal.setCellValueFactory(new TreeItemPropertyValueFactory<>("subTotal"));
        colDiscount.setCellValueFactory(new TreeItemPropertyValueFactory<>("availableDiscount"));
        colTotal.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));


        treeTableCalculate.setRoot(itemRoot);
        treeTableCalculate.setShowRoot(false);
        treeTableCalculate.getColumns().setAll(colQty, colPrice, colSubtotal,colDiscount, colTotal);
    }

    private void setCell(){
        colQty.setStyle("-fx-alignment: CENTER;");
//        colName.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue().getName()));
//        colPrice.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getValue().getTotal())));
//        treeTableView.getColumns().setAll(colQty,colName,colPrice);
//        treeTableView.setFixedCellSize(35.0);
    }

    public void saveAction(ActionEvent actionEvent) {
        if(product != null && promotion != null){
            if(setProduct && setPromo){
//                Discount discount = new Discount(product, promotion);
                System.out.println("can save");
                Session session;
                session = HibernateUtil.getSession();
                session.beginTransaction();
                Product ps = session.get(Product.class,product.getId());

                Promotion pt= new Promotion();
                pt = session.get(Promotion.class, promotion.getId());

                ps.getPromotions().add(pt);

                try{
                    session.update(ps);
                    session.getTransaction().commit();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Berjaya");
                    alert.setHeaderText(null);
                    alert.setContentText("Produk berjaya ditambah dengan promosi");
                    alert.showAndWait();

                }catch (HibernateException r){
                    r.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Peringatan !");
                    alert.setContentText("Masalah dalam megemaskini produk dengan promosi!");

                    alert.showAndWait();
                }
            }
        }
    }

    private void bottomExit() {

        JFXToolbar toolbar = (JFXToolbar) borderPane.getBottom();
        JFXButton btnExit = new JFXButton("Exit");
        btnExit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CLOSE, "25"));
        btnExit.setId("button-warning");

        btnExit.setOnAction(event -> {
            comboProduct.setItems(null);
            comboPromo.setItems(null);

            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();

            StageView.removeMap("discount");
        });

        if(toolbar != null){
            toolbar.setRightItems(btnExit);
        }
    }

}
