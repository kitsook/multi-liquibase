package net.clarenceho.multi_liquibase.datasource;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("demo")
public class DataSourceProperties {
    private Map<String, DataSourceProperty> dataSources = new HashMap<>();
}
