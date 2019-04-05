package entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Class for recorded sales as per counter closed
 * Sales divided into 3 group for differentiate by their method of payment
 */
@Entity
@Table(name = "sales")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cash_amount")
    private BigDecimal cashSales;

    @Column(name = "card_amount")
    private BigDecimal cardSales;

    @Column(name = "credit_amount")
    private BigDecimal creditSales;

    @Column(name = "total_amount")
    private BigDecimal totalSales;

    @Column(name = "user_id")
    private int userId;

    public Sales() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getCashSales() {
        return cashSales;
    }

    public void setCashSales(BigDecimal cashSales) {
        this.cashSales = cashSales;
    }

    public BigDecimal getCardSales() {
        return cardSales;
    }

    public void setCardSales(BigDecimal cardSales) {
        this.cardSales = cardSales;
    }

    public BigDecimal getCreditSales() {
        return creditSales;
    }

    public void setCreditSales(BigDecimal creditSales) {
        this.creditSales = creditSales;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @ManyToOne
    @JoinColumn(name = "counter_id")
    private CounterRegister counterRegister;

    public CounterRegister getCounterRegister() {
        return counterRegister;
    }

    public void setCounterRegister(CounterRegister counterRegister) {
        this.counterRegister = counterRegister;
    }
}
