-- Description: This file contains the functions for authorization
-- Argument variables start with an underscore
CREATE OR REPLACE FUNCTION login(
  _username VARCHAR(15),
  _password VARCHAR(40),
  _role VARCHAR(7)
) RETURNS BOOLEAN AS $$
  DECLARE
    password VARCHAR(40);
  BEGIN
    -- Checks the user_details table to find a user with the given username and role
    SELECT user_details.password INTO password FROM user_details WHERE username = _username AND role = _role;
    -- If the entered password matches the password that is found, the login request is accepted and role is set
    IF password = _password THEN
      RAISE NOTICE '%', _role;
      EXECUTE 'SET ROLE ' || _role;
      RETURN TRUE;
    END IF;
    -- Otherwise the login request is rejected
    RETURN FALSE;
  END
$$ LANGUAGE plpgsql;