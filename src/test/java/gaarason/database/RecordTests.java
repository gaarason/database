package gaarason.database;

import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.OrderBy;
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
public class RecordTests extends DatabaseApplicationTests {
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
    TeacherModel teacherModel;

    @Test
    public void 查询一条(){
        Record<StudentSingleModel.Entity> activeRecord = studentModel.newQuery().where("id", "3").firstOrFail();

        StudentSingleModel.Entity entity = activeRecord.getEntity();

        System.out.println(entity);


//        Record<StudentSingleModel.Entity>     entityRecord     = studentModel.newRecord();
//        Record<StudentSingleModel.Entity>     entityRecord1    = entityRecord.find("3");
//        Assert.assertEquals(entityRecord, entityRecord1);
//
//
        activeRecord.getEntity().setAge(Byte.valueOf("55"));
        boolean save = activeRecord.save();
        System.out.println(save);
//
        Byte age = studentModel.newQuery().where("id", "3").firstOrFail().toObject().getAge();
        Assert.assertEquals(age, Byte.valueOf("55"));

    }

//    @Test
//    public void test() {
//
//        Record<StudentSingleModel.Entity> entityRecord = studentModel.newRecord();
//
//        StudentSingleModel.Entity entity = entityRecord.getEntity();
//        entity.setId(95);
//        entity.setName("行啊");
//        entity.setAge(Byte.valueOf("5"));
//        entity.setSex(Byte.valueOf("1"));
//        entity.setTeacherId(0);
//        entity.setCreatedAt(new Date());
//        entity.setUpdatedAt(new Date());
//
//        boolean save = entityRecord.save();
//        Assert.assertTrue(save);
//
//        List<StudentSingleModel.Entity> entities = studentModel.newQuery().get().toObjectList();
//        Assert.assertEquals(entities.size(), 11);
//
////        List<StudentSingleModel.Entity> entities2 = studentModel.all("id", "name").toObjectList();
////        Assert.assertEquals(entities2.size(), 11);
//
//    }

    @Test
    public void 事件_retrieved(){
//        Collection<StudentSingleModel.Entity> entityCollection = studentModel.find("6");
    }

}
