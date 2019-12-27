import React from "react";
import {Layout, Menu, Icon, Drawer, Button} from 'antd';
import {Switch, Link} from "react-router-dom";
import RouteWithSubRoutes from "../../js/RouteWithSubRoutes"
import {Redirect} from 'react-router'
import "./Dashboard.css";
import {withConfigContext} from "../../context/ConfigContext";
import Logout from "./../Logout";
import logo from "../../../public/images/logo.png";

const {Header, Content, Footer} = Layout;
const {SubMenu} = Menu;

class Dashboard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            routes: props.routes,
            visible: false,
            collapsed: false
        };
        this.config = this.props.context;
        this.Logo = this.config.theme.logo;
    }

    showMobileNavigationBar = () => {
        this.setState({
            visible: true,
            collapsed: !this.state.collapsed
        });
    };

    onCloseMobileNavigationBar = () => {
        this.setState({
            visible: false,
        });
    };

    render() {
        return (
            <div>
                <Layout>
                    <Header style={{paddingLeft: 0, paddingRight: 0, backgroundColor: "white"}}>
                        <div className="logo-image">
                            <Link to="/dashboard/home"><img alt="logo" src={logo}/></Link>
                        </div>

                        <div className="web-layout">
                            <Menu
                                theme="light"
                                mode="horizontal"
                                defaultSelectedKeys={['1']}
                                style={{lineHeight: '64px'}}>
                                <Menu.Item key="1">
                                    <Link to="/dashboard/home">
                                        <Icon type="home"/>Home
                                    </Link>
                                </Menu.Item>
                                <SubMenu className="profile"
                                         title={
                                             <span className="submenu-title-wrapper">
                                               <Icon type="user"/>{this.config.user}
                                             </span>}>
                                    <Logout/>
                                </SubMenu>
                            </Menu>
                        </div>
                    </Header>
                </Layout>

                <Layout className="mobile-layout">
                    <div className="mobile-menu-button">
                        <Button type="link" onClick={this.showMobileNavigationBar}>
                            <Icon
                                type={this.state.collapsed ? 'menu-fold' : 'menu-unfold'}
                                className="bar-icon"/>
                        </Button>
                    </div>
                </Layout>
                    <Drawer
                        title={
                            <Link to="/dashboard/home" onClick={this.onCloseMobileNavigationBar}>
                                <img alt="logo"
                                     src={logo}
                                     style={{marginLeft: 30}}
                                     width={"60%"}/>
                            </Link>
                        }
                        placement="left"
                        closable={false}
                        onClose={this.onCloseMobileNavigationBar}
                        visible={this.state.visible}
                        getContainer={false}
                        style={{position: 'absolute'}}>
                        <Menu
                            theme="light"
                            mode="inline"
                            defaultSelectedKeys={['1']}
                            style={{lineHeight: '64px', width: 231}}
                            onClick={this.onCloseMobileNavigationBar}>
                            <Menu.Item key="1">
                                <Link to="/dashboard/home">
                                    <Icon type="home"/>Home
                                </Link>
                            </Menu.Item>
                        </Menu>
                    </Drawer>
                <Layout className="mobile-layout">
                    <Menu
                        mode="horizontal"
                        defaultSelectedKeys={['1']}
                        style={{lineHeight: '63px', position: 'fixed', marginLeft: '80%'}}>
                        <SubMenu
                            title={
                                <span className="submenu-title-wrapper">
                                    <Icon type="user"/>
                                </span>}>
                            <Logout/>
                        </SubMenu>
                    </Menu>
                </Layout>

                <Layout className="dashboard-body">
                    <Content style={{marginTop: 2}}>
                        <Switch>
                            <Redirect exact from="/dashboard" to="/publisher/home"/>
                            {this.state.routes.map((route) => (
                                <RouteWithSubRoutes key={route.path} {...route} />
                            ))}
                        </Switch>
                    </Content>
                    <Footer style={{textAlign: 'center'}}>
                        © 2020 sefglobal.org
                    </Footer>
                </Layout>
            </div>
        );
    }
}

export default withConfigContext(Dashboard);