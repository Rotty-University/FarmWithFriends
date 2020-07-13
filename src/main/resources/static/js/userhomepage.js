'use strict';

class Main extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            active: "home",
            currentUserName: "default"
        }
        this.changeActive = this.changeActive.bind(this)
        
        window.mainComponent = this;
    }
    
    getCurrentUsername() {
    	return this.state.currentUserName;
    }
    
    componentDidMount() {        
        // assign the current username and pass it down to children
        $.get("/currentUserName").done(function(response) {
        	this.setState({currentUserName: JSON.parse(response)});
        }.bind(this));
    	
        console.log("mounted that bitch");
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
    
    goToFarm() {
        var newactive = "home";
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
                <Game active = {this.state.active} currentUserName={this.state.currentUserName}/>
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
                {<p onClick={this.props.action}><a href={"#"}><img id={"delete"} src={"css/images/iconDel.svg"} height={40} width={40}/></a></p>}
                {/*<p onClick={this.props.action}><a href={"#"}><img id={"settings"} src={"css/images/iconGear.svg"} height={40} width={40}/></a></p>*/}
                <p><a href={"/logout"}><img src={"css/images/iconLeave.svg"} height={40} width={40}/></a></p>
            </div>
        )
    }
}

class Game extends React.Component {

    constructor(props) {
        super(props);

        this.tabsMap = new Map();
    }

    render() {
        this.tabsMap.set("map", <GameMap id={"map"} currentUserName={this.props.currentUserName}/>);
        this.tabsMap.set("home", <Home id={"home"} currentUserName={this.props.currentUserName}/>);
        this.tabsMap.set("friends", <Friends id={"friends"} currentUserName={this.props.currentUserName}/>);
        this.tabsMap.set("shop", <Shop id={"shop"} currentUserName={this.props.currentUserName}/>);
        this.tabsMap.set("settings", <Settings id={"settings"} currentUserName={this.props.currentUserName}/>);
        this.tabsMap.set("store", <Store id={"store"} currentUserName={this.props.currentUserName}/>);

        return (
            <div className={"content-window"}>
                {this.tabsMap.get(this.props.active)}
            </div>
        );
    }
}

class Inventory extends React.Component {
	constructor(props) {
		super(props);
		
		this.hide = this.hide.bind(this);
	}
    
    hide() {
    	document.getElementById("inventoryWindow").style.display="none";
    }
	
	render() {
//<DropSlot id="tool1" className={"toolSlot"}> <DragItem className={"toolbaritem"} id={"defaultPlough"} type={"plough"} onClick={this.updatePrevSelectedTool}> <img src={"css/images/iconHoe.svg"} height={40} width={40}/> </DragItem> </DropSlot>		
		
		return (
				<div className={"inventoryWindow"} id={"inventoryWindow"}>
					<div className={"inventoryBox"} id={"inventoryBox"}>
					{this.props.children}
					</div>
					<button className={"closeInventory"} onClick={this.hide}> close inventory </button>
				</div>
	            );
	}
}

class ToolBox extends React.Component {
	constructor(props)
	{
		super(props);
	}

	render() {
		return(
				<div id="toolBoxWindow">
				<div className="toolbox" id="toolBox">
				{this.props.children}
				</div>

				<button className={"showInventory"} onClick={this.props.showInventory}> show inventory </button>
				</div>
		);
	}
}

