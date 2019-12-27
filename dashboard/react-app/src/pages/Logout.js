import React from "react";
import {notification, Menu, Icon} from 'antd';
import axios from 'axios';
import {withConfigContext} from "../context/ConfigContext";

/*
This class for call the logout api by sending request
 */
class Logout extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            inValid: false,
            loading: false
        };
    }
    /*
    This function call the logout api when the request is success
     */
    handleSubmit = () => {

        const thisForm = this;
        const config = this.props.context;

        thisForm.setState({
            inValid: false
        });

        axios.post(window.location.origin + config.serverConfig.logoutUri
        ).then(res => {
            //if the api call status is correct then user will logout and then it goes to login page
            if (res.status === 200) {
                window.location = window.location.origin + "/publisher/login";
            }
        }).catch(function (error) {
            if (error.hasOwnProperty("response") && error.response.status === 400) {
                thisForm.setState({
                    inValid: true
                });
            } else {
                notification["error"]({
                    message: "There was a problem",
                    duration: 0,
                    description:
                        "Error occurred while trying to logout.",
                });
            }
        });
    };

    render() {
        return (
            <Menu>
                <Menu.Item key="1" onClick={this.handleSubmit}><Icon type="logout"/>Logout</Menu.Item>
            </Menu>
        );
    }
}

export default withConfigContext(Logout);
