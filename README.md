﻿# P6-PayMyBuddy

## Description
This project is a money transfer application built with Spring Boot, Maven, and JPA/Hibernate. It includes models for users, accounts, transactions, and user connections.

## Database

### Diagramme de classe

![diagramme de classe](https://github.com/user-attachments/assets/0d7c46b0-89c2-48b4-80b0-3ea620a0c590)


### Modèle physique de données

![schéma sql](https://github.com/user-attachments/assets/99da483a-de92-4b31-a26d-cec44d0b5196)

### SQL Scripts

#### Tables

```sql
CREATE TABLE user (
    userID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE UserConnection (
    userID INT,
    connectedUserID INT,
    PRIMARY KEY (userID, connectedUserID),
    FOREIGN KEY (userID) REFERENCES user(userID),
    FOREIGN KEY (connectedUserID) REFERENCES user(userID)
);

CREATE TABLE account (
    accountID INT AUTO_INCREMENT PRIMARY KEY,
    userID INT,
    balance DECIMAL(10, 2),
    FOREIGN KEY (userID) REFERENCES user(userID)
);

CREATE TABLE Transaction (
    transactionID INT AUTO_INCREMENT PRIMARY KEY,
    accountID INT,
    senderID INT,
    receiver VARCHAR(255),
    description VARCHAR(255),
    amount DECIMAL(10, 2),
    FOREIGN KEY (accountID) REFERENCES account(accountID),
    FOREIGN KEY (senderID) REFERENCES user(userID)
);

CREATE TABLE user_roles (
    userID INT,
    role VARCHAR(255),
    PRIMARY KEY (userID, role),
    FOREIGN KEY (userID) REFERENCES user(userID)
);
```

## Running the Application

### Prerequisites

Java 11 or higher
Maven 3.6.3 or higher
MySQL database

### Setting up the Database

Create a new MySQL database.
Run the SQL scripts provided above to create the necessary tables.

### Building and Running the Application

#### Build the application:

mvn clean install

#### Run the application:

mvn spring-boot:run

#### Accessing the Application

Once the application is running, you can access it at http://localhost:8080.
