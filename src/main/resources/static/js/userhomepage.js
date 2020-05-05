'use strict';

class Main extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            active: "home"
        }
        this.changeActive = this.changeActive.bind(this)
    }

    changeActive(e) {
        var newactive = e.target.id;
        console.log(e.target.id);
        var active = document.getElementById(this.state.active);
        active = active.parentElement;
        active = active.parentElement;
        active.className = "";
        var update = document.getElementById(newactive);
        update = update.parentElement;
        update = update.parentElement;
        update.className = "nav-bar-active";
        this.setState({active: newactive});
    }

    render() {
        return (
            <div id={"homepagecontainer"}>
                <NavBar active={this.state.active} action={this.changeActive}/>
                <Game active = {this.state.active}/>
            </div>
        );
    }
}

class NavBar extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className={"nav-bar"}>
                <p onClick={this.props.action}><a href={"#"}><img id={"shop"} src={"css/images/iconShop.svg"} height={40} width={40}/></a></p>
                <p onClick={this.props.action}><a href={"#"}><img id={"map"} src={"css/images/iconMap.svg"} height={40} width={40}/></a></p>
                <Friends id={"friends"}/>
                <p className={"nav-bar-active"} onClick={this.props.action}><a href={"#"}><img  id={"home"} src={"css/images/iconHome.svg"} height={40} width={40}/></a></p>
                {/*<p onClick={this.props.action}><a href={"#"}><img id={"delete"} src={"css/images/iconDel.svg"} height={40} width={40}/></a></p>*/}
                {/*<p onClick={this.props.action}><a href={"#"}><img id={"settings"} src={"css/images/iconGear.svg"} height={40} width={40}/></a></p>*/}
                <p><a href={"/logout"}><img src={"css/images/iconLeave.svg"} height={40} width={40}/></a></p>
            </div>
        );
    }
}

class Game extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {

        let tabsMap = new Map();
        tabsMap.set("map", <GameMap id={"map"}/>)
        tabsMap.set("home", <Home id={"home"}/>)
        tabsMap.set("friends", <Friends id={"friends"}/>)
        tabsMap.set("shop", <Shop id={"shop"}/>)
        tabsMap.set("settings", <Settings id={"settings"}/>)

        return (
            <div className={"content-window"}>
                {tabsMap.get(this.props.active)}
            </div>
        );
    }
}

class Home extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            prevselectedtool: "select",
        }
        this.updatePrevSelectedTool = this.updatePrevSelectedTool.bind(this)
        this.generateFarmArray = this.generateFarmArray.bind(this)
        this.closeTheDiv = this.closeTheDiv.bind(this)
    }

    generateFarmArray(rows, columns, activetool) {
        return <Table id={"farmTable"} rows={rows} columns={columns} active={activetool}/>;
    }

    updatePrevSelectedTool(e) {
        let newTool = e.target.id;
        let current = document.getElementById(this.state.prevselectedtool);
        if (current != null) {
            current.className = "toolbaritem";
        }
        let selected = document.getElementById(newTool);
        selected.className = "toolbarSelected";
        this.setState({prevselectedtool: newTool});
    }
    closeTheDiv(){
        document.getElementById("map_viewer").innerHTML = "";
    }


    render() {
        this.closeTheDiv()
        let table = this.generateFarmArray(1, 4, this.state.prevselectedtool);
        return (
            <div className={"homeContainer"} onClick={this.resetTool}>
                <div className={"farmContainer"}>
                    {table}
                </div>
                <div className="toolbox">
                    <img className={"toolbarSelected"} onClick={this.updatePrevSelectedTool} id={"select"} src={"css/images/iconSelect.svg"} height={40} width={40}/>
                    <img className={"toolbaritem"} onClick={this.updatePrevSelectedTool} id={"plough"} src={"css/images/iconHoe.svg"} height={40} width={40}/>
                    <img className={"toolbaritem"} onClick={this.updatePrevSelectedTool} id={"plant"} src={"css/images/iconPlant.svg"} height={40} width={40}/>
                    <img className={"toolbaritem"} onClick={this.updatePrevSelectedTool} id={"water"} src={"css/images/iconWaterCan.svg"} height={40} width={40}/>
                    <img className={"toolbaritem"} onClick={this.updatePrevSelectedTool} id={"harvest"} src={"css/images/iconSickle.svg"} height={40} width={40}/>
                </div>
            </div>
        )
    }
}


class Table extends React.Component {
    constructor(props){
        super(props);
    }

