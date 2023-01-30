-- Method of inserting
INSERT INTO course_catalog VALUES(
  'CS301', 
  'Introduction to Data Structures', 
  '1-2-3', 
  '{"CS101", "CS102"}'
);

-- Insert into course_offerings
INSERT INTO course_offerings VALUES(
  'CS301', 
  'Gunturi', 
  '2022-II', 
  11
);

-- Testing credit limit function
SELECT check_credit_limit(
  '2020', 
  2022,
  'II'
);

-- Testing enroll function
SELECT enroll(
  '2020CSB1062', 
  'CS301', 
  2021, 
  'II'
);

-- Testing login function
SELECT login(
  '2020CSB1062', 
  'random',
  'student'
);    

-- Complete set of queries for inserting dummy data
insert into department values('DEPT01', 'Computer Science');

insert into user_details values('2020CSB1062', 'random', 'student'); 

insert into faculty values('FAC1', 'Dr Gunturi', 'DEPT01');  
insert into faculty values('FAC2', 'Dr. Puneet', 'DEPT01');

insert into students values('2020CSB1062', 'Abhijith T R', 'DEPT01'); 

insert into course_catalog values('CS301', 'Databases', 3, 1, 2, 4, 4, '{"CS101", "CS102"}', 'DEPT01'); 
insert into course_catalog(course_code, course_title, lecture_hours, tutorial_hours, practical_hours, self_study_hours, credits, department_id) values('CP301', 'DEP', 0, 0, 4, 0, 3, 'DEPT01');

insert into course_offerings values('CS301', 'FAC1', 2022, 2, 8.5); 
insert into course_offerings values('CP301', 'FAC2', 2022, 1, 6);

insert into "2020CSB1062_credit" values('CS301', 2022, 2, 'A');
-- insert into "2020CSB1062_credit" values('CP301', 2022, 1, 'A-');