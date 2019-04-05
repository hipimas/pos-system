package entity;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Table(name = "product_prices")
public class Price {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name = "type_buy")
    private int priceTypeBuy;

    @Column(name = "type_sell")
    private int priceTypeSell;

    @Column(name = "price_buy")
    private BigDecimal priceBuy;

    @Column(name = "price_sell")
    private BigDecimal priceSell;

    @Column(name="unit_sell")
    private int unitSell;


    public Price(){

    }

    public Price(int priceTypeBuy, int priceTypeSell, BigDecimal priceBuy, BigDecimal priceSell, int unitSell) {
        this.priceTypeBuy = priceTypeBuy;
        this.priceTypeSell = priceTypeSell;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
        this.unitSell = unitSell;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriceTypeBuy() {
        return priceTypeBuy;
    }

    public void setPriceTypeBuy(int priceTypeBuy) {
        this.priceTypeBuy = priceTypeBuy;
    }

    public int getPriceTypeSell() {
        return priceTypeSell;
    }

    public void setPriceTypeSell(int priceTypeSell) {
        this.priceTypeSell = priceTypeSell;
    }

    public BigDecimal getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(BigDecimal priceBuy) {
        this.priceBuy = priceBuy;
    }

    public BigDecimal getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(BigDecimal priceSell) {
        this.priceSell = priceSell;
    }

    public int getUnitSell() {
        return unitSell;
    }

    public void setUnitSell(int unitSell) {
        this.unitSell = unitSell;
    }

    @OneToOne(mappedBy = "price")
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @OneToMany(mappedBy = "price", fetch = FetchType.EAGER)
    @BatchSize(size = 50)
    private Collection<PriceAdditional> priceAdditional;

    public Collection<PriceAdditional> getPriceAdditional() {
        return priceAdditional;
    }

    public void setPriceAdditional(Collection<PriceAdditional> priceAdditional) {
        this.priceAdditional = priceAdditional;
    }
}
