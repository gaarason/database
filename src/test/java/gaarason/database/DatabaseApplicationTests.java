package gaarason.database;

import com.alibaba.druid.pool.DruidDataSource;
import gaarason.database.connections.ProxyDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
@Slf4j
public class DatabaseApplicationTests {

//    @Resource
//    DataSource dataSource;

    @Resource
    List<DataSource> dataSourceSlaveSingleList;
    @Resource
    List<DataSource> dataSourceMasterSingleList;
    @Resource
    ProxyDataSource proxyDataSourceSingle;

    private static String initSql = "";

    /**
     * 初始化数据库
     */
    @BeforeClass
    public static void beforeClass() {
        log.debug("spring 初始化完成");
        String sqlFilename = Thread.currentThread().getStackTrace()[1].getClass().getResource("/").toString().replace(
            "file:", "") + "../../src" +
            "/test/java/gaarason/database/init/testInit.sql";
        initSql  = readToString(sqlFilename);
        System.out.println("in before class");
    }

    @Before
    public void before() throws SQLException {
        log.debug("in before");
        log.debug("数据库重新初始化开始");

//        initDataSourceList(dataSourceSlaveSingleList);
        initDataSourceList(dataSourceMasterSingleList);

        log.debug("数据库重新初始化完成");
    }

    private void initDataSourceList(List<DataSource> dataSourceList) throws SQLException {
        String[]   split      = initSql.split(";\n");
        for(DataSource dataSource : dataSourceList){
            Connection connection = dataSource.getConnection();
            for (String sql : split) {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                int               i                 = preparedStatement.executeUpdate();
            }
            connection.close();
        }
    }

    private static String readToString(String fileName) {
        System.out.println(fileName);
        String encoding = "UTF-8";
        File   file     = new File(fileName);
        file.setReadable(true);
        Long   filelength  = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        }
        return "";
    }

    //execute for each test, after executing test
    @After
    public void after() {
        log.debug("in after");
    }

    /**
     * 清空数据库
     */
    @AfterClass
    public static void afterClass() {
        log.debug("in after class");
    }
}
