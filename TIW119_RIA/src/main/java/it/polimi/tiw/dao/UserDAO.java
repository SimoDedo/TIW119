package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.UserContacts;

public class UserDAO {
    private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT  id, username, email, name, surname FROM tiw119.user  WHERE BINARY username = ? AND BINARY password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setID(result.getInt("id"));
                    user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}

    public User getUserByID(int userID) throws SQLException{
        String query = "SELECT  id, username, email, name, surname FROM tiw119.user  WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setID(result.getInt("id"));
                    user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
        }
    }

    public User getUserByUsername(String username) throws SQLException{
        String query = "SELECT  id, username, email, name, surname FROM tiw119.user  WHERE BINARY username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setID(result.getInt("id"));
                    user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
        }
    }

    public User getUserByEmail(String email) throws SQLException{
        String query = "SELECT  id, username, email, name, surname FROM tiw119.user  WHERE email = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, email);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setID(result.getInt("id"));
                    user.setEmail(result.getString("email"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
        }
    }

    public void registerUser(String name,String  surname,String  username,String  email,String  password) throws SQLException{
        String query = "INSERT into tiw119.user (username, email, password, name, surname) VALUES (?, ?, ?, ?, ?)";
        con.setAutoCommit(false);
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, email);
			pstatement.setString(3, password);
			pstatement.setString(4, name);
			pstatement.setString(5, surname);
            pstatement.executeUpdate();
            con.commit();
        }
        catch(SQLException e){
            con.rollback();
            throw e;
        }
		finally {
			con.setAutoCommit(true);
		} 
    }

	public boolean hasContact(int ownerUserid, int contactUserid) throws SQLException{
		String query = "SELECT  * FROM tiw119.contact  WHERE ownerUserid = ? AND contactUserid = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, ownerUserid);
			pstatement.setInt(2, contactUserid);
			try (ResultSet result = pstatement.executeQuery();) {
				return result.isBeforeFirst();
			}
        }
	}

	public UserContacts getContacts(int ownerUserid) throws SQLException{
		String query = "SELECT con.contactUserid, acc.id "
						+ "FROM tiw119.contact AS con JOIN tiw119.account AS acc ON con.contactUserid = acc.userid "  
						+ "WHERE con.ownerUserid = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, ownerUserid);
			try (ResultSet result = pstatement.executeQuery();) {
				UserContacts userContacts = new UserContacts();
				userContacts.setContactsOwner(ownerUserid);
				while(result.next()){
					userContacts.addContact(result.getInt("contactUserid"), result.getInt("id"));
				}
				return userContacts;
			}
		}
	}

	public void addContact(int ownerUserid, int contactUserid) throws SQLException{
        String query = "INSERT into tiw119.contact (ownerUserid, contactUserid) VALUES (?, ?)";
        con.setAutoCommit(false);
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, ownerUserid);
			pstatement.setInt(2, contactUserid);
            pstatement.executeUpdate();
            con.commit();
        }
        catch(SQLException e){
            con.rollback();
            throw e;
        }
		finally {
			con.setAutoCommit(true);
		} 
    }
}
