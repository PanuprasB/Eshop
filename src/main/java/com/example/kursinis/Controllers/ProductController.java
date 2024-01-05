package com.example.kursinis.Controllers;

import com.example.kursinis.model.Product;
import jakarta.persistence.EntityManagerFactory;
import javafx.scene.control.*;

public class ProductController {

 boolean isClosed =true;
    public Button productAbort;
    public Button productSubmit;
    public Label productAction;
    public Label productId;
    public TextField productTitle;
    public TextField productManufacturer;
    public TextArea productDescription;
    public TextField productPrice;
    public TextField productStock;
    public RadioButton cigaretteCheckbox;
    public ToggleGroup category;
    public RadioButton otherCheckbox;
    public RadioButton heetsCheckbox;
    private String action;

    private EntityManagerFactory entityManagerFactory;
    private Runnable onProductUpdated;

    public void setOnProductUpdated(Runnable callback) {
        this.onProductUpdated = callback;
    }

    // Call this method when the product is successfully added or edited
    private void productUpdated() {
        if (onProductUpdated != null) {
            onProductUpdated.run();
        }
    }
    public void setData(EntityManagerFactory entityManagerFactory, String action, Product product){
        this.entityManagerFactory = entityManagerFactory;
        this.action = action;
        isClosed = false;
        if (action.equals("create")){
           productSubmit.setOnAction(e -> createProduct());
        } else if (action.equals("edit")){
            productSubmit.setOnAction(e -> editProduct());
            // i need to prefill the fields with the data from the product
            productId.setText(String.valueOf(product.getId()));
            productTitle.setText(product.getTitle());
            productManufacturer.setText(product.getManufacturer());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.valueOf(product.getPrice()));
            productStock.setText(String.valueOf(product.getStock()));
            if (product.getCategory().equals("Cigarettes")){
                cigaretteCheckbox.setSelected(true);
            }
            if (product.getCategory().equals("Other")){
                otherCheckbox.setSelected(true);
            }
            if (product.getCategory().equals("Heets")){
                heetsCheckbox.setSelected(true);
            }
        }
    }
    public void createProduct(){
        HibernateController hibernateController = new HibernateController(entityManagerFactory);

        if (cigaretteCheckbox.isSelected()) {
// Corrected parsing without specifying a radix
            hibernateController.createProduct(new Product(productTitle.getText(), "Cigarettes", productManufacturer.getText(), productDescription.getText(), Integer.parseInt(productStock.getText()), Double.parseDouble(productPrice.getText())));
        }
        if (otherCheckbox.isSelected()) {
            hibernateController.createProduct(new Product(productTitle.getText(), "Other", productManufacturer.getText(), productDescription.getText(), Integer.parseInt(productStock.getText()), Double.parseDouble(productPrice.getText())));
        }
        if (heetsCheckbox.isSelected()) {
            hibernateController.createProduct(new Product(productTitle.getText(), "Heets", productManufacturer.getText(), productDescription.getText(), Integer.parseInt(productStock.getText()), Double.parseDouble(productPrice.getText())));        }
        productUpdated();
        productAbort.getScene().getWindow().hide();
    }

   public void editProduct(){
        HibernateController hibernateController = new HibernateController(entityManagerFactory);
        Product product = hibernateController.getProductById(Integer.parseInt(productId.getText()));
        product.setTitle(productTitle.getText());
        product.setManufacturer(productManufacturer.getText());
        product.setDescription(productDescription.getText());
        product.setPrice(Double.parseDouble(productPrice.getText()));
        product.setStock(Integer.parseInt(productStock.getText()));
        if (cigaretteCheckbox.isSelected()) {
            product.setCategory("Cigarettes");
        }
        if (otherCheckbox.isSelected()) {
            product.setCategory("Other");
        }
        if (heetsCheckbox.isSelected()) {
            product.setCategory("Heets");
        }
        hibernateController.updateProduct(product);
       productUpdated();
        productAbort.getScene().getWindow().hide();
    }
    public void returnToMain(){
        productAbort.getScene().getWindow().hide();
        isClosed = true;
    }


    public boolean isClosed() {
        return isClosed;
    }
}

