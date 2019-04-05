package interfaces;

import entity.Brand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface BrandInterface {
    public ObservableList<Brand> BRANDLIST = FXCollections.observableArrayList();
}
