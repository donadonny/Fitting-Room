## An app that assists users with their fashion choices

This app allows users to upload a photo of an outfit for users to rate by swiping left or right to show their thoughts. This way, the user can always go out knowing that they have an amazing outfit that is approved by people who know style.

### To do:
 * Put app on Google play
 * Create tests
 * Create Terms of Service
 * Have interstitial or native ads show up after x - swipes 

### Done
 * Set up swipe card layout
 * Set up sign in and sign out using Firebase AUTH and Firebase UI
 * Choose Material Design Color Scheme
 * Have photo upload to Firebase storage
 * Set up Firebase database
   * Db structure
    * [user|photo urls]
    * [photo urls|likes|dislikes|reports|usernames]
 * Set up photo functionality
 * Create layout for new photo creation
 * Allow reporting of photos
 * Have my account section with photos
 * Set up rating system for user likes or dislikes
 * Create catchy launcher icon 
 * Localize all strings
 * Make a progress bar for user to see photo upload progress

### Known bugs/todo/ to fix:
* Gets repeat photos
* Improve layout
   * Make it nice
   * Decide what to do with add photo fab
   * Make tablet layout
* Handle activity rotation for MainActivity
* Reinstate report button functionality
   * Adds section to database for reports, photo, and username only (easier viewing)
   * Allows for querying based on amount of reports (so higher amount of reports will show up first
   * Clicking report will report the photo and go on to the next photo without voting
* The following line crashed a Samsung s8+ (API 24) with a can not divide by 0 error (only once after dozens of photos were uploaded)
```java
 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        prog = toIntExact(100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    }
                    else{
                        prog = (int)(100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                    }
```


### Possible features:
* If enough users are not being rated then allow for only one photo to be uploaded/ rated for free and then future photos will be on a per-rate basis where 1 rate = 1 new photo uploaded
* Allow following other users
   * This will add a field to the user part of the database which will be followed users. Possibilities for this are:
     * The app will push photos from the followed users to the front of the list of photos to rate.
     * Dedicated section to followed users (like with youtube subscriptions)
   * Add number of followers to user part of database
   * Add animations/transitions
   * Add search user
   * Add profile photos
 * Add option to skip or indifferent view of style
 * Have local database so that "my photos" section will load faster
 * Make text dynamically size to view



###### Credit to
* Diolor for SwipeCards Layout
* Google for the Google Libraries used
* Ben Manes for the Dependency Updates library
