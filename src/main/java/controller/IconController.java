package controller;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import interfaces.ProductInterface;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ProductModel;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.controlsfx.control.cell.ColorGridCell;


import java.net.URL;
import java.util.*;


public class IconController extends GridView<Color> implements Initializable, ProductInterface {
    public AnchorPane currentpane;


    FilteredList<MaterialDesignIcon> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<MaterialDesignIcon> list = FXCollections.observableArrayList();

        Collections.addAll(list, MaterialDesignIcon.values());

        GridView<MaterialDesignIcon> myGrid = new GridView<>();
        for (MaterialDesignIcon aList : list) {
            myGrid.getItems().addAll(aList);
        }

        myGrid.setCellFactory(new Callback<GridView<MaterialDesignIcon>, GridCell<MaterialDesignIcon>>() {
            @Override
            public GridCell<MaterialDesignIcon> call(GridView<MaterialDesignIcon> param) {
                return new MenuItemCell();
            }
        });


        myGrid.prefHeightProperty().bind(currentpane.heightProperty());
        myGrid.prefWidthProperty().bind(currentpane.widthProperty());
        myGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

//        myGrid.setCellHeight(30);
//        myGrid.setCellWidth(100);
        VBox vBox = new VBox(10);
        TextField textField = new TextField();

        vBox.getChildren().addAll(textField, myGrid);
        vBox.prefHeightProperty().bind(currentpane.heightProperty());
        vBox.prefWidthProperty().bind(currentpane.widthProperty());
        vBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        currentpane.getChildren().add(vBox);

        filteredList = new FilteredList<>(list);

//        textField.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if(newValue != null){
//                    String lowerCase = newValue.toLowerCase();
////                    myGrid.getItems().filtered(x -> x.name().contains(lowerCase));
//                    filteredList.setPredicate(x -> {
//                        if(x.name().toLowerCase().contains(lowerCase)){
//                            return true;
//                        }
//                        return false;
//                    });
//
//                }
//            }
//        });

        textField.textProperty().addListener(changeListenerSearch);

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<MaterialDesignIcon> sortedData = new SortedList<>(filteredList);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(myGrid.getItems().sorted().comparatorProperty());


        // 5. Add sorted (and filtered) data to the table.
        myGrid.setItems(sortedData);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) currentpane.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    System.out.println("icon close");
                    textField.textProperty().removeListener(changeListenerSearch);
                });
            }
        });
    }

    private ChangeListener<String> changeListenerSearch = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue != null){
                String lowerCase = newValue.toLowerCase();
//                    myGrid.getItems().filtered(x -> x.name().contains(lowerCase));
                filteredList.setPredicate(x -> {
                    if(x.name().toLowerCase().contains(lowerCase)){
                        return true;
                    }
                    return false;
                });

            }
        }
    };

    private class MenuItemCell extends GridCell<MaterialDesignIcon> {
        @Override
        protected void updateItem(MaterialDesignIcon item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else{
                setText(null);
                VBox vBox = new VBox(5);
                Label label = new Label();
                label.setText(item.name());
                label.setWrapText(true);
                MaterialDesignIconView materialDesignIconView = new MaterialDesignIconView();
                materialDesignIconView.setGlyphName(item.name());
                materialDesignIconView.setSize("20");

                vBox.getChildren().addAll(materialDesignIconView, label);

                setGraphic(vBox);

            }
        }
    }
}