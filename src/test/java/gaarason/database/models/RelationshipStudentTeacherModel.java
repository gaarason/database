package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.Primary;
import gaarason.database.eloquent.Table;
import gaarason.database.models.base.MasterSlaveModel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class RelationshipStudentTeacherModel extends MasterSlaveModel<RelationshipStudentTeacherModel.Entity> {

    @Data
    @Table(name = "relationship_student_teacher")
    public static class Entity {
        @Primary
        private Integer id;

        @Column(name = "student_id")
        private Integer studentId;

        @Column(name = "teacher_id")
        private Integer teacherId;

        @Column(name = "created_at")
        private Date createdAt;

        @Column(name = "updated_at")
        private Date updatedAt;
    }
}
