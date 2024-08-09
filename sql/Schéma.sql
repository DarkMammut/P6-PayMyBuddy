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