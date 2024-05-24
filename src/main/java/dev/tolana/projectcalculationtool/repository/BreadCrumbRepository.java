package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.NameHierarchy;
import dev.tolana.projectcalculationtool.enums.EntityType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class BreadCrumbRepository {

    private final String NAME_HIERARCHY_SQL = """
                        SELECT tsk.name AS task_name,
                               pjt.name AS project_name,
                               tm.name AS team_name,
                               dpt.name AS department_name,
                               org.name AS organisation_name,
                               pjt2.name AS parent_project_name,
                               pjt2.id AS parent_project_id,
                               tsk2.name AS parent_task_name,
                               tsk2.id AS parent_task_id
                        FROM organisation org
                             LEFT JOIN department dpt
                                       ON org.id = dpt.organisation_id
                             LEFT JOIN team tm
                                       ON dpt.id = tm.department_id
                             LEFT JOIN project pjt
                                       ON tm.id = pjt.team_id
                             LEFT JOIN project pjt2
                                       ON pjt.parent_id = pjt2.id
                             LEFT JOIN task tsk
                                       ON pjt.id = tsk.project_id
                             LEFT JOIN task tsk2
                                       ON tsk.parent_id = tsk2.id
                        WHERE org.id = ? OR dpt.id = ? OR tm.id = ? OR pjt.id = ? OR tsk.id = ?;
            """;

    private DataSource dataSource;

    public BreadCrumbRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public NameHierarchy getNameHierarchy(long id, EntityType entityType) {
        System.out.println("WE'RE CHILLIN'");
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement(NAME_HIERARCHY_SQL);

            switch (entityType) {
                case TASK -> {ps.setLong(5, id); ps.setLong(4, 0);ps.setLong(3, 0);ps.setLong(2, 0);ps.setLong(1, 0);}
                case PROJECT -> {ps.setLong(5,0); ;ps.setLong(4, id);ps.setLong(3, 0);ps.setLong(2, 0);ps.setLong(1, 0);}
                case TEAM -> {ps.setLong(5,0); ;ps.setLong(4, 0);ps.setLong(3, id);ps.setLong(2, 0);ps.setLong(1, 0);}
                case DEPARTMENT -> {ps.setLong(5,0); ;ps.setLong(4, 0);ps.setLong(3, 0);ps.setLong(2, id);ps.setLong(1, 0);}
                case ORGANISATION -> {ps.setLong(5,0); ;ps.setLong(4, 0);ps.setLong(3, 0);ps.setLong(2, 0);ps.setLong(1, id);}
            }
            System.out.println("PS: " + ps.toString());
            System.out.println("EntityType: " + entityType.name());


            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new NameHierarchy(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getLong(7),
                        rs.getString(8),
                        rs.getLong(9)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getBreadCrumb(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
