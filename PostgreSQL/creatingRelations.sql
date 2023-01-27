-- department_id 
CREATE TABLE department(
  department_id INTEGER PRIMARY KEY,
  department_name VARCHAR(40) NOT NULL
)

CREATE TABLE faculty(
  faculty_id INTEGER PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  department_id INTEGER NOT NULL,
  FOREIGN KEY (department_id) REFERENCES department(department_id)
)

-- entry_number: Currently 11 characters. 15 to accomodate older entry numbers
-- name: Longest name encountered: 40 characters
CREATE TABLE student(
  entry_number VARCHAR(15) PRIMARY KEY,
  name VARCHAR(40) NOT NULL,
  department_id INTEGER NOT NULL,
  FOREIGN KEY (department_id) REFERENCES department(department_id)
);


-- Longest name encountered: Introduction to Organic Chemistry and Biochemistry
-- credit_structure: L-T-P (All 3 can be a maximum of 24)
-- pre_requisites: Array of course_code
-- credits: integer field
-- course_code: 5 characters (currently), 6 characters (formerly). 

-- Note: The course_code is unique to each course offered at IIT Ropar
-- The pre-requisites are allowed to be empty.
CREATE TABLE course_catalog(
  course_code VARCHAR(6) PRIMARY KEY, 
  course_title VARCHAR(70) NOT NULL, 
  lecture_hours INTEGER CHECK (lecture_hours >= 0 AND lecture_hours <= 24) NOT NULL,
  tutorial_hours INTEGER CHECK (tutorial_hours >= 0 AND tutorial_hours <= 24) NOT NULL,
  practical_hours INTEGER CHECK (practical_hours >= 0 AND practical_hours <= 24) NOT NULL,
  self_study_hours INTEGER CHECK (self_study_hours >= 0 AND self_study_hours <= 24) NOT NULL,
  credits INTEGER CHECK (credits >= 0 AND credits <= 24) NOT NULL,
  pre_requisites VARCHAR[6][],
  department_id INTEGER NOT NULL,
  FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- course_code: Same considerations as above
-- academic_session: XXXX-XX i.e., YEAR-SESSION (SESSION: I, II, S or W)
-- The course_code is a foreign key, as only a course in the course_catalog can be offered
CREATE TABLE course_offerings(
  course_code VARCHAR(6),
  instructor VARCHAR(40) NOT NULL,
  academic_session VARCHAR(7) NOT NULL,
  cgpa_criteria DECIMAL(4, 2) CHECK (
    cgpa_criteria >= 0.0 
    AND cgpa_criteria <= 10.0
    ) NOT NULL,
  FOREIGN KEY (course_code) REFERENCES course_catalog(course_code),
  PRIMARY KEY (course_code, academic_session)
);

-- entry_number: Currently 11 characters. 15 to accomodate older entry numbers
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

-- Note: The entry_number is a foreign key, as only a student in the list_of_students can register for a course
-- The course_code is a foreign key, as only a course in the course_catalog can be offered
-- Note: EN is for Enrolled and - is for Not Applicable
-- academic_session and course_code as in previous tables
CREATE TABLE student_course_registration(
  entry_number VARCHAR(15),
  course_code VARCHAR(6),
  academic_session VARCHAR(7),
  status VARCHAR(10) NOT NULL,
  grade VARCHAR(2),
  PRIMARY KEY (entry_number, course_code, academic_session),
  FOREIGN KEY (course_code, academic_session) REFERENCES course_offerings(course_code, academic_session),
  FOREIGN KEY (entry_number) REFERENCES list_of_students(entry_number),
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