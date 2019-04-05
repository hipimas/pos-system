package model;

import connectivity.HibernateUtil;
import configuration.StatusBar;
import dao.ProductDao;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import entity.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.hibernate.*;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class ProductModel implements ProductDao {

    private Session session = null;

    @Override
    public ObservableList<Product> getProducts() {
        ObservableList<Product> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            List<Product> products = session.createQuery("from Product p LEFT JOIN FETCH p.price LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.promotions", Product.class).getResultList();
            list.addAll(products);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }


        return list;
    }

    @Override
    public ObservableList<Product> getProductsChunks(int fromIndex, int toIndex) {
        ObservableList<Product> list = FXCollections.observableArrayList();
//        TypedQuery<Product> typedQuery = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            CriteriaQuery<Product> selectQuery = criteriaQuery.select(root);

            TypedQuery<Product> typedQuery = session.createQuery(selectQuery);
            typedQuery.setFirstResult(fromIndex);
            typedQuery.setMaxResults(toIndex - fromIndex);
            list.addAll(typedQuery.getResultList());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }


        return list;
    }

    @Override
    public Long getProductCount() {
        Long count = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

            countQuery.select(criteriaBuilder.count(countQuery.from(Product.class)));
            count = session.createQuery(countQuery).getSingleResult();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return count;
    }

    @Override
    public Product getProduct(long id) {
        return null;
    }

    @Override
    public Product getProductByName(String productName) {
        return null;
    }

    @Override
    public Product getProductByBarcode(String barcode) {
        Product product = null;
        try{
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from Product p LEFT JOIN FETCH p.price LEFT JOIN FETCH p.category LEFT JOIN FETCH p.brand LEFT JOIN FETCH p.promotions where barcode=:barcode");
            query.setParameter("barcode", barcode);
            product = (Product) ((org.hibernate.query.Query) query).uniqueResult();

        } catch (Exception e){
            e.printStackTrace();

        } finally {
            if (session != null) {
                session.close();
            }
        }

        return product;
    }

    @Override
    public void saveProduct(Product product) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(product);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Produk baharu ditambah dalam senarai").show();
            new StatusBar("Produk baharu ditambah dalam senarai", "success", MaterialDesignIcon.CHECK_CIRCLE_OUTLINE);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam menambah produk baharu!").show();
            new StatusBar("Masalah dalam menambah produk baharu! " + e.getMessage(), "failed", MaterialDesignIcon.ALERT_CIRCLE);
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public void updateProduct(Product editProduct,Product oldProduct) {
        session = HibernateUtil.getSession();
        session.beginTransaction();

        Product ps = session.get(Product.class, oldProduct.getId());
        ps.setBarcode(editProduct.getBarcode());
        ps.setName(editProduct.getName());
        ps.setDescription(editProduct.getDescription());
        ps.setDate(editProduct.getDate());
        ps.setImageUrl(editProduct.getImageUrl());
        ps.setBrand(editProduct.getBrand());
        ps.setCategory(editProduct.getCategory());
        ps.getPrice().setPriceTypeBuy(editProduct.getPrice().getPriceTypeBuy());
        ps.getPrice().setPriceBuy(editProduct.getPrice().getPriceBuy());
        ps.getPrice().setPriceTypeSell(editProduct.getPrice().getPriceTypeSell());
        ps.getPrice().setPriceSell(editProduct.getPrice().getPriceSell());
        ps.getPrice().setUnitSell(editProduct.getPrice().getUnitSell());

        try {
            session.update(ps);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Produk berjaya dikemaskini").show();

            new StatusBar("Produk berjaya dikemaskini", "success", MaterialDesignIcon.CHECK_CIRCLE_OUTLINE);
        }catch (HibernateException r){
            session.getTransaction().rollback();
            r.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam megemaskini produk!").show();

            new StatusBar("Masalah dalam megemaskini produk!", "failed", MaterialDesignIcon.ALERT_CIRCLE);
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    @Override
    public void decreaseProduct(Product product) {

    }

    @Override
    public void deleteProduct(Product product) {

    }

    @Override
    public ObservableList<String> getProductNames() {
        return null;
    }


    @Override
    public void increaseProduct(Product product) {

    }

    @Override
    public boolean checkProductByBarcode(String barcode) {
        try{
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from Product where barcode=:barcode");
            query.setParameter("barcode", barcode);
            Product product = (Product) ((org.hibernate.query.Query) query).uniqueResult();

            if(product == null){
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return true;
    }

    @Override
    public void saveProductImport(Product product){
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(product);
            session.getTransaction().commit();
            new StatusBar("Product ditambah dalam senarai", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam mengimport product " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteAllProduct(Product product){
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.delete(product);
            session.getTransaction().commit();
            new StatusBar("product dibuang dalam senarai", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam membuang product " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }
    }

    @Override
    public void alterTableID(){
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("ALTER TABLE products AUTO_INCREMENT = 1");
        Query query2 = session.createSQLQuery("ALTER TABLE product_prices AUTO_INCREMENT = 1");
        Query query3 = session.createSQLQuery("ALTER TABLE product_prices_additional AUTO_INCREMENT = 1");
        Query query4 = session.createSQLQuery("ALTER TABLE brand_product AUTO_INCREMENT = 1");
        Query query5 = session.createSQLQuery("ALTER TABLE category_product AUTO_INCREMENT = 1");
        query.executeUpdate();
        query2.executeUpdate();
        query3.executeUpdate();
        query4.executeUpdate();
        query5.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
