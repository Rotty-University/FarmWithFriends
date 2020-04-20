'use strict';

class NavBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = { active: "home" };
    }

    render() {
        if (this.state.liked) {
            return 'You liked this.';
        }

        return (
            <div className={"nav-bar"}>
                <p><a href={"/shop"}><img src={"css/images/iconShop.svg"} height={40} width={40}/></a></p>
                <p><a href={"/map"}><img src={"css/images/iconMap.svg"} height={40} width={40}/></a></p>
                <p><a href={"/store"}><img src={"css/images/iconCoin.svg"} height={40} width={40}/></a></p>
                <p><a href={"/home"}><img src={"css/images/iconHome.svg"} height={40} width={40}/></a></p>
                <p><a href={"/del"}><img src={"css/images/iconDel.svg"} height={40} width={40}/></a></p>
                <p><a href={"/settings"}><img src={"css/images/iconGear.svg"} height={40} width={40}/></a></p>
                <p><a href={"/logout"}><img src={"css/images/iconLeave.svg"} height={40} width={40}/></a></p>
            </div>
        );
    }
}

ReactDOM.render(<NavBar/>, document.getElementById('nav_bar_container'));