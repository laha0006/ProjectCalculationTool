package dev.tolana.projectcalculationtool;

import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.util.RoleUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RoleAssingUtilTest {

    @Autowired
    private DataSource dataSource;

    private Connection con;


    @BeforeEach
    public void setUp() throws SQLException {
        if (con == null) {
            con = dataSource.getConnection();
        }
    }

    @Test
    public void test() throws SQLException {
        assertEquals(16, RoleUtil.removeAllRoles(con, EntityType.ORGANISATION, 5, "pig"));
    }
}
