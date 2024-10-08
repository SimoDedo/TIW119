package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.Movement;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
import it.polimi.tiw.dao.MovementDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class RequestMovement
 */
@WebServlet("/RequestMovement")
public class RequestMovement extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RequestMovement() {
        super();
    }

	public void init() throws UnavailableException{
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User outUser = (User) session.getAttribute("user");

		//Checks that the source account ID is valid. If it isn't, redirects to home. If it is, all next errors can redirect to movement failure.
		String outAccountIDString = request.getParameter("outaccountid");
		if(outAccountIDString == null || outAccountIDString.isEmpty()){ //Checks that the accountid parameter is not null or empty
			toHomeWithError(request, response, ServletError.MISSING_FORM_DATA);
			return;
		}

		int outAccountID;
		try{ //Checks that the accountid parameter is actually a number
			outAccountID = Integer.valueOf(outAccountIDString);
		}catch(NumberFormatException e){ 
			toHomeWithError(request, response, ServletError.NUMBER_FORMAT);
			return;
		}

		//Checks that POST parameters aren't empty
		String motive = request.getParameter("motive");
		String inUserIDString = request.getParameter("inuserid");
		String inAccountIDString = request.getParameter("inaccountid");
		String amountString = request.getParameter("amount");

		if(motive == null || motive.isEmpty() || inUserIDString == null || inUserIDString.isEmpty()
			|| inAccountIDString == null || inAccountIDString.isEmpty() || amountString == null || amountString.isEmpty()){ 
			toMovementFailure(request, response, ServletError.MISSING_FORM_DATA, outAccountID);
			return;
		}

		int inUserID;
		int inAccountID;
		Double amount = null;
		try{ 
			inUserID = Integer.valueOf(inUserIDString);
			inAccountID = Integer.valueOf(inAccountIDString);
			amount = Double.valueOf(amountString);
		}catch(NumberFormatException e){ //Checks that the given numbers are actually a number
			toMovementFailure(request, response, ServletError.NUMBER_FORMAT, outAccountID);
			return;
		}
		if( amount <= 0){ //Checks that the given amount is positive
			toMovementFailure(request, response, ServletError.NEGATIVE_OR_ZERO_AMOUNT, outAccountID);
			return;
		}
		if(inAccountID == outAccountID){ //Checks that user isn't billing the same account
			toMovementFailure(request, response, ServletError.ACC_SAME, outAccountID);
			return;
		}

		UserDAO userDAO = new UserDAO(connection);
		User inUser = null;
		try{
			inUser = userDAO.getUserByID(inUserID);
		}catch(SQLException e){
			toMovementFailure(request, response, ServletError.IE_RETRIEVE_USER, outAccountID);
			return;
		}
		if(inUser == null ){ //Checks that user exists
			toMovementFailure(request, response, ServletError.USER_ID_NOT_FOUND, outAccountID);
			return;
		}

		AccountDAO accountDAO = new AccountDAO(connection);
		Account inAccount = null;
		Account outAccount = null;		
		try {
			inAccount = accountDAO.getAccountByID(inAccountID);
			outAccount = accountDAO.getAccountByID(outAccountID);
		} catch (SQLException e) {
			toMovementFailure(request, response, ServletError.IE_ACC_NOT_FOUND, outAccountID);
			return;
		}
		if(inAccount == null || outAccount == null){ //Checks that accounts exists
			toMovementFailure(request, response, ServletError.ACC_NOT_FOUND, outAccountID);
			return;
		}
		if(inAccount.getOwnerID() != inUser.getID() || outAccount.getOwnerID() != outUser.getID() ){ //Checks that users own their accounts
			toMovementFailure(request, response, ServletError.ACC_NOT_OWNED_BY_USER, outAccountID);
			return;
		}
		if(outAccount.getBalance().doubleValue() < amount){ //Checks that balance is sufficient to make movement
			toMovementFailure(request, response, ServletError.ACC_INSUFFICIENT_BALANCE, outAccountID);
			return;
		}

		MovementDAO movementDAO = new MovementDAO(connection);
		Date date = new Date(System.currentTimeMillis());
		try { //Creates the movement
			movementDAO.requestMovement(date, BigDecimal.valueOf(amount), motive, inAccount, outAccount);
		} catch (SQLException e1) {
			toMovementFailure(request, response, ServletError.IE_CREATE_MOVEMENT, outAccountID);
			return;
		}

		Movement movement = new Movement();
		movement.setDate(date);
		movement.setAmount(BigDecimal.valueOf(amount).setScale(2));
		movement.setMotive(motive);
		movement.setInAccountID(inAccountID);
		movement.setOutAccountID(outAccountID);

		//Adds movement just made to session so that user can be redirected to a confirmation page and see the data
		session.setAttribute("movementMade", movement);		

		String path = getServletContext().getContextPath() + "/MovementSuccess";
		response.sendRedirect(path);
	}

	private void toHomeWithError(HttpServletRequest request, HttpServletResponse response, ServletError accountErrorMsg) throws IOException{
		String path = getServletContext().getContextPath() + "/Home?errorid=" + accountErrorMsg.ordinal();
		response.sendRedirect(path);
	}

	private void toMovementFailure(HttpServletRequest request, HttpServletResponse response, ServletError errorMsg, int outAccountID) throws IOException{
		String path = getServletContext().getContextPath() + "/MovementFailure?accountid=" + outAccountID +"&errorid=" + errorMsg.ordinal();
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
