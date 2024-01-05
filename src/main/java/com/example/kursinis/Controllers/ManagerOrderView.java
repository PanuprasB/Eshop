package com.example.kursinis.Controllers;

import com.example.kursinis.model.Comment;
import com.example.kursinis.model.Manager;
import com.example.kursinis.model.Order;
import jakarta.persistence.EntityManagerFactory;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class ManagerOrderView {
    public Label orderId;
    public Label orderDate;
    public Label orderStatus;
    public TextArea orderProductList;
    public Label orderAddress;
    public Button orderClose;
    public Label orderTotal;
    public Button orderSetCompletedButton;
    public Button claimOrderButton;
    public Label orderManger;
    public Button cancelOrder;
    public ScrollPane chatPane;
    public VBox chatVbox;
    public TextField messageText;
    public Button sendMessage;
    private EntityManagerFactory entityManagerFactory;
    private HibernateController hibernateController;
    Runnable onOrderUpdated;
private Manager manager;
private Order order;

    public void setOnOrderUpdated(Runnable callback) {
        this.onOrderUpdated = callback;
    }
    public void setData(EntityManagerFactory entityManagerFactory, Manager manager, Order order) {
        this.entityManagerFactory = entityManagerFactory;
        this.manager = manager;
        this.order = order;
        cancelOrder.setVisible(false);
        hibernateController = new HibernateController(entityManagerFactory);
        orderId.setText(String.valueOf(order.getId()));
        orderDate.setText(String.valueOf(order.getDate()));
        orderStatus.setText(order.getStatus());
        orderAddress.setText(order.getAddress());
        orderTotal.setText(String.valueOf(order.getTotal())+"€");
        orderProductList.setText(order.getProducts());
        loadComments(order);

        if(order.getManager() == null){
            orderManger.setText("Not claimed");
          cancelOrder.setVisible(true);

        }else {
            orderManger.setText(String.valueOf(order.getManager().getId())+" | "+order.getManager().getName()+" "+order.getManager().getSurname());
        }
        if(order.getStatus().equals("Completed")){
            orderSetCompletedButton.setDisable(true);
            claimOrderButton.setDisable(true);
        }
        if(order.getStatus().equals("Claimed")){
            claimOrderButton.setDisable(true);
            if(!(order.getManager().getId()==manager.getId())) {
                orderSetCompletedButton.setDisable(true);
            }
            if(order.getManager().getId()==manager.getId() || manager.isAdmin()){
                claimOrderButton.setDisable(false);
                claimOrderButton.setText("Unclaim");
                claimOrderButton.setOnAction(e -> unclaimOrder());

            }
        }

    }
private void orderUpdated(){
        if(onOrderUpdated != null){
            onOrderUpdated.run();
        }}

    public void claimOrder(){
        order.setManager(manager);
       order.setManager(manager);
       order.setStatus("Claimed");

          hibernateController.updateOrder(order);
       orderManger.setText(String.valueOf(order.getManager().getId())+" | "+order.getManager().getName()+" "+order.getManager().getSurname());
       claimOrderButton.setDisable(true);
       orderUpdated();
    }
    public void setOrderCompleted(){
        order.setStatus("Completed");
        hibernateController.updateOrder(order);
        orderStatus.setText("Completed");
        orderSetCompletedButton.setDisable(true);
        onOrderUpdated.run();
    }

    public void cancelOrder(){
        hibernateController.deleteOrder(order);
        cancelOrder.getScene().getWindow().hide();
    }

    public void closeOrder(){
        orderClose.getScene().getWindow().hide();

    }
    public  void  unclaimOrder() {
        order.setManager(null);
        order.setStatus("unassigned");
        hibernateController.updateOrder(order);
        orderManger.setText("Not claimed");
        claimOrderButton.setText("Claim");
        claimOrderButton.setOnAction(e -> claimOrder());
        orderUpdated();
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
        comment.setUser(manager);
        comment.setDateCreated(java.time.LocalDate.now());
        hibernateController.addComment(comment);
    }
    public void loadComments(Order order){
        List<Comment> comments = hibernateController.getCommentsByOrderId(order.getId());
        for (Comment comment : comments) {
            Label userInfoLabel = new Label("User ID: " + comment.getUser().getId() +"  "+ comment.getUser().getName() +  ", Date: " + comment.getDateCreated());
            Label message = new Label(comment.getCommentBody());
            message.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 10px; -fx-background-radius: 10px;");
            chatVbox.getChildren().addAll(userInfoLabel, message);
        }
    }

}
