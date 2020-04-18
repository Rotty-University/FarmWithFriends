<#assign content>

<h1> SO YOU WANNA FIND YOUR NEIGHBOR CULT STARS? </h1>


<div id="center">
        <form method="POST" action="/neighbors_coordinates">
  <label for="numOfNeighbors1">Number of Neighbors:</label> 
  <br>
  <textarea name="numOfNeighbors1"></textarea> 
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
        

         <form method="POST" action="/neighbors_name">
  <label for="numOfNeighbors2">Number of Neighbors:</label>
  <br>
  <textarea name="numOfNeighbors2"></textarea>
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