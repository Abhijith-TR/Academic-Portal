CREATE OR REPLACE FUNCTION create_student_tables_and_role() 
RETURNS TRIGGER AS $$ 
  DECLARE
  BEGIN
    -- One table to store the courses that the student is crediting
    EXECUTE FORMAT('CREATE TABLE %I
    (
      course_code VARCHAR(6) NOT NULL,
      year INTEGER NOT NULL,
      semester INTEGER NOT NULL,
      grade VARCHAR(2) NOT NULL,
      CHECK (grade = ''A'' OR grade = ''A-'' OR grade = ''B'' OR grade = ''B-'' OR grade = ''C'' OR grade = ''C-'' OR grade = ''D'' OR grade = ''E'' OR grade = ''F'' OR grade = ''EN''),
      PRIMARY KEY (course_code, year, semester)
    );', NEW.entry_number || '_credit');

    -- One table to store the courses that the student in auditing
    EXECUTE FORMAT('CREATE TABLE %I
    (
      course_code VARCHAR(6) NOT NULL,
      year INTEGER NOT NULL,
      semester INTEGER NOT NULL,
      grade VARCHAR(2) NOT NULL,
      CHECK (grade = ''NP'' OR grade = ''NF'' OR grade = ''EN''),
      PRIMARY KEY (course_code, year, semester)
    );', NEW.entry_number || '_audit');

    -- One table to store the courses that the student has withdrawn from
    EXECUTE FORMAT('CREATE TABLE %I
    (
      course_code VARCHAR(6) NOT NULL,
      year INTEGER NOT NULL,
      semester INTEGER NOT NULL,
      PRIMARY KEY (course_code, year, semester)
    );', NEW.entry_number || '_withdrawn');

    EXECUTE FORMAT('
      CREATE ROLE 
    ')

    RETURN NEW;
  END
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_student_tables_and_role 
  BEFORE INSERT
  ON students
  FOR EACH ROW
  EXECUTE PROCEDURE create_student_tables_and_role();