package interfaces;

import entity.CartTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface CartInterface {
    public ObservableList<CartTransaction> CARTTRANSACTIONLIST = FXCollections.observableArrayList();
}
