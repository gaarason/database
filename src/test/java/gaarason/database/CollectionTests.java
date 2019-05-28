package gaarason.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import gaarason.database.models.StudentSingleModel;
import gaarason.database.support.Collection;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
@Slf4j
public class CollectionTests extends DatabaseApplicationTests {
    @Resource
    DataSource dataSourceMaster0;

    private Collection<StudentSingleModel.Entity> collection0;

    private Collection<StudentSingleModel.Entity> collection1;

    private Collection<StudentSingleModel.Entity> collection2;

    private Collection<StudentSingleModel.Entity> collection3;

    @Before
    public void before() throws SQLException {
        Connection        connection        = dataSourceMaster0.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from student limit 1");
        ResultSet         resultSet         = preparedStatement.executeQuery();
        collection0 = new Collection<>(StudentSingleModel.Entity.class, resultSet, false);
        connection.close();


        Connection connection1 = dataSourceMaster0.getConnection();
        PreparedStatement preparedStatement1 = connection1.prepareStatement("select * from student order by " +
            "id desc limit 3 ");
        ResultSet resultSet1 = preparedStatement1.executeQuery();
        collection1 = new Collection<>(StudentSingleModel.Entity.class, resultSet1, false);
        connection1.close();

        Connection connection2 = dataSourceMaster0.getConnection();
        PreparedStatement preparedStatement2 = connection2.prepareStatement("select count(*) as re from student limit" +
            " 1");
        ResultSet resultSet2 = preparedStatement2.executeQuery();
        collection2 = new Collection<>(StudentSingleModel.Entity.class, resultSet2, false);
        connection2.close();


        Connection        connection3        = dataSourceMaster0.getConnection();
        PreparedStatement preparedStatement3 = connection3.prepareStatement("select * from student limit 3");
        ResultSet         resultSet3         = preparedStatement3.executeQuery();
        collection3 = new Collection<>(StudentSingleModel.Entity.class, resultSet3,false);
        connection3.close();
    }

    @Test
    public void testToJson() throws JsonProcessingException {
        String s = collection0.toJson();
        Assert.assertEquals(s,
            "{\"updated_at\":1272118263000,\"teacher_id\":0,\"sex\":2,\"name\":\"小明\",\"created_at\":1237022123000,\"id\":1,\"age\":6}");

        String s1 = collection1.toJsonMultidimensional();
        Assert.assertEquals(s1,
            "[{\"updated_at\":1272118263000,\"teacher_id\":0,\"sex\":1,\"name\":\"象帕\",\"created_at\":1237090523000,\"id\":10,\"age\":15},{\"updated_at\":1272118263000,\"teacher_id\":0,\"sex\":1,\"name\":\"莫西卡\",\"created_at\":1237126523000,\"id\":9,\"age\":17},{\"updated_at\":1272118263000,\"teacher_id\":0,\"sex\":1,\"name\":\"金庸\",\"created_at\":1237025903000,\"id\":8,\"age\":17}]");

        String s2 = collection2.toJsonMultidimensional();
        Assert.assertEquals(s2, "[{\"re\":10}]");

    }

    @Test
    public void testToMap() {
        Map map = collection0.toMap();
        log.info("map : {}", map);
        Map map1 = collection1.toMap();
        log.info("map1 : {}", map1);
        Map map2 = collection2.toMap();
        log.info("map2 : {}", map2);

//        Collection objectCollection1 = new Collection(mapList.get(1));
//        Map        map1              = objectCollection1.toMap();
//        log.info("map1 : {}", map1);
//
//        Collection objectCollection2 = new Collection(mapList.get(2));
//        Map        map2              = objectCollection2.toMap();
//        log.info("map2 : {}", map2);
//
//        Collection objectCollection3 = new Collection(mapList.get(3));
//        Map        map3              = objectCollection3.toMap();
//        log.info("map3 : {}", map3);

    }

    @Test
    public void testToMapMultidimensional() {
        List list = collection0.toMapMultidimensional();
        log.info("list : {}", list);
        List list1 = collection1.toMapMultidimensional();
        log.info("list1 : {}", list1);
        List list2 = collection2.toMapMultidimensional();
        log.info("list2 : {}", list2);
    }

    @Test
    public void testToString() {
        String s = collection0.toSearch();
        Assert.assertEquals(s,
            "age=6&created_at=2009-03-14 17:15:23.0&id=1&name=小明&sex=2&teacher_id=0&updated_at=2010-04-24 22:11:03.0");

        String s1 = collection1.toSearch();
        Assert.assertEquals(s1,
            "age=15&created_at=2009-03-15 12:15:23.0&id=10&name=象帕&sex=1&teacher_id=0&updated_at=2010-04-24 22:11:03.0");

        String s2 = collection2.toSearch();
        Assert.assertEquals(s2, "re=10");
    }

    @Test
    public void testToStringMultidimensional() {
        String s = collection0.toSearchMultidimensional();
        Assert.assertEquals(s,
            "0[age]=6&0[created_at]=2009-03-14 17:15:23.0&0[id]=1&0[name]=小明&0[sex]=2&0[teacher_id]=0&0[updated_at]=2010-04-24 22:11:03.0");

        String s1 = collection1.toSearchMultidimensional();
        Assert.assertEquals(s1,
            "0[age]=15&0[created_at]=2009-03-15 12:15:23" +
                ".0&0[id]=10&0[name]=象帕&0[sex]=1&0[teacher_id]=0&0[updated_at]=2010-04-24 22:11:03.0&1[age]=17&1[created_at]=2009-03-15 22:15:23.0&1[id]=9&1[name]=莫西卡&1[sex]=1&1[teacher_id]=0&1[updated_at]=2010-04-24 22:11:03.0&2[age]=17&2[created_at]=2009-03-14 18:18:23.0&2[id]=8&2[name]=金庸&2[sex]=1&2[teacher_id]=0&2[updated_at]=2010-04-24 22:11:03.0");
        String s2 = collection2.toSearchMultidimensional();
        Assert.assertEquals(s2, "0[re]=10");
    }

    @Test
    public void testToObject() {
        StudentSingleModel.Entity entity = collection0.toObject();
        log.info("entity : {}", entity);
        Assert.assertEquals(entity.getAge(), Byte.valueOf("6"));
        Assert.assertEquals(entity.getName(), "小明");

        StudentSingleModel.Entity entity1 = collection1.toObject();
        log.info("entity1 : {}", entity1);
        Assert.assertEquals(entity1.getAge(), Byte.valueOf("15"));
        Assert.assertEquals(entity1.getName(), "象帕");


    }

    @Test
    public void testToObjectList() {
        List<StudentSingleModel.Entity> entityList = collection0.toObjectList();
        log.info("entityList : {}", entityList);
        Assert.assertEquals(entityList.size(), 1);
        Assert.assertEquals(entityList.get(0).getName(), "小明");

        List<StudentSingleModel.Entity> entityList1 = collection1.toObjectList();
        log.info("entityList1 : {}", entityList1);
        Assert.assertEquals(entityList1.size(), 3);
        Assert.assertEquals(entityList1.get(0).getName(), "象帕");
        Assert.assertEquals(entityList1.get(1).getName(), "莫西卡");
        Assert.assertEquals(entityList1.get(2).getName(), "金庸");


    }

}
