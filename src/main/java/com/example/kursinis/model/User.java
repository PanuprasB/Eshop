package com.example.kursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

public abstract class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(unique = true)
    String login;
    String name;
    String surname;

   ;
    String password;
    LocalDate birthDate;
 @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
 @LazyCollection(LazyCollectionOption.FALSE)
 List<Order> myOrders;


    public User(String login, String password, LocalDate birthDate, String name, String surname) {
        this.login = login;
        this.password = password;
        this.birthDate = birthDate;
        this.name = name;
        this.surname = surname;
    }

    public User(int id, String login, String password, LocalDate birthDate) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.birthDate = birthDate;
    }

}
