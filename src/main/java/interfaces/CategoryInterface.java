package interfaces;

import entity.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface CategoryInterface {
    public ObservableList<Category> CATEGORIESLIST = FXCollections.observableArrayList();

}
