package configuration;

import entity.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Cart implements Serializable {

    private int tax = 6;

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    private static final List<Item> itemList = new ArrayList<>();

    public static List<Item> getItemList() {
        return itemList;
    }

    public Iterator<Item> getAllCartItems(){
        return itemList.iterator();
    }

    public int getNumberOfItems() {
        return itemList.size();
    }

    public void deleteItem(Item item){
        itemList.remove(item);
    }

    public void clearItem(){
        itemList.clear();
    }

    public BigDecimal getSubTotal() {
        BigDecimal subTotal = new BigDecimal(0.00);
        List<Item> items = getItemList();
        if(items.size() > 0){
            for(Item item : items){
                subTotal = subTotal.add(item.getTotal());
            }
        }
        return subTotal.setScale(2,RoundingMode.FLOOR);
    }

    public BigDecimal getTotal(){
        return getSubTotal().add(getChargesTax());
    }

    public BigDecimal getChargesTax(){
        BigDecimal chargesTax;
        chargesTax = (getSubTotal().multiply(new BigDecimal(tax)).divide(new BigDecimal(100)).setScale(2,RoundingMode.FLOOR));
        return chargesTax.setScale(2,RoundingMode.FLOOR);
    }

    public int getTotalItem(){
        int totalItem = 0;
        for (Iterator<Item> it1 = getAllCartItems(); it1.hasNext(); ) {
            Item it = it1.next();
            totalItem = totalItem + it.getQuantity();
        }
        return totalItem;
    }

    public void addCart(Item item){
        for (Item anItemList : itemList) {
            //check the new addCart already exist
            if (anItemList.getBarcode().toLowerCase().contains(item.getBarcode().toLowerCase())) {
                updateCart(item);
                return;
            }
        }

        itemList.add(item);
    }

    //update cart quantity based on addon product
    private void updateCart(Item item){
        for (int i = 0; i < itemList.size() ; i++) {
            //check the new addCart already exist
            if(itemList.get(i).getBarcode().toLowerCase().contains(item.getBarcode().toLowerCase())){
                int oldQty = itemList.get(i).getQuantity();
                int currentQty = item.getQuantity();
                int newQty = oldQty + currentQty;
                itemList.set(i, new Item(item.getProduct(), newQty));
            }
        }
    }

    public void updateCartQuantity(Item item, int qty){
        for (int i = 0; i < itemList.size() ; i++) {
            //check the new addCart already exist
            if(itemList.get(i).getBarcode().toLowerCase().contains(item.getBarcode().toLowerCase())){
                itemList.set(i, new Item(item.getProduct(), qty));
            }
        }
    }
}
