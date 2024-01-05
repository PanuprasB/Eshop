package com.example.kursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "`Order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String status;
    private LocalDate date;

    @ManyToOne
    private User client;

    @ManyToOne
    private User manager;


    private String products;


    private String address;
    private double total;

   public void addProduct(String name, int quantity, double price){
       if(products == null){
           products = "";
       }
       products += name + " | " + quantity + " | " + price + "EUR;\n ";


   }

}