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
    Deleted BOOLEAN,
    PRIMARY KEY (IDCourse)
);
INSERT INTO iumtweb.Courses (Title, Deleted) VALUES ('Programmazione III', False), ('Basi di Dati', False),('Sistemi Operativi', False),('Analisi I', False),('Fisica I', False),('Reti I', False),('Logica', False);
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
    Deleted BOOLEAN,
    PRIMARY KEY (IDTeacher)
);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'marinosegnan@unito.it', 'Segnan', 'Marino', False); 
INSERT INTO iumtweb.Teachers VALUES (NULL, 'ardissonoliliana@unito.it', 'Ardissono', 'Liliana', False);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'espositoroberto@unito.it', 'Esposito', 'Roberto', False);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'bottamarco@unito.it', 'Botta', 'Marco', False);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'pensaruggero@unito.it', 'Pensa', 'Ruggero', False);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'barogliocristina@unito.it', 'Baroglio', 'Cristina', False);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'aringhieriroberto@unito.it', 'Aringhieri', 'Roberto', False);
INSERT INTO iumtweb.Teachers VALUES (NULL, 'binienrico@unito.it', 'Bini', 'Enrico', False);
CREATE TABLE iumtweb.Teaches(
    IDTeacher MEDIUMINT NOT NULL,
    IDCourse MEDIUMINT NOT NULL,
    Deleted BOOLEAN,
    PRIMARY KEY (IDTeacher, IDCourse),
    FOREIGN KEY (IDTeacher) REFERENCES Teachers(IDTeacher) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (IDCourse) REFERENCES Courses(IDCourse) ON UPDATE CASCADE ON DELETE NO ACTION
);
INSERT INTO iumtweb.Teaches VALUES (1,1, False);
INSERT INTO iumtweb.Teaches VALUES (1,3, False);
INSERT INTO iumtweb.Teaches VALUES (1,4, False);
INSERT INTO iumtweb.Teaches VALUES (1,5, False);
INSERT INTO iumtweb.Teaches VALUES (1,7, False);
INSERT INTO iumtweb.Teaches VALUES (2,2, False);
INSERT INTO iumtweb.Teaches VALUES (2,4, False);
INSERT INTO iumtweb.Teaches VALUES (2,6, False);
INSERT INTO iumtweb.Teaches VALUES (3,1, False);
INSERT INTO iumtweb.Teaches VALUES (3,5, False);
INSERT INTO iumtweb.Teaches VALUES (3,7, False);
INSERT INTO iumtweb.Teaches VALUES (4,1, False);
INSERT INTO iumtweb.Teaches VALUES (4,2, False);
INSERT INTO iumtweb.Teaches VALUES (4,3, False);
INSERT INTO iumtweb.Teaches VALUES (4,4, False);
INSERT INTO iumtweb.Teaches VALUES (5,7, False);
INSERT INTO iumtweb.Teaches VALUES (5,6, False);
INSERT INTO iumtweb.Teaches VALUES (5,5, False);
INSERT INTO iumtweb.Teaches VALUES (5,4, False);
INSERT INTO iumtweb.Teaches VALUES (6,1, False);
INSERT INTO iumtweb.Teaches VALUES (6,3, False);
INSERT INTO iumtweb.Teaches VALUES (6,6, False);
INSERT INTO iumtweb.Teaches VALUES (6,7, False);
INSERT INTO iumtweb.Teaches VALUES (7,3, False);
INSERT INTO iumtweb.Teaches VALUES (7,5, False);
INSERT INTO iumtweb.Teaches VALUES (7,6, False);
INSERT INTO iumtweb.Teaches VALUES (8,1, False);
INSERT INTO iumtweb.Teaches VALUES (8,2, False);
INSERT INTO iumtweb.Teaches VALUES (8,4, False);
INSERT INTO iumtweb.Teaches VALUES (8,5, False);
CREATE TABLE iumtweb.Repetitions(
    IDRepetition MEDIUMINT NOT NULL AUTO_INCREMENT,
    Day VARCHAR(20) NOT NULL CHECK (Day='Monday' OR Day='Tuesday' OR Day='Wednesday' OR Day='Thursday' OR Day='Friday'),
    StartTime VARCHAR(10) NOT NULL CHECK (StartTime='15:00' OR StartTime='16:00' OR StartTime='17:00' OR StartTime='18:00' OR StartTime='19:00'),
    IDCourse MEDIUMINT NOT NULL,
    IDTeacher MEDIUMINT NOT NULL,
    Account VARCHAR(50) NOT NULL,
    State VARCHAR(10) NOT NULL CHECK (State='Active' OR State='Cancelled' OR State='Done'),
    PRIMARY KEY (IDRepetition),
    FOREIGN KEY (IDTeacher, IDCourse) REFERENCES Teaches(IDTeacher, IDCourse) ON UPDATE CASCADE ON DELETE NO ACTION,
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