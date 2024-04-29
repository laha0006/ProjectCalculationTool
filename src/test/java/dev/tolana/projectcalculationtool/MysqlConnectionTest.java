package dev.tolana.projectcalculationtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootTest
public class MysqlConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() {
        String SQL = """
                CREATE TABLE test (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255));
                """;
        try(Connection con = dataSource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.execute("DROP TABLE IF EXISTS test");
            stmt.execute(SQL);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
