package controller.menuCont;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXToolbar;
import configuration.Pref;
import configuration.StatusBar;
import controller.cashierCont.CashierController;
import controller.cashierCont.CounterController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CashierNavigationController implements Initializable {

    private BorderPane borderPane;
    private JFXButton btnAdd;

    public JFXButton btnLock;

    public JFXDrawer jfxDrawer;


    public void initData(BorderPane borderPane, JFXButton btnAdd){
        this.borderPane = borderPane;
        this.btnAdd = btnAdd;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(Pref.getStatus() != null){
            if(Pref.getStatus().equals("lock")){
                //current lock the cashier show unlock icon
                MaterialDesignIconView iconViewLockOpen = new MaterialDesignIconView(MaterialDesignIcon.LOCK_OPEN, "25");
                if(btnLock != null){
                    btnLock.setGraphic(iconViewLockOpen);
                }
            }
        }


        btnAdd.setOnAction(event -> {
            borderPane.setLeft(jfxDrawer);
            BorderPane.setAlignment(borderPane.getLeft(), Pos.BOTTOM_CENTER);

            if(jfxDrawer.isOpened()){
                jfxDrawer.close();
                if(jfxDrawer.isClosing()){
                    borderPane.setLeft(null);
                }
            }else {
                jfxDrawer.open();
                System.out.println("open drawer");
            }

            System.out.println("second click asd");
        });
    }


    public void start(ActionEvent actionEvent) {
        if(Pref.getStatus() != null){
            if(Pref.getStatus().equals("lock")){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Counter is Lock! ..Please unlock");
                alert.show();
            } else{
                if(!Pref.getStatus().equals("active")){
                    BorderPane borderPane = showBorderPane("Start Cash", "green");

                    Stage stage = new Stage();
                    stage.setTitle("Start Cash");
                    stage.setScene(new Scene(borderPane));
                    stage.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Counter already active!.. Stop counter and start again");
                    alert.show();
                }
            }
        }

        jfxDrawer.close();
        if(jfxDrawer.isClosing()){
            borderPane.setLeft(null);
        }
    }

    public void addCash(ActionEvent actionEvent){
        if(Pref.getStatus() != null){
            if(Pref.getStatus().equals("lock")){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Counter is Lock! ..Please unlock");
                alert.show();
            } else {
                if(Pref.getStatus().equals("active")){
                    BorderPane borderPane = showBorderPane("Add Cash", "blue");
                    Stage stage = new Stage();
                    stage.setTitle("Add Cash");
                    stage.setScene(new Scene(borderPane));
                    stage.show();
                } else {
                    System.out.println("counter not active");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Counter not active!.. Please start counter");
                    alert.show();
                }
            }
        }

        jfxDrawer.close();
        if(jfxDrawer.isClosing()){
            borderPane.setLeft(null);
        }
    }

    public void drawCash(ActionEvent actionEvent){
        if(Pref.getStatus() != null) {
            if (Pref.getStatus().equals("lock")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Counter is Lock! ..Please unlock");
                alert.show();
            } else {
                if(Pref.getStatus().equals("active")){
                    BorderPane borderPane = showBorderPane("Withdraw Cash", "yellow");
                    Stage stage = new Stage();
                    stage.setTitle("Withdraw Cash");
                    stage.setScene(new Scene(borderPane));
                    stage.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Counter not active!.. Please start counter");
                    alert.show();
                }
            }
        }

        jfxDrawer.close();
        if(jfxDrawer.isClosing()){
            borderPane.setLeft(null);
        }
    }

    public void stopCash(ActionEvent actionEvent){
        if(Pref.getStatus() != null) {
            if (Pref.getStatus().equals("lock")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Counter is Lock! ..Please unlock");
                alert.show();
            } else {
                if(Pref.getStatus().equals("active")){
                    BorderPane borderPane = new BorderPane();

                    String fileContentFxmlUrl = "/views/cashier/counterClose.fxml";
                    FXMLLoader fxmlLoaderCenter = new FXMLLoader(getClass().getResource(fileContentFxmlUrl));
                    try {

                        AnchorPane anchorPane = fxmlLoaderCenter.load();
                        borderPane.setCenter(anchorPane);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Close counter");
                    stage.setScene(new Scene(borderPane));
                    stage.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Counter not active!.. Please start counter");
                    alert.show();
                }
            }
        }

        jfxDrawer.close();
        if(jfxDrawer.isClosing()){
            borderPane.setLeft(null);
        }
    }

    public void lock(ActionEvent actionEvent){
        if(Pref.getStatus().equals("lock")){
            //unlock counter
            Pref.setStatus("unlock");
            CashierController.statusProperty.set("unlock");
            MaterialDesignIconView iconViewLock = new MaterialDesignIconView(MaterialDesignIcon.LOCK, "25");
            btnLock.setGraphic(iconViewLock);
            new StatusBar("Counter unlock", "success", MaterialDesignIcon.LOCK_OPEN_OUTLINE);
        } else{
            //lock the counter
            Pref.setStatus("lock");
            CashierController.statusProperty.set("lock");
            MaterialDesignIconView iconViewLockOpen = new MaterialDesignIconView(MaterialDesignIcon.LOCK_OPEN, "25");
            btnLock.setGraphic(iconViewLockOpen);
            new StatusBar("Counter lock", "warning", MaterialDesignIcon.LOCK_OUTLINE);
        }

        jfxDrawer.close();
        if(jfxDrawer.isClosing()){
            borderPane.setLeft(null);
        }

    }

    private BorderPane showBorderPane(String title, String Color){
        BorderPane borderPane = new BorderPane();

        CounterController counterController = new CounterController();
        counterController.initData(title, Color, borderPane);

        String fileContentFxmlUrl = "/views/cashier/counter.fxml";
        String fileBottomFxmlUrl = "/views/menu/bottomCounterMenu.fxml";
        FXMLLoader fxmlLoaderCenter = new FXMLLoader(getClass().getResource(fileContentFxmlUrl));
        FXMLLoader fxmlLoaderBottom = new FXMLLoader(getClass().getResource(fileBottomFxmlUrl));
        try {

            fxmlLoaderCenter.setController(counterController);
            fxmlLoaderBottom.setController(counterController);

            AnchorPane anchorPane = fxmlLoaderCenter.load();
            JFXToolbar toolbar = fxmlLoaderBottom.load();

            borderPane.setCenter(anchorPane);
            borderPane.setBottom(toolbar);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return borderPane;
    }

}
