package gaarason.database;

import gaarason.database.eloquent.OrderBy;
import gaarason.database.exception.ConfirmOperationException;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.models.RelationshipStudentTeacherModel;
import gaarason.database.models.StudentModel;
import gaarason.database.models.TeacherModel;
import gaarason.database.utils.EntityUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.internal.bytebuddy.implementation.FixedValue.value;

@FixMethodOrder(MethodSorters.JVM)
public class ModelTests extends DatabaseApplicationTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    RelationshipStudentTeacherModel relationshipStudentTeacherModel;

    @Autowired
    StudentModel studentModel;

    @Autowired
    TeacherModel teacherModel;


    @Test
    public void 新增_非entity方式() {
        List<String> columnNameList = new ArrayList<>();
        columnNameList.add("id");
        columnNameList.add("name");
        columnNameList.add("age");
        columnNameList.add("sex");
        List<String> valueList = new ArrayList<>();
        valueList.add("134");
        valueList.add("testNAme");
        valueList.add("11");
        valueList.add("1");

        int insert = studentModel.newQuery().select(columnNameList).value(valueList).insert();
        Assert.assertEquals(insert, 1);
        StudentModel.Entity entityFirst = studentModel.newQuery().where("id", "134").first();
        SimpleDateFormat    formatter   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertNotNull(entityFirst);
        Assert.assertEquals(134, entityFirst.getId().intValue());
        Assert.assertEquals(11, entityFirst.getAge().intValue());
        Assert.assertEquals("testNAme", entityFirst.getName());
        Assert.assertEquals(0, entityFirst.getTeacherId().intValue());
    }

    @Test
    public void 新增_单条记录() {
        StudentModel.Entity entity = new StudentModel.Entity();
        entity.setId(99);
        entity.setName("姓名");
        entity.setAge(Byte.valueOf("13"));
        entity.setSex(Byte.valueOf("1"));
        entity.setTeacherId(0);
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        int insert = studentModel.newQuery().insert(entity);
        Assert.assertEquals(insert, 1);

        StudentModel.Entity entityFirst = studentModel.newQuery().where("id", "99").first();
        SimpleDateFormat    formatter   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertNotNull(entityFirst);
        Assert.assertEquals(entity.getId(), entityFirst.getId());
        Assert.assertEquals(entity.getAge(), entityFirst.getAge());
        Assert.assertEquals(entity.getName(), entityFirst.getName());
        Assert.assertEquals(entity.getTeacherId(), entityFirst.getTeacherId());
        Assert.assertEquals(formatter.format(entity.getCreatedAt()), formatter.format(entityFirst.getCreatedAt()));
        Assert.assertEquals(formatter.format(entity.getUpdatedAt()), formatter.format(entityFirst.getUpdatedAt()));
    }


    @Test
    public void 新增_多条记录() {
        List<StudentModel.Entity> entityList = new ArrayList<>();
        for (int i = 99; i< 1000 ; i++){
            StudentModel.Entity entity = new StudentModel.Entity();
            entity.setId(i);
            entity.setName("姓名");
            entity.setAge(Byte.valueOf("13"));
            entity.setSex(Byte.valueOf("1"));
            entity.setTeacherId(i * 3);
            entity.setCreatedAt(new Date());
            entity.setUpdatedAt(new Date());
            entityList.add(entity);
        }
        int insert = studentModel.newQuery().insert(entityList);
        Assert.assertEquals(insert, 901);

        List<StudentModel.Entity> entityList1 =
                studentModel.newQuery().whereBetween("id", "300", "350").orderBy("id", OrderBy.DESC).get();
        Assert.assertEquals(entityList1.size(), 51);
        Assert.assertEquals(entityList1.get(0).getTeacherId().intValue(), 1050);
    }

    @Test
    public void 更新_普通更新(){
        int update = studentModel.newQuery().data("name", "xxcc").where("id", "3").update();
        Assert.assertEquals(update, 1);

        StudentModel.Entity entity = studentModel.newQuery().where("id", "3").firstOrFail();
        Assert.assertEquals(entity.getId().intValue(), 3);
        Assert.assertEquals(entity.getName(), "xxcc");


        int update2 = studentModel.newQuery().data("name", "vvv").where("id", ">","3").update();
        Assert.assertEquals(update2, 7);
        List<StudentModel.Entity> entityList = studentModel.newQuery().whereRaw("id>3").get();
        Assert.assertEquals(entityList.size(), 7);
        for(StudentModel.Entity gg : entityList){
            Assert.assertEquals(gg.getName(), "vvv");
        }
    }
    @Test
    public void 更新_字段自增自减(){
        int update = studentModel.newQuery().dataDecrement("age", 2).whereRaw("id=4").update();
        Assert.assertEquals(update, 1);
        StudentModel.Entity entity = studentModel.newQuery().where("id", "4").firstOrFail();
        Assert.assertEquals(entity.getId().intValue(), 4);
        Assert.assertEquals(entity.getAge(), Byte.valueOf("9"));

        int update2 = studentModel.newQuery().dataIncrement("age", 4).whereRaw("id=4").update();
        Assert.assertEquals(update2, 1);
        StudentModel.Entity entity2 = studentModel.newQuery().where("id", "4").firstOrFail();
        Assert.assertEquals(entity2.getId().intValue(), 4);
        Assert.assertEquals(entity2.getAge(), Byte.valueOf("13"));

    }

    @Test
    public void 更新_通过MAP更新(){
        Map<String, String> map = new HashMap<>();
        map.put("name", "gggg");
        map.put("age", "7");

        int update = studentModel.newQuery().data(map).where("id", "3").update();
        Assert.assertEquals(update, 1);
        StudentModel.Entity entity = studentModel.newQuery().where("id", "3").firstOrFail();
        Assert.assertEquals(entity.getId().intValue(), 3);
        Assert.assertEquals(entity.getName(), "gggg");
        Assert.assertEquals(entity.getAge(), Byte.valueOf("7"));

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().data("name", "ee").update();
    }

    @Test
    public void 更新_通过entity更新(){
        StudentModel.Entity entity1 = new StudentModel.Entity();
        entity1.setAge(Byte.valueOf("7"));
        entity1.setName("ggg");
        int update = studentModel.newQuery().where("id", "3").update(entity1);
        Assert.assertEquals(update, 1);
        StudentModel.Entity entity = studentModel.newQuery().where("id", "3").firstOrFail();
        Assert.assertEquals(entity.getId().intValue(), 3);
        Assert.assertEquals(entity.getName(), "ggg");
        Assert.assertEquals(entity.getAge(), Byte.valueOf("7"));
        Assert.assertEquals(entity.getSex(), Byte.valueOf("1"));

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().update(entity1);
    }

    @Test
    public void 删除_硬(){
        int id = studentModel.newQuery().where("id", "3").delete();
        Assert.assertEquals(id, 1);

        StudentModel.Entity id1 = studentModel.newQuery().where("id", "3").first();
        Assert.assertNull(id1);

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().delete();

    }

    @Test
    public void 删除_软(){
        int id = studentModel.newQuery().where("id", "3").delete();
        Assert.assertEquals(id, 1);

        StudentModel.Entity id1 = studentModel.newQuery().where("id", "3").first();
        Assert.assertNull(id1);
    }

    @Test
    public void 查询_单条记录() {
        StudentModel.Entity first1 = studentModel.newQuery().select("name").select("id").first();
        Assert.assertNotNull(first1);
        Assert.assertEquals(first1.getId(), new Integer(1));
        Assert.assertEquals(first1.getId().intValue(), 1);
        Assert.assertEquals(first1.getName(), "小明");
        Assert.assertNull(first1.getAge());
        Assert.assertNull(first1.getTeacherId());
        Assert.assertNull(first1.getCreatedAt());
        Assert.assertNull(first1.getUpdatedAt());

        StudentModel.Entity first2 = studentModel.newQuery().select("name", "id", "created_at").first();
        Assert.assertNotNull(first2);
        Assert.assertEquals(first2.getId(), new Integer(1));
        Assert.assertEquals(first2.getId().intValue(), 1);
        Assert.assertEquals(first2.getName(), "小明");
        Assert.assertNull(first2.getAge());
        Assert.assertNull(first2.getTeacherId());
        Assert.assertEquals(first2.getCreatedAt().toString(), "2009-03-14 17:15:23.0");
        Assert.assertNull(first2.getUpdatedAt());

        Assert.assertNotNull(first1);
        Assert.assertEquals(first1.getId(), new Integer(1));
        Assert.assertEquals(first1.getId().intValue(), 1);
        Assert.assertEquals(first1.getName(), "小明");
        Assert.assertNull(first1.getAge());
        Assert.assertNull(first1.getTeacherId());
        Assert.assertNull(first1.getCreatedAt());
        Assert.assertNull(first1.getUpdatedAt());

        StudentModel.Entity first3 =
                studentModel.newQuery().select("name", "id").where("id", "not found").first();
        Assert.assertNull(first3);

        StudentModel.Entity first5 = studentModel.newQuery().first();
        System.out.println(first5);
        Assert.assertNotNull(first5);
        Assert.assertEquals(first5.getId(), new Integer(1));
        Assert.assertEquals(first5.getId().intValue(), 1);
        Assert.assertEquals(first5.getName(), "小明");
        Assert.assertEquals(first5.getAge().intValue(), 6);
        Assert.assertEquals(first5.getTeacherId().intValue(), 0);
        Assert.assertEquals(first5.getCreatedAt().toString(), "2009-03-14 17:15:23.0");
        Assert.assertEquals(first5.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");

        thrown.expect(EntityNotFoundException.class);
        studentModel.newQuery().select("name", "id").where("id", "not found").firstOrFail();
    }

    @Test
    public void 查询_多条记录() {
        List<StudentModel.Entity> entities1 = studentModel.newQuery().select("name").select("id").get();
        Assert.assertEquals(entities1.size(), 10);

        List<StudentModel.Entity> entities2 = studentModel.newQuery().get();
        StudentModel.Entity       entity2   = entities2.get(0);
        System.out.println(entity2);
        Assert.assertNotNull(entity2);
        Assert.assertEquals(entity2.getId(), new Integer(1));
        Assert.assertEquals(entity2.getId().intValue(), 1);
        Assert.assertEquals(entity2.getName(), "小明");
        Assert.assertEquals(entity2.getAge().intValue(), 6);
        Assert.assertEquals(entity2.getTeacherId().intValue(), 0);
        Assert.assertEquals(entity2.getCreatedAt().toString(), "2009-03-14 17:15:23.0");
        Assert.assertEquals(entity2.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");
    }

    @Test
    public void 条件_字段之间比较() {
        StudentModel.Entity first = studentModel.newQuery().whereColumn("id", ">", "sex").first();
        Assert.assertNotNull(first);
        System.out.println(first);
        Assert.assertEquals(first.getId(), new Integer(3));
        Assert.assertEquals(first.getId().intValue(), 3);
        Assert.assertEquals(first.getName(), "小腾");
        Assert.assertEquals(first.getAge().intValue(), 16);
        Assert.assertEquals(first.getTeacherId().intValue(), 0);
        Assert.assertEquals(first.getCreatedAt().toString(), "2009-03-14 15:11:23.0");
        Assert.assertEquals(first.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");

        StudentModel.Entity first2 = studentModel.newQuery().whereColumn("id","sex").first();
        Assert.assertNotNull(first2);
        System.out.println(first2);
        Assert.assertEquals(first.getId(), new Integer(3));
        Assert.assertEquals(first.getId().intValue(), 3);
        Assert.assertEquals(first.getName(), "小腾");
        Assert.assertEquals(first.getAge().intValue(), 16);
        Assert.assertEquals(first.getTeacherId().intValue(), 0);
        Assert.assertEquals(first.getCreatedAt().toString(), "2009-03-14 15:11:23.0");
        Assert.assertEquals(first.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");
    }

    @Test
    public void 条件_普通条件() {
        StudentModel.Entity first = studentModel.newQuery().where("id", ">", "2").first();
        Assert.assertNotNull(first);
        System.out.println(first);
        Assert.assertNotNull(first);
        Assert.assertEquals(first.getId(), new Integer(3));
        Assert.assertEquals(first.getId().intValue(), 3);
        Assert.assertEquals(first.getName(), "小腾");
        Assert.assertEquals(first.getAge().intValue(), 16);
        Assert.assertEquals(first.getTeacherId().intValue(), 0);
        Assert.assertEquals(first.getCreatedAt().toString(), "2009-03-14 15:11:23.0");
        Assert.assertEquals(first.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");


        List<StudentModel.Entity> entityList1 = studentModel.newQuery().where("id", ">", "2").get();
        Assert.assertEquals(entityList1.size(), 8);

        List<StudentModel.Entity> entityList2 = studentModel.newQuery().where("id", ">", "2")
                                                            .where("id", "<", "7").get();
        Assert.assertEquals(entityList2.size(), 4);

        StudentModel.Entity entity2 = studentModel.newQuery().where("id", "4").firstOrFail();
        Assert.assertEquals(entity2.getId().intValue(), 4);

        StudentModel.Entity entity1 = studentModel.newQuery()
                                                  .where("created_at", ">=", "2009-03-15 22:15:23.0")
                                                  .firstOrFail();
        Assert.assertEquals(entity1.getId().intValue(), 9);

        StudentModel.Entity entity3 = studentModel.newQuery()
                                                  .where("created_at", ">=", "2009-03-15 22:15:23")
                                                  .firstOrFail();
        Assert.assertEquals(entity3.getId().intValue(), 9);

    }

    @Test
    public void 条件_Between() {
        List<StudentModel.Entity> entityList1 = studentModel.newQuery().whereBetween("id", "3", "5").get();
        Assert.assertEquals(entityList1.size(), 3);

        List<StudentModel.Entity> entityList2 = studentModel.newQuery().whereBetween("id", "3", "5").whereNotBetween(
                "id", "3", "4").get();
        Assert.assertEquals(entityList2.size(), 1);
    }

    @Test
    public void 条件_in() {
        List<String> idList = new ArrayList<>();
        idList.add("4");
        idList.add("5");
        idList.add("6");
        idList.add("7");
        List<StudentModel.Entity> entityList1 = studentModel.newQuery().whereIn("id", idList).get();
        Assert.assertEquals(entityList1.size(), 4);

        List<String> idList2 = new ArrayList<>();
        idList2.add("10");
        idList2.add("9");
        idList2.add("7");

        List<StudentModel.Entity> entityList2 = studentModel.newQuery().whereIn("id", idList).whereNotIn("id",
                idList2).get();
        Assert.assertEquals(entityList2.size(), 3);
    }

    @Test
    public void 条件_null() {
        List<StudentModel.Entity> entityList1 = studentModel.newQuery().whereNotNull("id").get();
        Assert.assertEquals(entityList1.size(), 10);

        List<StudentModel.Entity> entityList2 = studentModel.newQuery().whereNull("age").get();
        Assert.assertEquals(entityList2.size(), 0);
    }

    @Test
    public void 条件_exists() {
        // todo

    }

    @Test
    public void 排序() {
        StudentModel.Entity first = studentModel.newQuery().orderBy("id", OrderBy.DESC).first();
        Assert.assertNotNull(first);
        Assert.assertEquals(first.getId().intValue(), 10);

        StudentModel.Entity first2 = studentModel.newQuery()
                                                 .where("id", "<>", "10")
                                                 .orderBy("id", OrderBy.DESC)
                                                 .first();
        Assert.assertNotNull(first2);
        Assert.assertEquals(first2.getId().intValue(), 9);
    }

    @Test
    public void 偏移量() {
        List<StudentModel.Entity> entityList1 = studentModel.newQuery().orderBy("id", OrderBy.DESC).limit(2, 3).get();
        Assert.assertNotNull(entityList1);
        Assert.assertEquals(entityList1.size(), 3);

        List<StudentModel.Entity> entityList2 = studentModel.newQuery().orderBy("id", OrderBy.DESC).limit(8, 3).get();
        Assert.assertNotNull(entityList2);
        Assert.assertEquals(entityList2.size(), 2);
    }


}
