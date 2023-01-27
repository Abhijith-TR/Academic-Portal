-- Description: This file contains utility functions used by other functions in the database.

-- Function: Converts year and semester to the combined academic session used in the course_offerings and student_course_registration relations
CREATE OR REPLACE FUNCTION convert_to_academic_session(
  year INTEGER,
  semester TEXT
) RETURNS VARCHAR(7) AS $$
  DECLARE
  BEGIN
    RETURN CONCAT(year::VARCHAR(4), '-', semester);
  END
$$ LANGUAGE plpgsql;

-- Function: Verifies the enrollment request by checking whether the course exists in the given semester and year and whether the student exists
CREATE OR REPLACE PROCEDURE verify_enrollment_request(
  _entry_number VARCHAR(15),
  _course_code VARCHAR(6),
  year INTEGER,
  semester VARCHAR(2)
) AS $$
  DECLARE
    _academic_session VARCHAR(7) := convert_to_academic_session(year, semester);
  BEGIN
    IF NOT EXISTS (SELECT * FROM course_offerings WHERE course_code = _course_code AND academic_session = _academic_session) THEN
      RAISE EXCEPTION 'Course Not Found';
    ELSIF NOT EXISTS (SELECT * FROM list_of_students WHERE entry_number = _entry_number) THEN
      RAISE EXCEPTION 'Student Not Found';
    END IF;
  END
$$ LANGUAGE plpgsql;