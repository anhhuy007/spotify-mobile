# Spotify Clone

A comprehensive Spotify clone mobile app developed on Android. This project replicates the core features of Spotify, offering users a rich music streaming experience with modern functionalities and a sleek interface.

![App Mockup](https://github.com/anhhuy007/spotify-mobile/blob/master/spotify%20clone.png) <!-- Replace with actual image URL if available -->

<div align="center">
  <p align="center">
    A mobile clone app of <strong>Spotify</strong> built on Android using Java and Node.js backend.
    <br />
    <a href="https://drive.google.com/file/d/1zRXpzmPPI7xydeOQe341ZAkbJTfrPVCZ/view?usp=drive_link"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://drive.google.com/file/d/1vwkwNnwSuI_T6_1Q6ug_HKUAcVRE89-f/view?usp=sharing">View Demo</a>
    ·
    <a href="https://docs.google.com/presentation/d/1PAafdFnVqIbzCh_kr04a4PRySFD_EqOU/edit?usp=drive_link&ouid=100651601658120224541&rtpof=true&sd=true">View Presentation</a>
  </p>
</div>

## Features

- **User Authentication**
  - Sign up, login (standard & Google)
  - JWT-based authentication with access token refresh
  - Forgot password and account security features

- **Music Streaming**
  - Basic controls: Play, Pause, Next, Previous, Shuffle, Repeat
  - Queue management and song progress persistence
  - Background playback with system notifications
  - Offline listening with downloadable tracks
  - Lyrics display for each song

- **Homepage Experience**
  - Featured artists and albums
  - Trending, recommended, and most loved albums

- **User Accounts**
  - Premium account registration and feature access
  - Profile management and user settings

- **Library & Personal Content**
  - Create and manage playlists
  - Save favorite songs and artists
  - Listening history and downloads
  - Follow favorite artists

- **Search and Discovery**
  - Search by song, album, artist, genre
  - Smart suggestions, pagination, and filters
  - Album, artist, and genre detail pages
  - New releases and music charts

- **Notifications**
  - Firebase Cloud Messaging
  - Music and system alerts
  - Token management for user-specific push notifications

- **Chatbot & Utility Features**
  - Chatbot with AI-based song suggestions
  - Sleep timer for automatic playback stop

## Technology Stack

### Client (Android App)
- **Language**: Java  
- **Architecture**: MVVM  
- **IDE**: Android Studio  
- **Notifications**: Firebase Cloud Messaging  
- **API Communication**: REST via Retrofit  
- **Offline Mode**: Local storage and media caching

### Server (Backend)
- **Platform**: Node.js  
- **Framework**: Express.js  
- **Database**: MongoDB with Mongoose  
- **Authentication**: JWT (Access & Refresh Tokens)  
- **Architecture**: Three-Tier (Controller - Service - Model)  
- **Media Storage**: Cloudinary  
- **Testing**: Postman for API validation

### Tools & Integrations
<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=androidstudio,java,nodejs,express,mongodb,firebase,postman,cloudinary" />
  </a>
</p>

## Project Structure

This project is divided into two main repositories:

- 📱 **Client App (Android)**  
  Handles the UI/UX, playback, user interaction, and connection with backend services.

- 🔗 **Server API**  
  Provides authentication, content management, media handling, and user data processing.

## Purpose

This project was created as a final assignment for a mobile development course. It serves as a practical exercise in building a full-featured, scalable music streaming app with real-world architecture and technologies, inspired by Spotify's success model.

---

> 📂 Repositories:  
> - Server: [https://github.com/anhhuy007/spotify-server]

Feel free to explore, contribute, or fork the project!
