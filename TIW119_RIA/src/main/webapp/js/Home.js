(function(){
	
	var topBar, homeView, statusView, resultView; //displayable
	
	var addressBook; //non-displayable
	
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
			
			homeView = new HomeView(/*TODO*/);
			
			statusView = new StatusView(/*TODO*/);
			
			resultView = new ResultView(/*TODO*/);
			
			addressBook = new AddressBook(/*TODO*/);
			
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
			this.back.dispatchEvent("click"); //removing event listener in case user manually displays the back button
		}
	
	
		this.logout.addEventListener("click", () => {
			
			//eeeeee mo vediamo
			
		}, false);	
	}

})()