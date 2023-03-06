# **CS305 Mini Project**

### How to Compile
---
1. Open the PostgreSQL folder. Open a terminal in this folder and run postgres. Use ```\i initialization.sql``` to initialize the server with dummy data. Use ```\i production.sql``` to initialize server without dummy data. 
Note - You should not be in a database named temp or mini_project during this step.

2. Navigate to the client folder. You can run ``` ./gradlew build ``` to build the jar file. The jar file will be present in the client\build\libs under the name **client-1.0.jar**.

3. Copy this file to a folder of your choice. Unzip the file using ``` unzip client-1.0.jar ```. Now in the same folder run ``` unzip postgresql-42.5.4.jar ``` ( Click yes if overwrite permissions are asked )

4. Now open the config.properties file and set the connection string, username and password of the database to connectionURL and db.connectionURL, username and db.username, password and db.password respectively.

5. If all steps are followed properly, you can run the file using java org.abhijith.Main.