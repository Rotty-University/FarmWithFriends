<#assign content>
      <ul class="nav-bar">
      <li><a href="/create_account">Create Account</a></li>
      </ul>
<style>
.nav-bar{
  list-style-type: none;
  margin: 0;
  padding: 0;
}
li {
  display: inline;
  float: right
}
li a {
  display: block;
  padding: 8px;
  background-color: #dddddd;
}
</style>
<br>
<br>
<h1> Welcome to the FARMING SIMULATOR </h1>
<h2> This is the Login Page </h2>
<form method="POST" action="/home">
	<label for="text">Username</label>
	<br>
  	<input type="text" name="username" id="text" required>
	<br>
	<label for="pass">Password</label>
	<br>
  	<input type="password" name="password" id="pass" required>
  	<br>
  	<input type="submit">
</form>
<br><br><br>
<p>${message}</p>


</#assign>
<#include "main.ftl">