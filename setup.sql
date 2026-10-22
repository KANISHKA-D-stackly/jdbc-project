create database if not exists jdbc_projects;
use jdbc_projects;

create table if not exists students (
    student_id int primary key,
    student_name varchar(100) not null,
    department varchar(50) not null,
    email varchar(100) unique,
    marks decimal(5,2) default 0
);

create table if not exists employees (
    employee_id int primary key,
    employee_name varchar(100) not null,
    department varchar(50) not null,
    email varchar(100) unique
);

create table if not exists leave_requests (
    request_id int primary key auto_increment,
    employee_id int not null,
    start_date date not null,
    end_date date not null,
    reason varchar(255) not null,
    status varchar(20) not null default 'PENDING',
    foreign key (employee_id) references employees(employee_id),
    check (end_date >= start_date)
);
