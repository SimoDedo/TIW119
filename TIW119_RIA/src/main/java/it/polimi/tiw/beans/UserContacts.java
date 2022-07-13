package it.polimi.tiw.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean for the contacts of a user
 */
public class UserContacts{
	
	int contactsOwner;
	Map<Integer, List<Integer>> contacts = new HashMap<>();
	
	public int getContactsOwner() {
		return contactsOwner;
	}

	public void setContactsOwner(int contactsOwner) {
		this.contactsOwner = contactsOwner;
	}

	public void addContact(Integer contactUserid, Integer accountid){
		if(contacts.containsKey(contactUserid)){
			contacts.get(contactUserid).add(accountid);
		}
		else{
			contacts.put(contactUserid, new ArrayList<>());
			contacts.get(contactUserid).add(accountid);
		}
	}

	public Map<Integer, List<Integer>> getContacts(){
		return new HashMap<>(contacts);
	}
	

}