-- In order to log into the database, the faculty can use their faculty ID, the students can use their entry number
-- The admin will have the username mentioned in the project specification
CREATE TABLE admin
(
    admin_id VARCHAR(15) PRIMARY KEY,
    name     VARCHAR(40) NOT NULL
);

-- department_id: Used to uniquely identify each department. Given based on number of departments
CREATE TABLE department
(
    department_id   VARCHAR(15) PRIMARY KEY,
    department_name VARCHAR(40) NOT NULL
);

-- faculty_id: Used to uniquely identify each faculty. Given based on number of faculties
-- Each faculty belongs to some specific department
CREATE TABLE faculty
(
    faculty_id    VARCHAR(15) PRIMARY KEY,
    name          VARCHAR(40) NOT NULL,
    department_id VARCHAR(15) NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department (department_id) ON DELETE CASCADE
);

-- Longest name encountered: Introduction to Organic Chemistry and Biochemistry
-- lecture_hours, tutorial_hours, practical_hours, self_study_hours: The course credit structure i.e., L-T-P-S
-- credits: integer field
-- pre_requisites: Array of course_code
-- course_code: 5 characters (currently), 6 characters (formerly). 

-- Note: The course_code is unique to each course offered at IIT Ropar
-- The pre-requisites are allowed to be empty.
-- Any course can be offered by any department, but the prerequisites for a course are always same
CREATE TABLE course_catalog
(
    course_code      VARCHAR(6),
    course_title     VARCHAR(70)                                                            NOT NULL,
    lecture_hours    DECIMAL(4, 2) CHECK (lecture_hours >= 0 AND lecture_hours <= 24)       NOT NULL,
    tutorial_hours   DECIMAL(4, 2) CHECK (tutorial_hours >= 0 AND tutorial_hours <= 24)     NOT NULL,
    practical_hours  DECIMAL(4, 2) CHECK (practical_hours >= 0 AND practical_hours <= 24)   NOT NULL,
    self_study_hours DECIMAL(4, 2) CHECK (self_study_hours >= 0 AND self_study_hours <= 24) NOT NULL,
    credits          DECIMAL(3, 1) CHECK (credits > 0)                                      NOT NULL,
    pre_requisites   VARCHAR[6][],
    PRIMARY KEY (course_code)
);

