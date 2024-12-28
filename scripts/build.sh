#!/bin/bash

# Check for dependencies
command -v npm >/dev/null 2>&1 || { echo "npm is required but not installed. Aborting."; exit 1; }
command -v gradle >/dev/null 2>&1 || { echo "gradle is required but not installed. Aborting."; exit 1; }

# Set the Gradle command based on the operating system
if [[ "$OSTYPE" == "linux-gnu"* ]] || [[ "$OSTYPE" == "darwin"* ]]; then
    GRADLE_CMD="./gradlew"
elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]]; then
    GRADLE_CMD="./gradlew.bat"
else
    echo "Unsupported OS."
    exit 1
fi

# Function to build the Angular frontend for the specified environment
build_angular() {
    echo "Building Angular frontend..."
    cd frontend || exit

    # Clean up old files before the build
    rm -rf dist
    if [ "$1" == "local" ]; then
        npm run build:local >> build.log 2>&1
    elif [ "$1" == "prod" ]; then
        npm run build:prod >> build.log 2>&1
    else
        echo "Invalid environment for Angular build."
        exit 1
    fi

    if [ $? -ne 0 ]; then
        echo "Angular build failed!"
        exit 1
    fi
    cd ..
}

# Function to delete the previous static folder and ensure it's clean before copying files
clean_static_folder() {
    echo "Cleaning old static folder..."
    rm -rf backend/src/main/resources/static
    mkdir -p backend/src/main/resources/static
}

# Function to copy files from Angular dist to the static folder, omitting the browser folder
copy_angular_to_static() {
    echo "Copying files to static folder..."

    # Copy everything from dist folder (including the 'browser' folder)
    cp -r frontend/dist/* backend/src/main/resources/static/

    # Move the contents of 'static/browser' to 'static/' (flattening the browser folder)
    mv backend/src/main/resources/static/browser/* backend/src/main/resources/static/

    # Remove the now-empty 'browser' folder
    rm -rf backend/src/main/resources/static/browser
}

# Function to grant read permissions to all files in the static folder
grant_read_permissions() {
    echo "Granting read permissions to static folder and its contents..."
    chmod -R a+r backend/src/main/resources/static
}

# Function to build the backend with Gradle for the specified environment
build_backend() {
    echo "Building Spring Boot backend for $1 environment..."
    cd backend || exit
    if [ "$1" == "local" ]; then
        $GRADLE_CMD clean bootJar -Dspring.profiles.active=local >> build.log 2>&1
    elif [ "$1" == "prod" ]; then
        $GRADLE_CMD clean buildForProd -Dspring.profiles.active=prod >> build.log 2>&1
    else
        echo "Invalid environment for backend build."
        exit 1
    fi

    if [ $? -ne 0 ]; then
        echo "Spring Boot build failed!"
        exit 1
    fi
    cd ..
}

# Main build process
if [ "$1" == "local" ] || [ "$1" == "prod" ]; then
    echo "Starting build process for $1 environment..."

    # 1. Build Angular frontend
    build_angular $1

    # 2. Clean static folder and copy files
    clean_static_folder
    copy_angular_to_static

    # 3. Grant read permissions to static folder
    grant_read_permissions

    # 4. Build the backend
    build_backend $1

    echo "Build process completed successfully for $1 environment."
else
    echo "Please specify 'local' or 'prod' as an argument."
    exit 1
fi
