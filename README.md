# StudentDBMS
A Students Database Management System.

It uses the following:
  1. Java 8.
  2. JavaFX.
  3. MongoDB.
  
It is a simple management system which have two level of users:
  1. admin
  2. teachers
 
The admin has access to student's basic profile, and can CRUD students, users and departments profiles.
The teachers can update only marks/scores of the allocated students only but can view academic data of other students
of the same department.

# Security
The project uses the same certificate file (to keep things simple).
If you want to use your own certificate then:
  1. Use OpenSSL to create the PEM file.
  2. Add that to the keystore ( create it via "keytool" which is by default available in JDKs ).
  3. Make the corresponding changes in the source code and typh.cfg.
  

