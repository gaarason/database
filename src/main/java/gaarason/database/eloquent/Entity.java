package gaarason.database.eloquent;


import java.io.Serializable;
import java.util.HashMap;

public interface Entity extends Serializable,Cloneable {

    default HashMap<String, Object> toHashMap(){
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        return stringObjectHashMap;
    }

}
