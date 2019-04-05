package dao;

import entity.Promotion;
import javafx.collections.ObservableList;

public interface PromotionDao {
    public ObservableList<Promotion> getPromotion();
    public Promotion getPromotion(long id);
    public Promotion getPromotionByName(String promotionName);
    public void savPromotion(Promotion promotion);
    public void updatePromotion(Promotion promotion);
    public void deletePromotion(Promotion promotion);
    public ObservableList<String> getPromotionNames();
}
