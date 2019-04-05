package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for all cash transaction e.g start, add, withdraw, and close.
 * Differentiate each counter setup for each individual cashier
 */
@Entity
@Table(name = "counter")
public class CounterRegister extends RecursiveTreeObject<CounterRegister> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cash_start")
    private BigDecimal cashStart;

    @Column(name = "total_withdrawal_amount")
    private BigDecimal totalCashWithdrawal;

    @Column(name = "total_add_amount")
    private BigDecimal totalCashAdd;

    @Column(name = "cash_ending")
    private BigDecimal cashEnding;

    @Column(name = "date_start")
    private String dateStart;

    @Column(name = "date_end")
    private String dateEnd;

    public CounterRegister() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getCashStart() {
        return cashStart;
    }

    public void setCashStart(BigDecimal cashStart) {
        this.cashStart = cashStart;
    }

    public BigDecimal getTotalCashWithdrawal() {
        return totalCashWithdrawal;
    }


    public void setTotalCashWithdrawal(BigDecimal totalCashWithdrawal) {
        this.totalCashWithdrawal = totalCashWithdrawal;
    }

    public BigDecimal getTotalCashAdd() {
        return totalCashAdd;
    }

    public void setTotalCashAdd(BigDecimal totalCashAdd) {
        this.totalCashAdd = totalCashAdd;
    }

    public BigDecimal getCashEnding() {
        return cashEnding;
    }

    public void setCashEnding(BigDecimal cashEnding) {
        this.cashEnding = cashEnding;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "counter_id")
    private List<CashTransaction> cashTransactions = new ArrayList<>();

    public List<CashTransaction> getCashTransactions() {
        return cashTransactions;
    }

    public void setCashTransactions(List<CashTransaction> cashTransactions) {
        this.cashTransactions = cashTransactions;
    }

    @OneToMany()
    @JoinColumn(name = "counter_id")
    private List<Sales> salesList = new ArrayList<>();

    public List<Sales> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<Sales> salesList) {
        this.salesList = salesList;
    }

    @OneToMany(mappedBy = "counterRegister")
    private List<CartTransaction> cartTransList = new ArrayList<>();

    public List<CartTransaction> getCartTransList() {
        return cartTransList;
    }

    public void setCartTransList(List<CartTransaction> cartTransList) {
        this.cartTransList = cartTransList;
    }
}
