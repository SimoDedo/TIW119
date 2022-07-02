package it.polimi.tiw.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class Movement {
	private int ID;
	private Date date;
	private BigDecimal amount;
	private String motive;
	private int inAccountID;
	private int outAccountID;

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMotive() {
		return motive;
	}
	public void setMotive(String motive) {
		this.motive = motive;
	}

	public int getInAccountID() {
		return inAccountID;
	}
	public void setInAccountID(int inAccountID) {
		this.inAccountID = inAccountID;
	}
	
	public int getOutAccountID() {
		return outAccountID;
	}
	public void setOutAccountID(int outAccountID) {
		this.outAccountID = outAccountID;
	}
}
