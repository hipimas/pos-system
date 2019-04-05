package model;

import connectivity.HibernateUtil;
import dao.ProductDetailsDao;
import entity.ProductDetails;
import org.hibernate.Session;

public class ProductDetailsModel implements ProductDetailsDao {

    private Session session = null;

    @Override
    public void saveProductDetails(ProductDetails productDetails) {
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(productDetails);
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
}
