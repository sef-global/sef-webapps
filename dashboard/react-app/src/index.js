import React from 'react';
import ReactDOM from 'react-dom';
import * as serviceWorker from './serviceWorker';
import App from "./App";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard/Dashboard";
import './index.css';
import Home from "./pages/Dashboard/Home/Home";

const routes = [
    {
        path: '/dashboard/login',
        exact: true,
        component: Login
    },
    {
        path: '/dashboard/',
        exact: false,
        component: Dashboard,
        routes: [
            {
                path: '/dashboard/home',
                component: Home,
                exact: true
            }
        ]
    }
];


ReactDOM.render(
        <App routes={routes}/>,
    document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
