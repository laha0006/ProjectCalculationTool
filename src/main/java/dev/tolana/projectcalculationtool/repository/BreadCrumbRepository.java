package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.NameHierarchy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class BreadCrumbRepository {

    private final String NAME_HIERARCHY_SQL = """
            SELECT tsk.name      AS task_name,
                   pjt.name      AS project_name,
                   tm.name       AS team_name,
                   dpt.name      AS department_name,
                   org.name      AS organisation_name,
                   pjt2.name AS parent_project_name,
                   tsk2.name AS parent_task_name
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
                           ON tsk.parent_id = tsk2.id;
            """;

    private DataSource dataSource;

    public BreadCrumbRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public NameHierarchy getNameHierarchy() {
        try(Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement(NAME_HIERARCHY_SQL);
            ResultSet rs = ps.executeQuery(NAME_HIERARCHY_SQL);
            if (rs.next()) {
                return new NameHierarchy(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7)
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
