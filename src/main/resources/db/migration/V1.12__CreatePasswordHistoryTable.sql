CREATE TABLE PASSWORD_HISTORY
(
    USER_ID BIGINT REFERENCES USERTABLE (ID),
    PASSWORD VARCHAR(255),
    CREATED_AT TIMESTAMP NOT NULL,
    PRIMARY KEY (USER_ID, PASSWORD)
 );