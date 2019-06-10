package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Primary;
import gaarason.database.eloquent.Record;
import gaarason.database.eloquent.Table;
import gaarason.database.models.base.SingleModel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StudentSingle4Model extends SingleModel<StudentSingle4Model.Entity> {

    @Data
    @Table(name = "student")
    public static class Entity {
        @Primary
        private Integer id;

        private String name;

        private Byte age;

        private Byte sex;

        private Integer teacherId;

        @Column(insertable = false, updatable = false)
        private Date createdAt;

        @Column(insertable = false, updatable = false)
        private Date updatedAt;
    }

    @Override
    public boolean updating(Record<Entity> record){
        if(record.getEntity().getId() == 9){
            System.out.println("正要修改id为9的数据, 但是拒绝");
            return false;
        }
        return true;
    }

    @Override
    public void retrieved(Record<Entity> entityRecord){
        System.out.println("已经从数据库中查询到数据");
    }

    public StudentSingle4Model.Entity getById(String id){
        return newQuery().where("id", id).firstOrFail().toObject();
    }

    public String getNameById(String id){
        return newQuery().where("id", id).select("name").firstOrFail().toObject().getName();
    }
}