class Home extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            prevSelectedToolType: "select",
            prevSelectedToolID: "",
            shortcutTools: [],
            inventoryInfo: []
        }
        this.updatePrevSelectedTool = this.updatePrevSelectedTool.bind(this)
        this.generateFarmArray = this.generateFarmArray.bind(this)
        this.closeTheDiv = this.closeTheDiv.bind(this)
        
        this.showInventory = this.showInventory.bind(this);
        this.swapItems = this.swapItems.bind(this);
    }

    generateFarmArray(rows, columns, activeToolType, activeToolID) {
        return <Table id={"farmTable"} rows={rows} columns={columns} activeToolType={activeToolType} activeToolID={activeToolID} currentUserName={this.props.currentUserName}/>;
    }

    updatePrevSelectedTool(e) {
        let selected = e.target;
        // if the img is selected, then set back to the parent DragItem
        // we can NOT select the img here because the tool info is in DragItem
        if (selected.nodeName == "IMG") {
        	selected = selected.parentElement;
        }
        
    	// tool-type represents type of action while id represents exactly the tool/seed's name
        const newToolType = selected.getAttribute("data-tool-type");
        const newToolID = selected.id;
//        console.log(newToolType);
//        console.log(newToolID);
        
        // remove the highlight on the tool selected before by changing style
        let current = document.getElementById(this.state.prevSelectedToolID);
        if (current != null) {
            current.className = "toolbaritem";
        }
        
        // select the tool by highlighting
        selected.className = "toolbarSelected";
        this.setState({
						prevSelectedToolID: newToolID,
        				prevSelectedToolType: newToolType
        			   });
        
    }
    
    closeTheDiv(){
        document.getElementById("map_viewer").innerHTML = "";
//        document.getElementById("message_for_clicking_on_map").innerHTML = "";
    }
    
    showInventory() {

    	$.get("/loadUserInventorySize").done(function(response1) {  
    		this.inventorySize = JSON.parse(response1);

    		$.get("/currentUserInventory").done(function(response2) {        		
    			const itemMap = JSON.parse(response2);

//    			for (var key in itemMap) {
//    				console.log(key)
//    			}

    			// init the inventory box
    			const inventoryBox = [];
    			// init counter for number of items we've looped through
    			let count = 0;

    			for (var key in itemMap) {
    				if (count >= this.inventorySize) {
    					break;
    				}

    				const itemInfo = [];
    				itemInfo.push("true");
    				itemInfo.push("toolbaritem");
    				itemInfo.push(key);
    				itemInfo.push(itemMap[key][0]);
    				itemInfo.push(itemMap[key][1]);

    				const thisSlot = <DropSlot key={count} itemInfo={itemInfo} className={"inventorySlot"} 
    				id={count} handleItemClick={this.updatePrevSelectedTool} 
    				swapItems={this.swapItems}/>;

    				inventoryBox.push(thisSlot);

    				count++;
    			}

    			for (count; count<this.inventorySize; count++) {
    				// just create a slot with no item
    				const itemInfo = [];
    				itemInfo.push("false");
    				itemInfo.push(null);
    				itemInfo.push(null);
    				itemInfo.push(null);
    				itemInfo.push(null);

    				const thisSlot = <DropSlot key={count} itemInfo={itemInfo} className={"inventorySlot"} id={count}
    				handleItemClick={this.updatePrevSelectedTool} swapItems={this.swapItems}/>;
    				
    				inventoryBox.push(thisSlot);
    			}

    			this.setState({inventoryInfo: inventoryBox});

    			document.getElementById("inventoryWindow").style.display="inline";

    		}.bind(this));
    	}.bind(this));
    }
    
    swapItems(slotClass1, slotKey1, itemInfo1, slotClass2, slotKey2, itemInfo2) {
    	let currToolBox = this.state.shortcutTools;
    	let currInventory = this.state.inventoryInfo;
    	
    	// update slot1
    	let newSlot1 = null;
    	if (slotClass1 == "toolSlot") {
    		newSlot1 = <DropSlot key={slotKey1} itemInfo={itemInfo2} className={"toolSlot"} id={slotKey1}
    		handleItemClick={this.updatePrevSelectedTool} swapItems={this.swapItems}/>;
    				
        	currToolBox[slotKey1] = newSlot1;
    	} else {
    		newSlot1 = <DropSlot key={slotKey1} itemInfo={itemInfo2} className={"inventorySlot"} id={slotKey1}
    		handleItemClick={this.updatePrevSelectedTool} swapItems={this.swapItems}/>;
        	
    		currInventory[slotKey1] = newSlot1;
    	}
    	
    	// update slot2
    	let newSlot2 = null;
    	if (slotClass2 == "toolSlot") {
    		newSlot2 = <DropSlot key={slotKey2} itemInfo={itemInfo1} className={"toolSlot"} id={slotKey2}
    		handleItemClick={this.updatePrevSelectedTool} swapItems={this.swapItems}/>;
        	
    		currToolBox[slotKey2] = newSlot2;
    	} else {
    		newSlot2 = <DropSlot key={slotKey2} itemInfo={itemInfo1} className={"inventorySlot"} id={slotKey2}
    		handleItemClick={this.updatePrevSelectedTool} swapItems={this.swapItems}/>;
        	
    		currInventory[slotKey2] = newSlot2;
    	}
    	
    	console.log("Class1: " + slotClass1, "key1: " + slotKey1, "item1: " + itemInfo1);
    	console.log("Class2: " + slotClass2, "key2: " + slotKey2, "item2: " + itemInfo2);
    	
    	// set states
    	this.setState(
    			{
    				shortcutTools: currToolBox,
    				inventoryInfo: currInventory
    			});
    }

    componentDidMount() {
    	$.get("/shortcutTools").done(function(response) {
    		const toolNames = JSON.parse(response);
    		const toolBoxReturnValues = [];

    		// example toolSlot: <DropSlot id="tool1" className={"toolSlot"}/>
    		for (var i=0; i<toolNames.length; i++) {
    			const toolType = toolNames[i][0];
    			const toolName = toolNames[i][1];

    			const itemInfo = [];
    			if (toolType === "" || toolName === "") {
        			// no item here
        			itemInfo.push("false");
        			itemInfo.push(null);
        			itemInfo.push(null);
        			itemInfo.push(null);
        			itemInfo.push(null);
        		} else {
        			// push item info here
        			itemInfo.push("true");
        			itemInfo.push("toolbaritem");
        			itemInfo.push(toolName);
        			itemInfo.push(toolType);
        			// TODO: place holder, add in amount later
        			itemInfo.push(null);
        		}
        		
        		const thisSlot = <DropSlot key={i} itemInfo={itemInfo} className={"toolSlot"} 
        		id={i} handleItemClick={this.updatePrevSelectedTool} swapItems={this.swapItems}/>;
        	
        		toolBoxReturnValues.push(thisSlot);
        	}
    		
        	this.setState({shortcutTools: toolBoxReturnValues});
        }.bind(this));
    }

    render() {
        this.closeTheDiv()
        let table = this.generateFarmArray(12, 20, this.state.prevSelectedToolType, this.state.prevSelectedToolID);
        return (
            <div className={"homeContainer"} onClick={this.resetTool}>
                <div className={"farmContainer"}>
                    {table}
                </div>    
                <Inventory children={this.state.inventoryInfo} currentUserName={this.props.currentUserName}/>
                <ToolBox children={this.state.shortcutTools} showInventory={this.showInventory}/>
            </div>
        )
    }
}


