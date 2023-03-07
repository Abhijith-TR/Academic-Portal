# **CS305 Mini Project**

### **How to Compile**
---
1. Open the PostgreSQL folder. Open a terminal in this folder and run postgres. Use ```\i initialization.sql``` to initialize the server with dummy data. Use ```\i production.sql``` to initialize server without dummy data. 
Note - You should not be in a database named temp or mini_project during this step.

2. Navigate to the client folder. You can run ``` ./gradlew build ``` to build the jar file. The jar file will be present in the client\build\libs under the name **client-1.0.jar**. Note - If there is an error associated with running the ./gradlew command, try running ``` chmod +x gradlew ``` in the folder.

3. Copy this file to a folder of your choice. Unzip the file using ``` unzip client-1.0.jar ```. Now in the same folder run ``` unzip postgresql-42.5.4.jar ``` ( Click yes if overwrite permissions are asked )

4. Now open the config.properties file and set the connection string, username and password of the database to connectionURL and db.connectionURL, username and db.username, password and db.password respectively.

5. If all steps are followed properly, you can run the file using ```java org.abhijith.Main```. The user name will be **staffdeanoffice** and the password will be **iitropar**.

### **Running Tests**
---
In order to run the tests, the same has to be done as the compilation. However, you **MUST** run the ```\i initialization.sql``` and not production.sql. Once, this is done, navigate to the gradle folder and go to src/main/resources/config.properties and set the database connection URL, username and password for both sets of variables. Now you can run the tests using ```./gradlew clean test```. ( If ./gradlew command is not recognised, try running ```chmod +x gradlew``` )