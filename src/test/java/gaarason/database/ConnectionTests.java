package gaarason.database;

import gaarason.database.models.StudentModel;
import gaarason.database.utils.FormatUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
public class ConnectionTests extends DatabaseApplicationTests {

    @Resource
    StudentModel studentModel;

    @Test
    public void dataSourceListSize(){

        List<DataSource> masterDataSourceList = studentModel.getProxyDataSource().getMasterDataSourceList();
        List<DataSource> slaveDataSourceList  = studentModel.getProxyDataSource().getSlaveDataSourceList();

        Assert.assertEquals(masterDataSourceList.size(), 3);
        Assert.assertEquals(slaveDataSourceList.size(), 4);

    }
}