class Table extends React.Component {
    constructor(props){
        super(props);
        
        // bind functions
        this.updateTiles = this.updateTiles.bind(this);
        
        // set up array of default sprite paths
        this.spritePaths = [];
        for (var i = 0; i < this.props.rows; i++) {
        	let thisRow = [];
        	for (var j = 0; j < this.props.columns; j++) {
        		thisRow.push("css/images/landImages/unplowed.png");
        	}
        	this.spritePaths.push(thisRow);
        }
        
        // set up state corresponding to each child tile component
        this.state = {
        		spritePaths: this.spritePaths
        };
        
        // set up action map for handleClick()
        this.actionMap = new Map();
        this.actionMap.set("select", 0);
        this.actionMap.set("plow", 1);
        this.actionMap.set("seeds", 2);
        this.actionMap.set("water", 3);
        this.actionMap.set("harvest", 4);
        this.actionMap.set("steal", 5);
        this.actionMap.set("cure", 6);
        
        // set up timer to constantly update
        setInterval(this.updateTiles, 500);
    }
    
    updateTiles() {    	
    	if (this.props.currentUserName === "default") {
    		return;
    	}

    	// send as parameter
    	$.post("/farmUpdates/" + (String)(this.props.currentUserName), response => {
    		// get result
    		const result = JSON.parse(response);

    		// update board
    		let newSpritePaths = [];
    		for (var i = 0; i < this.props.rows; i++) {                    
    			// update this tile's sprite path
    			let thisRow = [];
    			for (var j = 0; j < this.props.columns; j++) {
    				// get this tile's info
    				let row = (String)(i);
    				let col = (String)(j);
    				const thisTileInfo = result[row + "#" + col];

    				const isPlowed = thisTileInfo[0];
    				const isWatered = thisTileInfo[1];
    				const cropID = thisTileInfo[2];
    				const cropStatus = thisTileInfo[3];

    				//general path
    				let newPath = "css/images/";

    				if (cropStatus != -9) {
    					// show a crop (for now, until we figure out overlay)
    					newPath += "cropImages/" + (String)(cropID) + "/" + (String)(cropStatus);
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
    				thisRow.push(newPath);
    			}

    			newSpritePaths.push(thisRow);
    		}

    		this.setState({spritePaths: newSpritePaths});
    	});
    }

    render(){
        let rows = [];
        for (var i = 0; i < this.props.rows; i++){
            let rowID = `row${i}`
            let cell = []
            for (var idx = 0; idx < this.props.columns; idx++){
                let cellID = `cell${i}-${idx}`
                cell.push(<td key={cellID} id={cellID}><Tile
                    spritepath={this.state.spritePaths[i][idx]}
                    row={i}
                    column={idx}
                	tileId={i*this.props.columns + idx}
                    activeToolType={this.props.activeToolType}
                	activeToolID={this.props.activeToolID}
                	actionMap = {this.actionMap}
                	currentUserName = {this.props.currentUserName}
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
            spritepath: props.spritepath
        }
        
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick() {

        const dict = {
        	row : this.props.row,
            col : this.props.column,
            action : this.props.actionMap.get(this.props.activeToolType),
            //TODO: change the crop name here once the front end selection is set up
            crop : this.props.activeToolID,
            //TODO: change the water duration here
            waterDuration : 10
        };

        // send as parameter
        $.post("/farmActions/" + (String)(this.props.currentUserName), dict, response => {
            // get result
            const thisTileInfo = JSON.parse(response);
            
            // illegal action, return immediately
            if (thisTileInfo == 0) {
            	Swal.fire({
            		  title: 'Trespassing!',
            		  text: "Can't do that when you are not the farm owner",
            		  icon: 'error',
            		  confirmButtonText: "Sorry, I'm just walking around",
                	  allowOutsideClick: false
            		});
            	
            	return;
            }
            
            const isPlowed = thisTileInfo[0];
            const isWatered = thisTileInfo[1];
            const cropID = thisTileInfo[2];
            const cropStatus = thisTileInfo[3];
            // update board

            //general path
            let newPath = "css/images/";
            
            if (cropStatus != -9) {
            	// show a crop (for now, until we figure out overlay)
            	newPath += "cropImages/" + (String)(cropID) + "/" + (String)(cropStatus);
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
            
            // play particle effect
            this.pop();
            
            // alert to show the result of steal
            const stealStatus = thisTileInfo[5];
        	// always check this first because most operations are not steal
            if (stealStatus == -100) {
            	return;
            }

            let stealTitle = "Failed to steal";
            let stealMessage = "";
            let stealIcon = "error";
            let stealButtonMessage = "ugh, fine";
            switch (stealStatus) {
			case -1:
				stealMessage = "Can't do that: you ARE the owner";
				break;
			case 0:
				stealMessage = "You are too late, this crop has been stolen too many times";
				break;
			case -2:
				stealMessage = "Nothing to steal here";
				break;
			case -3:
				stealMessage = "Can't steal withered crop";
				break;
			case -4:
				stealMessage = "Crop cannot be harvested yet, come back to steal later";
				break;
			case -5:
				stealMessage = "You already stole once, don't be greedy";
				break;
			case 100:
				stealTitle = "HOLY SMOKE"
	            stealIcon = "warning";
	            stealMessage = "YOU STOLE EVERYTHING!!! (this only happens 1% of the time)";
	            stealButtonMessage = "I knew I was the 1%";
	            break;
			default:
				stealTitle = "Hehe...";
        		stealIcon = "success";
        		stealMessage = "You successfully stole " + stealStatus + " from the owner";
        		stealButtonMessage = "I'll fuckin do it again";
				break;
			}
            
        	Swal.fire({
        		  title: stealTitle,
        		  text: stealMessage,
        		  icon: stealIcon,
        		  confirmButtonText: stealButtonMessage,
            	  allowOutsideClick: false
        		});            
        });
    }
    
    // ******************************************
    // *for popping particle effect when clicked*
    // ******************************************

    pop() {
    	const rect = document.getElementById("tile" + this.props.tileId).getBoundingClientRect();
    	const centerX = rect.left;
    	const centerY = rect.top;

    	for (let i = 0; i < 70; i++) {
    		this.createParticle(centerX, centerY);
    	}
    }
    
    createParticle(x, y) {
    	const particle = document.createElement("particle");
    	document.body.appendChild(particle);
    	
    	const size = Math.floor(Math.random() * 50 + 5);
    	// Apply the size on each particle
    	particle.style.width = `${size}px`;
    	particle.style.height = `${size}px`;
    	// Generate a random color in a blue/purple palette
//    	particle.style.background = `hsl(${Math.random() * 90 + 180}, 70%, 60%)`;
    	// set image for this particle
    	const chance = Math.floor(Math.random() * Math.floor(100));
    	let backgroundImagePath = null;
    	if (chance >= 60) {
    		backgroundImagePath = 'url("css/images/particleImages/rock/small.png")';
    	} else if (chance >= 35) {
    		backgroundImagePath = 'url("css/images/particleImages/rock/lightBrown.png")';
    	} else if (chance >= 10) {
    		backgroundImagePath = 'url("css/images/particleImages/rock/darkBrown.png")';
		} else {
			backgroundImagePath = 'url("css/images/particleImages/rock/grey.png")';
		}
    	particle.style.backgroundImage = backgroundImagePath;
//    	particle.style.backgroundImage = 'url("css/images/particleImages/rock/darkBrown.png")';
    	
    	// Generate a random x & y destination within a distance of 75px from the mouse
    	const destinationX = x + (Math.random() - 0.5) * 2 * 75;
//    	const destinationY = y + (Math.random() - 0.5) * 2 * 75;
    	const destinationY = y - Math.random() * 50;
    	
    	// GSAP implementation
    	gsap.fromTo(particle, {opacity: 1, x: x, y: y}, 
    			{opacity: 0.8, x: destinationX, y: destinationY, 
    		duration: 0.3 + Math.random() * 0.8, delay: Math.random() * 0.1, 
    		ease: CustomEase.create("custom", "M0,0 C0,0.9 0.57,1 1,1"), 
    		onComplete: function(){particle.remove()}});
    }
    
    // --------------------------------------------------------------------------------------
    
    componentDidUpdate(prevProps) {
    	if (this.props.spritepath !== prevProps.spritepath) {
    	  this.setState({spritepath: this.props.spritepath});
    	}
    }

    render() {
        return (
            <img id={"tile" + this.props.tileId} onClick={this.handleClick} className={"tileImage"} src={this.state.spritepath}/>
        );
    }
}

function clickingForFriends(){
    let map_viewer = $("#map_viewer");
//     document.getElementById("message_for_clicking_on_map").innerHTML = "";
//        console.log(event.pageX);
//        console.log(event.pageY);
        //storing the map object
//        console.log(map_viewer.offset());
        //calculating the row and column values.
        const col_val = event.pageX - map_viewer.offset().left;
        const row_val = event.pageY - map_viewer.offset().top;
        //Getting the indices of the row and column.
        let row_num = Math.floor(row_val/30);
        let col_num = Math.floor(col_val/30);
        //if it isnt friend space don't do anything. 
//        console.log(map_information);
        
        // legacy code: before we implemented hover display of friend's name
//        if(map_information[(row_num+1)+','+(col_num+1)][2] === 'white_space' ){
//            document.getElementById("message_for_clicking_on_map").innerHTML = "";
//            document.getElementById("message_for_clicking_on_map").innerHTML = "Your own farm";
//            document.getElementById("message_for_clicking_on_map").style.paddingLeft = "30px";
//            document.getElementById("message_for_clicking_on_map").style.paddingRight = "30px";
//        }

        //the space is valid and we can output which friend they clicked on. 
        if(map_information[(row_num+1)+','+(col_num+1)][2] === 'white_space' || map_information[(row_num+1)+','+(col_num+1)][2] === 'friend_space' ){
        	//sending  a post request with the row and column to get the username based off this row and column and same map.
            const postParameters = {
                row: (row_num+1),
                col: (col_num+1),
            }
            //sending a post request to the showingWhatFriendwasclicked
            $.post("/showingWhatFriendWasClicked", postParameters, response =>{
                //the response has the name in it. 
                const object  = JSON.parse(response);
                const name = object.name;

                // redirect to farm
                window.mainComponent.goToFarm();
                
                Swal.fire({
                	  text: "Welcome to " + name + "'s farm, play nice and watch out for coyos",
                	  icon: "info",
                	  confirmButtonText: "Nobody is afraid of a coyo",
                	  allowOutsideClick: false
                	});
//                document.getElementById("message_for_clicking_on_map").innerHTML = name +"'s farm";
//                document.getElementById("message_for_clicking_on_map").style.paddingLeft = "30px";
//                document.getElementById("message_for_clicking_on_map").style.paddingRight = "30px";
                // document.getElementById("message_for_clicking_on_map").onclick = function () {
                    //load and show friends farm here
                    // let viewer = React.createElement(FriendHomeViewer);
                    // console.log("here")
                    // ReactDOM.render(viewer, document.getElementById('map_message_container'))
                // };
            });
            
        }
        else{
//        	document.getElementById("message_for_clicking_on_map").innerHTML = "";
//            document.getElementById("message_for_clicking_on_map").style.paddingLeft = "0px";
//            document.getElementById("message_for_clicking_on_map").style.paddingRight = "0px";
        	Swal.fire({
          	  text: "Nobody has set up a farm there yet, invite your friends to move here!",
          	  icon: "question",
          	  confirmButtonText: "Share with my friends NOW!",
          	  showCancelButton: true,
          	  cancelButtonText: "Not now"
          	}).then((result) => {
          	  if (result.value) {
          	    Swal.fire(
          	      'Not yet!',
          	      'You are playing this game in beta, consider yourself privileged',
          	      'warning'
          	    )
          	  }
          	});
        }
}

function setMapVar(mapinfo){
    map_information = mapinfo;
}

class GameMap extends React.Component {
        constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
        this.fillMap();
    }
        
        fillMap() {
        	var total_x = 25; //Total width
            var total_y = 25; // Total height
            var total_elements = total_x * total_y; //Total of elements in the matrix
            // var map = createArray(total_x, total_y);
            var map_empty = [];
            var basic_elements = [];
            var tolerance = 10; // Number of consecutive blocks of the same type to make it right
            var allow_multiples_seeds = true; //If this is set up to true, once we cannot continue expanding a current seed, we are going to generate a new one.
            var total_options = [];
            let dictionaryy = {};
            //this dictionary will be set from the the getting map from database so we can use it in the clickhandler. 
            // let map_information= {};
            //will be used to count the number of total free spaces available through subtraction with total. 
            let waterSpaceCount = {};
            document.getElementById("map_viewer").innerHTML = "";
            for(let x = 0 ; x < 20 ; x++){
                var extra = '';
                extra += '<div class="map_row">';
                for(let y = 0 ; y < 20 ; y++){
                    extra += '<div class="map_col elementTab empty" id="spacee_' + (x+1) + '-' + (y+1) + '"></div>';
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
            let friends_map = JSON.parse(object.friends)
//            console.log(friends_map);
            // map_information = map_dictionary_with_objectlocations;
            map_dictionary_with_objectlocations[row+","+col][2] = "white_space";
            $("#spacee_" + row + "-" + col).append("<span class='friend_pop_up'>" +  
                    "Your own farm" +  
                    "</span>");
                for(let x = 1; x<20+1;x++){
                    for(let y = 1; y<20+1;y++){
                    	const xString = x.toString();
                    	const yString = y.toString();
                    	const friendName = friends_map[xString+","+yString];
                    	
                        if(friendName != undefined || friendName != null){
                            map_dictionary_with_objectlocations[xString+","+yString][2] = "friend_space";
                            changeElementTypee(map_dictionary_with_objectlocations[xString+","+yString][0],map_dictionary_with_objectlocations[xString+","+yString][1],"friend_space");
                            // add a pop up to show name of the friend
                            const spanText = "<span class='friend_pop_up'>" +  
                            friendName + "'s farm" +  
                            "</span>";
                            $("#spacee_" + xString + "-" + yString).append(spanText);
                        }else{
                            changeElementTypee(map_dictionary_with_objectlocations[xString+","+yString][0],map_dictionary_with_objectlocations[xString+","+yString][1],map_dictionary_with_objectlocations[xString+","+yString][2]);

                        }
                    }
                }
            document.getElementById("map_viewer").style.display = "block";
            // map_information = map_dictionary_with_objectlocations;
            setMapVar(map_dictionary_with_objectlocations);
            });   
        }
        
        handleClick() {
        	
        }

    render() {
        return (
            <div id={"mapContainer"}>
                <div className="maplegend">
                    <table className={"maplegendtable"}>
                        <tr>
                            <th><div id={"redsquare"}></div></th>
                            <th><div id={"blacksquare"}></div></th>
                            <th><div id={"bluesquare"}></div></th>
                            <th><div id={"yellowsquare"}></div></th>
                            <th><div id={"lightgreensquare"}></div></th>
                            <th><div id={"darkgreensquare"}></div></th>
                            <th><div id={"purplesquare"}></div></th>
                        </tr>
                        <tr>
                            <th>You</th>
                            <th>Occupied</th>
                            <th>Water</th>
                            <th>Dessert</th>
                            <th>Grass</th>
                            <th>Forest</th>
                            <th>Friend</th>
                        </tr>
                    </table>
                    <p>Click On the Friend's Location to Visit their farms</p>
                </div>
                
            </div>
        );

    }
}

class Shop extends React.Component {
    render() {
        return (
        <div id={"shopContainer"} className={"shopContainer"}>
            <div id={"toptoolbar"}>
                <h1>Marketplace</h1>
                <button className={"add_button"} onClick={ () => openTradeForm() }>New Trade</button>
                {/*<p>Trade List:</p>*/}
                <div className={"shop_form"} id={"newTrade"}>
                    <h1>New Trade</h1>
                    <label htmlFor={"sell_id"}><b>Crop to Trade</b></label>
                    <label htmlFor="sell_quantity "><b>Quantity (between 1 and 5)</b>:</label>
                    <p><input type="number" id={"sell_quantity"} name="sell_quantity" min="1" max="5"></input><select id={"sell_id"} required></select></p>
                    <br></br>
                    <label htmlFor={"buy_id"}><b>Crop Requested</b></label>
                    <label htmlFor="buy_quantity "><b>Quantity (between 1 and 5)</b>:</label>
                    <p><input type="number" id={"buy_quantity"} name="buy_quantity" min="1" max="5"></input><select id={"buy_id"} required></select></p>
                    <p><button type={"button"} className={"btnn"} id={"post_trade_button"} onClick={ () => addTradeListing() }>Post Trade</button></p>
                    <p><button type={"button"} id={"shopcancel"} onClick={ () => closeAddForm('newTrade') }>Close</button></p>
                </div>
            </div>
            <div className="grid-container">
                <div className="grid-item">
                    <button onClick={() => openInventory() }>Refresh Inventory</button>
                    <table id={"inventory"} width={300}>
                    </table>
                </div>
                <div className="grid-item">
                    <button onClick={() => openTradeList() }>Refresh Trades</button>
                    <table id={"trade_list"} width={1000}>
                    </table>
                </div>
            </div>
        </div>
        )
    }
}

function makeTrade(trade_data) {
    console.log("works :)")
    const postParameters = {
        //TODO: get the text inside the input box
        data: trade_data
    };
    $.post("/acceptTrade", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        //showing a list of the friends when the button is clicked.
        window.alert(object.message);
        this.openTradeList()
    });
};

function openTradeForm() {
    document.getElementById('newTrade').style.display = "block";
    const inventory = $("#sell_id");
    const availableCrops = $("#buy_id");
    inventory.empty();
    availableCrops.empty();
    const postParameters = {
        //TODO: get the text inside the input box
        text: "placeholder"
    };
    $.post("/retrieve_sell", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        //showing a list of the friends when the button is clicked.
        inventory.append(object.list1);
        availableCrops.append(object.list2);
    });
};

function openTradeList(){
    const suggestionList = $("#trade_list");
    suggestionList.empty();
    const postParameters = {
        //TODO: get the text inside the input box
        text: "placeholder"
    };
    $.post("/tradeLoader", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        //showing a list of the friends when the button is clicked.
        suggestionList.append(object.list);
    });
};

function openInventory(){
    const suggestionList = $("#inventory");
    suggestionList.empty();
    const postParameters = {
        //TODO: get the text inside the input box
        text: "placeholder"
    };
    $.post("/inventoryLoader", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        //showing a list of the friends when the button is clicked.
        suggestionList.append(object.list);
    });
};

