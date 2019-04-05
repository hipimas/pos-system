package model;

import connectivity.HibernateUtil;
import dao.CounterRegisterDao;
import entity.CashTransaction;
import entity.CounterRegister;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CounterRegisterModel implements CounterRegisterDao {

    private Session session = null;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public CounterRegister getCounterRegister(int id) {
        CounterRegister cashRegister = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query query = session.createQuery("from CounterRegister c LEFT JOIN FETCH c.cartTransList WHERE c.id=:ID");
            query.setParameter("ID", id);

            cashRegister = (CounterRegister) ((org.hibernate.query.Query) query).uniqueResult();

//            cashRegister = session.get(CashRegister.class, id);


        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(session != null){
                session.close();
            }
        }

        return cashRegister;
    }

    @Override
    public ObservableList<CounterRegister> getCounterRegisters() {
        return null;
    }

    @Override
    public ObservableList<CounterRegister> getCounterByDate(String dateStart, String dateEnd) {
        ObservableList<CounterRegister> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();

            Query query = session.createQuery("from CounterRegister c LEFT JOIN FETCH c.salesList WHERE c.dateStart >= :start AND c.dateEnd <= :end");
            query.setParameter("start", dateStart);
            query.setParameter("end", dateEnd);

            List<CounterRegister> cashRegisterList = query.getResultList();
            list.addAll(cashRegisterList);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(session != null){
                session.close();
            }
        }

        return list;
    }

    @Override
    public void saveCounter(CounterRegister counterRegister) {
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            counterRegister.setCashEnding(counterRegister.getCashStart());

            this.id = (Integer) session.save(counterRegister);
            session.getTransaction().commit();

            new Alert(Alert.AlertType.INFORMATION,"Amaun permulaan cash berjaya ditambah").show();
        } catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();

            new Alert(Alert.AlertType.WARNING,"Masalah dalam memulakan cash!").show();
        } finally {
            if(session != null){
                session.close();
            }
        }
    }

    @Override
    public void updateCounter(int counterID, String updateDetails , CashTransaction cashTransaction) {
        session = HibernateUtil.getSession();
        session.beginTransaction();

        CounterRegister cr = session.get(CounterRegister.class, counterID);

        //!!!! should move the logic outside dao
        if(updateDetails.equals("Menambah")){
            BigDecimal oldAmount = cr.getTotalCashAdd() != null ? cr.getTotalCashAdd() : new BigDecimal(0.00);
            BigDecimal amount = cashTransaction.getAmount();
            BigDecimal newAmount = amount.add(oldAmount);
            cr.setTotalCashAdd(newAmount);
        }
        if(updateDetails.equals("Mengeluarkan")){
            BigDecimal oldAmount = cr.getTotalCashWithdrawal() != null ? cr.getTotalCashWithdrawal() : new BigDecimal(0.00);
            BigDecimal amount = cashTransaction.getAmount();
            BigDecimal newAmount = amount.add(oldAmount);
            cr.setTotalCashWithdrawal(newAmount);
        }

        BigDecimal start = cr.getCashStart();
        BigDecimal add = cr.getTotalCashAdd() != null ? cr.getTotalCashAdd() : new BigDecimal(0.00);
        BigDecimal withdraw = cr.getTotalCashWithdrawal() != null ? cr.getTotalCashWithdrawal() : new BigDecimal(0.00);
        BigDecimal total = start.add(add).subtract(withdraw);

        cr.getCashTransactions().add(cashTransaction);
        cr.setCashEnding(total);
        try {
            session.update(cr);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,updateDetails + " cash di kemaskini").show();
        }catch (HibernateException r){
            session.getTransaction().rollback();
            r.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam megemaskini " + updateDetails + " cash!").show();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void closeCounter(int counterID, String updateDetails, CashTransaction cashTransaction) {
        session = HibernateUtil.getSession();
        session.beginTransaction();

        CounterRegister cr = session.get(CounterRegister.class, counterID);
        cr.setCashEnding(cashTransaction.getAmount());
        cr.setDateEnd(LocalDateTime.now().toString());
        cr.getCashTransactions().add(cashTransaction);

        try {
            session.update(cr);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,updateDetails + " cash di kemaskini").show();
        }catch (HibernateException r){
            session.getTransaction().rollback();
            r.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam megemaskini " + updateDetails + " cash!").show();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
