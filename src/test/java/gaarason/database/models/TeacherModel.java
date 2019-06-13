package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.Primary;
import gaarason.database.eloquent.Table;
import gaarason.database.models.base.MasterSlaveModel;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class TeacherModel extends MasterSlaveModel<TeacherModel.Entity> {

    @Data
    @Table(name = "teacher")
    public static class Entity {
        @Primary
        private Integer id;

        private String name;

        private Byte age;

        private Byte sex;

        @Column(length = 20)
        private String subject;

        @Column(name = "created_at")
        private Date createdAt;

        @Column(name = "updated_at")
        private Date updatedAt;
    }
}
