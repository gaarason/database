package gaarason.database.generator;

import gaarason.database.eloquent.Model;
import gaarason.database.generator.element.ColumnAnnotation;
import gaarason.database.generator.element.Field;
import gaarason.database.generator.element.PrimaryAnnotation;
import gaarason.database.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Data
@Slf4j
abstract public class Manager {

    private String namespace = "temp";

    final private static String pojoTemplate = "pojo";

    final private static String fieldTemplate = "field";

    final private static String pojoTemplateStr = fileGetContent(getAbsoluteReadFileName(pojoTemplate));

    final private static String fieldTemplateStr = fileGetContent(getAbsoluteReadFileName(fieldTemplate));

    /**
     * @return 数据库操作model
     */
    abstract public Model getModel();

    public void run() {
        // 表信息
        List<Map<String, Object>> tables = showTables();

        for (Map<String, Object> table : tables) {
            // 单个表
            for (String key : table.keySet()) {
                // 表名
                String tableName = table.get(key).toString();
                // pojo文件名
                String pojoFileName = StringUtil.lineToHump(tableName, true) + ".java";
                // pojo文件内容
                String pojoTemplateStrReplace = fillPojoTemplate(tableName);
                // 写入文件
                filePutContent(getAbsoluteWriteFilePath(), pojoFileName, pojoTemplateStrReplace);
            }
        }
    }

    /**
     * 填充pojo模板内容
     * @param tableName 表名
     * @return 内容
     */
    private String fillPojoTemplate(String tableName) {
        String              pojoName     = StringUtil.lineToHump(tableName, true);
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("${namespace}", namespace);
        parameterMap.put("${pojo_name}", pojoName);
        parameterMap.put("${table}", tableName);
        parameterMap.put("${fields}", fillFieldsTemplate(tableName));

        return fillTemplate(pojoTemplateStr, parameterMap);
    }

    /**
     * 填充所有字段
     * @param tableName 表名
     * @return 内容
     */
    private String fillFieldsTemplate(String tableName) {
        StringBuilder str = new StringBuilder();
        // 字段信息
        List<Map<String, Object>> fields = descTable(tableName);

        log.info("tableName 字段信息 {}", fields);

        for (Map<String, Object> field : fields) {
            // 每个字段的填充
            String fieldTemplateStrReplace = fillFieldTemplate(field);
            // 追加
            str.append(fieldTemplateStrReplace);
        }
        return str.toString();
    }

    /**
     * 填充单个字段
     * @param field 字段属性
     * @return 内容
     */
    private String fillFieldTemplate(Map<String, Object> field) {
        // field
        Field fieldInfo = new Field();
        fieldInfo.setName(StringUtil.lineToHump(field.get("COLUMN_NAME").toString()));
        fieldInfo.setDataType(field.get("DATA_TYPE").toString());
        fieldInfo.setColumnType(field.get("COLUMN_TYPE").toString());

        // @Column
        ColumnAnnotation columnAnnotation = new ColumnAnnotation();
        columnAnnotation.setName(field.get("COLUMN_NAME").toString());
        columnAnnotation.setUnique(field.get("COLUMN_KEY").toString().equals("UNI"));
        columnAnnotation.setUnsigned(field.get("COLUMN_TYPE").toString().contains("unsigned"));
        columnAnnotation.setNullable(field.get("IS_NULLABLE").toString().equals("YES"));
        columnAnnotation.setInsertable(true);
        columnAnnotation.setUpdatable(true);
        if (field.get("CHARACTER_MAXIMUM_LENGTH") != null) {
            columnAnnotation.setLength(Integer.valueOf(field.get("CHARACTER_MAXIMUM_LENGTH").toString()));
        }
        columnAnnotation.setComment(field.get("COLUMN_COMMENT").toString());

        // @primary
        PrimaryAnnotation primaryAnnotation = null;
        if (field.get("COLUMN_KEY").toString().equals("PRI")) {
            primaryAnnotation = new PrimaryAnnotation();
            primaryAnnotation.setIncrement(field.get("EXTRA").toString().equals("auto_increment"));
        }


        // 模板替换参数
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("${primary}", primaryAnnotation == null ? "" : primaryAnnotation.toString());
        parameterMap.put("${column}", columnAnnotation.toString());
        parameterMap.put("${field}", fieldInfo.toString());

        return fillTemplate(fieldTemplateStr, parameterMap);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> showTables() {
        return getModel().newQuery().queryList("show tables", new ArrayList<>()).toMapList();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> descTable(String tableName) {
        List<String> parameters = new ArrayList<>();
        parameters.add(DBName());
        parameters.add(tableName);
        return getModel().newQuery()
            .queryList("select * from information_schema.`columns` where table_schema = ? and table_name = ? ",
                parameters)
            .toMapList();
    }

    private static String namespace2dir(String namespace) {
        return namespace.replace('.', '/');
    }

    /**
     * @return 数据库库名
     */
    @SuppressWarnings("unchecked")
    private String DBName() {
        String              name  = "";
        Model               model = getModel();
        Map<String, Object> map   = model.newQuery().queryOrFail("select database()", new ArrayList<>()).toMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                name = entry.getValue().toString();
                break;
            }
        }
        if ("".equals(name)) {
            throw new RuntimeException("获取当前库名失败");
        }
        return name;
    }

    /**
     * 填充模板
     * @param template     模板内容
     * @param parameterMap 参数
     * @return 填充后的内容
     */
    private static String fillTemplate(String template, Map<String, String> parameterMap) {
        Set<String> strings = parameterMap.keySet();
        for (String string : strings) {
            template = template.replace(string, parameterMap.get(string));
        }
        return template;
    }


    private static String getAbsoluteReadFileName(String name) {
        return Thread.currentThread().getStackTrace()[1].getClass().getResource("/").toString().replace(
            "file:", "") + "../../src" +
            "/main/java/gaarason/database/generator/template/" + name;
    }

    private String getAbsoluteWriteFilePath() {
        return Thread.currentThread().getStackTrace()[1].getClass()
            .getResource("/")
            .toString()
            .replace("file:", "") + "../../src/test/java/" + namespace2dir(namespace) + '/';
    }

    private static String fileGetContent(String fileName) {
        try {
            String          encoding    = "UTF-8";
            File            file        = new File(fileName);
            Long            filelength  = file.length();
            byte[]          filecontent = new byte[filelength.intValue()];
            FileInputStream in          = new FileInputStream(file);
            in.read(filecontent);
            in.close();
            return new String(filecontent, encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void filePutContent(String path, String fileName, String content) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileWriter writer = new FileWriter(path + fileName, false);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
