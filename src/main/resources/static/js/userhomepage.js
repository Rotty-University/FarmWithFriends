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
                <p onClick={this.props.action}><a href={"#"}><img id={"store"} src={"css/images/iconCoin.svg"} height={40} width={40}/></a></p>
                <p class={"nav-bar-active"} onClick={this.props.action}><a href={"#"}><img  id={"home"} src={"css/images/iconHome.svg"} height={40} width={40}/></a></p>
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
        tabsMap.set("store", <Store id={"store"}/>)
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
            prevselectedtool: "",
        }
        this.updatePrevSelectedTool = this.updatePrevSelectedTool.bind(this)
        this.generateFarmArray = this.generateFarmArray.bind(this)
    }

    generateFarmArray(rows, columns) {
        return <Table id={"farmTable"} rows={rows} columns={columns}/>;
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

        let table = this.generateFarmArray(1, 4);

        return (
            <div className={"homeContainer"} onClick={this.resetTool}>
                <div className={"farmContainer"}>
                    {table}
                </div>
                <div className="toolbox">
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
                cell.push(<td key={cellID} id={cellID}><Tile type={"ploughed"} spritepath={"css/images/testsprite.png"}/></td>)
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
    }

    render() {
        return (
            <img className={"tileImage"} src={this.props.spritepath}/>
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
            <p>{this.props.id}</p>
        )
    }
}

class Store extends React.Component {
    render() {
        return (
            <p>{this.props.id}</p>
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