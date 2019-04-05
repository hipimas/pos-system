package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXToolbar;
import configuration.Setting;
import configuration.StageView;
import configuration.StatusBar;
import controller.cashierCont.CashierController;
import controller.menuCont.NavigationController;
import controller.productsCont.AddProductController;
import controller.productsCont.LabelChargeController;
import controller.productsCont.ProductGridController;
import controller.subCont.DiscountController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    public AnchorPane currentPane;
    public BorderPane currentBorderPane;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StageView stageView = new StageView();

        currentBorderPane.setId("backgroundPane");

        NavigationController navigationController = new NavigationController();
        navigationController.initData(currentBorderPane);

        //create top using method
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, currentBorderPane);

        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileBottomFxmlUrl));
            fxmlLoader.setController(navigationController);
            JFXToolbar jfxToolbar = fxmlLoader.load();

            currentBorderPane.setBottom(jfxToolbar);
            //for setting info at bottom
            if(jfxToolbar != null){
                StatusBar.setToolbar(jfxToolbar);
                if(jfxToolbar.getBottom() instanceof VBox){
                    VBox vBox = (VBox) jfxToolbar.getBottom();
                    if(vBox.getChildren().get(1) instanceof HBox){
                        HBox hBox = (HBox) vBox.getChildren().get(1);
                        if(hBox != null){
                            MaterialDesignIconView iconView = (MaterialDesignIconView) hBox.getChildren().get(0);
                            Label label = (Label) hBox.getChildren().get(1);

                            StatusBar.setIconView(iconView);
                            StatusBar.setLabel(label);
                        }

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileDrawerFxmlUrl = "/views/menu/drawerMenu.fxml";
        try {
            FXMLLoader fxmlLoaderDrawer = new FXMLLoader(getClass().getResource(fileDrawerFxmlUrl));
            fxmlLoaderDrawer.setController(navigationController);
            JFXDrawer leftMenu = fxmlLoaderDrawer.load();

            String fileDrawerItemFxmlUrl = "/views/menu/drawerItem.fxml";
            FXMLLoader fxmlLoaderDrawerItem = new FXMLLoader(getClass().getResource(fileDrawerItemFxmlUrl));
            fxmlLoaderDrawerItem.setController(navigationController);
            HBox hBox = fxmlLoaderDrawerItem.load();
            leftMenu.setSidePane(hBox);

            currentBorderPane.setLeft(leftMenu);
            BorderPane.setAlignment(currentBorderPane.getLeft(), Pos.BOTTOM_CENTER);

        } catch (IOException e) {
            e.printStackTrace();
        }

        new StatusBar(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), "success", MaterialDesignIcon.HOME);
    }

    private BorderPane getBorderPane(){
        BorderPane borderPane = new BorderPane();
        borderPane.setId("backgroundPane");
        borderPane.setPrefSize(1024,768);
        try {
            if(Setting.getPathTheme() != null){
                String cssColor = this.getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm();
                String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();

                borderPane.getStylesheets().addAll(cssColor, cssComponent);
                StatusBar.setBorderPane(borderPane);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }


        return borderPane;
    }

    private void addTopMenu(String fileTopFxmlUrl, BorderPane borderPane ) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileTopFxmlUrl));
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            borderPane.setTop(anchorPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addBottomMenu(String fileBottomFxmlUrl, BorderPane borderPane, Boolean buttonExit, String stringPane){
        FXMLLoader fxmlLoader= new FXMLLoader(getClass().getResource(fileBottomFxmlUrl));
        try {
            JFXToolbar jfxToolbar = fxmlLoader.load();
            if(jfxToolbar != null){
                StatusBar.setToolbar(jfxToolbar);
                if(jfxToolbar.getBottom() instanceof VBox){
                    VBox vBox = (VBox) jfxToolbar.getBottom();
                    if(vBox.getChildren().get(1) instanceof HBox){
                        HBox hBox = (HBox) vBox.getChildren().get(1);
                        if(hBox != null){
                            MaterialDesignIconView iconView = (MaterialDesignIconView) hBox.getChildren().get(0);
                            Label label = (Label) hBox.getChildren().get(1);

                            StatusBar.setIconView(iconView);
                            StatusBar.setLabel(label);
                        }

                    }
                }
            }

            if(buttonExit){
                JFXButton btnExit = new JFXButton("Close");
                btnExit.setGraphic(new MaterialDesignIconView(MaterialDesignIcon.CLOSE, "25"));
                btnExit.setId("button-warning");

                btnExit.setOnAction(event -> {
                    Stage stage = (Stage) borderPane.getScene().getWindow();
                    stage.close();

                    StageView.removeMap(stringPane);
                });

                jfxToolbar.setRightItems(btnExit);
            }
            borderPane.setBottom(jfxToolbar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCenter(String fileFxml, BorderPane borderPane, String viewPane){
        FXMLLoader fxmlLoader= new FXMLLoader(getClass().getResource(fileFxml));
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            if(viewPane.equals("grid")){
                ProductGridController productGridController = fxmlLoader.getController();
                productGridController.initData(borderPane);
            }
            if(viewPane.equals("addProduct")){
                AddProductController addProductController = fxmlLoader.getController();
                addProductController.initData(borderPane);
            }
            if(viewPane.equals("labelCharge")){
                LabelChargeController labelChargeController = fxmlLoader.getController();
                labelChargeController.initData(borderPane);
            }
            if(viewPane.equals("pos")){
                CashierController cashierController = fxmlLoader.getController();
                cashierController.initData(borderPane);
            }
            if(viewPane.equals("discount")){
                DiscountController discountController = fxmlLoader.getController();
                discountController.initData(borderPane);
            }
            borderPane.setCenter(anchorPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void addProductAction(ActionEvent actionEvent) {
        if(StageView.getMap("addProduct") != null){
            StageView.getMap("addProduct").requestFocus();
            return;
        }

        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, false, "addProduct");

        //create center
        String fileFxmlUrl = "/views/products/addProduct.fxml";
        addCenter(fileFxmlUrl, borderPane, "addProduct");

        Stage stage = new Stage();
        stage.setTitle("Add new product");
        stage.setScene(new Scene(borderPane));
        stage.show();

        StageView.addMap("addProduct", stage);
    }

    public void allProductAction(ActionEvent actionEvent) {
    }

    public void discountAction(ActionEvent actionEvent) {
        if(StageView.getMap("discount") != null){
            StageView.getMap("discount").requestFocus();
            return;
        }

        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, false, "discount");

        //create center
        String fileFxmlUrl = "/views/subview/discount.fxml";
        addCenter(fileFxmlUrl, borderPane, "discount");

        Stage stage = new Stage();
        stage.setTitle("Discount");
        stage.setScene(new Scene(borderPane));
        stage.show();

        StageView.addMap("discount", stage);
    }

    public void labelChargeAction(ActionEvent actionEvent) {
        if(StageView.getMap("labelCharge") != null){
            StageView.getMap("labelCharge").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, false, "labelCharge");

        //create center
        String fileFxmlUrl = "/views/products/labelCharge.fxml";
        addCenter(fileFxmlUrl, borderPane, "labelCharge");

        Stage stage = new Stage();
        stage.setTitle("Label Charge");
        stage.setScene(new Scene(borderPane));
        stage.show();

        StageView.addMap("labelCharge", stage);
    }

    public void barcodeAction(ActionEvent actionEvent) {
    }

    public void brandAction(ActionEvent actionEvent) {
        if(StageView.getMap("brand") != null){
            StageView.getMap("brand").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "brand");

        //create center
        String fileFxmlUrl = "/views/subview/brand.fxml";
        addCenter(fileFxmlUrl, borderPane, "brand");

        Stage stage = new Stage();
        stage.setTitle("Jenama");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("brand");
        });

        StageView.addMap("brand", stage);
    }

    public void posAction(ActionEvent actionEvent) {
        if(StageView.getMap("pos") != null){
            StageView.getMap("pos").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, false, "pos");

        //create center
        String fileFxmlUrl = "/views/cashier/mainCashier.fxml";
        addCenter(fileFxmlUrl, borderPane, "pos");

        Stage stage = new Stage();
        stage.setTitle("POS");
        stage.setScene(new Scene(borderPane));
        stage.show();

        StageView.addMap("pos", stage);
    }

    public void promotionAction(ActionEvent actionEvent) {
        if(StageView.getMap("promotion") != null){
            StageView.getMap("promotion").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "promotion");

        //create center
        String fileFxmlUrl = "/views/subview/promotion.fxml";
        addCenter(fileFxmlUrl, borderPane, "promotion");

        Stage stage = new Stage();
        stage.setTitle("Promotion");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("promotion");
        });

        StageView.addMap("promotion", stage);
    }

    public void categoryAction(ActionEvent actionEvent) {
        if(StageView.getMap("category") != null){
            StageView.getMap("category").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "category");

        //create center
        String fileFxmlUrl = "/views/subview/categories.fxml";
        addCenter(fileFxmlUrl, borderPane, "category");

        Stage stage = new Stage();
        stage.setTitle("Kategori");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("category");
        });

        StageView.addMap("category", stage);
    }

    public void stockAction(ActionEvent actionEvent) {
    }

    public void customerAction(ActionEvent actionEvent) {
        if(StageView.getMap("customer") != null){
            StageView.getMap("customer").requestFocus();
            return;
        }

        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "customer");

        //create center
        String fileFxmlUrl = "/views/customer/mainCustomer.fxml";
        addCenter(fileFxmlUrl, borderPane, "customer");

        Stage stage = new Stage();
        stage.setTitle("Customers");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("customer");
        });

        StageView.addMap("customer", stage);
    }

    public void productGrid(ActionEvent actionEvent) {
        if(StageView.getMap("productGrid") != null){
            StageView.getMap("productGrid").requestFocus();
            return;
        }

        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, false, "productGrid");

        //create center
        String fileFxmlUrl = "/views/products/productGrid.fxml";
        addCenter(fileFxmlUrl, borderPane, "grid");

        Stage stage = new Stage();
        stage.setTitle("grid");
        stage.setScene(new Scene(borderPane));
        stage.show();

        StageView.addMap("productGrid", stage);
    }

    public void statAction(ActionEvent actionEvent) {
        if(StageView.getMap("stats") != null){
            StageView.getMap("stats").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "stats");

        //create center
        String fileFxmlUrl = "/views/subview/stats.fxml";
        addCenter(fileFxmlUrl, borderPane, "stats");

        Stage stage = new Stage();
        stage.setTitle("Statistic");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("stats");
        });

        StageView.addMap("stats", stage);
    }

    public void icon(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/icon.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void salesAnalyticsAction(ActionEvent actionEvent) {
        if(StageView.getMap("sales") != null){
            StageView.getMap("sales").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "sales");

        //create center
        String fileFxmlUrl = "/views/analytics/salesAnalytics.fxml";
        addCenter(fileFxmlUrl, borderPane, "sales");

        Stage stage = new Stage();
        stage.setTitle("Sales Analytics");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("sales");
        });

        StageView.addMap("sales", stage);
    }

    public void cashAnalyticsAction(ActionEvent actionEvent) {
        if(StageView.getMap("cash") != null){
            StageView.getMap("cash").requestFocus();
            return;
        }
        BorderPane borderPane = getBorderPane();

        //create top
        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
        addTopMenu(fileTopFxmlUrl, borderPane);

        //create bottom
        String fileBottomFxmlUrl = "/views/menu/bottomAll.fxml";
        addBottomMenu(fileBottomFxmlUrl, borderPane, true, "cash");

        //create center
        String fileFxmlUrl = "/views/analytics/cashAnalytics.fxml";
        addCenter(fileFxmlUrl, borderPane, "cash");

        Stage stage = new Stage();
        stage.setTitle("Cash Analytics");
        stage.setScene(new Scene(borderPane));
        stage.show();

        stage.setOnCloseRequest(event -> {
            StageView.removeMap("cash");
        });


        StageView.addMap("cash", stage);
    }
}
