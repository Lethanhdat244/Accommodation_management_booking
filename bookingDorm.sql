CREATE DATABASE bookingDorm;
use bookingDorm;

CREATE TABLE user (
                      user_id INT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(50) NOT NULL,
                      password VARCHAR(255),
                      role_user ENUM('ADMIN','EMPLOYEE', 'USER'),
                      gender ENUM('Male', 'Female', 'Other'),
                      birthdate DATE,
                      phone_number VARCHAR(20),
                      address VARCHAR(255),
                      email VARCHAR(100),
                      cccd_number VARCHAR(12),
                      avatar VARCHAR(255),
                      front_cccd_image VARCHAR(255),
                      back_cccd_image VARCHAR(255),
                      is_active BOOLEAN DEFAULT TRUE,
                      is_profile_complete BOOLEAN DEFAULT FALSE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE new (
                     news_id INT AUTO_INCREMENT PRIMARY KEY,
                     title VARCHAR(255) NOT NULL,
                     content TEXT NOT NULL,
                     image_url VARCHAR(255),
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


create table dorm(
                     dorm_id int auto_increment primary key,
                     dorm_name varchar(10) not null,
                     dorm_gender ENUM('Male', 'Female', 'Other') NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table floors(
                       floor_id int auto_increment primary key,
                       dorm_id int not null,
                       floor_number int not null,
                       foreign key (dorm_id) references dorm(dorm_id),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table room (
                      room_id int auto_increment primary key,
                      floor_id int not null,
                      room_number varchar(20) not null,
                      capacity int not null,
                      price_per_bed float not null,
                      foreign key (floor_id) references floors(floor_id),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table bed(
                    bed_id int auto_increment primary key,
                    room_id int not null,
                    bed_name VARCHAR(255) NOT NULL,
                    is_available boolean not null default true,
                    maintenance_status enum('Available', 'Under Maintenance') NOT NULL DEFAULT 'Available',
                    foreign key (room_id) references room(room_id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table booking(
                        booking_id int auto_increment primary key,
                        bed_id int not null,
                        user_id int not null,
                        room_id int not null,
                        start_date date not null,
                        end_date date not null,
                        total_price float not null,
                        amount_paid float default 0,
                        refund_amount float default 0,
                        refund_date date null,
                        booking_date datetime not null default current_timestamp,
                        status enum('Pending', 'Confirmed', 'Canceled') NOT NULL DEFAULT 'Pending',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        foreign key (bed_id) references bed(bed_id),
                        foreign key (user_id) references user(user_id),
                        foreign key (room_id) references room(room_id)
);

CREATE TABLE service (
                         service_id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100),
                         price DECIMAL(10, 2),
                         description TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usage_service (
                               service_booking_id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT not null,
                               booking_id int not null,
                               electricity float not null,
                               water float not null,
                               others float not null,
                               payment_method enum('Paypal', 'BankQRCode') NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES user(user_id),
                               FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);

create table payment(
                        payment_id int auto_increment primary key,
                        booking_id int not null,
                        payment_method enum('PayPal', 'BankQRCode') NOT NULL,
                        payment_detail varchar(255),
                        payment_date datetime not null default current_timestamp,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        foreign key (booking_id) references booking(booking_id)
);

CREATE TABLE notification (
                              notification_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT,
                              content TEXT,
                              is_read BOOLEAN DEFAULT FALSE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE feedback(
                         feedback_id INT AUTO_INCREMENT PRIMARY KEY,
                         user_id INT,
                         booking_id INT,
                         rating INT,
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES user(user_id),
                         FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);

CREATE TABLE equipment (
                           equipment_id INT AUTO_INCREMENT PRIMARY KEY,
                           room_id INT,
                           name VARCHAR(100),
                           quantity INT,
                           equip_condition ENUM('Good', 'Fair', 'Poor'),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (room_id) REFERENCES room(room_id)
);

CREATE TABLE complaint(
                          complaint_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT,
                          title nvarchar(255),
                          description TEXT,
                          reply nvarchar(255),
                          status ENUM('WAITING', 'IN PROGRESS', 'DONE', 'REJECT'),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE comment(
                        comment_id INT auto_increment primary key,
                        name nvarchar(255),
                        email varchar(255) not null,
                        message text not null,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);