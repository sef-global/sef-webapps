# sef-webapps

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
    
5. Build the invoker module using maven.
    ```$xslt
    mvn clean install
    ``` 
    Deploy the generated war in tomcat9 server.

## Building the project

1. Build the project using maven
    ```$xslt
    mvn clean install
    ```
2. Deploy three war files in module target directories in tomcat9

## Configuring the DB and API endpoints

### DB

( Upgade to MySQL 8.0 if you are using lower versions )

1. Create new mysql user for sef
    ```sql
    CREATE USER 'sef'@'localhost' IDENTIFIED BY 'sef123';
    ```
2. Grant access to the new user
    ```sql
    GRANT ALL PRIVILEGES ON *.* TO 'sef'@'localhost';
    ```
3. Run the mysql.sql script in scripts directory to import the database.


### API
1. Login to the WSO2 APIM publisher portal. https://localhost:9443/publisher
2. Go to 'Add New API' > I Have an API > Swagger File > Select the scripts/swagger.json file
3. Set context name to "partnership" and hit next
4. Click on Managed api and add production endpoint to `http://localhost:8080/api/partnership/v1` 
    (8080 is your tomacat port) hit next
5. Set transports to HTTPS, Subscription Tiers to Unlimited
6. Hit Save & Publish
7. **Login** to API store and subscribe to the created api. https://localhost:9443/store (you must login)


### How to use invoker to invoke APIs?
1. To invoke any open endpoints use `http://localhost:8080/invoker/open/api/<api-url>`

    ex: `http://localhost:8080/invoker/open/api/partnership/v1/engagements`
    
2. To invoke other endpoints use `http://localhost:8080/invoker/api/<api-url>`
3. To login use:
    `POST: http://localhost:8080/invoker/login`
4. Import  `scripts/postman_collection.json` to postman to see available endpoints.    

