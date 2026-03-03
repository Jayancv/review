package it.schwarz.jobs.review.coupon.config.initializer;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@Profile("dev")
@ConditionalOnProperty(name = "coupon.provider", havingValue = "jpa")
public class JpaDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(JpaDataInitializer.class);

    @Value("${coupon.init-data:false}")
    private boolean initData;

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        if (initData) {
            log.warn("Loading JPA test data from SQL - dev only");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("db/data.sql"));
            initializer.setDatabasePopulator(populator);
        } else {
            log.info("JPA data initializer skipped - coupon.init-data=false");
        }

        return initializer;
    }
}