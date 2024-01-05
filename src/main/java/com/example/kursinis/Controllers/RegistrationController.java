package com.example.kursinis.Controllers;

import com.example.kursinis.model.Customer;
import com.example.kursinis.model.Manager;
import com.example.kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.scene.control.*;

import java.sql.Date;

public class RegistrationController {
    public TextField loginField;
    public PasswordField passwordField;
    public TextField nameField;
    public PasswordField repeatPasswordField;
    public TextField surnameField;
    public RadioButton customerCheckbox;
    public ToggleGroup userType;
    public RadioButton managerCheckBox;
    public Button ReturnButton;
    public Button CreateUserButton;
    public TextField homeAddressField;
    public TextField cardNoField;
    public DatePicker birthFateFIeld;
    public TextField employeeIdField;
    public TextField medCertificateField;
    public DatePicker employmentDateField;
    public CheckBox isAdminCheck;
    public Label userId;
private  String action;
private EntityManagerFactory entityManagerFactory;
private HibernateController hibernateController;
private  Runnable onUserUpdated;

    public void setOnUserUpdated(Runnable callback) {
          this.onUserUpdated = callback;
    }

    private void userUpdated(){
        if(onUserUpdated != null){
            onUserUpdated.run();
        }
    }
    public void setData(EntityManagerFactory entityManagerFactory, boolean showManagerFields, String action, User user) {
        this.entityManagerFactory = entityManagerFactory;
        disableFields(showManagerFields);
        if (action.equals("create")){
            CreateUserButton.setOnAction(e -> createUser());
        } else if (action.equals("edit")){
            CreateUserButton.setOnAction(e -> updateUser());
            userId.setText(String.valueOf(user.getId()));
            loginField.setText(user.getLogin());
            passwordField.setText(user.getPassword());
            repeatPasswordField.setText(user.getPassword());
            nameField.setText(user.getName());
            surnameField.setText(user.getSurname());
            birthFateFIeld.setValue(user.getBirthDate());
            if (user instanceof Customer){
                toggleManagerFields();
                customerCheckbox.setSelected(true);
                homeAddressField.setText(((Customer) user).getAddress());
                cardNoField.setText(((Customer) user).getCardNo());
            } else if (user instanceof Manager){
                toggleCustomerFields();
                managerCheckBox.setSelected(true);
                employeeIdField.setText(((Manager) user).getEmployeeId());
                medCertificateField.setText(((Manager) user).getMedCertificate());
                employmentDateField.setValue(((Manager) user).getEmploymentDate());
                isAdminCheck.setSelected(((Manager) user).isAdmin());
            }


        }
    }

    public void setData(EntityManagerFactory entityManagerFactory, Manager manager) {
        this.entityManagerFactory = entityManagerFactory;
        toggleFields(customerCheckbox.isSelected(), manager);
    }

    private void disableFields(boolean showManagerFields) {
        if (!showManagerFields) {
            employeeIdField.setVisible(false);
            medCertificateField.setVisible(false);
            employmentDateField.setVisible(false);
            isAdminCheck.setVisible(false);
            managerCheckBox.setVisible(false);
        }
    }

    private void toggleFields(boolean isManager, Manager manager) {
        if (isManager) {
            homeAddressField.setDisable(true);
            cardNoField.setDisable(true);
            employeeIdField.setDisable(false);
            medCertificateField.setDisable(false);
            employmentDateField.setDisable(false);
            if (manager.isAdmin()) isAdminCheck.setDisable(false);
        } else {
            homeAddressField.setDisable(false);
            cardNoField.setDisable(false);
            employeeIdField.setDisable(true);
            medCertificateField.setDisable(true);
            employmentDateField.setDisable(true);
            isAdminCheck.setDisable(true);
        }
    }



    public void createUser(){
        hibernateController = new HibernateController(entityManagerFactory);
        if(customerCheckbox.isSelected()){
        hibernateController.createUser(
       new Customer(loginField.getText(),passwordField.getText(),birthFateFIeld.getValue(),nameField.getText(),surnameField.getText(),homeAddressField.getText(),cardNoField.getText()));
        }
        else if(managerCheckBox.isSelected()){
           hibernateController.createUser(new Manager(loginField.getText(), passwordField.getText(), birthFateFIeld.getValue(),nameField.getText(),surnameField.getText(), employeeIdField.getText(), medCertificateField.getText(),employmentDateField.getValue(), isAdminCheck.isSelected()));
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("User type not selected");
            alert.setContentText("Please select user type");
            alert.showAndWait();
        }
        userUpdated();
        ReturnButton.getScene().getWindow().hide();
    }
    public void toggleManagerFields(){
        if(managerCheckBox.isSelected()){
            employeeIdField.setVisible(true);
            medCertificateField.setVisible(true);
            employmentDateField.setVisible(true);
            isAdminCheck.setVisible(true);
        }
        else{
            employeeIdField.setVisible(false);
            medCertificateField.setVisible(false);
            employmentDateField.setVisible(false);
            isAdminCheck.setVisible(false);
        }
    }
    public void toggleCustomerFields(){
        if(customerCheckbox.isSelected()){
            homeAddressField.setVisible(true);
            cardNoField.setVisible(true);
        }
        else{
            homeAddressField.setVisible(false);
            cardNoField.setVisible(false);
        }
    }
    public void updateUser(){
        hibernateController = new HibernateController(entityManagerFactory);
        if(customerCheckbox.isSelected()){
            Customer customer = (Customer) hibernateController.getUserById(Integer.parseInt(userId.getText()));
            customer.setLogin(loginField.getText());
            customer.setPassword(passwordField.getText());
            customer.setBirthDate(Date.valueOf(birthFateFIeld.getValue()).toLocalDate());
            customer.setName(nameField.getText());
            customer.setSurname(surnameField.getText());
            customer.setAddress(homeAddressField.getText());
            customer.setCardNo(cardNoField.getText());
            hibernateController.updateUser(customer);
        }
        else if(managerCheckBox.isSelected()){
            Manager manager = (Manager) hibernateController.getUserById(Integer.parseInt(userId.getText()));
            manager.setLogin(loginField.getText());
            manager.setPassword(passwordField.getText());
            manager.setBirthDate(Date.valueOf(birthFateFIeld.getValue()).toLocalDate());
            manager.setName(nameField.getText());
            manager.setSurname(surnameField.getText());
            manager.setEmployeeId(employeeIdField.getText());
            manager.setMedCertificate(medCertificateField.getText());
            manager.setEmploymentDate(Date.valueOf(employmentDateField.getValue()).toLocalDate());
            manager.setAdmin(isAdminCheck.isSelected());
            hibernateController.updateUser(manager);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("User type not selected");
            alert.setContentText("Please select user type");
            alert.showAndWait();
        }
        userUpdated();
        ReturnButton.getScene().getWindow().hide();
    }

    public void returnToMain(){
        userUpdated();

        ReturnButton.getScene().getWindow().hide();
    }



}
