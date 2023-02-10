-- In order to log into the database, the faculty can use their faculty ID, the students can use their entry number
-- The admin will have the username mentioned in the project specification
CREATE TABLE admin(
  admin_id VARCHAR(15) PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  phone INTEGER,
  password VARCHAR(40) NOT NULL
);

-- department_id: Used to uniquely identify each department. Given based on number of departments
CREATE TABLE department(
  department_id VARCHAR(15) PRIMARY KEY,
  department_name VARCHAR(40) NOT NULL
);

-- faculty_id: Used to uniquely identify each faculty. Given based on number of faculties
-- Each faculty belongs to some specific department
CREATE TABLE faculty(
  faculty_id VARCHAR(15) PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  department_id VARCHAR(15) NOT NULL,
  phone INTEGER,
  FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- entry_number: Currently 11 characters. 15 to accommodate older entry numbers
-- name: Longest name encountered: 40 characters
CREATE TABLE student(
  entry_number VARCHAR(15) PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  phone INTEGER,
  department_id VARCHAR(15) NOT NULL,
  FOREIGN KEY (department_id) REFERENCES department(department_id)
);


-- Longest name encountered: Introduction to Organic Chemistry and Biochemistry
-- lecture_hours, tutorial_hours, practical_hours, self_study_hours: The course credit structure i.e., L-T-P-S
-- credits: integer field
-- pre_requisites: Array of course_code
-- course_code: 5 characters (currently), 6 characters (formerly). 

-- Note: The course_code is unique to each course offered at IIT Ropar
-- The pre-requisites are allowed to be empty.
CREATE TABLE course_catalog(
  course_code VARCHAR(6),
  course_title VARCHAR(70) NOT NULL, 
  lecture_hours DECIMAL(4, 2) CHECK (lecture_hours >= 0 AND lecture_hours <= 24) NOT NULL,
  tutorial_hours DECIMAL(4, 2) CHECK (tutorial_hours >= 0 AND tutorial_hours <= 24) NOT NULL,
  practical_hours DECIMAL(4, 2) CHECK (practical_hours >= 0 AND practical_hours <= 24) NOT NULL,
  self_study_hours DECIMAL(4, 2) CHECK (self_study_hours >= 0 AND self_study_hours <= 24) NOT NULL,
  credits DECIMAL(3, 1) CHECK (credits > 0) NOT NULL,
  pre_requisites VARCHAR[6][],
  department_id VARCHAR(15) NOT NULL,
  PRIMARY KEY (course_code, department_id),
  FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- course_code: Same considerations as above
-- semester: One of the following:
--   [
--    1, 2, 3, 4
--   ]
-- 1 and 2 refer to the normal semesters. 3 refers to the summer semester and 4 refers to the winter semester
-- The course_code is a foreign key, as only a course in the course_catalog can be offered
-- instructor refers only to the instructor in charge of the course, not all the instructors teaching the course
CREATE TABLE course_offerings(
  course_code VARCHAR(6),
  faculty_id VARCHAR(15) NOT NULL,
  year INTEGER NOT NULL,
  semester INTEGER NOT NULL,
  department_id VARCHAR(15) NOT NULL,
  cgpa_criteria DECIMAL(4, 2) CHECK (
    cgpa_criteria >= 0.0 
    AND cgpa_criteria <= 10.0
    ) DEFAULT 0,
  instructor_prerequisites JSONB,
  CHECK (semester IN (1, 2, 3, 4)),
  FOREIGN KEY (course_code, department_id) REFERENCES course_catalog(course_code, department_id),
  FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id),
  PRIMARY KEY (course_code, year, semester)
);

-- entry_number: Currently 11 characters. 15 to accommodate older entry numbers
-- status: One of the following:
--   [ 
--    Enrolled
--    Dropped
--    Rejected By Instructor
--    Rejected By Faculty Advisor
--    Pending Instructor Approval
--    Pending Faculty Advisor Approval
--    ]
-- GRADE: One of the following:
--   [
--    A, A-, B, B-, C, C-, D, E, F, NP, W, I, NF, EN, -
--   ]

-- Note: The entry_number is a foreign key, as only a student in the students can register for a course
-- The course_code is a foreign key, as only a course in the course_catalog can be offered
-- Note: EN is for Enrolled and - is for Not Applicable
-- academic_session and course_code as in previous tables
CREATE TABLE student_course_registration(
  entry_number VARCHAR(15),
  course_code VARCHAR(6),
  year INTEGER NOT NULL,
  semester INTEGER NOT NULL,
  status VARCHAR(10) NOT NULL,
  grade VARCHAR(2),
  PRIMARY KEY (entry_number, course_code, year, semester),
  FOREIGN KEY (course_code, year, semester) REFERENCES course_offerings(course_code, year, semester),
  FOREIGN KEY (entry_number) REFERENCES student(entry_number),
  CHECK (status in (
    'Enrolled', 
    'Dropped',
    'Rejected By Instructor',
    'Pending Faculty Advisor Approval',
    'Rejected By Faculty Advisor',
    'Pending Instructor Approval',
    'Available'
  )),
  CHECK (grade in (
    'A', 'A-', 'B', 'B-', 'C', 'C-', 'D', 'E', 'F', 'NP', 'W', 'I', 'NF', 'EN', '-'
  ))
);

-- This table contains the login details for all admin, students and faculty
-- The length of the password must be at least 8
-- The default password is iitropar which can be changed by the user once they login
CREATE TABLE user_login_details (
    id VARCHAR(15) PRIMARY KEY,
    password VARCHAR(40) DEFAULT 'iitropar',
    role VARCHAR(7) NOT NULL,
    CHECK ( role in ('admin', 'student', 'faculty')),
    CHECK ( length(password) >= 8 )
);

-- A table that stores the current year and semester to ensure that students can only enroll in courses that have been offered in the current semester
-- JAVA code ensures that this table always contains only one entry
CREATE TABLE current_year_and_semester (
    year INTEGER NOT NULL,
    semester INTEGER NOT NULL
);