 # UnBorify
## A collection of entertaining media to brighten up your day!
### To do list
* Choose Material Design color scheme
* Add more API functions in menu 
  *  Use switches to add these rest apis according to user preferences
* Add ads (Admob / Firebase Ads)
* Localize
* Refactor code
  * Refactor and simplify REST API Retrieveal
* fix bugs
  * On rotation retrofit re-queries Api
  * Set retrofit to requery or leave an error message if query fails.
  * Remove unused views
  * Make text dynamically size to view
  * Lower or remove joke text see-through-ness
  * Have several entertaining things ready in order to decrease load time
 
  
#### Possible future updates
* Add help section
* Add favorites to database
* Add image/video/meme share functionality
* Allow backward navigation functionality
* Connect more REST APIs for media
  * Memes
    * https://github.com/k3min/infinigag
    * https://github.com/PoprostuRonin/memes-api
  * Photos/Gifs
    * https://github.com/Giphy/GiphyAPI
  * Jokes
    * https://github.com/jamesseanwright/ron-swanson-quotes
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
* Implement share functionality for text jokes
* Implement instrumentation tests to confirm response from Retrofit using Espresso
* Create and add new app launcher icon
* Add swipe gestures

###### Credit to
* Diolor for SwipeCards Layout
* Square for Retrofit
