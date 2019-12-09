# SEF Common Webapps

# Setup Development Environment

## Setting up Dependencies
1. Setup WSO2 API Manager 2.6.0  (not 3.0.0)
    https://docs.wso2.com/display/AM260/Installing+via+the+Installer
    Download from this link : https://wso2.com/api-management/previous-releases/ (Version: 2.6.0) 
2. Setup tomcat9 https://tomcat.apache.org/download-90.cgi
3. Clone the sef common webapps repo https://github.com/sef-global/sef-webapps
4. Create a new application in WSO2 APIM store and add the creadentials to the invoker.
    
    Go to WSO2 APIM Store: https://localhost:9443/store
    
    Go to Applications > Add new application > Pick a name > Click Add
    
    Go to the created application and click Generate button.
    
    Copy the key and secret to `sefwebapps/invoker/src/main/resources/configprops.properties` file
    
##Building the invoker

1. Build the invoker module using maven.
    ```$xslt
    mvn clean install
    ``` 
2. Deploy the generated war in tomcat9 server.

## How to use invoker to invoke APIs?
1. To invoke any open endpoints use `http://localhost:8080/invoker/open/api/<api-url>`

    ex: `http://localhost:8080/invoker/open/api/partnership/v1/engagements`
    
2. To invoke other endpoints use `http://localhost:8080/invoker/api/<api-url>`
3. To login use:
    `POST: http://localhost:8080/invoker/login`

