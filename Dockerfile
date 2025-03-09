FROM adoptopenjdk/openjdk11:jre-11.0.13_8-alpine
WORKDIR /opt/exathreat/gateway/
ADD build/libs/exathreat-gateway-springboot.jar /opt/exathreat/gateway/
EXPOSE 1514 5000
ENTRYPOINT [ "java", \
	"-Djava.security.egd=file:/dev/./urandom", \
	"-Dapi.url=${API_URL}", \
	"-Dapi.key=${API_KEY}", \
	"-Dgw.health=${GW_HEALTH}", \
	"-jar", \
	"exathreat-gateway-springboot.jar" ]