package configuration;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;

public class Setting {
    private static OutputStream os;
    private static InputStream is;
    private static File target;

    public Setting(File targetFile) throws IOException {
        target = targetFile;
        try {
            is = new FileInputStream(targetFile);
        } catch (Exception e) {
            try {
                os = new FileOutputStream(targetFile);

                Configurations configs = new Configurations();
                File propertiesFile = new File(Setting.target.toURI());
                FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);
                PropertiesConfiguration config = null;
                try {
                    config = builder.getConfiguration();
                    config.addProperty("theme", "defaultTheme.css");
                    config.addProperty("pathImage", System.getProperty("user.home") + File.separatorChar + "My Documents" + File.separatorChar + "Posterminal" + File.separatorChar + "Image");
                    builder.save();
                } catch (ConfigurationException e1) {
                    e1.printStackTrace();
                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } finally {
                os.close();
            }
        }
        finally {
            if (is!=null) {
                is.close();
            }
        }

    }

    private static String pathTheme;
    private static String pathFolderImages;
    private static String pathImagesDefault;

    public static File getTarget() {
        return target;
    }

    public static void setTarget(File target) {
        Setting.target = target;
    }

    public static String getPathTheme() throws ConfigurationException {
        Configurations configs = new Configurations();
        File propertiesFile = new File(Setting.getTarget().toURI());
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);
        PropertiesConfiguration config = builder.getConfiguration();
        pathTheme = config.getString("theme");
        return pathTheme;
    }

    public static void setPathTheme(String pathTheme) throws ConfigurationException {
        Configurations configs = new Configurations();
        File propertiesFile = new File(Setting.target.toURI());
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);
        PropertiesConfiguration config =  builder.getConfiguration();
        config.setProperty("theme", pathTheme);
        builder.save();

        Setting.pathTheme = pathTheme;
    }

    public static String getPathFolderImages() {
        return pathFolderImages;
    }

    public static void setPathFolderImages(String pathFolderImages) {
        Setting.pathFolderImages = pathFolderImages;
    }

    public static String getPathImagesDefault() throws ConfigurationException {
        Configurations configs = new Configurations();
        File propertiesFile = new File(Setting.getTarget().toURI());
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);
        PropertiesConfiguration config = builder.getConfiguration();
        pathImagesDefault = config.getString("pathImage");
        return pathImagesDefault;
    }

    public static void setPathImagesDefault(String pathImagesDefault) throws ConfigurationException {
        Configurations configs = new Configurations();
        File propertiesFile = new File(Setting.target.toURI());
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);
        PropertiesConfiguration config =  builder.getConfiguration();
        config.setProperty("pathImage", pathImagesDefault);
        builder.save();
        Setting.pathImagesDefault = pathImagesDefault;
    }
}
