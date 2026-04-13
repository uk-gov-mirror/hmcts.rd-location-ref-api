ARG APP_INSIGHTS_AGENT_VERSION=3.6.2
ARG PLATFORM=""
# Application image

FROM hmctsprod.azurecr.io/base/java${PLATFORM}:21-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/rd-location-ref-api.jar /opt/app/

EXPOSE 8099
CMD [ "rd-location-ref-api.jar" ]
