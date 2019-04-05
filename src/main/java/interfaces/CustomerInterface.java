package interfaces;

import entity.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface CustomerInterface {
    public ObservableList<Customer> CUSTOMERLIST = FXCollections.observableArrayList();
}
