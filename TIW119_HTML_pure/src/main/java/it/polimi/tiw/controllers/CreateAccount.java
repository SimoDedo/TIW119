package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class CreateAccount
 */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateAccount() {
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
		User user = (User) session.getAttribute("user");


		String accountName = request.getParameter("name");
		
		if(accountName == null || accountName.isEmpty()){ //Checks that POST parameters aren't empty
			toHomeWithError(request, response, ServletError.MISSING_FORM_DATA);
			return;
		}

		Double balance = null;
		try{ 
			balance = Double.parseDouble(request.getParameter("balance"));
		}catch(NumberFormatException | NullPointerException e){ //Checks that the given balance is actually a number
			toHomeWithError(request, response, ServletError.NUMBER_FORMAT);
			return;
		}
		if( balance < 0){ //Checks that the given balance is positive
			toHomeWithError(request, response, ServletError.NEGATIVE_BALANCE);
			return;
		}
		
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account = null;		
		try {
			account = accountDAO.getAccountByName(accountName);
		} catch (SQLException e) {
			toHomeWithError(request, response, ServletError.IE_CHECK_CREDENTIALS);
			return;
		}

		if(account != null){ //Checks that no other account exists with the same name
			toHomeWithError(request, response, ServletError.ACC_NAME_ALREADY_EXISTS);
			return;
		}

		try { //Creates the bank account
			accountDAO.createAccount(accountName, BigDecimal.valueOf(balance), user.getID());
		} catch (SQLException e1) {
			toHomeWithError(request, response, ServletError.IE_CREATE_ACC);
			return;
		}

		String path = getServletContext().getContextPath() + "/Home";
		response.sendRedirect(path);
		
	}

	private void toHomeWithError(HttpServletRequest request, HttpServletResponse response, ServletError accountErrorMsg) throws IOException{
		String path = getServletContext().getContextPath() + "/Home?accErrorid=" + accountErrorMsg.ordinal();
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
