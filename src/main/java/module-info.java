module mp3.dam.elpuig.pokeFX {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.json;
    requires java.net.http;
    requires java.logging;
    requires java.base;

    opens mp3.dam.elpuig.pokeFX to javafx.fxml;
    opens mp3.dam.elpuig.pokeFX.control to javafx.fxml;
    opens mp3.dam.elpuig.pokeFX.model to javafx.base;
    
    exports mp3.dam.elpuig.pokeFX;
    exports mp3.dam.elpuig.pokeFX.control;
    exports mp3.dam.elpuig.pokeFX.model;
}