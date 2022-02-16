# Spend Manager React Native Sample

# Structure

* CaptureBridge contains Android and iOS bridge for Capture
* SDKBridge contains Android and iOS bridge for SDK
* CaptureBridgeDemo contains a demo app, using CaptureBridge
* SDKBridgeDemo contains a demo app, using SDKBridge

# Installation

## Capture Bridge

### iOS

* Add Sensibill Capture SDK to iOS project.
* Add camera permission to info.plist.
* Add provided bridge files to iOS project.

### Android

* Add SensibillPackage.kt and SensibillCaptureBridge.kt to the Android Project.
* Verify camera permission has been added to the manifest.


## SDK Bridge

### iOS

* Add Sensibill SDK to iOS project either drag and drop or using cocoapods.
* Add camera permission to info.plist.
* Add provided bridge files to iOS project.
* Add Sensibill.plist with valid key values.
* Provide Sensibill API access token when asked by SDK.

### Android

* Add SensibillPackage.kt and SensibillCaptureBridge.kt to the Android Project.
* If you are using the Receipt Capture Flow, add the ReceiptCaptureActivity.kt (or copy its functionality) to the Android Project
* Verify camera permission has been added to the manifest.
* Update the SensibillCaptureBridge.kt with your credentials provided by Sensibill.


# Integration

## Capture Bridge

### iOS

1. Import `NativeModules` from React Native.
2. Create `SensibillCaptureBridge` const in order to access bridged functions.
3. Launch Capture using bridge.
4. Create instance of `NativeEventEmitter` and use it to add `sendImage` listener in order to receive captured image provided as base64 encoded string.

```objc
// Step 1
import { NativeModules, } from 'react-native';

// Step 2
const { SensibillCaptureBridge } = NativeModules;

// Step 3
SensibillCaptureBridge.launchCapture();

// Step 4
let captureEventEmitter = new NativeEventEmitter(NativeModules.SensibillCaptureBridgeEventEmitter)

captureEventEmitter.addListener('sendImage', (data) => {
	this.setImage('data:image/png;base64,' + data);
})
```

### Android

#### Step 1: Add dependencies

In your app's build.gradle, add the Sensibill implementation for capture standalone

```
implementation ("com.getsensibill:sensibill-capture-standalone:2022.X.X")
```

## SDK Bridge

### iOS

Pre-requisites:
- Make sure you have access to Sensibill Bitbucket to either download Sensibill SDK or access it using SSH key when using CocoaPods.

#### Step 1: Add Sensibill SDK to iOS project.
- Either Direct Download or using CocoaPods from `Sensibill Bitbucket`.

#### Step 2: Add Sensibill.plist with valid key values.
- In order for SDK to communicate with Sensibill API.

#### Step 3: Add camera permission to info.plist.
- In order to launch capture directly or while using spend manager, application needs access to camera.

#### Step 4: Add provided bridge under SDKBridge is added in iOS Project.

#### Step 5: Provide Sensibill API Token configured for the environment specified in `Sensibill.plist`
- In `SensibillSDKBridge.m`, there is a TODO section, make sure you place appropriate token retrieved for the environment specified on `Sensibill.plist`.

#### Step 6: Run `SDKBridgeDemo` target on device/simulator.

* Perform steps as specified in sample application in order to understand usage.
    i.e.
		 - `Start SDK`
		 - `Launch Spend Manager`
		 - `Launch Capture`
		 - `Stop SDK`

### Android

#### Step 1: Copy Capture Bridge Integration Steps
See Integration Section for Capture Bridge first. If you plan to use the entire SDK, make sure to implement the sensibill-sdk-all project instead.

```
implementation ("com.getsensibill:sensibill-sdk-all:2022.X.X")
```

#### Step 2: Setup Credentials
In the SensibillCaptureBridge.kt, there is a TODO section near the top of the file. Here you need to provide your credentials as provided by sensibill. If you enter in a token, the logic will attempt to use the token and ignore the username/password signIn(). Once credentials are correctly updated, you can run the demo.
