package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User outUser = (User) session.getAttribute("user");

		
		String motive = request.getParameter("motive");
		
		//Checks that POST parameters aren't empty
		if(motive == null || motive.isEmpty()){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.MISSING_FORM_DATA.toString());
			return;
		}
		
		int outAccountID;
		int inUserID;
		int inAccountID;
		Double amount = null;
		try{ 
			outAccountID = Integer.valueOf(request.getParameter("outaccountid"));
			inUserID = Integer.valueOf(request.getParameter("inuserid"));
			inAccountID = Integer.valueOf(request.getParameter("inaccountid"));
			amount = Double.valueOf(request.getParameter("amount"));
		}catch(NumberFormatException | NullPointerException e){ //Checks that the given numbers are actually numbers
			if(e instanceof NullPointerException){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(ServletError.MISSING_FORM_DATA.toString());
			}
			if(e instanceof NumberFormatException){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(ServletError.NUMBER_FORMAT.toString());
			}
			return;
		}
		if( amount <= 0){ //Checks that the given amount is positive
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.NEGATIVE_OR_ZERO_AMOUNT.toString());
			return;
		}
		if(inAccountID == outAccountID){ //Checks that movement isn't being made to itself
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.ACC_SAME.toString());
			return;
		}

		UserDAO userDAO = new UserDAO(connection);
		User inUser = null;
		try{
			inUser = userDAO.getUserByID(inUserID);
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_USER.toString());
			return;
		}
		if(inUser == null ){ //Checks that user exists
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.USER_ID_NOT_FOUND.toString());
			return;
		}

		AccountDAO accountDAO = new AccountDAO(connection);
		Account inAccount = null;
		Account outAccount = null;		
		try {
			inAccount = accountDAO.getAccountByID(inAccountID);
			outAccount = accountDAO.getAccountByID(outAccountID);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_ACC_NOT_FOUND.toString());
			return;
		}
		if(inAccount == null || outAccount == null){ //Checks that accounts exists
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.ACC_NOT_FOUND.toString());
			return;
		}
		if(inAccount.getOwnerID() != inUser.getID() || outAccount.getOwnerID() != outUser.getID() ){ //Checks that users own their accounts
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println(ServletError.ACC_NOT_OWNED_BY_USER.toString());
			return;
		}
		if(outAccount.getBalance().doubleValue() < amount){ //Checks that balance is sufficient to make movement
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.ACC_INSUFFICIENT_BALANCE.toString());
			return;
		}

		MovementDAO movementDAO = new MovementDAO(connection);
		Date date = new Date(System.currentTimeMillis());
		try { //Creates the movement
			movementDAO.requestMovement(date, BigDecimal.valueOf(amount), motive, inAccount, outAccount);
		} catch (SQLException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_CREATE_MOVEMENT.toString());
			return;
		}

		Movement movement = new Movement();
		movement.setDate(date);
		movement.setAmount(BigDecimal.valueOf(amount).setScale(2));
		movement.setMotive(motive);
		movement.setInAccountID(inAccountID);
		movement.setOutAccountID(outAccountID);

		HashMap<String, Object> movementMap = new HashMap<>();
		movementMap.put("movement", movement);
		movementMap.put("outAccount", outAccount);
		movementMap.put("inAccount", inAccount);

		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
		String json = gson.toJson(movementMap);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
