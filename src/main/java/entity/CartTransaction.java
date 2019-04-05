package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for cart sales
 */
@Entity
@Table(name = "cart_transaction")
public class CartTransaction extends RecursiveTreeObject<CartTransaction> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private int id;

    @Column(name= "sub_total")
    private BigDecimal subTotal;

    @Column(name= "total_amount")
    private BigDecimal totalAmount;

    @Column(name= "tax_amount")
    private BigDecimal taxAmount;

    @Column(name= "total_item")
    private int totalItem;

    @Column(name= "date_modify")
    private String date;

    @Column(name= "user_id")
    private int userId;

    @Column(name= "payment_type")
    private String paymentType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Relationship between cartTransaction@item purchases and cashRegister@cashier
     * Many cart can only have one cashier at the one time
     */
    @ManyToOne
    @JoinColumn(name = "counter_id")
    private CounterRegister counterRegister;

    public CounterRegister getCounterRegister() {
        return counterRegister;
    }

    public void setCounterRegister(CounterRegister counterRegister) {
        this.counterRegister = counterRegister;
    }

    @ManyToMany( cascade = CascadeType.ALL)
    @JoinTable(
            name = "cart_customer",
            joinColumns = {@JoinColumn(name ="cart_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "customer_id", referencedColumnName = "id")})

    private List<Customer> customerList = new ArrayList<>();

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_transaction_id")
    private List<Item> itemList = new ArrayList<>();

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
