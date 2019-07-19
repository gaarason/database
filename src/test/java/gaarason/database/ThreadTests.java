package gaarason.database;

import gaarason.database.utils.FormatUtil;
import gaarason.database.utils.StringUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
public class ThreadTests {

    class Common{
        public String mark = "o";
    }

    private Common common = new Common();
    private ThreadLocal<String> markThreadLocal = ThreadLocal.withInitial(() -> "00");

    private String getThreadName(){
        String processName        = ManagementFactory.getRuntimeMXBean().getName();
        String threadName         = Thread.currentThread().getName();
        String className          = getClass().toString();
        return processName + threadName + className;
    }

    @Test
    public void test() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {

            Common t = new Common();
            t.mark = "11";

            System.out.println("1 当前线程名" + getThreadName());

            for(int i = 0; i < 10; i++){
                System.out.println("thread 1 get" + common.mark);
                markThreadLocal.set("111");
                System.out.println("thread 1 markThreadLocal" + markThreadLocal.get());
                common.mark = "1";
                System.out.println("thread 1 get" + common.mark);
                System.out.println("thread 1 get mark" + t.mark);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        }).start();

        new Thread(() -> {
            Common t = new Common();
            t.mark = "22";
            System.out.println("2 当前线程名" + getThreadName());
            for(int i = 0; i < 10; i++){
                System.out.println("thread 2 get" + common.mark);;
                markThreadLocal.set("222");
                System.out.println("thread 2 markThreadLocal" + markThreadLocal.get());
                common.mark = "2";
                System.out.println("thread 2 get" + common.mark);
                System.out.println("thread 2 get mark" + t.mark);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        }).start();

        System.out.println("主线程" + markThreadLocal.get());
        countDownLatch.await();
        System.out.println("主线程" + markThreadLocal.get());
    }


}
