package dev.tolana.projectcalculationtool.util;

import dev.tolana.projectcalculationtool.constant.AccessLevel;
import dev.tolana.projectcalculationtool.constant.Role;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RoleAssignUtil {
    private static final String ORGANISATION_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES(?,?,?)";
    private static final String DEPARMTNET_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,department_id) VALUES(?,?,?)";
    private static final String TEAM_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,team_id) VALUES(?,?,?)";
    private static final String PROJECT_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,project_id) VALUES(?,?,?)";
    private static final String TASK_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,task_id) VALUES(?,?,?)";

    private static int assignRole(Connection con,AccessLevel accessLevel,long id, long roleId, String username) throws SQLException {
        String SQL = switch(accessLevel) {
            case TASK -> TASK_INSERT_ROLE_SQL;
            case PROJECT -> PROJECT_INSERT_ROLE_SQL;
            case TEAM -> TEAM_INSERT_ROLE_SQL;
            case DEPARTMENT -> DEPARMTNET_INSERT_ROLE_SQL;
            case ORGANISATION -> ORGANISATION_INSERT_ROLE_SQL;
        };
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString(1, username);
        ps.setLong(2, roleId);
        ps.setLong(3, id);
        return ps.executeUpdate();
    }

    public static int assignOrganisationRole(Connection con,long id, long roleId, String username) throws SQLException {
        return assignRole(con,AccessLevel.ORGANISATION,id,roleId,username);
    }

    public static int assignDepartmentRole(Connection con,long id, long roleId, String username) throws SQLException {
        return assignRole(con,AccessLevel.DEPARTMENT,id,roleId,username);
    }

    public static int assignTeamRole(Connection con,long id, long roleId, String username) throws SQLException {
        return assignRole(con,AccessLevel.TEAM,id,roleId,username);
    }

    public static int assignProjectRole(Connection con,long id, long roleId, String username) throws SQLException {
        return assignRole(con,AccessLevel.PROJECT,id,roleId,username);
    }

    public static int assignTaskRole(Connection con,long id, long roleId, String username) throws SQLException {
        return assignRole(con,AccessLevel.TASK,id,roleId,username);
    }


}
