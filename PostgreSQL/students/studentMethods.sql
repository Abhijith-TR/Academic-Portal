-- The credit limit is 1.25 times the average of the credits in the previous two semesters
-- Assumption: Only the two normal (I and II) semesters are considered
CREATE OR REPLACE FUNCTION get_credit_limit(
  _entry_number VARCHAR(15),
  _year INTEGER,
  _semester INTEGER
) RETURNS DECIMAL(4, 2) AS $$
  DECLARE
    previous_semester_credits INTEGER;
    credit_limit INTEGER;
    previous_academic_session INTEGER[2] := get_previous_academic_session(_year, _semester);
  BEGIN
    EXECUTE FORMAT('
    SELECT SUM(credits) FROM course_catalog WHERE course_code IN (
      SELECT course_code FROM %I WHERE 
      year = %s AND 
      semester = %s AND 
      grade IN (''A'', ''A-'', ''B'', ''B-'', ''C'', ''C-'', ''D'')
    )', _entry_number || '_credit', previous_academic_session[1], previous_academic_session[2])
    INTO previous_semester_credits;

    previous_academic_session := get_previous_academic_session(
      previous_academic_session[1],
      previous_academic_session[2]
    );
    
    EXECUTE FORMAT('
    SELECT SUM(credits) FROM course_catalog WHERE course_code IN (
      SELECT course_code FROM %I WHERE 
      year = %s AND 
      semester = %s AND 
      grade IN (''A'', ''A-'', ''B'', ''B-'', ''C'', ''C-'', ''D'')
    )', _entry_number || '_credit', previous_academic_session[1], previous_academic_session[2])
    INTO credit_limit;

    IF credit_limit IS NULL THEN
      credit_limit = 0;
    END IF;

    IF previous_semester_credits IS NULL THEN
      previous_semester_credits = 0;
    END IF;

    RETURN (credit_limit + previous_semester_credits)/2;
  END
$$ LANGUAGE plpgsql;

-- Function to check whether the student has exceeded the credit limit
-- Uses the get_credit_limit function to get the credit limit and then validates with the current semester
CREATE OR REPLACE FUNCTION check_credit_limit(
  _entry_number VARCHAR(15),
  _year INTEGER,
  _semester INTEGER,
  _course_code VARCHAR(6)
) RETURNS BOOLEAN AS $$
  DECLARE
    credit_limit DECIMAL(4, 2) = get_credit_limit(_entry_number, _year, _semester);
    credits_in_current_semester INTEGER;
    subject_credits INTEGER;
  BEGIN
    SELECT credits INTO subject_credits FROM course_catalog WHERE course_code = _course_code;

    IF credit_limit * 1.25 < 18 THEN 
      credit_limit := 18;
    ELSIF credit_limit * 1.25 > 24 THEN
      credit_limit := 24;
    ELSE 
      credit_limit := FLOOR(credit_limit * 1.25);
    END IF;
    
    EXECUTE FORMAT('
      SELECT SUM(credits)
      FROM course_catalog WHERE course_code IN (
        SELECT course_code
        FROM %I 
        WHERE year = %s
        AND semester = %s
      )
    ', _entry_number || '_credit', _year, _semester) 
    INTO credits_in_current_semester;

    IF credits_in_current_semester IS NULL THEN 
      credits_in_current_semester := 0;
    END IF;

    credits_in_current_semester := credits_in_current_semester + subject_credits;
    
    if credits_in_current_semester > credit_limit THEN 
      RETURN FALSE;
    END IF;

    RETURN TRUE;
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
  _semester INTEGER
) RETURNS VARCHAR(20) AS $$
  DECLARE
    enrollment_allowed BOOLEAN;
  BEGIN

    EXECUTE FORMAT('
      CALL verify_enrollment_request(''%s'', ''%s'', %s, ''%s'');', 
      _entry_number, 
      _course_code, 
      _year, 
      _semester
    );

    SELECT check_credit_limit(_entry_number, _year, _semester, _course_code) INTO enrollment_allowed;

    RAISE NOTICE '%', enrollment_allowed;
    
    IF enrollment_allowed is TRUE THEN
      EXECUTE FORMAT('
        INSERT INTO %I VALUES(''%s'', %s, %s, ''EN'')
      ', _entry_number || '_credit', _course_code, _year, _semester);
      RETURN 'Inserted!';
    ELSE 
      RETURN 'Invalid Enrollment Request';
    END IF;

  END
$$ LANGUAGE plpgsql;

