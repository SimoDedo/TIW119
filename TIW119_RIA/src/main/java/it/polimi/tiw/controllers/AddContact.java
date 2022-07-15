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
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class RequestMovement
 */
@WebServlet("/AddContact")
@MultipartConfig
public class AddContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddContact() {
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
		HttpSession session = request.getSession();
		User ownerUser = (User) session.getAttribute("user");

		String contactUserIDString = request.getParameter("accountid");
		if(contactUserIDString == null || contactUserIDString.isEmpty()){ //Checks that the contact user id parameter is not null or empty
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.MISSING_FORM_DATA.toString());
			return;
		}

		int contactUserID;
		try{ //Checks that post parameters (contact user id) is actually a number
			contactUserID = Integer.valueOf(contactUserIDString);
		}catch(NumberFormatException e){ 
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.NUMBER_FORMAT.toString());
			return;
		}

        UserDAO userDAO = new UserDAO(connection);
		User contactUser = null;
		try{
			contactUser = userDAO.getUserByID(contactUserID);
		}catch(SQLException e){
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_USER.toString());
			return;
		}
		if(contactUser == null ){ //Checks that user exists
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.USER_ID_NOT_FOUND.toString());
			return;
		}

        boolean isPresent = false;
        try { //Checks contact is not already saved
            isPresent = userDAO.hasContact(ownerUser.getID(), contactUser.getID());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_CONTACT.toString());
            return;
        }
        if(isPresent){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(ServletError.CONTACT_ALREADY_PRESENT.toString());
			return;
        }

        try {//Adds contact
            userDAO.addContact(ownerUser.getID(), contactUser.getID());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(ServletError.IE_RETRIEVE_USER.toString());
            return;
        }
        
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(contactUser.getID());

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}