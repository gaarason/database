package gaarason.database.spring.boot.autoconfigure;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.ToString;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Component
//@EnableConfigurationProperties
//@ConfigurationProperties("gaarason.database.druid")
public class DatabaseDruidDataSourceWrapper extends DruidDataSource implements InitializingBean {

    DatabaseDruidDataSourceWrapper(Map<String, Object> configMap){
//        Set<String> strings = configMap.keySet();
//        for (String string : strings) {
//
//        }

        BeanInfo beanInfo = null; // 获取类属性
        try {
            beanInfo = Introspector.getBeanInfo(getClass());


            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
            for (int i = 0; i< propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();

                if (configMap.containsKey(propertyName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    Object value = configMap.get(propertyName);

                    Object[] args = new Object[1];
                    args[0] = value;

                    try {
                        descriptor.getWriteMethod().invoke(this, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }


    }
    DatabaseDruidDataSourceWrapper(){

    }

    public void afterPropertiesSet() throws Exception {


        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~ " + this);

    }

    @Autowired(required = false)
    public void autoAddFilters(List<Filter> filters) {
        super.filters.addAll(filters);
    }

    public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
        try {
            super.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        } catch (IllegalArgumentException var4) {
            super.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
        }

    }
}