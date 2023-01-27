-- Description: This file contains utility functions used by other functions in the database.

-- Function: Verifies the enrollment request by checking whether the course exists in the given semester and year and whether the student exists
CREATE OR REPLACE PROCEDURE verify_enrollment_request(
  _entry_number VARCHAR(15),
  _course_code VARCHAR(6),
  _year INTEGER,
  _semester INTEGER
) AS $$
  DECLARE
  BEGIN
    IF NOT EXISTS (SELECT * FROM course_offerings WHERE course_code = _course_code AND year = _year and semester = _semester) THEN
      RAISE EXCEPTION 'Course Not Found';
    ELSIF NOT EXISTS (SELECT * FROM list_of_students WHERE entry_number = _entry_number) THEN
      RAISE EXCEPTION 'Student Not Found';
    END IF;
  END
$$ LANGUAGE plpgsql;

-- Function: Given a semester and year, returns the previous semester and year
CREATE OR REPLACE FUNCTION get_previous_academic_session(
  _year INTEGER,
  _semester INTEGER
) RETURNS INTEGER[2] AS $$
  DECLARE
  BEGIN
    IF _semester = 1 THEN
      RETURN ARRAY[_year - 1, 2];
    ELSE
      RETURN ARRAY[_year, _semester - 1];
    END IF;
  END
$$ LANGUAGE plpgsql;