import connectivity.HibernateUtil;
import configuration.Setting;
import configuration.StatusBar;
import controller.HomeController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.hibernate.Session;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;



public class Main extends Application {
    @Override
    public void start(Stage primaryStage){
        System.out.println(System.getProperty("user.home") + File.separatorChar + "My Documents");
        File targetFile = new File(System.getProperty("user.home") + File.separatorChar + "My Documents" + File.separatorChar + "Posterminal" + File.separatorChar + "config.properties");
        File parent = targetFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        //home view
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/views/home.fxml"));
            Scene scene = new Scene(root);

            Setting setting = new Setting(targetFile);
            System.out.println(Setting.getTarget());
            System.out.println(Setting.getPathTheme());

            if(Setting.getPathTheme() == null || !Setting.getPathTheme().isEmpty()){
                String cssColor = this.getClass().getResource("/css/" + Setting.getPathTheme()).toExternalForm();
                String cssComponent = this.getClass().getResource("/css/component.css").toExternalForm();

                scene.getStylesheets().addAll(cssColor, cssComponent);
            }

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mysql://localhost/posapp?useLegacyDatetimeCode=false&serverTimezone=Asia/Kuala_Lumpur";
        String username = "root";
        String password = "";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,username,password);
//            new StatusBar(con.getCatalog(), "success", MaterialDesignIcon.DATABASE);
            con.isValid(1000);

            con.close();
        }catch(Exception e ) {
            System.out.println("no conn");
            e.printStackTrace();
//            new StatusBar("Database error: " + e.getCause(), "failed", MaterialDesignIcon.ALERT_CIRCLE);
        }

        //        SQLException
//        C3P0ConnectionProvider provider = (C3P0ConnectionProvider) sessionFactoryImpl.getConnectionProvider()

        Session session = HibernateUtil.getSession();
        session.doWork(connection -> {
            //You got a connection
            System.out.println("yup");
        });
        session.close();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
