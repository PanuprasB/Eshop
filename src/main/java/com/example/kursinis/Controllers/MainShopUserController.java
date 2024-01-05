package com.example.kursinis.Controllers;

import com.example.kursinis.*;
import com.example.kursinis.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class MainShopUserController implements Initializable {


    public Tab produtsTab;
    public Button addToCart;
    public ListView produtsList;
    public Tab cartTab;
    public ListView cartList;
    public Button removeFromCart;
    public Button createOrder;
    public Tab orderHistoryTab;
    public ListView orderHistoryList;
    public Button orderInfo;
    public AnchorPane singleProduct;
    public ImageView productImage;
    public TextArea productDescription;
    public Label productName;
    public Label attribute1;
    public Label attribute2;
    public Label attribute3;
    public Label attribute4;
    public ScrollPane commentPane;
    public Label productId;
    public TextField productAmountField;

    @FXML
    private ScrollPane commentScrollPane;

    @FXML
    private VBox commentVBox;


    private List<Product> products;


    private EntityManagerFactory entityManagerFactory;
    private HibernateController hibernateController;
    private Cart cart;
    private Customer customer;



    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.customer = (Customer) user;
        this.hibernateController = new HibernateController(entityManagerFactory);
        this.cart = new Cart();
        populateProductList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        singleProduct.setVisible(false);
        entityManagerFactory = Persistence.createEntityManagerFactory("Eshop");
        hibernateController = new HibernateController(entityManagerFactory);
        products = hibernateController.getAllProducts();

        populateProductList();

        this.cart = new Cart();
        produtsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showProduct());
        cartList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showProduct());
        populateOrderHistoryList();
    }
    public void populateOrderHistoryList(){

        if (this.customer == null) {

            return;
        }
        orderHistoryList.setCellFactory(lv -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                     setText(null);
                } else {
                     setText(item.getDate() + "\n" +item.getStatus() +"\n"+item.getProducts() + "\n"+ item.getTotal()+ " EUR");
                }
            }
        });
        orderHistoryList.getItems().clear();
        orderHistoryList.getItems().addAll(hibernateController.getAllOrdersByUserId(customer.getId()));
    }
    public void showProduct() {
        Product product = (Product) produtsList.getSelectionModel().getSelectedItem();
        productName.setText(product.getTitle());
        productDescription.setText(product.getDescription());
        productId.setText(String.valueOf(product.getId()));
        productAmountField.setText("1");
        attribute1.setText("manufacturer " + product.getManufacturer());
        attribute2.setText(product.getCategory());
        attribute3.setText("Unit price: " + String.valueOf(product.getPrice() + "€"));
        attribute4.setDisable(true);
        attribute4.setVisible(false);
        singleProduct.setVisible(true);
        List<Comment> comments = hibernateController.getCommentsByProductId(product.getId());
        loadComments(comments);


        Button newCommentButton = new Button("Write a new comment");
        TextField newCommentField = new TextField();
        newCommentField.setPromptText("Enter your comment here");
        newCommentField.setPrefWidth(200);
        newCommentField.setPrefHeight(50);

        newCommentButton.setOnAction(e -> {
            String commentText = newCommentField.getText();
            if (!commentText.isEmpty()) {
                Comment comment = new Comment();
                comment.setCommentBody(commentText);
                comment.setProduct(product);
                comment.setUser(this.customer);
                comment.setDateCreated(new Date(System.currentTimeMillis()).toLocalDate());

                hibernateController.addComment(comment);


                loadComments(comments);


                newCommentField.clear();
            }
        });


        commentVBox.getChildren().addAll(newCommentField, newCommentButton);
    }


    public void populateProductList() {
        produtsList.getItems().clear();

        produtsList.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + "  |  " + item.getPrice());
                }
            }
        });


        products = hibernateController.getAllProducts();
        produtsList.getItems().addAll(products);


    }

    public void populateCartList() {
       cartList.getItems().clear();
       cartList.getItems().addAll(cart.getItemsInCart());

         cartList.setCellFactory(lv -> new ListCell<Product>() {
              @Override
              protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                     setText(null);
                } else {
                     setText(item.getTitle() + "  |  " +item.getQuantity() +"  |  "+ item.getPrice()+ " EUR");
                }
              }
         });
    }

    public void addToCart() {


        Product product = (Product) produtsList.getSelectionModel().getSelectedItem();
        int amount = Integer.parseInt(productAmountField.getText());

        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                if(cart.getItemsInCart().contains(product)){
                    product.setQuantity(product.getQuantity()+1);
            }else{
                    cart.addProduct(product);
                }
            }
            populateCartList();
        }
    }
    public void removeFromCart(){
        Product product = (Product) cartList.getSelectionModel().getSelectedItem();
        cart.removeProduct(product);
        populateCartList();
    }
    public void createOrder() {

        Order newOrder = new Order();
        newOrder.setClient(customer);
        newOrder.setDate(LocalDate.now());
        newOrder.setStatus("unassigned");

        for (Product product : cart.getItemsInCart()) {
           newOrder.addProduct(product.getTitle(), product.getQuantity(), product.getPrice());
            newOrder.setTotal(newOrder.getTotal() + product.getPrice());

        }
        newOrder .setAddress(customer.getAddress());


        hibernateController.createOrder(newOrder); // Implement this method in HibernateController

        cart.getItemsInCart().clear(); // Clear the cart after creating the order
        populateCartList(); // Update the cart list UI
        populateOrderHistoryList();
    }
    public void orderInfo() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("userOrderView.fxml"));
        Parent parent = fxmlLoader.load();
        UserOrderView userOrderView = fxmlLoader.getController();
        userOrderView.setData( (Order) orderHistoryList.getSelectionModel().getSelectedItem(), entityManagerFactory, hibernateController);

        Stage stage = new Stage();
        stage.setTitle("Order info");
        stage.setScene(new Scene(parent));
        stage.show();


    }

    public void loadComments(List<Comment> comments) {
        commentVBox.getChildren().clear();
        for (Comment comment : comments) {
            addCommentToView(comment, commentVBox);
        }
    }

    private void addCommentToView(Comment comment, VBox container) {
        VBox commentBox = new VBox();
        String commentText = " ";
        if (comment.getUser() != null) {
            commentText = "User ID: " + comment.getUser().getId() + "       " + comment.getUser().getName() + ", Date: " + comment.getDateCreated();
        }
        Label commentTextLabel = new Label(commentText);
        commentTextLabel.setWrapText(true);
        commentTextLabel.setPrefWidth(330); // Set the preferred width of the label
        commentTextLabel.setPrefHeight(50);

        Label commentBodyLabel = new Label(comment.getCommentBody());
        commentBodyLabel.setWrapText(true);
        commentBodyLabel.setPrefWidth(330); // Set the preferred width of the label
        commentBodyLabel.setPrefHeight(50);

        commentBox.getChildren().addAll(commentTextLabel, commentBodyLabel); // Add commentTextLabel and commentBodyLabel here

        Button replyButton = new Button("Reply");
        TextArea replyTextArea = new TextArea();
        replyTextArea.setPromptText("Enter your reply here");
        replyTextArea.setPrefWidth(200); // Set the preferred width of the text area
        replyTextArea.setPrefHeight(50);
        replyTextArea.setWrapText(true);
        HBox replyBox = new HBox(replyTextArea, replyButton); // Create a HBox for replyTextArea and replyButton

        replyButton.setOnAction(e -> {
            String replyText = replyTextArea.getText();
            if (!replyText.isEmpty()) {
                Comment reply = new Comment();
                reply.setCommentBody(replyText);
                reply.setParentComment(comment);
                reply.setProduct(comment.getProduct());
                reply.setUser(this.customer); // Set the user of the reply
                reply.setDateCreated(new Date(System.currentTimeMillis()).toLocalDate()); // Set the date of the reply
                // Save the reply to the database
                hibernateController.addComment(reply);

                // Refresh the comments view
                List<Comment> comments = hibernateController.getCommentsByProductId(comment.getProduct().getId());
                loadComments(comments);

                // Clear the text area
                replyTextArea.clear();
            }
        });
        commentBox.getChildren().add(replyBox); // Add replyBox to commentBox
        container.getChildren().add(commentBox);

        VBox repliesBox = new VBox();
        repliesBox.setStyle("-fx-padding: 0 0 0 20;"); // Add padding to the left side
        container.getChildren().add(repliesBox);
        for (Comment reply : comment.getReplies()) {
            addCommentToView(reply, repliesBox);
        }
    }





}
