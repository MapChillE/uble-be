FROM openjdk:17
COPY build/libs/uble-0.0.1-SNAPSHOT.jar app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone
ENTRYPOINT ["java", "-jar", "app.jar"]
