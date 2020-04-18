<#assign content>

<h1> Welcome to the Goop Cult </h1>
<h2> tIMDb Edition </h2>

<div id="notificationBar">
</div>
<br>

<div id="center">
<form action="javascript:void(0);">
  
  <#-- TODO: These are placeholder labels btw -->
  
  <label for="actor1">Passing from (actor name) </label>
  <textarea name="actor1" id="actor1"></textarea> 
  <label for="actor2">to (actor name) </label> 
  <textarea name="actor2" id="actor2"></textarea> 
  <br><br>
  <input type="submit" value="submit to Gwenyth Paltrow" id="connectSubmit">
</form>
  <br><br>
        
  <label for="connectOutput">Output results:</label>
  <br>
  <ul id="connectOutput" class="fancy-output">
  </ul>
  <br><br>

  <label for="loadform">Load preset data:</label>
  <form id="loadform" action="javascript:void(0);">
    <button id="loadSmall">Load small preset database</button>
    <button id="loadLarge">Load large preset database</button>
  </form>
  
  <form action="/home">
	<button type="submit">Back To Home</button>
  </form>
</div>


</#assign>
<#assign scripts>
<script src="js/timdb/timdb-connect.js"></script>
</#assign>
<#include "../main.ftl">