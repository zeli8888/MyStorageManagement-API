FROM openjdk:17-jdk-slim

WORKDIR /home/storage-management-api

COPY target/MyStorageManagement-0.0.1-SNAPSHOT.jar ./storage-management-api.jar

# need to set database

CMD ["java", "-jar", "storage-management-api.jar"]