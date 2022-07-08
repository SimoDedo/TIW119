var checkNonEmptyFields = function(fields) {

  var nonEmpty = true;

  Array.from(fields).forEach((field) => {
    if (field.value === "") {

      field.placeholder = "Field " + field.id + " is required";
      nonEmpty = false;

    }
  });

  return nonEmpty;
};

var checkEmailFields = function() {

  var email = document.getElementById("email").value;

  if (email !== "") {

    var atIndex = email.indexOf("@");


    //todo change
    var email_error = document.getElementById("signup error");

    if (atIndex === 0) {

      email_error.textContent = "Error! Email has no identifier!";

      return false;

    } else if (atIndex === -1  || atIndex === email.length) { //no domain

      email_error.textContent = "Error! Email has no domain!";
      return false;

    } else {

      var dotIndex = email.indexOf(".", atIndex);


      if (dotIndex === -1) {

        email_error.textContent = "Error! Email has no domain!";
        return false;

      } else if (dotIndex === 1) { //point is right after the '@' sign

        email_error.textContent = "Error! Missing email server domain!";
        return false;

      } else if (dotIndex === email.length) {

        email_error.textContent = "Error! Missing top-level domain!";
        return false;
      }
    }
  } else { //virtually unreachable
    return false;
  }

  return true;
}

var checkPasswords = function() {

  var password = document.getElementById("password");
  var repeat_password = document.getElementById("repeat password");

  return password == repeat_password;
}

document.getElementById("signup").addEventListener("click", (e) => {

  document.getElementById("signup_form").style.display = "block";
  document.getElementById("login_form").style.display = "none";

  document.getElementsByTagName("h1")[0].innerHTML = "SIGN UP";
}, false)

document.getElementById("login").addEventListener("click", (e) => {

  document.getElementById("login_form").style.display = "block";
  document.getElementById("signup_form").style.display = "none";

  document.getElementsByTagName("h1")[0].innerHTML = "LOGIN";
}, false)

document.getElementById("b2").addEventListener("click", (e) => {

  e.preventDefault();

  if (!checkNonEmptyFields(document.getElementsByClassName("signup_field")) ||
    !checkEmailFields() ||
    !checkPasswords()) {
    document.getElementById("signup error").style.display = "block";
    return;
  } else { //all inputs are correct

  }

}, false)
