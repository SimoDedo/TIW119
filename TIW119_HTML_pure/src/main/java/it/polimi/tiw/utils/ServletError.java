package it.polimi.tiw.utils;

/**
 * Enum for all possible error a servlet might send
 */
public enum ServletError {
    
    NOT_LOGGED_IN("Login before trying to access Home page!"),
    MISSING_CREDENTIALS("Missing or empty credentials"),
    MISSING_DATA("Missing data in form!"),
    NUMBER_FORMAT("Number not formatted correctly. Use \".\" to insert decimals!"),
    NEGATIVE_NUMBER("Balance can't be negative!"),
    USER_NOT_FOUND("No user exists with given credentials"),
    PASSWORD_MISMATCH("Passwords do not match!"),
    EMAIL_FORMAT("Email given is not a valid address!"),
    USERNAME_ALREADY_EXISTS("A user with the given username already exists"),
    EMAIL_ALREADY_EXISTS("A user with the given email already exists"),
    ACC_NAME_ALREADY_EXISTS("An account with the given name already exists"),
    ACC_ID_FORMAT("The account ID given is not a number"),
    ACC_NON_EXISTENT("The account you're trying to access does not exist"),
    ACC_NOT_OWNED("The account you're trying to access is not owned by you. If the account is yours, try loggin in with another account"),
    IE_RETRIEVE_ACC("Internal error: unable to retrieve account data."),
    IE_REGISTRATION("Internal error: can't register user"),
    IE_CREATE_ACC("Internal error: can't create account"),
    IE_USER_NOT_FOUND("Internal error: can't find the registered user"),
    IE_CHECK_CREDENTIALS("Internal error: can't check credentials"),
    IE_CHECK_ACC_NAME("Internal error: can't check if name already exists");

    private ServletError(String desc){

        this.desc = desc;
    }

    private String desc;

    public String toString() {
        return desc;
    }
}
