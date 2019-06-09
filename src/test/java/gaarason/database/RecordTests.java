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
    StudentSingle4Model studentModel;

    @Test
    public void 查询一条() {
        Record<StudentSingle4Model.Entity> record = studentModel.findOrFail("3");
        StudentSingle4Model.Entity         entity = record.getEntity();
        Assert.assertEquals(entity.getAge(), Byte.valueOf("16"));
        Assert.assertEquals(entity.getName(), "小腾");

        Record<StudentSingle4Model.Entity> noRecord = studentModel.find("32");
        Assert.assertNull(noRecord);

        Record<StudentSingle4Model.Entity> record2 = studentModel.find("9");
        Assert.assertNotNull(record2);
        StudentSingle4Model.Entity         entity2 = record2.getEntity();
        Assert.assertEquals(entity2.getAge(), Byte.valueOf("17"));
        Assert.assertEquals(entity2.getName(), "莫西卡");

    }

    @Test
    public void 事件_retrieved() {
//        Collection<StudentSingleModel.Entity> entityCollection = studentModel.find("6");
    }

}
