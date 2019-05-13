package gaarason.database.models;

import gaarason.database.eloquent.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class StudentModel extends Model<StudentModel.Entity> {

    @Data
    @Table(name = "student")
    public static class Entity implements gaarason.database.eloquent.Entity {
        @Primary
        private Integer id;

        @Column(length = 20)
        private String name;

        private Byte age;

        private Byte sex;

        @Column(name = "teacher_id")
        private Integer teacherId;

        @Column(name = "created_at")
        private Date createdAt;

        @Column(name = "updated_at")
        private Date updatedAt;
    }
}
