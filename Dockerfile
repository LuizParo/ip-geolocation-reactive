# maven image
FROM maven:3.6.3-openjdk-11 as artifacts

# Creates the dir to generate the project artifacts
RUN mkdir -p /ip-geolocation-service
WORKDIR /ip-geolocation-service

# Copies the projects file for artifacts creation
ADD ./src/main ./src/main
ADD ./pom.xml ./pom.xml

# Compile code and generate JAR file
RUN mvn clean package -DskipTests

########################################################################################################################
########################################################################################################################
# JRE image
FROM openjdk:11.0.8-jre-slim

# Exposes port 8080
EXPOSE 8080/tcp

# Copy generate jar from maven image to JRE image
COPY --from=artifacts /ip-geolocation-service/target/geolocation-reactive-1.0.0.jar ./geolocation-reactive-1.0.0.jar

# Entrypoint starting the application
ENTRYPOINT ["java", "-jar", "geolocation-reactive-1.0.0.jar"]