# Book*Swap*

## Content

* [Overview](#overview)
* [Features](#features)
* [Technologies Used](#technologies-used)
  * [Backend](#backend)
  * [Frontend](#frontend)
* [License](#license)

## Overview

Book*Swap* is a full-stack application that provides users with the ability to comfortably exchange books. 
The application offers functionalities such as user registration, book management, exchange approval, one-on-one chatting, profile and wishlist management. It is designed using REST API principles for efficient communication between the frontend and backend, 
and JWT tokens for secure authentication. The [backend](https://github.com/artsol0/BookSwap-SpringBoot-Backend) is developed using Spring Boot 3, while the [frontend](https://github.com/artsol0/BookSwap-Angular-Frontend) is developed using Angular 17.

## Features

* User Registration and Authentication: Users can register accounts and log in to them.
* Email Confirmation: Accounts are activated after confirming email via a received link.
* Password Recovery: Users can reset their password if forgotten via a link received in their email.
* Book Searching: Users can search for books by various attributes (genre, language, quality, status) and keywords.
* Book Management: Users can create, update, and delete their own books.
* Wishlist Management: Users can add books to their wishlist and remove them.
* One-on-One Chatting: Users can chat with each other directly.
* Profile Management: Users can change their avatar, password, or location.
* Exchange Approval: Users can send exchange offers to each other and confirm or delete them.

## Technologies Used

### Backend
* Spring Boot 3
* Spring Security 6 with JWT Token Authentication
* Spring Data JPA
* Spring Validation
* MySQL Driver
* H2 Database
* Mockito with JUnit 5
* Lombok

### Frontend
* Angular 17
* Tailwind CSS
* SockJS
* Angular Materil Library

## License
