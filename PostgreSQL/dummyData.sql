-- Creating all the departments in the institute
INSERT INTO department VALUES ('CS', 'Computer Science and Engineering');
INSERT INTO department VALUES ('BM', 'Biomedical Engineering');
INSERT INTO department VALUES ('CH', 'Chemical Engineering');
INSERT INTO department VALUES ('CE', 'Civil Engineering');
INSERT INTO department VALUES ('EE', 'Electrical Engineering');
INSERT INTO department VALUES ('ME', 'Mechanical Engineering');
INSERT INTO department VALUES ('MME', 'Metallurgical and Materials Engineering');
INSERT INTO department VALUES ('CY', 'Chemistry');
INSERT INTO department VALUES ('PY', 'Physics');
INSERT INTO department VALUES ('MA', 'Mathematics');
INSERT INTO department VALUES ('HS', 'Humanities and Social Sciences');

-- Creating the course catalog
INSERT INTO course_catalog VALUES ('GE103', 'INTRODUCTION TO COMPUTING AND DATA STRUCTURES', 3, 0, 3, 7, 4.5, '{}', 'CS');
INSERT INTO faculty VALUES ('FAC1', 'DR SHIRSHENDU DAS', 'CS');
INSERT INTO course_offerings VALUES ('GE103', 'FAC1', 2020, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA101', 'CALCULUS', 3, 1, 0, 5, 3, '{}', 'MA');
INSERT INTO faculty VALUES('FAC2', 'DR KAUSHIK MONDAL', 'MA');
INSERT INTO course_offerings VALUES ('MA101', 'FAC2', 2020, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS103', 'PROFESSIONAL ENGLISH COMMUNICATION', 2, 0, 2, 5, 3, '{}', 'HS');
INSERT INTO faculty VALUES('FAC3', 'DR RANO RINGO', 'HS');
INSERT INTO course_offerings VALUES ('HS103', 'FAC3', 2020, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('PH101', 'PHYSICS FOR ENGINEERS', 3, 1, 0, 5, 3, '{}', 'PY');
INSERT INTO faculty VALUES('FAC4', 'DR DEEPIKA CHOUDHARY', 'PY');
INSERT INTO course_offerings VALUES ('PH101', 'FAC4', 2020, 1, 'PY', 0);

INSERT INTO course_catalog VALUES ('GE105', 'ENGINEERING DRAWING', 0, 0, 3, 1.5, 1.5, '{}', 'ME');
INSERT INTO faculty VALUES('FAC5', 'DR EKTA SINGLA', 'ME');
INSERT INTO course_offerings VALUES ('GE105', 'FAC5', 2020, 1, 'ME', 0);

INSERT INTO course_catalog VALUES ('CS101', 'DISCRETE MATHEMATICAL STRUCTURES', 3, 1, 0, 5, 3, '{}', 'CS');
INSERT INTO faculty VALUES('FAC6', 'DR APURVA MUDGAL', 'CS');
INSERT INTO course_offerings VALUES ('CS101', 'FAC6', 2020, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA102', 'LINEAR ALGEBRA, INTEGRAL TRANSFORMS AND SPECIAL FUNCTIONS', 3, 1, 0, 5, 3, '{}', 'MA');
INSERT INTO faculty VALUES('FAC7', 'DR MANJU KHAN', 'MA');
INSERT INTO course_offerings VALUES ('MA102', 'FAC7', 2020, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS101', 'HISTORY OF TECHNOLOGY', 1.5, 0.5, 0, 2.5, 1.5, '{}', 'HS');
INSERT INTO faculty VALUES('FAC8', 'DR KAMAL CHAUDHARY', 'HS');
INSERT INTO course_offerings VALUES ('HS101', 'FAC8', 2020, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('GE104', 'INTRODUCTION TO ELECTRICAL ENGINEERING', 2, 2/3, 2, 13/3, 3, '{}', 'EE');
INSERT INTO faculty VALUES('FAC9', 'DR KALAISELVI J', 'EE');
INSERT INTO course_offerings VALUES ('GE104', 'FAC9', 2020, 2, 'EE', 0);

INSERT INTO course_catalog VALUES ('GE101', 'TECHNOLOGY MUSEUM LAB', 0, 0, 2, 1, 1, '{}', 'HS');
INSERT INTO faculty VALUES('FAC10', 'DR RAJESH GUPTA', 'HS');
INSERT INTO course_offerings VALUES ('GE101', 'FAC10', 2020, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('GE102', 'WORKSHOP PRACTICE', 0, 0, 4, 2, 2, '{}', 'ME');
INSERT INTO faculty VALUES('FAC11', 'DR ANSHU JAYAL', 'ME');
INSERT INTO course_offerings VALUES ('GE102', 'FAC11', 2020, 2, 'ME', 0);

INSERT INTO course_catalog VALUES ('CY101', 'CHEMISTRY FOR ENGINEERS', 3, 1, 2, 6, 4, '{}', 'CY');
INSERT INTO faculty VALUES('FAC12', 'DR PRABAL BANERJEE', 'CY');
INSERT INTO course_offerings VALUES ('CY101', 'FAC12', 2020, 2, 'CY', 0);

INSERT INTO course_catalog VALUES ('PH102', 'PHYSICS FOR ENGINEERS LAB', 0, 0, 4, 2, 2, '{}', 'PY');
INSERT INTO faculty VALUES('FAC13', 'DR ASOKA BISWAS', 'PY');
INSERT INTO course_offerings VALUES ('PH102', 'FAC13', 2020, 2, 'PY', 0);

INSERT INTO course_catalog VALUES ('CS201', 'DATA STRUCTURES', 3, 1, 2, 6, 4, '{"GE103"}', 'CS');
INSERT INTO faculty VALUES('FAC14', 'DR ANIL SHUKLA', 'CS');
INSERT INTO course_offerings VALUES ('CS201', 'FAC14', 2021, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA201', 'DIFFERENTIAL EQUATIONS', 3, 1, 0, 5, 3, '{}', 'MA');
INSERT INTO faculty VALUES('FAC15', 'DR P S DUTTA', 'MA');
INSERT INTO course_offerings VALUES ('MA201', 'FAC15', 2021, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS203', 'DIGITAL LOGIC DESIGN', 3, 1, 3, 6, 4, '{}', 'CS');
INSERT INTO faculty VALUES('FAC16', 'DR NEERAJ GOEL', 'CS');
INSERT INTO course_offerings VALUES ('CS203', 'FAC16', 2021, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('GE107', 'TINKERING LAB', 0, 0, 3, 3/2, 1.5, '{}', 'CS');
INSERT INTO faculty VALUES('FAC17', 'DR NITIN AULUCK', 'CS');
INSERT INTO course_offerings VALUES ('GE107', 'FAC17', 2021, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('HS201', 'ECONOMICS', 3, 1, 0, 5, 3, '{}', 'HS');
INSERT INTO faculty VALUES('FAC18', 'DR SAMARESH BARDHAN', 'HS');
INSERT INTO course_offerings VALUES ('HS201', 'FAC18', 2021, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('EE201', 'SIGNALS AND SYSTEMS', 3, 1, 0, 5, 3, '{}', 'EE');
INSERT INTO faculty VALUES('FAC19', 'DR BRIJESH KUMBHANI', 'EE');
INSERT INTO course_offerings VALUES ('EE201', 'FAC19', 2021, 1, 'EE', 0);

INSERT INTO course_catalog VALUES ('HS202', 'HUMAN GEOGRAPHY AND SOCIAL NEEDS', 1, 1/3, 4, 11/3, 3, '{}', 'HS');
INSERT INTO course_offerings VALUES ('HS202', 'FAC8', 2021, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('MA202', 'PROBABILITY AND STATISTICS', 3, 0, 0, 6, 3, '{}', 'MA');
INSERT INTO faculty VALUES('FAC20', 'DR ARUN KUMAR', 'MA');
INSERT INTO course_offerings VALUES ('MA202', 'FAC20', 2021, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('GE108', 'BASIC ELECTRONICS', 2, 2/3, 2, 13/3, 3, '{}', 'EE');
INSERT INTO faculty VALUES('FAC21', 'DR DEVARSHI MRINAL DAS', 'EE');
INSERT INTO course_offerings VALUES ('GE108', 'FAC21', 2021, 2, 'EE', 0);

INSERT INTO course_catalog VALUES ('GE109', 'INTRODUCTION TO ENGINEERING PRODUCTS', 0, 0, 2, 1, 1, '{}', 'CE');
INSERT INTO faculty VALUES('FAC22', 'DR NAVEEN JAMES', 'CE');
INSERT INTO course_offerings VALUES ('GE109', 'FAC22', 2021, 2, 'CE', 0);

INSERT INTO course_catalog VALUES ('CS202', 'PROGRAMMING PARADIGMS AND PRAGMATICS', 3, 1, 2, 6, 4, '{"CS201"}', 'CS');
INSERT INTO faculty VALUES('FAC23', 'DR DEEPTI R BATHULA', 'CS');
INSERT INTO course_offerings VALUES ('CS202', 'FAC23', 2021, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS204', 'COMPUTER ARCHITECTURE', 3, 1, 2, 6, 4, '{"CS203", "GE103"}', 'CS');
INSERT INTO faculty VALUES('FAC24', 'DR SHIRSHENDU DAS', 'CS');
INSERT INTO course_offerings VALUES ('CS204', 'FAC24', 2021, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS101', 'NSS I', 0, 0, 2, 1, 1, '{}', 'MA');
INSERT INTO faculty VALUES('FAC25', 'DR BALESH KUMAR', 'MA');
INSERT INTO course_offerings VALUES ('NS101', 'FAC25', 2021, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS522', 'SOCIAL COMPUTING AND NETWORKS', 2, 0, 2, 5, 3, '{}', 'CS');
INSERT INTO faculty VALUES('FAC26', 'DR SUDARSHAN IYENGAR', 'CS');
INSERT INTO course_offerings VALUES ('CS522', 'FAC26', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('HS104', 'PROFESSIONAL ETHICS', 1, 1/3, 1, 13/6, 1.5, '{}', 'HS');
INSERT INTO faculty VALUES('FAC27', 'DR SREEKUMAR JAYADEVAN', 'HS');
INSERT INTO course_offerings VALUES ('HS104', 'FAC27', 2022, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('GE111', 'INTRODUCTION TO ENVIRONMENTAL SCIENCE AND ENGINEERING', 3, 1, 0, 5, 3, '{}', 'CH');
INSERT INTO faculty VALUES('FAC28', 'DR SARANG GUMFEKAR', 'CH');
INSERT INTO course_offerings VALUES ('GE111', 'FAC28', 2022, 1, 'CH', 0);

INSERT INTO course_catalog VALUES ('BM101', 'BIOLOGY FOR ENGINEERS', 3, 1, 0, 5, 3, '{}', 'BM');
INSERT INTO faculty VALUES('FAC29', 'DR BODHISATWA DAS', 'BM');
INSERT INTO course_offerings VALUES ('BM101', 'FAC29', 2022, 1, 'BM', 0);

INSERT INTO course_catalog VALUES ('CS301', 'DATABASES', 3, 1, 2, 6, 4, '{"CS201"}', 'CS');
INSERT INTO faculty VALUES('FAC30', 'DR VISWANATH GUNTURI', 'CS');
INSERT INTO course_offerings VALUES ('CS301', 'FAC30', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS302', 'ANALYSIS AND DESIGN OF ALGORITHMS', 3, 1, 0, 5, 3, '{"CS101", "CS201"}', 'CS');
INSERT INTO course_offerings VALUES ('CS302', 'FAC6', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS303', 'OPERATING SYSTEMS', 3, 1, 2, 6, 4, '{"CS201"}', 'CS');
INSERT INTO course_offerings VALUES ('CS303', 'FAC17', 2022, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS102', 'NSS II', 0, 0, 2, 1, 1, '{}', 'MA');
INSERT INTO course_offerings VALUES ('NS102', 'FAC25', 2022, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS301', 'INDUSTRIAL MANAGEMENT', 3, 1, 0, 5, 3, '{}', 'HS');
INSERT INTO faculty VALUES('FAC31', 'DR AMRITESH', 'HS');
INSERT INTO course_offerings VALUES ('HS301', 'FAC31', 2022, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('CS304', 'COMPUTER NETWORKS', 3, 1, 2, 6, 4, '{"CS201"}', 'CS');
INSERT INTO faculty VALUES('FAC32', 'DR SUJATA PAL', 'CS');
INSERT INTO course_offerings VALUES ('CS304', 'FAC32', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS305', 'SOFTWARE ENGINEERING', 3, 1, 2, 6, 4, '{"CS301", "CS303"}', 'CS');
INSERT INTO faculty VALUES('FAC33', 'DR BALWINDER SODHI', 'CS');
INSERT INTO course_offerings VALUES ('CS305', 'FAC33', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CS306', 'THEORY OF COMPUTATION', 3, 1, 0, 5, 3, '{"CS101"}', 'CS');
INSERT INTO course_offerings VALUES ('CS306', 'FAC14', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CP301', 'DEVELOPMENT ENGINEERING PRODUCT', 0, 0, 6, 3, 3, '{}', 'CS');
INSERT INTO faculty VALUES('FAC34', 'DR PUNEET GOYAL', 'CS');
INSERT INTO course_offerings VALUES ('CP301', 'FAC34', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS103', 'NSS III', 0, 0, 2, 1, 1, '{}', 'MA');
INSERT INTO course_offerings VALUES ('NS103', 'FAC25', 2022, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS535', 'INTRODUCTION TO GAME THEORY AND MECHANISM DESIGN', 3, 0, 0, 6, 3, '{}', 'CS');
INSERT INTO faculty VALUES('FAC35', 'DR SHWETA JAIN', 'CS');
INSERT INTO course_offerings VALUES ('CS535', 'FAC35', 2022, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('CP302', 'CAPSTONE I', 0, 0, 6, 3, 3, '{}', 'CS');
INSERT INTO faculty VALUES('FAC36', 'DR ABHINAV DHALL', 'CS');
INSERT INTO course_offerings VALUES ('CP302', 'FAC36', 2023, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('II301', 'INDUSTRIAL INTERNSHIP AND COMPREHENSIVE VIVA', 0, 0, 7, 3.5, 3.5, '{}', 'CS');
INSERT INTO course_offerings VALUES ('II301', 'FAC36', 2023, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('MA515', 'FOUNDATIONS OF DATA SCIENCE', 3, 0, 2, 7, 4, '{}', 'MA');
INSERT INTO course_offerings VALUES ('MA515', 'FAC20', 2023, 1, 'MA', 0);

INSERT INTO course_catalog VALUES ('HS475', 'AN INTRODUCTION TO FANTASY AND SCIENCE FICTION', 3, 0, 0, 6, 3, '{}', 'HS');
INSERT INTO course_offerings VALUES ('HS475', 'FAC3', 2023, 1, 'HS', 0);

INSERT INTO course_catalog VALUES ('CS517', 'DIGITAL IMAGE PROCESSING AND ANALYSIS', 2, 1, 2, 4, 3, '{}', 'CS');
INSERT INTO course_offerings VALUES ('CS517', 'FAC23', 2023, 1, 'CS', 0);

INSERT INTO course_catalog VALUES ('CP303', 'CAPSTONE II', 0, 0, 6, 3, 3, '{"CP302"}', 'CS');
INSERT INTO course_offerings VALUES ('CP303', 'FAC36', 2023, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('NS104', 'NSS IV', 0, 0, 2, 1, 1, '{}', 'MA');
INSERT INTO course_offerings VALUES ('NS104', 'FAC25', 2023, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('MA628', 'FINANCIAL DERIVATIVES PRICING', 3, 0, 2, 7, 4, '{}', 'MA');
INSERT INTO course_offerings VALUES ('MA628', 'FAC20', 2023, 2, 'MA', 0);

INSERT INTO course_catalog VALUES ('CS550', 'RESEARCH METHODOLOGIES IN COMPUTER SCIENCE', 1, 0, 0, 2, 1, '{}', 'CS');
INSERT INTO faculty VALUES('FAC37', 'DR MUKESH SAINI', 'CS');
INSERT INTO course_offerings VALUES ('CS550', 'FAC37', 2023, 2, 'CS', 0);

INSERT INTO course_catalog VALUES ('HS507', 'POSITIVE PSYCHOLOGY AND WELL-BEING', 3, 0, 0, 6, 3, '{}', 'HS');
INSERT INTO faculty VALUES('FAC38', 'DR PARWINDER SINGH', 'HS');
INSERT INTO course_offerings VALUES ('HS507', 'FAC38', 2023, 2, 'HS', 0);

INSERT INTO course_catalog VALUES ('CS539', 'INTERNET OF THINGS', 3, 0, 0, 6, 3, '{}', 'CS');
INSERT INTO course_offerings VALUES ('CS539', 'FAC32', 2023, 2, 'CS', 0);