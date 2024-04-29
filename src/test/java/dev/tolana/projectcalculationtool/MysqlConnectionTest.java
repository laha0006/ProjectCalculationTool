package dev.tolana.projectcalculationtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.function.Try.success;

@SpringBootTest
public class MysqlConnectionTest {

    @Autowired
    private DataSource dataSource;
//    test
    @Test
    void testConnection() {
        String SQL = """
                CREATE TABLE test (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255));
                """;
        boolean res = false;
        try(Connection con = dataSource.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.execute("DROP TABLE IF EXISTS test");
            System.out.println("RES: " + res);
            res = stmt.execute(SQL);
            System.out.println("RES: " + res);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertTrue(res);
    }
}
