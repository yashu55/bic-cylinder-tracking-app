#!/bin/bash

# Function to handle local build and deployment
build_and_deploy_local() {
    echo "Building Angular for local environment..."
    cd frontend || exit
    npm run build:local
    if [ $? -ne 0 ]; then
        echo "Angular build for local failed!"
        exit 1
    fi
    echo "Building Spring Boot for local environment..."
    cd ../backend || exit
    ../gradlew.bat clean bootJar -Dspring.profiles.active=local
    if [ $? -ne 0 ]; then
        echo "Spring Boot build for local failed!"
        exit 1
    fi

    echo "Running Spring Boot JAR for local environment..."
    java -jar build/libs/backend.jar --spring.profiles.active=local
}

# Function to handle production build and deployment
build_and_deploy_prod() {
    echo "Building Angular for production environment..."
    cd frontend || exit
    npm run build:prod
    if [ $? -ne 0 ]; then
        echo "Angular build for production failed!"
        exit 1
    fi

    echo "Building Spring Boot for production environment..."
    cd ../backend || exit
    ../gradlew clean buildForProd -Dspring.profiles.active=prod
    if [ $? -ne 0 ]; then
        echo "Spring Boot build for production failed!"
        exit 1
    fi

    echo "Running Spring Boot JAR for production environment..."
    java -jar build/libs/backend.jar --spring.profiles.active=prod
}

# Check input argument to determine environment
if [ "$1" == "local" ]; then
    echo "Deploying for local environment..."
    build_and_deploy_local
elif [ "$1" == "prod" ]; then
    echo "Deploying for production environment..."
    build_and_deploy_prod
else
    echo "Invalid argument. Please specify 'local' or 'prod'."
    exit 1
fi
