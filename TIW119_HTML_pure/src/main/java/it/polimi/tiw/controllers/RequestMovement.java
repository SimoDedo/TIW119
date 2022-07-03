package it.polimi.tiw.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

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
	private TemplateEngine templateEngine;   

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RequestMovement() {
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
		User outUser = (User) session.getAttribute("user");

		String motive = StringEscapeUtils.escapeJava(request.getParameter("motive"));

		if(motive == null || motive.isEmpty()){ //Checks that POST parameters aren't empty
			toMovementFailure(request, response, ServletError.MISSING_DATA);
			return;
		}

		int inUserID;
		int inAccountID;
		Double amount = null;
		int outAccountID;
		try{ 
			inUserID = Integer.valueOf(request.getParameter("inuserid"));
			inAccountID = Integer.valueOf(request.getParameter("inaccountid"));
			amount = Double.valueOf(request.getParameter("amount"));
			outAccountID = Integer.valueOf(request.getParameter("outaccountid"));
		}catch(NumberFormatException | NullPointerException e){ //Checks that the given balance is actually a number
			if(e instanceof NullPointerException)
				toMovementFailure(request, response, ServletError.MISSING_DATA);
			if(e instanceof NumberFormatException)
				toMovementFailure(request, response, ServletError.NUMBER_FORMAT);
			return;
		}
		if( amount < 0){ //Checks that the given amount is positive
			toMovementFailure(request, response, ServletError.NEGATIVE_AMOUNT);
			return;
		}
		if(inAccountID == outAccountID){
			toMovementFailure(request, response, ServletError.ACC_SAME);
			return;
		}

		UserDAO userDAO = new UserDAO(connection);
		User inUser = null;
		try{
			inUser = userDAO.getUserByID(inUserID);
		}catch(SQLException e){
			toMovementFailure(request, response, ServletError.IE_RETRIEVE_USER);
			return;
		}
		if(inUser == null ){ //Checks that user exists
			toMovementFailure(request, response, ServletError.USER_ID_NOT_FOUND);
			return;
		}

		AccountDAO accountDAO = new AccountDAO(connection);
		Account inAccount = null;
		Account outAccount = null;		
		try {
			inAccount = accountDAO.getAccountByID(inAccountID);
			outAccount = accountDAO.getAccountByID(outAccountID);
		} catch (SQLException e) {
			toMovementFailure(request, response, ServletError.IE_ACC_NOT_FOUND);
			return;
		}
		if(inAccount == null || outAccount == null){ //Checks that accounts exists
			toMovementFailure(request, response, ServletError.ACC_NOT_FOUND);
			return;
		}
		if(inAccount.getOwnerID() != inUser.getID() || outAccount.getOwnerID() != outUser.getID() ){ //Checks that users own their accounts
			toMovementFailure(request, response, ServletError.ACC_NOT_OWNED_BY_USER);
			return;
		}
		if(outAccount.getBalance().doubleValue() < amount){
			toMovementFailure(request, response, ServletError.ACC_INSUFFICIENT_BALANCE);
			return;
		}

		MovementDAO movementDAO = new MovementDAO(connection);
		Date date = new Date(System.currentTimeMillis());
		try { //Creates the movement
			movementDAO.requestMovement(date, BigDecimal.valueOf(amount), motive, inAccount, outAccount);
		} catch (SQLException e1) {
			toMovementFailure(request, response, ServletError.IE_CREATE_MOVEMENT);
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


	private void toLoginWithError(HttpServletRequest request, HttpServletResponse response, ServletError signupErrorMsg) throws IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("generalError", signupErrorMsg.toString());
		String path = "/WEB-INF/Login.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	private void toMovementFailure(HttpServletRequest request, HttpServletResponse response, ServletError errorMsg) throws IOException{
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("movementError", errorMsg.toString());
		String path = "/WEB-INF/MovementFailure.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
