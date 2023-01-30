-- This file contains all the roles and the permissions that each role has
-- Remove the roles if they already exist
DROP ROLE IF EXISTS admin;
DROP ROLE IF EXISTS faculty;
DROP ROLE IF EXISTS student;

-- Creating new roles
CREATE ROLE admin;
CREATE ROLE faculty;
CREATE ROLE student;

-- Student Permissions
GRANT SELECT ON course_catalog TO student;
GRANT SELECT ON course_offerings TO student;

GRANT EXECUTE ON FUNCTION enroll TO student;
GRANT EXECUTE ON FUNCTION get_credit_limit TO student;
GRANT EXECUTE ON FUNCTION check_credit_limit TO student;