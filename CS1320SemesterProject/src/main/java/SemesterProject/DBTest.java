package SemesterProject;

import SemesterProject.Database.Database;

/**
 * Tests the SemesterProject.Database class and prints out results
 *
 * @author Yitzie Scheinman
 * @version 5.17.2017
 */

public class DBTest
{
    private static Database db = new Database();

    public DBTest()
    {

    }

    public static void main(String[] args) {
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //CREATE TABLE TESTS
        //Create two tables
        //Print empty table if each create is successful
        System.out.println("CREATE TABLE TESTS: Print empty tables if successful");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("CREATE TABLE YCStudent (BannerID int, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 3.00, CurrentStudent boolean DEFAULT true, Class varchar(255), PRIMARY KEY (BannerID));");
        System.out.println();
        System.out.println(db.execute("CREATE TABLE YCStudent (BannerID int, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 3.00, CurrentStudent boolean DEFAULT true, Class varchar(255), PRIMARY KEY (BannerID));"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("CREATE TABLE SternStudent (BannerID int, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 3.00, CurrentStudent boolean DEFAULT true, Class varchar(255), PRIMARY KEY (BannerID));");
        System.out.println();
        System.out.println(db.execute("CREATE TABLE SternStudent (BannerID int, FirstName varchar(255), LastName varchar(255) NOT NULL, GPA decimal(1,2) DEFAULT 3.00, CurrentStudent boolean DEFAULT true, Class varchar(255), PRIMARY KEY (BannerID));"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //CREATE INDEX TESTS
        //Create indexes on a column in each table
        //Print true if each create is successful
        System.out.println("CREATE INDEX TESTS: Print true if successful");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("CREATE INDEX FirstName_Index on YCStudent (FirstName);");
        System.out.println();
        System.out.println(db.execute("CREATE INDEX FirstName_Index on YCStudent (FirstName);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("CREATE INDEX LastName_Index on SternStudent (LastName);");
        System.out.println();
        System.out.println(db.execute("CREATE INDEX LastName_Index on SternStudent (LastName);"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //INSERT TESTS
        //Insert six rows into each table, plus one row referencing a nonexistent column/table
        //Print true if each insert is successful
        System.out.println("INSERT TESTS: Print true if successful, and print full tables");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Ploni', 'Almoni', 4.00, 'Senior', 800012345);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Ploni', 'Almoni', 4.00, 'Senior', 800012345);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO YCStudent (FirstName, LastName, CurrentStudent, BannerID) VALUES ('Dani', 'Scheinman', false, 800887620);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO YCStudent (FirstName, LastName, CurrentStudent, BannerID) VALUES ('Dani', 'Scheinman', false, 800887620);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO YCStudent (FirstName, LastName, BannerID, Class, GPA) VALUES ('Yitzie', 'Scheinman', 800323810, 'Junior', 3.950);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO YCStudent (FirstName, LastName, BannerID, Class, GPA) VALUES ('Yitzie', 'Scheinman', 800323810, 'Junior', 3.90);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO YCStudent (FirstName, LastName, BannerID, CurrentStudent) VALUES ('Judah', 'Diament', 800987643, false);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO YCStudent (FirstName, LastName, BannerID, CurrentStudent) VALUES ('Judah', 'Diament', 800987643, false);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO YCStudent (FirstName, LastName, GPA, BannerID, Class) VALUES ('Ariel', 'Sacknovitz', 3.75, 800555666, 'Junior');");
        System.out.println();
        System.out.println(db.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, BannerID, Class) VALUES ('Ariel', 'Sacknovitz', 3.75, 800555666, 'Junior');"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('John', 'Doe', 1.32, 'Freshman', 800000000);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO YCStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('John', 'Doe', 1.32, 'Freshman', 800000000);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO SternStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Plonit', 'Almonit', 4.00, 'Senior', 700012345);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, LastName, GPA, Class, BannerID) VALUES ('Plonit', 'Almonit', 4.00, 'Senior', 700012345);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO SternStudent (FirstName, LastName, GPA, BannerID, Class) VALUES ('Yael', 'Scheinman', 3.50, 700502858, 'Sophomore')");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, LastName, GPA, BannerID, Class) VALUES ('Yael', 'Scheinman', 3.50, 700502858, 'Sophomore');"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO SternStudent (FirstName, LastName, CurrentStudent, BannerID) VALUES ('Rachel', 'Mesch', false, 700987643);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, LastName, CurrentStudent, BannerID) VALUES ('Rachel', 'Mesch', false, 700987643);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO SternStudent (FirstName, LastName, BannerID) VALUES ('Jane', 'Doe', 700000000);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, LastName, BannerID) VALUES ('Jane', 'Doe', 700000000);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO SternStudent (FirstName, LastName, BannerID) VALUES ('Jane', 'Doe II', 700000001);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, LastName, BannerID) VALUES ('Jane', 'Doe II', 700000001);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("INSERT INTO SternStudent (FirstName, LastName, BannerID) VALUES ('Jane', 'Doe III', 700000002);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, LastName, BannerID) VALUES ('Jane', 'Doe III', 700000002);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Attempt to insert into a nonexistent column
        System.out.println("INSERT INTO SternStudent (FirstName, MiddleInitial, LastName, BannerID) VALUES ('Jane', 'H', 'Doe III', 700000002);");
        System.out.println();
        System.out.println(db.execute("INSERT INTO SternStudent (FirstName, MiddleInitial, LastName, BannerID) VALUES ('Jane', 'H', 'Doe III', 700000002);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Select * from each table to print the full tables
        System.out.println("SELECT * FROM YCStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM YCStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT * FROM SternStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM SternStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //SELECT COLUMNS TESTS
        //Select various combinations of columns
        //Print table of selected columns and rows for each selection
        System.out.println("SELECT COLUMNS TESTS: Print tables of selected columns and rows");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT BannerID, LastName, GPA FROM SternStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT BannerID, LastName, GPA FROM SternStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT LastName, FirstName, Class FROM YCStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT LastName, FirstName, Class FROM YCStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Add conditions
        System.out.println("SELECT * FROM SternStudent WHERE Class<>null;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM SternStudent WHERE Class<>null;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT * FROM YCStudent WHERE LastName='Scheinman' OR GPA<3.00");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM YCStudent WHERE LastName='Scheinman' OR GPA<3.00"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT LastName, FirstName FROM YCStudent WHERE CurrentStudent=false OR (Class<>'Freshman' AND GPA>=3.9);");
        System.out.println();
        System.out.println(db.execute("SELECT LastName, FirstName FROM YCStudent WHERE CurrentStudent=false OR (Class<>'Freshman' AND GPA>=3.9);"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Add distinct
        System.out.println("SELECT DISTINCT FirstName, GPA FROM SternStudent WHERE (CurrentStudent=true OR LastName='Mesch') AND GPA<=3.50;");
        System.out.println();
        System.out.println(db.execute("SELECT DISTINCT FirstName, GPA FROM SternStudent WHERE (CurrentStudent=true OR LastName='Mesch') AND GPA<=3.50;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Add order bys
        System.out.println("SELECT BannerID, LastName, FirstName FROM YCStudent WHERE GPA>2.00 ORDER BY LastName ASC;");
        System.out.println();
        System.out.println(db.execute("SELECT BannerID, LastName, FirstName FROM YCStudent WHERE GPA>2.00 ORDER BY LastName ASC, FirstName ASC;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT * FROM SternStudent WHERE GPA<=3.5 ORDER BY Class ASC, FirstName ASC, BannerID DESC;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM SternStudent WHERE GPA<=3.50 ORDER BY Class ASC, FirstName ASC, BannerID DESC;"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //SELECT FUNCTIONS TESTS
        //Select various combinations of functions on columns
        //Print a single-row table of the function results for each selection
        System.out.println("SELECT FUNCTIONS TESTS: Print single-row tables of selected functions");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT AVG(GPA) FROM YCStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT AVG(GPA) FROM YCStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT MIN(BannerID), MAX(GPA) FROM SternStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT MIN(BannerID), MAX(GPA) FROM SternStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Add distinct
        System.out.println("SELECT COUNT(DISTINCT LastName) FROM YCStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT COUNT(DISTINCT LastName) FROM YCStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT SUM(DISTINCT GPA) FROM SternStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT SUM(DISTINCT GPA) FROM SternStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Add conditions
        System.out.println("SELECT AVG(GPA) FROM YCStudent WHERE CurrentStudent=true;");
        System.out.println();
        System.out.println(db.execute("SELECT AVG(GPA) FROM YCStudent WHERE CurrentStudent=true;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT AVG(GPA) FROM YCStudent WHERE CurrentStudent=true;");
        System.out.println();
        System.out.println(db.execute("SELECT COUNT(BannerID), SUM(DISTINCT GPA) FROM SternStudent WHERE GPA<=3.00 OR Class<>'Sophomore';"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //UPDATE TESTS
        //Update values in a few columns, plus in one nonexistent column/table
        //Print true if each update is successful
        System.out.println("UPDATE TESTS: Print true if successful, and print updated tables");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("UPDATE SternStudent SET CurrentStudent=true;");
        System.out.println();
        System.out.println(db.execute("UPDATE SternStudent SET CurrentStudent=true;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Add conditions
        //Update to null
        System.out.println("UPDATE YCStudent SET CurrentStudent=false, Class=null WHERE GPA>3.7;");
        System.out.println();
        System.out.println(db.execute("UPDATE YCStudent SET CurrentStudent=false, Class=null WHERE GPA<3.7;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Attempt to update in a nonexistent table
        System.out.println("UPDATE SymsStudent SET GPA=4.0;");
        System.out.println();
        System.out.println(db.execute("UPDATE SymsStudent SET GPA=4.0;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Select * from each table to print the updated tables
        System.out.println("SELECT * FROM SternStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM SternStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT * FROM YCStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM YCStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println();


        //DELETE TESTS
        //Delete one row from each table, and one from a nonexistent column/table
        //Print true if each update is successful
        System.out.println("DELETE TESTS: Print true if successful, and print updated tables");
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("DELETE FROM SternStudent WHERE BannerID=700502858;");
        System.out.println();
        System.out.println(db.execute("DELETE FROM SternStudent WHERE BannerID=700502858;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("DELETE FROM YCStudent WHERE LastName<>'Scheinman' AND (CurrentStudent=false OR Class='Senior');");
        System.out.println();
        System.out.println(db.execute("DELETE FROM YCStudent WHERE LastName<>'Scheinman' AND (CurrentStudent=false OR Class='Senior');"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Attempt to delete from a nonexistent table
        System.out.println("DELETE FROM SymsStudent WHERE GPA<=3.0;");
        System.out.println();
        System.out.println(db.execute("DELETE FROM SymsStudent WHERE GPA<=3.0;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        //Select * each table to print the updated tables
        System.out.println("SELECT * FROM SternStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM SternStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println();
        System.out.println("SELECT * FROM YCStudent;");
        System.out.println();
        System.out.println(db.execute("SELECT * FROM YCStudent;"));
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
        System.out.println("------------------------------------------------------------");
    }
}