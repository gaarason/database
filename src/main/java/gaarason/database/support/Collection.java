package gaarason.database.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.database.eloquent.Entity;
import gaarason.database.exception.InvalidEntityException;
import gaarason.database.utils.EntityUtil;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Collection<T> {

    /**
     * 元数据
     */
    private Map<String, Object> metadataMap;

    /**
     * 新建集合
     * @param metadataMap 元数据
     */
    public Collection(Map<String, Object> metadataMap) {
        this.metadataMap = metadataMap;
    }

    /**
     * 元数据
     * @return
     */
    public Map<String, Object> toMap() {
        return metadataMap;
    }

    /**
     * 元数据转String
     * @return eg:age=16&name=alice&sex=
     */
    public String toString() {
        Set<String> keySet   = metadataMap.keySet();
        String[]    keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String key : keyArray) {
            Object s = metadataMap.get(key);
            if (s != null) {
                sb.append(key).append("=").append(s.toString()).append("&");
            }
        }
        return rtrim(sb.toString(), "&");

    }

    /**
     * 元数据转json字符串
     * @return eg:{"subject":null,"sex":"","name":"小明明明","age":"16"}
     * @throws JsonProcessingException 元数据不可转json
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(metadataMap);
    }

    public T toObject() {
        return EntityUtil.setValueToEntity(newEntity(), metadataMap);
    }

    private T newEntity() {
        try {
            Class<T> entityClass =
                (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidEntityException();
        } catch (IllegalAccessException e) {
            throw new InvalidEntityException();
        }
    }

    /**
     * 移除字符串右侧的所有character
     * @param str       原字符串
     * @param character 将要移除的字符
     * @return 处理后的字符
     */
    private static String rtrim(String str, String character) {
        if(str.equals(""))
            return str;
        return str.substring(str.length() - 1).equals(character) ? rtrim(str.substring(0, str.length() - 1),
            character) : str;
    }

}
