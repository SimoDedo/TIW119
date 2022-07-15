(function(){
	
	var topBar, homeView, statusView, resultView; //displayable
	
	var displayedAccount,  transferResult, addressBook; //non-displayable
	
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
			
			statusView = new StatusView(
				document.getElementById("status view"),
				document.getElementById("info_table_body"),
				document.getElementById("movements_error"),
				document.getElementById("incoming_movements"),
				document.getElementById("in_mov_table_body"),
				document.getElementById("no_incoming_movements"),
				document.getElementById("outgoing_movements"),
				document.getElementById("out_mov_table_body"),
				document.getElementById("no_outgoing_movements"),
				document.getElementById("request_movement"),
				document.getElementById("request_movement_form"),
				document.getElementById("request_movement_error")
			);
			
			resultView = new ResultView(
				document.getElementById("result view"),
				document.getElementById("result_success"),
				document.getElementById("out_account_id"),
				document.getElementById("in_account_id"),
				document.getElementById("movement_date"),
				document.getElementById("out_account_id_repeat"),
				document.getElementById("out_account_id_old_balance"),
				document.getElementById("movement_amount"),
				document.getElementById("out_account_new_balance"),
				document.getElementById("movement_amount"),
				document.getElementById("out_account_new_balance"),
				document.getElementById("in_account_id_repeat"),
				document.getElementById("in_account_old_balance"),
				document.getElementById("movement_amount_repeat"),
				document.getElementById("in_account_new_balance"),
				document.getElementById("add_contact_div"),
				document.getElementById("in_account_user"),
				document.getElementById("add_contact_button"),
				document.getElementById("refuse_contact_button"),
				document.getElementById("result_failure"),
				document.getElementById("movement_error")
			);
						
		}
		
		
		this.refresh = function(page){
			
			switch(page){
				
				default:
				case "home":
					topBar.hideBack(); //'back' is the same as 'logout' in home. Keeping only 'logout' because it's more specific
					homeView.show();
					statusView.hide();
					resultView.hide();
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
		
		var new_account_submit = document.getElementById("submit_create_new_account");
		var actual_create_new_account_form = new_account_submit.closest("form");
		
		var accountList = null;
		
		var self = this;
		
		this.show = function(){
			
			
			self.name.textContent = sessionStorage.getItem("name") + " " + sessionStorage.getItem("surname");
			
			self.new_account_form.style.display = "none";
			
			self.create_new_account.addEventListener("click", function show_form(e){
				
				e.target.textContent = "Hide";
				self.new_account_form.style.display = "block";
				e.target.removeEventListener("click", show_form);
				e.target.addEventListener("click", function hide_form(e){
					
					e.target.textContent = "Create new account";
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
			
			
			let new_account_name = actual_create_new_account_form.querySelector("input[name='name']");
			
			
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
				
				else if (actual_create_new_account_form.checkValidity()) {
					
					makeCall("POST", "CreateAccount", actual_create_new_account_form, (request) => {
						
						switch(request.status){
							
							case 200: //ok
							accountList = null;
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
				
				else actual_create_new_account_form.reportValidity();
				
			}, false)
			
			this.home_div.style.display = "block";
		}
		
		this.hide = function(){
		
		self.home_div.style.display = "none";
		self.accounts_error.style.display = "none";
		actual_create_new_account_form.reset();
		if(self.create_new_account.textContent === "Hide") self.create_new_account.dispatchEvent(new Event("click")); //close form when hiding view
		}
	
	
	}
	
	function StatusView(
		_status_div,
		_info_table_body,
		_movements_error,
		_incoming_movements,
		_in_mov_table_body,
		_no_incoming_movements,
		_outgoing_movements,
		_out_mov_table_body,
		_no_outgoing_movements,
		_request_movement,
		_request_movement_form,
		_request_movement_error){
			
			this.status_div = _status_div;
			this.info_table_body = _info_table_body;
			this.movements_error = _movements_error;
			this.incoming_movements = _incoming_movements;
			this.in_mov_table_body = _in_mov_table_body;
			this.no_incoming_movements = _no_incoming_movements;
			this.outgoing_movements = _outgoing_movements;
			this.out_mov_table_body = _out_mov_table_body;
			this.no_outgoing_movements = _no_outgoing_movements;
			this.request_movement = _request_movement;
			this.request_movement_form = _request_movement_form;
			this.request_movement_error = _request_movement_error;
		
		
		
		var request_submit = document.getElementById("request_movement_submit");
		var actual_request_movement_form = request_submit.closest("form");
		
		var self = this;
		
		this.show = function(){
			
			self.info_table_body.innerHTML = ""; //clear table content
			
			let info_row = document.createElement("tr");
			
			let userID, user, accountID, accountName, balance;
			
			userID = document.createElement("td");
			userID.textContent = sessionStorage.getItem("ID");
			info_row.appendChild(userID);
			
			user = document.createElement("td");
			user.textContent = sessionStorage.getItem("username");
			info_row.appendChild(user);
			
			accountID = document.createElement("td");
			accountID.textContent = displayedAccount.ID;
			info_row.appendChild(accountID);

			accountName = document.createElement("td");
			accountName.textContent = displayedAccount.name;
			info_row.appendChild(accountName);
			
			balance = document.createElement("td");
			balance.textContent = displayedAccount.balance;
			info_row.appendChild(balance);
			
			self.info_table_body.appendChild(info_row);
						
			var movements, in_movements, out_movements;
			
			var showMovements = function(){
				
				//incoming movements
				if (in_movements.length === 0){
					self.incoming_movements.style.display = "none";
					self.no_incoming_movements.style.display = "block";
				}
				
				else {
					
					self.incoming_movements.style.display = "block";
					self.no_incoming_movements.style.display = "none";
					
					self.in_mov_table_body.innerHTML = ""; //clear table body
					
					in_movements.forEach((movement) => {
						
						let in_mov_row = document.createElement("tr");
						
						let date, amount, motive, senderID;
						
						date = document.createElement("td");
						date.textContent = movement.date;
						in_mov_row.appendChild(date);
						
						amount = document.createElement("td");
						amount.textContent = movement.amount;
						in_mov_row.appendChild(amount);
						
						motive = document.createElement("td");
						motive.textContent = movement.motive;
						in_mov_row.appendChild(motive);
						
						senderID = document.createElement("td");
						senderID.textContent = movement.outAccountID;
						in_mov_row.appendChild(senderID);
						
						self.in_mov_table_body.appendChild(in_mov_row);
					});
				}
				
				//outgoing movements
				if (out_movements.length === 0){
					self.outgoing_movements.style.display = "none";
					self.no_outgoing_movements.style.display = "block";
				}
				
				else {
					
					self.outgoing_movements.style.display = "block";
					self.no_outgoing_movements.style.display = "none";

					
					self.out_mov_table_body.innerHTML = ""; //clear table body
					
					out_movements.forEach((movement) => {
						
						let out_mov_row = document.createElement("tr");
						
						let date, amount, motive, recipientID;
						
						date = document.createElement("td");
						date.textContent = movement.date;
						out_mov_row.appendChild(date);
						
						amount = document.createElement("td");
						amount.textContent = movement.amount;
						out_mov_row.appendChild(amount);
						
						motive = document.createElement("td");
						motive.textContent = movement.motive;
						out_mov_row.appendChild(motive);
						
						recipientID = document.createElement("td");
						recipientID.textContent = movement.inAccountID;
						out_mov_row.appendChild(recipientID);
						
						self.out_mov_table_body.appendChild(out_mov_row);
					});
				}
			}
			
			makeCall("GET", "GetMovementsData?accountid=" + displayedAccount.ID, null, (request) => {
				
				switch(request.status){
							
						case 200: //ok
						movements = JSON.parse(request.responseText);
						in_movements = movements["inMovs"];
						out_movements = movements["outMovs"];
						self.movements_error.style.display = "none";
						showMovements();
						break;
						case 400: //bad request
						case 401: //unauthorized
						case 500: //server error
							self.movements_error.textContent = request.responseText;
							self.movements_error.style.display = 'block';
							break;
						default: //error
							self.movements_error.textContent = "Request reported status " + request.status;
							self.movements_error.style.display = 'block';
						}
				
			} );
			
			self.request_movement_form.style.display = "none";
			
			self.request_movement.addEventListener("click", function show_form(e){
				
				e.target.textContent = "Hide";
				self.request_movement_form.style.display = "block";
				e.target.removeEventListener("click", show_form);
				e.target.addEventListener("click", function hide_form(e){
					
					e.target.textContent = "Request movement";
					self.request_movement_form.style.display = "none";
					e.target.removeEventListener("click", hide_form);
					e.target.addEventListener("click", show_form, false);
					
				}, false);
				
			}, false);
			
			
			let destUserID = actual_request_movement_form.querySelector("input[name='inuserid']");
			let amount = actual_request_movement_form.querySelector("input[name='amount']");
			let srcAccountID = actual_request_movement_form.querySelector("input[name='outaccountid']");
			
			request_submit.addEventListener("click", () => {
				
				if (destUserID === sessionStorage.getItem("user").ID){
					self.request_movement_error.textContent = "Error! Cannot transfer from self";
					self.request_movement_error.style.display = "block";
				}
				
				else if (amount <= 0){
					self.request_movement_error.textContent = "Error! Amount must be greater than 0";
					self.request_movement_error.style.display = "block";
				}
				
				else if (actual_request_movement_form.checkValidity()){
					
					transferResult = null;
					
					srcAccountID.value = displayedAccount.ID;
					
					makeCall("POST", "RequestMovement", actual_request_movement_form, (request) => {
						
						switch(request.status){
							
							case 200: //ok
							transferResult = JSON.parse(request.responseText);
							pageOrchestrator.refresh("result");
							break;
							case 400: //bad request
							case 401: //unauthorized
							case 500: //server error
								resultView.movement_error.textContent = request.responseText;
								break;
							default: //error
								resultView.movement_error.textContent = "Request reported status " + request.status;
						}
						
					})
				}
				
			}, false);
			
			self.status_div.style.display = "block";
		}
		
		this.hide = function(){
			
			self.status_div.style.display = "none";
			actual_request_movement_form.reset();
			if (self.request_movement.textContent === "Hide") self.request_movement.dispatchEvent(new Event("click"));
		}
	}


	function ResultView(
		_result_div,
		_result_success,
		_out_account_id,
		_in_account_id,
		_movement_date,
		_out_account_id_repeat,
		_out_account_old_balance,
		_movement_amount,
		_out_account_new_balance,
		_in_account_id_repeat,
		_in_account_old_balance,
		_movement_amount_repeat,
		_in_account_new_balance,
		_add_contact_div,
		_in_account_user,
		_add_contact_button,
		_refuse_contact_button,
		_result_failure,
		_movement_error
	){
		
		this.result_div = _result_div;
		this.result_success = _result_success;
		this.out_account_id = _out_account_id;
		this.in_account_id =_in_account_id;
		this.movement_date = _movement_date;
		this.out_account_id_repeat = _out_account_id_repeat;
		this.out_account_old_balance = _out_account_old_balance;
		this.movement_amount = _movement_amount;
		this.out_account_new_balance = _out_account_new_balance;
		this.in_account_id_repeat = _in_account_id_repeat;
		this.in_account_old_balance = _in_account_old_balance;
		this.movement_amount_repeat = _movement_amount_repeat;
		this.in_account_new_balance = _in_account_new_balance;
		this.add_contact_div = _add_contact_div;
		this.in_account_user = _in_account_user;
		this.add_contact_button = _add_contact_button;
		this.refuse_contact_button = _refuse_contact_button;
		this.result_failure = _result_failure;
		this.movement_error = _movement_error;
		
		var self = this;
		
		this.show = function(){
			
			if (transferResult === null){ //failure
				
				self.result_success.style.display = "none";
				self.result_failure.style.display = "block";
				self.movement_error.style.display = "block";
			}
			
			else{
				self.result_failure.style.display = "none";
				
				var movement, outAccount, inAccount;
				
				movement = transferResult["movement"];
				outAccount = transferResult["outAccount"];
				inAccount = transferResult["inAccount"];
				
				self.out_account_id.textContent = outAccount.ID;
				self.out_account_id_repeat.textContent = self.out_account_id.textContent;
				
				self.in_account_id.textContent = inAccount.ID.textContent;
				self.in_account_id_repeat.textContent = self.in_account_id.textContent;
				
				self.movement_date.textContent = movement.date;
				
				self.out_account_old_balance.textContent = outAccount.balance;
				
				self.movement_amount.textContent = movement.amount;
				self.movement_amount_repeat.textContent = self.movement_amount.textContent;
				
				self.out_account_new_balance.textContent = outAccount.balance + movement.amount;
				
				self.in_account_old_balance.textContent = inAccount.balance;
				
				self.in_account_new_balance.textContent = inAccount.balance + movement.amount; 
				
				//TODO address book
				
				self.result_success.style.display = "block";
			}
		}
		
		this.hide = function(){
			
			self.result_div.style.display = "none";
		}
		
	}
	
})()