package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.Primary;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class RelationshipStudentTeacherModel extends Model<RelationshipStudentTeacherModel.Entity> {

    @Data
    public static class Entity {
        @Primary
        private int id;

        @Column(name = "student_id")
        private int studentId;

        @Column(name = "teacher_id")
        private int teacherId;

        @Column(name = "created_at")
        private Timestamp createdAt;

        @Column(name = "updated_at")
        private Timestamp updatedAt;
    }
}
