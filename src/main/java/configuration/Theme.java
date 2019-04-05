package configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Theme {
    private int id;
    private File file;
    private String name;
    private String slugName;
    private String pathFile;
    private File defaultCss;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        setPathFile(file.getAbsolutePath());
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlugName() {
        return slugName;
    }

    public void setSlugName(String slugName) {
        this.slugName = slugName;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public List<Theme> getList(){
        List<Theme> list = new ArrayList<>();

        Theme defaultTheme = new Theme();
        defaultTheme.setId(1);
        defaultTheme.setName("Default Theme");
        defaultTheme.setSlugName("defaultTheme.css");
        list.add(defaultTheme);

        Theme brownTheme = new Theme();
        brownTheme.setId(2);
        brownTheme.setName("Brown Theme");
        brownTheme.setSlugName("brownTheme.css");
        list.add(brownTheme);

        Theme lightGreenTheme = new Theme();
        lightGreenTheme.setId(3);
        lightGreenTheme.setName("Light Green Theme");
        lightGreenTheme.setSlugName("lightGreenTheme.css");
        list.add(lightGreenTheme);

        Theme orangeTheme = new Theme();
        orangeTheme.setId(4);
        orangeTheme.setName("Orange Theme");
        orangeTheme.setSlugName("orangeTheme.css");
        list.add(orangeTheme);

        Theme pinkTheme = new Theme();
        pinkTheme.setId(5);
        pinkTheme.setName("Pink Theme");
        pinkTheme.setSlugName("pinkTheme.css");
        list.add(pinkTheme);

        Theme purpleTheme = new Theme();
        purpleTheme.setId(6);
        purpleTheme.setName("Purple Theme");
        purpleTheme.setSlugName("purpleTheme.css");
        list.add(purpleTheme);

        Theme tealTheme = new Theme();
        tealTheme.setId(7);
        tealTheme.setName("Teal Theme");
        tealTheme.setSlugName("tealTheme.css");
        list.add(tealTheme);


        return list;
    }
}
