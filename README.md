# CarPlayBLE
 
## Introduction
This is a project that helps me to get navigation information when I am driving, it is consisted of the android app and ESP32 hardware, the ESP32 would display the navigation information I need when I am driving.
this project is devided into two programs, the app part is writen in Android/Java and the ESP32's used Arduino/C as it's language.<br>
Two programs used BLE to communicate, the ESP32 is set as the GATT server and the Android app is set as client.<br>
The ESP32 hardware is better to use TTGO T-Display, it prefect fit current progarm as it has a screen on it. needs to set the screen pins (edit the screen's H file) and button pin if use different ESP32 hardware, the screen needs to be the 1.14 Inch LCD otherwise the display might not correct.<br>
## Overview
## How to use
