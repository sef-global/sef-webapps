import React from "react";

const ConfigContext = React.createContext();

export const withConfigContext = Component => {
    return props => (
        <ConfigContext.Consumer>
            {context => {
                return <Component {...props} context={context}/>;
            }}
        </ConfigContext.Consumer>
    );
};

export default ConfigContext;
