package net.clarenceho.multi_liquibase.modules.vessel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "net.clarenceho.multi_liquibase.modules.vessel",
    entityManagerFactoryRef = "vesselEntityManager",
    transactionManagerRef = "vesselTransactionManager"
)
@ConditionalOnProperty(prefix = "demo.datasources.vessel", name = "url")  // for use with multiple data sources deployment only
public class PersistenceVesselConfiguration {
    @Autowired
    private Environment env;

    @Bean
    @DependsOn("dataSources")
    public LocalContainerEntityManagerFactoryBean vesselEntityManager(Map<Object, Object> dataSources) {
        LocalContainerEntityManagerFactoryBean em
            = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(vesselDataSource(dataSources));
        em.setPackagesToScan("net.clarenceho.multi_liquibase.modules.vessel");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",
            env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect",
            env.getProperty("hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @DependsOn("dataSources")
    public PlatformTransactionManager vesselTransactionManager(Map<Object, Object> dataSources) {
        JpaTransactionManager transactionManager
            = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
            vesselEntityManager(dataSources).getObject());
        return transactionManager;
    }

    @Bean
    @DependsOn("dataSources")
    DataSource vesselDataSource(Map<Object, Object> dataSources) {
        Object ds = dataSources.get("vessel");
        if (ds == null) {
            throw new RuntimeException("Failed to fine data source for vehicle module");
        }
        return (DataSource)ds;
    }
}
