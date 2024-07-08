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
The application offers functionalities such as user registration, book management, writing revievs, exchange approval, one-on-one chatting, profile and wishlist management. It is designed using REST API principles for efficient communication between the frontend and backend, 
and JWT tokens for secure authentication. The [backend](https://github.com/artsol0/BookSwap-SpringBoot-Backend) is developed using Spring Boot 3, while the [frontend](https://github.com/artsol0/BookSwap-Angular-Frontend) is developed using Angular 17.

## Features

* User Registration and Authentication: Users can register accounts and log in to them.
* Email Confirmation: Accounts are activated after confirming email via a received link.
* Password Recovery: Users can reset their password if forgotten via a link received in their email.
* Book Searching: Users can search for books by various attributes (genre, language, quality, status) and keywords.
* Book Management: Users can create, update, and delete their own books.
* Writing Revievs: Users can add their reviews to books.
* Wishlist Management: Users can add books to their wishlist and remove them.
* One-on-One Chatting: Users can chat with each other directly.
* Profile Management: Users can change their avatar, password, or location.
* Exchange Approval: Users can send exchange offers to each other and confirm or delete them.

### Database ERD

![BookSwap-ERD](https://github.com/artsol0/BookSwap-SpringBoot-Backend/assets/108554037/ad7e0043-72a1-4c13-a66f-816ab10e4333)
Created with [dbdiagram.io](https://dbdiagram.io/d)
> [!NOTE]
> The list of countries and their cities is retrieved using the [Country State City API](https://github.com/dr5hn/countries-states-cities-database). To use it, you need to request your own API key via the link on the [site](https://countrystatecity.in/).

## Technologies Used

### Backend
* Spring Boot 3
* Spring Security 6 with JWT Token Authentication
* Spring Web
* Spring Data JPA
* Spring WebSocket
* Spring Validation
* MySQL Driver
* H2 Database
* Thymeleaf
* Java Mail Sender
* Mockito with JUnit 5
* Lombok
* OpenAPI
* Docker

### Frontend
* Angular 17
* Tailwind CSS
* SockJS
* Angular Materil Library

## License

This project is licensed under MIT License. See the [LICENSE](https://github.com/artsol0/BookSwap-SpringBoot-Backend/blob/master/LICENSE) file for details.
