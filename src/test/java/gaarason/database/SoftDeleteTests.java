package gaarason.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.RecordList;
import gaarason.database.models.StudentSingle4Model;
import gaarason.database.models.StudentSingle5Model;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@FixMethodOrder(MethodSorters.JVM)
public class SoftDeleteTests extends DatabaseApplicationTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    StudentSingle5Model studentModel;

    @Test
    public void 软删除() {
        int id = studentModel.newQuery().where("id", "5").delete();
        Assert.assertEquals(id, 1);

        Record<StudentSingle5Model.Entity> record = studentModel.withTrashed().where("id", "5").first();
        Assert.assertNotNull(record);
        Assert.assertTrue(record.toObject().isDeleted());

        RecordList<StudentSingle5Model.Entity> records = studentModel.onlyTrashed().get();
        Assert.assertEquals(records.size(), 1);
    }

    @Test
    public void 硬删除() {
        int id = studentModel.newQuery().where("id", "5").forceDelete();
        Assert.assertEquals(id, 1);

        Record<StudentSingle5Model.Entity> record = studentModel.withTrashed().where("id", "5").first();
        Assert.assertNull(record);

        RecordList<StudentSingle5Model.Entity> records = studentModel.onlyTrashed().get();
        Assert.assertEquals(records.size(), 0);
    }


}
