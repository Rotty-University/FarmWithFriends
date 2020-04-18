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
}
li a {
  display: block;
  padding: 8px;
  background-color: #dddddd;
}
</style>
<h1> CREATE YOUR ACCOUNT HERE</h1>
<form method="POST" action="/new_user">
	<label for="text">Enter Your Desired Username</label>
	<br>
  	<input type="text" name="username" id="text" required>
<br>
	<label for="pass">Enter Password</label>
	<br>
  	<input type="password" name="password" id="pass" required>
  	<br>
  	<label for="pass_re">Re-Enter Password</label>
  	<br>
  	<input type="password" name="re_password" id="pass_re" required>
  	<br>
  	<input type="submit">
</form>
<br><br><br>
<p>${create_message}</p>

</#assign>
<#include "main.ftl">