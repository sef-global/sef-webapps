import React from "react";
import {Typography, Row, Col, Form, Icon, Input, Button, Checkbox, message, notification} from 'antd';
import './Login.css';
import axios from 'axios';
import "./Login.css";
import {withConfigContext} from "../context/ConfigContext";
import logo from "../../public/images/logo.png";
import config from "../../public/conf/config.json";

const {Title} = Typography;
const {Text} = Typography;

class Login extends React.Component {
    render() {
        const config = this.props.context;
        return (
            <div className="login">
                <div className="background">
                </div>
                <div className="content">
                    <Row>
                        <Col xs={3} sm={3} md={10}>
                        </Col>
                        <Col xs={18} sm={18} md={4}>
                            <Row style={{marginBottom: 20}}>
                                <Col style={{textAlign: "center"}}>
                                    <img style={
                                        {
                                            marginTop: 36,
                                            height: 60
                                        }
                                    }
                                         src={logo}/>
                                </Col>
                            </Row>
                            <Title level={2}>Login</Title>
                            <WrappedNormalLoginForm/>
                        </Col>
                    </Row>
                    <Row>
                        <Col span={4} offset={10}>

                        </Col>
                    </Row>
                </div>
            </div>

        );
    }
}

class NormalLoginForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            inValid: false,
            loading: false
        };
    }

    handleSubmit = (e) => {
        // const config = this.props.context;
        const thisForm = this;
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            thisForm.setState({
                inValid: false
            });
            if (!err) {
                thisForm.setState({
                    loading: true
                });
                const parameters = {
                    username: values.username,
                    password: values.password,
                    platform: "publisher"
                };

                const request = Object.keys(parameters).map(key => key + '=' + parameters[key]).join('&');
                const url = window.location.origin+ config.serverConfig.loginUri;

                axios.post(url, request
                ).then(res=>{
                    if (res.status === 200) {
                        let redirectUrl = window.location.origin+"/dashboard";
                        const searchParams = new URLSearchParams(window.location.search);
                        if(searchParams.has("redirect")){
                            redirectUrl = searchParams.get("redirect");
                        }
                        window.location = redirectUrl;
                    }
                }).catch(function (error) {
                    if (error.hasOwnProperty("response") && error.response.status === 401) {
                        thisForm.setState({
                            loading: false,
                            inValid: true
                        });
                    } else {
                        thisForm.setState({
                            loading: false,
                            inValid: false
                        });
                    }
                });
            }

        });
    };

    render() {
        const {getFieldDecorator} = this.props.form;
        let errorMsg = "";
        if (this.state.inValid) {
            errorMsg = <Text type="danger">Invalid Login Details</Text>;
        }
        let loading = "";
        if (this.state.loading) {
            loading = <Text type="secondary">Loading..</Text>;
        }
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <Form.Item>
                    {getFieldDecorator('username', {
                        rules: [{required: true, message: 'Please enter your username'}],
                    })(
                        <Input style={{height: 32}} prefix={<Icon type="user" style={{color: 'rgba(0,0,0,.25)'}}/>}
                               placeholder="Username"/>
                    )}
                </Form.Item>
                <Form.Item>
                    {getFieldDecorator('password', {
                        rules: [{required: true, message: 'Please enter your password'}],
                    })(
                        <Input style={{height: 32}}
                               prefix={<Icon type="lock" style={{color: 'rgba(0,0,0,.25)'}}/>} type="password"
                               placeholder="Password"/>
                    )}
                </Form.Item>
                {loading}
                {errorMsg}
                <Form.Item>
                    <Button loading={this.state.loading} block type="primary" htmlType="submit" className="login-form-button">
                        Log in
                    </Button>
                </Form.Item>
            </Form>
        );
    }
}

const WrappedNormalLoginForm = Form.create({name: 'normal_login'})(withConfigContext(NormalLoginForm));

export default withConfigContext(Login);
