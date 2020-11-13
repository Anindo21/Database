package de.unidue.inf.is.ProjectFunder;

import de.unidue.inf.is.utils.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class NewComment extends HttpServlet {
    private int projectid;
    private final String loggedInUser = "dummy@dummy.com";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String error_message = "";
        String projectTitle = "";
        boolean error_detected = false;
        try {
            projectid = Integer.parseInt(req.getParameter("id"));
            projectTitle = getTitle(projectid);
        } catch(NumberFormatException | NullPointerException ex) {
            error_message = "Could not find project with that id";
            error_detected = true;
        }
        req.setAttribute("error_detected", error_detected);
        req.setAttribute("error_message", error_message);
        req.setAttribute("loggedInUser", loggedInUser);
        req.setAttribute("projectTitle", projectTitle);
        req.getRequestDispatcher("ProjectFunder_FTL/new_comment.ftl").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String comment = req.getParameter("comment");
        boolean anonymous = Objects.equals(req.getParameter("anonymous"), "true");

        if(insertComment(comment, anonymous, loggedInUser, projectid)){
            resp.sendRedirect("ProjectFunder_FTL/view_project?id=" + projectid);
        }

    }

    private String getTitle(int id)  throws NullPointerException{
        String title = "";
        try {
            Connection con = DBUtil.getExternalConnection();
            PreparedStatement ps = con.prepareStatement("SELECT titel FROM dbp034.projekt WHERE kennung = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    title = rs.getString(1);
                } else {
                    throw new NullPointerException();
                }
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    private boolean insertComment(String comment, boolean anonymous, String user, int projectID)  {
        boolean success = true;
        short generatedID;

        try {
            Connection con = DBUtil.getExternalConnection();
            con.setAutoCommit(false);
            try {
                PreparedStatement ps1 = con.prepareStatement("SELECT id from final table (INSERT INTO dbp034.Kommentar (id, text, sichtbarkeit) VALUES (?,?,?))");
                PreparedStatement ps2 = con.prepareStatement("INSERT INTO dbp034.schreibt (benutzer, projekt, kommentar) values (?,?,?)");
                Clob clob = con.createClob();
                ps1.setInt(1,90);
                clob.setString(2, comment);
                ps1.setClob(2, clob);
                if (anonymous) ps1.setString(3, "privat");
                else ps1.setString(3, "oeffentlich");

                try (ResultSet rs = ps1.executeQuery()) {
                    if(rs.next()) {
                        generatedID = rs.getShort(1);
                    } else {
                        throw new SQLException();
                    }
                }

                ps2.setString(1, user);
                ps2.setInt(2, projectID);
                ps2.setShort(3, generatedID);

                ps2.executeUpdate();

                //Success
                con.commit();
                con.setAutoCommit(true);
                ps2.close();
                ps1.close();
            } catch (SQLException e) {
                e.printStackTrace();
                success = false;

                try {
                    con.rollback();
                    con.setAutoCommit(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            con.close();
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }
}