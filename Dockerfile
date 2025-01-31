# Use Gradle 8.12.0 with JDK 21 as the base image, This needs to be modified to multistage to avoid downloading maven dependencies in every run. 
FROM gradle:8.12.0-jdk21

# Set the working directory in the container
 WORKDIR /app

# Copy the contents of the infra-guard-aws folder to /app
 COPY . /app

# Set the default environment variable for AWS Profile
ENV AWS_PROFILE=AdministratorAccess-100000000000

# RUN 
# Define the default command to clean and test the project with Gradle
 CMD ["gradle", "clean", "test", "--info"]