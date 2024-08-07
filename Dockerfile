# Set the base image
FROM openjdk:17-bullseye

# Set the working directory
WORKDIR /app

# Copy the generated .jar file from Gradle build output directory
COPY ./build/libs/H10E01-Containers-1.0.0.jar ./app.jar

# Copy the start.sh file into the working directory
COPY start.sh ./start.sh

# Make start.sh executable
#RUN chmod 770 start.sh

# Define the command to execute the script defined in start.sh
CMD ./start.sh

