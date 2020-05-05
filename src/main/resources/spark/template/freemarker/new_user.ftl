<#assign content>
    <div class="nav-bar">
        <p><a href="/logout"><img src="css/images/iconLeave.svg" height=40 width=40/></a></p>
    </div>
    <h1> Welcome to your set up page </h1>
    <h1> Pick your location on the Map to Set Up your Farm. Click anywhere that is available to set up your DREAM FARM.</h1>
		<button id="retrieval" onClick="makeMapFromDataBase()">Click to Make Map Appear</button>
		<div id="map_table" onClick="mapClickHandler()">
		</div>
		<p id="message_for_user_on_click"></p>
</#assign>
<#include "main.ftl">