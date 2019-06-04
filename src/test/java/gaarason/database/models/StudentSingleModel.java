package gaarason.database.models;

import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Primary;
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

        @Column(name = "created_at")
        private Date createdAt;

        @Column(name = "updated_at")
        private Date updatedAt;
    }

    @Override
    public boolean retrieving(){
        System.out.println("正在从数据库中查询");
        return true;
    }

    @Override
    public void retrieved(){
        System.out.println("已经从数据库中查询到数据");
    }

    public StudentSingleModel.Entity getById(String id){
        return newQuery().where("id", id).firstOrFail().toObject();
    }

    public String getNameById(String id){
        return newQuery().where("id", id).select("name").firstOrFail().toObject().getName();
    }
}
