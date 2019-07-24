# database
Eloquent ORM for Java
## 目录
* [注册bean](/document/bean.md)
* [数据映射](/document/mapping.md)
* [数据模型](/document/model.md)
* [查询结果集](/document/record.md)
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
        * [字段与值的比较](#字段与值的比较)
        * [字段之间的比较](#字段之间的比较)
        * [字段(不)在两值之间](#字段(不)在两值之间)
        * [字段(不)在范围内](#字段(不)在范围内)
        * [字段(不)为null](#字段(不)为null)
        * [子查询](#子查询)
            * [且](#且)
            * [或](#或)
        * [条件为真(假)](#条件为真(假))
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

一下以示例的方式说明, 均来自源码中的单元测试

## 原生语句

### 原生查询

```java
// 查询单条
Record<Student> record = studentModel.newQuery()
            .query("select * from student where id=1", new ArrayList<>());

// 查询多条
List<String> parameters = new ArrayList<>();
parameters.add("2");
RecordList<Student> records = studentModel.newQuery().queryList("select * from student where sex=?", parameters);
```
### 原生更新
```java
List<String> parameters = new ArrayList<>();
parameters.add("134");
parameters.add("testNAme");
parameters.add("11");
parameters.add("1");
int num = studentModel.newQuery()
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
int num = studentModel.newQuery().insert(entity);

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
int num = studentModel.newQuery().insert(entityList);

// 构造语句插入
 List<String> columnNameList = new ArrayList<>();
columnNameList.add("name");
columnNameList.add("age");
columnNameList.add("sex");
List<String> valueList = new ArrayList<>();
valueList.add("testNAme134");
valueList.add("11");
valueList.add("1");
int num = studentModel.newQuery().select(columnNameList).value(valueList).insert();

```

## 更新
```java
int num = studentModel.newQuery().data("name", "xxcc").where("id", "3").update();

int num = studentModel.newQuery().data("name", "vvv").where("id", ">", "3").update();

```

## 删除

当前model如果非软删除, 则`默认删除`与`强力删除`效果一致  
`软删除`定义以及启用,请看[数据模型](/document/model.md)

### 默认删除
```java
int num = studentModel.newQuery().where("id", "3").delete();
```
### 强力删除
```java
int num = studentModel.newQuery().where("id", "3").forceDelete();
```

## 聚合函数
```java
Long count0 = studentModel.newQuery().where("sex", "1").group("age").count("id");

Long count = studentModel.newQuery().where("sex", "1").count("age");

String max = studentModel.newQuery().where("sex", "1").max("id");

String min = studentModel.newQuery().where("sex", "1").min("id");

String avg = studentModel.newQuery().where("sex", "1").avg("id");

String sum = studentModel.newQuery().where("sex", "2").sum("id");
```
## 自增或自减
```java
int update = studentModel.newQuery().dataDecrement("age", 2).whereRaw("id=4").update();

int update2 = studentModel.newQuery().dataIncrement("age", 4).whereRaw("id=4").update();

```

## 随机获取

略

## select
```java
Record<Student> record = studentModel.newQuery().select("name").select("id").first();

Record<Student> record = studentModel.newQuery().select("name","id","created_at").first();

Record<Student> record = studentModel.newQuery().selectFunction("concat_ws", "\"-\",`name`,`id`", "newKey").first();
```

## where
### 字段与值的比较
whereColumn
```java
Record<Student> record = studentModel.newQuery().whereRaw("id<2").first();
Record<Student> record = studentModel.newQuery().where("id", ">", "2").first();
Record<Student> record = studentModel.newQuery().where("id", "!=", "2").first();
Record<Student> record = studentModel.newQuery().where("id", "2").first();
Record<Student> record = studentModel.newQuery().where("name", "like", "%明%").first();
```
### 字段之间的比较
whereColumn
```java
Record<Student> record = studentModel.newQuery().whereColumn("id", ">", "sex").first();
```
### 字段(不)在两值之间
whereBetween
whereNotBetween
```java
RecordList<Student> records = studentModel.newQuery().whereBetween("id", "3", "5").get();

RecordList<Student> records = studentModel.newQuery().whereNotBetween("id", "3", "5").get();
```
### 字段(不)在范围内
whereIn
whereNotIn
```java
List<Object> idList = new ArrayList<>();
idList.add("4");
idList.add("5");
idList.add("6");
idList.add("7");
RecordList<Student> records = studentModel.newQuery().whereIn("id", idList).get();

RecordList<Student> records = studentModel.newQuery().whereNotIn("id", idList).get();

RecordList<Student> records = studentModel.newQuery().whereIn("id",
    builder -> builder.select("id").where("age", ">=", "11")
).andWhere(
    builder -> builder.whereNotIn("sex",
        builder1 -> builder1.select("sex").where("sex", "1")
    )
).get()
```
whereNotIn

### 字段(不)为null
whereNull
whereNotNull
```java
RecordList<Student> records = studentModel.newQuery().whereNull("id").get();

RecordList<Student> records = studentModel.newQuery().whereNotNull("id").get();
```

### 子查询

子查询可以无限相互嵌套

#### 且
andWhere
```java
RecordList<Student> records = studentModel.newQuery().where("id", "3").andWhere(
    (builder) -> builder.whereRaw("id=4")
).get();
```
#### 或
orWhere
```java
RecordList<Student> records = studentModel.newQuery().where("id", "3").orWhere(
    (builder) -> builder.whereRaw("id=4")
).get();
```

### 条件为真(假)
whereExists
whereNotExists
```java
RecordList<Student> records = studentModel.newQuery()
.select("id", "name", "age")
.whereBetween("id", "1", "2")
.whereExists(
    builder -> builder.select("id", "name", "age").whereBetween("id", "2", "3")
)
.whereNotExists(
    builder -> builder.select("id", "name", "age").whereBetween("id", "2", "4")
)
.get();
```
## having


