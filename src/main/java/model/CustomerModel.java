package model;

import connectivity.HibernateUtil;
import dao.CustomerDao;
import entity.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class CustomerModel implements CustomerDao {

    private Session session = null;
    @Override
    public ObservableList<Customer> getCustomers() {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            List<Customer> customers = session.createQuery("from Customer").getResultList();
            customers.stream().forEach(list::add);
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
    public Customer getCustomer(int id) {
        Customer customer = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();
            customer = session.get(Customer.class, id);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(session != null){
                session.close();
            }
        }
        return customer;
    }

    @Override
    public boolean checkCustomerByName(String name) {
        session = HibernateUtil.getSession();
        session.beginTransaction();
        Query query = session.createQuery("from Customer where name=:name");
        query.setParameter("name", name);
        Customer customer = (Customer) ((org.hibernate.query.Query) query).uniqueResult();

        if(customer == null){
            return false;
        }

        session.close();
        return true;
    }
}
