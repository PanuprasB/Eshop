package com.example.kursinis.Controllers;
import com.example.kursinis.*;
import com.example.kursinis.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainShopAdminController implements Initializable {
    public ListView<Product> productList;
    public ListView<User> userList;


    public AnchorPane singleProduct;
    public TextArea productDescription;
    public Label productName;
    public Label attribute1;
    public Label attribute2;
    public Label attribute3;
    public ScrollPane commentPane;
    public Button editProduct;
    public Button deleteProduct;
    public Label productId;
    public TextField searchProductField;
    public Button searchProduct;
    public Button addNewProduct;
    //user tab
    public AnchorPane singleUser;
    public Label userId;
    public Label userName;
    public Label userSurname;
    public Label userStatus;
    public Label userBirthDate;
    public Label userLogin;
    public Label userMedCertificate;
    public Label userEmploymentDate;
    public Button editUser;
    public Button removeUser;
    public Button userMessage;

    public TextField searchUserField;

    public Button searchUser;
    public Button addNewUserM;
    public Label userCardNo;
    public Label userAddress;

    //tasks tab
    public ListView unassignedTasksList;
    public Button unassignedTasksInfo;
    public ListView completedTasksList;
    public ListView assignedTasksList;
    public Button completedTasksInfo;
    public Button assignedTasksInfo;
    public Label attribute4;
    public TextField assignedOrderSearchField;
    public TextField completedOrderSearchField;
    public Button assignedOrderSearchButton;
    public Button completedOrderSearchButton;
    public RadioButton allOrdersRadio;
    public ToggleGroup orders;
    public RadioButton myOrdersRadio;
    public Button reportButton;

    @FXML
    private ScrollPane commentScrollPane;

    @FXML
    private VBox commentVBox;


    private List<Product> products;
    private List<User> users;
    private EntityManagerFactory entityManagerFactory;
    private HibernateController hibernateController;
private Manager manager;



    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.manager = (Manager) user;
        this.hibernateController = new HibernateController(entityManagerFactory);
        populateProductList();
        populateUserList();
        if(!manager.isAdmin()){
            addNewUserM.setVisible(false);
            removeUser.setVisible(false);
            editUser.setVisible(false);
            addNewProduct.setVisible(false);
            deleteProduct.setVisible(false);
            editProduct.setVisible(false);

        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        singleProduct.setVisible(false);
        singleUser.setVisible(false);
        entityManagerFactory = Persistence.createEntityManagerFactory("Eshop");
        hibernateController = new HibernateController(entityManagerFactory);
        users = hibernateController.getAllUsers();
        userList.getItems().addAll(users);
        populateProductList();
        populateUserList();
        populateUnassignedTasksList();
        populateAssignedTasksList();
        populateCompletedTasksList();
        productList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showProduct());
        userList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showUser());
      //  unassignedTasksList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showUnassignedTask());
     //   assignedTasksList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showAssignedTask());
        allOrdersRadio.selectedProperty().addListener((observable, oldValue, newValue) -> filterOrders());
        myOrdersRadio.selectedProperty().addListener((observable, oldValue, newValue) -> filterOrders());

    }

    public void populateUnassignedTasksList(){
        unassignedTasksList.setCellFactory(lv -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + "  |  " + item.getClient().getId() +" | " + item.getDate()+ " | " + item.getStatus());
                }
            }
        });

        unassignedTasksList.getItems().clear();
        unassignedTasksList.getItems().addAll(hibernateController.getAllUnassignedOrders());
    }
    public void populateAssignedTasksList(){
        assignedTasksList.setCellFactory(lv -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + "  |  " + item.getClient().getId() +" | " + item.getDate()+ " | " + item.getStatus()+ " | " + item.getManager().getId());
                }
            }
        });

        assignedTasksList.getItems().clear();
        assignedTasksList.getItems().addAll(hibernateController.getAllClaimedOders());
    }


    public void populateCompletedTasksList(){
        completedTasksList.setCellFactory(lv -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + "  |  " + item.getClient().getId() +" | " + item.getDate()+ " | " + item.getStatus()+ " | " + item.getManager().getId());
                }
            }
        });

        completedTasksList.getItems().clear();
        completedTasksList.getItems().addAll(hibernateController.getAllCompletedOrders());
    }

    public void unassignedTasksInfo() throws IOException {
        FXMLLoader fxmlLoader =  new FXMLLoader(HelloApplication.class.getResource("managerOrderView.fxml"));
        Parent parent = fxmlLoader.load();
        ManagerOrderView managerOrderView = fxmlLoader.getController();
        managerOrderView.setOnOrderUpdated(this::refreshTaskLists);

        managerOrderView.setData(entityManagerFactory, manager , (Order) unassignedTasksList.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Order info");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void assignedTasksInfo() throws IOException {
        FXMLLoader fxmlLoader =  new FXMLLoader(HelloApplication.class.getResource("managerOrderView.fxml"));
        Parent parent = fxmlLoader.load();
        ManagerOrderView managerOrderView = fxmlLoader.getController();
        managerOrderView.setOnOrderUpdated(this::refreshTaskLists);

        managerOrderView.setData(entityManagerFactory, manager , (Order) assignedTasksList.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Order info");
        stage.setScene(new Scene(parent));
        stage.show();
    }
    public void completedTasksInfo() throws IOException {
        FXMLLoader fxmlLoader =  new FXMLLoader(HelloApplication.class.getResource("managerOrderView.fxml"));
        Parent parent = fxmlLoader.load();
        ManagerOrderView managerOrderView = fxmlLoader.getController();
        managerOrderView.setOnOrderUpdated(this::refreshTaskLists);


        managerOrderView.setData(entityManagerFactory, manager , (Order) completedTasksList.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Order info");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void showProduct() {
        singleProduct.setVisible(true);
        Product product = productList.getSelectionModel().getSelectedItem();
        if (product != null) {
            singleProduct.setVisible(true);
            productName.setText(product.getTitle());
            productDescription.setText(product.getDescription());
            productId.setText("Id: " + String.valueOf(product.getId()));
            attribute1.setText("manufacturer " + product.getManufacturer());
            attribute2.setText(product.getCategory());
            attribute3.setText("Unit price: " + String.valueOf(product.getPrice() + "€"));
            attribute4.setText("Current stock: " + String.valueOf(product.getStock()));
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
                    comment.setUser(this.manager);
                    comment.setDateCreated(new Date(System.currentTimeMillis()).toLocalDate());

                    hibernateController.addComment(comment);


                    loadComments(comments);


                    newCommentField.clear();
                }
            });


            commentVBox.getChildren().addAll(newCommentField, newCommentButton);
        }
    }

    public void showUser() {
        singleUser.setVisible(true);
        User user = userList.getSelectionModel().getSelectedItem();
        if (user != null) {
            singleUser.setVisible(true);
            userName.setText(user.getName());
            userSurname.setText(user.getSurname());
            userId.setText("Id: " + String.valueOf(user.getId()));
            userBirthDate.setText("Birth date: " + user.getBirthDate());
            userLogin.setText("Login: " + user.getLogin());

            if (user.getClass() == Manager.class) {
                Manager manager = (Manager) user;
                userEmploymentDate.setText("Employment date: " + manager.getEmploymentDate());
                userMedCertificate.setText("Med certificate: " + manager.getMedCertificate());
                userCardNo.setVisible(false);
                userAddress.setVisible(false);
                userEmploymentDate.setVisible(true);
                userMedCertificate.setVisible(true);
                if (manager.isAdmin()) {
                    userStatus.setText("Status: Admin");
                } else {
                    userStatus.setText("Status: Manager");
                }

            } else {
                Customer customer = (Customer) user;
                userStatus.setText("Status: Customer");
                userCardNo.setVisible(true);
                userAddress.setVisible(true);
                userEmploymentDate.setVisible(false);
                userMedCertificate.setVisible(false);
                userCardNo.setText("Card no: " + customer.getCardNo());
                userAddress.setText("Address: " + customer.getAddress());

            }
        }
    }

    private void populateProductList() {

        productList.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + "  |  " + item.getTitle()); // Assuming Product has a getTitle method
                }
            }
        });

        productList.getItems().clear();
        productList.getItems().addAll(hibernateController.getAllProducts());

    }
    //need to update all the lists when i add/delete/edit a product, also need to update the single product view

    private void populateUserList() {
        userList.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + "  |  " + item.getLogin()); // Assuming Product has a getTitle method
                }
            }
        });

        userList.getItems().clear();
        userList.getItems().addAll(hibernateController.getAllUsers());
    }
    //need to update all the lists when i add/delete/edit a user, also need to update the single user view

    public void addNewProduct() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productManager.fxml"));
        Parent parent = fxmlLoader.load();
        ProductController productController = fxmlLoader.getController();
        productController.setOnProductUpdated(this::refreshProductList);

        productController.setData(entityManagerFactory, "create", null);

        Stage stage = new Stage();
        stage.setTitle("Product");
        stage.setScene(new Scene(parent));
        stage.show();

        //when i close the window i need to update the product list

    }

    public void editProduct() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("productManager.fxml"));
        Parent parent = fxmlLoader.load();
        ProductController productController = fxmlLoader.getController();
        productController.setOnProductUpdated(this::refreshProductList);
        productController.setData(entityManagerFactory, "edit", productList.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        stage.setTitle("Product");
        stage.setScene(new Scene(parent));
        stage.show();


    }

    public void deleteProduct() throws IOException {
        Product product = productList.getSelectionModel().getSelectedItem();
        if (product != null) {
            hibernateController.deleteProduct(product);


            populateProductList();
            singleProduct.setVisible(false);

        }
    }
    public void searchProduct() {
        String search = searchProductField.getText();
        if (search != null) {
            List<Product> products = hibernateController.getAllProducts();
            List<Product> searchResults = new ArrayList<>();
            for (Product product : products) {
                String title = product.getTitle();
                if (title != null && title.contains(search)) {
                    searchResults.add(product);
                }
            }
            productList.getItems().clear();
            productList.getItems().addAll(searchResults);
        }



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
                reply.setUser(this.manager); // Set the user of the reply
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









    public void searchUser() {
        String search = searchUserField.getText();
        if (search != null) {
            List<User> users = hibernateController.getAllUsers();
            List<User> searchResults = new ArrayList<>();
            for (User user : users) {
                if (user.getLogin().contains(search)) {
                    searchResults.add(user);
                }
            }
            userList.getItems().clear();
            userList.getItems().addAll(searchResults);
        }
    }
    public void refreshProductList() {
        populateProductList();
        showProduct();
    }

    public void refreshUserList() {
        populateUserList();
        showUser();
    }
    public void refreshTaskLists(){
        populateUnassignedTasksList();
        populateAssignedTasksList();
        populateCompletedTasksList();
    }

    //USER TAB
    public void addNewUserM() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration.fxml"));
        Parent parent = fxmlLoader.load();
        RegistrationController registrationController = fxmlLoader.getController();
        registrationController.setOnUserUpdated(this::refreshUserList);
        registrationController.setData(entityManagerFactory, true, "create", null);
        Stage stage = new Stage();
        stage.setTitle("Add new user");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void editUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration.fxml"));
        Parent parent = fxmlLoader.load();
        RegistrationController registrationController = fxmlLoader.getController();
        registrationController.setOnUserUpdated(this::refreshUserList);
        registrationController.setData(entityManagerFactory, true, "edit", userList.getSelectionModel().getSelectedItem());
        Stage stage = new Stage();
        stage.setTitle("Edit user");
        stage.setScene(new Scene(parent));
        stage.show();


    }
    public void removeUser() throws IOException {
        User user = userList.getSelectionModel().getSelectedItem();
        if (user != null) {
            hibernateController.deleteUser(user);
            populateUserList();
            singleUser.setVisible(false);
        }
    }
    public void searchAssignedOrder() {
        String search = assignedOrderSearchField.getText();
        if (search != null) {
            List<Order> orders = hibernateController.getAllClaimedOders();
            List<Order> searchResults = new ArrayList<>();
            for (Order order : orders) {
                if (String.valueOf(order.getId()).contains(search)) {
                    searchResults.add(order);
                }
            }
            assignedTasksList.getItems().clear();
            assignedTasksList.getItems().addAll(searchResults);
        }
    }
    public void searchCompletedOrder() {
        String search = completedOrderSearchField.getText();
        if (search != null) {
            List<Order> orders = hibernateController.getAllCompletedOrders();
            List<Order> searchResults = new ArrayList<>();
            for (Order order : orders) {
                if (String.valueOf(order.getId()).contains(search)) {
                    searchResults.add(order);
                }
            }
            completedTasksList.getItems().clear();
            completedTasksList.getItems().addAll(searchResults);
        }
    }
    public  void filterOrders() {
        boolean ShowAllOrders = allOrdersRadio.isSelected();
        boolean ShowMyOrders = myOrdersRadio.isSelected();
        if (ShowAllOrders) {
            populateUnassignedTasksList();
            populateAssignedTasksList();
            populateCompletedTasksList();
        }
        if (ShowMyOrders) {
            List<Order> orders = hibernateController.getAllClaimedOders();
            List<Order> searchResults = new ArrayList<>();
            for (Order order : orders) {
                if (order.getManager().getId() == manager.getId()) {
                    searchResults.add(order);
                }
            }

            assignedTasksList.getItems().clear();
            assignedTasksList.getItems().addAll(searchResults);
            // refreshTaskLists();

            orders.clear();
            searchResults.clear();
        orders = hibernateController.getAllCompletedOrders();
        searchResults = new ArrayList<>();
        for (Order order : orders) {
            if (order.getManager().getId() == manager.getId()) {
                searchResults.add(order);
            }
        }
          completedTasksList.getItems().clear();
        completedTasksList.getItems().addAll(searchResults);
        }


    }

    public void generateReport() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("report.fxml"));
        Parent parent = fxmlLoader.load();
        ReportController reportController = fxmlLoader.getController();
       reportController.SetData(hibernateController);
        Stage stage = new Stage();
        stage.setTitle("Report");
        stage.setScene(new Scene(parent));
        stage.show();
    }

}