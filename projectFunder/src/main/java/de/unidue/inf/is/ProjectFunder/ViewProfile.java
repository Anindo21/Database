package de.unidue.inf.is.ProjectFunder;

import de.unidue.inf.is.domain.Project;
import de.unidue.inf.is.domain.SupportedProjects;
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

public class ViewProfile extends HttpServlet {
    private Boolean error_detected = false;
    private String error_message = "Error with the SQL commands";


    private String userEmail = "";
    private String name = "";
    private int funding = 0;
    private int created = 0;
    private ArrayList<Project> uProject = new ArrayList<>();
    private ArrayList<SupportedProjects> sProject = new ArrayList<>();
    
    private String profiles= "(SELECT b.name, b.email, funding, created " + 
    		"FROM dbp034.benutzer b " + 
    		"LEFT JOIN (SELECT spender as s, count(spender) as funding from dbp034.spenden group by spender) " + 
    		"ON b.email = s " + 
    		"LEFT JOIN (SELECT ersteller as e, count(ersteller) as created from dbp034.projekt group by ersteller) " + 
    		"ON b.email = e)";
    private String mainP = "(SELECT email, kennung, b.name , titel, status, icon, sum(spendenbetrag) as summe " + 
    		"FROM dbp034.projekt p LEFT JOIN dbp034.benutzer b " + 
    		"ON b.email = p.ersteller " + 
    		"LEFT JOIN dbp034.kategorie k ON k.id = p.kategorie " + 
    		"LEFT JOIN dbp034.spenden s ON p.kennung = s.projekt " + 
    		"GROUP BY email, kennung, b.name, titel, status, icon)";
    private String supProject = "(SELECT kennung, titel, status, icon, spendenbetrag, spender, finanzierungslimit as limit " + 
    		"FROM dbp034.projekt p " + 
    		"INNER JOIN dbp034.spenden s ON p.kennung = s.projekt " + 
    		"INNER JOIN dbp034.kategorie k ON p.kategorie = k.id " + 
    		"WHERE sichtbarkeit = 'oeffentlich')";
    

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	Connection con = null;
    	PreparedStatement psInfo = null;
    	PreparedStatement psCreated = null;
    	PreparedStatement psSupported = null;
        String pEmail = request.getParameter("u");
        ResultSet rsInfo = null;
        ResultSet rsCreated = null;
        ResultSet rsSupported = null;
        try {
            con = DBUtil.getExternalConnection();
            psInfo = con.prepareStatement("SELECT * FROM " + profiles + " WHERE email = ?");
            psCreated = con.prepareStatement("SELECT kennung, titel, status, icon, summe FROM " + mainP + " WHERE email = ?");
            psSupported = con.prepareStatement("SELECT * FROM " + supProject + " WHERE spender = ?");
            psInfo.setString(1, pEmail);
            psCreated.setString(1, pEmail);
            psSupported.setString(1, pEmail);
            try {
                rsInfo = psInfo.executeQuery();
                rsCreated = psCreated.executeQuery();
                rsSupported = psSupported.executeQuery();
                //Load info section of profile page
                //Load projected which have been created by that user
                while(rsCreated.next()) {
                    int id = rsCreated.getInt("kennung");
                    String titel = rsCreated.getString("titel");
                    String status = rsCreated.getString("status");
                    String icon = rsCreated.getString("icon");
                    BigDecimal sum = rsCreated.getBigDecimal("summe");
                    sum = (sum == null)? BigDecimal.valueOf(0) : sum;
                    Project p = new Project(id, titel, "", "", status, icon, sum);
                    uProject.add(p);
                }
                
                while(rsInfo.next()) {
                    userEmail = rsInfo.getString("email");
                    name = rsInfo.getString("name");
                    funding = rsInfo.getInt("funding");
                    created = rsInfo.getInt("created");
                }               
                //show donated projects
                while(rsSupported.next()) {
                    int id = rsSupported.getInt("kennung");
                    String titel = rsSupported.getString("titel");
                    String status = rsSupported.getString("status");
                    String icon  = rsSupported.getString("icon");
                    java.math.BigDecimal betrag = rsSupported.getBigDecimal("spendenbetrag");
                    java.math.BigDecimal limit = rsSupported.getBigDecimal("limit");

                    SupportedProjects sp = new SupportedProjects(id, titel, status, icon, limit, betrag);
                    sProject.add(sp);
                }
                rsSupported.close();
                rsCreated.close();
                rsInfo.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            psSupported.close();
            psCreated.close();
            psInfo.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            error_detected = true;
            error_message = "SQL ERROR - NO RESULTS";
        }

        request.setAttribute("error_detected", error_detected);
        request.setAttribute("error_message", error_message);
        request.setAttribute("userEmail", userEmail);
        request.setAttribute("name", name);
        request.setAttribute("created", created);
        request.setAttribute("funding", funding);
        request.setAttribute("userCreatedProjects", uProject);
        request.setAttribute("supportedProjects", sProject);
        request.getRequestDispatcher("ProjectFunder_FTL/view_profile.ftl").forward(request, response);

        error_detected = false;
        userEmail = "";
        funding = 0;
        name = "";
        created = 0;
        uProject.clear();
        sProject.clear();

    }


}
