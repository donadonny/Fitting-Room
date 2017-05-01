 # UnBorify
## A collection of entertaining media to brighten up your day!
### To do list
* Create and add new app launcher icon
* Choose Material Design color scheme
* Connect more REST APIs for media
  * Memes
  * Photos
  * Jokes
  * Videos
* Allow forward (and possibly backwards) navigation
* Add more API functions in menu 
  *  Use switches to add these rest apis according to user preferences
* Add ads (Admob / Firebase Ads)
* Localize
* Refactor code
* fix bugs
  * On rotation retrofit re-queries Api
  * Set retrofit to requery or leave an error message if query fails.

#### Possible future updates
* Add swipe-functions to swipe forward and back instead of physical buttons
* Add favorites to database
* Add image/video/meme share functionality

##### Done List
* Added REST API
  * Jokes APIs
     * https://api.chucknorris.io/jokes/random
     * tambal.azurewebsites.net/joke/random
* Created layout for testing basic functionality
* Implement share functionality for text jokes
* Implement instrumentation tests to confirm response from Retrofit using Espresso
