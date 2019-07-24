# database
Eloquent ORM for Java
## 目录
* [注册bean](/document/bean.md)
* [数据映射](/document/mapping.md)
* [数据模型](/document/model.md)
* [查询结果集](/document/model.md)
* [查询构造器](/document/query.md)
    * [原生语句](#原生语句)
        * [原生查询](#原生查询)
        * [原生更新](#原生更新)
    * [获取](#获取)
    * [插入](#插入)
    * [更新](#更新)
    * [删除](#删除)
        * [默认删除](#默认删除)
        * [强力删除](#强力删除)
    * [聚合函数](#聚合函数)
    * [自增或自减](#自增或自减)
    * [随机获取](#随机获取)
    * [select](#select)
    * [where](#where)
    * [having](#having)
    * [order](#order)
    * [group](#group)
    * [join](#join)
    * [limit](#limit)
    * [table](#table)
    * [data](#data)
    * [union](#union)
    * [index](#index)
    * [lock](#lock)
    * [数据库配置文件](#数据库配置文件)
    * [参数绑定](#参数绑定)
    * [闭包事务](#闭包事务)
        * [多连接嵌套事务](#闭包事务)
    * [ORM](#ORM)
    * [debug](#debug)
    * [where子查询](#where子查询)
    * [分块查询](#分块查询)
    * [预处理语句复用](#预处理语句复用)
    * [注册查询方法](#注册查询方法)
    
## 总览

构造sql  
以下示例

```java
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
```

## 原生语句

### 原生查询

```java
Record<StudentSingleModel.Entity> record = studentModel.newQuery()
            .query("select * from student where id=1", new ArrayList<>());

List<String> parameters = new ArrayList<>();
parameters.add("2");
RecordList<StudentSingleModel.Entity> records = studentModel.newQuery().queryList("select * from student where sex=?", parameters);
```
### 原生更新
```java
List<String> parameters = new ArrayList<>();
parameters.add("134");
parameters.add("testNAme");
parameters.add("11");
parameters.add("1");
int execute = studentModel.newQuery()
    .execute("insert into `student`(`id`,`name`,`age`,`sex`) values( ? , ? , ? , ? )", e2);
```

## 获取

```java
// select name,id from student limit 1
Record<Student> record = studentModel.newQuery().select("name").select("id").first();

// select * from student where name="小龙" limit 1
Record<Student> record = studentModel.newQuery().where("name", "小龙").firstOrFail();

// select * from student where id=9 limit 1
Record<Student> record = studentModel.findOrFail("9")

// select * from student where `age`<9
RecordList<Student> records = studentModel.where("age","<","9").get();

```
## 插入

```java
// 实体赋值插入
Student student = new Student();
student.setId(99);
student.setName("姓名");
student.setAge(Byte.valueOf("13"));
student.setSex(Byte.valueOf("1"));
student.setTeacherId(0);
student.setCreatedAt(new Date(1312312312));
student.setUpdatedAt(new Date(1312312312));
int insert = studentModel.newQuery().insert(entity);

// 实体批量操作
List<Student> studentList = new ArrayList<>();
for (int i = 99; i < 1000; i++) {
    Student student = new Student();
    entity.setId(i);
    entity.setName("姓名");
    entity.setAge(Byte.valueOf("13"));
    entity.setSex(Byte.valueOf("1"));
    entity.setTeacherId(i * 3);
    entity.setCreatedAt(new Date());
    entity.setUpdatedAt(new Date());
    entityList.add(entity);
}
int insert = studentModel.newQuery().insert(entityList);

// 构造语句插入
 List<String> columnNameList = new ArrayList<>();
columnNameList.add("name");
columnNameList.add("age");
columnNameList.add("sex");
List<String> valueList = new ArrayList<>();
valueList.add("testNAme134");
valueList.add("11");
valueList.add("1");
int insert = studentModel.newQuery().select(columnNameList).value(valueList).insert();

```

## 更新
```java
int update = studentModel.newQuery().data("name", "xxcc").where("id", "3").update();

int update2 = studentModel.newQuery().data("name", "vvv").where("id", ">", "3").update();

```

## 删除

当前model如果非软删除, 则`默认删除`与`强力删除`效果一致  
`软删除`定义以及启用,请看[数据模型](/document/model.md)

### 默认删除
```java
int id = student5Model.newQuery().where("id", "3").delete();
```
### 强力删除
```java
int id = student5Model.newQuery().where("id", "3").forceDelete();
```
