<#assign content>

<h1> LOOKING FOR ALL GOOP CULT STARS IN YOUR AREA? </h1>

<div id="center">
        <form method="POST" action="/radius_coordinates">
  <label for="radius1">Radius:</label> 
  <br>
  <textarea name="radius1"></textarea> 
  <br><br>
  <label for="text">X: </label>
  <textarea name="X"></textarea> 
  <label for="text">Y: </label> 
  <textarea name="Y"></textarea> 
  <label for="text">Z: </label> 
  <textarea name="Z"></textarea>
  <br><br>
  <input type="submit" value="submit to Gwenyth Paltrow">
  </form>
  <br><br>
        

         <form method="POST" action="/radius_name">
  <label for="radius2">Radius:</label>
  <br>
  <textarea name="radius2"></textarea>
  <br><br>
  <label for="text">Target star's name: </label>
  <br>
  <textarea name="name"></textarea> 
  <br><br>
  <input type="submit" value="submit to Gwenyth Paltrow">
  </form>
  <br><br>

        
    <label for="output">Output results:</label>
    <br>

	<textarea readonly rows=5 cols=30 id="output">
	${results}
	</textarea>
	<br><br>
	
	<form action="/stars">
		<button type="submit">Back</button>
	</form>
        
    </div>


</#assign>
<#include "../main.ftl">