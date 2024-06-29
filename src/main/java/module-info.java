module com.ai.expert {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires projog.clp;
    requires transitive projog.core;
	requires transitive org.json;
	requires javafx.base;
 
    opens com.ai.expert.ui to javafx.fxml;
    opens com.ai.expert.controller to javafx.fxml;
       
    exports com.ai.expert.controller;
    exports com.ai.expert.system;
    exports com.ai.expert.ui;
}
