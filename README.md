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

## Configuration
Overridable properties:
base.url (default '/delius')
server.port (default '8080')

To run up on a specific port and base url:
- java -jar -Dserver.port=8090 -Dbase.url='/mydelius' delius-case-notes-api.jar
 
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

## Resolving the oracle drivers
Helpfully, Oracle don't publish the drivers in any open repositories.
You will have to download the jar from 
http://www.oracle.com/technetwork/database/features/jdbc/jdbc-ucp-122-3110062.html

and the easiest thing (if you have maven installed) is to install it into your local maven repo:

mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=12.2.0.1 -Dpackaging=jar

## Running the service against local oracle
Assuming you have downloaded the oracle binaries, built a docker image, configured the database, applied the ddl, 
dropped a load of FKs, inserted the dummy data from src/test/resources, downloaded the oracle drivers and installed 
them somewhere discoverable by gradle, you will probably want to test it.

As a temporary convenience, there is a spring profile to enable which will do just that. Pass the 
following onto the command line when starting the application:

--spring.profiles.active=oracle

## Running as a docker container
Run buildDocker.sh. This creates the docker image.

The default behaviour is to for the container to run against an oracle database.
This is enacted by running the spring profile 'oracle' by default.
The host name for the oracle instance is exposed by the environment varaiable DB_HOSTNAME.

If you want to run against the sort-of realistic oracle db container described above, the easiest
thing to do is to create a docker network:

docker network create <network_name>

and attach the oracle container to it:

docker network connect <oracle_container_name> <network_name>

Finally, run the delius_case_notes_api image on the network:

docker run -it -e DB_HOSTNAME=<oracle_container_name> --net=<network_name> -p 8080:8080 delius-case-notes-api


## Running as a docker container with the in-memory H2 db
No need to set up a docker network.

You will need to force the 'default' spring profile:

docker run -it -p 8080:8080 delius-case-notes-api --spring.profiles.active=default
