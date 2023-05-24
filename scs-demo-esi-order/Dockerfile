FROM eclipse-temurin:20.0.1_9-jre-alpine
COPY target/scs-demo-esi-order-0.0.1-SNAPSHOT.jar .
CMD java -Djava.security.egd=file:/dev/./urandom -Xmx300m -Xms300m  -XX:TieredStopAtLevel=1 -noverify -jar scs-demo-esi-order-0.0.1-SNAPSHOT.jar
EXPOSE 8080
