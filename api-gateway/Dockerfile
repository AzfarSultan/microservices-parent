FROM openjdk:17

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]


## draw backs, its using jdk instead of JRE,
## and if there will be a small change in the code or any file,-
## this docker image will build the whole and its also bigger in size .

## to avoid this docker introduce multi stage build with Dockerfile.layered