FROM java:8
RUN echo "Asia/Shanghai" > /etc/timezone
ADD app.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]