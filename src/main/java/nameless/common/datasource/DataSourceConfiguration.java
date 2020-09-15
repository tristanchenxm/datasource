package nameless.common.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Bean("masterDataSourceConfig")
    @Primary
    @ConditionalOnClass(HikariDataSource.class)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties masterConfig() {
        return new DataSourceProperties();
    }

    @Bean("slaveDataSourceConfig")
    @ConditionalOnClass(HikariDataSource.class)
    @ConfigurationProperties(prefix = "spring.datasource-slave")
    public DataSourceProperties slaveConfig() {
        return new DataSourceProperties();
    }

    @Bean
    @ConditionalOnClass(HikariDataSource.class)
    public DataSource dataSource() {
        HikariDataSource masterDs = masterConfig().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        logger.info("************* Created master datasource {} connection to {}", masterDs.toString(), masterDs.getJdbcUrl());
        if (slaveConfig().getUrl() != null) {
            HikariDataSource slaveDs = slaveConfig().initializeDataSourceBuilder().type(HikariDataSource.class).build();
            logger.info("************* Create slave datasource {} connecting to {}", slaveDs.toString(), slaveDs.getJdbcUrl());
            return new MasterSlaveDualConnectionDataSource(masterDs, slaveDs);
        } else {
            logger.info("************* Use single node datasource");
            return new MasterSlaveDualConnectionDataSource(masterDs, masterDs);
        }
    }
}
