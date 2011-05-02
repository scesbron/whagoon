# --- !Ups
CREATE TABLE ENVIRONMENT (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE WEBLODOMAIN (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    host varchar(255) NOT NULL,
    port varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    env_id bigint(20) NOT NULL,
    FOREIGN KEY (env_id) REFERENCES ENVIRONMENT(id),
    PRIMARY KEY (id)
);
CREATE TABLE WEBLOSERVER (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    host varchar(255) NOT NULL,
    port varchar(255) NOT NULL,
    domain_id bigint(20) NOT NULL,
    FOREIGN KEY (domain_id) REFERENCES WEBLODOMAIN(id),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE ENVIRONMENT;
DROP TABLE WEBLODOMAIN;
DROP TABLE WEBLOSERVER;