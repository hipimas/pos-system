package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="products")
@DynamicUpdate
public class Product extends RecursiveTreeObject<Product> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id")
    private int id;

    @Column(name= "barcode")
    private String barcode;

    @Column(name= "name")
    private String name;

    @Column(name= "description")
    private String description;

    @Column(name="created_at")
    private String date;

    @Column(name="image")
    private String imageUrl;

    @Transient
    @Column(name="plu")
    private String plu;

    @Transient
    @Column(name="sku")
    private String sku;

    public Product(){

    }

    public Product(String barcode, String name, String description, String date) {
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPlu() {
        return plu;
    }

    public void setPlu(String plu) {
        this.plu = plu;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "brand_product",
            joinColumns = {@JoinColumn(name ="product_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "brand_id", referencedColumnName = "id")})


    private Brand brand;

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "category_product",
            joinColumns = {@JoinColumn(name ="product_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id", referencedColumnName = "id")})

    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

//    @ManyToMany( fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @ManyToMany(fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    @JoinTable(
            name = "promotion_product",
            joinColumns = {@JoinColumn(name ="product_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "promotion_id", referencedColumnName = "id")})

    private Set<Promotion> promotions = new HashSet<>();

    public Set<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(Set<Promotion> promotions) {
        this.promotions = promotions;
    }

//    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_prices_id")
    private Price price;

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
