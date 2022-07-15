package it.polimi.tiw.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Account;

public class AccountDAO {
        private Connection con;
    
        public AccountDAO(Connection connection) {
            this.con = connection;
        }

        public List<Account> getAccountsByUser(int id) throws SQLException {
            List<Account> accounts = new ArrayList<>();
            String query = "SELECT  id, name, balance, userid FROM tiw119.account  WHERE userid = ?";
            try (PreparedStatement pstatement = con.prepareStatement(query);) {
                pstatement.setInt(1, id);
                try (ResultSet result = pstatement.executeQuery();) {
                        while(result.next()){
                            Account account = new Account();
                            account.setID(result.getInt("id"));
                            account.setName(result.getString("name"));
                            account.setBalance(result.getBigDecimal("balance"));
                            account.setOwnerID(result.getInt("userid"));

                            accounts.add(account);
                        }
                    }
                }
            return accounts;
        }

        public Account getAccountByID(int id) throws SQLException {
            String query = "SELECT  id, name, balance, userid FROM tiw119.account  WHERE id = ?";
            try (PreparedStatement pstatement = con.prepareStatement(query);) {
                pstatement.setInt(1, id);
                try (ResultSet result = pstatement.executeQuery();) {
                    if (!result.isBeforeFirst())
                        return null;
                    else {
                        result.next();
                        Account account = new Account();
                        account.setID(result.getInt("id"));
                        account.setName(result.getString("name"));
                        account.setBalance(result.getBigDecimal("balance"));
                        account.setOwnerID(result.getInt("userid"));
                        return account;
                    }
                }
            }
        }

        public Account getAccountByName(String name) throws SQLException {
            String query = "SELECT  id, name, balance, userid FROM tiw119.account  WHERE BINARY name = ?";
            try (PreparedStatement pstatement = con.prepareStatement(query);) {
                pstatement.setString(1, name);
                try (ResultSet result = pstatement.executeQuery();) {
                    if (!result.isBeforeFirst())
                        return null;
                    else {
                        result.next();
                        Account account = new Account();
                        account.setID(result.getInt("id"));
                        account.setName(result.getString("name"));
                        account.setBalance(result.getBigDecimal("balance"));
                        account.setOwnerID(result.getInt("userid"));
                        return account;
                    }
                }
            }
        }

        public int createAccount(String name, BigDecimal balance, int userid) throws SQLException{
            int accountid = -1;
            String query = "INSERT into tiw119.account (name, balance, userid) VALUES (?, ?, ?)";
            balance.setScale(2);
            con.setAutoCommit(false);
            try (PreparedStatement pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
                pstatement.setString(1, name);
                pstatement.setBigDecimal(2, balance);
                pstatement.setInt(3, userid);
                pstatement.executeUpdate();
                
                ResultSet generatedKeys = pstatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    accountid =  generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
                con.commit();
                return accountid;
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
