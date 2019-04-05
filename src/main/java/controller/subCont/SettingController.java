package controller.subCont;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import configuration.Setting;
import configuration.Theme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.net.URL;
import java.util.*;

public class SettingController implements Initializable {
    public JFXComboBox comboTheme;
    public JFXTextField lblPath;
    public AnchorPane mainPane;

    private String pathImage;
    private String slugFileName;

    private Map<Integer, Theme> map;

    private BorderPane borderPane;

    public void initData(BorderPane borderPane){
        this.borderPane = borderPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Theme theme = new Theme();
        List<Theme> themeList = theme.getList();
        map = new HashMap<>();
        for (int i = 0; i < themeList.size() ; i++) {
            map.put(i, themeList.get(i));
        }

        //insert data into comboBox
        ObservableMap<Integer, Theme> itemTheme = FXCollections.observableMap(map);
        comboTheme.getItems().setAll(itemTheme.values());

        //set the string name for display combo box and get id
        comboTheme.setConverter(new StringConverter<Theme>() {
            @Override
            public String toString(Theme object) {
                return object == null ? null : object.getName();
            }

            @Override
            public Theme fromString(String string) {
                return null;
            }
        });

        //listener for comboTheme when change the selected and inserted the required field
        comboTheme.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null){
                for(Map.Entry<Integer, Theme> entry : map.entrySet()) {
                    if(newval.equals(entry.getValue())){
                        System.out.println("here key:" + entry.getKey() + " value:" + entry.getValue() + " value detail: " + entry.getValue().getName() + " slugfilename: "+ entry.getValue().getSlugName());
                        slugFileName = entry.getValue().getSlugName();
                    }
                }
            }
        });
    }

    public void saveAction(ActionEvent actionEvent) throws ConfigurationException {
        if(pathImage != null){

        }
        if(slugFileName != null){
            System.out.println(slugFileName);
            Setting.setPathTheme(slugFileName);
            if(Setting.getPathTheme() == null || !Setting.getPathTheme().isEmpty()){
                URL url = this.getClass().getResource("/css/" + Setting.getPathTheme());
                mainPane.getStylesheets().clear();
                mainPane.getStylesheets().add(url.toExternalForm());

                borderPane.getScene().getStylesheets().clear();
                borderPane.getStylesheets().add(url.toExternalForm());
            }

        }
    }

    public void exitAction(ActionEvent actionEvent) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    public void pathBrowse(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory");
        File defaultDirectory = new File("c:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(mainPane.getScene().getWindow());
        if (selectedDirectory != null) {
            lblPath.setText(selectedDirectory.getAbsolutePath());
            pathImage = selectedDirectory.getAbsolutePath();
        }
    }
}
