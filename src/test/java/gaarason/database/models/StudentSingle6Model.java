package gaarason.database.models;

import gaarason.database.eloquent.*;
import gaarason.database.eloquent.relations.MoreToMore;
import gaarason.database.models.base.SingleModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class StudentSingle6Model extends SingleModel<StudentSingle6Model.Entity> {

    @Autowired
    TeacherModel teacherModel;

    @Autowired
    RelationshipStudentTeacherModel relationshipStudentTeacherModel;

    @Data
    @Table(name = "student")
    public static class Entity {
        @Primary
        private Integer id;

        private String name;

        private Byte age;

        private Byte sex;

        private Integer teacherId;

        /**
         * 关联关系
         */
//        @MoreToMore
//        private List<TeacherModel.Entity> teacherList;

        private boolean isDeleted;

        @Column(insertable = false, updatable = false)
        private Date createdAt;

        @Column(insertable = false, updatable = false)
        private Date updatedAt;
    }

    public Record<StudentSingle6Model.Entity> findById(Integer id) {
        Record<Entity> record = this.findOrFail(id.toString());

        List<Object> list = relationshipStudentTeacherModel.newQuery()
            .where("student_id", id.toString())
            .select("teacher_id")
            .get()
            .toList((theRecord) ->theRecord.toObject().getTeacherId());

        List<TeacherModel.Entity> id1 = teacherModel.newQuery().whereIn("id", list).get().toObjectList();

        System.out.println(id1);
        return record;
    }

}
