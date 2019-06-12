package gaarason.database.eloquent;

import java.util.*;

public class RecordList<T> extends ArrayList<Record<T>> {

    public List<T> toObjectList() {
        List<T> list = new ArrayList<>();
        for (Record<T> record : this) {
            list.add(record.toObject());
        }
        return list;
    }

    public List<Map<String, Object>> toMapList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Record<T> record : this) {
            list.add(record.toMap());
        }
        return list;
    }


}
