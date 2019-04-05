package model;

import connectivity.HibernateUtil;
import configuration.StatusBar;
import dao.CategoryDao;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import entity.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoryModel implements CategoryDao {

    private Session session = null;

    @Override
    public ObservableList<Category> getAllCategories(){
        ObservableList<Category> list = FXCollections.observableArrayList();
        try{

            session = HibernateUtil.getSession();
            List<Category> categories = session.createQuery("from Category c ORDER BY c.id", Category.class).getResultList();
            list.addAll(categories);
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
    public ObservableList<Category> getCategories(){
        ObservableList<Category> list = FXCollections.observableArrayList();
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();

            List<Category> categories = session.createQuery("from Category c LEFT JOIN FETCH c.productList ORDER BY c.id", Category.class).getResultList();
            Set<Category> categorySet = new LinkedHashSet<>(categories);
            list.addAll(categorySet);
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
    public ObservableList<Category> getParentCategory() {
        ObservableList<Category> list = FXCollections.observableArrayList();
        try{
            session = HibernateUtil.getSession();
            List<Category> categories = session.createQuery("from Category c WHERE c.parent is NULL", Category.class).getResultList();
            list.addAll(categories);
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
    public ObservableList<Category> getChildCategory() {
        ObservableList<Category> list = FXCollections.observableArrayList();
        try{
            session = HibernateUtil.getSession();
            List<Category> categories = session.createQuery("from Category c WHERE c.parent != NULL", Category.class).getResultList();
            list.addAll(categories);
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
    public Category getCategory(int id) {
        Category category = null;
        try{
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from Category where id=:id");
            query.setParameter("id", id);
            category = (Category) ((org.hibernate.query.Query) query).uniqueResult();

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return category;
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        return null;
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        Category category = null;
        try{
            session = HibernateUtil.getSession();
            Query query = session.createQuery("from Category where slug=:slug");
            query.setParameter("slug", slug);
            category = (Category) ((org.hibernate.query.Query) query).uniqueResult();

        }catch (Exception e){
            e.printStackTrace();
            new StatusBar("Masalah dalam mendandapatkan kategori " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return category;
    }

    @Override
    public void saveCategory(Category category) {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        try{
            session.save(category);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Berjaya tambah kategori baru").show();
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam menambah kategori!").show();
        } finally {
            session.close();
        }

    }

    @Override
    public void updateCategory(Category updatedCategory, Category oldCategory, String option){
        session = HibernateUtil.getSession();
        session.beginTransaction();

        Category old = session.get(Category.class,oldCategory.getId());
        switch (option){
            case "updateName":
                old.setName(updatedCategory.getName());
                old.setSlug(updatedCategory.getSlug());
                break;
            case "updateParent":
                old.setParent(updatedCategory);
                break;
            case "parentNull":
                old.setParent(null);
        }

        try{
            session.update(old);
            session.getTransaction().commit();
            new Alert(Alert.AlertType.INFORMATION,"Kategori berjaya di kemaskini").show();

        }catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam megemaskini kategori!").show();
        } finally {
            session.close();
        }


    }

    @Override
    public void decreaseCategory(Category category) {

    }

    @Override
    public void deleteCategory(Category category) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.delete(category);
            session.getTransaction().commit();
            new StatusBar("Kategori dibuang dalam senarai", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam membuang kategori " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }
    }

    @Override
    public ObservableList<String> getCategoryNames() {
        return null;
    }

    @Override
    public void increaseCategory(Category category) {

    }

    @Override
    public boolean checkCategoryBySlug(String slug) {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createQuery("from Category where slug=:slug");
        query.setParameter("slug", slug);
//        Category category = null;
        try {
            Category category = (Category) ((org.hibernate.query.Query) query).uniqueResult();
            if(category != null){
                return true;
            }
        } catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.WARNING,"Masalah dalam megemaskini kategori!").show();
            new StatusBar("Masalah dalam megemaskini kategori! " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }
        return false;
    }


    @Override
    public void saveCategoryImport(Category category) {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        try{
            session.save(category);
            session.getTransaction().commit();
            new StatusBar("Kategori berjaya ditambah ", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam mengimport kategori " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }

    }

    @Override
    public void deleteAllCategory(Category category) {
        try{
            session = HibernateUtil.getSession();
            session.beginTransaction();
            session.delete(category);
            session.getTransaction().commit();
            new StatusBar("Kategori dibuang dalam senarai", "success", MaterialDesignIcon.CHECK_ALL);
        } catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
            new StatusBar("Masalah dalam membuang kategori " + e.getMessage(), "failed", MaterialDesignIcon.ALERT);
        } finally {
            session.close();
        }
    }

    @Override
    public void alterTableID() {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("ALTER TABLE categories AUTO_INCREMENT = 1");
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void removeAllCategoriesProduct() {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("DELETE FROM category_product");
        query.executeUpdate();
        Query query2 = session.createSQLQuery("ALTER TABLE category_product AUTO_INCREMENT = 1");
        query2.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
