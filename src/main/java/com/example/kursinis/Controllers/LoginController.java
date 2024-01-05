package com.example.kursinis.Controllers;



import com.example.kursinis.*;
import com.example.kursinis.model.Customer;
import com.example.kursinis.model.Manager;
import com.example.kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    private EntityManagerFactory entityManagerFactory;
    private HibernateController userHib;


    public void registerNewUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration.fxml"));
        Parent parent = fxmlLoader.load();

        RegistrationController registrationController = fxmlLoader.getController();
        registrationController.setData(entityManagerFactory,false,  "create", null);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("Shop");
        stage.setScene(scene);
        stage.show();
    }

    public void validateAndConnect() throws IOException {
        userHib = new HibernateController(entityManagerFactory);
        User user = userHib.getUserByCredentials(loginField.getText(), passwordField.getText());

        if (user != null) {
            if (user instanceof Customer) {


                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-shop-user.fxml"));
                Parent parent = fxmlLoader.load();
                MainShopUserController mainShopController = fxmlLoader.getController();
                mainShopController.setData(entityManagerFactory, user);
                Scene scene = new Scene(parent);
                Stage stage = (Stage) loginField.getScene().getWindow();
                stage.setTitle("Shop");
                stage.setScene(scene);
                stage.show();
            }else if (user instanceof Manager) {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-shop.fxml"));
                Parent parent = fxmlLoader.load();
                MainShopAdminController mainShopController = fxmlLoader.getController();
                mainShopController.setData(entityManagerFactory, user);
                Scene scene = new Scene(parent);
                Stage stage = (Stage) loginField.getScene().getWindow();
                stage.setTitle("Shop");
                stage.setScene(scene);
                stage.show();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Wrong credentials");
                alert.setContentText("Wrong username or password");
                alert.showAndWait();

            }
        }



         else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Wrong credentials");
            alert.setContentText("Wrong username or password");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = Persistence.createEntityManagerFactory("Eshop");
    }
}