# CarPlayBLE
 
## Introduction
This is a project that helps me to get navigation information when I am driving, it is consisted of the android app and ESP32 hardware, the ESP32 would display the navigation information I need when I am driving.
this project is devided into two programs, the app part is writen in Android/Java and the ESP32's used Arduino/C as it's language.<br>
Two programs used BLE to communicate, the ESP32 is set as the GATT server and the Android app is set as client.<br>
The ESP32 hardware is better to use TTGO T-Display, it prefect fit current progarm as it has a screen on it. needs to set the screen pins (edit the screen's H file) and button pin if use different ESP32 hardware, the screen needs to be the 1.14 Inch LCD otherwise the display might not correct.<br>
## Overview
How the device looks like before connect to the app<br>
<img src="https://github.com/appleshaman/CarPlayBLE/blob/main/docs/4.jpg" width = "300"><br>
## How to use
When you first open the app, you should see three green dot and one gray dot indicated on the screen, this means all the services needed are already enabled. If the third (Notification Status) dot is still gray, click the left buttom button to enable the Notification Listerner service. The first two dots would generally be green unless your device does not support Bluetooth or Ble.<br>
<img src="https://github.com/appleshaman/CarPlayBLE/blob/main/docs/1.jpg" width = "300"><br>
Now you can click the "scan new device" button to start scan the ESP32 hardware, scanning will start automatically and you can also click the "scan" button to scan again, once you find the device name as "Navigator", you can click it then click "connect" button to connect<br>
<img src="https://github.com/appleshaman/CarPlayBLE/blob/main/docs/2.jpg" width = "300"><br>
Once the device is connected, you can see the changes on the ESP32's screen and back to the main page of the app, you would see the four green dots, this means the app has successfully connected to the ESP32.<br>
<img src="https://github.com/appleshaman/CarPlayBLE/blob/main/docs/3.jpg" width = "300"><br>
After you connect to the device, you can start to use Google map app to navigate, the navigation information would be sent to the ESP32 automatically.
Here is the explaination to each line of the information displayed.<br>
<img src="https://github.com/appleshaman/CarPlayBLE/blob/main/docs/5.jpg" width = "500"><br>
1. Estimated time of arrival<br>
2. Minutes left before arrive<br>
3. Distance left before arrive<br>
4. Destination<br>
5. Street/place you are currently at<br>
6. N/A: distance before change to next direction/ distance before you need to do the instruction be told (turn left, trun right etc.), N/A means no data yet and just keep going<br>

### remember you can click the left button to switch the orientation of the screen
## Bugs or disadvantages already known
The ble messege sent between two parts are not effiecient as the MTU settings are inefficient, the messege sent each time are limited in 20 bytes so I have send it for multiple times.
The part about read the ble message for ESP32 is also hard to use, this is due to I could not do a deep copy to the data I received.
Both of them are caused by the Andrino frame that is not 100% compatiable with ESP32, I would use ESPidf to rebuild this in the future
The app could not store the device that connected before, so you have to scan it every time, this is due to I have not found a good way to store the device yet, even the BluetoothDevice class had implemented Parcelable but still could not be transfered into bytes and store it, I will find a way in the future to fix it.