    render(){
        let rows = [];
        for (var i = 0; i < this.props.rows; i++){
            let rowID = `row${i}`
            let cell = []
            for (var idx = 0; idx < this.props.columns; idx++){
                let cellID = `cell${i}-${idx}`
                cell.push(<td key={cellID} id={cellID}><Tile
                    type={"ploughed"}
                    spritepath={"css/images/testgrass.png"}
                    row={i}
                    column={idx}
                    watered={false}
                    activetool={this.props.active}
                /></td>)
            }
            rows.push(<tr key={i} id={rowID}>{cell}</tr>)
        }
        return(
            <div className="row">
                <div className="col s12 board">
                    <table id="simple-board">
                        <tbody>
                        {rows}
                        </tbody>
                    </table>
                </div>
            </div>
        )
    }
}

class Tile extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            spritepath: "css/images/landImages/unplowed.png",
            updating: false,
        }
        this.handleClick = this.handleClick.bind(this);
        this.initupdate = this.initupdate.bind(this);
        this.handleupdate = this.handleupdate.bind(this);
    }

    //TODO this gonna be a big boi method
    handleClick() {
        let toolsMap = new Map();
        toolsMap.set("select", 0);
        toolsMap.set("plough", 1);
        toolsMap.set("plant", 2);
        toolsMap.set("water", 3);
        toolsMap.set("harvest", 4);
        toolsMap.set("delete", 5);

        const dict = {row : this.props.row,
            col : this.props.column,
            action : toolsMap.get(this.props.activetool)};

        // send as parameter
        $.post("/farmland", dict, response => {
            // get result
            const result = JSON.parse(response);
            let row = (String)(this.props.row);
            let col = (String)(this.props.column);
            const thisTileInfo = result[row + "#" + col];

//            alert("row: " + row +
//                " col: " + col +
//                " isPlowed " + (String)(thisTileInfo[0]) +
//                " isWatered " + (String)(thisTileInfo[1]) +
//                " cropID: " + (String)(thisTileInfo[2]) +
//                " crop status: " + (String)(thisTileInfo[3]));
            const cropStatus = thisTileInfo[3];
            const isPlowed = thisTileInfo[0];
            const isWatered = thisTileInfo[1];
            const cropID = thisTileInfo[2];
            // update board

            //general path
            let newPath = "css/images/";
            
            if (cropStatus != -9) {
            	// show a crop (for now, until we figure out overlay)
            	newPath += "cropImages/" + (String)(cropID) + "/" + (String)(cropStatus);
            	console.log(newPath);
            } else {
            	// no crop, just show land
            	newPath += "landImages/";
            	
            	if (isPlowed == 0) {
            		// not plowed
            		newPath += "unplowed";
            	} else if (isWatered == 0) {
            		// plowed but NOT watered
            		newPath += "plowed";
            	} else {
            		// plowed AND watered
            		newPath += "watered";
            	}
            }
            
            // add file format
            newPath += ".png";

            //This is updating the visual appearance of the tile:
            this.setState({spritepath: newPath})
        });
    }

    initupdate() {
        if (!this.state.updating) {
            setInterval(this.handleupdate, 10000);
            this.setState({updating: true})
        }
    }

    handleupdate() {
        const dict = {row : this.props.row,
            col : this.props.column,
            action : 0};

        // send as parameter
        $.post("/farmland", dict, response => {
            // get result
            const result = JSON.parse(response);
            let row = (String)(this.props.row);
            let col = (String)(this.props.column);
            const thisTileInfo = result[row + "#" + col];
            const cropStatus = thisTileInfo[3];
            const isPlowed = thisTileInfo[0];
            const isWatered = thisTileInfo[1];
            const cropID = thisTileInfo[2];
            // update board

            //general path
            let newPath = "css/images/";

            if (cropStatus != -9) {
                // show a crop (for now, until we figure out overlay)
                newPath += "cropImages/" + (String)(cropID) + "/" + (String)(cropStatus);
                console.log(newPath);
            } else {
                // no crop, just show land
                newPath += "landImages/";

                if (isPlowed == 0) {
                    // not plowed
                    newPath += "unplowed";
                } else if (isWatered == 0) {
                    // plowed but NOT watered
                    newPath += "plowed";
                } else {
                    // plowed AND watered
                    newPath += "watered";
                }
            }

            // add file format
            newPath += ".png";

            //This is updating the visual appearance of the tile:
            this.setState({spritepath: newPath})
        });
    }

    render() {
        this.initupdate()
    	// needs to re render all farm tiles
        return (
            <img onClick={this.handleClick} className={"tileImage"} src={this.state.spritepath}/>
        );
    }
}

