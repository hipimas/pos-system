package entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Class for each transaction details of cash. Record all activities inside list
 * and combine for all cashier
 */
@Entity
@Table(name = "cash_transaction")
public class CashTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cash_type")
    private String cashType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "comment")
    private String comment;

    @Column(name = "date_added")
    private String date;

    @Column(name = "user_id")
    private int userId;

    public CashTransaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCashType() {
        return cashType;
    }

    public void setCashType(String cashType) {
        this.cashType = cashType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "counter_id")
    private CounterRegister counterRegister;

    public CounterRegister getCounterRegister() {
        return counterRegister;
    }

    public void setCounterRegister(CounterRegister counterRegister) {
        this.counterRegister = counterRegister;
    }
}
