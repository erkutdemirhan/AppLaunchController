# AppLaunchController

An Android application project listing all of the installed apps on the Android device and adding/removing them from a black list.
If the user starts do not disturb mode and tries to launch any app in the black list, the AppLaunchController launches and prevents
the black listed app to be shown on the foreground. The AppLaunchController also shows a notification message to warn the user that the app she tries to launch is on the black list.

In this project, an [AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService) 
class is implemented to listen for screen changes on the foreground and let the app know if a black listed app shows up on the screen.
Even though the AccessibilityService implementation on this project is not suiting to its recommended purpose of use, it provided a
convenient method for listening foreground screen change events on newer versions of Android. 

The app requires user to enable AccessibilityService in order to start the do not disturb mode.
