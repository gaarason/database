package gaarason.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import gaarason.database.support.Collection;
import gaarason.database.utils.FormatUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
@Slf4j
public class CollectionTests {

    private static List<Map<String, Object>> mapList = new ArrayList<>();

    @BeforeClass
    public static void beforeClass() {
        Map<String, Object> map0 = new HashMap<>();
        mapList.add(map0);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "小明");
        map1.put("age", Integer.valueOf("16"));
        mapList.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "小明明明");
        map2.put("age", Integer.valueOf("16"));
        map2.put("sex", Byte.valueOf("0"));
        map2.put("subject", null);
        mapList.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("name", "小明明明");
        map3.put("age", Integer.valueOf("16"));
        map3.put("sex", Byte.valueOf("0"));
        map3.put("subject", null);
        map3.put("something else", "一个不在entity中的属性");
        mapList.add(map3);

        Map<String, Object> map4 = new HashMap<>();
        mapList.add(map4);
    }

    @Test
    public void testToJson() throws JsonProcessingException {

        Collection objectCollection = new Collection(mapList.get(0));
        String s = objectCollection.toJson();
        Assert.assertEquals(s, "{}");

        Collection objectCollection1 = new Collection(mapList.get(1));
        String s1 = objectCollection1.toJson();
        Assert.assertEquals(s1, "{\"name\":\"小明\",\"age\":16}");

        Collection objectCollection2 = new Collection(mapList.get(2));
        String s2 = objectCollection2.toJson();
        Assert.assertEquals(s2, "{\"subject\":null,\"sex\":0,\"name\":\"小明明明\",\"age\":16}");
    }

    @Test
    public void testToString()  {
        Collection objectCollection = new Collection(mapList.get(0));
        String s = objectCollection.toString();
        Assert.assertEquals(s, "");

        Collection objectCollection1 = new Collection(mapList.get(1));
        String s1 = objectCollection1.toString();
        Assert.assertEquals(s1, "age=16&name=小明");

        Collection objectCollection2 = new Collection(mapList.get(2));
        String s2 = objectCollection2.toString();
        Assert.assertEquals(s2, "age=16&name=小明明明&sex=0");
    }

    @Test
    public void testToObject()  {
        TestCollection objectCollection = new TestCollection(mapList.get(0));
        SomeEntity     someEntity       = objectCollection.toObject();
        log.info("someEntity : {}", someEntity);
        Assert.assertNull(someEntity.getName());

        TestCollection objectCollection1 = new TestCollection(mapList.get(1));
        SomeEntity     someEntity1       = objectCollection1.toObject();
        log.info("someEntity1 : {}", someEntity1);
        Assert.assertEquals(someEntity1.getName(), "小明");

        TestCollection objectCollection2 = new TestCollection(mapList.get(2));
        SomeEntity     someEntity2        = objectCollection2.toObject();
        log.info("someEntity2 : {}", someEntity2);
        Assert.assertEquals(someEntity2.getSex(), Byte.valueOf("0"));

        TestCollection objectCollection3 = new TestCollection(mapList.get(3));
        SomeEntity     someEntity3        = objectCollection3.toObject();
        log.info("someEntity3 : {}", someEntity3);
        Assert.assertEquals(someEntity2.getSex(), Byte.valueOf("0"));
    }

    @Data
    public static class SomeEntity {
        private String  name;

        private Integer age;

        private Byte    sex;

        private String  subject;
    }

    public static class TestCollection extends Collection<SomeEntity>{

        /**
         * 新建集合
         * @param metadataMap 元数据
         */
        public TestCollection(Map<String, Object> metadataMap) {
            super(metadataMap);
        }
    }
}
