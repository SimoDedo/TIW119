package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
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
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class GetAccountState
 */
@WebServlet("/GetMovementsData")
@MultipartConfig
public class GetMovementsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetMovementsData() {
        super();
    }

	public void init() throws UnavailableException{
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		String accountidString = request.getParameter("accountid");
		if(accountidString == null || accountidString.isEmpty()){ //Checks that the accountid parameter is not null or empty
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.MISSING_REQUEST_DATA.toString());
			return;
		}

		Integer accountid = null;
		try {
			accountid = Integer.valueOf(accountidString);	
		} catch (NumberFormatException e) { //Checks that the accountid parameter is not null and actually a number
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.ACC_ID_FORMAT.toString());
			return;
		}
		
		AccountDAO accountDAO = new AccountDAO(connection);
		Account account;
		try {
			account = accountDAO.getAccountByID(accountid);	
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_ACC.toString());
			return;
		}

		if(account == null){ //Checks that the account actually exists
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.ACC_NOT_FOUND.toString());
			return;
		}
		if(account.getOwnerID() != user.getID()){ //Check that the logged user actually owns the account
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println(ServletError.ACC_NOT_OWNED.toString());
			return;
		}

		List<Movement> inMovs = null;
		List<Movement> outMovs = null;

		MovementDAO movementDAO = new MovementDAO(connection);

		try {
			inMovs = movementDAO.getIncomingMovementsByAccount(accountid);	
			outMovs = movementDAO.getOutgoingMovementsByAccount(accountid);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_MOV.toString());
			return;
		}
				
		HashMap<String, List<Movement>> map = new HashMap<>();
		map.put("inMovs", inMovs);
		map.put("outMovs", outMovs);
		
		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
		String json = gson.toJson(map);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
