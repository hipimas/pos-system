package interfaces;

import entity.Promotion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface PromotionInterface {
    public ObservableList<Promotion> PROMOTIONSLIST = FXCollections.observableArrayList();
}
