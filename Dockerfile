FROM eclipse-temurin:17

# Copia el archivo JAR de la aplicación al contenedor
COPY ./target/store-0.0.1-SNAPSHOT.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
