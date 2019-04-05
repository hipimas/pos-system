package controller.menuCont;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXListView;
import configuration.Pref;
import configuration.Setting;
import configuration.Theme;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.controlsfx.control.PopOver;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {

    public JFXDrawer jfxDrawer;
    public JFXButton btnQuit;
    public JFXButton btnSetting;
    public JFXDrawer secondDrawer;

    public JFXButton btnAccount;
    public JFXButton btnTheme;

    private BorderPane borderPane;

    public void initData(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnSetting.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(jfxDrawer.isOpened()){
                    System.out.println("close the drawer");
                    jfxDrawer.close();
                    if(jfxDrawer.isClosing()){
                        jfxDrawer.setVisible(false);
                    }
                } else{
                    System.out.println("open drawer");
                    jfxDrawer.open();
                    jfxDrawer.setVisible(true);
                }
            }
        });

        if(jfxDrawer != null){
            jfxDrawer.setVisible(false);
        }

        if(secondDrawer != null){
            secondDrawer.setVisible(false);
        }

//        btnAccount.setOnAction(event -> {
//
//        });

        PopOver popOver = new PopOver();
        String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();
        popOver.getRoot().getStylesheets().add(cssComponent);

        if(btnTheme != null){
            btnTheme.setOnAction(event -> {

                Theme theme = new Theme();
                List<Theme> themeList = theme.getList();

                JFXListView<Theme> listTheme = new JFXListView<>();
                for (Theme aThemeList : themeList) {
                    listTheme.getItems().add(aThemeList);
                }

                JFXButton btnSave = new JFXButton("Save");
                btnSave.setId("button-primary");

                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(listTheme, btnSave);

                btnSave.setVisible(false);


                listTheme.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Theme>() {
                    @Override
                    public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
                        if(newValue != null){
                            btnSave.setVisible(true);
                            btnSave.setOnAction(new ThemeEvent(newValue));
                        }
                    }
                });


                listTheme.setCellFactory(new Callback<ListView<Theme>, ListCell<Theme>>() {
                    @Override
                    public ListCell<Theme> call(ListView<Theme> param) {
                        return new MenuCell();
                    }
                });


                popOver.setContentNode(vBox);
                popOver.setCloseButtonEnabled(true);
                popOver.setTitle("Theme");
                popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_TOP);
                popOver.setArrowIndent(100);
                popOver.setDetached(true);
                popOver.setAutoHide(true);


                if(popOver.isShowing()){
                    popOver.hide();
                } else {
                    popOver.show(jfxDrawer);
                }


            });
        }


        btnQuit.setOnAction(event -> {
            System.out.println("Quit " + Pref.getStatus());
            if(Pref.getStatus().equals("lock")){
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please unlock the counter!");
                alert.showAndWait();
            }
            else if(Pref.getStatus().equals("active")){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You are now exit, Current counter still active if you proceed the counter need to closed!.. Click ok to continue");
                alert.showAndWait();

                if(alert.getResult() == ButtonType.OK){
                    String fileFxmlUrl = "/views/cashier/counterClose.fxml";
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fileFxmlUrl));
                    AnchorPane anchorPane = null;
                    try {
                        anchorPane = fxmlLoader.load();

                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Close counter");
                    stage.setScene(new Scene(anchorPane));
                    stage.show();
                }
            } else {

                Stage stage = (Stage) borderPane.getScene().getWindow();
                stage.close();

                Platform.exit();
            }

        });

        Platform.runLater(() -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if(Pref.getStatus().equals("active")){
                    System.out.println("cant close");
                    return;
                };
                System.out.println("Quit " + Pref.getStatus());
            });
        });
    }

    private class MenuCell extends ListCell<Theme> {
        @Override
        protected void updateItem(Theme item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else{
                setText(item.getName());
            }
        }
    }

    private class ThemeEvent implements EventHandler<ActionEvent> {
        Theme theme;

        ThemeEvent(Theme theme) {
            this.theme = theme;
        }

        @Override
        public void handle(ActionEvent event) {
            try {
                Setting.setPathTheme(theme.getSlugName());
                if(Setting.getPathTheme() == null || !Setting.getPathTheme().isEmpty()){
                    borderPane.getScene().getStylesheets().clear();

                    String cssColor = this.getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm();
                    String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();

                    borderPane.getStylesheets().setAll(cssColor, cssComponent);
                }
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        }
    }


}
