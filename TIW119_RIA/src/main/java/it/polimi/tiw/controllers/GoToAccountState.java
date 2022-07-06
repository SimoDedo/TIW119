package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import it.polimi.tiw.beans.Movement;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
import it.polimi.tiw.dao.MovementDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class GetAccountState
 */
@WebServlet("/AccountState")
public class GoToAccountState extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;   

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToAccountState() {
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
		User user = (User) session.getAttribute("user");

		String accountidString = request.getParameter("accountid");
		Integer accountid = null;
		try {
			accountid = Integer.valueOf(accountidString);	
		} catch (NumberFormatException e) { //Checks that the accountid parameter is actually a number
			toHomeWithError(request, response, ServletError.ACC_ID_FORMAT);
			return;
		}
		
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account;
		try {
			account = accountDAO.getAccountByID(accountid);	
		} catch (SQLException e) {
			toHomeWithError(request, response, ServletError.IE_RETRIEVE_ACC);
			return;
		}

		if(account == null){ //Checks that the account actually exists
			toHomeWithError(request, response, ServletError.ACC_NOT_FOUND);
			return;
		}
		if(account.getOwnerID() != user.getID()){ //Check that the logged user actually owns the account
			toHomeWithError(request, response, ServletError.ACC_NOT_OWNED);
			return;
		}

		List<Movement> inMovs = null;
		List<Movement> outMovs = null;

		MovementDAO movementDAO = new MovementDAO(connection);

		try {
			inMovs = movementDAO.getIncomingMovementsByAccount(accountid);	
			outMovs = movementDAO.getOutgoingMovementsByAccount(accountid);
		} catch (SQLException e) {
			toHomeWithError(request, response, ServletError.IE_RETRIEVE_MOV);
			return;
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("account", account);
		ctx.setVariable("inmovements", inMovs);
		ctx.setVariable("outmovements", outMovs);
		ctx.setVariable("backPath", "/Home");
		String path = "/AccountState.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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
