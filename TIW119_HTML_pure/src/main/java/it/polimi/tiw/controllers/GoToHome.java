package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
 * Servlet implementation class GoToHome
 */
@WebServlet("/Home")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHome() {
        super();
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
		HttpSession session = request.getSession();
		
		if(session.isNew() || session.getAttribute("user") == null ){ //Checks that user has logged in (and is thus saved in the session)
			toLoginWithError(request, response, ServletError.NOT_LOGGED_IN);
			return;
		}
		User user = (User) session.getAttribute("user");

		List<Account> accounts = new ArrayList<>();
		AccountDAO accountDAO = new AccountDAO(connection);
		try { //Retrieves the accounts to put in the template
			accounts = accountDAO.getAccountsByUser(user.getID());
		} catch (SQLException e) {
			session.invalidate();
			toLoginWithError(request, response, ServletError.IE_RETRIEVE_ACC);
			return;
		}

		Integer errorid = getErrorID(request); 

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("accounts", accounts);
		if(errorid != null) //Checks if an error is to be shown in the template
			ctx.setVariable("accountError", ServletError.values()[errorid].toString());
		String path = "/WEB-INF/Home.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void toLoginWithError(HttpServletRequest request, HttpServletResponse response, ServletError generalErrorMsg) throws IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("generalError", generalErrorMsg.toString());
		String path = "/WEB-INF/Login.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * Returns the errorid set in the request. Returns null if parameter "errorid" is empty, negative or outside the errorid range.
	 * @param request the HTTP request.
	 * @return the id of the error to display, null if invalid
	 */
	private Integer getErrorID(HttpServletRequest request){
		String err = request.getParameter("errorid");
		Integer id = null;
		try{
			id = Integer.parseInt(err);
		}
		catch(NumberFormatException e){ //Checks that errorid parameter is actually an integer
			return null;
		}
		if(id<0 || id >= ServletError.values().length) //Checks that errorid parameter is valid
			return null;
		else
			return id;
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
