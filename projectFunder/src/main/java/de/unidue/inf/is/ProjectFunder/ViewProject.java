package de.unidue.inf.is.ProjectFunder;

import de.unidue.inf.is.domain.Comment;
import de.unidue.inf.is.domain.Donation;
import de.unidue.inf.is.utils.DBUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewProject extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {

        final String loggedInUser = "dummy@dummy.com";
        final String comment = "(SELECT * FROM (" + 
        		"SELECT text, projekt as kennung, " + 
        		"CASE " + 
        		"	WHEN sichtbarkeit = 'privat' THEN 'Anonymous' " + 
        		"	ELSE name " + 
        		"END AS name " + 
        		"FROM dbp034.kommentar INNER JOIN dbp034.schreibt " + 
        		"ON id = kommentar " + 
        		"INNER JOIN dbp034.Benutzer ON " + 
        		"Benutzer = email " + 
        		"ORDER BY datum desc)) ";
        final String donation ="(select * from (" + 
        		"select spendenbetrag, projekt, " + 
        		"CASE " + 
        		"	WHEN sichtbarkeit = 'privat' THEN 'Anonymous' " + 
        		"	ELSE name " + 
        		"END AS name " + 
        		"FROM dbp034.spenden inner join dbp034.benutzer " + 
        		"ON spender = email " + 
        		"ORDER BY spendenbetrag desc" + 
        		"))";
        final String infos = "(SELECT kennung,summe, titel, p.beschreibung, status, finanzierungslimit, ersteller, b.name, icon, vid, vtitel " + 
        		"FROM dbp034.projekt p LEFT JOIN ( SELECT Projekt as pid, sum(spendenbetrag) as summe from dbp034.spenden group by Projekt) " + 
        		"ON kennung = pid " + 
        		"INNER JOIN dbp034.benutzer b ON " + 
        		"ersteller = email " + 
        		"INNER JOIN dbp034.kategorie ON " + 
        		"id = kategorie " + 
        		"LEFT JOIN (SELECT titel as vtitel, kennung as vid from dbp034.projekt) ON " + 
        		"vorgaenger = vid)";

        //Info section variables
        BigDecimal sum, limit;
        String title, description, status, cMail, cName, icon, vtitle;
        Integer vid;
        int id;
        
      //Comment section variables
        ArrayList<Comment> comments = new ArrayList<>();

        //Donation section variables
        ArrayList<Donation> donations = new ArrayList<>();

        id = Integer.parseInt(req.getParameter("id"));
        
        Connection con = null;
        PreparedStatement psComment = null;
        PreparedStatement psDonate = null;
        PreparedStatement psInfo = null;
        
     //Connect to database and get results
        try {
            con = DBUtil.getExternalConnection();
            psComment = con.prepareStatement("SELECT name, text from " + comment + " WHERE kennung = ?");
            psDonate = con.prepareStatement("SELECT name, spendenbetrag from " + donation +" WHERE projekt = ?");
            psInfo = con.prepareStatement("SELECT * FROM " + infos +" WHERE kennung = ?");

            psComment.setInt(1, id);
            psDonate.setInt(1, id);
            psInfo.setInt(1, id);

            try(ResultSet rInfo = psInfo.executeQuery();
                ResultSet rDonation = psDonate.executeQuery();
                ResultSet rComment = psComment.executeQuery()) {

                // Read results for info section
                if (rInfo.next()) {
                    sum = rInfo.getBigDecimal("summe");
                    title = rInfo.getString("titel");
                    description = rInfo.getString("beschreibung");
                    status = rInfo.getString("status");
                    limit = rInfo.getBigDecimal("finanzierungslimit");
                    cMail = rInfo.getString("ersteller");
                    cName = rInfo.getString("name");
                    icon = rInfo.getString("icon");
                    vid = rInfo.getInt("vid"); //Vorgänger ID
                    if (rInfo.wasNull()) vid = null;
                    vtitle = rInfo.getString("vtitel"); // Vorgänger Titel
                } else {
                    //If project is not found then id will be abort
                    resp.sendError(400, "Project not found");
                    return;
                }

                //Read donation result set into ArrayList
                while(rDonation.next()) {
                    String name = rDonation.getString("name");
                    BigDecimal amount = rDonation.getBigDecimal("spendenbetrag");

                    donations.add(new Donation(name, amount));
                }

                //Read comment result set into ArrayList
                while(rComment.next()) {
                    String name = rComment.getString("name");
                    String text = rComment.getString("text");

                    comments.add(new Comment(name, text));
                }
            }

            //Set attributes for info section
            if(sum != null) {
                req.setAttribute("sum", sum);
            } else 
            	req.setAttribute("sum", 0);
            req.setAttribute("id", id);
            req.setAttribute("title", title);
            req.setAttribute("description", description);
            req.setAttribute("status", status);
            req.setAttribute("limit", limit);
            req.setAttribute("creatorMail", cMail);
            req.setAttribute("creatorName", cName);
            req.setAttribute("icon", icon);
            

            if(vid == null) {
                req.setAttribute("vorganger", false);
            } else {
                req.setAttribute("vorganger", true);
                req.setAttribute("vid", vid);
                req.setAttribute("vtitle", vtitle);
            }

            //Set attribute for donation section
            req.setAttribute("donations", donations);

            //Set attribute for comment section
            req.setAttribute("comments", comments);

            req.setAttribute("loggedInUser", loggedInUser);

            req.getRequestDispatcher("ProjectFunder_FTL/view_project.ftl").forward(req, resp);

            psInfo.close();
            psDonate.close();
            psComment.close();
            con.close();

        } catch(SQLException e) {
            //Handle Error - Abort
            resp.sendError(500, "Database Error");
        }
    }

    //If user clicks delete button
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws IOException {


        int id = Integer.parseInt(req.getParameter("projectid"));
        String loggedInUser = req.getParameter("loggedInUser");


        //Array Lists to hold the returned values
        ArrayList<Integer> commentIds = new ArrayList<>();
        ArrayList<Donation> donations = new ArrayList<>();
        Connection con = null;
        PreparedStatement psIds = null;
        PreparedStatement psdeleteComment = null;
        PreparedStatement psdeleteSchreibt = null;
        PreparedStatement psgetDonations = null;
        PreparedStatement psupdateAccount = null;
        PreparedStatement psdeleteDonations = null;
        PreparedStatement psdeleteProject = null;
        ResultSet rIds = null;
        ResultSet rDonations = null;

        try {
            con = DBUtil.getExternalConnection();
            try {
                psIds = con.prepareStatement("SELECT kommentar FROM dbp034.schreibt WHERE projekt = ?");
                psgetDonations = con.prepareStatement("SELECT spender, spendenbetrag FROM dbp034.spenden WHERE projekt = ?");
                psdeleteComment = con.prepareStatement("DELETE FROM dbp034.kommentar WHERE id = ?");
                psdeleteSchreibt = con.prepareStatement("DELETE FROM dbp034.schreibt where projekt = ?");              
                psdeleteDonations = con.prepareStatement("DELETE FROM dbp034.spenden WHERE projekt = ?");
                psdeleteProject = con.prepareStatement("DELETE from dbp034.projekt where kennung = ?");
                psupdateAccount = con.prepareStatement("UPDATE dbp034.konto SET guthaben = guthaben + ? WHERE inhaber = ?");
                

                //Prepare the SELECT Queries
                psIds.setInt(1, id);
                psgetDonations.setInt(1, id);

                //Get the results of SELECT Queries
                try{
                    rIds = psIds.executeQuery();
                    rDonations = psgetDonations.executeQuery();

                    //Add all comment Ids to list
                    while(rIds.next()) {
                        commentIds.add(rIds.getInt("kommentar"));
                    }

                    //Add all donations to list
                    while(rDonations.next()) {
                        String name = rDonations.getString("spender");
                        BigDecimal amount = rDonations.getBigDecimal("spendenbetrag");
                        donations.add(new Donation(name, amount));
                    }
                    rDonations.close();
                    rIds.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Delete all the stuff
                con.setAutoCommit(false);

                //delete all comments and schreibt relation
                psdeleteSchreibt.setInt(1, id);
                psdeleteSchreibt.executeUpdate();
                for(Integer cid : commentIds) {
                    psdeleteComment.setInt(1, cid);
                    psdeleteComment.executeUpdate();
                }

                //update accounts
                for(Donation d : donations) {
                    psupdateAccount.setBigDecimal(1, d.getAmount());
                    psupdateAccount.setString(2, d.getName());
                    psupdateAccount.executeUpdate();
                }

                //delete donations
                psdeleteDonations.setInt(1, id);
                psdeleteDonations.executeUpdate();

                //delete project
                psdeleteProject.setInt(1, id);
                psdeleteProject.executeUpdate();


                //Success -- commit changes
                con.commit();
                con.setAutoCommit(true);
                psdeleteProject.close();
                psdeleteDonations.close();
                psupdateAccount.close();
                psgetDonations.close();
                psdeleteSchreibt.close();
                psdeleteComment.close();
                psIds.close();

            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    con.rollback();
                    con.setAutoCommit(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                resp.sendError(500, "SQL ERROR");
            }
            con.close();
        } catch(SQLException e) {
            e.printStackTrace();
            resp.sendError(500, "SQL ERROR");
        }

        resp.sendRedirect("/view_main");

    }
}

