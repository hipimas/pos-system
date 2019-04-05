package configuration;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class StageView {

    private static Map<String, Stage> stageMap;

    public StageView() {
        stageMap = new HashMap<>();
    }

    private static Map<String, Stage> getStageMap() {
        return stageMap;
    }

    private static void setStageMap(Map<String, Stage> stageMap) {
        StageView.stageMap = stageMap;
    }

    public static void addMap(String s, Stage st){
        getStageMap().put(s, st);
    }

    public static void removeMap(String s){
        if(getStageMap().size() > 0){
            getStageMap().remove(s);
        }
    }

    public static Stage getMap(String s){
        Stage stage = null;
        for(Map.Entry<String, Stage> entry: getStageMap().entrySet()){
            if(s.equals(entry.getKey())){
                stage = entry.getValue();
            }
        }

        return stage;
    }
}
