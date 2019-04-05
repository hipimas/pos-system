//package controller;
//
//import com.jfoenix.controls.*;
//import configuration.Setting;
//import configuration.StatusBar;
//import controller.cashierCont.CashierController;
//import controller.productsCont.AllProductController;
//import controller.productsCont.LabelChargeController;
//import controller.subCont.DiscountController;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.layout.*;
//import javafx.stage.Stage;
//import org.apache.commons.configuration2.ex.ConfigurationException;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.ResourceBundle;
//
//public class HomeController2 implements Initializable {
//    public BorderPane borderPane;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        //set controller for menu
//        MainMenuController mainMenuController = new MainMenuController();
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//
////        String fileTopFxmlUrl = "/views/menu/topSecondMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "dashboard");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
////        new StatusBar(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), FontAwesomeIcon.HOME);
//
//    }
//
//    public void addProduct(ActionEvent actionEvent) throws ConfigurationException {
//
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl, "addProduct");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIconProduct.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/products/addProduct.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "addProduct");
//
//        Stage stage = new Stage();
//        stage.setTitle("Add Product");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//
//
//        StatusBar.setStatus("Add new product");
//    }
//
//    public void listProductAction(ActionEvent actionEvent) throws ConfigurationException {
//        ProductMenuController productMenuController = new ProductMenuController();
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,productMenuController, mainPane, "allProduct");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIconProduct.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, productMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,productMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/products/listAllProduct.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "allProduct");
//
//        Stage stage = new Stage();
//        stage.setTitle("List Product");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//
////        stage.setOnCloseRequest( event ->
////        {
////            System.out.println("CLOSING");
////
////        });
//    }
//
//    public void discountAction(ActionEvent actionEvent) throws ConfigurationException {
//
//        MainMenuController mainMenuController = new MainMenuController();
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "discount");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/subview/discount.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "discount");
//
//        Stage stage = new Stage();
//        stage.setTitle("Discount");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void brandAction(ActionEvent actionEvent) throws ConfigurationException {
//        MainMenuController mainMenuController = new MainMenuController();
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "brand");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/subview/brand.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "brand");
//
//        Stage stage = new Stage();
//        stage.setTitle("Jenama");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void posAction(ActionEvent actionEvent) throws ConfigurationException {
//        MenuCashierController menuCashierController = new MenuCashierController();
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,menuCashierController, mainPane, "cashier");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIconCashier.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, menuCashierController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,menuCashierController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/cashier/mainCashier.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "cashier");
//
//        //send additional cashierController to menu
////        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/cashier/mainCashier.fxml"));
////        CashierController cc = fxmlLoader.getController();
////        menuCashierController.initData(mainPane, "cashier", cc);
//
//        menuCashierController.initData(mainPane, "cashier");
//
//        Stage stage = new Stage();
//        stage.setTitle("Cashier");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void promotionAction(ActionEvent actionEvent) throws ConfigurationException {
//        MainMenuController mainMenuController = new MainMenuController();
//
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "promotion");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/subview/promotion.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "promotion");
//
//        Stage stage = new Stage();
//        stage.setTitle("Promosi dan Diskaun");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void stockAction(ActionEvent actionEvent) {
//    }
//
//
//    public void salesAnalyticsAction(ActionEvent actionEvent) throws ConfigurationException {
//        MainMenuController mainMenuController = new MainMenuController();
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "salesAnalytics");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/analytics/salesAnalytics.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "salesAnalytics");
//
//        Stage stage = new Stage();
//        stage.setTitle("Sales Analytics");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void cashAnalyticsAction(ActionEvent actionEvent) throws ConfigurationException {
//        MainMenuController mainMenuController = new MainMenuController();
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "cashAnalytics");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/analytics/cashAnalytics.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "cashAnalytics");
//
//        Stage stage = new Stage();
//        stage.setTitle("Cash Analytics");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void categoryAction(ActionEvent actionEvent) throws ConfigurationException {
//        MainMenuController mainMenuController = new MainMenuController();
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "category");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/subview/categories.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "category");
//
//        Stage stage = new Stage();
//        stage.setTitle("Kategori");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void barcodeAction(ActionEvent actionEvent) throws ConfigurationException {
//
//    }
//
//    public void labelChargeAction(ActionEvent actionEvent) throws ConfigurationException {
//        MainMenuController mainMenuController = new MainMenuController();
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,mainMenuController, mainPane, "labelCharge");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIcon.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, mainMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,mainMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/products/labelCharge.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "labelCharge");
//
//        Stage stage = new Stage();
//        stage.setTitle("Label Charge Price");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void customerAction(ActionEvent actionEvent) throws ConfigurationException {
//        CustomerMenuController customerMenuController = new CustomerMenuController();
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,customerMenuController, mainPane, "customer");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIconCustomer.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, customerMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,customerMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/customer/mainCustomer.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "customer");
//
//        Stage stage = new Stage();
//        stage.setTitle("Customers");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    public void statAction(ActionEvent actionEvent) throws ConfigurationException {
//        CustomerMenuController customerMenuController = new CustomerMenuController();
//        //create top using method
//        String fileTopFxmlUrl = "/views/menu/topMenu.fxml";
//        addNewTop(fileTopFxmlUrl,customerMenuController, mainPane, "stats");
//
//        //create navigation using method
//        String fileDrawerFxmlUrl = "/views/menu/drawerOnly.fxml";
//        String fileIconFxmlUrl = "/views/menu/drawerIconCustomer.fxml";
//        addNewNavigation(fileDrawerFxmlUrl,fileIconFxmlUrl, customerMenuController, mainPane);
//
//        //create bottom using method
//        String fileBottomFxmlUrl = "/views/menu/bottomMenu.fxml";
//        addNewBottom(fileBottomFxmlUrl,customerMenuController, mainPane);
//
//        //create center using method
//        String fileCenterFxmlUrl = "/views/subview/stats.fxml";
//        addNewCenter(fileCenterFxmlUrl, mainPane, "stats");
//
//        Stage stage = new Stage();
//        stage.setTitle("Stats");
//        stage.setScene(new Scene(mainPane));
//        stage.show();
//    }
//
//    private void addNewCenter(String fileCenterFxmlUrl, AnchorPane mainPane, String currentPage) {
//        BorderPane bd = new BorderPane();
//        for (Node pane: mainPane.getChildren()){
//            if(pane instanceof BorderPane){
//                bd = (BorderPane) pane;
//            }
//        }
//
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileCenterFxmlUrl));
//        try {
//            AnchorPane anchorPane = fxmlLoader.load();
//            bd.setCenter(anchorPane);
//            if(currentPage.equals("allProduct")){
//                AllProductController allProductController = fxmlLoader.getController();
//                allProductController.initData(mainPane);
//            }
//            if(currentPage.equals("labelCharge")){
//                LabelChargeController labelChargeController = fxmlLoader.getController();
//                labelChargeController.initData(mainPane);
//            }
//            if(currentPage.equals("discount")){
//                DiscountController discountController = fxmlLoader.getController();
//                discountController.initData(mainPane);
//            }
//            if(currentPage.equals("cashier")){
//                CashierController cashierController = fxmlLoader.getController();
//                cashierController.initData(mainPane);
//            }
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    private void addNewTop(String fileTopFxmlUrl, MainMenuController menuController, AnchorPane mainPane, String currentPage) {
//        BorderPane bd = new BorderPane();
//        for (Node pane: mainPane.getChildren()){
//            if(pane instanceof BorderPane){
//                bd = (BorderPane) pane;
//            }
//        }
//
//        FXMLLoader fxmlLoaderTop = new FXMLLoader(getClass().getResource(fileTopFxmlUrl));
//        try {
//            fxmlLoaderTop.setController(menuController);
//            menuController.initData(mainPane, currentPage);
//            AnchorPane pane = fxmlLoaderTop.load();
//
//            bd.setTop(pane);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void addNewNavigation(String fileDrawerFxmlUrl, String fileIconFxmlUrl, MainMenuController mainMenuController, AnchorPane mainPane) {
//        //add navigation
//        FXMLLoader fxmlLoaderDrawer = new FXMLLoader(getClass().getResource(fileDrawerFxmlUrl));
//        FXMLLoader fxmlLoaderIcon = new FXMLLoader(getClass().getResource(fileIconFxmlUrl));
//        try {
//            fxmlLoaderDrawer.setController(mainMenuController);
//            fxmlLoaderIcon.setController(mainMenuController);
//            JFXDrawer leftMenu = fxmlLoaderDrawer.load();
//            HBox box = fxmlLoaderIcon.load();
//            leftMenu.setSidePane(box);
//            leftMenu.prefHeightProperty().bind(mainPane.heightProperty());
////            mainPane.getChildren().add(1,leftMenu);
//            BorderPane borderPane = (BorderPane) mainPane.getChildren().get(0);
//            borderPane.setLeft(leftMenu);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void addNewBottom(String fileBottomFxmlUrl, MainMenuController menuController, AnchorPane mainPane) {
//        BorderPane bd = new BorderPane();
//        for (Node pane: mainPane.getChildren()){
//            if(pane instanceof BorderPane){
//                bd = (BorderPane) pane;
//            }
//        }
//
//        FXMLLoader fxmlLoaderBottom = new FXMLLoader(getClass().getResource(fileBottomFxmlUrl));
//        fxmlLoaderBottom.setController(menuController);
//        try {
//            JFXToolbar bottomToolbar = fxmlLoaderBottom.load();
//            StatusBar.setJfxToolbar(bottomToolbar);
//
//            //add exit button
//            HBox hBoxB = new HBox();
//            JFXButton exitButton = new JFXButton("Exit");
//            exitButton.setId("button-danger");
//            exitButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TIMES, "24"));
//
//            BorderPane finalBd = bd;
//            exitButton.setOnAction(event -> {
//                Stage stage = (Stage) finalBd.getScene().getWindow();
//                stage.close();
//            });
//
//            hBoxB.getChildren().add(exitButton);
////            hBoxB.setAlignment(Pos.CENTER_RIGHT);
//            hBoxB.setPadding(new Insets(5, 5, 5, 5));
//            bottomToolbar.setRightItems(hBoxB);
//
//            bd.setBottom(bottomToolbar);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private BorderPane getMainBorderPane() throws ConfigurationException {
//        BorderPane borderPane = new BorderPane();
//        borderPane.setPrefSize(1024,768);
//        if(Setting.getPathTheme() != null){
//            borderPane.getStylesheets().add(getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm());
//        }
//        StatusBar.setBorderPane(bdPane);
//        return borderPane;
//    }
//
//    public void testingGrid(ActionEvent actionEvent){
//        Parent root = null;
//        try {
//            root = FXMLLoader.load(getClass().getResource("/views/productGrid.fxml"));
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void icon(){
//        Parent root = null;
//        try {
//            root = FXMLLoader.load(getClass().getResource("/views/icon.fxml"));
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