-- course_code: Same considerations as above
-- semester: One of the following:
--   [
--    1, 2, 3, 4
--   ]
-- 1 and 2 refer to the normal semesters. 3 refers to the summer semester and 4 refers to the winter semester
-- The course_code is a foreign key, as only a course in the course_catalog can be offered
-- instructor refers only to the instructor in charge of the course, not all the instructors teaching the course
CREATE TABLE course_offerings
(
    course_code   VARCHAR(6),
    faculty_id    VARCHAR(15) NOT NULL,
    year          INTEGER     NOT NULL,
    semester      INTEGER     NOT NULL,
    department_id VARCHAR(15) NOT NULL,
    cgpa_criteria DECIMAL(4, 2) CHECK (
                cgpa_criteria >= 0.0
            AND cgpa_criteria <= 10.0
        )                DEFAULT 0,
    CHECK (semester IN (1, 2)),
    FOREIGN KEY (course_code) REFERENCES course_catalog (course_code) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department (department_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty (faculty_id) ON DELETE CASCADE,
    PRIMARY KEY (course_code, year, semester, department_id)
);

-- Table that stores all the batches that are there in the student table
-- Links the student with the core courses and the UG curriculum corresponding to his batch
CREATE TABLE batch
(
    year INTEGER PRIMARY KEY
);

-- entry_number: Currently 11 characters. 15 to accommodate older entry numbers
-- name: Longest name encountered: 40 characters
-- batch: denotes the year of joining the institute
CREATE TABLE student
(
    entry_number  VARCHAR(15) PRIMARY KEY,
    name          VARCHAR(40) NOT NULL,
    department_id VARCHAR(15) NOT NULL,
    batch         INTEGER     NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department (department_id) ON DELETE CASCADE,
    FOREIGN KEY (batch) REFERENCES batch (year) ON DELETE CASCADE
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
CREATE TABLE student_course_registration
(
    entry_number  VARCHAR(15),
    course_code   VARCHAR(6),
    year          INTEGER     NOT NULL,
    semester      INTEGER     NOT NULL,
    grade         VARCHAR(2) DEFAULT '-',
    department_id VARCHAR(15) NOT NULL,
    category      TEXT        NOT NULL,
    PRIMARY KEY (entry_number, course_code, year, semester),
    FOREIGN KEY (course_code, year, semester, department_id) REFERENCES course_offerings (course_code, year, semester, department_id) ON DELETE CASCADE,
    FOREIGN KEY (entry_number) REFERENCES student (entry_number) ON DELETE CASCADE,
    CHECK (grade in (
                     'A', 'A-', 'B', 'B-', 'C', 'C-', 'D', 'E', 'F', 'W', 'I', '-'
        )),
    CHECK (category IN ('SC', 'SE', 'GR', 'PC', 'PE', 'HC', 'HE', 'CP', 'II', 'NN', 'OE'))
);

-- This table contains the common details for all admin, students and faculty
-- The phone number and the email are not compulsory and can be changed by the users themselves
-- The length of the password must be at least 8
-- The default password is iitropar which can be changed by the user once they login
CREATE TABLE common_user_details
(
    id       VARCHAR(15) PRIMARY KEY,
    password VARCHAR(40) DEFAULT 'iitropar',
    role     VARCHAR(7) NOT NULL,
    phone    TEXT,
    email    TEXT,
    CHECK ( role in ('ADMIN', 'STUDENT', 'FACULTY')),
    CHECK ( length(password) >= 8 ),
    PRIMARY KEY (id)
);

-- A table that stores the current year and semester to ensure that students can only enroll in courses that have been offered in the current semester
-- JAVA code ensures that this table always contains only one entry
CREATE TABLE current_year_and_semester
(
    year          INTEGER NOT NULL,
    semester      INTEGER NOT NULL,
    current_event TEXT DEFAULT 'RUNNING',
    PRIMARY KEY (year, semester),
    CHECK ( current_event IN ('ENROLLING', 'OFFERING', 'GRADE SUBMISSION', 'COMPLETED', 'RUNNING'))
);


-- Table that stores who has logged into and out of the database along with their roles
-- Used to figure out who is responsible in case of database anomalies
CREATE TABLE log
(
    id        VARCHAR(15) NOT NULL,
    role      VARCHAR(7)  NOT NULL,
    log_time  TIMESTAMP   NOT NULL,
    in_or_out VARCHAR(4)  NOT NULL,
    CHECK ( role in ('ADMIN', 'STUDENT', 'FACULTY')),
    CHECK (in_or_out IN ('IN', 'OUT'))
);

-- This table holds the pass criteria for every batch
-- sc - Science Core
-- se - Science Elective
-- gr - General Engineering
-- pc - Program Core
-- pe - Program Elective
-- hc - Humanities Core
-- he - Humanities Elective
-- cp - Capstone
-- ii - Industrial Internship
-- nn - NSS / NCC / NSO
-- oe - Open Elective
CREATE TABLE ug_curriculum
(
    year INTEGER       NOT NULL PRIMARY KEY,
    sc   NUMERIC(4, 2) NOT NULL,
    se   NUMERIC(4, 2) NOT NULL,
    gr   NUMERIC(4, 2) NOT NULL,
    pc   NUMERIC(4, 2) NOT NULL,
    pe   NUMERIC(4, 2) NOT NULL,
    hc   NUMERIC(4, 2) NOT NULL,
    he   NUMERIC(4, 2) NOT NULL,
    cp   NUMERIC(4, 2) NOT NULL,
    ii   NUMERIC(4, 2) NOT NULL,
    nn   NUMERIC(4, 2) NOT NULL,
    oe   NUMERIC(4, 2) NOT NULL,
    FOREIGN KEY (year) REFERENCES batch (year) ON DELETE CASCADE
);

-- Table that stores the core courses of all batches
CREATE TABLE core_courses
(
    course_code     VARCHAR(6) NOT NULL,
    department_id   VARCHAR(15),
    batch           INTEGER,
    course_category TEXT       NOT NULL,
    CHECK (course_category IN ('SC', 'SE', 'GR', 'PC', 'PE', 'HC', 'HE', 'CP', 'II', 'NN', 'OE')),
    PRIMARY KEY (course_code, department_id, batch),
    FOREIGN KEY (department_id) REFERENCES department (department_id) ON DELETE CASCADE,
    FOREIGN KEY (batch) REFERENCES batch (year) ON DELETE CASCADE
);

-- Table to store the prerequisites of the instructor
CREATE TABLE instructor_prerequisites
(
    course_code    VARCHAR(6) NOT NULL,
    year           INTEGER,
    semester       INTEGER,
    department_id  VARCHAR(15),
    prereq         VARCHAR(6) NOT NULL,
    grade_criteria INTEGER,
    type          INTEGER,
    CHECK (grade_criteria >= 0 AND grade_criteria <= 10),
    FOREIGN KEY (course_code, year, semester, department_id) REFERENCES course_offerings (course_code, year, semester, department_id) ON DELETE CASCADE,
    PRIMARY KEY (course_code, year, semester, department_id, prereq, type)
);

-- Table that stores what category each course offering is for a particular batch and department
CREATE TABLE course_category
(
    course_code   VARCHAR(6) NOT NULL,
    year          INTEGER,
    semester      INTEGER,
    department_id VARCHAR(15),
    category      TEXT       NOT NULL,
    batch         INTEGER,
    department    VARCHAR(15),
    FOREIGN KEY (course_code, year, semester, department_id) REFERENCES course_offerings (course_code, year, semester, department_id) ON DELETE CASCADE,
    PRIMARY KEY (course_code, year, semester, department_id, batch, department),
    CHECK (category IN ('SC', 'SE', 'GR', 'PC', 'PE', 'HC', 'HE', 'CP', 'II', 'NN', 'OE'))
);