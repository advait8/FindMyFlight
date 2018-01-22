# FindMyFlight
Udacity Android Nanodegree : Capstone Project : Phase 2

Main highlights of the project :
1) Uses 3rd party library : a) RXJava 
                            b) Retrofit 2
                            c) Commons I/O
2) Validates Input from user and prevents app crash : Used snackbars to make the user aware of any API failure. 
3) Content descriptions are maintained in content_descriptions.xml
4) Strings are all translatable with RTL support.
5) Widget created with all code under appwidget package. 
6) App uses following Firebase features :
    a) Firebase AdMob (implemented Banner ad on flight details screen. Shows test ad)
    b) Firebase Analytics (implemented Singleton class to get a single instance of FirebaseAnalytics class)
    c) Firebase Applink : Created a dynamic url so as to test instantapp. 
7) App theme extends AppCompat. 
8) Use app bar to reflect, what screen we are on. 
9) App uses device back button to go back from flight details to flight search screen. 
10) All dependencies are maintained in gradle. installRelease task works to build a release version of the app. 
11) App uses contentProvider to store the recently searched flights. 
12) App uses IntentService to update the widget data. 
13) Implemented an AsynctaskLoader to load the flight details screen.
    
