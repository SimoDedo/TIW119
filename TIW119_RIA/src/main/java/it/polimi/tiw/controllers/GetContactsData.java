package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.UserContacts;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class GetAccountState
 */
@WebServlet("/GetContactsData")
@MultipartConfig
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
		UserContacts contactIDs = new UserContacts();
		try { //Retrieves the contact ids
			contactIDs = userDAO.getContacts(user.getID());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_CONTACT.toString());
			return;
		}
		
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(contactIDs.getContacts());

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