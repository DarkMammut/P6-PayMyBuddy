# P6-PayMyBuddy

## Description
This project is a money transfer application built with Spring Boot, Maven, and JPA/Hibernate. It includes models for users, accounts, transactions, and user connections.

## Templates

### Login
![P6-Login](https://github.com/user-attachments/assets/6f8be4c7-5ff3-4bd8-832e-063d9e6660d2)

### Register
![P6-Register](https://github.com/user-attachments/assets/718300e8-3161-4973-9e5e-cda0ef1f234c)

### Transfer
![P6-Transfer](https://github.com/user-attachments/assets/7c8c6c91-42a9-498c-9659-17be93acb94c)

### Profile
![P6-Profile](https://github.com/user-attachments/assets/480f345c-4d09-4553-8d29-49a67945239c)

### Contact
![P6-Contact](https://github.com/user-attachments/assets/febc7bed-9ad0-460f-a860-44cdead5c214)

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

## Tests

### Jacoco
![P6-Jacoco](https://github.com/user-attachments/assets/6f07001c-2280-437e-a9f7-1a39586fd3db)

### Surefire
![P6-Surefire1](https://github.com/user-attachments/assets/27504667-b698-4a1b-a441-c5e9b6e9fe05)
![P6-Surefire2](https://github.com/user-attachments/assets/c843a6eb-c6fa-4618-a4ab-fa1acc2b4c3b)
![P6-Surefire3](https://github.com/user-attachments/assets/cb60d814-482a-4bc3-8787-28daad8a9948)
![P6-Surefire4](https://github.com/user-attachments/assets/5ccab7db-19d7-4b63-8f32-ee230da1fd22)
![P6-Surefire5](https://github.com/user-attachments/assets/d123d7d8-f34a-42d0-84f5-0c3452745b26)
![P6-Surefire6](https://github.com/user-attachments/assets/03ada50b-4684-4cd1-b0b6-98caa305b2df)


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
