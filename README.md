# Doogle Maps

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Screen SnapShot](#Screen-Snapshots)
5. [App Features](#App-Features)

## Overview
### Description
- This app is essentially a lost dog tracker, that uses googles mapping system. This app is centered for pet owners or lovers of animals. The app allows the user to post pictures of missing/stray pets in their community. The picture that they post uses their location to add a marker on the map within the app. This marker contains a picture of the animal, along with the location and any other information the user wants to add. 
- I would also love to implement a type of blog that allows for users to post notices of their pets going missing. Something kind of like twitter, with the main focus just allowing others within the community to be aware and on the lookout for that person’s pet.

### App Evaluation
- **Category:** Navigation
- **Mobile:** Its essential for this app to be on mobile devices as it makes it possible for quick updates on the location of the missing/stray animals. The camera app is used to take pictures of the animals, and the map is used to give the location of the animals the user is posting.
- **Story:** This app gives pet owners the opportunity to reunite with their lost companions by having the their community work together to make it possible. 
- **Market:** Anybody who owns a pet or has a love for animals could use this app. With this in mind, based on the community that using it, the user base for this app could be quite large. If the user base is large, pet owners within that community will benefit greatly from it.
- **Habit:** This app will only become a habit when the user has lost their pet, or if their community contains lots of animals. The more a user posts updates of the animals around them, the more likely they are to open the app out of habit.
- **Scope:**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User will login/signup to access the application
* User can interact with the onscreen map to look for reported animals nearby
* Users can create their own reports by interacting with an onscreen map
* Report window will allow the user to provide the location, description, and a picture of the lost animal
* User can post & interact with a feed that consists of pet owners reporting their lost pets
* An algorithm will be implemented that matches similar reports and posts to each other
* Special search feature will be implemented that searches through the animal type, description, and location all at the same time


**Optional Nice-to-have Stories**

* 1:1 Chat feature within the application
* Notifications for both the chat and algorithm feature
* Share feature that allows the user to share a report/post to someone in their contacts


### 2. Screen Archetypes

* Login/Signup
   * After downloading the app, the user is prompted to login or create an account.
* Home Screen
   * After login, user is directed to multiple feeds that contain reports and missing animal posts.
* Report/Missing Post Screen
   * User can direct themselves to detailed screens of the report/missing posts. These detailed screens give the user access to a special Map, sharing features, and chat features
* Map Screen
   * User can interact with this special map that contains reports made in the area
* Chat Screen
   * User can message other users within the application
* Settings Screen
   * User can direct themselves to settings where they can edit their profile or logout

   
### 3. Navigation

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Signup Screen
   * Home Screen
* Signup Screen
   * Login Screen
   * Home Screen
* Home Screen
   * Report Tab
   * Missing Tab
   * Chat Tab
   * Profile Screen
   * Settings Screen
* Setting Screen
   * Edit Profile Tab
   * Logout Tab


## Wireframes
<img src="https://i.imgur.com/Zmm2fi5.jpg" width=600>

## Screen Snapshots

<p float="left">
  <img src="https://i.imgur.com/Na4jsMY.jpg" width=200 />
  <img src="https://i.imgur.com/hGKS1GM.jpg" width=200 />
  <img src="https://i.imgur.com/1D0PUAb.png" width=200 />
  <img src="https://i.imgur.com/lFKB3Ac.png" width=200 />
  <img src="https://i.imgur.com/YEuztrG.png" width=200 />
</p>

## App Features

### Complex Features

- **Algorithm:** The application has an algorithm implemented that matches reports to their respective posts. It does this by checking three things. The first thing it checks is the animal type listed for the post, this is the one thing that has to be exact. The next thing it checks is the description, the algorithm uses a levenshtein string checker to check if two strings are similar enough. The last thing it checks is the location, the report and post have to be within 10 miles of each other.
- **Search:** The application has a search feature implemented that filters the reports/missing animal posts by three things at a time. The first thing the filter checks for is the location, so if the user is typing in a location posts that are within that location take priority for the filter. The second thing the filter checks for is the animal type, and the last thing it checks for is the description. These filters make it much easier to find a specfic report/ missing animal post

### Other Features

- **Chat:** The application has a 1:1 chat implemented within the application. This 1:1 chat can be accessed within the detailed view for reports/missing animal posts, or through the chat tab that can be seen through the home screen. In this chat one can send a message to another user and recieve it in real time. This chat has read/delievered receipts as it is important that users see when their message has been seen.
- **Notifications:** The application has a notification system implemented that works for both the chat and algorithm. For the chat whenever a user sends a message to another, the receiving user gets a notification that shows who sent the message and part of the message itself. For the algorithm, once the matching reports/posts are found, a notification is sent to the users of all the missing animals posts who recieved a match. This notification lets the user know that a match for them has been found, and takes them to a detailed view of the report. 
- **Edit Profile:** The application contains an edit profile feature that allows the user to change their profile picture and their username. These changes are made real time and can be seen instantly by other users of the application.
- **Share:** The application contains a share feature that allows the user to share a report/missing animal post to someone in their contacts, email, etc. The information that is shared consists of the description and a picture of the animal in question.


