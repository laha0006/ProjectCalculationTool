package dev.tolana.projectcalculationtool.util;

import dev.tolana.projectcalculationtool.enums.EntityType;
import dev.tolana.projectcalculationtool.enums.UserRole;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class RoleUtil {
    private static final String ORGANISATION_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES(?,?,?)";
    private static final String DEPARMTNET_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,department_id) VALUES(?,?,?)";
    private static final String TEAM_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,team_id) VALUES(?,?,?)";
    private static final String PROJECT_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,project_id) VALUES(?,?,?)";
    private static final String TASK_INSERT_ROLE_SQL = "INSERT INTO user_entity_role(username,role_id,task_id) VALUES(?,?,?)";
    private static final String ORGANISATION_DELETE_ROLE_SQL = "DELETE FROM user_entity_role WHERE username= ? AND organisation_id = ? AND role_id = ?";
    private static final String DEPARTMENT_DELETE_ROLE_SQL = "DELETE FROM user_entity_role WHERE username= ? AND department_id = ? AND role_id = ?";
    private static final String TEAM_DELETE_ROLE_SQL = "DELETE FROM user_entity_role WHERE username= ? AND team_id= ? AND role_id = ?";
    private static final String PROJECT_DELETE_ROLE_SQL = "DELETE FROM user_entity_role WHERE username= ? AND project_id= ? AND role_id = ?";
    private static final String TASK_DELETE_ROLE_SQL = "DELETE FROM user_entity_role WHERE username= ? AND task_id = ? AND role_id = ?";
    private static final String ORGANISATION_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE organisation_id = ?";
    private static final String DEPARTMENT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE department_id = ?";
    private static final String TEAM_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE team_id = ?";
    private static final String PROJECT_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE project_id = ?";
    private static final String TASK_HIERARCHY_SQL = "SELECT * FROM hierarchy WHERE task_id = ?";
    private static final String DELETE_ALL_USER_ROLE__SQL = "DELETE FROM user_entity_role WHERE username = ? AND (task_id IN (%s) OR project_id IN (%s) OR team_id IN (%s) OR department_id IN (%s) OR organisation_id = ?);";

    private static int assignRole(Connection con, EntityType entityType, long id, UserRole userRole, String username) throws SQLException {
        String SQL = switch (entityType) {
            case TASK -> TASK_INSERT_ROLE_SQL;
            case PROJECT -> PROJECT_INSERT_ROLE_SQL;
            case TEAM -> TEAM_INSERT_ROLE_SQL;
            case DEPARTMENT -> DEPARMTNET_INSERT_ROLE_SQL;
            case ORGANISATION -> ORGANISATION_INSERT_ROLE_SQL;
        };
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString(1, username);
        ps.setLong(2, userRole.getRoleId());
        ps.setLong(3, id);
        return ps.executeUpdate();
    }

    private static int removeRole(Connection con, EntityType entityType, long id, UserRole userRole, String username) throws SQLException {
        String SQL = switch (entityType) {
            case TASK -> TASK_DELETE_ROLE_SQL;
            case PROJECT -> PROJECT_DELETE_ROLE_SQL;
            case TEAM -> TEAM_DELETE_ROLE_SQL;
            case DEPARTMENT -> DEPARTMENT_DELETE_ROLE_SQL;
            case ORGANISATION -> ORGANISATION_DELETE_ROLE_SQL;
        };
        PreparedStatement ps = con.prepareStatement(SQL);
        ps.setString(1, username);
        ps.setLong(2, id);
        return ps.executeUpdate();
    }

    private static Map<EntityType, Set<Long>> getEntityIdMapFromHierarchy(Connection con, EntityType entityType, long id) throws SQLException {
        Map<EntityType, Set<Long>> entityIdMap = new HashMap<>();
        String SQL = switch (entityType) {
            case TASK -> TASK_HIERARCHY_SQL;
            case PROJECT -> PROJECT_HIERARCHY_SQL;
            case TEAM -> TEAM_HIERARCHY_SQL;
            case DEPARTMENT -> DEPARTMENT_HIERARCHY_SQL;
            case ORGANISATION -> ORGANISATION_HIERARCHY_SQL;
        };
        PreparedStatement preparedStatement = con.prepareStatement(SQL);
        preparedStatement.setLong(1, id);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            if (!entityIdMap.containsKey(EntityType.TASK)) {
                entityIdMap.put(EntityType.TASK, new HashSet<>());
            }
            entityIdMap.get(EntityType.TASK).add(rs.getLong(1));
            entityIdMap.get(EntityType.TASK).add(rs.getLong(7));
            if (!entityIdMap.containsKey(EntityType.PROJECT)) {
                entityIdMap.put(EntityType.PROJECT, new HashSet<>());
            }
            entityIdMap.get(EntityType.PROJECT).add(rs.getLong(2));
            entityIdMap.get(EntityType.PROJECT).add(rs.getLong(6));
            if (!entityIdMap.containsKey(EntityType.TEAM)) {
                entityIdMap.put(EntityType.TEAM, new HashSet<>());
            }
            entityIdMap.get(EntityType.TEAM).add(rs.getLong(3));
            if (!entityIdMap.containsKey(EntityType.DEPARTMENT)) {
                entityIdMap.put(EntityType.DEPARTMENT, new HashSet<>());
            }
            entityIdMap.get(EntityType.DEPARTMENT).add(rs.getLong(4));
            if (!entityIdMap.containsKey(EntityType.ORGANISATION)) {
                entityIdMap.put(EntityType.ORGANISATION, new HashSet<>());
            }
            entityIdMap.get(EntityType.ORGANISATION).add(rs.getLong(5));
        }
        return entityIdMap;
    }

    public static String buildEntityIdString(Map<EntityType, Set<Long>> entityIdMap, EntityType entityType) {
        StringBuilder entityIdString = new StringBuilder();
        if (entityIdMap.get(entityType) == null) {
            return "0";
        }
        for (long id : entityIdMap.get(entityType)) {
            entityIdString.append(id).append(",");
        }
        return entityIdString.substring(0, entityIdString.length() - 1);
    }


    public static int removeAllRoles(Connection con, EntityType entityType, long id, String username) throws SQLException {
        Map<EntityType, Set<Long>> entityIdMap = getEntityIdMapFromHierarchy(con, entityType, id);
        String SQL = switch (entityType) {
            case TASK ->
                    String.format(DELETE_ALL_USER_ROLE__SQL, buildEntityIdString(entityIdMap, EntityType.TASK), "(0)", "(0)", "(0)");
            case PROJECT -> String.format(DELETE_ALL_USER_ROLE__SQL,
                    buildEntityIdString(entityIdMap, EntityType.TASK),
                    buildEntityIdString(entityIdMap, EntityType.PROJECT), "(0)", "(0)");
            case TEAM -> String.format(DELETE_ALL_USER_ROLE__SQL,
                    buildEntityIdString(entityIdMap, EntityType.TASK),
                    buildEntityIdString(entityIdMap, EntityType.PROJECT),
                    buildEntityIdString(entityIdMap, EntityType.TEAM), "(0)");
            case DEPARTMENT, ORGANISATION -> String.format(DELETE_ALL_USER_ROLE__SQL,
                    buildEntityIdString(entityIdMap, EntityType.TASK),
                    buildEntityIdString(entityIdMap, EntityType.PROJECT),
                    buildEntityIdString(entityIdMap, EntityType.TEAM),
                    buildEntityIdString(entityIdMap, EntityType.DEPARTMENT));
        };
        PreparedStatement preparedStatement = con.prepareStatement(SQL);
        preparedStatement.setString(1, username);
        if (entityType == EntityType.ORGANISATION) {
            preparedStatement.setLong(2, id);
        } else {
            preparedStatement.setLong(2, 0);
        }
        return preparedStatement.executeUpdate();
    }


    public static int removeOrganisationRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return removeRole(con, EntityType.ORGANISATION, id, userRole, username);
    }

    public static int removeDepartmentRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return removeRole(con, EntityType.DEPARTMENT, id, userRole, username);
    }

    public static int removeTeamRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return removeRole(con, EntityType.TEAM, id, userRole, username);
    }

    public static int removeProjectRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return removeRole(con, EntityType.PROJECT, id, userRole, username);
    }

    public static int removeTaskRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return removeRole(con, EntityType.TASK, id, userRole, username);
    }

    public static int assignOrganisationRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return assignRole(con, EntityType.ORGANISATION, id, userRole, username);
    }

    public static int assignDepartmentRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return assignRole(con, EntityType.DEPARTMENT, id, userRole, username);
    }

    public static int assignTeamRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return assignRole(con, EntityType.TEAM, id, userRole, username);
    }

    public static int assignProjectRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return assignRole(con, EntityType.PROJECT, id, userRole, username);
    }

    public static int assignTaskRole(Connection con, long id, UserRole userRole, String username) throws SQLException {
        return assignRole(con, EntityType.TASK, id, userRole, username);
    }


}