function addTradeListing() {
    const submit = $("#post_trade_button");
    console.log(submit.innerHTML);
    const cropS = $("#sell_id");
    const quantS = $("#sell_quantity");
    const cropB = $("#buy_id");
    const quantB = $("#buy_quantity");
    const postParameters = {
        //TODO: get the text inside the input box
        cSell: cropS.val(),
        qSell: quantS.val(),
        cBuy: cropB.val(),
        qBuy: quantB.val()
    };
    console.log(postParameters.text);
    //send the post and show the message from the backend.
    $.post("/posting_trade", postParameters, response => {
        // Do something with the response here
        const object = JSON.parse(response);
        window.alert(object.message);
    });
    document.getElementById("newTrade").style.display = "none";
};

class Friends extends React.Component {

    render() {
        document.getElementById("map_viewer").innerHTML = "";
        return (
            <div id={"friendsContainer"}>
                <div className={"form-popup"} id={"myFriendList"}>
                    <h1 id={"title_of_friends_list"}>Friends List</h1>
                    <ul id={"list_of_friends"}>
                    </ul>
                    <p className={"color_of_text"}>Pending Requests:</p>
                    {/*<p className={"color_of_text"}>Click on a name to accept the user</p>*/}
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
                    
// *******
// *Store*
// *******

class Store extends React.Component {
	render() {
        return (
            <p>{this.props.id}</p>
        )
    }
}
// ---------------------------------------

ReactDOM.render(<Main/>, document.getElementById('nav_bar_container'));