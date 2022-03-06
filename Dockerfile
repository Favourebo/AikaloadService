FROM openjdk:8
ADD target/aikaloadservice.jar aikaloadservice.jar
EXPOSE 7075
ENTRYPOINT ["java","-jar","aikaloadservice.jar"]
