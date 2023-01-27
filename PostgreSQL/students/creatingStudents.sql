CREATE TRIGGER create_student_tables_and_role 
  BEFORE INSERT
  ON students
  FOR EACH ROW
  EXECUTE PROCEDURE create_student_tables_and_role();

CREATE OR REPLACE FUNCTION create_student_tables_and_role() 
RETURNS TRIGGER AS $$ 
  DECLARE
  BEGIN
    EXECUTE 'CREATE TABLE ' || NEW.entry_number || '_credit
    (
      course_code VARCHAR(6) NOT NULL,
      year INTEGER NOT NULL,
      semester VARCHAR(2) NOT NULL,
      grade VARCHAR(2) NOT NULL,
      PRIMARY KEY (course_code, year, semester)
    );';
    EXECUTE 'CREATE TABLE ' || NEW.entry_number || '_audit
    (
      course_code VARCHAR(6) NOT NULL,
      year INTEGER NOT NULL,
      semester VARCHAR(2) NOT NULL,
      grade VARCHAR(2) NOT NULL,
      PRIMARY KEY (course_code, year, semester)
    );';
  END
$$ LANGUAGE plpgsql;