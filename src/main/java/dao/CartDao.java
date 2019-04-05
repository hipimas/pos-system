package dao;

import entity.CartTransaction;
import javafx.collections.ObservableList;

public interface CartDao {
    ObservableList<CartTransaction> getCashByDate(String dateStart, String dateEnd);
    void saveCart(CartTransaction cartTrans);
}
