# STORE REST API

This project was generated with [SpringBoot] version 3.2.5 and [JAVA] version 17.

## Development server

Change database credentials in application.properties, then run `./mvnw spring-boot:run` for a dev server. Navigate to `http://localhost:8080/swagger-ui/index.html` to see the documentation.

## Deploy on Docker

Open the root project folder, then run `docker-compose up`. Navigate to `http://localhost:8080/swagger-ui/index.html` to see the documentation. If any change is done, you must
run `docker-compose down` and remove the store-rest-api-store_app image created, then re-run `docker-compose up` in order to see the changes.

To get more help on the SpringBoot use go check out the [SpringBoot Getting Started Guide](https://github.com/spring-guides/gs-spring-boot.git) page.

## COMMON ERRORS

### mvn wrapper error

ERROR -> [notes_api 7/9] RUN ./mvnw dependency:go-offline                                                                                                                     0.7s
[notes_api 7/9] RUN ./mvnw dependency:go-offline:
0.611 /bin/sh: ./mvnw: not found
failed to solve: process "/bin/sh -c ./mvnw dependency:go-offline" did not complete successfully: exit code: 127

SOLUTION -> Run `mvn -N wrapper:wrapper` to generate the mvnw file.

To get maven in your device go to [Downloading Apache Maven 3.9.9](https://maven.apache.org/download.cgi) page.

## Unit Tests

To run unit tests, first, change database credentials, after that, run `./mvnw test`.

