package net.clarenceho.multi_liquibase;

import net.clarenceho.multi_liquibase.datasource.DataSourceProperties;
import net.clarenceho.multi_liquibase.datasource.DataSourceProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

import static java.util.stream.Collectors.toMap;


@Configuration
public class MultiDataSourceConfiguration {

    @Bean(name = "dataSources")
    @Primary
    public Map<Object, Object> getDataSources(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.getDataSources().entrySet().stream().map(e -> {
            String source = e.getKey();
            DataSourceProperty dataSourceProperty = e.getValue();
            DataSource dataSource = DataSourceBuilder.create()
                .url(dataSourceProperty.getUrl())
                .username(dataSourceProperty.getUsername())
                .password(dataSourceProperty.getPassword())
                .driverClassName(dataSourceProperty.getDriverClassName())
                .build();
            return new DataSourceRecord(source, dataSource);
        }).collect(toMap(DataSourceRecord::source, DataSourceRecord::dataSource));
    }

    private record DataSourceRecord(Object source, Object dataSource) { }
}
