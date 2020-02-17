package org.cn.o2o.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement//Service上添加@Transactional
public class TransactionManagementConfguration implements TransactionManagementConfigurer {
    @Autowired
    private DataSource dataSource;

    @Override
    /**
     * 关于事务管理，返回PlatformTransactionManager的实现
     */
    public PlatformTransactionManager annotationDrivenTransactionManager(){
        return new DataSourceTransactionManager(dataSource);
    }
}
