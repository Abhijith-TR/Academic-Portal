-- Remove a database with this name, if exists
DROP DATABASE IF EXISTS temp;
CREATE DATABASE temp;
\c temp;
DROP DATABASE IF EXISTS mini_project;

-- Create a new database with the required name
CREATE DATABASE mini_project;

-- Change the current database to the newly created one
\c mini_project;

-- Run the database initialization script
\i 'creatingRelations.sql';

-- Start inserting the functions
\i 'utilities.sql';
\i './students/studentMethods.sql';
\i './students/creatingStudents.sql';

-- Creating roles and assigning permissions
\i './authorization/permissions.sql';