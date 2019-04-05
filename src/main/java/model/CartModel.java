package model;

import connectivity.HibernateUtil;
import dao.CartDao;
import entity.CartTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.List;

public class CartModel implements CartDao {

    private Session session = null;

    @Override
    public ObservableList<CartTransaction> getCashByDate(String dateStart, String dateEnd) {
        ObservableList<CartTransaction> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Query query = session.createQuery("from CartTransaction WHERE date BETWEEN :start AND :end");
            query.setParameter("start", dateStart);
            query.setParameter("end", dateEnd);

            List<CartTransaction> cashRegisterList = query.getResultList();
            cashRegisterList.stream().forEach(list::add);
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
    public void saveCart(CartTransaction cart) {
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(cart);
            session.getTransaction().commit();
        } catch (Exception e){
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if(session != null){
                session.close();
            }
        }

    }
}
