package de.unidue.inf.is.ProjectFunder;

import de.unidue.inf.is.utils.DBUtil;
import de.unidue.inf.is.domain.Project;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewProject extends HttpServlet {
    private String loggedInUser = "dummy@dummy.com";

    private String error_message = "";
    private Boolean error_detected = false;
    private Boolean success = false;

    private String title = "";
    private BigDecimal limit ;
    private Integer category = 0;
    private Integer vorganger = 0;
    private String description = "";

    private ArrayList<Project> getProjects() {
        ArrayList<Project> projects = new ArrayList<>();
        try {
            Connection con = DBUtil.getExternalConnection();
            PreparedStatement ps = con.prepareStatement("SELECT titel, kennung from dbp034.projekt WHERE ersteller = ?");
            ps.setString(1, loggedInUser);
            try {
                ResultSet rs = ps.executeQuery();
                while(rs.next()) {
                    int id = rs.getInt("kennung");
                    String title = rs.getString("titel");

                    Project p = new Project(id, title);
                    projects.add(p);
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ps.close();
            con.close();
        } catch(SQLException e) {
            error_message = "SQL ERROR - CAN NOT LOAD USER PROJECTS";
            error_detected = true;
            return null;
        }

        return projects;
    }
    //validates user input and sets attribute values if correct
    private Boolean varification(String title, String limit, String category, String vorganger, String description) {
//Check if whether all parameters exist
        if (title != null && limit != null && category != null && vorganger != null && description != null) {

            //check title length
            try {
                if (title.length() > 0 && title.length() <= 30) {
                    this.title = title;
                }
            } catch (NumberFormatException e) {
            error_message = "title must be greater than 0 and smaller than 30";
            error_detected = true;
            return false;
            }

            //check Limit
                this.limit = new java.math.BigDecimal(limit);
                if (this.limit.compareTo(new BigDecimal(100)) < 0) {
                    error_message = "Limit must be greater than 100";
                    error_detected = true;
                    return false;
                }

            //Parse vorgange
            if (vorganger.equals("None")) this.vorganger = null;
            else {
                try {
                    this.vorganger = Integer.parseInt(vorganger);
                } catch (NumberFormatException e) {
                    error_message = "Could not parse vorganger";
                    error_detected = true;
                    return false;
                }
            }
            //Check and parse category
            switch (category) {
                case "1": {
                    this.category = 1;
                    break;
                }
                case "2": {
                    this.category = 2;
                    break;
                }
                case "3": {
                    this.category = 3;
                    break;
                }
                case "4": {
                    this.category = 4;
                    break;
                }
                default: {
                    error_message = "Category not found";
                    return false;
                }
            }

            this.description = description;
        }
        //Everything successful - Return true
        return true;
    }
        //Creates project with given parameters
        private void createProject (String titel, String beschreibung, BigDecimal limit, String
        ersteller, Integer vorganger, Integer kategorie){
            try {
                Connection con = DBUtil.getExternalConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO dbp034.projekt (titel, beschreibung, finanzierungslimit, ersteller, vorgaenger, kategorie) VALUES (?, ?, ?, ?, ?, ?)");

                ps.setString(1, titel);
                ps.setString(2, beschreibung);
                ps.setBigDecimal(3, limit);
                ps.setString(4, ersteller);
                if (vorganger != null) {
                    ps.setInt(5, vorganger);

                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                ps.setInt(6, kategorie);
                ps.executeUpdate();

                success = true;
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
                error_detected = true;
            }
        }



    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String titleI = request.getParameter("title");
        String limitI = request.getParameter("limit");
        String categoryI = request.getParameter("category");
        String vorgangerI = request.getParameter("vorganger");
        String descriptionI = request.getParameter("description");
        if(varification(titleI, limitI, categoryI, vorgangerI, descriptionI)) {
            createProject(title, description, limit, loggedInUser, vorganger, category);
        } else {
            error_detected = true;
        }

        ArrayList<Project> projects = getProjects();
        request.setAttribute("projectList", projects);
        request.setAttribute("error_detected", error_detected);
        request.setAttribute("error_message", error_message);
        request.setAttribute("success", success);
        request.getRequestDispatcher("ProjectFunder_FTL/new_project.ftl").forward(request, response);

        error_detected = false;
        error_message = "";
        success = false;
        title = "";
        category = -1;
        vorganger = -1;
        description = "";

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
