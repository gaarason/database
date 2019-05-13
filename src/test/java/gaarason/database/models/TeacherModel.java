package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.Primary;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class TeacherModel extends Model<TeacherModel.Entity> {

    @Data
    public static class Entity {
        @Primary
        private int id;

        @Column(name = "student_id", length = 20)
        private String name;

        private Byte age;

        private Byte sex;

        @Column(length = 20)
        private String subject;

        @Column(name = "created_at")
        private Timestamp createdAt;

        @Column(name = "updated_at")
        private Timestamp updatedAt;
    }
}