class GameMap extends React.Component {
        constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
    }
        handleClick() {
        var total_x = 20; //Total width
        var total_y = 20; // Total height
        var total_elements = total_x * total_y; //Total of elements in the matrix
        // var map = createArray(total_x, total_y);
        var map_empty = [];
        var basic_elements = [];
        var tolerance = 10; // Number of consecutive blocks of the same type to make it right
        var allow_multiples_seeds = true; //If this is set up to true, once we cannot continue expanding a current seed, we are going to generate a new one.
        var total_options = [];
        let dictionaryy = {};
        //this dictionary will be set from the the getting map from database so we can use it in the clickhandler. 
        let map_information= {};
        //will be used to count the number of total free spaces available through subtraction with total. 
        let waterSpaceCount = {};
        document.getElementById("map_viewer").innerHTML = "";
        for(let x = 0 ; x < total_x ; x++){
            var extra = '';
            extra += '<div class="row">';
            for(let y = 0 ; y < total_y ; y++){
                extra += '<div class="col element empty" x="' + (x+1) + '" y="' + (y+1) + '" id="spacee_' + (x+1) + '-' + (y+1) + '"></div>';
                map_empty.push((x+1)+","+(y+1));
            }
            extra += '</div>';
            $("#map_viewer").append(extra);
        }
        $.get("/mapRetrieverForMapsComponent", response => {
        const object = JSON.parse(response);
        let map_dictionary_with_objectlocations = JSON.parse(object.data);
        let row = object.row;
        let col = object.col;
        map_information = map_dictionary_with_objectlocations;
        map_dictionary_with_objectlocations[row+","+col][2] = "white_space";
            for(let x = 1; x<total_x+1;x++){
                for(let y = 1; y<total_y+1;y++){
                    changeElementTypee(map_dictionary_with_objectlocations[x.toString()+","+y.toString()][0],map_dictionary_with_objectlocations[x.toString()+","+y.toString()][1],map_dictionary_with_objectlocations[x.toString()+","+y.toString()][2]);
                }
            }
        document.getElementById("map_viewer").style.display = "block";
        });   
    }
    changeElementTypee(x,y,cl){ 
        selector = "#spacee_"+x+'-'+y;
        var element = $(selector);
        $.each(basic_elements, function(index,value){
            element.removeClass(value.class);
        });
        element.removeClass('empty').addClass('selected').addClass(cl);
    }
    render() {
        this.handleClick()
        return (

            <div>
            <button onClick={ () => showMapp() } >Click to Show the Map your on</button>
            <p>Your farm location is the white space</p>
            </div>
            
        );

    }
}

class Shop extends React.Component {
    render() {
        return (
            <p>{this.props.id}</p>
        )
    }
}

class Friends extends React.Component {

    render() {
        document.getElementById("map_viewer").innerHTML = "";
        return (
            <div id={"friendsContainer"}>
                <div className={"form-popup"} id={"myFriendList"}>
                    <h1 id={"title_of_friends_list"}>Friends List</h1>
                    <ul id={"list_of_friends"}>
                    </ul>
                    <p className={"color_of_text"}>These are your pending requests below</p>
                    <p className={"color_of_text"}>Click on a name to accept the user</p>
                    <ul id={"list_of_friends_pending"}>
                    </ul>
                    <button className={"friend_button"} onClick={ () => openForm('myForm') }>Add Friends</button>
                    <button type={"button"} className={"btnn cancel"} onClick={ () => closeForm('myFriendList') }>Close</button>
                </div>
                <p onClick={ () => openFriendList('myFriendList') }><a href={"#"}><img id={"friends"} src={"css/images/iconFriends.svg"} height={40} width={40}/></a></p>
                <div className={"form-popup"} id={"myForm"}>
                    <h1>Add A Friend</h1>
                    <label htmlFor={"friend_username"}><b>Username of Friend</b></label>
                    <input type={"text"} id={"addfriendstext"} placeholder={"Enter username of player"} name={"friend_username"}required></input>
                    <p id={"message_for_friend_status"}></p>
                    <button type={"button"} className={"btnn"} id={"add_friend_button"} onClick={ () => sendAddRequest() }>Send Request</button>
                    <button type={"button"} className={"btnn cancel"} onClick={ () => closeAddForm('myForm') }>Close</button>
                </div>

            </div>
        )
    }
}

class Settings extends React.Component {
    render() {
        return (
            <p>{this.props.id}</p>
        )
    }
}

ReactDOM.render(<Main/>, document.getElementById('nav_bar_container'));