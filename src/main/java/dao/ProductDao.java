package dao;

import entity.Product;
import javafx.collections.ObservableList;

import javax.persistence.TypedQuery;

public interface ProductDao {
    public ObservableList<Product> getProducts();
    public ObservableList<Product> getProductsChunks(int fromIndex, int toIndex);
    public Long getProductCount();
    public Product getProduct(long id);
    public Product getProductByName(String productName);
    public Product getProductByBarcode(String barcode);
    public void saveProduct(Product product);
    public void updateProduct(Product editProduct, Product oldProduct);
    public void decreaseProduct(Product product);
    public void deleteProduct(Product product);
    public ObservableList<String> getProductNames();
    public void increaseProduct(Product product);
    public boolean checkProductByBarcode(String barcode);
    public void saveProductImport(Product product);
    public void deleteAllProduct(Product product);
    public void alterTableID();
}
