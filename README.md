## An app that assists users with their fashion choices

This app allows users to upload a photoModel of an outfit for users to rate by swiping left or right to show their thoughts. This way, the userModel can always go out knowing that they have an amazing outfit that is approved by people who know style.

### To do:
 * Put app on Google play
 * Create tests
 * Create Terms of Service
 * Refactor code
     * Take deep look and delete ALL non-necessary code (including things that are not commented out and/or listed as unused by Android Studio)
* Add badges
     * x amount of photoModels uploaded
     * x amount of photoModels rated
     * x amount positive rated photoModels (both sent and received)
     * x amount negative rated photoModels (both sent and received)
     * Surpassed x% positive and negative rating on a photoModel.
* Make tablet layout
* Improve looks of native ads
* Localize all strings

### Done
 * Set up swipe card layout
 * Set up sign in and sign out using Firebase AUTH and Firebase UI
 * Choose Material Design Color Scheme
 * Add native ads, banner ads, and interstitial ads
 * Have photoModel upload to Firebase storage
 * Set up Firebase database
   * Db structure
    * [userModel|photoModel urls]
    * [photoModel urls|likes|dislikes|reports|usernames]
 * Set up photoModel functionality
 * Create layout for new photoModel creation
 * Create photoModel reporting functionality
 * Have my account section with photoModels
 * Set up rating system for userModel likes or dislikes
 * Create catchy launcher icon
 * Make a progress bar for userModel to see photoModel upload progress
 * Decreased image size by up to 33 times with no noticeable image quality loss in order to reduce:
     * Server storage space
     * Server bandwidth usage
     * User upload time
     * Time it takes userModel to view photoModels
 * Add ability for users to zoom into photoModel
 * Add option for commenting on photoModels
 * Created notifications for when userModel gets a commentModel on their photoModel
  * Add Firebase analytics

### Known bugs/ to fix:
   * None



### Possible features:
* Make a sort of timeline view like facebook/instagram?
* If enough users are not being rated then allow for only one photoModel to be uploaded/ rated for free and then future photoModels will be on a per-rate basis where 1 rate = 1 new photoModel uploaded
* Allow following other users
   * This will add a field to the userModel part of the database which will be followed users. Possibilities for this are:
     * The app will push photoModels from the followed users to the front of the list of photoModels to rate.
     * Dedicated section to followed users (like with youtube subscriptions)
   * Add number of followers to userModel part of database
   * Add animations/transitions
   * Add search userModel
   * Add profile photoModels
 * Add option to skip or indifferent view of style
 * Have local database so that "my photoModels" section will load faster
 * Make text dynamically size to view



###### Credit to

* Janishar Ali for the PlaceHolder Library
* Google for the Google Libraries used
* Ben Manes for the Dependency Updates library
* Zetra(zetbaitsu) for the Compressor Library
* Bump Technologies for the Glide Library
* Chris Banes for the PhotoView Library
* Ivan Arcuschin for the SimpleRatingBar Library
* Ravi Tamada from AndroidHive
* Tango Agency for the Avatar View Library
* Jacek for the EasyImage Library
* Icons from https://icons8.com/
