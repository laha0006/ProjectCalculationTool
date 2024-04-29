package dev.tolana.projectcalculationtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.function.Try.success;

@SpringBootTest
public class MysqlConnectionTest {

    @Autowired
    private DataSource dataSource;
//    test
    @Test
    void testConnection() {

        try(Connection con = dataSource.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES;");
            if (rs.next()) {
                String dbName = rs.getString(1);
                assertTrue(dbName.contains("MySQL"));
            } else {
                fail();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
