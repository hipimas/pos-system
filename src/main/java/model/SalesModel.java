package model;

import connectivity.HibernateUtil;
import dao.SalesDao;
import entity.Sales;
import javafx.scene.control.Alert;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class SalesModel implements SalesDao {
    private Session session = null;

    @Override
    public void saveSales(Sales sales) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(sales);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Sales been updated").show();
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Problem in sales!").show();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
