<#assign content>
    <div class="nav-bar">
        <p><a href="/logout"><img src="css/images/iconLeave.svg" height=40 width=40/></a></p>
    </div>
    <h1> Welcome to your set up page </h1>
    <h1> Pick your location on the Map to Set Up your farm.NONFUNCTIONAL CLICK RN </h1>
    	<button id="make_map" onClick="makeMap()">Make the Map</button>
		<button id="undisplay" onClick="closeMap()">Close the map</button>
		<button id="initial" onClick="makeInitialMap()">Make the Initial</button>
		<button id="retrieval" onClick="makeMapFromDataBase()">Get the Map From DataBase</button>
		<div id="map_table">
		</div>
</#assign>
<#include "main.ftl">