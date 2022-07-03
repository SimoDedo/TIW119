package it.polimi.tiw.utils;

/**
 * Enum for all possible error a servlet might send
 */
public enum ServletError {
    
    NOT_LOGGED_IN("Login before trying to access any page!"),
    MISSING_CREDENTIALS("Missing or empty credentials"),
    MISSING_DATA("Missing data in form!"),
    NUMBER_FORMAT("Number not formatted correctly. Use \".\" to insert decimals!"),
    NEGATIVE_BALANCE("Balance can't be negative!"),
    NEGATIVE_AMOUNT("Amount can't be negative!"),
    USER_NOT_FOUND("No user exists with given credentials"),
    USER_ID_NOT_FOUND("No user exists with given ID"),
    PASSWORD_MISMATCH("Passwords do not match!"),
    EMAIL_FORMAT("Email given is not a valid address!"),
    USERNAME_ALREADY_EXISTS("A user with the given username already exists"),
    EMAIL_ALREADY_EXISTS("A user with the given email already exists"),
    ACC_NAME_ALREADY_EXISTS("An account with the given name already exists"),
    ACC_ID_FORMAT("The account ID given is not a number"),
    ACC_NOT_FOUND("The account does not exist"),
    ACC_NOT_OWNED("The account you're trying to access is not owned by you. If the account is yours, try loggin in with another account"),
    ACC_NOT_OWNED_BY_USER("The account you're trying to access is not owned by the given user"),
    ACC_INSUFFICIENT_BALANCE("The account doesn't have enough money to make the transfer"),
    ACC_SAME("You can't move money from and to the same account"),
    NO_MOVEMENT_MADE("You have not made any new movement"),
    IE_RETRIEVE_USER("Internal error: unable to retrieve user data."),
    IE_RETRIEVE_ACC("Internal error: unable to retrieve account data."),
    IE_RETRIEVE_MOV("Internal error: can't retrieve account movements"),
    IE_REGISTRATION("Internal error: can't register user"),
    IE_CREATE_ACC("Internal error: can't create account"),
    IE_USER_NOT_FOUND("Internal error: can't find the registered user"),
    IE_ACC_NOT_FOUND("Internal error: can't find account with given ID"),
    IE_CHECK_CREDENTIALS("Internal error: can't check credentials"),
    IE_CREATE_MOVEMENT("Internal error: can't create movement"),
    IE_CHECK_ACC_NAME("Internal error: can't check if name already exists");

    private ServletError(String desc){

        this.desc = desc;
    }

    private String desc;

    public String toString() {
        return desc;
    }
}
