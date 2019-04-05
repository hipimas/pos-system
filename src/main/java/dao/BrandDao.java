package dao;

import entity.Brand;
import entity.Product;
import javafx.collections.ObservableList;

public interface BrandDao {
    public ObservableList<Brand> getBrandsOnly();
    public ObservableList<Brand> getBrands();
    public Brand getBrand(int id);
    public Brand getBrandByName(String brandName);
    public Brand getBrandBySlug(String slug);
    public void saveBrand(Brand brand);
    public void updateBrand(Brand editProduct, Brand oldBrand);
    public void decreaseBrand(Brand brand);
    public void deleteBrand(Brand brand);
    public ObservableList<String> getBrandNames();
    public void increaseBrand(Brand brand);
    public boolean checkBrandBySlug(String slug);
    public void saveBrandImport(Brand brand);
    public void deleteAllBrand(Brand brand);
    public void alterTableID();
}
