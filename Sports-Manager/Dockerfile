FROM openjdk:17
COPY ./build/libs /usr/app
COPY ./static-content /usr/app/static-content
COPY ./static-content /usr/app/
WORKDIR /usr/app
CMD ["java", "-jar", "/usr/app/2122-2-LEIC41N-G01.jar", "postgres"]
# no meu caso usar "/usr/app/2122-2-LEIC41N-G01_LS.jar"