package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import configuration.Discount;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "cart_item")
public class Item extends RecursiveTreeObject<Item> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private int id;

    @Transient
    private Product product;
    @Transient
    private String name;
    @Transient
    private String barcode;
    @Transient
    private BigDecimal priceSell;

    @Column(name= "quantity")
    private int quantity;

    @Column(name= "original_amount")
    private BigDecimal originalPrice;

    @Column(name= "total_amount")
    private BigDecimal total;

    @Transient
    private Set<Promotion> promotionSet;

    @Column(name= "promotion_amount")
    private BigDecimal promotionDiscount;

    @Column(name= "bulk_price_discount_amount")
    private BigDecimal bulkPriceDiscount;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "date_modify")
    private String date;

    public Item(){

    }

    public Item(Product product, int quantity) {
        this.product = product;
        this.name = product.getName();
        this.barcode = product.getBarcode();
        this.quantity = quantity;
        this.promotionSet = product.getPromotions();
        this.productId = product.getId();
        this.priceSell = product.getPrice().getPriceSell();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(BigDecimal priceSell) {
        this.priceSell = priceSell;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBulkPriceDiscount() {
        return bulkPriceDiscount;
    }

    public void setBulkPriceDiscount(BigDecimal bulkPriceDiscount) {
        this.bulkPriceDiscount = bulkPriceDiscount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        //can use both
//        calculateSubTotal();
//        calculateDiscount();
//        calculatePriceOption();
//        total = total.subtract(bulkPriceDiscount).subtract(promotionDiscount);

        total = originalPrice().subtract(calculatePriceOption()).subtract(calculateDiscount());
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(BigDecimal promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    private BigDecimal originalPrice() {
//        total = product.getPrice().getPriceSell().multiply(new BigDecimal(quantity));
//        return total;
        originalPrice = product.getPrice().getPriceSell().multiply(new BigDecimal(quantity));
        return originalPrice;
    }

    //calculate discount based on promotion given
    private BigDecimal calculateDiscount(){
        promotionDiscount = new BigDecimal(0.00);
        if(promotionSet.size() > 0){
            Discount discount = null;
            for(Promotion promotion: promotionSet){
                discount = new Discount(product, promotion, quantity);
                promotionDiscount = promotionDiscount.add(discount.getAvailableDiscount());
            }
        }
        return promotionDiscount;
    }

    //calculate automatic discount based set prices already set
    private BigDecimal calculatePriceOption(){
        BigDecimal bulkPriceDiscount = new BigDecimal(0.00);
        //check optional price if provide for bulk item
        BigDecimal totalPriceUnit = new BigDecimal(0.00);
        BigDecimal percentageDiscount = new BigDecimal(0.00);

//        //carton is set
//        if(product.getPrice().getCarton() != 0 && product.getPrice().getPriceCarton() != null){
//            //carton 24 price
//            //24  * price sell = Rm42.00
//            totalPriceUnit = new BigDecimal(product.getPrice().getCarton()).multiply(product.getPrice().getPriceSell());
//
//            //get ceiling of how many qualified
//            //e.g q=24, conditiontomeet = 1 , 24/24= 1 if 1/24 = 0
//            int qualifiedCarton = quantity/product.getPrice().getCarton();
//
//            //42.00-36 = RM6.00 ============= 42-42
//            percentageDiscount = totalPriceUnit.subtract(product.getPrice().getPriceCarton());
//
//            //return 0*6 = discount;
//            bulkPriceDiscount = percentageDiscount.multiply(new BigDecimal(qualifiedCarton));
//        }
        this.bulkPriceDiscount = bulkPriceDiscount;
        return bulkPriceDiscount;
    }

    @ManyToOne
    @JoinColumn(name = "cart_transaction_id")
    private CartTransaction cartTransaction;

    public CartTransaction getCartTransaction() {
        return cartTransaction;
    }

    public void setCartTransaction(CartTransaction cartTransaction) {
        this.cartTransaction = cartTransaction;
    }
}