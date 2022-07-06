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


import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.ServletError;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
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

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if(username == null || username.isEmpty() || password == null || password.isEmpty()){ //Checks that POST parameters are not empty
			toLoginWithError(request, response, ServletError.MISSING_CREDENTIALS);
			return;
		}
		
		UserDAO userDAO = new UserDAO(connection);
		User user = null;
		try {
			user = userDAO.checkCredentials(username, password); 
		} catch (SQLException e) {
			toLoginWithError(request, response, ServletError.IE_CHECK_CREDENTIALS);
			return;
		}

	
		if(user == null){ //Checks that credentials correspond to a user
			toLoginWithError(request, response, ServletError.USER_NOT_FOUND);
			return;
		}
		else{
			request.getSession().setAttribute("user", user);
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}

	private void toLoginWithError(HttpServletRequest request, HttpServletResponse response, ServletError loginErrorMsg) throws IOException{
		String path = getServletContext().getContextPath() + "/?loginErrorid=" + loginErrorMsg.ordinal();
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
