(function(){
	
	var topBar, homeView, statusView, resultView; //displayable
	
	var displayedAccount, addressBook; //non-displayable
	
	var pageOrchestrator = new PageOrchestrator();
	
	window.addEventListener("load", () => {
		
		pageOrchestrator.start();
		pageOrchestrator.refresh();
		
	}, false);
	
	function PageOrchestrator(){
		this.start = function(){
			
			topBar = new TopBar(
				document.getElementById("back"),
				document.getElementById("logout")
			);
			
			homeView = new HomeView(
				document.getElementById("home view"),
				document.getElementById("name"),
				document.getElementById("account_table"),
				document.getElementById("account_table_body"),
				document.getElementById("accounts_error"),
				document.getElementById("no_accounts"),
				document.getElementById("create_new_account"),
				document.getElementById("new_account_form"),
				document.getElementById("create_new_account_error")
			);
			
			//statusView = new StatusView(/*TODO*/);
			
			//resultView = new ResultView(/*TODO*/);
			
			//addressBook = new AddressBook(/*TODO*/);
			
		}
		
		
		this.refresh = function(page){
			
			switch(page){
				
				default:
				case "home":
					topBar.hideBack(); //'back' is the same as 'logout' in home. Keeping only 'logout' because it's more specific
					homeView.show();
					//statusView.hide();
					//resultView.hide();
					break;
				case "status":
					topBar.showBack("home");
					homeView.hide();
					statusView.show();
					resultView.hide();
					break;
				case "result":
					topBar.showBack("status");
					homeView.hide();
					statusView.hide();
					resultView.show();
					break;
			}
		}
	};
	
	function TopBar(_back, _logout){
		
		this.back = _back;
		this.logout = _logout;
		
		this.showBack = function(previous){
			
			this.back.style.display = "block";
			this.back.addEventListener("click", () => {
				
				pageOrchestrator.refresh(previous);
				
			}, false);
		}
		
		this.hideBack = function(){
			this.back.style.display = "none";
			
			//removing event listener in case user manually displays the back button
			let newBack = this.back.cloneNode(true);
			this.back.parentNode.replaceChild(newBack, this.back);
			this.back = newBack;
		}
	
	
		this.logout.addEventListener("click", () => {
			
			sessionStorage.clear();
			
		}, false);	
	}
	
	function HomeView(
		_home_div,
		_name,
		_account_table,
		_account_table_body,
		_accounts_error,
		_no_accounts_div,
		_create_new_account,
		_new_account_form,
		_create_new_account_error){
		
		this.home_div = _home_div;
		this.name = _name,
		this.account_table = _account_table;
		this.account_table_body = _account_table_body;
		this.accounts_error = _accounts_error;
		this.no_accounts_div = _no_accounts_div;
		this.create_new_account = _create_new_account;
		this.new_account_form = _new_account_form;
		this.create_new_account_error = _create_new_account_error;
		
		var accountList = null;
		
		var self = this;
		
		this.show = function(){
			
			
			self.name.textContent = sessionStorage.getItem("user");
			
			self.new_account_form.style.display = "none";
			self.no_accounts_div.style.display = "none";
			
			self.create_new_account.addEventListener("click", function show_form(e){
				
				e.target.value = "Hide";
				self.new_account_form.style.display = "block";
				e.target.removeEventListener("click", show_form);
				e.target.addEventListener("click", function hide_form(e){
					
					e.target.value = "Create new account";
					self.new_account_form.style.display = "none";
					e.target.removeEventListener("click", hide_form);
					e.target.addEventListener("click", show_form, false);
					
				}, false);
				
			}, false);
			
			
			var showAccounts = function(){
				
				if (accountList !== null){
				
				if (accountList.length == 0){
					self.account_table.style.display = "none";
					self.no_accounts_div.style.display = "block";
					self.create_new_account.dispatchEvent(new Event("click")); 
				}
				
				else {
					
					self.account_table.style.display = "block";
					self.no_accounts_div.style.display = "none";
										
					self.account_table_body.innerHTML = ""; //clear table body
										
					accountList.forEach((account) => { //fill table body
						
						let tableRow = document.createElement("tr");
						
						let id, name, balance, status;
						
						id = document.createElement("td");
						id.textContent = account.ID;
						tableRow.appendChild(id);
						
						name = document.createElement("td");
						name.textContent = account.name;
						tableRow.appendChild(name);
						
						balance = document.createElement("td");
						balance.textContent = account.balance;
						tableRow.appendChild(balance);
						
						status = document.createElement("td");
						let status_anchor = document.createElement("a");
						status_anchor.href = "#";
						status_anchor.textContent = "Show account status";
						
						status_anchor.addEventListener("click", () => {
							
							displayedAccount = account;
							
							pageOrchestrator.refresh("status");
							
						}, false);
						
						status.appendChild(status_anchor);
						
						tableRow.appendChild(status);
						
						self.account_table_body.appendChild(tableRow);
					});
				}
			}
			};
			
			if (accountList === null){
				
				makeCall("GET", "GetAccountsData", null, (request) => {
					
					switch(request.status){ //get status code
						
						case 200: //ok
							accountList = JSON.parse(request.responseText);
							self.accounts_error.style.display = "none";
							showAccounts();
							break;
						case 400: //bad request
						case 401: //unauthorized
						case 500: //server error
							self.accounts_error.textContent = request.responseText;
							self.accounts_error.style.display = 'block';
							break;
						default: //error
							self.accounts_error.textContent = "Request reported status " + request.status;
							self.accounts_error.style.display = 'block';
					}
				});
			}
			
			else showAccounts();
			
			
			let new_account_submit = self.new_account_form.querySelector("input[name='create new account']");
			let new_account_name = self.new_account_form.querySelector("input[name='name']");
			
			
			new_account_submit.addEventListener("click", () => {
				
				if ((function(){ //check for duplicate account name
					
					accountList.forEach((account) => {	
						if (account.name === new_account_name) return true;
					})
					return false;
				})()) {
					
					self.create_new_account_error.textContent = "Error! There's already an account with the same name";
					self.create_new_account_error.style.display = "block";
				}
				
				else {
					
					makeCall("POST", "CreateAccount", self.new_account_form, (request) => {
						
						switch(request.status){
							
							case 200: //ok
							pageOrchestrator.refresh("home");
							break;
						case 400: //bad request
						case 401: //unauthorized
						case 500: //server error
							self.create_new_account_error.textContent = request.responseText;
							self.create_new_account_error.style.display = 'block';
							break;
						default: //error
							self.create_new_account_error.textContent = "Request reported status " + request.status;
							self.create_new_account_error.style.display = 'block';
						}
						
					})
				}
				
			}, false)
			
			this.home_div.style.display = "block";
		}
	}	

})()