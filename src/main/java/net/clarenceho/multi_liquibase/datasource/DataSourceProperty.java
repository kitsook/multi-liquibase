package net.clarenceho.multi_liquibase.datasource;

import lombok.Data;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;

@Data
public class DataSourceProperty {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private LiquibaseProperties liquibase;
}
