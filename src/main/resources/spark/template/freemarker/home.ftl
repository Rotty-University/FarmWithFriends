<#assign content>
	<div class="nav-bar">
		<p><a href="/create_account"><img src="css/images/iconNew.svg" height=40 width=40/></a></p>
	</div>
	<h1> Welcome to the FARMING SIMULATOR </h1>
	<h2>Please Login:</h2>
	<form method="POST" action="/home" class="inputForm">
		<label for="text">Username:</label>
	  	<p><input type="text" name="username" id="text" required></p>
		<label for="pass">Password:</label>
		<p><input type="password" name="password" id="pass" required></p>
	  	<div class="submitbuttonbox" ><input class="submitbutton" type="submit"></div>
	</form>
	<p>${message}</p>
</#assign>
<#include "main.ftl">