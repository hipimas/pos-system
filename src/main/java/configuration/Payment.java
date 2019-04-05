package configuration;

import entity.Customer;

import java.util.HashMap;
import java.util.Map;

public class Payment {

    private static Map<Integer, String> paymentMap;
    private String type;
    private Customer customer;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static Map<Integer, String> getPaymentType(){
        paymentMap = new HashMap<>();
        paymentMap.put(1, "Cash");
        paymentMap.put(2, "Credit card");
        paymentMap.put(3, "Credit");
        return paymentMap;
    }
}
