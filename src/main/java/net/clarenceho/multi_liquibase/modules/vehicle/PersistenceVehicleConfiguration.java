package net.clarenceho.multi_liquibase.modules.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
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
    basePackages = "net.clarenceho.multi_liquibase.modules.vehicle",
    entityManagerFactoryRef = "vehicleEntityManager",
    transactionManagerRef = "vehicleTransactionManager"
)
@ConditionalOnProperty(prefix = "demo.datasources.vehicle", name = "url")  // for use with multiple data sources deployment only
public class PersistenceVehicleConfiguration {
    @Autowired
    private Environment env;

    @Bean
    @Primary    // one of the entity manager factory beans needs to be the primary
    @DependsOn("dataSources")
    public LocalContainerEntityManagerFactoryBean vehicleEntityManager(Map<Object, Object> dataSources) {
        LocalContainerEntityManagerFactoryBean em
            = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(vehicleDataSource(dataSources));
        em.setPackagesToScan("net.clarenceho.multi_liquibase.modules.vehicle");

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
    @Primary    // one of the transaction managers beans needs to be the primary
    @DependsOn("dataSources")
    public PlatformTransactionManager vehicleTransactionManager(Map<Object, Object> dataSources) {
        JpaTransactionManager transactionManager
            = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
            vehicleEntityManager(dataSources).getObject());
        return transactionManager;
    }

    @Bean
    @Primary    // one of the DataSource beans needs to be the primary
    @DependsOn("dataSources")
    DataSource vehicleDataSource(Map<Object, Object> dataSources) {
        Object ds = dataSources.get("vehicle");
        if (ds == null) {
            throw new RuntimeException("Failed to fine data source for vehicle module");
        }
        return (DataSource)ds;
    }
}
