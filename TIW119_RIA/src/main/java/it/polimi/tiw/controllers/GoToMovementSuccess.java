package it.polimi.tiw.controllers;

import java.io.IOException;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.Movement;
import it.polimi.tiw.dao.AccountDAO;

import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class GoToMovementSuccess
 */
@Deprecated
@WebServlet("/MovementSuccess")
public class GoToMovementSuccess extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToMovementSuccess() {
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

		Movement movement = (Movement) session.getAttribute("movementMade");
		if(movement == null){ //Checks that there is a movement that requires confirmation to be shown
			toHomeWithError(request, response, ServletError.NO_MOVEMENT_MADE);
			return;
		}
		//Removes movement from session to avoid being able to show confirmation more than once
		session.removeAttribute("movementMade"); 

		AccountDAO accountDAO = new AccountDAO(connection);
		Account inAccount = null;
		Account outAccount = null;
		try { //Retrieves the accounts to put in the template
			inAccount = accountDAO.getAccountByID(movement.getInAccountID());
			outAccount = accountDAO.getAccountByID(movement.getOutAccountID());
		} catch (SQLException e) {
			toHomeWithError(request, response, ServletError.IE_RETRIEVE_ACC);
			return;
		}

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("movement", movement);
		ctx.setVariable("outAccount", outAccount);
		ctx.setVariable("inAccount", inAccount);
		ctx.setVariable("backPath", "/AccountState?accountid=" + outAccount.getID());
		String path = "/MovementSuccess.html";
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
