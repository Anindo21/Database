package de.unidue.inf.is.ProjectFunder;

import de.unidue.inf.is.domain.Project;
import de.unidue.inf.is.utils.DBUtil;
import org.apache.commons.lang.math.NumberUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class EditProject extends HttpServlet {
    enum Status {TITLE,CREATOR,LIMIT,CATEGORY,PREDECESSOR,OPEN,CLOSED}

    private String error_message = "";
    private Boolean error_detected = false;
    private Boolean success = false;
    @Override
    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {

        final String loggedInUser = "dummy@dummy.com";
        String title = "";
        String description = "";
        BigDecimal limit = null;
        int id = 0;
        int predecissor;
        int category;

        id = Integer.parseInt(req.getParameter("id"));
        Connection con = null;
		PreparedStatement ps = null;

        //Get the information that the user can edit

        try{
            con = DBUtil.getExternalConnection();
            ps = con.prepareStatement("SELECT titel, beschreibung, finanzierungslimit, vorgaenger, kategorie FROM dbp034.projekt WHERE kennung = ?");
            ps.setInt(1, id);
            try{
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    title = rs.getString("titel");
                    description = rs.getString("beschreibung");
                    limit = rs.getBigDecimal("finanzierungslimit");
                    predecissor = rs.getInt("vorgaenger");
                    category = rs.getInt("kategorie");
                } else {
                    error_message = "No project for that id";
                    error_detected = true;
                    return;
                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ps.close();
            con.close();
        } catch(SQLException e) {
            error_message = "Database Error";
            error_detected = true;
            return;
        }


        //get all projects created by user
        //these are all the projects the user can choose as predecessors
        //except for the project itself
        ArrayList<Project> projects = getProjects(loggedInUser);
      
            for (int i = 0; i < projects.size(); i++) {
                if (projects.get(i).getId() == id) projects.remove(i);
            }

        //Set attributes and forward to user
        req.setAttribute("title", title);
        req.setAttribute("limit", limit);
        req.setAttribute("description", description);
        req.setAttribute("projectList",projects);
        req.setAttribute("loggedInUser",loggedInUser);
        req.setAttribute("error_detected", error_detected);
        req.setAttribute("error_message", error_message);
        req.setAttribute("success", success);
        req.getRequestDispatcher("ProjectFunder_FTL/edit_project.ftl").forward(req,resp);

        error_message = "";
        error_detected = false;
        success = false;
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        String creator = req.getParameter("creator");
        int id;
        BigDecimal limit;
        Integer category;
        Integer predecissor;
        String obj = req.getParameter("predecissor");
        String title = req.getParameter("title");
        String desciption = req.getParameter("description");

        try {
        	id = Integer.parseInt(req.getParameter("id"));
            predecissor = NumberUtils.createInteger(obj);
            limit = NumberUtils.createBigDecimal(req.getParameter("limit"));
            category = NumberUtils.createInteger(req.getParameter("category"));
        } catch (NumberFormatException e) {
            error_message = "Error in input";
            error_detected = true;
            return;
        }

        Status status = varification(id, title, creator, predecissor, limit,category);
        if (status == Status.OPEN) {
            if (updateDatabase(id,title,desciption,predecissor,limit,category)) {
                resp.sendRedirect("/view_project?id=" + id);
            } else {
                //Updating failed
                error_message = "Database Error";
                error_detected = true;
            }
        } else {
            //Status is NOT OPEN
            switch (status) {
                case CLOSED:
                    error_message = "Project is closed";
                    error_detected = true;
                    break;
                
                default:
                    error_message = "Other errors";
                    error_detected = true;
            }
        }

    }

    private boolean updateDatabase(int id, String title,String description,Integer predecissor,BigDecimal limit,Integer category) {
        try {
        	Connection con =null;
        	PreparedStatement ps = null;
            con = DBUtil.getExternalConnection();
            ps = con.prepareStatement("UPDATE dbp034.projekt SET titel = ?, beschreibung = ?, finanzierungslimit = ?, kategorie = ?, vorgaenger = ? WHERE kennung = ?");
            ps.setInt(1,id);
            ps.setString(2,title);
            ps.setString(3,description);
            if (predecissor == null) {
                ps.setNull(4,Types.INTEGER);
            } else {
                ps.setInt(4,predecissor);
            }
            ps.setBigDecimal(5,limit);
            ps.setInt(6,category);

            ps.executeUpdate();
            success = true;
            ps.close();
            con.close();

        } catch (SQLException e) {
            error_message = "Update Failed!";
            error_detected = true;
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Status varification(int id, String title, String creator, Integer predecissor, BigDecimal limit, Integer category) {
        if (creator == null) return Status.CREATOR;
        if (title == null) return Status.TITLE;
        if (category == null) return Status.CATEGORY;
        if (limit == null) return Status.LIMIT;

        if (title.length() <= 0 || title.length() > 30) {
            return Status.TITLE;
        }
        if (category < 1 || category > 4) {
            return Status.CATEGORY;
        }

        if (!(predecissor == null)) {
            ArrayList<Project> projects = getProjects(creator);
            boolean getid = false;
            for (Project p : projects) {
                if (p.getId() == predecissor) {
                	getid = true;
                    break;
                }
            }
            if (getid = false) {
                return Status.PREDECESSOR;
            }
        }
        try {
        	Connection con = null;
        	PreparedStatement ps = null;
            con = DBUtil.getExternalConnection();
            ps = con.prepareStatement("SELECT ersteller, finanzierungslimit, status FROM dbp034.projekt WHERE kennung = ?");
            ps.setInt(1,id);
            try {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String c = rs.getString("ersteller");
                    BigDecimal l = rs.getBigDecimal("finanzierungslimit");
                    String status = rs.getString("status");

                    if (!Objects.equals(creator,c)) return Status.CREATOR;
                    if (Objects.equals(status,"geschlossen")) return Status.CLOSED;
                    if (limit.compareTo(l) < 0) return Status.LIMIT;


                }
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ps.close();
            con.close();
            return Status.OPEN;


        } catch (SQLException e) {
            error_message = "Error";
            error_detected = true;
        }
		return null;
    }

    private ArrayList<Project> getProjects(String creator) {

        ArrayList<Project> projects = new ArrayList<>();

        //Create connection and get the project objects
        try {
        	Connection con = null;
        	PreparedStatement ps = null;
            con = DBUtil.getExternalConnection();
            ps = con.prepareStatement("SELECT titel, kennung from dbp034.projekt WHERE ersteller = ?");
            ps.setString(1,creator);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    //Add each result to project list
                    int id = rs.getInt("kennung");
                    String title = rs.getString("titel");

                    Project p = new Project(id,title);
                    projects.add(p);
                }
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            error_message = "Error getting the project";
            error_detected = true;
            return null;
        }

        return projects;
    }


}

