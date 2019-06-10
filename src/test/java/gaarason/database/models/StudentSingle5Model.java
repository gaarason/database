package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Primary;
import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.Table;
import gaarason.database.models.base.SingleModel;
import gaarason.database.query.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StudentSingle5Model extends SingleModel<StudentSingle5Model.Entity> {

    @Data
    @Table(name = "student")
    public static class Entity {
        @Primary
        private Integer id;

        private String name;

        private Byte age;

        private Byte sex;

        private Integer teacherId;

        private boolean isDeleted;

        @Column(insertable = false, updatable = false)
        private Date createdAt;

        @Column(insertable = false, updatable = false)
        private Date updatedAt;
    }

    @Override
    protected boolean softDeleting() {
        return true;
    }

}
