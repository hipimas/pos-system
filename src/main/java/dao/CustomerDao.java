package dao;

import entity.Customer;
import javafx.collections.ObservableList;

public interface CustomerDao {
    public ObservableList<Customer> getCustomers();
    public Customer getCustomer(int id);
    public boolean checkCustomerByName(String name);
}
