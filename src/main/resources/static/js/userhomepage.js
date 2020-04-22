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
                <p onClick={this.props.action}><a href={"#"}><img id={"delete"} src={"css/images/iconDel.svg"} height={40} width={40}/></a></p>
                <p onClick={this.props.action}><a href={"#"}><img id={"settings"} src={"css/images/iconGear.svg"} height={40} width={40}/></a></p>
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


    render() {

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
                    spritepath={"css/images/testsprite.png"}
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
        this.handleClick = this.handleClick.bind(this);
    }

    //TODO this gonna be a big boi method
    handleClick() {
        alert("you clicked me! " + this.props.row + "," + this.props.column + " active tool: " + this.props.activetool);
    }

    render() {
        return (
            <img onClick={this.handleClick} className={"tileImage"} src={this.props.spritepath}/>
        );
    }
}

class GameMap extends React.Component {
    render() {
        return (
            <p>{this.props.id}</p>
        );
    }
}

class Shop extends React.Component {
    render() {
        return (
        <div id={"friendsContainer"}>
            <p>{this.props.id}</p>
            <h1>Shop and Trade</h1>
            <button className={"add_button"} onClick={ () => openForm('myForm') }>List a Trade</button>
        </div>
        )
    }
}

class Friends extends React.Component {

    render() {
        return (
            <div id={"friendsContainer"}>
                <div className={"form-popup"} id={"myFriendList"}>
                    <p>Friends List:</p>
                    <ul id={"list_of_friends"}>
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