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
}
