# Turner Casting Instant App

## Overview

The goal of this sample project is to leverage Android Instant Apps to enable users to cast to nearby TVs from mobile devices. The typical use-case is :

1. User sees a marketing ad on any of the known mobile marketing channels
* When user clicks on the marketing link, the instant app is launched
* Instant app detects FireTV or Chromecast if available
* User can cast to discovered TV device or watch on the phone

## Instructions

### Setup
1. Connect mobile phone/tablet to Wifi
* Connect an Amazon Fire TV device to the same Wifi network

### Running project as a Full App
To run this project as a full app, set Android Studio's Run Configuration to 'app'
![alt text][fullAppRunConfig]

#### Expected
Connected Fire TV devices should appear in the list
![alt text][fullAppScreenshot]

[fullAppRunConfig]: screenshots/full_app_run_config.png ""
[fullAppScreenshot]: screenshots/full_app_screenshot.png ""

---

### Running project as an Instant App
To run this project as an instant app, set Android Studio's Run Configuration to 'instantapp'
![alt text][instantAppRunConfig]

#### Expected
Connected Fire TV devices will not appear in the list
![alt text][instantAppScreenshot]

[instantAppRunConfig]: screenshots/instant_app_run_config.png ""
[instantAppScreenshot]: screenshots/instant_app_screenshot.png ""
