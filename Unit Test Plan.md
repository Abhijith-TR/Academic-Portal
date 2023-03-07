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
- User Interfaces - All of the user interfaces must be tested to ensure that the are resilient to invalid inputs. The user interfaces must be able to interact with the other objects in the database and product the correct output when the proper output is returned by the other objects. The user interfaces include include custom scanner and printer objects which must also be tested to ensure proper functioning.
    * Student UI
    * Faculty UI
    * Admin UI
- DAO - All of the separate DAOs must be tested to ensure that they have the ability to access the database correctly. The various SQL queries must be tested to ensure that the correct activities are being performed. The return values must be tested to ensure that they are conformant to the values mentioned in the interfaces and the values required by the users of the object.
    - Student DAO
    - Faculty DAO
    - Admin DAO
- Business Logic - The business logic modules must be tested to ensure that the business requirements are met. The modules must return proper outputs and handle all the values returned by the DAO. Test cases must be generated to assess all possible flows through various functions.
    - Student 
    - Faculty
    - Admin

### **Approach**
---
The testing will be done in a bottom up manner with the low level elements being tested first, then the higher level elements. This implies that the DAO will be tested first, followed by the business logic section and finally the user interface. The following is the testing methodology to be used
- Agile Testing - This is the testing methodology that will be adopted in the development workflow. It allows for rapid development while ensuring that the modules work correctly independently and thus reducing errors while integrating with other modules. This allows for rapid error detection and minimizes the time spent debugging code. 

### **Pass / Fail Criteria**
---
A testing phase in the development workflow is only complete when 100% of unit tests execute successfully. The pass rate is 100%, achieving the pass rate is mandatory.

### **Suspension Criteria**
---
If over 30% of test cases fail, further testing can be suspended until all the errors have been mitigated and the bugs fixed. 

### **Testing Environment**
---

| S.No | Resources | Description |
| --- | ----------- | ------ |
| 1 | Database | PostgreSQL server containing the required schema
| 2 | Java | JDK Version 17 preferably
| 3 | Gradle | Gradle version 7.5.1 to run the automated test suite
| 4 | Test Tool | Junit and Mockito to develop and run unit tests
