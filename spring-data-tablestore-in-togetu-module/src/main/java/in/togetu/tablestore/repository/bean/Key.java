package in.togetu.tablestore.repository.bean;

import java.util.LinkedHashMap;
import java.util.Map;

public class Key {

    // 主键字段名：主键字段值
    private Map<String, Object> keys;

    public Key() {
        keys = new LinkedHashMap<>();
    }

    public Key(String name, Object value) {
        this();
        keys.put(name, value);
    }

    public Key put(String name, Object value) {
        keys.put(name, value);
        return this;
    }


    public Map<String, Object> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
    }


    @Override
    public String toString() {
        return keys.toString();
    }
}
