package gaarason.database;

import gaarason.database.eloquent.OrderBy;
import gaarason.database.eloquent.Paginate;
import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.RecordList;
import gaarason.database.exception.ConfirmOperationException;
import gaarason.database.exception.EntityNotFoundException;
import gaarason.database.exception.NestedTransactionException;
import gaarason.database.exception.SQLRuntimeException;
import gaarason.database.models.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@FixMethodOrder(MethodSorters.JVM)
public class QueryBuilderTests extends DatabaseApplicationTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    RelationshipStudentTeacherModel relationshipStudentTeacherModel;

    @Autowired
    StudentSingleModel studentModel;

    @Autowired
    StudentSingle2Model student2Model;

    @Autowired
    StudentSingle3Model student3Model;

    @Autowired
    StudentSingle5Model student5Model;

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
        StudentSingleModel.Entity entityFirst = studentModel.newQuery().where("id", "134").firstOrFail().toObject();
        SimpleDateFormat          formatter   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertNotNull(entityFirst);
        Assert.assertEquals(134, entityFirst.getId().intValue());
        Assert.assertEquals(11, entityFirst.getAge().intValue());
        Assert.assertEquals("testNAme", entityFirst.getName());
        Assert.assertEquals(0, entityFirst.getTeacherId().intValue());
    }

    @Test
    public void 新增_单条记录() {
        StudentSingleModel.Entity entity = new StudentSingleModel.Entity();
        entity.setId(99);
        entity.setName("姓名");
        entity.setAge(Byte.valueOf("13"));
        entity.setSex(Byte.valueOf("1"));
        entity.setTeacherId(0);
        entity.setCreatedAt(new Date(1312312312));
        entity.setUpdatedAt(new Date(1312312312));
        int insert = studentModel.newQuery().insert(entity);
        Assert.assertEquals(insert, 1);

        StudentSingleModel.Entity entityFirst = studentModel.newQuery().where("id", "99").firstOrFail().toObject();
        SimpleDateFormat          formatter   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertNotNull(entityFirst);
        Assert.assertEquals(entity.getId(), entityFirst.getId());
        Assert.assertEquals(entity.getAge(), entityFirst.getAge());
        Assert.assertEquals(entity.getName(), entityFirst.getName());
        Assert.assertEquals(entity.getTeacherId(), entityFirst.getTeacherId());
        // 这两个字段在entity中标记为不可更新
        Assert.assertNotEquals(formatter.format(entity.getCreatedAt()), formatter.format(entityFirst.getCreatedAt()));
        Assert.assertNotEquals(formatter.format(entity.getUpdatedAt()), formatter.format(entityFirst.getUpdatedAt()));
    }

    @Test
    public void 新增_多条记录() {
        List<StudentSingleModel.Entity> entityList = new ArrayList<>();
        for (int i = 99; i < 1000; i++) {
            StudentSingleModel.Entity entity = new StudentSingleModel.Entity();
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

//        List<StudentSingleModel.Entity> entityList1 =
        RecordList<StudentSingleModel.Entity> records = studentModel.newQuery()
            .whereBetween("id", "300", "350")
            .orderBy("id", OrderBy.DESC)
            .get();
        Assert.assertEquals(records.size(), 51);
        Assert.assertEquals(records.get(0).getEntity().getTeacherId().intValue(), 1050);
    }

    @Test
    public void 更新_普通更新() {
        int update = studentModel.newQuery().data("name", "xxcc").where("id", "3").update();
        Assert.assertEquals(update, 1);

        StudentSingleModel.Entity entity = studentModel.newQuery().where("id", "3").firstOrFail().toObject();
        Assert.assertEquals(entity.getId().intValue(), 3);
        Assert.assertEquals(entity.getName(), "xxcc");


        int update2 = studentModel.newQuery().data("name", "vvv").where("id", ">", "3").update();
        Assert.assertEquals(update2, 7);
        RecordList<StudentSingleModel.Entity> records = studentModel.newQuery().whereRaw("id>3").get();
        Assert.assertEquals(records.size(), 7);


        for (Record<StudentSingleModel.Entity> record : records) {

        }


//        for (Record<StudentSingleModel.Entity> record : records) {
//            Assert.assertEquals(record.getEntity().getName(), "vvv");
//        }

    }

    @Test
    public void 更新_字段自增自减() {
        int update = studentModel.newQuery().dataDecrement("age", 2).whereRaw("id=4").update();
        Assert.assertEquals(update, 1);
        StudentSingleModel.Entity entity = studentModel.newQuery().where("id", "4").firstOrFail().toObject();
        Assert.assertEquals(entity.getId().intValue(), 4);
        Assert.assertEquals(entity.getAge(), Byte.valueOf("9"));

        int update2 = studentModel.newQuery().dataIncrement("age", 4).whereRaw("id=4").update();
        Assert.assertEquals(update2, 1);
        StudentSingleModel.Entity entity2 = studentModel.newQuery().where("id", "4").firstOrFail().toObject();
        Assert.assertEquals(entity2.getId().intValue(), 4);
        Assert.assertEquals(entity2.getAge(), Byte.valueOf("13"));

    }

    @Test
    public void 更新_通过MAP更新() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "gggg");
        map.put("age", "7");

        int update = studentModel.newQuery().data(map).where("id", "3").update();
        Assert.assertEquals(update, 1);
        StudentSingleModel.Entity entity = studentModel.newQuery().where("id", "3").firstOrFail().toObject();
        Assert.assertEquals(entity.getId().intValue(), 3);
        Assert.assertEquals(entity.getName(), "gggg");
        Assert.assertEquals(entity.getAge(), Byte.valueOf("7"));

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().data("name", "ee").update();
    }

    @Test
    public void 更新_通过entity更新() {
        StudentSingleModel.Entity entity1 = new StudentSingleModel.Entity();
        entity1.setAge(Byte.valueOf("7"));
        entity1.setName("ggg");
        int update = studentModel.newQuery().where("id", "3").update(entity1);
        Assert.assertEquals(update, 1);
        StudentSingleModel.Entity entity = studentModel.newQuery().where("id", "3").firstOrFail().toObject();
        Assert.assertEquals(entity.getId().intValue(), 3);
        Assert.assertEquals(entity.getName(), "ggg");
        Assert.assertEquals(entity.getAge(), Byte.valueOf("7"));
        Assert.assertEquals(entity.getSex(), Byte.valueOf("1"));

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().update(entity1);
    }

    @Test
    public void 删除_硬() {
        int id = student5Model.newQuery().where("id", "3").forceDelete();
        Assert.assertEquals(id, 1);

        Record<StudentSingleModel.Entity> id1 = studentModel.newQuery().where("id", "3").first();
        Assert.assertNull(id1);

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().delete();

    }

    @Test
    public void 删除_软() {
        int id = student5Model.newQuery().where("id", "3").delete();
        Assert.assertEquals(id, 1);

        Record<StudentSingle5Model.Entity> id1 = student5Model.newQuery().where("id", "3").first();
        Assert.assertNull(id1);

        // 强行查询所有

    }

    @Test
    public void 查询_单条记录() {
        Record<StudentSingleModel.Entity> RecordFirst1 =
            studentModel.newQuery().select("name").select("id").first();
        log.info("RecordFirst1 : {}", RecordFirst1);
        Assert.assertNotNull(RecordFirst1);
        StudentSingleModel.Entity first1 = RecordFirst1.toObject();
        Assert.assertEquals(first1.getId(), new Integer(1));
        Assert.assertEquals(first1.getId().intValue(), 1);
        Assert.assertEquals(first1.getName(), "小明");
        Assert.assertNull(first1.getAge());
        Assert.assertNull(first1.getTeacherId());
        Assert.assertNull(first1.getCreatedAt());
        Assert.assertNull(first1.getUpdatedAt());

        Record<StudentSingleModel.Entity> RecordFirst2 = studentModel.newQuery().select("name", "id",
            "created_at").first();
        Assert.assertNotNull(RecordFirst2);
        StudentSingleModel.Entity first2 = RecordFirst2.toObject();
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

        Record<StudentSingleModel.Entity> first3 =
            studentModel.newQuery().select("name", "id").where("id", "not found").first();
        Assert.assertNull(first3);

        Record<StudentSingleModel.Entity> RecordFirst5 = studentModel.newQuery().first();
        System.out.println(RecordFirst5);
        Assert.assertNotNull(RecordFirst5);
        StudentSingleModel.Entity first5 = RecordFirst5.toObject();
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
        List<StudentSingleModel.Entity> entities1 = studentModel.newQuery()
            .select("name")
            .select("id")
            .get()
            .toObjectList();

        Assert.assertEquals(entities1.size(), 10);

        List<StudentSingleModel.Entity> entities2 = studentModel.newQuery().get().toObjectList();
        StudentSingleModel.Entity       entity2   = entities2.get(0);
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
    public void 查询_调用mysql中的其他函数() {
        Record<StudentSingleModel.Entity> entityRecord = studentModel.newQuery()
            .selectFunction("concat_ws", "\"-\",`name`,`id`", "newKey")
            .first();
        Assert.assertNotNull(entityRecord);
        StudentSingleModel.Entity entity          = entityRecord.toObject();
        Map<String, Object>       stringObjectMap = entityRecord.toMap();
        Assert.assertNull(entity.getId());
        Assert.assertNull(entity.getName());
        Assert.assertNull(entity.getAge());
        Assert.assertNull(entity.getTeacherId());
        Assert.assertEquals(stringObjectMap.get("newKey"), "小明-1");
    }

    @Test
    public void 查询_聚合函数() {
        Long count0 = studentModel.newQuery().where("sex", "1").group("age").count("id");
        Assert.assertEquals(count0.intValue(), 1);

        Long count = studentModel.newQuery().where("sex", "1").count("age");
        Assert.assertEquals(count.intValue(), 6);

        String max = studentModel.newQuery().where("sex", "1").max("id");
        Assert.assertEquals(max, "10");

        String min = studentModel.newQuery().where("sex", "1").min("id");
        Assert.assertEquals(min, "3");

        String avg = studentModel.newQuery().where("sex", "1").avg("id");
        Assert.assertEquals(avg, "7.1667");

        String sum = studentModel.newQuery().where("sex", "2").sum("id");
        Assert.assertEquals(sum, "12");


    }

    @Test
    public void 条件_字段之间比较() {
        Record<StudentSingleModel.Entity> entityRecord = studentModel.newQuery()
            .whereColumn("id", ">", "sex")
            .first();
        Assert.assertNotNull(entityRecord);
        System.out.println(entityRecord);
        StudentSingleModel.Entity first = entityRecord.toObject();
        Assert.assertEquals(first.getId(), new Integer(3));
        Assert.assertEquals(first.getId().intValue(), 3);
        Assert.assertEquals(first.getName(), "小腾");
        Assert.assertEquals(first.getAge().intValue(), 16);
        Assert.assertEquals(first.getTeacherId().intValue(), 0);
        Assert.assertEquals(first.getCreatedAt().toString(), "2009-03-14 15:11:23.0");
        Assert.assertEquals(first.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");

        Record<StudentSingleModel.Entity> entityRecord2 =
            studentModel.newQuery().whereColumn("id", "sex").first();
        Assert.assertNotNull(entityRecord2);
        System.out.println(entityRecord2);
        StudentSingleModel.Entity first2 = entityRecord2.toObject();
        Assert.assertEquals(first2.getId(), new Integer(2));
        Assert.assertEquals(first2.getId().intValue(), 2);
        Assert.assertEquals(first2.getName(), "小张");
        Assert.assertEquals(first2.getAge().intValue(), 11);
        Assert.assertEquals(first2.getTeacherId().intValue(), 0);
        Assert.assertEquals(first2.getCreatedAt().toString(), "2009-03-14 15:15:23.0");
        Assert.assertEquals(first2.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");
    }

    @Test
    public void 条件_普通条件() {
        Record<StudentSingleModel.Entity> entityRecord = studentModel.newQuery().where("id", ">", "2").first();
        Assert.assertNotNull(entityRecord);
        System.out.println(entityRecord);
        StudentSingleModel.Entity first = entityRecord.toObject();
        Assert.assertEquals(first.getId(), new Integer(3));
        Assert.assertEquals(first.getId().intValue(), 3);
        Assert.assertEquals(first.getName(), "小腾");
        Assert.assertEquals(first.getAge().intValue(), 16);
        Assert.assertEquals(first.getTeacherId().intValue(), 0);
        Assert.assertEquals(first.getCreatedAt().toString(), "2009-03-14 15:11:23.0");
        Assert.assertEquals(first.getUpdatedAt().toString(), "2010-04-24 22:11:03.0");


        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery()
            .where("id", ">", "2")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList1.size(), 8);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().where("id", ">", "2")
            .where("id", "<", "7").get().toObjectList();
        Assert.assertEquals(entityList2.size(), 4);

        StudentSingleModel.Entity entity2 = studentModel.newQuery().where("id", "4").firstOrFail().toObject();
        Assert.assertEquals(entity2.getId().intValue(), 4);

        StudentSingleModel.Entity entity1 = studentModel.newQuery()
            .where("created_at", ">=", "2009-03-15 22:15:23.0")
            .firstOrFail().toObject();
        Assert.assertEquals(entity1.getId().intValue(), 9);

        StudentSingleModel.Entity entity3 = studentModel.newQuery()
            .where("created_at", ">=", "2009-03-15 22:15:23")
            .firstOrFail().toObject();
        Assert.assertEquals(entity3.getId().intValue(), 9);

    }

    @Test
    public void 条件_Between() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery()
            .whereBetween("id", "3", "5")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList1.size(), 3);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery()
            .whereBetween("id", "3", "5")
            .whereNotBetween(
                "id", "3", "4")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList2.size(), 1);
    }

    @Test
    public void 条件_whereIn() {
        List<String> idList = new ArrayList<>();
        idList.add("4");
        idList.add("5");
        idList.add("6");
        idList.add("7");
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery()
            .whereIn("id", idList)
            .get()
            .toObjectList();
        Assert.assertEquals(entityList1.size(), 4);

        List<String> idList2 = new ArrayList<>();
        idList2.add("10");
        idList2.add("9");
        idList2.add("7");

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().whereIn("id", idList).whereNotIn("id",
            idList2).get().toObjectList();
        Assert.assertEquals(entityList2.size(), 3);
    }

    @Test
    public void 条件_whereIn_closure() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().whereIn("id",
            builder -> builder.select("id").where("age", ">=", "11")
        ).andWhere(
            builder -> builder.whereNotIn("sex",
                builder1 -> builder1.select("sex").where("sex", "1")
            )
        ).get().toObjectList();
        Assert.assertEquals(entityList1.size(), 3);
    }

    @Test
    public void 条件_whereNull() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().whereNotNull("id").get().toObjectList();
        Assert.assertEquals(entityList1.size(), 10);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().whereNull("age").get().toObjectList();
        Assert.assertEquals(entityList2.size(), 0);
    }

    @Test
    public void 条件_orWhere() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().where("id", "3").orWhere(
            (builder) -> builder.whereRaw("id=4")
        ).get().toObjectList();
        Assert.assertEquals(entityList1.size(), 2);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().where("id", "3").orWhere(
            (builder) -> builder.whereBetween("id", "4", "10").where("age", ">", "11")
        ).get().toObjectList();
        Assert.assertEquals(entityList2.size(), 6);
    }

    @Test
    public void 条件_andWhere() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().where("id", "3").andWhere(
            (builder) -> builder.whereRaw("id=4")
        ).get().toObjectList();
        Assert.assertEquals(entityList1.size(), 0);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().where("id", "7").andWhere(
            (builder) -> builder.whereBetween("id", "4", "10").where("age", ">", "11")
        ).get().toObjectList();
        Assert.assertEquals(entityList2.size(), 1);
        Assert.assertEquals(entityList2.get(0).getId().intValue(), 7);
    }

    @Test
    public void 条件_andWhere与orWhere无线嵌套() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().where("id", "3").orWhere(
            (builder) -> builder.where("age", ">", "11").where("id", "7").andWhere(
                (builder2) -> builder2.whereBetween("id", "4", "10").where("age", ">", "11")
            )
        ).from("student").select("id", "name").get().toObjectList();
        Assert.assertEquals(entityList1.size(), 2);
    }

    @Test
    public void 条件_exists() {
        // EXISTS用于检查子查询是否至少会返回一行数据，该子查询实际上并不返回任何数据，而是返回值True或False
        // EXISTS 指定一个子查询，检测 行 的存在。

        List<StudentSingleModel.Entity> entityList = studentModel.newQuery()
            .select("id", "name", "age")
            .whereBetween("id", "1", "2")
            .whereExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "2", "3")
            )
            .whereExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "1", "4")
            )
            .get().toObjectList();
        Assert.assertEquals(entityList.size(), 2);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery()
            .select("id", "name", "age")
            .whereBetween("id", "1", "2")
            .whereExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "2", "3")
            )
            .whereNotExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "2", "4")
            )
            .get().toObjectList();
        Assert.assertEquals(entityList2.size(), 0);
    }

    @Test
    public void GROUP() {
        List<StudentSingleModel.Entity> entities = studentModel.newQuery()
            .select("id", "age")
            .where("id", "&", "1")
            .orderBy("id", OrderBy.DESC)
            .group("sex", "id", "age")
            .get()
            .toObjectList();
        System.out.println(entities);
        Assert.assertEquals(entities.size(), 5);
        Assert.assertEquals(entities.get(0).getId().intValue(), 9);
        Assert.assertEquals(entities.get(1).getId().intValue(), 7);

        // 严格模式
        thrown.expect(SQLRuntimeException.class);
        List<StudentSingleModel.Entity> entities1 = studentModel.newQuery()
            .select("id", "name", "age")
            .where("id", "&", "1")
            .orderBy("id", OrderBy.DESC)
            .group("sex", "age")
            .group("id")
            .get()
            .toObjectList();
        System.out.println(entities1);
    }


    @Test
    public void 筛选_字段之间比较() {
        RecordList<StudentSingleModel.Entity> records = studentModel.newQuery()
            .havingColumn("age", ">", "sex").group("age", "sex").select("age", "sex")
            .get();
        Assert.assertEquals(records.size(), 5);
        System.out.println(records);
        StudentSingleModel.Entity first = records.get(0).toObject();
        Assert.assertEquals(first.getAge().intValue(), 6);
        Assert.assertEquals(first.getSex().intValue(), 2);

        RecordList<StudentSingleModel.Entity> records2 = studentModel.newQuery()
            .havingColumn("age", "<", "sex").group("age", "sex").select("age", "sex")
            .get();
        Assert.assertTrue(records2.isEmpty());
    }

    @Test
    public void 筛选_having() {
        Record<StudentSingleModel.Entity> entityRecord =
            studentModel.newQuery().select("id").group("id").where("id", "<", "3").having("id", ">=", "2").first();
        Assert.assertNotNull(entityRecord);
        System.out.println(entityRecord);
        StudentSingleModel.Entity first = entityRecord.toObject();
        Assert.assertEquals(first.getId(), new Integer(2));
    }

    @Test
    public void 筛选_havingBetween() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().group("id")
            .havingBetween("id", "3", "5").select("id")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList1.size(), 3);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery()
            .havingBetween("id", "3", "5")
            .havingNotBetween(
                "id", "3", "4").select("id").group("id")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList2.size(), 1);
    }

    @Test
    public void 筛选_havingIn() {
        List<String> idList = new ArrayList<>();
        idList.add("4");
        idList.add("5");
        idList.add("6");
        idList.add("7");
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery()
            .havingIn("id", idList).group("id").select("id")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList1.size(), 4);

        List<String> idList2 = new ArrayList<>();
        idList2.add("10");
        idList2.add("9");
        idList2.add("7");

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().whereIn("id", idList).whereNotIn("id",
            idList2).get().toObjectList();
        Assert.assertEquals(entityList2.size(), 3);
    }

    @Test
    public void 筛选_havingIn_closure() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().havingIn("id",
            builder -> builder.select("id").where("age", ">=", "11")
        ).group("id").select("id").get().toObjectList();
        Assert.assertEquals(entityList1.size(), 9);
    }

    @Test
    public void 筛选_havingNull() {
        List<StudentSingleModel.Entity> entityList1 =
            studentModel.newQuery().group("id").select("id").havingNotNull("id").get().toObjectList();
        Assert.assertEquals(entityList1.size(), 10);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery()
            .group("id")
            .select("id")
            .havingNull("id")
            .get()
            .toObjectList();
        Assert.assertEquals(entityList2.size(), 0);
    }

    @Test
    public void 筛选_orHaving() {
        List<StudentSingleModel.Entity> entityList1 =
            studentModel.newQuery().select("id").group("id").having("id",">", "3").orHaving(
                (builder) -> builder.havingRaw("id=4")
            ).get().toObjectList();
        Assert.assertEquals(entityList1.size(), 7);

        List<StudentSingleModel.Entity> entityList3 =
            studentModel.newQuery().select("id").group("id").having("id",">", "3").orHaving(
                (builder) -> builder.having("id", "4")
            ).get().toObjectList();
        Assert.assertEquals(entityList3.size(), 7);

        List<StudentSingleModel.Entity> entityList2 =
            studentModel.newQuery().select("id").group("id","age").having("id", "3").orHaving(
                (builder) -> builder.havingBetween("id", "4", "10").having("age", ">", "11")
            ).get().toObjectList();
        Assert.assertEquals(entityList2.size(), 6);
    }

    @Test
    public void 筛选_andHaving() {
        List<StudentSingleModel.Entity> entityList1 = studentModel.newQuery().select("id").group("id").having("id", "3").andHaving(
            (builder) -> builder.havingRaw("id=4")
        ).get().toObjectList();
        Assert.assertEquals(entityList1.size(), 0);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery().select("id").group("id","age").having(
            "id", "7").andHaving(
            (builder) -> builder.havingBetween("id", "4", "10").having("age", ">", "11")
        ).get().toObjectList();
        Assert.assertEquals(entityList2.size(), 1);
        Assert.assertEquals(entityList2.get(0).getId().intValue(), 7);
    }

    @Test
    public void 筛选_andHaving与orHaving无线嵌套() {
        List<StudentSingleModel.Entity> entityList1 =
            studentModel.newQuery().select("id").group("id","age","name").having(
            "id", "3").orHaving(
            (builder) -> builder.having("age", ">", "11").having("id", "7").andHaving(
                (builder2) -> builder2.havingBetween("id", "4", "10").having("age", ">", "11")
            )
        ).from("student").select("id", "name").get().toObjectList();
        Assert.assertEquals(entityList1.size(), 2);
    }

    @Test
    public void 筛选_exists() {
        // EXISTS用于检查子查询是否至少会返回一行数据，该子查询实际上并不返回任何数据，而是返回值True或False
        // EXISTS 指定一个子查询，检测 行 的存在。

        List<StudentSingleModel.Entity> entityList = studentModel.newQuery()
            .select("id", "name", "age")
            .group("id","name","age")
            .havingBetween("id", "1", "2")
            .havingExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "2", "3")
            )
            .havingExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "1", "4")
            )
            .get().toObjectList();
        Assert.assertEquals(entityList.size(), 2);

        List<StudentSingleModel.Entity> entityList2 = studentModel.newQuery()
            .select("id", "name", "age")
            .group("id","name","age")
            .havingBetween("id", "1", "2")
            .havingExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "2", "3")
            )
            .havingNotExists(
                builder -> builder.select("id", "name", "age").whereBetween("id", "2", "4")
            )
            .get().toObjectList();
        Assert.assertEquals(entityList2.size(), 0);
    }

    @Test
    public void join(){
        RecordList<StudentSingleModel.Entity> student_as_t = studentModel.newQuery()
            .select("student.*","t.age as age2")
            .join("student as t", "student.id", "=", "t.age")
            .get();
        System.out.println(student_as_t.toMapList());
    }

    @Test
    public void 排序() {
        StudentSingleModel.Entity first = studentModel.newQuery().orderBy("id", OrderBy.DESC).firstOrFail().toObject();
        Assert.assertNotNull(first);
        Assert.assertEquals(first.getId().intValue(), 10);

        StudentSingleModel.Entity first2 = studentModel.newQuery()
            .where("id", "<>", "10")
            .orderBy("id", OrderBy.DESC)
            .firstOrFail().toObject();
        Assert.assertNotNull(first2);
        Assert.assertEquals(first2.getId().intValue(), 9);
    }

    @Test
    public void union() {
        Record<StudentSingleModel.Entity> record = studentModel.newQuery()
            .union((builder -> builder.where("id", "2")))
            .firstOrFail();
        System.out.println(record);

    }

    @Test
    public void unionAll() {
        Record<StudentSingleModel.Entity> record = studentModel.newQuery()
            .unionAll((builder -> builder.where("id", "2")))
            .union((builder -> builder.where("id", "7")))
            .firstOrFail();
        System.out.println(record);

    }

    @Test
    public void 偏移量() {
        List<StudentSingleModel.Entity> entityList1 =
            studentModel.newQuery().orderBy("id", OrderBy.DESC).limit(2, 3).get().toObjectList();
        Assert.assertNotNull(entityList1);
        Assert.assertEquals(entityList1.size(), 3);

        List<StudentSingleModel.Entity> entityList2 =
            studentModel.newQuery().orderBy("id", OrderBy.DESC).limit(8, 3).get().toObjectList();
        Assert.assertNotNull(entityList2);
        Assert.assertEquals(entityList2.size(), 2);
    }

    @Test
    public void 事物() {
        thrown.expect(RuntimeException.class);
        studentModel.newQuery().transaction(() -> {
            studentModel.newQuery().where("id", "1").data("name", "dddddd").update();
            StudentSingleModel.Entity entity = studentModel.newQuery().where("id", "1").firstOrFail().toObject();
            Assert.assertEquals(entity.getName(), "dddddd");
            throw new RuntimeException("ssss");
        }, 3);

        StudentSingleModel.Entity entity = studentModel.newQuery().where("id", "1").firstOrFail().toObject();
        Assert.assertNotEquals(entity.getName(), "dddddd");
    }

    @Test
    public void 事物_多个数据连接嵌套事物() {
        // 1层事物
        studentModel.newQuery().transaction(() -> {
            studentModel.newQuery().data("name", "testttt").where("id", "9").update();
            // 2层事物
            student2Model.newQuery().transaction(() -> {
                student2Model.newQuery().data("name", "testttt").where("id", "4").update();
                try {
                    // 3层事物
                    student3Model.newQuery().transaction(() -> {
                        student3Model.newQuery().where("id", "1").data("name", "dddddd").update();
                        StudentSingle3Model.Entity entity = student3Model.newQuery()
                            .where("id", "1")
                            .firstOrFail()
                            .toObject();
                        Assert.assertEquals(entity.getName(), "dddddd");
                        throw new RuntimeException("业务上抛了个异常");
                    }, 1);
                } catch (RuntimeException e) {
                    log.info("student3Model 业务上抛了个异常, 成功捕获, 所以student3Model上的事物回滚");
                }
                // student3Model 回滚
                StudentSingle3Model.Entity entity = student3Model.newQuery().where("id", "1").firstOrFail().toObject();
                Assert.assertNotEquals(entity.getName(), "dddddd");

                // student2Model 不受影响
                StudentSingle2Model.Entity id = student2Model.newQuery()
                    .where("id", "4")
                    .firstOrFail().toObject();
                Assert.assertEquals(id.getName(), "testttt");

            }, 1);
            StudentSingleModel.Entity id = studentModel.newQuery()
                .where("id", "9")
                .firstOrFail().toObject();
            Assert.assertEquals(id.getName(), "testttt");
        }, 3);

        // 事物结束后
        StudentSingleModel.Entity id = studentModel.newQuery()
            .where("id", "9")
            .firstOrFail().toObject();
        Assert.assertEquals(id.getName(), "testttt");

        StudentSingle2Model.Entity id2 = student2Model.newQuery()
            .where("id", "4")
            .firstOrFail().toObject();
        Assert.assertEquals(id2.getName(), "testttt");

        StudentSingle3Model.Entity entity = student3Model.newQuery().where("id", "1").firstOrFail().toObject();
        Assert.assertNotEquals(entity.getName(), "dddddd");

    }

    @Test
    public void 事物_单个数据连接不可嵌套事物() {
        thrown.expect(NestedTransactionException.class);
        // 1层事物
        studentModel.newQuery().transaction(() -> {
            // 2层事物
            studentModel.newQuery().transaction(() -> {
                // 3层事物
                studentModel.newQuery().transaction(() -> {
                    try {
                        // 4层事物
                        studentModel.newQuery().transaction(() -> {
                            studentModel.newQuery().where("id", "1").data("name", "dddddd").update();
                            StudentSingleModel.Entity entity = studentModel.newQuery()
                                .where("id", "1")
                                .firstOrFail()
                                .toObject();
                            Assert.assertEquals(entity.getName(), "dddddd");
                            throw new RuntimeException("业务上抛了个异常");
                        }, 1);
                    } catch (RuntimeException e) {
                    }
                }, 1);
            }, 1);
        }, 3);
    }

    @Test
    public void 事物_lock_in_share_mode() {

    }

    @Test
    public void 事物_for_update() {

    }

    @Test
    public void 安全_更新操作需要确认() {
        int update = studentModel.newQuery().data("name", "xxcc").whereRaw("1").update();
        Assert.assertEquals(update, 10);

        List<StudentSingleModel.Entity> entities = studentModel.newQuery().get().toObjectList();
        Assert.assertEquals(entities.get(0).getName(), entities.get(2).getName());

        thrown.expect(ConfirmOperationException.class);
        studentModel.newQuery().data("name", "xxcc").update();
    }

    @Test
    public void 安全_SQL注入() {
        String 用户非法输入 = "小明\' and 0<>(select count(*) from student) and \'1";
        thrown.expect(EntityNotFoundException.class);
        studentModel.newQuery().where("name", 用户非法输入).firstOrFail();
    }

    @Test
    public void 便捷_model方法定义() {
        StudentSingleModel.Entity byId = studentModel.getById("3");
        Assert.assertEquals(byId.getName(), "小腾");

        String nameById = studentModel.getNameById("4");
        Assert.assertEquals(nameById, "小云");
    }

    @Test
    public void 分页_快速分页() {
        Paginate<StudentSingleModel.Entity> paginate = studentModel.newQuery().orderBy("id").simplePaginate(1, 3);
        System.out.println(paginate);
        Assert.assertEquals(paginate.getCurrentPage(), 1);
        Assert.assertNotNull(paginate.getFrom());
        Assert.assertNotNull(paginate.getTo());
        Assert.assertEquals(paginate.getFrom().intValue(), 1);
        Assert.assertEquals(paginate.getTo().intValue(), 3);
        Assert.assertNull(paginate.getLastPage());
        Assert.assertNull(paginate.getTotal());


        Paginate<StudentSingleModel.Entity> paginate2 = studentModel.newQuery().orderBy("id").simplePaginate(2, 3);
        System.out.println(paginate2);
        Assert.assertEquals(paginate2.getCurrentPage(), 2);
        Assert.assertNotNull(paginate2.getFrom());
        Assert.assertNotNull(paginate2.getTo());
        Assert.assertEquals(paginate2.getFrom().intValue(), 4);
        Assert.assertEquals(paginate2.getTo().intValue(), 6);
        Assert.assertNull(paginate2.getLastPage());
        Assert.assertNull(paginate2.getTotal());

        Paginate<StudentSingleModel.Entity> paginate3 = studentModel.newQuery().orderBy("id").simplePaginate(3, 3);
        System.out.println(paginate3);
        Assert.assertEquals(paginate3.getCurrentPage(), 3);
        Assert.assertNotNull(paginate3.getFrom());
        Assert.assertNotNull(paginate3.getTo());
        Assert.assertEquals(paginate3.getFrom().intValue(), 7);
        Assert.assertEquals(paginate3.getTo().intValue(), 9);
        Assert.assertNull(paginate3.getLastPage());
        Assert.assertNull(paginate3.getTotal());


        Paginate<StudentSingleModel.Entity> paginate4 = studentModel.newQuery().orderBy("id").simplePaginate(4, 3);
        System.out.println(paginate4);
        Assert.assertEquals(paginate4.getCurrentPage(), 4);
        Assert.assertNotNull(paginate4.getFrom());
        Assert.assertNotNull(paginate4.getTo());
        Assert.assertEquals(paginate4.getFrom().intValue(), 10);
        Assert.assertEquals(paginate4.getTo().intValue(), 10);
        Assert.assertNull(paginate4.getLastPage());
        Assert.assertNull(paginate4.getTotal());


        Paginate<StudentSingleModel.Entity> paginate5 = studentModel.newQuery().orderBy("id").simplePaginate(5, 3);
        System.out.println(paginate5);
        Assert.assertEquals(paginate5.getCurrentPage(), 5);
        Assert.assertNull(paginate5.getFrom());
        Assert.assertNull(paginate5.getTo());
//        Assert.assertEquals(paginate5.getFrom().intValue(), 13);
//        Assert.assertEquals(paginate5.getTo().intValue(), 10);
        Assert.assertNull(paginate5.getLastPage());
        Assert.assertNull(paginate5.getTotal());

    }

    @Test
    public void 分页_通用分页() {
        Paginate<StudentSingleModel.Entity> paginate = studentModel.newQuery().orderBy("id").paginate(1, 4);
        System.out.println(paginate);
        Assert.assertEquals(paginate.getCurrentPage(), 1);
        Assert.assertNotNull(paginate.getFrom());
        Assert.assertNotNull(paginate.getTo());
        Assert.assertEquals(paginate.getFrom().intValue(), 1);
        Assert.assertEquals(paginate.getTo().intValue(), 4);
        Assert.assertNotNull(paginate.getLastPage());
        Assert.assertNotNull(paginate.getTotal());
        Assert.assertEquals(paginate.getLastPage().intValue(), 3);
        Assert.assertEquals(paginate.getTotal().intValue(), 10);


        Paginate<StudentSingleModel.Entity> paginate2 = studentModel.newQuery().orderBy("id").paginate(2, 4);
        System.out.println(paginate2);
        Assert.assertEquals(paginate2.getCurrentPage(), 2);
        Assert.assertNotNull(paginate2.getFrom());
        Assert.assertNotNull(paginate2.getTo());
        Assert.assertEquals(paginate2.getFrom().intValue(), 5);
        Assert.assertEquals(paginate2.getTo().intValue(), 8);
        Assert.assertNotNull(paginate2.getLastPage());
        Assert.assertNotNull(paginate2.getTotal());
        Assert.assertEquals(paginate2.getLastPage().intValue(), 3);
        Assert.assertEquals(paginate2.getTotal().intValue(), 10);


        Paginate<StudentSingleModel.Entity> paginate3 = studentModel.newQuery().orderBy("id").paginate(3, 4);
        System.out.println(paginate3);
        Assert.assertEquals(paginate3.getCurrentPage(), 3);
        Assert.assertNotNull(paginate3.getFrom());
        Assert.assertNotNull(paginate3.getTo());
        Assert.assertEquals(paginate3.getFrom().intValue(), 9);
        Assert.assertEquals(paginate3.getTo().intValue(), 10);
        Assert.assertNotNull(paginate3.getLastPage());
        Assert.assertNotNull(paginate3.getTotal());
        Assert.assertEquals(paginate3.getLastPage().intValue(), 3);
        Assert.assertEquals(paginate3.getTotal().intValue(), 10);

        // 防止过界
        Paginate<StudentSingleModel.Entity> paginate4 = studentModel.newQuery().orderBy("id").paginate(4, 4);
        System.out.println(paginate4);
        Assert.assertEquals(paginate4.getCurrentPage(), 4);
        Assert.assertNull(paginate4.getFrom());
        Assert.assertNull(paginate4.getTo());
//        Assert.assertEquals(paginate4.getFrom().intValue(), 9);
//        Assert.assertEquals(paginate4.getTo().intValue(), 10);
        Assert.assertNotNull(paginate4.getLastPage());
        Assert.assertNotNull(paginate4.getTotal());
        Assert.assertEquals(paginate4.getLastPage().intValue(), 3);
        Assert.assertEquals(paginate4.getTotal().intValue(), 10);
    }

    @Test
    public void 原生(){
        Record<StudentSingleModel.Entity> record = studentModel.newQuery()
            .query("select * from student where id=1", new ArrayList<>());
        Assert.assertNotNull(record);
        Assert.assertEquals(record.toObject().getId().intValue(), 1);

        List<String> e = new ArrayList<>();
        e.add("2");
        RecordList<StudentSingleModel.Entity> records = studentModel.newQuery()
            .queryList("select * from student where sex=?", e);
        Assert.assertEquals(records.size(), 4);
        Assert.assertEquals(records.get(0).toObject().getId().intValue(), 1);

        List<String> e2 = new ArrayList<>();
        e2.add("134");
        e2.add("testNAme");
        e2.add("11");
        e2.add("1");
        int execute = studentModel.newQuery()
            .execute("insert into `student`(`id`,`name`,`age`,`sex`) values( ? , ? , ? , ? )", e2);
        Assert.assertEquals(execute, 1);

        Record<StudentSingleModel.Entity> query = studentModel.newQuery()
            .query("select * from student where sex=12", new ArrayList<>());
        Assert.assertNull(query);

        thrown.expect(EntityNotFoundException.class);
        studentModel.newQuery().queryOrFail("select * from student where sex=12", new ArrayList<>());
    }

}
