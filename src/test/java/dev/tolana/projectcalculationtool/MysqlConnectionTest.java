package dev.tolana.projectcalculationtool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class MysqlConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testMysqlConnection() throws SQLException {
        String url1 = "/organisation/6/department/5/team/3/project/6/task/4";
        String url2 = "/organisation/6/department/5/team/3/project/6";
        String url3 = "/organisation/6/department/5/team/3";
        String url4 = "/organisation/6/department/5";
        String url5 = url1.substring(1);
        String[] tokens = url5.split("/");
        for (String token : tokens) {
            System.out.println("token: " + token);
        }
        int length = tokens.length;
        System.out.println("length: " + length);
        switch(length) {
            case 2 -> System.out.println("org");
            case 4 -> System.out.println("department");
            case 6 -> System.out.println("team");
            case 8 -> System.out.println("project");
            case 10 -> System.out.println("task");
        }

    }


    @Test
    void testConnection() {

        try(Connection con = dataSource.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES;");
            if (rs.next()) {
                String dbName = rs.getString(1);
                System.out.println("Database Name: " + dbName);
                assertFalse(dbName.isEmpty());
            } else {
                fail();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
