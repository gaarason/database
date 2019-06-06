package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Primary;
import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.Table;
import gaarason.database.models.base.MasterSlaveModel;
import gaarason.database.models.base.SingleModel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StudentSingleModel extends SingleModel<StudentSingleModel.Entity> {

    @Data
    @Table(name = "student")
    public static class Entity {
        @Primary
        private Integer id;

        @Column(length = 20)
        private String name;

        private Byte age;

        private Byte sex;

        @Column(name = "teacher_id")
        private Integer teacherId;

        @Column(name = "created_at", insertable = false, updatable = false)
        private Date createdAt;

        @Column(name = "updated_at", insertable = false, updatable = false)
        private Date updatedAt;
    }

    @Override
    public boolean saving(Record<Entity> entityRecord){
        System.out.println("正要 保存数据库中, 但是拒绝");
        return false;
    }

    @Override
    public void retrieved(Record<Entity> entityRecord){
        System.out.println("已经从数据库中查询到数据");
    }

    public StudentSingleModel.Entity getById(String id){
        return newQuery().where("id", id).firstOrFail().toObject();
    }

    public String getNameById(String id){
        return newQuery().where("id", id).select("name").firstOrFail().toObject().getName();
    }
}
