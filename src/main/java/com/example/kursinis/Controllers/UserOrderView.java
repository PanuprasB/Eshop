package com.example.kursinis.Controllers;

import com.example.kursinis.model.Comment;
import com.example.kursinis.model.Order;
import jakarta.persistence.EntityManagerFactory;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserOrderView {
    public Label orderId;
    public Label orderDate;
    public Label orderStatus;
    public TextArea orderProductList;
    public Label orderAddress;
    public Label orderTotal;
    public Button orderClose;
    public Button cancelOrder;
    public ScrollPane chatPane;
    public VBox chatVbox;
    public TextField messageText;
    public Button sendMessage;
    EntityManagerFactory entityManagerFactory;
    HibernateController hibernateController;
    Order order;

    public void setData(Order order, EntityManagerFactory entityManagerFactory, HibernateController hibernateController) {
        cancelOrder.setVisible(false);
        this.order = order;
        orderId.setText(String.valueOf(order.getId()));
        orderDate.setText(String.valueOf(order.getDate()));
        orderStatus.setText(order.getStatus());
        orderAddress.setText(order.getAddress());
        orderTotal.setText(String.valueOf(order.getTotal())+"€");
        orderProductList.setText(order.getProducts());
        cancelOrder.setOnAction(e -> cancelOrder(order));
        this.entityManagerFactory = entityManagerFactory;
        this.hibernateController = hibernateController;
        List<Comment> comments = hibernateController.getCommentsByOrderId(order.getId());
        if(order.getStatus().equals("unassigned")){
            cancelOrder.setVisible(true);

        }
        loadComments(order);

    }

    public void sendMessage() {
        if (!messageText.getText().isEmpty()) {
            Label message = new Label(messageText.getText());
            message.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 10px; -fx-background-radius: 10px;");
            chatVbox.getChildren().add(message);
            addComment(messageText.getText(), order);   // Save the comment to the database

            messageText.clear();
        }

    }

    public void addComment(String text, Order order) {
        Comment comment = new Comment();
        comment.setCommentBody(text);
        comment.setOrder(order);
        comment.setUser(order.getClient());
        comment.setDateCreated(java.time.LocalDate.now());
        hibernateController.addComment(comment);
    }

    public void closeOrder(){
        orderClose.getScene().getWindow().hide();

    }
    public void cancelOrder(Order order){
        hibernateController.deleteOrder(order);
          cancelOrder.getScene().getWindow().hide();
    }

    public void  loadComments(Order order){
        List<Comment> comments = hibernateController.getCommentsByOrderId(order.getId());
        for (Comment comment : comments) {
            Label userInfoLabel = new Label("User ID: " + comment.getUser().getId() +"  "+ comment.getUser().getName() +  ", Date: " + comment.getDateCreated());
            Label message = new Label(comment.getCommentBody());
            message.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 10px; -fx-background-radius: 10px;");
            chatVbox.getChildren().addAll(userInfoLabel, message);
        }
    }



}


