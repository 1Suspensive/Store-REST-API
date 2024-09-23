FROM eclipse-temurin:17-alpine

EXPOSE 8001

WORKDIR /root

COPY ./pom-docker.xml /root/pom.xml
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY ./src /root/src

#Running the tests
RUN ./mvnw clean install

#Without tests
#RUN ./mvnw clean install -DskipTests

ENTRYPOINT ["java","-jar","/root/target/store-0.0.1-SNAPSHOT.jar"]
