package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class RequestMovement
 */
@WebServlet("/AddContact")
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

		int contactUserID;
		try{ //Checks that post parameters (contact user id) is present and well formatted
			contactUserID = Integer.valueOf(request.getParameter("contactUserid"));
		}catch(NumberFormatException | NullPointerException e){ 
			if(e instanceof NullPointerException){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(ServletError.MISSING_DATA.toString());
			}
			if(e instanceof NumberFormatException){
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(ServletError.NUMBER_FORMAT.toString());
			}
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

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(contactUser.getID());
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}