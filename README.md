# delius-case-notes-api
A Spring Java RESTful API exposing access to Delius case notes

# Building in IntelliJ
This project uses Project Lombok to cut down boilerplate around data classes.
You will need to 
- import the Lombok plugin : Preferences -> Plugins -> search lombok

- enable annotation processing : Preferences -> Build, Execution, Deployment -> Compiler -> Annotation Processors -> Enable Annotation Processing

# Built artifact
./gradlew clean build


There is an executable jar in /build/libs:
- java -jar delius-case-notes-api.jar

To run up on a specific port:
- java -jar -Dserver.port=8090 delius-case-notes-api.jar
 
# Setting up local oracle
Assuming you have jumped through all the docker hoops, log on as SYSTEM and create a new user:

create user c##deliusapi identified by deliusapipwd container = all;
grant connect to c##deliusapi;
grant all privileges to c##deliusapi;

logon as c##deliusapi

CREATE TABLESPACE T_DATA datafile '/opt/oracle/oradata/DELIUS/t_data.dbf' size 10m;

create TABLESPACE T_AUDIT_DATA datafile '/opt/oracle/oradata/DELIUS/t_audit_data.dbf' size 10m;

create TABLESPACE T_SPG_DATA datafile '/opt/oracle/oradata/DELIUS/t_spg_data.dbf' size 10m;

create TABLESPACE T_CHANGE_CAPTURE_DATA datafile '/opt/oracle/oradata/DELIUS/t_change_capture_data.dbf' size 10m;

create TABLESPACE T_DOCUMENT_DATA datafile '/opt/oracle/oradata/DELIUS/t_document_data.dbf' size 10m;

create TABLESPACE T_REFERENCE_DATA datafile '/opt/oracle/oradata/DELIUS/t_reference_data.dbf' size 10m;

ALTER DATABASE DATAFILE '/opt/oracle/oradata/DELIUS/t_data.dbf' AUTOEXTEND ON MAXSIZE UNLIMITED;

ALTER DATABASE DATAFILE '/opt/oracle/oradata/DELIUS/t_reference_data.dbf' AUTOEXTEND ON MAXSIZE UNLIMITED;

Then run the nd_906.ddl