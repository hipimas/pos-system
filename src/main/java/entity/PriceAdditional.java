package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Table(name = "product_prices_additional")
public class PriceAdditional {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name = "type_sell")
    private int priceTypeSell;

    @Column(name = "price_sell")
    private BigDecimal priceSell;

    @Column(name="unit_sell")
    private int unitSell;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriceTypeSell() {
        return priceTypeSell;
    }

    public void setPriceTypeSell(int priceTypeSell) {
        this.priceTypeSell = priceTypeSell;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_prices_id")
    private Price price;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
