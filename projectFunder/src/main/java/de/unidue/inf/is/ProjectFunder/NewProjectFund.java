package de.unidue.inf.is.ProjectFunder;

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
import java.util.Objects;

public class NewProjectFund extends HttpServlet {
    enum Status {
    	OPEN, CLOSED, checkZero, enoughMoney, DONATION
        }
    final String loggedInUser = "dummy@dummy.com";

    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {

        String title;
        int id;

        // Check if there was an error with the previous post request
        try {
            id = Integer.parseInt(req.getParameter("id"));
            title = getTitle(id);
            if (title == null) {
                resp.sendError(400,"Id not valid");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendError(400,"Can not parse id");
            return;
        }


        //Set attributes
        req.setAttribute("projectTitle",title);
        req.setAttribute("loggedInUser",loggedInUser);
        req.setAttribute("id",id);
        req.getRequestDispatcher("ProjectFunder_FTL/new_project_fund.ftl").forward(req,resp);

    }

    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        Status status;
        try {
            //Read parameters
            boolean anonymous = Objects.equals(req.getParameter("anonymous"),"true"); //Defaults to false if attribute is null
            String user = req.getParameter("user");
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            int id = Integer.parseInt(req.getParameter("id"));
            
            //Check if input is valid
            status = varification(user,id,amount);
            if (status == Status.OPEN) {
                
                if (inputInDatabase(user,id,amount,anonymous)) {
                    //success -- redirect to detail page
                    resp.sendRedirect("view_project?id=" + id);
                } else {
                    // no success
                    resp.sendError(500,"Databaser Error. Money can not be donated");
                }
            } else {
                switch (status) {
                    case checkZero:
                        resp.sendError(400,"Amount must greater than 0");
                        break;
                    case enoughMoney:
                        resp.sendError(400,"User does not have enough enoughMoney");
                        break;
                    case CLOSED:
                        resp.sendError(499,"Project is closed");
                        break;
                    case DONATION:
                        resp.sendError(400,"User has already donated");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            resp.sendError(400,"Can not parse id");
        }
    }

    private boolean inputInDatabase(String user,int id,java.math.BigDecimal amount,boolean anonymous) {
    	Connection con = null;
    	PreparedStatement ps1 = null;
    	PreparedStatement ps2 = null;

        try {
            con = DBUtil.getExternalConnection();
            con.setAutoCommit(false);
            try {
                ps1 = con.prepareStatement("INSERT INTO dbp034.spenden(spender, projekt, spendenbetrag, sichtbarkeit) values (?, ?, ?, ?)");
                ps2 = con.prepareStatement("UPDATE dbp034.konto SET guthaben = (guthaben - ?) WHERE inhaber = ?");
                ps1.setString(1,user);
                ps1.setInt(2,id);
                ps1.setBigDecimal(3,amount);
                if (anonymous) {
                    ps1.setString(4,"privat");
                } else {
                    ps1.setString(4,"oeffentlich");
                }

                ps2.setBigDecimal(1,amount);
                ps2.setString(2,user);

                ps1.executeUpdate();
                ps2.executeUpdate();

                con.commit();
                con.setAutoCommit(true);

                ps2.close();
                ps1.close();
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    con.rollback();
                    con.setAutoCommit(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            con.close();
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    //Check if the input is valid
    private Status varification(String user,int id,BigDecimal amount) {
    	 Connection con = null;
    	 PreparedStatement open = null;
    	 PreparedStatement balance = null;
    	 PreparedStatement donated = null;
    	 ResultSet resO = null;
    	 ResultSet resB = null;
    	 ResultSet resD = null;
    	 
        //Amount is NOT greater than 0
        if (amount.compareTo(new BigDecimal(0)) < 0) 
        	return Status.checkZero;

        try {
        	int number1 = 0;
        	int number2 = 0;
        	int number3 = 0;
            con = DBUtil.getExternalConnection();
            open = con.prepareStatement("SELECT count(*) FROM dbp034.projekt WHERE kennung = ? AND status = 'offen'");
            balance = con.prepareStatement("SELECT count(*) FROM dbp034.konto WHERE inhaber = ? AND guthaben >=  ?");
            donated = con.prepareStatement("SELECT count(*) from dbp034.spenden where projekt = ? AND spender = ?");
            open.setInt(1,id);
            balance.setString(2,user);
            donated.setString(1,user);
            donated.setInt(2,id);
            balance.setBigDecimal(1,amount);
            
            try {
            	resO = open.executeQuery();
                resB = balance.executeQuery();
                resD = donated.executeQuery();

                //Check for open Projects
                if (resO.next()) {
                    number1 = resO.getInt(1);
                    if (number1 != 1) {
                        return Status.CLOSED;
                    }
                }

                //Check for enough money of the user
                if (resB.next()) {
                    number2 = resB.getInt(1);
                    if (number2 != 1) {
                        return Status.enoughMoney;
                    }
                } 

                //Check check for more than once donation
                if (resD.next()) {
                    number3 = resD.getInt(1);
                    if (number3 != 0) {
                        return Status.DONATION;
                    }
                }
                resO.close();
                resB.close();
                resD.close();
            }catch (SQLException e) {
                e.printStackTrace();               
            }
            donated.close();
            balance.close();
            open.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        //when all is ok
        return Status.OPEN;
    }

    //Get title for given id
    private String getTitle(int id) {
        final String getTitleSQL = "SELECT titel FROM dbp034.projekt WHERE kennung = ?";
        Connection con = null;
        PreparedStatement ps = null;

        String title;
        try {
            con = DBUtil.getExternalConnection();
            ps = con.prepareStatement(getTitleSQL);
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    title = rs.getString(1);
                } else {
                    title = null;
                }
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            title = null;
            e.printStackTrace();
        }
        return title;
    }


}
