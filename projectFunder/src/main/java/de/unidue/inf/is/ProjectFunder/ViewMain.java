package de.unidue.inf.is.ProjectFunder;

import de.unidue.inf.is.domain.Project;
import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class ViewMain extends HttpServlet {
    private String error_message = "There is Problem with your Database. Check and Come back!";
    private boolean error_report = false;
    private String loggedInUser = "dummy@dummy.com";
    
    private String mainP = "(SELECT email, kennung, b.name , titel, status, icon, sum(spendenbetrag) as summe " + 
    		"FROM dbp034.projekt p LEFT JOIN dbp034.benutzer b " + 
    		"ON b.email = p.ersteller " + 
    		"LEFT JOIN dbp034.kategorie k ON k.id = p.kategorie " + 
    		"LEFT JOIN dbp034.spenden s ON p.kennung = s.projekt " + 
    		"GROUP BY email, kennung, b.name, titel, status, icon)";


    private ArrayList<Project> getResults() {
        ArrayList<Project> projectList = new ArrayList<>();
        try {
            Connection con = DBUtil.getExternalConnection();
            PreparedStatement ps = con.prepareStatement(mainP);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                //Read and parse the result set into java objects
                int id = rs.getInt("kennung");
                String title = rs.getString("titel");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String status = rs.getString("status");
                String icon = rs.getString("icon");
                BigDecimal sum = rs.getBigDecimal("summe");
                sum = (sum == null)? BigDecimal.valueOf(0) : sum;

                Project p = new Project(id, title, name, email, status, icon, sum);
                projectList.add(p);
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException e) {
            error_message = "DATABASE ERROR - NO RESULTS";
            error_report = true;
            return null;
        }

        return projectList;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        error_report = false;
        //Get results and split them in open and closed projects
        ArrayList<Project> projects = getResults();
        ArrayList<Project> openProject = new ArrayList<>();
        ArrayList<Project> closedProject = new ArrayList<>();
        if(projects != null) {
            for (Project p : projects) {
                if (p.getStatus().equals("offen")) {
                    openProject.add(p);
                } else {
                    closedProject.add(p);
                }
            }
        }
        request.setAttribute("openProject", openProject);
        request.setAttribute("closedProject", closedProject);
        request.setAttribute("loggedInUser", loggedInUser);
        request.setAttribute("error_detected", error_report);
        request.setAttribute("error_message", error_message);
        request.getRequestDispatcher("ProjectFunder_FTL/view_main.ftl").forward(request, response);
        error_report = false;
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
}
