'use strict';

class Main extends React.Component {
    render() {
        return (
            <div id={"homepagecontainer"}>
                <NavBar/>
                <Game/>
            </div>
        );
    }
}

class NavBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = { active: "home" };
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
            <div className={"nav-bar"}>
                <p><a href={"#"}><img src={"css/images/iconShop.svg"} height={40} width={40}/></a></p>
                <p onClick={this.changeActive}><a href={"#"}><img id={"map"} src={"css/images/iconMap.svg"} height={40} width={40}/></a></p>
                <p><a href={"#"}><img src={"css/images/iconCoin.svg"} height={40} width={40}/></a></p>
                <p class={"nav-bar-active"} onClick={this.changeActive}><a href={"#"}><img  id={"home"} src={"css/images/iconHome.svg"} height={40} width={40}/></a></p>
                <p onClick={this.changeActive}><a href={"#"}><img id={"delete"} src={"css/images/iconDel.svg"} height={40} width={40}/></a></p>
                <p onClick={this.changeActive}><a href={"#"}><img id={"settings"} src={"css/images/iconGear.svg"} height={40} width={40}/></a></p>
                <p><a href={"/logout"}><img src={"css/images/iconLeave.svg"} height={40} width={40}/></a></p>
            </div>
        );
    }
}

'use strict';

class Game extends React.Component {
    render() {
        return (
            <div className={"content-window"}>
                <p>content window</p>
            </div>
        );
    }
}

ReactDOM.render(<Main/>, document.getElementById('nav_bar_container'));