package gaarason.database.models;

import gaarason.database.connections.ProxyDataSource;
import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Model;
import gaarason.database.eloquent.Primary;
import gaarason.database.eloquent.Table;
import gaarason.database.models.base.SingleModel;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class StudentSingle3Model extends Model<StudentSingle3Model.Entity> {

    @Data
    @Table(name = "student")
    public static class Entity{
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

    @Resource(name = "proxyDataSourceSingle3")
    protected ProxyDataSource dataSource;

    public ProxyDataSource getProxyDataSource(){
        return dataSource;
    }

    public StudentSingle3Model.Entity getById(String id){
        return newQuery().where("id", id).firstOrFail().toObject();
    }

    public String getNameById(String id){
        return newQuery().where("id", id).select("name").firstOrFail().toObject().getName();
    }
}
