Create
database BookingDorm;
Use
BookingDorm;
CREATE TABLE `user`
(
    `user_id`             int         NOT NULL AUTO_INCREMENT,
    `username`            varchar(50) NOT NULL,
    `password`            varchar(255) DEFAULT NULL,
    `role_user`           enum('' ADMIN '','' EMPLOYEE '','' USER '') DEFAULT NULL,
    `gender`              enum('' Male '','' Female '','' Other '') DEFAULT NULL,
    `birthdate`           date         DEFAULT NULL,
    `phone_number`        varchar(20)  DEFAULT NULL,
    `address`             varchar(255) DEFAULT NULL,
    `email`               varchar(100) DEFAULT NULL,
    `cccd_number`         varchar(12)  DEFAULT NULL,
    `avatar`              varchar(255) DEFAULT NULL,
    `front_cccd_image`    varchar(255) DEFAULT NULL,
    `back_cccd_image`     varchar(255) DEFAULT NULL,
    `is_active`           tinyint(1) DEFAULT '' 1 '',
    `is_profile_complete` tinyint(1) DEFAULT '' 0 '',
    `created_at`          timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`          timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
);



CREATE TABLE `new`
(
    `news_id`    bigint       NOT NULL AUTO_INCREMENT,
    `title`      varchar(255) NOT NULL,
    `content`    text         NOT NULL,
    `image_url`  varchar(255) DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`news_id`)
);


CREATE TABLE `dorm`
(
    `dorm_id`     int         NOT NULL AUTO_INCREMENT,
    `dorm_name`   varchar(10) NOT NULL,
    `dorm_gender` enum('' Male '','' Female '','' Other '') NOT NULL,
    `created_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`dorm_id`)
);


CREATE TABLE `floors`
(
    `floor_id`     int NOT NULL AUTO_INCREMENT,
    `dorm_id`      int NOT NULL,
    `floor_number` int NOT NULL,
    `created_at`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`floor_id`),
    KEY            `dorm_id` (`dorm_id`),
    CONSTRAINT `floors_ibfk_1` FOREIGN KEY (`dorm_id`) REFERENCES `dorm` (`dorm_id`)
);


CREATE TABLE `room`
(
    `room_id`       int         NOT NULL AUTO_INCREMENT,
    `floor_id`      int         NOT NULL,
    `room_number`   varchar(20) NOT NULL,
    `capacity`      int         NOT NULL,
    `price_per_bed` float       NOT NULL,
    `created_at`    timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`room_id`),
    KEY             `floor_id` (`floor_id`),
    CONSTRAINT `room_ibfk_1` FOREIGN KEY (`floor_id`) REFERENCES `floors` (`floor_id`)
);


CREATE TABLE `bed`
(
    `bed_id`             int          NOT NULL AUTO_INCREMENT,
    `room_id`            int          NOT NULL,
    `is_available`       tinyint(1) NOT NULL DEFAULT '' 1 '',
    `maintenance_status` enum('' Available '','' Under Maintenance '') NOT NULL DEFAULT '' Available '',
    `created_at`         timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `bed_name`           varchar(255) NOT NULL,
    PRIMARY KEY (`bed_id`),
    KEY                  `room_id` (`room_id`),
    CONSTRAINT `bed_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`)
);


CREATE TABLE `booking`
(
    `booking_id`    int      NOT NULL AUTO_INCREMENT,
    `bed_id`        int      NOT NULL,
    `user_id`       int      NOT NULL,
    `start_date`    date     NOT NULL,
    `end_date`      date     NOT NULL,
    `total_price`   float    NOT NULL,
    `amount_paid`   float             DEFAULT ''0 '',
    `refund_amount` float             DEFAULT ''0 '',
    `refund_date`   date              DEFAULT NULL,
    `booking_date`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status`        enum('' Pending '','' Confirmed '','' Canceled '') NOT NULL DEFAULT '' Pending '',
    `created_at`    timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `room_id`       int      NOT NULL,
    PRIMARY KEY (`booking_id`),
    KEY             `bed_id` (`bed_id`),
    KEY             `user_id` (`user_id`),
    KEY             `FKq83pan5xy2a6rn0qsl9bckqai` (`room_id`),
    CONSTRAINT `booking_ibfk_1` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`bed_id`),
    CONSTRAINT `booking_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `FKq83pan5xy2a6rn0qsl9bckqai` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`)
);


CREATE TABLE `usage_service`
(
    `service_booking_id` int   NOT NULL AUTO_INCREMENT,
    `user_id`            int   NOT NULL,
    `booking_id`         int   NOT NULL,
    `electricity`        float NOT NULL,
    `water`              float NOT NULL,
    `others`             float NOT NULL,
    `payment_method`     enum('' Paypal '','' BankQRCode '') NULL,
    `created_at`         timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `quantity`           float DEFAULT NULL,
    PRIMARY KEY (`service_booking_id`),
    KEY                  `user_id` (`user_id`),
    KEY                  `booking_id` (`booking_id`),
    CONSTRAINT `usage_service_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `usage_service_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`)
);


CREATE TABLE `payment`
(
    `payment_id`     int      NOT NULL AUTO_INCREMENT,
    `booking_id`     int      NOT NULL,
    `payment_method` enum('' PayPal '','' BankQRCode '') NULL,
    `payment_detail` varchar(255)      DEFAULT NULL,
    `payment_date`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `created_at`     timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`),
    KEY              `booking_id` (`booking_id`),
    CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`)
);


CREATE TABLE `notification`
(
    `notification_id` int NOT NULL AUTO_INCREMENT,
    `user_id`         int DEFAULT NULL,
    `content`         text,
    `is_read`         tinyint(1) DEFAULT '' 0 '',
    `created_at`      timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`notification_id`),
    KEY               `user_id` (`user_id`),
    CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);


CREATE TABLE `feedback`
(
    `feedback_id` int NOT NULL AUTO_INCREMENT,
    `user_id`     int DEFAULT NULL,
    `booking_id`  int DEFAULT NULL,
    `rating`      int DEFAULT NULL,
    `comment`     text,
    `created_at`  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`feedback_id`),
    KEY           `user_id` (`user_id`),
    KEY           `booking_id` (`booking_id`),
    CONSTRAINT `feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT `feedback_ibfk_2` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`booking_id`)
);

CREATE TABLE `complaint`
(
    `complaint_id` int NOT NULL AUTO_INCREMENT,
    `user_id`      int                                                           DEFAULT NULL,
    `title`        varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
    `description`  text,
    `reply`        varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
    `status`       enum('' WAITING '','' INPROGRESS '','' DONE '','' REJECT '') DEFAULT NULL,
    `created_at`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`complaint_id`),
    KEY            `user_id` (`user_id`),
    CONSTRAINT `complaint_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);


CREATE TABLE `comment`
(
    `comment_id` int          NOT NULL AUTO_INCREMENT,
    `name`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
    `email`      varchar(255) NOT NULL,
    `message`    text         NOT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`comment_id`)
);

CREATE TABLE `contract`
(
    contract_id   INT AUTO_INCREMENT PRIMARY KEY,
    booking_id    INT          Not null,
    contract_link VARCHAR(255) not null,
    FOREIGN KEY (booking_id) REFERENCES booking (booking_id)
);
CREATE TABLE `semester`
(
    semester_id   INT AUTO_INCREMENT PRIMARY KEY,
    semester_name varchar(255) not null,
    `start_date`  date         NOT NULL,
    `end_date`    date         NOT NULL
);

