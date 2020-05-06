<#assign content>
	<div class="nav-bar">
		<p><a href="/login"><img src="css/images/iconLeave.svg" height=40 width=40/></a></p>
	</div>
	<h1> CREATE YOUR ACCOUNT HERE</h1>
	<div class="inputForm">
		<label for="email">Enter Your Email</label>
		<p><input type="email" name="email" id="email" required></p>
		<label for="username">Enter Your Desired Username:</label>
	  	<p><input type="text" name="username" id="username" required></p>
		<label for="pass">Enter Password:</label>
		<p><input type="password" name="password" id="pass" required></p>
		<label for="re_password">Re-Enter Password:</label>
		<p><input type="password" name="re_password" id="re_password" required></p>
	  	<div class="submitbutton"><button class="submitbutton" onClick="createAccountHandler()">Submit</button></div>
	</div>
	<p id="message_for_account"></p>

</#assign>
<#include "main.ftl">