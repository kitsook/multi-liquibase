## Multi-liquibase

This is a Spring Boot JPA proof-of-concept(POC) application to test out multiple data sources together with Liquibase.

### Goal of this POC
- new data sources can be added with minimum or no change to the core logic to setup data sources and Liquibase
- a new "module" can be easily added to the project with separate persistence. The module will need to define its own JPA repository etc. Then add the data source information to `application.properties` of the main application 
- Liquibase can be enable/disable for the whole application or individual module

### Quick start
To run the application, clone the repo and run `mvn spring-boot:run`.

The application creates two H2 databases in memory for demo purposes. Go to http://localhost:8080/ to see the tables and data initialized by Liquibase. Use the form buttons to trigger JPA update actions.

Navigate to http://localhost:80801 to use H2 console to check the datbase. Database names are `jdbc:h2:mem:vehicle` and `jdbc:h2:mem:vessel`. Login as `sa` with `password` as the password.

### How it works
Brief description on how this Spring Boot application is configured.

#### Modules
- in this application, "module" means a standalone business logic and has separate data source
- this application has two sample "modules": `vehicle` and `vessel`, and each has their own entities and repositories
- each module has its own JPA configuration
- a module could be implemented in the same project source tree or pulled in from another source tree during compilation. They can even be coming from separate jar files and packaged together into an uber jar for deployment
- note that a module, in theory, could has one set of data source when the module is deployed as standalone microservice. And another data source when packaged as a "module" in a monolith application. See the `ConditionalOnProperty` in [PersistenceVehicleConfiguration](src/main/java/net/clarenceho/multi_liquibase/modules/vehicle/PersistenceVehicleConfiguration.java) or [PersistenceVesselConfiguration](src/main/java/net/clarenceho/multi_liquibase/modules/vessel/PersistenceVesselConfiguration.java)

#### [application.properties](src/main/resources/application.properties)
- this application doesn't use the usual `spring.datasourc.*` properties to configure data sources
- it even disables `DataSourceAutoConfiguration` and `LiquibaseAutoConfiguration`. See [MultiLiquibaseApplication](src/main/java/net/clarenceho/multi_liquibase/MultiLiquibaseApplication.java)
- instead, it uses custom properties `demo.datasources.[module_name].*` to define map of data source properties and Liquibase configuration for multiple modules

#### [DataSourceProperties](src/main/java/net/clarenceho/multi_liquibase/datasource/DataSourceProperties.java) and [DataSourceProperty](src/main/java/net/clarenceho/multi_liquibase/datasource/DataSourceProperty.java)
- these are the classes to read the configuration from `application.properties`
- each "module" can has its own data connection properties and Liquibase properties

#### [MultiDataSourceConfiguration](src/main/java/net/clarenceho/multi_liquibase/MultiDataSourceConfiguration.java)
- this configuration creates the `DataSource` objects for each module defined in the `application.properties` file

#### [MultiLiquibaseConfiguration](src/main/java/net/clarenceho/multi_liquibase/MultiLiquibaseConfiguration.java)
- this configuration initializes `MultiDataSourceLiquibase` class based on properties from `application.properties`
- it can be disabled with `spring.liquibase.enabled`, which switch on/off the Liquibase execution for the whole application during startup

#### [MultiDataSourceLiquibase](src/main/java/net/clarenceho/multi_liquibase/datasource/MultiDataSourceLiquibase.java)
- this bean, when initialized, will execute Liquibase (if enabled) for each module in parallel

#### Liquibase changelog files
- Liquibase changelog files are stored in separate sub-folder under `resources/*/db` for each "module" 

### Test Liquibase migration
- to test the Liquibase migration, change the data source url in `application.properties` to a persistent file (e.g. from `jdbc:h2:mem:vehicle` to `jdbc:h2:file:./spring-boot-h2-vehicle-db`)
- create new changelog files
- test it with Liquibase turn on/off for the whole application or individual module

### References

#### [spring-boot-multitenancy-datasource-liquibase](https://github.com/dijalmasilva/spring-boot-multitenancy-datasource-liquibase)
- a repo on using multiple data sources with Liquibase. But its goal is to configure the application to support multi-tenant. And it has a filter to detect the tenant on each incoming request and store the corresponding data source in ThreadLocal for subsequent db access
- it inspired the use of `MultiDataSourceLiquibase` class of this POC
 