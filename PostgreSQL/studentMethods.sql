-- The credit limit is 1.25 times the average of the credits in the previous two semesters
-- Assumption: Only the two normal (I and II) semesters are considered
CREATE OR REPLACE FUNCTION get_credit_limit(
  _entry_number VARCHAR(15),
  _year INTEGER,
  _semester VARCHAR(2)
) RETURNS INTEGER AS $$
  DECLARE
    credit_limit INTEGER;
  BEGIN
    
  END
$$ LANGUAGE plpgsql;

-- Function to check whether the student has exceeded the credit limit
-- Uses the get_credit_limit function to get the credit limit and then validates with the current semester
CREATE OR REPLACE PROCEDURE check_credit_limit(
  _entry_number VARCHAR(15),
  _year INTEGER,
  _semester VARCHAR(2),
  _course_code VARCHAR(6)
) AS $$
  DECLARE
    credit_limit INTEGER;
  BEGIN
    RAISE NOTICE '%', LENGTH(convert_to_academic_session(_year, _semester));
  END
$$ LANGUAGE plpgsql;

-- Function to enroll in a course
-- entry_number: Currently 11 characters. 15 to accomodate older entry numbers
-- course_code: Currently 5 characters. 6 to accomodate older courses
-- academic_session: XXXX-XX i.e., YEAR-SESSION (SESSION: I, II, S or W)

-- Function first verifies whether every argument is correct using the verify_enrollment_request procedure
CREATE OR REPLACE FUNCTION enroll(
  _entry_number VARCHAR(15),
  _course_code VARCHAR(6),
  _year INTEGER,
  _semester VARCHAR(2)
) RETURNS VARCHAR(20) AS $$
  DECLARE
    -- _academic_session VARCHAR(7) := convert_to_academic_session(year, semester);
  BEGIN
    EXECUTE FORMAT('
      CALL verify_enrollment_request(''%s'', ''%s'', %s, ''%s'');', 
      _entry_number, 
      _course_code, 
      _year, 
      _semester
    );

  END
$$ LANGUAGE plpgsql;

