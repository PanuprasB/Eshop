module com.example.kursinis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires com.almasb.fxgl.all;
    requires mysql.connector.j;
requires  lombok;
    requires  org.hibernate.orm.core;
    requires  jakarta.persistence;

    opens com.example.kursinis to javafx.fxml, org.hibernate.orm.core;

    exports com.example.kursinis;
    exports com.example.kursinis.Controllers;
    opens com.example.kursinis.Controllers to javafx.fxml, org.hibernate.orm.core;
    exports com.example.kursinis.model;
    opens com.example.kursinis.model to javafx.fxml, org.hibernate.orm.core;


}