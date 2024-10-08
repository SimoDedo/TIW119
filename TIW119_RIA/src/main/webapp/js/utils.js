function makeCall(method, relativeUrl, form, done_callback, reset = true) {
	var req = new XMLHttpRequest(); //Create new request
	//Init request
	req.onreadystatechange = function() {
		switch (req.readyState) {
			case XMLHttpRequest.UNSENT: break;
			case XMLHttpRequest.OPENED: break;
			case XMLHttpRequest.HEADERS_RECEIVED: break;
			case XMLHttpRequest.LOADING: break;
			case XMLHttpRequest.DONE: 
				if (checkRedirect(relativeUrl, req.responseURL)) { //Redirect if needed
					done_callback(req);
				}
				break;
		}
	};

	//Open request
	req.open(method, relativeUrl, true);
	//Send request
	if (form == null) {
		req.send(); //Send empty if no form provided				
	} else if (form instanceof FormData) {
		req.send(form); //Send already serialized form
	} else {
		req.send(new FormData(form)); //Send serialized form
	}
	//Eventually reset form (if provided)
	if (form !== null && !(form instanceof FormData) && reset === true) {
		form.reset(); //Do not touch hidden fields, and restore default values if any
	}

}


function checkRedirect(requestURL, responseURL) {
	if (responseURL !== null) {
		let actualRequestURL = absolutePathFromRelative(requestURL);
		if (actualRequestURL != responseURL) { //Url changed
			window.location.assign(responseURL); //Navigate to the url
			return false;
		}
		return true; //Pass the request to callback
	}
	//Else is CORS blocked or redirection loop 
	console.error("Invalid AJAX call");
	return false;
}

function absolutePathFromRelative(relative) {
	var stack = window.location.href.split("/"),
		parts = relative.split("/");
	stack.pop(); // remove current file name (or empty string)
	for (var i = 0; i < parts.length; i++) {
		if (parts[i] == ".")
			continue;
		if (parts[i] == "..")
			stack.pop(); //One directory back
		else
			stack.push(parts[i]); //Add to path
	}
	return stack.join("/"); //Join everything
}

/**
	makes errors hideable
 */
(function(){
	
	var errors = Array.from(document.getElementsByClassName("error"));
	
	errors.forEach((error) => {
		
		error.addEventListener("click", (e) => {
			
			e.target.style.display = "none";
		}, false);
	});
	
})()

Array.prototype.contains = function(element){ 
    for(let i = 0;i<this.length;i++)
        if (this[i] == element)
            return true;
    
    return false;
}