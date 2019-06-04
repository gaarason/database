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

    @Test
    public void simpleConnect() throws SQLException {
//        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
//        DruidDataSource druidDataSource = (DruidDataSource)dataSourceMasterListSingle.get(0);
        DataSource druidDataSource = proxyDataSourceSingle;

        System.out.println("数据源>>>>>>" + druidDataSource.getClass());
        Connection connection1 = druidDataSource.getConnection();
        Connection connection2 = druidDataSource.getConnection();
        Connection connection3 = druidDataSource.getConnection();

//        System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());

        connection2.close();
        connection3.close();
//        Connection connection4 = dataSource.getConnection();
//        Connection connection5 = dataSource.getConnection();

        System.out.println("连接>>>>>>>>>" + connection1);
        System.out.println("连接地址>>>>>" + connection1.getMetaData().getURL());


//        System.out.println("druidDataSource 数据源最大连接数：" + druidDataSource.getMaxActive());
//        System.out.println("druidDataSource 数据源初始化连接数：" + druidDataSource.getInitialSize());
//        System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());

        connection1.close();
//        connection2.close();
//        connection3.close();
//        connection4.close();
//        connection5.close();

//        System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());
//        System.out.println("druidDataSource 数据源初始化连接数：" + druidDataSource.getInitialSize());


        for (int i = 0; i < 20; i++) {
//            Connection connection999 = dataSource.getConnection();
//            connection999.close();
//            System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());
        }
    }



}
