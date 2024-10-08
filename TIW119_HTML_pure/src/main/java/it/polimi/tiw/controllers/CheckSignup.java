package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class CheckSignup
 */
@WebServlet("/CheckSignup")
public class CheckSignup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckSignup() {
        super();
    }
	
	public void init() throws UnavailableException{
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String repeatPassword = request.getParameter("repeatPassword");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		
		if(username == null || username.isEmpty() || email == null || email.isEmpty()
		|| password == null || password.isEmpty() || repeatPassword == null || repeatPassword.isEmpty()
		|| name == null || name.isEmpty() || surname == null || surname.isEmpty()){ //Checks that POST parameters are not empty
			toLoginWithError(request, response, ServletError.MISSING_CREDENTIALS);
			return;
		}
		if(! password.equals(repeatPassword)){ //Checks that passwords match
			toLoginWithError(request, response, ServletError.PASSWORD_MISMATCH);
			return;
		}
		if(! isEmailValid(email)){ //Checks that email is well formed
			toLoginWithError(request, response, ServletError.EMAIL_FORMAT);
			return;
		}
		
		
		UserDAO userDAO = new UserDAO(connection);
		User usernameUser = null;
		User emailUser = null;
		try {
			usernameUser = userDAO.getUserByUsername(username);
			emailUser = userDAO.getUserByEmail(email);
		} catch (SQLException e) {
			toLoginWithError(request, response, ServletError.IE_CHECK_CREDENTIALS);
			return;
		}

		if(usernameUser != null){ //Checks that no other user with the same username exists
			toLoginWithError(request, response, ServletError.USERNAME_ALREADY_EXISTS);
			return;
		}
		if(emailUser != null){ //Checks that no other user with the same email exists
			toLoginWithError(request, response, ServletError.EMAIL_ALREADY_EXISTS);
			return;
		}

		try { //Registers the user in the database
			userDAO.registerUser(name, surname, username, email, password);
		} catch (SQLException e1) {
			toLoginWithError(request, response, ServletError.IE_REGISTRATION);
			return;
		}

		User toLog = null;
		try { //Logs the newly registered user
			toLog = userDAO.getUserByUsername(username);
		} catch (SQLException e) {
			toLoginWithError(request, response, ServletError.IE_USER_NOT_FOUND);
			return;
		}

		request.getSession().setAttribute("user", toLog);
		String path = getServletContext().getContextPath() + "/Home";
		response.sendRedirect(path);
	
	}

	private void toLoginWithError(HttpServletRequest request, HttpServletResponse response, ServletError signupErrorMsg) throws IOException{
		String path = getServletContext().getContextPath() + "/?signupErrorid=" + signupErrorMsg.ordinal();
		response.sendRedirect(path);
	}

	private boolean isEmailValid(String email){
		Pattern pattern = Pattern.compile("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
		Matcher mat = pattern.matcher(email);
		return mat.matches();
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
