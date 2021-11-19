DROP DATABASE IF EXISTS iumtweb;
CREATE DATABASE iumtweb
    collate utf8_general_ci;

DROP TABLE if exists iumtweb.Repetitions;
DROP TABLE if exists iumtweb.Teaches;
DROP TABLE if exists iumtweb.Teachers;
DROP TABLE if exists iumtweb.Users;
DROP TABLE if exists iumtweb.Courses;

CREATE TABLE iumtweb.Courses(
    IDCourse MEDIUMINT NOT NULL AUTO_INCREMENT,
    Title VARCHAR(50) NOT NULL,
    PRIMARY KEY (IDCourse)
);
INSERT INTO iumtweb.Courses (Title) VALUES ('Programmazione III'),('Basi di Dati'),('Sistemi Operativi'),('Analisi I'),('Fisica I'),('Reti I'),('Logica');
CREATE TABLE iumtweb.Users(
    Account VARCHAR(50) NOT NULL PRIMARY KEY,
    Pwd VARCHAR(50) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Surname VARCHAR(50) NOT NULL,
    Role VARCHAR(20) NOT NULL CHECK (Role='Client' OR Role='Administrator')
);
INSERT INTO iumtweb.Users VALUES ('admin1@email.com', '2E33A9B0B06AA0A01EDE70995674EE23', 'Fabrizio', 'Sanino', 'Administrator'); 
# pwd = Admin1
INSERT INTO iumtweb.Users VALUES ('client1@email.com', '5FEC6C40FD245A243C32B3DB49013D45', 'Nicole', 'Gazzera', 'Client');
# pwd = Client1
INSERT INTO iumtweb.Users VALUES ('client2@email.com', '2FE7E2F7D7692ED9B5ADF75DCEF80FD1', 'Giacomo', 'Perlo','Client');
# pwd = Client2
CREATE TABLE iumtweb.Teachers(
    IDTeacher MEDIUMINT NOT NULL AUTO_INCREMENT,
    Mail VARCHAR(50) NOT NULL,
    Surname VARCHAR(50) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    PRIMARY KEY (IDTeacher)
);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'marinosegnan@unito.it', 'Segnan', 'Marino'); 
INSERT INTO iumtweb.Teachers VALUES (NULL, 'ardissonoliliana@unito.it', 'Ardissono', 'Liliana');
INSERT INTO iumtweb.Teachers VALUES (NULL, 'espositoroberto@unito.it', 'Esposito', 'Roberto');
INSERT INTO iumtweb.Teachers VALUES (NULL, 'bottamarco@unito.it', 'Botta', 'Marco');
INSERT INTO iumtweb.Teachers VALUES (NULL, 'pensaruggero@unito.it', 'Pensa', 'Ruggero');
INSERT INTO iumtweb.Teachers VALUES (NULL, 'barogliocristina@unito.it', 'Baroglio', 'Cristina');
INSERT INTO iumtweb.Teachers VALUES (NULL, 'aringhieriroberto@unito.it', 'Aringhieri', 'Roberto');
INSERT INTO iumtweb.Teachers VALUES (NULL, 'binienrico@unito.it', 'Bini', 'Enrico');
CREATE TABLE iumtweb.Teaches(
    IDTeacher MEDIUMINT NOT NULL,
    IDCourse MEDIUMINT NOT NULL,
    PRIMARY KEY (IDTeacher, IDCourse),
    FOREIGN KEY (IDTeacher) REFERENCES Teachers(IDTeacher) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (IDCourse) REFERENCES Courses(IDCourse) ON UPDATE CASCADE ON DELETE CASCADE
);
INSERT INTO iumtweb.Teaches VALUES (1,1);
INSERT INTO iumtweb.Teaches VALUES (1,3);
INSERT INTO iumtweb.Teaches VALUES (1,4);
INSERT INTO iumtweb.Teaches VALUES (1,5);
INSERT INTO iumtweb.Teaches VALUES (1,7);
INSERT INTO iumtweb.Teaches VALUES (2,2);
INSERT INTO iumtweb.Teaches VALUES (2,4);
INSERT INTO iumtweb.Teaches VALUES (2,6);
INSERT INTO iumtweb.Teaches VALUES (3,1);
INSERT INTO iumtweb.Teaches VALUES (3,5);
INSERT INTO iumtweb.Teaches VALUES (3,7);
INSERT INTO iumtweb.Teaches VALUES (4,1);
INSERT INTO iumtweb.Teaches VALUES (4,2);
INSERT INTO iumtweb.Teaches VALUES (4,3);
INSERT INTO iumtweb.Teaches VALUES (4,4);
INSERT INTO iumtweb.Teaches VALUES (5,7);
INSERT INTO iumtweb.Teaches VALUES (5,6);
INSERT INTO iumtweb.Teaches VALUES (5,5);
INSERT INTO iumtweb.Teaches VALUES (5,4);
INSERT INTO iumtweb.Teaches VALUES (6,1);
INSERT INTO iumtweb.Teaches VALUES (6,3);
INSERT INTO iumtweb.Teaches VALUES (6,6);
INSERT INTO iumtweb.Teaches VALUES (6,7);
INSERT INTO iumtweb.Teaches VALUES (7,3);
INSERT INTO iumtweb.Teaches VALUES (7,5);
INSERT INTO iumtweb.Teaches VALUES (7,6);
INSERT INTO iumtweb.Teaches VALUES (8,1);
INSERT INTO iumtweb.Teaches VALUES (8,2);
INSERT INTO iumtweb.Teaches VALUES (8,4);
INSERT INTO iumtweb.Teaches VALUES (8,5);
CREATE TABLE iumtweb.Repetitions(
    IDRepetition MEDIUMINT NOT NULL AUTO_INCREMENT,
    Day VARCHAR(20) NOT NULL CHECK (Day='Monday' OR Day='Tuesday' OR Day='Wednesday' OR Day='Thursday' OR Day='Friday'),
    StartTime VARCHAR(10) NOT NULL CHECK (StartTime='15:00' OR StartTime='16:00' OR StartTime='17:00' OR StartTime='18:00' OR StartTime='19:00'),
    IDCourse MEDIUMINT NOT NULL,
    IDTeacher MEDIUMINT NOT NULL,
    Account VARCHAR(50) NOT NULL,
    State VARCHAR(10) NOT NULL CHECK (State='Active' OR State='Cancelled' OR State='Done'),
    PRIMARY KEY (IDRepetition),
    FOREIGN KEY (IDTeacher, IDCourse) REFERENCES Teaches(IDTeacher, IDCourse) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Account) REFERENCES Users(Account) ON UPDATE CASCADE ON DELETE CASCADE
);
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Monday", "15:00", 6, 2, "client1@email.com", "Active");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Monday", "17:00", 5, 5, "client1@email.com", "Active");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Thursday", "18:00", 4, 4, "client1@email.com", "Done");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Wednesday", "16:00", 7, 3, "client1@email.com", "Cancelled");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Tuesday", "15:00", 2, 4, "client1@email.com", "Done");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Monday", "16:00", 1, 8, "client2@email.com", "Active");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Wednesday", "17:00", 3, 4, "client2@email.com", "Active");
INSERT INTO iumtweb.Repetitions VALUES (NULL, "Friday", "19:00", 6, 2, "client2@email.com", "Cancelled");