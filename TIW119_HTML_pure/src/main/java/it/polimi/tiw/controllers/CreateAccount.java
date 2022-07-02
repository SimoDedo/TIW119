package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
	private TemplateEngine templateEngine;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateAccount() {
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
		HttpSession session = request.getSession();
		if(session.isNew() || session.getAttribute("user") == null ){ //Checks that user has logged in (and is thus saved in the session)
			toLoginWithError(request, response, ServletError.NOT_LOGGED_IN);
			return;
		}
		User user = (User) session.getAttribute("user");

		String accountName = null;
		String balString = null;

		accountName = StringEscapeUtils.escapeJava(request.getParameter("name"));
		balString = StringEscapeUtils.escapeJava(request.getParameter("balance"));
		
		if(accountName == null || accountName.isEmpty() || balString == null || balString.isEmpty()){ //Checks that POST parameters aren't empty
			toHomeWithError(request, response, ServletError.MISSING_DATA);
			return;
		}

		Double balance = null;
		try{ 
			balance = (Double.valueOf(balString));
		}catch(NumberFormatException e){ //Checks that the given balance is actually a number
			toHomeWithError(request, response, ServletError.NUMBER_FORMAT);
			return;
		}
		if( balance < 0){ //Checks that the given balance is positive
			toHomeWithError(request, response, ServletError.NEGATIVE_NUMBER);
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

	private void toLoginWithError(HttpServletRequest request, HttpServletResponse response, ServletError signupErrorMsg) throws IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("signupError", signupErrorMsg.toString());
		String path = "/WEB-INF/Login.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	private void toHomeWithError(HttpServletRequest request, HttpServletResponse response, ServletError accountErrorMsg) throws IOException{
		String path = getServletContext().getContextPath() + "/Home?errorid=" + accountErrorMsg.ordinal();
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
