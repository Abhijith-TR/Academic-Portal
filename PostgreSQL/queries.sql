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