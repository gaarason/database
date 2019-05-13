package gaarason.database;

import gaarason.database.utils.FormatUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.JVM)
public class UtilTests {

    @Test
    public void FormatUtilBracket() {
        Assert.assertEquals(FormatUtil.bracket("name"), "(name)");
        Assert.assertEquals(FormatUtil.bracket("table.cname"), "(table.cname)");
    }

    @Test
    public void FormatUtilSpaces() {
        Assert.assertEquals(FormatUtil.spaces("and id=1"), " and id=1 ");
        Assert.assertEquals(FormatUtil.spaces(" and id=1"), " and id=1 ");
        Assert.assertEquals(FormatUtil.spaces("      and id=1      "), " and id=1 ");
    }

    @Test
    public void FormatUtilBackQuote() {
        Assert.assertEquals(FormatUtil.backQuote("sum(table.cname) aS name"), "sum(`table`.`cname`) as `name`");
        Assert.assertEquals(FormatUtil.backQuote("sum(table.cname) "), "sum(`table`.`cname`)");
        Assert.assertEquals(FormatUtil.backQuote("count(cname) aS name"), "count(`cname`) as `name`");
        Assert.assertEquals(FormatUtil.backQuote("cname aS name"), "`cname` as `name`");

    }
}
