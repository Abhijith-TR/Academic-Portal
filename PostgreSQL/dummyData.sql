-- Creating all the departments in the institute
INSERT INTO department VALUES ('CS', 'COMPUTER SCIENCE AND ENGINEERING');
INSERT INTO department VALUES ('BM', 'BIOMEDICAL ENGINEERING');
INSERT INTO department VALUES ('CH', 'CHEMICAL ENGINEERING');
INSERT INTO department VALUES ('CE', 'CIVIL ENGINEERING');
INSERT INTO department VALUES ('EE', 'ELECTRICAL ENGINEERING');
INSERT INTO department VALUES ('ME', 'MECHANICAL ENGINEERING');
INSERT INTO department VALUES ('MME', 'METALLURGICAL AND MATERIALS ENGINEERING');
INSERT INTO department VALUES ('CY', 'CHEMISTRY');
INSERT INTO department VALUES ('PY', 'PHYSICS');
INSERT INTO department VALUES ('MA', 'MATHEMATICS');
INSERT INTO department VALUES ('HS', 'HUMANITIES AND SOCIAL SCIENCES');
-- And one additional department for Mathematics and Computing which comes under both maths and computer science
INSERT INTO department VALUES ('MNC', 'MATHEMATICS AND COMPUTING');

-- Creating the course catalog
INSERT INTO course_catalog VALUES ('GE103', 'INTRODUCTION TO COMPUTING AND DATA STRUCTURES', 3, 0, 3, 7, 4.5, '{}');
INSERT INTO faculty VALUES ('FAC1', 'DR SHIRSHENDU DAS', 'CS');
INSERT INTO course_offerings VALUES ('GE103', 'FAC1', 2020, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA101', 'CALCULUS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC2', 'DR KAUSHIK MONDAL', 'MA');
INSERT INTO course_offerings VALUES ('MA101', 'FAC2', 2020, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS103', 'PROFESSIONAL ENGLISH COMMUNICATION', 2, 0, 2, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC3', 'DR RANO RINGO', 'HS');
INSERT INTO course_offerings VALUES ('HS103', 'FAC3', 2020, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('PH101', 'PHYSICS FOR ENGINEERS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC4', 'DR DEEPIKA CHOUDHARY', 'PY');
INSERT INTO course_offerings VALUES ('PH101', 'FAC4', 2020, 1, 'PY', 0);

INSERT INTO course_catalog VALUES ('GE105', 'ENGINEERING DRAWING', 0, 0, 3, 1.5, 1.5, '{}');
INSERT INTO faculty VALUES('FAC5', 'DR EKTA SINGLA', 'ME');
INSERT INTO course_offerings VALUES ('GE105', 'FAC5', 2020, 1, 'ME', 0);

INSERT INTO course_catalog VALUES ('CS101', 'DISCRETE MATHEMATICAL STRUCTURES', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC6', 'DR APURVA MUDGAL', 'CS');
INSERT INTO course_offerings VALUES ('CS101', 'FAC6', 2020, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA102', 'LINEAR ALGEBRA, INTEGRAL TRANSFORMS AND SPECIAL FUNCTIONS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC7', 'DR MANJU KHAN', 'MA');
INSERT INTO course_offerings VALUES ('MA102', 'FAC7', 2020, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS101', 'HISTORY OF TECHNOLOGY', 1.5, 0.5, 0, 2.5, 1.5, '{}');
INSERT INTO faculty VALUES('FAC8', 'DR KAMAL CHAUDHARY', 'HS');
INSERT INTO course_offerings VALUES ('HS101', 'FAC8', 2020, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('GE104', 'INTRODUCTION TO ELECTRICAL ENGINEERING', 2, 2/3, 2, 13/3, 3, '{}');
INSERT INTO faculty VALUES('FAC9', 'DR KALAISELVI J', 'EE');
INSERT INTO course_offerings VALUES ('GE104', 'FAC9', 2020, 2, 'EE', 0);

INSERT INTO course_catalog VALUES ('GE101', 'TECHNOLOGY MUSEUM LAB', 0, 0, 2, 1, 1, '{}');
INSERT INTO faculty VALUES('FAC10', 'DR RAJESH GUPTA', 'HS');
INSERT INTO course_offerings VALUES ('GE101', 'FAC10', 2020, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('GE102', 'WORKSHOP PRACTICE', 0, 0, 4, 2, 2, '{}');
INSERT INTO faculty VALUES('FAC11', 'DR ANSHU JAYAL', 'ME');
INSERT INTO course_offerings VALUES ('GE102', 'FAC11', 2020, 2, 'ME', 0);

INSERT INTO course_catalog VALUES ('CY101', 'CHEMISTRY FOR ENGINEERS', 3, 1, 2, 6, 4, '{}');
INSERT INTO faculty VALUES('FAC12', 'DR PRABAL BANERJEE', 'CY');
INSERT INTO course_offerings VALUES ('CY101', 'FAC12', 2020, 2, 'CY', 0);

INSERT INTO course_catalog VALUES ('PH102', 'PHYSICS FOR ENGINEERS LAB', 0, 0, 4, 2, 2, '{}');
INSERT INTO faculty VALUES('FAC13', 'DR ASOKA BISWAS', 'PY');
INSERT INTO course_offerings VALUES ('PH102', 'FAC13', 2020, 2, 'PY', 0);

INSERT INTO course_catalog VALUES ('CS201', 'DATA STRUCTURES', 3, 1, 2, 6, 4, '{"GE103"}');
INSERT INTO faculty VALUES('FAC14', 'DR ANIL SHUKLA', 'CS');
INSERT INTO course_offerings VALUES ('CS201', 'FAC14', 2021, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA201', 'DIFFERENTIAL EQUATIONS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC15', 'DR P S DUTTA', 'MA');
INSERT INTO course_offerings VALUES ('MA201', 'FAC15', 2021, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS203', 'DIGITAL LOGIC DESIGN', 3, 1, 3, 6, 4, '{}');
INSERT INTO faculty VALUES('FAC16', 'DR NEERAJ GOEL', 'CS');
INSERT INTO course_offerings VALUES ('CS203', 'FAC16', 2021, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('GE107', 'TINKERING LAB', 0, 0, 3, 3/2, 1.5, '{}');
INSERT INTO faculty VALUES('FAC17', 'DR NITIN AULUCK', 'CS');
INSERT INTO course_offerings VALUES ('GE107', 'FAC17', 2021, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('HS201', 'ECONOMICS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC18', 'DR SAMARESH BARDHAN', 'HS');
INSERT INTO course_offerings VALUES ('HS201', 'FAC18', 2021, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('EE201', 'SIGNALS AND SYSTEMS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC19', 'DR BRIJESH KUMBHANI', 'EE');
INSERT INTO course_offerings VALUES ('EE201', 'FAC19', 2021, 1, 'EE', 0);

INSERT INTO course_catalog VALUES ('HS202', 'HUMAN GEOGRAPHY AND SOCIAL NEEDS', 1, 1/3, 4, 11/3, 3, '{}');
INSERT INTO course_offerings VALUES ('HS202', 'FAC8', 2021, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('MA202', 'PROBABILITY AND STATISTICS', 3, 0, 0, 6, 3, '{}');
INSERT INTO faculty VALUES('FAC20', 'DR ARUN KUMAR', 'MA');
INSERT INTO course_offerings VALUES ('MA202', 'FAC20', 2021, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('GE108', 'BASIC ELECTRONICS', 2, 2/3, 2, 13/3, 3, '{}');
INSERT INTO faculty VALUES('FAC21', 'DR DEVARSHI MRINAL DAS', 'EE');
INSERT INTO course_offerings VALUES ('GE108', 'FAC21', 2021, 2, 'EE', 0);

INSERT INTO course_catalog VALUES ('GE109', 'INTRODUCTION TO ENGINEERING PRODUCTS', 0, 0, 2, 1, 1, '{}');
INSERT INTO faculty VALUES('FAC22', 'DR NAVEEN JAMES', 'CE');
INSERT INTO course_offerings VALUES ('GE109', 'FAC22', 2021, 2, 'CE', 0);

INSERT INTO course_catalog VALUES ('CS202', 'PROGRAMMING PARADIGMS AND PRAGMATICS', 3, 1, 2, 6, 4, '{"CS201"}');
INSERT INTO faculty VALUES('FAC23', 'DR DEEPTI R BATHULA', 'CS');
INSERT INTO course_offerings VALUES ('CS202', 'FAC23', 2021, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS204', 'COMPUTER ARCHITECTURE', 3, 1, 2, 6, 4, '{"CS203", "GE103"}');
INSERT INTO faculty VALUES('FAC24', 'DR SHIRSHENDU DAS', 'CS');
INSERT INTO course_offerings VALUES ('CS204', 'FAC24', 2021, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS101', 'NSS I', 0, 0, 2, 1, 1, '{}');
INSERT INTO faculty VALUES('FAC25', 'DR BALESH KUMAR', 'MA');
INSERT INTO course_offerings VALUES ('NS101', 'FAC25', 2021, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS522', 'SOCIAL COMPUTING AND NETWORKS', 2, 0, 2, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC26', 'DR SUDARSHAN IYENGAR', 'CS');
INSERT INTO course_offerings VALUES ('CS522', 'FAC26', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('HS104', 'PROFESSIONAL ETHICS', 1, 1/3, 1, 13/6, 1.5, '{}');
INSERT INTO faculty VALUES('FAC27', 'DR SREEKUMAR JAYADEVAN', 'HS');
INSERT INTO course_offerings VALUES ('HS104', 'FAC27', 2022, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('GE111', 'INTRODUCTION TO ENVIRONMENTAL SCIENCE AND ENGINEERING', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC28', 'DR SARANG GUMFEKAR', 'CH');
INSERT INTO course_offerings VALUES ('GE111', 'FAC28', 2022, 1, 'CH', 0);

INSERT INTO course_catalog VALUES ('BM101', 'BIOLOGY FOR ENGINEERS', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC29', 'DR BODHISATWA DAS', 'BM');
INSERT INTO course_offerings VALUES ('BM101', 'FAC29', 2022, 1, 'BM', 0);

INSERT INTO course_catalog VALUES ('CS301', 'DATABASES', 3, 1, 2, 6, 4, '{"CS201"}');
INSERT INTO faculty VALUES('FAC30', 'DR VISWANATH GUNTURI', 'CS');
INSERT INTO course_offerings VALUES ('CS301', 'FAC30', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS302', 'ANALYSIS AND DESIGN OF ALGORITHMS', 3, 1, 0, 5, 3, '{"CS101", "CS201"}');
INSERT INTO course_offerings VALUES ('CS302', 'FAC6', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS303', 'OPERATING SYSTEMS', 3, 1, 2, 6, 4, '{"CS201"}');
INSERT INTO course_offerings VALUES ('CS303', 'FAC17', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS102', 'NSS II', 0, 0, 2, 1, 1, '{}');
INSERT INTO course_offerings VALUES ('NS102', 'FAC25', 2022, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS301', 'INDUSTRIAL MANAGEMENT', 3, 1, 0, 5, 3, '{}');
INSERT INTO faculty VALUES('FAC31', 'DR AMRITESH', 'HS');
INSERT INTO course_offerings VALUES ('HS301', 'FAC31', 2022, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('CS304', 'COMPUTER NETWORKS', 3, 1, 2, 6, 4, '{"CS201"}');
INSERT INTO faculty VALUES('FAC32', 'DR SUJATA PAL', 'CS');
INSERT INTO course_offerings VALUES ('CS304', 'FAC32', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS305', 'SOFTWARE ENGINEERING', 3, 1, 2, 6, 4, '{"CS301", "CS303"}');
INSERT INTO faculty VALUES('FAC33', 'DR BALWINDER SODHI', 'CS');
INSERT INTO course_offerings VALUES ('CS305', 'FAC33', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS306', 'THEORY OF COMPUTATION', 3, 1, 0, 5, 3, '{"CS101"}');
INSERT INTO course_offerings VALUES ('CS306', 'FAC14', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CP301', 'DEVELOPMENT ENGINEERING PRODUCT', 0, 0, 6, 3, 3, '{}');
INSERT INTO faculty VALUES('FAC34', 'DR PUNEET GOYAL', 'CS');
INSERT INTO course_offerings VALUES ('CP301', 'FAC34', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS103', 'NSS III', 0, 0, 2, 1, 1, '{}');
INSERT INTO course_offerings VALUES ('NS103', 'FAC25', 2022, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS535', 'INTRODUCTION TO GAME THEORY AND MECHANISM DESIGN', 3, 0, 0, 6, 3, '{}');
INSERT INTO faculty VALUES('FAC35', 'DR SHWETA JAIN', 'CS');
INSERT INTO course_offerings VALUES ('CS535', 'FAC35', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CP302', 'CAPSTONE I', 0, 0, 6, 3, 3, '{}');
INSERT INTO faculty VALUES('FAC36', 'DR ABHINAV DHALL', 'CS');
INSERT INTO course_offerings VALUES ('CP302', 'FAC36', 2023, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('II301', 'INDUSTRIAL INTERNSHIP AND COMPREHENSIVE VIVA', 0, 0, 7, 3.5, 3.5, '{}');
INSERT INTO course_offerings VALUES ('II301', 'FAC36', 2023, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA515', 'FOUNDATIONS OF DATA SCIENCE', 3, 0, 2, 7, 4, '{}');
INSERT INTO course_offerings VALUES ('MA515', 'FAC20', 2023, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS475', 'AN INTRODUCTION TO FANTASY AND SCIENCE FICTION', 3, 0, 0, 6, 3, '{}');
INSERT INTO course_offerings VALUES ('HS475', 'FAC3', 2023, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('CS517', 'DIGITAL IMAGE PROCESSING AND ANALYSIS', 2, 1, 2, 4, 3, '{}');
INSERT INTO course_offerings VALUES ('CS517', 'FAC23', 2023, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('CP303', 'CAPSTONE II', 0, 0, 6, 3, 3, '{"CP302"}');
INSERT INTO course_offerings VALUES ('CP303', 'FAC36', 2023, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS104', 'NSS IV', 0, 0, 2, 1, 1, '{}');
INSERT INTO course_offerings VALUES ('NS104', 'FAC25', 2023, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('MA628', 'FINANCIAL DERIVATIVES PRICING', 3, 0, 2, 7, 4, '{}');
INSERT INTO course_offerings VALUES ('MA628', 'FAC20', 2023, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS550', 'RESEARCH METHODOLOGIES IN COMPUTER SCIENCE', 1, 0, 0, 2, 1, '{}');
INSERT INTO faculty VALUES('FAC37', 'DR MUKESH SAINI', 'CS');
INSERT INTO course_offerings VALUES ('CS550', 'FAC37', 2023, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('HS507', 'POSITIVE PSYCHOLOGY AND WELL-BEING', 3, 0, 0, 6, 3, '{}');
INSERT INTO faculty VALUES('FAC38', 'DR PARWINDER SINGH', 'HS');
INSERT INTO course_offerings VALUES ('HS507', 'FAC38', 2023, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('CS539', 'INTERNET OF THINGS', 3, 0, 0, 6, 3, '{}');
INSERT INTO course_offerings VALUES ('CS539', 'FAC32', 2023, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS999', 'TEST COURSE', 3, 0, 0, 6, 25, '{}');
INSERT INTO faculty VALUES('FAC39', 'TEST FACULTY', 'CS');
INSERT into course_offerings VALUES ('CS999', 'FAC39', 2023, 2, 'CS', 0);

-- Inserting a batch and a curriculum corresponding to that batch
INSERT INTO batch VALUES (2020);
INSERT INTO batch VALUES (2021);
INSERT INTO ug_curriculum VALUES(2020, 24, 6, 23.5, 36, 12, 15, 6, 9, 3.5, 4, 6);
INSERT INTO ug_curriculum VALUES(2021, 24, 6, 23.5, 36, 12, 15, 6, 9, 3.5, 4, 6);

-- Inserting students
INSERT INTO student(entry_number, name, department_id, batch) VALUES ('2020CSB1062', 'ABHIJITH T R', 'CS', 2020);
INSERT INTO student(entry_number, name, department_id, batch) VALUES ('2021CSB1062', 'TEST STUDENT', 'CS', 2021);
INSERT INTO admin(admin_id, name) VALUES ('ADMIN1', 'Dean Office');

-- Inserting core courses can be done using the CSV file
INSERT INTO current_year_and_semester VALUES (2023, 2, 'RUNNING');

-- Inserting all the entries of the student into the database
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE103', 2020, 1, 'A', 'CS', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'MA101', 2020, 1, 'A', 'MA', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS103', 2020, 1, 'A', 'HS', 'HC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'PH101', 2020, 1, 'A', 'PY', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE105', 2020, 1, 'A', 'ME', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS101', 2020, 1, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'MA102', 2020, 2, 'A', 'MA', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS101', 2020, 2, 'A', 'HS', 'HC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE104', 2020, 2, 'A', 'EE', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE101', 2020, 2, 'A', 'HS', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE102', 2020, 2, 'A', 'ME', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CY101', 2020, 2, 'A', 'CY', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'PH102', 2020, 2, 'A', 'PY', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS201', 2021, 1, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'MA201', 2021, 1, 'A', 'MA', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS203', 2021, 1, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE107', 2021, 1, 'A', 'CS', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS201', 2021, 1, 'A', 'HS', 'HC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'EE201', 2021, 1, 'A', 'EE', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS202', 2021, 2, 'A', 'HS', 'HC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'MA202', 2021, 2, 'A', 'MA', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE108', 2021, 2, 'A', 'EE', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE109', 2021, 2, 'A', 'CE', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS202', 2021, 2, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS204', 2021, 2, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'NS101', 2021, 2, 'A', 'MA', 'NN');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS522', 2022, 1, 'A', 'CS', 'PE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS104', 2022, 1, 'A', 'HS', 'HC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'GE111', 2022, 1, 'A', 'CH', 'GR');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'BM101', 2022, 1, 'A', 'BM', 'SC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS301', 2022, 1, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS302', 2022, 1, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS303', 2022, 1, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'NS102', 2022, 1, 'A', 'MA', 'NN');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS301', 2022, 2, 'A', 'HS', 'HC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS304', 2022, 2, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS305', 2022, 2, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS306', 2022, 2, 'A', 'CS', 'PC');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CP301', 2022, 2, 'A', 'CS', 'CP');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'NS103', 2022, 2, 'A', 'MA', 'NN');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS535', 2022, 2, 'A', 'CS', 'PE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CP302', 2023, 1, 'A', 'CS', 'CP');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'II301', 2023, 1, 'A', 'CS', 'II');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'MA515', 2023, 1, 'A', 'MA', 'SE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS475', 2023, 1, 'A', 'HS', 'HE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS517', 2023, 1, 'A', 'CS', 'PE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CP303', 2023, 2, 'A', 'CS', 'CP');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'NS104', 2023, 2, 'A', 'MA', 'NN');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'MA628', 2023, 2, 'A', 'MA', 'SE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS550', 2023, 2, 'A', 'CS', 'PE');
-- INSERT INTO student_course_registration VALUES( '2020CSB1062', 'HS507', 2023, 2, 'A', 'HS', 'HE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS539', 2023, 2, 'A', 'CS', 'PE');
INSERT INTO student_course_registration VALUES( '2020CSB1062', 'CS999', 2023, 2, 'F', 'CS', 'PE');

INSERT INTO instructor_prerequisites VALUES( 'CS539', 2023, 2, 'CS', 'CS303', 8, 1);

INSERT INTO common_user_details VALUES ('ADMIN1', 'iitropar', 'ADMIN');
INSERT INTO common_user_details VALUES ('FAC38', 'iitropar', 'FACULTY');
INSERT INTO common_user_details VALUES ('2020CSB1062', 'iitropar', 'STUDENT');
INSERT INTO common_user_details VALUES ('2021CSB1062', 'iitropar', 'STUDENT');

INSERT INTO core_courses VALUES( 'GE103', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'MA101', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'HS103', 'CS', 2020, 'HC');
INSERT INTO core_courses VALUES( 'PH101', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'GE105', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'CS101', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'MA102', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'HS101', 'CS', 2020, 'HC');
INSERT INTO core_courses VALUES( 'GE104', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'GE101', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'GE102', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'CY101', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'PH102', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'CS201', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'MA201', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'CS203', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'GE107', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'HS201', 'CS', 2020, 'HC');
INSERT INTO core_courses VALUES( 'EE201', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'HS202', 'CS', 2020, 'HC');
INSERT INTO core_courses VALUES( 'MA202', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'GE108', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'GE109', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'CS202', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'CS204', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'NS101', 'CS', 2020, 'NN');
INSERT INTO core_courses VALUES( 'HS104', 'CS', 2020, 'HC');
INSERT INTO core_courses VALUES( 'GE111', 'CS', 2020, 'GR');
INSERT INTO core_courses VALUES( 'BM101', 'CS', 2020, 'SC');
INSERT INTO core_courses VALUES( 'CS301', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'CS302', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'NS102', 'CS', 2020, 'NN');
INSERT INTO core_courses VALUES( 'CS303', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'HS301', 'CS', 2020, 'HC');
INSERT INTO core_courses VALUES( 'CS304', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'CS305', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'CS306', 'CS', 2020, 'PC');
INSERT INTO core_courses VALUES( 'CP301', 'CS', 2020, 'CP');
INSERT INTO core_courses VALUES( 'NS103', 'CS', 2020, 'NN');
INSERT INTO core_courses VALUES( 'NS104', 'CS', 2020, 'NN');
INSERT INTO core_courses VALUES( 'CP302', 'CS', 2020, 'CP');
INSERT INTO core_courses VALUES( 'CP303', 'CS', 2020, 'CP');
INSERT INTO core_courses VALUES( 'II301', 'CS', 2020, 'II');

INSERT INTO course_category VALUES( 'GE103', 2020, 1, 'CS', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'MA101', 2020, 1, 'MA', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS103', 2020, 1, 'HS', 'HC', 2020, 'CS');
INSERT INTO course_category VALUES( 'PH101', 2020, 1, 'PY', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE105', 2020, 1, 'ME', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS101', 2020, 1, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'MA102', 2020, 2, 'MA', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS101', 2020, 2, 'HS', 'HC', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE104', 2020, 2, 'EE', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE101', 2020, 2, 'HS', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE102', 2020, 2, 'ME', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'CY101', 2020, 2, 'CY', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'PH102', 2020, 2, 'PY', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS201', 2021, 1, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'MA201', 2021, 1, 'MA', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS203', 2021, 1, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE107', 2021, 1, 'CS', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS201', 2021, 1, 'HS', 'HC', 2020, 'CS');
INSERT INTO course_category VALUES( 'EE201', 2021, 1, 'EE', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS202', 2021, 2, 'HS', 'HC', 2020, 'CS');
INSERT INTO course_category VALUES( 'MA202', 2021, 2, 'MA', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE108', 2021, 2, 'EE', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE109', 2021, 2, 'CE', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS202', 2021, 2, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS204', 2021, 2, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'NS101', 2021, 2, 'MA', 'NN', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS522', 2022, 1, 'CS', 'PE', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS104', 2022, 1, 'HS', 'HC', 2020, 'CS');
INSERT INTO course_category VALUES( 'GE111', 2022, 1, 'CH', 'GR', 2020, 'CS');
INSERT INTO course_category VALUES( 'BM101', 2022, 1, 'BM', 'SC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS301', 2022, 1, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS302', 2022, 1, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS303', 2022, 1, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'NS102', 2022, 1, 'MA', 'NN', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS301', 2022, 2, 'HS', 'HC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS304', 2022, 2, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS305', 2022, 2, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS306', 2022, 2, 'CS', 'PC', 2020, 'CS');
INSERT INTO course_category VALUES( 'CP301', 2022, 2, 'CS', 'CP', 2020, 'CS');
INSERT INTO course_category VALUES( 'NS103', 2022, 2, 'MA', 'NN', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS535', 2022, 2, 'CS', 'PE', 2020, 'CS');
INSERT INTO course_category VALUES( 'CP302', 2023, 1, 'CS', 'CP', 2020, 'CS');
INSERT INTO course_category VALUES( 'II301', 2023, 1, 'CS', 'II', 2020, 'CS');
INSERT INTO course_category VALUES( 'MA515', 2023, 1, 'MA', 'SE', 2020, 'CS');
INSERT INTO course_category VALUES( 'HS475', 2023, 1, 'HS', 'HE', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS517', 2023, 1, 'CS', 'PE', 2020, 'CS');
INSERT INTO course_category VALUES( 'CP303', 2023, 2, 'CS', 'CP', 2020, 'CS');
INSERT INTO course_category VALUES( 'NS104', 2023, 2, 'MA', 'NN', 2020, 'CS');
INSERT INTO course_category VALUES( 'MA628', 2023, 2, 'MA', 'SE', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS550', 2023, 2, 'CS', 'PE', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS539', 2023, 2, 'CS', 'PE', 2020, 'CS');
INSERT INTO course_category VALUES( 'CS999', 2023, 2, 'CS', 'PE', 2020, 'CS');