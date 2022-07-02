package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class CheckSignup
 */
@WebServlet("/CheckSignup")
public class CheckSignup extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckSignup() {
        super();
        // TODO Auto-generated constructor stub
    }
	
	public void init() throws UnavailableException{
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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
		String username = null;
		String email = null;
		String password = null;
		String repeatPassword = null;
		String name = null;
		String surname = null;

		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		repeatPassword = StringEscapeUtils.escapeJava(request.getParameter("repeatPassword"));
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
		
		if(username == null || username.isEmpty() || email == null || email.isEmpty()
		|| password == null || password.isEmpty() || repeatPassword == null || repeatPassword.isEmpty()
		|| name == null || name.isEmpty() || surname == null || surname.isEmpty()){
			returnWithError(request, response, "Missing or empty credentials!");
			return;
		}
		if(! password.equals(repeatPassword)){
			returnWithError(request, response, "Passwords do not match!");
			return;
		}
		if(! isEmailValid(email)){
			returnWithError(request, response, "Email is not a valid address!");
			return;
		}
		
		
		UserDAO userDAO = new UserDAO(connection);
		User usernameUser = null;
		User emailUser = null;
		try {
			usernameUser = userDAO.getUserByUsername(username);
			emailUser = userDAO.getUserByEmail(email);
		} catch (SQLException e) {
			returnWithError(request, response, "Internal error: can't check credentials");
			return;
		}

		if(usernameUser != null){
			returnWithError(request, response, "A user with the given username already exists");
			return;
		}
		else if(emailUser != null){
			returnWithError(request, response, "A user with the given email already exists");
			return;
		}
		else{

			try {
				userDAO.registerUser(name, surname, username, email, password);
			} catch (SQLException e1) {
				returnWithError(request, response, "Internal error: can't register user");
				return;
			}

			User toLog = null;
			try {
				toLog = userDAO.getUserByUsername(username);
			} catch (SQLException e) {
				returnWithError(request, response, "Internal error: can't find the registered user");
				return;
			}

			request.getSession().setAttribute("user", toLog);
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}

	private void returnWithError(HttpServletRequest request, HttpServletResponse response, String signupErrorMsg) throws IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("signupError", signupErrorMsg);
		String path = "/WEB-INF/Login.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	private boolean isEmailValid(String email){
		Pattern pattern = Pattern.compile("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)");
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
