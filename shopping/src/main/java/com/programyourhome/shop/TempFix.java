package com.programyourhome.shop;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

// FIXME: Should not be needed, Spring boot should take care of this automatically, dig further...
@Configuration
public class TempFix {

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/pyh");
        dataSource.setUsername("postgres");
        dataSource.setPassword("1qw23er4");
        return dataSource;
    }

}
