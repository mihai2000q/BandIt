# BandIt

This project aims to implement an Android Mobile Application for musicians. 
The main goal would be to make it easier for artists to: manage their time, organize their songs and chat with other band members.

To run this project the following course of actions is advised.

## Installation
### Android Studio
Use the Android Studio [IDE](developer.android.com/studio) provided by Google. 
It will install all the tools that one needs in order to run this application.

Open the project folder in the IDE (the photo below shows one method to do so).

![image](https://user-images.githubusercontent.com/103370692/222988861-e23a1500-363f-4c72-8066-ff383bab746c.png)

Ignore all the Gradle configurations (just make sure it is synced with the latest version). 
The below figure illustrates the module that has to be synced.

![image](https://user-images.githubusercontent.com/103370692/222988885-f31aebe2-002c-4e29-a9ec-db78d6130861.png)

### Create an Android Virtual Machine
The project can be run on a physical phone too, but I recommend the usage of an android emulator.

To create one, open the Device Manager tab located on the right-side of the IDE.

![image](https://user-images.githubusercontent.com/103370692/222988936-487e75cb-6809-4945-8e73-37fc1753a7e2.png)

Click on Create Device

![image](https://user-images.githubusercontent.com/103370692/222988959-c0a51962-855d-4577-8856-6a96dc9a2d89.png)

Choose any of the emulator devices provided by Google, then click on Next. 

![image](https://user-images.githubusercontent.com/103370692/222988991-3301dfb6-0c2f-44d1-8db7-44d3e2d7a111.png)

Select the default system image for the selected device (it has to be above API Level 29).

![image](https://user-images.githubusercontent.com/103370692/222988998-c60458ea-b8cb-42c5-98f1-02a928ae1dc5.png)

Then choose the name and click on Finish.

![image](https://user-images.githubusercontent.com/103370692/222989002-8976efd4-a3bb-4484-861c-e0d3169938c8.png)

### Run the source code

Now, one can run the program by simply clicking on the Run 'app' button (shortcut: Shift+F10).
Note that an emulator has to be chosen, but it is, most of the times, chosen by default once it is installed.

![image](https://user-images.githubusercontent.com/103370692/222989037-3057dd71-f84d-4900-98f3-0666250f562f.png)

Once you open the application, the emulator will take some time to boot. 
Then, the application wil get installed, just to be compiled and up running, right after.

### Tests

The tests can be run as any Java Unit Test, by right clicking on the unit/integration test folder and selecting "Run 'tests'..."

Legend: 
test - Unit Testing, 
androidTest - Integration Testing


![image](https://user-images.githubusercontent.com/103370692/222989318-d63c91e9-f3c4-4995-9e10-915b7924c4d9.png)

Be careful that most integration tests have preconditions that have to be met before running them.
