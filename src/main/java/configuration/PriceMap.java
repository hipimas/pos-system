package configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class PriceMap {
    private Map<Integer, String> map = null;

    public Map<Integer, String> getTypeBuyMap(){
        map = new HashMap<>();
        map.put(1, "Per Pcs");
        map.put(2, "Per Kg");
        map.put(3, "Per Gram");
        map.put(4, "Each");
        map.put(5, "Per Bundle");
        map.put(6, "Per Pkt");
        map.put(7, "Per Carton");
        map.put(8, "Per Dozen");
        map.put(9, "Per Biji");
        return map;
    }

    public Map<Integer, String> getTypeSellMap(){
        map = new HashMap<>();
        map.put(1, "Per Pcs");
        map.put(2, "Per Kg");
        map.put(3, "Per Gram");
        map.put(4, "Each");
        map.put(5, "Per Bundle");
        map.put(6, "Per Pkt");
        map.put(7, "Per Carton");
        map.put(8, "Per Dozen");
        map.put(9, "Per Biji");
        return map;
    }

    public int getKeySell(String s){
        int value = 0;
        for(Map.Entry<Integer, String> entry: getTypeSellMap().entrySet()){
            if(s.equals(entry.getValue())){
                value = entry.getKey();
            }
        }

        return value;
    }

    public String getValueSell(int i) {
        String value = null;
        for (Map.Entry<Integer, String> entry : getTypeSellMap().entrySet()) {
            if(i == entry.getKey()){
               value = entry.getValue();
            }
        }

        return value;
    }

    public int getKeyBuy(String s){
        int value = 0;
        for(Map.Entry<Integer, String> entry: getTypeBuyMap().entrySet()){
            if(s.equals(entry.getValue())){
                value = entry.getKey();
            }
        }

        return value;
    }

    public String getValueBuy(int i){
        String value = null;
        for (Map.Entry<Integer, String> entry : getTypeBuyMap().entrySet()) {
            if(i == entry.getKey()){
                value = entry.getValue();
            }
        }

        return value;
    }
}
