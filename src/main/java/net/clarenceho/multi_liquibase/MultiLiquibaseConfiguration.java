package net.clarenceho.multi_liquibase;

import lombok.AllArgsConstructor;
import net.clarenceho.multi_liquibase.datasource.DataSourceProperties;
import net.clarenceho.multi_liquibase.datasource.MultiDataSourceLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = false)
@EnableConfigurationProperties(LiquibaseProperties.class)
@AllArgsConstructor
public class MultiLiquibaseConfiguration {
    private LiquibaseProperties properties;
    private DataSourceProperties dataSourceProperties;

    @Bean
    @DependsOn("dataSources")
    public MultiDataSourceLiquibase liquibaseMultiSource(Map<Object, Object> dataSources,
                                                         @Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        // run liquibase update for each source in parallel
        MultiDataSourceLiquibase liquibase = new MultiDataSourceLiquibase(taskExecutor);
        dataSources.forEach((source, dataSource) -> liquibase.addDataSource((String) source, (DataSource) dataSource));
        dataSourceProperties.getDataSources().forEach((source, dbProperty) -> {
            if (dbProperty.getLiquibase() != null) {
                liquibase.addLiquibaseProperties(source, dbProperty.getLiquibase());
            }
        });

        liquibase.setContexts(properties.getContexts());
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        return liquibase;
    }
}
