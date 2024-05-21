CREATE DATABASE bookingDorm;
use bookingDorm;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role_number INT,
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
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE bookings (
  booking_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  room_id INT,
  check_in_date DATE,
  check_out_date DATE,
  deposit DECIMAL(10, 2),
  total_amount DECIMAL(10, 2),
  status ENUM('Pending', 'Confirmed', 'Cancelled', 'Completed'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  promotion_id INT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);
CREATE TABLE services (
  service_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  price DECIMAL(10, 2),
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service_bookings (
  service_booking_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT,
  service_id INT,
  quantity INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
  FOREIGN KEY (service_id) REFERENCES services(service_id)
);

CREATE TABLE rooms (
  room_id INT AUTO_INCREMENT PRIMARY KEY,
  room_status ENUM('Available', 'Occupied', 'Maintenance'),
  room_number VARCHAR(10),
  capacity INT,
  status ENUM('Active', 'Inactive'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE payments (
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT,
  amount DECIMAL(10, 2),
  payment_date DATE,
  payment_method ENUM('Cash', 'Card', 'Bank Transfer'),
  payment_status ENUM('Pending', 'Completed', 'Failed'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

CREATE TABLE notifications (
  notification_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  content TEXT,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE maintenance_requests (
  request_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  room_id INT,
  description TEXT,
  status ENUM('Open', 'In Progress', 'Completed'),
  assigned_to INT,
  completed_at DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE feedbacks (
  feedback_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  booking_id INT,
  rating INT,
  comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

CREATE TABLE equipment (
  equipment_id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT,
  name VARCHAR(100),
  quantity INT,
  equip_condition ENUM('Good', 'Fair', 'Poor'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE complaints (
  complaint_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  room_id INT,
  description TEXT,
  status ENUM('Open', 'In Progress', 'Closed'),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

