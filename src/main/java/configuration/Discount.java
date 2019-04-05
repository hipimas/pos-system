package configuration;

import entity.Product;
import entity.Promotion;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Discount extends Promotion {

    Product product;
    Promotion promotion;

    private String calculation;

    private int quantity;
    private BigDecimal priceSell;
    private BigDecimal availableDiscount;
    private BigDecimal subTotal;
    private BigDecimal total;
    private int type;

    //Product A
    //Discount.setItem(A)
    //Discount.setType(1)
    //Discount.setItem(2)
    //Discount.setAmountDiscount(0.50)
    //Discount.setActive(true)
    public Discount(){

    }

    public Discount(Product product, Promotion promotion){
        this.product = product;
        this.promotion = promotion;
    }

    public Discount(Product product, Promotion promotion, int quantity){
        this.product = product;
        this.promotion = promotion;
        this.quantity = quantity;
        this.priceSell = product.getPrice().getPriceSell();
        calculateSubtotal(quantity, priceSell);
        this.type = promotion.getPromoType();
        calculateDiscount(promotion,product,quantity);
        calculateTotal();
    }

    private BigDecimal calculateTotal() {
        total = this.subTotal.subtract(this.availableDiscount);
        return total;
    }

    private BigDecimal calculateDiscount(Promotion promotion, Product product, int quantity) {
        //get quantity to calculate discount

        switch (promotion.getPromoType()){
            case 1:
                //qualified quantity for discount
                int conditionItem1 = Integer.parseInt(promotion.getCondition());
                //get currentqty/conditionItem
                int qualifiedDiscount1 = quantity/conditionItem1;
                availableDiscount = promotion.getDiscountAmount().multiply(new BigDecimal(qualifiedDiscount1));
                break;
            case 2:
                //example one unit e.g 2.50
                //get condition promotion item e.g= 15%
                int conditionItem2 = Integer.parseInt(promotion.getCondition());
                //get ceiling of how many qualified
                //e.g q=6, conditiontomeet = 2 , 6/2 = 3  ==== 1
                int qualifiedDiscount2 = quantity/conditionItem2;

                //covert promo discount to percentage
                //e.g 15/100 = 0.15
                int percentage = promotion.getDiscountPercent();
                BigDecimal percent = new BigDecimal(percentage).divide(new BigDecimal(100)).setScale(2, RoundingMode.FLOOR);

                //get how many should be deducted
                //e.g 3 * 0.15 ====0.15
                BigDecimal getPercentDeduction = percent.multiply(new BigDecimal(qualifiedDiscount2)).setScale(2, RoundingMode.FLOOR);

                //get total meed condition
                //e.g 3 * 2.5 = 7.50
                BigDecimal subTotalCondition = product.getPrice().getPriceSell().multiply(new BigDecimal(conditionItem2)).setScale(2, RoundingMode.FLOOR);

                //get how many discount
                availableDiscount = subTotalCondition.multiply(getPercentDeduction).setScale(2, RoundingMode.FLOOR);
//                availableDiscount = product.getPrice().getPriceSell().multiply(percent).setScale(2, RoundingMode.FLOOR);
                break;
            case 17:

                int conditionItem17 = Integer.parseInt(promotion.getCondition());
                //24/24 = 1
                int qualifiedDiscount17 = quantity/conditionItem17;

                BigDecimal perUnit = product.getPrice().getPriceSell();
                BigDecimal perUnitCondition = perUnit.multiply(new BigDecimal(conditionItem17));
                BigDecimal discountTotal = promotion.getDiscountTotal();
                BigDecimal differenceBox = perUnitCondition.subtract(discountTotal);
                availableDiscount = differenceBox.multiply(new BigDecimal(qualifiedDiscount17));
                break;
        }
//        availableDiscount = optionDiscount;

        return availableDiscount;
    }

    private BigDecimal calculateSubtotal(int quantity, BigDecimal priceSell) {
        subTotal = priceSell.multiply(new BigDecimal(quantity));

        return subTotal;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(BigDecimal priceSell) {
        this.priceSell = priceSell;
    }

    public BigDecimal getAvailableDiscount() {
        return availableDiscount;
    }

    public void setAvailableDiscount(BigDecimal availableDiscount) {
        this.availableDiscount = availableDiscount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }


}
