package configuration;

import com.jfoenix.controls.JFXToolbar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class StatusBar {

    private static String messageStatus;
    private static String status;
    private static MaterialDesignIconView iconView;
    private static MaterialDesignIcon icon;
    private static Label label;
    private static BorderPane borderPane;
    private static JFXToolbar toolbar;


    public StatusBar(String messageStatus, String status,MaterialDesignIcon icon) {
        StatusBar.messageStatus = messageStatus;
        StatusBar.status = status;
        StatusBar.icon = icon;

        showStatus();
    }

    private void showStatus() {
        label.setText(messageStatus);
        iconView.setIcon(icon);
        iconView.setId("icon-"+status);
    }

    public static BorderPane getBorderPane() {
        return borderPane;
    }

    public static void setBorderPane(BorderPane borderPane) {
        StatusBar.borderPane = borderPane;
    }

    public static MaterialDesignIconView getIconView() {
        return iconView;
    }

    public static void setIconView(MaterialDesignIconView iconView) {
        StatusBar.iconView = iconView;
    }

    public static JFXToolbar getToolbar() {
        return toolbar;
    }

    public static void setToolbar(JFXToolbar toolbar) {
        StatusBar.toolbar = toolbar;
    }

    public static Label getLabel() {
        return label;
    }

    public static void setLabel(Label label) {
        StatusBar.label = label;
    }

    public static MaterialDesignIcon getIcon() {
        return icon;
    }

    public static void setIcon(MaterialDesignIcon icon) {
        StatusBar.icon = icon;
    }
}
