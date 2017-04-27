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