import React from 'react';
import {Route} from 'react-router-dom';
class RouteWithSubRoutes extends React.Component{
    props;
    constructor(props){
        super(props);
        this.props = props;
    }
    render() {
        return(
            <Route path={this.props.path} exact={this.props.exact} render={(props) => (
                <this.props.component {...props} routes={this.props.routes}/>
            )}/>
        );
    }

}

export default RouteWithSubRoutes;