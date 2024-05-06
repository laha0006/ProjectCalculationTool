package dev.tolana.projectcalculationtool.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AuthorizationRepository {

    private DataSource dataSource;

    public AuthorizationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    
}
