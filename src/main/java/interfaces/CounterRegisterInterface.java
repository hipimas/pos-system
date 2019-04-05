package interfaces;

import entity.CounterRegister;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface CounterRegisterInterface {
    public ObservableList<CounterRegister> COUNTERREGISTERLIST = FXCollections.observableArrayList();
}
