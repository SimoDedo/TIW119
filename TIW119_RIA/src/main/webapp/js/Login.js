(function() {
	var login_div = document.getElementById("login_div");
	var signup_div = document.getElementById("signup_div");
	var login_button = document.getElementById("login_button");
	var signup_button = document.getElementById("signup_button");
	var login_error = document.getElementById("login_error");
	var signup_error = document.getElementById("signup_error");
	var go_to_signup = document.getElementById("go_to_signup");
	var go_to_login = document.getElementById("go_to_login");
	var password_input = signup_button.closest("form").querySelector("input[name='password']");
	var repeat_password_input = signup_button.closest("form").querySelector("input[name='repeatPassword']");

	//allow switching between login and signup view
	go_to_signup.addEventListener("click", (e) => {

		e.target.closest("form").reset();
		login_div.style.display = "none";
		signup_div.style.display = "block";

	}, false);

	go_to_login.addEventListener("click", (e) => {

		e.target.closest("form").reset();
		signup_div.style.display = "none";
		login_div.style.display = "block";

	}, false);

	//attach login function
	login_button.addEventListener("click", (e) => {

		login_error.style.display = "none";

		var form = e.target.closest("form");

		if (form.checkValidity()) {
			sendToServer("CheckLogin", form, login_error);
		} else {
			form.reportValidity();
		}

	}, false);

	//attach signup function
	signup_button.addEventListener("click", (e) => {

		signup_error.style.display = "none";

		var form = e.target.closest("form");

		if (form.checkValidity()) {
			if (repeat_password_input.value != password_input.value){
                signup_error.textContent = "Passwords do not match";
                signup_error.style.display = 'block';
            }
			else{
				sendToServer("CheckSignup", form, signup_error);
			}
		} else {
			form.reportValidity();
		}

	}, false);

	function sendToServer(request_url, form, error_div) {
		makeCall("POST", request_url, form, function(request) {
			switch (request.status) { //get status code
				case 200: //ok
					var username = JSON.parse(request.responseText);
					sessionStorage.setItem('user', username);
					window.location.href = "Home.html";
					break;
				case 400: //bad request
				case 401: //unauthorized
				case 500: //server error
					error_div.textContent = request.responseText;
					error_div.style.display = 'block';
					break;
				default: //error
					error_div.textContent = "Request reported status " + request.status;
					error_div.style.display = 'block';
			}
		}, false);
	}


})()
