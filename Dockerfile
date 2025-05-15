FROM openjdk:8-jdk-alpine

# set working dir
WORKDIR /app

# copy Maven wrapper and pom.xml
COPY pom.xml ./

# copy source code
COPY src ./src
COPY Data ./Data

# install Maven
RUN apk add --no-cache maven

# build the project
RUN mvn clean package -DskipTests

# run the application
CMD ["mvn", "exec:java", "-Dexec.mainClass=com.yushiz.project.UsageTranslator.UsageTranslator"]
