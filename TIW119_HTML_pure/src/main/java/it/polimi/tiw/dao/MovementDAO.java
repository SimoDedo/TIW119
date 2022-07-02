package it.polimi.tiw.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Movement;

public class MovementDAO {
    private Connection con;
    
    public MovementDAO(Connection connection) {
        this.con = connection;
    }

    public List<Movement> getIncomingMovementsByAccount(int accountID) throws SQLException{
        List<Movement> movements = new ArrayList<>();
        String query = "SELECT  id, date, amount, motive, inAccountid, outAccountid FROM tiw119.movement  WHERE inAccountid = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setInt(1, accountID);
            try (ResultSet result = pstatement.executeQuery();) {
                    while(result.next()){
                        Movement movement = new Movement();
                        movement.setID(result.getInt("id"));
                        movement.setDate(result.getTimestamp("date"));
                        movement.setAmount(result.getBigDecimal("amount"));
                        movement.setMotive(result.getString("motive"));
                        movement.setInAccountID(result.getInt("inAccountid"));
                        movement.setOutAccountID(result.getInt("outAccountid"));
                        movements.add(movement);
                    }
                }
            }
        return movements;
    }

    public List<Movement> getOutgoingMovementsByAccount(int accountID) throws SQLException{
        List<Movement> movements = new ArrayList<>();
        String query = "SELECT  id, date, amount, motive, inAccountid, outAccountid FROM tiw119.movement  WHERE outAccountid = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setInt(1, accountID);
            try (ResultSet result = pstatement.executeQuery();) {
                    while(result.next()){
                        Movement movement = new Movement();
                        movement.setID(result.getInt("id"));
                        movement.setDate(result.getTimestamp("date"));
                        movement.setAmount(result.getBigDecimal("amount"));
                        movement.setMotive(result.getString("motive"));
                        movement.setInAccountID(result.getInt("inAccountid"));
                        movement.setOutAccountID(result.getInt("outAccountid"));
                        movements.add(movement);
                    }
                }
            }
        return movements;
    }

    public Movement getMovementByID(int movementID) throws SQLException{
        String query = "SELECT  id, date, amount, motive, inAccountid, outAccountid FROM tiw119.movement  WHERE outAccountid = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setInt(1, movementID);
            try (ResultSet result = pstatement.executeQuery();) {
                if(result.isBeforeFirst()){
                    return null;
                }
                else{
                    result.next();
                    Movement movement = new Movement();
                    movement.setID(result.getInt("id"));
                    movement.setDate(result.getTimestamp("date"));
                    movement.setAmount(result.getBigDecimal("amount"));
                    movement.setMotive(result.getString("motive"));
                    movement.setInAccountID(result.getInt("inAccountid"));
                    movement.setOutAccountID(result.getInt("outAccountid"));
                    
                    return movement;
                }
            }
        }
    }

    public void requestMovement(Date date, BigDecimal amount, String motive, int inAccountID, int outAccountID) throws SQLException{
        String query = "INSERT into tiw119.movement (date, amount, motive, inAccountid, outAccountid) VALUES (?, ?, ?, ?, ?)";
        amount.setScale(2);
        con.setAutoCommit(false);
        try (PreparedStatement pstatement = con.prepareStatement(query);) {
            pstatement.setTimestamp(1, new Timestamp(date.getTime()));
            pstatement.setBigDecimal(2, amount);
            pstatement.setString(3, motive);
            pstatement.setInt(4, inAccountID);
            pstatement.setInt(5, outAccountID);
            pstatement.executeUpdate();
            
            con.commit();
        }
        catch(SQLException e){
            con.rollback();
            throw e;
        }
        
    }
}
