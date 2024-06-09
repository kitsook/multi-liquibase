package net.clarenceho.multi_liquibase;

import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class})
public class MultiLiquibaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiLiquibaseApplication.class, args);
    }

}
