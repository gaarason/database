package gaarason.database;

import gaarason.database.tool.Generator;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
@Slf4j
public class GeneratorTests {


    @Resource
    Generator generator;

    @Test
    public void run() {
        generator.setNamespace("temp");
        generator.setDisInsertable("created_at", "updated_at", "created_on", "updated_on");
        generator.setDisUpdatable("created_at", "updated_at", "created_on", "updated_on");
        generator.run();
    }

}
