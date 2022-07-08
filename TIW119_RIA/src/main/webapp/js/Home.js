document.getElementById("new account").addEventListener("click", function new_account(e) {

  document.getElementById("new account form").style.display = "block";
  e.target.textContent = "Hide";
  e.target.removeEventListener("click", new_account);
  e.target.addEventListener("click", function hide(e) {

    document.getElementById("new account form").style.display = "none";
    e.target.textContent = "Create new account";
    e.target.removeEventListener("click", hide);
    e.target.addEventListener("click", new_account, false);

  }, false);
}, false);

Array.from(document.getElementsByClassName("show status")).forEach(account => {
  account.addEventListener("click", (e) => {

    document.getElementById("status view").style.display = "block";
    document.getElementById("home view").style.display = "none";

  }, false)
})
