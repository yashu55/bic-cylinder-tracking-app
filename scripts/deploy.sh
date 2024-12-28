#!/bin/bash

# Check if the JAR file path and environment are provided
if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Error: JAR file path and environment ('local' or 'prod') are required."
    echo "Usage: ./deploy.sh <path_to_jar_file> <local|prod>"
    exit 1
fi

JAR_PATH=$1
ENV=$2

# Check if the JAR file exists
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: JAR file '$JAR_PATH' not found."
    exit 1
fi

# Check if the environment is valid
if [[ "$ENV" != "local" && "$ENV" != "prod" ]]; then
    echo "Error: Invalid environment. Please specify 'local' or 'prod'."
    exit 1
fi

# Create the logs directory if it doesn't exist
LOG_DIR="./logs"
if [ ! -d "$LOG_DIR" ]; then
    echo "Logs directory not found. Creating it..."
    mkdir -p "$LOG_DIR"
fi

# Get the current date for log file naming
LOG_FILE="$LOG_DIR/deploy_$(date +'%Y-%m-%d_%H-%M-%S').log"

# Run the JAR file in the background and redirect output to the log file
echo "Deploying application from '$JAR_PATH' with environment '$ENV'..."
nohup java -jar "$JAR_PATH" --spring.profiles.active=$ENV >> "$LOG_FILE" 2>&1 &

# Get the process ID of the background job
PID=$!

echo "Deployment started. Process ID: $PID"
echo "Logs are being saved to '$LOG_FILE'."

# Wait for the process to start
sleep 5

# Check if the process is running
if ps -p $PID > /dev/null; then
    echo "Deployment successful. Application is running in '$ENV' environment."
else
    echo "Error: Deployment failed."
    exit 1
fi
