# Car Sharing Service API

## Project Overview

Welcome to the Car Sharing Service project! This initiative streamlines car rental operations, 
simplifying user management, rental processes, 
and payment handling for an efficient and automated car-sharing experience.

## Technologies Used

- **Spring Boot**: Core framework for building REST APIs.
- **Spring MVC**: Facilitates building web applications and RESTful APIs using the MVC pattern.
- **Spring Security**: Secures the application with JWT-based authentication.
- **Spring Data JPA**: Handles interactions with the relational database using Hibernate.
- **MySQL**: Relational database for storing cars, users, rental and payment information.
- **Maven**: Builds and manages project dependencies.
- **JUnit 5**: A testing framework for unit and integration tests.
- **Lombok**: Reduces boilerplate code with annotations for getters, setters, constructors, etc.
- **MapStruct**: Simplifies object mapping between DTOs and entities.
- **Liquibase**: Manages database schema changes with version control.
- **Stripe API**: Manages payment processing for rentals.
- **Telegram API**: Sends notifications via a Telegram bot.
- **Swagger/OpenAPI**: Provides an interactive UI for exploring the API documentation.
- **Docker**: Containerization to run the application in isolated environments.

## Controllers Functionality

### For users

- **Register and Login**: New users can register, login, and start renting cars.
- **Browse Cars**: Users can view available cars and their details.
- **Rent a Car**: Users can rent cars, initiating rental transactions.
- **View Rentals**: Users can check active and past rentals.
- **Return Cars**: Users return rented cars, updating rental status.
- **Payments**: Secure payment processing via Stripe.

### For admins:
- **Manage Inventory**: Managers can add, update, or delete cars.
- **View Rentals and Payments**: Access to all rentals and payment history.
- **Telegram Notifications**: Managers receive updates about new rentals, overdue rentals, and successful payments.

## Requirements

- **Java** version 17 and higher
- **Maven** for dependency management
- **Docker** to prepare the environment

## How to set up

1. Clone repository
    ```bash
       git clone https://github.com/fetis-off/course-project.git
   ```

2. Create .env file for environment by filling the .env.template
    ```
    MYSQLDB_DATABASE=
    MYSQLDB_USER=
    MYSQLDB_PASSWORD=
    MYSQLDB_ROOT_PASSWORD=

    MYSQLDB_PORT=
    DEBUG_PORT=

    MYSQLDB_LOCAL_PORT=
    MYSQLDB_DOCKER_PORT=
    SPRING_LOCAL_PORT=
    SPRING_DOCKER_PORT=
   ```
3. Build and then start the containers by using Docker Compose
    ```
   docker-compose build
   docker-compose up
   ```
4. The application will be accessible at `http://localhost:<YOUR_PORT>/api`.

## Testing

1. You can run tests to check if everything is working as it should be by executing following:
    ```bash
      mvn clean test
    ```
2. If there are any trouble of understanding
   the purpose of specific controller or
   endpoint you can check out swagger by visiting:
   http://localhost:8080/swagger-ui/index.html

## Challenges Faced

1. Data Modeling and Database Relationships
   Building a clear and flexible data model for the car sharing service was challenging,
   particularly in representing relationships among entities.

2. Authentication and Authorization
   Setting up secure, JWT-based authentication was crucial but challenging.
   Ensuring proper role-based access control.

3. Managing Environment Variables Securely
   Handling environment variables in Docker can be tricky,
   especially when trying to prevent sensitive information
   from being hard-coded into Dockerfiles or images.

4. Integration external api for providing payments feature and notification service and 
   proper configure them was really challenging part of this project. So I received new experience
   doing that.


## Postman collection

I've provided Postman collection with all the API requests that can be used.
It's located in `postman` folder.

### How to use it

1. **Locate the collection**: The Postman collection is located in the `postman` folder of the project.
    - File path: `./postman/Car Sharing.postman_collection.json"`
2. **Import the collection**
    - In the upper left corner of Postman use `File -> Import` or just press `ctrl + o` to import the collection

---

Working on the Car Sharing Service project was an invaluable experience that greatly enhanced my 
understanding of application development. Through this project, I delved into a wide range of libraries and frameworks, 
gained a comprehensive understanding of their capabilities, and learned how to apply them effectively to solve real-world 
challenges in the domain of car-sharing and rental management.
