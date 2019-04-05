package dao;

import entity.Category;
import javafx.collections.ObservableList;

public interface CategoryDao {
    public ObservableList<Category> getAllCategories();
    public ObservableList<Category> getCategories();
    public ObservableList<Category> getParentCategory();
    public ObservableList<Category> getChildCategory();
    public Category getCategory(int id);
    public Category getCategoryByName(String categoryName);
    public Category getCategoryBySlug(String slug);
    public void saveCategory(Category category);
    public void updateCategory(Category updatedCategory, Category oldCategory, String option);
    public void decreaseCategory(Category category);
    public void deleteCategory(Category category);
    public ObservableList<String> getCategoryNames();
    public void increaseCategory(Category category);
    public boolean checkCategoryBySlug(String slug);
    public void saveCategoryImport(Category category);
    public void deleteAllCategory(Category category);
    public void alterTableID();
    public void removeAllCategoriesProduct();
}
