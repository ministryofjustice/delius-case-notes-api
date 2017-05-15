FROM java

MAINTAINER Mike Jackson <michael.jackson@digital.justice.gov.uk>

COPY build/libs/delius-case-notes-api.jar /root/

ENTRYPOINT ["/usr/bin/java", "-jar", "/root/delius-case-notes-api.jar"]
CMD ["--spring.profiles.active=oracle"]