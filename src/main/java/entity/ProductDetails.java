package entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_details")
public class ProductDetails {

    @Id
    @GeneratedValue
    @Column(name = "productID_PK")
    private int id;

    @Column(name = "unit_box")
    private int box;

    @Column(name = "unit_dozen")
    private int dozen;

    @Column(name = "unit_pax")
    private int pax;

    @Column(name = "amount_box")
    private BigDecimal amountBox;

    @Column(name = "amount_dozen")
    private BigDecimal amountDozen;

    @Column(name = "amount_pax")
    private BigDecimal amountPax;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public int getDozen() {
        return dozen;
    }

    public void setDozen(int dozen) {
        this.dozen = dozen;
    }

    public int getPax() {
        return pax;
    }

    public void setPax(int pax) {
        this.pax = pax;
    }

    public BigDecimal getAmountBox() {
        return amountBox;
    }

    public void setAmountBox(BigDecimal amountBox) {
        this.amountBox = amountBox;
    }

    public BigDecimal getAmountDozen() {
        return amountDozen;
    }

    public void setAmountDozen(BigDecimal amountDozen) {
        this.amountDozen = amountDozen;
    }

    public BigDecimal getAmountPax() {
        return amountPax;
    }

    public void setAmountPax(BigDecimal amountPax) {
        this.amountPax = amountPax;
    }

//    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    private Product product;
//
//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
}
