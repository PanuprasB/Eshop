package com.example.kursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)



    int id;
    @Column(unique = true)


    String title;
    String category;
    String manufacturer;
    String description;
    int stock;
    double price;


     @Transient
     private int quantity;

     @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
     @LazyCollection(LazyCollectionOption.FALSE)
     List<Comment> comments;



    public Product(String title, String category, String manufacturer, String description, int stock, double price){
      this.title = title;
      this.category = category;
      this.manufacturer = manufacturer;
      this.description = description;
      this.stock = stock;
      this.price = price;
    }

    public Product(String title, String category, String manufacturer, String description, int stock) {
        this.title = title;
        this.category = category;
        this.manufacturer = manufacturer;
        this.description = description;
        this.stock = stock;

    }


    //i dont know what to do with the image. i dont know how to store it inthe database and with inmpleent it in the class, and also from what ive read it is not a goood ieda to store images in the database, maybe we should just leave it empty for now and implement it later. we might be able to do this with pointers

  // also i i dont think allargsconstrcutor is a good idea here, because by default we might want to have have 0 stock .

 @Override public String toString() {
        return "Product{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", description='" + description + '\'' +
                ", stock=" + stock +
                ", price=" + price +
                '}';
    }


}
