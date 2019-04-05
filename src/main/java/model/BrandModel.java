package model;

import connectivity.HibernateUtil;
import configuration.StatusBar;
import dao.BrandDao;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import entity.Brand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BrandModel implements BrandDao {

    private Session session = null;

    @Override
    public ObservableList<Brand> getBrandsOnly() {
        ObservableList<Brand> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            List<Brand> brands = session.createQuery("from Brand b ORDER BY b.name", Brand.class).getResultList();
            list.addAll(brands);

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
    public ObservableList<Brand> getBrands() {
        ObservableList<Brand> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            List<Brand> brands = session.createQuery("from Brand b LEFT JOIN FETCH b.productList ORDER BY b.name", Brand.class).getResultList();
            Set<Brand> set = new LinkedHashSet<>(brands);
            list.addAll(set);

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
    public Brand getBrand(int id) {
        Brand brand = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            Query query = session.createQuery("from Brand where id=:id");
            query.setParameter("id", id);
            brand = (Brand) ((org.hibernate.query.Query) query).uniqueResult();

            session.close();
        } catch (Exception e){
            if(session != null){
                session.close();
            }
        }

        return brand;
    }

    @Override
    public Brand getBrandByName(String brandName) {
        return null;
    }

    @Override
    public Brand getBrandBySlug(String slug) {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createQuery("from Brand where slug=:slug");
        query.setParameter("slug", slug);
        Brand brand = (Brand) ((org.hibernate.query.Query) query).uniqueResult();

        session.close();
        return brand;
    }

    @Override
    public void saveBrand(Brand brand) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(brand);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Jenama baru ditambah dalam senarai").show();
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam menambah jenama baharu!").show();
        } finally {
            session.close();
        }

    }

    @Override
    public void updateBrand(Brand editProduct,Brand oldBrand) {
        session = HibernateUtil.getSession();
        session.beginTransaction();

        Brand ps = session.get(Brand.class, oldBrand.getId());
        ps.setName(editProduct.getName());
        ps.setSlug(editProduct.getSlug());
        try {
            session.update(ps);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Jenama berjaya di kemaskini").show();
        }catch (HibernateException r){
            session.getTransaction().rollback();
            r.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam megemaskini jenama!").show();
        } finally {
            if(session != null){
                session.close();
            }
        }

    }

    @Override
    public void decreaseBrand(Brand brand) {

    }

    @Override
    public void deleteBrand(Brand brand) {

    }

    @Override
    public ObservableList<String> getBrandNames() {
        return null;
    }

    @Override
    public void increaseBrand(Brand brand) {

    }

    @Override
    public boolean checkBrandBySlug(String slug) {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createQuery("from Brand where slug=:slug");
        query.setParameter("slug", slug);
        Brand brand = (Brand) ((org.hibernate.query.Query) query).uniqueResult();

        if(brand == null){
            return false;
        }

        session.close();
        return true;
    }

    @Override
    public void saveBrandImport(Brand brand) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.save(brand);
            session.getTransaction().commit();
            new StatusBar("Jenama ditambah dalam senarai", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam mengimport jenama " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }

    }

    @Override
    public void deleteAllBrand(Brand brand) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.delete(brand);
            session.getTransaction().commit();
            new StatusBar("Jenama dibuang dalam senarai", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam membuang jenama " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }
    }

    @Override
    public void alterTableID() {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("ALTER TABLE brand AUTO_INCREMENT = 1");
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
