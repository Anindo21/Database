package de.unidue.inf.is.ProjectFunder;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Project;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Servlet implementation class SearchServlet
 */
//@WebServlet(name = "search_servlet", urlPatterns = { "/search" })
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private Boolean error_detected = false;
    private String error_message = "SQL ERROR - NO RESULTS";
    private String PTitle = "";
	private List<Project> projects = new ArrayList<>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String loggedInUser = "dummy@dummy.com";
		PTitle = request.getParameter("title");
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
		//String titel = (String)request.getParameter("Suchen");
		Connection connection = null;
		PreparedStatement stmt = null;
		String mainP = "(SELECT email, kennung, b.name , titel, status, icon, sum(spendenbetrag) as summe " + 
	    		"FROM dbp034.projekt p LEFT JOIN dbp034.benutzer b " + 
	    		"ON b.email = p.ersteller " + 
	    		"LEFT JOIN dbp034.kategorie k ON k.id = p.kategorie " + 
	    		"LEFT JOIN dbp034.spenden s ON p.kennung = s.projekt " + 
	    		"GROUP BY email, kennung, b.name, titel, status, icon)";
				                 				
		try {
			connection = DBUtil.getExternalConnection();
			stmt = connection.prepareStatement("SELECT * FROM " + mainP + " WHERE LOWER(titel) like ?");
			stmt.setString(1, PTitle + "%");
			
			try{
				ResultSet res = stmt.executeQuery();
			while(res.next()) {
				int id = res.getInt("kennung");
                String title = res.getString("titel");
                String name = res.getString("name");
                String status = res.getString("status");
                String icon = res.getString("icon");
                BigDecimal sum = res.getBigDecimal("summe");
                sum = (sum == null) ? BigDecimal.valueOf(0) : sum;
                String email = res.getString("email");

                Project p = new Project(id, title, name, email, status, icon, sum);
                projects.add(p);

			}
			res.close();
			}catch(SQLException e) {
				  e.printStackTrace();
			  }
			stmt.close();
			connection.close();
		}catch(SQLException e) {
			error_message = "SQL ERROR - NO RESULTS";
			error_detected = true;
		}
		
			request.setAttribute("error_detected", error_detected);
	        request.setAttribute("error_message", error_message);
	        request.setAttribute("loggedInUser", loggedInUser); 
	        request.setAttribute("resultList", projects);
	        request.getRequestDispatcher("ProjectFunder_FTL/search.ftl").forward(request, response);
	        error_detected = false;
	        projects.clear();
		
		
		}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }
		
	}
