/*
 * Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 * Entgra (pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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