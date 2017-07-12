## An app that assists users with their fashion choices

This app allows users to upload a photo of an outfit for users to rate by swiping left or right to show their thoughts. This way, the user can always go out knowing that they have an amazing outfit that is approved by people who know style.

### To do:
 * Have my account section with photos
 * Set up rating system
   *  Need to add code to ensure same photo isn't voted on twice by single user
     * Either have a "last viewed" or "photos viewed" section in db, or have a timestamp which will show until what point user has seen photos. This will be useful if starting from oldest to newest.
     * Another option is to have from newest to older, but will need to find another way to ensure previously rated photos will not show up.
 * Come up with new clever name
 * Put app on Google play
 * Create tests
 * Improve layout
 * Handle screen rotations savedinstancestate on ALL activities
 * Create Terms of Service
 * Localize
  
### Done
 * Set up swipe card layout
 * Set up sign in and sign out
 * Choose Material Design Color Scheme
 * Set up Firebase ads
 * Have photo upload to Firebase storage
 * Set up Firebase database
   * Db structure
    * [user|photo urls]
    * [photo urls|likes|dislikes|reports|usernames]
 * Set up photo functionality
 * Create layout for new photo creation
 * Allow reporting of photos
 * Make a progress bar for user to see photo upload progress



### Known bugs/todo/ to fix:
 * Layout needs to be fixed /improved to look good
 * Photos load slowly and not always
 * User can vote twice on same photo
 * Photo pops up after voting on it due to app checking for data changes
 * Don't allow users to vote on own photos
 * Remove background images and data as it looks bad
 
### Possible features:
* If enough users are not being rated then allow for only one photo to be uploaded/ rated for free and then future photos will be on a per-rate basis where 1 rate = 1 new photo uploaded
* Allow following other users
   * This will add a field to the user part of the database which will be followed users. Possibilities for this are:
     * The app will push photos from the followed users to the front of the list of photos to rate.
     * Dedicated section to followed users (like with youtube subscriptions)
   * Add number of followers to user part of database
   * Add search user
   * Add profile photos
 * Add option to skip or indifferent view of style
 * Have local database so that "my photos" section will load faster




















## Note: Project being repurposed: Following information is from the previous version of the app.
## A collection of entertaining media to brighten up your day!
### Planned Features
* Post on Google Play Store
  * Set up release version using gradle
  * Prepare materials for posting on Google Play
    * Screenshots
    * Text
      * Details
      * Summary
* Localize
#### Known Bugs and Things That Need Fixing
* Several empty cards before arriving to one with information
* On rotation retrofit re-queries Api
* On too many rotations app crashes. Crash for some reason claims fab is responsible.
* App loses joke on rotation
* Set retrofit to requery or leave an error message if query fails.
* Lower or remove joke text see-through-ness
* Have several entertaining things ready in order to decrease load time
* Switch test to use swipe layout vs hidden textview layout
* Refactor and simplify REST API Retrieveal

 
  
#### Possible future updates
* Add help section
* Add favorites to database
* Add image/video/meme share functionality
* Allow backward navigation functionality
* Add more API functions in menu 
  *  Use switches to add/remove rest apis according to user preferences
* Connect more REST APIs for media
  * Memes
    * https://github.com/k3min/infinigag
    * https://github.com/PoprostuRonin/memes-api
  * Photos/Gifs
    * https://github.com/Giphy/GiphyAPI
  * Jokes
    * https://github.com/jamesseanwright/ron-swanson-quotes
    * Use jokes from own library created here https://github.com/tal32123/JokeTellingApp (library used jokes from elsewhere)
  * Videos
    * Youtube trending
  * Fun facts
    * http://catfacts-api.appspot.com/api/facts

##### Done List
* Added REST API
  * Jokes APIs
     * https://api.chucknorris.io/jokes/random
     * tambal.azurewebsites.net/joke/random
* Created layout for testing basic functionality
* Choose Material Design color scheme
* Implement share functionality for text jokes
* Implement instrumentation tests to confirm response from Retrofit using Espresso
* Create and add new app launcher icon
* Add swipe gestures
* Make text dynamically size to view
* Add ads (Firebase/AdMob)
* Add Gradle dependency version checker

###### Credit to
* Diolor for SwipeCards Layout
* Square for Retrofit
* Everyone responsible for the following APIs that were used:
  * https://api.chucknorris.io/jokes/random
  * tambal.azurewebsites.net/joke/random
* Google for the Google Libraries used
* Ben Manes for the Dependency Updates library
