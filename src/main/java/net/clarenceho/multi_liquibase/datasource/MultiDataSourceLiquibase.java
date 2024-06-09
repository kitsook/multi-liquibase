package net.clarenceho.multi_liquibase.datasource;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class MultiDataSourceLiquibase implements InitializingBean, ResourceLoaderAware {
    public static final long LIQUIBASE_TIME_WARN_THRESHOLD_SEC = 5;

    private final TaskExecutor taskExecutor;
    private final Map<String, DataSource> dataSources = new HashMap<>();
    private final Map<String, LiquibaseProperties> propertiesDataSources = new HashMap<>();

    // Liquibase properties
    private ResourceLoader resourceLoader;
    private String changeLog;
    private String contexts;
    private String labels;
    private Map<String, String> parameters;
    private String defaultSchema;
    private String liquibaseSchema;
    private String liquibaseTablespace;
    private String databaseChangeLogTable;
    private String databaseChangeLogLockTable;
    private boolean dropFirst;
    private boolean shouldRun = true;
    private File rollbackFile;

    public MultiDataSourceLiquibase(@Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void addDataSource(String source, DataSource dataSource) {
        this.dataSources.put(source, dataSource);
    }

    public void addLiquibaseProperties(String source, LiquibaseProperties properties) {
        this.propertiesDataSources.put(source, properties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Running liquibase");
        runOnAllDataSources();
    }

    private void runOnAllDataSources() throws LiquibaseException {
        dataSources.forEach((source, dataSource) -> {
            log.info("Initializing Liquibase for data source " + source);
            final LiquibaseProperties lProperty = propertiesDataSources.get(source);
            SpringLiquibase liquibase = lProperty != null ? getSpringLiquibase(dataSource, lProperty) : getSpringLiquibase(dataSource);
            if (taskExecutor != null) {
                taskExecutor.execute(() -> {
                    try {
                        log.warn("Starting Liquibase init...");
                        runLiquibase(liquibase);
                    } catch (LiquibaseException e) {
                        log.error("Liquibase exception: {}", e.getMessage(), e);
                    }
                });
            } else {
                try {
                    log.warn("Starting Liquibase init...");
                    runLiquibase(liquibase);
                } catch (LiquibaseException e) {
                    log.error("Liquibase exception: {}", e.getMessage(), e);
                }
            }

            log.info("Liquibase executed for data source " + source);
        });
    }

    private SpringLiquibase getSpringLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLog);
        liquibase.setChangeLogParameters(parameters);
        liquibase.setContexts(contexts);
        liquibase.setLabels(labels);
        liquibase.setDropFirst(dropFirst);
        liquibase.setShouldRun(shouldRun);
        liquibase.setRollbackFile(rollbackFile);
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setLiquibaseSchema(liquibaseSchema);
        liquibase.setLiquibaseTablespace(liquibaseTablespace);
        liquibase.setDatabaseChangeLogTable(databaseChangeLogTable);
        liquibase.setDatabaseChangeLogLockTable(databaseChangeLogLockTable);
        return liquibase;
    }

    private SpringLiquibase getSpringLiquibase(DataSource dataSource, LiquibaseProperties properties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setContexts(properties.getContexts());
        // TODO resolve
        //liquibase.setLabels(properties.getLabels());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setRollbackFile(properties.getRollbackFile());
        // TODO resolve
        //liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
        return liquibase;
    }

    private void runLiquibase(SpringLiquibase liquibase) throws LiquibaseException {
        StopWatch watch = new StopWatch();
        watch.start();
        liquibase.afterPropertiesSet();
        watch.stop();
        log.info("Liquibase completed in {} ms", watch.getTotalTimeMillis());
        if (watch.getTotalTimeMillis() > LIQUIBASE_TIME_WARN_THRESHOLD_SEC * 1000L) {
            log.warn("Warning, Liquibase took more than {} seconds to start up", LIQUIBASE_TIME_WARN_THRESHOLD_SEC);
        }
    }
}
