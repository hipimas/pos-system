package dao;

import entity.CashTransaction;
import entity.CounterRegister;
import javafx.collections.ObservableList;

public interface CounterRegisterDao {
    public CounterRegister getCounterRegister(int id);
    public ObservableList<CounterRegister> getCounterRegisters();
    public ObservableList<CounterRegister> getCounterByDate(String dateStart, String dateEnd);
    public void saveCounter(CounterRegister cashRegister);
    public void updateCounter(int counterID, String updateDetails, CashTransaction cashTransaction);
    public void closeCounter(int counterID, String updateDetails, CashTransaction cashTransaction);
}
