## An app that assists users with their fashion choices

### To do:
 * Set up Firebase database
 * Set up photo functionality
 * Have photo upload to Firebase storage
 * Have my account section with photos
 * Allow user to sign out
 * Choose new material design color scheme
 * Set up rating system
 * Come up with new clever name
 * Put app on Google play
 * Create tests
 * Improve layout
 
 
### Done
 * Set up swipe card layout
 * Set up sign in
 * Set up Firebase ads


### Known bugs/todo/ to fix:
 * Layout needs to be fixed /improved to look good
 * Signed in users must be connected to database

### Possible features:
* If enough users are not being rated then allow for only one photo to be uploaded/ rated for free and then future photos will be on a per-rate basis where 1 rate = 1 new photo uploaded


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
