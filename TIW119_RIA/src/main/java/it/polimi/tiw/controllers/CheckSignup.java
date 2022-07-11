package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class CheckSignup
 */
@WebServlet("/CheckSignup")
@MultipartConfig
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
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
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.MISSING_CREDENTIALS.toString());
			return;
		}
		if(! password.equals(repeatPassword)){ //Checks that passwords match
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.PASSWORD_MISMATCH.toString());
			return;
		}
		if(! isEmailValid(email)){ //Checks that email is well formed
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.EMAIL_FORMAT.toString());
			return;
		}
		
		
		UserDAO userDAO = new UserDAO(connection);
		User usernameUser = null;
		User emailUser = null;
		try {
			usernameUser = userDAO.getUserByUsername(username);
			emailUser = userDAO.getUserByEmail(email);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_CHECK_CREDENTIALS.toString());
			return;
		}

		if(usernameUser != null){ //Checks that no other user with the same username exists
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println(ServletError.USERNAME_ALREADY_EXISTS.toString());
			return;
		}
		if(emailUser != null){ //Checks that no other user with the same email exists
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println(ServletError.EMAIL_ALREADY_EXISTS.toString());
			return;
		}

		try { //Registers the user in the database
			userDAO.registerUser(name, surname, username, email, password);
		} catch (SQLException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_REGISTRATION.toString());
			return;
		}

		User toLog = null;
		try { //Logs the newly registered user
			toLog = userDAO.getUserByUsername(username);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_USER_NOT_FOUND.toString());
			return;
		}

		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(username);
		
		request.getSession().setAttribute("user", toLog);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
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
