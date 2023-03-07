# **Unit Test Plan**

### **Introduction**
---

The test plan has been designed to assess various functionalities required for the proper functioning of the record management system. This plan identifies all individual components in the design, their functionalities, the parts to be tested, along with the various methods and tools that were involved in their testing. This document further outlines the environment requirements for proper execution of the unit tests.

### **Notation**
---
- DAO - Data Access Object
- UI - User Interface

### **Test Items**
---
- User Interfaces
    * Student UI
    * Faculty UI
    * Admin UI
- DAO
    - Student DAO
    - Faculty DAO
    - Admin DAO
- Business Logic
    - Student 
    - Faculty
    - Admin

### **Test Environment**
---
The following are the software available on the system that was used to run the tests
- Java JDK - Version 17.0.6
- PostgreSQL - Version 15.1 
- Dependencies
  - PostgreSQL JDBC Driver
- Test Dependencies
  - Junit - Version 5.8.1
  - Mockito - Version 5.1.1

### **Test Files and Cases**
---
Mockito was used throughout the testing process to mock out dependencies to ensure that each unit was isolated during the unit testing. The test cases corresponding to each module are in the test file that carries the same name. <br> The test cases were generated such that each of the corner cases were covered, such as null arguments, negative batches, years and numbers, invalid file input and differering lengths from expected arguments. <br>
Further test cases were generated based on the expected outputs of each individual function on specific inputs from different modules to verify expected behaviour. 

1. User Interface - Unit tests considered all possible user input, results from other modules and the expected output that the user interface should generate.
2. Business Logic - The test cases considered all logical flows through the various functions in each use case. The inputs from other modules as well as the database were mocked. The outputs corresponding to irregular input parameters were also tested. 
3. DAO - The test cases considered various configurations of the database as well the return values if the database connection fails or some other error is returned by the connection. Running the tests requires initialization with the dummy data.