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

### **Test Cases and Methods**
---
- Mockito was used throughout the testing process to mock out the dependencies and ensure isolation of the units to prevent errors in other units from affecting the tests of any other unit. 
- User Interface - The faculty, student and admin interfaces involved the same premise of taking user input, invoking the corresponding logical object and displaying results based on the returned value. The system input was replaced by a string from which the input was read. The system output was collected in a ByteArrayOutputStream. 
- Enroll - 