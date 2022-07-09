package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class GetAccountState
 */
@WebServlet("/GetContactsData")
public class GetContactsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetContactsData() {
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

		UserDAO userDAO = new UserDAO(connection);
		List<Integer> contactIDs = new ArrayList<>();
		try {
			contactIDs = userDAO.getContacts(user.getID());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_CONTACT.toString());
			return;
		}

		//Map that contains contactUserid as key, and a list of their accountid as value to be sent to the client
		HashMap<Integer, List<Integer>> contactsMap = new HashMap<>(); 
		
		AccountDAO accountDAO = new AccountDAO(connection);
		List<Account> accountids = new ArrayList<>();

		for (Integer contactUserid : contactIDs) {
			try {
				accountids = accountDAO.getAccountsByUser(contactUserid);
				contactsMap.put(contactUserid, accountids.stream().map(a -> a.getID()).toList());
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println(ServletError.IE_RETRIEVE_ACC.toString());
				return;
			}
		}
		
		Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create();
		String json = gson.toJson(contactsMap);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(json);
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