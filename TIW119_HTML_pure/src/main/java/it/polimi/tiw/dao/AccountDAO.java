package it.polimi.tiw.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            String query = "SELECT  id, name, balance, userid FROM account  WHERE id = ?";
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
            String query = "SELECT  id, name, balance FROM account  WHERE id = ?";
            try (PreparedStatement pstatement = con.prepareStatement(query);) {
                pstatement.setInt(1, id);
                try (ResultSet result = pstatement.executeQuery();) {
                    if (!result.isBeforeFirst()) // no results, credential check failed
                        return null;
                    else {
                        result.next();
                        Account account = new Account();
                        account.setID(result.getInt("id"));
                        account.setName(result.getString("name"));
                        account.setBalance(result.getBigDecimal("balance"));
                        return account;
                    }
                }
            }
        }

        public void createAccount(String name, BigDecimal balance) throws SQLException{
            String query = "INSERT into account (name, balance) VALUES (?, ?)";
            con.setAutoCommit(false);
            try (PreparedStatement pstatement = con.prepareStatement(query);) {
                pstatement.setString(1, name);
                pstatement.setBigDecimal(2, balance);
                pstatement.executeUpdate();
                
                con.commit();
            }
            catch(SQLException e){
                con.rollback();
                throw e;
            }
            
        }

}
