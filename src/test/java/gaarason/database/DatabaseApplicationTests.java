package gaarason.database;

import com.alibaba.druid.pool.DruidDataSource;
import gaarason.database.models.RelationshipStudentTeacherModel;
import gaarason.database.models.SsoTenantSaltModel;
import gaarason.database.models.StudentModel;
import gaarason.database.models.TeacherModel;
import gaarason.database.pojo.po.SsoTenantSalt;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
public class DatabaseApplicationTests {

    @Resource
    DataSource dataSource;

    @Autowired
    SsoTenantSaltModel              ssoTenantSaltModel;

    private String table = "sso_tenant_salt";

    /**
     * 初始化数据库
     */
    @BeforeClass
    public static void beforeClass() {
        System.out.println("in before class");
    }

    //execute for each test, before executing test
    @Before
    public void before() throws SQLException {
        System.out.println("in before");
        String sqlFilename = this.getClass().getResource("/").toString().replace("file:", "") + "../../src" +
                "/test/java/gaarason/database/init/testInit.sql";
        String     sqlString  = readToString(sqlFilename);
        String[]   split      = sqlString.split(";\n");
        Connection connection = dataSource.getConnection();
        for (String sql : split) {
//            System.out.println("初始化 sql : " + sql);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            int               i                 = preparedStatement.executeUpdate();
        }
        connection.close();
        System.out.println("数据库重新初始化");
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
            return null;
        }
    }

    //execute for each test, after executing test
    @After
    public void after() {
        System.out.println("in after");
    }

    /**
     * 清空数据库
     */
    @AfterClass
    public static void afterClass() {
        System.out.println("in after class");
    }

    @Test
    public void simpleConnect() throws SQLException {
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        System.out.println("数据源>>>>>>" + dataSource.getClass());
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();
        Connection connection3 = dataSource.getConnection();

        System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());

        connection2.close();
        connection3.close();
        Connection connection4 = dataSource.getConnection();
        Connection connection5 = dataSource.getConnection();

        System.out.println("连接>>>>>>>>>" + connection1);
        System.out.println("连接地址>>>>>" + connection1.getMetaData().getURL());


        System.out.println("druidDataSource 数据源最大连接数：" + druidDataSource.getMaxActive());
        System.out.println("druidDataSource 数据源初始化连接数：" + druidDataSource.getInitialSize());
        System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());

        connection1.close();
//        connection2.close();
//        connection3.close();
        connection4.close();
        connection5.close();

        System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());
//        System.out.println("druidDataSource 数据源初始化连接数：" + druidDataSource.getInitialSize());


        for (int i = 0; i < 20; i++) {
            Connection connection999 = dataSource.getConnection();
            connection999.close();
            System.out.println("druidDataSource 数据源当前连接数：" + druidDataSource.getConnectCount());
        }
    }

    @Test
    public void simpleSelect() throws SQLException {
        String            sql               = "select * from " + table + " where id=?";
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);

        preparedStatement.setString(1, "qrwqrwr");

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
            System.out.println(resultSet.getString(2));
            System.out.println(resultSet.getString(3));
            System.out.println(resultSet.getString(4));
            System.out.println(resultSet.getString(5));
            System.out.println(resultSet.getString(6));
        }
        System.out.println(resultSet);
    }

    @Test
    public void select() throws SQLException {

//        DataSource dataSource = ssoTenantSaltModel.getDataSource();
        SsoTenantSalt ssoTenantSalt = ssoTenantSaltModel.newQuery().from("sso_tenant_salt")
                .where("id", "qrwqrwr").first();

        System.out.println(ssoTenantSalt);
    }


//    @Test
//    public void close() throws SQLException {
//        dataSource.getConnection().close();
//        System.out.println("数据源关闭连接数");
//
//    }


}
