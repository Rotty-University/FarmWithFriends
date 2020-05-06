<#assign content>
    <div class="nav-bar">
        <p><a href="/logout"><img src="css/images/iconLeave.svg" height=40 width=40/></a></p>
    </div>
    <div class="newusercontainer">
        <h1>Farm Setup</h1>
        <h2> Pick your location on the Map to Set Up your Farm.</h2>
        <button id="retrieval" onClick="makeMapFromDataBase()">Generate Map</button>
        <div id="map_table" onClick="mapClickHandler()"></div>
        <p id="message_for_user_on_click"></p>
        <div class="maplegendcreate">
            <table className="maplegendtablecreate">
                <tr>
                    <th style="width: 100px; height: 50px"><div id="blacksquare"></div></th>
                    <th style="width: 100px; height: 50px"><div id="bluesquare"></div></th>
                    <th style="width: 100px; height: 50px"><div id="yellowsquare"></div></th>
                    <th style="width: 100px; height: 50px"><div id="lightgreensquare"></div></th>
                    <th style="width: 100px; height: 50px"><div id="darkgreensquare"></div></th>
                </tr>
                <tr>
                    <th style="width: 100px; height: 50px">Occupied</th>
                    <th style="width: 100px; height: 50px">Water</th>
                    <th style="width: 100px; height: 50px">Dessert</th>
                    <th style="width: 100px; height: 50px">Grass</th>
                    <th style="width: 100px; height: 50px">Forest</th>
                </tr>
            </table>
        </div>
    </div>
</#assign>
<#include "main.ftl">