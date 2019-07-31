package gaarason.database;

import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.RecordList;
import gaarason.database.models.StudentSingle5Model;
import gaarason.database.models.StudentSingle6Model;
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
public class RelationTests extends DatabaseApplicationTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    StudentSingle6Model studentModel;

    @Test
    public void oneToOne() {

        Record<StudentSingle6Model.Entity> record = studentModel.newQuery()
            .where(StudentSingle6Model.Entity.NAME, "id")
            .firstOrFail();
        StudentSingle6Model.Entity student = record.toObject();

    }

    @Test
    public void oneToMore() {

        Record<StudentSingle6Model.Entity> byId = studentModel.findById(2);
        System.out.println(byId);
    }
}
