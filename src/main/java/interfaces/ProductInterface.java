package interfaces;

import entity.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public interface ProductInterface {
    public ObservableList<Product> PRODUCTLIST = FXCollections.observableArrayList();
}
