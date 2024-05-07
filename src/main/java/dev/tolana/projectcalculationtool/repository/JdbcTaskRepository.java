package dev.tolana.projectcalculationtool.repository;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class JdbcTaskRepository extends TaskRepository {

    private DataSource dataSource;
    public JdbcTaskRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
