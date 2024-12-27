# cylinder-tracking-api
BIC Cylinder API

# Local Build:

## Build Angular for local:

```
cd frontend
npm run build:local
```

## Build Spring Boot for local:

```
cd ../backend
../gradlew clean bootJar -Dspring.profiles.active=local
```
## Run the JAR:

```
java -jar build/libs/backend.jar --spring.profiles.active=local
```
# Production Build:

## Build Angular for production:


```
cd frontend
npm run build:prod
```
## Build Spring Boot for production:

```
cd ../backend
../gradlew clean buildForProd -Dspring.profiles.active=prod
```
## Run the JAR:

```
java -jar build/libs/backend.jar --spring.profiles.active=prod
```