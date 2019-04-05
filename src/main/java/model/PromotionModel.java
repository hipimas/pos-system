package model;

import connectivity.HibernateUtil;
import dao.PromotionDao;
import entity.Promotion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;

import java.util.List;

public class PromotionModel implements PromotionDao {

    private Session session = null;


    @Override
    public ObservableList<Promotion> getPromotion() {
        ObservableList<Promotion> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();
            List<Promotion> promotions = session.createQuery("from Promotion").getResultList();
            promotions.stream().forEach(list::add);
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
    public Promotion getPromotion(long id) {
        return null;
    }

    @Override
    public Promotion getPromotionByName(String promotionName) {
        return null;
    }

    @Override
    public void savPromotion(Promotion promotion) {
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(promotion);
            session.getTransaction().commit();
        } catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            if(session != null){
                session.close();
            }
        }
    }

    @Override
    public void updatePromotion(Promotion promotion) {

    }

    @Override
    public void deletePromotion(Promotion promotion) {

    }

    @Override
    public ObservableList<String> getPromotionNames() {
        return null;
    }
}
