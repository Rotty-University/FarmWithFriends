<#assign content>
	<ul class="nav-bar">
    	<li class="nav_bar_top"><a href="/logout">Logout</a></li>
    </ul>
<style>
.nav-bar{
  list-style-type: none;
  margin: 0;
  padding: 0;
}
.nav_bar_top {
  display: inline;
}
li a {
  display: block;
  padding: 8px;
  background-color: #dddddd;
}
* {box-sizing: border-box;}

/* Button used to open the contact form - fixed at the bottom of the page */
.open-button {
  background-color: #555;
  color: white;
  padding: 16px 20px;
  border: none;
  cursor: pointer;
  opacity: 0.8;
  position: fixed;
  bottom: 23px;
  right: 28px;
  width: 280px;
}
.friend_list_button {
  background-color: #555;
  color: white;
  padding: 16px 20px;
  border: none;
  cursor: pointer;
  opacity: 0.8;
  width: 280px;
}
.friends_list{
  display: none;
  position: left;
  border: 3px solid #f1f1f1;
  z-index: 9;
  background-color: white;
}
/* The popup form - hidden by default */
.form-popup {
  display: none;
  position: fixed;
  bottom: 0;
  right: 15px;
  border: 3px solid #f1f1f1;
  z-index: 9;
  background-color: white;
}


/* Full-width input fields */
#addfriendstext{
  width: 100%;
  padding: 15px;
  margin: 5px 0 22px 0;
  border: none;
  background: #f1f1f1;
}

/* When the inputs get focus, do something */
#addfriendstext:focus{
  background-color: #ddd;
  outline: none;
}

/* Set a style for the submit/login button */
.btnn {
  background-color: #4CAF50;
  color: white;
  padding: 16px 20px;
  border: none;
  cursor: pointer;
  width: 100%;
  margin-bottom:10px;
  opacity: 0.8;
}

/* Add a red background color to the cancel button */
.cancel {
  background-color: red;
}

/* Add some hover effects to buttons */
.btn:hover, .open-button:hover {
  opacity: 1;
}
</style>
<div class="friends_list" id="myFriendList">
    <h1>Friends List</h1>
    <ul id ="list_of_friends">
    <li>hello</li>
    </ul>
        <button type="button" class="btnn cancel" onclick="closeForm('myFriendList')">Close</button>
</div>
<button class="friend_list_button" onclick="openFriendList('myFriendList')">FriendsList</button>
<button class="open-button" onclick="openForm('myForm')">Click Here to Add Friends</button>

<div class="form-popup" id="myForm">
    <h1>Add A Friend</h1>

    <label for="friend_username"><b>Username of Friend</b></label>
    <input type="text" id="addfriendstext" placeholder="Enter username of player" name="friend_username" required>
    
    <button type="button" class="btnn", id="add_friend_button">Send Request</button>
    <button type="button" class="btnn cancel" onclick="closeForm('myForm')">Close</button>
    <p id ="message_for_friend_status">s</p>
</div>

<script>
function openForm(form) {
  document.getElementById(form).style.display = "block";
}

function closeForm(form) {
  document.getElementById(form).style.display = "none";
}
</script>
<h1> Welcome to your home page man </h1>
<br><br><br>


</#assign>
<#include "main.ftl">